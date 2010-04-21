/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.*;
import java.security.Principal;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public final class FakeContextUtil {

  private FakeContextUtil() {
    // prevent instantiation
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
    //                the test cases...
    boolean wasMapped = false;
    if( useDifferentContext ) {
      contextBuffer = ContextProvider.getContext();
      wasMapped = ContextProvider.releaseContextHolder();
    }
    boolean useFakeContext = !ContextProvider.hasContext();
    if( useFakeContext ) {
      IDisplayAdapter adapter = getDisplayAdapter( display );
      ISessionStore session = adapter.getSession();
      ContextProvider.setContext( createFakeContext( session ) );
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

  private static IDisplayAdapter getDisplayAdapter( final Display display ) {
    return ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
  }

  public static ServiceContext createFakeContext( final ISessionStore store ) {
    HttpServletRequest request = new DummyRequest( store.getHttpSession() );
    HttpServletResponse response = new DummyResponse();
    return new ServiceContext( request, response, store );
  }

  private static final class DummyRequest implements HttpServletRequest {
  
    private final HttpSession session;
  
    DummyRequest( final HttpSession session ) {
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
}
