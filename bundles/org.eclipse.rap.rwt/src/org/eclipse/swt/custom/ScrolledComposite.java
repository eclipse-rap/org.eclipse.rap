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

package org.eclipse.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

/**
 * A ScrolledComposite provides scrollbars and will scroll its content when the user
 * uses the scrollbars.
 *
 *
 * <p>There are two ways to use the ScrolledComposite:
 * 
 * <p>
 * 1) Set the size of the control that is being scrolled and the ScrolledComposite 
 * will show scrollbars when the contained control can not be fully seen.
 * 
 * 2) The second way imitates the way a browser would work.  Set the minimum size of
 * the control and the ScrolledComposite will show scroll bars if the visible area is 
 * less than the minimum size of the control and it will expand the size of the control 
 * if the visible area is greater than the minimum size.  This requires invoking 
 * both setMinWidth(), setMinHeight() and setExpandHorizontal(), setExpandVertical().
 * 
 * <dl>
 * <dt><b>Styles:</b><dd>H_SCROLL, V_SCROLL
 * </dl>
 */
public class ScrolledComposite extends Composite {

  private final ControlAdapter contentListener;
  private ScrollBar horizontalBar;
  private ScrollBar verticalBar;
  Control content;
  int minHeight = 0;
  int minWidth = 0;
  boolean expandHorizontal;
  boolean expandVertical;
  boolean alwaysShowScroll;

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
   * @see SWT#H_SCROLL
   * @see SWT#V_SCROLL
   * @see #getStyle()
   */	
  public ScrolledComposite( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    super.setLayout( new ScrolledCompositeLayout() );
    createScrollBars( style );
    registerScrollListeners();
    contentListener = new ControlAdapter() {
      public void controlResized( final ControlEvent event ) {
        layout();
//        layout( false );
      }
    };
  }

  /**
   * Sets the layout which is associated with the receiver to be
   * the argument which may be null.
   * <p>
   * Note: No Layout can be set on this Control because it already
   * manages the size and position of its children.
   * </p>
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
    // ignore - ScrolledComposite manages its own layout
  }
  
  // TODO [rh] move client area calculation to Scrollable
  public Rectangle getClientArea () {
    checkWidget ();
    Rectangle result = getBounds();
    ScrollBar hBar = getHorizontalBar();
    if( hBar != null && hBar.getVisible() ) {
      result.height -= hBar.getSize().y;
    }
    ScrollBar vBar = getVerticalBar();
    if( vBar != null && vBar.getVisible() ) {
      result.width -= vBar.getSize().x;
    }
    return result;
  }
  
  /**
   * Scrolls the content so that the specified point in the content is in the top 
   * left corner.  If no content has been set, nothing will occur.  
   * 
   * Negative values will be ignored.  Values greater than the maximum scroll 
   * distance will result in scrolling to the end of the scrollbar.
   *
   * @param origin the point on the content to appear in the top left corner 
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_INVALID_ARGUMENT - value of origin is outside of content
   * </ul>
   * @since 1.0
   */
  public void setOrigin( final Point origin ) {
    setOrigin( origin.x, origin.y );
  }

  /**
   * Scrolls the content so that the specified point in the content is in the top 
   * left corner.  If no content has been set, nothing will occur.  
   * 
   * Negative values will be ignored.  Values greater than the maximum scroll 
   * distance will result in scrolling to the end of the scrollbar.
   *
   * @param x the x coordinate of the content to appear in the top left corner 
   * 
   * @param y the y coordinate of the content to appear in the top left corner 
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setOrigin( final int left, final int top ) {
    checkWidget();
    if( content != null ) {
      int x = left;
      int y = top;
      ScrollBar hBar = getHorizontalBar();
      if( hBar != null ) {
        hBar.setSelection( x );
        x = -hBar.getSelection();
      } else {
        x = 0;
      }
      ScrollBar vBar = getVerticalBar();
      if( vBar != null ) {
        vBar.setSelection( y );
        y = -vBar.getSelection();
      } else {
        y = 0;
      }
      content.setLocation( x, y );
    } 
  }

  /**
   * Return the point in the content that currently appears in the top left 
   * corner of the scrolled composite.
   * 
   * @return the point in the content that currently appears in the top left 
   * corner of the scrolled composite.  If no content has been set, this returns
   * (0, 0).
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public Point getOrigin() {
    checkWidget();
    Point result;
    if( content == null ) {
      result = new Point( 0, 0 );
    } else {
      Point location = content.getLocation();
      result = new Point( -location.x, -location.y );
    }
    return result;
  }

  /**
   * Set the Always Show Scrollbars flag.  True if the scrollbars are 
   * always shown even if they are not required.  False if the scrollbars are only 
   * visible when some part of the composite needs to be scrolled to be seen.
   * The H_SCROLL and V_SCROLL style bits are also required to enable scrollbars in the 
   * horizontal and vertical directions.
   * 
   * @param show true to show the scrollbars even when not required, false to show scrollbars only when required
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setAlwaysShowScrollBars( final boolean show ) {
    checkWidget();
    if( !show != !alwaysShowScroll ) {
      alwaysShowScroll = show;
      ScrollBar hBar = getHorizontalBar();
      if( hBar != null && alwaysShowScroll ) {
        hBar.setVisible( true );
      }
      ScrollBar vBar = getVerticalBar();
      if( vBar != null && alwaysShowScroll ) {
        vBar.setVisible( true );
      }
// layout( false );
      layout();
    } 
  }

  /**
   * Returns the Always Show Scrollbars flag.  True if the scrollbars are 
   * always shown even if they are not required.  False if the scrollbars are only 
   * visible when some part of the composite needs to be scrolled to be seen.
   * The H_SCROLL and V_SCROLL style bits are also required to enable scrollbars in the 
   * horizontal and vertical directions.
   * 
   * @return the Always Show Scrollbars flag value
   */
  public boolean getAlwaysShowScrollBars() {
    //checkWidget();  // <- commented in SWT
    return alwaysShowScroll;
  }
  
