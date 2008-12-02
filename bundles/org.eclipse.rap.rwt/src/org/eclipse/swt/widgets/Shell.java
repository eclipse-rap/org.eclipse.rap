/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.rwt.internal.theme.IThemeAdapter;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.shellkit.ShellThemeAdapter;

/**
 * Instances of this class represent the "windows"
 * which the desktop or "window manager" is managing.
 * Instances that do not have a parent (that is, they
 * are built using the constructor, which takes a
 * <code>Display</code> as the argument) are described
 * as <em>top level</em> shells. Instances that do have
 * a parent are described as <em>secondary</em> or
 * <em>dialog</em> shells.
 * <p>
 * Instances are always displayed in one of the maximized,
 * minimized or normal states:
 * <ul>
 * <li>
 * When an instance is marked as <em>maximized</em>, the
 * window manager will typically resize it to fill the
 * entire visible area of the display, and the instance
 * is usually put in a state where it can not be resized
 * (even if it has style <code>RESIZE</code>) until it is
 * no longer maximized.
 * </li><li>
 * When an instance is in the <em>normal</em> state (neither
 * maximized or minimized), its appearance is controlled by
 * the style constants which were specified when it was created
 * and the restrictions of the window manager (see below).
 * </li><li>
 * When an instance has been marked as <em>minimized</em>,
 * its contents (client area) will usually not be visible,
 * and depending on the window manager, it may be
 * "iconified" (that is, replaced on the desktop by a small
 * simplified representation of itself), relocated to a
 * distinguished area of the screen, or hidden. Combinations
 * of these changes are also possible.
 * </li>
 * </ul>
 * </p><p>
 * The <em>modality</em> of an instance may be specified using
 * style bits. The modality style bits are used to determine
 * whether input is blocked for other shells on the display.
 * The <code>PRIMARY_MODAL</code> style allows an instance to block
 * input to its parent. The <code>APPLICATION_MODAL</code> style
 * allows an instance to block input to every other shell in the
 * display. The <code>SYSTEM_MODAL</code> style allows an instance
 * to block input to all shells, including shells belonging to
 * different applications.
 * </p><p>
 * Note: The styles supported by this class are treated
 * as <em>HINT</em>s, since the window manager for the
 * desktop on which the instance is visible has ultimate
 * control over the appearance and behavior of decorations
 * and modality. For example, some window managers only
 * support resizable windows and will always assume the
 * RESIZE style, even if it is not set. In addition, if a
 * modality style is not supported, it is "upgraded" to a
 * more restrictive modality style that is supported. For
 * example, if <code>PRIMARY_MODAL</code> is not supported,
 * it would be upgraded to <code>APPLICATION_MODAL</code>.
 * A modality style may also be "downgraded" to a less
 * restrictive style. For example, most operating systems
 * no longer support <code>SYSTEM_MODAL</code> because
 * it can freeze up the desktop, so this is typically
 * downgraded to <code>APPLICATION_MODAL</code>.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BORDER, CLOSE, MIN, MAX, NO_TRIM, RESIZE, TITLE, ON_TOP, TOOL</dd>
 * <dd>APPLICATION_MODAL, MODELESS, PRIMARY_MODAL, SYSTEM_MODAL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Activate, Close, Deactivate, Deiconify, Iconify</dd>
 * </dl>
 * Class <code>SWT</code> provides two "convenience constants"
 * for the most commonly required style combinations:
 * <dl>
 * <dt><code>SHELL_TRIM</code></dt>
 * <dd>
 * the result of combining the constants which are required
 * to produce a typical application top level shell: (that
 * is, <code>CLOSE | TITLE | MIN | MAX | RESIZE</code>)
 * </dd>
 * <dt><code>DIALOG_TRIM</code></dt>
 * <dd>
 * the result of combining the constants which are required
 * to produce a typical application dialog shell: (that
 * is, <code>TITLE | CLOSE | BORDER</code>)
 * </dd>
 * </dl>
 * </p>
 * <p>
 * Note: Only one of the styles APPLICATION_MODAL, MODELESS,
 * PRIMARY_MODAL and SYSTEM_MODAL may be specified.
 * </p><p>
 * IMPORTANT: This class is not intended to be subclassed.
 * </p>
 *
 * @see SWT
 */
