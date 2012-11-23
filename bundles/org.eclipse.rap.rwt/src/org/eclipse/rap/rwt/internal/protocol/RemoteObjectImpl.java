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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.remote.RemoteObjectSpecification;
import org.eclipse.swt.internal.widgets.IdGenerator;


public class RemoteObjectImpl<T> implements RemoteObject {

  private final String id;
  private boolean initialized;
  private boolean destroyed;
  private final T remoteObject;
  private List<Runnable> queue;
  private HashMap<String, Boolean> listeners;

  public RemoteObjectImpl( T remoteObject, Class<? extends RemoteObjectSpecification<T>> specificationType ) {
    this( remoteObject, specificationType, "o" );
  }

  @SuppressWarnings( "unchecked" )
  public RemoteObjectImpl( T remoteObject,
                                  Class<? extends RemoteObjectSpecification<T>> specifierType,
                                  String customPrefix )
  {
    this.id = IdGenerator.getInstance().newId( customPrefix );
    this.remoteObject = remoteObject;
    this.queue = new ArrayList<Runnable>();
    this.listeners = new HashMap<String, Boolean>();
    if( specifierType != null ) {
      Class<T> remoteType = ( Class<T> )remoteObject.getClass();
      // TODO: Think about register implementation. Second call does nothing!
      RemoteObjectSynchronizerRegistry.getInstance().register( remoteType, specifierType );
      RemoteObjectRegistry.getInstance().register( this );
    }
  }

  public String getId() {
    return id;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized( boolean initialized ) {
    this.initialized = initialized;
  }

  public void destroy() {
    destroyed = true;
  }

  public boolean isDestroyed() {
    return destroyed;
  }

  public T getObject() {
    return remoteObject;
  }

  public void call( final String methodName, final Map<String, Object> properties ) {
    ParamCheck.notNullOrEmpty( methodName, "Methodname" );
    Runnable appendCallRunnable = new Runnable() {
      public void run() {
        getProtocolWriter().appendCall( getId(), methodName, properties );
      }
    };
    queue.add( appendCallRunnable );
  }

  public void set( final String propertyName, final String propertyValue ) {
    ParamCheck.notNullOrEmpty( propertyName, "Property Name" );
    ParamCheck.notNull( propertyValue, "Property Value" );
    if( !PhaseId.READ_DATA.equals( CurrentPhase.get() ) ) {
      Runnable appendSetRunnable = new Runnable() {
        public void run() {
          getProtocolWriter().appendSet( getId(), propertyName, propertyValue );
        }
      };
      queue.add( appendSetRunnable );
    }
  }

  public void listen( String eventName, boolean shoudlListen ) {
    ParamCheck.notNullOrEmpty( eventName, "Eventname" );
    Boolean oldShoudListenEntry = listeners.get( eventName );
    if( oldShoudListenEntry == null || oldShoudListenEntry.booleanValue() != shoudlListen ) {
      appendListenRunnable( eventName, shoudlListen );
      listeners.put( eventName, Boolean.valueOf( shoudlListen ) );
    }
  }

  private void appendListenRunnable( final String eventName, final boolean shoudlListen ) {
    Runnable appendListenRunnable = new Runnable() {
      public void run() {
        getProtocolWriter().appendListen( getId(), eventName, shoudlListen );
      }
    };
    queue.add( appendListenRunnable );
  }

  public List<Runnable> getRenderQueue() {
    return queue;
  }

}
