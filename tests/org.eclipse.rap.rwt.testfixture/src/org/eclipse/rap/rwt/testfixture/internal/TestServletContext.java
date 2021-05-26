/*******************************************************************************
 * Copyright (c) 2009, 2021 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture.internal;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;


/**
 * <p>
 * <strong>IMPORTANT:</strong> This class is <em>not</em> part the public RAP
 * API. It may change or disappear without further notice. Use this class at
 * your own risk.
 * </p>
 */
public class TestServletContext implements ServletContext {
  private final Map<String,Object> initParameters;
  private final Map<String,Object> attributes;
  private final Map<String,FilterRegistration> filters;
  private final Map<String,ServletRegistration> servlets;
  private String servletContextName;
  private TestLogger logger;
  private int majorVersion;
  private int minorVersion;

  public TestServletContext() {
    initParameters = new HashMap<String,Object>();
    attributes = new HashMap<String,Object>();
    filters = new HashMap<String,FilterRegistration>();
    servlets = new HashMap<String,ServletRegistration>();
  }

  public void setVersion( int majorVersion, int minorVersion ) {
    this.majorVersion = majorVersion;
    this.minorVersion = minorVersion;
  }

  public void setLogger( TestLogger logger ) {
    this.logger = logger;
  }

  @Override
  public ServletContext getContext( String arg0 ) {
    return null;
  }

  @Override
  public int getMajorVersion() {
    return majorVersion;
  }

  @Override
  public int getMinorVersion() {
    return minorVersion;
  }

  @Override
  public String getMimeType( String arg0 ) {
    return null;
  }

  @Override
  public Set<String> getResourcePaths( String arg0 ) {
    return null;
  }

  @Override
  public URL getResource( String arg0 ) throws MalformedURLException {
    return null;
  }

  @Override
  public InputStream getResourceAsStream( String arg0 ) {
    return null;
  }

  @Override
  public RequestDispatcher getRequestDispatcher( String arg0 ) {
    return null;
  }

  @Override
  public RequestDispatcher getNamedDispatcher( String arg0 ) {
    return null;
  }

  @Override
  public Servlet getServlet( String arg0 ) throws ServletException {
    return null;
  }

  @Override
  public Enumeration<Servlet> getServlets() {
    return null;
  }

  @Override
  public Enumeration<String> getServletNames() {
    return null;
  }

  @Override
  public void log( String arg0 ) {
    log( arg0, null );
  }

  @Override
  public void log( Exception arg0, String arg1 ) {
    log( arg1, arg0 );
  }

  @Override
  public void log( String arg0, Throwable arg1 ) {
    if( logger != null ) {
      logger.log( arg0, arg1 );
    }
  }

  @Override
  public String getRealPath( String path ) {
    return Fixture.WEB_CONTEXT_DIR + path;
  }

  @Override
  public String getServerInfo() {
    return null;
  }

  @Override
  public String getInitParameter( String name ) {
    return ( String )initParameters.get( name );
  }

  @Override
  public boolean setInitParameter( String name, String value ) {
    initParameters.put( name, value );
    return true;
  }

  @Override
  public Enumeration<String> getInitParameterNames() {
    return null;
  }

  @Override
  public Object getAttribute( String arg0 ) {
    return attributes.get( arg0 );
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return new Enumeration<String>() {
      Iterator<String> iterator = attributes.keySet().iterator();

      @Override
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }

      @Override
      public String nextElement() {
        return iterator.next();
      }
    };
  }

  @Override
  public void setAttribute( String arg0, Object arg1 ) {
    attributes.put( arg0, arg1 );
  }

  @Override
  public void removeAttribute( String arg0 ) {
    attributes.remove( arg0 );
  }

  @Override
  public String getServletContextName() {
    return servletContextName;
  }

  public void setServletContextName( String servletContextName ) {
    this.servletContextName = servletContextName;
  }

  @Override
  public String getVirtualServerName() {
    return null;
  }

  @Override
  public String getContextPath() {
    return null;
  }

  @Override
  public int getEffectiveMajorVersion() {
    return 0;
  }

  @Override
  public int getEffectiveMinorVersion() {
    return 0;
  }

  @Override
  public Dynamic addServlet( String servletName, String className ) {
    return null;
  }

  @Override
  public Dynamic addServlet( String servletName, Servlet servlet ) {
    TestServletRegistration result = new TestServletRegistration( servletName, servlet );
    servlets.put( servletName, result );
    return result;
  }

  @Override
  public Dynamic addServlet( String servletName, Class<? extends Servlet> servletClass ) {
    return null;
  }

  @Override
  public <T extends Servlet> T createServlet( Class<T> clazz ) throws ServletException {
    return null;
  }

  @Override
  public ServletRegistration getServletRegistration( String servletName ) {
    return servlets.get( servletName );
  }

  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    return servlets;
  }

  @Override
  public javax.servlet.FilterRegistration.Dynamic addFilter( String filterName, String className ) {
    return null;
  }

  @Override
  public javax.servlet.FilterRegistration.Dynamic addFilter( String filterName, Filter filter ) {
    TestFilterRegistration result = new TestFilterRegistration( filterName, filter );
    filters.put( filterName, result );
    return result;
  }

  @Override
  public javax.servlet.FilterRegistration.Dynamic addFilter( String filterName,
                                                             Class<? extends Filter> filterClass )
  {
    return null;
  }

  @Override
  public <T extends Filter> T createFilter( Class<T> clazz ) throws ServletException {
    return null;
  }

  @Override
  public FilterRegistration getFilterRegistration( String filterName ) {
    return filters.get( filterName );
  }

  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    return filters;
  }

  @Override
  public SessionCookieConfig getSessionCookieConfig() {
    return null;
  }

  @Override
  public void setSessionTrackingModes( Set<SessionTrackingMode> sessionTrackingModes ) {
  }

  @Override
  public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
    return null;
  }

  @Override
  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
    return null;
  }

  @Override
  public void addListener( String className ) {
  }

  @Override
  public <T extends EventListener> void addListener( T t ) {
  }

  @Override
  public void addListener( Class<? extends EventListener> listenerClass ) {
  }

  @Override
  public <T extends EventListener> T createListener( Class<T> clazz ) throws ServletException {
    return null;
  }

  @Override
  public JspConfigDescriptor getJspConfigDescriptor() {
    return null;
  }

  @Override
  public ClassLoader getClassLoader() {
    return null;
  }

  @Override
  public void declareRoles( String... roleNames ) {
  }

  @Override
  public Dynamic addJspFile( String servletName, String jspFile ) {
    return null;
  }

  @Override
  public int getSessionTimeout() {
    return 0;
  }

  @Override
  public void setSessionTimeout( int sessionTimeout ) {
  }

  @Override
  public String getRequestCharacterEncoding() {
    return null;
  }

  @Override
  public void setRequestCharacterEncoding( String encoding ) {
  }

  @Override
  public String getResponseCharacterEncoding() {
    return null;
  }

  @Override
  public void setResponseCharacterEncoding( String encoding ) {
  }

}
