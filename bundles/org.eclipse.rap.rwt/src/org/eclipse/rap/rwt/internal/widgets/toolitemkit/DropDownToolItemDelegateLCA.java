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

package org.eclipse.rap.rwt.internal.widgets.toolitemkit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolItem;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;

final class DropDownToolItemDelegateLCA extends ToolItemDelegateLCA {

  // part of the param name for the y-location of the menu
  private static final String BOUNDS_Y = ".boundsMenu.y";
  // part of the param name for the y-location of the menu
  private static final String BOUNDS_X = ".boundsMenu.x";
  // suffix (postfix) for the widget id of the second push button
  private static final String MENU_SUFFIX = "menu";
  //tool item functions as defined in org.eclipse.rap.rwt.ToolItemUtil
  private static final String CREATE_PUSH 
    = "org.eclipse.rap.rwt.ToolItemUtil.createToolItemPush";
  private static final String CREATE_PUSH_MENU 
    = "org.eclipse.rap.rwt.ToolItemUtil.createToolItemPushMenu";
  private static final String WIDGET_SELECTED 
    = "org.eclipse.rap.rwt.ToolItemUtil.widgetSelected";
  private static final String UPDATE_LISTENER 
    = "org.eclipse.rap.rwt.ToolItemUtil.addEventListenerForDropDownButton";
  // radio functions as defined in org.eclipse.rap.rwt.ButtonUtil
  private final static JSListenerInfo JS_LISTENER_INFO = 
    new JSListenerInfo( JSConst.QX_EVENT_CLICK,
                        WIDGET_SELECTED,
                        JSListenerType.ACTION );

  void readData( final ToolItem toolItem ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String widgetId = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( widgetId != null ) {
      if( WidgetUtil.getId( toolItem ).equals( widgetId ) ) {
        Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
        SelectionEvent event = newSelectionEvent( toolItem, bounds, RWT.NONE );
        event.processEvent();
      } else if( getMenuId( toolItem ).equals( widgetId ) ) {
        String paramX = request.getParameter( getMenuBoundsX( toolItem ) );
        int x = Integer.parseInt( paramX );
        String paramY = request.getParameter( getMenuBoundsY( toolItem ) );
        int y = Integer.parseInt( paramY );
        Rectangle bounds = new Rectangle( x, y, 0, 0 );
        SelectionEvent event = newSelectionEvent( toolItem, bounds, RWT.ARROW );
        event.processEvent();
      }
    }
  }

  void renderInitialization( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    Object[] args = new Object[] {
      WidgetUtil.getId( toolItem ),
      toolItem.getParent()
    };
    // TODO [rst] The first push button can also be created on the client side
    //      within the CREATE_PUSH_MENU method.
    writer.callStatic( CREATE_PUSH, args );
    boolean parentFlat = (toolItem.getParent().getStyle() & RWT.FLAT) != 0;
    // the second push button
    args = new Object[] {
      getMenuId( toolItem ),
      toolItem.getParent(),
      parentFlat ? "1" : ""
    };
    writer.callStatic( CREATE_PUSH_MENU, args );
    if (parentFlat) {
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
    // for Button the bounds are undefined
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( toolItem ) );
    // event handler for the second push button
    Object[] args = new Object[] {
      getMenuId( toolItem ),
      JSConst.QX_EVENT_CLICK,
      WIDGET_SELECTED
    };    
    writer.callStatic( UPDATE_LISTENER, args );
    ItemLCAUtil.writeFont( toolItem, toolItem.getParent().getFont() );
  }
  
  
  //////////////////
  // helping methods
  
  private static String getMenuBoundsY( final Widget widget ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( WidgetUtil.getId( widget ) );
    buffer.append( MENU_SUFFIX );
    buffer.append( BOUNDS_Y );
    return buffer.toString();
  }

  private static String getMenuBoundsX( final Widget widget ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( WidgetUtil.getId( widget ) );
    buffer.append( MENU_SUFFIX );
    buffer.append( BOUNDS_X );
    return buffer.toString();
  }

  private static String getMenuId( final Widget widget ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( WidgetUtil.getId( widget ) );
    buffer.append( MENU_SUFFIX );
    return buffer.toString();
  }
}
