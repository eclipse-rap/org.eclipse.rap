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

package org.eclipse.rap.rwt.lifecycle;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.widgets.*;

/**
 * TODO [rh] JavaDoc
 * <p></p>
 */
public class ControlLCAUtil {
  
  public static final int MAX_STATIC_ZORDER = 300;
  
  // Property name to preserve ActivateListener 
  private static final String ACTIVATE_LISTENER = "activateListener";

  private ControlLCAUtil() {
    // prevent instance creation
  }
  
  public static void preserveValues( final Control control ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.preserve( Props.BOUNDS, control.getBounds() );
    if( !( control instanceof Shell ) ) {
      adapter.preserve( Props.Z_INDEX, new Integer( getZIndex( control ) ) );
    }
    WidgetLCAUtil.preserveToolTipText( control, control.getToolTipText() );
    adapter.preserve( Props.MENU, control.getMenu() );
    adapter.preserve( Props.VISIBLE, Boolean.valueOf( control.getVisible() ) );
    adapter.preserve( Props.ENABLED, Boolean.valueOf( control.isEnabled() ) );
    adapter.preserve( Props.FG_COLOR, control.getForeground() );
    adapter.preserve( Props.BG_COLOR, control.getBackground() );
    adapter.preserve( Props.CONTROL_LISTENERS, 
                      Boolean.valueOf( ControlEvent.hasListener( control ) ) );
    adapter.preserve( Props.FONT, control.getFont() );
    adapter.preserve( ACTIVATE_LISTENER, 
                      Boolean.valueOf( ActivateEvent.hasListener( control ) ) );
  }
  
  public static void readBounds( final Control control ) {
    Rectangle current = control.getBounds();
    Rectangle newBounds = WidgetLCAUtil.readBounds( control, current );
    control.setBounds( newBounds );
  }
  
  public static void readSelectionEvent( final Widget widget ) {
    if( WidgetLCAUtil.wasEventSent( widget, JSConst.EVENT_WIDGET_DEFAULT_SELECTED ) ) {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      int detail = RWT.NONE;
      SelectionEvent event = new SelectionEvent( widget,
                                                 null,
                                                 SelectionEvent.WIDGET_DEFAULT_SELECTED,
                                                 bounds,
                                                 true,
                                                 detail );
      event.processEvent();
    }
  }
  
  public static void writeBounds( final Control control ) throws IOException {
    Composite parent = control.getParent();
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds(), false );
  }
  
  public static void writeZIndex( final Control control ) throws IOException {
    // TODO [rst] find out what moveAbove/Below does on SWT shells
    if( !( control instanceof Shell ) ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      Integer newValue = new Integer( getZIndex( control ) );
      writer.set( Props.Z_INDEX, JSConst.QX_FIELD_Z_INDEX, newValue, null );
    }
  }
  
  // TODO [rh] there seems to be a qooxdoo problem when trying to change the
  //      visibility of a newly created widget (no flushGlobalQueues was called)
  //      MSG: Modification of property "visibility" failed with exception: 
  //           Error - Element must be created previously!
  public static void writeVisible( final Control control ) 
    throws IOException
  {
    // we only need getVisible here (not isVisible), as qooxdoo also hides/shows
    // contained controls
    Boolean newValue = Boolean.valueOf( control.getVisible() );
    Boolean defValue = control instanceof Shell ? Boolean.FALSE : Boolean.TRUE;
    JSWriter writer = JSWriter.getWriterFor( control );
    writer.set( Props.VISIBLE, JSConst.QX_FIELD_VISIBLE, newValue, defValue );
  }

  public static void writeEnabled( final Control control )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( control.isEnabled() );
    JSWriter writer = JSWriter.getWriterFor( control );
    writer.set( Props.ENABLED, JSConst.QX_FIELD_ENABLED, newValue, Boolean.TRUE );
  }

  public static void writeChanges( final Control control ) throws IOException {
    writeBounds( control );
    writeZIndex( control );
    writeVisible( control );
    writeEnabled( control );
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
    if( WidgetLCAUtil.hasChanged( widget, Props.IMAGE, image, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      // work around qooxdoo, that interprets 'null' as an image path 
      String path = image == null ? "" : Image.getPath( image );
      writer.set( JSConst.QX_FIELD_ICON, path );
    }
  }

  public static void writeColors( final Control control ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    // Foreground color
    Color color = control.getForeground();
    if( WidgetLCAUtil.hasChanged( control, Props.FG_COLOR, color, null ) ) {
      Object[] args = new Object[] { control, color };
      writer.call( JSWriter.WIDGET_MANAGER_REF, "setForeground", args );
    }
    // Background color
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
    if( WidgetLCAUtil.hasChanged( control, prop, newValue, defValue ) ) {
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
    if( WidgetLCAUtil.wasEventSent( widget, JSConst.EVENT_WIDGET_SELECTED ) ) {
      Rectangle bounds;
      if( widget instanceof Control && readBounds ) {
        Control control = ( Control )widget;
        bounds = WidgetLCAUtil.readBounds( control, control.getBounds() ); 
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

  private static int getZIndex( final Control control ) {
    Object adapter = control.getAdapter( IControlAdapter.class );
    IControlAdapter controlAdapter = ( IControlAdapter )adapter;
    int max = MAX_STATIC_ZORDER;
    if( control.getParent() != null ) {
      max = Math.max( control.getParent().getChildrenCount(), max );
    }
    return max - controlAdapter.getIndex();
  }
}
