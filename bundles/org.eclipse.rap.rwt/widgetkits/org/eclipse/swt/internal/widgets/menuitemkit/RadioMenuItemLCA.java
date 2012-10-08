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

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.events.DeselectionEvent;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.widgets.MenuItem;


final class RadioMenuItemLCA extends MenuItemDelegateLCA {

  @Override
  void preserveValues( MenuItem item ) {
    MenuItemLCAUtil.preserveValues( item );
  }

  @Override
  void readData( MenuItem item ) {
    readSelection( item );
    processSelectionEvent( item ); // order is relevant
    WidgetLCAUtil.processHelp( item );
    MenuItemLCAUtil.processArmEvent( item );
  }

  @Override
  void renderInitialization( MenuItem item ) throws IOException {
    MenuItemLCAUtil.renderInitialization( item );
  }

  @Override
  void renderChanges( MenuItem item ) throws IOException {
    MenuItemLCAUtil.renderChanges( item );
  }

  /////////////////////////////////////////////
  // Helping methods to select radio menu items

  private boolean readSelection( MenuItem item ) {
    String value = WidgetLCAUtil.readPropertyValue( item, "selection" );
    if( value != null ) {
      item.setSelection( Boolean.valueOf( value ).booleanValue() );
    }
    return value != null;
  }

  private static void processSelectionEvent( MenuItem item ) {
    String eventName = ClientMessageConst.EVENT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( item, eventName ) ) {
      SelectionEvent event;
      if( item.getSelection() ) {
        event = new SelectionEvent( item, null, SelectionEvent.WIDGET_SELECTED );
      } else {
        event = new DeselectionEvent( item );
      }
      event.stateMask = EventLCAUtil.readStateMask( item, eventName );
      event.processEvent();
    }
  }

}
