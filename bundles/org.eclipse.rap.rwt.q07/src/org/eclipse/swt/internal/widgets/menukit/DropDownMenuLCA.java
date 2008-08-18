/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
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

  void preserveValues( final Menu menu ) {
    MenuLCAUtil.preserveEnabled( menu );
    MenuLCAUtil.preserveMenuListener( menu );
    MenuLCAUtil.preserveWidth( menu );
    WidgetLCAUtil.preserveCustomVariant( menu );
  }
  
  void readData( final Menu menu ) {
    MenuLCAUtil.readMenuEvent( menu );
  }
  
  void renderInitialization( final Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    // TODO [rh] check whether it is allowed (in SWT and/or Qooxdoo) to 
    //      assign a Menu to more than one MenuItem
    //      [rst] It's allowed in SWT but not in qooxdoo - we have a problem here
    writer.newWidget( "qx.ui.menu.Menu" );
    writer.call( "addToDocument", null );    
    MenuItem[] menuItems = DropDownMenuLCA.findReferringMenuItems( menu );
    for( int i = 0; i < menuItems.length; i++ ) {
      writer.call( menuItems[ i ], "setMenu", new Object[] { menu } );
    }
  }

  void renderChanges( final Menu menu ) throws IOException {
    MenuLCAUtil.writeEnabled( menu );
    MenuLCAUtil.writeMenuListener( menu );
    MenuLCAUtil.writeUnhideMenu( menu );
    MenuLCAUtil.writeWidth( menu );
    WidgetLCAUtil.writeCustomVariant( menu );
  }

  /**
   * <p>Returns all <code>MenuItems</code> whose <code>getMenu()</code> returns 
   * the given <code>menu</code>. An empty array is returned if no 
   * <code>MenuItem</code>s refer to the given <code>menu</code>.</p> 
   */
  private static MenuItem[] findReferringMenuItems( final Menu menu ) {
    final List menuItems = new ArrayList();
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
    MenuItem[] result = new MenuItem[ menuItems.size() ];
    menuItems.toArray( result );
    return result;
  }
}
