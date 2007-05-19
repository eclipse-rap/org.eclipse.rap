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

import org.eclipse.swt.widgets.Control;
import com.w4t.Adaptable;

/**
 * TODO: [rh] JavaDoc
 */
public final class ActivateEvent extends TypedEvent {

  public static final int ACTIVATED = 0;
  public static final int DEACTIVATED = 1;
  
  private static final Class LISTENER = ActivateListener.class;


  public ActivateEvent( final Control source, final int id ) {
    super( source, id );
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case ACTIVATED:
        ( ( ActivateListener )listener ).activated( this );
      break;
      case DEACTIVATED:
        ( ( ActivateListener )listener ).deactivated( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  public static void addListener( final Adaptable adaptable, 
                                  final ActivateListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final ActivateListener listener )
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