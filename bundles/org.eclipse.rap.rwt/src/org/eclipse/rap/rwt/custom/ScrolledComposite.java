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

package org.eclipse.rap.rwt.custom;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.widgets.*;


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
  
  public void setOrigin( final Point origin ) {
    setOrigin( origin.x, origin.y );
  }

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

  public boolean getAlwaysShowScrollBars() {
    //checkWidget();  // <- commented in SWT
    return alwaysShowScroll;
  }
  
  public boolean getExpandHorizontal() {
    checkWidget();
    return expandHorizontal;
  }

  public boolean getExpandVertical() {
    checkWidget();
    return expandVertical;
  }
  
  public void setExpandHorizontal( final boolean expand ) {
    checkWidget();
    if( expand != expandHorizontal ) {
      expandHorizontal = expand;
      // layout( false );
      layout();
    }
  }
  
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
  
  public void setMinWidth( final int width ) {
    setMinSize( width, minHeight );
  }

  public int getMinWidth() {
    checkWidget();
    return minWidth;
  }

  public void setMinHeight( final int height ) {
    setMinSize( minWidth, height );
  }

  public int getMinHeight() {
    checkWidget();
    return minHeight;
  }

  public void setMinSize( final Point size ) {
    if( size == null ) {
      setMinSize( 0, 0 );
    } else {
      setMinSize( size.x, size.y );
    }
  }

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
    if( ( style & RWT.H_SCROLL ) != 0 ) {
      horizontalBar = new ScrollBar( this, RWT.H_SCROLL );
    }
    if( ( style & RWT.V_SCROLL ) != 0 ) {
      verticalBar = new ScrollBar( this, RWT.V_SCROLL );
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
      = RWT.H_SCROLL 
      | RWT.V_SCROLL 
      | RWT.BORDER; 
//      | RWT.LEFT_TO_RIGHT 
//      | RWT.RIGHT_TO_LEFT;
    return style & mask;
  }
}
