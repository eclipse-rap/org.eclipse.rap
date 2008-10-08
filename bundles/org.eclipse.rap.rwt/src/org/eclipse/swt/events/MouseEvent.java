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
package org.eclipse.swt.events;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


/**
 * Instances of this class are sent whenever mouse
 * related actions occur. This includes mouse buttons
 * being pressed and released, the mouse pointer being 
 * moved and the mouse pointer crossing widget boundaries.
 * <p>
 * Note: The <code>button</code> field is an integer that
 * represents the mouse button number.  This is not the same
 * as the <code>SWT</code> mask constants <code>BUTTONx</code>.
 * </p>
 *
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of 
 * this class are <em>not</em> part of the RWT public API. They are marked 
 * public only so that they can be shared within the packages provided by RWT. 
 * They should never be accessed from application code.
 * </p>
 * 
 * @see MouseListener
 * @see MouseMoveListener
 * @see MouseTrackListener
 * 
 * @since 1.1
 */
public final class MouseEvent extends TypedEvent {

  public static final int MOUSE_DOWN = SWT.MouseDown;
  public static final int MOUSE_UP = SWT.MouseUp;
  public static final int MOUSE_DOUBLE_CLICK = SWT.MouseDoubleClick;
  
  private static final Class LISTENER = MouseListener.class;

  /**
   * the button that was pressed or released; 1 for the
   * first button, 2 for the second button, and 3 for the
   * third button, etc.
   */
  public int button;
  
//  /**
//   * the state of the keyboard modifier keys at the time
//   * the event was generated
//   */
//  public int stateMask;
  
  /**
   * the widget-relative, x coordinate of the pointer
   * at the time the mouse button was pressed or released
   */
  public int x;
  
  /**
   * the widget-relative, y coordinate of the pointer
   * at the time the mouse button was pressed or released
   */ 
  public int y;
  
//  /**
//   * the number times the mouse has been clicked, as defined
//   * by the operating system; 1 for the first click, 2 for the
//   * second click and so on.
//   * 
//   * @since 3.3
//   */
//  public int count;

  /**
   * Constructs a new instance of this class based on the
   * information in the given untyped event.
   *
   * @param event the untyped event containing the information
   */
  public MouseEvent( final Event event ) {
    this( event.widget, event.type );
    this.x = event.x;
    this.y = event.y;
    this.button = event.button;
  }
  
  /**
   * Constructs a new instance of this class. 
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed 
   * from application code.
   * </p>
   */
  public MouseEvent( final Widget source, final int id ) {
    super( source, id );
  }
  
  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case MOUSE_UP:
        ( ( MouseListener )listener ).mouseUp( this );
        break;
      case MOUSE_DOWN:
        ( ( MouseListener )listener ).mouseDown( this );
        break;
      case MOUSE_DOUBLE_CLICK:
        ( ( MouseListener )listener ).mouseDoubleClick( this );
        break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  protected boolean allowProcessing() {
    return EventUtil.isAccessible( widget );
  }

  /**
   * Returns a string containing a concise, human-readable description of the
   * receiver.
   * 
   * @return a string representation of the event
   */
  public String toString() {
    String string = super.toString();
    return string.substring( 0, string.length() - 1 ) // remove trailing '}'
           + " button="
           + button
//           + " stateMask="
//           + stateMask
           + " x="
           + x
           + " y="
           + y
//           + " count="
//           + count
           + "}";
  }

  public static void addListener( final Adaptable adaptable, 
                                  final MouseListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final MouseListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }
  
  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }
  
  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
