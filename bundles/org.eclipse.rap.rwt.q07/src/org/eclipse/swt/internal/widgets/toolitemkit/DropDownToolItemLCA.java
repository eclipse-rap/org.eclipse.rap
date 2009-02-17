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
import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.widgets.Display;

final class DropDownToolItemLCA extends ToolItemDelegateLCA {

  private static final String DROP_DOWN_SUFFIX = "_dropDown";

  // tool item functions as defined in org.eclipse.swt.ToolItemUtil
  private static final String CREATE_DROP_DOWN
    = "org.eclipse.swt.ToolItemUtil.createDropDown";
  private static final String UPDATE_DROP_DOWN_LISTENER
    = "org.eclipse.swt.ToolItemUtil.updateDropDownListener";

  private final static JSListenerInfo JS_BUTTON_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_EXECUTE,
                          JSConst.JS_WIDGET_SELECTED,
                          JSListenerType.ACTION );

  void preserveValues( final ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
    ToolItemLCAUtil.preserveImages( toolItem );
    WidgetLCAUtil.preserveCustomVariant( toolItem );
  }

  void readData( final ToolItem toolItem ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String widgetId = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( toolItem ).equals( widgetId ) ) {
      ToolItemLCAUtil.processSelection( toolItem );
    } else {
      String toolItemId = getDropDownId( toolItem );
      if( toolItemId.equals( widgetId ) ) {
        Rectangle defaultValue = new Rectangle( 0, 0, 0, 0 );
        Rectangle bounds = WidgetLCAUtil.readBounds( toolItemId, defaultValue );
        Display display = toolItem.getDisplay();
        Point coords
          = display.map( null, toolItem.getParent(), bounds.x, bounds.y );
        bounds.x = coords.x;
        bounds.y = coords.y;
        SelectionEvent event
          = ToolItemLCAUtil.newSelectionEvent( toolItem, bounds, SWT.ARROW );
        event.processEvent();
      }
    }
  }

  void renderInitialization( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    Object[] args = new Object[] {
      WidgetUtil.getId( toolItem ),
      toolItem.getParent(),
      Boolean.valueOf( ( toolItem.getParent().getStyle() & SWT.FLAT ) != 0 )
    };
    writer.callStatic( CREATE_DROP_DOWN, args );
  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    ItemLCAUtil.writeText( toolItem, false );
    ToolItemLCAUtil.writeImages( toolItem );
    // TODO [rh] could be optimized in that way, that qooxdoo forwards the
    //      right-click on a toolbar item to the toolbar iteself if the toolbar
    //      item does not have a context menu assigned
    WidgetLCAUtil.writeMenu( toolItem, toolItem.getParent().getMenu() );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    writer.updateListener( JS_BUTTON_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( toolItem ) );
    writeDropDownListener( toolItem );
    WidgetLCAUtil.writeToolTip( toolItem, toolItem.getToolTipText() );
    WidgetLCAUtil.writeEnabled( toolItem, toolItem.getEnabled() );
    ToolItemLCAUtil.writeVisible( toolItem );
    ToolItemLCAUtil.writeBounds( toolItem );
    WidgetLCAUtil.writeCustomVariant( toolItem );
  }

  //////////////////
  // helping methods

  private static void writeDropDownListener( final ToolItem toolItem )
    throws IOException
  {
    Boolean value = Boolean.valueOf( SelectionEvent.hasListener( toolItem ) );
    String prop = Props.SELECTION_LISTENERS;
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( toolItem, prop, value, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( toolItem );
      Boolean remove = Boolean.valueOf( !value.booleanValue() );
      Object[] args = new Object[] { getDropDownId( toolItem ), remove };
      writer.callStatic( UPDATE_DROP_DOWN_LISTENER, args );
    }
  }

  private static String getDropDownId( final Widget widget ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( WidgetUtil.getId( widget ) );
    buffer.append( DROP_DOWN_SUFFIX );
    return buffer.toString();
  }
}
