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
package org.eclipse.rap.rwt.testfixture;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.*;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.eclipse.rap.rwt.testfixture.internal.engine.ThemeManagerHelper;


public class TestServletContext implements ServletContext {
  private final Map<String,Object> initParameters = new HashMap<String,Object>();
  private final Map<String,Object> attributes = new HashMap<String,Object>();
  private String servletContextName;
  private TestLogger logger;

  public void setLogger( TestLogger logger ) {
    this.logger = logger;
  }

  public ServletContext getContext( String arg0 ) {
    return null;
  }

  public int getMajorVersion() {
    return 0;
  }

  public int getMinorVersion() {
    return 0;
  }

  public String getMimeType( String arg0 ) {
    return null;
  }

  public Set<String> getResourcePaths( String arg0 ) {
    return null;
  }

  public URL getResource( String arg0 ) throws MalformedURLException {
    return null;
  }

  public InputStream getResourceAsStream( String arg0 ) {
    return null;
  }

  public RequestDispatcher getRequestDispatcher( String arg0 ) {
    return null;
  }

  public RequestDispatcher getNamedDispatcher( String arg0 ) {
    return null;
  }

  public Servlet getServlet( String arg0 ) throws ServletException {
    return null;
  }

  public Enumeration<Servlet> getServlets() {
    return null;
  }

  public Enumeration<String> getServletNames() {
    return null;
  }

  public void log( String arg0 ) {
    log( arg0, null );
  }

  public void log( Exception arg0, String arg1 ) {
    log( arg1, arg0 );
  }

  public void log( String arg0, Throwable arg1 ) {
    if( logger != null ) {
      logger.log( arg0, arg1 );
    }
  }

  public String getRealPath( String path ) {
    return Fixture.WEB_CONTEXT_DIR + path;
  }

  public String getServerInfo() {
    return null;
  }

  public String getInitParameter( String name ) {
    return ( String )initParameters.get( name );
  }

  public boolean setInitParameter( String name, String value ) {
    initParameters.put( name, value );
    return true;
  }

  public Enumeration<String> getInitParameterNames() {
    return null;
  }

  public Object getAttribute( String arg0 ) {
    return attributes.get( arg0 );
  }

  public Enumeration<String> getAttributeNames() {
    return new Enumeration<String>() {
      Iterator<String> iterator = attributes.keySet().iterator();

      public boolean hasMoreElements() {
        return iterator.hasNext();
      }

      public String nextElement() {
        return iterator.next();
      }
    };
  }

  public void setAttribute( String arg0, Object arg1 ) {
    ThemeManagerHelper.adaptApplicationContext( arg1 );
    attributes.put( arg0, arg1 );
  }

  public void removeAttribute( String arg0 ) {
    attributes.remove( arg0 );
  }

  public String getServletContextName() {
    return servletContextName;
  }

  public void setServletContextName( String servletContextName ) {
    this.servletContextName = servletContextName;
  }

  public String getContextPath() {
    return null;
  }

  public int getEffectiveMajorVersion() {
    return 0;
  }

  public int getEffectiveMinorVersion() {
    return 0;
  }

  public Dynamic addServlet( String servletName, String className ) {
    return null;
  }

  public Dynamic addServlet( String servletName, Servlet servlet ) {
    return null;
  }

  public Dynamic addServlet( String servletName, Class<? extends Servlet> servletClass ) {
    return null;
  }

  public <T extends Servlet> T createServlet( Class<T> clazz ) throws ServletException {
    return null;
  }

  public ServletRegistration getServletRegistration( String servletName ) {
    return null;
  }

  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    return null;
  }

  public javax.servlet.FilterRegistration.Dynamic addFilter( String filterName, String className ) {
    return null;
  }

  public javax.servlet.FilterRegistration.Dynamic addFilter( String filterName, Filter filter ) {
    return null;
  }

  public javax.servlet.FilterRegistration.Dynamic addFilter( String filterName,
                                                             Class<? extends Filter> filterClass )
  {
    return null;
  }

  public <T extends Filter> T createFilter( Class<T> clazz ) throws ServletException {
    return null;
  }

  public FilterRegistration getFilterRegistration( String filterName ) {
    return null;
  }

  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    return null;
  }

  public SessionCookieConfig getSessionCookieConfig() {
    return null;
  }

  public void setSessionTrackingModes( Set<SessionTrackingMode> sessionTrackingModes ) {
  }

  public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
    return null;
  }

  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
    return null;
  }

  public void addListener( String className ) {
  }

  public <T extends EventListener> void addListener( T t ) {
  }

  public void addListener( Class<? extends EventListener> listenerClass ) {
  }

  public <T extends EventListener> T createListener( Class<T> clazz ) throws ServletException {
    return null;
  }

  public JspConfigDescriptor getJspConfigDescriptor() {
    return null;
  }

  public ClassLoader getClassLoader() {
    return null;
  }

  public void declareRoles( String... roleNames ) {
  }
}