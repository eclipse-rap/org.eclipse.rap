/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IItemHolderAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;

/**
 * Instances of this class support the layout of selectable
 * tool bar items.
 * <p>
 * The item children that may be added to instances of this class
 * must be of type <code>ToolItem</code>.
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add <code>Control</code> children to it,
 * or set a layout on it.
 * </p><p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>FLAT, WRAP, RIGHT, HORIZONTAL, VERTICAL, SHADOW_OUT</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles HORIZONTAL and VERTICAL may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * @since 1.0
 */
public class ToolBar extends Composite {

  private final ItemHolder itemHolder = new ItemHolder( ToolItem.class );

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
   * @see SWT#FLAT
   * @see SWT#WRAP
   * @see SWT#RIGHT
   * @see SWT#HORIZONTAL
   * @see SWT#SHADOW_OUT
   * @see SWT#VERTICAL
   * <!--@see Widget#checkSubclass()-->
   * @see Widget#getStyle()
   */
  public ToolBar( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    /*
    * Ensure that either of HORIZONTAL or VERTICAL is set.
    * NOTE: HORIZONTAL and VERTICAL have the same values
    * as H_SCROLL and V_SCROLL so it is necessary to first
    * clear these bits to avoid scroll bars and then reset
    * the bits using the original style supplied by the
    * programmer.
    */
    if( ( style & SWT.VERTICAL ) != 0 ) {
      this.style |= SWT.VERTICAL;
    } else {
      this.style |= SWT.HORIZONTAL;
    }
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  //////////////////
  // Item management

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
  public ToolItem getItem( final int index ) {
    checkWidget();
    return ( ToolItem )itemHolder.getItem( index );
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
    return itemHolder.size();
  }

  /**
   * Returns an array of <code>ToolItem</code>s which are the items
   * in the receiver.
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
  public ToolItem[] getItems() {
    checkWidget();
    return (org.eclipse.swt.widgets.ToolItem[] )itemHolder.getItems();
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
   *    <li>ERROR_NULL_ARGUMENT - if the tool item is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the tool item has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int indexOf( final ToolItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( item );
  }

  ////////////////////
  // Size computations

  // TODO [rh] decent size computation for VERTICAL alignment missing
  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int width = 0;
    int height = 0;
    for( int i = 0; i < itemHolder.size(); i++ ) {
      ToolItem item = ( ToolItem )itemHolder.getItem( i );
      Rectangle itemBounds = item.getBounds();
      height = Math.max( height, itemBounds.height );
      width += itemBounds.width;
    }
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
    Rectangle trim = computeTrim( 0, 0, width, height );
    width = trim.width;
    height = trim.height;
    return new Point( width, height );
  }

  public int getBorderWidth() {
    return ( style & SWT.BORDER ) != 0 ? 1 : 0;
  }

  /**
   * Returns the number of rows in the receiver. When
   * the receiver has the <code>WRAP</code> style, the
   * number of rows can be greater than one.  Otherwise,
   * the number of rows is always one.
   *
   * @return the number of items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getRowCount() {
    checkWidget();
    // return 1 as long as we don't support the WRAP style bit
    return 1;
  }

  public void setBounds( final Rectangle bounds ) {
    super.setBounds( bounds );
    // check if there is enough space for all items
    // otherwise - hide all items which could be cut off
    // TODO: check again because the item bounds are not very exact
    // see ToolItem#getWidth()
    for( int i = 0; i < itemHolder.size(); i++ ) {
      ToolItem item = ( ToolItem )itemHolder.getItem( i );
      Rectangle ibounds = item.getBounds();
      boolean visible = ibounds.x + ibounds.width <= bounds.width;
      item.setVisible( visible );
    }
  }

  ////////////////////////
  // Child control removal

  void removeControl( final Control control ) {
    super.removeControl( control );
    ToolItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      ToolItem item = items[ i ];
      if( item != null && item.getControl() == control ) {
        item.setControl( null );
      }
    }
  }

  ////////////////////
  // Widget overrides

  final void releaseChildren() {
    ToolItem[] toolItems = getItems();
    for( int i = 0; i < toolItems.length; i++ ) {
      toolItems[ i ].dispose();
    }
  }

  //////////////////
  // Helping methods

  private static int checkStyle( final int style ) {
    /*
    * Even though it is legal to create this widget
    * with scroll bars, they serve no useful purpose
    * because they do not automatically scroll the
    * widget's client area.  The fix is to clear
    * the SWT style.
    */
    return style & ~( SWT.H_SCROLL | SWT.V_SCROLL );
  }
}
