/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import java.io.*;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public class UICallBackServiceHandler implements IServiceHandler {
  
  private static final String ACTIVATION_IDS
    = UICallBackServiceHandler.class.getName() + "ActivationIds";

  public final static String HANDLER_ID
    = UICallBackServiceHandler.class.getName();
  
  private static String jsUICallBack;
  
  
  ////////////////
  // inner classes
  
  private static final class DummyResponse implements HttpServletResponse {

    public void addCookie( Cookie cookie ) {
    }

    public void addDateHeader( String name, long date ) {
    }

    public void addHeader( String name, String value ) {
    }

    public void addIntHeader( String name, int value ) {
    }

    public boolean containsHeader( String name ) {
      return false;
    }

    public String encodeRedirectURL( String url ) {
      return null;
    }

    public String encodeRedirectUrl( String url ) {
      return null;
    }

    public String encodeURL( String url ) {
      return null;
    }

    public String encodeUrl( String url ) {
      return null;
    }

    public void sendError( int sc ) throws IOException {
    }

    public void sendError( int sc, String msg ) throws IOException {
    }

    public void sendRedirect( String location ) throws IOException {
    }

    public void setDateHeader( String name, long date ) {
    }

    public void setHeader( String name, String value ) {
    }

    public void setIntHeader( String name, int value ) {
    }

    public void setStatus( int sc ) {
    }

    public void setStatus( int sc, String sm ) {
    }

    public void flushBuffer() throws IOException {
    }

    public int getBufferSize() {
      return 0;
    }

    public String getCharacterEncoding() {
      return null;
    }

    public String getContentType() {
      return null;
    }

    public Locale getLocale() {
      return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
      return null;
    }

    public PrintWriter getWriter() throws IOException {
      return null;
    }

    public boolean isCommitted() {
      return false;
    }

    public void reset() {
    }

    public void resetBuffer() {
    }

    public void setBufferSize( int size ) {
    }

    public void setCharacterEncoding( String charset ) {
    }

    public void setContentLength( int len ) {
    }

    public void setContentType( String type ) {
    }

    public void setLocale( Locale loc ) {
    }
  }

  private static final class DummyRequest implements HttpServletRequest {

    private final HttpSession session;

    private DummyRequest( HttpSession session ) {
      this.session = session;
    }

    public String getAuthType() {
      return null;
    }

    public String getContextPath() {
      return null;
    }

    public Cookie[] getCookies() {
      return null;
    }

    public long getDateHeader( String name ) {
      return 0;
    }

    public String getHeader( String name ) {
      return null;
    }

    public Enumeration getHeaderNames() {
      return null;
    }

    public Enumeration getHeaders( String name ) {
      return null;
    }

    public int getIntHeader( String name ) {
      return 0;
    }

    public String getMethod() {
      return null;
    }

    public String getPathInfo() {
      return null;
    }

    public String getPathTranslated() {
      return null;
    }

    public String getQueryString() {
      return null;
    }

    public String getRemoteUser() {
      return null;
    }

    public String getRequestURI() {
      return null;
    }

    public StringBuffer getRequestURL() {
      return null;
    }

    public String getRequestedSessionId() {
      return null;
    }

    public String getServletPath() {
      return null;
    }

    public HttpSession getSession() {
      return session;
    }

    public HttpSession getSession( boolean create ) {
      return session;
    }

    public Principal getUserPrincipal() {
      return null;
    }

    public boolean isRequestedSessionIdFromCookie() {
      return false;
    }

    public boolean isRequestedSessionIdFromURL() {
      return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
      return false;
    }

    public boolean isRequestedSessionIdValid() {
      return false;
    }

    public boolean isUserInRole( String role ) {
      return false;
    }

    public Object getAttribute( String name ) {
      return null;
    }

    public Enumeration getAttributeNames() {
      return null;
    }

    public String getCharacterEncoding() {
      return null;
    }

    public int getContentLength() {
      return 0;
    }

    public String getContentType() {
      return null;
    }

    public ServletInputStream getInputStream() throws IOException {
      return null;
    }

    public String getLocalAddr() {
      return null;
    }

    public String getLocalName() {
      return null;
    }

    public int getLocalPort() {
      return 0;
    }

    public Locale getLocale() {
      return null;
    }

    public Enumeration getLocales() {
      return null;
    }

    public String getParameter( String name ) {
      return null;
    }

    public Map getParameterMap() {
      return null;
    }

    public Enumeration getParameterNames() {
      return null;
    }

    public String[] getParameterValues( String name ) {
      return null;
    }

    public String getProtocol() {
      return null;
    }

    public BufferedReader getReader() throws IOException {
      return null;
    }

    public String getRealPath( String path ) {
      return null;
    }

    public String getRemoteAddr() {
      return null;
    }

    public String getRemoteHost() {
      return null;
    }

    public int getRemotePort() {
      return 0;
    }

    public RequestDispatcher getRequestDispatcher( String path ) {
      return null;
    }

    public String getScheme() {
      return null;
    }

    public String getServerName() {
      return null;
    }

    public int getServerPort() {
      return 0;
    }

    public boolean isSecure() {
      return false;
    }

    public void removeAttribute( String name ) {
    }

    public void setAttribute( String name, Object o ) {
    }

    public void setCharacterEncoding( String env ) 
      throws UnsupportedEncodingException
    {
    }
  }

  public void service() throws IOException, ServletException {
    UICallBackManager.getInstance().blockCallBackRequest();
    if( ContextProvider.hasContext() ) {
      HttpServletResponse response = ContextProvider.getResponse();
      PrintWriter writer = response.getWriter();
      writer.print( jsUICallBack() );
      writer.flush();
    }
  }

  public static void runNonUIThreadWithFakeContext( final Display display,
                                                    final Runnable runnable,
                                                    final boolean asUIThread )
  {
    boolean useFakeContext = !ContextProvider.hasContext();
    if( useFakeContext ) {
      IDisplayAdapter adapter = getDisplayAdapter( display );
      ISessionStore session = adapter.getSession();
      DummyRequest request = new DummyRequest( session.getHttpSession() );
      DummyResponse response = new DummyResponse();
      ServiceContext context = new ServiceContext( request, response, session );
      ContextProvider.setContext( context );
      if( asUIThread ) {
        ServiceStateInfo serviceStateInfo = new ServiceStateInfo();
        context.setStateInfo( serviceStateInfo );
        RWTLifeCycle.setThread( Thread.currentThread() );
      }
    }
    try {
      runnable.run();
    } finally {
      if( useFakeContext ) {
        ContextProvider.disposeContext();
      }
    }
  }

  private static String jsUICallBack() {
    String result = "";
    StringBuffer code = new StringBuffer();
    code.append( "org.eclipse.swt.Request.getInstance().send();" );
    if(    isUICallBackActive()
        && !UICallBackManager.getInstance().isCallBackRequestBlocked() )
    {
      if( jsUICallBack == null ) {
        code.append( jsEnableUICallBack() );
        jsUICallBack = code.toString();      
      }
      result = jsUICallBack;
    } else {
      result = code.toString();
    }
    return result;
  }

  public static String jsEnableUICallBack() {
    String result = "";
    if(    isUICallBackActive()
        && !UICallBackManager.getInstance().isCallBackRequestBlocked() )
    {
      Object[] param = new Object[] { 
        ContextProvider.getRequest().getServletPath().substring( 1 ),
        IServiceHandler.REQUEST_PARAM,
        HANDLER_ID
      };
      String callBackPattern =   "org.eclipse.swt.Request.getInstance()." 
                               + "enableUICallBack( \"{0}\",\"{1}\",\"{2}\" );";
      result = MessageFormat.format( callBackPattern, param );
    }
    return result;
  }

  private static boolean isUICallBackActive() {
    return !getActivationIds().isEmpty();
  }

  public static void activateUICallBacksFor( final String id ) {
    if( id == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    getActivationIds().add( id );
    if( getActivationIds().size() == 1 ) {
      registerUICallBackActivator();
    }
  }

  private static void registerUICallBackActivator() {
    final String id = ContextProvider.getSession().getId();
    LifeCycleFactory.getLifeCycle().addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;

      public void beforePhase( final PhaseEvent event ) {
      }

      public void afterPhase( final PhaseEvent event ) {
        if( id == ContextProvider.getSession().getId() ) {
          UICallBackManager.getInstance().setActive( true );
          IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
          HtmlResponseWriter writer = stateInfo.getResponseWriter();
          try {
            writer.write( jsEnableUICallBack() );
          } catch( IOException e ) {
            // TODO [rh] exception handling
            e.printStackTrace();
          } finally {
            LifeCycleFactory.getLifeCycle().removePhaseListener( this );
          }
        }
      }
      
      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
  }

  public static void deactivateUICallBacksFor( final String id ) {
    if( id == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    getActivationIds().remove( id );
    // release blocked callback handler request
    if( getActivationIds().isEmpty() ) {
      final UICallBackManager instance = UICallBackManager.getInstance();
      instance.setActive( false );
      instance.sendUICallBack();
    }
  }
  
  private static Set getActivationIds() {
    ISessionStore session = ContextProvider.getSession();
    Set result = ( Set )session.getAttribute( ACTIVATION_IDS );
    if( result == null ) {
      result = new HashSet();
      session.setAttribute( ACTIVATION_IDS, result );
    }
    return result;
  }

  private static IDisplayAdapter getDisplayAdapter( final Display display ) {
    return ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
  }
}
