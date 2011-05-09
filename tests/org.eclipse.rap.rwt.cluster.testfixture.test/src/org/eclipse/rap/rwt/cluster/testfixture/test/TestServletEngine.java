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
package org.eclipse.rap.rwt.cluster.testfixture.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;

public class TestServletEngine implements IServletEngine {

  private final int port;
  private HttpURLConnection connection;
  private URL connectionUrl;

  public TestServletEngine() {
    this( -1 );
  }
  
  public TestServletEngine( int port ) {
    this.port = port;
  }

  public int getPort() {
    return port;
  }
  
  public URL getConnectionUrl() {
    return connectionUrl;
  }
  
  public void setConnection( HttpURLConnection connection ) {
    this.connection = connection;
  }
  
  public HttpURLConnection createConnection( URL url ) throws IOException {
    connectionUrl = url;
    return connection;
  }
}