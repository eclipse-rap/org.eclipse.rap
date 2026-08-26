/*******************************************************************************
 * Copyright (c) 2011, 2024 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.jstest.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.servlet.runtime.HttpServiceRuntime;
import org.osgi.service.servlet.runtime.HttpServiceRuntimeConstants;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;


public class HttpServiceTracker
  extends ServiceTracker<HttpServiceRuntime, HttpServiceRuntime>
{

  private final BundleContext context;

  // Registrations for the whiteboard-based services
  private ServiceRegistration<?> resourceRegistration;

  public HttpServiceTracker( BundleContext context ) {
    super( context, HttpServiceRuntime.class.getName(), null );
    this.context = context;
  }

  @Override
  public HttpServiceRuntime addingService( ServiceReference<HttpServiceRuntime> reference ) {
    HttpServiceRuntime runtime = super.addingService( reference );
    try {
      registerWhiteboardResources();
      printUrl( reference );
    } catch( Exception exception ) {
      throw new RuntimeException( "Failed to add http service runtime", exception );
    }
    return runtime;
  }

  @Override
  public void removedService( ServiceReference<HttpServiceRuntime> reference,
                              HttpServiceRuntime service )
  {
    unregisterWhiteboardResources();
  }


  private void registerWhiteboardResources() {
    Dictionary<String, Object> properties = new Hashtable<>();
    // Maps requests under /resources to files under /htdocs in the bundle
    properties.put( HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN,
                    "/resources/*" );
    properties.put( HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX,
                    "/htdocs" );
    // A resource service is registered as an arbitrary marker service.
    resourceRegistration =
      context.registerService( Object.class, new Object(), properties );
  }

  private void unregisterWhiteboardResources() {
    if( resourceRegistration != null ) {
      resourceRegistration.unregister();
      resourceRegistration = null;
    }
  }

  private void printUrl( ServiceReference<HttpServiceRuntime> reference ) {
    // The runtime advertises its endpoints via the standard property
    Object endpoints =
      reference.getProperty( HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT );
    System.out.println( "Open this URL to start the tests:" );
    if( endpoints instanceof String[] && ( ( String[] )endpoints ).length > 0 ) {
      String base = ( ( String[] )endpoints )[ 0 ];
      if( !base.endsWith( "/" ) ) {
        base = base + "/";
      }
      System.out.println( base + "index.html" );
    } else if( endpoints instanceof String ) {
      String base = ( String )endpoints;
      if( !base.endsWith( "/" ) ) {
        base = base + "/";
      }
      System.out.println( base + "index.html" );
    } else {
      System.out.println( "<http-service-endpoint>/index.html" );
    }
  }
}