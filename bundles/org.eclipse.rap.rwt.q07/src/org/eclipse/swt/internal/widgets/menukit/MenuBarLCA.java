/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.menukit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;


final class MenuBarLCA extends MenuDelegateLCA {

  // pseudo-property that denotes the shell which uses a menu for its menu bar
  static final String PROP_SHELL = "menuBarShell";
  private static final String PROP_SHELL_MENU = "menuBar";
  private static final String PROP_SHELL_MENU_BOUNDS
    = "menuBarShellClientArea";

  void preserveValues( final Menu menu ) {
    Decorations parent = getParent( menu );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    adapter.preserve( PROP_SHELL, parent );
    MenuLCAUtil.preserveEnabled( menu );
    MenuLCAUtil.preserveMenuListener( menu );
    WidgetLCAUtil.preserveCustomVariant( menu );
    WidgetLCAUtil.preserveHelpListener( menu );
  }

  void readData( final Menu menu ) {
    MenuLCAUtil.readMenuEvent( menu );
    WidgetLCAUtil.processHelp( menu );
  }

  void renderInitialization( final Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    writer.newWidget( "qx.ui.menubar.MenuBar" );
  }

  void renderChanges( final Menu menu ) throws IOException {
    writeParent( menu );
    writeBounds( menu );
    MenuLCAUtil.writeEnabled( menu );
    // TODO [rst] Disable menu listener on Menubars? In SWT/Win, only the
    //      SWT.HIDE is sent but this behavior seems to be undocumented.
    //      Check out other platforms.
    MenuLCAUtil.writeMenuListener( menu );
    MenuLCAUtil.writeUnhideMenu( menu );
    WidgetLCAUtil.writeCustomVariant( menu );
    WidgetLCAUtil.writeHelpListener( menu );
  }

  //////////////////////////////////////////////////
  // Helping method to write properties for menu bar

  private static Decorations getParent( final Menu menu ) {
    Decorations result = null;
    if( menu.getParent().getMenuBar() == menu ) {
      result = menu.getParent();
    }
    return result;
  }

  private static void writeParent( final Menu menu ) throws IOException {
    Decorations parent = getParent( menu );
    if( WidgetLCAUtil.hasChanged( menu, PROP_SHELL, parent, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( menu );
      writer.set( "parent", parent );
    }
  }

  private static void writeBounds( final Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    Decorations parent = getParent( menu );
    if( parent != null ) {
      IShellAdapter shellAdapter
        = ( IShellAdapter )parent.getAdapter( IShellAdapter.class );
      Rectangle menuBounds = shellAdapter.getMenuBounds();
      String prop = PROP_SHELL_MENU_BOUNDS;
      // [if] MenuBar and its bounds are preserved in ShellLCA.
      if(    WidgetLCAUtil.hasChanged( parent, prop, menuBounds, null )
          || WidgetLCAUtil.hasChanged( parent, PROP_SHELL_MENU, menu, null ) )
      {
        // parameter order of setSpace: x, width, y, height
        Object[] args = new Object[] {
          new Integer( menuBounds.x ),
          new Integer( menuBounds.width ),
          new Integer( menuBounds.y ),
          new Integer( menuBounds.height )
        };
        writer.set( "space", args );
      }
    }
  }
}
