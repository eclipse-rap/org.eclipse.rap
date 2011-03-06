/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.*;
import java.security.Principal;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;


public final class TestRequest implements HttpServletRequest {
  public static final int PORT = 8080;
  public static final String SERVLET_PATH = "/W4TDelegate";
  public static final String REQUEST_URI = "/fooapp/W4TDelegate";
  public static final String CONTEX_PATH = "/fooapp";
  public static final String SERVER_NAME = "fooserver";

  private HttpSession session;
  private String scheme = "http";
  private String serverName = SERVER_NAME;
  private String contextPath = CONTEX_PATH;
  private String requestURI = REQUEST_URI;
  private final StringBuffer requestURL = new StringBuffer();
  private String servletPath = SERVLET_PATH;
  private String pathInfo;
  private Map parameters = new HashMap();
  private Map headers = new HashMap();
  private Map attributes = new HashMap();
  private Set cookies = new HashSet();
  private Locale locale;
  
  public String getAuthType() {
    return null;
  }
  
  public void addCookie( final Cookie cookie ) {
    cookies.add( cookie );
  }
  
  public Cookie[] getCookies() {
    return ( Cookie[] )cookies.toArray( new Cookie[ cookies.size() ] );
  }
  
  public long getDateHeader( final String arg0 ) {
    return 0;
  }
  
  public String getHeader( final String arg0 ) {
    return ( String )headers.get( arg0 );
  }
  
  public void setHeader(final String arg0, final String arg1) {
    headers.put(arg0, arg1);      
  }
  
  public Enumeration getHeaders( final String arg0 ) {
    return null;
  }
  
  public Enumeration getHeaderNames() {
    return new Enumeration() {
      private Iterator iterator = headers.keySet().iterator();
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }
      public Object nextElement() {
        return iterator.next();
      }
    };
  }
  
  public int getIntHeader( final String arg0 ) {
    return 0;
  }
  
  public String getMethod() {
    return null;
  }
  
  public String getPathInfo() {
    return pathInfo;
  }
  
  public void setPathInfo( final String pathInfo ) {
    this.pathInfo = pathInfo;
  }
  
  public String getPathTranslated() {
    return null;
  }
  
  public String getContextPath() {
    return contextPath;
  }
  
  public String getQueryString() {
    return null;
  }
  
  public String getRemoteUser() {
    return null;
  }
  
  public boolean isUserInRole( final String arg0 ) {
    return false;
  }
  
  public Principal getUserPrincipal() {
    return null;
  }
  
  public String getRequestedSessionId() {
    return null;
  }
  
  public String getRequestURI() {
    return requestURI;
  }
  
  public void setRequestURI( final String requestURI ) {
    this.requestURI = requestURI;
  }
  
  public StringBuffer getRequestURL() {
    return requestURL;
  }
  
  public String getServletPath() {
    return servletPath;
  }
  
  public void setServletPath( final String servletPath ) {
    this.servletPath = servletPath;
  }
  
  public HttpSession getSession( final boolean arg0 ) {
    return session;
  }
  
  /**
   * @return  Returns the session.
   * @uml.property  name="session"
   */
  public HttpSession getSession() {
    return session;
  }
  
  public boolean isRequestedSessionIdValid() {
    return false;
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
  
  public Object getAttribute( final String arg0 ) {
    return attributes.get( arg0 );
  }
  
  public Enumeration getAttributeNames() {
    return null;
  }
  
  public String getCharacterEncoding() {
    return null;
  }
  
  public void setCharacterEncoding( final String arg0 )
    throws UnsupportedEncodingException
  {
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
  
  public String getParameter( final String arg0 ) {
    String[] value = ( String[] )parameters.get( arg0 );
    String result = null;
    if( value != null ) {
      result = value[ 0 ];
    }
    return result;
  }
  
  public Enumeration getParameterNames() {
    return new Enumeration() {
      private Iterator iterator = parameters.keySet().iterator();
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }
      
      public Object nextElement() {
        return iterator.next();
      }
    };
  }
  
  public String[] getParameterValues( final String arg0 ) {
    return ( String[] )parameters.get( arg0 );
  }
  
  public void setParameter( final String key, final String value ) {      
    if( value == null ) {
      parameters.remove( key );
    } else {
      parameters.put( key, new String[] { value } );
    }
  }
  
  public void addParameter( final String key, final String value ) {
    if( parameters.containsKey( key ) ) {
      String[] values = ( String[] )parameters.get( key );
      String[] newValues = new String[ values.length + 1 ];
      System.arraycopy( values, 0, newValues, 0, values.length );
      newValues[ values.length ] = value;
      parameters.put( key, newValues );
    } else {
      setParameter( key, value );
    }
  }
  
  public Map getParameterMap() {
    return parameters;
  }
  
  public String getProtocol() {
    return null;
  }
  
  /**
   * @return  Returns the scheme.
   * @uml.property  name="scheme"
   */
  public String getScheme() {
    return scheme;
  }
  
  /**
   * @param scheme  The scheme to set.
   * @uml.property  name="scheme"
   */
  public void setScheme( final String scheme ) {
    this.scheme = scheme;
  }
  
  /**
   * @return  Returns the serverName.
   * @uml.property  name="serverName"
   */
  public String getServerName() {
    return serverName;
  }
  
  /**
   * @param serverName  The serverName to set.
   * @uml.property  name="serverName"
   */
  public void setServerName( final String serverName ) {
    this.serverName = serverName;
  }
  
  public int getServerPort() {
    return PORT;
  }
  
  public BufferedReader getReader() throws IOException {
    return null;
  }
  
  public String getRemoteAddr() {
    return null;
  }
  
  public String getRemoteHost() {
    return null;
  }
  
  public void setAttribute( final String arg0, final Object arg1 ) {
    attributes.put( arg0, arg1 );
  }
  
  public void removeAttribute( final String arg0 ) {
  }
  
  public Locale getLocale() {
    return locale == null ? Locale.getDefault() : locale ;
  }

  public void setLocale( final Locale locale ) {
    this.locale = locale;
  }

  public Enumeration getLocales() {
    return null;
  }
  
  public boolean isSecure() {
    return false;
  }
  
  public RequestDispatcher getRequestDispatcher( final String arg0 ) {
    return null;
  }
  
  public String getRealPath( final String arg0 ) {
    return null;
  }
  
  /**
   * @param session  The session to set.
   * @uml.property  name="session"
   */
  public void setSession( final HttpSession session ) {
    this.session = session;
  }

  public String getLocalAddr() {
    throw new UnsupportedOperationException();
  }

  public String getLocalName() {
    throw new UnsupportedOperationException();
  }

  public int getLocalPort() {
    throw new UnsupportedOperationException();
  }

  public int getRemotePort() {
    throw new UnsupportedOperationException();
  }
}