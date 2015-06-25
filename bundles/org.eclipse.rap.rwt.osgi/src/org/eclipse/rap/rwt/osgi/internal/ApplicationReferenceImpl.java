/*******************************************************************************
 * Copyright (c) 2011, 2015 Frank Appel and others.
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

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ApplicationRunner;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.eclipse.rap.rwt.osgi.ApplicationReference;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;


class ApplicationReferenceImpl implements ApplicationReference {

  static final String SERVLET_CONTEXT_FINDER_ALIAS = "/servlet_context_finder";
  static final String DEFAULT_ALIAS = "/rap";

  private ApplicationConfiguration configuration;
  private HttpService httpService;
  private HttpContext httpContext;
  private String contextLocation;
  private String contextName;
  private ServletContext servletContextWrapper;
  private ApplicationRunner applicationRunner;
  private ApplicationLauncherImpl applicationLauncher;
  private ServiceRegistration<?> serviceRegistration;
  private volatile boolean alive;

  ApplicationReferenceImpl( ApplicationConfiguration configuration,
                            HttpService httpService,
                            HttpContext httpContext,
                            String contextName,
                            String contextLocation,
                            ApplicationLauncherImpl applicationLauncher )
  {
    this.configuration = configuration;
    this.httpService = httpService;
    this.httpContext = wrapHttpContext( httpContext );
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
    applicationRunner = new ApplicationRunner( configuration, servletContextWrapper );
  }

  private void startRWTApplication() {
    applicationRunner.start();
    registerServlets();
    registerResourceDirectory();
    registerAsService();
  }

  private void registerServlets() {
    Collection<String> aliases = getServletPaths();
    if( aliases.isEmpty() ) {
      registerServlet( DEFAULT_ALIAS, new RWTServlet() );
    }
    for( String alias : aliases ) {
      registerServlet( alias, new RWTServlet() );
    }
  }

  @Override
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
    // We unregister servlets at the end, because the servlet bridge blocks while unregistering
    // servlets with standing requests. Stopping the application releases standing push requests.
    // See bug 407371: Tomcat hangs during shutdown
    Collection<String> aliases = getServletPaths();
    serviceRegistration.unregister();
    applicationRunner.stop();
    unregisterServlets( aliases );
    unregisterResourcesDirectory();
  }

  private void unregisterServlets( Collection<String> aliases ) {
    if( aliases.isEmpty() ) {
      unregisterServlet( DEFAULT_ALIAS );
    }
    for( String alias : aliases ) {
      unregisterServlet( alias );
    }
  }

  @SuppressWarnings( "restriction" )
  private Collection<String> getServletPaths() {
    ApplicationContext applicationContext = applicationRunner.getApplicationContext();
    org.eclipse.rap.rwt.internal.application.ApplicationContextImpl applicationContextImpl
      = ( org.eclipse.rap.rwt.internal.application.ApplicationContextImpl ) applicationContext;
    return applicationContextImpl.getEntryPointManager().getServletPaths();
  }

  boolean belongsTo( Object service ) {
    return configuration == service || httpService == service;
  }

  private void unregisterServletContextProviderServlet() {
    unregisterServlet( SERVLET_CONTEXT_FINDER_ALIAS );
  }

  private HttpContext wrapHttpContext( HttpContext context ) {
    HttpContext wrapped = context != null ? context : httpService.createDefaultHttpContext();
    return new HttpContextWrapper( wrapped );
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
    String alias = ApplicationRunner.RESOURCES;
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
    applicationRunner = null;
    httpService = null;
    httpContext = null;
    configuration = null;
    contextName = null;
    contextLocation = null;
    applicationLauncher = null;
    servletContextWrapper = null;
  }

  private void unregisterServlet( String alias ) {
    httpService.unregister( getContextSegment() + alias );
  }

  private void unregisterResourcesDirectory() {
    httpService.unregister( getContextSegment() + "/" + ApplicationRunner.RESOURCES );
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
