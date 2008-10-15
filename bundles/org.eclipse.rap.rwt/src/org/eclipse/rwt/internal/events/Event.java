/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.events;

import java.util.EventObject;

import org.eclipse.rwt.Adaptable;

public abstract class Event extends EventObject {
  
  private static final long serialVersionUID = 1L;
  
  private final Object source;
  private final int id;
  
  public Event( final Object source, final int id ) {
    super( source );
    this.source = source;
    this.id = id;
  }
  
  public int getID() {
    return id;
  }
  
  protected static IEventAdapter getEventAdapter( final Adaptable adaptable ) {
    return ( IEventAdapter )adaptable.getAdapter( IEventAdapter.class );
  }

  public void processEvent() {
    IEventAdapter eventAdapter = getEventAdapter( getEventSource() );
    if( eventAdapter.hasListener( getListenerType() ) ) {
      Object[] listener = eventAdapter.getListener( getListenerType() );
      for( int i = 0; i < listener.length; i++ ) {
        // TODO: [fappel] Exception handling ? 
        dispatchToObserver( listener[ i ] );
      }
    }
  }

  private Adaptable getEventSource() {
    return ( Adaptable )source;
  }

  protected abstract void dispatchToObserver( final Object listener );

  protected abstract Class getListenerType();

  protected static boolean hasListener( final Adaptable adaptable,
                                        final Class listenerType )
  {
    return getEventAdapter( adaptable ).hasListener( listenerType );
  }

  protected static Object[] getListener( final Adaptable adaptable, 
                                         final Class listenerType )
  {
    return getEventAdapter( adaptable ).getListener( listenerType );
  }

  protected static void addListener( final Adaptable adaptable, 
                                     final Class listenerType, 
                                     final Object listener )
  {
    getEventAdapter( adaptable ).addListener( listenerType, listener );
  }

  protected static void removeListener( final Adaptable adaptable, 
                                        final Class listenerType,
                                        final Object listener )
  {
    getEventAdapter( adaptable ).removeListener( listenerType, listener );
  }
}