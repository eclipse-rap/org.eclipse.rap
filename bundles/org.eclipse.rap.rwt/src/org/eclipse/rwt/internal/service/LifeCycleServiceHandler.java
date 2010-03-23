/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.service.ISessionStore;


public class LifeCycleServiceHandler extends AbstractServiceHandler {

  public static final String RWT_INITIALIZE = "rwt_initialize";

  // TODO [if]: Move this code to a fragment
  private static final String PATTERN_RELOAD
    = "qx.core.Init.getInstance().getApplication().reload( \"{0}\" )";

  final static String SESSION_INITIALIZED
    = LifeCycleServiceHandler.class.getName() + "#isSessionInitialized";

  public void service() throws IOException, ServletException {
    synchronized( ContextProvider.getSession() ) {
      synchronizedService();
    }
  }

  void synchronizedService() throws ServletException, IOException {
    initializeStateInfo();
    RWTRequestVersionControl.beforeService();
    try {
      if(    RWTRequestVersionControl.isValid()
          || LifeCycleServiceHandler.isSessionRestart()
          || ContextProvider.getRequest().getSession().isNew() )
      {
        runLifeCycle();
      } else {
        handleInvalidRequestCounter();
      }
    } finally {
      RWTRequestVersionControl.afterService();
    }
  }

  private static void runLifeCycle() throws ServletException, IOException {
    checkRequest();
    initializeSession();
    if( isSessionInitialized() ) {
      RequestParameterBuffer.merge();
      LifeCycle lifeCycle = ( LifeCycle )LifeCycleFactory.getLifeCycle();
      lifeCycle.execute();
    } else {
      Map parameters = ContextProvider.getRequest().getParameterMap();
      RequestParameterBuffer.store( parameters );
      StartupPage.send();
    }
    writeOutput();
  }


  //////////////////
  // helping methods

  private static boolean isSessionRestart() {
    HttpServletRequest request = getRequest();
    boolean startup = request.getParameter( RequestParams.STARTUP ) != null;
    String uiRoot = request.getParameter( RequestParams.UIROOT );
    HttpSession session = request.getSession();
    return    !session.isNew() && !startup && uiRoot == null
           || startup && isSessionInitialized();
  }

  private static void initializeStateInfo() {
    if( ContextProvider.getStateInfo() == null ) {
      IServiceStateInfo stateInfo = new ServiceStateInfo();
      ContextProvider.getContext().setStateInfo( stateInfo );
    }
    if( ContextProvider.getStateInfo().getResponseWriter() == null ) {
      HtmlResponseWriter htmlResponseWriter = new HtmlResponseWriter();
      ContextProvider.getStateInfo().setResponseWriter( htmlResponseWriter );
    }
  }

  private static void handleInvalidRequestCounter()
    throws IOException
  {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter out = stateInfo.getResponseWriter();
    String message = RWTMessages.getMessage( "RWT_MultipleInstancesError" );
    Object[] args = new Object[] { message };
    // Note: [rst] Do not use writeText as umlauts must not be encoded here
    out.write( MessageFormat.format( PATTERN_RELOAD, args ) );
    HttpServletResponse response = ContextProvider.getResponse();
    response.setContentType( HTML.CONTENT_TEXT_JAVASCRIPT_UTF_8 );
    LifeCycleServiceHandler.writeOutput();
  }

  private static boolean isSessionInitialized() {
    ISessionStore session = ContextProvider.getSession();
    return Boolean.TRUE.equals( session.getAttribute( SESSION_INITIALIZED ) );
  }

  public static void initializeSession() {
    if( !isSessionInitialized() ) {
      if(  getRequest().getParameter( RWT_INITIALIZE ) != null ) {
        ISessionStore session = ContextProvider.getSession();
        session.setAttribute( SESSION_INITIALIZED, Boolean.TRUE );
      }
    }
  }

  private static void checkRequest() {
    if( isSessionRestart() ) {
      clearSessionStore();
    }
  }

  private static void clearSessionStore() {
    SessionStoreImpl sessionStore
      = ( SessionStoreImpl )ContextProvider.getSession();
    // clear attributes of session store to enable new startup
    sessionStore.valueUnbound( null );
    // reinitialize session store state
    sessionStore.valueBound( null );
    sessionStore.setAttribute( SessionSingletonBase.LOCK, new Object() );
  }

  public static void writeOutput() throws IOException {
    if( !ContextProvider.getContext().isDisposed() ) {
      HtmlResponseWriter content
        = ContextProvider.getStateInfo().getResponseWriter();
      PrintWriter out = getOutputWriter();
      try {
        // send the body to the client
        for( int i = 0; i < content.getBodySize(); i++ ) {
          out.print( content.getBodyToken( i ) );
        }
      } finally {
        out.close();
      }
    }
  }
}