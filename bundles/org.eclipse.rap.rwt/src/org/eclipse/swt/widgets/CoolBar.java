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

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IItemHolderAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;


/**
 * Instances of this class provide an area for dynamically
 * positioning the items they contain.
 * <p>
 * The item children that may be added to instances of this class
 * must be of type <code>CoolItem</code>.
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add <code>Control</code> children to it,
 * or set a layout on it.
 * </p><p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>FLAT, HORIZONTAL, VERTICAL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * </p><p>
 * Note: Only one of the styles HORIZONTAL and VERTICAL may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class CoolBar extends Composite {

  private final ItemHolder itemHolder = new ItemHolder( CoolItem.class );
  private boolean locked;
  
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
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public CoolBar( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    if( ( style & SWT.VERTICAL ) != 0 ) {
      this.style |= SWT.VERTICAL;
    } else {
      this.style |= SWT.HORIZONTAL;
    }
  }
  
  ///////////////////////////
  // Adaptable implementation
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  ////////////////
  // Getter/setter
  
  /**
   * Sets whether or not the receiver is 'locked'. When a coolbar
   * is locked, its items cannot be repositioned.
   *
   * @param locked lock the coolbar if true, otherwise unlock the coolbar
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setLocked( final boolean locked ) {
    checkWidget();
    this.locked = locked;
  }

  /**
   * Returns whether or not the receiver is 'locked'. When a coolbar
   * is locked, its items cannot be repositioned.
   *
   * @return true if the coolbar is locked, false otherwise
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public boolean getLocked() {
    checkWidget();
    return locked;
  }
  
  ///////////////////////
  // Size-related methods
  
  public Point computeSize( final int wHint,
                            final int hHint, 
                            final boolean changed )
  {
    checkWidget();
    // TODO [rh] replace with decent implementation
    Point result = super.computeSize( wHint, hHint, changed );
    Item[] items = itemHolder.getItems();
    int height = 30;
    for( int i = 0; i < items.length; i++ ) {
      int itemHeight = ( ( CoolItem )items[ i ] ).getSize().y;
      height = Math.max( height, itemHeight );
    }
    return new Point( result.x, height );
  }
  
  //////////////////////////
  // Management of CoolItems
  
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
   * Returns an array of <code>CoolItem</code>s in the order
   * in which they are currently being displayed.
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of items, so modifying the array will
   * not affect the receiver. 
   * </p>
   *
   * @return the receiver's items in their current visual order
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public CoolItem[] getItems() {
    checkWidget();
    return (org.eclipse.swt.widgets.CoolItem[] )itemHolder.getItems();
  }
  
  /**
   * Returns the item that is currently displayed at the given,
   * zero-relative index. Throws an exception if the index is
   * out of range.
   *
   * @param index the visual index of the item to return
   * @return the item at the given visual index
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public CoolItem getItem( final int index ) {
    checkWidget();
    return ( CoolItem )itemHolder.getItem( index );
  }
  
  /**
   * Searches the receiver's items in the order they are currently
   * being displayed, starting at the first item (index 0), until
   * an item is found that is equal to the argument, and returns
   * the index of that item. If no item is found, returns -1.
   *
   * @param item the search item
   * @return the visual order index of the search item, or -1 if the item is not found
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the item is disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int indexOf( final CoolItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( item );
  }
  
  /**
   * Returns an array of zero-relative ints that map
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
   */
  public int[] getItemOrder() {
    checkWidget();
    int[] result = new int[ getItemCount() ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = getItem( i ).getOrder();
    }
    return result;
  }
  
  public void setItemOrder( final int[] itemOrder ) {
    checkWidget();
    if( itemOrder == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int itemCount = getItemCount();
    if( itemOrder.length != itemCount ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    // Ensure that itemOrder does not contain any duplicates.
    boolean[] set = new boolean[ itemCount ];
    for( int i = 0; i < itemOrder.length; i++ ) {
      int index = itemOrder[ i ];
      if( index < 0 || index >= itemCount ) {
        SWT.error( SWT.ERROR_INVALID_RANGE );
      }
      if( set[ index ] ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      set[ index ] = true;
    }
    for( int i = 0; i < itemOrder.length; i++ ) {
      CoolItem item = getItem( i );
      item.setOrder( itemOrder[ i ] );
    }
  }
  
  ///////////////////////////////////////////////////
  // Internal methods to maintain the child controls
  
  protected void releaseChildren() {
    CoolItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    super.releaseChildren();
  }

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int result = style | SWT.NO_FOCUS;
    /*
     * Even though it is legal to create this widget with scroll bars, they
     * serve no useful purpose because they do not automatically scroll the
     * widget's client area. The fix is to clear the SWT style.
     */
    return result & ~( SWT.H_SCROLL | SWT.V_SCROLL );
  }

  int getMargin( final int index ) {
    return CoolItem.HANDLE_SIZE;
  }
}
