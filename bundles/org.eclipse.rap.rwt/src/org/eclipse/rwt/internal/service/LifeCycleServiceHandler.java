/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.rwt.internal.browser.Browser;
import org.eclipse.rwt.internal.browser.BrowserLoader;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.resources.IResourceManager;


public class LifeCycleServiceHandler extends AbstractServiceHandler {

  // TODO [rh] Should we have a separate class that contains all logger names?
  //      e.g. com.w4t.util.LogerNames?
  public static final String LOG_REQUEST_PARAMS 
    = LifeCycleServiceHandler.class.getName() + ".requestParams"; 
  public static final String LOG_REQUEST_HEADER 
    = LifeCycleServiceHandler.class.getName() + ".requestHeaders"; 
  public static final String LOG_RESPONSE_CONTENT
    = LifeCycleServiceHandler.class.getName() + ".responseContent"; 
  
  // The log level used by all loggers thoughout this class
  private static final Level LOG_LEVEL = Level.FINE;
  private static final String PARAM_BUFFER
    = "org.eclipse.rap.w4t.startupRequestParameterBuffer:-)";
  
  private static Logger requestParamsLogger 
    = Logger.getLogger( LOG_REQUEST_PARAMS );
  private static Logger requestHeaderLogger 
    = Logger.getLogger( LOG_REQUEST_HEADER );
  private static Logger responseContentLogger
    = Logger.getLogger( LOG_RESPONSE_CONTENT );
  
  private final static DefaultLifeCycleServiceHandlerSync syncHandler
    = new DefaultLifeCycleServiceHandlerSync();
   
  public static ILifeCycleRunner lifeCycleRunner = new ILifeCycleRunner() {
    public void init() {
      // do nothing
    }
    public void run() throws ServletException, IOException {
      LifeCycle lifeCycle = ( LifeCycle )LifeCycleFactory.getLifeCycle();
      lifeCycle.execute();
    }
  };
  
  public static ILifeCycleServiceHandlerConfigurer configurer 
    = new ILifeCycleServiceHandlerConfigurer()
  {
    public InputStream getTemplateOfStartupPage() throws IOException {
      String resourceName = BrowserSurvey.getResourceName();
      IResourceManager manager = ResourceManagerImpl.getInstance();
      InputStream result = manager.getResourceAsStream( resourceName );
      if ( result == null ) {
        String text =   "Failed to load Browser Survey HTML Page. "
                      + "Resource {0} could not be found.";
        Object[] param = new Object[]{ resourceName };
        String msg = MessageFormat.format( text, param );
        throw new IOException( msg );
      }
      return result;
    }
    public void registerResources() throws IOException {
    }
    public boolean isStartupPageModifiedSince() {
      return true;
    }
    public LifeCycleServiceHandlerSync getSynchronizationHandler() {
      return syncHandler;
    }
  };

  public interface ILifeCycleServiceHandlerConfigurer {
    InputStream getTemplateOfStartupPage() throws IOException;
    boolean isStartupPageModifiedSince();
    void registerResources() throws IOException;
    LifeCycleServiceHandlerSync getSynchronizationHandler();
  }
    
  public interface ILifeCycleRunner {
    void init();
    void run() throws ServletException, IOException;
  }
  
  /**
   * The default implementation of <code>LifeCycleServiceHandlerSync</code>
   * aquires the session store as synchronization lock, so that
   * only one request at a time can be executed.   
   */
  private final static class DefaultLifeCycleServiceHandlerSync
    extends LifeCycleServiceHandlerSync
  {
    public void service() throws ServletException, IOException {
      synchronized( ContextProvider.getSession() ) {
        doService();
      }
    }
  }
  
