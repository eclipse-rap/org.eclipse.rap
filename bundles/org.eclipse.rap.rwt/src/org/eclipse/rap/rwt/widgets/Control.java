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

package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.internal.widgets.IDisplayAdapter;
import org.eclipse.rap.rwt.lifecycle.IControlAdapter;

/**
 * TODO: [fappel] comment
 * <p>
 * </p>
 */
public abstract class Control extends Widget {

  private final class ControlAdapter implements IControlAdapter {
    public int getIndex() {
      Composite parent = getParent();
      int result = 0;
      if( parent != null ) {
        result = ControlHolder.indexOf( parent, Control.this );
      }
      return result;
    }
  }

  private static final Rectangle EMPTY_RECTANGLE = new Rectangle( 0, 0, 0, 0 );
  
  private final IControlAdapter controlAdapter;
  final Composite parent;
  private Rectangle bounds = EMPTY_RECTANGLE;
  private Object layoutData;
  private String toolTipText;
  private Menu menu;
  private DisposeListener menuDisposeListener;
  private Color foreground;
  private Color background;
  private Font font;

  Control( final Composite parent ) {
    // prevent instantiation from outside this package; only called by Shell
    // and its super-classes
    this.parent = parent;
    controlAdapter = new ControlAdapter();
  }

  public Control( final Composite parent, final int style ) {
    super( parent, style );
    this.parent = parent;
    ControlHolder.addControl( parent, this );
    controlAdapter = new ControlAdapter();
  }

  public final Composite getParent() {
    checkWidget();
    return parent;
  }

  public Shell getShell() {
    checkWidget();
    return parent.getShell();
  }

