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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.FontSizeEstimation;
import org.eclipse.swt.internal.widgets.IItemHolderAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;

/**
 * Instances of this class implement the notebook user interface
 * metaphor.  It allows the user to select a notebook page from
 * set of pages.
 * <p>
 * The item children that may be added to instances of this class
 * must be of type <code>TabItem</code>.
 * <code>Control</code> children are created and then set into a
 * tab item using <code>TabItem#setControl</code>.
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to set a layout on it.
 * </p><p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>TOP, BOTTOM</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles TOP and BOTTOM may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class TabFolder extends Composite {

  private static final TabItem[] EMPTY_TAB_ITEMS = new TabItem[ 0 ];
  
  private final ItemHolder itemHolder = new ItemHolder( TabItem.class );
  private int selectionIndex = -1;

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
  public TabFolder( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
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
   * Returns an array of <code>TabItem</code>s which are the items
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
  public TabItem[] getItems() {
    checkWidget();
    return (org.eclipse.swt.widgets.TabItem[] )itemHolder.getItems();
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
  public TabItem getItem( final int index ) {
    checkWidget();
    return ( TabItem )itemHolder.getItem( index );
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
  public int indexOf( final TabItem item ) {
    checkWidget();
    if( item == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( item.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( item );
  }

  /////////////////////
  // Seletion handling
  
  /**
   * Returns an array of <code>TabItem</code>s that are currently
   * selected in the receiver. An empty array indicates that no
   * items are selected.
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
  public TabItem[] getSelection() {
    checkWidget();
    TabItem[] result = EMPTY_TAB_ITEMS;
    if( getSelectionIndex() != -1 ) {
      TabItem selected = ( TabItem )itemHolder.getItem( getSelectionIndex() );
      result = new TabItem[]{
        selected
      };
    }
    return result;
  }

  /**
   * Sets the receiver's selection to the given item.
   * The current selected is first cleared, then the new item is
   * selected.
   *
   * @param item the item to select
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final TabItem item ) {
    checkWidget();
    if( item == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    setSelection( new TabItem[]{ item } );
  }
  
  /**
   * Sets the receiver's selection to be the given array of items.
   * The current selected is first cleared, then the new items are
   * selected.
   *
   * @param items the array of items
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the items array is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final TabItem[] items ) {
    checkWidget();
    if( items == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int newIndex = -1;
    if( items.length > 0 ) {
      newIndex = itemHolder.indexOf( items[ 0 ] );
    }
    setSelection( newIndex );
  }

  /**
   * Selects the item at the given zero-relative index in the receiver. 
   * If the item at the index was already selected, it remains selected.
   * The current selection is first cleared, then the new items are
   * selected. Indices that are out of range are ignored.
   *
   * @param index the index of the item to select
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final int selectionIndex ) {
    checkWidget();
    if( selectionIndex >= -1 && selectionIndex < itemHolder.size() ) {
      this.selectionIndex = selectionIndex;
    }
  }

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
    checkWidget();
    if( selectionIndex >= itemHolder.size() ) {
      selectionIndex = itemHolder.size() - 1;
    }
    return selectionIndex;
  }
  
  ///////////////////////////////
  // Layout and size computations

  public void layout() {
    checkWidget();
    Control[] children = getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].setBounds( getClientArea() );
    }
  }

  public Rectangle getClientArea() {
    checkWidget();
    Rectangle current = getBounds();
    int width = current.width;
    int height = current.height;
    int border = 1;
    int hTabBar = 23;
    return new Rectangle( border,
                          hTabBar + border,
                          width - border * 2,
                          height - ( hTabBar + border * 2 ) );
  }

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    Point itemsSize = new Point( 0, 0 );
    Point contentsSize = new Point( 0, 0 );
    TabItem[] items = getItems();
    // TODO: one item should be enough since layout already includes all items
    for( int i = 0; i < items.length; i++ ) {
      Point thisItemSize = computeItemSize( items[ i ] );
      itemsSize.x += thisItemSize.x;
      itemsSize.y = Math.max( itemsSize.y, thisItemSize.y );
      Control control = items[ i ].getControl();
      if( control != null ) {
        Point thisSize = control.computeSize( SWT.DEFAULT, SWT.DEFAULT );
        contentsSize.x = Math.max( contentsSize.x, thisSize.x );
        contentsSize.y = Math.max( contentsSize.y, thisSize.y );
      }
    }
    int width = Math.max( itemsSize.x, contentsSize.x );
    int height = itemsSize.y + contentsSize.y;
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
    width += 2 * border;
    height += 2 * border;
    return new Point( width, height );
  }

  ///////////////////////////////////////
  // Listener registration/deregistration
  
  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the receiver's selection changes, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * When <code>widgetSelected</code> is called, the item field of the event object is valid.
   * <code>widgetDefaultSelected</code> is not called.
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
  
  ///////////
  // Disposal
  
  protected void releaseChildren() {
    TabItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    super.releaseChildren();
  }
  
  ////////////////
  // Item creation
  
  void createItem( final TabItem item, final int index ) {
    itemHolder.insert( item, index );
    if( getItemCount() == 1 ) {
      setSelection( 0 );
      SelectionEvent event 
        = new SelectionEvent( this, item, SelectionEvent.WIDGET_SELECTED );
      event.processEvent();
    }
  }

  ///////////////////
  // Helping methods
  
  private Point computeItemSize( final TabItem item ) {
    Point result = new Point( 0, 0 );
    String text = item.getText();
    if( text != null ) {
      Point extent = FontSizeEstimation.stringExtent( text, getFont() );
      // TODO [rst] these are only rough estimations
      result.x += extent.x + 10 + 6;
      result.y = extent.y + 4 + 6;
    }
    Image image = item.getImage();
    if( image != null ) {
      // TODO [rst] use image.getBounds()
      Point size = new Point( 16, 16 );
      result.x += size.x + 4;
      result.y = Math.max( size.x, result.x );
    }
    return result;
  }
  
  private static int checkStyle( final int style ) {
    int result = checkBits( style, SWT.TOP, SWT.BOTTOM, 0, 0, 0, 0 );
    /*
    * Even though it is legal to create this widget
    * with scroll bars, they serve no useful purpose
    * because they do not automatically scroll the
    * widget's client area.  The fix is to clear
    * the SWT style.
    */
    return result & ~( SWT.H_SCROLL | SWT.V_SCROLL );
  }
}
