/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import java.util.*;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.internal.graphics.FontSizeEstimation;
import org.eclipse.swt.internal.widgets.IItemHolderAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;


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
 * <li>Though Table inherits the font property from Control, it is currently
 * not evaluated client-side.</li>
 * <li>Though it is possible to create a table with MULTI style, only SINGLE
 *  selections are implemented. This also applies when using setSelection with 
 *  more than one TableItem</li>
 *  <li>VIRTUAL not yet implemented</li>
 *  <li>showSelection and showItem currently do a very rough proximation since
 *  getClientArea is not yet implemented properly</li>
 *  <li>Tables created with style BORDER are not yet drawn correctly</li>
 *  <li>Scroll bars stay enabled even though the table itself is disabled. This
 *  is due to a qooxdoo limitation, see 
 *  http://bugzilla.qooxdoo.org/show_bug.cgi?id=352
 *  </li>
 *  <li>No images yet</li>
 *  <li>No keyboard navigation</li>
 *  <li>ColumnOrder and re-ordering columns on the client-side not yet
 *  implemented</li>
 * </ul> 
 */
public class Table extends Composite {
  
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
      Item[] items = itemHolder.getItems();
      Item[] columns = columnHolder.getItems();
      Item[] result = new Item[ items.length + columns.length ];
      System.arraycopy( columns, 0, result, 0, columns.length );
      System.arraycopy( items, 0, result, columns.length, items.length );
      return result;
    }
  }
  
  private static final int GRID_WIDTH = 1;
  private static final int DEFAULT_ITEM_HEIGHT = 15;
  private static final TableItem[] EMPTY_SELECTION = new TableItem[ 0 ];
  
  private final ItemHolder itemHolder;
  private final ItemHolder columnHolder;
  private TableItem[] selection;
  private boolean linesVisible;
  private boolean headerVisible;
  private int topIndex;
  
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
   * @see SWT#HIDE_SELECTION
   * @see SWT#VIRTUAL
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Table( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    itemHolder = new ItemHolder( TableItem.class );
    columnHolder = new ItemHolder( TableColumn.class );
    selection = EMPTY_SELECTION;
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = new CompositeItemHolder();
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
   * @param column the search column
   * @return the index of the column
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int indexOf( final TableColumn tableColumn ) {
    checkWidget();
    if( tableColumn == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return columnHolder.indexOf( tableColumn );
  }

  ////////////////////////
  // Item handling methods

  /**
   * Returns the number of items contained in the receiver.
   *
   * @return the number of items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
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
   */
  public TableItem getItem( final int index ) {
    checkWidget();
    return ( TableItem )itemHolder.getItem( index );
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
   */
  public int indexOf( final TableItem tableItem ) {
    checkWidget();
    if( tableItem == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return itemHolder.indexOf( tableItem );
  }
  
  /**
   * Removes all of the items from the receiver.
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void removeAll() {
    checkWidget();
    TableItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      if( !items[ i ].isDisposed() ) {
        items[ i ].dispose();
      }
    }
    topIndex = 0;
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
   */
  public void remove( final int start, final int end ) {
    checkWidget();
    if( start > end ) {
      return;
    }
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
   */
  public int getSelectionIndex() {
    // TODO: [fappel] currently we do not have a focus indicator, so
    //                we return simply return the first index in range
    int selectionIndex = -1;
    TableItem[] currentSelection = getSelection();
    if( currentSelection.length > 0 ) {
      for( int i = 0; selectionIndex == -1 && i < itemHolder.size(); i++ ) {
        if( itemHolder.getItem( i ) == currentSelection[ 0 ] ) {
          selectionIndex = i;
        }
      }
    }
    return selectionIndex;
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
   */
  public void setSelection( final int index ) {
    if( index >= 0 && index < itemHolder.size() ) {
      TableItem item = ( TableItem )itemHolder.getItem( index );
      selection = new TableItem[] { item };
    } else {
      selection = EMPTY_SELECTION;
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
   */
  public int getSelectionCount() {
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
   */
  public void setSelection( final int start, final int end ) {
    // TODO: [fappel] style bits for single/multi selection
    if( end >= 0 && start <= end ) {
      int actualStart = Math.max( 0, start );
      int actualEnd = Math.min( end, itemHolder.size() - 1 );
      selection = new TableItem[ actualEnd - actualStart + 1 ];
      int count = 0;
      for( int i = actualStart; i < actualEnd + 1; i++ ) {
        selection[ count ] = ( TableItem )itemHolder.getItem( i );
        count++;
      }
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
   */
  public TableItem[] getSelection() {
    List buffer = new ArrayList();
    for( int i = 0; i < selection.length; i++ ) {
      if( !selection[ i ].isDisposed() ) {
        buffer.add( selection[ i ] );
      }
    }
    selection = new TableItem[ buffer.size() ];
    buffer.toArray( selection );
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
   */
  public void setSelection( final int[] indices ) {
    if( indices == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    // TODO: [fappel] style bits for single/multi selection
    
    Integer[] filteredIndices = filterIndices( indices );    
    TableItem[] newSelection = new TableItem[ filteredIndices.length ];
    for( int i = 0; i < filteredIndices.length; i++ ) {
      int index = filteredIndices[ i ].intValue();
      newSelection[ i ] = ( TableItem )itemHolder.getItem( index );
    }
    selection = newSelection;
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
   */
  public void setSelection( final TableItem[] items ) {
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
   */
  public int[] getSelectionIndices() {
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
   */
  public boolean isSelected( final int index ) {
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
   * Deselects all selected items in the receiver.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void deselectAll() {
    checkWidget();
    selection = EMPTY_SELECTION;
  }

  //////////////////////////////////
  // TopIndex and showItem/Selection
  
  /**
   * Sets the zero-relative index of the item which is currently
   * at the top of the receiver. This index can change when items
   * are scrolled or new items are added and removed.
   *
   * @param index the index of the top item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
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
   */
  public void showSelection() {
    checkWidget();
    int index = getSelectionIndex();
    if( index != -1 ) {
      TableItem item = ( TableItem )itemHolder.getItem( index ); 
      showItem( item );
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
   * @param show the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
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
   * @param show the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setLinesVisible( final boolean linesVisible ) {
    checkWidget();
    this.linesVisible = linesVisible;
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
   */
  public int getItemHeight () {
    checkWidget();
    int result = DEFAULT_ITEM_HEIGHT;
    if( itemHolder.size() > 0 ) {
      TableItem item = ( TableItem )itemHolder.getItem( 0 );
      result = item.getHeight();
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
  public int getHeaderHeight() {
    checkWidget();
    int result = 0;
    if( headerVisible ) {
      // TODO [rh] preliminary implementation
      result = FontSizeEstimation.getCharHeight( getFont() ) + 4;
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
   */
  public void addSelectionListener( final SelectionListener listener ) {
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
   */
  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  /////////////////////////////
  // Create and destroy columns
  
  final void createColumn( final TableColumn column, final int index ) {
    columnHolder.insert( column, index );
  }
  
  final void destroyColumn( final TableColumn column ) {
    TableItem[] items = getItems();
    int index = indexOf( column );
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].removeData( index );
    }
    columnHolder.remove( column );
  }
  
  ////////////////////////////
  // Create and destroy items
  
  final void createItem( final TableItem item, final int index ) {
    itemHolder.insert( item, index );
  }
  
  final void destroyItem( final TableItem item ) {
    itemHolder.remove( item );
    if( topIndex > getItemCount() - 1 ) {
      topIndex = getItemCount() - 1;
    }
  }
  
  //////////////////
  // helping methods
  
  private Integer[] filterIndices( final int[] indices ) {
    Set buffer = new HashSet();
    for( int i = 0; i < indices.length; i++ ) {
      if( indices[ i ] >= 0 && indices[ i ] < itemHolder.size() ) {
        buffer.add( new Integer( indices[ i ] ) );
      }
    }
    Integer[] result = new Integer[ buffer.size() ];
    buffer.toArray( result );
    Arrays.sort( result );
    return result;
  }
  
  private int getVisibleItemCount() {
    //  TODO [rh] replace this once getClientArea is working    
    int clientHeight = getBounds().height 
                     - getHeaderHeight() 
                     - ScrollBar.SCROLL_BAR_HEIGHT;
    return clientHeight / getItemHeight();
  }

  private static int checkStyle( final int style ) {
    int result = style;
    result |= SWT.H_SCROLL | SWT.V_SCROLL;
    return checkBits( result, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0 );
  }
}