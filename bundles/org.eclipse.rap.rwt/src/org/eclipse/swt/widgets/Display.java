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

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import com.w4t.Adaptable;
import com.w4t.W4TContext;
import com.w4t.engine.requests.RequestParams;
import com.w4t.engine.service.ContextProvider;

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
 * @see #readAndDispatch
 * @see #sleep
 * @see Device#dispose
 */
// TODO: [doc] Update display javadoc
public class Display extends Device implements Adaptable {

  private static final String DISPLAY_ID = "org.eclipse.swt.display";

  /**
   * Returns the display which the currently running thread is
   * the user-interface thread for, or null if the currently
   * running thread is not a user-interface thread for any display.
   *
   * @return the current display
   */
  public static Display getCurrent() {
    return ( Display )ContextProvider.getSession().getAttribute( DISPLAY_ID );
  }
  
  private final List shells;
 
  private Rectangle bounds;
  private Shell activeShell;
  private IDisplayAdapter displayAdapter;
  public Control focusControl;



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
    HttpSession session = ContextProvider.getSession();
    if( getCurrent() != null ) {
      String msg = "Currently only one display per session is supported.";
      throw new IllegalStateException( msg );
    }
    session.setAttribute( DISPLAY_ID, this );
    shells = new ArrayList(); 
    readInitialBounds();
  }

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
    return new Rectangle( bounds );
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
      if( this.focusControl != null ) {
        FocusEvent event = FocusEvent.focusLost( this.focusControl );
        event.processEvent();
      }
      this.focusControl = focusControl;
      if( this.focusControl != null ) {
        FocusEvent event = FocusEvent.focusGained( this.focusControl );
        event.processEvent();
      }
    }
  }

  ///////////////////////////
  // Systen colors and images
  
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
   * 
   * @since 1.0
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
   * 
   * @since 1.0
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
   * 
   * @since 1.0
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
   * 
   * @since 1.0.2
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
    if( from != null ) {
      Control currentFrom = from;
      do {
        Rectangle bounds = currentFrom.getBounds();
        newX += bounds.x;
        newY += bounds.y;
        currentFrom = currentFrom.getParent();
      } while( currentFrom != null );
    }
    
    if( to != null ) {
      Control currentTo = to;
      do {
        Rectangle bounds = currentTo.getBounds();
        newX -= bounds.x;
        newY -= bounds.y;
        currentTo = currentTo.getParent();
      } while( currentTo != null );
    }
    return new Rectangle( newX, newY, width, height );
  }
  
  //////////
  // Dispose
  
  // TODO [rh] This is preliminary!
  public void dispose() {
    ContextProvider.getSession().removeAttribute( DISPLAY_ID );
  }
  
  /////////////////////
  // Adaptable override

  public Object getAdapter( final Class adapter ) {
    Object result = null;
    if( adapter == IDisplayAdapter.class ) {
      if( displayAdapter == null ) {
        displayAdapter = new DisplayAdapter();
      }
      result = displayAdapter;
    } else {
      result = W4TContext.getAdapterManager().getAdapter( this, adapter );  
    }
    return result;
  }

  ///////////////////
  // Shell management
  
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
  
  ////////////////
  // Inner classes

  private final class DisplayAdapter implements IDisplayAdapter {

    public void setBounds( final Rectangle bounds ) {
      if( bounds == null ) {
        SWT.error( SWT.ERROR_NULL_ARGUMENT );
      }
      Display.this.bounds = new Rectangle( bounds );
    }
    
    public void setActiveShell( final Shell activeShell ) {
      Display.this.setActiveShell( activeShell );
    }

    public void setFocusControl( final Control focusControl ) {
      Display.this.setFocusControl( focusControl );
    }
  }
  
  //////////////////
  // Helping methods
  
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
}
