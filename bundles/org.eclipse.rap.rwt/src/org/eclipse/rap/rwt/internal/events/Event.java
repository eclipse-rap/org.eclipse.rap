/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.events;

import java.util.EventListener;
import java.util.EventObject;

import org.eclipse.rwt.Adaptable;


public abstract class Event extends EventObject {

  private static final long serialVersionUID = 1L;
  
  private final Object source;
  private final int id;
  
  public Event( Object source, int id ) {
    super( source );
    this.source = source;
    this.id = id;
  }
  
  public int getID() {
    return id;
  }
  
  protected static IEventAdapter getEventAdapter( Adaptable adaptable ) {
    return adaptable.getAdapter( IEventAdapter.class );
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

  protected abstract void dispatchToObserver( Object listener );

  protected abstract Class getListenerType();

  protected static boolean hasListener( Adaptable adaptable, Class listenerType ) {
    return getEventAdapter( adaptable ).hasListener( listenerType );
  }

  protected static Object[] getListener( Adaptable adaptable, Class listenerType ) {
    return getEventAdapter( adaptable ).getListener( listenerType );
  }

  protected static void addListener( Adaptable adaptable,
                                     Class listenerType,
                                     EventListener listener )
  {
    getEventAdapter( adaptable ).addListener( listenerType, listener );
  }

  protected static void removeListener( Adaptable adaptable,
                                        Class listenerType,
                                        EventListener listener )
  {
    getEventAdapter( adaptable ).removeListener( listenerType, listener );
  }
}