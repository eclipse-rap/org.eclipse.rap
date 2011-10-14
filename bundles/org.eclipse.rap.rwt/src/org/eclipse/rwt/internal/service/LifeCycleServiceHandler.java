/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.SingletonManager;
import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;
import org.eclipse.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rwt.internal.lifecycle.RWTRequestVersionControl;
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;


public class LifeCycleServiceHandler implements IServiceHandler {
  public static final String RWT_INITIALIZE = "rwt_initialize";
  static final String SESSION_INITIALIZED
    = LifeCycleServiceHandler.class.getName() + "#isSessionInitialized";

  private final LifeCycleFactory lifeCycleFactory;
  private final StartupPage startupPage;

  public LifeCycleServiceHandler( LifeCycleFactory lifeCycleFactory, StartupPage startupPage ) {
    this.lifeCycleFactory = lifeCycleFactory;
    this.startupPage = startupPage;
  }

  public void service() throws IOException {
    synchronized( ContextProvider.getSession() ) {
      synchronizedService();
    }
  }

  void synchronizedService() throws IOException {
    initializeJavaScriptResponseWriter();
    if(    RWTRequestVersionControl.getInstance().isValid()
        || isSessionRestart()
        || ContextProvider.getRequest().getSession().isNew() )
    {
      runLifeCycle();
    } else {
      handleInvalidRequestCounter();
    }
    finishJavaScriptResponseWriter();
  }

  public static void initializeSession() {
    if( !isSessionInitialized() ) {
      if( ContextProvider.getRequest().getParameter( RWT_INITIALIZE ) != null ) {
        ISessionStore session = ContextProvider.getSession();
        session.setAttribute( SESSION_INITIALIZED, Boolean.TRUE );
      }
    }
  }

  private void runLifeCycle() throws IOException {
    checkRequest();
    initializeSession();
    if( isSessionInitialized() ) {
      RequestParameterBuffer.merge();
      LifeCycle lifeCycle = ( LifeCycle )lifeCycleFactory.getLifeCycle();
      lifeCycle.execute();
    } else {
      Map<String, String[]> parameters = ContextProvider.getRequest().getParameterMap();
      RequestParameterBuffer.store( parameters );
      startupPage.send();
    }
  }

  //////////////////
  // helping methods

  private static boolean isSessionRestart() {
    HttpServletRequest request = ContextProvider.getRequest();
    boolean startup = request.getParameter( RequestParams.STARTUP ) != null;
    String uiRoot = request.getParameter( RequestParams.UIROOT );
    HttpSession session = request.getSession();
    return    !session.isNew() && !startup && uiRoot == null
           || startup && isSessionInitialized();
  }

  private static void initializeJavaScriptResponseWriter() throws IOException {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    if( stateInfo.getResponseWriter() == null ) {
      HttpServletResponse response = ContextProvider.getResponse();
      stateInfo.setResponseWriter( new JavaScriptResponseWriter( response ) );
    }
  }

  private static void finishJavaScriptResponseWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JavaScriptResponseWriter responseWriter = stateInfo.getResponseWriter();
    responseWriter.finish();
  }

  private static void handleInvalidRequestCounter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "message", RWTMessages.getMessage( "RWT_MultipleInstancesError" ) );
    ProtocolMessageWriter writer = stateInfo.getResponseWriter().getProtocolWriter();
    // TODO [tb] : do not assume "w1" as id for display
    writer.appendCall( "w1", "reload", properties );
  }

  private static boolean isSessionInitialized() {
    ISessionStore session = ContextProvider.getSession();
    return Boolean.TRUE.equals( session.getAttribute( SESSION_INITIALIZED ) );
  }

  private static void checkRequest() {
    if( isSessionRestart() ) {
      clearSessionStore();
    }
  }

  private static void clearSessionStore() {
    Integer version = RWTRequestVersionControl.getInstance().getCurrentRequestId();
    SessionStoreImpl sessionStore = ( SessionStoreImpl )ContextProvider.getSession();
    // clear attributes of session store to enable new startup
    sessionStore.valueUnbound( null );
    // reinitialize session store state
    sessionStore.valueBound( null );
    // TODO [rh] ContextProvider#getSession() also initializes a session (slightly different)
    //      merge both code passages
    SingletonManager.install( sessionStore );
    RWTRequestVersionControl.getInstance().setCurrentRequestId( version );
  }
}
