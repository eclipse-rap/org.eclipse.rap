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

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.events.DeselectionEvent;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.widgets.MenuItem;


final class RadioMenuItemLCA extends MenuItemDelegateLCA {

  void preserveValues( MenuItem item ) {
    MenuItemLCAUtil.preserveValues( item );
  }

  void readData( MenuItem item ) {
    if( readSelection( item ) ) {
      processSelectionEvent( item );
    }
    ControlLCAUtil.processSelection( item, null, false );
    WidgetLCAUtil.processHelp( item );
    MenuItemLCAUtil.processArmEvent( item );
  }

  void renderInitialization( MenuItem item ) throws IOException {
    MenuItemLCAUtil.renderInitialization( item );
  }

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
    if( SelectionEvent.hasListener( item ) ) {
      int type = SelectionEvent.WIDGET_SELECTED;
      SelectionEvent event;
      if( item.getSelection() ) {
        event = new SelectionEvent( item, null, type );
      } else {
        event = new DeselectionEvent( item, null, type );
      }
      event.stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
      event.processEvent();
    }
  }

}
