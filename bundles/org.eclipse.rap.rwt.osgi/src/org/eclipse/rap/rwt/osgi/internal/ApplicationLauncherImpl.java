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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.eclipse.rap.rwt.osgi.ApplicationReference;
import org.eclipse.rap.rwt.osgi.internal.ServiceContainer.ServiceHolder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.service.servlet.runtime.HttpServiceRuntime;


public class ApplicationLauncherImpl implements ApplicationLauncher {

  private final Object lock;
  private final ServiceContainer<ApplicationConfiguration> configurations;
  private final ServiceContainer<HttpServiceRuntime> httpServices;
  private final HashSet<ApplicationReferenceImpl> applicationReferences;
  private BundleContext bundleContext;

  public ApplicationLauncherImpl( BundleContext bundleContext ) {
    lock = new Object();
    configurations = new ServiceContainer<>( bundleContext );
    httpServices = new ServiceContainer<>( bundleContext );
    applicationReferences = new HashSet<>();
    this.bundleContext = bundleContext;
  }

  public HttpServiceRuntime addHttpService( ServiceReference<HttpServiceRuntime> reference ) {
    ServiceHolder<HttpServiceRuntime> httpServiceHolder;
    synchronized( lock ) {
      httpServiceHolder = httpServices.add( reference );
      launchAtHttpService( httpServiceHolder );
    }
    return httpServiceHolder.getService();
  }

  public void removeHttpService( HttpServiceRuntime httpService ) {
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

  @Override
  public ApplicationReference launch( ApplicationConfiguration configuration,
                                      HttpServiceRuntime httpService,
                                      String contextName,
                                      String contextDirectory )
  {
    synchronized( lock ) {
      if( isAlive() ) {
        return doLaunch( configuration, httpService, contextName, contextDirectory );
      }
      return null;
    }
  }

  private ApplicationReferenceImpl doLaunch( ApplicationConfiguration configuration,
                                             HttpServiceRuntime httpService,
                                             String contextName,
                                             String contextDirectory )
  {
    ApplicationReferenceImpl result = new ApplicationReferenceImpl( configuration,
                                                                    httpService,
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

  private void launchAtHttpService( ServiceHolder<HttpServiceRuntime> httpServiceHolder ) {
    ServiceHolder<ApplicationConfiguration>[] services = configurations.getServices();
    for( ServiceHolder<ApplicationConfiguration> configurationHolder : services ) {
      if( matches( httpServiceHolder, configurationHolder ) ) {
        launch( configurationHolder, httpServiceHolder );
      }
    }
  }

  private void launchWithConfiguration( ServiceHolder<ApplicationConfiguration> configurationHolder )
  {
    ServiceHolder<HttpServiceRuntime>[] services = httpServices.getServices();
    for( ServiceHolder<HttpServiceRuntime> httpServiceHolder : services ) {
      if( matches( httpServiceHolder, configurationHolder ) ) {
        launch( configurationHolder, httpServiceHolder );
      }
    }
  }

  private void launch( ServiceHolder<ApplicationConfiguration> configurationHolder,
                       ServiceHolder<HttpServiceRuntime> httpServiceHolder )
  {
    ApplicationConfiguration configuration = configurationHolder.getService();
    HttpServiceRuntime httpService = httpServiceHolder.getService();
    String contextName = getContextName( configurationHolder );
    String contextLocation = getLocation( contextName, configuration, httpService );
    try {
      launch( configuration, httpService, contextName, contextLocation );
    } catch( RuntimeException rte ) {
      logProblem( "Unable to start RWT application.", rte );
    }
  }

  private static String getContextName( ServiceHolder<ApplicationConfiguration> configurationHolder )
  {
    ServiceReference<ApplicationConfiguration> reference = configurationHolder.getReference();
    return ( String )reference.getProperty( PROPERTY_CONTEXT_NAME );
  }

  private void stopApplicationReferences( Object service ) {
    ArrayList<ApplicationReferenceImpl> allReferences = new ArrayList<>( applicationReferences );
    for( ApplicationReferenceImpl applicationReference : allReferences ) {
      if( applicationReference.belongsTo( service ) ) {
        stopApplicationReference( applicationReference );
      }
    }
  }

  private void stopAllApplicationReferences() {
    ArrayList<ApplicationReferenceImpl> allReferences = new ArrayList<>( applicationReferences );
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

  private static boolean matches( ServiceHolder<HttpServiceRuntime> httpServiceHolder,
                                  ServiceHolder<ApplicationConfiguration> configurationHolder )
  {
    ServiceReference<HttpServiceRuntime> httpServiceRef = httpServiceHolder.getReference();
    ServiceReference<ApplicationConfiguration> configurationRef = configurationHolder.getReference();
    return new Matcher( httpServiceRef, configurationRef ).matches();
  }

  private void logProblem( String failureMessage, Throwable failure ) {
    ServiceReference<?> logReference = bundleContext.getServiceReference( LogService.class.getName() );
    if( logReference != null ) {
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
                      HttpServiceRuntime service )
  {
    String pathToContext = getContextFileName( contextName, configuration, service );
    File dataFile = bundleContext.getDataFile( pathToContext );
    return dataFile.toString();
  }

  static String getContextFileName( String name,
                                    ApplicationConfiguration configuration,
                                    HttpServiceRuntime service )
  {
    return new StringBuilder()
      .append( name == null ? "rwtcontext" : name )
      .append( "_" )
      .append( configuration.hashCode() )
      .append( "_" )
      .append( service.hashCode() )
      .toString();
  }

}
