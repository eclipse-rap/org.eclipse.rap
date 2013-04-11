/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.eclipse.rap.rwt.osgi.ApplicationReference;
import org.eclipse.rap.rwt.osgi.internal.ServiceContainer.ServiceHolder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;


public class ApplicationLauncherImpl implements ApplicationLauncher {

  private final Object lock;
  private final ServiceContainer<ApplicationConfiguration> configurations;
  private final ServiceContainer<HttpService> httpServices;
  private final HashSet<ApplicationReferenceImpl> applicationReferences;
  private BundleContext bundleContext;

  public ApplicationLauncherImpl( BundleContext bundleContext ) {
    lock = new Object();
    configurations = new ServiceContainer<ApplicationConfiguration>( bundleContext );
    httpServices = new ServiceContainer<HttpService>( bundleContext );
    applicationReferences = new HashSet<ApplicationReferenceImpl>();
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

  public ApplicationConfiguration addConfiguration( ServiceReference<ApplicationConfiguration> ref )
  {
    ServiceHolder<ApplicationConfiguration> configurationHolder;
    synchronized( lock ) {
      configurationHolder = configurations.add( ref );
      launchWithConfiguration( configurationHolder );
    }
    return configurationHolder.getService();
  }

  public void removeConfiguration( ApplicationConfiguration configuration ) {
    synchronized( lock ) {
      configurations.remove( configuration );
      stopApplicationReferences( configuration );
    }
  }

  public ApplicationReference launch( ApplicationConfiguration configuration,
                                      HttpService httpService,
                                      HttpContext httpContext,
                                      String contextName,
                                      String contextDirectory )
  {
    synchronized( lock ) {
      ApplicationReference result = null;
      if( isAlive() ) {
        result = doLaunch( configuration, httpService, httpContext, contextName, contextDirectory );
      }
      return result;
    }
  }

  private ApplicationReferenceImpl doLaunch( ApplicationConfiguration configuration,
                                             HttpService httpService,
                                             HttpContext httpContext,
                                             String contextName,
                                             String contextDirectory )
  {
    ApplicationReferenceImpl result = new ApplicationReferenceImpl( configuration,
                                                                    httpService,
                                                                    httpContext,
                                                                    contextName,
                                                                    contextDirectory,
                                                                    this );
    result.start();
    applicationReferences.add( result );
    httpServices.add( httpService );
    configurations.add( configuration );
    return result;
  }


  public void deactivate() {
    synchronized( lock ) {
      stopAllApplicationReferences();
      configurations.clear();
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
    ServiceHolder<ApplicationConfiguration>[] services = configurations.getServices();
    for( ServiceHolder<ApplicationConfiguration> configurationHolder : services ) {
      if( matches( httpServiceHolder, configurationHolder ) ) {
        launch( configurationHolder, httpServiceHolder );
      }
    }
  }

  private void launchWithConfiguration( ServiceHolder<ApplicationConfiguration> configurationHolder )
  {
    ServiceHolder<HttpService>[] services = httpServices.getServices();
    for( ServiceHolder<HttpService> httpServiceHolder : services ) {
      if( matches( httpServiceHolder, configurationHolder ) ) {
        launch( configurationHolder, httpServiceHolder );
      }
    }
  }

  private void launch( ServiceHolder<ApplicationConfiguration> configurationHolder,
                       ServiceHolder<HttpService> httpServiceHolder )
  {
    ApplicationConfiguration configuration = configurationHolder.getService();
    HttpService httpService = httpServiceHolder.getService();
    String contextName = getContextName( configurationHolder );
    String contextLocation = getLocation( contextName, configuration, httpService );
    try {
      launch( configuration, httpService, null, contextName, contextLocation );
    } catch( RuntimeException rte ) {
      logProblem( "Unable to start RWT application.", rte );
    }
  }

  private String getContextName( ServiceHolder<ApplicationConfiguration> configurationHolder ) {
    ServiceReference<ApplicationConfiguration> reference = configurationHolder.getReference();
    return ( String )reference.getProperty( PROPERTY_CONTEXT_NAME );
  }

  private void stopApplicationReferences( Object service ) {
    ArrayList<ApplicationReferenceImpl> allReferences
      = new ArrayList<ApplicationReferenceImpl>( applicationReferences );
    for( ApplicationReferenceImpl applicationReference : allReferences ) {
      if( applicationReference.belongsTo( service ) ) {
        stopApplicationReference( applicationReference );
      }
    }
  }

  private void stopAllApplicationReferences() {
    ArrayList<ApplicationReferenceImpl> allReferences
      = new ArrayList<ApplicationReferenceImpl>( applicationReferences );
    for( ApplicationReferenceImpl applicationReference : allReferences ) {
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
                           ServiceHolder<ApplicationConfiguration> configurationHolder )
  {
    ServiceReference<HttpService> httpServiceRef = httpServiceHolder.getReference();
    ServiceReference<ApplicationConfiguration> configurationRef = configurationHolder.getReference();
    return new Matcher( httpServiceRef, configurationRef ).matches();
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
                      ApplicationConfiguration configuration,
                      HttpService service )
  {
    String pathToContext = getContextFileName( contextName, configuration, service );
    File dataFile = bundleContext.getDataFile( pathToContext );
    return dataFile.toString();
  }

  static String getContextFileName( String name,
                                    ApplicationConfiguration configuration,
                                    HttpService service )
  {
    StringBuilder result = new StringBuilder();
    result.append( name == null ? "rwtcontext" : name );
    result.append( "_" );
    result.append( configuration.hashCode() );
    result.append( "_" );
    result.append( service.hashCode() );
    return result.toString();
  }
}
