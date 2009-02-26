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

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.AdapterManagerImpl;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.internal.widgets.IDisplayAdapter.IFilterEntry;


/**
 * Instances of this class are responsible for managing the
 * connection between SWT and the underlying operating
 * system. Their most important function is to implement
 * the SWT event loop in terms of the platform event model.
 * They also provide various methods for accessing information
 * about the operating system, and have overall control over
 * the operating system resources which SWT allocates.
 * <p>
 * Applications which are built with SWT will <em>almost always</em>
 * require only a single display. In particular, some platforms
 * which SWT supports will not allow more than one <em>active</em>
 * display. In other words, some platforms do not support
 * creating a new display if one already exists that has not been
 * sent the <code>dispose()</code> message.
 * <p>
 * In SWT, the thread which creates a <code>Display</code>
 * instance is distinguished as the <em>user-interface thread</em>
 * for that display.
 * </p>
 * The user-interface thread for a particular display has the
 * following special attributes:
 * <ul>
 * <li>
 * The event loop for that display must be run from the thread.
 * </li>
 * <li>
 * Some SWT API methods (notably, most of the public methods in
 * <code>Widget</code> and its subclasses), may only be called
 * from the thread. (To support multi-threaded user-interface
 * applications, class <code>Display</code> provides inter-thread
 * communication methods which allow threads other than the
 * user-interface thread to request that it perform operations
 * on their behalf.)
 * </li>
 * <li>
 * The thread is not allowed to construct other
 * <code>Display</code>s until that display has been disposed.
 * (Note that, this is in addition to the restriction mentioned
 * above concerning platform support for multiple displays. Thus,
 * the only way to have multiple simultaneously active displays,
 * even on platforms which support it, is to have multiple threads.)
 * </li>
 * </ul>
 * Enforcing these attributes allows SWT to be implemented directly
 * on the underlying operating system's event model. This has
 * numerous benefits including smaller footprint, better use of
 * resources, safer memory management, clearer program logic,
 * better performance, and fewer overall operating system threads
 * required. The down side however, is that care must be taken
 * (only) when constructing multi-threaded applications to use the
 * inter-thread communication mechanisms which this class provides
 * when required.
 * </p><p>
 * All SWT API methods which may only be called from the user-interface
 * thread are distinguished in their documentation by indicating that
 * they throw the "<code>ERROR_THREAD_INVALID_ACCESS</code>"
 * SWT exception.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Close, Dispose</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * @see #syncExec
 * @see #asyncExec
 * @see #wake
 *
 * @see #readAndDispatch
 * @see #sleep
 * <!--@see Device#dispose-->
 *
 * @since 1.0
 */
// TODO: [doc] Update display javadoc
public class Display extends Device implements Adaptable {

  private static final String DISPLAY_ID = "org.eclipse.swt.display";
  private static final String INVALIDATE_FOCUS
    = DisplayAdapter.class.getName() + "#invalidateFocus";


  /* Package Name */
  static final String PACKAGE_PREFIX = "org.eclipse.swt.widgets.";

  // Keep in sync with client-side (EventUtil.js)
  private static final int DOUBLE_CLICK_TIME = 500;

  /**
   * Returns the display which the currently running thread is
   * the user-interface thread for, or null if the currently
   * running thread is not a user-interface thread for any display.
   *
   * @return the current display
   */
  public static Display getCurrent() {
    Display result = null;
    if( ContextProvider.hasContext() ) {
      ISessionStore sessionStore = ContextProvider.getSession();
      result = ( Display )sessionStore.getAttribute( DISPLAY_ID );
    }
    return result;
  }

  /**
   * Returns the default display. One is created (making the
   * thread that invokes this method its user-interface thread)
   * if it did not already exist.
   * <p>
   * RWT specific: This will not return a new display if there is none
   * available. This may be fixed in the future.
   * </p>
   * 
   * @return the default display
   */
  public static Display getDefault() {
    return getCurrent();
  }

  private final List shells;
  private final Thread thread;
  private final ISessionStore session;
  private Rectangle bounds;
  private Shell activeShell;
  private List filters;
  private Control focusControl;
  private IDisplayAdapter displayAdapter;
  private WidgetAdapter widgetAdapter;
  
  /* Display Data */
  private Object data;
  private String [] keys;
  private Object [] values;

