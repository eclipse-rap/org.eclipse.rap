/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


final class MenuLCAUtil {

  private static final String TYPE = "rwt.widgets.Menu";
  private static final String[] ALLOWED_STYLES = new String[] {
    "BAR", "DROP_DOWN", "POP_UP", "NO_RADIO_GROUP"
  };

  private static final String PROP_ENABLED = "enabled";
  private static final String PROP_SHOW_LISTENER = "Show";
  private static final String PROP_HIDE_LISTENER = "Hide";
  private static final String METHOD_UNHIDE_ITEMS = "unhideItems";

  static void preserveValues( Menu menu ) {
    WidgetLCAUtil.preserveCustomVariant( menu );
    preserveProperty( menu, PROP_ENABLED, menu.getEnabled() );
    preserveListener( menu, PROP_SHOW_LISTENER, hasShowListener( menu ) );
    preserveListener( menu, PROP_HIDE_LISTENER, hasHideListener( menu ) );
    WidgetLCAUtil.preserveHelpListener( menu );
  }

  static void renderInitialization( Menu menu ) {
    IClientObject clientObject = ClientObjectFactory.getClientObject( menu );
    clientObject.create( TYPE );
    clientObject.set( "style", WidgetLCAUtil.getStyles( menu, ALLOWED_STYLES ) );
  }

  static void renderChanges( Menu menu ) {
    WidgetLCAUtil.renderCustomVariant( menu );
    renderProperty( menu, PROP_ENABLED, menu.getEnabled(), true );
    renderListener( menu, PROP_SHOW_LISTENER, hasShowListener( menu ), false );
    renderListener( menu, PROP_HIDE_LISTENER, hasHideListener( menu ), false );
    WidgetLCAUtil.renderListenHelp( menu );
  }

  public static void readMenuEvent( Menu menu ) {
    if( WidgetLCAUtil.wasEventSent( menu, ClientMessageConst.EVENT_SHOW ) ) {
      menu.notifyListeners( SWT.Show, new Event() );
    }
    if( WidgetLCAUtil.wasEventSent( menu, ClientMessageConst.EVENT_HIDE ) ) {
      menu.notifyListeners( SWT.Hide, new Event() );
    }
  }

  /* (intentionally non-JavaDoc'ed)
   * Activates the menu if a menu event was received (in this case, only a
   * preliminary menu is displayed).
   */
  static void renderUnhideItems( Menu menu ) {
    if( WidgetLCAUtil.wasEventSent( menu, ClientMessageConst.EVENT_SHOW ) ) {
      Boolean reveal = Boolean.valueOf( menu.getItemCount() > 0 );
      IClientObject clientObject = ClientObjectFactory.getClientObject( menu );
      Map<String, Object> args = new HashMap<String, Object>();
      args.put( "reveal", reveal );
      clientObject.call( METHOD_UNHIDE_ITEMS, args );
    }
  }

  //////////////////
  // Helping methods

  private static boolean hasShowListener( Menu menu ) {
    boolean result = false;
    if( ( menu.getStyle() & SWT.BAR ) == 0 ) {
      result = menu.isListening( SWT.Show );
      if( !result ) {
        MenuItem[] items = menu.getItems();
        for( int i = 0; !result && i < items.length && !result; i++ ) {
          result = items[ i ].isListening( SWT.Arm );
        }
      }
    }
    return result;
  }

  private static boolean hasHideListener( Menu menu ) {
    boolean result = false;
    if( ( menu.getStyle() & SWT.BAR ) == 0 ) {
      result = menu.isListening( SWT.Hide );
    }
    return result;
  }

}
