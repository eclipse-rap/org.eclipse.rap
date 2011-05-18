/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.http.*;

import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.SingletonManager;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;


public class LifeCycleServiceHandler implements IServiceHandler {

  public static final String RWT_INITIALIZE = "rwt_initialize";
  // TODO [if]: Move this code to a fragment
  private static final String PATTERN_RELOAD
    = "qx.core.Init.getInstance().getApplication().reload( \"{0}\" )";
  static final String SESSION_INITIALIZED
    = LifeCycleServiceHandler.class.getName() + "#isSessionInitialized";

  public void service() throws IOException {
    synchronized( ContextProvider.getSession() ) {
      synchronizedService();
    }
  }

  void synchronizedService() throws IOException {
    initializeStateInfo();
    initializeJavaScriptResponseWriter();
    if(    RWTRequestVersionControl.getInstance().isValid()
        || isSessionRestart()
        || ContextProvider.getRequest().getSession().isNew() )
    {
      runLifeCycle();
    } else {
      handleInvalidRequestCounter();
    }
  }

  public static void initializeSession() {
    if( !isSessionInitialized() ) {
      if( ContextProvider.getRequest().getParameter( RWT_INITIALIZE ) != null ) {
        ISessionStore session = ContextProvider.getSession();
        session.setAttribute( SESSION_INITIALIZED, Boolean.TRUE );
      }
    }
  }

  private static void runLifeCycle() throws IOException {
    checkRequest();
    initializeSession();
    if( isSessionInitialized() ) {
      RequestParameterBuffer.merge();
      LifeCycle lifeCycle = ( LifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
      lifeCycle.execute();
    } else {
      Map parameters = ContextProvider.getRequest().getParameterMap();
      RequestParameterBuffer.store( parameters );
      RWTFactory.getStartupPage().send();
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

  private static void initializeStateInfo() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    if( stateInfo == null ) {
      stateInfo = new ServiceStateInfo();
      ContextProvider.getContext().setStateInfo( stateInfo );
    }
  }

  private static void initializeJavaScriptResponseWriter() throws IOException {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    if( stateInfo.getResponseWriter() == null ) {
      HttpServletResponse response = ContextProvider.getResponse();
      response.setContentType( HTTP.CONTENT_TEXT_JAVASCRIPT );
      response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
      stateInfo.setResponseWriter( new JavaScriptResponseWriter( response.getWriter() ) );
    }
  }

  private static void handleInvalidRequestCounter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JavaScriptResponseWriter responseWriter = stateInfo.getResponseWriter();
    String message = RWTMessages.getMessage( "RWT_MultipleInstancesError" );
    Object[] args = new Object[] { message };
    responseWriter.write( MessageFormat.format( PATTERN_RELOAD, args ) );
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
