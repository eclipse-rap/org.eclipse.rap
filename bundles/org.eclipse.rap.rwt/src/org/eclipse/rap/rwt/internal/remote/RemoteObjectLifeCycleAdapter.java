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
package org.eclipse.rap.rwt.internal.remote;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.Operation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.SetOperation;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;


public class RemoteObjectLifeCycleAdapter {

  public static void render() {
    RemoteObjectRegistry registry = RemoteObjectRegistry.getInstance();
    ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
    for( RemoteObjectImpl remoteObject : registry.getRemoteObjects() ) {
      remoteObject.render( writer );
    }
  }

  public static void readData() {
    RemoteObjectRegistry registry = RemoteObjectRegistry.getInstance();
    for( RemoteObjectImpl remoteObject : registry.getRemoteObjects() ) {
      dispatchOperations( remoteObject );
    }
  }

  private static void dispatchOperations( RemoteObjectImpl remoteObject ) {
    List<Operation> operations = getOperations( remoteObject );
    for( Operation operation : operations ) {
      dispatchOperation( remoteObject, operation );
    }
  }

  private static void dispatchOperation( RemoteObjectImpl remoteObject, Operation operation ) {
    if( operation instanceof SetOperation ) {
      remoteObject.handleSet( getProperties( operation ) );
    } else if( operation instanceof CallOperation ) {
      CallOperation callOperation = ( CallOperation )operation;
      remoteObject.handleCall( callOperation.getMethodName(), getProperties( operation ) );
    } else if( operation instanceof NotifyOperation ) {
      NotifyOperation notifyOperation = (NotifyOperation)operation;
      remoteObject.handleNotify( notifyOperation.getEventName(), getProperties( operation ) );
    }
  }

  private static List<ClientMessage.Operation> getOperations( RemoteObjectImpl remoteObject ) {
    ClientMessage message = ProtocolUtil.getClientMessage();
    Operation[] operations = message.getAllOperationsFor( remoteObject.getId() );
    return Arrays.asList( operations );
  }

  private static Map<String, Object> getProperties( ClientMessage.Operation operation ) {
    Map<String, Object> result = new HashMap<String, Object>();
    List<String> propertyNames = operation.getPropertyNames();
    for( String name : propertyNames ) {
      result.put( name, operation.getProperty( name ) );
    }
    return result;
  }

}
