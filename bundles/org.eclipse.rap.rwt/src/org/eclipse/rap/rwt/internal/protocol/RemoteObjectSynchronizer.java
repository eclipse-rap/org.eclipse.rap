/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getProtocolWriter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.remote.EventHandler;
import org.eclipse.rap.rwt.remote.MethodHandler;
import org.eclipse.rap.rwt.remote.PropertyHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;


public class RemoteObjectSynchronizer<T> {
  
  private final RemoteObjectDefinitionImpl<T> definition;
  private final String type;

  public RemoteObjectSynchronizer( RemoteObjectDefinitionImpl<T> definition, String type ) {
    this.definition = definition;
    this.type = type;
  }
  
  public Class<T> getType() {
    return definition.getType();
  }

  public void set( T object, Map<String, Object> setProperties ) {
    for( Entry<String, Object> entry : setProperties.entrySet() ) {
      PropertyHandler<T> propertyHandler = definition.getProperty( entry.getKey() );
      if( propertyHandler != null ) {
        propertyHandler.set( object, entry.getValue() );
      } else {
        throw new IllegalStateException( "Property " 
                                         + entry.getKey() 
                                         + " is not configured for type " 
                                         + object.getClass().getName() );
      }
    }
  }

  public void call( T object, String methodName, Map<String, Object> callProperties ) {
    MethodHandler<T> methodHandler = definition.getMethod( methodName );
    if( methodHandler != null ) {
      methodHandler.call( object, callProperties );
    } else {
      throw new IllegalStateException( "Method "
                                       + methodName
                                       + " is not configured for type "
                                       + object.getClass().getName() );
    }
  }

  public void notify( T object, String eventName, Map<String, Object> properties ) {
    EventHandler<T> eventHandler = definition.getEventHandler( eventName );
    if( eventHandler != null ) {
      notifyEventInProcessActionPhase( object, properties, eventHandler );
    } else {
      throw new IllegalStateException( "Event "
                                       + eventName
                                       + " is not configured for type "
                                       + object.getClass().getName() );
    }
  }

  private void notifyEventInProcessActionPhase( final T object,
                                                final Map<String, Object> properties,
                                                final EventHandler<T> eventHandler )
  {
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        eventHandler.notify( object, properties );
      }
    } );
  }

  public void create( RemoteObject adapter ) {
    getProtocolWriter().appendCreate( adapter.getId(), type );
  }

  public void render( RemoteObject remoteObject ) {
    RemoteObjectImpl<?> remoteObjectImpl = ( RemoteObjectImpl )remoteObject;
    List<Runnable> renderQueue = remoteObjectImpl.getRenderQueue();
    for( Runnable runnable : renderQueue ) {
      runnable.run();
    }
    renderQueue.clear();
  }

  public void destroy( RemoteObject remoteObject ) {
    getProtocolWriter().appendDestroy( remoteObject.getId() );
  }
}