  /**
   * Returns <code>true</code> if the content control 
   * will be expanded to fill available horizontal space.
   *
   * @return the receiver's horizontal expansion state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public boolean getExpandHorizontal() {
    checkWidget();
    return expandHorizontal;
  }

  /**
   * Returns <code>true</code> if the content control 
   * will be expanded to fill available vertical space.
   *
   * @return the receiver's vertical expansion state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public boolean getExpandVertical() {
    checkWidget();
    return expandVertical;
  }
  
  /**
   * Configure the ScrolledComposite to resize the content object to be as wide as the 
   * ScrolledComposite when the width of the ScrolledComposite is greater than the
   * minimum width specified in setMinWidth.  If the ScrolledComposite is less than the
   * minimum width, the content will not be resized and instead the horizontal scroll bar will be
   * used to view the entire width.
   * If expand is false, this behaviour is turned off.  By default, this behaviour is turned off.
   * 
   * @param expand true to expand the content control to fill available horizontal space
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setExpandHorizontal( final boolean expand ) {
    checkWidget();
    if( expand != expandHorizontal ) {
      expandHorizontal = expand;
      // layout( false );
      layout();
    }
  }
  
  /**
   * Configure the ScrolledComposite to resize the content object to be as tall as the 
   * ScrolledComposite when the height of the ScrolledComposite is greater than the
   * minimum height specified in setMinHeight.  If the ScrolledComposite is less than the
   * minimum height, the content will not be resized and instead the vertical scroll bar will be
   * used to view the entire height.
   * If expand is false, this behaviour is turned off.  By default, this behaviour is turned off.
   * 
   * @param expand true to expand the content control to fill available vertical space
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setExpandVertical( final boolean expand ) {
    checkWidget();
    if( expand != expandVertical ) {
      expandVertical = expand;
      // layout( false );
      layout();
    }
  }
  
  // TODO [rh] move to Scrollable
  public ScrollBar getHorizontalBar() {
    checkWidget ();
    return horizontalBar;
  }

  // TODO [rh] move to Scrollable
  public ScrollBar getVerticalBar() {
    checkWidget ();
    return verticalBar;
  }

  ///////////////////////
  // Min width and height
  
  /**
   * Specify the minimum width at which the ScrolledComposite will begin scrolling the
   * content with the horizontal scroll bar.  This value is only relevant if  
   * setExpandHorizontal(true) has been set.
   * 
   * @param width the minimum width or 0 for default width
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setMinWidth( final int width ) {
    setMinSize( width, minHeight );
  }

  /**
   * Returns the minimum width of the content control.
   *
   * @return the minimum width
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public int getMinWidth() {
    checkWidget();
    return minWidth;
  }

  /**
   * Specify the minimum height at which the ScrolledComposite will begin scrolling the
   * content with the vertical scroll bar.  This value is only relevant if  
   * setExpandVertical(true) has been set.
   * 
   * @param height the minimum height or 0 for default height
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setMinHeight( final int height ) {
    setMinSize( minWidth, height );
  }

  /**
   * Returns the minimum height of the content control.
   *
   * @return the minimum height
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public int getMinHeight() {
    checkWidget();
    return minHeight;
  }

  /**
   * Specify the minimum width and height at which the ScrolledComposite will begin scrolling the
   * content with the horizontal scroll bar.  This value is only relevant if  
   * setExpandHorizontal(true) and setExpandVertical(true) have been set.
   * 
   * @param size the minimum size or null for the default size
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setMinSize( final Point size ) {
    if( size == null ) {
      setMinSize( 0, 0 );
    } else {
      setMinSize( size.x, size.y );
    }
  }

  /**
   * Specify the minimum width and height at which the ScrolledComposite will begin scrolling the
   * content with the horizontal scroll bar.  This value is only relevant if  
   * setExpandHorizontal(true) and setExpandVertical(true) have been set.
   * 
   * @param width the minimum width or 0 for default width
   * @param height the minimum height or 0 for default height
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setMinSize( final int width, final int height ) {
    checkWidget();
    if( width != minWidth || height != minHeight ) {
      minWidth = Math.max( 0, width );
      minHeight = Math.max( 0, height );
      // layout(false);
      layout();
    }
  }
  
  //////////////////
  // Content control

  /**
   * Set the content that will be scrolled.
   * 
   * @param content the control to be displayed in the content area
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setContent( final Control content ) {
    checkWidget();
    if( this.content != null && !this.content.isDisposed() ) {
      this.content.removeControlListener( contentListener );
      this.content.setBounds( new Rectangle( -200, -200, 0, 0 ) );
    }
    this.content = content;
    ScrollBar vBar = getVerticalBar();
    ScrollBar hBar = getHorizontalBar();
    if( this.content != null ) {
      if( vBar != null ) {
        vBar.setMaximum( 0 );
        vBar.setThumb( 0 );
        vBar.setSelection( 0 );
      }
      if( hBar != null ) {
        hBar.setMaximum( 0 );
        hBar.setThumb( 0 );
        hBar.setSelection( 0 );
      }
      content.setLocation( 0, 0 );
      layout();
      // layout(false);
      this.content.addControlListener( contentListener );
    } else {
      if( hBar != null ) {
        hBar.setVisible( alwaysShowScroll );
      }
      if( vBar != null ) {
        vBar.setVisible( alwaysShowScroll );
      }
    }
  }
  
  /**
   * Get the content that is being scrolled.
   * 
   * @return the control displayed in the content area
   */
  public Control getContent() {
    //checkWidget();  // <- commented in SWT
    return content;
  }

