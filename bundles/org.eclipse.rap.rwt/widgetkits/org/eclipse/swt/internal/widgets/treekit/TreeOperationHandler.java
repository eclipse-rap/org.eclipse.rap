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
package org.eclipse.swt.internal.widgets.treekit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_ITEM;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.createKeyEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.createMenuDetectEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.createSelectionEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.processMouseEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.processTraverseEvent;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.find;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.util.OperationHandlerUtil;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;


public class TreeOperationHandler extends AbstractOperationHandler {

  private static final String PROP_SELECTION = "selection";
  private static final String PROP_SCROLL_LEFT = "scrollLeft";
  private static final String PROP_TOP_ITEM_INDEX = "topItemIndex";
  private static final String METHOD_RENDER_TOOLTIP_TEXT = "renderToolTipText";

  private final Tree tree;

  public TreeOperationHandler( Tree tree ) {
    this.tree = tree;
  }

  @Override
  public void handleNotify( String eventName, JsonObject properties ) {
    OperationHandlerUtil.handleNotify( this, eventName, properties );
  }

  /*
   * PROTOCOL NOTIFY Selection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param detail (string) "check" is checkbox is selected
   * @item item (string) id of selected item
   */
  public void handleNotifySelection( JsonObject properties ) {
    Event event = createSelectionEvent( SWT.Selection, properties );
    event.item = getItem( properties.get( EVENT_PARAM_ITEM ).asString() );
    tree.notifyListeners( SWT.Selection, event );
  }

  /*
   * PROTOCOL NOTIFY Selection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param detail (string) "check" is checkbox is selected
   * @item item (string) id of selected item
   */
  public void handleNotifyDefaultSelection( JsonObject properties ) {
    Event event = createSelectionEvent( SWT.DefaultSelection, properties );
    event.item = getItem( properties.get( EVENT_PARAM_ITEM ).asString() );
    tree.notifyListeners( SWT.DefaultSelection, event );
  }

  /*
   * PROTOCOL NOTIFY FocusIn
   */
  public void handleNotifyFocusIn( JsonObject properties ) {
    tree.notifyListeners( SWT.FocusIn, new Event() );
  }

  /*
   * PROTOCOL NOTIFY FocusOut
   */
  public void handleNotifyFocusOut( JsonObject properties ) {
    tree.notifyListeners( SWT.FocusOut, new Event() );
  }

  /*
   * PROTOCOL NOTIFY MouseDown
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param tree (int) the number of the mouse tree as in Event.tree
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyMouseDown( JsonObject properties ) {
    processMouseEvent( SWT.MouseDown, tree, properties );
  }

  /*
   * PROTOCOL NOTIFY MouseDoubleClick
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param tree (int) the number of the mouse tree as in Event.tree
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyMouseDoubleClick( JsonObject properties ) {
    processMouseEvent( SWT.MouseDoubleClick, tree, properties );
  }

  /*
   * PROTOCOL NOTIFY MouseUp
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param tree (int) the number of the mouse tree as in Event.tree
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyMouseUp( JsonObject properties ) {
    processMouseEvent( SWT.MouseUp, tree, properties );
  }

  /*
   * PROTOCOL NOTIFY Traverse
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param keyCode (int) the key code of the key that was typed
   * @param charCode (int) the char code of the key that was typed
   */
  public void handleNotifyTraverse( JsonObject properties ) {
    processTraverseEvent( tree, properties );
  }

