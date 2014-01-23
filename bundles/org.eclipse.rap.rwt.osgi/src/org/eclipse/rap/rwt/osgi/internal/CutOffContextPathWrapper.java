/*******************************************************************************
 * Copyright (c) 2011, 2014 Frank Appel and others.
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

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;


class CutOffContextPathWrapper extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private final HttpServlet servlet;
  private final String alias;
  private final ServletContext servletContext;

  static class RequestWrapper extends HttpServletRequestWrapper {
    private final String alias;
    private final HttpSession httpSession;

    RequestWrapper( HttpServletRequest request, ServletContext servletContext, String alias ) {
      super( request );
      httpSession = new HttpSessionWrapper( request.getSession(), servletContext );
      this.alias = alias;
    }

    @Override
    public String getServletPath() {
      return "/".equals( alias ) ? "" : alias;
    }

    @Override
    public HttpSession getSession() {
      return httpSession;
    }
  }

  CutOffContextPathWrapper( HttpServlet servlet, ServletContext servletContext, String alias ) {
    this.servlet = servlet;
    this.servletContext = servletContext;
    this.alias = alias;
  }

  @Override
  public void destroy() {
    servlet.destroy();
  }

  @Override
  public String getInitParameter( String name ) {
    return servlet.getInitParameter( name );
  }

  @Override
  public ServletConfig getServletConfig() {
    return servlet.getServletConfig();
  }

  @Override
  public Enumeration<String> getInitParameterNames() {
    return servlet.getInitParameterNames();
  }

  @Override
  public ServletContext getServletContext() {
    return servlet.getServletContext();
  }

  @Override
  public String getServletInfo() {
    return servlet.getServletInfo();
  }

  @Override
  public void init() throws ServletException {
    servlet.init();
  }

  @Override
  public void init( ServletConfig config ) throws ServletException {
    servlet.init( config );
  }

  @Override
  public String getServletName() {
    return servlet.getServletName();
  }

  @Override
  public void service( ServletRequest req, ServletResponse res )
    throws ServletException, IOException
  {
    RequestWrapper request = new RequestWrapper( ( HttpServletRequest )req, servletContext, alias );
    servlet.service( request, res );
  }

  @Override
  public void log( String message, Throwable t ) {
    servlet.log( message, t );
  }

  @Override
  public void log( String msg ) {
    servlet.log( msg );
  }

}
