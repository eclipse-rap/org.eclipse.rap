/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.widgets.*;


public final class TableLCA extends AbstractWidgetLCA {

  // Property names to preserve values
  static final String PROP_HEADER_HEIGHT = "headerHeight";
  static final String PROP_HEADER_VISIBLE = "headerVisible";
  static final String PROP_LINES_VISIBLE = "linesVisible";
  static final String PROP_ITEM_HEIGHT = "itemHeight";
  static final String PROP_TOP_INDEX = "topIndex";
  static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  static final String PROP_DEFAULT_COLUMN_WIDTH = "defaultColumnWidth";
  static final String PROP_ITEM_COUNT = "itemCount";
  static final String PROP_HIDE_SELECTION = "hideSelection";

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
    TableLCAUtil.preserveFocusIndex( table );
    WidgetLCAUtil.preserveCustomVariant( table );
    adapter.preserve( PROP_HIDE_SELECTION, hideSelection( table ) );
  }

  public void readData( final Widget widget ) {
    Table table = ( Table )widget;
    readTopIndex( table ); // topIndex MUST be read *before* processSetData
    readLeftOffset( table );
    readSelection( table );
    readSetData( table );
    readWidgetSelected( table );
    readWidgetDefaultSelected( table );
    ControlLCAUtil.processMouseEvents( table );
    ControlLCAUtil.processKeyEvents( table );
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
    writeHideSelection( table );
    WidgetLCAUtil.writeCustomVariant( table );
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

  public void doRedrawFake( final Control control ) {
    Table table = ( Table )control;
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    tableAdapter.checkData();
  }

  ////////////////////////////////////////////////////
  // Helping methods to read client-side state changes

  private static void readSelection( final Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "selection" );
    if( value != null ) {
      int[] newSelection;
      if( "".equals( value ) ) {
        newSelection = new int[ 0 ];
      } else {
        String[] selectedIndices = value.split( "," );
        newSelection = new int[ selectedIndices.length ];
        for( int i = 0; i < selectedIndices.length; i++ ) {
          newSelection[ i ] = Integer.parseInt( selectedIndices[ i ] );
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

  private void readLeftOffset( final Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "leftOffset" );
    if( value != null ) {
      Object adapter = table.getAdapter( ITableAdapter.class );
      ITableAdapter tableAdapter = ( ITableAdapter )adapter;
      tableAdapter.setLeftOffset( Integer.parseInt( value ) );
    }
  }

  private static void readSetData( final Table table ) {
    if( WidgetLCAUtil.wasEventSent( table, JSConst.EVENT_SET_DATA ) ) {
      HttpServletRequest request = ContextProvider.getRequest();
      String value = request.getParameter( JSConst.EVENT_SET_DATA_INDEX );
      String[] indices = value.split( "," );
      Object adapter = table.getAdapter( ITableAdapter.class );
      ITableAdapter tableAdapter = ( ITableAdapter )adapter;
      for( int i = 0; i < indices.length; i++ ) {
        int index = Integer.parseInt( indices[ i ] );
        if (index >-1 && index < table.getItemCount()) {
            tableAdapter.checkData( index );
        }
      }
    }
  }

  private void readWidgetSelected( final Table table ) {
    if( WidgetLCAUtil.wasEventSent( table, JSConst.EVENT_WIDGET_SELECTED ) ) {
      // TODO [rh] do something about when index points to unresolved item!
      final int widgetSelectedIndex = getWidgetSelectedIndex();
      // Bugfix: check if index is valid before firing event to avoid problems with fast scrolling
      if (widgetSelectedIndex > -1 && widgetSelectedIndex < table.getItemCount()) {
          TableItem item = table.getItem( widgetSelectedIndex );
          int detail = getWidgetSelectedDetail();
          int id = SelectionEvent.WIDGET_SELECTED;
          SelectionEvent event = new SelectionEvent( table,
                  item,
                  id,
                  new Rectangle( 0, 0, 0, 0 ),
                  "",
                  true,
                  detail );
          event.processEvent();
      }
    }
  }

  private void readWidgetDefaultSelected( final Table table ) {
    String defaultSelectedParam = JSConst.EVENT_WIDGET_DEFAULT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( table, defaultSelectedParam ) ) {
      // A default-selected event can occur without a selection being present.
      // In this case the event.item field points to the focused item
      TableItem item = getFocusedItem( table );
      int selectedIndex = getWidgetSelectedIndex();
      if( selectedIndex != -1 ) {
        // TODO [rh] do something about when index points to unresolved item!
        item = table.getItem( selectedIndex );
      }
      int id = SelectionEvent.WIDGET_DEFAULT_SELECTED;
      SelectionEvent event = new SelectionEvent( table, item, id );
      event.processEvent();
    }
  }

  private static int getWidgetSelectedDetail() {
    HttpServletRequest request = ContextProvider.getRequest();
    String value = request.getParameter( JSConst.EVENT_WIDGET_SELECTED_DETAIL );
    return "check".equals( value ) ? SWT.CHECK : SWT.NONE;
  }

  private static int getWidgetSelectedIndex() {
    HttpServletRequest request = ContextProvider.getRequest();
    String value = request.getParameter( JSConst.EVENT_WIDGET_SELECTED_INDEX );
    return Integer.parseInt( value );
  }

  private static TableItem getFocusedItem( final Table table ) {
    TableItem result = null;
    ITableAdapter tableAdapter
      = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    int focusIndex = tableAdapter.getFocusIndex();
    if( focusIndex != -1 ) {
      // TODO [rh] do something about when index points to unresolved item!
      result = table.getItem( focusIndex );
    }
    return result;
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

  private void writeHideSelection( final Table table ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( table );
    Boolean newValue = hideSelection( table );
    Boolean defValue = Boolean.FALSE;
    writer.set( PROP_HIDE_SELECTION, "hideSelection", newValue, defValue );
  }

  //////////////////
  // Helping methods

  static int getDefaultColumnWidth( final Table table ) {
    int result = 0;
    if( table.getColumnCount() == 0 ) {
      Object adapter = table.getAdapter( ITableAdapter.class );
      ITableAdapter tableAdapter = ( ITableAdapter )adapter;
      result = tableAdapter.getDefaultColumnWidth();
    }
    return result;
  }

  static Boolean hideSelection( final Table table ) {
    Boolean result = Boolean.FALSE;
    Object data = table.getData( Table.HIDE_SELECTION );
    if( Boolean.TRUE.equals( data ) ) {
      result = Boolean.TRUE;
    }
    return result;
  }
}