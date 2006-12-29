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
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;


public class MenuLCA extends AbstractWidgetLCA {

  private static final String SHOW_MENU 
    = "org.eclipse.rap.rwt.MenuUtil.showMenu";
  // pseudo-property that denotes the shell which uses a menu for its menu bar
  private static final String MENU_BAR_SHELL = "menuBarShell";
  private static final String MENU_BAR_SHELL_CLIENT_AREA 
    = "menuBarShellClientArea";

  public void preserveValues( final Widget widget ) {
    Menu menu = ( Menu )widget;
    if( MenuLCAUtil.isBar( menu ) ) {
      Shell menuBarShell = MenuLCAUtil.getMenuBarShell( menu );
      IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
      adapter.preserve( MENU_BAR_SHELL, menuBarShell );
      if( menuBarShell != null ) {
        Rectangle clientArea = menuBarShell.getClientArea();
        adapter.preserve( MENU_BAR_SHELL_CLIENT_AREA, clientArea );
      }
    }
  }
  
  public void readData( final Widget widget ) {
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    Menu menu = ( Menu )widget;
    JSWriter writer = JSWriter.getWriterFor( menu );
    if( MenuLCAUtil.isBar( menu ) ) {
       writer.newWidget( "qx.ui.menu.MenuBar" );
    } else if( MenuLCAUtil.isDropDown( menu ) ) {
      // TODO [rh] check whether it is allowed (in SWT and/or Qooxdoo) to 
      //      assign a Menu to more than one MenuItem
      writer.newWidget( "qx.ui.menu.Menu" );
      writer.call( "addToDocument", null );
      MenuItem[] menuItems = MenuLCAUtil.findReferringMenuItems( menu );
      for( int i = 0; i < menuItems.length; i++ ) {
        writer.call( menuItems[ i ], "setMenu", new Object[] { menu } );
      }
    } else if( MenuLCAUtil.isPopUp( menu ) ) {
      writer.newWidget( "qx.ui.menu.Menu" );
      writer.call( "addToDocument", null );
    }
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Menu menu = ( Menu )widget;
    if( MenuLCAUtil.isBar( menu ) ) {
      writeBarParent( menu );
      writeBarBounds( menu );
    } else if( MenuLCAUtil.isPopUp( menu ) && menu.isVisible() ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      Rectangle bounds = menu.getBounds();
      Object[] args = new Object[] {
        menu,
        new Integer( bounds.x ),
        new Integer( bounds.y )
      };
      writer.callStatic( SHOW_MENU, args );
      menu.setVisible( false );
    }
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  /////////////////////////////////////////////////////////////////
  // Helping method to write properties for Menu with style RWT.BAR
  
  private static void writeBarParent( Menu menu ) throws IOException {
    Shell menuBarShell = MenuLCAUtil.getMenuBarShell( menu );
    if( WidgetUtil.hasChanged( menu, MENU_BAR_SHELL, menuBarShell, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( menu );
      writer.set( "parent", menuBarShell );
    }
  }

  private static void writeBarBounds( final Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    Shell menuBarShell = MenuLCAUtil.getMenuBarShell( menu );
    Rectangle changedClientArea = null;
    if( menuBarShell != null ) {
      Rectangle clientArea = menuBarShell.getClientArea();
      String prop = MENU_BAR_SHELL_CLIENT_AREA;
      if( WidgetUtil.hasChanged( menu, prop, clientArea, null ) ) {
        changedClientArea = menuBarShell.getClientArea();
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
  }
}
