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

package org.eclipse.swt.internal.widgets.tablekit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.*;


public final class TableLCA extends AbstractWidgetLCA {

  // Property names to preserve values
  private static final String PROP_HEADER_HEIGHT = "headerHeight";
  private static final String PROP_HEADER_VISIBLE = "headerVisible";
  private static final String PROP_LINES_VISIBLE = "linesVisible";
  private static final String PROP_ITEM_HEIGHT = "itemHeight";
  private static final String PROP_TOP_INDEX = "topIndex";
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  private static final String PROP_DEFAULT_COLUMN_WIDTH = "defaultColumnWidth";
  private static final String PROP_ITEM_COUNT = "itemCount";

  private static final Integer DEFAULT_TOP_INDEX = new Integer( 0 );
  private static final Integer DEFAULT_ITEM_COUNT = new Integer( 0 );
  private static final Integer DEFAUT_ITEM_HEIGHT = new Integer( 0 );
  private static final Integer DEFAULT_DEFAULT_COLUMN_WIDTH = new Integer( 0 );

  private static final JSListenerInfo SELECTION_LISTENER
    = new JSListenerInfo( "itemselected", 
                          "this.onItemSelected", 
                          JSListenerType.ACTION );
  
  private static final JSListenerInfo DEFAULT_SELECTION_LISTENER
    = new JSListenerInfo( "itemdefaultselected", 
                          "this.onItemDefaultSelected", 
                          JSListenerType.ACTION );
  
