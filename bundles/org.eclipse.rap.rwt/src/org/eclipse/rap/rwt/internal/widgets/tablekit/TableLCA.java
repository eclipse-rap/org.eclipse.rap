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

package org.eclipse.rap.rwt.internal.widgets.tablekit;

import java.io.IOException;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;

public final class TableLCA extends AbstractWidgetLCA {

  private static final String PROP_HEADER_HEIGHT = "headerHeight";
  private static final String PROP_HEADER_VISIBLE = "headerVisible";
  private static final String PROP_LINES_VISIBLE = "linesVisible";
  private static final String PROP_ITEM_HEIGHT = "itemHeight";
  private static final String PROP_TOP_INDEX = "topIndex";
  private static final String PROP_SELECTION = "selection";
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";

  private static final Integer DEFAULT_TOP_INDEX = new Integer( 0 );
  private static final Object DEFAULT_SELECTION = new int[ 0 ];
  private static final Integer DEFAUT_ITEM_HEIGHT = new Integer( 0 );

  private static final JSListenerInfo SELECTION_LISTENER
    = new JSListenerInfo( "itemselected", 
                          "this.onItemSelected", 
                          JSListenerType.ACTION );
  
  public void preserveValues( final Widget widget ) {
    Table table = ( Table )widget;
    ControlLCAUtil.preserveValues( table );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_HEADER_HEIGHT, 
                      new Integer( table.getHeaderHeight() ) );
    adapter.preserve( PROP_HEADER_VISIBLE, 
                      Boolean.valueOf( table.getHeaderVisible() ) );
    adapter.preserve( PROP_LINES_VISIBLE, 
                      Boolean.valueOf( table.getLinesVisible() ) );
    adapter.preserve( PROP_ITEM_HEIGHT, new Integer( table.getItemHeight() ) );
    adapter.preserve( PROP_TOP_INDEX, new Integer( table.getTopIndex() ) );
    adapter.preserve( PROP_SELECTION, table.getSelection() );
    adapter.preserve( PROP_SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( table ) ) );
    TableLCAUtil.preserveColumnCount( table );
  }

  public void readData( final Widget widget ) {
    Table table = ( Table )widget;
    readSelection( table );
    readTopIndex( table );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    final Table table = ( Table )widget;
    JSWriter writer = JSWriter.getWriterFor( table );
    Object[] args = new Object[] { WidgetUtil.getId( table ) };
    writer.newWidget( "org.eclipse.rap.rwt.widgets.Table", args );
    ControlLCAUtil.writeStyleFlags( table );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Table table = ( Table )widget;
    ControlLCAUtil.writeChanges( table );
    writeHeaderHeight( table );
    writerHeaderVisible( table );
    writeItemHeight( table );
    writeTopIndex( table );
    writeLinesVisible( table );
    writeSelection( table );
    writeSelectionListener( table );
    // Make the JavaScript client area the parent of all children of table 
    Control[] children = table.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      IWidgetAdapter adapter = WidgetUtil.getAdapter( children[ i ] );
      adapter.setJSParent( TableLCAUtil.getItemJSParent( table ) );
    }
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  ////////////////////////////////////////////////////
  // Helping method sto read client-side state changes
  
  private static void readSelection( final Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "selection" );
    if( value != null ) {
      String[] selectedIds = value.split( "," );
      TableItem[] newSelection = new TableItem[ selectedIds.length ];
      for( int i = 0; i < selectedIds.length; i++ ) {
        newSelection[ i ] = findItem( table, selectedIds[ i ] );
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

  ///////////////////////////////////////////
  // Helping methods to write JavaScript code
  
  private static void writeHeaderHeight( final Table table ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( table );
    Integer newValue = new Integer( table.getHeaderHeight() );
    writer.set( PROP_HEADER_HEIGHT, "headerHeight", newValue, null );
  }

  private static void writerHeaderVisible( final Table table ) 
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

  private static void writeTopIndex( final Table table ) throws IOException {
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

  private static void writeSelection( final Table table ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( table );
    int[] newValue = table.getSelectionIndices();
    Object defValue = DEFAULT_SELECTION;
    if( WidgetLCAUtil.hasChanged( table, PROP_SELECTION, newValue, defValue ) ) 
    {
      Integer[] indices = new Integer[ newValue.length ];
      for( int i = 0; i < indices.length; i++ ) {
        indices[ i ] = new Integer( newValue[ i ] );
      }
      writer.set( "selection", new Object[] { indices } );
    }
  }

  private static void writeSelectionListener( final Table table ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( table );
    writer.updateListener( SELECTION_LISTENER, 
                           PROP_SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( table ) );
  }
}