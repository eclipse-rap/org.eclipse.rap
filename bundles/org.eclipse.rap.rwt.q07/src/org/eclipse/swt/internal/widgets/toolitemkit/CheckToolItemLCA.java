/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.ToolItem;


final class CheckToolItemLCA extends ToolItemDelegateLCA {

  // tool item functions as defined in org.eclipse.swt.ToolItemUtil
  private static final String WIDGET_SELECTED
    = "org.eclipse.swt.ToolItemUtil.checkSelected";
  private static final String CREATE_CHECK
    = "org.eclipse.swt.ToolItemUtil.createCheck";
  
  private static final String PROP_SELECTION = "selection";

  private final JSListenerInfo JS_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_CHECKED,
                          WIDGET_SELECTED,
                          JSListenerType.STATE_AND_ACTION );

  void preserveValues( final ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
    ToolItemLCAUtil.preserveImage( toolItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem );
    adapter.preserve( PROP_SELECTION,
                      Boolean.valueOf( toolItem.getSelection() ) );
    WidgetLCAUtil.preserveCustomVariant( toolItem );
  }

  void readData( final ToolItem toolItem ) {
    if( WidgetLCAUtil.wasEventSent( toolItem, JSConst.EVENT_WIDGET_SELECTED ) ) {
      String value = WidgetLCAUtil.readPropertyValue( toolItem, "selection" );
      toolItem.setSelection( Boolean.valueOf( value ).booleanValue() );
      ToolItemLCAUtil.processSelection( toolItem );
    }
  }

  void renderInitialization( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    Object[] args = new Object[] {
      WidgetUtil.getId( toolItem ),
      toolItem.getParent()
    };
    writer.callStatic( CREATE_CHECK, args );    
    writer.set( "checked", toolItem.getSelection() );
    if( ( toolItem.getParent().getStyle() & SWT.FLAT ) != 0 ) {
      writer.call( "addState", new Object[]{ "rwt_FLAT" } );
    }
  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    ItemLCAUtil.writeText( toolItem, false );
    ToolItemLCAUtil.writeImage( toolItem );
    // TODO [rh] could be optimized in that way, that qooxdoo forwards the
    //      right-click on a toolbar item to the toolbar iteself if the toolbar
    //      item does not have a context menu assigned
    WidgetLCAUtil.writeMenu( toolItem, toolItem.getParent().getMenu() );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for ToolItem the bounds are undefined
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( toolItem ) );
    WidgetLCAUtil.writeToolTip( toolItem, toolItem.getToolTipText() );
    WidgetLCAUtil.writeEnabled( toolItem, toolItem.getEnabled() );
    ToolItemLCAUtil.writeVisible( toolItem );
    ToolItemLCAUtil.writeBounds( toolItem );
    writeSelection( toolItem, toolItem.getSelection() );
    WidgetLCAUtil.writeCustomVariant( toolItem );
  }

  static void writeSelection( final ToolItem toolItem, final boolean selection )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    Boolean newValue = Boolean.valueOf( selection );
    Boolean defValue = Boolean.FALSE;
    writer.set( PROP_SELECTION, "checked", newValue, defValue );
  }
}
