/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.*;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.eclipse.rwt.application.ApplicationConfigurator;


class ServletContextWrapper implements ServletContext {
  private final ServletContext servletContext;
  private final Map<String, Object> attributes;

  ServletContextWrapper( ServletContext servletContext, String contextDirectory ) {
    this.servletContext = servletContext;
    this.attributes = new HashMap<String, Object>();
    this.attributes.put( ApplicationConfigurator.RESOURCE_ROOT_LOCATION, contextDirectory );
    
  }

  public ServletContext getContext( String uripath ) {
    return servletContext.getContext( uripath );
  }

  public String getContextPath() {
    return servletContext.getContextPath();
  }

  public int getMajorVersion() {
    return servletContext.getMajorVersion();
  }

  public int getMinorVersion() {
    return servletContext.getMinorVersion();
  }

  public String getMimeType( String file ) {
    return servletContext.getMimeType( file );
  }

  public Set<String> getResourcePaths( String path ) {
    return servletContext.getResourcePaths( path );
  }

  public URL getResource( String path ) throws MalformedURLException {
    return servletContext.getResource( path );
  }

  public InputStream getResourceAsStream( String path ) {
    return servletContext.getResourceAsStream( path );
  }

  public RequestDispatcher getRequestDispatcher( String path ) {
    return servletContext.getRequestDispatcher( path );
  }

  public RequestDispatcher getNamedDispatcher( String name ) {
    return servletContext.getNamedDispatcher( name );
  }

  @Deprecated
  public Servlet getServlet( String name ) throws ServletException {
    return servletContext.getServlet( name );
  }

  @Deprecated
  public Enumeration<Servlet> getServlets() {
    return servletContext.getServlets();
  }

  @Deprecated
  public Enumeration<String> getServletNames() {
    return servletContext.getServletNames();
  }

  public void log( String msg ) {
    servletContext.log( msg );
  }

  @Deprecated
  public void log( Exception exception, String msg ) {
    servletContext.log( exception, msg );
  }

  @Deprecated
  public void log( String message, Throwable throwable ) {
    servletContext.log( message, throwable );
  }

  public String getRealPath( String path ) {
    return servletContext.getRealPath( path );
  }

  public String getServerInfo() {
    return servletContext.getServerInfo();
  }

  public String getInitParameter( String name ) {
    return servletContext.getInitParameter( name );
  }

  public Enumeration<String> getInitParameterNames() {
    return servletContext.getInitParameterNames();
  }

  public Object getAttribute( String name ) {
    Object result;
    synchronized( attributes ) {
      if( isAttributeInWrappedContext( name ) ) {
        result = servletContext.getAttribute( name );
      } else {
        result = attributes.get( name );
      }
    }
    return result;
  }

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

  private boolean needEnumerationFromLocalAttributeBuffer( Enumeration result ) {
    return ( result == null || !result.hasMoreElements() ) && !attributes.isEmpty();
  }

  private Enumeration<String> createAttributeNamesEnumeration() {
    return new Enumeration<String>() {
      Iterator<String> names = attributes.keySet().iterator();

      public boolean hasMoreElements() {
        return names.hasNext();
      }

      public String nextElement() {
        return names.next();
      }
    };
  }

  public void setAttribute( String name, Object object ) {
    synchronized( attributes ) {
      servletContext.setAttribute( name, object );
      if( !isAttributeInWrappedContext( name ) ) {
        attributes.put( name, object );
      }
    }
  }

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

  public String getServletContextName() {
    return servletContext.getServletContextName();
  }

  public int getEffectiveMajorVersion() {
    return 0;
  }

  public int getEffectiveMinorVersion() {
    return 0;
  }

  public boolean setInitParameter( String name, String value ) {
    return false;
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
