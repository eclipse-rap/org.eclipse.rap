/*******************************************************************************
 * Copyright (c) 2011, 2024 Frank Appel and others.
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
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ApplicationRunner;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.eclipse.rap.rwt.osgi.ApplicationReference;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.osgi.framework.*;
import org.osgi.service.servlet.runtime.HttpServiceRuntime;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;


class ApplicationReferenceImpl implements ApplicationReference {

  static final String SERVLET_CONTEXT_FINDER_ALIAS = "/servlet_context_finder";
  static final String DEFAULT_ALIAS = "/rap";
  static final String RAP_HTTP_CONTEXT_CLASS_NAME = "org.eclipse.rap.ui.internal.RAPHttpContext";

  private ApplicationConfiguration configuration;
  private HttpServiceRuntime httpService;
  private String contextLocation;
  private String contextName;
  private ServletContext servletContextWrapper;
  private ApplicationRunner applicationRunner;
  private ApplicationLauncherImpl applicationLauncher;
  private ServiceRegistration<?> serviceRegistration;
  private volatile boolean alive;
  private BundleContext bundleContext;

  // Whiteboard-Registrierungen, damit wir sie beim Stoppen wieder abmelden koennen.
  private final Map<String, ServiceRegistration<Servlet>> servletRegistrations = new HashMap<>();
  private ServiceRegistration<?> resourceRegistration;

  ApplicationReferenceImpl( ApplicationConfiguration configuration,
                            HttpServiceRuntime httpService,
                            String contextName,
                            String contextLocation,
                            ApplicationLauncherImpl applicationLauncher )
  {
    this.configuration = configuration;
    this.httpService = httpService;
    this.contextLocation = contextLocation;
    this.contextName = contextName;
    this.applicationLauncher = applicationLauncher;
    this.bundleContext = FrameworkUtil.getBundle( getClass() ).getBundleContext();
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
      String pattern = getContextSegment() + alias;
      HttpServlet wrapper = new CutOffContextPathWrapper( servlet, servletContextWrapper, alias );
      Dictionary<String, Object> properties = new Hashtable<>();
      // Muster, unter dem das Servlet erreichbar ist (Whiteboard-Spec).
      properties.put( HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, pattern );
      // Eindeutiger Name; optional, aber empfohlen.
      properties.put( HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
                      "rwt-servlet" + pattern );
      ServiceRegistration<Servlet> registration
        = bundleContext.registerService( Servlet.class, wrapper, properties );
      servletRegistrations.put( pattern, registration );
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private void registerResourceDirectory() {
    String alias = ApplicationRunner.RESOURCES;
    String prefix = contextLocation + "/" + alias;
    String pattern = getContextSegment() + "/" + alias + "/*";
    try {
      Dictionary<String, Object> properties = new Hashtable<>();
      // Muster + Prefix fuer eine Whiteboard-Ressourcenregistrierung.
      properties.put( HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, pattern );
      properties.put( HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, prefix );
      // Ein beliebiges Marker-Objekt genuegt als Service-Instanz.
      resourceRegistration
        = bundleContext.registerService( Object.class, new Object(), properties );
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private void clearFields() {
    applicationRunner = null;
    configuration = null;
    contextName = null;
    contextLocation = null;
    applicationLauncher = null;
    servletContextWrapper = null;
  }

  private void unregisterServlet( String alias ) {
    String pattern = getContextSegment() + alias;
    ServiceRegistration<Servlet> registration = servletRegistrations.remove( pattern );
    if( registration != null ) {
      try {
        registration.unregister();
      } catch( IllegalStateException alreadyUnregistered ) {
        // Registrierung wurde bereits abgemeldet - ignorieren.
      }
    }
  }

  private void unregisterResourcesDirectory() {
    if( resourceRegistration != null ) {
      try {
        resourceRegistration.unregister();
      } catch( IllegalStateException alreadyUnregistered ) {
        // bereits abgemeldet - ignorieren.
      } finally {
        resourceRegistration = null;
      }
    }
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