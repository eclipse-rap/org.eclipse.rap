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

package org.eclipse.rap.rwt.internal.custom.ctabitemkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.*;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.custom.ctabfolderkit.CTabFolderLCA;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Widget;


public final class CTabItemLCA extends AbstractWidgetLCA {
  
  public static final String EVENT_ITEM_CLOSED
    = "org.eclipse.rap.rwt.events.ctabItemClosed";
  
  public static final String PROP_BOUNDS = "bounds";
  private static final String PROP_FONT = "font";
  public static final String PROP_SELECTED = "selected";
  public static final String PROP_SHOWING = "showing";
  public static final String PROP_UNSELECTED_CLOSE_VISIBLE 
    = "unselectedCloseVisible";


  public void preserveValues( final Widget widget ) {
    CTabItem item = ( CTabItem )widget;
    ItemLCAUtil.preserve( item );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.TOOL_TIP_TEXT, item.getToolTipText() );
    adapter.preserve( PROP_BOUNDS, item.getBounds() );
    boolean selected = item == item.getParent().getSelection();
    adapter.preserve( PROP_SELECTED, Boolean.valueOf( selected ) );
    boolean closeVisible = item.getParent().getUnselectedCloseVisible();
    adapter.preserve( PROP_UNSELECTED_CLOSE_VISIBLE, 
                      Boolean.valueOf( closeVisible ) );
    adapter.preserve( PROP_SHOWING, 
                      Boolean.valueOf( item.isShowing() ) );
    adapter.preserve( PROP_FONT, 
                      item.getFont() );
  }
  
  public void readData( final Widget widget ) {
    final CTabItem item = ( CTabItem )widget;
    if( WidgetUtil.wasEventSent( item, EVENT_ITEM_CLOSED ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          CTabFolderEvent event = CTabFolderEvent.close( item );
          event.processEvent();
          if( event.doit ) {
            item.dispose();
          }
        }
      } );
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    CTabItem item = ( CTabItem )widget;
    JSWriter writer = JSWriter.getWriterFor( item );
    boolean canClose = ( item.getStyle() & RWT.CLOSE ) != 0;
    Object[] args = new Object[] { 
      Boolean.valueOf( canClose ),
      "Close"
    };
    writer.newWidget( "org.eclipse.rap.rwt.custom.CTabItem", args );
    writer.call( item.getParent(), "add", new Object[] { item } );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    CTabItem item = ( CTabItem )widget;
    Rectangle bounds = item.getBounds();
    WidgetLCAUtil.writeBounds( item, item.getParent(), bounds, true );
    ItemLCAUtil.writeChanges( item );
    ItemLCAUtil.writeFont( item, item.getFont() );
    WidgetLCAUtil.writeToolTip( item, item.getToolTipText() );
    writeShowing( item );
    writeUnselectedCloseVisible( item );
    writeSelectionForeground( item );
    writeSelectionBackground( item );
    writeSelection( item );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  ////////////////////////////////////////////
  // Helping methods to render JavaScript code
  
  private static void writeSelection( final CTabItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    boolean selected = item == item.getParent().getSelection();
    Boolean newValue = Boolean.valueOf( selected );
    if( WidgetUtil.hasChanged( item, PROP_SELECTED, newValue, Boolean.FALSE ) ) 
    {
      writer.set( "selected", Boolean.valueOf( selected ) );
    }
  }

  private static void writeShowing( final CTabItem item ) throws IOException {
    Boolean newValue = Boolean.valueOf( item.isShowing() );
    if( WidgetUtil.hasChanged( item, PROP_SHOWING, newValue, Boolean.TRUE ) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( "visibility", newValue );
    }
  }

  private static void writeSelectionBackground( final CTabItem item ) 
    throws IOException 
  {
    String prop = CTabFolderLCA.PROP_SELECTION_BG;
    CTabFolder parent = item.getParent();
    Color newValue = parent.getSelectionBackground();
    boolean itemInitialized = WidgetUtil.getAdapter( item ).isInitialized();
    if( !itemInitialized || WidgetUtil.hasChanged( parent, prop, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( "selectionBackground", newValue );
    }
  }

  private static void writeSelectionForeground( final CTabItem item ) 
  throws IOException 
  {
    String prop = CTabFolderLCA.PROP_SELECTION_FG;
    CTabFolder parent = item.getParent();
    Color newValue = parent.getSelectionForeground();
    boolean itemInitialized = WidgetUtil.getAdapter( item ).isInitialized();
    if( !itemInitialized || WidgetUtil.hasChanged( parent, prop, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( "selectionForeground", newValue );
    }
  }
  
  private static void writeUnselectedCloseVisible( final CTabItem item )
    throws IOException
  {
    String prop = PROP_UNSELECTED_CLOSE_VISIBLE;
    Boolean newValue 
      = Boolean.valueOf( item.getParent().getUnselectedCloseVisible() );
    if( WidgetUtil.hasChanged( item, prop, newValue, Boolean.TRUE) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( "unselectedCloseVisible", newValue );
    }
  }
}