  public Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }

  
  /////////////
  // Visibility

  public void setVisible( final boolean visible ) {
    checkWidget();
    state = visible ? state & ~HIDDEN : state | HIDDEN;
  }

  public boolean isVisible () {
    checkWidget();
    boolean visible = getVisible() && parent.isVisible();
    return visible;
  }

  public boolean getVisible() {
    checkWidget();
    return ( state & HIDDEN ) == 0;
  }

  /////////////
  // Enablement
  
  public void setEnabled( final boolean enabled ) {
    checkWidget();
    /*
     * TODO [rst] handle focus
     * Feature in Windows.  If the receiver has focus, disabling
     * the receiver causes no window to have focus.  The fix is
     * to assign focus to the first ancestor window that takes
     * focus.  If no window will take focus, set focus to the
     * desktop.
     */
    if( enabled ) {
      state &= ~DISABLED;
    } else {
      state |= DISABLED;
    }
  }

  public boolean getEnabled() {
    checkWidget();
    return ( state & DISABLED ) == 0;
  }

  public boolean isEnabled () {
    checkWidget();
    return getEnabled() && parent.isEnabled();
  }

  /////////
  // Colors

  public void setBackground( final Color color ) {
    checkWidget();
    background = color;
  }
  
  public Color getBackground () {
    checkWidget();
    // Control control = findBackgroundControl ();
    // if (control == null) control = this;
    return background;
  }

  public void setForeground( final Color color ) {
    checkWidget();
    foreground = color;
  }

  public Color getForeground () {
    checkWidget();
    return foreground;
  }
  
  ////////
  // Fonts
  
  public void setFont( final Font font ) {
    checkWidget();
    this.font = font;
  }
  
  public Font getFont() {
    checkWidget();
    Font result;
    if( font == null ) {
      result = getDisplay().getSystemFont();
    } else {
      result = font;
    }
    return result;
  }

  /////////////////
  // Focus handling
  
  public boolean setFocus() {
    checkWidget();
    boolean result = false;
    if( ( style & RWT.NO_FOCUS ) == 0 ) {
      result = forceFocus();
    } 
    return result;
  }

  public boolean forceFocus() {
    checkWidget();
// if (display.focusEvent == SWT.FocusOut) return false;
    Shell shell = getShell(); // was: Decorations shell = menuShell();
    shell.setSavedFocus( this );
    if( !isEnabled() || !isVisible() /* || !isActive() */ ) {
      return false;
    }
    if( isFocusControl() ) {
      return true;
    }
    shell.setSavedFocus( null );
    setFocusControl( this ); // was: OS.SetFocus( handle );
    if( isDisposed() ) {
      return false;
    }
    shell.setSavedFocus( this );
    return isFocusControl();
  }
  
  public boolean isFocusControl() {
    checkWidget();
    return this == getDisplay().getFocusControl();
  }

  boolean setSavedFocus() {
    return forceFocus();
  }

  //////////////////////////////////////////////////////////////////////
  // Methods to manipulate, transform and query the controls' dimensions
  
  public Rectangle getBounds() {
    checkWidget();
    return new Rectangle( bounds );
  }

  public void setBounds( final Rectangle bounds ) {
    checkWidget();
    if( bounds == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    Point oldLocation = getLocation();
    Point oldSize = getSize();
    this.bounds = new Rectangle( bounds );
    this.bounds.width = Math.max( 0, this.bounds.width );
    this.bounds.height = Math.max( 0, this.bounds.height );
    notifyMove( oldLocation );
    notifyResize( oldSize );
  }

  public void setBounds( final int x,
                         final int y,
                         final int width,
                         final int height )
  {
    setBounds( new Rectangle( x, y, width, height ) );
  }

  public void setLocation( final Point location ) {
    if( location == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    Rectangle newBounds = new Rectangle( location.x,
                                         location.y,
                                         bounds.width,
                                         bounds.height );
    setBounds( newBounds );
  }

  public void setLocation( final int x, final int y ) {
    setLocation( new Point( x, y ) );
  }

  public Point getLocation() {
    return new Point( bounds.x, bounds.y );
  }

  public void setSize( final Point size ) {
    if( size == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    setBounds( new Rectangle( bounds.x, bounds.y, size.x, size.y ) );
  }

  public void setSize( final int width, final int height ) {
    setSize( new Point( width, height ) );
  }

  public Point getSize() {
    checkWidget();
    return new Point( bounds.width, bounds.height );
  }

  public Point computeSize( final int wHint, final int hHint ) {
    return computeSize( wHint, hHint, true );
  }

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    // TODO: [fappel] reasonable implementation
    // TODO: [gröver]: copied from swt:
    checkWidget();
    int width = DEFAULT_WIDTH;
    int height = DEFAULT_HEIGHT;
    if( wHint != RWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != RWT.DEFAULT ) {
      height = hHint;
    }
    int border = getBorderWidth();
    width += border * 2;
    height += border * 2;
    return new Point( width, height );
  }

  public void pack() {
    checkWidget();
    pack( true );
  }

  public void pack( final boolean changed ) {
    checkWidget();
    setSize( computeSize( RWT.DEFAULT, RWT.DEFAULT, changed ) );
  }

  public int getBorderWidth() {
    // TODO: [rst] This must be kept in sync with appearances, controls using
    //             different borders must overwrite this mehtod
    checkWidget();
    return ( style & RWT.BORDER ) != 0 ? 2 : 0;
  }

  public Point toDisplay( final int x, final int y ) {
    // TODO: [fappel] doesn't seem to work right, revise this
    checkWidget();
    Rectangle current = new Rectangle( x, y, 0, 0 );
    Rectangle result = getDisplay().map( this, null, current );
    return new Point( result.x, result.y );
  }

  public Point toDisplay( final Point point ) {
    checkWidget();
    if( point == null ) {
      error( RWT.ERROR_NULL_ARGUMENT );
    }
    return toDisplay( point.x, point.y );
  }
  
  //////////////////////////
  // Layout related methods
  
  public Object getLayoutData() {
    checkWidget();
    return layoutData;
  }

  public void setLayoutData( final Object layoutData ) {
    checkWidget();
    this.layoutData = layoutData;
  }
  

  //////////////////////
  // ToolTip operations
  
  public void setToolTipText( final String toolTipText ) {
    this.toolTipText = toolTipText;
  }

  public String getToolTipText() {
    return toolTipText;
  }
  

  ///////////////////
  // Menu operations
  
  public void setMenu( final Menu menu ) {
    if( this.menu != menu ) {
      if( menu != null ) {
        if( menu.isDisposed() ) {
          RWT.error( RWT.ERROR_INVALID_ARGUMENT );
        }
        if( ( menu.getStyle() & RWT.POP_UP ) == 0 ) {
          RWT.error( RWT.ERROR_MENU_NOT_POP_UP );
        }
        if( menu.getParent() != getShell() ) {
          RWT.error( RWT.ERROR_INVALID_PARENT );
        }
      }
      removeMenuDisposeListener();
      this.menu = menu;
      addMenuDisposeListener();
    }
  }

  public Menu getMenu() {
    return menu;
  }
  
  //////////
  // Z-Order
  
  public void moveAbove( final Control control ) {
    checkWidget();
    if( control != null && control.isDisposed() ) {
      error( RWT.ERROR_INVALID_ARGUMENT );
    }
    if( control == null || control.parent == parent && control != this ) {
      ControlHolder.removeControl( getParent(), this );
      int index = 0;
      if( control != null ) {
        index = ControlHolder.indexOf( getParent(), control );
      }
      ControlHolder.addControl( getParent(), this, index );
    }
  }

  public void moveBelow( final Control control ) {
    checkWidget();
    if( control != null && control.isDisposed() ) {
      error( RWT.ERROR_INVALID_ARGUMENT );
    }
    if( control == null || control.parent == parent && control != this ) {
      ControlHolder.removeControl( getParent(), this );
      int index = ControlHolder.size( getParent() );
      if( control != null ) {
        index = ControlHolder.indexOf( getParent(), control ) + 1;
      }
      ControlHolder.addControl( getParent(), this, index );
    }
  }

  public Object getAdapter( final Class adapter ) {
    Object result = null;
    if( adapter == IControlAdapter.class ) {
      result = controlAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  //////////////////////////////////
  // Methods to add/remove listener
  
  public void addControlListener( final ControlListener listener ) {
    ControlEvent.addListener( this, listener );
  }

  public void removeControlListener( final ControlListener listener ) {
    ControlEvent.removeListener( this, listener );
  }
  
  public void addFocusListener( final FocusListener listener ) {
    FocusEvent.addListener( this, listener );
  }
  
  public void removeFocusListener( final FocusListener listener ) {
    FocusEvent.removeListener( this, listener );
  }
  
  ////////////
  // Disposal
  
  protected void releaseParent() {
    if( getParent() != null ) {
      getParent().removeControl( this );
    }
  }

  protected void releaseWidget() {
    if( menu != null ) {
      removeMenuDisposeListener();
      menu.dispose();
      menu = null;
    }
    if( getDisplay().getFocusControl() == this ) {
      Control focusControl = null;
      Control parent = getParent();
      while( focusControl == null && parent != null ) {
        if( !parent.isDisposed() ) {
          focusControl = parent;
        } else {
          parent = parent.getParent();
        }
      }
      setFocusControl( focusControl );
    }
  }

  protected void releaseChildren() {
    // do nothing
  }

  
  /////////////////////////////////////////////////////
  // Helping methods that throw move- and resize-events
  
  void notifyResize( final Point oldSize ) {
    if( !oldSize.equals( getSize() ) ) {
      new ControlEvent( this, ControlEvent.CONTROL_RESIZED ).processEvent();
    }
  }

  void notifyMove( final Point oldLocation ) {
    if( !oldLocation.equals( getLocation() ) ) {
      new ControlEvent( this, ControlEvent.CONTROL_MOVED ).processEvent();
    }
  }

  /////////////////////////////////////////////////////
  // Helping method to set the focus control on display
  
  private void setFocusControl( final Control control ) {
    Object adapter = getDisplay().getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setFocusControl( control );
  }

  ///////////////////////////////////////////////////////
  // Helping methods to observe the disposal of the menu
  
  private void addMenuDisposeListener() {
    if( menu != null ) {
      if( menuDisposeListener == null ) {
        menuDisposeListener = new DisposeListener() {

          public void widgetDisposed( final DisposeEvent event ) {
            Control.this.menu = null;
          }
        };
      }
      menu.addDisposeListener( menuDisposeListener );
    }
  }

  private void removeMenuDisposeListener() {
    if( menu != null ) {
      menu.removeDisposeListener( menuDisposeListener );
    }
  }
}
