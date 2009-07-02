/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.*;


/**
 * Instances of this class support the layout of selectable expand bar items.
 * <p>
 * The item children that may be added to instances of this class must be of
 * type <code>ExpandItem</code>.
 * </p>
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>V_SCROLL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Expand, Collapse</dd>
 * </dl>
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see ExpandItem
 * @see ExpandEvent
 * @see ExpandListener
 * @see ExpandAdapter
 * @since 1.2
 */
public class ExpandBar extends Composite {

  private final class ExpandBarAdapter implements IExpandBarAdapter {

    public Rectangle getBounds( final ExpandItem item ) {
      int index = ExpandBar.this.indexOf( item );
      return ExpandBar.this.getItem( index ).getBounds();
    }

    public boolean isVScrollbarVisible() {
      return ExpandBar.this.isVScrollbarVisible();
    }

    public Rectangle getBottomSpacingBounds() {
      return ExpandBar.this.getBottomSpacingBounds();
    }
  }

  private final class ResizeListener extends ControlAdapter {
    public void controlResized( final ControlEvent event ) {
      layoutItems( 0, true );
    }
  }

  static final int V_SCROLL_WIDTH = 16;
  static final int BORDER = 2;
  ExpandItem focusItem;
  int spacing;
  int v_scroll;
  int border;
  int allItemsHeight;
  int charHeight;
  private final ItemHolder itemHolder;
  private final IExpandBarAdapter expandBarAdapter;
  private final ResizeListener resizeListener;

