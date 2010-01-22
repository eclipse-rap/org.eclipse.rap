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
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.browser.Browser;
import org.eclipse.rwt.internal.browser.BrowserLoader;
import org.eclipse.rwt.internal.lifecycle.*;


public class LifeCycleServiceHandler extends AbstractServiceHandler {

  private static final LifeCycleServiceHandlerSync sync
    = new RWTLifeCycleServiceHandlerSync();

  /**
   * This class handles request synchronization of the 
   * <code>LifeCycleServiceHandler</code>. It was introduced to allow
   * different stratiegies for W4Toolkit and RWT.
   */
  public static abstract class LifeCycleServiceHandlerSync {

    /**
     * This method installs the fitting synchronization strategie for each 
     * Lifecycle type. It has to call the <code>doService</code> method
     * to get the lifecycle executed.
     */
    public abstract void service() throws ServletException, IOException;
    
    /**
     * This method does the actual lifecycle service handler call.
     */
    protected final void doService() throws ServletException, IOException {
      internalService();
    }
  }

  public void service() throws IOException, ServletException {
    sync.service();
  }

  public static boolean isSessionRestart() {
    HttpServletRequest request = getRequest();
    boolean startup = request.getParameter( RequestParams.STARTUP ) != null;
    String uiRoot = request.getParameter( RequestParams.UIROOT );
    HttpSession session = request.getSession();
    return    !session.isNew() && !startup && uiRoot == null 
           || startup && isBrowserDetected();
  }
  
  public static void initializeStateInfo() {
    if( ContextProvider.getStateInfo() == null ) {
      IServiceStateInfo stateInfo = new ServiceStateInfo();
      ContextProvider.getContext().setStateInfo( stateInfo );
    }
    if( ContextProvider.getStateInfo().getResponseWriter() == null ) {
      HtmlResponseWriter htmlResponseWriter = new HtmlResponseWriter();
      ContextProvider.getStateInfo().setResponseWriter( htmlResponseWriter );
    }
  }


  //////////////////
  // helping methods
  
  private static void internalService() throws ServletException, IOException {
    initializeStateInfo();
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