public class Shell extends Decorations {

  private static final int MODE_NONE = 0;
  private static final int MODE_MAXIMIZED = 1;
  private static final int MODE_MINIMIZED = 2;

  private static final int INITIAL_SIZE_PERCENT = 60;

  private Control lastActive;
  private IShellAdapter shellAdapter;
  private String text = "";
  private Image image;
  private int alpha = 0xFF;
  private Button defaultButton;
  private Button saveDefault;
  private Control savedFocus;  // TODO [rh] move to Decorations when exist
  private int mode = MODE_NONE;

  private Shell( final Display display,
                 final Shell parent,
                 final int style,
                 final int handle )
  {
    super( checkParent( parent ) );
    if( display != null ) {
      this.display = display;
    } else {
      this.display = Display.getCurrent();
    }
    this.style = checkStyle( style );
    state |= HIDDEN;
    this.display.addShell( this );
    createWidget();
    setInitialSize();
  }

  /**
   * Constructs a new instance of this class given only the style value
   * describing its behavior and appearance. This is equivalent to calling
   * <code>Shell((Display) null, style)</code>.
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must
   * be built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code>
   * style constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   *
   * @param style the style of control to construct
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the parent</li>
   *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
   *                allowed subclass</li>
   *                </ul>
   * @see SWT#BORDER
   * @see SWT#CLOSE
   * @see SWT#MIN
   * @see SWT#MAX
   * @see SWT#RESIZE
   * @see SWT#TITLE
   * @see SWT#NO_TRIM
   * @see SWT#SHELL_TRIM
   * @see SWT#DIALOG_TRIM
   * <!--@see SWT#MODELESS-->
   * <!--@see SWT#PRIMARY_MODAL-->
   * @see SWT#APPLICATION_MODAL
   * <!--@see SWT#SYSTEM_MODAL-->
   */
  public Shell( final int style ) {
    this( ( Display )null, style );
  }

  /**
   * Constructs a new instance of this class given only the display
   * to create it on. It is created with style <code>SWT.SHELL_TRIM</code>.
   * <p>
   * Note: Currently, null can be passed in for the display argument.
   * This has the effect of creating the shell on the currently active
   * display if there is one. If there is no current display, the
   * shell is created on a "default" display. <b>Passing in null as
   * the display argument is not considered to be good coding style,
   * and may not be supported in a future release of SWT.</b>
   * </p>
   *
   * @param display the display to create the shell on
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   */
  public Shell( final Display display ) {
    this( display, SWT.SHELL_TRIM );
  }

  /**
   * Constructs a new instance of this class given the display
   * to create it on and a style value describing its behavior
   * and appearance.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p><p>
   * Note: Currently, null can be passed in for the display argument.
   * This has the effect of creating the shell on the currently active
   * display if there is one. If there is no current display, the
   * shell is created on a "default" display. <b>Passing in null as
   * the display argument is not considered to be good coding style,
   * and may not be supported in a future release of SWT.</b>
   * </p>
   *
   * @param display the display to create the shell on
   * @param style the style of control to construct
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#BORDER
   * @see SWT#CLOSE
   * @see SWT#MIN
   * @see SWT#MAX
   * @see SWT#RESIZE
   * @see SWT#TITLE
   * @see SWT#NO_TRIM
   * @see SWT#SHELL_TRIM
   * @see SWT#DIALOG_TRIM
   * <!--@see SWT#MODELESS-->
   * <!--@see SWT#PRIMARY_MODAL-->
   * @see SWT#APPLICATION_MODAL
   * <!--@see SWT#SYSTEM_MODAL-->
   */
  public Shell( final Display display, final int style ) {
    this( display, null, style, 0 );
  }

