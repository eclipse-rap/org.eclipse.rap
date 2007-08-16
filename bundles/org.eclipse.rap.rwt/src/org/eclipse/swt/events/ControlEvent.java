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

package org.eclipse.swt.events;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;


/**
 * Instances of this class are sent as a result of
 * controls being moved or resized.
 *
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of 
 * this class are <em>not</em> part of the RWT public API. They are marked 
 * public only so that they can be shared within the packages provided by RWT. 
 * They should never be accessed from application code.
 * </p>
 * 
 * @see ControlListener
 */
public final class ControlEvent extends TypedEvent {

  public static final int CONTROL_MOVED = SWT.Move;
  public static final int CONTROL_RESIZED = SWT.Resize;
  
  private static final Class LISTENER = ControlListener.class;

  /**
   * Constructs a new instance of this class based on the
   * information in the given untyped event.
   *
   * @param e the untyped event containing the information
   */
  public ControlEvent( final Event event ) {
    this( event.widget, event.type );
  }

  /**
   * Constructs a new instance of this class. 
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed 
   * from application code.
   * </p>
   */
  public ControlEvent( final Object source, final int id ) {
    super( source, id );
  }
  
  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case CONTROL_MOVED:
        ( ( ControlListener )listener ).controlMoved( this );
      break;
      case CONTROL_RESIZED:
        ( ( ControlListener )listener ).controlResized( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }
  
  protected boolean allowProcessing() {
    return true;
  }

  public static void addListener( final Adaptable adaptable, 
                                  final ControlListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final ControlListener listener )
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