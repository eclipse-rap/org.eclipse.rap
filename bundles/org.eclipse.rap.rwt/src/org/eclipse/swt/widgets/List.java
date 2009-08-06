/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.IListAdapter;
import org.eclipse.swt.internal.widgets.ListModel;


/**
 * Instances of this class represent a selectable user interface
 * object that displays a list of strings and issues notification
 * when a string is selected.  A list may be single or multi select.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection</dd>
 * </dl>
 * <p>
 * Note: Only one of SINGLE and MULTI may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * <p><strong>Note:</strong> Setting only one of <code>H_SCROLL</code> or
 * <code>V_SCROLL</code> leads - at least in IE 7 - to unexpected behavior
 * (items are drawn outside list bounds). Setting none or both scroll style
 * flags works as expected. We will work on a solution for this.</p>
 * @since 1.0
 */
public class List extends Scrollable {

  // This values must be kept in sync with appearance of list items
  private static final int VERTICAL_ITEM_MARGIN = 3;
  private static final int HORIZONTAL_ITEM_MARGIN = 5;

  private static final int SCROLL_SIZE = 16;

  private final ListModel model;
  private int focusIndex = -1;
  private IListAdapter listAdapter;
  private int topIndex = 0;

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
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public List( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    model = new ListModel( ( style & SWT.SINGLE ) != 0 );
  }

