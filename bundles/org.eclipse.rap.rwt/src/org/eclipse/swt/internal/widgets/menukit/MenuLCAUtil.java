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

package org.eclipse.swt.internal.widgets.menukit;

import java.io.IOException;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.Menu;

final class MenuLCAUtil {
  
  private static final String SET_MENU_LISTENER
    = "org.eclipse.swt.MenuUtil.setMenuListener";
  private static final String UNHIDE_MENU
    = "org.eclipse.swt.MenuUtil.unhideMenu";
  private static final String PROP_MENU_LISTENER = "menuListener";
  
  public static void preserveMenuListener( final Menu menu ) {
    Boolean hasListener = Boolean.valueOf( MenuEvent.hasListener( menu ) );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    adapter.preserve( PROP_MENU_LISTENER, hasListener );
  }
  
  public static void writeEnabled( final Menu menu ) throws IOException {
    Boolean newValue = Boolean.valueOf( menu.isEnabled() );
    JSWriter writer = JSWriter.getWriterFor( menu );
    writer.set( Props.ENABLED, JSConst.QX_FIELD_ENABLED, newValue, Boolean.TRUE );
  }
  
  public static void writeMenuListener( final Menu menu ) throws IOException {
    Boolean newValue = Boolean.valueOf( MenuEvent.hasListener( menu ) );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( menu, PROP_MENU_LISTENER, newValue, defValue ) )
    {
      JSWriter writer = JSWriter.getWriterFor( menu );
      Object[] args = new Object[]{ menu, newValue };
      writer.callStatic( SET_MENU_LISTENER, args );
    }
  }
  
  public static void readMenuEvent( final Menu menu ) {
    if( WidgetLCAUtil.wasEventSent( menu, JSConst.EVENT_MENU_SHOWN ) ) {
      MenuEvent event = new MenuEvent( menu, MenuEvent.MENU_SHOWN );
      event.processEvent();
    }
    if( WidgetLCAUtil.wasEventSent( menu, JSConst.EVENT_MENU_HIDDEN ) ) {
      MenuEvent event = new MenuEvent( menu, MenuEvent.MENU_HIDDEN );
      event.processEvent();
    }
  }
  
  /**
   * Activates the menu if a menu event was received (in this case, only a
   * preliminary menu is displayed).
   */
  public static void writeUnhideMenu( final Menu menu ) throws IOException {
    String eventId = JSConst.EVENT_MENU_SHOWN;
    if( WidgetLCAUtil.wasEventSent( menu, eventId ) ) {
      JSWriter writer = JSWriter.getWriterFor( menu );
      Object[] args = new Object[]{ menu };
      writer.callStatic( UNHIDE_MENU, args );
    }
  }
}
