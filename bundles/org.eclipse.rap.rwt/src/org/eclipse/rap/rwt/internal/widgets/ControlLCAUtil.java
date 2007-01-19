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
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;

/**
 * TODO [rh] JavaDoc
 * <p></p>
 */
public class ControlLCAUtil {
  
  private static final String PROPERTY_X_LOCATION = "bounds.x";
  private static final String PROPERTY_Y_LOCATION = "bounds.y";
  private static final String PROPERTY_WIDTH = "bounds.width";
  private static final String PROPERTY_HEIGHT = "bounds.height";

  // Property name to preserve ActivateListener 
  private static final String ACTIVATE_LISTENER = "activateListener";

  private ControlLCAUtil() {
    // prevent instance creation
  }
  
  public static void preserveValues( final Control control ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.preserve( Props.BOUNDS, control.getBounds() );
    adapter.preserve( Props.TOOL_TIP_TEXT, control.getToolTipText() );
    adapter.preserve( Props.MENU, control.getMenu() );
    adapter.preserve( Props.VISIBLE, Boolean.valueOf( control.isVisible() ) );
    adapter.preserve( Props.FG_COLOR, control.getForeground() );
    adapter.preserve( Props.BG_COLOR, control.getBackground() );
    adapter.preserve( Props.CONTROL_LISTENERS, 
                      Boolean.valueOf( ControlEvent.hasListener( control ) ) );
    adapter.preserve( Props.FONT, control.getFont() );
    adapter.preserve( ACTIVATE_LISTENER, 
                      Boolean.valueOf( ActivateEvent.hasListener( control ) ) );
  }
  
  public static void readBounds( final Control control ) {
    int width = readControlWidth( control );
    int height = readControlHeight( control );
    int xLocation = readControlXLocation( control );
    int yLocation = readControlYLocation( control );
    control.setBounds( xLocation, yLocation, width, height );
  }
  
  public static void writeBounds( final Control control ) throws IOException {
    Composite parent = control.getParent();
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds(), false );
  }
  
  // TODO [rh] there seems to be a qooxdoo problem when trying to change the
  //      visibility of a newly created widget (no flushGlobalQueues was called)
  //      MSG: Modification of property "visibility" failed with exception: 
  //           Error - Element must be created previously!
  public static void writeVisblility( final Control control ) 
    throws IOException
  {
    // we only need getVisible here (not isVisible), as qooxdoo also hides/shows
    // contained controls
    Boolean newValue = Boolean.valueOf( control.getVisible() );
    Boolean defValue = Boolean.TRUE;
    if( WidgetUtil.hasChanged( control, Props.VISIBLE, newValue, defValue ) ) 
    {
      JSWriter writer = JSWriter.getWriterFor( control );
      writer.set( Props.VISIBLE, newValue );
    }
  }

  public static void writeEnablement( final Control control )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( control.isEnabled() );
    Boolean defValue = Boolean.TRUE;
    if( WidgetUtil.hasChanged( control, Props.ENABLED, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      writer.set( Props.ENABLED, newValue );
    }
  }

  public static void writeChanges( final Control control ) throws IOException {
    writeBounds( control );
    writeVisblility( control );
    writeEnablement( control );
    writeColors( control );
    writeFont( control );
    writeToolTip( control );
    writeMenu( control );
    writeActivateListener( control );
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
    WidgetLCAUtil.writeMenu( control, control.getMenu() );
  }
  
  public static void writeToolTip( final Control control ) 
    throws IOException 
  {
    WidgetLCAUtil.writeToolTip( control, control.getToolTipText() );
  }
  
  // TODO [rh] move this to WidgetLCAUtil, move test case along, change LCA's
  //      to use this instead of manually setting images
  public static void writeImage( final Widget widget, final Image image ) 
    throws IOException 
  {
    if( WidgetUtil.hasChanged( widget, Props.IMAGE, image, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      // work around qooxdoo, that interprets 'null' as an image path 
      String path = image == null ? "" : Image.getPath( image );
      writer.set( JSConst.QX_FIELD_ICON, path );
    }
  }

  public static void writeColors( final Control control ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    writer.set( Props.FG_COLOR,
                JSConst.QX_FIELD_COLOR,
                control.getForeground(),
                null );
    writer.set( Props.BG_COLOR,
                JSConst.QX_FIELD_BG_COLOR,
                control.getBackground(),
                null );
  }

  /**
   * Writes RWT style flags that must be handled on the client side (e.g.
   * <code>RWT.BORDER</code>). Flags are transmitted as qooxdoo <q>states</q>
   * that will be respected by the appearance that renders the widget.
   * 
   * @param widget
   * @throws IOException
   */
  public static void writeStyleFlags( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    if( ( widget.getStyle() & RWT.BORDER ) != 0 ) {
      writer.call( "addState", new Object[]{ "rwt_BORDER" } );
    }
    if( ( widget.getStyle() & RWT.FLAT ) != 0 ) {
      writer.call( "addState", new Object[]{ "rwt_FLAT" } );
    }
  }

  public static void writeFont( final Control control ) throws IOException {
    WidgetLCAUtil.writeFont( control, control.getFont() );
  }
  
  public static void writeActivateListener( final Control control ) 
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( ActivateEvent.hasListener( control ) );
    Boolean defValue = Boolean.FALSE;
    String prop = ACTIVATE_LISTENER;
    if( WidgetUtil.hasChanged( control, prop, newValue, defValue ) ) {
      String function = newValue.booleanValue()
                      ? "addActivateListenerWidget"
                      : "removeActivateListenerWidget";
      JSWriter writer = JSWriter.getWriterFor( control );
      Object[] args = new Object[] { control };
      writer.call( control.getShell(), function, args );
    }
  }
  
  public static void processSelection( final Widget widget, 
                                       final Item item, 
                                       final boolean readBounds )
  {
    if( WidgetUtil.wasEventSent( widget, JSConst.EVENT_WIDGET_SELECTED ) ) {
      Rectangle bounds;
      if( widget instanceof Control && readBounds ) {
        Control control = ( Control )widget;
        bounds = new Rectangle( readControlXLocation( control ), 
                                readControlYLocation( control ),
                                readControlWidth( control ),
                                readControlHeight( control ) );
      } else {
        bounds = new Rectangle( 0, 0, 0, 0 );
      }
      SelectionEvent event;
      event = new SelectionEvent( widget, 
                                  item,
                                  SelectionEvent.WIDGET_SELECTED,
                                  bounds,
                                  true,
                                  RWT.NONE );
      event.processEvent();
    }
  }
  
  //////////////////
  // helping methods

  private static int readControlYLocation( final Control control ) {
    String value = WidgetUtil.readPropertyValue( control, PROPERTY_Y_LOCATION );
    return readCoordinate( value, control.getBounds().y );
  }
  
  private static int readControlXLocation( final Control control ) {
    String value = WidgetUtil.readPropertyValue( control, PROPERTY_X_LOCATION );
    return readCoordinate( value, control.getBounds().x );
  }

  private static int readCoordinate( final String value, final int current ) {
    int result = 0;
    if( value != null && !"null".equals( value ) ) {
      result = Integer.parseInt( value );
    } else {
      result = current;
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
}
