/*******************************************************************************
 * Copyright (c) 2011, 2021 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.*;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;


class ServletContextWrapper implements ServletContext {
  private final ServletContext servletContext;
  private final Map<String, Object> attributes;

  ServletContextWrapper( ServletContext servletContext, String contextDirectory ) {
    this.servletContext = servletContext;
    attributes = new HashMap<>();
    attributes.put( ApplicationConfiguration.RESOURCE_ROOT_LOCATION, contextDirectory );
  }

  @Override
  public ServletContext getContext( String uripath ) {
    return servletContext.getContext( uripath );
  }

  @Override
  public String getContextPath() {
    return servletContext.getContextPath();
  }

  @Override
  public int getMajorVersion() {
    return servletContext.getMajorVersion();
  }

  @Override
  public int getMinorVersion() {
    return servletContext.getMinorVersion();
  }

  @Override
  public String getMimeType( String file ) {
    return servletContext.getMimeType( file );
  }

  @Override
  public Set<String> getResourcePaths( String path ) {
    return servletContext.getResourcePaths( path );
  }

  @Override
  public URL getResource( String path ) throws MalformedURLException {
    return servletContext.getResource( path );
  }

  @Override
  public InputStream getResourceAsStream( String path ) {
    return servletContext.getResourceAsStream( path );
  }

  @Override
  public RequestDispatcher getRequestDispatcher( String path ) {
    return servletContext.getRequestDispatcher( path );
  }

  @Override
  public RequestDispatcher getNamedDispatcher( String name ) {
    return servletContext.getNamedDispatcher( name );
  }

  @Override
  @Deprecated
  public Servlet getServlet( String name ) throws ServletException {
    return servletContext.getServlet( name );
  }

  @Override
  @Deprecated
  public Enumeration<Servlet> getServlets() {
    return servletContext.getServlets();
  }

  @Override
  @Deprecated
  public Enumeration<String> getServletNames() {
    return servletContext.getServletNames();
  }

  @Override
  public void log( String msg ) {
    servletContext.log( msg );
  }

  @Override
  @Deprecated
  public void log( Exception exception, String msg ) {
    servletContext.log( exception, msg );
  }

  @Override
  @Deprecated
  public void log( String message, Throwable throwable ) {
    servletContext.log( message, throwable );
  }

  @Override
  public String getRealPath( String path ) {
    return servletContext.getRealPath( path );
  }

  @Override
  public String getServerInfo() {
    return servletContext.getServerInfo();
  }

  @Override
  public String getInitParameter( String name ) {
    return servletContext.getInitParameter( name );
  }

  @Override
  public Enumeration<String> getInitParameterNames() {
    return servletContext.getInitParameterNames();
  }

  @Override
  public Object getAttribute( String name ) {
    synchronized( attributes ) {
      if( isAttributeInWrappedContext( name ) ) {
        return servletContext.getAttribute( name );
      }
      return attributes.get( name );
    }
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    Enumeration<String> result;
    synchronized( attributes ) {
      result = servletContext.getAttributeNames();
      if( needEnumerationFromLocalAttributeBuffer( result ) ) {
        result = createAttributeNamesEnumeration();
      }
    }
    return result;
  }

  private boolean needEnumerationFromLocalAttributeBuffer( Enumeration<String> result ) {
    return ( result == null || !result.hasMoreElements() ) && !attributes.isEmpty();
  }

  private Enumeration<String> createAttributeNamesEnumeration() {
    return new Enumeration<String>() {
      Iterator<String> names = attributes.keySet().iterator();

      @Override
      public boolean hasMoreElements() {
        return names.hasNext();
      }

      @Override
      public String nextElement() {
        return names.next();
      }
    };
  }

  @Override
  public void setAttribute( String name, Object object ) {
    synchronized( attributes ) {
      servletContext.setAttribute( name, object );
      if( !isAttributeInWrappedContext( name ) ) {
        attributes.put( name, object );
      }
    }
  }

  @Override
  public void removeAttribute( String name ) {
    synchronized( attributes ) {
      if( isAttributeInWrappedContext( name ) ) {
        servletContext.removeAttribute( name );
      } else {
        attributes.remove( name );
      }
    }
  }

  private boolean isAttributeInWrappedContext( String name ) {
    return null != servletContext.getAttribute( name );
  }

  @Override
  public String getServletContextName() {
    return servletContext.getServletContextName();
  }

  @Override
  public String getVirtualServerName() {
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
  public boolean setInitParameter( String name, String value ) {
    return false;
  }

  @Override
  public Dynamic addServlet( String servletName, String className ) {
    return null;
  }

  @Override
  public Dynamic addServlet( String servletName, Servlet servlet ) {
    return null;
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
    return null;
  }

  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    return null;
  }

  @Override
  public javax.servlet.FilterRegistration.Dynamic addFilter( String filterName, String className ) {
    return null;
  }

  @Override
  public javax.servlet.FilterRegistration.Dynamic addFilter( String filterName, Filter filter ) {
    return null;
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
    return null;
  }

  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    return null;
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
    return servletContext.getSessionTimeout();
  }

  @Override
  public void setSessionTimeout( int sessionTimeout ) {
    servletContext.setSessionTimeout( sessionTimeout );
  }

  @Override
  public String getRequestCharacterEncoding() {
    return servletContext.getRequestCharacterEncoding();
  }

  @Override
  public void setRequestCharacterEncoding( String encoding ) {
    servletContext.setRequestCharacterEncoding( encoding );
  }

  @Override
  public String getResponseCharacterEncoding() {
    return servletContext.getResponseCharacterEncoding();
  }

  @Override
  public void setResponseCharacterEncoding( String encoding ) {
    servletContext.setResponseCharacterEncoding( encoding );
  }

}