  /////////////////////
  // Adaptable override

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IListAdapter.class ) {
      if( listAdapter == null ) {
        listAdapter = new IListAdapter() {
          public void setFocusIndex( final int focusIndex ) {
            List.this.setFocusIndex( focusIndex );
          }
        };
      }
      result = listAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  ///////////////////////////////
  // Methods to get/set selection

  /**
   * Returns an array of <code>String</code>s that are currently
   * selected in the receiver.  The order of the items is unspecified.
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
  public String[] getSelection() {
    checkWidget();
    int[] selectionIndices = model.getSelectionIndices();
    String[] result = new String[ selectionIndices.length ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = model.getItem( selectionIndices[ i ] );
    }
    return result;
  }

  /**
   * Returns the zero-relative index of the item which is currently
   * selected in the receiver, or -1 if no item is selected.
   *
   * @return the index of the selected item or -1
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getSelectionIndex() {
    checkWidget();
    return model.getSelectionIndex();
  }

  /**
   * Returns the zero-relative indices of the items which are currently
   * selected in the receiver.  The order of the indices is unspecified.
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
    checkWidget();
    return model.getSelectionIndices();
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
    checkWidget();
    return model.getSelectionCount();
  }

  /**
   * Selects the item at the given zero-relative index in the receiver.
   * If the item at the index was already selected, it remains selected.
   * The current selection is first cleared, then the new item is selected.
   * Indices that are out of range are ignored.
   *
   * @param selection the index of the item to select
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * @see List#deselectAll()
   * @see List#select(int)
   */
  // TODO [rh] selection is not scrolled into view (see List.js)
  public void setSelection( final int selection ) {
    checkWidget();
    model.setSelection( selection );
    updateFocusIndexAfterSelectionChange();
  }

  /**
   * Selects the items at the given zero-relative indices in the receiver.
   * The current selection is cleared before the new items are selected.
   * <p>
   * Indices that are out of range and duplicate indices are ignored.
   * If the receiver is single-select and multiple indices are specified,
   * then all indices are ignored.
   *
   * @param selection the indices of the items to select
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see List#deselectAll()
   * @see List#select(int[])
   */
  public void setSelection( final int[] selection ) {
    checkWidget();
    model.setSelection( selection );
    updateFocusIndexAfterSelectionChange();
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
   *
   * @param start the start index of the items to select
   * @param end the end index of the items to select
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see List#deselectAll()
   * @see List#select(int,int)
   */
  public void setSelection( final int start, final int end ) {
    checkWidget();
    model.setSelection( start, end );
    updateFocusIndexAfterSelectionChange();
  }

  /**
   * Sets the receiver's selection to be the given array of items.
   * The current selection is cleared before the new items are selected.
   * <p>
   * Items that are not in the receiver are ignored.
   * If the receiver is single-select and multiple items are specified,
   * then all items are ignored.
   *
   * @param selection the array of items
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the array of items is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see List#deselectAll()
   * @see List#select(int[])
   * @see List#setSelection(int[])
   */
  public void setSelection( final String[] selection ) {
    checkWidget();
    model.setSelection( selection );
    updateFocusIndexAfterSelectionChange();
  }

  /**
   * Selects the item at the given zero-relative index in the receiver's
   * list.  If the item at the index was already selected, it remains
   * selected. Indices that are out of range are ignored.
   *
   * @param index the index of the item to select
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void select( final int index ) {
    checkWidget();
    if( ( style & SWT.SINGLE ) != 0 ) {
      if( index >= 0 && index < model.getItemCount() ) {
        model.setSelection( index );
      }
    } else {
      model.addSelection( index );
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
   * @see List#setSelection(int[])
   */
  public void select( final int[] indices ) {
    checkWidget();
    if( indices == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    int length = indices.length;
    if( length != 0 && ( ( style & SWT.SINGLE ) == 0 || length <= 1 ) ) {
      int i = 0;
      while( i < length ) {
        int index = indices[ i ];
        model.addSelection( index );
        i++;
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
   *
   * @param start the start of the range
   * @param end the end of the range
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see List#setSelection(int,int)
   */
  public void select( final int start, final int end ) {
    checkWidget();
    if(    end >= 0
        && start <= end
        && ( ( style & SWT.SINGLE ) == 0 || start == end ) )
    {
      int count = model.getItemCount();
      if( count != 0 && start < count ) {
        int startIndex = Math.max( 0, start );
        int endIndex = Math.min( end, count - 1 );
        if( ( style & SWT.SINGLE ) != 0 ) {
          model.setSelection( startIndex );
        } else {
          for( int i = startIndex; i <= endIndex; i++ ) {
            model.addSelection( i );
          }
        }
      }
    }
  }

  /**
   * Selects all of the items in the receiver.
   * <p>
   * If the receiver is single-select, do nothing.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void selectAll() {
    checkWidget();
    model.selectAll();
    updateFocusIndexAfterSelectionChange();
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
    model.deselectAll();
    updateFocusIndexAfterSelectionChange();
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
    checkWidget();
    boolean result;
    if( ( style & SWT.SINGLE ) != 0 ) {
      result = index == getSelectionIndex();
    } else {
      int[] selectionIndices = getSelectionIndices();
      result = false;
      for( int i = 0; !result && i < selectionIndices.length; i++ ) {
        if( index == selectionIndices[ i ] ) {
          result = true;
        }
      }
    }
    return result;
  }
  
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
   * 
   * @since 1.3
   */
  public void setTopIndex( final int topIndex ) {
    checkWidget();
    int count = model.getItemCount();
    if( this.topIndex != topIndex && topIndex >= 0 && topIndex < count ) {
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
   * @since 1.3
   */
  public int getTopIndex() {
    checkWidget();
    return topIndex;
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
   * @since 1.3
   */
  public void showSelection() {
    checkWidget();
    int index = getSelectionIndex();
    if( index != -1 ) {
      int itemCount = getVisibleItemCount();
      if( index < topIndex ) {
        // Show item as top item
        setTopIndex( index );
      } else if( itemCount > 0 && index >= topIndex + itemCount ) {
        // Show item as last item
        setTopIndex( index - itemCount + 1 );
      }
    }
  }

  /**
   * Returns the zero-relative index of the item which currently
   * has the focus in the receiver, or -1 if no item has focus.
   *
   * @return the index of the selected item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getFocusIndex() {
    checkWidget();
    return focusIndex;
  }

  ////////////////////////////////
  // Methods to maintain the items

  /**
   * Adds the argument to the end of the receiver's list.
   *
   * @param string the new item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #add(String,int)
   */
  public void add( final String string ) {
    checkWidget();
    model.add( string );
    updateFocusIndexAfterItemChange();
  }

  /**
   * Adds the argument to the receiver's list at the given
   * zero-relative index.
   * <p>
   * Note: To add an item at the end of the list, use the
   * result of calling <code>getItemCount()</code> as the
   * index or use <code>add(String)</code>.
   * </p>
   *
   * @param string the new item
   * @param index the index for the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #add(String)
   */
  public void add( final String string, final int index ) {
    checkWidget();
    model.add( string, index );
    updateFocusIndexAfterItemChange();
  }

  /**
   * Removes the item from the receiver at the given
   * zero-relative index.
   *
   * @param index the index for the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void remove( final int index ) {
    checkWidget();
    model.remove( index );
    updateFocusIndexAfterItemChange();
    adjustTopIndex();
  }

  /**
   * Removes the items from the receiver which are
   * between the given zero-relative start and end
   * indices (inclusive).
   *
   * @param start the start of the range
   * @param end the end of the range
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if either the start or end are not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void remove( final int start, final int end ) {
    checkWidget();
    model.remove( start, end );
    updateFocusIndexAfterItemChange();
    adjustTopIndex();
  }

  /**
   * Removes the items from the receiver at the given
   * zero-relative indices.
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
   */
  public void remove( final int[] indices ) {
    checkWidget();
    model.remove( indices );
    updateFocusIndexAfterItemChange();
    adjustTopIndex();
  }

  /**
   * Searches the receiver's list starting at the first item
   * until an item is found that is equal to the argument,
   * and removes that item from the list.
   *
   * @param string the item to remove
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the string is not found in the list</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void remove( final String string ) {
    checkWidget();
    model.remove( string );
    updateFocusIndexAfterItemChange();
    adjustTopIndex();
  }

  /**
   * Removes all of the items from the receiver.
   * <p>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void removeAll() {
    checkWidget();
    model.removeAll();
    updateFocusIndexAfterItemChange();
    adjustTopIndex();
  }

  /**
   * Sets the text of the item in the receiver's list at the given
   * zero-relative index to the string argument. This is equivalent
   * to <code>remove</code>'ing the old item at the index, and then
   * <code>add</code>'ing the new item at that index.
   *
   * @param index the index for the item
   * @param string the new text for the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setItem( final int index, final String string ) {
    checkWidget();
    model.setItem( index, string );
  }

  /**
   * Sets the receiver's items to be the given array of items.
   *
   * @param items the array of items
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the items array is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if an item in the items array is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setItems( final String[] items ) {
    checkWidget();
    model.setItems( items );
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
  public String getItem( final int index ) {
    checkWidget();
    return model.getItem( index );
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
   */
  public int getItemCount() {
    checkWidget();
    return model.getItemCount();
  }

  /**
   * Returns a (possibly empty) array of <code>String</code>s which
   * are the items in the receiver.
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of items, so modifying the array will
   * not affect the receiver.
   * </p>
   *
   * @return the items in the receiver's list
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String[] getItems() {
    checkWidget();
    return model.getItems();
  }

  /**
   * Gets the index of an item.
   * <p>
   * The list is searched starting at 0 until an
   * item is found that is equal to the search item.
   * If no item is found, -1 is returned.  Indexing
   * is zero based.
   *
   * @param string the search item
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
  public int indexOf( final String string ) {
    checkWidget();
    return indexOf( string, 0 );
  }

  /**
   * Searches the receiver's list starting at the given,
   * zero-relative index until an item is found that is equal
   * to the argument, and returns the index of that item. If
   * no item is found or the starting index is out of range,
   * returns -1.
   *
   * @param string the search item
   * @param start the zero-relative index at which to start the search
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
  public int indexOf( final String string, final int start ) {
    checkWidget();
    if( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int result = -1;
    int count = getItemCount();
    for( int i = start; result == -1 && i < count; i++ ) {
      if( string.equals( getItem( i ) ) ) {
        result = i;
      }
    }
    return result;
  }

  /**
   * Returns the height of the area which would be used to
   * display <em>one</em> of the items in the list.
   *
   * @return the height of one item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getItemHeight() {
    checkWidget();
    int margin = VERTICAL_ITEM_MARGIN * 2;
    return TextSizeDetermination.getCharHeight( getFont() ) + margin;
  }

  /////////////////////////////////////////
  // Listener registration/de-registration

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the receiver's selection changes, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * <code>widgetSelected</code> is called when the selection changes.
   * <code>widgetDefaultSelected</code> is typically called when an item is double-clicked.
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
   * @see #addSelectionListener
   */
  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }

  boolean isTabGroup() {
    return true;
  }

  /////////////////////////////////////////
  // Widget dimensions

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed ) {
    checkWidget ();
    int width = 0, height = 0;
    String[] items = getItems();
    int itemWidth = 0;
    for( int i = 0; i < items.length; i++ ) {
      itemWidth = getItemWidth( items[ i ] );
      width = Math.max( width, itemWidth );
    }
    height = getItemHeight() * items.length;
    if( width == 0 ) {
      width = DEFAULT_WIDTH;
    }
    if( height == 0 ) {
      height = DEFAULT_HEIGHT;
    }
    if( wHint != SWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
      height = hHint;
    }
    int border = getBorderWidth();
    width += border * 2;
    height += border * 2;
    if( ( style & SWT.V_SCROLL ) != 0 ) {
      width += SCROLL_SIZE;
    }
    if( ( style & SWT.H_SCROLL ) != 0 ) {
      height += SCROLL_SIZE;
    }
    return new Point( width, height );
  }

  /////////////////////////////////
  // Helping methods for focusIndex

  private void setFocusIndex( final int focusIndex ) {
    int count = model.getItemCount();
    if( focusIndex == -1 || ( focusIndex >= 0 && focusIndex < count ) ) {
      this.focusIndex = focusIndex;
    }
  }

  private void updateFocusIndexAfterSelectionChange() {
    focusIndex = -1;
    if( model.getItemCount() > 0 ) {
      if( model.getSelectionIndex() == -1 ) {
        focusIndex = 0;
      } else {
        focusIndex = model.getSelectionIndices()[ 0 ];
      }
    }
  }

  private void updateFocusIndexAfterItemChange() {
    if( model.getItemCount() == 0 ) {
      focusIndex = -1;
    } else if( model.getSelectionIndex() == -1 ){
      focusIndex = model.getItemCount() - 1;
    }
  }

  //////////////////
  // Helping methods

  private static int checkStyle( final int style ) {
    return checkBits( style, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0 );
  }

  private int getItemWidth( final String item ) {
    int margin = HORIZONTAL_ITEM_MARGIN * 2;
    return TextSizeDetermination.stringExtent( getFont(), item ).x + margin;
  }
  
  private void adjustTopIndex() {
    int count = model.getItemCount();
    if( count == 0 ) {
      topIndex = 0;
    } else if( topIndex >= count - 1 ) {
      topIndex = count - 1;
    }
  }
  
  final int getVisibleItemCount() {
    int clientHeight = getBounds().height;
    if( ( style & SWT.H_SCROLL ) != 0 ) {
      clientHeight -= SCROLL_SIZE;
    }
    int result = 0;
    if( clientHeight >= 0 ) {
      int itemHeight = getItemHeight();
      result = clientHeight / itemHeight;
    }
    return result;
  }
}
