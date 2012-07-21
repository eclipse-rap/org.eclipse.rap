/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Ralf Zahn (ARS) - browser history support (Bug 283291)
 ******************************************************************************/
package org.eclipse.rap.rwt.events;

import org.eclipse.rap.rwt.*;
import org.eclipse.rap.rwt.internal.events.Event;


/**
 * Instances of this class provide information about a browser history
 * navigation event.
 *
 * @see BrowserHistoryListener
 * @see org.eclipse.rap.rwt.IBrowserHistory
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public final class BrowserHistoryEvent extends Event {

  public static final int NAVIGATED = 40;

  private static final long serialVersionUID = 1L;
  private static final Class LISTENER = BrowserHistoryListener.class;

  /**
   * The browser history entry to which the user navigated.
   */
  public String entryId;

  public BrowserHistoryEvent( Object source, String entryId ) {
    super( source, NAVIGATED );
    this.entryId = entryId;
  }

  @Override
  protected void dispatchToObserver( Object listener ) {
    switch( getID() ) {
      case NAVIGATED:
        ( ( BrowserHistoryListener )listener ).navigated( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  @Override
  protected Class getListenerType() {
    return LISTENER;
  }

  public static void addListener( Adaptable adaptable, BrowserHistoryListener listener ) {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( Adaptable adaptable, BrowserHistoryListener listener ) {
    removeListener( adaptable, LISTENER, listener );
  }

  public static boolean hasListener( Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }

  public static Object[] getListeners( Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
