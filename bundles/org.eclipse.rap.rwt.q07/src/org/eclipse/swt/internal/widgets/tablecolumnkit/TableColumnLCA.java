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

package org.eclipse.swt.internal.widgets.tablecolumnkit;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.tablekit.TableLCAUtil;
import org.eclipse.swt.widgets.*;


public final class TableColumnLCA extends AbstractWidgetLCA {

// Property names to preserve values
  static final String PROP_LEFT = "left";
  static final String PROP_WIDTH = "width";
  static final String PROP_Z_INDEX = "zIndex";
  static final String PROP_SORT_DIRECTION = "sortDirection";
  static final String PROP_RESIZABLE = "resizable";
  static final String PROP_MOVEABLE = "moveable";
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";

  private static final Integer DEFAULT_LEFT = new Integer( 0 );

  private static final JSListenerInfo SELECTION_LISTENER
    = new JSListenerInfo( "click", "this.onClick", JSListenerType.ACTION );

  public void preserveValues( final Widget widget ) {
    TableColumn column = ( TableColumn )widget;
    ItemLCAUtil.preserve( column );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    WidgetLCAUtil.preserveToolTipText( column, column.getToolTipText() );
    TableLCAUtil.preserveAlignment( column );
    adapter.preserve( PROP_Z_INDEX, new Integer( getZIndex( column ) ) );
    adapter.preserve( PROP_LEFT, new Integer( getLeft( column ) ) );
    adapter.preserve( PROP_WIDTH, new Integer( column.getWidth() ) );
    adapter.preserve( PROP_SORT_DIRECTION, getSortDirection( column ) );
    adapter.preserve( PROP_RESIZABLE,
                      Boolean.valueOf( column.getResizable() ) );
    adapter.preserve( PROP_MOVEABLE,
                      Boolean.valueOf( column.getMoveable() ) );
    adapter.preserve( PROP_SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( column ) ) );
    WidgetLCAUtil.preserveCustomVariant( column );
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
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TableColumn column = ( TableColumn )widget;
    ItemLCAUtil.writeChanges( column );
    writeLeft( column );
    writeWidth( column );
    writeZIndex( column );
    WidgetLCAUtil.writeToolTip( column, column.getToolTipText() );
    writeSortDirection( column );
    writeResizable( column );
    writeMoveable( column );
    writeAlignment( column );
    writeSelectionListener( column );
    WidgetLCAUtil.writeCustomVariant( column );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    TableColumn column = ( TableColumn )widget;
    JSWriter writer = JSWriter.getWriterFor( column );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
  }

  public String getTypePoolId( final Widget widget ) {
    return null;
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

  // TODO [rh] writing Z-Index seems unnecessary since it is relative to the
  //      parent and thus could be hard-coded client-side
  private static void writeZIndex( final TableColumn column ) throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( getZIndex( column ) );
    writer.set( PROP_Z_INDEX, "zIndex", newValue, null );
  }

  private static void writeSortDirection( final TableColumn column )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    String newValue = getSortDirection( column );
    writer.set( PROP_SORT_DIRECTION, "sortDirection", newValue, null );
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
    if( TableLCAUtil.hasAlignmentChanged( column ) ) {
      Integer newValue = new Integer( column.getAlignment() );
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
    Object adapter = column.getParent().getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    return tableAdapter.getColumnLeft( column );
  }

  static int getZIndex( final TableColumn column ) {
    return ControlLCAUtil.getZIndex( column.getParent() ) + 1;
  }

  static String getSortDirection( final TableColumn column ) {
    String result = null;
    Table table = column.getParent();
    if( table.getSortColumn() == column ) {
      if( table.getSortDirection() == SWT.UP ) {
        result = "up";
      } else if( table.getSortDirection() == SWT.DOWN ) {
        result = "down";
      }
    }
    return result;
  }

  /////////////////////////////////
  // Helping methods to move column

  static void moveColumn( final TableColumn column, final int newLeft ) {
    Table table = column.getParent();
    int targetColumn = findMoveTarget( table, newLeft );
    int[] columnOrder = table.getColumnOrder();
    int index = table.indexOf( column );
    int orderIndex = arrayIndexOf( columnOrder, index );
    columnOrder = arrayRemove( columnOrder, orderIndex );
    if( orderIndex < targetColumn ) {
      targetColumn--;
    }
    columnOrder = arrayInsert( columnOrder, targetColumn, index );
    if( Arrays.equals( columnOrder, table.getColumnOrder() ) ) {
      // TODO [rh] HACK mark left as changed
      TableColumn[] columns = table.getColumns();
      for( int i = 0; i < columns.length; i++ ) {
        IWidgetAdapter adapter = WidgetUtil.getAdapter( columns[ i ] );
        adapter.preserve( PROP_LEFT, null );
      }
    } else {
      table.setColumnOrder( columnOrder );
    }
  }

  /* (intentionally non-JavaDoc'ed)
   * Returns the index in the columnOrder array at which the moved column
   * should be inserted (moving remaining columns to the right). A return
   * value of columnCount indicates that the moved column should be inserted
   * after the right-most column.
   */
  private static int findMoveTarget( final Table table, final int newLeft ) {
    int result = -1;
    TableColumn[] columns = table.getColumns();
    int[] columnOrder = table.getColumnOrder();
    if( newLeft < 0 ) {
      result = 0;
    } else {
      for( int i = 0; result == -1 && i < columns.length; i++ ) {
        int left = getLeft( columns[ columnOrder [ i ] ] );
        int width = columns[ columnOrder [ i ] ].getWidth();
        if( newLeft >= left && newLeft <= left + width ) {
          result = i;
          if( newLeft >= left + width / 2 && result < columns.length ) {
            result++;
          }
        }
      }      
    }
    // Column was moved right of the right-most column
    if( result == -1 ) {
      result = columns.length;
    }
    return result;
  }

  private static int arrayIndexOf( final int[] array, final int value ) {
    int result = -1;
    for( int i = 0; result == -1 && i < array.length; i++ ) {
      if( array[ i ] == value ) {
        result = i;
      }
    }
    return result;
  }

  private static int[] arrayRemove( final int[] array, final int index ) {
    int length = array.length;
    int[] result = new int[ length - 1 ];
    System.arraycopy( array, 0, result, 0, index );
    if( index < length - 1 ) {
      System.arraycopy( array, index + 1, result, index, length - index - 1 );
    }
    return result;
  }

  private static int[] arrayInsert( final int[] array,
                                    final int index,
                                    final int value )
  {

    int length = array.length;
    int[] result = new int[ length + 1 ];
    System.arraycopy( array, 0, result, 0, length );
    System.arraycopy( result, index, result, index + 1, length - index );
    result[ index ] = value;
    return result;
  }
}
