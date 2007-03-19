/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.menukit;

import java.io.IOException;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.IShellAdapter;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Menu;
import org.eclipse.rap.rwt.widgets.Shell;


final class MenuBarLCA extends MenuDelegateLCA {

  // pseudo-property that denotes the shell which uses a menu for its menu bar
  private static final String PROP_SHELL 
    = "menuBarShell";
  private static final String PROP_SHELL_MENU_BOUNDS 
    = "menuBarShellClientArea";
  
  void preserveValues( final Menu menu ) {
    Shell shell = MenuBarLCA.getShell( menu );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    adapter.preserve( PROP_SHELL, shell );
    adapter.preserve( Props.ENABLED, Boolean.valueOf( menu.getEnabled() ) );
  }
  
  void readData( Menu menu ) {
  }

  void renderInitialization( final Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    writer.newWidget( "qx.ui.menubar.MenuBar" );
  }

  void renderChanges( final Menu menu ) throws IOException {
    writeParent( menu );
    writeBounds( menu );
    MenuLCAUtil.writeEnabled( menu );
  }
  
  //////////////////////////////////////////////////
  // Helping method to write properties for menu bar
  
  private static Shell getShell( final Menu menu ) {
    Shell result = null;
    if( menu.getParent().getMenuBar() == menu ) {
      result = menu.getParent();
    }
    return result;
  }

  private static void writeParent( final Menu menu ) throws IOException {
    Shell shell = MenuBarLCA.getShell( menu );
    if( WidgetLCAUtil.hasChanged( menu, PROP_SHELL, shell, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( menu );
      writer.set( "parent", shell );
    }
  }

  private static void writeBounds( final Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    Shell shell = getShell( menu );
    if( shell != null ) {
      IShellAdapter shellAdapter
        = ( IShellAdapter )shell.getAdapter( IShellAdapter.class );
      Rectangle menuBounds = shellAdapter.getMenuBounds();
      String prop = PROP_SHELL_MENU_BOUNDS;
      if( WidgetLCAUtil.hasChanged( menu, prop, menuBounds, null ) ) {
        // parameter order of setSpace: x, width, y, height
        Object[] args = new Object[] {
          new Integer( menuBounds.x ),
          new Integer( menuBounds.width ),
          new Integer( menuBounds.y ),
          new Integer( menuBounds.height )
        };
        writer.set( "space", args );
        writer.set( "clipWidth", menuBounds.width );
        writer.set( "clipHeight", menuBounds.height );
      }
      // We can't preserve values in the right phase because client-side changes
      // wouldn't be noticed this way (perserveValues happens after readData)
      IWidgetAdapter widgetAdapter = WidgetUtil.getAdapter( menu );
      widgetAdapter.preserve( PROP_SHELL_MENU_BOUNDS, menuBounds );
    }
  }
}
