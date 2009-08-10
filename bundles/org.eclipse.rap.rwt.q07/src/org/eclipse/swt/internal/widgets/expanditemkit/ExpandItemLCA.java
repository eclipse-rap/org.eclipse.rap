/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.expanditemkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.IExpandBarAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public final class ExpandItemLCA extends AbstractWidgetLCA {

  // Request parameters that denote ExpandEvents
  public static final String EVENT_ITEM_EXPANDED
    = "org.eclipse.swt.events.expandItemExpanded";
  public static final String EVENT_ITEM_COLLAPSED
    = "org.eclipse.swt.events.expandItemCollapsed";
  // Property names for preserveValues
  public static final String PROP_TEXT = Props.TEXT;
  public static final String PROP_IMAGE = Props.IMAGE;
  public static final String PROP_BOUNDS = Props.BOUNDS;
  public static final String PROP_EXPANDED = "expanded";
  public static final String PROP_ENABLED = "enabled";
  public static final String PROP_HEADER_HEIGHT = "headerHeight";
  public static final Integer DEFAULT_HEADER_HEIGHT = new Integer( 24 );

  public void preserveValues( final Widget widget ) {
    ExpandItem expandItem = ( ExpandItem )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_TEXT, expandItem.getText() );
    adapter.preserve( PROP_IMAGE, expandItem.getImage() );
    adapter.preserve( PROP_EXPANDED,
                      Boolean.valueOf( expandItem.getExpanded() ) );
    adapter.preserve( PROP_ENABLED,
                      Boolean.valueOf( expandItem.getParent().isEnabled() ) );
    adapter.preserve( PROP_HEADER_HEIGHT,
                      new Integer( expandItem.getHeaderHeight() ) );
    IExpandBarAdapter expandBarAdapter = getExpandBarAdapter( expandItem.getParent() );
    adapter.preserve( PROP_BOUNDS, expandBarAdapter.getBounds( expandItem ) );
  }

  public void readData( final Widget widget ) {
    final ExpandItem expandItem = ( ExpandItem )widget;
    if( WidgetLCAUtil.wasEventSent( expandItem, EVENT_ITEM_EXPANDED ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          expandItem.setExpanded( true );
          ExpandEvent event = createExpandEvent( expandItem );
          event.processEvent();
        }
      } );
    }
    if( WidgetLCAUtil.wasEventSent( expandItem, EVENT_ITEM_COLLAPSED ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          expandItem.setExpanded( false );
          ExpandEvent event = createCollapseEvent( expandItem );
          event.processEvent();
        }
      } );
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    ExpandItem expandItem = ( ExpandItem )widget;
    ExpandBar parent = expandItem.getParent();
    JSWriter writer = JSWriter.getWriterFor( expandItem );
    Object[] args = new Object[]{ parent };
    writer.newWidget( "org.eclipse.swt.widgets.ExpandItem", args );
    writer.call( parent, "add", new Object[]{ expandItem } );
    WidgetLCAUtil.writeCustomVariant( widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ExpandItem expandItem = ( ExpandItem )widget;
    writeText( expandItem );
    writeImage( expandItem );
    writeExpanded( expandItem );
    writeEnabled( expandItem );
    writeHeaderHeight( expandItem );
    IExpandBarAdapter adapter = getExpandBarAdapter( expandItem.getParent() );
    WidgetLCAUtil.writeBounds( expandItem,
                               expandItem.getParent(),
                               adapter.getBounds( expandItem ) );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  ////////////////
  // Event helper

  private static ExpandEvent createExpandEvent( final ExpandItem expandItem ) {
    ExpandEvent event = new ExpandEvent( expandItem.getParent(),
                                         expandItem,
                                         ExpandEvent.EXPANDED );
    event.item = expandItem;
    return event;
  }

  private static ExpandEvent createCollapseEvent( final ExpandItem expandItem )
  {
    ExpandEvent event = new ExpandEvent( expandItem.getParent(),
                                         expandItem,
                                         ExpandEvent.COLLAPSED );
    event.item = expandItem;
    return event;
  }

  ////////////////////////////////////////////
  // Helping methods to render JavaScript code

  private static void writeText( final ExpandItem item ) throws IOException {
    String text = item.getText();
    if( WidgetLCAUtil.hasChanged( item, Props.TEXT, text ) ) {
      text = WidgetLCAUtil.escapeText( text, false );
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( PROP_TEXT, text );
    }
  }

  private static void writeImage( final ExpandItem item ) throws IOException {
    Image image = item.getImage();
    if( WidgetLCAUtil.hasChanged( item, PROP_IMAGE, image, null ) ) {
      String imagePath;
      if( image == null ) {
        imagePath = null;
      } else {
        imagePath = ResourceFactory.getImagePath( image );
      }
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( PROP_IMAGE, imagePath );
    }
  }

  private static void writeExpanded( final ExpandItem item ) throws IOException
  {
    Boolean newValue = Boolean.valueOf( item.getExpanded() );
    if( WidgetLCAUtil.hasChanged( item,
                                  PROP_EXPANDED,
                                  newValue,
                                  Boolean.FALSE ) )
    {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( PROP_EXPANDED, newValue );
    }
  }

  private static void writeEnabled( final ExpandItem item ) throws IOException {
    Boolean newValue = Boolean.valueOf( item.getParent().isEnabled() );
    if( WidgetLCAUtil.hasChanged( item,
                                  PROP_ENABLED,
                                  newValue,
                                  Boolean.TRUE ) )
    {
      JSWriter writer = JSWriter.getWriterFor( item );
      if( newValue.booleanValue() ) {
        writer.call( "addState", new Object[] {
          "enabled"
        } );
      } else {
        writer.call( "removeState", new Object[] {
          "enabled"
        } );
      }
    }
  }

  private static void writeHeaderHeight( final ExpandItem item )
    throws IOException
  {
    Integer newValue = new Integer( item.getHeaderHeight() );
    if( WidgetLCAUtil.hasChanged( item,
                                  PROP_HEADER_HEIGHT,
                                  newValue,
                                  DEFAULT_HEADER_HEIGHT ) )
    {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( PROP_HEADER_HEIGHT, newValue );
    }
  }

  private static IExpandBarAdapter getExpandBarAdapter( final ExpandBar bar ) {
    return ( IExpandBarAdapter )bar.getAdapter( IExpandBarAdapter.class );
  }
}