  /**
   * Constructs a new instance of this class.
   * <p>
   * Note: The resulting display is marked as the <em>current</em>
   * display. If this is the first display which has been
   * constructed since the application started, it is also
   * marked as the <em>default</em> display.
   * </p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if called from a thread that already created an existing display</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see #getCurrent
   * @see #getDefault
   * @see Widget#checkSubclass
   * @see Shell
   */
  public Display() {
    thread = Thread.currentThread();
    session = ContextProvider.getSession();
    if( getCurrent() != null ) {
      String msg = "Currently only one display per session is supported.";
      throw new IllegalStateException( msg );
    }
    ContextProvider.getSession().setAttribute( DISPLAY_ID, this );
    shells = new ArrayList();
    readInitialBounds();
  }

  /**
   * Returns a rectangle describing the receiver's size and location.
   *
   * @return the bounding rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Rectangle getBounds() {
    checkDevice();
    return new Rectangle( bounds.x, bounds.y, bounds.width, bounds.height );
  }

  /**
   * Returns the control which currently has keyboard focus,
   * or null if keyboard events are not currently going to
   * any of the controls built by the currently running
   * application.
   *
   * @return the control under the cursor
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Control getFocusControl() {
    checkDevice();
    return focusControl;
  }

  private void setFocusControl( final Control focusControl ) {
    if( this.focusControl != focusControl ) {
      if( this.focusControl != null && !this.focusControl.isInDispose() ) {
        FocusEvent event
          = new FocusEvent( this.focusControl, FocusEvent.FOCUS_LOST );
        event.processEvent();
      }
      this.focusControl = focusControl;
      if( this.focusControl != null ) {
        FocusEvent event
          = new FocusEvent( this.focusControl, FocusEvent.FOCUS_GAINED );
        event.processEvent();
      }
    }
  }

  /**
   * Maps a point from one coordinate system to another.
   * When the control is null, coordinates are mapped to
   * the display.
   * <p>
   * NOTE: On right-to-left platforms where the coordinate
   * systems are mirrored, special care needs to be taken
   * when mapping coordinates from one control to another
   * to ensure the result is correctly mirrored.
   *
   * Mapping a point that is the origin of a rectangle and
   * then adding the width and height is not equivalent to
   * mapping the rectangle.  When one control is mirrored
   * and the other is not, adding the width and height to a
   * point that was mapped causes the rectangle to extend
   * in the wrong direction.  Mapping the entire rectangle
   * instead of just one point causes both the origin and
   * the corner of the rectangle to be mapped.
   * </p>
   *
   * @param from the source <code>Control</code> or <code>null</code>
   * @param to the destination <code>Control</code> or <code>null</code>
   * @param point to be mapped
   * @return point with mapped coordinates
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the Control from or the Control to have been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Point map( final Control from, final Control to, final Point point ) {
    checkDevice();
    if( point == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return map( from, to, point.x, point.y );
  }

  /**
   * Maps a point from one coordinate system to another.
   * When the control is null, coordinates are mapped to
   * the display.
   * <p>
   * NOTE: On right-to-left platforms where the coordinate
   * systems are mirrored, special care needs to be taken
   * when mapping coordinates from one control to another
   * to ensure the result is correctly mirrored.
   *
   * Mapping a point that is the origin of a rectangle and
   * then adding the width and height is not equivalent to
   * mapping the rectangle.  When one control is mirrored
   * and the other is not, adding the width and height to a
   * point that was mapped causes the rectangle to extend
   * in the wrong direction.  Mapping the entire rectangle
   * instead of just one point causes both the origin and
   * the corner of the rectangle to be mapped.
   * </p>
   *
   * @param from the source <code>Control</code> or <code>null</code>
   * @param to the destination <code>Control</code> or <code>null</code>
   * @param x coordinates to be mapped
   * @param y coordinates to be mapped
   * @return point with mapped coordinates
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the Control from or the Control to have been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Point map( final Control from,
                    final Control to,
                    final int x,
                    final int y )
  {
    checkDevice();
    Rectangle rectangle = map( from, to, x, y, 0, 0 );
    return new Point( rectangle.x, rectangle.y );
  }

  /**
   * Maps a point from one coordinate system to another.
   * When the control is null, coordinates are mapped to
   * the display.
   * <p>
   * NOTE: On right-to-left platforms where the coordinate
   * systems are mirrored, special care needs to be taken
   * when mapping coordinates from one control to another
   * to ensure the result is correctly mirrored.
   *
   * Mapping a point that is the origin of a rectangle and
   * then adding the width and height is not equivalent to
   * mapping the rectangle.  When one control is mirrored
   * and the other is not, adding the width and height to a
   * point that was mapped causes the rectangle to extend
   * in the wrong direction.  Mapping the entire rectangle
   * instead of just one point causes both the origin and
   * the corner of the rectangle to be mapped.
   * </p>
   *
   * @param from the source <code>Control</code> or <code>null</code>
   * @param to the destination <code>Control</code> or <code>null</code>
   * @param rectangle to be mapped
   * @return rectangle with mapped coordinates
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the rectangle is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the Control from or the Control to have been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Rectangle map( final Control from,
                        final Control to,
                        final Rectangle rectangle )
  {
    checkDevice();
    if( rectangle == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return map( from,
                to,
                rectangle.x,
                rectangle.y,
                rectangle.width,
                rectangle.height );
  }

  /**
   * Maps a point from one coordinate system to another.
   * When the control is null, coordinates are mapped to
   * the display.
   * <p>
   * NOTE: On right-to-left platforms where the coordinate
   * systems are mirrored, special care needs to be taken
   * when mapping coordinates from one control to another
   * to ensure the result is correctly mirrored.
   *
   * Mapping a point that is the origin of a rectangle and
   * then adding the width and height is not equivalent to
   * mapping the rectangle.  When one control is mirrored
   * and the other is not, adding the width and height to a
   * point that was mapped causes the rectangle to extend
   * in the wrong direction.  Mapping the entire rectangle
   * instead of just one point causes both the origin and
   * the corner of the rectangle to be mapped.
   * </p>
   *
   * @param from the source <code>Control</code> or <code>null</code>
   * @param to the destination <code>Control</code> or <code>null</code>
   * @param x coordinates to be mapped
   * @param y coordinates to be mapped
   * @param width coordinates to be mapped
   * @param height coordinates to be mapped
   * @return rectangle with mapped coordinates
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the Control from or the Control to have been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Rectangle map( final Control from,
                        final Control to,
                        final int x,
                        final int y,
                        final int width,
                        final int height )
  {
    checkDevice();
    int newX = x;
    int newY = y;
    Control currentFrom = from;
    while( currentFrom != null ) {
      Rectangle bounds = currentFrom.getBounds();
      newX += bounds.x;
      newY += bounds.y;
      if( currentFrom instanceof Shell ) {
        currentFrom = null;
      } else {
        currentFrom = currentFrom.getParent();
      }
    }
    Control currentTo = to;
    while( currentTo != null ) {
      Rectangle bounds = currentTo.getBounds();
      newX -= bounds.x;
      newY -= bounds.y;
      if( currentTo instanceof Shell ) {
        currentTo = null;
      } else {
        currentTo = currentTo.getParent();
      }
    }
    return new Rectangle( newX, newY, width, height );
  }
  
  //////////
  // Dispose

  // TODO [rh] This is preliminary!
  // TODO [rh] move to Device
  public void dispose() {
    ContextProvider.getSession().removeAttribute( DISPLAY_ID );
  }

  // TODO [rh] move to Device
  public boolean isDisposed() {
    return false;
  }

  /////////////////////
  // Adaptable override

  public Object getAdapter( final Class adapter ) {
    Object result = null;
    if( adapter == IDisplayAdapter.class ) {
      if( displayAdapter == null ) {
        displayAdapter = new DisplayAdapter( session );
      }
      result = displayAdapter;
    } else if( adapter == IWidgetAdapter.class ) {
      if( widgetAdapter == null ) {
        widgetAdapter = new WidgetAdapter( "w1" );
      }
      result = widgetAdapter;
    } else {
      result = AdapterManagerImpl.getInstance().getAdapter( this, adapter );
    }
    return result;
  }

  ///////////////////
  // Shell management

  /**
   * Returns a (possibly empty) array containing all shells which have
   * not been disposed and have the receiver as their display.
   *
   * @return the receiver's shells
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Shell[] getShells() {
    checkDevice();
    Shell[] result = new Shell[ shells.size() ];
    shells.toArray( result );
    return result;
  }

  /**
   * Returns the currently active <code>Shell</code>, or null
   * if no shell belonging to the currently running application
   * is active.
   *
   * @return the active shell or null
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Shell getActiveShell() {
    checkDevice();
    return activeShell;
  }

  final void setActiveShell( final Shell activeShell ) {
    checkDevice();
    if( this.activeShell != null ) {
      this.activeShell.saveFocus();
    }
    // Move active shell to end of list to maintain correct z-order
    if( activeShell != null ) {
      shells.remove( activeShell );
      shells.add( activeShell );
    }
    this.activeShell = activeShell;
    if( this.activeShell != null ) {
      this.activeShell.restoreFocus();
    }
  }

  final void addShell( final Shell shell ) {
    shells.add( shell );
  }

  final void removeShell( final Shell shell ) {
    shells.remove( shell );
    if( shell == activeShell ) {
      if( shells.size() > 0 ) {
        // activate the least recently added / activated element
        setActiveShell( ( Shell )shells.get( shells.size() - 1 ) );
      } else {
        setActiveShell( null );
      }
    }
  }

  ////////////////////
  // Thread management

  /**
   * Returns the user-interface thread for the receiver. Note that the
   * user-interface thread may change per user-request.
   *
   * @return the receiver's user-interface thread or null if there's no
   *         current user-request executed that belongs to the display.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Thread getThread () {
    return thread;
  }

  /**
   * Causes the <code>run()</code> method of the runnable to
   * be invoked by the user-interface thread at the next
   * reasonable opportunity. Note that the user-interface thread may change
   * per user-request. The caller of this method continues
   * to run in parallel, and is not notified when the
   * runnable has completed.  Specifying <code>null</code> as the
   * runnable simply wakes the user-interface thread when run.
   * <p>
   * Note that at the time the runnable is invoked, widgets
   * that have the receiver as their display may have been
   * disposed. Therefore, it is necessary to check for this
   * case inside the runnable before accessing the widget.
   * </p>
   *
   * @param runnable code to run on the user-interface thread or <code>null</code>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #syncExec
   */
  public void asyncExec( final Runnable runnable ) {
// TODO: [fappel] disposal check
//    if( isDisposed() ) {
//      error( SWT.ERROR_DEVICE_DISPOSED );
//    }
    UICallBack.runNonUIThreadWithFakeContext( this, new Runnable() {
      public void run() {
        UICallBackManager.getInstance().addAsync( runnable, Display.this );
      }
    } );
  }

