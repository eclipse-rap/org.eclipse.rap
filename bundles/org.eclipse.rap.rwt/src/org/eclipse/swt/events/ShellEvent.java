/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.widgets.Event;


/**
 * Instances of this class are sent as a result of
 * operations being performed on shells.
 *
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of
 * this class are <em>not</em> part of the RWT public API. They are marked
 * public only so that they can be shared within the packages provided by RWT.
 * They should never be accessed from application code.
 * </p>
 *
 * @see ShellListener
 */
public final class ShellEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int SHELL_CLOSED = SWT.Close;
  public static final int SHELL_ACTIVATED = SWT.Activate;
  public static final int SHELL_DEACTIVATED = SWT.Deactivate;

  private static final Class LISTENER = ShellListener.class;
  private static final int[] EVENT_TYPES = { SHELL_CLOSED, SHELL_ACTIVATED, SHELL_DEACTIVATED };

  /**
   * A flag indicating whether the operation should be allowed.
   * Setting this field to <code>false</code> will cancel the operation.
   */
  public boolean doit;

  /**
   * Constructs a new instance of this class based on the
   * information in the given untyped event.
   *
   * @param event the untyped event containing the information
   */
  public ShellEvent( Event event ) {
    super( event );
    doit = true;
  }

  /**
   * Constructs a new instance of this class.
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public ShellEvent( Object source, int id ) {
    super( source, id );
    doit = true;
  }

  @Override
  protected void dispatchToObserver( Object listener ) {
    switch( getID() ) {
      case SHELL_CLOSED:
        ( ( ShellListener )listener ).shellClosed( this );
        break;
      case SHELL_ACTIVATED:
        ( ( ShellListener )listener ).shellActivated( this );
        break;
      case SHELL_DEACTIVATED:
        ( ( ShellListener )listener ).shellDeactivated( this );
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
    boolean result;
    if( getID() == SHELL_CLOSED ) {
      result = EventUtil.isAccessible( widget );
    } else {
      result = true;
    }
    return result;
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
  public static void addListener( Adaptable adaptable, ShellListener listener ) {
    addListener( adaptable, EVENT_TYPES, listener );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void removeListener( Adaptable adaptable, ShellListener listener ) {
    removeListener( adaptable, EVENT_TYPES, listener );
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
