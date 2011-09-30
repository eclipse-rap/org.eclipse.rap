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

import org.eclipse.rap.rwt.osgi.ApplicationReference;
import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.eclipse.rap.rwt.osgi.internal.ServiceContainer.ServiceHolder;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;


public class ApplicationLauncherImpl implements ApplicationLauncher {
  
  private final Object lock;
  private final ServiceContainer<ApplicationConfigurator> configurators;
  private final ServiceContainer<HttpService> httpServices;
  private final ApplicationReferencesContainer applicationReferences;
  private BundleContext bundleContext;

  public ApplicationLauncherImpl( BundleContext bundleContext ) {
    this.lock = new Object();
    this.configurators = new ServiceContainer<ApplicationConfigurator>( bundleContext );
    this.httpServices = new ServiceContainer<HttpService>( bundleContext );
    this.applicationReferences = new ApplicationReferencesContainer();
    this.bundleContext = bundleContext;
  }

  public HttpService addHttpService( ServiceReference<HttpService> reference ) {
    ServiceHolder<HttpService> httpServiceHolder;
    synchronized( lock ) {
      httpServiceHolder = httpServices.add( reference );
      launchAtHttpService( httpServiceHolder );
    }
    return httpServiceHolder.getService();
  }

  public void removeHttpService( HttpService httpService ) {
    synchronized( lock ) {
      httpServices.remove( httpService );
      stopApplicationReferences( httpService );
    }
  }

  public ApplicationConfigurator addConfigurator( ServiceReference<ApplicationConfigurator> ref ) {
    ServiceHolder<ApplicationConfigurator> configuratorHolder;
    synchronized( lock ) {
      configuratorHolder = configurators.add( ref );
      launchWithConfigurator( configuratorHolder );
    }
    return configuratorHolder.getService();
  }

  public void removeConfigurator( ApplicationConfigurator configurator ) {
    synchronized( lock ) {
      configurators.remove( configurator );
      stopApplicationReferences( configurator );
    }
  }

  public ApplicationReference launch( ApplicationConfigurator configurator,
                                      HttpService httpService,
                                      HttpContext httpContext,
                                      String contextName,
                                      String contextDirectory )
  {
    synchronized( lock ) {
      ApplicationReference result = null;
      if( isAlive() ) {
        result = doLaunch( configurator, httpService, httpContext, contextName, contextDirectory );
      }
      return result;
    }
  }

  private ApplicationReferenceImpl doLaunch( ApplicationConfigurator configurator,
                                             HttpService httpService,
                                             HttpContext httpContext,
                                             String contextName,
                                             String contextDirectory )
  {
    ApplicationReferenceImpl result = new ApplicationReferenceImpl( configurator,
                                                                    httpService,
                                                                    httpContext,
                                                                    contextName,
                                                                    contextDirectory,
                                                                    this );
    result.start();
    applicationReferences.add( result );
    httpServices.add( httpService );
    configurators.add( configurator );
    return result;
  }


  public void deactivate() {
    synchronized( lock ) {
      stopAllApplicationReferences();
      configurators.clear();
      applicationReferences.clear();
      httpServices.clear();
      bundleContext = null;
    }
  }

  boolean isAlive() {
    return bundleContext != null;
  }

  void notifyContextAboutToStop( ApplicationReferenceImpl applicationReference ) {
    synchronized( lock ) {
      applicationReferences.remove( applicationReference );
    }
  }

  BundleContext getBundleContext() {
    return bundleContext;
  }

  private void launchAtHttpService( ServiceHolder<HttpService> httpServiceHolder ) {
    ServiceHolder<ApplicationConfigurator>[] services = configurators.getServices();
    for( ServiceHolder<ApplicationConfigurator> configuratorHolder : services ) {
      if( matches( httpServiceHolder, configuratorHolder ) ) {
        launch( configuratorHolder, httpServiceHolder );
      }
    }
  }

  private void launchWithConfigurator( ServiceHolder<ApplicationConfigurator> configuratorHolder ) {
    ServiceHolder<HttpService>[] services = httpServices.getServices();
    for( ServiceHolder<HttpService> httpServiceHolder : services ) {
      if( matches( httpServiceHolder, configuratorHolder ) ) {
        launch( configuratorHolder, httpServiceHolder );
      }
    }
  }

  private void launch( ServiceHolder<ApplicationConfigurator> configuratorHolder,
                      ServiceHolder<HttpService> httpServiceHolder )
  {
    ApplicationConfigurator configurator = configuratorHolder.getService();
    HttpService httpService = httpServiceHolder.getService();
    String contextName = getContextName( configuratorHolder );
    String contextLocation = getLocation( contextName, configurator, httpService );
    try {
      launch( configurator, httpService, null, contextName, contextLocation );
    } catch( RuntimeException rte ) {
      logProblem( "Unable to start RWT application.", rte );
    }
  }

  private String getContextName( ServiceHolder<ApplicationConfigurator> configuratorHolder ) {
    ServiceReference<ApplicationConfigurator> reference = configuratorHolder.getReference();
    return ( String )reference.getProperty( PROPERTY_CONTEXT_NAME );
  }

  private void stopApplicationReferences( Object service ) {
    ApplicationReferenceImpl[] iterator = applicationReferences.getAll();
    for( ApplicationReferenceImpl applicationReference : iterator ) {
      if( applicationReference.belongsTo( service ) ) {
        stopApplicationReference( applicationReference );
      }
    }
  }

  private void stopAllApplicationReferences() {
    ApplicationReferenceImpl[] all = applicationReferences.getAll();
    for( ApplicationReferenceImpl applicationReference : all ) {
      stopApplicationReference( applicationReference );
    }
  }

  void stopApplicationReference( ApplicationReferenceImpl applicationReference ) {
    try {
      applicationReference.stopApplication();
    } catch( RuntimeException rte ) {
      logProblem( "Unable to stop ApplicationReference properly.", rte );
    }
  }

  private boolean matches( ServiceHolder<HttpService> httpServiceHolder,
                           ServiceHolder<ApplicationConfigurator> configuratorHolder )
  {
    ServiceReference<HttpService> httpServiceRef = httpServiceHolder.getReference();
    ServiceReference<ApplicationConfigurator> configuratorRef = configuratorHolder.getReference();
    return new Matcher( httpServiceRef, configuratorRef ).matches();
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

  String getLocation( String contextName,
                      ApplicationConfigurator configurator,
                      HttpService service )
  {
    String pathToContext = getContextFileName( contextName, configurator, service );
    File dataFile = bundleContext.getDataFile( pathToContext );
    return dataFile.toString();
  }

  static String getContextFileName( String name,
                                    ApplicationConfigurator configurator,
                                    HttpService service )
  {
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