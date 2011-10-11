/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rwt.lifecycle.JSWriter;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.*;


final class DropDownMenuLCA extends MenuDelegateLCA {

  void preserveValues( Menu menu ) {
    MenuLCAUtil.preserveEnabled( menu );
    MenuLCAUtil.preserveMenuListener( menu );
    MenuLCAUtil.preserveWidth( menu );
    WidgetLCAUtil.preserveCustomVariant( menu );
    WidgetLCAUtil.preserveHelpListener( menu );
  }
  
  void readData( Menu menu ) {
    MenuLCAUtil.readMenuEvent( menu );
    WidgetLCAUtil.processHelp( menu );
  }
  
  void renderInitialization( Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    // TODO [rh] check whether it is allowed (in SWT and/or Qooxdoo) to 
    //      assign a Menu to more than one MenuItem
    //      [rst] It's allowed in SWT but not in qooxdoo - we have a problem here
    writer.newWidget( "org.eclipse.rwt.widgets.Menu" );
    MenuItem[] menuItems = findReferringMenuItems( menu );
    for( int i = 0; i < menuItems.length; i++ ) {
      writer.call( menuItems[ i ], "setMenu", new Object[] { menu } );
    }
  }

  void renderChanges( Menu menu ) throws IOException {
    WidgetLCAUtil.writeCustomVariant( menu );
    MenuLCAUtil.writeEnabled( menu );
    MenuLCAUtil.writeMenuListener( menu );
    MenuLCAUtil.writeUnhideMenu( menu );
    WidgetLCAUtil.writeHelpListener( menu );
  }

  /**
   * <p>Returns all <code>MenuItems</code> whose <code>getMenu()</code> returns 
   * the given <code>menu</code>. An empty array is returned if no 
   * <code>MenuItem</code>s refer to the given <code>menu</code>.</p> 
   */
  private static MenuItem[] findReferringMenuItems( final Menu menu ) {
    final List<MenuItem> menuItems = new ArrayList<MenuItem>();
    Decorations parent = menu.getParent();
    WidgetTreeVisitor.accept( parent, new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        if( widget instanceof MenuItem ) {
          MenuItem menuItem = ( MenuItem )widget;
          if( menuItem.getMenu() == menu ) {
            menuItems.add( menuItem );
          }
        }
        // TODO [rh] find a way to cancel visitor after all menus are done?
        return true;
      }
    } );
    return menuItems.toArray( new MenuItem[ menuItems.size() ] );
  }
}
