/*******************************************************************************
 * Copyright (c) 2008, 2012 EclipseSource and others.
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


public class HttpServiceTracker extends ServiceTracker<HttpService, HttpService> {

  public static final String ID_HTTP_CONTEXT = "org.eclipse.rap.httpcontext";

  private HttpContextExtensionService httpCtxExtService;
  private HttpContextTracker httpContextTracker;
  private ApplicationLauncherTracker applicationLauncherTracker;
  private ApplicationLauncher applicationLauncher;
  private ApplicationReference applicationReference;

  public HttpServiceTracker( BundleContext context ) {
    super( context, HttpService.class.getName(), null );
  }

  @Override
  public HttpService addingService( ServiceReference<HttpService> reference ) {
    HttpService result = context.getService( reference );
    HttpContext httpContext = httpCtxExtService.getHttpContext( reference, ID_HTTP_CONTEXT );
    applicationReference = startApplication( reference, result, httpContext );
    return result;
  }

  @Override
  public void removedService( ServiceReference<HttpService> reference, HttpService service ) {
    applicationReference.stopApplication();
    super.removedService( reference, service );
  }

  @Override
  public void open() {
    httpContextTracker = new HttpContextTracker( context );
    httpContextTracker.open();
  }

  @Override
  public void close() {
    super.close();
    httpContextTracker.close();
  }

  private ApplicationReference startApplication( ServiceReference<HttpService> httpServiceReference,
                                                 HttpService service,
                                                 HttpContext context )
  {
    ApplicationConfigurator configurator
      = new WorkbenchApplicationConfigurator( httpServiceReference );
    String contextDirectory = findContextPath().toString();
    return applicationLauncher.launch( configurator, service, context, null, contextDirectory );
  }

  private static IPath findContextPath() {
    Bundle bundle = Platform.getBundle( PlatformUI.PLUGIN_ID );
    IPath stateLocation = Platform.getStateLocation( bundle );
    return stateLocation.append( "context" );
  }

  private class HttpContextTracker
    extends ServiceTracker<HttpContextExtensionService, HttpContextExtensionService>
  {

    private HttpContextTracker( BundleContext context ) {
      super( context, HttpContextExtensionService.class.getName(), null );
    }

    @Override
    public HttpContextExtensionService
      addingService( ServiceReference<HttpContextExtensionService> reference )
    {
      HttpContextExtensionService result = super.addingService( reference );
      httpCtxExtService = context.getService( reference );
      applicationLauncherTracker = new ApplicationLauncherTracker( context );
      applicationLauncherTracker.open();
      return result;
    }

    @Override
    public void removedService( ServiceReference<HttpContextExtensionService> reference,
                                HttpContextExtensionService service )
    {
      applicationLauncherTracker.close();
      httpCtxExtService = null;
      super.removedService( reference, service );
    }
  }

  private class ApplicationLauncherTracker
    extends ServiceTracker<ApplicationLauncher, ApplicationLauncher>
  {

    private ApplicationLauncherTracker( BundleContext context ) {
      super( context, ApplicationLauncher.class.getName(), null );
    }

    @Override
    public ApplicationLauncher addingService( ServiceReference<ApplicationLauncher> reference ) {
      ApplicationLauncher result = super.addingService( reference );
      applicationLauncher = context.getService( reference );
      HttpServiceTracker.super.open();
      return result;
    }

    @Override
    public void removedService( ServiceReference<ApplicationLauncher> reference,
                                ApplicationLauncher service )
    {
      applicationLauncher = null;
      super.removedService( reference, service );
    }
  }
}
