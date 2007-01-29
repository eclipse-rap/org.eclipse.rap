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
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.internal.widgets.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.Menu;
import org.eclipse.rap.rwt.widgets.Shell;


final class MenuBarLCA extends MenuDelegateLCA {

  // pseudo-property that denotes the shell which uses a menu for its menu bar
  private static final String PROP_SHELL 
    = "menuBarShell";
  private static final String PROP_SHELL_CLIENT_AREA 
    = "menuBarShellClientArea";
  
  void preserveValues( final Menu menu ) {
    Shell shell = MenuBarLCA.getShell( menu );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    adapter.preserve( PROP_SHELL, shell );
  }
  
  void readData( Menu menu ) {
  }

  void renderInitialization( final Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    writer.newWidget( "qx.ui.menu.MenuBar" );
  }

  void renderChanges( final Menu menu ) throws IOException {
    writeParent( menu );
    writeBounds( menu );
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
    Rectangle changedClientArea = null;
    if( shell != null ) {
      Rectangle clientArea = shell.getClientArea();
      String prop = PROP_SHELL_CLIENT_AREA;
      if( WidgetLCAUtil.hasChanged( menu, prop, clientArea, null ) ) {
        changedClientArea = shell.getClientArea();
      }
    }
    if( changedClientArea != null ) { 
      // parameter order of setSpace: x, width, y, height
      Object[] args = new Object[] { 
        new Integer( changedClientArea.x ), 
        new Integer( changedClientArea.width ), 
        new Integer( Shell.TITLE_BAR_HEIGHT + 5 ), 
        new Integer( Shell.MENU_BAR_HEIGHT )
      };
      writer.set( "space", args );
      writer.set( "clipWidth", changedClientArea.width );
      writer.set( "clipHeight", Shell.MENU_BAR_HEIGHT );
    }
    // We can't preserve values in the right phase because client-side changes
    // wouldn't be noticed this way (perserveValues happens after readData)
    if( shell != null ) {
      IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
      adapter.preserve( PROP_SHELL_CLIENT_AREA, shell.getClientArea() );
    }
  }
}