  /*
   * PROTOCOL NOTIFY KeyDown
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param keyCode (int) the key code of the key that was typed
   * @param charCode (int) the char code of the key that was typed
   */
  public void handleNotifyKeyDown( JsonObject properties ) {
    tree.notifyListeners( SWT.KeyDown, createKeyEvent( properties ) );
    tree.notifyListeners( SWT.KeyUp, createKeyEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY MenuDetect
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   */
  public void handleNotifyMenuDetect( JsonObject properties ) {
    tree.notifyListeners( SWT.MenuDetect, createMenuDetectEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY Help
   */
  public void handleNotifyHelp( JsonObject properties ) {
    tree.notifyListeners( SWT.Help, new Event() );
  }

  /*
   * PROTOCOL NOTIFY Expand
   *
   * @item item (string) id of expanded item
   */
  public void handleNotifyExpand( JsonObject properties ) {
    Event event = new Event();
    event.item = getItem( properties.get( EVENT_PARAM_ITEM ).asString() );
    tree.notifyListeners( SWT.Expand, event );
  }

  /*
   * PROTOCOL NOTIFY Collapse
   *
   * @item item (string) id of collapsed item
   */
  public void handleNotifyCollapse( JsonObject properties ) {
    Event event = new Event();
    event.item = getItem( properties.get( EVENT_PARAM_ITEM ).asString() );
    tree.notifyListeners( SWT.Collapse, event );
  }

  @Override
  public void handleCall( String method, JsonObject properties ) {
    if( method.equals( METHOD_RENDER_TOOLTIP_TEXT ) ) {
      handleCallRenderToolTipText( properties );
    }
  }

  /*
   * PROTOCOL CALL renderToolTipText
   *
   * @item item (string) id of the hovered item
   * @column (int) column index of the hovered cell
   */
  private void handleCallRenderToolTipText( JsonObject properties ) {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipText( null );
    ICellToolTipProvider provider = adapter.getCellToolTipProvider();
    if( provider != null ) {
      TreeItem item = getItem( properties.get( "item" ).asString() );
      int columnIndex = properties.get( "column" ).asInt();
      if( item != null && ( columnIndex == 0 || columnIndex < tree.getColumnCount() ) ) {
        provider.getToolTipText( item, columnIndex );
      }
    }
  }

  @Override
  public void handleSet( JsonObject properties ) {
    handleSetSelection( properties );
    handleSetScrollLeft( properties );
    handleSetTopItemIndex( properties );
  }

  /*
   * PROTOCOL SET selection
   *
   * @param selection ([string]) array with ids of selected items
   */
  private void handleSetSelection( JsonObject properties ) {
    JsonValue values = properties.get( PROP_SELECTION );
    if( values != null ) {
      JsonArray itemIds = values.asArray();
      TreeItem[] selectedItems = new TreeItem[ itemIds.size() ];
      boolean validItemFound = false;
      for( int i = 0; i < itemIds.size(); i++ ) {
        selectedItems[ i ] = getItem( itemIds.get( i ).asString() );
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

  /*
   * PROTOCOL SET scrollLeft
   *
   * @param scrollLeft (int) left scroll offset in pixels
   */
  private void handleSetScrollLeft( JsonObject properties ) {
    JsonValue value = properties.get( PROP_SCROLL_LEFT );
    if( value != null ) {
      int scrollLeft = value.asInt();
      getTreeAdapter( tree ).setScrollLeft( scrollLeft );
      setScrollBarSelection( tree.getHorizontalBar(), scrollLeft );
    }
  }

  /*
   * PROTOCOL SET topItemIndex
   *
   * @param topItemIndex (int) visual index of the item, which is on the top of the tree
   */
  private void handleSetTopItemIndex( JsonObject properties ) {
    JsonValue value = properties.get( PROP_TOP_ITEM_INDEX );
    if( value != null ) {
      int topItemIndex = value.asInt();
      getTreeAdapter( tree ).setTopItemIndex( topItemIndex );
      int scrollTop = topItemIndex * tree.getItemHeight();
      setScrollBarSelection( tree.getVerticalBar(), scrollTop );
    }
  }

  private TreeItem getItem( String itemId ) {
    TreeItem item = null;
    String[] idParts = itemId.split( "#" );
    if( idParts.length == 2 ) {
      Widget parent = find( tree, idParts[ 0 ] );
      if( parent != null ) {
        int itemIndex = Integer.parseInt( idParts[ 1 ] );
        if( getId( tree ).equals( idParts[ 0 ] ) ) {
          item = tree.getItem( itemIndex );
        } else {
          item = ( ( TreeItem )parent ).getItem( itemIndex );
        }
      }
    } else {
      item = ( TreeItem )find( tree, itemId );
    }
    return item;
  }

  private static void setScrollBarSelection( ScrollBar scrollBar, int selection ) {
    if( scrollBar != null ) {
      scrollBar.setSelection( selection );
    }
  }

  private static ITreeAdapter getTreeAdapter( Tree tree ) {
    return tree.getAdapter( ITreeAdapter.class );
  }

}