  //////////////////
  // Widget disposal
  
  protected void releaseWidget() {
    if( content != null && !content.isDisposed() ) {
      content.removeControlListener( contentListener );
    }
    super.releaseWidget();
  }
  
  ////////////
  // Scrolling
  
  boolean needHScroll( final Rectangle contentRect, final boolean vVisible ) {
    ScrollBar hBar = getHorizontalBar();
    if( hBar == null ) {
      return false;
    }
    Rectangle hostRect = getBounds();
    int border = getBorderWidth();
    hostRect.width -= 2 * border;
    ScrollBar vBar = getVerticalBar();
    if( vVisible && vBar != null ) {
      hostRect.width -= vBar.getSize().x;
    }
    if( !expandHorizontal && contentRect.width > hostRect.width ) {
      return true;
    }
    if( expandHorizontal && minWidth > hostRect.width ) {
      return true;
    }
    return false;
  }

  boolean needVScroll( final Rectangle contentRect, final boolean hVisible ) {
    ScrollBar vBar = getVerticalBar();
    if( vBar == null ) {
      return false;
    }
    Rectangle hostRect = getBounds();
    int border = getBorderWidth();
    hostRect.height -= 2 * border;
    ScrollBar hBar = getHorizontalBar();
    if( hVisible && hBar != null ) {
      hostRect.height -= hBar.getSize().y;
    }
    if( !expandVertical && contentRect.height > hostRect.height ) {
      return true;
    }
    if( expandVertical && minHeight > hostRect.height ) {
      return true;
    }
    return false;
  }
  
  void hScroll() {
    if( content == null ) {
      return;
    }
    Point location = content.getLocation();
    ScrollBar hBar = getHorizontalBar();
    int hSelection = hBar.getSelection();
    content.setLocation( -hSelection, location.y );
  }

  void vScroll() {
    if( content == null ) {
      return;
    }
    Point location = content.getLocation();
    ScrollBar vBar = getVerticalBar();
    int vSelection = vBar.getSelection();
    content.setLocation( location.x, -vSelection );
  }

  // ////////////////
  // Helping methods
  
  private void createScrollBars( final int style ) {
    // TODO [rh] move ScrollBar creation to Scrollable as in SWT
    if( ( style & SWT.H_SCROLL ) != 0 ) {
      horizontalBar = new ScrollBar( this, SWT.H_SCROLL );
    }
    if( ( style & SWT.V_SCROLL ) != 0 ) {
      verticalBar = new ScrollBar( this, SWT.V_SCROLL );
    }
  }

  private void registerScrollListeners() {
    ScrollBar hBar = getHorizontalBar();
    if( hBar != null ) {
      hBar.setVisible( false );
      hBar.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent event ) {
          hScroll();
        }
      } );
    }
    ScrollBar vBar = getVerticalBar();
    if( vBar != null ) {
      vBar.setVisible( false );
      vBar.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent event ) {
          vScroll();
        }
      } );
    }
  }
  
  private static int checkStyle( final int style ) {
    int mask 
      = SWT.H_SCROLL 
      | SWT.V_SCROLL 
      | SWT.BORDER; 
//      | SWT.LEFT_TO_RIGHT 
//      | SWT.RIGHT_TO_LEFT;
    return style & mask;
  }
}
