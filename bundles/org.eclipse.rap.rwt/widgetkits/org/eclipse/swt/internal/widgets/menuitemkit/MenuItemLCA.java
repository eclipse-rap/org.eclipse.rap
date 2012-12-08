/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;


public final class MenuItemLCA extends AbstractWidgetLCA {

  private static final BarMenuItemLCA BAR_MENU_ITEM_LCA = new BarMenuItemLCA();
  private static final PushMenuItemLCA PUSH_MENU_ITEM_LCA = new PushMenuItemLCA();
  private static final CheckMenuItemLCA CHECK_MENU_ITEM_LCA = new CheckMenuItemLCA();
  private static final RadioMenuItemLCA RADIO_MENU_ITEM_LCA = new RadioMenuItemLCA();
  private static final SeparatorMenuItemLCA SEPARATOR_MENU_ITEM_LCA = new SeparatorMenuItemLCA();

  public void preserveValues( Widget widget ) {
    MenuItem item = ( MenuItem )widget;
    getDelegateLCA( item ).preserveValues( item );
  }

  public void readData( Widget widget ) {
    MenuItem item = ( MenuItem )widget;
    getDelegateLCA( item ).readData( item );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    MenuItem item = ( MenuItem )widget;
    getDelegateLCA( item ).renderInitialization( item );
  }

  public void renderChanges( Widget widget ) throws IOException {
    MenuItem item = ( MenuItem )widget;
    getDelegateLCA( item ).renderChanges( item );
  }

  private static boolean isTopLevelMenuBarItem( MenuItem item ) {
    return ( item.getParent().getStyle() & SWT.BAR ) != 0;
  }

  private static MenuItemDelegateLCA getDelegateLCA( MenuItem item ) {
    MenuItemDelegateLCA result;
    if( isTopLevelMenuBarItem( item ) ) {
      result = BAR_MENU_ITEM_LCA;
    } else if( ( item.getStyle() & ( SWT.PUSH | SWT.CASCADE ) ) != 0 ) {
      result = PUSH_MENU_ITEM_LCA;
    } else if( ( item.getStyle() & SWT.CHECK ) != 0 ) {
      result = CHECK_MENU_ITEM_LCA;
    } else if( ( item.getStyle() & SWT.RADIO ) != 0 ) {
      result = RADIO_MENU_ITEM_LCA;
    } else if( ( item.getStyle() & SWT.SEPARATOR ) != 0 ) {
      result = SEPARATOR_MENU_ITEM_LCA;
    } else {
      throw new IllegalStateException( "Unknown menu item type." );
    }
    return result;
  }
}
