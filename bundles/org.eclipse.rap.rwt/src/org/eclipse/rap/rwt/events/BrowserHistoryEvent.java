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
import org.eclipse.swt.internal.events.EventTypes;


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

  public static final int NAVIGATED = EventTypes.BROWSER_HISTORY_NAVIGATED;

  private static final Class LISTENER = BrowserHistoryListener.class;
  private static final int[] EVENT_TYPES = { NAVIGATED };

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
    addListener( adaptable, EVENT_TYPES, listener );
  }

  public static void removeListener( Adaptable adaptable, BrowserHistoryListener listener ) {
    removeListener( adaptable, EVENT_TYPES, listener );
  }

  public static boolean hasListener( Adaptable adaptable ) {
    return hasListener( adaptable, EVENT_TYPES );
  }

  public static Object[] getListeners( Adaptable adaptable ) {
    return getListener( adaptable, EVENT_TYPES );
  }
}
