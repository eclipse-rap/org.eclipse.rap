/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.treecolumnkit;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

public final class TreeColumnLCA extends AbstractWidgetLCA {

  // TODO
//  private static final String SORT_IMAGE_UP 
//    = "widget/table/up.png";
//  private static final String SORT_IMAGE_DOWN 
//    = "widget/table/down.png";
  
  // Property names to preserve values
  private static final String PROP_LEFT = "left";
  private static final String PROP_WIDTH = "width";
  private static final String PROP_Z_INDEX = "zIndex";
//  private static final String PROP_SORT_IMAGE = "sortImage";
  private static final String PROP_RESIZABLE = "resizable";
  private static final String PROP_MOVEABLE = "moveable";
  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  
  private static final Integer DEFAULT_LEFT = new Integer( 0 );
  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );
  
  private static final JSListenerInfo SELECTION_LISTENER
    = new JSListenerInfo( "click", "this.onClick", JSListenerType.ACTION );
  
  public void preserveValues( final Widget widget ) {
    TreeColumn column = ( TreeColumn )widget;
    ItemLCAUtil.preserve( column );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    WidgetLCAUtil.preserveToolTipText( column, column.getToolTipText() );
    adapter.preserve( PROP_Z_INDEX, new Integer( getZIndex( column ) ) );
    adapter.preserve( PROP_LEFT, new Integer( getLeft( column ) ) );
    adapter.preserve( PROP_WIDTH, new Integer( column.getWidth() ) );
//    adapter.preserve( PROP_SORT_IMAGE, getSortImage( column ) );
    adapter.preserve( PROP_RESIZABLE, 
                      Boolean.valueOf( column.getResizable() ) );
    adapter.preserve( PROP_MOVEABLE, 
                      Boolean.valueOf( column.getMoveable() ) );
    adapter.preserve( PROP_ALIGNMENT, new Integer( column.getAlignment() ) );
    adapter.preserve( PROP_SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( column ) ) );
  }
  
  public void readData( final Widget widget ) {
    final TreeColumn column = ( TreeColumn )widget;
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
    TreeColumn column = ( TreeColumn )widget;
    JSWriter writer = JSWriter.getWriterFor( column );
    Object[] args = new Object[] { column.getParent() };
    writer.newWidget( "org.eclipse.swt.widgets.TreeColumn", args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TreeColumn column = ( TreeColumn )widget;
    ItemLCAUtil.writeChanges( column );
    writeLeft( column );
    writeWidth( column );
    writeZIndex( column );
    WidgetLCAUtil.writeToolTip( column, column.getToolTipText() );
//    writeSortImage( column );
    writeResizable( column );
    writeMoveable( column );
    writeAlignment( column );
    writeSelectionListener( column );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    TreeColumn column = ( TreeColumn )widget;
    JSWriter writer = JSWriter.getWriterFor( column );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId ) throws IOException {
  }
  
  public String getTypePoolId( final Widget widget ) throws IOException {
    return null;
  }
  
  
  //////////////////////////////////////////
  // Helping method to write JavaScript code
  
  private static void writeLeft( final TreeColumn column ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( getLeft( column ) );
    writer.set( PROP_LEFT, "left", newValue, DEFAULT_LEFT );
  }

  private static void writeWidth( final TreeColumn column ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( column.getWidth() );
    writer.set( PROP_WIDTH, "width", newValue, null );
  }

  private static void writeZIndex( final TreeColumn column ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( getZIndex( column ) );
    writer.set( PROP_Z_INDEX, "zIndex", newValue, null );
  }
//
//  private static void writeSortImage( final TableColumn column ) 
//    throws IOException 
//  {
//    JSWriter writer = JSWriter.getWriterFor( column );
//    writer.set( PROP_SORT_IMAGE, "sortImage", getSortImage( column ), "" );
//  }

  private static void writeResizable( final TreeColumn column ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Boolean newValue = Boolean.valueOf( column.getResizable() );
    writer.set( PROP_RESIZABLE, "resizable", newValue, Boolean.TRUE );
  }

  private static void writeMoveable( final TreeColumn column ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Boolean newValue = Boolean.valueOf( column.getMoveable() );
    writer.set( PROP_MOVEABLE, "moveable", newValue, Boolean.FALSE );
  }
  
  private static void writeAlignment( final TreeColumn column ) 
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
  private static void writeSelectionListener( final TreeColumn column )
    throws IOException
  {
    // TODO [rh] dispose of selection listener when widget is disposed of
    JSWriter writer = JSWriter.getWriterFor( column );
    writer.updateListener( SELECTION_LISTENER, 
                           PROP_SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( column ) );
  }

//  //////////////////////////////////////////////////
//  // Helping methods to obtain calculated properties
  
  static int getLeft( final TreeColumn column ) {
////    Object adapter = column.getParent().getAdapter( ITableAdapter.class );
////    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
////    return tableAdapter.getColumnLeft( column );
//    TreeColumn[] cols = column.getParent().getColumns();
// // int index = getOrderIndex ();
// // int result = -parent.horizontalOffset;
//     int newValue = 0;
// // for (int i = 0; i < index; i++) {
// // result += orderedColumns [i].width;
// // }
//     TreeColumn current = cols[0];
//     int i = 1;
//     while( current != column ) {
//       newValue += current.getWidth();
//       i++;
//       current = cols[i-1];
//     }
//     return newValue;
      Tree parent = column.getParent();
    int result = 0;
    TreeColumn[] columns = parent.getColumns();
    int[] columnOrder = parent.getColumnOrder();
    int orderedIndex = -1;
    for( int i = 0; orderedIndex == -1 && i < columnOrder.length; i++ ) {
      if( columnOrder[ i ] == parent.indexOf( column ) ) {
        orderedIndex = i;
      }
    }
    for( int i = 0; i < orderedIndex; i++ ) {
      result += columns[ columnOrder[ i ] ].getWidth();
    }
    return result;
  }
  
  private static int getZIndex( final TreeColumn column ) {
    return ControlLCAUtil.getZIndex( column.getParent() ) + 1;
  }
//
//  private static String getSortImage( final TableColumn column ) {
//    String result = "";
//    Table table = column.getParent();
//    if( table.getSortColumn() == column ) {
//      if( table.getSortDirection() == SWT.UP ) {
//        result = SORT_IMAGE_UP;
//      } else if( table.getSortDirection() == SWT.DOWN ) {
//        result = SORT_IMAGE_DOWN;
//      }
//    }
//    return result;
//  }
  
  /////////////////////////////////
  // Helping methods to move column
  
  static void moveColumn( final TreeColumn column, final int newLeft ) {
    Tree tree = column.getParent();
    int targetColumn = findMoveTarget( tree, newLeft );
    int[] columnOrder = tree.getColumnOrder();
    int index = tree.indexOf( column );
    int orderIndex = arrayIndexOf( columnOrder, index );
    columnOrder = arrayRemove( columnOrder, orderIndex );
    if( orderIndex < targetColumn ) {
      targetColumn--;
    }
    columnOrder = arrayInsert( columnOrder, targetColumn, index );
    if( Arrays.equals( columnOrder, tree.getColumnOrder() ) ) {
      // TODO [rh] HACK mark left as changed
      TreeColumn[] columns = tree.getColumns();
      for( int i = 0; i < columns.length; i++ ) {
        IWidgetAdapter adapter = WidgetUtil.getAdapter( columns[ i ] );
        adapter.preserve( PROP_LEFT, null );
      }
    } else {
      tree.setColumnOrder( columnOrder );
    }
  }
  
  /* (intentionally non-JavaDoc'ed)
   * Returns the index in the columnOrder array at which the moved column
   * should be inserted (moving remaining columns to the right). A return
   * value of columnCount indicates that the moved column should be inserted 
   * after the right-most column. 
   */
  private static int findMoveTarget( final Tree tree, final int newLeft ) {
    int result = -1;
    TreeColumn[] columns = tree.getColumns();
    int[] columnOrder = tree.getColumnOrder();
    if( newLeft < 0 ) {
      result = 0;
    } else {
      for( int i = 0; result == -1 && i < columns.length; i++ ) {
        int left = getLeft( columns[ i ] );
        int width = columns[ i ].getWidth();
        if( newLeft >= left && newLeft <= left + width ) {
          result = columnOrder[ i ];
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