  private static final JSListenerInfo CHECK_SELECTION_LISTENER
    = new JSListenerInfo( "itemchecked", 
                          "this.onItemChecked", 
                          JSListenerType.ACTION );

  
  public void preserveValues( final Widget widget ) {
    Table table = ( Table )widget;
    ControlLCAUtil.preserveValues( table );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    adapter.preserve( PROP_HEADER_HEIGHT, 
                      new Integer( table.getHeaderHeight() ) );
    adapter.preserve( PROP_HEADER_VISIBLE, 
                      Boolean.valueOf( table.getHeaderVisible() ) );
    adapter.preserve( PROP_LINES_VISIBLE, 
                      Boolean.valueOf( table.getLinesVisible() ) );
    adapter.preserve( PROP_ITEM_HEIGHT, new Integer( table.getItemHeight() ) );
    TableLCAUtil.preserveItemMetrics( table );
    adapter.preserve( PROP_ITEM_COUNT, new Integer( table.getItemCount() ) );
    adapter.preserve( PROP_TOP_INDEX, new Integer( table.getTopIndex() ) );
    adapter.preserve( PROP_SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( table ) ) );
    adapter.preserve( PROP_DEFAULT_COLUMN_WIDTH, 
                      new Integer( getDefaultColumnWidth( table ) ) );
  }

  public void readData( final Widget widget ) {
    Table table = ( Table )widget;
    readSelection( table );
    readTopIndex( table );
    processSetData( table );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    final Table table = ( Table )widget;
    JSWriter writer = JSWriter.getWriterFor( table );
    String style = "";
    if( ( table.getStyle() & SWT.CHECK ) != 0 ) {
      style = "check";
    }
    if( ( table.getStyle() & SWT.MULTI ) != 0 ) {
      style += "|multi";
    }
    Object[] args = new Object[] { WidgetUtil.getId( table ), style };
    writer.newWidget( "org.eclipse.swt.widgets.Table", args );
    ControlLCAUtil.writeStyleFlags( table );
    writer.set( "borderWidth", table.getBorderWidth() );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Table table = ( Table )widget;
    ControlLCAUtil.writeChanges( table );
    writeHeaderHeight( table );
    writeHeaderVisible( table );
    writeItemHeight( table );
    TableLCAUtil.writeItemMetrics( table );
    writeItemCount( table );
    writeTopIndex( table );
    writeLinesVisible( table );
    writeSelectionListener( table );
    writeDefaultColumnWidth( table );
    // Make the JavaScript client area the parent of all children of table 
    String itemJSParent = getItemJSParent( table );
    Control[] children = table.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      WidgetAdapter adapter 
        = ( WidgetAdapter )WidgetUtil.getAdapter( children[ i ] );
      adapter.setJSParent( itemJSParent );
    }
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId ) throws IOException {
  }
  
  public String getTypePoolId( final Widget widget ) {
    return null;
  }

  ////////////////////////////////////////////////////
  // Helping method sto read client-side state changes
  
  private static void readSelection( final Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "selection" );
    if( value != null ) {
      TableItem[] newSelection;
      if( "".equals( value ) ) {
        newSelection = new TableItem[ 0 ];
      } else {
        String[] selectedIds = value.split( "," );
        newSelection = new TableItem[ selectedIds.length ];
        for( int i = 0; i < selectedIds.length; i++ ) {
          newSelection[ i ] = findItem( table, selectedIds[ i ] );
        }
      }
      table.setSelection( newSelection );
    }
  }
  
  private static void readTopIndex( final Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "topIndex" );
    if( value != null ) {
      table.setTopIndex( Integer.parseInt( value ) );
    }
  }
  
  private static void processSetData( final Table table ) {
    boolean setDataEvent 
      = WidgetLCAUtil.wasEventSent( table, JSConst.EVENT_SET_DATA );
    if( setDataEvent ) {
      HttpServletRequest request = ContextProvider.getRequest();
      String value = request.getParameter( JSConst.EVENT_SET_DATA_INDEX );
      String[] indices = value.split( "," );
      Object adapter = table.getAdapter( ITableAdapter.class );
      ITableAdapter tableAdapter = ( ITableAdapter )adapter;
      for( int i = 0; i < indices.length; i++ ) {
        int index = Integer.parseInt( indices[ i ] );
        tableAdapter.checkData( index );
      }
    }
  }

  ///////////////////////////////////////////
  // Helping methods to write JavaScript code
  
  private static void writeHeaderHeight( final Table table ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( table );
    Integer newValue = new Integer( table.getHeaderHeight() );
    writer.set( PROP_HEADER_HEIGHT, "headerHeight", newValue, null );
  }

  private static void writeHeaderVisible( final Table table ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( table );
    Boolean newValue = Boolean.valueOf( table.getHeaderVisible() );
    writer.set( PROP_HEADER_VISIBLE, "headerVisible", newValue, null );
  }

  private static void writeItemHeight( final Table table ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( table );
    Integer newValue = new Integer( table.getItemHeight( ) );
    writer.set( PROP_ITEM_HEIGHT, "itemHeight", newValue, DEFAUT_ITEM_HEIGHT );
  }

  private static void writeItemCount( final Table table ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( table );
    Integer newValue = new Integer( table.getItemCount() );
    writer.set( PROP_ITEM_COUNT, "itemCount", newValue, DEFAULT_ITEM_COUNT );
  }

  private static void writeTopIndex( final Table table ) throws IOException {
    // TODO [rh] investigate if we can optimize item updates by rendering
    //      item.update()-JS-code from the server-side. e.g. compare 
    //      item.preservedIsVisible != item.currentIsVisible
    JSWriter writer = JSWriter.getWriterFor( table );
    Integer newValue = new Integer( table.getTopIndex() );
    writer.set( PROP_TOP_INDEX, "topIndex", newValue, DEFAULT_TOP_INDEX );
  }

  private static void writeLinesVisible( final Table table ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( table );
    Boolean newValue = Boolean.valueOf( table.getLinesVisible() );
    writer.set( PROP_LINES_VISIBLE, "linesVisible", newValue, Boolean.FALSE );
  }

  private static void writeSelectionListener( final Table table ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( table );
    writer.updateListener( SELECTION_LISTENER, 
                           PROP_SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( table ) );
    writer.updateListener( DEFAULT_SELECTION_LISTENER, 
                           PROP_SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( table ) );
    if( ( table.getStyle() & SWT.CHECK ) != 0 ) {
      writer.updateListener( CHECK_SELECTION_LISTENER, 
                             PROP_SELECTION_LISTENERS, 
                             SelectionEvent.hasListener( table ) );
    }
  }

  private static void writeDefaultColumnWidth( final Table table ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( table );
    String prop = PROP_DEFAULT_COLUMN_WIDTH;
    Integer newValue = new Integer( getDefaultColumnWidth( table ) );
    Integer defValue = DEFAULT_DEFAULT_COLUMN_WIDTH;
    writer.set( prop, "defaultColumnWidth", newValue, defValue );
  }

  //////////////////
  // Helping methods 
  
  private static String getItemJSParent( final Table table ) {
    StringBuffer parentId = new StringBuffer();
    parentId.append( WidgetUtil.getId( table ) );
    parentId.append( "_clientArea"  );
    return parentId.toString();
  }

  private static TableItem findItem( final Table table, final String itemId ) {
    TableItem result = null;
    TableItem[] items = table.getItems();
    for( int i = 0; result == null && i < items.length; i++ ) {
      if( WidgetUtil.getId( items[ i ] ).equals( itemId ) ) {
        result = items[ i ];
      }
    }
    return result;
  }

  private static int getDefaultColumnWidth( final Table table ) {
    int result = 0;
    if( table.getColumnCount() == 0 ) {
      Object adapter = table.getAdapter( ITableAdapter.class );
      ITableAdapter tableAdapter = ( ITableAdapter )adapter;
      result = tableAdapter.getDefaultColumnWidth();
    }
    return result;
  }
}