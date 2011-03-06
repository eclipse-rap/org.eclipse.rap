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

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.http.registry.HttpContextExtensionService;
import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.internal.engine.RWTContextUtil;
import org.eclipse.rwt.internal.engine.RWTDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;


public class HttpServiceTracker extends ServiceTracker {
  public static final String DEFAULT_SERVLET = "rap";
  public static final String ID_HTTP_CONTEXT = "org.eclipse.rap.httpcontext";

  private final List servletAliases;
  private RWTContext rwtContext;
  private HttpContextExtensionService httpCtxExtService;

  public HttpServiceTracker( BundleContext context ) {
    super( context, HttpService.class.getName(), null );
    servletAliases = new LinkedList();
  }

  public Object addingService( ServiceReference reference ) {
    rwtContext = createAndInitializeRWTContext();
    HttpService httpService = getHttpService( reference );
    HttpContext httpContext = getHttpContext( reference );
    ensureDefaultAlias();
    registerServlets( httpService, httpContext );
    return httpService;
  }

  public void removedService( ServiceReference reference, Object service ) {
    HttpService httpService = ( HttpService )service;
    deregisterServlets( httpService );
    super.removedService( reference, service );
  }

  public void addServletAlias( String name ) {
    servletAliases.add( name );
  }

  public void open() {
    openWhenHttpContextServiceHasBeenStarted();
  }

  private void openWhenHttpContextServiceHasBeenStarted() {
    String service = HttpContextExtensionService.class.getName();
    ServiceTracker httpContextTracker = new ServiceTracker( context, service, null ) {
      public Object addingService( final ServiceReference reference ) {
        HttpContextExtensionService result = (HttpContextExtensionService)super.addingService( reference );
        httpCtxExtService = createHttpCtxExtService( reference );
        HttpServiceTracker.super.open();
        return result;
      }
      public void removedService( ServiceReference reference, Object service ) {
        httpCtxExtService = null;
        super.removedService( reference, service );
      }
    };
    httpContextTracker.open();
  }
  
  private void ensureDefaultAlias() {
    if( servletAliases.size() == 0 ) {
      servletAliases.add( DEFAULT_SERVLET );
    }
  }

  private void registerServlets( HttpService httpService, HttpContext httpContext ) {
    Iterator aliases = servletAliases.iterator();
    while( aliases.hasNext() ) {
      String alias = ( String )aliases.next();
      registerServlet( alias, httpService, httpContext );
    }
  }

  private void deregisterServlets( HttpService httpService ) {
    Iterator aliases = servletAliases.iterator();
    while( aliases.hasNext() ) {
      String alias = ( String )aliases.next();
      httpService.unregister( "/" + alias );
    }
  }

  private void registerServlet( String name, HttpService httpService, HttpContext httpContext ) {
    try {
      RWTDelegate handler = new RWTDelegate();
      httpService.registerServlet( "/" + name, handler, null, httpContext );
      ServletContext servletContext = handler.getServletContext();
      RWTContextUtil.registerRWTContext( servletContext, rwtContext );
    } catch( Exception e ) {
      String text = "Could not register servlet mapping ''{0}''.";
      Object[] param = new Object[] { name };
      String msg = MessageFormat.format( text, param );
      Status status = new Status( IStatus.ERROR,
                                  PlatformUI.PLUGIN_ID,
                                  IStatus.OK,
                                  msg,
                                  e );
      WorkbenchPlugin.getDefault().getLog().log( status );
    }
  }

  private HttpContext getHttpContext( ServiceReference httpServiceReference ) {
    return httpCtxExtService.getHttpContext( httpServiceReference, ID_HTTP_CONTEXT );
  }

  private RWTContext createAndInitializeRWTContext() {
    RWTContext result = RWTContextUtil.createRWTContext();
    RWTContextUtil.runWithInstance( result, new EngineConfigWrapper() );
    return result;
  }

  private HttpService getHttpService( ServiceReference reference ) {
    return ( HttpService )context.getService( reference );
  }

  private HttpContextExtensionService createHttpCtxExtService( ServiceReference reference ) {
    return ( HttpContextExtensionService )context.getService( reference );
  }
}
