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
package org.eclipse.rwt.internal.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.SingletonManager;
import org.eclipse.rwt.internal.application.ApplicationContext;
import org.eclipse.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rwt.internal.lifecycle.RWTRequestVersionControl;
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.internal.theme.JsonValue;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;


public class LifeCycleServiceHandler implements IServiceHandler {
  private static final String NEW_HTTP_SESSION
    = LifeCycleServiceHandler.class.getName() + "#isNewHttpSession";

  private final LifeCycleFactory lifeCycleFactory;
  private final StartupPage startupPage;

  public LifeCycleServiceHandler( LifeCycleFactory lifeCycleFactory, StartupPage startupPage ) {
    this.lifeCycleFactory = lifeCycleFactory;
    this.startupPage = startupPage;
  }

  public void service() throws IOException {
    synchronized( ContextProvider.getSessionStore() ) {
      synchronizedService();
    }
  }

  void synchronizedService() throws IOException {
    checkForNewSession();
    if( HTTP.METHOD_GET.equals( ContextProvider.getRequest().getMethod() ) ) {
      handleGetRequest();
    } else {
      handlePostRequest();
      clearNewSessionAttribute();
    }
  }

  private void handleGetRequest() throws IOException {
    Map<String, String[]> parameters = ContextProvider.getRequest().getParameterMap();
    RequestParameterBuffer.store( parameters );
    startupPage.send();
  }

  private void handlePostRequest() throws IOException {
    setJsonResponseHeaders();
    if( isSessionTimeout() ) {
      handleSessionTimeout();
    } else if( isRequestCounterValid() ) {
      initializeSessionStore();
      RequestParameterBuffer.merge();
      runLifeCycle();
    } else {
      handleInvalidRequestCounter();
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
    return RWTRequestVersionControl.getInstance().isValid()
           || isSessionRestart()
           || isNewHttpSession();
  }

  private static void handleInvalidRequestCounter() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "message", RWTMessages.getMessage( "RWT_MultipleInstancesError" ) );
    ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
    // TODO [tb] : do not assume "w1" as id for display
    writer.appendCall( "w1", "reload", properties );
  }

  private static void handleSessionTimeout() {
    ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
    writer.appendMeta( "timeout", JsonValue.TRUE );
  }

  private static void initializeSessionStore() {
    if( isSessionRestart() ) {
      ISessionStore sessionStore = ContextProvider.getSessionStore();
      Integer version = RWTRequestVersionControl.getInstance().getCurrentRequestId();
      Map<String, String[]> bufferedParameters = RequestParameterBuffer.getBufferedParameters();
      ApplicationContext applicationContext = ApplicationContextUtil.get( sessionStore );
      clearSessionStore();
      RWTRequestVersionControl.getInstance().setCurrentRequestId( version );
      if( bufferedParameters != null ) {
        RequestParameterBuffer.store( bufferedParameters );
      }
      ApplicationContextUtil.set( sessionStore, applicationContext );
      AbstractBranding branding = BrandingUtil.determineBranding();
      if( branding.getThemeId() != null ) {
        ThemeUtil.setCurrentThemeId( branding.getThemeId() );
      }
    }
  }

  private static void clearSessionStore() {
    SessionStoreImpl sessionStore = ( SessionStoreImpl )ContextProvider.getSessionStore();
    // clear attributes of session store to enable new startup
    sessionStore.valueUnbound( null );
    // reinitialize session store state
    sessionStore.valueBound( null );
    // TODO [rh] ContextProvider#getSessionStore() also initializes a session (slightly different)
    //      merge both code passages
    SingletonManager.install( sessionStore );
  }

  /*
   * Session restart: we're in the same HttpSession and start over (e.g. by pressing F5)
   */
  private static boolean isSessionRestart() {
    return !isNewHttpSession() && hasInitializeParameter();
  }

  private static boolean isSessionTimeout() {
    return isNewHttpSession() && !hasInitializeParameter();
  }

  private void checkForNewSession() {
    if( ContextProvider.getRequest().getSession().isNew() ) {
      ISessionStore sessionStore = ContextProvider.getSessionStore();
      sessionStore.setAttribute( NEW_HTTP_SESSION, Boolean.TRUE );
    }
  }

  private void clearNewSessionAttribute() {
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    sessionStore.removeAttribute( NEW_HTTP_SESSION );
  }

  private static boolean isNewHttpSession() {
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    return Boolean.TRUE.equals( sessionStore.getAttribute( NEW_HTTP_SESSION ) );
  }

  private static boolean hasInitializeParameter() {
    HttpServletRequest request = ContextProvider.getRequest();
    String initializeParameter = request.getParameter( RequestParams.RWT_INITIALIZE );
    return "true".equals( initializeParameter );
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
