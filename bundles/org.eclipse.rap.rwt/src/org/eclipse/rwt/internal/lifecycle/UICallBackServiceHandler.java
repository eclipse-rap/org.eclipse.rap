/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
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
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public class UICallBackServiceHandler implements IServiceHandler {

  // keep in sync with function enableUICallBack() in Request.js
  public final static String HANDLER_ID
    = UICallBackServiceHandler.class.getName();

  private static final String JS_SEND_CALLBACK_REQUEST
    = "org.eclipse.swt.Request.getInstance().enableUICallBack();";
  private static final String JS_SEND_UI_REQUEST
    = "org.eclipse.swt.Request.getInstance().send();";

  private static final String BUFFERED_SEND_CALLBACK_REQUEST
    = UICallBackServiceHandler.class.getName() + "#jsUICallback";


  ////////////////
  // inner classes

  private static final class IdManager {
    
    static IdManager getInstance() {
      return ( IdManager )SessionSingletonBase.getInstance( IdManager.class );
    }
    
    private final Set ids;
    
    IdManager() {
      ids = new HashSet();
    }
    
    Object getLock() {
      return ids;
    }

    void add( final String id ) {
      ids.add( id );
    }

    void remove( final String id ) {
      ids.remove( id );
    }
    
    boolean isEmpty() {
      return ids.isEmpty();
    }
    
    int size() {
      return ids.size();
    }
  }

  private static final class DummyResponse implements HttpServletResponse {

    public void addCookie( final Cookie cookie ) {
    }

    public void addDateHeader( final String name, final long date ) {
    }

    public void addHeader( final String name, final String value ) {
    }

    public void addIntHeader( final String name, final int value ) {
    }

    public boolean containsHeader( final String name ) {
      return false;
    }

    public String encodeRedirectURL( final String url ) {
      return null;
    }

    public String encodeRedirectUrl( final String url ) {
      return null;
    }

    public String encodeURL( final String url ) {
      return null;
    }

    public String encodeUrl( final String url ) {
      return null;
    }

    public void sendError( final int sc ) throws IOException {
    }

    public void sendError( final int sc, final String msg ) throws IOException {
    }

    public void sendRedirect( final String location ) throws IOException {
    }

    public void setDateHeader( final String name, final long date ) {
    }

    public void setHeader( final String name, final String value ) {
    }

    public void setIntHeader( final String name, final int value ) {
    }

    public void setStatus( final int sc ) {
    }

    public void setStatus( final int sc, final String sm ) {
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

    public void setBufferSize( final int size ) {
    }

    public void setCharacterEncoding( final String charset ) {
    }

    public void setContentLength( final int len ) {
    }

    public void setContentType( final String type ) {
    }

    public void setLocale( final Locale loc ) {
    }
  }

  private static final class DummyRequest implements HttpServletRequest {

    private final HttpSession session;

    private DummyRequest( final HttpSession session ) {
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

    public long getDateHeader( final String name ) {
      return 0;
    }

    public String getHeader( final String name ) {
      return null;
    }

    public Enumeration getHeaderNames() {
      return null;
    }

    public Enumeration getHeaders( final String name ) {
      return null;
    }

    public int getIntHeader( final String name ) {
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

    public HttpSession getSession( final boolean create ) {
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

    public boolean isUserInRole( final String role ) {
      return false;
    }

    public Object getAttribute( final String name ) {
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

    public String getParameter( final String name ) {
      return null;
    }

    public Map getParameterMap() {
      return null;
    }

    public Enumeration getParameterNames() {
      return null;
    }

    public String[] getParameterValues( final String name ) {
      return null;
    }

    public String getProtocol() {
      return null;
    }

    public BufferedReader getReader() throws IOException {
      return null;
    }

    public String getRealPath( final String path ) {
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

    public RequestDispatcher getRequestDispatcher( final String path ) {
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

    public void removeAttribute( final String name ) {
    }

    public void setAttribute( final String name, final Object o ) {
    }

    public void setCharacterEncoding( final String env )
      throws UnsupportedEncodingException
    {
    }
  }

  public void service() throws IOException, ServletException {
    ISessionStore sessionStore = RWT.getSessionStore();
    if(    !UICallBackManager.getInstance().blockCallBackRequest()
        && ContextProvider.hasContext() 
        && sessionStore.isBound() )
    {
      writeResponse();
    }
  }

  public static void runNonUIThreadWithFakeContext( final Display display,
                                                    final Runnable runnable )
  {
    // Don't replace local variables by method calls, since the context may
    // change during the methods execution.
    Display sessionDisplay = RWTLifeCycle.getSessionDisplay();
    boolean useDifferentContext
      =  ContextProvider.hasContext() && sessionDisplay != display;
    ServiceContext contextBuffer = null;
    // TODO [fappel]: The context handling's getting very awkward in case of
    //                having the context mapped instead of stored it in
    //                the ContextProvider's ThreadLocal (see ContextProvider).
    //                Because of this the wasMapped variable is used to
    //                use the correct way to restore the buffered context.
    //                See whether this can be done more elegantly and supplement
    //                the testcases...
    boolean wasMapped = false;
    if( useDifferentContext ) {
      contextBuffer = ContextProvider.getContext();
      wasMapped = ContextProvider.releaseContextHolder();
    }
    boolean useFakeContext = !ContextProvider.hasContext();
    if( useFakeContext ) {
      IDisplayAdapter adapter = getDisplayAdapter( display );
      ISessionStore session = adapter.getSession();
      DummyRequest request = new DummyRequest( session.getHttpSession() );
      DummyResponse response = new DummyResponse();
      ServiceContext context = new ServiceContext( request, response, session );
      ContextProvider.setContext( context );
    }
    try {
      runnable.run();
    } finally {
      if( useFakeContext ) {
        ContextProvider.disposeContext();
      }
      if( useDifferentContext ) {
        if( wasMapped ) {
          ContextProvider.setContext( contextBuffer, Thread.currentThread() );
        } else {
          ContextProvider.setContext( contextBuffer );
        }
      }
    }
  }

  public static ServiceContext getFakeContext( final HttpSession session ) {
    String id = SessionStoreImpl.ID_SESSION_STORE;
    ISessionStore sessionStore = ( ISessionStore )session.getAttribute( id );
    return getFakeContext( sessionStore );
  }

  public static ServiceContext getFakeContext( final ISessionStore store )
  {
    DummyRequest request = new DummyRequest( store.getHttpSession() );
    DummyResponse response = new DummyResponse();
    return new ServiceContext( request, response, store );
  }

  public static void activateUICallBacksFor( final String id ) {
    if( id == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int size;
    synchronized( IdManager.getInstance().getLock() ) {
      IdManager.getInstance().add( id );
      size = IdManager.getInstance().size();
    }
    if( size == 1 ) {
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
        if( id.equals( ContextProvider.getSession().getId() ) ) {
          LifeCycleFactory.getLifeCycle().removePhaseListener( this );
          UICallBackManager.getInstance().setActive( true );
          IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
          HtmlResponseWriter writer = stateInfo.getResponseWriter();
          try {
            writer.write( jsEnableUICallBack() );
          } catch( IOException e ) {
            // TODO [rh] exception handling
            e.printStackTrace();
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
    // release blocked callback handler request
    boolean empty;
    synchronized( IdManager.getInstance().getLock() ) {
      IdManager.getInstance().remove( id );
      empty = IdManager.getInstance().isEmpty();
    }
    if( empty ) {
      final UICallBackManager instance = UICallBackManager.getInstance();
      instance.setActive( false );
      instance.sendUICallBack();
    }
  }

  private static IDisplayAdapter getDisplayAdapter( final Display display ) {
    return ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
  }

  //////////////////////////
  // Service helping methods
  
  static void writeResponse() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    response.setHeader( HTML.CONTENT_TYPE, HTML.CONTENT_TEXT_JAVASCRIPT_UTF_8 );
    PrintWriter writer = response.getWriter();
    writer.print( jsUICallBack() );
    writer.flush();
  }

  private static String jsUICallBack() {
    String result;
    if(    isUICallBackActive()
        && !UICallBackManager.getInstance().isCallBackRequestBlocked() )
    {
      ISessionStore session = ContextProvider.getSession();
      String bufferedCode
        = ( String )session.getAttribute( BUFFERED_SEND_CALLBACK_REQUEST );
      if( bufferedCode == null ) {
        StringBuffer code = new StringBuffer();
        code.append( JS_SEND_UI_REQUEST );
        code.append( JS_SEND_CALLBACK_REQUEST );
        bufferedCode = code.toString();
        session.setAttribute( BUFFERED_SEND_CALLBACK_REQUEST, bufferedCode );
      }
      result = bufferedCode;
    } else {
      result = JS_SEND_UI_REQUEST;
    }
    return result;
  }

  public static String jsEnableUICallBack() {
    String result = "";
    if(    isUICallBackActive()
        && !UICallBackManager.getInstance().isCallBackRequestBlocked() )
    {
      result = JS_SEND_CALLBACK_REQUEST;
    }
    return result;
  }

  private static boolean isUICallBackActive() {
    synchronized( IdManager.getInstance().getLock() ) {
      return !IdManager.getInstance().isEmpty();
    }
  }
}
