/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal.gridcolumnkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;

import java.util.Arrays;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.internal.IGridAdapter;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.WidgetOperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;


@SuppressWarnings( "restriction" )
public class GridColumnOperationHandler extends WidgetOperationHandler<GridColumn> {

  private static final String METHOD_MOVE = "move";
  private static final String METHOD_RESIZE = "resize";
  private static final String PROP_LEFT = "left";
  private static final String PROP_WIDTH = "width";

  public GridColumnOperationHandler( GridColumn column ) {
    super( column );
  }

  @Override
  public void handleCall( GridColumn column, String method, JsonObject properties ) {
    if( METHOD_MOVE.equals( method ) ) {
      handleCallMove( column, properties );
    } else if( METHOD_RESIZE.equals( method ) ) {
      handleCallResize( column, properties );
    }
  }

  @Override
  public void handleNotify( GridColumn column, String eventName, JsonObject properties ) {
    if( EVENT_SELECTION.equals( eventName ) ) {
      handleNotifySelection( column, properties );
    } else if( EVENT_DEFAULT_SELECTION.equals( eventName ) ) {
      handleNotifyDefaultSelection( column, properties );
    } else {
      super.handleNotify( column, eventName, properties );
    }
  }

  /*
   * PROTOCOL CALL move
   *
   * @param left (int) the left position of the column
   */
  public void handleCallMove( final GridColumn column, JsonObject properties ) {
    final int newLeft = properties.get( PROP_LEFT ).asInt();
    ProcessActionRunner.add( new Runnable() {
      @Override
      public void run() {
        moveColumn( column, newLeft );
      }
    } );
  }

  /*
   * PROTOCOL CALL resize
   *
   * @param width (int) the width of the column
   */
  public void handleCallResize( final GridColumn column, JsonObject properties ) {
    final int width = properties.get( PROP_WIDTH ).asInt();
    ProcessActionRunner.add( new Runnable() {
      @Override
      public void run() {
        column.setWidth( width );
      }
    } );
  }

  /*
   * PROTOCOL NOTIFY Selection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   */
  public void handleNotifySelection( GridColumn column, JsonObject properties ) {
    Event event = createSelectionEvent( SWT.Selection, properties );
    column.notifyListeners( SWT.Selection, event );
  }

  /*
   * PROTOCOL NOTIFY DefaultSelection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   */
  public void handleNotifyDefaultSelection( GridColumn column, JsonObject properties ) {
    Event event = createSelectionEvent( SWT.DefaultSelection, properties );
    column.notifyListeners( SWT.DefaultSelection, event );
  }

  static void moveColumn( GridColumn column, int newLeft ) {
    Grid grid = column.getParent();
    int index = grid.indexOf( column );
    int targetColumn = findMoveTarget( grid, newLeft );
    int[] columnOrder = grid.getColumnOrder();
    int orderIndex = arrayIndexOf( columnOrder, index );
    columnOrder = arrayRemove( columnOrder, orderIndex );
    if( orderIndex < targetColumn ) {
      targetColumn--;
    }
    columnOrder = arrayInsert( columnOrder, targetColumn, index );
    if( Arrays.equals( columnOrder, grid.getColumnOrder() ) ) {
      GridColumn[] columns = grid.getColumns();
      for( int i = 0; i < columns.length; i++ ) {
        RemoteAdapter adapter = WidgetUtil.getAdapter( columns[ i ] );
        adapter.preserve( PROP_LEFT, null );
      }
    } else {
      try {
        grid.setColumnOrder( columnOrder );
      } catch( IllegalArgumentException exception ) {
        // move the column in/out of a group is invalid
      } finally {
        RemoteAdapter adapter = WidgetUtil.getAdapter( column );
        adapter.preserve( PROP_LEFT, null );
      }
    }
  }

  private static int findMoveTarget( Grid grid, int newLeft ) {
    int result = -1;
    GridColumn[] columns = grid.getColumns();
    int[] columnOrder = grid.getColumnOrder();
    if( newLeft < 0 ) {
      result = 0;
    } else {
      for( int i = 0; result == -1 && i < columns.length; i++ ) {
        GridColumn column = columns[ columnOrder[ i ] ];
        int left = getLeft( column );
        int width = getWidth( column );
        if( newLeft >= left && newLeft <= left + width ) {
          result = i;
          if( newLeft >= left + width / 2 && result < columns.length ) {
            result++;
          }
        }
      }
    }
    if( result == -1 ) {
      result = columns.length;
    }
    return result;
  }

  private static int getLeft( GridColumn column ) {
    return getGridAdapter( column ).getCellLeft( getIndex( column ) );
  }

  private static int getWidth( GridColumn column ) {
    return getGridAdapter( column ).getCellWidth( getIndex( column ) );
  }

  private static int getIndex( GridColumn column ) {
    return column.getParent().indexOf( column );
  }

  private static IGridAdapter getGridAdapter( GridColumn column ) {
    return column.getParent().getAdapter( IGridAdapter.class );
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
