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

package org.eclipse.swt.internal.widgets.tablecolumnkit;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

public final class TableColumnLCA extends AbstractWidgetLCA {

  private static final String SORT_IMAGE_UP 
    = "widget/table/up.png";
  private static final String SORT_IMAGE_DOWN 
    = "widget/table/down.png";
  
  // Property names to preserve values
  private static final String PROP_LEFT = "left";
  private static final String PROP_WIDTH = "width";
  private static final String PROP_Z_INDEX = "zIndex";
  private static final String PROP_FONT = "font";
  private static final String PROP_SORT_IMAGE = "sortImage";
  private static final String PROP_RESIZABLE = "resizable";
  private static final String PROP_MOVEABLE = "moveable";
  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  
  private static final Integer DEFAULT_LEFT = new Integer( 0 );
  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );
  
  private static final JSListenerInfo SELECTION_LISTENER
    = new JSListenerInfo( "click", "this.onClick", JSListenerType.ACTION );
  
  public void preserveValues( final Widget widget ) {
    TableColumn column = ( TableColumn )widget;
    ItemLCAUtil.preserve( column );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    WidgetLCAUtil.preserveToolTipText( column, column.getToolTipText() );
    adapter.preserve( PROP_Z_INDEX, new Integer( getZIndex( column ) ) );
    adapter.preserve( PROP_LEFT, new Integer( getLeft( column ) ) );
    adapter.preserve( PROP_WIDTH, new Integer( column.getWidth() ) );
    adapter.preserve( PROP_FONT, column.getParent().getFont() );
    adapter.preserve( PROP_SORT_IMAGE, getSortImage( column ) );
    adapter.preserve( PROP_RESIZABLE, 
                      Boolean.valueOf( column.getResizable() ) );
    adapter.preserve( PROP_MOVEABLE, 
                      Boolean.valueOf( column.getMoveable() ) );
    adapter.preserve( PROP_ALIGNMENT, new Integer( column.getAlignment() ) );
    adapter.preserve( PROP_SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( column ) ) );
  }
  
  public void readData( final Widget widget ) {
    final TableColumn column = ( TableColumn )widget;
    // Though there is sent an event parameter called 
    // org.eclipse.swt.events.controlResized
    // we will ignore it since setting the new width itself fires the 
    // desired controlResized-event
    String value = WidgetLCAUtil.readPropertyValue( column, "width" );
    if( value != null ) {
      // TODO [rh] HACK: force width to have changed when client-side changes
      //      it. Since this is done while a column resize we must re-layout
      //      all columns including the resized one.
      final int newWidth = Integer.parseInt( value );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          column.setWidth( newWidth );
        }
      } );
    }
    // Though there is an org.eclipse.swt.events.controlMoved event sent,
    // we will ignore it since changing the column order fires the desired
    // controlMoved event
    value = WidgetLCAUtil.readPropertyValue( column, "left" );
    if( value != null ) {
      final int newLeft = Integer.parseInt( value );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          moveColumn( column, newLeft );
        }
      } );
    }
    ControlLCAUtil.processSelection( column, null, false );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    TableColumn column = ( TableColumn )widget;
    JSWriter writer = JSWriter.getWriterFor( column );
    Object[] args = new Object[] { column.getParent() };
    writer.newWidget( "org.eclipse.swt.widgets.TableColumn", args );
    // Keep this order: initialize must be called after setParent  
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TableColumn column = ( TableColumn )widget;
    ItemLCAUtil.writeChanges( column );
    writeLeft( column );
    writeWidth( column );
    writeZIndex( column );
    WidgetLCAUtil.writeFont( column, column.getParent().getFont() );
    WidgetLCAUtil.writeToolTip( column, column.getToolTipText() );
    writeSortImage( column );
    writeResizable( column );
    writeMoveable( column );
    writeAlignment( column );
    writeSelectionListener( column );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    TableColumn column = ( TableColumn )widget;
    JSWriter writer = JSWriter.getWriterFor( column );
    writer.dispose();
  }

  //////////////////////////////////////////
  // Helping method to write JavaScript code
  
  private static void writeLeft( final TableColumn column ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( getLeft( column ) );
    writer.set( PROP_LEFT, "left", newValue, DEFAULT_LEFT );
  }

  private static void writeWidth( final TableColumn column ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( column.getWidth() );
    writer.set( PROP_WIDTH, "width", newValue, null );
  }

  private static void writeZIndex( final TableColumn column ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( getZIndex( column ) );
    writer.set( PROP_Z_INDEX, "zIndex", newValue, null );
  }

  private static void writeSortImage( final TableColumn column ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    writer.set( PROP_SORT_IMAGE, "sortImage", getSortImage( column ), "" );
  }

  private static void writeResizable( final TableColumn column ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Boolean newValue = Boolean.valueOf( column.getResizable() );
    writer.set( PROP_RESIZABLE, "resizable", newValue, Boolean.TRUE );
  }

  private static void writeMoveable( final TableColumn column ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Boolean newValue = Boolean.valueOf( column.getMoveable() );
    writer.set( PROP_MOVEABLE, "moveable", newValue, Boolean.FALSE );
  }
  
  private static void writeAlignment( final TableColumn column ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( column.getAlignment() );
    Integer defValue = DEFAULT_ALIGNMENT;
    if( WidgetLCAUtil.hasChanged( column, PROP_ALIGNMENT, newValue, defValue ) ) 
    {
      JSVar alignment = JSConst.QX_CONST_ALIGN_LEFT;
      if( newValue.intValue() == SWT.CENTER ) {
        alignment = JSConst.QX_CONST_ALIGN_CENTER;
      } else if( newValue.intValue() == SWT.RIGHT ) {
        alignment = JSConst.QX_CONST_ALIGN_RIGHT;
      }
      writer.set( "horizontalChildrenAlign", new Object[] { alignment } );
      
    }
  }

  // TODO [rh] selection event is also fired when resizing columns!
  private static void writeSelectionListener( final TableColumn column )
    throws IOException
  {
    // TODO [rh] dispose of selection listener when widget is disposed of
    JSWriter writer = JSWriter.getWriterFor( column );
    writer.updateListener( SELECTION_LISTENER, 
                           PROP_SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( column ) );
  }

  //////////////////////////////////////////////////
  // Helping methods to obtain calculated properties
  
  static int getLeft( final TableColumn column ) {
    int result = 0;
    Table table = column.getParent();
    TableColumn[] columns = table.getColumns();
    int[] columnOrder = table.getColumnOrder();
    int orderedIndex = columnOrder[ table.indexOf( column ) ]; 
    for( int i = 0; i < orderedIndex; i++ ) {
      result += columns[ columnOrder[ i ] ].getWidth();
    }
    return result;
  }
  
  private static int getZIndex( final TableColumn column ) {
    return ControlLCAUtil.getZIndex( column.getParent() ) + 1;
  }

  private static String getSortImage( final TableColumn column ) {
    String result = "";
    Table table = column.getParent();
    if( table.getSortColumn() == column ) {
      if( table.getSortDirection() == SWT.UP ) {
        result = SORT_IMAGE_UP;
      } else if( table.getSortDirection() == SWT.DOWN ) {
        result = SORT_IMAGE_DOWN;
      }
    }
    return result;
  }
  
  /////////////////////////////////
  // Helping methods to move column
  
  static void moveColumn( final TableColumn column, final int newLeft ) {
    int targetColumn = findMoveTarget( column, newLeft );
    boolean changed = false;
    if( targetColumn != -1 ) {
      changed = changeOrder( column, targetColumn );
    } 
    if( !changed ) {
      // TODO [rh] HACK mark left as changed
      TableColumn[] columns = column.getParent().getColumns();
      for( int i = 0; i < columns.length; i++ ) {
        IWidgetAdapter adapter = WidgetUtil.getAdapter( columns[i] );
        adapter.preserve( PROP_LEFT, null );
        adapter.preserve( PROP_WIDTH, null );
      }
    }
  }

  private static int findMoveTarget( final TableColumn column, 
                                     final int newLeft ) 
  {
    int result = -1;
    Table table = column.getParent();
    TableColumn[] columns = table.getColumns();
    for( int i = 0; result == -1 && i < columns.length; i++ ) {
      int left = getLeft( columns[ i ] );
      int width = columns[ i ].getWidth();
      if( newLeft >= left + width / 2 && newLeft <= left + width ) {
        result = table.getColumnOrder()[ i ];
      }
    }
    return result;
  }

  private static boolean changeOrder( final TableColumn movedColumn, 
                                      final int newOrder ) 
  {
    boolean result;
    Table table = movedColumn.getParent();
    int index = table.indexOf( movedColumn );
    int[] columnOrder = table.getColumnOrder();
    int oldOrder = columnOrder[ index ];
    if( oldOrder != newOrder ) {
      if( newOrder < oldOrder ) {
        for( int i = 0; i < columnOrder.length; i++ ) {
          TableColumn column = table.getColumn( i );
          if(    column != movedColumn 
              && columnOrder[ i ] >= newOrder 
              && columnOrder[ i ] < columnOrder[ index ] ) 
          {
            columnOrder[ i ] += 1;
          }
        }
      } else {
        for( int i = 0; i < columnOrder.length; i++ ) {
          TableColumn column = table.getColumn( i );
          if(     column != movedColumn 
              && columnOrder[ i ] <= newOrder 
              && columnOrder[ i ] > columnOrder[ index ] ) 
          {
            columnOrder[ i ] -= 1;
          }
        }
      }
      result = columnOrder[ index ] != newOrder;
      if( result ) {
        columnOrder[ index ] = newOrder;
        table.setColumnOrder( columnOrder );
      }
    } else {
      result = false;
    }
    return result;
  }
}