  /**
   * This class handles request synchronization of the 
   * <code>LifeCycleServiceHandler</code>. It was introduced to allow
   * different stratiegies for W4Toolkit and RWT.
   *  
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
    logRequestHeader();
    logRequestParams();
    configurer.getSynchronizationHandler().service();
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
    if( getStateInfo() == null ) {
      IServiceStateInfo stateInfo;
      stateInfo = new ServiceStateInfo();
      ContextProvider.getContext().setStateInfo( stateInfo );
    }
    if( getStateInfo().getResponseWriter() == null ) {
      HtmlResponseWriter htmlResponseWriter = new HtmlResponseWriter();
      getStateInfo().setResponseWriter( htmlResponseWriter );
    }
  }


  //////////////////
  // helping methods
  
  private static void internalService() throws ServletException, IOException {
    long startTime = System.currentTimeMillis();
    initializeStateInfo();
    checkRequest();
    detectBrowser();
    if( isBrowserDetected() ) {
      lifeCycleRunner.init();
      wrapStartupRequest();
      lifeCycleRunner.run();
    } else {
      bufferStartupRequestParams();
      BrowserSurvey.sendBrowserSurvey();
    }
    appendProcessTime( startTime );
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
  
  private static void bufferStartupRequestParams() {
    Map parameters = getRequest().getParameterMap();
    HashMap paramBuffer = new HashMap( parameters );
    getRequest().getSession().setAttribute( PARAM_BUFFER, paramBuffer );
  }

  private static void wrapStartupRequest() {
    HttpSession session = getRequest().getSession();
    Map params = ( Map )session.getAttribute( PARAM_BUFFER );
    if( params != null ) {
      ServiceContext context = ContextProvider.getContext();
      context.setRequest( new StartupRequest( getRequest(), params ) );
    }
    session.removeAttribute( PARAM_BUFFER );
  }

  private static void checkRequest( ) {
    HttpSession session = getRequest().getSession();
    if( isSessionRestart() ) {
      clearSession( session );
    }
  }

  private static void clearSession( HttpSession session ) {
    Enumeration keys = session.getAttributeNames();
    List keyBuffer = new ArrayList();
    while( keys.hasMoreElements() ) {
      keyBuffer.add( keys.nextElement() );
    }
    Object[] attributeNames = keyBuffer.toArray();
    for( int i = 0; i < attributeNames.length; i++ ) {
      session.removeAttribute( ( String )attributeNames[ i ] );
    }
  }
  
  private static void appendProcessTime( final long startTime ) {
    if( getInitProps().isProcessTime() ) {
      // end point of process time
      long finish = System.currentTimeMillis();
      HtmlResponseWriter content = getStateInfo().getResponseWriter();
      content.appendFoot( getProcessTime( startTime, finish ) );
    }
  }
  
  private static StringBuffer getProcessTime( final long start, 
                                              final long finish )
  {
    long processTime = finish - start;
    StringBuffer result = new StringBuffer();
    result.append( "\nTime to process: " );
    result.append( processTime );
    result.append( " ms" );
    return result;
  }

  public static void writeOutput() throws IOException {
    HtmlResponseWriter content = getStateInfo().getResponseWriter();
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
    logResponseContent();
  }

  private static IServiceStateInfo getStateInfo() {
    return ContextProvider.getStateInfo();
  }
  

  //////////////////
  // Logging methods
  
  private static void logRequestHeader() {
    if( requestHeaderLogger.isLoggable( LOG_LEVEL ) ) {
      HttpServletRequest request = ContextProvider.getRequest();
      Enumeration headerNames = request.getHeaderNames();
      StringBuffer msg = new StringBuffer();
      msg.append( "Request header:\n" );
      msg.append( "(method):" );
      msg.append( request.getMethod() );
      while( headerNames.hasMoreElements() ) {
        String headerName = ( String )headerNames.nextElement();
        msg.append( headerName );
        msg.append( ": " );
        msg.append( request.getHeader( headerName ) );
      }
      requestHeaderLogger.log( LOG_LEVEL, msg.toString() );
    }    
  }
  
  private static void logRequestParams() {
    if( requestParamsLogger.isLoggable( LOG_LEVEL ) ) {
      StringBuffer msg = new StringBuffer();
      msg.append( "Request parameters:\n" );
      HttpServletRequest request = ContextProvider.getRequest();
      Enumeration parameterNames = request.getParameterNames();
      while( parameterNames.hasMoreElements() ) {
        String parameterName = ( String )parameterNames.nextElement();
        String parameterValue = request.getParameter( parameterName );
        msg.append( parameterName );
        msg.append( "=" );
        msg.append( parameterValue );      
        msg.append( "\n" );      
      }
      requestParamsLogger.log( LOG_LEVEL, msg.toString() );    
    }
  }
  
  private static void logResponseContent() {
    if( responseContentLogger.isLoggable( LOG_LEVEL ) ) {
      HtmlResponseWriter content = getStateInfo().getResponseWriter();
      StringBuffer msg = new StringBuffer();
      for( int i = 0; i < content.getHeadSize(); i ++ ) {
        msg.append( content.getHeadToken( i ) );
      }
      for( int i = 0; i < content.getBodySize(); i ++ ) {
        msg.append( content.getBodyToken( i ) );
      }
      for( int i = 0; i < content.getFootSize(); i ++ ) {
        msg.append( content.getFootToken( i ) );
      }
      responseContentLogger.log( LOG_LEVEL, msg.toString() );
    }
  }
}