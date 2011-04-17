/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.q07.jstest.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin implements BundleActivator {

  private static final String PORT_PROPERTY_NAME = "org.osgi.service.http.port";
  private static final String PATH_TO_TESTS = "/org.eclipse.rap.rwt.q07.jstest/index.html";
  private static final String ADDRESS = "127.0.0.1";
  private static final String PROTOCOL = "http";

  public void start( BundleContext context ) throws Exception {
    super.start( context );
    try {
      handlePort();
    } catch( Exception e ) {
      System.err.println( "An exception occured: " + e.getMessage() );
    }
  }

  private void handlePort() throws MalformedURLException {
    String httpPortProperty = System.getProperty( PORT_PROPERTY_NAME );
    if( httpPortProperty != null ) {
      printUrlInformation( httpPortProperty );
    } else {
      System.err.println( "No org.osgi.service.http.port property defined" );
    }
  }

  private void printUrlInformation( String httpPortProperty ) throws MalformedURLException {
    int port = Integer.parseInt( httpPortProperty );
    URL url = new URL( PROTOCOL, ADDRESS, port, PATH_TO_TESTS );
    System.out.println( "Use the URL below to start the tests:" );
    System.out.println( url );
  }
  
}
