/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.events;


import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class are sent whenever the platform-
 * specific trigger for showing a context menu is detected.
 *
 * @see MenuDetectListener
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 *
 * @since 1.3
 */

public final class MenuDetectEvent extends TypedEvent {

  private static final long serialVersionUID = -3061660596590828941L;
  
  private static final int MENU_DETECT = SWT.MenuDetect;
  
  private static final Class LISTENER = MenuDetectListener.class;

  /**
	 * the display-relative x coordinate of the pointer
	 * at the time the context menu trigger occurred
	 */
  public int x;

	/**
	 * the display-relative y coordinate of the pointer
	 * at the time the context menu trigger occurred
	 */
  public int y;
  
  /**
   * A flag indicating whether the operation should be allowed. Setting this
   * field to <code>false</code> will cancel the operation.
   */
  public boolean doit;
  

  /**
   * Constructs a new instance of this class based on the
   * information in the given untyped event.
   *
   * @param e the untyped event containing the information
   */
  public MenuDetectEvent( final Event event ) {
    super( event.widget, event.type );
    this.x = event.x;
    this.y = event.y;
    this.doit = event.doit;
  }

  /**
   * Returns a string containing a concise, human-readable
   * description of the receiver.
   *
   * @return a string representation of the event
   */
  public String toString() {
  	String string = super.toString ();
  	return string.substring (0, string.length() - 1) // remove trailing '}'
  		+ " x=" + x
  		+ " y=" + y
  		+ " doit=" + doit
  		+ "}";
  }

  /**
   * Constructs a new instance of this class.
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public MenuDetectEvent( final Widget source ) {
    super( source, MENU_DETECT );
    doit = true;
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case MENU_DETECT:
        ( ( MenuDetectListener )listener ).menuDetected( this );
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

  public static void addListener( final Adaptable adaptable,
                                  final MenuDetectListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final MenuDetectListener listener )
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
