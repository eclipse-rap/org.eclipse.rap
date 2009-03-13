/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ShowEvent;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;


/**
 * Control is the abstract superclass of all windowed user interface classes.
 * <p>
 * <dl>
 * <dt><b>Styles:</b>
 * <dd>BORDER</dd>
 * <dd>LEFT_TO_RIGHT <!--, RIGHT_TO_LEFT --></dd>
 * <dt><b>Events:</b>
 * <dd>FocusIn, FocusOut, Help, KeyDown, KeyUp, MouseDoubleClick, MouseDown, MouseEnter,
 *     MouseExit, MouseHover, MouseUp, MouseMove, Move, Paint, Resize, Traverse,
 *     DragDetect, MenuDetect</dd>
 * </dl>
 * </p><p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 *
 * @since 1.0
 */
public abstract class Control extends Widget {

  private final class ControlAdapter implements IControlAdapter {

    private int tabIndex = -1;

    public int getZIndex() {
      Composite parent = getParent();
      int result = 0;
      if( parent != null ) {
        result = ControlHolder.indexOf( parent, Control.this );
      }
      return result;
    }

    public Font getUserFont() {
      return font;
    }

    public Color getUserForeground() {
      return foreground;
    }

    public Color getUserBackground() {
      return background;
    }

    public Image getUserBackgroundImage() {
      return backgroundImage;
    }

    public boolean getBackgroundTransparency() {
      return backgroundTransparency;
    }

    public int getTabIndex() {
      return tabIndex;
    }

    public void setTabIndex( final int index ) {
      tabIndex = index;
    }
  }

  private static final Rectangle EMPTY_RECTANGLE = new Rectangle( 0, 0, 0, 0 );

  private final IControlAdapter controlAdapter;
  final Composite parent;
  Rectangle bounds = EMPTY_RECTANGLE;
  private Object layoutData;
  private String toolTipText;
  private Menu menu;
  private DisposeListener menuDisposeListener;
  private Color foreground;
  private Color background;
  private Image backgroundImage;
  private boolean backgroundTransparency;
  private Font font;
  private Cursor cursor;

