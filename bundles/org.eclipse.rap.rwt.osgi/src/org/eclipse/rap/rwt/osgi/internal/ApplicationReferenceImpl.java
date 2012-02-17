/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
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

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.eclipse.rap.rwt.osgi.ApplicationReference;
import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.engine.RWTServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;


class ApplicationReferenceImpl implements ApplicationReference {

  static final String SERVLET_CONTEXT_FINDER_ALIAS = "/servlet_context_finder";
  static final String DEFAULT_ALIAS = "/rap";

  private ApplicationConfigurator configurator;
  private HttpService httpService;
  private HttpContext httpContext;
  private String contextLocation;
  private String contextName;
  private ServletContext servletContextWrapper;
  private Application application;
  private ApplicationLauncherImpl applicationLauncher;
  private ServiceRegistration<?> serviceRegistration;
  private volatile boolean alive;

  ApplicationReferenceImpl( ApplicationConfigurator configurator,
                            HttpService httpService,
                            HttpContext httpContext,
                            String contextName,
                            String contextLocation,
                            ApplicationLauncherImpl applicationLauncher )
  {
    this.configurator = configurator;
    this.httpService = httpService;
    this.httpContext = createWrappedHttpContext( httpContext );
    this.contextLocation = contextLocation;
    this.contextName = contextName;
    this.applicationLauncher = applicationLauncher;
  }

  void start() {
    createApplication( registerServletContextProviderServlet() );
    try {
      startRWTApplication();
    } finally {
      unregisterServletContextProviderServlet();
    }
    markAlive();
  }

  private void createApplication( HttpServlet contextProviderServlet ) {
    ServletContext servletContext = contextProviderServlet.getServletContext();
    servletContextWrapper = new ServletContextWrapper( servletContext, contextLocation );
    application = new Application( configurator, servletContextWrapper );
  }

  private void startRWTApplication() {
    application.start();
    registerServlets();
    registerResourceDirectory();
    registerAsService();
  }

  private void registerServlets() {
    Collection<String> aliases = application.getServletPaths();
    if( aliases.isEmpty() ) {
      registerServlet( DEFAULT_ALIAS, new RWTServlet() );
    }
    for( String alias : aliases ) {
      registerServlet( alias, new RWTServlet() );
    }
  }

  public void stopApplication() {
    if( !hasBeenStopped() ) {
      doStopApplication();
    }
  }

  private synchronized boolean hasBeenStopped() {
    boolean result = !alive;
    if( alive ) {
      markNotAlive();
    }
    return result;
  }

  private void doStopApplication() {
    registerServletContextProviderServlet();
    notifyAboutToStop();
    try {
      stopRWTApplication();
    } finally {
      unregisterServletContextProviderServlet();
    }
    clearFields();
  }

  private void stopRWTApplication() {
    unregisterServlets();
    unregisterResourcesDirectory();
    serviceRegistration.unregister();
    application.stop();
  }

  private void unregisterServlets() {
    Collection<String> aliases = application.getServletPaths();
    if( aliases.isEmpty() ) {
      unregisterServlet( DEFAULT_ALIAS );
    }
    for( String alias : aliases ) {
      unregisterServlet( alias );
    }
  }

  boolean belongsTo( Object service ) {
    return configurator == service || httpService == service;
  }

  private void unregisterServletContextProviderServlet() {
    unregisterServlet( SERVLET_CONTEXT_FINDER_ALIAS );
  }

  private HttpContext createWrappedHttpContext( HttpContext httpContext ) {
    HttpContext result;
    if( httpContext == null ) {
      result = wrapHttpContext( httpService.createDefaultHttpContext() );
    } else {
      result = wrapHttpContext( httpContext );
    }
    return result;
  }

  private HttpContextWrapper wrapHttpContext( HttpContext createDefaultHttpContext ) {
    return new HttpContextWrapper( createDefaultHttpContext );
  }

  private HttpServlet registerServletContextProviderServlet() {
    HttpServlet result = new HttpServlet() {
      private static final long serialVersionUID = 1L;
    };
    registerServlet( SERVLET_CONTEXT_FINDER_ALIAS, result );
    return result;
  }

  private void registerAsService() {
    String clazz = ApplicationReference.class.getName();
    serviceRegistration = getBundleContext().registerService( clazz, this, null );
  }

  private BundleContext getBundleContext() {
    // TODO [fappel]: use FrameworkUtil instead..
    return applicationLauncher.getBundleContext();
  }

  private void registerServlet( String alias, HttpServlet servlet ) {
    try {
      HttpServlet wrapper = new CutOffContextPathWrapper( servlet, servletContextWrapper, alias );
      httpService.registerServlet( getContextSegment() + alias, wrapper, null, httpContext );
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private void registerResourceDirectory() {
    String alias = Application.RESOURCES;
    String location = contextLocation + "/" + alias;
    try {
      httpService.registerResources( getContextSegment() + "/" + alias, location, httpContext );
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private void clearFields() {
    application = null;
    httpService = null;
    httpContext = null;
    configurator = null;
    contextName = null;
    contextLocation = null;
    applicationLauncher = null;
    servletContextWrapper = null;
  }

  private void unregisterServlet( String alias ) {
    httpService.unregister( getContextSegment() + alias );
  }

  private void unregisterResourcesDirectory() {
    httpService.unregister( getContextSegment() + "/" + Application.RESOURCES );
  }

  private void notifyAboutToStop() {
    applicationLauncher.notifyContextAboutToStop( this );
  }

  private String getContextSegment() {
    String result = "";
    if( contextName != null ) {
      result = "/" + contextName;
    }
    return result;
  }

  boolean isAlive() {
    return alive;
  }

  private void markAlive() {
    alive = true;
  }

  private void markNotAlive() {
    alive = false;
  }
}