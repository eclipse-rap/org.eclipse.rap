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

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

class CutOffContextPathWrapper extends HttpServlet {
  private static final long serialVersionUID = 1L;

  private final HttpServlet servlet;
  private final String alias;

  static class RequestWrapper extends HttpServletRequestWrapper {
    private final String alias;

    RequestWrapper( HttpServletRequest request, String alias ) {
      super( request );
      this.alias = alias;
    }
    
    @Override
    public String getServletPath() {
      return "/" + alias;
    }
  }
  
  CutOffContextPathWrapper( HttpServlet servlet, String alias ) {
    this.servlet = servlet;
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
  public Enumeration getInitParameterNames() {
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
    servlet.service( new RequestWrapper( ( HttpServletRequest )req, alias ), res );
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