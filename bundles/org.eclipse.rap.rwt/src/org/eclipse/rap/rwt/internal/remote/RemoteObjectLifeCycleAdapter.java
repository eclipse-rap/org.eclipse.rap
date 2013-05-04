/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getClientMessage;

import java.util.List;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.Operation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.SetOperation;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;


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
    if( !operations.isEmpty() ) {
      OperationHandler handler = getHandler( remoteObject );
      for( Operation operation : operations ) {
        dispatchOperation( handler, operation );
      }
    }
  }

  public static OperationHandler getHandler( RemoteObjectImpl remoteObject ) {
    OperationHandler handler = remoteObject.getHandler();
    if( handler == null ) {
      String message = "No operation handler registered for remote object: "
                       + remoteObject.getId();
      throw new UnsupportedOperationException( message );
    }
    return handler;
  }

  private static List<ClientMessage.Operation> getOperations( RemoteObject remoteObject ) {
    return getClientMessage().getAllOperationsFor( remoteObject.getId() );
  }

  public static void dispatchOperation( OperationHandler handler, Operation operation ) {
    if( operation instanceof SetOperation ) {
      handler.handleSet( operation.getProperties() );
    } else if( operation instanceof CallOperation ) {
      CallOperation callOperation = ( CallOperation )operation;
      handler.handleCall( callOperation.getMethodName(), operation.getProperties() );
    } else if( operation instanceof NotifyOperation ) {
      NotifyOperation notifyOperation = ( NotifyOperation )operation;
      scheduleHandleNotify( handler, notifyOperation.getEventName(), operation.getProperties() );
    }
  }

  private static void scheduleHandleNotify( final OperationHandler handler,
                                            final String event,
                                            final JsonObject properties )
  {
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        handler.handleNotify( event, properties );
      }
    } );
  }

}
