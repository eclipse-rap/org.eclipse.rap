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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.rap.rwt.widgets.*;


/**
 * <p>Static utility methods used by <code>MenuLCA</code>.</p> 
 */
final class MenuLCAUtil {
  
  private MenuLCAUtil() {
    // prevent instantiation
  }
  
  /**
   * <p>Returns whether the given <code>menu</code> is a <code>BAR</code>.</p>
   */
  static boolean isBar( final Menu menu ) {
    return ( menu.getStyle() & RWT.BAR ) != 0;
  }
  
  /**
   * <p>Returns whether the given <code>menu</code> is a <code>DROP_DOWN</code>.
   * </p>
   */
  static boolean isDropDown( final Menu menu ) {
    return ( menu.getStyle() & RWT.DROP_DOWN ) != 0;
  }
  
  static boolean isPopUp( final Menu menu ) {
    return ( menu.getStyle() & RWT.POP_UP ) != 0;
  }

  /**
   * <p>Returns the shell that has the given <code>menu</code> set as its menu 
   * bar.</p>
   * <p>Returns <code>null</code> if the given <code>menu</code> is not a 
   * <code>BAR</code> or if it is not assigned to its parent shell as the menu 
   * bar.</p>
   */
  static Shell getMenuBarShell( final Menu menu ) {
    Shell result = null;
    if( menu.getParent().getMenuBar() == menu ) {
      result = menu.getParent();
    }
    return result;
  }

  /**
   * <p>Returns all <code>MenuItems</code> whose <code>getMenu()</code> returns 
   * the given <code>menu</code>. An empty array is returned if no 
   * <code>MenuItem</code>s refer to the given <code>menu</code>.</p> 
   */
  static MenuItem[] findReferringMenuItems( final Menu menu ) {
    final List menuItems = new ArrayList();
    Shell shell = menu.getParent();
    WidgetTreeVisitor.accept( shell, new  AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        if( widget instanceof MenuItem ) {
          MenuItem menuItem = ( MenuItem )widget;
          if( menuItem.getMenu() == menu ) {
            menuItems.add( menuItem );
          }
        }
        // TODO [rh] find a way to cancel visitor after all menus are done
        return true;
      }
    } );
    MenuItem[] result = new MenuItem[ menuItems.size() ];
    menuItems.toArray( result );
    return result;
  }
}
