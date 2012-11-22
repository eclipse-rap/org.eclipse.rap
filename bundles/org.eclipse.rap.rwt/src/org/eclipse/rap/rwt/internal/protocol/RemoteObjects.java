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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.Operation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.SetOperation;
import org.eclipse.rap.rwt.remote.RemoteObjectAdapter;


public class RemoteObjects {

  public static void readData() {
    List<RemoteObjectAdapter> adapters = RemoteObjectAdapterRegistry.getInstance().getAdapters();
    for( RemoteObjectAdapter adapter : adapters ) {
      dispatchOperations( adapter );
    }
  }

  private static void dispatchOperations( RemoteObjectAdapter adapter ) {
    RemoteObjectSynchronizer<Object> synchronizer = getSynchronizer( adapter );
    List<ClientMessage.Operation> operations = getOperations( adapter );
    for( ClientMessage.Operation operation : operations ) {
      dispatchOperation( adapter, synchronizer, operation );
    }
  }

  private static void dispatchOperation( RemoteObjectAdapter adapter, 
                                         RemoteObjectSynchronizer<Object> synchronizer, 
                                         ClientMessage.Operation operation ) 
  {
    Object remoteObject = ( ( RemoteObjectAdapterImpl )adapter ).getRemoteObject();
    if( operation instanceof SetOperation ) {
      synchronizer.set( remoteObject, getProperties( operation ) );
    } else if( operation instanceof CallOperation ) {
      synchronizer.call( remoteObject, ( ( CallOperation )operation ).getMethodName(), getProperties( operation ) );
    } else if( operation instanceof NotifyOperation ) {
      synchronizer.notify( remoteObject, ( ( NotifyOperation )operation ).getEventName(), getProperties( operation ) );
    }
  }

  private static Map<String, Object> getProperties( ClientMessage.Operation operation ) {
    Map<String, Object> result = new HashMap<String, Object>();
    List<String> propertyNames = operation.getPropertyNames();
    for( String name : propertyNames ) {
      result.put( name, operation.getProperty( name ) );
    }
    return result;
  }

  private static List<ClientMessage.Operation> getOperations( RemoteObjectAdapter adapter ) {
    Operation[] operations = ProtocolUtil.getClientMessage().getAllOperationsFor( adapter.getId() );
    return Arrays.asList( operations );
  }

  public static void render() {
    List<RemoteObjectAdapter> adapters = RemoteObjectAdapterRegistry.getInstance().getAdapters();
    for( RemoteObjectAdapter adapter : adapters ) {
      RemoteObjectAdapterImpl remoteObjectAdapter = ( RemoteObjectAdapterImpl )adapter;
      RemoteObjectSynchronizer<Object> synchronizer = getSynchronizer( adapter );
      renderAdapter( remoteObjectAdapter, synchronizer );
    }
  }

  private static void renderAdapter( RemoteObjectAdapterImpl adapter, RemoteObjectSynchronizer<Object> synchronizer ) {
    if( adapter.isDestroyed() ) {
      synchronizer.destroy( adapter );
      RemoteObjectAdapterRegistry.getInstance().remove( adapter );
    } else {
      ensureAdapterInitialization( adapter, synchronizer );
      synchronizer.render( adapter );
    }
  }

  private static void ensureAdapterInitialization( RemoteObjectAdapterImpl adapter,
                                                   RemoteObjectSynchronizer<Object> synchronizer )
  {
    if( !adapter.isInitialized() ) {
      synchronizer.create( adapter );
      adapter.setInitialized( true );
    }
  }
  
  @SuppressWarnings( "unchecked" )
  private static RemoteObjectSynchronizer<Object> getSynchronizer( RemoteObjectAdapter adapter ) {
    RemoteObjectAdapterImpl adapterImpl = ( RemoteObjectAdapterImpl )adapter;
    RemoteObjectSynchronizerRegistry registry = RemoteObjectSynchronizerRegistry.getInstance();
    Class<? extends Object> type = adapterImpl.getRemoteObject().getClass();
    RemoteObjectSynchronizer<Object> result 
      = ( RemoteObjectSynchronizer<Object> )registry.getSynchronizerForType( type );
    if( result == null ) {
      throw new IllegalStateException( "No synchronizer for type " + type.getName() );
    }
    return result;
  }

  private RemoteObjects() {
    // prevent instantiation
  }
}
