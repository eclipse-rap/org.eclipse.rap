/*******************************************************************************
 * Copyright (c) 2008, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 *    EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.servlet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.http.registry.HttpContextExtensionService;
import org.eclipse.rap.rwt.osgi.ApplicationReference;
import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.*;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;


public class HttpServiceTracker extends ServiceTracker {
  
  public static final String DEFAULT_SERVLET = "rap";
  public static final String ID_HTTP_CONTEXT = "org.eclipse.rap.httpcontext";

  private HttpContextExtensionService httpCtxExtService;
  private ServiceTracker httpContextTracker;
  private ServiceTracker ApplicationLauncherTracker;
  private ApplicationLauncher applicationLauncher;
  private ApplicationReference applicationReference;

  private class HttpContextTracker extends ServiceTracker {

    private HttpContextTracker( BundleContext context ) {
      super( context, HttpContextExtensionService.class.getName(), null );
    }

    public Object addingService( ServiceReference reference ) {
      Object result = super.addingService( reference );
      httpCtxExtService = getHttpCtxExtService( reference );
      openAsSoonAsApplicationLauncherHasBeenStarted();
      return result;
    }

    public void removedService( ServiceReference reference, Object service ) {
      httpCtxExtService = null;
      super.removedService( reference, service );
    }
  }

  private class ApplicationLauncherTracker extends ServiceTracker {

    private ApplicationLauncherTracker( BundleContext context ) {
      super( context, ApplicationLauncher.class.getName(), null );
    }

    public Object addingService( ServiceReference reference ) {
      Object result = super.addingService( reference );
      applicationLauncher = ( ApplicationLauncher )context.getService( reference );
      HttpServiceTracker.super.open();
      return result;
    }

    public void removedService( ServiceReference reference, Object service ) {
      applicationLauncher = null;
      super.removedService( reference, service );
    }
  }

  
  public HttpServiceTracker( BundleContext context ) {
    super( context, HttpService.class.getName(), null );
  }

  public Object addingService( ServiceReference reference ) {
    HttpService result = getHttpService( reference );
    HttpContext httpContext = getHttpContext( reference );
    applicationReference = startApplication( reference, result, httpContext );
    return result;
  }

  public void removedService( ServiceReference reference, Object service ) {
    applicationReference.stopApplication();
    super.removedService( reference, service );
  }

  public void open() {
    openAsSoonAsHttpContextServiceHasBeenStarted();
  }

  private void openAsSoonAsHttpContextServiceHasBeenStarted() {
    httpContextTracker = new HttpContextTracker( context );
    httpContextTracker.open();
  }

  private void openAsSoonAsApplicationLauncherHasBeenStarted() {
    ApplicationLauncherTracker = new ApplicationLauncherTracker( context );
    ApplicationLauncherTracker.open();
  }
  
  public void close() {
    super.close();
    httpContextTracker.close();
    ApplicationLauncherTracker.close();
  }

  private ApplicationReference startApplication( ServiceReference httpServiceReference,
                                      HttpService service,
                                      HttpContext context )
  {
    ApplicationConfigurator configurator = newConfigurator( httpServiceReference );
    String contextDirectory = findContextPath().toString();
    return applicationLauncher.launch( configurator, service, context, null, contextDirectory );
  }

  private ApplicationConfigurator newConfigurator( ServiceReference httpServiceReference ) {
    return new WorkbenchApplicationConfigurator( httpServiceReference );
  }

  private static IPath findContextPath() {
    Bundle bundle = Platform.getBundle( PlatformUI.PLUGIN_ID );
    IPath stateLocation = Platform.getStateLocation( bundle );
    return stateLocation.append( "context" );
  }

  private HttpService getHttpService( ServiceReference reference ) {
    return ( HttpService )context.getService( reference );
  }

  private HttpContext getHttpContext( ServiceReference reference ) {
    return httpCtxExtService.getHttpContext( reference, ID_HTTP_CONTEXT );
  }

  private HttpContextExtensionService getHttpCtxExtService( ServiceReference reference ) {
    return ( HttpContextExtensionService )context.getService( reference );
  }
}