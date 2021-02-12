/*******************************************************************************
 * Copyright (c) 2013, 2021 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal.gridkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_COLLAPSE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_EXPAND;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_INDEX;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_ITEM;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SET_DATA;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.internal.IGridAdapter;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ScrollBar;


@SuppressWarnings( "restriction" )
public class GridOperationHandler extends ControlOperationHandler<Grid> {

  private static final String PROP_SELECTION = "selection";
  private static final String PROP_CELL_SELECTION = "cellSelection";
  private static final String PROP_SCROLL_LEFT = "scrollLeft";
  private static final String PROP_TOP_ITEM_INDEX = "topItemIndex";
  private static final String PROP_FOCUS_ITEM = "focusItem";
  private static final String PROP_FOCUS_CELL = "focusCell";
  private static final String METHOD_RENDER_TOOLTIP_TEXT = "renderToolTipText";

  public GridOperationHandler( Grid grid ) {
    super( grid );
  }

  @Override
  public void handleSet( Grid grid, JsonObject properties ) {
    super.handleSet( grid, properties );
    handleSetSelection( grid, properties );
    handleSetCellSelection( grid, properties );
    handleSetScrollLeft( grid, properties );
    handleSetTopItemIndex( grid, properties );
    handleSetFocusItem( grid, properties );
    handleSetFocusCell( grid, properties );
  }

  @Override
  public void handleCall( Grid grid, String method, JsonObject properties ) {
    if( METHOD_RENDER_TOOLTIP_TEXT.equals( method ) ) {
      handleCallRenderToolTipText( grid, properties );
    }
  }

  @Override
  public void handleNotify( Grid grid, String eventName, JsonObject properties ) {
    if( EVENT_SELECTION.equals( eventName ) ) {
      handleNotifySelection( grid, properties );
    } else if( EVENT_DEFAULT_SELECTION.equals( eventName ) ) {
      handleNotifyDefaultSelection( grid, properties );
    } else if( EVENT_EXPAND.equals( eventName ) ) {
      handleNotifyExpand( grid, properties );
    } else if( EVENT_COLLAPSE.equals( eventName ) ) {
      handleNotifyCollapse( grid, properties );
    } else if( EVENT_SET_DATA.equals( eventName ) ) {
      handleNotifySetData( grid, properties );
    } else {
      super.handleNotify( grid, eventName, properties );
    }
  }

  /*
   * PROTOCOL SET selection
   *
   * @param selection ([string]) array with ids of selected items
   */
  public void handleSetSelection( Grid grid, JsonObject properties ) {
    JsonValue values = properties.get( PROP_SELECTION );
    if( values != null ) {
      JsonArray itemIds = values.asArray();
      GridItem[] selectedItems = new GridItem[ itemIds.size() ];
      boolean validItemFound = false;
      for( int i = 0; i < itemIds.size(); i++ ) {
        selectedItems[ i ] = getItem( grid, itemIds.get( i ).asString() );
        if( selectedItems[ i ] != null ) {
          validItemFound = true;
        }
      }
      if( !validItemFound ) {
        selectedItems = new GridItem[ 0 ];
      }
      grid.setSelection( selectedItems );
    }
  }

  /*
   * PROTOCOL SET cellSelection
   *
   * @param cellSelection ([[string, int]]) array with item/cell ids of selected cells
   */
  public void handleSetCellSelection( Grid grid, JsonObject properties ) {
    JsonValue values = properties.get( PROP_CELL_SELECTION );
    if( values != null ) {
      JsonArray cells = values.asArray();
      List<Point> selectedCells = new ArrayList<>();
      for( int i = 0; i < cells.size(); i++ ) {
        JsonArray currentCell = cells.get( i ).asArray();
        GridItem item = getItem( grid, currentCell.get( 0 ).asString() );
        if( item != null ) {
          int x = currentCell.get( 1 ).asInt() - getColumnOffset( grid );
          selectedCells.add( new Point( x, grid.indexOf( item ) ) );
        }
      }
      grid.setCellSelection( selectedCells.toArray( new Point[ 0 ] ) );
    }
  }

  /*
   * PROTOCOL SET scrollLeft
   *
   * @param scrollLeft (int) left scroll offset in pixels
   */
  public void handleSetScrollLeft( Grid grid, JsonObject properties ) {
    JsonValue value = properties.get( PROP_SCROLL_LEFT );
    if( value != null ) {
      setScrollBarSelection( grid.getHorizontalBar(), value.asInt() );
    }
  }

  /*
   * PROTOCOL SET topItemIndex
   *
   * @param topItemIndex (int) visual index of the item, which is on the top of the grid
   */
  public void handleSetTopItemIndex( Grid grid, JsonObject properties ) {
    JsonValue value = properties.get( PROP_TOP_ITEM_INDEX );
    if( value != null ) {
      getGridAdapter( grid ).invalidateTopIndex();
      setScrollBarSelection( grid.getVerticalBar(), value.asInt() );
    }
  }

  /*
   * PROTOCOL SET focusItem
   *
   * @param focusItem (string) id of focus item
   */
  public void handleSetFocusItem( Grid grid, JsonObject properties ) {
    JsonValue value = properties.get( PROP_FOCUS_ITEM );
    if( value != null ) {
      GridItem item = getItem( grid, value.asString() );
      if( item != null ) {
        grid.setFocusItem( item );
      }
    }
  }

  /*
   * PROTOCOL SET focusCell
   *
   * @param focusCell (int) index of focus column
   */
  public void handleSetFocusCell( Grid grid, JsonObject properties ) {
    JsonValue value = properties.get( PROP_FOCUS_CELL );
    if( value != null ) {
      GridColumn column = grid.getColumn( value.asInt() - getColumnOffset( grid ) );
      if( column != null ) {
        grid.setFocusColumn( column );
      }
    }
  }

  /*
   * PROTOCOL CALL renderToolTipText
   *
   * @param item (string) id of the hovered item
   * @param column (int) column index of the hovered cell
   */
  public void handleCallRenderToolTipText( Grid grid, JsonObject properties ) {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( grid );
    ICellToolTipProvider provider = adapter.getCellToolTipProvider();
    if( provider != null ) {
      GridItem item = getItem( grid, properties.get( "item" ).asString() );
      int columnIndex = properties.get( "column" ).asInt() - getColumnOffset( grid );
      if( item != null && ( columnIndex >= 0 && columnIndex < grid.getColumnCount() ) ) {
        provider.getToolTipText( item, columnIndex );
      }
    }
  }

  /*
   * PROTOCOL NOTIFY Selection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param detail (string) "check" if checkbox is selected, "hyperlink" if RWT hyperlink is
   *        selected
   * @param item (string) id of selected item
   * @param text (string) the value of href attribute or content of the selected RWT hyperlink
   * @param index (int) index of the selected column
   */
  public void handleNotifySelection( Grid grid, JsonObject properties ) {
    GridItem item = getItem( grid, properties.get( EVENT_PARAM_ITEM ).asString() );
    if( item != null ) {
      Event event = createSelectionEvent( SWT.Selection, properties );
      event.item = item;
      event.index = readIndex( properties );
      grid.notifyListeners( SWT.Selection, event );
    }
  }

  /*
   * PROTOCOL NOTIFY DefaultSelection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param detail (string) "check" is checkbox is selected
   * @param item (string) id of selected item
   */
  public void handleNotifyDefaultSelection( Grid grid, JsonObject properties ) {
    GridItem item = getItem( grid, properties.get( EVENT_PARAM_ITEM ).asString() );
    if( item != null ) {
      Event event = createSelectionEvent( SWT.DefaultSelection, properties );
      event.item = item;
      grid.notifyListeners( SWT.DefaultSelection, event );
    }
  }

  /*
   * PROTOCOL NOTIFY Expand
   *
   * @param item (string) id of expanded item
   */
  public void handleNotifyExpand( Grid grid, JsonObject properties ) {
    GridItem item = getItem( grid, properties.get( EVENT_PARAM_ITEM ).asString() );
    if( item != null ) {
      Event event = new Event();
      event.item = item;
      grid.notifyListeners( SWT.Expand, event );
    }
  }

  /*
   * PROTOCOL NOTIFY Collapse
   *
   * @param item (string) id of collapsed item
   */
  public void handleNotifyCollapse( Grid grid, JsonObject properties ) {
    GridItem item = getItem( grid, properties.get( EVENT_PARAM_ITEM ).asString() );
    if( item != null ) {
      Event event = new Event();
      event.item = item;
      grid.notifyListeners( SWT.Collapse, event );
    }
  }

  /*
   * PROTOCOL NOTIFY SetData
   * ignored, SetData event is fired when set topItemIndex
   */
  @SuppressWarnings( "unused" )
  public void handleNotifySetData( Grid grid, JsonObject properties ) {
  }

  private static GridItem getItem( Grid grid, String itemId ) {
    return ( GridItem )WidgetUtil.find( grid, itemId );
  }

  private static void setScrollBarSelection( ScrollBar scrollBar, int selection ) {
    if( scrollBar != null ) {
      scrollBar.setSelection( selection );
    }
  }

  private static int readIndex( JsonObject properties ) {
    JsonValue value = properties.get( EVENT_PARAM_INDEX );
    return value == null ? 0 : value.asInt();
  }

  private static int getColumnOffset( Grid grid ) {
    return getGridAdapter( grid ).getRowHeadersColumn() != null ? 1 : 0;
  }

  private static IGridAdapter getGridAdapter( Grid grid ) {
    return grid.getAdapter( IGridAdapter.class );
  }

}
