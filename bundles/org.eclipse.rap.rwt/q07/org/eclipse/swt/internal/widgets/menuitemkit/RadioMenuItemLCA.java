/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.events.DeselectionEvent;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.MenuItem;


final class RadioMenuItemLCA extends MenuItemDelegateLCA {

  private static final String ITEM_TYPE_RADIO = "radio";

  void preserveValues( MenuItem menuItem ) {
    ItemLCAUtil.preserve( menuItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    boolean hasListener = SelectionEvent.hasListener( menuItem );
    adapter.preserve( Props.SELECTION_LISTENERS, Boolean.valueOf( hasListener ) );
    adapter.preserve( MenuItemLCAUtil.PROP_SELECTION, Boolean.valueOf( menuItem.getSelection() ) );
    MenuItemLCAUtil.preserveEnabled( menuItem );
    WidgetLCAUtil.preserveCustomVariant( menuItem );
    WidgetLCAUtil.preserveHelpListener( menuItem );
  }

  void readData( MenuItem menuItem ) {
    if( readSelection( menuItem ) ) {
      processSelectionEvent( menuItem );
    }
    ControlLCAUtil.processSelection( menuItem, null, false );
    WidgetLCAUtil.processHelp( menuItem );
    MenuItemLCAUtil.processArmEvent( menuItem );
  }

  void renderInitialization( MenuItem menuItem ) throws IOException {
    MenuItemLCAUtil.newItem( menuItem, "org.eclipse.rwt.widgets.MenuItem", ITEM_TYPE_RADIO );
    if( ( menuItem.getParent().getStyle() & SWT.NO_RADIO_GROUP ) != 0 ) {
      JSWriter writer = JSWriter.getWriterFor( menuItem );
      writer.set( "noRadioGroup", true );
    }
  }

  // TODO [tb] : to MenuItemLCAUtil ?
  void renderChanges( MenuItem menuItem ) throws IOException {
    MenuItemLCAUtil.writeImageAndText( menuItem );
    MenuItemLCAUtil.writeSelectionListener( menuItem );
    MenuItemLCAUtil.writeSelection( menuItem );
    MenuItemLCAUtil.writeEnabled( menuItem );
    WidgetLCAUtil.writeCustomVariant( menuItem );
    WidgetLCAUtil.writeHelpListener( menuItem );
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
