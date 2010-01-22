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
import org.eclipse.rwt.internal.browser.Browser;
import org.eclipse.rwt.internal.browser.BrowserLoader;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.util.HTML;


public class LifeCycleServiceHandler extends AbstractServiceHandler {

  // TODO [if]: Move this code to a fragment
  private static final String PATTERN_RELOAD
    = "qx.core.Init.getInstance().getApplication().reload( \"{0}\" )";

  public void service() throws IOException, ServletException {
    synchronized( ContextProvider.getSession() ) {
      synchronizedService();
    }
  }

  void synchronizedService() throws ServletException, IOException {
    LifeCycleServiceHandler.initializeStateInfo();
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
    detectBrowser();
    if( isBrowserDetected() ) {
      RequestParameterBuffer.merge();
      LifeCycle lifeCycle = ( LifeCycle )LifeCycleFactory.getLifeCycle();
      lifeCycle.execute();
    } else {
      Map parameters = ContextProvider.getRequest().getParameterMap();
      RequestParameterBuffer.store( parameters );
      BrowserSurvey.sendBrowserSurvey();
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
           || startup && isBrowserDetected();
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

  private static boolean isBrowserDetected() {
    return getBrowser() != null;
  }

  private static Browser getBrowser() {
    String id = ServiceContext.DETECTED_SESSION_BROWSER;
    return ( Browser )ContextProvider.getSession().getAttribute( id );
  }
  
  private static void detectBrowser() {
    if( !isBrowserDetected() ) {
      if(    getRequest().getParameter( RequestParams.SCRIPT ) != null 
          && getRequest().getParameter( RequestParams.AJAX_ENABLED ) != null )
      {
        Browser browser = BrowserLoader.load();
        String id = ServiceContext.DETECTED_SESSION_BROWSER;
        ContextProvider.getSession().setAttribute( id, browser );
      }
    }
    if ( isBrowserDetected() ) {
      ContextProvider.getStateInfo().setDetectedBrowser( getBrowser() );
    }
  }
  
  private static void checkRequest() {
    HttpSession session = getRequest().getSession();
    if( isSessionRestart() ) {
      clearSession( session );
    }
  }

  private static void clearSession( final HttpSession session ) {
    Enumeration keys = session.getAttributeNames();
    List keyBuffer = new ArrayList();
    while( keys.hasMoreElements() ) {
      keyBuffer.add( keys.nextElement() );
    }
    Object[] attributeNames = keyBuffer.toArray();
    for( int i = 0; i < attributeNames.length; i++ ) {
      // ensure that the session store instance lives as long as the
      // underlying session to avoid problems with request synchronization
      String idSessionStore = SessionStoreImpl.ID_SESSION_STORE;
      if( !idSessionStore.equals( attributeNames[ i ] ) ) {
        session.removeAttribute( ( String )attributeNames[ i ] );
      } else {
        // clear attributes of session store to enable new startup
        SessionStoreImpl sessionStore
          = ( SessionStoreImpl )session.getAttribute( idSessionStore );
        sessionStore.valueUnbound( null );
        // reinitialize session store state
        sessionStore.valueBound( null );
        sessionStore.setAttribute( SessionSingletonBase.LOCK, new Object() );
      }
    }
  }
  
  public static void writeOutput() throws IOException {
    if( !ContextProvider.getContext().isDisposed() ) {
      HtmlResponseWriter content
        = ContextProvider.getStateInfo().getResponseWriter();
      PrintWriter out = getOutputWriter();
      try {
        // send the head to the client
        for( int i = 0; i < content.getHeadSize(); i++ ) {
          out.print( content.getHeadToken( i ) );
        }
        // send the body to the client
        for( int i = 0; i < content.getBodySize(); i++ ) {
          out.print( content.getBodyToken( i ) );
        }
        // send the foot to the client
        for( int i = 0; i < content.getFootSize(); i++ ) {
          out.print( content.getFootToken( i ) );
        }
      } finally {
        out.close();
      }
    }
  }
}