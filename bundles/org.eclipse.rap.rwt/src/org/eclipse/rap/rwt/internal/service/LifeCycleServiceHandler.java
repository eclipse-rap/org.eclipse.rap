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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.RWTMessages;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.RequestCounter;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.theme.JsonValue;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.ServiceHandler;


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
        markSessionStarted();
      }
    } else {
      handleGetRequest( request, response );
    }
  }

  private void handleGetRequest( ServletRequest request, HttpServletResponse response )
    throws IOException
  {
    Map<String, String[]> parameters = request.getParameterMap();
    RequestParameterBuffer.store( parameters );
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
    if( isSessionTimeout() ) {
      handleSessionTimeout( response );
    } else if( !isRequestCounterValid() ) {
      handleInvalidRequestCounter( response );
    } else {
      if( isSessionRestart() ) {
        reinitializeUISession( request );
        reinitializeServiceStore();
      }
      RequestParameterBuffer.merge();
      runLifeCycle();
    }
    writeProtocolMessage( response );
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
    return hasInitializeParameter() || RequestCounter.getInstance().isValid();
  }

  private static void handleInvalidRequestCounter( HttpServletResponse response ) {
    int statusCode = HttpServletResponse.SC_PRECONDITION_FAILED;
    String errorType = "invalid request counter";
    String errorMessage = RWTMessages.getMessage( "RWT_MultipleInstancesErrorMessage" );
    renderError( response, statusCode, errorType, formatMessage( errorMessage ) );
  }

  private static void handleSessionTimeout( HttpServletResponse response ) {
    int statusCode = HttpServletResponse.SC_FORBIDDEN;
    String errorType = "session timeout";
    String errorMessage = RWTMessages.getMessage( "RWT_SessionTimeoutErrorMessage" );
    renderError( response, statusCode, errorType, formatMessage( errorMessage ) );
  }

  private static String formatMessage( String message ) {
    Object[] arguments = new Object[]{ "<a {HREF_URL}>", "</a>" };
    return MessageFormat.format( message, arguments );
  }

  private static void renderError( HttpServletResponse response,
                                   int statusCode,
                                   String errorType,
                                   String errorMessage )
  {
    response.setStatus( statusCode );
    ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
    writer.appendHead( PROP_ERROR, JsonValue.valueOf( errorType ) );
    writer.appendHead( PROP_MESSAGE, JsonValue.valueOf( errorMessage ) );
  }

  private static void reinitializeUISession( HttpServletRequest request ) {
    UISessionImpl uiSession = ( UISessionImpl )ContextProvider.getUISession();
    Map<String, String[]> bufferedParameters = RequestParameterBuffer.getBufferedParameters();
    ApplicationContextImpl applicationContext = ApplicationContextUtil.get( uiSession );
    uiSession.valueUnbound( null );
    UISessionBuilder builder = new UISessionBuilder( applicationContext, request );
    uiSession = ( UISessionImpl )builder.buildUISession();
    ContextProvider.getContext().setUISession( uiSession );
    if( bufferedParameters != null ) {
      RequestParameterBuffer.store( bufferedParameters );
    }
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
    uiSession.setAttribute( SESSION_STARTED, Boolean.TRUE );
  }

  private static boolean isSessionStarted() {
    UISession uiSession = ContextProvider.getUISession();
    return Boolean.TRUE.equals( uiSession.getAttribute( SESSION_STARTED ) );
  }

  private static boolean hasInitializeParameter() {
    return "true".equals( ProtocolUtil.readHeadPropertyValue( RequestParams.RWT_INITIALIZE ) );
  }

  private static void setJsonResponseHeaders( ServletResponse response ) {
    response.setContentType( HTTP.CONTENT_TYPE_JSON );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
  }

  private static void writeProtocolMessage( ServletResponse response ) throws IOException {
    ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
    String message = protocolWriter.createMessage();
    response.getWriter().write( message );
  }

}
