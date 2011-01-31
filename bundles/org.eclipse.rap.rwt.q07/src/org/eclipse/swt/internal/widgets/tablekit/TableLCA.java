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
package org.eclipse.swt.internal.widgets.tablekit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.*;
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
  static final String PROP_ALWAYS_HIDE_SELECTION = "alwaysHideSelection";
  static final String PROP_HAS_H_SCROLL_BAR = "hasHScrollBar";
  static final String PROP_HAS_V_SCROLL_BAR = "hasVScrollBar";
  static final String PROP_LEFT_OFFSET = "leftOffset";
  static final String PROP_SCROLLBARS_SELECTION_LISTENER
    = "scrollBarsSelectionListeners";
  static final String PROP_ENABLE_CELL_TOOLTIP
    = "enableCellToolTip";

  private static final Integer DEFAULT_TOP_INDEX = new Integer( 0 );
  private static final Integer DEFAULT_ITEM_COUNT = new Integer( 0 );
  private static final Integer DEFAUT_ITEM_HEIGHT = new Integer( 0 );
  private static final Integer DEFAULT_COLUMN_WIDTH = new Integer( 0 );
  private static final Integer DEFAULT_LEFT_OFFSET = new Integer( 0 );

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
    adapter.preserve( PROP_ALWAYS_HIDE_SELECTION,
                      alwaysHideSelection( table ) );
    adapter.preserve( PROP_HAS_H_SCROLL_BAR, hasHScrollBar( table ) );
    adapter.preserve( PROP_HAS_V_SCROLL_BAR, hasVScrollBar( table ) );
    adapter.preserve( PROP_LEFT_OFFSET, getLeftOffset( table ) );
    adapter.preserve( PROP_SCROLLBARS_SELECTION_LISTENER,
                      hasScrollBarsSelectionListener( table ) );
    adapter.preserve( PROP_ENABLE_CELL_TOOLTIP, 
                      new Boolean( CellToolTipUtil.isEnabledFor( table ) ) );
  }

  public void readData( final Widget widget ) {
    Table table = ( Table )widget;
    readTopIndex( table ); // topIndex MUST be read *before* processSetData
    readLeftOffset( table );
    readSelection( table );
    readFocusIndex( table ); // must be called *after* readSelection
    readSetData( table );
    readWidgetSelected( table );
    readWidgetDefaultSelected( table );
    readCellToolTipTextRequested( table );
    ControlLCAUtil.processMouseEvents( table );
    ControlLCAUtil.processKeyEvents( table );
    ControlLCAUtil.processMenuDetect( table );
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
    if( ( table.getStyle() & SWT.HIDE_SELECTION ) != 0 ) {
      style += "|hideSelection";
    }
    if( ( table.getStyle() & SWT.NO_SCROLL ) != 0 ) {
      style += "|noScroll";
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
    writeFocusIndex( table );
    writeLinesVisible( table );
    writeSelectionListener( table );
    writeScrollBarsSelectionListener( table );
    writeDefaultColumnWidth( table );
    writeAlwaysHideSelection( table );
    writeScrollBarsVisible( table );
    writeLeftOffset( table );
    writeEnableCellToolTip( table );
    writeCellToolTipText( table );
    WidgetLCAUtil.writeCustomVariant( table );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
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
      table.deselectAll();
      table.select( newSelection );
    }
  }

  private static void readTopIndex( final Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "topIndex" );
    if( value != null ) {
      int topIndex = Integer.parseInt( value );
      int topOffset = topIndex * table.getItemHeight();
      table.setTopIndex( topIndex );
      processScrollBarSelection( table.getVerticalBar(), topOffset );
    }
  }

  private static void readFocusIndex( final Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "focusIndex" );
    if( value != null ) {
      ITableAdapter adapter
        = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
      adapter.setFocusIndex( Integer.parseInt( value ) );
    }
  }

  private static void readLeftOffset( final Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "leftOffset" );
    if( value != null ) {
      int leftOffset = Integer.parseInt( value );
      Object adapter = table.getAdapter( ITableAdapter.class );
      ITableAdapter tableAdapter = ( ITableAdapter )adapter;
      tableAdapter.setLeftOffset( leftOffset );
      processScrollBarSelection( table.getHorizontalBar(), leftOffset );
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
        if( index > -1 && index < table.getItemCount() ) {
          tableAdapter.checkData( index );
        }
      }
    }
  }

  private static void readWidgetSelected( final Table table ) {
    if( WidgetLCAUtil.wasEventSent( table, JSConst.EVENT_WIDGET_SELECTED ) ) {
      // TODO [rh] do something reasonable when index points to unresolved item
      int index = getWidgetSelectedIndex();
      // Bugfix: check if index is valid before firing event to avoid problems
      //         with fast scrolling
      if( index > -1 && index < table.getItemCount() ) {
        TableItem item = table.getItem( index );
        int detail = getWidgetSelectedDetail();
        int id = SelectionEvent.WIDGET_SELECTED;
        Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
        int stateMask
          = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
        SelectionEvent event = new SelectionEvent( table,
                                                   item,
                                                   id,
                                                   bounds,
                                                   stateMask,
                                                   "",
                                                   true,
                                                   detail );
        event.processEvent();
      }
    }
  }

  private static void readWidgetDefaultSelected( final Table table ) {
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
      event.stateMask
        = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
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

  private static void writeFocusIndex( final Table table ) throws IOException {
    if( TableLCAUtil.hasFocusIndexChanged( table ) ) {
      ITableAdapter adapter
        = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
      // TableItemLCA renders focusIndex in case != -1
      if( adapter.getFocusIndex() == -1 ) {
        JSWriter writer = JSWriter.getWriterFor( table );
        writer.set( "focusIndex", new Object[]{ new Integer( -1 ) } );
      }
    }
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

  private static void writeScrollBarsSelectionListener( final Table table )
    throws IOException
  {
    Boolean newValue = hasScrollBarsSelectionListener( table );
    String prop = PROP_SCROLLBARS_SELECTION_LISTENER;
    if( WidgetLCAUtil.hasChanged( table, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( table );
      writer.set( "hasScrollBarsSelectionListener", newValue );
    }
  }

  private static void writeDefaultColumnWidth( final Table table )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( table );
    String prop = PROP_DEFAULT_COLUMN_WIDTH;
    Integer newValue = new Integer( getDefaultColumnWidth( table ) );
    Integer defValue = DEFAULT_COLUMN_WIDTH;
    writer.set( prop, "defaultColumnWidth", newValue, defValue );
  }

  private static void writeAlwaysHideSelection( final Table table ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( table );
    Boolean newValue = alwaysHideSelection( table );
    Boolean defValue = Boolean.FALSE;
    String prop = PROP_ALWAYS_HIDE_SELECTION;
    writer.set( prop, "alwaysHideSelection", newValue, defValue );
  }

  private static void writeScrollBarsVisible( final Table table )
    throws IOException 
  {
    boolean hasHChanged = WidgetLCAUtil.hasChanged( table,
                                                    PROP_HAS_H_SCROLL_BAR,
                                                    hasHScrollBar( table ),
                                                    Boolean.TRUE );
    boolean hasVChanged = WidgetLCAUtil.hasChanged( table,
                                                    PROP_HAS_V_SCROLL_BAR,
                                                    hasVScrollBar( table ),
                                                    Boolean.TRUE );
    if( hasHChanged || hasVChanged ) {
      JSWriter writer = JSWriter.getWriterFor( table );
      Object[] args = new Object[]{
        hasHScrollBar( table ),
        hasVScrollBar( table )
      };
      writer.call( "setScrollBarsVisibile", args );
    }
  }

  private static void writeLeftOffset( final Table table ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( table );
    Integer newValue = getLeftOffset( table );
    writer.set( PROP_LEFT_OFFSET, "leftOffset", newValue, DEFAULT_LEFT_OFFSET );
  }

  ////////////////
  // Cell tooltips

  private static void writeEnableCellToolTip( final Table table )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( table );
    String prop = PROP_ENABLE_CELL_TOOLTIP;
    Boolean newValue = new Boolean( CellToolTipUtil.isEnabledFor( table ) );
    writer.set( prop, "enableCellToolTip", newValue, Boolean.FALSE );
  }

  private static void readCellToolTipTextRequested( final Table table ) {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    adapter.setToolTipText( null );
    String event = JSConst.EVENT_CELL_TOOLTIP_REQUESTED;
    if( WidgetLCAUtil.wasEventSent( table, event ) ) {
      ICellToolTipProvider provider = adapter.getCellToolTipProvider();
      if( provider != null ) {
        HttpServletRequest request = ContextProvider.getRequest();
        String cell = request.getParameter( JSConst.EVENT_CELL_TOOLTIP_DETAILS );
        String[] details = cell.split( "," );
        String itemId = details[ 0 ];
        int columnIndex = Integer.parseInt( details[ 1 ] );
        TableItem item = getItemById( table, itemId );
        // Bug 321119: Sometimes the client can request tooltips for already
        //             disposed cells.
        if(    item != null
            && ( columnIndex == 0 || columnIndex < table.getColumnCount() ) )
        {
          provider.getToolTipText( item, columnIndex );
        }
      }
    }
  }

  private static void writeCellToolTipText( final Table table )
    throws IOException
  {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    String text = adapter.getToolTipText();
    if( text != null ) {
      JSWriter writer = JSWriter.getWriterFor( table );
      text = WidgetLCAUtil.escapeText( text, false );
      text = WidgetLCAUtil.replaceNewLines( text, "<br/>" );
      writer.call( "setCellToolTipText", new String[]{ text } );
    }
  }

  private static TableItem getItemById( final Table table, final String itemId )
  {
    TableItem result = null;
    TableItem[] items = table.getItems();
    for( int i = 0; i < items.length && result == null; i++ ) {
      if( WidgetUtil.getId( items[ i ] ).equals( itemId ) ) {
        result = items[ i ];
      }
    }
    return result;
  }

  //////////////////
  // Helping methods

  private static Boolean hasHScrollBar( final Table table ) {
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    return Boolean.valueOf( tableAdapter.hasHScrollBar() );
  }

  private static Boolean hasVScrollBar( final Table table ) {
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    return Boolean.valueOf( tableAdapter.hasVScrollBar() );
  }

  private static Integer getLeftOffset( final Table table ) {
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    return new Integer( tableAdapter.getLeftOffset() );
  }

  private static Boolean hasScrollBarsSelectionListener( final Table table ) {
    boolean result = false;
    ScrollBar horizontalBar = table.getHorizontalBar();
    if( horizontalBar != null ) {
      result = result || SelectionEvent.hasListener( horizontalBar );
    }
    ScrollBar verticalBar = table.getVerticalBar();
    if( verticalBar != null ) {
      result = result || SelectionEvent.hasListener( verticalBar );
    }
    return Boolean.valueOf( result );
  }

  private static void processScrollBarSelection( final ScrollBar scrollBar,
                                                 final int selection )
  {
    if( scrollBar != null ) {
      scrollBar.setSelection( selection );
      if( SelectionEvent.hasListener( scrollBar ) ) {
        int eventId = SelectionEvent.WIDGET_SELECTED;
        SelectionEvent evt = new SelectionEvent( scrollBar, null, eventId );
        evt.stateMask
          = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
        evt.processEvent();
      }
    }
  }

  static int getDefaultColumnWidth( final Table table ) {
    int result = 0;
    if( table.getColumnCount() == 0 ) {
      Object adapter = table.getAdapter( ITableAdapter.class );
      ITableAdapter tableAdapter = ( ITableAdapter )adapter;
      result = tableAdapter.getDefaultColumnWidth();
    }
    return result;
  }

  static Boolean alwaysHideSelection( final Table table ) {
    Boolean result = Boolean.FALSE;
    Object data = table.getData( Table.ALWAYS_HIDE_SELECTION );
    if( Boolean.TRUE.equals( data ) ) {
      result = Boolean.TRUE;
    }
    return result;
  }
}