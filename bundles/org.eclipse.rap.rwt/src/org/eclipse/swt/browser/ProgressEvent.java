/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.browser;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.widgets.Widget;


/**
 * A <code>ProgressEvent</code> is sent by a {@link Browser} to
 * {@link ProgressListener}'s when a progress is made during the loading of the
 * current URL or when the loading of the current URL has been completed.
 *
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of
 * this class are <em>not</em> part of the RWT public API. They are marked
 * public only so that they can be shared within the packages provided by RWT.
 * They should never be accessed from application code.
 * </p>
 *
 * @since 1.4
 */
public class ProgressEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int CHANGED = EventTypes.PROGRESS_CHANGED;
  public static final int COMPLETED = EventTypes.PROGRESS_COMPLETED;

  private static final Class LISTENER = ProgressListener.class;
  private static final int[] EVENT_TYPES = { CHANGED, COMPLETED };

  /** current value */
  public int current;

  /** total value */
  public int total;

  /**
   * Constructs a new instance of this class.
   *
   * @param widget the widget that fired the event
   */
  public ProgressEvent( Widget widget ) {
    super( widget );
  }

  /**
   * Constructs a new instance of this class.
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public ProgressEvent( Widget source, int id ) {
    super( source, id );
  }

  @Override
  protected void dispatchToObserver( Object listener ) {
    switch( getID() ) {
      case CHANGED:
        ( ( ProgressListener )listener ).changed( this );
      break;
      case COMPLETED:
        ( ( ProgressListener )listener ).completed( this );
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
    return true;
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
  public static void addListener( Adaptable adaptable, ProgressListener listener ) {
    addListener( adaptable, EVENT_TYPES, listener );
  }

  /**
   * @since 2.0
   * @deprecated not part of the API, do not use in application code
   */
  @Deprecated
  public static void removeListener( Adaptable adaptable, ProgressListener listener ) {
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
           + " current="
           + current
           + " total="
           + total
           + "}";
  }
}
