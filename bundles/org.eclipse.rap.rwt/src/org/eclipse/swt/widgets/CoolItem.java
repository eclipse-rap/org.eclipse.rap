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

import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ItemHolder;



/**
 * Instances of this class are selectable user interface
 * objects that represent the dynamically positionable
 * areas of a <code>CoolBar</code>.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>DROP_DOWN</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class CoolItem extends Item {

  private static final class CoolItemOrderComparator implements Comparator {

    public int compare( final Object object1, final Object object2 ) {
      int result;
      CoolItem item1 = ( CoolItem )object1;
      CoolItem item2 = ( CoolItem )object2;
      if( item1.getOrder() > item2.getOrder() ) {
        result = +1;
      } else if( item1.getOrder() < item2.getOrder() ) {
        result = -1;
      } else {
        result = 0;
      }
      return result;
    }
  }

  // Keep in sync with defaultValue for handleSize in CoolItem.js 
  // (see function updateHandleBounds)
  public static final int HANDLE_SIZE = 3; 
  
  private final CoolBar parent;
  private int order;
  // TODO [rh] reasonable default value
  private Point size = new Point( 0, 0 );
  private Control control;

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>CoolBar</code>) and a style value
   * describing its behavior and appearance. The item is added
   * to the end of the items maintained by its parent.
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
   * @see SWT#DROP_DOWN
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  // TODO [rh] constructor missing: CoolItem(CoolBar,int,int)
  public CoolItem( final CoolBar parent, final int style ) {
    super( parent, style );
    this.parent = parent;
    ItemHolder.addItem( parent, this );
    order = parent.getItemCount() - 1;
  }
  
  ///////////////////
  // Widget overrides 
  
  public Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }
  
  ////////////////
  // Getter/setter
  
  /**
   * Returns the receiver's parent, which must be a <code>CoolBar</code>.
   *
   * @return the receiver's parent
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public CoolBar getParent () {
    checkWidget();
    return parent;
  }

  /**
   * Sets the control that is associated with the receiver
   * to the argument.
   *
   * @param control the new control that will be contained by the receiver
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li> 
   *    <li>ERROR_INVALID_PARENT - if the control is not in the same widget tree</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setControl( final Control control ) {
    checkWidget();
    if( control != null ) {
      if( control.isDisposed() ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      if( control.getParent() != getParent() ) {
        SWT.error( SWT.ERROR_INVALID_PARENT );
      }
    }
    this.control = control;
  }

  /**
   * Returns the control that is associated with the receiver.
   *
   * @return the control that is contained by the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Control getControl() {
    checkWidget();
    return control;
  }
  
  ///////////////////////
  // Size-related methods
  
  /**
   * Returns the preferred size of the receiver.
   * <p>
   * The <em>preferred size</em> of a <code>CoolItem</code> is the size that
   * it would best be displayed at. The width hint and height hint arguments
   * allow the caller to ask the instance questions such as "Given a particular
   * width, how high does it need to be to show all of the contents?"
   * To indicate that the caller does not wish to constrain a particular 
   * dimension, the constant <code>SWT.DEFAULT</code> is passed for the hint. 
   * </p>
   *
   * @param wHint the width hint (can be <code>SWT.DEFAULT</code>)
   * @param hHint the height hint (can be <code>SWT.DEFAULT</code>)
   * @return the preferred size
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Layout
   * @see #getBounds
   * @see #getSize
   * @see Control#getBorderWidth
   * @see Scrollable#computeTrim
   * @see Scrollable#getClientArea
   */
  public Point computeSize( final int wHint, final int hHint ) {
    checkWidget();
    Point result;
    int index = parent.indexOf( this );
    if( index == -1 ) {
      result = new Point( 0, 0 );
    } else {
      int width = wHint;
      int height = hHint;
      if( wHint == SWT.DEFAULT ) {
        width = 32;
      }
      if( hHint == SWT.DEFAULT ) {
        height = 32;
      }
      if( ( parent.style & SWT.VERTICAL ) != 0 ) {
        height += parent.getMargin( index );
      } else {
        width += parent.getMargin( index );
      }
      result = new Point( width, height );
    }
    return result;
  }

  /**
   * Sets the receiver's ideal size to the point specified by the argument.
   *
   * @param size the new ideal size for the receiver
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setPreferredSize( final Point preferredSize ) {
    checkWidget();
    if( preferredSize == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    setPreferredSize( preferredSize.x, preferredSize.y );
  }

  /**
   * Sets the receiver's ideal size to the point specified by the arguments.
   *
   * @param width the new ideal width for the receiver
   * @param height the new ideal height for the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setPreferredSize( final int wHint, final int hHint ) {
    checkWidget();
    if( parent.indexOf( this ) != -1 ) {
      int width = Math.max( 0, wHint );
      int height = Math.max( 0, hHint );
      int x, y;
      if( ( parent.style & SWT.VERTICAL ) != 0 ) {
        x = Math.max( 0, height - parent.getMargin( parent.indexOf( this ) ) );
        y = width;
      } else {
        x = Math.max( 0, width - parent.getMargin( parent.indexOf( this ) ) );
        y = height;
      }
      setSize( x, y );
    }
  }
  
  /**
   * Sets the receiver's size to the point specified by the arguments.
   * <p>
   * Note: Attempting to set the width or height of the
   * receiver to a negative number will cause that
   * value to be set to zero instead.
   * </p>
   *
   * @param width the new width for the receiver
   * @param height the new height for the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSize( final int wHint, final int hHint ) {
    checkWidget();
    if( parent.indexOf( this ) != -1 ) {
      int width = Math.max( 0, wHint );
      int height = Math.max( 0, hHint );
      int x;
      int y;
      if( ( parent.style & SWT.VERTICAL ) != 0 ) {
        x = height + HANDLE_SIZE;
        y = width;
      } else {
        x = width + HANDLE_SIZE;
        y = height;
      }
      size = new Point( x, y );
    }
  }
  
  /**
   * Sets the receiver's size to the point specified by the argument.
   * <p>
   * Note: Attempting to set the width or height of the
   * receiver to a negative number will cause them to be
   * set to zero instead.
   * </p>
   *
   * @param size the new size for the receiver
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSize( final Point size ) {
    checkWidget();
    if( size == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    setSize( size.x, size.y );
  }
  
  /**
   * Returns a point describing the receiver's size. The
   * x coordinate of the result is the width of the receiver.
   * The y coordinate of the result is the height of the
   * receiver.
   *
   * @return the receiver's size
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Point getSize() {
    checkWidget();
    return new Point( size.x, size.y );
  }
  
  /**
   * Returns a rectangle describing the receiver's size and location
   * relative to its parent.
   *
   * @return the receiver's bounding rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Rectangle getBounds() {
    checkWidget();
    int left = 0;
    int top = 0;
    CoolItem[] items = getOrderedItems();
    for( int i = 0; items[ i ] != this && i < items.length; i++ ) {
      Point itemSize = parent.getItem( i ).getSize();
      if( ( parent.style & SWT.VERTICAL ) != 0 ) {
        top += itemSize.x;
      } else {
        left += itemSize.x;
      }
    }
    return new Rectangle( left, top, size.x, size.y );
  }
  
  public Rectangle getClientArea() {
    checkWidget();
    Rectangle size = getBounds();
    Rectangle result;
    if( ( parent.style & SWT.VERTICAL ) != 0 ) {
      result = new Rectangle( 0, HANDLE_SIZE, size.x, size.y - HANDLE_SIZE );
    } else {
      result = new Rectangle( HANDLE_SIZE, 0, size.x - HANDLE_SIZE, size.y );
    }
    return result;
  }

  ////////////////////////////
  // Item overrides - disposal 
  
  protected void releaseChildren() {
  }

  protected void releaseParent() {
    ItemHolder.removeItem( parent, this );
  }

  protected void releaseWidget() {
    control = null;
  }
  
  /////////////////////////////////////////
  // Helping methods to maintain item order
  
  int getOrder() {
    return order;
  }

  void setOrder( final int order ) {
    this.order = order;
  }

  private CoolItem[] getOrderedItems() {
    CoolItem[] result = parent.getItems();
    Arrays.sort( result, new CoolItemOrderComparator() );
    return result;
  }
}
