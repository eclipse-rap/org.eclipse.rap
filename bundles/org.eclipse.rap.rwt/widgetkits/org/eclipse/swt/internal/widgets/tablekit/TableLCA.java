/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tablekit;

import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readCallPropertyValueAsString;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readEventPropertyValue;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.ScrollBarLCAUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;


public final class TableLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Grid";
  private static final String[] ALLOWED_STYLES = new String[] {
    "SINGLE",
    "MULTI",
    "CHECK",
    "FULL_SELECTION",
    "HIDE_SELECTION",
    "VIRTUAL",
    "NO_SCROLL",
    "NO_RADIO_GROUP",
    "BORDER"
  };

  private static final String PROP_ITEM_COUNT = "itemCount";
  private static final String PROP_ITEM_HEIGHT = "itemHeight";
  private static final String PROP_ITEM_METRICS = "itemMetrics";
  private static final String PROP_COLUMN_COUNT = "columnCount";
  private static final String PROP_TREE_COLUMN = "treeColumn";
  private static final String PROP_FIXED_COLUMNS = "fixedColumns";
  private static final String PROP_HEADER_HEIGHT = "headerHeight";
  private static final String PROP_HEADER_VISIBLE = "headerVisible";
  private static final String PROP_LINES_VISIBLE = "linesVisible";
  private static final String PROP_TOP_ITEM_INDEX = "topItemIndex";
  private static final String PROP_FOCUS_ITEM = "focusItem";
  private static final String PROP_SCROLL_LEFT = "scrollLeft";
  private static final String PROP_SELECTION = "selection";
  private static final String PROP_SORT_DIRECTION = "sortDirection";
  private static final String PROP_SORT_COLUMN = "sortColumn";
  private static final String PROP_SELECTION_LISTENER = "Selection";
  private static final String PROP_SETDATA_LISTENER = "SetData";
  private static final String PROP_DEFAULT_SELECTION_LISTENER = "DefaultSelection";
  private static final String PROP_ALWAYS_HIDE_SELECTION = "alwaysHideSelection";
  private static final String PROP_ENABLE_CELL_TOOLTIP = "enableCellToolTip";
  private static final String PROP_CELL_TOOLTIP_TEXT = "cellToolTipText";
  private static final String PROP_MARKUP_ENABLED = "markupEnabled";

  private static final int ZERO = 0 ;
  private static final String[] DEFAULT_SELECTION = new String[ 0 ];
  private static final String DEFAULT_SORT_DIRECTION = "none";

  @Override
  public void preserveValues( Widget widget ) {
    Table table = ( Table )widget;
    ControlLCAUtil.preserveValues( table );
    WidgetLCAUtil.preserveCustomVariant( table );
    preserveProperty( table, PROP_ITEM_COUNT, table.getItemCount() );
    preserveProperty( table, PROP_ITEM_HEIGHT, table.getItemHeight() );
    preserveProperty( table, PROP_ITEM_METRICS, getItemMetrics( table ) );
    preserveProperty( table, PROP_COLUMN_COUNT, table.getColumnCount() );
    preserveProperty( table, PROP_FIXED_COLUMNS, getFixedColumns( table ) );
    preserveProperty( table, PROP_HEADER_HEIGHT, table.getHeaderHeight() );
    preserveProperty( table, PROP_HEADER_VISIBLE, table.getHeaderVisible() );
    preserveProperty( table, PROP_LINES_VISIBLE, table.getLinesVisible() );
    preserveProperty( table, PROP_TOP_ITEM_INDEX, table.getTopIndex() );
    preserveProperty( table, PROP_FOCUS_ITEM, getFocusItem( table ) );
    preserveProperty( table, PROP_SCROLL_LEFT, getScrollLeft( table ) );
    preserveProperty( table, PROP_SELECTION, getSelection( table ) );
    preserveProperty( table, PROP_SORT_DIRECTION, getSortDirection( table ) );
    preserveProperty( table, PROP_SORT_COLUMN, table.getSortColumn() );
    preserveListener( table, PROP_SELECTION_LISTENER, table.isListening( SWT.Selection ) );
    preserveListener( table, PROP_SETDATA_LISTENER, listensToSetData( table ) );
    preserveListener( table,
                      PROP_DEFAULT_SELECTION_LISTENER,
                      table.isListening( SWT.DefaultSelection ) );
    preserveProperty( table, PROP_ALWAYS_HIDE_SELECTION, hasAlwaysHideSelection( table ) );
    preserveProperty( table, PROP_ENABLE_CELL_TOOLTIP, CellToolTipUtil.isEnabledFor( table ) );
    preserveProperty( table, PROP_CELL_TOOLTIP_TEXT, null );
    ScrollBarLCAUtil.preserveValues( table );
  }

  public void readData( Widget widget ) {
    Table table = ( Table )widget;
    readTopItemIndex( table );
    readScrollLeft( table );
    readSelection( table );
    readFocusIndex( table ); // must be called *after* readSelection
    readWidgetSelected( table );
    readWidgetDefaultSelected( table );
    readCellToolTipTextRequested( table );
    ControlLCAUtil.processEvents( table );
    ControlLCAUtil.processKeyEvents( table );
    ControlLCAUtil.processMenuDetect( table );
    ScrollBarLCAUtil.processSelectionEvent( table );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Table table = ( Table )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( table );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( table.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( table, ALLOWED_STYLES ) );
    clientObject.set( "appearance", "table" );
    ITableAdapter adapter = getTableAdapter( table );
    if( ( table.getStyle() & SWT.CHECK ) != 0 ) {
      int[] checkMetrics = new int[] { adapter.getCheckLeft(), adapter.getCheckWidth() };
      clientObject.set( "checkBoxMetrics", checkMetrics );
    }
    if( getFixedColumns( table ) >= 0 ) {
      clientObject.set( "splitContainer", true );
    }
    clientObject.set( "indentionWidth", 0 );
    clientObject.set( PROP_TREE_COLUMN, -1 );
    clientObject.set( PROP_MARKUP_ENABLED, isMarkupEnabled( table ) );
    ScrollBarLCAUtil.renderInitialization( table );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Table table = ( Table )widget;
    ControlLCAUtil.renderChanges( table );
    WidgetLCAUtil.renderCustomVariant( table );
    renderProperty( table, PROP_ITEM_COUNT, table.getItemCount(), ZERO );
    renderProperty( table, PROP_ITEM_HEIGHT, table.getItemHeight(), ZERO );
    renderItemMetrics( table );
    renderProperty( table, PROP_COLUMN_COUNT, table.getColumnCount(), ZERO );
    renderProperty( table, PROP_FIXED_COLUMNS, getFixedColumns( table ), -1 );
    renderProperty( table, PROP_HEADER_HEIGHT, table.getHeaderHeight(), ZERO );
    renderProperty( table, PROP_HEADER_VISIBLE, table.getHeaderVisible(), false );
    renderProperty( table, PROP_LINES_VISIBLE, table.getLinesVisible(), false );
    renderProperty( table, PROP_TOP_ITEM_INDEX, table.getTopIndex(), ZERO );
    renderProperty( table, PROP_FOCUS_ITEM, getFocusItem( table ), null );
    renderProperty( table, PROP_SCROLL_LEFT, getScrollLeft( table ), ZERO );
    renderProperty( table, PROP_SELECTION, getSelection( table ), DEFAULT_SELECTION );
    renderProperty( table, PROP_SORT_DIRECTION, getSortDirection( table ), DEFAULT_SORT_DIRECTION );
    renderProperty( table, PROP_SORT_COLUMN, table.getSortColumn(), null );
    renderListener( table, PROP_SELECTION_LISTENER, table.isListening( SWT.Selection ), false );
    renderListener( table, PROP_SETDATA_LISTENER, listensToSetData( table ), false );
    renderListener( table,
                    PROP_DEFAULT_SELECTION_LISTENER,
                    table.isListening( SWT.DefaultSelection ),
                    false );
    renderProperty( table, PROP_ALWAYS_HIDE_SELECTION, hasAlwaysHideSelection( table ), false );
    renderProperty( table, PROP_ENABLE_CELL_TOOLTIP, CellToolTipUtil.isEnabledFor( table ), false );
    renderProperty( table, PROP_CELL_TOOLTIP_TEXT, getCellToolTipText( table ), null );
    ScrollBarLCAUtil.renderChanges( table );
  }

  @Override
  public void doRedrawFake( Control control ) {
    Table table = ( Table )control;
    getTableAdapter( table ).checkData();
  }

  ////////////////////////////////////////////////////
  // Helping methods to read client-side state changes

  private static void readSelection( Table table ) {
    String[] values = ProtocolUtil.readPropertyValueAsStringArray( getId( table ), "selection" );
    if( values != null ) {
      int[] newSelection = new int[ values.length ];
      for( int i = 0; i < values.length; i++ ) {
        String itemId = values[ i ];
        TableItem item = getItem( table, itemId );
        if( item != null ) {
          newSelection[ i ] = table.indexOf( item );
        } else {
          newSelection[ i ] = -1;
        }
      }
      table.deselectAll();
      table.select( newSelection );
    }
  }

  private static void readTopItemIndex( Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "topItemIndex" );
    if( value != null ) {
      int topIndex = NumberFormatUtil.parseInt( value );
      table.setTopIndex( topIndex );
    }
  }

  private static void readFocusIndex( Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "focusItem" );
    if( value != null ) {
      TableItem item = getItem( table, value );
      if( item != null ) {
        getTableAdapter( table ).setFocusIndex( table.indexOf( item ) );
      }
    }
  }

  private static void readScrollLeft( Table table ) {
    String value = WidgetLCAUtil.readPropertyValue( table, "scrollLeft" );
    if( value != null ) {
      int leftOffset = NumberFormatUtil.parseInt( value );
      table.getAdapter( ITableAdapter.class ).setLeftOffset( leftOffset );
    }
  }

  private static void readWidgetSelected( Table table ) {
    String eventName = ClientMessageConst.EVENT_SELECTION;
    if( WidgetLCAUtil.wasEventSent( table, eventName ) ) {
      String value = readEventPropertyValue( table, eventName, ClientMessageConst.EVENT_PARAM_ITEM );
      TableItem item = getItem( table, value );
      // Bugfix: check if index is valid before firing event to avoid problems with fast scrolling
      // TODO [tb] : Still useful? bugzilla id?
      if( item != null ) {
        ControlLCAUtil.processSelection( table, item, false );
      }
    }
  }

  private static void readWidgetDefaultSelected( Table table ) {
    String eventName = ClientMessageConst.EVENT_DEFAULT_SELECTION;
    if( WidgetLCAUtil.wasEventSent( table, eventName ) ) {
      // A default-selected event can occur without a selection being present.
      // In this case the event.item field points to the focused item
      TableItem item = getFocusItem( table );
      String value = readEventPropertyValue( table, eventName, ClientMessageConst.EVENT_PARAM_ITEM );
      TableItem selectedItem = getItem( table, value );
      if( selectedItem != null ) {
        // TODO [rh] do something about when index points to unresolved item!
        item = selectedItem;
      }
      ControlLCAUtil.processDefaultSelection( table, item );
    }
  }

  ////////////////
  // Cell tooltips

  private static void readCellToolTipTextRequested( Table table ) {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    adapter.setCellToolTipText( null );
    ICellToolTipProvider provider = adapter.getCellToolTipProvider();
    String methodName = "renderToolTipText";
    if( provider != null && ProtocolUtil.wasCallSend( getId( table ), methodName ) ) {
      String itemId = readCallPropertyValueAsString( getId( table ), methodName, "item" );
      String column = readCallPropertyValueAsString( getId( table ), methodName, "column" );
      int columnIndex = NumberFormatUtil.parseInt( column );
      TableItem item = getItem( table, itemId );
      if( item != null && ( columnIndex == 0 || columnIndex < table.getColumnCount() ) ) {
        provider.getToolTipText( item, columnIndex );
      }
    }
  }

  private static String getCellToolTipText( Table table ) {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    return adapter.getCellToolTipText();
  }

  //////////////////
  // Helping methods

  private boolean listensToSetData( Table table ) {
    return ( table.getStyle() & SWT.VIRTUAL ) != 0;
  }

  private static boolean isMarkupEnabled( Table table ) {
    return Boolean.TRUE.equals( table.getData( RWT.MARKUP_ENABLED ) );
  }

  private static String[] getSelection( Table table ) {
    TableItem[] selection = table.getSelection();
    String[] result = new String[ selection.length ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = WidgetUtil.getId( selection[ i ] );
    }
    return result;
  }

  private int getFixedColumns( Table table ) {
    return getTableAdapter( table ).getFixedColumns();
  }

  private static int getScrollLeft( Table table ) {
    return getTableAdapter( table ).getLeftOffset();
  }

  private static TableItem getFocusItem( Table table ) {
    TableItem result = null;
    int focusIndex = getTableAdapter( table ).getFocusIndex();
    if( focusIndex != -1 ) {
      // TODO [rh] do something about when index points to unresolved item!
      result = table.getItem( focusIndex );
    }
    return result;
  }

  private static String getSortDirection( Table table ) {
    String result = "none";
    if( table.getSortDirection() == SWT.UP ) {
      result = "up";
    } else if( table.getSortDirection() == SWT.DOWN ) {
      result = "down";
    }
    return result;
  }

  static boolean hasAlwaysHideSelection( Table table ) {
    Object data = table.getData( Table.ALWAYS_HIDE_SELECTION );
    return Boolean.TRUE.equals( data );
  }

  private static TableItem getItem( Table table, String itemId ) {
    TableItem item;
    String[] idParts = itemId.split( "#" );
    if( idParts.length == 2 ) {
      int index = Integer.parseInt( idParts[ 1 ] );
      item = table.getItem( index );
    } else {
      item = ( TableItem )WidgetUtil.find( table, itemId );
    }
    return item;
  }

  private static ITableAdapter getTableAdapter( Table table ) {
    return table.getAdapter( ITableAdapter.class );
  }

  /////////////////
  // Item Metrics

  private static void renderItemMetrics( Table table ) {
    ItemMetrics[] itemMetrics = getItemMetrics( table );
    if( WidgetLCAUtil.hasChanged( table, PROP_ITEM_METRICS, itemMetrics ) ) {
      int[][] metrics = new int[ itemMetrics.length ][ 7 ];
      for( int i = 0; i < itemMetrics.length; i++ ) {
        metrics[ i ] = new int[] {
          i,
          itemMetrics[ i ].left,
          itemMetrics[ i ].width,
          itemMetrics[ i ].imageLeft,
          itemMetrics[ i ].imageWidth,
          itemMetrics[ i ].textLeft,
          itemMetrics[ i ].textWidth
        };
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( table );
      clientObject.set( PROP_ITEM_METRICS, metrics );
    }
  }

  static ItemMetrics[] getItemMetrics( Table table ) {
    int columnCount = Math.max( 1, table.getColumnCount() );
    ItemMetrics[] result = new ItemMetrics[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ] = new ItemMetrics();
    }
    ITableAdapter tableAdapter = getTableAdapter( table );
    TableItem measureItem = tableAdapter.getMeasureItem();
    if( measureItem != null ) {
      for( int i = 0; i < columnCount; i++ ) {
        int leftOffset = tableAdapter.getColumnLeftOffset( i );
        Rectangle bounds = measureItem.getBounds( i );
        Rectangle imageBounds = measureItem.getImageBounds( i );
        Rectangle textBounds = measureItem.getTextBounds( i );
        // If in column mode, cut image width if image exceeds right cell border
        int imageWidth = tableAdapter.getItemImageWidth( i );
        if( table.getColumnCount() > 0 ) {
          TableColumn column = table.getColumn( i );
          int columnLeft = tableAdapter.getColumnLeft( column );
          int columnWidth = column.getWidth();
          int maxImageWidth = columnWidth - ( imageBounds.x - columnLeft + leftOffset );
          if( imageWidth > maxImageWidth ) {
            imageWidth = Math.max( 0, maxImageWidth );
          }
        }
        result[ i ].left = bounds.x + leftOffset;
        result[ i ].width = bounds.width;
        result[ i ].imageLeft = imageBounds.x + leftOffset;
        result[ i ].imageWidth = imageWidth;
        result[ i ].textLeft = textBounds.x + leftOffset;
        result[ i ].textWidth = textBounds.width;
      }
    } else if( table.getColumnCount() > 0 ) {
      for( int i = 0; i < columnCount; i++ ) {
        TableColumn column = table.getColumn( i );
        int columnLeft = tableAdapter.getColumnLeft( column );
        int columnWidth = column.getWidth();
        result[ i ].left = columnLeft;
        result[ i ].width = columnWidth;
      }
    }
    return result;
  }

  static final class ItemMetrics {
    int left;
    int width;
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