  /**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must
   * be built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code>
   * style constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new
   *          instance (cannot be null)
   * @param style the style of control to construct
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *              subclass</li>
   *              </ul>
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public ExpandBar( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    spacing = 4;
    expandBarAdapter = new ExpandBarAdapter();
    resizeListener = new ResizeListener();
    addControlListener( resizeListener );
    itemHolder = new ItemHolder( ExpandItem.class );
    if( ( getStyle() & SWT.V_SCROLL ) != 0 ) {
      v_scroll = V_SCROLL_WIDTH;
    }
    if( ( getStyle() & SWT.BORDER ) != 0 ) {
      border = BORDER;
    }
  }

  /**
   * Adds the listener to the collection of listeners who will be notified when
   * an item in the receiver is expanded or collapsed by sending it one of the
   * messages defined in the <code>ExpandListener</code> interface.
   *
   * @param listener the listener which should be notified
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see ExpandListener
   * @see #removeExpandListener
   */
  public void addExpandListener( final ExpandListener listener ) {
    ExpandEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will be notified
   * when items in the receiver are expanded or collapsed.
   *
   * @param listener the listener which should no longer be notified
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see ExpandListener
   * @see #addExpandListener
   */
  public void removeExpandListener( final ExpandListener listener ) {
    ExpandEvent.removeListener( this, listener );
  }

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int height = 0, width = 0;
    int itemCount = getItemCount();
    if( wHint == SWT.DEFAULT || hHint == SWT.DEFAULT ) {
      if( itemCount > 0 ) {
        height += spacing;
        for( int i = 0; i < itemCount; i++ ) {
          ExpandItem item = getItem( i );
          height += item.getHeaderHeight();
          if( item.expanded ) {
            height += item.height;
          }
          height += spacing;
          int barPreferredWidth = item.getPreferredWidth()
                                  + v_scroll
                                  + 2
                                  * spacing
                                  + 2
                                  * border;
          width = Math.max( width, barPreferredWidth );
        }
        height += 2 * border;
      }
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
    return new Point( width, height );
  }

  void createItem( final ExpandItem item, final int style, final int index ) {
    itemHolder.insert( item, index );
    if( focusItem == null ) {
      focusItem = item;
    }
    layoutItems( index, true );
  }

  void destroyItem( final ExpandItem item ) {
    int index = 0;
    int itemCount = getItemCount();
    for( int i = 0; i < itemCount; i++ ) {
      if( getItem( i ) == item ) {
        index = i;
      }
    }
    if( index != itemCount ) {
      if( item == focusItem ) {
        int focusIndex = index > 0
                                  ? index - 1
                                  : 1;
        if( focusIndex < itemCount ) {
          focusItem = getItem( focusIndex );
        } else {
          focusItem = null;
        }
      }
      itemHolder.remove( item );
      layoutItems( index, true );
    }
  }

  Control findBackgroundControl() {
    Control control = super.findBackgroundControl();
    if( !isAppThemed() ) {
      if( control == null ) {
        control = this;
      }
    }
    return control;
  }

  int getBandHeight() {
    return Math.max( ExpandItem.CHEVRON_SIZE, charHeight );
  }

  /**
   * Returns the item at the given, zero-relative index in the receiver. Throws
   * an exception if the index is out of range.
   *
   * @param index the index of the item to return
   * @return the item at the given index
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
  public ExpandItem getItem( final int index ) {
    checkWidget();
    return ( ExpandItem )itemHolder.getItem( index );
  }

  /**
   * Returns the number of items contained in the receiver.
   *
   * @return the number of items
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  /**
   * Returns an array of <code>ExpandItem</code>s which are the items in the
   * receiver.
   * <p>
   * Note: This is not the actual structure used by the receiver to maintain its
   * list of items, so modifying the array will not affect the receiver.
   * </p>
   *
   * @return the items in the receiver
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public ExpandItem[] getItems() {
    checkWidget();
    return ( org.eclipse.swt.widgets.ExpandItem[] )itemHolder.getItems();
  }

  /**
   * Returns the receiver's spacing.
   *
   * @return the spacing
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int getSpacing() {
    checkWidget();
    return spacing;
  }

  /**
   * Searches the receiver's list starting at the first item (index 0) until an
   * item is found that is equal to the argument, and returns the index of that
   * item. If no item is found, returns -1.
   *
   * @param item the search item
   * @return the index of the item
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the item is null</li>
   *              <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public int indexOf( final ExpandItem item ) {
    checkWidget();
    return itemHolder.indexOf( item );
  }

  void layoutItems( final int index, final boolean setScrollbar ) {
    int itemCount = getItemCount();
    if( index < itemCount ) {
      int y = spacing;
      for( int i = 0; i < index; i++ ) {
        ExpandItem item = getItem( i );
        if( item.expanded ) {
          y += item.height;
        }
        y += item.getHeaderHeight() + spacing;
      }
      for( int i = index; i < itemCount; i++ ) {
        ExpandItem item = getItem( i );
        item.setBounds( spacing, y, 0, 0, true, false );
        if( item.expanded ) {
          y += item.height;
        }
        y += item.getHeaderHeight() + spacing;
      }
    }
    // Calculate all items size
    if( itemCount > 0 ) {
      ExpandItem lastItem = getItem( itemCount - 1 );
      allItemsHeight = lastItem.y + lastItem.getBounds().height;
    }
    // Set items width based on scrollbar visibility
    for( int i = 0; i < itemCount; i++ ) {
      ExpandItem item = getItem( i );
      if( isVScrollbarVisible() ) {
        int width = getBounds().width - v_scroll - 2 * border - 2 * spacing;
        item.setBounds( 0, 0, width, item.height, false, true );
      } else {
        int width = getBounds().width - 2 * border - 2 * spacing;
        item.setBounds( 0, 0, width, item.height, false, true );
      }
    }
  }

  /**
   * Sets the font that the receiver will use to paint textual information
   * to the font specified by the argument, or to the default font for that
   * kind of control if the argument is null.
   *
   * @param font the new font (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public void setFont( final Font font ) {
    if( font != getFont() ) {
      super.setFont( font );
      charHeight = TextSizeDetermination.getCharHeight( font ) + 4;
    }
  }

  /**
   * Sets the receiver's spacing. Spacing specifies the number of pixels
   * allocated around each item.
   *
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public void setSpacing( final int spacing ) {
    checkWidget();
    if( spacing >= 0 ) {
      if( spacing != this.spacing ) {
        this.spacing = spacing;
        layoutItems( 0, true );
      }
    }
  }

  void showItem( final ExpandItem item ) {
    Control control = item.control;
    if( control != null && !control.isDisposed() ) {
      control.setVisible( item.expanded );
    }
    int index = indexOf( item );
    layoutItems( index + 1, true );
  }

  protected void checkSubclass() {
    if( !isValidSubclass() ) {
      error( SWT.ERROR_INVALID_SUBCLASS );
    }
  }

  static int checkStyle( int style ) {
    int aStyle = style & ~SWT.H_SCROLL;
    return aStyle;
  }

  boolean isAppThemed() {
    return false;
  }

  boolean isVScrollbarVisible() {
    return ( getStyle() & SWT.V_SCROLL ) != 0
           && ( allItemsHeight > getBounds().height - 2 * border - spacing );
  }

  Rectangle getBottomSpacingBounds() {
    return new Rectangle( spacing, allItemsHeight, 10, spacing );
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else if( adapter == IExpandBarAdapter.class ) {
      result = expandBarAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  /////////////////////
  // Destroy expand bar

  void releaseWidget() {
    super.releaseWidget();
    if( resizeListener != null ) {
      removeControlListener( resizeListener );
    }
  }

  void releaseChildren() {
    Item[] expandItems = new ExpandItem[ getItemCount() ];
    System.arraycopy( getItems(), 0, expandItems, 0, getItems().length );
    for( int i = 0; i < expandItems.length; i++ ) {
      if( expandItems[ i ] != null ) {
        expandItems[ i ].dispose();
      }
    }
  }
}
