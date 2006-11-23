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
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolItem;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;

public class DropDownToolItemDelegateLCA extends ToolItemDelegateLCA {

  // part of the param name for the y-location of the menu
  private static final String BOUNDS_Y = ".boundsMenu.y";
  // part of the param name for the y-location of the menu
  private static final String BOUNDS_X = ".boundsMenu.x";
  // suffix (postfix) for the widget id of the second push button
  private static final String MENU_SUFFIX = "menu";
  //tool item functions as defined in org.eclipse.rap.rwt.ToolItemUtil
  private static final String CREATE_PUSH = 
    "org.eclipse.rap.rwt.ToolItemUtil.createToolItemPush";
  private static final String CREATE_PUSH_MENU = 
    "org.eclipse.rap.rwt.ToolItemUtil.createToolItemPushMenu";
  private static final String WIDGET_SELECTED = 
    "org.eclipse.rap.rwt.ToolItemUtil.widgetSelected";
  private static final String UPDATE_LISTENER = 
    "org.eclipse.rap.rwt.ToolItemUtil.addEventListenerForDropDownButton";
  // radio functions as defined in org.eclipse.rap.rwt.ButtonUtil
  private final static JSListenerInfo JS_LISTENER_INFO = 
    new JSListenerInfo( JSConst.QX_EVENT_CLICK,
                        WIDGET_SELECTED,
                        JSListenerType.ACTION );

  public void delegateProcessAction( final Widget widget ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    
    if( WidgetUtil.getId( widget ).equals( id ) ) {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      SelectionEvent event = newSelectionEvent( widget, bounds ,RWT.NONE);
      event.processEvent();
    } else if( getMenuId( widget ).equals( id ) ) {
      String paramX = request.getParameter( getMenuBoundsX( widget ) );
      int x = Integer.parseInt( paramX );
      String paramY = request.getParameter( getMenuBoundsY( widget ) );
      int y = Integer.parseInt( paramY );
      Rectangle bounds = new Rectangle( x, y, 0, 0 );
      SelectionEvent event = newSelectionEvent( widget, bounds ,RWT.ARROW );
      event.processEvent();
    }
  }

  public void delegateRenderInitialization( final Widget widget ) 
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    ToolItem push = ( ToolItem )widget;
    Object[] args = new Object[] {
      WidgetUtil.getId( push ),
      push.getParent()
    };
    writer.callStatic( CREATE_PUSH, args );
    // the second push button
    args = new Object[] {
      getMenuId( push ),
      push.getParent()
    };
    writer.callStatic( CREATE_PUSH_MENU, args );
  }
  
  public void delegateRenderChanges( final Widget widget ) throws IOException {
    ToolItem push = ( ToolItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( push ));
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, push.getText() );
    if( push.getImage() != null ) {
      writer.set( Props.IMAGE, 
                  JSConst.QX_FIELD_ICON,
                  Image.getPath( push.getImage() ) );
    }
    // event handler for the second push button
    Object[] args = new Object[] {
      getMenuId( push ),
      JSConst.QX_EVENT_CLICK,
      WIDGET_SELECTED
    };    
    writer.callStatic( UPDATE_LISTENER, args );
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
