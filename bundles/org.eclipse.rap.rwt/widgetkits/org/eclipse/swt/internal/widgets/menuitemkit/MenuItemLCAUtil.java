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
package org.eclipse.swt.internal.widgets.menuitemkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

final class MenuItemLCAUtil {

  private static final String TYPE = "rwt.widgets.MenuItem";

  private static final String PROP_MENU = "menu";
  private static final String PROP_ENABLED = "enabled";
  private static final String PROP_SELECTION = "selection";
  private static final String PROP_SELECTION_LISTENER = "selection";

  static void preserveValues( MenuItem item ) {
    WidgetLCAUtil.preserveCustomVariant( item );
    ItemLCAUtil.preserve( item );
    preserveProperty( item, PROP_MENU, item.getMenu() );
    preserveProperty( item, PROP_ENABLED, item.getEnabled() );
    preserveProperty( item, PROP_SELECTION, item.getSelection() );
    preserveListener( item, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( item ) );
    WidgetLCAUtil.preserveHelpListener( item );
  }

  static void renderInitialization( MenuItem item ) {
    IClientObject clientObject = ClientObjectFactory.getForWidget( item );
    clientObject.create( TYPE );
    Menu parent = item.getParent();
    clientObject.setProperty( "parent", WidgetUtil.getId( parent ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( item ) );
    clientObject.setProperty( "index", parent.indexOf( item ) );
  }

  static void renderChanges( MenuItem item ) throws IOException {
    WidgetLCAUtil.renderCustomVariant( item );
    ItemLCAUtil.renderChanges( item );
    WidgetLCAUtil.renderMenu( item, item.getMenu() );
    renderProperty( item, PROP_ENABLED, item.getEnabled(), true );
    renderProperty( item, PROP_SELECTION, item.getSelection(), false );
    renderListener( item, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( item ), false );
    WidgetLCAUtil.renderListenHelp( item );
  }

  static void processArmEvent( MenuItem item ) {
    Menu menu = item.getParent();
    if( WidgetLCAUtil.wasEventSent( menu, JSConst.EVENT_MENU_SHOWN ) ) {
      if( ArmEvent.hasListener( item ) ) {
        ArmEvent event = new ArmEvent( item );
        event.processEvent();
      }
    }
  }
}
