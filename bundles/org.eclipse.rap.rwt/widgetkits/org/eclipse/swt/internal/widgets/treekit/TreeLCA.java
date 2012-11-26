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
package org.eclipse.swt.internal.widgets.treekit;

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
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.ScrollBarLCAUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;


public final class TreeLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Grid";
  private static final String[] ALLOWED_STYLES = new String[] {
    "SINGLE",
    "MULTI",
    "CHECK",
    "FULL_SELECTION",
    "VIRTUAL",
    "NO_SCROLL",
    "NO_RADIO_GROUP",
    "BORDER"
  };

  private static final String PROP_ITEM_COUNT = "itemCount";
  private static final String PROP_ITEM_HEIGHT = "itemHeight";
  private static final String PROP_ITEM_METRICS = "itemMetrics";
  private static final String PROP_COLUMN_COUNT = "columnCount";
  private static final String PROP_FIXED_COLUMNS = "fixedColumns";
  private static final String PROP_TREE_COLUMN = "treeColumn";
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
  private static final String PROP_EXPAND_LISTENER = "Expand";
  private static final String PROP_COLLAPSE_LISTENER = "Collapse";
  private static final String PROP_ENABLE_CELL_TOOLTIP = "enableCellToolTip";
  private static final String PROP_CELL_TOOLTIP_TEXT = "cellToolTipText";
  private static final String PROP_MARKUP_ENABLED = "markupEnabled";

  private static final int ZERO = 0 ;
  private static final String[] DEFAULT_SELECTION = new String[ 0 ];
  private static final String DEFAULT_SORT_DIRECTION = "none";

  @Override
  public void preserveValues( Widget widget ) {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.preserveValues( ( Control )widget );
    WidgetLCAUtil.preserveCustomVariant( tree );
    preserveProperty( tree, PROP_ITEM_COUNT, tree.getItemCount() );
    preserveProperty( tree, PROP_ITEM_HEIGHT, tree.getItemHeight() );
    preserveProperty( tree, PROP_ITEM_METRICS, getItemMetrics( tree ) );
    preserveProperty( tree, PROP_COLUMN_COUNT, tree.getColumnCount() );
    preserveProperty( tree, PROP_FIXED_COLUMNS, getFixedColumns( tree ) );
    preserveProperty( tree, PROP_TREE_COLUMN, getTreeColumn( tree ) );
    preserveProperty( tree, PROP_HEADER_HEIGHT, tree.getHeaderHeight() );
    preserveProperty( tree, PROP_HEADER_VISIBLE, tree.getHeaderVisible() );
    preserveProperty( tree, PROP_LINES_VISIBLE, tree.getLinesVisible() );
    preserveProperty( tree, PROP_TOP_ITEM_INDEX, getTopItemIndex( tree ) );
    preserveProperty( tree, PROP_FOCUS_ITEM, getFocusItem( tree ) );
    preserveProperty( tree, PROP_SCROLL_LEFT, getScrollLeft( tree ) );
    preserveProperty( tree, PROP_SELECTION, getSelection( tree ) );
    preserveProperty( tree, PROP_SORT_DIRECTION, getSortDirection( tree ) );
    preserveProperty( tree, PROP_SORT_COLUMN, tree.getSortColumn() );
    preserveListener( tree, PROP_SELECTION_LISTENER, tree.isListening( SWT.Selection ) );
    preserveListener( tree, PROP_SETDATA_LISTENER, listensToSetData( tree ) );
    preserveListener( tree,
                      PROP_DEFAULT_SELECTION_LISTENER,
                      tree.isListening( SWT.DefaultSelection ) );
    preserveListener( tree, PROP_EXPAND_LISTENER, hasExpandListener( tree ) );
    preserveListener( tree, PROP_COLLAPSE_LISTENER, hasCollapseListener( tree ) );
    preserveProperty( tree, PROP_ENABLE_CELL_TOOLTIP, CellToolTipUtil.isEnabledFor( tree ) );
    preserveProperty( tree, PROP_CELL_TOOLTIP_TEXT, null );
    ScrollBarLCAUtil.preserveValues( tree );
  }

  public void readData( Widget widget ) {
    Tree tree = ( Tree )widget;
    readSelection( tree );
    readScrollLeft( tree );
    readTopItemIndex( tree );
    processWidgetSelectedEvent( tree );
    processWidgetDefaultSelectedEvent( tree );
    processTreeEvent( tree, SWT.Expand, ClientMessageConst.EVENT_EXPAND );
    processTreeEvent( tree, SWT.Collapse, ClientMessageConst.EVENT_COLLAPSE );
    readCellToolTipTextRequested( tree );
    ControlLCAUtil.processEvents( tree );
    ControlLCAUtil.processKeyEvents( tree );
    ControlLCAUtil.processMenuDetect( tree );
    WidgetLCAUtil.processHelp( tree );
    ScrollBarLCAUtil.processSelectionEvent( tree );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( tree );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( tree.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( tree, ALLOWED_STYLES ) );
    clientObject.set( "appearance", "tree" );
    ITreeAdapter adapter = getTreeAdapter( tree );
    if( ( tree.getStyle() & SWT.CHECK ) != 0 ) {
      int[] checkMetrics = new int[] { adapter.getCheckLeft(), adapter.getCheckWidth() };
      clientObject.set( "checkBoxMetrics", checkMetrics );
    }
    if( getFixedColumns( tree ) >= 0 ) {
      clientObject.set( "splitContainer", true );
    }
    if( ( tree.getStyle() & SWT.FULL_SELECTION ) == 0 ) {
      Rectangle textMargin = getTreeAdapter( tree ).getTextMargin();
      int[] selectionPadding = new int[] { textMargin.x, textMargin.width - textMargin.x };
      clientObject.set( "selectionPadding", selectionPadding );
    }
    clientObject.set( "indentionWidth", adapter.getIndentionWidth() );
    clientObject.set( PROP_MARKUP_ENABLED, isMarkupEnabled( tree ) );
    ScrollBarLCAUtil.renderInitialization( tree );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.renderChanges( tree );
    WidgetLCAUtil.renderCustomVariant( tree );
    renderProperty( tree, PROP_ITEM_COUNT, tree.getItemCount(), ZERO );
    renderProperty( tree, PROP_ITEM_HEIGHT, tree.getItemHeight(), ZERO );
    renderItemMetrics( tree );
    renderProperty( tree, PROP_COLUMN_COUNT, tree.getColumnCount(), ZERO );
    renderProperty( tree, PROP_FIXED_COLUMNS, getFixedColumns( tree ), -1 );
    renderProperty( tree, PROP_TREE_COLUMN, getTreeColumn( tree ), ZERO );
    renderProperty( tree, PROP_HEADER_HEIGHT, tree.getHeaderHeight(), ZERO );
    renderProperty( tree, PROP_HEADER_VISIBLE, tree.getHeaderVisible(), false );
    renderProperty( tree, PROP_LINES_VISIBLE, tree.getLinesVisible(), false );
    renderProperty( tree, PROP_TOP_ITEM_INDEX, getTopItemIndex( tree ), ZERO );
    if( tree.getSelectionCount() > 0 ) {
      renderProperty( tree, PROP_FOCUS_ITEM, getFocusItem( tree ), null );
    }
    renderProperty( tree, PROP_SCROLL_LEFT, getScrollLeft( tree ), ZERO );
    renderProperty( tree, PROP_SELECTION, getSelection( tree ), DEFAULT_SELECTION );
    renderProperty( tree, PROP_SORT_DIRECTION, getSortDirection( tree ), DEFAULT_SORT_DIRECTION );
    renderProperty( tree, PROP_SORT_COLUMN, tree.getSortColumn(), null );
    renderListener( tree, PROP_SELECTION_LISTENER, tree.isListening( SWT.Selection ), false );
    renderListener( tree, PROP_SETDATA_LISTENER, listensToSetData( tree ), false );
    renderListener( tree,
                    PROP_DEFAULT_SELECTION_LISTENER,
                    tree.isListening( SWT.DefaultSelection ),
                    false );
    renderListener( tree, PROP_EXPAND_LISTENER, hasExpandListener( tree ), false );
    renderListener( tree, PROP_COLLAPSE_LISTENER, hasCollapseListener( tree ), false );
    renderProperty( tree, PROP_ENABLE_CELL_TOOLTIP, CellToolTipUtil.isEnabledFor( tree ), false );
    renderProperty( tree, PROP_CELL_TOOLTIP_TEXT, getCellToolTipText( tree ), null );
    ScrollBarLCAUtil.renderChanges( tree );
  }

  @Override
  public void doRedrawFake( Control control ) {
    Tree tree = ( Tree )control;
    getTreeAdapter( tree ).checkData();
  }

  private static void processWidgetSelectedEvent( Tree tree ) {
    String eventName = ClientMessageConst.EVENT_SELECTION;
    if( WidgetLCAUtil.wasEventSent( tree, eventName ) ) {
      String value = readEventPropertyValue( tree, eventName, ClientMessageConst.EVENT_PARAM_ITEM );
      Item treeItem = getItem( tree, value );
      ControlLCAUtil.processSelection( tree, treeItem, false );
    }
  }

  private static void processWidgetDefaultSelectedEvent( Tree tree ) {
    String eventName = ClientMessageConst.EVENT_DEFAULT_SELECTION;
    if( WidgetLCAUtil.wasEventSent( tree, eventName ) ) {
      String value = readEventPropertyValue( tree, eventName, ClientMessageConst.EVENT_PARAM_ITEM );
      Item treeItem = getItem( tree, value );
      ControlLCAUtil.processDefaultSelection( tree, treeItem );
    }
  }

  /////////////////////////////////
  // Process expand/collapse events

  private static void processTreeEvent( Tree tree, int eventType, String eventName ) {
    if( WidgetLCAUtil.wasEventSent( tree, eventName ) ) {
      String value = readEventPropertyValue( tree, eventName, ClientMessageConst.EVENT_PARAM_ITEM );
      Event event = new Event();
      event.item = getItem( tree, value );
      tree.notifyListeners( eventType, event );
    }
  }

  /////////////////////////////////////////////
  // Helping methods to read client-side state

  private static void readSelection( Tree tree ) {
    String[] values = ProtocolUtil.readPropertyValueAsStringArray( getId( tree ), "selection" );
    if( values != null ) {
      TreeItem[] selectedItems = new TreeItem[ values.length ];
      boolean validItemFound = false;
      for( int i = 0; i < values.length; i++ ) {
        selectedItems[ i ] = getItem( tree, values[ i ] );
        if( selectedItems[ i ] != null ) {
          validItemFound = true;
        }
      }
      if( !validItemFound ) {
        selectedItems = new TreeItem[ 0 ];
      }
      tree.setSelection( selectedItems );
    }
  }

  private static void readScrollLeft( Tree tree ) {
    String left = WidgetLCAUtil.readPropertyValue( tree, "scrollLeft" );
    if( left != null ) {
      int leftOffset = parsePosition( left );
      final ITreeAdapter treeAdapter = getTreeAdapter( tree );
      treeAdapter.setScrollLeft( leftOffset );
      processScrollBarSelection( tree.getHorizontalBar(), leftOffset );
    }
  }

  private static void readTopItemIndex( Tree tree ) {
    String topItemIndex = WidgetLCAUtil.readPropertyValue( tree, "topItemIndex" );
    if( topItemIndex != null ) {
      final ITreeAdapter treeAdapter = getTreeAdapter( tree );
      int newIndex = parsePosition( topItemIndex );
      int topOffset = newIndex * tree.getItemHeight();
      treeAdapter.setTopItemIndex( newIndex );
      processScrollBarSelection( tree.getVerticalBar(), topOffset );
    }
  }

  private static int parsePosition( String position ) {
    int result = 0;
    try {
      result = Integer.valueOf( position ).intValue();
    } catch( NumberFormatException e ) {
      // ignore and use default value
    }
    return result;
  }

  ////////////////
  // Cell tooltips

  private static void readCellToolTipTextRequested( Tree tree ) {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipText( null );
    ICellToolTipProvider provider = adapter.getCellToolTipProvider();
    String methodName = "renderToolTipText";
    if( provider != null && ProtocolUtil.wasCallSend( getId( tree ), methodName ) ) {
      String itemId = readCallPropertyValueAsString( getId( tree ), methodName, "item" );
      String column = readCallPropertyValueAsString( getId( tree ), methodName, "column" );
      int columnIndex = NumberFormatUtil.parseInt( column );
      TreeItem item = getItem( tree, itemId );
      if( item != null && ( columnIndex == 0 || columnIndex < tree.getColumnCount() ) ) {
        provider.getToolTipText( item, columnIndex );
      }
    }
  }

  private static String getCellToolTipText( Tree tree ) {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    return adapter.getCellToolTipText();
  }

  //////////////////
  // Helping methods

  private boolean listensToSetData( Tree tree ) {
    return ( tree.getStyle() & SWT.VIRTUAL ) != 0;
  }

  private static boolean isMarkupEnabled( Tree tree ) {
    return Boolean.TRUE.equals( tree.getData( RWT.MARKUP_ENABLED ) );
  }

  private static String[] getSelection( Tree tree ) {
    TreeItem[] selection = tree.getSelection();
    String[] result = new String[ selection.length ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = getId( selection[ i ] );
    }
    return result;
  }

  private int getFixedColumns( Tree tree ) {
    return getTreeAdapter( tree ).getFixedColumns();
  }

  private static int getScrollLeft( Tree tree ) {
    return getTreeAdapter( tree ).getScrollLeft();
  }

  private static int getTopItemIndex( Tree tree ) {
    return getTreeAdapter( tree ).getTopItemIndex();
  }

  private static TreeItem getFocusItem( Tree tree ) {
    TreeItem result = null;
    TreeItem[] selection = tree.getSelection();
    if( selection.length > 0 ) {
      result = selection[ 0 ];
    }
    return result;
  }

  private static int getTreeColumn( Tree tree ) {
    int[] values = tree.getColumnOrder();
    return values.length > 0 ? values[ 0 ] : 0;
  }

  private static String getSortDirection( Tree tree ) {
    String result = "none";
    if( tree.getSortDirection() == SWT.UP ) {
      result = "up";
    } else if( tree.getSortDirection() == SWT.DOWN ) {
      result = "down";
    }
    return result;
  }

  private static void processScrollBarSelection( ScrollBar scrollBar, int selection ) {
    if( scrollBar != null ) {
      scrollBar.setSelection( selection );
    }
  }

  private static boolean hasExpandListener( Tree tree ) {
    // Always render listen for Expand and Collapse, currently required for scrollbar
    // visibility update and setData events.
    return true;
  }

  private static boolean hasCollapseListener( Tree tree ) {
    // Always render listen for Expand and Collapse, currently required for scrollbar
    // visibility update and setData events.
    return true;
  }

  private static TreeItem getItem( Tree tree, String itemId ) {
    TreeItem item = null;
    String[] idParts = itemId.split( "#" );
    if( idParts.length == 2 ) {
      Widget parent = WidgetUtil.find( tree, idParts[ 0 ] );
      if( parent != null ) {
        int itemIndex = Integer.parseInt( idParts[ 1 ] );
        if( getId( tree ).equals( idParts[ 0 ] ) ) {
          item = tree.getItem( itemIndex );
        } else {
          item = ( ( TreeItem )parent ).getItem( itemIndex );
        }
      }
    } else {
      item = ( TreeItem )WidgetUtil.find( tree, itemId );
    }
    return item;
  }

  private static ITreeAdapter getTreeAdapter( Tree tree ) {
    return tree.getAdapter( ITreeAdapter.class );
  }

  ///////////////
  // Item Metrics

  private static void renderItemMetrics( Tree tree ) {
    ItemMetrics[] itemMetrics = getItemMetrics( tree );
    if( WidgetLCAUtil.hasChanged( tree, PROP_ITEM_METRICS, itemMetrics ) ) {
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
      IClientObject clientObject = ClientObjectFactory.getClientObject( tree );
      clientObject.set( PROP_ITEM_METRICS, metrics );
    }
  }

  static ItemMetrics[] getItemMetrics( Tree tree ) {
    int columnCount = Math.max( 1, tree.getColumnCount() );
    ItemMetrics[] result = new ItemMetrics[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ] = new ItemMetrics();
    }
    ITreeAdapter adapter = getTreeAdapter( tree );
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ].left = adapter.getCellLeft( i );
      result[ i ].width = adapter.getCellWidth( i );
      result[ i ].imageLeft = result[ i ].left + adapter.getImageOffset( i );
      result[ i ].imageWidth = adapter.getItemImageSize( i ).x;
      result[ i ].textLeft = result[ i ].left + adapter.getTextOffset( i );
      result[ i ].textWidth = adapter.getTextMaxWidth( i );
    }
    return result;
  }

  // TODO: merge with Table:
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
