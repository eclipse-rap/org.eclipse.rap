/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.RWTMessages;
import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.RequestId;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.theme.JsonValue;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.ISessionStore;


public class LifeCycleServiceHandler implements ServiceHandler {
  private static final String PROP_ERROR = "error";
  private static final String PROP_MESSAGE = "message";
  private static final String SESSION_STARTED
    = LifeCycleServiceHandler.class.getName() + "#isSessionStarted";

  private final LifeCycleFactory lifeCycleFactory;
  private final StartupPage startupPage;

  public LifeCycleServiceHandler( LifeCycleFactory lifeCycleFactory, StartupPage startupPage ) {
    this.lifeCycleFactory = lifeCycleFactory;
    this.startupPage = startupPage;
  }

  public void service() throws IOException {
    // Do not use session store itself as a lock
    // see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=372946
    SessionStoreImpl sessionStore = ( SessionStoreImpl )ContextProvider.getSessionStore();
    synchronized( sessionStore.getRequestLock() ) {
      synchronizedService();
    }
  }

  void synchronizedService() throws IOException {
    if( HTTP.METHOD_POST.equals( ContextProvider.getRequest().getMethod() ) ) {
      try {
        handlePostRequest();
      } finally {
        markSessionStarted();
      }
    } else {
      handleGetRequest();
    }
  }

  private void handleGetRequest() throws IOException {
    Map<String, String[]> parameters = ContextProvider.getRequest().getParameterMap();
    RequestParameterBuffer.store( parameters );
    HttpServletResponse response = ContextProvider.getResponse();
    if( RWT.getClient() instanceof WebClient ) {
      startupPage.send( response );
    } else {
      StartupJson.send( response );
    }
  }

  private void handlePostRequest() throws IOException {
    setJsonResponseHeaders();
    if( isSessionTimeout() ) {
      handleSessionTimeout();
    } else if( !isRequestCounterValid() ) {
      handleInvalidRequestCounter();
    } else {
      if( isSessionRestart() ) {
        reinitializeSessionStore();
        reinitializeServiceStore();
      }
      RequestParameterBuffer.merge();
      runLifeCycle();
    }
    writeProtocolMessage();
  }

  private void runLifeCycle() throws IOException {
    LifeCycle lifeCycle = ( LifeCycle )lifeCycleFactory.getLifeCycle();
    lifeCycle.execute();
  }

  //////////////////
  // helping methods

  private static boolean isRequestCounterValid() {
    return hasInitializeParameter() || RequestId.getInstance().isValid();
  }

  private static void handleInvalidRequestCounter() {
    int statusCode = HttpServletResponse.SC_PRECONDITION_FAILED;
    String errorType = "invalid request counter";
    String errorMessage = RWTMessages.getMessage( "RWT_MultipleInstancesErrorMessage" );
    renderError( statusCode, errorType, formatMessage( errorMessage ) );
  }

  private static void handleSessionTimeout() {
    int statusCode = HttpServletResponse.SC_FORBIDDEN;
    String errorType = "session timeout";
    String errorMessage = RWTMessages.getMessage( "RWT_SessionTimeoutErrorMessage" );
    renderError( statusCode, errorType, formatMessage( errorMessage ) );
  }

  private static String formatMessage( String message ) {
    Object[] arguments = new Object[]{ "<a {HREF_URL}>", "</a>" };
    return MessageFormat.format( message, arguments );
  }

  private static void renderError( int statusCode, String errorType, String errorMessage) {
    ContextProvider.getResponse().setStatus( statusCode );
    ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
    writer.appendHead( PROP_ERROR, JsonValue.valueOf( errorType ) );
    writer.appendHead( PROP_MESSAGE, JsonValue.valueOf( errorMessage ) );
  }

  private static void reinitializeSessionStore() {
    SessionStoreImpl sessionStore = ( SessionStoreImpl )ContextProvider.getSessionStore();
    Map<String, String[]> bufferedParameters = RequestParameterBuffer.getBufferedParameters();
    ApplicationContext applicationContext = ApplicationContextUtil.get( sessionStore );
    sessionStore.valueUnbound( null );
    HttpServletRequest request = ContextProvider.getRequest();
    SessionStoreBuilder builder = new SessionStoreBuilder( applicationContext, request );
    sessionStore = ( SessionStoreImpl )builder.buildSessionStore();
    ContextProvider.getContext().setSessionStore( sessionStore );
    if( bufferedParameters != null ) {
      RequestParameterBuffer.store( bufferedParameters );
    }
  }

  private static void reinitializeServiceStore() {
    ClientMessage clientMessage = ProtocolUtil.getClientMessage();
    ServiceStore serviceStore = ( ServiceStore )ContextProvider.getServiceStore();
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
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    sessionStore.setAttribute( SESSION_STARTED, Boolean.TRUE );
  }

  private static boolean isSessionStarted() {
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    return Boolean.TRUE.equals( sessionStore.getAttribute( SESSION_STARTED ) );
  }

  private static boolean hasInitializeParameter() {
    return "true".equals( ProtocolUtil.readHeadPropertyValue( RequestParams.RWT_INITIALIZE ) );
  }

  private static void setJsonResponseHeaders() {
    ServletResponse response = ContextProvider.getResponse();
    response.setContentType( HTTP.CONTENT_TYPE_JSON );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
  }

  private static void writeProtocolMessage() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
    String message = protocolWriter.createMessage();
    response.getWriter().write( message );
  }
}
