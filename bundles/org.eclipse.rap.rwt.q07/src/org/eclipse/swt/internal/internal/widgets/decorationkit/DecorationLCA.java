/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.internal.widgets.decorationkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.Decoration;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class DecorationLCA extends AbstractWidgetLCA {

  static final String PROP_DESCRIPTION_TEXT = "descriptionText";
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
    Decoration decoration = ( Decoration )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( decoration );
    adapter.preserve( Props.BOUNDS, decoration.getBounds() );
    adapter.preserve( Props.IMAGE, decoration.getImage() );
    adapter.preserve( PROP_DESCRIPTION_TEXT, decoration.getDescriptionText() );
    Boolean showHover = Boolean.valueOf( decoration.getShowHover() );
    adapter.preserve( PROP_SHOW_HOVER, showHover );
    adapter.preserve( Props.VISIBLE, Boolean.valueOf( decoration.isVisible() ) );
    Boolean hasListener
      = Boolean.valueOf( SelectionEvent.hasListener( decoration ) );
    adapter.preserve( PROP_SELECTION_LISTENERS, hasListener );
  }

  public void readData( final Widget widget ) {
    Decoration decoration = ( Decoration )widget;
    readSelectionEvent( decoration );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Decoration decoration = ( Decoration )widget;
    JSWriter writer = JSWriter.getWriterFor( decoration );
    Object[] args = new Object[]{
      decoration.getParent()
    };
    writer.newWidget( "org.eclipse.rwt.widgets.Decoration", args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Decoration decoration = ( Decoration )widget;
    WidgetLCAUtil.writeBounds( decoration,
                               decoration.getParent(),
                               decoration.getBounds() );
    WidgetLCAUtil.writeImage( decoration,
                              Props.IMAGE,
                              "source",
                              decoration.getImage() );
    writeDescriptionText( decoration );
    writeShowHover( decoration );
    writeVisible( decoration );
    writeSelectionListener( decoration );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  //////////////////////////////////////
  // Helping methods to write JavaScript

  private static void writeDescriptionText( final Decoration decoration )
    throws IOException
  {
    String newValue = decoration.getDescriptionText();
    JSWriter writer = JSWriter.getWriterFor( decoration );
    writer.set( PROP_DESCRIPTION_TEXT, PROP_DESCRIPTION_TEXT, newValue, null );
  }

  private static void writeShowHover( final Decoration decoration )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( decoration.getShowHover() );
    JSWriter writer = JSWriter.getWriterFor( decoration );
    writer.set( PROP_SHOW_HOVER, PROP_SHOW_HOVER, newValue, Boolean.TRUE );
  }

  private static void writeVisible( final Decoration decoration )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( decoration.isVisible() );
    Boolean defValue = Boolean.TRUE;
    JSWriter writer = JSWriter.getWriterFor( decoration );
    writer.set( Props.VISIBLE, JSConst.QX_FIELD_VISIBLE, newValue, defValue );
  }

  private static void writeSelectionListener( final Decoration decoration )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( decoration );
    writer.updateListener( SELECTION_LISTENER,
                           PROP_SELECTION_LISTENERS,
                           SelectionEvent.hasListener( decoration ) );
    writer.updateListener( DEFAULT_SELECTION_LISTENER,
                           PROP_SELECTION_LISTENERS,
                           SelectionEvent.hasListener( decoration ) );
  }

  ////////////////////////////////////////////////////
  // Helping methods to read client-side state changes

  private static void readSelectionEvent( final Decoration decoration ) {
    String eventName = JSConst.EVENT_WIDGET_SELECTED;
    int eventId = SelectionEvent.WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( decoration, eventName ) ) {
      processSelectionEvent( decoration, eventId );
    }
    eventName = JSConst.EVENT_WIDGET_DEFAULT_SELECTED;
    eventId = SelectionEvent.WIDGET_DEFAULT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( decoration, eventName ) ) {
      processSelectionEvent( decoration, eventId );
    }
  }

  private static void processSelectionEvent( final Decoration decoration,
                                             final int id )
  {
    Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
    int stateMask
      = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
    SelectionEvent event = new SelectionEvent( decoration,
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
