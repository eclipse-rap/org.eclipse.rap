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

package org.eclipse.swt.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.*;


/** 
 * Instances of this class implement a selectable user interface
 * object that displays a list of images and strings and issues
 * notification when selected.
 * <p>
 * The item children that may be added to instances of this class
 * must be of type <code>TableItem</code>.
 * </p><p>
 * Style <code>VIRTUAL</code> is used to create a <code>Table</code> whose
 * <code>TableItem</code>s are to be populated by the client on an on-demand basis
 * instead of up-front.  This can provide significant performance improvements for
 * tables that are very large or for which <code>TableItem</code> population is
 * expensive (for example, retrieving values from an external source).
 * </p><p>
 * Here is an example of using a <code>Table</code> with style <code>VIRTUAL</code>:
 * <code><pre>
 *  final Table table = new Table (parent, SWT.VIRTUAL | SWT.BORDER);
 *  table.setItemCount (1000000);
 *  table.addListener (SWT.SetData, new Listener () {
 *      public void handleEvent (Event event) {
 *          TableItem item = (TableItem) event.item;
 *          int index = table.indexOf (item);
 *          item.setText ("Item " + index);
 *          System.out.println (item.getText ());
 *      }
 *  }); 
 * </pre></code>
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add <code>Control</code> children to it,
 * or set a layout on it.
 * </p><p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI, CHECK, FULL_SELECTION, HIDE_SELECTION, VIRTUAL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection, SetData, MeasureItem, EraseItem, PaintItem</dd>
 * </dl>
 * </p><p>
 * Note: Only one of the styles SINGLE, and MULTI may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * <p>Current state of Table implementation:</p> 
 * <ul>
 *  <li>showSelection and showItem currently do a very rough proximation since
 *  getClientArea is not yet implemented properly</li>
 *  <li>Scroll are visible even though not necessary</li>
 *  <li>No keyboard navigation</li>
 * </ul> 
 */
public class Table extends Composite {
  
  private static final int GRID_WIDTH = 1;
  private static final int CHECK_HEIGHT = 13;
  
  private static final TableItem[] EMPTY_ITEMS = new TableItem[ 0 ];

  private final ItemHolder itemHolder;
  private final ItemHolder columnHolder;
  private final ITableAdapter tableAdapter;
  private final ResizeListener resizeListener;
  private int[] columnOrder;
  private TableItem[] selection;
  private boolean linesVisible;
  private boolean headerVisible;
  private int topIndex;
  private int focusIndex;
  private TableColumn sortColumn;
  private int sortDirection;
  private Point itemImageSize;
  
  
  // handle the fact that we have two item types to deal with
  private final class CompositeItemHolder implements IItemHolderAdapter {
    public void add( final Item item ) {
      if( item instanceof TableItem ) {
        itemHolder.add( item );
      } else {
        columnHolder.add( item );
      }
    }
    public void insert( final Item item, final int index ) {
      if( item instanceof TableItem ) {
        itemHolder.insert( item, index );
      } else {
        columnHolder.insert( item, index );
      }
    }
    public void remove( final Item item ) {
      if( item instanceof TableItem ) {
        itemHolder.remove( item );
      } else {
        columnHolder.remove( item );
      }
    }
    public Item[] getItems() {
      TableItem[] allItems = ( TableItem[] )itemHolder.getItems();
      TableItem[] items = filterCachedItems( allItems );
      Item[] columns = columnHolder.getItems();
      Item[] result = new Item[ items.length + columns.length ];
      System.arraycopy( columns, 0, result, 0, columns.length );
      System.arraycopy( items, 0, result, columns.length, items.length );
      return result;
    }
    private TableItem[] filterCachedItems( final TableItem[] items ) {
      int count = 0;
      for( int i = 0; i < items.length; i++ ) {
        if( items[ i ].cached ) {
          count++;
        }
      }
      TableItem[] result = new TableItem[ count ];
      count = 0;
      for( int i = 0; i < items.length; i++ ) {
        if( items[ i ].cached ) {
          result[ count ] = items[ i ];
          count++;
        }
      }
      return result;
    }
  }
  
  private final class TableAdapter implements ITableAdapter {
    public int getCheckWidth() {
      return Table.this.getCheckWidth();
    }
    public int getFocusIndex() {
      return Table.this.focusIndex; 
    }
    
    public void setFocusIndex( final int focusIndex ) {
      Table.this.setFocusIndex( focusIndex );
    }
    
    public void checkData( final int index ) {
      Table.this.checkData( Table.this.getItem( index ), index );  
    }
    
    public int getDefaultColumnWidth() {
      int result = 0;
      TableItem[] items = getItems();
      for( int i = 0; i < items.length; i++ ) {
        result = Math.max( result, items[i].getPackWidth( 0 ) );
      }
      return result;
    }
    
    public int getColumnLeft( final TableColumn column ) {
      int index = Table.this.indexOf( column );
      return Table.this.getColumn( index ).getLeft();
    }

    public boolean isItemVisible( final TableItem item ) {
      return item.isVisible();
    }
    public boolean isItemVirtual( final TableItem item ) {
      return !item.cached;
    }
  }
  
  private static final class ResizeListener extends ControlAdapter {
    public void controlResized( final ControlEvent event ) {
      Table table = ( Table )event.widget;
      boolean visible = true;
      int index = Math.max( 0, table.getTopIndex() );
      int count = table.getItemCount();
      while( visible && index < count ) {
        TableItem item = table.getItem( index );
        visible = item.isVisible();
        if( visible ) {
          table.checkData( item, index );
        }
        index++;
      }
    }
  }
  
