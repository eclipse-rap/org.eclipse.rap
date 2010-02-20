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
 * Instances of this class are sent as a result of
 * a widget such as a menu item being armed.
 *
 * @see ArmListener
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 *
 * @since 1.3
 */

public final class ArmEvent extends TypedEvent {

  private static final long serialVersionUID = 3258126964249212217L;

	private static final int WIDGET_ARMED = SWT.Arm;

	private static final Class LISTENER = ArmListener.class;

  /**
   * Constructs a new instance of this class based on the
   * information in the given untyped event.
   *
   * @param event the untyped event containing the information
   */
  public ArmEvent( final Event event ) {
  	this( event.widget );
  }

  /**
   * Constructs a new instance of this class.
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public ArmEvent( final Widget source ) {
    super( source, WIDGET_ARMED );
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case WIDGET_ARMED:
        ( ( ArmListener )listener ).widgetArmed( this );
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
                                  final ArmListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final ArmListener listener )
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
