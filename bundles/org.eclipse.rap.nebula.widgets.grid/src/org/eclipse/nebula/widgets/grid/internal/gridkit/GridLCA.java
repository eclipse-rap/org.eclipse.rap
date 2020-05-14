/******************************************************************************
 * Copyright (c) 2012, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal.gridkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderListenDefaultSelection;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderListenSelection;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.createRemoteObject;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.swt.internal.widgets.MarkupUtil.isMarkupEnabledFor;

import java.io.IOException;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.internal.IGridAdapter;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.internal.template.TemplateLCAUtil;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;


@SuppressWarnings("restriction")
public class GridLCA extends WidgetLCA<Grid> {

  public static final GridLCA INSTANCE = new GridLCA();

  private static final String TYPE = "rwt.widgets.Grid";
  private static final String[] ALLOWED_STYLES = new String[] {
    "SINGLE",
    "MULTI",
    "FULL_SELECTION",
    "VIRTUAL",
    "NO_FOCUS",
    "BORDER"
  };

  private static final String PROP_ITEM_COUNT = "itemCount";
  private static final String PROP_ITEM_HEIGHT = "itemHeight";
  private static final String PROP_ITEM_METRICS = "itemMetrics";
  private static final String PROP_COLUMN_COUNT = "columnCount";
  private static final String PROP_COLUMN_ORDER = "columnOrder";
  private static final String PROP_FIXED_COLUMNS = "fixedColumns";
  private static final String PROP_TREE_COLUMN = "treeColumn";
  private static final String PROP_HEADER_HEIGHT = "headerHeight";
  private static final String PROP_HEADER_VISIBLE = "headerVisible";
  private static final String PROP_FOOTER_HEIGHT = "footerHeight";
  private static final String PROP_FOOTER_VISIBLE = "footerVisible";
  private static final String PROP_LINES_VISIBLE = "linesVisible";
  private static final String PROP_TOP_ITEM_INDEX = "topItemIndex";
  private static final String PROP_FOCUS_ITEM = "focusItem";
  private static final String PROP_FOCUS_CELL = "focusCell";
  private static final String PROP_SCROLL_LEFT = "scrollLeft";
  private static final String PROP_SELECTION = "selection";
  private static final String PROP_CELL_SELECTION = "cellSelection";
  private static final String PROP_CELL_SELECTION_ENABLED = "cellSelectionEnabled";
  private static final String PROP_SELECTION_TYPE = "selectionType";
  private static final String PROP_AUTO_HEIGHT = "autoHeight";
  private static final String PROP_INDENTION_WIDTH = "indentionWidth";
  // TODO: [if] Sync sortDirection and sortColumn in GridColumnLCA when multiple sort columns are
  // possible on the client
  private static final String PROP_SORT_DIRECTION = "sortDirection";
  private static final String PROP_SORT_COLUMN = "sortColumn";
  private static final String PROP_SETDATA_LISTENER = "SetData";
  private static final String PROP_EXPAND_LISTENER = "Expand";
  private static final String PROP_COLLAPSE_LISTENER = "Collapse";
  // TODO: [if] Sync toolTipText in GridItemLCA when it's possible on the client
  private static final String PROP_ENABLE_CELL_TOOLTIP = "enableCellToolTip";
  private static final String PROP_CELL_TOOLTIP_TEXT = "cellToolTipText";
  private static final String PROP_MARKUP_ENABLED = "markupEnabled";

  private static final int ZERO = 0 ;
  private static final String[] DEFAULT_SELECTION = new String[ 0 ];
  private static final String[] DEFAULT_COLUMN_ORDER = new String[ 0 ];
  private static final String DEFAULT_SORT_DIRECTION = "none";

  @Override
  public void renderInitialization( Grid grid ) throws IOException {
    RemoteObject remoteObject = createRemoteObject( grid, TYPE );
    remoteObject.setHandler( new GridOperationHandler( grid ) );
    remoteObject.set( "parent", getId( grid.getParent() ) );
    remoteObject.set( "style", createJsonArray( getStyles( grid, ALLOWED_STYLES ) ) );
    remoteObject.set( "appearance", "grid" );
    if( getFixedColumns( grid ) >= 0 ) {
      remoteObject.set( "splitContainer", true );
    }
    remoteObject.set( PROP_MARKUP_ENABLED, isMarkupEnabledFor( grid ) );
    TemplateLCAUtil.renderRowTemplate( grid );
    remoteObject.listen( PROP_SETDATA_LISTENER, isVirtual( grid ) );
    // Always render listen for Expand and Collapse, currently required for scrollbar
    // visibility update and setData events.
    remoteObject.listen( PROP_EXPAND_LISTENER, true );
    remoteObject.listen( PROP_COLLAPSE_LISTENER, true );
  }

  @Override
  public void preserveValues( Grid grid ) {
    preserveProperty( grid, PROP_ITEM_COUNT, grid.getRootItemCount() );
    preserveProperty( grid, PROP_ITEM_HEIGHT, grid.getItemHeight() );
    preserveProperty( grid, PROP_ITEM_METRICS, getItemMetrics( grid ) );
    preserveProperty( grid, PROP_COLUMN_COUNT, getColumnCount( grid ) );
    preserveProperty( grid, PROP_COLUMN_ORDER, getColumnOrder( grid ) );
    preserveProperty( grid, PROP_FIXED_COLUMNS, getFixedColumns( grid ) );
    preserveProperty( grid, PROP_TREE_COLUMN, getTreeColumn( grid ) );
    preserveProperty( grid, PROP_HEADER_HEIGHT, grid.getHeaderHeight() );
    preserveProperty( grid, PROP_HEADER_VISIBLE, grid.getHeaderVisible() );
    preserveProperty( grid, PROP_FOOTER_HEIGHT, grid.getFooterHeight() );
    preserveProperty( grid, PROP_FOOTER_VISIBLE, grid.getFooterVisible() );
    preserveProperty( grid, PROP_LINES_VISIBLE, grid.getLinesVisible() );
    preserveProperty( grid, PROP_TOP_ITEM_INDEX, getTopItemIndex( grid ) );
    preserveProperty( grid, PROP_FOCUS_ITEM, grid.getFocusItem() );
    preserveProperty( grid, PROP_FOCUS_CELL, getFocusCell( grid ) );
    preserveProperty( grid, PROP_SCROLL_LEFT, getScrollLeft( grid ) );
    preserveProperty( grid, PROP_SELECTION_TYPE, getSelectionType( grid ) );
    preserveProperty( grid, PROP_SELECTION, getSelection( grid ) );
    preserveProperty( grid, PROP_AUTO_HEIGHT, grid.isAutoHeight() );
    preserveProperty( grid, PROP_INDENTION_WIDTH, getIndentationWidth( grid ) );
    preserveProperty( grid, PROP_SORT_DIRECTION, getSortDirection( grid ) );
    preserveProperty( grid, PROP_SORT_COLUMN, getSortColumn( grid ) );
    preserveProperty( grid, PROP_ENABLE_CELL_TOOLTIP, CellToolTipUtil.isEnabledFor( grid ) );
    preserveProperty( grid, PROP_CELL_TOOLTIP_TEXT, null );
    preserveProperty( grid, PROP_CELL_SELECTION_ENABLED, grid.isCellSelectionEnabled() );
    preserveProperty( grid, PROP_CELL_SELECTION, getCellSelection( grid ) );
  }

  @Override
  public void renderChanges( Grid grid ) throws IOException {
    ControlLCAUtil.renderChanges( grid );
    WidgetLCAUtil.renderCustomVariant( grid );
    renderProperty( grid, PROP_ITEM_COUNT, grid.getRootItemCount(), ZERO );
    renderProperty( grid, PROP_ITEM_HEIGHT, grid.getItemHeight(), ZERO );
    renderItemMetrics( grid );
    renderProperty( grid, PROP_COLUMN_COUNT, getColumnCount( grid ), ZERO );
    renderProperty( grid, PROP_COLUMN_ORDER, getColumnOrder( grid ), DEFAULT_COLUMN_ORDER );
    renderProperty( grid, PROP_FIXED_COLUMNS, getFixedColumns( grid ), -1 );
    renderProperty( grid, PROP_TREE_COLUMN, getTreeColumn( grid ), ZERO );
    renderProperty( grid, PROP_HEADER_HEIGHT, grid.getHeaderHeight(), ZERO );
    renderProperty( grid, PROP_HEADER_VISIBLE, grid.getHeaderVisible(), false );
    renderProperty( grid, PROP_FOOTER_HEIGHT, grid.getFooterHeight(), ZERO );
    renderProperty( grid, PROP_FOOTER_VISIBLE, grid.getFooterVisible(), false );
    renderProperty( grid, PROP_LINES_VISIBLE, grid.getLinesVisible(), false );
    renderProperty( grid, PROP_TOP_ITEM_INDEX, getTopItemIndex( grid ), ZERO );
    renderProperty( grid, PROP_FOCUS_ITEM, grid.getFocusItem(), null );
    renderProperty( grid, PROP_FOCUS_CELL, getFocusCell( grid ), -1 );
    renderProperty( grid, PROP_SCROLL_LEFT, getScrollLeft( grid ), ZERO );
    renderProperty( grid, PROP_SELECTION_TYPE, getSelectionType( grid ), "SINGLE" );
    renderProperty( grid, PROP_SELECTION, getSelection( grid ), DEFAULT_SELECTION );
    renderProperty( grid, PROP_AUTO_HEIGHT, grid.isAutoHeight(), false );
    renderProperty( grid, PROP_INDENTION_WIDTH, getIndentationWidth( grid ), ZERO );
    renderProperty( grid, PROP_SORT_DIRECTION, getSortDirection( grid ), DEFAULT_SORT_DIRECTION );
    renderProperty( grid, PROP_SORT_COLUMN, getSortColumn( grid ), null );
    renderListenSelection( grid );
    renderListenDefaultSelection( grid );
    renderProperty( grid, PROP_ENABLE_CELL_TOOLTIP, CellToolTipUtil.isEnabledFor( grid ), false );
    renderProperty( grid, PROP_CELL_TOOLTIP_TEXT, getAndResetCellToolTipText( grid ), null );
    renderProperty( grid, PROP_CELL_SELECTION_ENABLED, grid.isCellSelectionEnabled(), false );
    renderProperty( grid, PROP_CELL_SELECTION, getCellSelection( grid ), DEFAULT_SELECTION );
  }

  @Override
  public void doRedrawFake( Control control ) {
    getGridAdapter( ( Grid )control ).doRedraw();
  }

  //////////////////
  // Helping methods

  private static String getAndResetCellToolTipText( Grid grid ) {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( grid );
    String toolTipText = adapter.getCellToolTipText();
    adapter.setCellToolTipText( null );
    return toolTipText;
  }

  private static boolean isVirtual( Grid grid ) {
    return ( grid.getStyle() & SWT.VIRTUAL ) != 0;
  }

  private static int getTreeColumn( Grid grid ) {
    return getGridAdapter( grid ).getTreeColumn();
  }

  private static int getTopItemIndex( Grid grid ) {
    int result = 0;
    ScrollBar verticalBar = grid.getVerticalBar();
    if( verticalBar != null ) {
      result = verticalBar.getSelection();
    }
    return result;
  }

  private static int getScrollLeft( Grid grid ) {
    int result = 0;
    ScrollBar horizontalBar = grid.getHorizontalBar();
    if( horizontalBar != null ) {
      result = horizontalBar.getSelection();
    }
    return result;
  }

  private static String getSelectionType( Grid grid ) {
    return getGridAdapter( grid ).getSelectionType() == SWT.MULTI ? "MULTI" : "SINGLE";
  }

  private static String[] getSelection( Grid grid ) {
    GridItem[] selection = grid.getSelection();
    String[] result = new String[ selection.length ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = getId( selection[ i ] );
    }
    return result;
  }

  private static String[] getCellSelection( Grid grid ) {
    Point[] selection = grid.getCellSelection();
    int offset = getColumnOffset( grid );
    String[] result = new String[ selection.length ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = getId( grid.getItem( selection[ i ].y ) ) + "#" + ( selection[ i ].x + offset );
    }
    return result;
  }

  private static int getIndentationWidth( Grid grid ) {
    return getGridAdapter( grid ).getIndentationWidth();
  }

  private static String getSortDirection( Grid grid ) {
    String result = "none";
    for( int i = 0; i < grid.getColumnCount() && result.equals( "none" ); i++ ) {
      int sort = grid.getColumn( i ).getSort();
      if( sort == SWT.UP ) {
        result = "up";
      } else if( sort == SWT.DOWN ) {
        result = "down";
      }
    }
    return result;
  }

  private static GridColumn getSortColumn( Grid grid ) {
    GridColumn result = null;
    for( int i = 0; i < grid.getColumnCount() && result == null; i++ ) {
      GridColumn column = grid.getColumn( i );
      if( column.getSort() != SWT.NONE ) {
        result = column;
      }
    }
    return result;
  }

  private static int getColumnCount( Grid grid ) {
    int columnCount = grid.getColumnCount();
    return getRowHeadersColumn( grid ) == null ? columnCount : columnCount + 1;
  }

  private static String[] getColumnOrder( Grid grid ) {
    int[] order = grid.getColumnOrder();
    int offset = getColumnOffset( grid );
    String[] result = new String[ order.length + offset ];
    for( int i = offset; i < result.length; i++ ) {
      result[ i ] = getId( grid.getColumn( order[ i - offset ] ) );
    }
    if( offset == 1 ) {
      result[ 0 ] = getId( getRowHeadersColumn( grid ) );
    }
    return result;
  }

  private static int getFixedColumns( Grid grid ) {
    return getGridAdapter( grid ).getFixedColumns();
  }

  private static int getFocusCell( Grid grid ) {
    GridColumn focusColumn = grid.getFocusColumn();
    return focusColumn == null ? -1 : grid.indexOf( focusColumn ) + getColumnOffset( grid );
  }

  ///////////////
  // Item Metrics

  private static void renderItemMetrics( Grid grid ) {
    ItemMetrics[] itemMetrics = getItemMetrics( grid );
    if( WidgetLCAUtil.hasChanged( grid, PROP_ITEM_METRICS, itemMetrics ) ) {
      JsonArray metrics = new JsonArray();
      for( int i = 0; i < itemMetrics.length; i++ ) {
        metrics.add( new JsonArray().add( i )
                                    .add( itemMetrics[ i ].left )
                                    .add( itemMetrics[ i ].width )
                                    .add( itemMetrics[ i ].imageLeft )
                                    .add( itemMetrics[ i ].imageWidth )
                                    .add( itemMetrics[ i ].textLeft )
                                    .add( itemMetrics[ i ].textWidth )
                                    .add( itemMetrics[ i ].checkLeft )
                                    .add( itemMetrics[ i ].checkWidth ) );
      }
      getRemoteObject( grid ).set( PROP_ITEM_METRICS, metrics );
    }
  }

  static ItemMetrics[] getItemMetrics( Grid grid ) {
    int columnCount = grid.getColumnCount();
    int offset = getColumnOffset( grid );
    ItemMetrics[] result = new ItemMetrics[ columnCount + offset ];
    IGridAdapter adapter = getGridAdapter( grid );
    for( int i = offset; i < columnCount + offset; i++ ) {
      result[ i ] = new ItemMetrics();
      result[ i ].left = adapter.getCellLeft( i - offset );
      result[ i ].width = adapter.getCellWidth( i - offset );
      result[ i ].checkLeft = result[ i ].left + adapter.getCheckBoxOffset( i - offset );
      result[ i ].checkWidth = adapter.getCheckBoxWidth( i - offset );
      result[ i ].imageLeft = result[ i ].left + adapter.getImageOffset( i - offset );
      result[ i ].imageWidth = adapter.getImageWidth( i - offset );
      result[ i ].textLeft = result[ i ].left + adapter.getTextOffset( i - offset );
      result[ i ].textWidth = adapter.getTextWidth( i - offset );
    }
    if( offset == 1 ) {
      result[ 0 ] = getRowHeaderItemMetrics( grid );
    }
    return result;
  }

  private static ItemMetrics getRowHeaderItemMetrics( Grid grid ) {
    IGridAdapter adapter = getGridAdapter( grid );
    ItemMetrics result = new ItemMetrics();
    result.width = grid.getItemHeaderWidth();
    result.imageLeft = adapter.getRowHeaderImageOffset();
    result.imageWidth = adapter.getRowHeaderImageWidth();
    result.textLeft = adapter.getRowHeaderTextOffset();
    result.textWidth = adapter.getRowHeaderTextWidth();
    return result;
  }

  private static int getColumnOffset( Grid grid ) {
    return getRowHeadersColumn( grid ) != null ? 1 : 0;
  }

  private static GridColumn getRowHeadersColumn( Grid grid ) {
    return getGridAdapter( grid ).getRowHeadersColumn();
  }

  private static IGridAdapter getGridAdapter( Grid grid ) {
    return grid.getAdapter( IGridAdapter.class );
  }

  ////////////////
  // Inner classes

  static final class ItemMetrics {
    int left;
    int width;
    int checkLeft;
    int checkWidth;
    int imageLeft;
    int imageWidth;
    int textLeft;
    int textWidth;

    @Override
    public boolean equals( Object obj ) {
      boolean result;
      if( obj == this ) {
        result = true;
      } else  if( obj instanceof ItemMetrics ) {
        ItemMetrics other = ( ItemMetrics )obj;
        result =  other.left == left
               && other.width == width
               && other.checkLeft == checkLeft
               && other.checkWidth == checkWidth
               && other.imageLeft == imageLeft
               && other.imageWidth == imageWidth
               && other.textLeft == textLeft
               && other.textWidth == textWidth;
      } else {
        result = false;
      }
      return result;
    }

    @Override
    public int hashCode() {
      String msg = "ItemMetrics#hashCode() not implemented";
      throw new UnsupportedOperationException( msg );
    }
  }

}
