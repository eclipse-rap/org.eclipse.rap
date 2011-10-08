/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;


class TestServletRegistration implements ServletRegistration.Dynamic {
  private final String servletName;
  private final String className;

  TestServletRegistration( String servletName, Servlet servlet ) {
    this.servletName = servletName;
    this.className = servlet.getClass().getName();
  }

  public String getName() {
    return servletName;
  }

  public String getClassName() {
    return className;
  }

  public boolean setInitParameter( String name, String value ) {
    return false;
  }

  public String getInitParameter( String name ) {
    return null;
  }

  public Set<String> setInitParameters( Map<String, String> initParameters ) {
    return null;
  }

  public Map<String, String> getInitParameters() {
    return null;
  }

  public Set<String> addMapping( String... urlPatterns ) {
    return null;
  }

  public Collection<String> getMappings() {
    return null;
  }

  public String getRunAsRole() {
    return null;
  }

  public void setAsyncSupported( boolean isAsyncSupported ) {
  }

  public void setLoadOnStartup( int loadOnStartup ) {
  }

  public Set<String> setServletSecurity( ServletSecurityElement constraint ) {
    return null;
  }

  public void setMultipartConfig( MultipartConfigElement multipartConfig ) {
  }

  public void setRunAsRole( String roleName ) {
  }
}