  Control( final Composite parent ) {
    // prevent instantiation from outside this package; only called by Shell
    // and its super-classes
    this.parent = parent;
    controlAdapter = new ControlAdapter();
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
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#BORDER
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Control( final Composite parent, final int style ) {
    super( parent, style );
    this.parent = parent;
    ControlHolder.addControl( parent, this );
    controlAdapter = new ControlAdapter();
    createWidget();
  }

  void createWidget () {
    initState();
    checkOrientation( parent );
    checkBackground();
    updateBackground();
  }

  void initState() {
    // by default let states empty
  }

  /**
   * Returns the receiver's parent, which must be a <code>Composite</code>
   * or null when the receiver is a shell that was created with null or
   * a display for a parent.
   *
   * @return the receiver's parent
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Composite getParent() {
    checkWidget();
    return parent;
  }

  /**
   * Returns the receiver's shell. For all controls other than
   * shells, this simply returns the control's nearest ancestor
   * shell. Shells return themselves, even if they are children
   * of other shells.
   *
   * @return the receiver's shell
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #getParent
   */
  public Shell getShell() {
    checkWidget();
    return parent.getShell();
  }

  /**
   * Returns the receiver's monitor.
   * 
   * @return the receiver's monitor
   * 
   * @since 1.2
   */
  public Monitor getMonitor() {
    checkWidget();
    return display.getPrimaryMonitor();
  }
  
  //////////////
  // Visibility

  /**
   * Marks the receiver as visible if the argument is <code>true</code>,
   * and marks it invisible otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some
   * other condition makes the receiver not visible, marking
   * it visible may not actually cause it to be displayed.
   * </p>
   *
   * @param visible the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setVisible( final boolean visible ) {
    checkWidget();
    if( ( ( state & HIDDEN ) != 0 ) != !visible ) {
      if( visible ) {
        ShowEvent event = new ShowEvent( this, ShowEvent.SHOWN );
        event.processEvent();
      }
      Control control = null;
      boolean fixFocus = false;
      if( !visible ) {
        control = display.getFocusControl();
        fixFocus = isFocusAncestor( control );
      }
      state = visible ? state & ~HIDDEN : state | HIDDEN;
      if( !visible ) {
        ShowEvent event = new ShowEvent( this, ShowEvent.HIDDEN );
        event.processEvent();
      }
      if( fixFocus ) {
        fixFocus( control );
      }
    } 
  }

  /**
   * Returns <code>true</code> if the receiver is visible and all
   * ancestors up to and including the receiver's nearest ancestor
   * shell are visible. Otherwise, <code>false</code> is returned.
   *
   * @return the receiver's visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #getVisible
   */
  public boolean isVisible() {
    checkWidget();
    return getVisible() && parent.isVisible();
  }

  /**
   * Returns <code>true</code> if the receiver is visible, and
   * <code>false</code> otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some
   * other condition makes the receiver not visible, this method
   * may still indicate that it is considered visible even though
   * it may not actually be showing.
   * </p>
   *
   * @return the receiver's visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public boolean getVisible() {
    checkWidget();
    return ( state & HIDDEN ) == 0;
  }

  //////////////
  // Enablement

  /**
   * Enables the receiver if the argument is <code>true</code>,
   * and disables it otherwise. A disabled control is typically
   * not selectable from the user interface and draws with an
   * inactive or "grayed" look.
   *
   * @param enabled the new enabled state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
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

  /**
   * Returns <code>true</code> if the receiver is enabled, and
   * <code>false</code> otherwise. A disabled control is typically
   * not selectable from the user interface and draws with an
   * inactive or "grayed" look.
   *
   * @return the receiver's enabled state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see #isEnabled
   */
  public boolean getEnabled() {
    checkWidget();
    return ( state & DISABLED ) == 0;
  }

  /**
   * Returns <code>true</code> if the receiver is enabled and all
   * ancestors up to and including the receiver's nearest ancestor
   * shell are enabled.  Otherwise, <code>false</code> is returned.
   * A disabled control is typically not selectable from the user
   * interface and draws with an inactive or "grayed" look.
   *
   * @return the receiver's enabled state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #getEnabled
   */
  public boolean isEnabled() {
    checkWidget();
    return getEnabled() && parent.isEnabled();
  }

  /////////
  // Colors

  /**
   * Sets the receiver's background color to the color specified
   * by the argument, or to the default system color for the control
   * if the argument is null.
   *
   * @param color the new color (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setBackground( final Color color ) {
    checkWidget();
    background = color;
    updateBackground();
  }

  /**
   * Returns the receiver's background color.
   *
   * @return the background color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Color getBackground() {
    checkWidget();
    Control control = findBackgroundControl();
    if( control == null ) {
      control = this;
    }
    Color result = control.background;
    if( result == null ) {
      IControlThemeAdapter adapter = getControlThemeAdapter( control.getClass() );
      result = adapter.getBackground( control );
    }
    Shell shell = control.getShell();
    control = control.parent;
    while( result == null && control != null ) {
      result = control.getBackground();
      control = control == shell ? null : control.parent;
    }
    if( result == null ) {
      // Should never happen as the theming must prevent transparency for
      // shell background colors
      throw new IllegalStateException( "Transparent shell background color" );
    }
    return result;
  }

  /**
   * Sets the receiver's background image to the image specified
   * by the argument, or to the default system color for the control
   * if the argument is null.  The background image is tiled to fill
   * the available space.
   * <p>
   * Note: This operation is a hint and may be overridden by the platform.
   * For example, on Windows the background of a Button cannot be changed.
   * </p>
   * @param image the new image (or null)
   *
   * <!--@exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument is not a bitmap</li>
   * </ul>-->
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.1
   */
  public void setBackgroundImage( final Image image ) {
    checkWidget();
    if( image != null ) {
//      if( image.isDisposed() )
//        error( SWT.ERROR_INVALID_ARGUMENT );
//      if( image.type != SWT.BITMAP )
//        error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( backgroundImage != image ) {
      backgroundImage = image;
    }
  }

  /**
   * Returns the receiver's background image.
   *
   * @return the background image
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.1
   */
  public Image getBackgroundImage() {
    checkWidget();
    Control control = findBackgroundControl();
    if( control == null ) {
      control = this;
    }
    return control.backgroundImage;
  }

  /**
   * Sets the receiver's foreground color to the color specified
   * by the argument, or to the default system color for the control
   * if the argument is null.
   *
   * @param color the new color (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public void setForeground( final Color color ) {
    checkWidget();
    foreground = color;
  }

  /**
   * Returns the foreground color that the receiver will use to draw.
   *
   * @return the receiver's foreground color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public Color getForeground() {
    checkWidget();
    Color result = foreground;
    if( result == null ) {
      IControlThemeAdapter adapter = getControlThemeAdapter();
      result = adapter.getForeground( this );
    }
    if( result == null ) {
      // Should never happen as the theming must prevent transparency for
      // foreground colors
      throw new IllegalStateException( "Transparent foreground color" );
    }
    return result;
  }

  void updateBackgroundMode() {
    int oldState = state & PARENT_BACKGROUND;
    checkBackground();
    if( oldState != ( state & PARENT_BACKGROUND ) ) {
      updateBackground();
    }
  }

  /*
   * Checks whether parent background should be applied to this control and and
   * sets PARENT_BACKGROUND state if so.
   */
  // verbatim copy of SWT 3.4.0 GTK
  private void checkBackground() {
    Shell shell = getShell();
    if( this == shell ) {
      return;
    }
    state &= ~PARENT_BACKGROUND;
    Composite composite = parent;
    do {
      int mode = composite.backgroundMode;
      if( mode != 0 ) {
        if( mode == SWT.INHERIT_DEFAULT ) {
          Control control = this;
          do {
            if( ( control.state & THEME_BACKGROUND ) == 0 ) {
              return;
            }
            control = control.parent;
          } while( control != composite );
        }
        state |= PARENT_BACKGROUND;
        return;
      }
      if( composite == shell ) {
        break;
      }
      composite = composite.parent;
    } while( true );
  }

  /**
   * Applies the background according to PARENT_BACKGROUND state.
   */
  private void updateBackground() {
    backgroundTransparency =    background == null
                             && backgroundImage == null
                             && ( state & PARENT_BACKGROUND ) != 0;
  }

  Control findBackgroundControl() {
    Control result = null;
    if( background != null || backgroundImage != null ) {
      result = this;
    } else if( ( state & PARENT_BACKGROUND ) != 0 ) {
      result = parent.findBackgroundControl();
    }
    return result;
  }

  /////////
  // Fonts

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
    checkWidget();
    this.font = font;
  }

