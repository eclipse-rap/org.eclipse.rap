/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getClientMessage;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getProtocolWriter;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getUISession;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.RequestCounter;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.UISession;


public class LifeCycleServiceHandler implements ServiceHandler {

  private static final String PROP_ERROR = "error";
  private static final String PROP_REQUEST_COUNTER = "requestCounter";
  private static final String ATTR_LAST_PROTOCOL_MESSAGE
    = LifeCycleServiceHandler.class.getName() + "#lastProtocolMessage";
  private static final String ATTR_SESSION_STARTED
    = LifeCycleServiceHandler.class.getName() + "#isSessionStarted";

  private final LifeCycleFactory lifeCycleFactory;
  private final StartupPage startupPage;

  public LifeCycleServiceHandler( LifeCycleFactory lifeCycleFactory, StartupPage startupPage ) {
    this.lifeCycleFactory = lifeCycleFactory;
    this.startupPage = startupPage;
  }

  public void service( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    // Do not use session store itself as a lock
    // see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=372946
    UISessionImpl uiSession = ( UISessionImpl )ContextProvider.getUISession();
    synchronized( uiSession.getRequestLock() ) {
      synchronizedService( request, response );
    }
  }

  void synchronizedService( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    if( HTTP.METHOD_POST.equals( request.getMethod() ) ) {
      try {
        handlePostRequest( request, response );
      } finally {
        if( !isSessionShutdown() ) {
          markSessionStarted();
        }
      }
    } else {
      try {
        handleGetRequest( request, response );
      } finally {
        // The GET request currently creates a dummy UI session needed for accessing the client
        // information. It is not meant to be reused by other requests.
        shutdownUISession();
      }
    }
  }

  private void handleGetRequest( ServletRequest request, HttpServletResponse response )
    throws IOException
  {
    if( RWT.getClient() instanceof WebClient ) {
      startupPage.send( response );
    } else {
      StartupJson.send( response );
    }
  }

  private void handlePostRequest( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    setJsonResponseHeaders( response );
    if( isSessionShutdown() ) {
      shutdownUISession();
    } else if( isSessionTimeout() ) {
      writeSessionTimeoutError( response );
    } else if( !isRequestCounterValid() ) {
      if( isDuplicateRequest() ) {
        writeBufferedResponse( response );
      } else {
        writeInvalidRequestCounterError( response );
      }
    } else {
      if( isSessionRestart() ) {
        reinitializeUISession( request );
        reinitializeServiceStore();
      }
      UrlParameters.merge();
      runLifeCycle();
      writeProtocolMessage( response );
    }
  }

  private void runLifeCycle() throws IOException {
    if( hasInitializeParameter() ) {
      // TODO [tb] : This is usually done in DisplayLCA#readData, but the ReadData
      // phase is omitted in the first POST request. Since RemoteObjects may already be registered
      // at this point, this workaround is currently required. We should find a solution that
      // does not require RemoteObjectLifeCycleAdapter.readData to be called in different places.
      RemoteObjectLifeCycleAdapter.readData();
    }
    LifeCycle lifeCycle = lifeCycleFactory.getLifeCycle();
    lifeCycle.execute();
  }

  //////////////////
  // helping methods

  private static boolean isRequestCounterValid() {
    return hasInitializeParameter() || hasValidRequestCounter();
  }

  static boolean hasValidRequestCounter() {
    int currentRequestId = RequestCounter.getInstance().currentRequestId();
    JsonValue sentRequestId = getClientMessage().getHeader( PROP_REQUEST_COUNTER );
    if( sentRequestId == null ) {
      return currentRequestId == 0;
    }
    return currentRequestId == sentRequestId.asInt();
  }

  private static boolean isDuplicateRequest() {
    int currentRequestId = RequestCounter.getInstance().currentRequestId();
    JsonValue sentRequestId = getClientMessage().getHeader( PROP_REQUEST_COUNTER );
    return sentRequestId != null && sentRequestId.asInt() == currentRequestId - 1;
  }

  private static void shutdownUISession() {
    UISessionImpl uiSession = ( UISessionImpl )ContextProvider.getUISession();
    uiSession.shutdown();
  }

  private static void writeInvalidRequestCounterError( HttpServletResponse response )
    throws IOException
  {
    String errorType = "invalid request counter";
    writeError( response, HttpServletResponse.SC_PRECONDITION_FAILED, errorType );
  }

  private static void writeSessionTimeoutError( HttpServletResponse response ) throws IOException {
    String errorType = "session timeout";
    writeError( response, HttpServletResponse.SC_FORBIDDEN, errorType );
  }

  private static void writeError( HttpServletResponse response,
                                  int statusCode,
                                  String errorType ) throws IOException
  {
    response.setStatus( statusCode );
    ProtocolMessageWriter writer = new ProtocolMessageWriter();
    writer.appendHead( PROP_ERROR, JsonValue.valueOf( errorType ) );
    writer.createMessage().writeTo( response.getWriter() );
  }

  private static void reinitializeUISession( HttpServletRequest request ) {
    ServiceContext serviceContext = ContextProvider.getContext();
    UISessionImpl uiSession = ( UISessionImpl )ContextProvider.getUISession();
    uiSession.shutdown();
    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    uiSession = builder.buildUISession();
    serviceContext.setUISession( uiSession );
  }

  private static void reinitializeServiceStore() {
    ClientMessage clientMessage = ProtocolUtil.getClientMessage();
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.clear();
    ProtocolUtil.setClientMessage( clientMessage );
  }

  /*
   * Session restart: we're in the same HttpSession and start over (e.g. by pressing F5)
   */
  private static boolean isSessionRestart() {
    return isSessionStarted() && hasInitializeParameter();
  }

  private static boolean isSessionTimeout() {
    // Session is not initialized because we got a new HTTPSession
    return !isSessionStarted() && !hasInitializeParameter();
  }

  static void markSessionStarted() {
    UISession uiSession = ContextProvider.getUISession();
    uiSession.setAttribute( ATTR_SESSION_STARTED, Boolean.TRUE );
  }

  private static boolean isSessionStarted() {
    UISession uiSession = ContextProvider.getUISession();
    return Boolean.TRUE.equals( uiSession.getAttribute( ATTR_SESSION_STARTED ) );
  }

  private static boolean isSessionShutdown() {
    JsonValue shutdownHeader = getClientMessage().getHeader( ClientMessageConst.RWT_SHUTDOWN );
    return JsonValue.TRUE.equals( shutdownHeader );
  }

  private static boolean hasInitializeParameter() {
    JsonValue initializeHeader = getClientMessage().getHeader( ClientMessageConst.RWT_INITIALIZE );
    return JsonValue.TRUE.equals( initializeHeader );
  }

  private static void setJsonResponseHeaders( ServletResponse response ) {
    response.setContentType( HTTP.CONTENT_TYPE_JSON );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
  }

  private static void writeProtocolMessage( ServletResponse response ) throws IOException {
    JsonObject message = getProtocolWriter().createMessage();
    bufferProtocolMessage( message );
    message.writeTo( response.getWriter() );
  }

  private static void writeBufferedResponse( HttpServletResponse response ) throws IOException {
    getBufferedMessage().writeTo( response.getWriter() );
  }

  private static void bufferProtocolMessage( JsonObject message ) {
    UISession uiSession = getUISession();
    if( uiSession != null ) {
      uiSession.setAttribute( ATTR_LAST_PROTOCOL_MESSAGE, message );
    }
  }

  private static JsonObject getBufferedMessage() {
    return ( JsonObject )getUISession().getAttribute( ATTR_LAST_PROTOCOL_MESSAGE );
  }

}
