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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.rap.rwt.osgi.*;
import org.eclipse.rap.rwt.osgi.internal.ServiceContainer.ServiceHolder;
import org.eclipse.rwt.engine.Configurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;


public class RWTServiceImpl implements RWTService {
  private final Object lock;
  private final ServiceContainer< Configurator > configurators;
  private final ServiceContainer< HttpService > httpServices;
  private final RWTContextContainer contexts;
  private final Set< RWTServiceObserver >observers;
  private BundleContext bundleContext;
  
  public RWTServiceImpl( BundleContext bundleContext ) {
    this.lock = new Object();
    this.configurators = new ServiceContainer< Configurator >( bundleContext );
    this.httpServices = new ServiceContainer< HttpService >( bundleContext );
    this.contexts = new RWTContextContainer();
    this.observers = new HashSet< RWTServiceObserver >();
    this.bundleContext = bundleContext;
  }
  
  public void addHttpService( ServiceReference<HttpService> reference ) {
    checkAlive();
    synchronized( lock ) {
      ServiceHolder<HttpService> httpService = httpServices.add( reference );
      startAtHttpService( httpService );
    }
  }
  
  public void removeHttpService( HttpService httpService ) {
    checkAlive();
    synchronized( lock ) {
      httpServices.remove( httpService );
      stopRWTContexts( httpService );
    }
  }

  public void addConfigurator( ServiceReference<Configurator> reference ) {
    checkAlive();
    synchronized( lock ) {
      ServiceHolder< Configurator > configurator = configurators.add( reference );
      startOfConfigurator( configurator );
    }
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
                            String contextName )
  {
    checkAlive();
    String contextLocation = getLocation( contextName, configurator, httpService );
    return start( configurator, httpService, contextName, contextLocation );
  }

  public RWTContext start( Configurator configurator,
                           HttpService httpService,
                           String contextName,
                           String contextLocation )
  {
    checkAlive();
    RWTContextImpl result
      = new RWTContextImpl( configurator, httpService, contextName, contextLocation, this );
    synchronized( lock ) {
      contexts.add( result );
      httpServices.add( httpService );
      configurators.add( configurator );
      result.start();
      notifyContextStarted( result );
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
      observers.clear();
      bundleContext = null;
    }
  }
  
  public boolean isAlive() {
    return bundleContext != null;
  }

  public void addObserver( RWTServiceObserver observer ) {
    RWTContextImpl[] all;
    synchronized( lock ) {
      observers.add( observer );
      all = contexts.getAll();
    }
    for( RWTContextImpl rwtContextImpl : all ) {
      notifyContextStarted( rwtContextImpl );
    }
  }

  public void removeObserver( RWTServiceObserver observer ) {
    synchronized( lock ) {
      observers.remove( observer );
    }
  }

  void notifyContextStopped( RWTContextImpl context ) {
    RWTServiceObserver[] observerList;
    synchronized( lock ) {
      contexts.remove( context );
      observerList = getObserverList();
    }
    notifyStoppedUnsynchronized( context, observerList );
  }

  private void notifyStoppedUnsynchronized( RWTContext context, RWTServiceObserver[] toNotify ) {
    for( RWTServiceObserver observer : toNotify ) {
      // TODO [fappel]: think about exception handling
      observer.contextStopped( context );
    }
  }

  private void notifyContextStarted( RWTContextImpl context ) {
    RWTServiceObserver[] observerList;
    synchronized( lock ) {
      observerList = getObserverList();
    }
    notifyStartedUnsynchronized( context, observerList );
  }

  private void notifyStartedUnsynchronized( RWTContext context, RWTServiceObserver[] toNotify ) {
    for( RWTServiceObserver observer : toNotify ) {
      // TODO [fappel]: think about exception handling
      observer.contextStarted( context );
    }
  }
  
  private RWTServiceObserver[] getObserverList() {
    RWTServiceObserver[] result = new RWTServiceObserver[ observers.size() ];
    observers.toArray( result );
    return result;
  }

  private void startAtHttpService( ServiceHolder< HttpService > httpServiceHolder ) {
    ServiceHolder<Configurator>[] services = configurators.getServices();
    for( ServiceHolder< Configurator > configuratorHolder : services ) {
      if( matches( httpServiceHolder, configuratorHolder ) ) {
        start( configuratorHolder, httpServiceHolder );
      }
    }
  }

  private void startOfConfigurator( ServiceHolder< Configurator > configuratorHolder ) {
    ServiceHolder< HttpService >[] services = httpServices.getServices();
    for( ServiceHolder< HttpService > httpServiceHolder : services ) {
      if( matches( httpServiceHolder, configuratorHolder ) ) {
        start( configuratorHolder, httpServiceHolder );
      }
    }
  }

  private void start( ServiceHolder< Configurator > configuratorHolder,
                      ServiceHolder< HttpService > httpServiceHolder )
  {
    Configurator configurator = configuratorHolder.getService();
    HttpService httpService = httpServiceHolder.getService();
    String contextName = getContextName( configuratorHolder );
    start( configurator, httpService, contextName );
  }

  private String getContextName( ServiceHolder< Configurator > configuratorHolder ) {
    ServiceReference< Configurator > reference = configuratorHolder.getReference();
    return ( String )reference.getProperty( PROPERTY_CONTEXT_NAME );
  }

  private void stopRWTContexts( Object service ) {
    RWTContextImpl[] iterator = contexts.getAll();
    for( RWTContextImpl context : iterator ) {
      if( context.belongsTo( service ) ) {
        context.stop();
      }
    }
  }

  private void stopAllContexts() {
    RWTContextImpl[] all = contexts.getAll();
    for( RWTContextImpl context : all ) {
      context.stop();
    }
  }

  private boolean matches( ServiceHolder< HttpService > httpServiceHolder,
                           ServiceHolder< Configurator > configuratorHolder )
  {
    ServiceReference< HttpService > httpServiceRef = httpServiceHolder.getReference();
    ServiceReference< Configurator > configuratorRef = configuratorHolder.getReference();
    return new Matcher( httpServiceRef, configuratorRef ).matches();
  }
  
  private void checkAlive() {
    if( !isAlive() ) {
      throw new IllegalStateException( "RWTService is not alive." );
    }
  }

  private String getLocation( String contextName, Configurator configurator, HttpService service ) {
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