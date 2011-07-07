/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

final class MenuItemLCAUtil {

  static final String PROP_ENABLED = "enabled";
  static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  static final String PROP_SELECTION = "selection";
  static final String JS_PROP_SELECTION = "selection";

  static void newItem( MenuItem menuItem, String jsClass, String type ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.newWidget( jsClass, new String[]{ type } );
    int index = menuItem.getParent().indexOf( menuItem );
    writer.call( menuItem.getParent(),
                 "addMenuItemAt",
                 new Object[]{ menuItem, new Integer( index ) } );
  }

  static void preserveEnabled( MenuItem menuItem ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    adapter.preserve( Props.ENABLED, Boolean.valueOf( menuItem.getEnabled() ) );
  }

  static void writeEnabled( MenuItem menuItem ) throws IOException {
    Boolean newValue = Boolean.valueOf( menuItem.getEnabled() );
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.set( PROP_ENABLED, JSConst.QX_FIELD_ENABLED, newValue, Boolean.TRUE );
  }

  static void writeImageAndText( MenuItem menuItem ) throws IOException {
    String text = menuItem.getText();
    if( WidgetLCAUtil.hasChanged( menuItem, Props.TEXT, text ) ) {
      JSWriter writer = JSWriter.getWriterFor( menuItem );
      // Strip accelerator text
      int index = text.indexOf( "\t" );
      if( index != -1 ) {
        text = text.substring( 0, index );
      }
      text = WidgetLCAUtil.escapeText( text, true );
      writer.set( "text", text.equals( "" ) ? null : text );
    }
    writeImage( menuItem );
  }

  static void writeImage( MenuItem item ) throws IOException {
    Image image = item.getImage();
    if( WidgetLCAUtil.hasChanged( item, Props.IMAGE, image, null ) ) {
      String imagePath = ImageFactory.getImagePath( image );
      JSWriter writer = JSWriter.getWriterFor( item );
      Rectangle bounds = image != null ? image.getBounds() : null;
      Object[] args = new Object[]{
        imagePath,
        new Integer( bounds != null ? bounds.width : 0 ),
        new Integer( bounds != null ? bounds.height : 0 )
      };
      writer.set( "image", args );
    }
  }

  static void writeSelectionListener( MenuItem menuItem ) throws IOException {
    boolean hasListener = SelectionEvent.hasListener( menuItem );
    Boolean newValue = Boolean.valueOf( hasListener );
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.set( PROP_SELECTION_LISTENERS, "hasSelectionListener", newValue, Boolean.FALSE );
  }

  static void writeSelection( MenuItem menuItem ) throws IOException {
    Boolean newValue = Boolean.valueOf( menuItem.getSelection() );
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.set( PROP_SELECTION, JS_PROP_SELECTION, newValue, Boolean.FALSE );
  }

  static void processArmEvent( MenuItem menuItem ) {
    Menu menu = menuItem.getParent();
    String eventId = JSConst.EVENT_MENU_SHOWN;
    if( WidgetLCAUtil.wasEventSent( menu, eventId ) ) {
      if( ArmEvent.hasListener( menuItem ) ) {
        ArmEvent event = new ArmEvent( menuItem );
        event.processEvent();
      }
    }
  }
}
