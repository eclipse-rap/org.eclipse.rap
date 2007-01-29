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

package org.eclipse.rap.rwt.widgets;

import java.util.List;
import org.eclipse.rap.rwt.internal.widgets.SlimList;

public final class MenuHolder {

  interface IMenuHolderAdapter {
    // marker interface
  }

  public static boolean isMenuHolder( final Widget widget ) {
    return widget.getAdapter( IMenuHolderAdapter.class ) != null;
  }

  public static void addMenu( final Widget widget, final Menu menu ) {
    getMenuHolder( widget ).addMenu( menu );
  }

  public static void removeMenu( final Widget widget, final Menu menu ) {
    getMenuHolder( widget ).removeMenu( menu );
  }

  public static int getMenuCount( final Widget widget ) {
    return getMenuHolder( widget ).getMenuCount();
  }

  public static Menu[] getMenus( final Widget widget ) {
    return getMenuHolder( widget ).getMenus();
  }
  private final List menus;

  MenuHolder() {
    menus = new SlimList();
  }

  private void addMenu( final Menu menu ) {
    menus.add( menu );
  }

  private void removeMenu( final Menu menu ) {
    menus.remove( menu );
  }

  private Menu[] getMenus() {
    Menu[] result = new Menu[ menus.size() ];
    menus.toArray( result );
    return result;
  }

  private int getMenuCount() {
    return menus.size();
  }

  // ////////////////
  // Helping methods
  private static MenuHolder getMenuHolder( final Widget widget ) {
    Object adapter = widget.getAdapter( IMenuHolderAdapter.class );
    return ( MenuHolder )adapter;
  }
}
