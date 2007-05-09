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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.widgets.ControlHolder.IControlHolderAdapter;

/**
 * Instances of this class are controls which are capable
 * of containing other controls.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>NO_FOCUS</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * This class may be subclassed by custom control implementors
 * who are building controls that are constructed from aggregates
 * of other controls.
 * </p>
 *
 * @see Canvas
 */
public class Composite extends Scrollable {

  private Layout layout;
  private final ControlHolder controlHolder = new ControlHolder();
  private Control[] tabList;
  
  Composite( final Composite parent ) {
    // prevent instantiation from outside this package
    super( parent );
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
   * @param parent a widget which will be the parent of the new instance (cannot be null)
   * @param style the style of widget to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   * </ul>
   *
   * @see SWT#NO_FOCUS
   * @see Widget#getStyle
   */
  public Composite( final Composite parent, final int style ) {
    super( parent, style );
  }

  /**
   * Returns a (possibly empty) array containing the receiver's children.
   * Children are returned in the order that they are drawn.  The topmost
   * control appears at the beginning of the array.  Subsequent controls
   * draw beneath this control and appear later in the array.
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of children, so modifying the array will
   * not affect the receiver. 
   * </p>
   *
   * @return an array of children
   * 
   * @see Control#moveAbove
   * @see Control#moveBelow
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Control[] getChildren() {
    checkWidget();
    return controlHolder.getControls();
  }

  public int getChildrenCount() {
    checkWidget();
    return controlHolder.size();
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IControlHolderAdapter.class ) {
      result = controlHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  //////////////////
  // Layout methods
  
  /**
   * Sets the layout which is associated with the receiver to be
   * the argument which may be null.
   *
   * @param layout the receiver's new layout or null
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setLayout( final Layout layout ) {
    checkWidget();
    this.layout = layout;
  }

  /**
   * Returns layout which is associated with the receiver, or
   * null if one has not been set.
   *
   * @return the receiver's layout or null
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Layout getLayout() {
    checkWidget();
    return layout;
  }

  /**
   * If the receiver has a layout, asks the layout to <em>lay out</em>
   * (that is, set the size and location of) the receiver's children. 
   * If the receiver does not have a layout, do nothing.
   * <p>
   * This is equivalent to calling <code>layout(true)</code>.
   * </p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void layout() {
    checkWidget();
    if( layout != null ) {
      layout.layout( this, true );
    }
  }
  
  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    Point size;
    boolean hasChanged = changed;
    if( layout != null ) {
      if( wHint == SWT.DEFAULT || hHint == SWT.DEFAULT ) {
        hasChanged |= ( state & LAYOUT_CHANGED ) != 0;
        state &= ~LAYOUT_CHANGED;
        size = layout.computeSize( this, wHint, hHint, hasChanged );
      } else {
        size = new Point( wHint, hHint );
      }
    } else {
      size = minimumSize( wHint, hHint, hasChanged );
    }
    if( size.x == 0 ) {
      size.x = DEFAULT_WIDTH;
    }
    if( size.y == 0 ) {
      size.y = DEFAULT_HEIGHT;
    }
    if( wHint != SWT.DEFAULT ) {
      size.x = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
      size.y = hHint;
    }
    Rectangle trim = computeTrim( 0, 0, size.x, size.y );
    return new Point( trim.width, trim.height );
  }

  ////////////////////
  // setFocus override 
  
  public boolean setFocus() {
    checkWidget();
    Control[] children = getChildren();
//     for( int i = 0; i < children.length; i++ ) {
//      Control child = children[ i ];
//      if( child.setRadioFocus() )
//        return true;
//    }
    Control focusedChild = null;
    for( int i = 0; focusedChild == null && i < children.length; i++ ) {
      Control child = children[ i ];
      if( child.setFocus() ) {
        focusedChild = child;
      }
    }
    boolean result = true;
    if( focusedChild == null ) {
      result = super.setFocus();
    } 
    return result;
  }

  ////////////
  // Tab Order
  
  /**
   * Sets the tabbing order for the specified controls to
   * match the order that they occur in the argument list.
   *
   * @param tabList the ordered list of controls representing the tab order or null
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if a widget in the tabList is null or has been disposed</li> 
   *    <li>ERROR_INVALID_PARENT - if widget in the tabList is not in the same widget tree</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setTabList( final Control[] tabList ) {
    checkWidget();
    Control[] newList = tabList;
    if( tabList != null ) {
      for( int i = 0; i < tabList.length; i++ ) {
        Control control = tabList[ i ];
        if( control == null ) {
          error( SWT.ERROR_INVALID_ARGUMENT );
        }
        if( control.isDisposed() ) {
          error( SWT.ERROR_INVALID_ARGUMENT );
        }
        if( control.parent != this ) {
          error( SWT.ERROR_INVALID_PARENT );
        }
      }
      newList = new Control[ tabList.length ];
      System.arraycopy( tabList, 0, newList, 0, tabList.length );
    }
    this.tabList = newList;
  }
  
  /**
   * Gets the (possibly empty) tabbing order for the control.
   *
   * @return tabList the ordered list of controls representing the tab order
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see #setTabList
   */
  // returns only tabGroups
  public Control[] getTabList() {
    checkWidget();
    Control[] result = _getTabList();
    if( result == null ) {
      int count = 0;
      Control[] list = controlHolder.getControls();
      for( int i = 0; i < list.length; i++ ) {
        if( list[ i ].isTabGroup() ) {
          count++;
        }
      }
      result = new Control[ count ];
      int index = 0;
      for( int i = 0; i < list.length; i++ ) {
        if( list[ i ].isTabGroup() ) {
          result[ index++ ] = list[ i ];
        }
      }
    }
    return result;
  }

  // filters disposed controls out
  Control[] _getTabList() {
    if( tabList != null ) {
      int count = 0;
      for( int i = 0; i < tabList.length; i++ ) {
        if( !tabList[ i ].isDisposed() ) {
          count++;
        }
      }
      if( count != tabList.length ) {
        Control[] newList = new Control[ count ];
        int index = 0;
        for( int i = 0; i < tabList.length; i++ ) {
          if( !tabList[ i ].isDisposed() ) {
            newList[ index++ ] = tabList[ i ];
          }
        }
        tabList = newList;
      }
    }
    return tabList;
  }
  
  boolean isTabGroup() {
    return true;
  }
  
  /////////////////////////////////////
  // Helping method used by computeSize
  
  Point minimumSize( final int wHint, final int hHint, final boolean changed ) {
    Control[] children = getChildren();
    int width = 0, height = 0;
    for( int i = 0; i < children.length; i++ ) {
      Rectangle rect = children[ i ].getBounds();
      width = Math.max( width, rect.x + rect.width );
      height = Math.max( height, rect.y + rect.height );
    }
    return new Point( width, height );
  }

  /////////////////////////////////////////////////
  // Internal methods to maintain the child controls
  
  protected void releaseChildren() {
    Control[] children = getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].dispose();
    }
  }
  
  void removeControl( final Control control ) {
    controlHolder.remove( control );
  }

  ////////////////
  // Resize helper
  
  void notifyResize( final Point oldSize ) {
    if( !oldSize.equals( getSize() ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          layout();
        }
      } );
    }
    super.notifyResize( oldSize );
  }
}