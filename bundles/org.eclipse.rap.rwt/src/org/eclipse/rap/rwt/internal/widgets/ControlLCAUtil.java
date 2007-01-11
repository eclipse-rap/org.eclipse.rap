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
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.internal.graphics.IColor;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.W4TContext;
import com.w4t.util.browser.Mozilla;

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
    adapter.preserve( Props.CONTROL_LISTENERS, 
                      Boolean.valueOf( ControlEvent.hasListener( control ) ) );
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
    writeBounds( control, control.getParent(), control.getBounds(), false );
  }
  
  public static void writeBounds( final Widget widget, 
                                  final Control parent, 
                                  final Rectangle bounds, 
                                  final boolean clip ) 
    throws IOException 
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    // TODO [rh] replace code below with WidgetUtil.hasChanged
    Rectangle oldBounds = ( Rectangle )adapter.getPreserved( Props.BOUNDS );
    Rectangle newBounds = bounds;
    if( !adapter.isInitialized() || !newBounds.equals( oldBounds ) ) {
      
      // the RWT coordinates for client area differ in some cases to
      // the widget realisation of qooxdoo
      if( parent != null ) {
        AbstractWidgetLCA parentLCA = WidgetUtil.getLCA( parent );
        newBounds = parentLCA.adjustCoordinates( newBounds ); 
      }
      
      JSWriter writer = JSWriter.getWriterFor( widget );
      
      //////////////////////////////////////////////////////////////////
      // TODO: [fappel] height values of controls are not displayed 
      //                proper in mozilla. This is a very rude approximation
      //                and should be eighter solved in qooxdoo or by a more
      //                sophisticated approach...
      int[] args;
      if( W4TContext.getBrowser() instanceof Mozilla ) {
        if( newBounds.height > 5 ) {
          args = new int[] {
            newBounds.x, newBounds.width, newBounds.y, newBounds.height - 4
          };
        } else {
          args = new int[] {
            newBounds.x, newBounds.width, newBounds.y, newBounds.height
          };
        }
      } else {
        args = new int[] {
          newBounds.x, newBounds.width, newBounds.y, newBounds.height
        };
      }
      //////////////////////////////////////////////////////////////////
      
      writer.set( "space", args );
      if( !WidgetUtil.getAdapter( widget ).isInitialized() ) {
        writer.set( "minWidth", 0 );
        writer.set( "minHeight", 0 );
      }
      if( clip ) {
        writer.set( "clipHeight", args[ 3 ] );
        writer.set( "clipWidth", args[ 1 ] );
      }
    }
  }
  
  public static void writeVisblility( final Control control ) 
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( control.isVisible() );
    Boolean defValue = Boolean.TRUE;
    if( WidgetUtil.hasChanged( control, Props.VISIBLE, newValue, defValue ) ) 
    {
      JSWriter writer = JSWriter.getWriterFor( control );
      writer.set( "visibility", control.isVisible() );
    }
  }

  public static void writeChanges( final Control control ) throws IOException {
    writeBounds( control );
    writeVisblility( control );
    ControlLCAUtil.writeColors( control );
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
  
  public static void writeImage( final Widget widget, final Image newImage ) 
    throws IOException
  {
    if( WidgetUtil.hasChanged( widget, Props.IMAGE, newImage, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      // work around qooxdoo, that interprets 'null' as an image path 
      String path = newImage == null ? "" : Image.getPath( newImage );
      writer.set( JSConst.QX_FIELD_ICON, path );
    }
  }

  public static void writeColors( final Control control ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    Color fgColor = control.getForeground();
    Color bgColor = control.getBackground();
    if( fgColor != null ) {
      writer.set( Props.FG_COLOR, 
                  JSConst.QX_FIELD_COLOR, 
                  ( ( IColor )fgColor ).toColorValue() );
    }
    if( bgColor != null ) {
      writer.set( Props.BG_COLOR,
                  JSConst.QX_FIELD_BACKGROUND_COLOR,
                  ( ( IColor )bgColor ).toColorValue() );
    }
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
