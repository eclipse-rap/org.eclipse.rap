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

package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.ToolItem;

final class CheckToolItemDelegateLCA extends ToolItemDelegateLCA {

  // check functions as defined in org.eclipse.swt.ButtonUtil
  private static final String WIDGET_SELECTED 
    = "org.eclipse.swt.ButtonUtil.checkSelected";
  // tool item functions as defined in org.eclipse.swt.ToolItemUtil
  private static final String CREATE_CHECK 
    = "org.eclipse.swt.ToolItemUtil.createCheck";
  
  private final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_CHECKED,
                          WIDGET_SELECTED,
                          JSListenerType.STATE_AND_ACTION );

  void preserveValues( final ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem );
    adapter.preserve( "selection",
                      Boolean.valueOf( toolItem.getSelection() ) );
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
    ItemLCAUtil.writeChanges( toolItem );
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
    WidgetLCAUtil.writeFont( toolItem, toolItem.getParent().getFont() );
    WidgetLCAUtil.writeToolTip( toolItem, toolItem.getToolTipText() );
    WidgetLCAUtil.writeEnabled( toolItem, toolItem.isEnabled() );
    writeSelection( toolItem, toolItem.getSelection() );
  }
  
  void writeSelection( ToolItem toolItem, boolean selection ) throws IOException {
	  JSWriter writer = JSWriter.getWriterFor( toolItem );
	  Boolean newValue = Boolean.valueOf( selection );
	  Boolean defValue = Boolean.FALSE;
	  writer.set( "selection", "checked", newValue, defValue );
  }
}
