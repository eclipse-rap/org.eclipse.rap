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

import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.remote.Call;
import org.eclipse.rap.rwt.remote.EventNotification;
import org.eclipse.rap.rwt.remote.Property;
import org.eclipse.rap.rwt.remote.RemoteObjectAdapter;


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
    List<Property<T>> objectProperties = definition.getProperties();
    for( Property<T> objectProperty : objectProperties ) {
      Object newValue = setProperties.get( objectProperty.getName() );
      if( newValue != null ) {
        objectProperty.set( object, newValue );
      }
    }
  }

  public void call( T object, String methodName, Map<String, Object> callProperties ) {
    List<Call<T>> calls = definition.getCalls();
    for( Call<T> call : calls ) {
      if( call.getName().equals( methodName ) ) {
        call.call( object, callProperties );
      }
    }
  }

  public void notify( T object, String eventName, Map<String, Object> properties ) {
    List<EventNotification<T>> events = definition.getEvents();
    for( EventNotification<T> event : events ) {
      if( event.getName().equals( eventName ) ) {
        notifyEventInProcessActionPhase( object, properties, event );
      }
    }
  }

  private void notifyEventInProcessActionPhase( final T object,
                                                final Map<String, Object> properties,
                                                final EventNotification<T> event )
  {
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        event.notify( object, properties );
      }
    } );
  }

  public void create( RemoteObjectAdapter adapter ) {
    getProtocolWriter().appendCreate( adapter.getId(), type );
  }

  public void render( RemoteObjectAdapter adapter ) {
    RemoteObjectAdapterImpl<?> adapterImpl = ( RemoteObjectAdapterImpl )adapter;
    List<Runnable> renderQueue = adapterImpl.getRenderQueue();
    for( Runnable runnable : renderQueue ) {
      runnable.run();
    }
    renderQueue.clear();
  }

  public void destroy( RemoteObjectAdapter adapter ) {
    getProtocolWriter().appendDestroy( adapter.getId() );
  }
}
