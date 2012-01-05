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
package org.eclipse.swt.internal.widgets.menukit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


final class MenuLCAUtil {

  private static final String TYPE = "rwt.widgets.Menu";
  private static final String[] ALLOWED_STYLES = new String[] {
    "BAR", "DROP_DOWN", "POP_UP", "NO_RADIO_GROUP"
  };

  private static final String PROP_ENABLED = "enabled";
  private static final String PROP_MENU_LISTENER = "menu";
  private static final String METHOD_UNHIDE_ITEMS = "unhideItems";

  static void preserveValues( Menu menu ) {
    WidgetLCAUtil.preserveCustomVariant( menu );
    preserveProperty( menu, PROP_ENABLED, menu.getEnabled() );
    preserveListener( menu, PROP_MENU_LISTENER, hasMenuListener( menu) );
    WidgetLCAUtil.preserveHelpListener( menu );
  }

  static void renderInitialization( Menu menu ) {
    IClientObject clientObject = ClientObjectFactory.getForWidget( menu );
    clientObject.create( TYPE );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( menu, ALLOWED_STYLES ) );
  }

  static void renderChanges( Menu menu ) throws IOException {
    WidgetLCAUtil.renderCustomVariant( menu );
    renderProperty( menu, PROP_ENABLED, menu.getEnabled(), true );
    renderListener( menu, PROP_MENU_LISTENER, hasMenuListener( menu ), false );
    WidgetLCAUtil.renderListenHelp( menu );
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
  static void renderUnhideItems( Menu menu ) {
    if( WidgetLCAUtil.wasEventSent( menu, JSConst.EVENT_MENU_SHOWN ) ) {
      Boolean reveal = Boolean.valueOf( menu.getItemCount() > 0 );
      IClientObject clientObject = ClientObjectFactory.getForWidget( menu );
      Map<String, Object> args = new HashMap<String, Object>();
      args.put( "reveal", reveal );
      clientObject.call( METHOD_UNHIDE_ITEMS, args );
    }
  }

  //////////////////
  // Helping methods

  private static boolean hasMenuListener( Menu menu ) {
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