  /**
   * Causes the <code>run()</code> method of the runnable to
   * be invoked by the user-interface thread at the next
   * reasonable opportunity. Note that the user-interface thread may change
   * per user-request. The thread which calls this method
   * is suspended until the runnable completes.  Specifying <code>null</code>
   * as the runnable simply wakes the user-interface thread.
   * <p>
   * Note that at the time the runnable is invoked, widgets
   * that have the receiver as their display may have been
   * disposed. Therefore, it is necessary to check for this
   * case inside the runnable before accessing the widget.
   * </p>
   *
   * @param runnable code to run on the user-interface thread or <code>null</code>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_FAILED_EXEC - if an exception occured when executing the runnable</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #asyncExec
   */
  public void syncExec( final Runnable runnable ) {
//  TODO: [fappel] disposal check
//  if( isDisposed() ) {
//    error( SWT.ERROR_DEVICE_DISPOSED );
//  }
    UICallBack.runNonUIThreadWithFakeContext( this, new Runnable() {
      public void run() {
        UICallBackManager.getInstance().addSync( runnable, Display.this );
      }
    } );
  }


  /**
   * Reads an event from the <!-- operating system's --> event queue,
   * dispatches it appropriately, and returns <code>true</code>
   * if there is potentially more work to do, or <code>false</code>
   * if the caller can sleep until another event is placed on
   * the event queue.
   * <p>
   * In addition to checking the system event queue, this method also
   * checks if any inter-thread messages (created by <code>syncExec()</code>
   * or <code>asyncExec()</code>) are waiting to be processed, and if
   * so handles them before returning.
   * </p>
   *
   * @return <code>false</code> if the caller can sleep upon return from this method
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_FAILED_EXEC - if an exception occurred while running an inter-thread message</li>
   * </ul>
   *
   * @see #sleep
   * @see #wake
   *
   * @since 1.1
   */
  public boolean readAndDispatch() {
    return RWTLifeCycle.readAndDispatch();
  }

