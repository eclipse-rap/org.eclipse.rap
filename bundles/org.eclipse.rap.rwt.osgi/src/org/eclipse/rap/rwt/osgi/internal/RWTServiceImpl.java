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

import java.io.File;

import org.eclipse.rap.rwt.osgi.RWTContext;
import org.eclipse.rap.rwt.osgi.RWTService;
import org.eclipse.rap.rwt.osgi.internal.ServiceContainer.ServiceHolder;
import org.eclipse.rwt.engine.Configurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;


public class RWTServiceImpl implements RWTService {
  private final Object lock;
  private final ServiceContainer<Configurator> configurators;
  private final ServiceContainer<HttpService> httpServices;
  private final RWTContextContainer contexts;
  private BundleContext bundleContext;

  public RWTServiceImpl( BundleContext bundleContext ) {
    lock = new Object();
    configurators = new ServiceContainer<Configurator>( bundleContext );
    httpServices = new ServiceContainer<HttpService>( bundleContext );
    contexts = new RWTContextContainer();
    this.bundleContext = bundleContext;
  }

  public HttpService addHttpService( ServiceReference<HttpService> reference ) {
    checkAlive();
    ServiceHolder<HttpService> httpServiceHolder;
    synchronized( lock ) {
      httpServiceHolder = httpServices.add( reference );
      startAtHttpService( httpServiceHolder );
    }
    return httpServiceHolder.getService();
  }

  public void removeHttpService( HttpService httpService ) {
    checkAlive();
    synchronized( lock ) {
      httpServices.remove( httpService );
      stopRWTContexts( httpService );
    }
  }

  public Configurator addConfigurator( ServiceReference<Configurator> reference ) {
    checkAlive();
    ServiceHolder<Configurator> configuratorHolder;
    synchronized( lock ) {
      configuratorHolder = configurators.add( reference );
      startOfConfigurator( configuratorHolder );
    }
    return configuratorHolder.getService();
  }

  public void removeConfigurator( Configurator configurator ) {
    checkAlive();
    synchronized( lock ) {
      configurators.remove( configurator );
      stopRWTContexts( configurator );
    }
  }

  public RWTContext start( Configurator configurator,
                           HttpService httpService,
                           HttpContext httpContext,
                           String contextName,
                           String contextDirectory )
  {
    checkAlive();
    RWTContextImpl result = new RWTContextImpl( configurator,
                                                httpService,
                                                httpContext,
                                                contextName,
                                                contextDirectory,
                                                this );
    synchronized( lock ) {
      result.start();
      contexts.add( result );
      httpServices.add( httpService );
      configurators.add( configurator );

    }
    return result;
  }


  public void deactivate() {
    checkAlive();
    synchronized( lock ) {
      stopAllContexts();
      configurators.clear();
      contexts.clear();
      httpServices.clear();
      bundleContext = null;
    }
  }

  public boolean isAlive() {
    return bundleContext != null;
  }

  void notifyContextAboutToStop( RWTContextImpl context ) {
    synchronized( lock ) {
      contexts.remove( context );
    }
  }

  BundleContext getBundleContext() {
    return bundleContext;
  }

  private void startAtHttpService( ServiceHolder<HttpService> httpServiceHolder ) {
    ServiceHolder<Configurator>[] services = configurators.getServices();
    for( ServiceHolder<Configurator> configuratorHolder : services ) {
      if( matches( httpServiceHolder, configuratorHolder ) ) {
        start( configuratorHolder, httpServiceHolder );
      }
    }
  }

  private void startOfConfigurator( ServiceHolder<Configurator> configuratorHolder ) {
    ServiceHolder<HttpService>[] services = httpServices.getServices();
    for( ServiceHolder<HttpService> httpServiceHolder : services ) {
      if( matches( httpServiceHolder, configuratorHolder ) ) {
        start( configuratorHolder, httpServiceHolder );
      }
    }
  }

  private void start( ServiceHolder<Configurator> configuratorHolder,
                      ServiceHolder<HttpService> httpServiceHolder )
  {
    Configurator configurator = configuratorHolder.getService();
    HttpService httpService = httpServiceHolder.getService();
    String contextName = getContextName( configuratorHolder );
    String contextLocation = getLocation( contextName, configurator, httpService );
    try {
      start( configurator, httpService, null, contextName, contextLocation );
    } catch( RuntimeException rte ) {
      logProblem( "Unable to start RWTContext.", rte );
    }
  }

  private String getContextName( ServiceHolder<Configurator> configuratorHolder ) {
    ServiceReference<Configurator> reference = configuratorHolder.getReference();
    return ( String )reference.getProperty( PROPERTY_CONTEXT_NAME );
  }

  private void stopRWTContexts( Object service ) {
    RWTContextImpl[] iterator = contexts.getAll();
    for( RWTContextImpl context : iterator ) {
      if( context.belongsTo( service ) ) {
        stopContext( context );
      }
    }
  }

  private void stopAllContexts() {
    RWTContextImpl[] all = contexts.getAll();
    for( RWTContextImpl context : all ) {
      stopContext( context );
    }
  }

  void stopContext( RWTContextImpl context ) {
    try {
      context.stop();
    } catch( RuntimeException rte ) {
      logProblem( "Unable to stop RWTContext properly.", rte );
    }
  }

  private boolean matches( ServiceHolder<HttpService> httpServiceHolder,
                           ServiceHolder<Configurator> configuratorHolder )
  {
    ServiceReference<HttpService> httpServiceRef = httpServiceHolder.getReference();
    ServiceReference<Configurator> configuratorRef = configuratorHolder.getReference();
    return new Matcher( httpServiceRef, configuratorRef ).matches();
  }

  private void checkAlive() {
    if( !isAlive() ) {
      throw new IllegalStateException( "RWTService is not alive." );
    }
  }

  private void logProblem( String failureMessage, Throwable failure ) {
    ServiceReference logReference = bundleContext.getServiceReference( LogService.class.getName() );
    if( logReference != null ) {
      @SuppressWarnings( "unchecked" )
      LogService log = ( LogService )bundleContext.getService( logReference );
      log.log( LogService.LOG_ERROR, failureMessage, failure );
    } else {
      // TODO [fappel]: is there a better solution?
      System.err.println( failureMessage );
      failure.printStackTrace();
    }
  }

  String getLocation( String contextName, Configurator configurator, HttpService service ) {
    String pathToContext = getContextFileName( contextName, configurator, service );
    File dataFile = bundleContext.getDataFile( pathToContext );
    return dataFile.toString();
  }

  static String getContextFileName( String name, Configurator configurator, HttpService service ) {
    StringBuilder result = new StringBuilder();
    result.append( "/" );
    result.append( name == null ? "rwtcontext" : name );
    result.append( "_" );
    result.append( configurator.hashCode() );
    result.append( "_" );
    result.append( service.hashCode() );
    return result.toString();
  }
}
