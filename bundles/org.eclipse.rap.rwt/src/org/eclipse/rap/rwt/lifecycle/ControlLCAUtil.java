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
  
  private static final JSListenerInfo FOCUS_GAINED_LISTENER_INFO 
    = new JSListenerInfo( "focusin", 
                          "org.eclipse.rap.rwt.EventUtil.focusGained", 
                          JSListenerType.ACTION );
  private static final JSListenerInfo FOCUS_LOST_LISTENER_INFO 
    = new JSListenerInfo( "focusout", 
                          "org.eclipse.rap.rwt.EventUtil.focusLost", 
                          JSListenerType.ACTION );
  
  // Property names to preserve widget property values 
  private static final String PROP_BACKGROUND = "background";
  private static final String PROP_FOREGROUND = "foreground";
  private static final String PROP_ACTIVATE_LISTENER = "activateListener";
  private static final String PROP_FOCUS_LISTENER = "focusListener";
  
  private ControlLCAUtil() {
    // prevent instance creation
  }
  
  public static void preserveValues( final Control control ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.preserve( Props.BOUNDS, control.getBounds() );
    // TODO [rh] revise this (see also writeZIndex)
    if( !( control instanceof Shell ) ) {
      adapter.preserve( Props.Z_INDEX, new Integer( getZIndex( control ) ) );
    }
    WidgetLCAUtil.preserveToolTipText( control, control.getToolTipText() );
    adapter.preserve( Props.MENU, control.getMenu() );
    adapter.preserve( Props.VISIBLE, Boolean.valueOf( control.getVisible() ) );
    adapter.preserve( Props.ENABLED, Boolean.valueOf( control.isEnabled() ) );
    adapter.preserve( PROP_FOREGROUND, control.getForeground() );
    adapter.preserve( PROP_BACKGROUND, control.getBackground() );
    WidgetLCAUtil.preserveFont( control, control.getFont() );
    adapter.preserve( Props.CONTROL_LISTENERS, 
                      Boolean.valueOf( ControlEvent.hasListener( control ) ) );
    adapter.preserve( PROP_ACTIVATE_LISTENER, 
                      Boolean.valueOf( ActivateEvent.hasListener( control ) ) );
    if( ( control.getStyle() & RWT.NO_FOCUS ) == 0 ) {
      adapter.preserve( PROP_FOCUS_LISTENER, 
                        Boolean.valueOf( FocusEvent.hasListener( control ) ) );
    }
  }
  
  public static void readBounds( final Control control ) {
    Rectangle current = control.getBounds();
    Rectangle newBounds = WidgetLCAUtil.readBounds( control, current );
    control.setBounds( newBounds );
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
    writeForeground( control );
    writeBackground( control );
    writeFont( control );
    writeToolTip( control );
    writeMenu( control );
    writeActivateListener( control );
    writeFocusListener( control );
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

  public static void writeForeground( final Control control ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    Color color = control.getForeground();
    if( WidgetLCAUtil.hasChanged( control, PROP_FOREGROUND, color, null ) ) {
      Object[] args = new Object[] { control, color };
      writer.call( JSWriter.WIDGET_MANAGER_REF, "setForeground", args );
    }
  }

  public static void writeBackground( final Control control ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    Color newValue = control.getBackground();
    writer.set( PROP_BACKGROUND, JSConst.QX_FIELD_BG_COLOR, newValue, null );
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
    String prop = PROP_ACTIVATE_LISTENER;
    if( WidgetLCAUtil.hasChanged( control, prop, newValue, defValue ) ) {
      String function = newValue.booleanValue()
                      ? "addActivateListenerWidget"
                      : "removeActivateListenerWidget";
      JSWriter writer = JSWriter.getWriterFor( control );
      Object[] args = new Object[] { control };
      writer.call( control.getShell(), function, args );
    }
  }
  
  /**
   * Note that there is no corresponding readData metod to fire the focus events
   * that are send by the JavaScript event listeners that are registered below.
   * FocusEvents are thrown when the focus is changed programmatically and when
   * it is change by the user.
   * Therefore the methods in Display that maintain the current focusControl
   * also fire FocusEvents. The current client-side focusControl is read in
   * DisplayLCA#readData.
   */
  private static void writeFocusListener( final Control control ) 
    throws IOException 
  {
    if( ( control.getStyle() & RWT.NO_FOCUS ) == 0 ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      boolean hasListener = FocusEvent.hasListener( control );
      writer.updateListener( FOCUS_GAINED_LISTENER_INFO, 
                             PROP_FOCUS_LISTENER, 
                             hasListener );
      writer.updateListener( FOCUS_LOST_LISTENER_INFO, 
                             PROP_FOCUS_LISTENER, 
                             hasListener );
    }
  }

  public static void processSelection( final Widget widget, 
                                       final Item item, 
                                       final boolean readBounds )
  {
    String eventId = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( widget, eventId ) ) {
      SelectionEvent event;
      event = createSelectionEvent( widget,
                                    item,
                                    readBounds,
                                    SelectionEvent.WIDGET_SELECTED );
      event.processEvent();
    }
    eventId = JSConst.EVENT_WIDGET_DEFAULT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( widget, eventId ) ) {
      SelectionEvent event;
      event = createSelectionEvent( widget,
                                    item,
                                    readBounds,
                                    SelectionEvent.WIDGET_DEFAULT_SELECTED );
      event.processEvent();
    }
  }

  private static SelectionEvent createSelectionEvent( final Widget widget,
                                                      final Item item,
                                                      final boolean readBounds,
                                                      final int type )
  {
    Rectangle bounds;
    if( widget instanceof Control && readBounds ) {
      Control control = ( Control )widget;
      bounds = WidgetLCAUtil.readBounds( control, control.getBounds() ); 
    } else {
      bounds = new Rectangle( 0, 0, 0, 0 );
    }
    return new SelectionEvent( widget, item, type, bounds, true, RWT.NONE );
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