  /**
   * Causes the user-interface thread to <em>sleep</em> (that is,
   * to be put in a state where it does not consume CPU cycles)
   * until an event is received or it is otherwise awakened.
   *
   * @return <code>true</code> if an event requiring dispatching was placed on the queue.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #wake
   *
   * @since 1.1
   */
  public boolean sleep() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.sleep();
    // return true as we cannot reliably determinate what actually caused 
    // lifeCycle#sleep() to return
    return true;
  }

  /**
   * Notifies the client side to send a life cycle request as UI thread to
   * perform UI-updates. Note that this method may be called from any thread.
   *
   * <p>Note that this only works as expected if the 
   * <code>{@link org.eclipse.rwt.lifecycle.UICallBack UICallBack}</code>
   * mechanism is activated.</p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   */
  public void wake() {
//  TODO: [fappel] disposal check
//  if( isDisposed() ) {
//    error( SWT.ERROR_DEVICE_DISPOSED );
//  }
    if( getThread() != Thread.currentThread() ) {
      UICallBack.runNonUIThreadWithFakeContext( this, new Runnable() {
        public void run() {
          UICallBackManager.getInstance().sendUICallBack();
        }
      } );
    }
  }

  //////////////////////
  // Information methods

  /**
   * Returns the matching standard color for the given
   * constant, which should be one of the color constants
   * specified in class <code>SWT</code>. Any value other
   * than one of the SWT color constants which is passed
   * in will result in the color black. This color should
   * not be free'd because it was allocated by the system,
   * not the application.
   *
   * @param id the color constant
   * @return the matching color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see SWT
   */
  public Color getSystemColor( final int id ) {
    checkDevice();
    Color result = null;
    QxType value = null;
    switch( id ) {
      case SWT.COLOR_WIDGET_DARK_SHADOW:
        value = ThemeUtil.getCssValue( "Display",
                                       "rwt-darkshadow-color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_WIDGET_NORMAL_SHADOW:
        value = ThemeUtil.getCssValue( "Display",
                                       "rwt-shadow-color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_WIDGET_LIGHT_SHADOW:
        value = ThemeUtil.getCssValue( "Display",
                                       "rwt-lightshadow-color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW:
        value = ThemeUtil.getCssValue( "Display",
                                       "rwt-highlight-color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_WIDGET_BORDER:
        value = ThemeUtil.getCssValue( "Display",
                                       "rwt-thinborder-color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_WIDGET_BACKGROUND:
        // TODO [rst] Revise element name
        value = ThemeUtil.getCssValue( "NONE",
                                       "background-color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_WIDGET_FOREGROUND:
        // TODO [rst] Revise element name
        value = ThemeUtil.getCssValue( "NONE", "color", SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_LIST_FOREGROUND:
        value = ThemeUtil.getCssValue( "List", "color", SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_LIST_BACKGROUND:
        value = ThemeUtil.getCssValue( "List",
                                       "background-color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_LIST_SELECTION:
        value = ThemeUtil.getCssValue( "List-Item",
                                       "background-color",
                                       SimpleSelector.SELECTED );
      break;
      case SWT.COLOR_LIST_SELECTION_TEXT:
        value = ThemeUtil.getCssValue( "List-Item",
                                       "color",
                                       SimpleSelector.SELECTED );
      break;
      case SWT.COLOR_INFO_FOREGROUND:
        value = ThemeUtil.getCssValue( "ToolTip",
                                       "color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_INFO_BACKGROUND:
        value = ThemeUtil.getCssValue( "ToolTip",
                                       "background-color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_TITLE_FOREGROUND:
        value = ThemeUtil.getCssValue( "Shell-Titlebar",
                                       "color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_TITLE_INACTIVE_FOREGROUND:
        value = ThemeUtil.getCssValue( "Shell-Titlebar",
                                       "color",
                                       SimpleSelector.INACTIVE );
      break;
      case SWT.COLOR_TITLE_BACKGROUND:
        value = ThemeUtil.getCssValue( "Shell-Titlebar",
                                       "background-color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_TITLE_INACTIVE_BACKGROUND:
        value = ThemeUtil.getCssValue( "Shell-Titlebar",
                                       "background-color",
                                       SimpleSelector.INACTIVE );
      break;
      case SWT.COLOR_TITLE_BACKGROUND_GRADIENT:
        value = ThemeUtil.getCssValue( "Shell-Titlebar",
                                       "background-gradient-color",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT:
        value = ThemeUtil.getCssValue( "Shell-Titlebar",
                                       "background-gradient-color",
                                       SimpleSelector.INACTIVE );
      break;
      default:
        result = super.getSystemColor( id );
    }
    if( value != null ) {
      result = QxColor.createColor( ( QxColor )value );
      if( result == null ) {
        // TODO [rst] Revise: theming must prevent transparency for system colors
        throw new IllegalArgumentException( "Transparent system color" );
      }
    }
    return result;
  }

  /**
   * Returns the matching standard platform image for the given
   * constant, which should be one of the icon constants
   * specified in class <code>SWT</code>. This image should
   * not be free'd because it was allocated by the system,
   * not the application.  A value of <code>null</code> will
   * be returned either if the supplied constant is not an
   * SWT icon constant or if the platform does not define an
   * image that corresponds to the constant.
   *
   * @param id the SWT icon constant
   * @return the corresponding image or <code>null</code>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see SWT#ICON_ERROR
   * @see SWT#ICON_INFORMATION
   * @see SWT#ICON_QUESTION
   * @see SWT#ICON_WARNING
   * @see SWT#ICON_WORKING
   */
  public Image getSystemImage( final int id ) {
    checkDevice();
    Image result = null;
    QxType value = null;
    switch( id ) {
      case SWT.ICON_ERROR:
        value = ThemeUtil.getCssValue( "Display",
                                       "rwt-error-image",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.ICON_WORKING:
      case SWT.ICON_INFORMATION:
        value = ThemeUtil.getCssValue( "Display",
                                       "rwt-information-image",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.ICON_QUESTION:
        value = ThemeUtil.getCssValue( "Display",
                                       "rwt-question-image",
                                       SimpleSelector.DEFAULT );
      break;
      case SWT.ICON_WARNING:
        value = ThemeUtil.getCssValue( "Display",
                                       "rwt-warning-image",
                                       SimpleSelector.DEFAULT );
      break;
    }
    if( value != null ) {
      QxImage image = ( QxImage )value;
      try {
        InputStream inStream = image.loader.getResourceAsStream( image.path );
        result = Graphics.getImage( image.path, inStream );
        inStream.close();
      } catch( final IOException shouldNotHappen ) {
        String txt = "Could not read system image from ''{0}''.";
        String msg = MessageFormat.format( txt, new Object[] { image.path } );
        throw new RuntimeException( msg, shouldNotHappen );
      }
    }
    return result;
  }

  /**
   * Returns the longest duration, in milliseconds, between
   * two mouse button clicks that will be considered a
   * <em>double click</em> <!-- by the underlying operating system -->.
   *
   * @return the double click time
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   * 
   * @since 1.2
   */
  public int getDoubleClickTime() {
    checkDevice ();
    return DOUBLE_CLICK_TIME;
  }

  //////////
  // Filters

  /**
   * Adds the listener to the collection of listeners who will be notified when
   * an event of the given type occurs anywhere in a widget. The event type is
   * one of the event constants defined in class <code>SWT</code>. When the
   * event does occur, the listener is notified by sending it the
   * <code>handleEvent()</code> message.
   * <p>
   * Setting the type of an event to <code>SWT.None</code> from within the
   * <code>handleEvent()</code> method can be used to change the event type
   * and stop subsequent Java listeners from running. Because event filters run
   * before other listeners, event filters can both block other listeners and
   * set arbitrary fields within an event. For this reason, event filters are
   * both powerful and dangerous. They should generally be avoided for
   * performance, debugging and code maintenance reasons.
   * </p>
   *
   * @param eventType the type of event to listen for
   * @param listener the listener which should be notified when the event occurs
   * @exception IllegalArgumentException
   *                <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *                </ul>
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                <li>ERROR_DEVICE_DISPOSED - if the receiver has been
   *                disposed</li>
   *                </ul>
   * @see Listener
   * @see SWT
   * @see #removeFilter
   * <!--@see #removeListener-->
   */
  public void addFilter( final int eventType, final Listener listener ) {
    checkDevice();
    if( listener == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( filters == null ) {
      filters = new ArrayList();
    }
    filters.add( new IFilterEntry() {
      public Listener getListener() {
        return listener;
      }
      public int getType() {
        return eventType;
      }
    } );
  }

  /**
   * Removes the listener from the collection of listeners who will be notified
   * when an event of the given type occurs anywhere in a widget. The event type
   * is one of the event constants defined in class <code>SWT</code>.
   *
   * @param eventType the type of event to listen for
   * @param listener the listener which should no longer be notified when the
   *            event occurs
   * @exception IllegalArgumentException
   *                <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *                </ul>
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   * @see Listener
   * @see SWT
   * @see #addFilter
   * <!--@see #addListener-->
   */
  public void removeFilter( final int eventType, final Listener listener ) {
    checkDevice();
    if( listener == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    IFilterEntry[] entries = getFilterEntries();
    boolean found = false;
    for( int i = 0; !found && i < entries.length; i++ ) {
      found =    entries[ i ].getListener() == listener
              && entries[ i ].getType() == eventType;
      if( found ) {
        filters.remove( entries[ i ] );
      }
    }
    if( filters != null && filters.isEmpty() ) {
      filters = null;
    }
  }

  /**
   * Does whatever display specific cleanup is required, and then uses the code
   * in <code>SWTError.error</code> to handle the error.
   *
   * @param code the descriptive error code
   * @see SWT#error(int)
   */
  void error( int code ) {
    SWT.error( code );
  }

  ///////////////
  // Data methods

  /**
   * Returns the application defined, display specific data
   * associated with the receiver, or null if it has not been
   * set. The <em>display specific data</em> is a single,
   * unnamed field that is stored with every display.
   * <p>
   * Applications may put arbitrary objects in this field. If
   * the object stored in the display specific data needs to
   * be notified when the display is disposed of, it is the
   * application's responsibility to provide a
   * <code>disposeExec()</code> handler which does so.
   * </p>
   *
   * @return the display specific data
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   *    created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #setData(Object)
   * @see #disposeExec(Runnable)
   * 
   * @since 1.2
   */
  public Object getData() {
    checkDevice();
    return data;
  }
  
  /**
   * Sets the application defined, display specific data
   * associated with the receiver, to the argument.
   * The <em>display specific data</em> is a single,
   * unnamed field that is stored with every display. 
   * <p>
   * Applications may put arbitrary objects in this field. If
   * the object stored in the display specific data needs to
   * be notified when the display is disposed of, it is the
   * application's responsibility provide a
   * <code>disposeExec()</code> handler which does so.
   * </p>
   *
   * @param data the new display specific data
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   *    created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #getData()
   * @see #disposeExec(Runnable)
   * 
   * @since 1.2
   */
  public void setData( final Object data ) {
    checkDevice();
    this.data = data;
  }

  /**
   * Sets the application defined property of the receiver
   * with the specified name to the given argument.
   * <p>
   * Applications may have associated arbitrary objects with the
   * receiver in this fashion. If the objects stored in the
   * properties need to be notified when the display is disposed
   * of, it is the application's responsibility provide a
   * <code>disposeExec()</code> handler which does so.
   * </p>
   *
   * @param key the name of the property
   * @param value the new value for the property
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the key is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   *    created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #getData(String)
   * @see #disposeExec(Runnable)
   * 
   * @since 1.2
   */
  // [bm]: This is a verbatim copy of SWT, thus no reformatting was done.
  public void setData( final String key, final Object value ) {
    checkDevice ();
    if (key == null) error (SWT.ERROR_NULL_ARGUMENT);

    /* Remove the key/value pair */
    if (value == null) {
      if (keys == null) return;
      int index = 0;
      while (index < keys.length && !keys [index].equals (key)) index++;
      if (index == keys.length) return;
      if (keys.length == 1) {
        keys = null;
        values = null;
      } else {
        String [] newKeys = new String [keys.length - 1];
        Object [] newValues = new Object [values.length - 1];
        System.arraycopy (keys, 0, newKeys, 0, index);
        System.arraycopy (keys, index + 1, newKeys, index, newKeys.length - index);
        System.arraycopy (values, 0, newValues, 0, index);
        System.arraycopy (values, index + 1, newValues, index, newValues.length - index);
        keys = newKeys;
        values = newValues;
      }
      return;
    }
    
    /* Add the key/value pair */
    if (keys == null) {
      keys = new String [] {key};
      values = new Object [] {value};
      return;
    }
    for (int i=0; i<keys.length; i++) {
      if (keys [i].equals (key)) {
        values [i] = value;
        return;
      }
    }
    String [] newKeys = new String [keys.length + 1];
    Object [] newValues = new Object [values.length + 1];
    System.arraycopy (keys, 0, newKeys, 0, keys.length);
    System.arraycopy (values, 0, newValues, 0, values.length);
    newKeys [keys.length] = key;
    newValues [values.length] = value;
    keys = newKeys;
    values = newValues;
  }

  /**
   * Returns the application defined property of the receiver
   * with the specified name, or null if it has not been set.
   * <p>
   * Applications may have associated arbitrary objects with the
   * receiver in this fashion. If the objects stored in the
   * properties need to be notified when the display is disposed
   * of, it is the application's responsibility to provide a
   * <code>disposeExec()</code> handler which does so.
   * </p>
   *
   * @param key the name of the property
   * @return the value of the property or null if it has not been set
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the key is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   *    created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see #setData(String, Object)
   * @see #disposeExec(Runnable)
   * 
   * @since 1.2
   */
  // [bm]: This is a verbatim copy of SWT, thus no reformatting was done.
  public Object getData( final String key ) {
    checkDevice ();
    if( key == null ) {
      error (SWT.ERROR_NULL_ARGUMENT);
    }
    if (keys == null) return null;
    for (int i=0; i<keys.length; i++) {
      if (keys [i].equals (key)) return values [i];
    }
    return null;
  }
  
  //////////////////
  // Helping methods

  static boolean isValidClass( final Class clazz ) {
//    String name = clazz.getName();
//    int index = name.lastIndexOf( '.' );
//    return name.substring( 0, index + 1 ).equals( PACKAGE_PREFIX );
    return true;
  }

  private void readInitialBounds() {
    HttpServletRequest request = ContextProvider.getRequest();
    String widthVal = request.getParameter( RequestParams.AVAILABLE_WIDTH );
    int width = 1024;
    if( widthVal != null ) {
      width = Integer.parseInt( widthVal );
    }
    String height_val = request.getParameter( RequestParams.AVAILABLE_HEIGHT );
    int height = 768;
    if( height_val != null ) {
      height = Integer.parseInt( height_val );
    }
    bounds = new Rectangle( 0, 0, width, height );
  }

  private IFilterEntry[] getFilterEntries() {
    IFilterEntry[] result = IDisplayAdapter.EMPTY_FILTERS;
    if( filters != null ) {
      result = new IFilterEntry[ filters.size() ];
      filters.toArray( result );
    }
    return result;
  }

  /////////////////
  // Inner classes

  private final class DisplayAdapter implements IDisplayAdapter {

    private final ISessionStore session;

    private DisplayAdapter( final ISessionStore session ) {
      this.session = session;
    }

    public void setBounds( final Rectangle bounds ) {
      if( bounds == null ) {
        SWT.error( SWT.ERROR_NULL_ARGUMENT );
      }
      Display.this.bounds
        = new Rectangle( bounds.x, bounds.y, bounds.width, bounds.height );
    }

    public void setActiveShell( final Shell activeShell ) {
      Display.this.setActiveShell( activeShell );
    }

    public void setFocusControl( final Control focusControl ) {
      Display.this.setFocusControl( focusControl );
    }

    public void invalidateFocus() {
      RWT.getServiceStore().setAttribute( INVALIDATE_FOCUS, Boolean.TRUE );
    }

    public boolean isFocusInvalidated() {
      Object value = RWT.getServiceStore().getAttribute( INVALIDATE_FOCUS );
      return value != null;
    }

    public ISessionStore getSession() {
      return session;
    }

    public IFilterEntry[] getFilters() {
      return getFilterEntries();
    }
  }
}
