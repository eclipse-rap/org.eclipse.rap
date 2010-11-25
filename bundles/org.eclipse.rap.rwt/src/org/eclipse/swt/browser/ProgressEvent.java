/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.browser;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.internal.widgets.EventUtil;
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

  // TODO [if]: Think about a better solution!
  //            Do not use SWT.None (0) as event handler identifier
  //            -> causes problems with the filter implementation
  public static final int CHANGED = 1;
  public static final int COMPLETED = 2;

  private static final Class LISTENER = ProgressListener.class;

  /** current value */
  public int current;

  /** total value */
  public int total;

  /**
   * Constructs a new instance of this class.
   *
   * @param widget the widget that fired the event
   */
  public ProgressEvent( final Widget widget ) {
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
  public ProgressEvent( final Widget source, final int id ) {
    super( source, id );
  }

  protected void dispatchToObserver( final Object listener ) {
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

  protected Class getListenerType() {
    return LISTENER;
  }

  protected boolean allowProcessing() {
    return EventUtil.isAccessible( widget );
  }

  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }

  public static void addListener( final Adaptable adaptable,
                                  final ProgressListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final ProgressListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
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
           + " current="
           + current
           + " total="
           + total
           + "}";
  }
}