  /**
   * Constructs a new instance of this class given its parent
   * and a style value describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together 
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#SINGLE
   * @see SWT#MULTI
   * @see SWT#CHECK
   * @see SWT#FULL_SELECTION
   * <!--@see SWT#HIDE_SELECTION-->
   * @see SWT#VIRTUAL
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   * 
   * @since 1.0 
   */
  public Table( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    focusIndex = -1;
    sortDirection = SWT.NONE;
    tableAdapter = new TableAdapter();
    itemHolder = new ItemHolder( TableItem.class );
    columnHolder = new ItemHolder( TableColumn.class );
    selection = EMPTY_ITEMS;
    if( ( this.style & SWT.VIRTUAL ) != 0 ) {
      resizeListener = new ResizeListener();
      addControlListener( resizeListener );
    } else {
      resizeListener = null;
    }
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = new CompositeItemHolder();
    } else if( adapter == ITableAdapter.class ) {
      result = tableAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  ///////////////////////////
  // Column handling methods

  /**
   * Returns the number of columns contained in the receiver.
   * If no <code>TableColumn</code>s were created by the programmer,
   * this value is zero, despite the fact that visually, one column
   * of items may be visible. This occurs when the programmer uses
   * the table like a list, adding items but never creating a column.
   *
   * @return the number of columns
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public int getColumnCount() {
    checkWidget();
    return columnHolder.size();
  }

  /**
   * Returns an array of <code>TableColumn</code>s which are the
   * columns in the receiver.  Columns are returned in the order
   * that they were created.  If no <code>TableColumn</code>s were
   * created by the programmer, the array is empty, despite the fact
   * that visually, one column of items may be visible. This occurs
   * when the programmer uses the table like a list, adding items but
   * never creating a column.
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of items, so modifying the array will
   * not affect the receiver. 
   * </p>
   *
   * @return the items in the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see Table#getColumnOrder()
   * @see Table#setColumnOrder(int[])
   * @see TableColumn#getMoveable()
   * @see TableColumn#setMoveable(boolean)
   * @see SWT#Move
   * 
   * @since 1.0 
   */
  public TableColumn[] getColumns() {
    checkWidget();
    return ( TableColumn[] )columnHolder.getItems();
  }

  /**
   * Returns the column at the given, zero-relative index in the
   * receiver. Throws an exception if the index is out of range.
   * Columns are returned in the order that they were created.
   * If no <code>TableColumn</code>s were created by the programmer,
   * this method will throw <code>ERROR_INVALID_RANGE</code> despite
   * the fact that a single column of data may be visible in the table.
   * This occurs when the programmer uses the table like a list, adding
   * items but never creating a column.
   *
   * @param index the index of the column to return
   * @return the column at the given index
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see Table#getColumnOrder()
   * @see Table#setColumnOrder(int[])
   * @see TableColumn#getMoveable()
   * @see TableColumn#setMoveable(boolean)
   * @see SWT#Move
   * 
   * @since 1.0 
   */
  public TableColumn getColumn( final int index ) {
    checkWidget();
    return ( TableColumn )columnHolder.getItem( index );
  }

  /**
   * Searches the receiver's list starting at the first column
   * (index 0) until a column is found that is equal to the 
   * argument, and returns the index of that column. If no column
   * is found, returns -1.
   *
   * @param tableColumn the search column
   * @return the index of the column
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public int indexOf( final TableColumn tableColumn ) {
    checkWidget();
    if( tableColumn == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return columnHolder.indexOf( tableColumn );
  }
  
  /**
   * Sets the order that the items in the receiver should 
   * be displayed in to the given argument which is described
   * in terms of the zero-relative ordering of when the items
   * were added.
   *
   * @param order the new order to display the items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the item order is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the item order is not the same length as the number of items</li>
   * </ul>
   * 
   * @see Table#getColumnOrder()
   * @see TableColumn#getMoveable()
   * @see TableColumn#setMoveable(boolean)
   * @see SWT#Move
   * 
   * @since 1.0
   */
  public void setColumnOrder( final int[] order ) {
    checkWidget();
    if( order == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    int columnCount = getColumnCount();
    if( order.length != columnCount ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( columnCount > 0 ) {
      int[] oldOrder = new int[ columnCount ];
      System.arraycopy( columnOrder, 0, oldOrder, 0, columnOrder.length );
      boolean reorder = false;
      boolean[] seen = new boolean[ columnCount ];
      for( int i = 0; i < order.length; i++ ) {
        int index = order[ i ];
        if( index < 0 || index >= columnCount ) {
          error( SWT.ERROR_INVALID_RANGE );
        }
        if( seen[ index ] ) {
          error( SWT.ERROR_INVALID_ARGUMENT );
        }
        seen[ index ] = true;
        if( index != oldOrder[ i ] ) {
          reorder = true;
        }
      }
      if( reorder ) {
        System.arraycopy( order, 0, columnOrder, 0, columnOrder.length );
        for( int i = 0; i < seen.length; i++ ) {
          if( oldOrder[ i ] != columnOrder[ i ] ) {
            TableColumn column = getColumn( columnOrder[ i ] );
            int controlMoved = ControlEvent.CONTROL_MOVED;
            ControlEvent controlEvent = new ControlEvent( column, controlMoved );
            controlEvent.processEvent();
          }
        }
      }
    } 
  }

  /**
   * Returns an array of zero-relative integers that map
   * the creation order of the receiver's items to the
   * order in which they are currently being displayed.
   * <p>
   * Specifically, the indices of the returned array represent
   * the current visual order of the items, and the contents
   * of the array represent the creation order of the items.
   * </p><p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of items, so modifying the array will
   * not affect the receiver. 
   * </p>
   *
   * @return the current visual order of the receiver's items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see Table#setColumnOrder(int[])
   * @see TableColumn#getMoveable()
   * @see TableColumn#setMoveable(boolean)
   * @see SWT#Move
   * 
   * @since 1.0
   */
  public int[] getColumnOrder() {
    checkWidget();
    int[] result;
    if( columnHolder.size() == 0 ) {
      result = new int[ 0 ]; 
    } else {
      result = new int[ columnOrder.length ];
      System.arraycopy( columnOrder, 0, result, 0, columnOrder.length );
    }
    return result;
  }
  
  ////////////////////////
  // Item handling methods

  /**
   * Sets the number of items contained in the receiver.
   *
   * @param itemCount the number of items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.0
   */
  public void setItemCount( final int itemCount ) {
    checkWidget();
    int oldItemCount = itemHolder.size();
    int newItemCount = Math.max( 0, itemCount );
    if( newItemCount != oldItemCount ) {
      boolean isVirtual = ( style & SWT.VIRTUAL ) != 0;
      TableItem[] items = getItems();
      int index = newItemCount;
      while( index < oldItemCount ) {
        TableItem item = items[ index ];
        if( item != null && !item.isDisposed() ) {
          item.dispose();
        }
        index++;
      }
      // TODO [rh] do not eagerly create items when VIRTUAL
      for( int i = oldItemCount; i < newItemCount; i++ ) {
        new TableItem( this, SWT.NONE, i, !isVirtual );
      }
      for( int i = oldItemCount; i < newItemCount; i++ ) {
        TableItem item = ( TableItem )itemHolder.getItem( i );
        if( item.isVisible() ) {
          checkData( item, itemHolder.indexOf( item ) );
        }
      }
  //    if( itemCount == 0 ) {
  //      setScrollWidth( null, false );
  //    }
    } 
  }

  /**
   * Returns the number of items contained in the receiver.
   *
   * @return the number of items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  /**
   * Returns a (possibly empty) array of <code>TableItem</code>s which
   * are the items in the receiver. 
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of items, so modifying the array will
   * not affect the receiver. 
   * </p>
   *
   * @return the items in the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public TableItem[] getItems() {
    checkWidget();
    return ( TableItem[] )itemHolder.getItems();
  }

  /**
   * Returns the item at the given, zero-relative index in the
   * receiver. Throws an exception if the index is out of range.
   *
   * @param index the index of the item to return
   * @return the item at the given index
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public TableItem getItem( final int index ) {
    checkWidget();
    if( !( 0 <= index && index < itemHolder.size() ) ) {
      error( SWT.ERROR_INVALID_RANGE );
    }
    return ( TableItem )itemHolder.getItem( index );
  }
  
  
  /**
   * Returns the item at the given point in the receiver
   * or null if no such item exists. The point is in the
   * coordinate system of the receiver.
   * <p>
   * The item that is returned represents an item that could be selected by the user.
   * For example, if selection only occurs in items in the first column, then null is 
   * returned if the point is outside of the item. 
   * Note that the SWT.FULL_SELECTION style hint, which specifies the selection policy,
   * determines the extent of the selection.
   * </p>
   *
   * @param point the point used to locate the item
   * @return the item at the given point, or null if the point is not in a selectable item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public TableItem getItem( final Point point ) {
    checkWidget();
    TableItem result = null;
    Rectangle clientArea = getClientArea();
    if(    point.x >= 0 
        && point.y >= 0 
        && point.x <= clientArea.width 
        && point.y <= clientArea.height ) 
    {
      int itemHeight = getItemHeight();
      int index = ( point.y / itemHeight ) - 1;
      if( point.y == 0 || point.y % itemHeight != 0 ) {
        index++;
      }
      index += topIndex;
      if( index < getItemCount() ) {
        result = getItem( index );
      }
    }
    return result;
  }

  /**
   * Searches the receiver's list starting at the first item
   * (index 0) until an item is found that is equal to the 
   * argument, and returns the index of that item. If no item
   * is found, returns -1.
   *
   * @param item the search item
   * @return the index of the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public int indexOf( final TableItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return itemHolder.indexOf( item );
  }
  
  /**
   * Removes all of the items from the receiver.
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public void removeAll() {
    checkWidget();
    TableItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      if( !items[ i ].isDisposed() ) {
        items[ i ].dispose();
      }
    }
  }
  
  /**
   * Removes the items from the receiver which are between the given
   * zero-relative start and end indices (inclusive).
   * 
   * @param start the start of the range
   * @param end the end of the range
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_INVALID_RANGE - if either the start or end are not
   *              between 0 and the number of elements in the list minus 1
   *              (inclusive)</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * 
   * @since 1.0 
   */
  public void remove( final int start, final int end ) {
    checkWidget();
    if( start <= end ) {
      if( !( 0 <= start && start <= end && end < getItemCount() ) ) {
        error( SWT.ERROR_INVALID_RANGE );
      }
      TableItem[] items = getItems();
      for( int i = start; i <= end; i++ ) {
        if( !items[ i ].isDisposed() ) {
          items[ i ].dispose();
        }
      }
    } 
  }

  /**
   * Removes the item from the receiver at the given zero-relative index.
   * 
   * @param index the index for the item
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_INVALID_RANGE - if the index is not between 0 and
   *              the number of elements in the list minus 1 (inclusive)</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * 
   * @since 1.0 
   */
  public void remove( final int index ) {
    checkWidget();
    if( !( 0 <= index && index < getItemCount() ) ) {
      error( SWT.ERROR_ITEM_NOT_REMOVED );
    }
    TableItem item = getItem( index );
    if( !item.isDisposed() ) {
      item.dispose();
    }
  }

  /**
   * Removes the items from the receiver's list at the given zero-relative
   * indices.
   * 
   * @param indices the array of indices of the items
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_INVALID_RANGE - if the index is not between 0 and
   *              the number of elements in the list minus 1 (inclusive)</li>
   *              <li>ERROR_NULL_ARGUMENT - if the indices array is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * 
   * @since 1.0 
   */
  public void remove( final int[] indices ) {
    checkWidget();
    if( indices == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( indices.length == 0 ) {
      return;
    }
    TableItem item;
    for( int i = 0; i < indices.length; i++ ) {
      item = getItem( indices[ i ] );
      if( item != null && !item.isDisposed() ) {
        item.dispose();
      }
    }
  }
  
  /**
   * Clears the item at the given zero-relative index in the receiver.
   * The text, icon and other attributes of the item are set to the default
   * value.  If the table was created with the <code>SWT.VIRTUAL</code> style,
   * these attributes are requested again as needed.
   *
   * @param index the index of the item to clear
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see SWT#VIRTUAL
   * @see SWT#SetData
   * 
   * @since 1.0
   */
  public void clear( final int index ) {
    checkWidget();
    TableItem item = getItem( index );
    item.clear();
  }
  
  /**
   * Removes the items from the receiver which are between the given
   * zero-relative start and end indices (inclusive).  The text, icon
   * and other attributes of the items are set to their default values.
   * If the table was created with the <code>SWT.VIRTUAL</code> style,
   * these attributes are requested again as needed.
   *
   * @param start the start index of the item to clear
   * @param end the end index of the item to clear
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if either the start or end are not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see SWT#VIRTUAL
   * @see SWT#SetData
   * 
   * @since 1.0
   */
  public void clear( final int start, final int end ) {
    checkWidget();
    int itemCount = getItemCount();
    if( start > end ) {
      return;
    }
    if( !( 0 <= start && start <= end && end < itemCount ) ) {
      error( SWT.ERROR_INVALID_RANGE );
    }
    if( start == 0 && end == itemCount - 1 ) {
      clearAll();
    } else {
      for( int i = start; i <= end; i++ ) {
        TableItem item = getItem( i );
        if( item != null ) {
          item.clear();
        }
      }
    }
  }
  
  /**
   * Clears all the items in the receiver. The text, icon and other
   * attributes of the items are set to their default values. If the
   * table was created with the <code>SWT.VIRTUAL</code> style, these
   * attributes are requested again as needed.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see SWT#VIRTUAL
   * @see SWT#SetData
   * 
   * @since 1.0
   */
  public void clearAll() {
    checkWidget();
    int itemCount = getItemCount();
    for( int i = 0; i < itemCount; i++ ) {
      TableItem item = getItem( i );
      if( item != null ) {
        item.clear();
      }
    }
  }
  
  /**
   * Clears the items at the given zero-relative indices in the receiver.
   * The text, icon and other attributes of the items are set to their default
   * values.  If the table was created with the <code>SWT.VIRTUAL</code> style,
   * these attributes are requested again as needed.
   *
   * @param indices the array of indices of the items
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   *    <li>ERROR_NULL_ARGUMENT - if the indices array is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see SWT#VIRTUAL
   * @see SWT#SetData
   * 
   * @since 1.0
   */
  public void clear( final int[] indices ) {
    checkWidget();
    int itemCount = getItemCount();
    if( indices == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( indices.length == 0 ) {
      return;
    }
    for( int i = 0; i < indices.length; i++ ) {
      if( !( 0 <= indices[ i ] && indices[ i ] < itemCount ) ) {
        error( SWT.ERROR_INVALID_RANGE );
      }
    }
    for( int i = 0; i < indices.length; i++ ) {
      TableItem item = getItem( indices[ i ] );
      if( item != null ) {
        item.clear();
      }
    }
  }
  
  /////////////////////////////
  // Selection handling methods
  
  /**
   * Returns the zero-relative index of the item which is currently
   * selected in the receiver, or -1 if no item is selected.
   *
   * @return the index of the selected item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public int getSelectionIndex() {
    checkWidget();
    int result = -1;
    if( selection.length > 0 ) {
      result = indexOf( selection[ selection.length - 1 ] );
    }
    if( focusIndex != result ) {
      boolean found = false;
      for( int i = 0; !found && i < selection.length; i++ ) {
        if( focusIndex == indexOf( selection[ 0 ] ) ) {
          result = focusIndex;
          found = true;
        }
      }
    } 
    return result;
  }

  /**
   * Selects the item at the given zero-relative index in the receiver. 
   * The current selection is first cleared, then the new item is selected.
   *
   * @param index the index of the item to select
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Table#deselectAll()
   * @see Table#select(int)
   * 
   * @since 1.0 
   */
  public void setSelection( final int index ) {
    checkWidget();
    deselectAll();
    select( index );
    if( index < itemHolder.size() ) {
      setFocusIndex( index );
    }
  }

  /**
   * Returns the number of selected items contained in the receiver.
   *
   * @return the number of selected items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public int getSelectionCount() {
    checkWidget();
    return getSelection().length;
  }

  /**
   * Selects the items in the range specified by the given zero-relative
   * indices in the receiver. The range of indices is inclusive.
   * The current selection is cleared before the new items are selected.
   * <p>
   * Indices that are out of range are ignored and no items will be selected
   * if start is greater than end.
   * If the receiver is single-select and there is more than one item in the
   * given range, then all indices are ignored.
   * </p>
   * 
   * @param start the start index of the items to select
   * @param end the end index of the items to select
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Table#deselectAll()
   * @see Table#select(int,int)
   * 
   * @since 1.0 
   */
  public void setSelection( final int start, final int end ) {
    checkWidget();
    deselectAll();
    select( start, end );
    int count = itemHolder.size();
    if(    end >= 0
        && start <= end
        && ( ( style & SWT.SINGLE ) == 0 || start == end ) 
        && count != 0 
        && start < count ) 
    {
      setFocusIndex( Math.max( 0, start ) );
    }
  }

  /**
   * Returns an array of <code>TableItem</code>s that are currently
   * selected in the receiver. The order of the items is unspecified.
   * An empty array indicates that no items are selected.
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its selection, so modifying the array will
   * not affect the receiver. 
   * </p>
   * @return an array representing the selection
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public TableItem[] getSelection() {
    checkWidget();
    // TODO [rh] handle this (remove disposed item from selection) in destroyItem 
    // clean up internal structure: remove disposed items from selection
    List buffer = new ArrayList();
    for( int i = 0; i < selection.length; i++ ) {
      if( !selection[ i ].isDisposed() ) {
        buffer.add( selection[ i ] );
      }
    }
    selection = new TableItem[ buffer.size() ];
    buffer.toArray( selection );
    // return a copy of the now clean internal structure
    return ( TableItem[] )selection.clone();
  }

  /**
   * Selects the items at the given zero-relative indices in the receiver.
   * The current selection is cleared before the new items are selected.
   * <p>
   * Indices that are out of range and duplicate indices are ignored.
   * If the receiver is single-select and multiple indices are specified,
   * then all indices are ignored.
   * </p>
   *
   * @param indices the indices of the items to select
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Table#deselectAll()
   * @see Table#select(int[])
   * 
   * @since 1.0 
   */
  public void setSelection( final int[] indices ) {
    checkWidget();
    if( indices == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    deselectAll();
    select( indices );
    int length = indices.length;
    if( length != 0 && ( ( style & SWT.SINGLE ) == 0 || length <= 1 ) ) {
      setFocusIndex( indices[ 0 ] );
    } 
  }
  
  /**
   * Sets the receiver's selection to the given item.
   * The current selection is cleared before the new item is selected.
   * <p>
   * If the item is not in the receiver, then it is ignored.
   * </p>
   *
   * @param item the item to select
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setSelection( final TableItem item ) {
    checkWidget();
    if( item == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    setSelection( new TableItem[]{ item } );
  }

  /**
   * Sets the receiver's selection to be the given array of items.
   * The current selection is cleared before the new items are selected.
   * <p>
   * Items that are not in the receiver are ignored.
   * If the receiver is single-select and multiple items are specified,
   * then all items are ignored.
   * </p>
   *
   * @param items the array of items
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the array of items is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if one of the items has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Table#deselectAll()
   * @see Table#select(int[])
   * @see Table#setSelection(int[])
   * 
   * @since 1.0 
   */
  public void setSelection( final TableItem[] items ) {
    checkWidget();
    if( items == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int[] indices = new int[ items.length ];
    for( int i = 0; i < items.length; i++ ) {
      indices[ i ] = indexOf( items[ i ] );
    }
    setSelection( indices );
  }
  
  /**
   * Returns the zero-relative indices of the items which are currently
   * selected in the receiver. The order of the indices is unspecified.
   * The array is empty if no items are selected.
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its selection, so modifying the array will
   * not affect the receiver. 
   * </p>
   * @return the array of indices of the selected items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public int[] getSelectionIndices() {
    checkWidget();
    TableItem[] currentSelection = getSelection();
    int[] result = new int[ currentSelection.length ];
    for( int i = 0; i < currentSelection.length; i++ ) {
      result[ i ] = indexOf( currentSelection[ i ] );
    }
    return result;
  }
  
  /**
   * Returns <code>true</code> if the item is selected,
   * and <code>false</code> otherwise.  Indices out of
   * range are ignored.
   *
   * @param index the index of the item
   * @return the visibility state of the item at the index
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public boolean isSelected( final int index ) {
    checkWidget();
    boolean result = false;
    if( index >= 0 && index < itemHolder.size() ) {
      Item item = itemHolder.getItem( index );
      TableItem[] currentSelection = getSelection();
      for( int i = 0; !result && i < currentSelection.length; i++ ) {
        result = currentSelection[ i ] == item;
      }
    }
    return result;
  }
  
  /**
   * Selects the item at the given zero-relative index in the receiver. 
   * If the item at the index was already selected, it remains
   * selected. Indices that are out of range are ignored.
   *
   * @param index the index of the item to select
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public void select( final int index ) {
    checkWidget();
    if( index >= 0 && index < itemHolder.size() ) {
      TableItem item = ( TableItem )itemHolder.getItem( index );
      if( ( style & SWT.SINGLE ) != 0 ) {
        selection = new TableItem[] { item };
      } else {
        int length = selection.length;
        if( !isSelected( index ) ) {
          TableItem[] newSelection = new TableItem[ length + 1 ];
          System.arraycopy( selection, 0, newSelection, 0, length );
          newSelection[ length ] = item;
          selection = newSelection;
        }
      }
    }
  }
  
  /**
   * Selects the items in the range specified by the given zero-relative
   * indices in the receiver. The range of indices is inclusive.
   * The current selection is not cleared before the new items are selected.
   * <p>
   * If an item in the given range is not selected, it is selected.
   * If an item in the given range was already selected, it remains selected.
   * Indices that are out of range are ignored and no items will be selected
   * if start is greater than end.
   * If the receiver is single-select and there is more than one item in the
   * given range, then all indices are ignored.
   * </p>
   *
   * @param start the start of the range
   * @param end the end of the range
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see Table#setSelection(int,int)
   * 
   * @since 1.0 
   */
  public void select( final int start, final int end ) {
    checkWidget();
    if(    end >= 0
        && start <= end
        && ( ( style & SWT.SINGLE ) == 0 || start == end ) ) 
    {
      int count = itemHolder.size();
      if( count != 0 && start < count ) {
        int adjustedStart = Math.max( 0, start );
        int adjustedEnd = Math.min( end, count - 1 );
        if( adjustedStart == 0 && adjustedEnd == count - 1 ) {
          selectAll();
        } else {
          for( int i = adjustedStart; i <= adjustedEnd; i++ ) {
            select( i );
          }
        }
      }
    }
  }
  
  /**
   * Selects the items at the given zero-relative indices in the receiver.
   * The current selection is not cleared before the new items are selected.
   * <p>
   * If the item at a given index is not selected, it is selected.
   * If the item at a given index was already selected, it remains selected.
   * Indices that are out of range and duplicate indices are ignored.
   * If the receiver is single-select and multiple indices are specified,
   * then all indices are ignored.
   * </p>
   *
   * @param indices the array of indices for the items to select
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see Table#setSelection(int[])
   * 
   * @since 1.0 
   */
  public void select( final int[] indices ) {
    checkWidget();
    if( indices == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    int length = indices.length;
    if( length != 0 && ( ( style & SWT.SINGLE ) == 0 || length <= 1 ) ) {
      for( int i = length - 1; i >= 0; --i ) {
        select( indices[ i ] );
      }
    } 
  }

  /**
   * Selects all of the items in the receiver.
   * <p>
   * If the receiver is single-select, do nothing.
   * </p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public void selectAll() {
    checkWidget();
    if( ( style & SWT.SINGLE ) == 0 ) {
      setSelection( getItems() );
    }
  }
  
  /**
   * Deselects the item at the given zero-relative index in the receiver.
   * If the item at the index was already deselected, it remains
   * deselected. Indices that are out of range are ignored.
   *
   * @param index the index of the item to deselect
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void deselect( final int index ) {
    checkWidget();
    removeFromSelection( index );
  }

  /**
   * Deselects the items at the given zero-relative indices in the receiver.
   * If the item at the given zero-relative index in the receiver 
   * is selected, it is deselected.  If the item at the index
   * was not selected, it remains deselected.  The range of the
   * indices is inclusive. Indices that are out of range are ignored.
   *
   * @param start the start index of the items to deselect
   * @param end the end index of the items to deselect
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void deselect( final int start, final int end ) {
    checkWidget ();
    if( start == 0 && end == itemHolder.size() - 1 ) {
      deselectAll();
    } else {
      int actualStart = Math.max( 0, start );
      for( int i = actualStart; i <= end; i++ ) {
        removeFromSelection( i );
      }
    }
  }

  /**
   * Deselects the items at the given zero-relative indices in the receiver.
   * If the item at the given zero-relative index in the receiver 
   * is selected, it is deselected.  If the item at the index
   * was not selected, it remains deselected. Indices that are out
   * of range and duplicate indices are ignored.
   *
   * @param indices the array of indices for the items to deselect
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the set of indices is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void deselect( final int[] indices ) {
    checkWidget();
    if( indices == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    for( int i = 0; i < indices.length; i++ ) {
      removeFromSelection( i );
    }
  }

  /**
   * Deselects all selected items in the receiver.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public void deselectAll() {
    checkWidget();
    selection = EMPTY_ITEMS;
  }

  //////////////////////////////////
  // TopIndex and showItem/Selection
  
  /**
   * Sets the zero-relative index of the item which is currently
   * at the top of the receiver. This index can change when items
   * are scrolled or new items are added and removed.
   *
   * @param topIndex the index of the top item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public void setTopIndex( final int topIndex ) {
    checkWidget();
    if( topIndex >= 0 && topIndex < getItemCount() ) {
      this.topIndex = topIndex;
    }
  }
  
  /**
   * Returns the zero-relative index of the item which is currently
   * at the top of the receiver. This index can change when items are
   * scrolled or new items are added or removed.
   *
   * @return the index of the top item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public int getTopIndex() {
    checkWidget();
    return topIndex;
  }
  
  /**
   * Shows the item.  If the item is already showing in the receiver,
   * this method simply returns.  Otherwise, the items are scrolled until
   * the item is visible.
   *
   * @param item the item to be shown
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Table#showSelection()
   * 
   * @since 1.0 
   */
  public void showItem( final TableItem item ) {
    checkWidget();
    if( item == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int itemIndex = indexOf( item );
    int visibleItemCount = getVisibleItemCount();
    if( itemIndex < topIndex || itemIndex > topIndex + visibleItemCount ) {
      // Show item as top item
      setTopIndex( itemIndex );
      // try to show it 2 rows above the bottom/last item
      int idealTopIndex = itemIndex - visibleItemCount + 2;
      if( idealTopIndex >= getItemCount() ) {
        idealTopIndex = getItemCount() - 1;
      }
      if( idealTopIndex >= 0 ) {
        setTopIndex( idealTopIndex );
      }
    } 
  }
  
  /**
   * Shows the selection.  If the selection is already showing in the receiver,
   * this method simply returns.  Otherwise, the items are scrolled until
   * the selection is visible.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Table#showItem(TableItem)
   * 
   * @since 1.0 
   */
  public void showSelection() {
    checkWidget();
    int index = getSelectionIndex();
    if( index != -1 ) {
      showItem( ( TableItem )itemHolder.getItem( index ) );
    }
  }

  ////////////////////
  // Visual appearance
  
  /**
   * Marks the receiver's header as visible if the argument is <code>true</code>,
   * and marks it invisible otherwise. 
   * <p>
   * If one of the receiver's ancestors is not visible or some
   * other condition makes the receiver not visible, marking
   * it visible may not actually cause it to be displayed.
   * </p>
   *
   * @param headerVisible the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public void setHeaderVisible( final boolean headerVisible ) {
    checkWidget();
    this.headerVisible = headerVisible;
  }
  
  /**
   * Returns <code>true</code> if the receiver's header is visible,
   * and <code>false</code> otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some
   * other condition makes the receiver not visible, this method
   * may still indicate that it is considered visible even though
   * it may not actually be showing.
   * </p>
   *
   * @return the receiver's header's visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public boolean getHeaderVisible() {
    checkWidget();
    return headerVisible;
  }
  
  /**
   * Returns <code>true</code> if the receiver's lines are visible,
   * and <code>false</code> otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some
   * other condition makes the receiver not visible, this method
   * may still indicate that it is considered visible even though
   * it may not actually be showing.
   * </p>
   *
   * @return the visibility state of the lines
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public boolean getLinesVisible() {
    checkWidget();
    return linesVisible;
  }
  
  /**
   * Marks the receiver's lines as visible if the argument is <code>true</code>,
   * and marks it invisible otherwise. 
   * <p>
   * If one of the receiver's ancestors is not visible or some
   * other condition makes the receiver not visible, marking
   * it visible may not actually cause it to be displayed.
   * </p>
   *
   * @param linesVisible the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public void setLinesVisible( final boolean linesVisible ) {
    checkWidget();
    this.linesVisible = linesVisible;
  }
  
  /**
   * Sets the column used by the sort indicator for the receiver. A null
   * value will clear the sort indicator.  The current sort column is cleared 
   * before the new column is set.
   *
   * @param column the column used by the sort indicator or <code>null</code>
   * 
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the column is disposed</li> 
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setSortColumn( final TableColumn column ) {
    checkWidget();
    if( column != null && column.isDisposed() ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
    }
    sortColumn = column;
  }

  /**
   * Returns the column which shows the sort indicator for
   * the receiver. The value may be null if no column shows
   * the sort indicator.
   *
   * @return the sort indicator 
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see #setSortColumn(TableColumn)
   * 
   * @since 1.0
   */
  public TableColumn getSortColumn() {
    checkWidget();
    return sortColumn;
  }

  /**
   * Sets the direction of the sort indicator for the receiver. The value 
   * can be one of <code>UP</code>, <code>DOWN</code> or <code>NONE</code>.
   *
   * @param direction the direction of the sort indicator 
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setSortDirection( final int direction ) {
    checkWidget();
    if( ( direction & ( SWT.UP | SWT.DOWN ) ) != 0 || direction == SWT.NONE ) {
      sortDirection = direction;
    } 
  }

  /**
   * Returns the direction of the sort indicator for the receiver. 
   * The value will be one of <code>UP</code>, <code>DOWN</code> 
   * or <code>NONE</code>.
   *
   * @return the sort direction
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see #setSortDirection(int)
   * 
   * @since 1.0
   */
  public int getSortDirection() {
    checkWidget();
    return sortDirection;
  }

  ///////////////////////////////////
  // Dimensions and size calculations
  
  /**
   * Returns the height of the area which would be used to
   * display <em>one</em> of the items in the receiver's.
   *
   * @return the height of one item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public int getItemHeight() {
    checkWidget();
    int result = TextSizeDetermination.getCharHeight( getFont() ) + 4;
    int itemImageHeight = getItemImageSize().y;
    result = Math.max( itemImageHeight, result );
    if( ( style & SWT.CHECK ) != 0 ) {
      result = Math.max( CHECK_HEIGHT, result );
    }
    return result;
  }
  
  /**
   * Returns the height of the receiver's header 
   *
   * @return the height of the header or zero if the header is not visible
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  // TODO [rh] preliminary implementation
  public int getHeaderHeight() {
    checkWidget();
    int result = 0;
    if( headerVisible ) {
      int textHeight = TextSizeDetermination.getCharHeight( getFont() );
      int imageHeight = 0;
      for( int i = 0; i < getColumnCount(); i++ ) {
        Image image = getColumn( i ).getImage();
        int height = image == null ? 0 : image.getBounds().height;
        if( height > imageHeight ) {
          imageHeight = height;
        }
      }
      result = Math.max( textHeight, imageHeight ) + 4;
    }
    return result;
  }
  
  /**
   * Returns the width in pixels of a grid line.
   *
   * @return the width of a grid line in pixels
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0 
   */
  public int getGridLineWidth () {
    checkWidget();
    return GRID_WIDTH;
  }
  
  //////////////////
  // Selection event
  
  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the receiver's selection changes, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * When <code>widgetSelected</code> is called, the item field of the event object is valid.
   * If the receiver has <code>SWT.CHECK</code> style set and the check selection changes,
   * the event object detail field contains the value <code>SWT.CHECK</code>.
   * <code>widgetDefaultSelected</code> is typically called when an item is double-clicked.
   * The item field of the event object is valid for default selection, but the detail field is not used.
   * </p>
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SelectionListener
   * @see #removeSelectionListener
   * @see SelectionEvent
   * 
   * @since 1.0 
   */
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }
  
  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the receiver's selection changes.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SelectionListener
   * @see #addSelectionListener(SelectionListener)
   * 
   * @since 1.0 
   */
  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }

  /////////////////////////////
  // Create and destroy columns
  
  final void createColumn( final TableColumn column, final int index ) {
    columnHolder.insert( column, index );
    if( columnOrder == null ) {
      columnOrder = new int[] { index };
    } else {
      int length = columnOrder.length;
      for( int i = index; i < length; i++ ) {
        columnOrder[ i ]++;
      }
      int[] newColumnOrder = new int[ length + 1 ];
      System.arraycopy( columnOrder, 0, newColumnOrder, 0, index );
      System.arraycopy( columnOrder, 
                        index, 
                        newColumnOrder, 
                        index + 1, 
                        length - index );
      columnOrder = newColumnOrder;
      columnOrder[ index ] = index;
    }
  }
  
  final void destroyColumn( final TableColumn column ) {
    int index = indexOf( column );
    // Remove data from TableItems
    TableItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].removeData( index );
    }
    // Reset sort column if necessary
    if( column == sortColumn ) {
      sortColumn = null;
    }
    // Remove from column holder
    columnHolder.remove( column );
    // Remove from column order
    int length = columnOrder.length;
    int[] newColumnOrder = new int[ length - 1 ];
    int count = 0;
    for( int i = 0; i < length; i++ ) {
      if( columnOrder[ i ] != index ) {
        int newOrder = columnOrder[ i ];
        if( index < newOrder ) {
          newOrder--;
        }
        newColumnOrder[ count ] = newOrder;
        count++;
      }
    }
    columnOrder = newColumnOrder;
  }
  
  ////////////////////////////
  // Create and destroy items
  
  final void createItem( final TableItem item, final int index ) {
    itemHolder.insert( item, index );
    // advance focusIndex when an item is inserted before the focused item
    if( index <= focusIndex ) {
      focusIndex++;
    }
  }
  
  final void destroyItem( final TableItem item ) {
    int index = indexOf( item );
    removeFromSelection( index );
    itemHolder.remove( item );
    if( topIndex > getItemCount() - 1 ) {
      topIndex = Math.max( 0, getItemCount() - 1 );
    }
    if( index == focusIndex || focusIndex > getItemCount() - 1 ) {
      // Must reset focusIndex before calling getSelectionIndex 
      focusIndex = -1;
      focusIndex = getSelectionIndex();
    }
  }
  
  ////////////////
  // Destroy table
  
  void releaseChildren() {
    Item[] items = itemHolder.getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    Item[] columns = columnHolder.getItems();
    for( int i = 0; i < columns.length; i++ ) {
      columns[ i ].dispose();
    }
  }
  
  void releaseWidget() {
    super.releaseWidget();
    if( resizeListener != null ) {
      removeControlListener( resizeListener );
    }
  }
  
  //////////////////
  // helping methods

  final void checkData( final TableItem item, final int index ) {
    if( ( style & SWT.VIRTUAL ) != 0 && !item.cached ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          item.cached = true;
          SetDataEvent event = new SetDataEvent( Table.this, item, index );
          event.processEvent();
          // widget could be disposed at this point
          if( isDisposed() || item.isDisposed() ) {
            SWT.error( SWT.ERROR_WIDGET_DISPOSED );
          }
        }
      } );
    } 
  }
  
  private void setFocusIndex( final int focusIndex ) {
    if( focusIndex >= 0 ) {
      this.focusIndex = focusIndex;
    }
  }

  private void removeFromSelection( final int index ) {
    if( index >= 0 && index < itemHolder.size() ) {
      TableItem item = getItem( index );
      boolean found = false;
      for( int i = 0; !found && i < selection.length; i++ ) {
        if( item == selection[ i ] ) {
          int length = selection.length;
          TableItem[] newSel = new TableItem[ length - 1 ];
          System.arraycopy( selection, 0, newSel, 0, i );
          if( i < length - 1 ) {
            System.arraycopy( selection, i + 1, newSel, i, length - i - 1 );
          }
          selection = newSel;
          found = true;
        }
      }
    }
  }

  final int getVisibleItemCount() {
    //  TODO [rh] replace this once getClientArea is working    
    int clientHeight = getBounds().height 
                     - getHeaderHeight() 
                     - ScrollBar.SCROLL_BAR_HEIGHT;
    return clientHeight >= 0 ? clientHeight / getItemHeight() : 0;
  }

  private static int checkStyle( final int style ) {
    int result = style;
    result |= SWT.H_SCROLL | SWT.V_SCROLL;
    return checkBits( result, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0 );
  }
  
  final void updateItemImageSize( final Image image ) {
    if( image != null && itemImageSize == null ) {
      Rectangle imageBounds = image.getBounds();
      itemImageSize = new Point( imageBounds.width, imageBounds.height );
    }
  }
  
  final Point getItemImageSize() {
    return itemImageSize == null ? new Point( 0, 0 ) : itemImageSize;
  }

  final int getCheckWidth() {
    int result = 0;
    if( ( Table.this.style & SWT.CHECK ) != 0 ) {
      // TODO [rh] read from theme
      result = 21;
    }
    return result;
  }
}