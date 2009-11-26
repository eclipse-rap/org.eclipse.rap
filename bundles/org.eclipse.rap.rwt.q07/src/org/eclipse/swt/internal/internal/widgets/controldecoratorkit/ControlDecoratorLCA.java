/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.internal.widgets.controldecoratorkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.ControlDecorator;
import org.eclipse.swt.widgets.Widget;

public class ControlDecoratorLCA extends AbstractWidgetLCA {

  private static final String PROP_IMAGE = "image";
  private static final String PROP_VISIBLE = "visible";
  static final String PROP_TEXT = "text";
  static final String PROP_SHOW_HOVER = "showHover";
  static final String PROP_SELECTION_LISTENERS = "selectionListeners";

  private static final JSListenerInfo SELECTION_LISTENER
    = new JSListenerInfo( "mousedown",
                          "this.onWidgetSelected",
                          JSListenerType.ACTION );

  private static final JSListenerInfo DEFAULT_SELECTION_LISTENER
    = new JSListenerInfo( "dblclick",
                          "this.onWidgetDefaultSelected",
                          JSListenerType.ACTION );

  public void preserveValues( final Widget widget ) {
    ControlDecorator decorator = ( ControlDecorator )widget;
    WidgetLCAUtil.preserveBounds( decorator, decorator.getBounds() );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( decorator );
    adapter.preserve( PROP_IMAGE, decorator.getImage() );
    adapter.preserve( PROP_TEXT, decorator.getText() );
    Boolean showHover = Boolean.valueOf( decorator.getShowHover() );
    adapter.preserve( PROP_SHOW_HOVER, showHover );
    adapter.preserve( PROP_VISIBLE, 
                      Boolean.valueOf( decorator.isVisible() ) );
    Boolean hasListener
      = Boolean.valueOf( SelectionEvent.hasListener( decorator ) );
    adapter.preserve( PROP_SELECTION_LISTENERS, hasListener );
  }
  
  public void readData( final Widget widget ) {
    readSelectionEvent( ( ControlDecorator )widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    ControlDecorator decorator = ( ControlDecorator )widget;
    JSWriter writer = JSWriter.getWriterFor( decorator );
    Object[] args = new Object[]{
      decorator.getParent()
    };
    writer.newWidget( "org.eclipse.rwt.widgets.ControlDecorator", args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ControlDecorator decorator = ( ControlDecorator )widget;
    WidgetLCAUtil.writeBounds( decorator,
                               decorator.getParent(),
                               decorator.getBounds() );
    WidgetLCAUtil.writeImage( decorator,
                              PROP_IMAGE,
                              "source",
                              decorator.getImage() );
    writeText( decorator );
    writeShowHover( decorator );
    writeVisible( decorator );
    writeSelectionListener( decorator );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  //////////////////////////////////////
  // Helping methods to write JavaScript

  private static void writeText( final ControlDecorator decorator )
    throws IOException
  {
    String newValue = decorator.getText();
    JSWriter writer = JSWriter.getWriterFor( decorator );
    writer.set( PROP_TEXT, "text", newValue, "" );
  }

  private static void writeShowHover( final ControlDecorator decorator )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( decorator.getShowHover() );
    JSWriter writer = JSWriter.getWriterFor( decorator );
    writer.set( PROP_SHOW_HOVER, PROP_SHOW_HOVER, newValue, Boolean.TRUE );
  }

  private static void writeVisible( final ControlDecorator decorator )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( decorator.isVisible() );
    Boolean defValue = Boolean.TRUE;
    JSWriter writer = JSWriter.getWriterFor( decorator );
    writer.set( PROP_VISIBLE, JSConst.QX_FIELD_VISIBLE, newValue, defValue );
  }

  private static void writeSelectionListener( final ControlDecorator decorator )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( decorator );
    writer.updateListener( SELECTION_LISTENER,
                           PROP_SELECTION_LISTENERS,
                           SelectionEvent.hasListener( decorator ) );
    writer.updateListener( DEFAULT_SELECTION_LISTENER,
                           PROP_SELECTION_LISTENERS,
                           SelectionEvent.hasListener( decorator ) );
  }

  ////////////////////////////////////////////////////
  // Helping methods to read client-side state changes

  private static void readSelectionEvent( final ControlDecorator decorator ) {
    String eventName = JSConst.EVENT_WIDGET_SELECTED;
    int eventId = SelectionEvent.WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( decorator, eventName ) ) {
      processSelectionEvent( decorator, eventId );
    }
    eventName = JSConst.EVENT_WIDGET_DEFAULT_SELECTED;
    eventId = SelectionEvent.WIDGET_DEFAULT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( decorator, eventName ) ) {
      processSelectionEvent( decorator, eventId );
    }
  }

  private static void processSelectionEvent( final ControlDecorator decorator,
                                             final int id )
  {
    Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
    int stateMask
      = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
    SelectionEvent event = new SelectionEvent( decorator,
                                               null,
                                               id,
                                               bounds,
                                               stateMask,
                                               "",
                                               true,
                                               SWT.NONE );
    event.processEvent();
  }
}
