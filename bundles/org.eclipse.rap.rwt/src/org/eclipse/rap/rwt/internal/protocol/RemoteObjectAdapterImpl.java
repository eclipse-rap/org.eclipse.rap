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
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.remote.RemoteObjectAdapter;
import org.eclipse.rap.rwt.remote.RemoteObjectSpecifier;
import org.eclipse.swt.internal.widgets.IdGenerator;


public class RemoteObjectAdapterImpl<T> implements RemoteObjectAdapter {

  private final String id;
  private boolean initialized;
  private boolean destroyed;
  private final T remoteObject;
  private List<Runnable> queue;
  private HashMap<String, Boolean> listeners;

  public RemoteObjectAdapterImpl( T remoteObject, Class<? extends RemoteObjectSpecifier<T>> specifierType ) {
    this( remoteObject, specifierType, "o" );
  }

  @SuppressWarnings( "unchecked" )
  public RemoteObjectAdapterImpl( T remoteObject,
                                  Class<? extends RemoteObjectSpecifier<T>> specifierType,
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
      RemoteObjectAdapterRegistry.getInstance().register( this );
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

  public T getRemoteObject() {
    return remoteObject;
  }

  public void call( final String methodName, final Map<String, Object> properties ) {
    Runnable appendCallRunnable = new Runnable() {
      public void run() {
        getProtocolWriter().appendCall( getId(), methodName, properties );
      }
    };
    queue.add( appendCallRunnable );
  }

  public void set( final String propertyName, final String propertyValue ) {
    // TODO: Also accessible from Threads without any phase?
    if( !CurrentPhase.get().equals( PhaseId.READ_DATA ) ) {
      Runnable appendSetRunnable = new Runnable() {
        public void run() {
          getProtocolWriter().appendSet( getId(), propertyName, propertyValue );
        }
      };
      queue.add( appendSetRunnable );
    }
  }

  public void listen( String eventName, boolean shoudlListen ) {
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
