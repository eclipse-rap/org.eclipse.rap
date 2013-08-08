/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treecolumnkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getAdapter;

import java.util.Arrays;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.WidgetOperationHandler;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;


public class TreeColumnOperationHandler extends WidgetOperationHandler<TreeColumn> {

  private static final String METHOD_MOVE = "move";
  private static final String METHOD_RESIZE = "resize";
  private static final String PROP_LEFT = "left";
  private static final String PROP_WIDTH = "width";

  public TreeColumnOperationHandler( TreeColumn column ) {
    super( column );
  }

  @Override
  public void handleCall( String method, JsonObject properties ) {
    if( method.equals( METHOD_MOVE ) ) {
      handleCallMove( properties );
    } else if( method.equals( METHOD_RESIZE ) ) {
      handleCallResize( properties );
    }
  }

  /*
   * PROTOCOL CALL move
   *
   * @left (int) the left position of the column
   */
  private void handleCallMove( JsonObject properties ) {
    final TreeColumn column = widget;
    final int newLeft = properties.get( PROP_LEFT ).asInt();
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        moveColumn( column, newLeft );
      }
    } );
  }

  /*
   * PROTOCOL CALL resize
   *
   * @width (int) the width of the column
   */
  private void handleCallResize( JsonObject properties ) {
    final TreeColumn column = widget;
    final int width = properties.get( PROP_WIDTH ).asInt();
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        column.setWidth( width );
      }
    } );
  }

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
    if( isFixed( column ) || isFixed( tree.getColumn( targetColumn ) ) ) {
      targetColumn = tree.indexOf( column );
    }
    columnOrder = arrayInsert( columnOrder, targetColumn, index );
    if( Arrays.equals( columnOrder, tree.getColumnOrder() ) ) {
      // TODO [rh] HACK mark left as changed
      TreeColumn[] columns = tree.getColumns();
      for( int i = 0; i < columns.length; i++ ) {
        getAdapter( columns[ i ] ).preserve( PROP_LEFT, null );
      }
    } else {
      tree.setColumnOrder( columnOrder );
      // [if] HACK mark left as changed - see bug 336340
      getAdapter( column ).preserve( PROP_LEFT, null );
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
        TreeColumn column = columns[ columnOrder [ i ] ];
        int left = getLeft( column );
        int width = column.getWidth();
        if( isFixed( column ) ) {
          left += getLeftOffset( column );
        }
        if( newLeft >= left && newLeft <= left + width ) {
          result = i;
          if( newLeft >= left + width / 2 && result < columns.length && !isFixed( column ) ) {
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

  private static boolean isFixed( TreeColumn column ) {
    return getTreeAdapter( column ).isFixedColumn( column );
  }

  private static int getLeft( TreeColumn column ) {
    return getTreeAdapter( column ).getColumnLeft( column );
  }

  private static ITreeAdapter getTreeAdapter( TreeColumn column ) {
    return column.getParent().getAdapter( ITreeAdapter.class );
  }

  private static int getLeftOffset( TreeColumn column ) {
    ITreeAdapter adapter = column.getParent().getAdapter( ITreeAdapter.class );
    return adapter.getScrollLeft();
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
