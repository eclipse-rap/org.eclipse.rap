/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import java.io.IOException;
import java.net.*;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.cluster.test.SessionFailover_Test.SerializableRunnable;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Synchronizer;


public class AsyncExecEntryPoint implements IEntryPoint {

  private static final String ATTRIBUTE_NAME = "foo";
  private static final Boolean ATTRIBUTE_VALUE = Boolean.TRUE;

  public static void scheduleAsyncRunnable( final Display display ) {
    display.asyncExec( new SerializableRunnable() {
      private static final long serialVersionUID = 1L;
      public void run() {
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
          public void run() {
            RWT.getSessionStore().setAttribute( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );
          }
        } );
      }
    } );
  }
  
  public static boolean wasAsyncRunnableExecuted( ISessionStore sessionStore ) {
    return ATTRIBUTE_VALUE.equals( sessionStore.getAttribute( ATTRIBUTE_NAME ) ); 
  }

  public int createUI() {
    AsyncExecServiceHandler serviceHandler = new AsyncExecServiceHandler();
    RWT.getServiceManager().registerServiceHandler( AsyncExecServiceHandler.ID, serviceHandler );
    
    Display display = new Display();
    display.setSynchronizer( new ClusteredSynchronizer( display ) );
    
    return 0;
  }
  
  private static class ClusteredSynchronizer extends Synchronizer {
    private static final long serialVersionUID = 1L;

    private final String requestUrl;
    
    ClusteredSynchronizer( Display display ) {
      super( display );
      requestUrl = AsyncExecServiceHandler.createRequestUrl( RWT.getRequest() );
    }

    @Override
    protected void asyncExec( Runnable runnable ) {
      super.asyncExec( runnable );
      try {
        URL url = new URL( requestUrl );
        HttpURLConnection connection = ( HttpURLConnection )url.openConnection();
        connection.setRequestMethod( "GET" );
        connection.connect();
        int responseCode = connection.getResponseCode();
        if( responseCode != HttpURLConnection.HTTP_OK ) {
          String msg = "AsyncExec service request returned response code " + responseCode;
          throw new RuntimeException( msg );
        }
      } catch( IOException ioe ) {
        throw new RuntimeException( ioe );
      }
    }
  }
  
  private static class AsyncExecServiceHandler implements IServiceHandler {
    static final String ID = "asyncExecServiceHandler";
    
    static String createRequestUrl( HttpServletRequest request ) {
      StringBuffer buffer = new StringBuffer();
      buffer.append( "http://127.0.0.1:" );
      buffer.append( request.getServerPort() );
      buffer.append( request.getRequestURI() );
      buffer.append( "?" );
      buffer.append( IServiceHandler.REQUEST_PARAM );
      buffer.append( "=asyncExecServiceHandler" );
      return buffer.toString();
    }
    
    public void service() {
      // do nothing
    }
  }
}
