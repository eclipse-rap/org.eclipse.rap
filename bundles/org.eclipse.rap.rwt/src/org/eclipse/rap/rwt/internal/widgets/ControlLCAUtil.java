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

package org.eclipse.rap.rwt.internal.widgets;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.ControlEvent;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.graphics.IColor;
import org.eclipse.rap.rwt.internal.widgets.toolitemkit.SeparatorToolItemDelegateLCA;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.engine.service.ContextProvider;

/**
 * TODO [rh] JavaDoc
 * <p></p>
 */
public class ControlLCAUtil {
  
  private static final String PROPERTY_X_LOCATION = "bounds.x";
  private static final String PROPERTY_Y_LOCATION = "bounds.y";
  private static final String PROPERTY_WIDTH = "bounds.width";
  private static final String PROPERTY_HEIGHT = "bounds.height";

  private ControlLCAUtil() {
    // prevent instance creation
  }
  
  public static void preserveValues( final Control control ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.preserve( Props.BOUNDS, control.getBounds() );
    adapter.preserve( Props.CONTROL_LISTENERS, 
                      Boolean.valueOf( ControlEvent.hasListener( control ) ) );
    adapter.preserve( Props.TOOL_TIP_TEXT, control.getToolTipText() );
    adapter.preserve( Props.MENU, control.getMenu() );
    adapter.preserve( Props.VISIBILITY, 
                      Boolean.valueOf( control.isVisible() ) );
  }
  
  public static void readBounds( final Control widget ) {
    int width = readControlWidth( widget );
    int height = readControlHeight( widget );
    int xLocation = readControlXLocation( widget );
    int yLocation = readControlYLocation( widget );
    widget.setBounds( xLocation, yLocation, width, height );
  }
  
  public static void setControlIntoToolItem ( final Control control ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    Runnable runnable = ( Runnable )adapter.getPreserved( 
              SeparatorToolItemDelegateLCA.SET_CONTROL_FOR_SEPARATOR_RUNNABLE );
    if (runnable!=null){
      runnable.run();
    }
  }
  
