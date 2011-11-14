/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menukit;

import java.io.IOException;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


final class MenuLCAUtil {

  private static final int MENU_PADDING = 1;
  private static final int MENU_BORDER = 1;
  private static final int ITEM_LEFT_PADDING = 2;
  private static final int ITEM_RIGHT_PADDING = 4;
  private static final int ITEM_SPACING = 2;
  private static final int ITEM_IMAGE = 16;

  static final String PROP_ENABLED = "enabled";
  static final String PROP_WIDTH = "width";
  static final String PROP_MENU_LISTENER = "menuListener";

  public static void preserveEnabled( Menu menu ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    adapter.preserve( PROP_ENABLED, Boolean.valueOf( menu.getEnabled() ) );
  }

  public static void writeEnabled( Menu menu ) throws IOException {
    Boolean newValue = Boolean.valueOf( menu.getEnabled() );
    Boolean defValue = Boolean.TRUE;
    JSWriter writer = JSWriter.getWriterFor( menu );
    writer.set( PROP_ENABLED, JSConst.QX_FIELD_ENABLED, newValue, defValue );
  }

  public static void preserveMenuListener( Menu menu ) {
    Boolean hasListener = Boolean.valueOf( hasListener( menu ) );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    adapter.preserve( PROP_MENU_LISTENER, hasListener );
  }

  public static void writeMenuListener( Menu menu ) throws IOException {
    String prop = PROP_MENU_LISTENER;
    Boolean newValue = Boolean.valueOf( hasListener( menu ) );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( menu, prop, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( menu );
      Object[] args = new Object[]{ newValue };
      writer.call( "setHasMenuListener", args );
    }
  }

  public static void readMenuEvent( Menu menu ) {
    if( WidgetLCAUtil.wasEventSent( menu, JSConst.EVENT_MENU_SHOWN ) ) {
      MenuEvent event = new MenuEvent( menu, MenuEvent.MENU_SHOWN );
      event.processEvent();
    }
    if( WidgetLCAUtil.wasEventSent( menu, JSConst.EVENT_MENU_HIDDEN ) ) {
      MenuEvent event = new MenuEvent( menu, MenuEvent.MENU_HIDDEN );
      event.processEvent();
    }
  }

  /* (intentionally non-JavaDoc'ed)
   * Activates the menu if a menu event was received (in this case, only a
   * preliminary menu is displayed).
   */
  public static void writeUnhideMenu( Menu menu ) throws IOException {
    String eventId = JSConst.EVENT_MENU_SHOWN;
    if( WidgetLCAUtil.wasEventSent( menu, eventId ) ) {
      JSWriter writer = JSWriter.getWriterFor( menu );
      Boolean reveal = Boolean.valueOf( menu.getItemCount() > 0 );
      Object[] args = new Object[]{ reveal };
      writer.call( "unhideItems", args );
    }
  }

  static void preserveWidth( Menu menu ) {
    int width = computeWidth( menu );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    adapter.preserve( PROP_WIDTH, new Integer( width ) );
  }

  static void writeWidth( Menu menu ) throws IOException {
    int width = computeWidth( menu );
    JSWriter writer = JSWriter.getWriterFor( menu );
    writer.set( PROP_WIDTH, "width", new Integer( width ), null );
  }

  static int computeWidth( Menu menu ) {
    int maxItemWidth = 0;
    MenuItem[] items = menu.getItems();
    for( int i = 0; i < items.length; i++ ) {
      int width = MenuLCAUtil.getMenuItemWidth( items [ i ] );
      maxItemWidth = Math.max( width, maxItemWidth );
    }
    return maxItemWidth + MENU_PADDING * 2 + MENU_BORDER * 2;
  }

  private static int getMenuItemWidth( MenuItem menuItem ) {
    Font systemFont = menuItem.getDisplay().getSystemFont();
    int result
      = ITEM_LEFT_PADDING
      + ITEM_IMAGE
      + ITEM_SPACING
      + Graphics.stringExtent( systemFont, menuItem.getText() ).x
      + ITEM_SPACING
      + ITEM_IMAGE
      + ITEM_RIGHT_PADDING;
    return result;
  }

  private static boolean hasListener( Menu menu ) {
    boolean result = MenuEvent.hasListener( menu );
    if( !result ) {
      MenuItem[] items = menu.getItems();
      for( int i = 0; i < items.length && !result; i++ ) {
        result = ArmEvent.hasListener( items[ i ] );
      }
    }
    return result;
  }
}
