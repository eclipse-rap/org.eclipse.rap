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


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.eclipse.rap.rwt.osgi.RWTContext;
import org.eclipse.rwt.engine.Configurator;
import org.eclipse.rwt.engine.ContextControl;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

class RWTContextImpl implements RWTContext {
  static final String SERVLET_CONTEXT_FINDER_ALIAS = "servlet_context_finder";
  static final String DEFAULT_ALIAS = "rwt";

  private Configurator configurator;
  private HttpService httpService;
  private HttpContext httpContext;
  private String contextLocation;
  private String contextName;
  private ContextControl contextControl;
  private boolean alive;

  public RWTContextImpl( Configurator configurator,
                         HttpService httpService,
                         String contextName, 
                         String contextLocation )
  {
    this.configurator = configurator;
    this.httpService = httpService;
    this.contextLocation = contextLocation;
    this.contextName = contextName;
  }
  
  public boolean isAlive() {
    return alive;
  }

  public void stop() {
    checkAlive();
    markNotAlive();
    registerServletContextProvider();
    try {
      stopContext();
    } finally {
      unregisterServletContextProvider();
    }
    clearFields();
  }

  void start() {
    createHttpContext();
    createContextControl( registerServletContextProvider() );
    try {
      startContext();
    } finally {
      unregisterServletContextProvider();
    }
    markAlive();
  }

  boolean belongsTo( Object service ) {
    return this.configurator == service || this.httpService == service;
  }

  private void createContextControl( HttpServlet contextProvider ) {
    ServletContext servletContext = contextProvider.getServletContext();
    ServletContext wrapper = new ServletContextWrapper( servletContext, contextLocation );
    contextControl = new ContextControl( wrapper, configurator );
  }

  private void unregisterServletContextProvider() {
    unregisterServlet( SERVLET_CONTEXT_FINDER_ALIAS );
  }

  private void createHttpContext() {
    httpContext = new HttpContextWrapper( httpService.createDefaultHttpContext() );
  }

  private HttpServlet registerServletContextProvider() {
    HttpServlet result = new HttpServlet() {
      private static final long serialVersionUID = 1L;
    };
    registerServlet( SERVLET_CONTEXT_FINDER_ALIAS, result );
    return result;
  }
  
  private void startContext() {
    contextControl.startContext();
    String[] aliases = contextControl.getServletNames();
    if( aliases.length == 0 ) {
      registerServlet( DEFAULT_ALIAS, contextControl.createServlet() );
    }
    for( String alias : aliases ) {
      registerServlet( alias, contextControl.createServlet() );
    }
    registerResourceDirectory();
  }

  private void registerServlet( String alias, final HttpServlet servlet ) {
    try {
      HttpServlet wrapper = new CutOffContextPathWrapper( servlet, alias );
      httpService.registerServlet( getContextSegment() + "/" + alias, wrapper, null, httpContext );
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }
  
  private void registerResourceDirectory() {
    String alias = ContextControl.RESOURCES;
    String location = contextLocation + "/" + alias;
    try {
      httpService.registerResources( getContextSegment() + "/" + alias, location, httpContext );
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private void stopContext() {
    String[] aliases = contextControl.getServletNames();
    if( aliases.length == 0 ) {
      unregisterServlet( DEFAULT_ALIAS );
    }
    for( String alias : aliases ) {
      unregisterServlet( alias );
    }
    unregisterResourcesDirectory();
    contextControl.stopContext();
  }

  private void clearFields() {
    contextControl = null;
    httpService = null;
    httpContext = null;
    configurator = null;
    contextName = null;
    contextLocation = null;
  }

  private void unregisterServlet( String alias ) {
    httpService.unregister( getContextSegment() + "/" + alias );
  }

  private void unregisterResourcesDirectory() {
    httpService.unregister( getContextSegment() + "/" + ContextControl.RESOURCES );
  }

  private String getContextSegment() {
    String result = "";
    if( contextName != null ) {
      result = "/" + contextName;
    }
    return result;
  }

  private void checkAlive() {
    if( !isAlive() ) {
      throw new IllegalStateException( "RWTContext is not alive." );
    }
  }
  
  private void markAlive() {
    alive = true;
  }
  
  private void markNotAlive() {
    alive = false;
  }
}