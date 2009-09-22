/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.events.DeselectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.MenuItem;


final class RadioMenuItemLCA extends MenuItemDelegateLCA {

  private static final String ITEM_TYPE_RADIO = "radio";
  private static final String PROP_SELECTION = "selection";
  private static final String PARAM_SELECTION = "selection";

  void preserveValues( final MenuItem menuItem ) {
    ItemLCAUtil.preserve( menuItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    boolean hasListener = SelectionEvent.hasListener( menuItem );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListener ) );
    adapter.preserve( PROP_SELECTION,
                      Boolean.valueOf( menuItem.getSelection() ) );
    MenuItemLCAUtil.preserveEnabled( menuItem );
    WidgetLCAUtil.preserveCustomVariant( menuItem );
    WidgetLCAUtil.preserveHelpListener( menuItem );
  }

  void readData( final MenuItem menuItem ) {
    if( readSelection( menuItem ) ) {
      processSelectionEvent( menuItem );
    }
    ControlLCAUtil.processSelection( menuItem, null, false );
    WidgetLCAUtil.processHelp( menuItem );
  }

  void renderInitialization( final MenuItem menuItem ) throws IOException {
    MenuItemLCAUtil.newItem( menuItem, 
                             "org.eclipse.rwt.widgets.MenuItem", 
                             ITEM_TYPE_RADIO );
  }

  // TODO [tb] : to MenuItemLCAUtil ?
  void renderChanges( final MenuItem menuItem ) throws IOException {
    MenuItemLCAUtil.writeImageAndText( menuItem );
    MenuItemLCAUtil.writeSelectionListener( menuItem ); 
    MenuItemLCAUtil.writeSelection( menuItem );
    MenuItemLCAUtil.writeEnabled( menuItem );
    WidgetLCAUtil.writeCustomVariant( menuItem );
    WidgetLCAUtil.writeHelpListener( menuItem );
  }

  void renderDispose( final MenuItem menuItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
//    writer.callStatic( "org.eclipse.swt.MenuUtil.disposeRadioMenuItem",
//                       new Object[] { menuItem } );
    writer.dispose();
  }

  /////////////////////////////////////////////
  // Helping methods to select radio menu items

  private boolean readSelection( final MenuItem item ) {
    String value = WidgetLCAUtil.readPropertyValue( item, PARAM_SELECTION );
    if( value != null ) {
      item.setSelection( Boolean.valueOf( value ).booleanValue() );
    }
    return value != null;
  }
  
  private static void processSelectionEvent( final MenuItem item ) {
    if( SelectionEvent.hasListener( item ) ) {
      int type = SelectionEvent.WIDGET_SELECTED;
      SelectionEvent event;
      if( item.getSelection() ) {
        event = new SelectionEvent( item, null, type );
      } else {
        event = new DeselectionEvent( item, null, type );
      }
      event.processEvent();
    }
  }
  

}
