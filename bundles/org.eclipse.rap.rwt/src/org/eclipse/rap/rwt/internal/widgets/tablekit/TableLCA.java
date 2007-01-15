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
import java.util.Arrays;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.HtmlResponseWriter;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;

// Note: [fappel] due to the complex structure of Table, TableItem and 
//                TableColumn all the work is done by this LCA
public class TableLCA extends AbstractWidgetLCA {

  private final static JSListenerInfo JS_LISTENER_INFO
    = new JSListenerInfo( "selectionChanged",
                          "org.eclipse.rap.rwt.TableUtil.selectionChanged",
                          JSListenerType.STATE_AND_ACTION );

  public void preserveValues( final Widget widget ) {
    Table table = ( Table  )widget;
    ControlLCAUtil.preserveValues( table );
    preserveColumnWidths( table );
    preserveSelection( table );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( table ) ) );

  }

  public void readData( final Widget widget ) {
    Table table = ( Table )widget;
    readColumnWidths( table );
    readSelection( table );
    preserveSelection( table );
    // TODO: [fappel] retrieve selected item...
    // TODO [rh] clarify whether bounds should be sent (last parameter)
    ControlLCAUtil.processSelection( widget, null, true );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    Table table = ( Table )widget;
    // TODO: [fappel] react on content or column changes of the table
    createTableContent( table );
    createTableColumns( table );
    createTableModel();
    createTable( table );
    ControlLCAUtil.writeStyleFlags( table );
    JSWriter writer = JSWriter.getWriterFor( table );
    writer.set( "appearance", "table" );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    Table table = ( Table )widget;
    ControlLCAUtil.writeChanges( table );
    // Note: [fappel] order is crucial here, first set the bounds of the control
    //                before manipulating the column width
    writeColWidths( table );
    JSWriter writer = JSWriter.getWriterFor( table );
    writer.updateListener( "selectionModel",
                           JS_LISTENER_INFO, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( table ) );
    writeSelection( table );
  }

  public void renderDispose( final Widget widget ) throws IOException {
  }

  
  ////////////////////////////////////////////////////////
  // helping methods for table creation and initialization

  static void createTableContent( final Table table ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter writer = stateInfo.getResponseWriter();
    writer.append( "var tableData=[];" );
    TableColumn[] columns = table.getColumns();
    TableItem[] items = table.getItems();
    for( int i = 0; i < items.length; i++ ) {
      writer.append( "tableData.push([" );
      for( int j = 0; j < columns.length; j++ ) {
        writer.append( "\"" );
        writer.append( items[ i ].getText( j ) );
        writer.append( "\"" );
        if( j + 1 < columns.length ) {
          writer.append( "," );
        }
      }
      writer.append( "]);" );
    }
  }

  static void createTableColumns( final Table table ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter writer = stateInfo.getResponseWriter();
    writer.append( "var tableColumns=[" );
    TableColumn[] columns = table.getColumns();
    for( int i = 0; i < columns.length; i++ ) {
      writer.append( "\"" );
      writer.append( columns[ i ].getText() );
      writer.append( "\"" );
      if( i + 1 < columns.length ) {
        writer.append( "," );
      }
    }
    writer.append( "];" );
  }
  
  static void createTableModel() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter writer = stateInfo.getResponseWriter();
    writer.append( "var tableModel = " );
    writer.append( "new org.eclipse.rap.rwt.UnsortableTableModel();" );
    writer.append( "tableModel.setColumns( tableColumns );" );
    writer.append( "tableModel.setData( tableData );" );
  }
  

  private static void createTable( final Table table ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( table );
    Object[] args = new Object[] { new JSVar( "tableModel" ) };
    writer.newWidget( "qx.ui.table.Table", args );
    writer.callFieldAssignment( new JSVar( "qx.ui.table.TablePane" ), 
                                "CONTENT_BGCOL_EVEN", 
                                "\"white\"" ); // TODO [rh] use Color
    writer.set( "statusBarVisible", false );
    writer.set( "columnVisibilityButtonVisible", false );
    writer.addListener( "tableColumnModel",
                        "widthChanged", 
                        "org.eclipse.rap.rwt.TableUtil.columnWidthChanged" );
    // this is needed to be able to get the table to which the 
    // column width changes belong to on client side
    writer.callFieldAssignment( new JSVar( "w.getTableColumnModel()" ), 
                                "table", 
                                "w" );
    String[] propertyChain = new String[] { "selectionModel", "selectionMode" };
    Object[] param = new Object[] { 
      new JSVar( "qx.ui.table.SelectionModel.MULTIPLE_INTERVAL_SELECTION" )
    };
    writer.set( propertyChain, param );
    // this is needed to be able to get the table to which the 
    // selection changes belong to
    writer.callFieldAssignment( new JSVar( "w.getSelectionModel()" ), 
                                "table", 
                                "w" );
  }

  
  ///////////////////////////////////////////////////
  // helping methods for column width synchronization
  
  private static String createColWidthParam( final int colIndex ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "columnWidth_" );
    buffer.append( colIndex );
    return buffer.toString();
  }
  
  private static void preserveColumnWidths( final Table table ) {
    TableColumn[] columns = table.getColumns();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    for( int i = 0; i < columns.length; i++ ) {
      adapter.preserve( createColWidthParam( i ), 
                        new Integer( columns[ i ].getWidth() ) );
    }
  }
  
  private static void readColumnWidths( final Table table ) {
    TableColumn[] columns = table.getColumns();
    for( int i = 0; i < columns.length; i++ ) {
      String parmColWidth = createColWidthParam( i );
      String newColWidth  = WidgetUtil.readPropertyValue( table, parmColWidth );
      if( newColWidth != null ) {
        columns[ i ].setWidth( Integer.parseInt( newColWidth ) );
      }
    }
  }

  private static void writeColWidths( final Table table ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( table );
    TableColumn[] columns = table.getColumns();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    for( int i = 0; i < columns.length; i++ ) {
      String key = createColWidthParam( i );
      Integer oldWidth = ( Integer )adapter.getPreserved( key );
      int newWidth = columns[ i ].getWidth();
      if( !adapter.isInitialized() || oldWidth.intValue() != newWidth ) {
        writer.set( "columnWidth", new int[] { i, newWidth });
      }
    }
  }

  
  ////////////////////////////////////////////////
  // helping methods for selection synchronization
  
  private void preserveSelection( final Table table ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    adapter.preserve( Props.SELECTION_INDICES, table.getSelectionIndices() );
  }

  private static void readSelection( final Table table ) {
    String selection = WidgetUtil.readPropertyValue( table, "selection" );
    if( selection != null ) {
      String[] split = selection.split( "," );
      int[] indices = new int[ split.length ];
      for( int i = 0; i < indices.length; i++ ) {
        indices[ i ] = Integer.parseInt( split[ i ] );
      }
      table.setSelection( indices );
    }
  }

  private void writeSelection( final Table table ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( table );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    int[] oldIndices = ( int[] )adapter.getPreserved( Props.SELECTION_INDICES );
    int[] currentIndices = table.getSelectionIndices();
    if(    !adapter.isInitialized() 
        || !Arrays.equals( currentIndices, oldIndices ) )
    {
      for( int i = 0; i < currentIndices.length; i++ ) {
        Integer index = new Integer( currentIndices[ i ] );
        Object[] args = new Object[] { index, index };
        writer.call( table, "getSelectionModel().addSelectionInterval", args );
        // TODO: [fappel] scroll into view
      }
    }
  }
}
