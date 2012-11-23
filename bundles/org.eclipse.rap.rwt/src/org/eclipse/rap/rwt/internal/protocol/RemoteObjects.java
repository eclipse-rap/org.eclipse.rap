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
import org.eclipse.rap.rwt.remote.RemoteObject;


public class RemoteObjects {

  public static void readData() {
    List<RemoteObject> remoteObjects = RemoteObjectRegistry.getInstance().getRemoteObjects();
    for( RemoteObject remoteObject : remoteObjects ) {
      dispatchOperations( remoteObject );
    }
  }

  private static void dispatchOperations( RemoteObject remoteObject ) {
    RemoteObjectSynchronizer<Object> synchronizer = getSynchronizer( remoteObject );
    List<ClientMessage.Operation> operations = getOperations( remoteObject );
    for( ClientMessage.Operation operation : operations ) {
      dispatchOperation( remoteObject, synchronizer, operation );
    }
  }

  private static void dispatchOperation( RemoteObject remoteObject, 
                                         RemoteObjectSynchronizer<Object> synchronizer, 
                                         ClientMessage.Operation operation ) 
  {
    Object object = ( ( RemoteObjectImpl )remoteObject ).getObject();
    if( operation instanceof SetOperation ) {
      synchronizer.set( object, getProperties( operation ) );
    } else if( operation instanceof CallOperation ) {
      synchronizer.call( object, ( ( CallOperation )operation ).getMethodName(), getProperties( operation ) );
    } else if( operation instanceof NotifyOperation ) {
      synchronizer.notify( object, ( ( NotifyOperation )operation ).getEventName(), getProperties( operation ) );
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

  private static List<ClientMessage.Operation> getOperations( RemoteObject adapter ) {
    Operation[] operations = ProtocolUtil.getClientMessage().getAllOperationsFor( adapter.getId() );
    return Arrays.asList( operations );
  }

  public static void render() {
    List<RemoteObject> remoteObjects = RemoteObjectRegistry.getInstance().getRemoteObjects();
    for( RemoteObject remoteObject : remoteObjects ) {
      RemoteObjectImpl remoteObjectImpl = ( RemoteObjectImpl )remoteObject;
      RemoteObjectSynchronizer<Object> synchronizer = getSynchronizer( remoteObject );
      renderAdapter( remoteObjectImpl, synchronizer );
    }
  }

  private static void renderAdapter( RemoteObjectImpl remoteObject, RemoteObjectSynchronizer<Object> synchronizer ) {
    if( remoteObject.isDestroyed() ) {
      synchronizer.destroy( remoteObject );
      RemoteObjectRegistry.getInstance().remove( remoteObject );
    } else {
      ensureAdapterInitialization( remoteObject, synchronizer );
      synchronizer.render( remoteObject );
    }
  }

  private static void ensureAdapterInitialization( RemoteObjectImpl remoteObject,
                                                   RemoteObjectSynchronizer<Object> synchronizer )
  {
    if( !remoteObject.isInitialized() ) {
      synchronizer.create( remoteObject );
      remoteObject.setInitialized( true );
    }
  }
  
  @SuppressWarnings( "unchecked" )
  private static RemoteObjectSynchronizer<Object> getSynchronizer( RemoteObject remoteObject ) {
    RemoteObjectImpl remoteObjectImpl = ( RemoteObjectImpl )remoteObject;
    RemoteObjectSynchronizerRegistry registry = RemoteObjectSynchronizerRegistry.getInstance();
    Class<? extends Object> type = remoteObjectImpl.getObject().getClass();
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
