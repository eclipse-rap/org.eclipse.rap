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

package org.eclipse.swt.internal.widgets.treecolumnkit;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.widgets.*;

public final class TreeColumnLCA extends AbstractWidgetLCA {

  // Property names to preserve values
  static final String PROP_LEFT = "left";
  static final String PROP_WIDTH = "width";
  static final String PROP_Z_INDEX = "zIndex";
  static final String PROP_SORT_DIRECTION = "sortDirection";
  static final String PROP_RESIZABLE = "resizable";
  static final String PROP_MOVEABLE = "moveable";
  static final String PROP_ALIGNMENT = "alignment";
  static final String PROP_SELECTION_LISTENERS = "selectionListeners";

  private static final Integer DEFAULT_LEFT = new Integer( 0 );
  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );

  public void preserveValues( Widget widget ) {
    TreeColumn column = ( TreeColumn )widget;
    ItemLCAUtil.preserve( column );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    WidgetLCAUtil.preserveToolTipText( column, column.getToolTipText() );
    adapter.preserve( PROP_Z_INDEX, new Integer( getZIndex( column ) ) );
    adapter.preserve( PROP_LEFT, new Integer( getLeft( column ) ) );
    adapter.preserve( PROP_WIDTH, new Integer( column.getWidth() ) );
    adapter.preserve( PROP_SORT_DIRECTION, getSortDirection( column ) );
    adapter.preserve( PROP_RESIZABLE, Boolean.valueOf( column.getResizable() ) );
    adapter.preserve( PROP_MOVEABLE, Boolean.valueOf( column.getMoveable() ) );
    adapter.preserve( PROP_ALIGNMENT, new Integer( column.getAlignment() ) );
    adapter.preserve( PROP_SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( column ) ) );
    WidgetLCAUtil.preserveCustomVariant( column );
  }

  public void readData( Widget widget ) {
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
      final int newWidth = NumberFormatUtil.parseInt( value );
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
      final int newLeft = NumberFormatUtil.parseInt( value );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          moveColumn( column, newLeft );
        }
      } );
    }
    ControlLCAUtil.processSelection( column, null, false );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    TreeColumn column = ( TreeColumn )widget;
    JSWriter writer = JSWriter.getWriterFor( column );
    Object[] args = new Object[] { column.getParent() };
    writer.newWidget( "org.eclipse.swt.widgets.TableColumn", args );
  }

  public void renderChanges( Widget widget ) throws IOException {
    TreeColumn column = ( TreeColumn )widget;
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

  public void renderDispose( Widget widget ) throws IOException {
    TreeColumn column = ( TreeColumn )widget;
    JSWriter writer = JSWriter.getWriterFor( column );
    writer.dispose();
  }

  //////////////////////////////////////////
  // Helping method to write JavaScript code

  private static void writeLeft( TreeColumn column ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( getLeft( column ) );
    writer.set( PROP_LEFT, "left", newValue, DEFAULT_LEFT );
  }

  private static void writeWidth( TreeColumn column ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( column.getWidth() );
    writer.set( PROP_WIDTH, "width", newValue, null );
  }

  private static void writeZIndex( TreeColumn column ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( getZIndex( column ) );
    writer.set( PROP_Z_INDEX, "zIndex", newValue, null );
  }

  private static void writeSortDirection( TreeColumn column ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( column );
    String newValue = getSortDirection( column );
    writer.set( PROP_SORT_DIRECTION, "sortDirection", newValue, null );
  }

  private static void writeResizable( TreeColumn column ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( column );
    Boolean newValue = Boolean.valueOf( column.getResizable() );
    writer.set( PROP_RESIZABLE, "resizable", newValue, Boolean.TRUE );
  }

  private static void writeMoveable( TreeColumn column ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( column );
    Boolean newValue = Boolean.valueOf( column.getMoveable() );
    writer.set( PROP_MOVEABLE, "moveable", newValue, Boolean.FALSE );
  }

  private static void writeAlignment( TreeColumn column ) throws IOException {
    Integer newValue = new Integer( column.getAlignment() );
    Integer defValue = DEFAULT_ALIGNMENT;
    if( WidgetLCAUtil.hasChanged( column, PROP_ALIGNMENT, newValue, defValue ) ) {
      JSVar alignment = JSConst.QX_CONST_ALIGN_LEFT;
      if( newValue.intValue() == SWT.CENTER ) {
        alignment = JSConst.QX_CONST_ALIGN_CENTER;
      } else if( newValue.intValue() == SWT.RIGHT ) {
        alignment = JSConst.QX_CONST_ALIGN_RIGHT;
      }
      Integer index = new Integer( column.getParent().indexOf( column ) );
      JSWriter writer = JSWriter.getWriterFor( column );
      writer.set( "alignment", new Object[] { index, alignment } );
    }
  }

  // TODO [rh] selection event is also fired when resizing columns!
  private static void writeSelectionListener( TreeColumn column ) throws IOException {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( column ) );
    String prop = PROP_SELECTION_LISTENERS;
    JSWriter writer = JSWriter.getWriterFor( column );
    writer.set( prop, "hasSelectionListener", newValue, Boolean.FALSE );
  }

  //////////////////////////////////////////////////
  // Helping methods to obtain calculated properties

  static int getLeft( TreeColumn column ) {
    Object adapter = column.getParent().getAdapter( ITreeAdapter.class );
    ITreeAdapter treeAdapter = ( ITreeAdapter )adapter;
    return treeAdapter.getColumnLeft( column );
  }

  static int getZIndex( TreeColumn column ) {
    return ControlLCAUtil.getZIndex( column.getParent() ) + 1;
  }

  static String getSortDirection( TreeColumn column ) {
    String result = null;
    Tree tree = column.getParent();
    if( tree.getSortColumn() == column ) {
      if( tree.getSortDirection() == SWT.UP ) {
        result = "up";
      } else if( tree.getSortDirection() == SWT.DOWN ) {
        result = "down";
      }
    }
    return result;
  }

  /////////////////////////////////
  // Helping methods to move column

  static void moveColumn( TreeColumn column, int newLeft ) {
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
  private static int findMoveTarget( Tree tree, int newLeft ) {
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

  private static int arrayIndexOf( int[] array, int value ) {
    int result = -1;
    for( int i = 0; result == -1 && i < array.length; i++ ) {
      if( array[ i ] == value ) {
        result = i;
      }
    }
    return result;
  }

  private static int[] arrayRemove( int[] array, int index ) {
    int length = array.length;
    int[] result = new int[ length - 1 ];
    System.arraycopy( array, 0, result, 0, index );
    if( index < length - 1 ) {
      System.arraycopy( array, index + 1, result, index, length - index - 1 );
    }
    return result;
  }

  private static int[] arrayInsert( int[] array, int index, int value ) {
    int length = array.length;
    int[] result = new int[ length + 1 ];
    System.arraycopy( array, 0, result, 0, length );
    System.arraycopy( result, index, result, index + 1, length - index );
    result[ index ] = value;
    return result;
  }
}