  /**
   * Returns the font that the receiver will use to paint textual information.
   *
   * @return the receiver's font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public Font getFont() {
    checkWidget();
    Font result = font;
    if( result == null ) {
      IControlThemeAdapter adapter = getControlThemeAdapter();
      result = adapter.getFont( this );
    }
    return result;
  }

  /////////
  // Cursors

  /**
   * Sets the receiver's cursor to the cursor specified by the
   * argument, or to the default cursor for that kind of control
   * if the argument is null.
   * <p>
   * When the mouse pointer passes over a control its appearance
   * is changed to match the control's cursor.
   * </p>
   *
   * @param cursor the new cursor (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.2
   */
  public void setCursor( final Cursor cursor ) {
    checkWidget();
    this.cursor = cursor;
  }

  /**
   * Returns the receiver's cursor, or null if it has not been set.
   * <p>
   * When the mouse pointer passes over a control its appearance
   * is changed to match the control's cursor.
   * </p>
   *
   * @return the receiver's cursor or <code>null</code>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.2
   */
  public Cursor getCursor() {
    checkWidget();
    return cursor;
  }

  //////////////////
  // Focus handling

  /**
   * Causes the receiver to have the <em>keyboard focus</em>,
   * such that all keyboard events will be delivered to it.  Focus
   * reassignment will respect applicable platform constraints.
   *
   * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see #forceFocus
   */
  public boolean setFocus() {
    checkWidget();
    boolean result = false;
    if( ( style & SWT.NO_FOCUS ) == 0 ) {
      result = forceFocus();
    }
    return result;
  }

