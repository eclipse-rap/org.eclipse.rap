/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.*;


final class ToolItemLCAUtil {

  private static final String PROP_VISIBLE = "visible";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_HOT_IMAGE = "hotImage";
  private static final String PROP_SELECTION = "selection";
  private static final String JS_PROP_SELECTION = "selection";
  private static final String QX_TYPE = "org.eclipse.rwt.widgets.ToolItem";

  private ToolItemLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( final ToolItem toolItem ) {
    ItemLCAUtil.preserve( toolItem );
    WidgetLCAUtil.preserveEnabled( toolItem, toolItem.getEnabled() );
    WidgetLCAUtil.preserveToolTipText( toolItem, toolItem.getToolTipText() );
    WidgetLCAUtil.preserveCustomVariant( toolItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem );
    adapter.preserve( PROP_VISIBLE, Boolean.valueOf( isVisible( toolItem ) ) );
    boolean hasListener = SelectionEvent.hasListener( toolItem );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListener ) );
    adapter.preserve( Props.BOUNDS, toolItem.getBounds() );
    adapter.preserve( Props.MENU, toolItem.getParent().getMenu() );
  }

  static void renderInitialization( final ToolItem toolItem,
                                    final String param )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    ToolBar toolBar = toolItem.getParent();
    Integer index = new Integer( toolBar.indexOf( toolItem ) );
    // TODO [tb] For the index, it is currently ignored that controls
    //           attached to a ToolItem use an index-slot of their own on
    //           the client, while they don't on the server. In theory,
    //           this could lead to an incorrect order of the items on the
    //           client, which is problematic with the keyboard-control
    //           and radio-groups.
    Boolean flat = Boolean.valueOf( ( toolBar.getStyle() & SWT.FLAT ) != 0 );
    writer.newWidget( QX_TYPE, new Object[]{ param, flat } );
    writer.call( toolBar, "addAt", new Object[]{ toolItem, index } );
    WidgetLCAUtil.writeStyleFlag( toolItem, SWT.FLAT, "FLAT" );
  }

  static void renderChanges( final ToolItem toolItem ) throws IOException {
    writeText( toolItem );
    writeImages( toolItem );
    writeVisible( toolItem );
    writeBounds( toolItem );
    writeSelectionListener( toolItem );
    WidgetLCAUtil.writeToolTip( toolItem, toolItem.getToolTipText() );
    WidgetLCAUtil.writeEnabled( toolItem, toolItem.getEnabled() );
    WidgetLCAUtil.writeCustomVariant( toolItem );
  }

  ////////////
  // Selection

  static void writeSelectionListener( final ToolItem toolItem )
    throws IOException
  {
    boolean hasListener = SelectionEvent.hasListener( toolItem );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( toolItem, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( toolItem );
      writer.set( "hasSelectionListener", newValue );
    }
  }

  static void preserveSelection( final ToolItem toolItem ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem );
    adapter.preserve( PROP_SELECTION,
                      Boolean.valueOf( toolItem.getSelection() ) );
  }

  static void writeSelection( final ToolItem toolItem ) throws IOException {
    Boolean newValue = Boolean.valueOf( toolItem.getSelection() );
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    writer.set( PROP_SELECTION, JS_PROP_SELECTION, newValue, Boolean.FALSE );
  }

  static void processSelection( final ToolItem toolItem ) {
    if( WidgetLCAUtil.wasEventSent( toolItem, JSConst.EVENT_WIDGET_SELECTED ) )
    {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      SelectionEvent event = newSelectionEvent( toolItem, bounds, SWT.NONE );
      event.processEvent();
    }
  }

  static SelectionEvent newSelectionEvent( final Widget widget,
                                           final Rectangle bounds,
                                           final int detail )
  {
    int stateMask
      = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
    return new SelectionEvent( widget,
                               null,
                               SelectionEvent.WIDGET_SELECTED,
                               bounds,
                               stateMask,
                               null,
                               true,
                               detail );
  }

  /////////////
  // Visibility

  static void writeVisible( final ToolItem item ) throws IOException {
    Object adapter = item.getAdapter( IToolItemAdapter.class );
    IToolItemAdapter tia = ( IToolItemAdapter )adapter;
    Boolean newValue = Boolean.valueOf( tia.getVisible() );
    Boolean defValue = Boolean.TRUE;
    JSWriter writer = JSWriter.getWriterFor( item );
    writer.set( PROP_VISIBLE, JSConst.QX_FIELD_VISIBLE, newValue, defValue );
  }

  private static boolean isVisible( final ToolItem toolItem ) {
    Object adapter = toolItem.getAdapter( IToolItemAdapter.class );
    IToolItemAdapter toolItemAdapter = ( IToolItemAdapter )adapter;
    return toolItemAdapter.getVisible();
  }

  //////////
  // Bounds

  static void writeBounds( final ToolItem toolItem ) throws IOException {
    Rectangle bounds = toolItem.getBounds();
    WidgetLCAUtil.writeBounds( toolItem, toolItem.getParent(), bounds );
  }

  ////////
  // Text

  static void writeText( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    String text = toolItem.getText();
    if( WidgetLCAUtil.hasChanged( toolItem, Props.TEXT, text, null ) ) {
      text = WidgetLCAUtil.escapeText( text, true );
      writer.set( "text", text.equals( "" ) ? null : text );
    }
  }

  ////////
  // Image

  static void preserveImages( final ToolItem toolItem ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem );
    adapter.preserve( PROP_IMAGE, getImage( toolItem ) );
    adapter.preserve( PROP_HOT_IMAGE, toolItem.getHotImage() );
  }

  static void writeImages( final ToolItem toolItem ) throws IOException {
    Image image = getImage( toolItem );
    if( WidgetLCAUtil.hasChanged( toolItem, PROP_IMAGE, image, null ) ) {
      writeImage( toolItem, "image", image );
    }
    Image hotImage = toolItem.getHotImage();
    if( WidgetLCAUtil.hasChanged( toolItem, PROP_HOT_IMAGE, hotImage, null ) ) {
      writeImage( toolItem, "hotImage", hotImage );
    }
  }

  private static void writeImage( final ToolItem toolItem,
                                  final String jsProperty,
                                  final Image image )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    Rectangle bounds = image != null ? image.getBounds() : null;
    Object[] args = new Object[] {
      getImagePath( image ),
      new Integer( bounds != null ? bounds.width : 0 ),
      new Integer( bounds != null ? bounds.height : 0 )
    };
    writer.set( jsProperty, args );
  }

  static Image getImage( final ToolItem toolItem ) {
    Image result;
    if( toolItem.getEnabled() && toolItem.getParent().getEnabled() ) {
      result = toolItem.getImage();
    } else {
      result = toolItem.getDisabledImage();
      if( result == null ) {
        result = toolItem.getImage();
      }
    }
    return result;
  }

  private static String getImagePath( final Image image ) {
    return image == null ? null : ResourceFactory.getImagePath( image );
  }
}
