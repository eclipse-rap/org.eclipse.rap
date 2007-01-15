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

package org.eclipse.rap.rwt.internal.widgets.menuitemkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.MenuItem;
import org.eclipse.rap.rwt.widgets.Widget;


public final class MenuItemLCA extends AbstractWidgetLCA {

  private static final BarMenuItemLCA BAR_MENU_ITEM_LCA 
    = new BarMenuItemLCA();
  private static final PushMenuItemLCA PUSH_MENU_ITEM_LCA 
    = new PushMenuItemLCA();
  private static final CheckMenuItemLCA CHECK_MENU_ITEM_LCA 
    = new CheckMenuItemLCA();
  private static final RadioMenuItemLCA RADIO_MENU_ITEM_LCA 
    = new RadioMenuItemLCA();
  private static final SeparatorMenuItemLCA SEPARATOR_MENU_ITEM_LCA 
    = new SeparatorMenuItemLCA();

  public void preserveValues( final Widget widget ) {
    MenuItem menuItem = ( MenuItem )widget;
    getDelegateLCA( menuItem ).preserveValues( menuItem );
  }
  
  public void readData( final Widget widget ) {
    MenuItem menuItem = ( MenuItem )widget;
    getDelegateLCA( menuItem ).readData( menuItem );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    MenuItem menuItem = ( MenuItem )widget;
    getDelegateLCA( menuItem ).renderInitialization( menuItem );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    MenuItem menuItem = ( MenuItem )widget;
    getDelegateLCA( menuItem ).renderChanges( menuItem );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    MenuItem menuItem = ( MenuItem )widget;
    getDelegateLCA( menuItem ).renderDispose( menuItem );
  }

  private static boolean isTopLevelMenuBarItem( final MenuItem menuItem ) {
    return ( menuItem.getParent().getStyle() & RWT.BAR ) != 0;
  }
  
  private static MenuItemDelegateLCA getDelegateLCA( final MenuItem menuItem ) 
  {
    MenuItemDelegateLCA result;
    if( isTopLevelMenuBarItem( menuItem ) ) {
      result = BAR_MENU_ITEM_LCA;
    } else if( ( menuItem.getStyle() & ( RWT.PUSH | RWT.CASCADE ) ) != 0 ) { 
      result = PUSH_MENU_ITEM_LCA;
    } else if( ( menuItem.getStyle() & RWT.CHECK ) != 0 ) {
      result = CHECK_MENU_ITEM_LCA;
    } else if( ( menuItem.getStyle() & RWT.RADIO ) != 0 ) {
      result = RADIO_MENU_ITEM_LCA;
    } else if( ( menuItem.getStyle() & RWT.SEPARATOR ) != 0 ) {
      result = SEPARATOR_MENU_ITEM_LCA;
    } else {
      throw new IllegalStateException( "Unknown menu item type." );
    }
    return result;
  }
}