  /**
   * Forces the receiver to have the <em>keyboard focus</em>, causing
   * all keyboard events to be delivered to it.
   *
   * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see #setFocus
   */
  public boolean forceFocus() {
    checkWidget();
    // if (display.focusEvent == SWT.FocusOut) return false;
    Shell shell = getShell(); // was: Decorations shell = menuShell();
    shell.setSavedFocus( this );
    if( !isEnabled() || !isVisible() /* || !isActive() */) {
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

  /**
   * Returns <code>true</code> if the receiver has the user-interface
   * focus, and <code>false</code> otherwise.
   *
   * @return the receiver's focus state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public boolean isFocusControl() {
    checkWidget();
    return this == getDisplay().getFocusControl();
  }

  boolean setSavedFocus() {
    return forceFocus();
  }

  ///////////////////////////////////////////////////////////////////////
  // Methods to manipulate, transform and query the controls' dimensions

  /**
   * Returns a rectangle describing the receiver's size and location
   * relative to its parent (or its display if its parent is null),
   * unless the receiver is a shell. In this case, the location is
   * relative to the display.
   *
   * @return the receiver's bounding rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public Rectangle getBounds() {
    checkWidget();
    return new Rectangle( bounds.x, bounds.y, bounds.width, bounds.height );
  }

  /**
   * Sets the receiver's size and location to the rectangular
   * area specified by the argument. The <code>x</code> and
   * <code>y</code> fields of the rectangle are relative to
   * the receiver's parent (or its display if its parent is null).
   * <p>
   * Note: Attempting to set the width or height of the
   * receiver to a negative number will cause that
   * value to be set to zero instead.
   * </p>
   *
   * @param bounds the new bounds for the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public void setBounds( final Rectangle bounds ) {
    checkWidget();
    if( bounds == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    Point oldLocation = getLocation();
    Point oldSize = getSize();
    this.bounds
      = new Rectangle( bounds.x, bounds.y, bounds.width, bounds.height );
    this.bounds.width = Math.max( 0, this.bounds.width );
    this.bounds.height = Math.max( 0, this.bounds.height );
    notifyMove( oldLocation );
    notifyResize( oldSize );
  }

  /**
   * Sets the receiver's size and location to the rectangular
   * area specified by the arguments. The <code>x</code> and
   * <code>y</code> arguments are relative to the receiver's
   * parent (or its display if its parent is null), unless
   * the receiver is a shell. In this case, the <code>x</code>
   * and <code>y</code> arguments are relative to the display.
   * <p>
   * Note: Attempting to set the width or height of the
   * receiver to a negative number will cause that
   * value to be set to zero instead.
   * </p>
   *
   * @param x the new x coordinate for the receiver
   * @param y the new y coordinate for the receiver
   * @param width the new width for the receiver
   * @param height the new height for the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public void setBounds( final int x,
                         final int y,
                         final int width,
                         final int height )
  {
    setBounds( new Rectangle( x, y, width, height ) );
  }

  /**
   * Sets the receiver's location to the point specified by
   * the arguments which are relative to the receiver's
   * parent (or its display if its parent is null), unless
   * the receiver is a shell. In this case, the point is
   * relative to the display.
   *
   * @param location the new location for the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public void setLocation( final Point location ) {
    if( location == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    Rectangle newBounds = new Rectangle( location.x,
                                         location.y,
                                         bounds.width,
                                         bounds.height );
    setBounds( newBounds );
  }

  /**
   * Sets the receiver's location to the point specified by
   * the arguments which are relative to the receiver's
   * parent (or its display if its parent is null), unless
   * the receiver is a shell. In this case, the point is
   * relative to the display.
   *
   * @param x the new x coordinate for the receiver
   * @param y the new y coordinate for the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public void setLocation( final int x, final int y ) {
    setLocation( new Point( x, y ) );
  }

  /**
   * Returns a point describing the receiver's location relative
   * to its parent (or its display if its parent is null), unless
   * the receiver is a shell. In this case, the point is
   * relative to the display.
   *
   * @return the receiver's location
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public Point getLocation() {
    checkWidget();
    return new Point( bounds.x, bounds.y );
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
   *                <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public void setSize( final Point size ) {
    if( size == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    setBounds( new Rectangle( bounds.x, bounds.y, size.x, size.y ) );
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
   *                </ul>
   */
  public void setSize( final int width, final int height ) {
    setSize( new Point( width, height ) );
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
   *                </ul>
   */
  public Point getSize() {
    checkWidget();
    return new Point( bounds.width, bounds.height );
  }

  /**
   * Returns the preferred size of the receiver.
   * <p>
   * The <em>preferred size</em> of a control is the size that it would
   * best be displayed at. The width hint and height hint arguments
   * allow the caller to ask a control questions such as "Given a particular
   * width, how high does the control need to be to show all of the contents?"
   * To indicate that the caller does not wish to constrain a particular
   * dimension, the constant <code>SWT.DEFAULT</code> is passed for the hint.
   * </p>
   *
   * @param wHint the width hint (can be <code>SWT.DEFAULT</code>)
   * @param hHint the height hint (can be <code>SWT.DEFAULT</code>)
   * @return the preferred size of the control
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see Layout
   * @see #getBorderWidth
   * @see #getBounds
   * @see #getSize
   * @see #pack(boolean)
   * @see "computeTrim, getClientArea for controls that implement them"
   */
  public Point computeSize( final int wHint, final int hHint ) {
    return computeSize( wHint, hHint, true );
  }

  /**
   * Returns the preferred size of the receiver.
   * <p>
   * The <em>preferred size</em> of a control is the size that it would
   * best be displayed at. The width hint and height hint arguments
   * allow the caller to ask a control questions such as "Given a particular
   * width, how high does the control need to be to show all of the contents?"
   * To indicate that the caller does not wish to constrain a particular
   * dimension, the constant <code>SWT.DEFAULT</code> is passed for the hint.
   * </p><p>
   * If the changed flag is <code>true</code>, it indicates that the receiver's
   * <em>contents</em> have changed, therefore any caches that a layout manager
   * containing the control may have been keeping need to be flushed. When the
   * control is resized, the changed flag will be <code>false</code>, so layout
   * manager caches can be retained.
   * </p>
   *
   * @param wHint the width hint (can be <code>SWT.DEFAULT</code>)
   * @param hHint the height hint (can be <code>SWT.DEFAULT</code>)
   * @param changed <code>true</code> if the control's contents have changed, and <code>false</code> otherwise
   * @return the preferred size of the control.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see Layout
   * @see #getBorderWidth
   * @see #getBounds
   * @see #getSize
   * @see #pack(boolean)
   * @see "computeTrim, getClientArea for controls that implement them"
   */
  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int width = DEFAULT_WIDTH;
    int height = DEFAULT_HEIGHT;
    if( wHint != SWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
      height = hHint;
    }
    int border = getBorderWidth();
    width += border * 2;
    height += border * 2;
    return new Point( width, height );
  }

  /**
   * Causes the receiver to be resized to its preferred size.
   * For a composite, this involves computing the preferred size
   * from its layout, if there is one.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see #computeSize(int, int, boolean)
   */
  public void pack() {
    checkWidget();
    pack( true );
  }

  /**
   * Causes the receiver to be resized to its preferred size.
   * For a composite, this involves computing the preferred size
   * from its layout, if there is one.
   * <p>
   * If the changed flag is <code>true</code>, it indicates that the receiver's
   * <em>contents</em> have changed, therefore any caches that a layout manager
   * containing the control may have been keeping need to be flushed. When the
   * control is resized, the changed flag will be <code>false</code>, so layout
   * manager caches can be retained.
   * </p>
   *
   * @param changed whether or not the receiver's contents have changed
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see #computeSize(int, int, boolean)
   */
  public void pack( final boolean changed ) {
    checkWidget();
    setSize( computeSize( SWT.DEFAULT, SWT.DEFAULT, changed ) );
  }

  /**
   * Returns the receiver's border width.
   *
   * @return the border width
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public int getBorderWidth() {
    checkWidget();
    IControlThemeAdapter adapter = getControlThemeAdapter();
    return adapter.getBorderWidth( this );
  }

  /**
   * Returns a point which is the result of converting the
   * argument, which is specified in display relative coordinates,
   * to coordinates relative to the receiver.
   * <p>
   * @param x the x coordinate to be translated
   * @param y the y coordinate to be translated
   * @return the translated coordinates
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public Point toControl( final int x, final int y ) {
    checkWidget();
    return getDisplay().map( null, this, x, y );
  }

  /**
   * Returns a point which is the result of converting the
   * argument, which is specified in display relative coordinates,
   * to coordinates relative to the receiver.
   * <p>
   * @param point the point to be translated (must not be null)
   * @return the translated coordinates
   *
   * @exception IllegalArgumentException <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public Point toControl( final Point point ) {
    checkWidget();
    if( point == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    return toControl( point.x, point.y );
  }

  /**
   * Returns a point which is the result of converting the
   * argument, which is specified in coordinates relative to
   * the receiver, to display relative coordinates.
   * <p>
   * @param x the x coordinate to be translated
   * @param y the y coordinate to be translated
   * @return the translated coordinates
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Point toDisplay( final int x, final int y ) {
    checkWidget();
    return getDisplay().map( this, null, x, y );
  }

  /**
   * Returns a point which is the result of converting the
   * argument, which is specified in coordinates relative to
   * the receiver, to display relative coordinates.
   * <p>
   * @param point the point to be translated (must not be null)
   * @return the translated coordinates
   *
   * @exception IllegalArgumentException <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public Point toDisplay( final Point point ) {
    checkWidget();
    if( point == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    return toDisplay( point.x, point.y );
  }

  ///////////////////////////
  // Layout related methods

  /**
   * Returns layout data which is associated with the receiver.
   *
   * @return the receiver's layout data
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public Object getLayoutData() {
    checkWidget();
    return layoutData;
  }

  /**
   * Sets the layout data associated with the receiver to the argument.
   *
   * @param layoutData the new layout data for the receiver.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public void setLayoutData( final Object layoutData ) {
    checkWidget();
    this.layoutData = layoutData;
  }

  void markLayout( final boolean changed, final boolean all ) {
    /* Do nothing */
  }

  void updateLayout( final boolean resize, final boolean all ) {
    /* Do nothing */
  }

  /////////////////////
  // ToolTip operations

  /**
   * Sets the receiver's tool tip text to the argument, which
   * may be null indicating that no tool tip text should be shown.
   *
   * @param toolTipText the new tool tip text (or null)
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setToolTipText( final String toolTipText ) {
    checkWidget();
    this.toolTipText = toolTipText;
  }

  /**
   * Returns the receiver's tool tip text, or null if it has
   * not been set.
   *
   * @return the receiver's tool tip text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getToolTipText() {
    checkWidget();
    return toolTipText;
  }

  ///////////////////
  // Menu operations

  /**
   * Sets the receiver's pop up menu to the argument.
   * All controls may optionally have a pop up
   * menu that is displayed when the user requests one for
   * the control. The sequence of key strokes, button presses
   * and/or button releases that are used to request a pop up
   * menu is platform specific.
   * <p>
   * Note: Disposing of a control that has a pop up menu will
   * dispose of the menu.  To avoid this behavior, set the
   * menu to null before the control is disposed.
   * </p>
   *
   * @param menu the new pop up menu
   *
   * @exception IllegalArgumentException <ul>
   *                <li>ERROR_MENU_NOT_POP_UP - the menu is not a pop up menu</li>
   *    <li>ERROR_INVALID_PARENT - if the menu is not in the same widget tree</li>
   *                <li>ERROR_INVALID_ARGUMENT - if the menu has been disposed</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public void setMenu( final Menu menu ) {
    checkWidget();
    if( this.menu != menu ) {
      if( menu != null ) {
        if( menu.isDisposed() ) {
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
        }
        if( ( menu.getStyle() & SWT.POP_UP ) == 0 ) {
          SWT.error( SWT.ERROR_MENU_NOT_POP_UP );
        }
        if( menu.getParent() != getShell() ) {
          SWT.error( SWT.ERROR_INVALID_PARENT );
        }
      }
      removeMenuDisposeListener();
      this.menu = menu;
      addMenuDisposeListener();
    }
  }

  /**
   * Returns the receiver's pop up menu if it has one, or null
   * if it does not. All controls may optionally have a pop up
   * menu that is displayed when the user requests one for
   * the control. The sequence of key strokes, button presses
   * and/or button releases that are used to request a pop up
   * menu is platform specific.
   *
   * @return the receiver's menu
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   */
  public Menu getMenu() {
    checkWidget();
    return menu;
  }

  ///////////
  // Z-Order

  /**
   * Moves the receiver above the specified control in the
   * drawing order. If the argument is null, then the receiver
   * is moved to the top of the drawing order. The control at
   * the top of the drawing order will not be covered by other
   * controls even if they occupy intersecting areas.
   *
   * @param control the sibling control (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see Control#moveBelow
   * @see Composite#getChildren
   */
  public void moveAbove( final Control control ) {
    checkWidget();
    if( control != null && control.isDisposed() ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
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

  /**
   * Moves the receiver below the specified control in the
   * drawing order. If the argument is null, then the receiver
   * is moved to the bottom of the drawing order. The control at
   * the bottom of the drawing order will be covered by all other
   * controls which occupy intersecting areas.
   *
   * @param control the sibling control (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see Control#moveAbove
   * @see Composite#getChildren
   */
  public void moveBelow( final Control control ) {
    checkWidget();
    if( control != null && control.isDisposed() ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
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

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the control is moved or resized, by sending
   * it one of the messages defined in the <code>ControlListener</code>
   * interface.
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see ControlListener
   * @see #removeControlListener
   */
  public void addControlListener( final ControlListener listener ) {
    ControlEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the control is moved or resized.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see ControlListener
   * @see #addControlListener
   */
  public void removeControlListener( final ControlListener listener ) {
    ControlEvent.removeListener( this, listener );
  }

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when mouse buttons are pressed and released, by sending
   * it one of the messages defined in the <code>MouseListener</code>
   * interface.
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
   * @see MouseListener
   * @see #removeMouseListener
   *
   * @since 1.1
   */
  public void addMouseListener( final MouseListener listener ) {
    MouseEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when mouse buttons are pressed and released.
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
   * @see MouseListener
   * @see #addMouseListener
   *
   * @since 1.1
   */
  public void removeMouseListener( final MouseListener listener ) {
    MouseEvent.removeListener( this, listener );
  }

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when keys are pressed and released on the system keyboard, by sending
   * it one of the messages defined in the <code>KeyListener</code>
   * interface.
   * <!--
   * TODO [rh] investigate whether this statements is true in RWT as well
   * <p>
   * When a key listener is added to a control, the control
   * will take part in widget traversal.  By default, all
   * traversal keys (such as the tab key and so on) are
   * delivered to the control.  In order for a control to take
   * part in traversal, it should listen for traversal events.
   * Otherwise, the user can traverse into a control but not
   * out.  Note that native controls such as table and tree
   * implement key traversal in the operating system.  It is
   * not necessary to add traversal listeners for these controls,
   * unless you want to override the default traversal.
   * </p>
   * -->
   * <!-- RAP specific -->
   * <p>
   * <strong>Note:</strong> the key events in RWT are not meant for 
   * general purpose.  
   * </p>
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
   * @see KeyListener
   * @see #removeKeyListener
   * 
   * @since 1.2
   */
  public void addKeyListener( final KeyListener listener ) {
    checkWidget();
    KeyEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when keys are pressed and released on the system keyboard.
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
   * @see KeyListener
   * @see #addKeyListener
   * 
   * @since 1.2
   */
  public void removeKeyListener( final KeyListener listener ) {
    checkWidget();
    KeyEvent.removeListener( this, listener );
  }
  
  /**
   * Adds the listener to the collection of listeners who will
   * be notified when traversal events occur, by sending it
   * one of the messages defined in the <code>TraverseListener</code>
   * interface.
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
   * @see TraverseListener
   * @see #removeTraverseListener
   * 
   * @since 1.2
   */
  public void addTraverseListener( final TraverseListener listener ) {
    checkWidget();
    TraverseEvent.addListener( this, listener );
  }
  
  /**
   * Removes the listener from the collection of listeners who will
   * be notified when traversal events occur.
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
   * @see TraverseListener
   * @see #addTraverseListener
   * 
   * @since 1.2
   */
  public void removeTraverseListener( final TraverseListener listener ) {
    checkWidget();
    TraverseEvent.removeListener( this, listener );
  }

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the control gains or loses focus, by sending
   * it one of the messages defined in the <code>FocusListener</code>
   * interface.
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see FocusListener
   * @see #removeFocusListener
   */
  public void addFocusListener( final FocusListener listener ) {
    FocusEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the control gains or loses focus.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *                </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *                </ul>
   *
   * @see FocusListener
   * @see #addFocusListener
   */
  public void removeFocusListener( final FocusListener listener ) {
    FocusEvent.removeListener( this, listener );
  }

  ////////////////
  // drawing (Note that we can't really force a redraw. This is just a
  // fake to for event notifications that come on OS systems
  // with redraws)

  /**
   * If the argument is <code>false</code>, causes subsequent drawing
   * operations in the receiver to be ignored. No drawing of any kind
   * can occur in the receiver until the flag is set to true.
   * Graphics operations that occurred while the flag was
   * <code>false</code> are lost. When the flag is set to <code>true</code>,
   * the entire widget is marked as needing to be redrawn.  Nested calls
   * to this method are stacked.
   * <p>
   * Note: This operation is a hint and may not be supported on some
   * platforms or for some widgets.
   * </p>
   * <p>
   * Note: With RAP we can't really force a redraw. This is just a
   *       fake to enable event notifications that come on OS systems
   *       with redraws.
   * </p>
   *
   * @param redraw the new redraw state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setRedraw( final boolean redraw ) {
    checkWidget();
    RWTLifeCycle.fakeRedraw( this, redraw );
  }

  /**
   * Causes the entire bounds of the receiver to be marked
   * as needing to be redrawn.
   *
   * <p>
   * Note: With RAP we can't really force a redraw. This is just a
   *       fake to enable event notifications that come on OS systems
   *       with redraws.
   * </p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void redraw() {
    checkWidget();
    setRedraw( true );
  }

  ////////////
  // Disposal

  void releaseParent() {
    if( getParent() != null ) {
      getParent().removeControl( this );
    }
  }

  void releaseWidget() {
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
    RWTLifeCycle.fakeRedraw( this, false );
    super.releaseWidget();
  }

  /////////////
  // Tab order

  boolean isTabGroup() {
    boolean result = false;
    Control[] tabList = parent._getTabList();
    if( tabList != null ) {
      for( int i = 0; i < tabList.length; i++ ) {
        if( tabList[ i ] == this ) {
          result = true;
        }
      }
    }
    return result;
  }

  //////////////////////////////////////////////////////
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

  ////////////////////////
  // Focus helping methods

  private void setFocusControl( final Control control ) {
    // focus
    Object adapter = getDisplay().getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setFocusControl( control );
    // active
    Shell shell = getShell();
    shell.setActiveControl( control );
  }

  Control[] getPath() {
    int count = 0;
    Shell shell = getShell();
    Control control = this;
    while( control != shell ) {
      count++;
      control = control.parent;
    }
    control = this;
    Control[] result = new Control[ count ];
    while( control != shell ) {
      result[ --count ] = control;
      control = control.parent;
    }
    return result;
  }

  // Copied from SWT/win32 as is
  boolean isFocusAncestor (Control control) {
    while (control != null && control != this && !(control instanceof Shell)) {
      control = control.parent;
    }
    return control == this;
  }

  // Copied from SWT/win32 as is
  void fixFocus (Control focusControl) {
    Shell shell = getShell ();
    Control control = this;
    while (control != shell && (control = control.parent) != null) {
      if (control.setFixedFocus ()) return;
    }
    shell.setSavedFocus (focusControl);
//    OS.SetFocus (0);
    // Replacement for OS.setFocus( 0 )
    IDisplayAdapter adapter
      = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    adapter.setFocusControl( null );
  }

  // Copied from SWT/win32 as is
  boolean setFixedFocus () {
    if ((style & SWT.NO_FOCUS) != 0) return false;
    return forceFocus ();
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

  private IControlThemeAdapter getControlThemeAdapter() {
    return getControlThemeAdapter( this.getClass() );
  }

  private IControlThemeAdapter getControlThemeAdapter(
    final Class controlClass )
  {
    ThemeManager themeMgr = ThemeManager.getInstance();
    return ( IControlThemeAdapter )themeMgr.getThemeAdapter( controlClass );
  }
}
