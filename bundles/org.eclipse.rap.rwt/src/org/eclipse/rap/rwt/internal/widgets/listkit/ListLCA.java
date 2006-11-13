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

package org.eclipse.rap.rwt.internal.widgets.listkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.List;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.HtmlResponseWriter;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;


public class ListLCA extends AbstractWidgetLCA {
  
  private final static JSListenerInfo JS_LISTENER_INFO
  = new JSListenerInfo( "selectionChanged",
                        "org.eclipse.rap.rwt.TableUtil.selectionChanged",
                        JSListenerType.STATE_AND_ACTION );

public void preserveValues( final Widget widget ) {
  List list = ( List  )widget;
  ControlLCAUtil.preserveValues( list );
  preserveColumnWidths( list );
  preserveSelection( list );
  IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
  adapter.preserve( Props.SELECTION_LISTENERS, 
                    Boolean.valueOf( SelectionEvent.hasListener( list ) ) );

}

public void readData( final Widget widget ) {
}

  public void processAction( final Widget widget ) {
    List list = ( List )widget;
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( list ).equals( id ) ) {
      StringBuffer value = 
        new StringBuffer( WidgetUtil.readPropertyValue( widget,"selection" ) );
      int from = 0;
      int to = 0;
      ArrayList indices = new ArrayList();
      while( value.indexOf( ",", from ) >= 0 ) {
        to = value.indexOf( ",", from );
        indices.add( new Integer( value.substring( from, to ) ) );
        from = to;
        from++;
      }
      int size = indices.size();
      int[] result = new int[ size ];
      for( int i = 0; i < size; i++ ) {
        result[ i ] = ( ( Integer )indices.get( i ) ).intValue();
      }
      list.setSelection( result );
      ControlLCAUtil.processSelection( ( List )widget, null );
    }
  }

public void renderInitialization( final Widget widget ) throws IOException {
  List list = ( List )widget;
  // TODO: [fappel] react on content or column changes of the table
  createTableContent( list );
  createTableColumns( list );
  createTableModel();
  createTable( list );
}

public void renderChanges( final Widget widget ) throws IOException {
  List list = ( List )widget;
  ControlLCAUtil.writeBounds( list );
  ControlLCAUtil.writeToolTip( list );
  // Note: [fappel] order is crucial here, first set the bounds of the control
  //                before manipulating the column width
  writeColWidths( list );
  JSWriter writer = JSWriter.getWriterFor( list );
  writer.updateListener( "selectionModel",
                         JS_LISTENER_INFO, 
                         Props.SELECTION_LISTENERS, 
                         SelectionEvent.hasListener( list ) );
  writeSelection( list );
}

public void renderDispose( final Widget widget ) throws IOException {
}


////////////////////////////////////////////////////////
// helping methods for table creation and initialization

static void createTableContent( final List list ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter writer = stateInfo.getResponseWriter();
    writer.append( "var tableData=[];" );
    String[] items = list.getItems();
    for( int i = 0; i < items.length; i++ ) {
      writer.append( "tableData.push([" );
      writer.append( "\"" );
      writer.append( items[ i ] );
      writer.append( "\"" );
      writer.append( "]);" );
    }
  }

static void createTableColumns( final List list ) {
  IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
  HtmlResponseWriter writer = stateInfo.getResponseWriter();
  writer.append( "var tableColumns=[" );
  writer.append( "\"" );
  writer.append( "" );
  writer.append( "\"" );
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


private static void createTable( final List list ) throws IOException {
  JSWriter writer = JSWriter.getWriterFor( list );
  Object[] args = new Object[] { new JSVar( "tableModel" ) };
  writer.newWidget( "qx.ui.table.Table", args );
  writer.callFieldAssignment( new JSVar( "qx.ui.table.TablePane" ), 
                              "CONTENT_BGCOL_EVEN", 
                              "\"white\"" );
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
  Object[] param = null;
  if( ( list.getStyle() & RWT.MULTI ) != 0 ) {
      param = new Object[]{
        new JSVar( "qx.ui.table.SelectionModel.MULTIPLE_INTERVAL_SELECTION" )
      };
    } else if( ( list.getStyle() & RWT.SINGLE ) != 0 ) {
      param = new Object[]{
        new JSVar( "qx.ui.table.SelectionModel.SINGLE_INTERVAL_SELECTION" )
      };
    } 
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

  private static void preserveColumnWidths( final List list ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( list );
    adapter.preserve( createColWidthParam( 0 ),
                      new Integer( list.getBounds().width ) );
  }

  private static void writeColWidths( final List list ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( list );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( list );
    String key = createColWidthParam( 0 );
    Integer oldWidth = ( Integer )adapter.getPreserved( key );
    int newWidth = list.getBounds().width/* columns[ i ].getWidth() */;
    if(    !adapter.isInitialized() 
        || oldWidth == null 
        || oldWidth.intValue() != newWidth )
    {
      writer.set( "columnWidth", new int[] { 0, newWidth } );
    }
  }


////////////////////////////////////////////////
// helping methods for selection synchronization

private void preserveSelection( final List list ) {
  IWidgetAdapter adapter = WidgetUtil.getAdapter( list );
  adapter.preserve( Props.SELECTION_INDICES, list.getSelectionIndices() );
}


private void writeSelection( final List list ) throws IOException {
  JSWriter writer = JSWriter.getWriterFor( list );
  IWidgetAdapter adapter = WidgetUtil.getAdapter( list );
  int[] oldIndices = ( int[] )adapter.getPreserved( Props.SELECTION_INDICES );
  int[] currentIndices = list.getSelectionIndices();
  if(    !adapter.isInitialized() 
      || !Arrays.equals( currentIndices, oldIndices ) )
  {
    for( int i = 0; i < currentIndices.length; i++ ) {
      Integer index = new Integer( currentIndices[ i ] );
      Object[] args = new Object[] { index, index };
      writer.call( list, "getSelectionModel().addSelectionInterval", args );
      // TODO: [fappel] scroll into view
    }
  }
}
}
