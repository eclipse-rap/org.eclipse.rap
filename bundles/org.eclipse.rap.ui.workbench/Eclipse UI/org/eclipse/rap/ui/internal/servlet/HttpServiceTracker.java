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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.http.registry.HttpContextExtensionService;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;


public class HttpServiceTracker extends ServiceTracker {

  private static final class HttpContextWrapper implements HttpContext {

    private final HttpContext context;

    private HttpContextWrapper( HttpContext context ) {
      Assert.isNotNull( context );
      this.context = context;
    }

    public String getMimeType( String name ) {
      return context.getMimeType( name );
    }

    public URL getResource( String name ) {
      URL result = null;
      try {
        // Preliminary fix for bug 268759
        // 268759: ResourceManager handles non-existing resources incorrectly
        File file = new File( name );
        if( file.exists() && !file.isDirectory() ) {
          result = new URL( "file", "", name );
        }
      } catch( MalformedURLException shouldNotHappen ) {
        throw new RuntimeException( shouldNotHappen );
      }
      return result;
    }

    public boolean handleSecurity( HttpServletRequest request, HttpServletResponse response ) {
      return true;
    }
  }

  public static final String DEFAULT_SERVLET = "rap";
  public static final String ID_HTTP_CONTEXT = "org.eclipse.rap.httpcontext";

  private final Map servletAliases;
  private final String resourceAlias;
  private HttpContextExtensionService httpCtxExtService;

  public HttpServiceTracker( BundleContext context ) {
    super( context, HttpService.class.getName(), null );
    servletAliases = new LinkedHashMap();
    resourceAlias = ResourceManagerImpl.RESOURCES;
  }

  public Object addingService( ServiceReference reference ) {
    HttpService result = getHttpService( reference );
    HttpContext httpContext = getHttpContext( reference, result );
    ApplicationContext applicationContext = createAndInitializeRWTContext();
    registerServlets( reference, result, httpContext, applicationContext );
    registerResourceDir( result, httpContext, applicationContext );
    return result;
  }

  public void removedService( ServiceReference reference, Object service ) {
    HttpService httpService = ( HttpService )service;
    deregisterResourceDir( httpService );
    deregisterServlets( reference, httpService );
    super.removedService( reference, service );
  }

  public void addServletAlias( String name, Filter filter ) {
    servletAliases.put( name, filter );
  }

  public void open() {
    openWhenHttpContextServiceHasBeenStarted();
  }

  private void openWhenHttpContextServiceHasBeenStarted() {
    String service = HttpContextExtensionService.class.getName();
    ServiceTracker httpContextTracker = new ServiceTracker( context, service, null ) {
      public Object addingService( ServiceReference reference ) {
        Object result = super.addingService( reference );
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

  private void registerServlets( ServiceReference reference,
                                 HttpService httpService,
                                 HttpContext httpContext,
                                 ApplicationContext rwtContext )
  {
    ensureDefaultAlias();
    Iterator aliases = servletAliases.keySet().iterator();
    while( aliases.hasNext() ) {
      String alias = ( String )aliases.next();
      Filter serviceFilter = ( Filter )servletAliases.get( alias );
      if( serviceFilter == null || serviceFilter.match( reference ) ) {
        registerServlet( alias, httpService, httpContext, rwtContext );
      }
    }
  }

  private void deregisterServlets( ServiceReference reference, HttpService httpService ) {
    Iterator aliases = servletAliases.keySet().iterator();
    while( aliases.hasNext() ) {
      String alias = ( String )aliases.next();
      Filter serviceFilter = ( Filter )servletAliases.get( alias );
      if( serviceFilter == null || serviceFilter.match( reference ) ) {
        deregisterAlias( alias, httpService );
      }
    }
  }

  private void registerServlet( String name,
                                HttpService httpService,
                                HttpContext httpContext,
                                ApplicationContext rwtContext )
  {
    try {
      RWTDelegate handler = new RWTDelegate();
      httpService.registerServlet( "/" + name, handler, null, httpContext );
      ServletContext servletContext = handler.getServletContext();
      ApplicationContextUtil.registerApplicationContext( servletContext, rwtContext );
    } catch( Exception exception ) {
      logError( "Failed to register servlet " + name, exception );
    }
  }

  private void registerResourceDir( HttpService httpService,
                                    HttpContext httpContext,
                                    ApplicationContext rwtContext )
  {
    if( httpService != null ) {
      String contextRoot = getContextRoot( rwtContext );
      String location = contextRoot + "/" + ResourceManagerImpl.RESOURCES;
      try {
        httpService.registerResources( "/" + resourceAlias, location, httpContext );
      } catch( Exception exception ) {
        logError( "Failed to register resource alias", exception );
      }
    }
  }

  private void deregisterResourceDir( HttpService httpService ) {
    deregisterAlias( resourceAlias, httpService );
  }

  private void deregisterAlias( String alias, HttpService httpService ) {
    try {
      httpService.unregister( "/" + alias );
    } catch( Exception exception ) {
      logError( "Failed to unregister servlet " + alias, exception );
    }
  }

  private void ensureDefaultAlias() {
    if( servletAliases.size() == 0 ) {
      servletAliases.put( DEFAULT_SERVLET, null );
    }
  }

  private HttpService getHttpService( ServiceReference reference ) {
    return ( HttpService )context.getService( reference );
  }

  private HttpContext getHttpContext( ServiceReference reference, HttpService httpService ) {
    HttpContext httpContext = httpCtxExtService.getHttpContext( reference, ID_HTTP_CONTEXT );
    if( httpContext == null ) {
      httpContext = httpService.createDefaultHttpContext();
    }
    return new HttpContextWrapper( httpContext );
  }

  private HttpContextExtensionService createHttpCtxExtService( ServiceReference reference ) {
    return ( HttpContextExtensionService )context.getService( reference );
  }

  private static ApplicationContext createAndInitializeRWTContext() {
    ApplicationContext result = ApplicationContextUtil.createApplicationContext();
    result.addConfigurable( new Configurable() {

      public void configure( ApplicationContext context ) {
        new EngineConfigWrapper( context );
      }

      public void reset( ApplicationContext context ) {
      }
      
    } );
    result.activate();
    return result;
  }

  private static String getContextRoot( ApplicationContext rwtContext ) {
    final String[] result = new String[ 1 ];
    ApplicationContextUtil.runWithInstance( rwtContext, new Runnable() {
      public void run() {
        IEngineConfig engineConfig = RWTFactory.getConfigurationReader().getEngineConfig();
		String contextRoot = engineConfig.getServerContextDir().toString();
        result[ 0 ] = ( new Path( contextRoot ) ).toString();
      }
    } );
    return result[ 0 ];
  }

  private static void logError( String message, Exception exception ) {
    Status status = new Status( IStatus.ERROR,
                                PlatformUI.PLUGIN_ID,
                                IStatus.OK,
                                message,
                                exception );
    WorkbenchPlugin.getDefault().getLog().log( status );
  }
}
