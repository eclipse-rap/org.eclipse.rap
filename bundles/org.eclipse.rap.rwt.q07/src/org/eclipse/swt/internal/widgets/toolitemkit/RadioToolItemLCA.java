/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


final class RadioToolItemLCA extends ToolItemDelegateLCA {

  // tool item functions as defined in org.eclipse.swt.ToolItemUtil
  private static final String CREATE_RADIO
    = "org.eclipse.swt.ToolItemUtil.createRadio";
  // radio functions as defined in org.eclipse.swt.ToolItemUtil
  private static final String WIDGET_SELECTED
    = "org.eclipse.swt.ToolItemUtil.radioSelected";

  private final JSListenerInfo JS_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_SELECTED,
                          WIDGET_SELECTED,
                          JSListenerType.STATE_AND_ACTION );

  void preserveValues( final ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
    ToolItemLCAUtil.preserveImages( toolItem );
    ToolItemLCAUtil.preserveSelection( toolItem );
    WidgetLCAUtil.preserveCustomVariant( toolItem );
  }

  void readData( final ToolItem toolItem ) {
    if( WidgetLCAUtil.wasEventSent( toolItem, JSConst.EVENT_WIDGET_SELECTED ) )
    {
      ToolItem[] items = toolItem.getParent().getItems();
      for( int i = 0; i < items.length; i++ ) {
        if( ( items[ i ].getStyle() & SWT.RADIO ) != 0 ) {
          items[ i ].setSelection( false );
        }
      }
      toolItem.setSelection( true );
      ToolItemLCAUtil.processSelection( toolItem );
    }
  }

  void renderInitialization( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    ToolBar bar = toolItem.getParent();
    int itemIndex = bar.indexOf( toolItem );
    ToolItem neighbour = getRadioNeighbour( bar, itemIndex );
    Object[] args = new Object[] {
      WidgetUtil.getId( toolItem ),
      toolItem.getParent(),
      ToolItemLCAUtil.getClientSideIndex( toolItem ),
      toolItem.getSelection() ? "true" : null,
      neighbour
    };
    writer.callStatic( CREATE_RADIO, args );
    if( ( toolItem.getParent().getStyle() & SWT.FLAT ) != 0 ) {
      writer.call( "addState", new Object[]{ "rwt_FLAT" } );
    }
  }

  // [bm] we need to take both neighbours into account to find a possible
  //      radio group
  private static ToolItem getRadioNeighbour( ToolBar bar, int itemIndex ) {
    ToolItem neighbour = null;
    if( itemIndex > 0 ) {
      neighbour = bar.getItem( itemIndex - 1 );
    }
    if( neighbour == null || ( neighbour.getStyle() & SWT.RADIO ) == 0 ) {
      if( itemIndex < bar.getItemCount() - 1 ) {
        ToolItem nextItem = bar.getItem( itemIndex + 1 );
        if( ( nextItem.getStyle() & SWT.RADIO ) != 0 ) {
          neighbour = nextItem;
        }
      }
    }
    return neighbour;
  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    ItemLCAUtil.writeText( toolItem, false );
    ToolItemLCAUtil.writeImages( toolItem );
    WidgetLCAUtil.writeToolTip( toolItem, toolItem.getToolTipText() );
    WidgetLCAUtil.writeEnabled( toolItem, toolItem.getEnabled() );
    ToolItemLCAUtil.writeVisible( toolItem );
    ToolItemLCAUtil.writeBounds( toolItem );
    ToolItemLCAUtil.writeSelection( toolItem, toolItem.getSelection() );
    // TODO [rh] could be optimized in that way, that qooxdoo forwards the
    //      right-click on a toolbar item to the toolbar iteself if the toolbar
    //      item does not have a context menu assigned
    WidgetLCAUtil.writeMenu( toolItem, toolItem.getParent().getMenu() );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for ToolItem the bounds are undefined
    writer.updateListener( "manager" ,
                           JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( toolItem ) );
    WidgetLCAUtil.writeCustomVariant( toolItem );
  }
}
