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
import org.eclipse.rap.rwt.osgi.RWTContext;
import org.eclipse.rap.rwt.osgi.RWTService;
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
  private ServiceTracker rwtServiceTracker;
  private RWTService rwtService;
  private RWTContext rwtContext;

  private class HttpContextTracker extends ServiceTracker {

    private HttpContextTracker( BundleContext context ) {
      super( context, HttpContextExtensionService.class.getName(), null );
    }

    public Object addingService( ServiceReference reference ) {
      Object result = super.addingService( reference );
      httpCtxExtService = getHttpCtxExtService( reference );
      openAsSoonAsRWTServiceHasBeenStarted();
      return result;
    }

    public void removedService( ServiceReference reference, Object service ) {
      httpCtxExtService = null;
      super.removedService( reference, service );
    }
  }

  private class RWTServiceTracker extends ServiceTracker {

    private RWTServiceTracker( BundleContext context ) {
      super( context, RWTService.class.getName(), null );
    }

    public Object addingService( ServiceReference reference ) {
      Object result = super.addingService( reference );
      rwtService = ( RWTService )context.getService( reference );
      HttpServiceTracker.super.open();
      return result;
    }

    public void removedService( ServiceReference reference, Object service ) {
      rwtService = null;
      super.removedService( reference, service );
    }
  }

  
  public HttpServiceTracker( BundleContext context ) {
    super( context, HttpService.class.getName(), null );
  }

  public Object addingService( ServiceReference reference ) {
    HttpService result = getHttpService( reference );
    HttpContext httpContext = getHttpContext( reference );
    rwtContext = startRWTContext( reference, result, httpContext );
    return result;
  }

  public void removedService( ServiceReference reference, Object service ) {
    rwtContext.stop();
    super.removedService( reference, service );
  }

  public void open() {
    openAsSoonAsHttpContextServiceHasBeenStarted();
  }

  private void openAsSoonAsHttpContextServiceHasBeenStarted() {
    httpContextTracker = new HttpContextTracker( context );
    httpContextTracker.open();
  }

  private void openAsSoonAsRWTServiceHasBeenStarted() {
    rwtServiceTracker = new RWTServiceTracker( context );
    rwtServiceTracker.open();
  }
  
  public void close() {
    super.close();
    httpContextTracker.close();
    rwtServiceTracker.close();
  }

  private RWTContext startRWTContext( ServiceReference httpServiceReference,
                                      HttpService service,
                                      HttpContext context )
  {
    RWTConfigurator rwtConfigurator = new RWTConfigurator( httpServiceReference );
    String contextDirectory = findContextPath().toString();
    return rwtService.start( rwtConfigurator, service, context, null, contextDirectory );
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