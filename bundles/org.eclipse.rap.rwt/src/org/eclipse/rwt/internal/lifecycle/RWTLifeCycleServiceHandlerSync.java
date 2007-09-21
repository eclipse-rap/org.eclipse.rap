/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.LifeCycleServiceHandlerSync;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.internal.util.URLHelper;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;

/**
 * TODO [fappel]: documentation
 */
public final class RWTLifeCycleServiceHandlerSync
  extends LifeCycleServiceHandlerSync
{ 
  private static final String RESTART = "restart";
  private static final String RESTART_TICKET
    = RWTLifeCycleServiceHandlerSync.class.getName() + ".RESTART_VALUE";
  private static final String HTML_P = "p";
  private static final String HTML_H3 = "h3";
  

  public void service() throws ServletException, IOException {
    RWTLifeCycleBlockControl.service( new IServiceHandler() {
      public void service() throws IOException, ServletException {
        serviceInternal();
      }
    } );
  }
  
  private void serviceInternal() throws ServletException, IOException {
    LifeCycleServiceHandler.initializeStateInfo();
    RWTRequestVersionControl.determine();
    if( isCloseRequested() ) {
      // notification that the user has unloaded the client document
      // nothing to do here
    } else if( LifeCycleServiceHandler.isSessionRestart() ) {
      // user requests reload of startup page
      if( isRestartAllowed() ) {
        // reload is requested after the unload notification and/or the
        // user explicitly requests a restart
        RWTRequestVersionControl.increase();
        markAsExpired();
        doService();
      } else {
        String restartTicket = allowRestart();
        if( isDeepLink() ) {
          bufferRequestParams();
          // user tries to load the startup document with additional 
          // request parameters within the existing session. This is 
          // considered a deep link request into a running session and
          // a warning page is sent.
          sendDeepLinkInExistingSessionPage( restartTicket );
        } else {
          // user tries to load another instance of the startup document in
          // the current session, which can't be handled by RAP. Send
          // multiple clients warning page.
          sendMultipleClientsPerSessionPage( restartTicket );
        }
      }
      resetCloseRequestedState();
    } else {
      // standard request service handling
      if( RWTRequestVersionControl.check() ) {
        doService();
      } else {
        int sc = HttpServletResponse.SC_PRECONDITION_FAILED;
        ContextProvider.getResponse().setStatus( sc );
        String restartTicket = allowRestart();
        sendMultipleClientsPerSessionPage( restartTicket );            
      }
    }
    RWTRequestVersionControl.store();
  }
  
  private boolean isCloseRequested() {
    boolean result = false;
    HttpServletRequest request = ContextProvider.getRequest();
    String value = request.getParameter( RequestParams.CLOSE_REQUESTED );
    if( value != null ) {
      HttpSession session = ContextProvider.getSession().getHttpSession();
      session.setAttribute( RequestParams.CLOSE_REQUESTED, value );
      result = true;
    }
    return result;
  }
  
  private void resetCloseRequestedState() {
    ISessionStore session = ContextProvider.getSession();
    session.setAttribute( RequestParams.CLOSE_REQUESTED, null );
  }
  
  private boolean isRestartAllowed() {
    ISessionStore session = ContextProvider.getSession();
    HttpSession httpSession = session.getHttpSession();
    HttpServletRequest request = ContextProvider.getRequest();
    String closeRequested = RequestParams.CLOSE_REQUESTED;
    String restart = request.getParameter( RESTART );
    String expected = ( String )session.getAttribute( RESTART_TICKET );
    session.removeAttribute( RESTART_TICKET );
    return    httpSession.getAttribute( closeRequested ) != null
           || restart != null && restart.equals( expected );
  }
  
  private String allowRestart() {
    String result = String.valueOf( new Object().hashCode() );
    ContextProvider.getSession().setAttribute( RESTART_TICKET, result );
    return result;
  }
  
  /* (intentionally non-JavaDoc'ed)
   * A request is considered to be a 'deep link' when it carries more
   * parameters than just ones necessary to (re-)start the session.
   * Namely: 'startup' and 'restart' 
   */
  private boolean isDeepLink() {
    HttpServletRequest request = ContextProvider.getRequest();
    HashMap parameters = new HashMap( request.getParameterMap() );
    parameters.remove( RequestParams.STARTUP );
    parameters.remove( RESTART );
    return parameters.size() > 0;
  }
  
  private void bufferRequestParams() {
    HttpServletRequest request = ContextProvider.getRequest();
    HashMap parameters = new HashMap( request.getParameterMap() );
    // Don't store parameters that would lead to a session restart
    parameters.remove( RequestParams.STARTUP );
    parameters.remove( RESTART );
    RequestParameterBuffer.store( parameters );
  }
  
  private void sendMultipleClientsPerSessionPage( final String restartTicket ) 
    throws IOException 
  {
    // TODO [fappel]: I18n, branding of this page...
    markAsExpired();
    
    LifeCycleServiceHandler.initializeStateInfo();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter out = stateInfo.getResponseWriter();
    out.startDocument();
    out.startElement( HTML.HEAD, null );
    out.startElement( HTML.TITLE, null );
    String title = "RAP Multiple Clients Warning";
    out.writeText( title, null );
    out.endElement( HTML.TITLE );
    out.endElement( HTML.HEAD );
    out.startElement( HTML.BODY, null );
    out.startElement( HTML_H3, null );
    out.writeText( title, null );
    out.endElement( HTML_H3 );
    out.startElement( HTML_P, null );
    StringBuffer msg = new StringBuffer();
    msg.append( "RAP does not support multiple browser-instances or " );
    msg.append( "browser-tabs per session. If you have already closed " );
    msg.append( "the browser/tab with which you have started the session, " );
    msg.append( "you may click the link below to restart the session from " );
    msg.append( "scratch." );
    out.writeText( msg, null );
    out.endElement( HTML_P );
    out.startElement( HTML.A, null );
    StringBuffer url = new StringBuffer();
    url.append( URLHelper.getURLString( false ) );
    String entryPoint = EntryPointManager.getCurrentEntryPoint();
    URLHelper.appendFirstParam( url, RequestParams.STARTUP, entryPoint );
    // TODO [fappel]: think about a better solution. The restart value
    //                avoids that FireFox loads the page from cache...
    URLHelper.appendParam( url, RESTART, restartTicket );
    out.writeAttribute( HTML.HREF, url, null );
    out.writeText( "Click here to restart session.", null );
    out.endElement( HTML.A );
    out.endElement( HTML.BODY );
    out.endDocument();
    LifeCycleServiceHandler.writeOutput();
  }
  
  private void sendDeepLinkInExistingSessionPage( final String restartTicket ) 
    throws IOException 
  {
    // TODO [rh]: I18n, branding of this page...
    markAsExpired();
    
    LifeCycleServiceHandler.initializeStateInfo();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter out = stateInfo.getResponseWriter();
    out.startDocument();
    out.startElement( HTML.HEAD, null );
    out.startElement( HTML.TITLE, null );
    String title = "RAP Warning: Deep Link into Existing Session";
    out.writeText( title, null );
    out.endElement( HTML.TITLE );
    out.endElement( HTML.HEAD );
    out.startElement( HTML.BODY, null );
    out.startElement( HTML_H3, null );
    out.writeText( title, null );
    out.endElement( HTML_H3 );
    out.startElement( HTML_P, null );
    String msg 
      = "RAP does not support multiple browser-instances or "
        + "browser-tabs per session.";
    out.writeText( msg, null );
    out.startElement( HTML.BR, null );
    msg 
      = "A request with deep link parameters was sent to an existing " 
        + "session and these parameters were passed to this session.";
    out.writeText( msg, null );
    out.startElement( HTML.BR, null );
    msg 
      = "You may click the link below to restart the session with the " 
        + "deep link parameters from scratch."; 
    out.writeText( msg, null );
    out.endElement( HTML_P );
    out.startElement( HTML.A, null );
    String url = getDeepLinkRestartURL( restartTicket );
    out.writeAttribute( HTML.HREF, url, null );
    out.writeText( "Click here to restart the session with the deep link.", 
                   null );
    out.endElement( HTML.A );
    out.endElement( HTML.BODY );
    out.endDocument();
    LifeCycleServiceHandler.writeOutput();
  }
  
  private String getDeepLinkRestartURL( final String restartTicket ) {
    StringBuffer url = new StringBuffer();
    url.append( URLHelper.getURLString( false ) );
    String entryPoint = EntryPointManager.getCurrentEntryPoint();
    URLHelper.appendFirstParam( url, RequestParams.STARTUP, entryPoint );
    // TODO [fappel]: think about a better solution. The restart ticket
    //                avoids that FireFox loads the page from cache...
    URLHelper.appendParam( url, RESTART, restartTicket );
    HttpServletRequest request = ContextProvider.getRequest();
    Map parameters = request.getParameterMap();
    Iterator iter = parameters.keySet().iterator();
    while( iter.hasNext() ) {
      String key = ( String )iter.next();
      if( !RequestParams.STARTUP.equals( key ) && !RESTART.equals( key ) ) {
        String[] values = ( String[] )parameters.get( key );
        for( int i = 0; i < values.length; i++ ) {
          URLHelper.appendParam( url, key, values[ i ] );
        }
      }
    }
    return url.toString();
  }
  
  private void markAsExpired() {
    HttpServletResponse response = ContextProvider.getResponse();
    response.setHeader( "Pragma", "no-cache" );
    response.setHeader( "Cache-Control", "no-cache" );
    response.setDateHeader( "Expires", 0 );
  }
}