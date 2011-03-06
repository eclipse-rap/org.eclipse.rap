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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.*;


public final class TestServletContext implements ServletContext {

  private String servletContextName;
  private final Map initParameters = new HashMap();
  private Map attributes = new HashMap();
  private TestLogger logger;

  public void setLogger( final TestLogger logger ) {
    this.logger = logger;
  }
  
  public ServletContext getContext( final String arg0 ) {
    return null;
  }

  public int getMajorVersion() {
    return 0;
  }

  public int getMinorVersion() {
    return 0;
  }

  public String getMimeType( final String arg0 ) {
    return null;
  }

  public Set getResourcePaths( final String arg0 ) {
    return null;
  }

  public URL getResource( final String arg0 ) throws MalformedURLException {
    return null;
  }

  public InputStream getResourceAsStream( final String arg0 ) {
    return null;
  }

  public RequestDispatcher getRequestDispatcher( final String arg0 ) {
    return null;
  }

  public RequestDispatcher getNamedDispatcher( final String arg0 ) {
    return null;
  }

  public Servlet getServlet( final String arg0 ) throws ServletException {
    return null;
  }

  public Enumeration getServlets() {
    return null;
  }

  public Enumeration getServletNames() {
    return null;
  }

  public void log( final String arg0 ) {
    log( arg0, null );
  }

  public void log( final Exception arg0, final String arg1 ) {
    log( arg1, arg0 );
  }

  public void log( final String arg0, final Throwable arg1 ) {
    if( logger != null ) {
      logger.log( arg0, arg1 );
    }
  }

  public String getRealPath( final String path ) {
    return Fixture.WEB_CONTEXT_DIR + path;
  }

  public String getServerInfo() {
    return null;
  }

  public String getInitParameter( final String name ) {
    return ( String )initParameters.get( name );
  }
  
  public void setInitParameter( final String name, final String value ) {
    initParameters.put( name, value );
  }

  public Enumeration getInitParameterNames() {
    return null;
  }

  public Object getAttribute( final String arg0 ) {
    return attributes.get( arg0 );
  }

  public Enumeration getAttributeNames() {
    return null;
  }

  public void setAttribute( final String arg0, final Object arg1 ) {
    attributes .put( arg0, arg1 );
  }

  public void removeAttribute( final String arg0 ) {
    attributes.remove( arg0 );
  }

  public String getServletContextName() {
    return servletContextName;
  }
  
  public void setServletContextName( final String servletContextName ) {
    this.servletContextName = servletContextName;
  }

  public String getContextPath() {
    return null;
  }
  
}