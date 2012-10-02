/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;


/**
 * Instances of this class are sent as a result of widgets gaining and losing
 * focus.
 *
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of
 * this class are <em>not</em> part of the RWT public API. They are marked
 * public only so that they can be shared within the packages provided by RWT.
 * They should never be accessed from application code.
 * </p>
 *
 * @see FocusListener
 */
public final class FocusEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int FOCUS_GAINED = SWT.FocusIn;
  public static final int FOCUS_LOST = SWT.FocusOut;

  private static final Class LISTENER = FocusListener.class;
  private static final int[] EVENT_TYPES = { FOCUS_GAINED, FOCUS_LOST };

  /**
   * Constructs a new instance of this class based on the
   * information in the given untyped event.
   *
   * @param event the untyped event containing the information
   */
  public FocusEvent( Event event ) {
    super( event );
  }

  /**
   * Constructs a new instance of this class.
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public FocusEvent( Control source, int id ) {
    super( source, id );
  }

  @Override
  protected void dispatchToObserver( Object listener ) {
    switch( getID() ) {
      case FOCUS_GAINED:
        ( ( FocusListener )listener ).focusGained( this );
      break;
      case FOCUS_LOST:
        ( ( FocusListener )listener ).focusLost( this );
        break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  @Override
  protected Class getListenerType() {
    return LISTENER;
  }

  @Override
  protected boolean allowProcessing() {
    return EventUtil.isAccessible( widget );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void addListener( Adaptable adaptable, FocusListener listener ) {
    addListener( adaptable, EVENT_TYPES, listener );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void removeListener( Adaptable adaptable, FocusListener listener ) {
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

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static Object[] getListeners( Adaptable adaptable ) {
    return getListener( adaptable, EVENT_TYPES );
  }
}