  public static void writeBounds( final Control control ) throws IOException {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    Rectangle oldBounds = ( Rectangle )adapter.getPreserved( Props.BOUNDS );
    Rectangle newBounds = control.getBounds();
    if( !adapter.isInitialized() || !newBounds.equals( oldBounds ) ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      int[] args = new int[] {
        newBounds.x, newBounds.width, newBounds.y, newBounds.height
      };
      writer.set( "space", args );
      if( !WidgetUtil.getAdapter( control ).isInitialized() ) {
        writer.set( "minWidth", 0 );
        writer.set( "minHeight", 0 );
      }
    }
  }
  public static void writeVisblility( final Control control ) 
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    // TODO: [Ralf] there is no set(String, String, boolean) method yet
    //              writer.set( Props.VISIBILITY, "visibility", control.isVisible() );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    Boolean isVisible = ( Boolean )adapter.getPreserved( Props.VISIBILITY );
    if(   !adapter.isInitialized()
        || isVisible == null
        || control.isVisible() != isVisible.booleanValue() )
    {
      writer.set( "visibility", control.isVisible() );
    }
  }

  public static void writeColors( final Control control ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    Color fgColor = control.getForeground();
    Color bgColor = control.getBackground();
    if( fgColor != null ) {
      writer.set( Props.FG_COLOR, "color", 
                  ( ( IColor )fgColor ).toColorValue() );
    }
    if( bgColor != null ) {
      writer.set( Props.BG_COLOR,
                  "backgroundColor",
                  ( ( IColor )bgColor ).toColorValue() );
    }
  }

  public static void writeChanges( final Control control ) throws IOException {
    writeBounds( control );
    writeVisblility( control );
    writeColors( control );
    writeToolTip( control );
    writeMenu( control );
    setControlIntoToolItem( control );
  }
  
  public static void writeResizeNotificator( final Widget widget )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.addListener( JSConst.QX_EVENT_CHANGE_WIDTH,
                        JSConst.JS_WIDGET_RESIZED );
    writer.addListener( JSConst.QX_EVENT_CHANGE_HEIGHT,
                        JSConst.JS_WIDGET_RESIZED );
  }
  
  public static void writeMoveNotificator( final Widget widget )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.addListener( JSConst.QX_EVENT_CHANGE_LOCATION_X,
                        JSConst.JS_WIDGET_MOVED );
    writer.addListener( JSConst.QX_EVENT_CHANGE_LOCATION_Y,
                        JSConst.JS_WIDGET_MOVED );
  }
  
  public static void writeMenu( final Control control ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    boolean hasChanged 
      = WidgetUtil.hasChanged( control, Props.MENU, control.getMenu(), null );
    if( hasChanged ) {
      writer.set( "contextMenu", control.getMenu() );
      if( control.getMenu() == null ) {
        writer.removeListener( JSConst.QX_EVENT_CONTEXTMENU, 
                               JSConst.JS_CONTEXT_MENU );
      } else {
        writer.addListener( JSConst.QX_EVENT_CONTEXTMENU, 
                            JSConst.JS_CONTEXT_MENU );
      }
    }
  }
  
  public static void writeToolTip( final Control control ) 
    throws IOException 
  {
    writeToolTip( control, control.getToolTipText() );
  }
  
  public static void writeToolTip( final Widget widget, final String newText ) 
    throws IOException 
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    if( adapter.isInitialized() ) {
      if( WidgetUtil.hasChanged( widget, Props.TOOL_TIP_TEXT, newText ) ) {
        doWriteToolTip( widget, newText );
      }
    } else if( newText != null && !"".equals( newText ) ) {
      doWriteToolTip( widget, newText );
    }
  }

  public static void processSelection( final Control control, 
                                       final Item item )
  {
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( control ).equals( id ) ) {
      Rectangle bounds = new Rectangle( readControlXLocation( control ), 
                                        readControlYLocation( control ),
                                        readControlWidth( control ),
                                        readControlHeight( control ) );
      SelectionEvent event = new SelectionEvent( control, 
                                                 item,
                                                 SelectionEvent.WIDGET_SELECTED,
                                                 bounds,
                                                 true,
                                                 RWT.NONE);
      event.processEvent();
    }
  }
  
  
  //////////////////
  // helping methods

  private static int readControlYLocation( final Control control ) {
    String value = WidgetUtil.readPropertyValue( control, PROPERTY_Y_LOCATION );
    int result = 0;
    if( value != null ) {
      result = Integer.parseInt( value );
    } else {
      result = control.getBounds().y;
    }
    return result;
  }

  private static int readControlXLocation( final Control control ) {
    String value = WidgetUtil.readPropertyValue( control, PROPERTY_X_LOCATION );
    int result = 0;
    if( value != null ) {
      result = Integer.parseInt( value );
    } else {
      result = control.getBounds().x;
    }
    return result;
  }

  private static int readControlWidth( final Control control ) {
    String value = WidgetUtil.readPropertyValue( control, PROPERTY_WIDTH );
    int result = 0;
    if( value != null ) {
      result = Integer.parseInt( value );
    } else {
      result = control.getBounds().width;
    }
    return result;
  }

  // TODO [rh] when maximizing/minimizing shell, the height might be 'null'.
  //      Should we be prepared here for invalid parameters (e.g. catch 
  //      NumberFormatExceptions) or ensure that the client side will not
  //      send illegal parameters
  private static int readControlHeight( final Control widget ) {
    String value = WidgetUtil.readPropertyValue( widget, PROPERTY_HEIGHT );
    int result = 0;
    if( value != null ) {
      result = Integer.parseInt( value );
    } else {
      result = widget.getBounds().height;
    }
    return result;
  }

  private static void doWriteToolTip( final Widget widget, final String text ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object[] args = new Object[] { widget, text };
    writer.call( JSWriter.WIDGET_MANAGER_REF, "setToolTip", args );
  }
}
