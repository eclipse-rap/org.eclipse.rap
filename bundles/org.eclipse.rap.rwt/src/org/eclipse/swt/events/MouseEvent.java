/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;


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
public class MouseEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  private static final int[] EVENT_TYPES = { SWT.MouseDown, SWT.MouseUp, SWT.MouseDoubleClick };

  /**
   * the button that was pressed or released; 1 for the
   * first button, 2 for the second button, and 3 for the
   * third button, etc.
   */
  public int button;

  /**
   * the state of the keyboard modifier keys at the time
   * the event was generated
   *
   * @since 1.3
   */
  public int stateMask;

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
   *
   * @since 1.2
   */
  public MouseEvent( Event event ) {
    super( event );
    x = event.x;
    y = event.y;
    button = event.button;
    stateMask = event.stateMask;
  }

  /**
   * Returns a string containing a concise, human-readable description of the
   * receiver.
   *
   * @return a string representation of the event
   */
  @Override
  public String toString() {
    String string = super.toString();
    return string.substring( 0, string.length() - 1 ) // remove trailing '}'
           + " button="
           + button
           + " stateMask="
           + stateMask
           + " x="
           + x
           + " y="
           + y
//           + " count="
//           + count
           + "}";
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void addListener( Adaptable adaptable, MouseListener listener ) {
    addListener( adaptable, EVENT_TYPES, listener );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void removeListener( Adaptable adaptable, MouseListener listener ) {
    removeListener( adaptable, EVENT_TYPES, listener );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static boolean hasListener( Adaptable adaptable ) {
    return hasListener( adaptable, EVENT_TYPES );
  }

}
