/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.jstest.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;


public class HttpServiceTracker extends ServiceTracker<HttpService, HttpService> {

  public HttpServiceTracker( BundleContext context ) {
    super( context, HttpService.class.getName(), null );
  }

  @Override
  public HttpService addingService( ServiceReference<HttpService> reference ) {
    HttpService httpService = super.addingService( reference );
    try {
      register( httpService );
      String port = ( String )reference.getProperty( "http.port" );
      printUrl( port );
    } catch( Exception exception ) {
      throw new RuntimeException( "Failed to add http service", exception );
    }
    return httpService;
  }

  @Override
  public void removedService( ServiceReference<HttpService> reference, HttpService service ) {
    unregister( service );
  }

  private void register( HttpService httpService ) throws NamespaceException {
    httpService.registerResources( "/", "/htdocs", null );
  }

  private void unregister( HttpService service ) {
    service.unregister( "/" );
  }

  private void printUrl( String port ) {
    System.out.println( "Open this URL to start the tests:" );
    System.out.println( "http://localhost:" + port + "/index.html" );
  }
}
