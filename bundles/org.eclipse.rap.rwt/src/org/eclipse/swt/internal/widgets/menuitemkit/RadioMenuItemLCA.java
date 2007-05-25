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

package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.MenuItem;


final class RadioMenuItemLCA extends MenuItemDelegateLCA {

  private static final String PROP_SELECTION = "selection";
  
  private static final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_EXECUTE, 
                          "org.eclipse.swt.MenuUtil.radioMenuItemSelected", 
                          JSListenerType.STATE_AND_ACTION );


  void preserveValues( final MenuItem menuItem ) {
    ItemLCAUtil.preserve( menuItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    boolean hasListener = SelectionEvent.hasListener( menuItem );
    adapter.preserve( Props.SELECTION_LISTENERS, 
                      Boolean.valueOf( hasListener ) );
    adapter.preserve( PROP_SELECTION, 
                      Boolean.valueOf( menuItem.getSelection() ) );
    adapter.preserve( Props.ENABLED,
                      Boolean.valueOf( menuItem.getEnabled() ) );
  }

  void readData( final MenuItem menuItem ) {
    String paramValue = WidgetLCAUtil.readPropertyValue( menuItem, "selection" );
    if( paramValue != null ) {
      boolean selection = Boolean.valueOf( paramValue ).booleanValue();
      if( selection ) {
        deselectRadioItems( menuItem );
      }
      menuItem.setSelection( selection );
    }
    ControlLCAUtil.processSelection( menuItem, null, false );
  }
  
  void renderInitialization( final MenuItem menuItem ) throws IOException {
    MenuItemLCAUtil.newItem( menuItem, "qx.ui.menu.RadioButton", true );
    MenuItem firstSiblingItem = getFirstSiblingRadioItem( menuItem );
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    if( firstSiblingItem == menuItem ) {
      writer.callStatic( "org.eclipse.swt.MenuUtil.createRadioManager", 
                         new Object[] { menuItem } );
    } else {
      writer.callStatic( "org.eclipse.swt.MenuUtil.assignRadioManager", 
                         new Object[] { firstSiblingItem, menuItem } );
    }
  }

  void renderChanges( final MenuItem menuItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    // TODO [rh] qooxdoo does not handle radio menu items with images, should
    //      we already ignore them when calling MenuItem#setImage()?
    MenuItemLCAUtil.writeImageAndText( menuItem );
    writer.updateListener( JS_LISTENER_INFO, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( menuItem ) );
    writer.set( PROP_SELECTION, 
                "checked", 
                Boolean.valueOf( menuItem.getSelection() ), 
                Boolean.FALSE );
    MenuItemLCAUtil.writeEnabled( menuItem );
  }

  void renderDispose( final MenuItem menuItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.callStatic( "org.eclipse.swt.MenuUtil.disposeRadioMenuItem", 
                       new Object[] { menuItem } );
  }

  /////////////////////////////////////////////
  // Helping methods to select radio menu items

  private static void deselectRadioItems( final MenuItem menuItem ) {
    MenuItem[] items = getSiblingRadioItems( menuItem );
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].setSelection( false );
    }
  }
  
  private static MenuItem[] getSiblingRadioItems( final MenuItem menuItem ) {
    java.util.List radioItems = new ArrayList();
    MenuItem[] siblingMenuItems = menuItem.getParent().getItems();
    int index = menuItem.getParent().indexOf( menuItem ) - 1;
    boolean isRadioItem = true;
    while( index >= 0 && isRadioItem ) {
      MenuItem item = siblingMenuItems[ index ];
      if( ( item.getStyle() & SWT.RADIO ) != 0 ) {
        radioItems.add( item );
      } else {
        isRadioItem = false;
      }
      index--;
    }
    index = menuItem.getParent().indexOf( menuItem ) + 1;
    isRadioItem = true;
    while( index < siblingMenuItems.length && isRadioItem ) {
      MenuItem item = siblingMenuItems[ index ];
      if( ( item.getStyle() & SWT.RADIO ) != 0 ) {
        radioItems.add( item );
      } else {
        isRadioItem = false;
      }
      index++;
    }
    MenuItem[] result = new MenuItem[ radioItems.size() ];
    radioItems.toArray( result );
    return result;
  }
  
  /////////////////////////////////////////////////////////
  // Helping methods to control client-side RadioManager(s)
  
  private static MenuItem getFirstSiblingRadioItem( final MenuItem menuItem ) {
    MenuItem result = null;
    MenuItem[] siblingMenuItems = menuItem.getParent().getItems();
    int index = menuItem.getParent().indexOf( menuItem ) - 1;
    while( index >= 0 && result == null ) {
      if( ( siblingMenuItems[ index ].getStyle() & SWT.RADIO ) == 0 ) {
        result = siblingMenuItems[ index + 1 ];
      }
      index--;
    }
    return result;
  }
}
