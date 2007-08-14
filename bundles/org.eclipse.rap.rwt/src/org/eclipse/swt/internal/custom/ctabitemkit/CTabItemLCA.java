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

package org.eclipse.swt.internal.custom.ctabitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.custom.ctabfolderkit.CTabFolderLCA;
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


  public void preserveValues( final Widget widget ) {
    CTabItem item = ( CTabItem )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_TEXT, item.getText() );
    adapter.preserve( PROP_IMAGE, getImage( item ) );
    WidgetLCAUtil.preserveToolTipText( widget, item.getToolTipText() );
    adapter.preserve( PROP_BOUNDS, item.getBounds() );
    boolean selected = item == item.getParent().getSelection();
    adapter.preserve( PROP_SELECTED, Boolean.valueOf( selected ) );
    boolean closeVisible = item.getParent().getUnselectedCloseVisible();
    adapter.preserve( PROP_UNSELECTED_CLOSE_VISIBLE, 
                      Boolean.valueOf( closeVisible ) );
    adapter.preserve( PROP_SHOWING, 
                      Boolean.valueOf( item.isShowing() ) );
    preserveFont( item );
  }

  public void readData( final Widget widget ) {
    final CTabItem item = ( CTabItem )widget;
    if( WidgetLCAUtil.wasEventSent( item, EVENT_ITEM_CLOSED ) ) {
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
    boolean canClose = ( item.getStyle() & SWT.CLOSE ) != 0;
    Object[] args = new Object[] { 
      Boolean.valueOf( canClose ),
      "Close"
    };
    writer.newWidget( "org.eclipse.swt.custom.CTabItem", args );
    writer.call( item.getParent(), "add", new Object[] { item } );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    CTabItem item = ( CTabItem )widget;
    Rectangle bounds = item.getBounds();
    WidgetLCAUtil.writeBounds( item, item.getParent(), bounds, true );
    writeText( item );
    writeImage( item );
    writeFont( item );
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

  public void createResetHandlerCalls( final String typePoolId ) throws IOException {
  }
  
  public String getTypePoolId( final Widget widget ) throws IOException {
    return null;
  }
  

  ////////////////////////////////////////////
  // Helping methods to render JavaScript code
  
  private static void preserveFont( final CTabItem item ) {
    Object adapter = item.getAdapter( IWidgetFontAdapter.class );
    IWidgetFontAdapter fontAdapter = ( IWidgetFontAdapter )adapter;
    Font font = fontAdapter.getUserFont();
    WidgetLCAUtil.preserveFont( item, font );
  }
  
  private void writeText( final CTabItem item ) throws IOException {
    String text = item.getText();
    if( WidgetLCAUtil.hasChanged( item, Props.TEXT, text ) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      text = WidgetLCAUtil.escapeText( text, false );
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

  private static void writeShowing( final CTabItem item ) throws IOException {
    Boolean newValue = Boolean.valueOf( item.isShowing() );
    if( WidgetLCAUtil.hasChanged( item, PROP_SHOWING, newValue, Boolean.TRUE ) ) 
    {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( JSConst.QX_FIELD_VISIBLE, newValue );
    }
  }

  private static void writeSelectionBackground( final CTabItem item ) 
    throws IOException 
  {
    String prop = CTabFolderLCA.PROP_SELECTION_BG;
    CTabFolder parent = item.getParent();
    Color newValue = parent.getSelectionBackground();
    boolean itemInitialized = WidgetUtil.getAdapter( item ).isInitialized();
    if( !itemInitialized || WidgetLCAUtil.hasChanged( parent, prop, newValue ) ) {
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
    if( !itemInitialized || WidgetLCAUtil.hasChanged( parent, prop, newValue ) ) {
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
}