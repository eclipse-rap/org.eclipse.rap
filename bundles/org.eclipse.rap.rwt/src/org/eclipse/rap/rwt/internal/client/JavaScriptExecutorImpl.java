/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Kyle Smith - Add evalute method
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.internal.widgets.IdGenerator;


public final class JavaScriptExecutorImpl implements JavaScriptExecutor {

  private static final String REMOTE_ID = "rwt.client.JavaScriptExecutor";
  private final RemoteObject remoteObject;
  private final Map<String, CompletableFuture<String>> futures;

  public JavaScriptExecutorImpl() {
    ConnectionImpl connection = ( ConnectionImpl )RWT.getUISession().getConnection();
    futures = new HashMap<>();
    remoteObject = connection.createServiceObject( REMOTE_ID );
    remoteObject.setHandler( new EvaluateHandler() );
  }

  @Override
  public void execute( String code ) {
    remoteObject.call( "execute", new JsonObject().add( "content", code.trim() ) );
  }

  @Override
  public CompletableFuture<String> evaluate( String code ) {
    CompletableFuture<String> future = new CompletableFuture<>();
    String id = IdGenerator.getInstance( RWT.getUISession() ).createId( future );
    futures.put( id, future );
    remoteObject.call( "evaluate",
                       new JsonObject().add( "futureId", id ).add( "content", code.trim() ) );
    return future;
  }

  private final class EvaluateHandler extends AbstractOperationHandler {

    @Override
    public void handleCall( String method, JsonObject properties ) {
      if( "complete".equals( method ) ) {
        JsonValue idJson = properties.get( "futureId" );
        JsonValue retvalJson = properties.get( "retval" );
        if( idJson != null && idJson.isString() ) {
          CompletableFuture<String> future = futures.get( idJson.asString() );
          if( future != null ) {
            if( retvalJson != null ) {
              future.complete( retvalJson.toString() );
            } else {
              future.complete( null );
            }
            futures.remove( idJson.asString() );
          }
        }
      }
    }
  }
}