  /**
   * Constructs a new instance of this class given only its
   * parent. It is created with style <code>SWT.DIALOG_TRIM</code>.
   * <p>
   * Note: Currently, null can be passed in for the parent.
   * This has the effect of creating the shell on the currently active
   * display if there is one. If there is no current display, the
   * shell is created on a "default" display. <b>Passing in null as
   * the parent is not considered to be good coding style,
   * and may not be supported in a future release of SWT.</b>
   * </p>
   *
   * @param parent a shell which will be the parent of the new instance
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the parent is disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   */
  public Shell( final Shell parent ) {
    this( parent, SWT.DIALOG_TRIM );
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
   * </p><p>
   * Note: Currently, null can be passed in for the parent.
   * This has the effect of creating the shell on the currently active
   * display if there is one. If there is no current display, the
   * shell is created on a "default" display. <b>Passing in null as
   * the parent is not considered to be good coding style,
   * and may not be supported in a future release of SWT.</b>
   * </p>
   *
   * @param parent a shell which will be the parent of the new instance
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the parent is disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#BORDER
   * @see SWT#CLOSE
   * @see SWT#MIN
   * @see SWT#MAX
   * @see SWT#RESIZE
   * @see SWT#TITLE
   * @see SWT#NO_TRIM
   * @see SWT#SHELL_TRIM
   * @see SWT#DIALOG_TRIM
   * @see SWT#ON_TOP
   * <!--@see SWT#TOOL-->
   * <!--@see SWT#MODELESS-->
   * <!--@see SWT#PRIMARY_MODAL-->
   * @see SWT#APPLICATION_MODAL
   * <!--@see SWT#SYSTEM_MODAL-->
   */
  public Shell( final Shell parent, final int style ) {
    this( parent != null ? parent.display : null, parent, style, 0 );
  }

  public Shell getShell() {
    return this;
  }

  /**
   * Returns an array containing all shells which are
   * descendents of the receiver.
   * <p>
   * @return the dialog shells
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Shell [] getShells() {
    checkWidget();
    int count = 0;
    Shell[] shells = display.getShells();
    for( int i = 0; i < shells.length; i++ ) {
      Control shell = shells[ i ];
      do {
        shell = shell.getParent();
      } while( shell != null && shell != this );
      if( shell == this ) {
        count++;
      }
    }
    int index = 0;
    Shell[] result = new Shell[ count ];
    for( int i = 0; i < shells.length; i++ ) {
      Control shell = shells[ i ];
      do {
        shell = shell.getParent();
      } while( shell != null && shell != this );
      if( shell == this ) {
        result[ index++ ] = shells[ i ];
      }
    }
    return result;
  }

  /**
   * If the receiver is visible, moves it to the top of the
   * drawing order for the display on which it was created
   * (so that all other shells on that display, which are not
   * the receiver's children will be drawn behind it) and asks
   * the window manager to make the shell active
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.0
   * @see Control#moveAbove
   * @see Control#setFocus
   * @see Control#setVisible
   * @see Display#getActiveShell
   * <!--@see Decorations#setDefaultButton-->
   * @see Shell#setDefaultButton(Button)
   * @see Shell#open
   * @see Shell#setActive
   */
  public void setActive () {
    checkWidget();
    if( isVisible() ) {
      display.setActiveShell( this );
    }
  }

  /////////////////////
  // Shell measurements

  // TODO [rst] Move to class Decorations, as soon as it exists
  public Rectangle getClientArea() {
    checkWidget();
    Rectangle bounds = getBounds();
    Rectangle padding = getPadding();
    int hTopTrim;
    hTopTrim = getTitleBarMargin().height;
    hTopTrim += getTitleBarHeight();
    hTopTrim += getMenuBarHeight();
    int border = getBorderWidth();
    return new Rectangle( padding.x,
                          hTopTrim + padding.y,
                          bounds.width - padding.width - border * 2,
                          bounds.height - hTopTrim - padding.height - border * 2 );
  }

  // TODO [rst] Move to class Decorations, as soon as it exists
  public Rectangle computeTrim( final int x,
                                final int y,
                                final int width,
                                final int height )
  {
    checkWidget();
    int hTopTrim;
    hTopTrim = getTitleBarMargin().height;
    hTopTrim += getTitleBarHeight();
    hTopTrim += getMenuBarHeight();
    Rectangle padding = getPadding();
    int border = getBorderWidth();
    Rectangle rect = new Rectangle( x - padding.x - border,
                                    y - hTopTrim - padding.y - border,
                                    width + padding.width + border * 2,
                                    height + hTopTrim + padding.height + border * 2 );
    return rect;
  }

  private void setInitialSize() {
    int width = display.getBounds().width * INITIAL_SIZE_PERCENT / 100;
    int height = display.getBounds().height * INITIAL_SIZE_PERCENT / 100;
    bounds = new Rectangle( 0, 0, width, height );
  }

  private Rectangle getMenuBounds() {
    Rectangle result = null;
    if( getMenuBar() != null ) {
      Rectangle bounds = getBounds();
      int hTop = ( style & SWT.TITLE ) != 0 ? 1 : 0;
      hTop += getTitleBarHeight();
      Rectangle padding = getPadding();
      int border = getBorderWidth();
      result = new Rectangle( padding.x,
                              hTop + padding.y,
                              bounds.width - padding.width - border * 2,
                              getMenuBarHeight() );
    }
    return result;
  }

  private int getTitleBarHeight() {
    ShellThemeAdapter adapter = getShellThemeAdapter();
    return adapter.getTitleBarHeight( this );
  }

  private Rectangle getTitleBarMargin() {
    ShellThemeAdapter adapter = getShellThemeAdapter();
    return adapter.getTitleBarMargin( this );
  }

  private int getMenuBarHeight() {
    ShellThemeAdapter adapter = getShellThemeAdapter();
    return adapter.getMenuBarHeight( this );
  }

  // margin of the client area
  private Rectangle getPadding() {
    ShellThemeAdapter adapter = getShellThemeAdapter();
    return adapter.getPadding( this );
  }

  private ShellThemeAdapter getShellThemeAdapter() {
    ThemeManager themeMgr = ThemeManager.getInstance();
    IThemeAdapter themeAdapter = themeMgr.getThemeAdapter( getClass() );
    ShellThemeAdapter adapter = ( ShellThemeAdapter )themeAdapter;
    return adapter;
  }

  Composite findDeferredControl() {
    return layoutCount > 0 ? this : null;
  }

  /////////////////////
  // Adaptable override

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IShellAdapter.class ) {
      if( shellAdapter == null ) {
        shellAdapter = new IShellAdapter() {
          public Control getActiveControl() {
            return Shell.this.lastActive;
          }
          public void setActiveControl( final Control control ) {
            Shell.this.setActiveControl( control );
          }
          public Rectangle getMenuBounds() {
            return Shell.this.getMenuBounds();
          }
        };
      }
      result = shellAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  /////////////
  // Enablement

  public void setEnabled( final boolean enabled ) {
    checkWidget();
    if( getEnabled() != enabled ) {
      super.setEnabled( enabled );
      if( enabled ) {
        if( !restoreFocus() ) {
          traverseGroup( true );
        }
      }
    }
  }

  public boolean isEnabled() {
    checkWidget();
    return getEnabled ();
  }

  /////////////
  // Visibility

  public boolean isVisible() {
    checkWidget();
    return getVisible();
  }

  public void setVisible( final boolean visible ) {
    checkWidget();
    boolean wasVisible = getVisible();
    super.setVisible( visible );
    // Emulate OS behavior: in SWT, a layout is triggered during
    // Shell#setVisible(true)
    if( visible && !wasVisible && !isDisposed() ) {
      changed( getChildren() );
      layout( true, true );
    }
  }

  /**
   * Moves the receiver to the top of the drawing order for
   * the display on which it was created (so that all other
   * shells on that display, which are not the receiver's
   * children will be drawn behind it), marks it visible,
   * sets the focus and asks the window manager to make the
   * shell active.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Control#moveAbove
   * @see Control#setFocus
   * @see Control#setVisible
   * @see Display#getActiveShell
   * <!-- @see Decorations#setDefaultButton -->
   * @see Shell#setDefaultButton(Button)
   * @see Shell#setActive
   * <!--@see Shell#forceActive-->
   */
  public void open() {
    checkWidget();
    bringToTop();
    display.setActiveShell( this );
    setVisible( true );
    if( !restoreFocus() && !traverseGroup( true ) ) {
      setFocus();
    }
    // fire shell activated event
    // TODO: is there any possibility where it should not be fired on open() ?
    ShellEvent shellEvent = new ShellEvent( this, ShellEvent.SHELL_ACTIVATED );
    shellEvent.processEvent();
  }

  /**
   * Requests that the window manager close the receiver in
   * the same way it would be closed when the user clicks on
   * the "close box" or performs some other platform specific
   * key or mouse combination that indicates the window
   * should be removed.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SWT#Close
   * @see Shell#dispose()
   */
  public void close() {
    checkWidget();
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        ShellEvent event
          = new ShellEvent( Shell.this, ShellEvent.SHELL_CLOSED );
        event.processEvent();
        if( event.doit ) {
          Shell.this.dispose();
          Shell[] dialogShells = getShells();
          for( int i = 0; i < dialogShells.length; i++ ) {
            dialogShells[ i ].dispose();
          }
        }
      }
    } );
  }

  ///////////////////////////
  // Title bar text and image

  /**
   * Sets the receiver's text, which is the string that the
   * window manager will typically display as the receiver's
   * <em>title</em>, to the argument, which must not be null.
   *
   * @param text the new text
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO [rh] move to Decorations
  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
  }

  /**
   * Returns the receiver's text, which is the string that the
   * window manager will typically display as the receiver's
   * <em>title</em>. If the text has not previously been set,
   * returns an empty string.
   *
   * @return the text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO [rh] move to Decorations
  public String getText() {
    checkWidget();
    return text;
  }

  /**
   * Sets the receiver's image to the argument, which may
   * be null. The image is typically displayed by the window
   * manager when the instance is marked as iconified, and
   * may also be displayed somewhere in the trim when the
   * instance is in normal or maximized states.
   *
   * @param image the new image (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  /* TODO [rst] move to Decorations as soon as it exists */
  public void setImage( final Image image ) {
    checkWidget();
    this.image = image;
  }

  /**
   * Returns the receiver's image if it had previously been
   * set using <code>setImage()</code>. The image is typically
   * displayed by the window manager when the instance is
   * marked as iconified, and may also be displayed somewhere
   * in the trim when the instance is in normal or maximized
   * states.
   * <p>
   * Note: This method will return null if called before
   * <code>setImage()</code> is called. It does not provide
   * access to a window manager provided, "default" image
   * even if one exists.
   * </p>
   *
   * @return the image
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  /* TODO [rst] move to Decorations as soon as it exists */
  public Image getImage() {
    checkWidget();
    return image;
  }

  //////////////////////////////
  // Methods for default button

  /**
   * If the argument is not null, sets the receiver's default
   * button to the argument, and if the argument is null, sets
   * the receiver's default button to the first button which
   * was set as the receiver's default button (called the
   * <em>saved default button</em>). If no default button had
   * previously been set, or the saved default button was
   * disposed, the receiver's default button will be set to
   * null.
   * <p>
   * The default button is the button that is selected when
   * the receiver is active and the user presses ENTER.
   * </p>
   *
   * @param button the new default button
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the button has been disposed</li>
   *    <li>ERROR_INVALID_PARENT - if the control is not in the same widget tree</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  // TODO [rst] move to class Decorations as soon as it exists
  public void setDefaultButton( final Button button ) {
    checkWidget();
    if( button != null ) {
      if( button.isDisposed() ) {
        error( SWT.ERROR_INVALID_ARGUMENT );
      }
      if( button.getShell() != this ) {
        error( SWT.ERROR_INVALID_PARENT );
      }
    }
    setDefaultButton( button, true );
  }

  // TODO [rst] move to class Decorations as soon as it exists
  void setDefaultButton( final Button button, final boolean save ) {
    if( button == null ) {
      if( defaultButton == saveDefault ) {
        if( save ) {
          saveDefault = null;
        }
        return;
      }
    } else {
      if( ( button.getStyle() & SWT.PUSH ) == 0 ) {
        return;
      }
      if( button == defaultButton ) {
        return;
      }
    }
    if( defaultButton != null && !defaultButton.isDisposed() ) {
      defaultButton.setDefault( false );
    }
    defaultButton = button;
    if( defaultButton == null ) {
      defaultButton = saveDefault;
    }
    if( defaultButton != null && !defaultButton.isDisposed() ) {
      defaultButton.setDefault( true );
    }
    if( save ) {
      saveDefault = defaultButton;
    }
    if( saveDefault != null && saveDefault.isDisposed() ) {
      saveDefault = null;
    }
  }

  /**
   * Returns the receiver's default button if one had
   * previously been set, otherwise returns null.
   *
   * @return the default button or null
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see Shell#setDefaultButton(Button)
   */
  // TODO [rst] move to class Decorations as soon as it exists
  public Button getDefaultButton() {
    checkWidget();
    return defaultButton;
  }

  /**
   * Sets the receiver's alpha value.
   * <p>
   * This operation <!-- requires the operating system's advanced
   * widgets subsystem which --> may not be available on some
   * platforms.
   * </p>
   * @param alpha the alpha value
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.1
   */
  public void setAlpha( final int alpha ) {
    checkWidget();
    this.alpha = alpha & 0xFF;
  }

  /**
   * Returns the receiver's alpha value.
   *
   * @return the alpha value
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @since 1.1
   */
  public int getAlpha() {
    checkWidget();
    return alpha;
  }

  // ///////////////////////////////////////////////
  // Event listener registration and deregistration

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when operations are performed on the receiver,
   * by sending the listener one of the messages defined in the
   * <code>ShellListener</code> interface.
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
   * @see ShellListener
   * @see #removeShellListener
   */
  public void addShellListener( final ShellListener listener ) {
    ShellEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when operations are performed on the receiver.
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
   * @see ShellListener
   * @see #addShellListener
   */
  public void removeShellListener( final ShellListener listener ) {
    ShellEvent.removeListener( this, listener );
  }

  ///////////
  // Disposal

  final void releaseParent() {
    // Do not call super.releaseParent()
    // This method would try to remove a child-shell from its ControlHolder
    // but shells are currently not added to the ControlHolder of its parent
    display.removeShell( this );
  }

  ////////////////////////////////////////////////////////////
  // Methods to maintain activeControl and send ActivateEvents

  void setActiveControl( final Control activateControl ) {
    Control control = activateControl;
    if( control != null && control.isDisposed() ) {
      control = null;
    }
    if( lastActive != null && lastActive.isDisposed() ) {
      lastActive = null;
    }
    if( lastActive != control ) {
      // Compute the list of controls to be activated and deactivated by finding
      // the first common parent control.
      Control[] activate
        = ( control == null ) ? new Control[ 0 ] : control.getPath();
      Control[] deactivate
        = lastActive == null ? new Control[ 0 ] : lastActive.getPath();
      lastActive = control;

      int index = 0;
      int length = Math.min( activate.length, deactivate.length );
      while( index < length && activate[ index ] == deactivate[ index ] ) {
        index++;
      }
      // It is possible (but unlikely), that application code could have
      // destroyed some of the widgets. If this happens, keep processing those
      // widgets that are not disposed.
      ActivateEvent evt;
      for( int i = deactivate.length - 1; i >= index; --i ) {
        if( !deactivate[ i ].isDisposed() ) {
          evt = new ActivateEvent( deactivate[ i ], ActivateEvent.DEACTIVATED );
          evt.processEvent();
        }
      }
      for( int i = activate.length - 1; i >= index; --i ) {
        if( !activate[ i ].isDisposed() ) {
          evt = new ActivateEvent( activate[ i ], ActivateEvent.ACTIVATED );
          evt.processEvent();
        }
      }
    }
  }

  /////////////////////////
  // Focus handling methods

  // TODO [rh] move to Decorations as soon as exists
  final void setSavedFocus( final Control control ) {
    savedFocus = control;
  }

  // TODO [rh] move to Decorations as soon as exists
  final void saveFocus() {
    Control control = display.getFocusControl();
    if( control != null && control != this && this == control.getShell() ) {
      setSavedFocus( control );
    }
  }

  // TODO [rh] move to Decorations as soon as exists
  final boolean restoreFocus() {
    if( savedFocus != null && savedFocus.isDisposed() ) {
      savedFocus = null;
    }
    boolean result = false;
    if( savedFocus != null && savedFocus.setSavedFocus() ) {
      result = true;
    }
    return result;
  }

  private void bringToTop() {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setFocusControl( this );
    // When a Shell is opened client-side the widget that is currently focused
    // loses its focus. This is unwanted in the case that the request that
    // opened the Shell sets the focus to some widget after opening the Shell.
    // The fix is to force the DisplayLCA to issue JavaScript that sets the
    // focus on the server-side focused widget.
    displayAdapter.invalidateFocus();
  }

  ////////////////
  // Tab traversal

  private boolean traverseGroup( final boolean next ) {
    // TODO [rh] fake implementation
    boolean result = false;
    if( getChildren().length > 0 ) {
      result = getChildren()[ 0 ].forceFocus();
    }
    return result;
  }

  //////////////////////
  // minimize / maximize

  // TODO [rst] Move these methods to class Decorations when implemented

  /**
   * Sets the minimized stated of the receiver.
   * If the argument is <code>true</code> causes the receiver
   * to switch to the minimized state, and if the argument is
   * <code>false</code> and the receiver was previously minimized,
   * causes the receiver to switch back to either the maximized
   * or normal states.
   * <!--
   * <p>
   * Note: The result of intermixing calls to <code>setMaximized(true)</code>
   * and <code>setMinimized(true)</code> will vary by platform. Typically,
   * the behavior will match the platform user's expectations, but not
   * always. This should be avoided if possible.
   * </p>
   * -->
   * @param minimized the new maximized state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #setMaximized
   */
  public void setMinimized( final boolean minimized ) {
    if( minimized ) {
      mode |= MODE_MINIMIZED;
    } else {
      if( ( mode & MODE_MINIMIZED ) != 0 ) {
        setActive();
      }
      mode &= ~MODE_MINIMIZED;
    }
  }

  /**
   * Sets the maximized state of the receiver.
   * If the argument is <code>true</code> causes the receiver
   * to switch to the maximized state, and if the argument is
   * <code>false</code> and the receiver was previously maximized,
   * causes the receiver to switch back to either the minimized
   * or normal states.
   * <!--<p>
   * Note: The result of intermixing calls to <code>setMaximized(true)</code>
   * and <code>setMinimized(true)</code> will vary by platform. Typically,
   * the behavior will match the platform user's expectations, but not
   * always. This should be avoided if possible.
   * </p>
   * -->
   * @param maximized the new maximized state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #setMinimized
   */
  public void setMaximized( final boolean maximized ) {
    if( maximized ) {
      if( mode != MODE_MAXIMIZED ) {
        setActive();
        setBounds( display.getBounds() );
      }
      mode |= MODE_MAXIMIZED;
      mode &= ~MODE_MINIMIZED;
    } else {
      mode &= ~MODE_MAXIMIZED;
    }
  }

  /**
   * Returns <code>true</code> if the receiver is currently
   * minimized, and false otherwise.
   * <p>
   *
   * @return the minimized state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #setMinimized
   */
  public boolean getMinimized() {
    return ( this.mode & MODE_MINIMIZED ) != 0;
  }

  /**
   * Returns <code>true</code> if the receiver is currently
   * maximized, and false otherwise.
   * <p>
   *
   * @return the maximized state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #setMaximized
   */
  public boolean getMaximized() {
    return this.mode == MODE_MAXIMIZED;
  }

  ///////////////////
  // Widget overrides

  // TODO [rh] move to class Decorations as soon as it exists
  String getNameText() {
    return getText();
  }

  ///////////////////
  // check... methods

  // TODO [rh] move to class Decorations as soon as it exists
  static int Decorations_checkStyle( final int style ) {
    int result = style;
    if( ( result & SWT.NO_TRIM ) != 0 ) {
      int trim = ( SWT.CLOSE
                 | SWT.TITLE
                 | SWT.MIN
                 | SWT.MAX
                 | SWT.RESIZE
                 | SWT.BORDER );
      result &= ~trim;
    }
    if( ( result & ( /* SWT.MENU | */ SWT.MIN | SWT.MAX | SWT.CLOSE ) ) != 0 ) {
      result |= SWT.TITLE;
    }
    if( ( result & ( SWT.MIN | SWT.MAX ) ) != 0 ) {
      result |= SWT.CLOSE;
    }
    return result;
  }

  private static int checkStyle( final int style ) {
    return Decorations_checkStyle( style );
  }

  private static Shell checkParent( final Shell parent ) {
    if( parent != null && parent.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return parent;
  }
}
