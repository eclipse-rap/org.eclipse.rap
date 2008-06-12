/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.custom.ctabitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.widgets.IWidgetFontAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Widget;


public final class CTabItemLCA extends AbstractWidgetLCA {

  public static final String EVENT_ITEM_CLOSED
    = "org.eclipse.swt.events.ctabItemClosed";

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_BOUNDS = "bounds";
  private static final String PROP_SELECTED = "selected";
  private static final String PROP_SHOWING = "showing";
  private static final String PROP_UNSELECTED_CLOSE_VISIBLE
    = "unselectedCloseVisible";
  private static final String PROP_FIRST_ITEM = "firstItem";

  public void preserveValues( final Widget widget ) {
    CTabItem item = ( CTabItem )widget;
    CTabFolder parent = item.getParent();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.preserve( PROP_TEXT, getShortenedText( item ) );
    adapter.preserve( PROP_IMAGE, getImage( item ) );
    WidgetLCAUtil.preserveToolTipText( item, item.getToolTipText() );
    adapter.preserve( PROP_BOUNDS, item.getBounds() );
    boolean selected = item == parent.getSelection();
    adapter.preserve( PROP_SELECTED, Boolean.valueOf( selected ) );
    boolean closeVisible = parent.getUnselectedCloseVisible();
    adapter.preserve( PROP_UNSELECTED_CLOSE_VISIBLE,
                      Boolean.valueOf( closeVisible ) );
    adapter.preserve( PROP_SHOWING,
                      Boolean.valueOf( item.isShowing() ) );
    adapter.preserve( PROP_FIRST_ITEM,
                      Boolean.valueOf( item == item.getParent().getItem( 0 ) ) );
    preserveFont( item );
  }

  public void readData( final Widget widget ) {
    final CTabItem item = ( CTabItem )widget;
    if( WidgetLCAUtil.wasEventSent( item, EVENT_ITEM_CLOSED ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          CTabFolderEvent event = createCloseEvent( item );
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
    CTabFolder parent = item.getParent();
    JSWriter writer = JSWriter.getWriterFor( item );
    Object[] args = new Object[] {
      parent,
      Boolean.valueOf( showClose( item ) )
    };
    writer.newWidget( "org.eclipse.swt.custom.CTabItem", args );
    writer.call( parent, "add", new Object[] { item } );
    WidgetLCAUtil.writeCustomVariant( widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    CTabItem item = ( CTabItem )widget;
    WidgetLCAUtil.writeBounds( item, item.getParent(), item.getBounds() );
    writeText( item );
    writeImage( item );
    writeFont( item );
    WidgetLCAUtil.writeToolTip( item, item.getToolTipText() );
    writeShowing( item );
    writeUnselectedCloseVisible( item );
    writeSelection( item );
    writeFirstItem( item );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
  }

  public String getTypePoolId( final Widget widget ) {
    return null;
  }


  ////////////////////////////////////////////
  // Helping methods to render JavaScript code

  private static void writeText( final CTabItem item ) throws IOException {
    String text = getShortenedText( item );
    if( WidgetLCAUtil.hasChanged( item, Props.TEXT, text ) ) {
      text = WidgetLCAUtil.escapeText( text, false );
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( JSConst.QX_FIELD_LABEL, text );
    }
  }

  private static void writeImage( final CTabItem item ) throws IOException {
    Image newValue = getImage( item );
    if( WidgetLCAUtil.hasChanged( item, PROP_IMAGE, newValue, null ) ) {
      WidgetLCAUtil.writeImage( item, JSConst.QX_FIELD_ICON, newValue );
    }
  }

  private static void writeFont( final CTabItem item ) throws IOException {
    Object adapter = item.getAdapter( IWidgetFontAdapter.class );
    IWidgetFontAdapter fontAdapter = ( IWidgetFontAdapter )adapter;
    Font font = fontAdapter.getUserFont();
    WidgetLCAUtil.writeFont( item, font );
  }

  private static void writeSelection( final CTabItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    boolean selected = item == item.getParent().getSelection();
    Boolean newValue = Boolean.valueOf( selected );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( item, PROP_SELECTED, newValue, defValue ) ) {
      writer.set( "selected", Boolean.valueOf( selected ) );
    }
  }

  private static void writeFirstItem( final CTabItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    boolean isFirst = item == item.getParent().getItem( 0 );
    Boolean newValue = Boolean.valueOf( isFirst );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( item, PROP_FIRST_ITEM, newValue, defValue ) )
    {
      if( isFirst ) {
        writer.call( "addState", new Object[] { "firstItem" } );
      } else {
        writer.call( "removeState", new Object[] { "firstItem" } );
      }
    }
  }

  private static void writeShowing( final CTabItem item ) throws IOException {
    Boolean newValue = Boolean.valueOf( item.isShowing() );
    if( WidgetLCAUtil.hasChanged( item, PROP_SHOWING, newValue, Boolean.TRUE ) )
    {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( JSConst.QX_FIELD_VISIBLE, newValue );
    }
  }

  private static void writeUnselectedCloseVisible( final CTabItem item )
    throws IOException
  {
    CTabFolder parent = item.getParent();
    Boolean newValue = Boolean.valueOf( parent.getUnselectedCloseVisible() );
    String prop = PROP_UNSELECTED_CLOSE_VISIBLE;
    if( WidgetLCAUtil.hasChanged( item, prop, newValue, Boolean.TRUE ) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( "unselectedCloseVisible", newValue );
    }
  }

  ////////////////////////////////////////////
  // Helping methods to obtain item properties

  private static Image getImage( final CTabItem item ) {
    Image result = item.getImage();
    if( result != null ) {
      Object adapter = item.getParent().getAdapter( ICTabFolderAdapter.class );
      ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
      if( !folderAdapter.showItemImage( item ) ) {
        result = null;
      }
    }
    return result;
  }

  private static String getShortenedText( final CTabItem item ) {
    CTabFolder folder = item.getParent();
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ( ICTabFolderAdapter )adapter );
    return folderAdapter.getShortenedItemText( item );
  }

  private static boolean showClose( final CTabItem item ) {
    CTabFolder parent = item.getParent();
    ICTabFolderAdapter adapter
      = ( ICTabFolderAdapter )parent.getAdapter( ICTabFolderAdapter.class );
    boolean canClose = adapter.showItemClose( item );
    return canClose;
  }

 ///////////////
  // Event helper

  private static CTabFolderEvent createCloseEvent( final CTabItem item ) {
    CTabFolderEvent result
      = new CTabFolderEvent( item.getParent(), CTabFolderEvent.CLOSE );
    result.item = item;
    result.doit = true;
    return result;
  }

  //////////////////
  // Preserve helper

  private static void preserveFont( final CTabItem item ) {
    Object adapter = item.getAdapter( IWidgetFontAdapter.class );
    IWidgetFontAdapter fontAdapter = ( IWidgetFontAdapter )adapter;
    Font font = fontAdapter.getUserFont();
    WidgetLCAUtil.preserveFont( item, font );
  }
}