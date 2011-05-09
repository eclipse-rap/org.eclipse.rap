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
package org.eclipse.rap.rwt.cluster.testfixture.client;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;


public class RWTClient {

  private IServletEngine servletEngine;
  private String sessionId;
  private int requestCounter;
  private final Map parameters;

  public RWTClient( IServletEngine servletEngine ) {
    this.servletEngine = servletEngine;
    this.sessionId = "";
    this.requestCounter = -2;
    this.parameters = new HashMap();
  }
  
  public void changeServletEngine( IServletEngine servletEngine ) {
    this.servletEngine = servletEngine;
  }

  public Object getServletEngine() {
    return servletEngine;
  }

  public String getSessionId() {
    return sessionId;
  }
  
  public Response sendStartupRequest() throws IOException {
    clearParameters();
    return sendStartupRequest( "default" );
  }
  
  public Response sendStartupRequest( String entryPoint ) throws IOException {
    clearParameters();
    addParameter( "startup", entryPoint );
    return sendRequest();
  }
  
  public Response sendInitializationRequest() throws IOException {
    clearParameters();
    addParameter( "rwt_initialize", "true" );
    addParameter( "startup", "default" );
    addParameter( "uiRoot", "w1" );
    addParameter( "w4t_width", "800" );
    addParameter( "w4t_height", "600" );
    addParameter( "w1.dpi.x", "96" );
    addParameter( "w1.dpi.y", "96" );
    addParameter( "w1.colorDepth", "32" );
    return sendRequest();
  }
  
  public Response sendDisplayResizeRequest( int width, int height ) throws IOException {
    clearParameters();
    addParameter( "uiRoot", "w1" );
    addParameter( "w1.bounds.width", String.valueOf( width ) );
    addParameter( "w1.bounds.height", String.valueOf( height ) );
    return sendRequest();
  }
  
  public Response sendWidgetSelectedRequest( String widgetId ) throws IOException {
    clearParameters();
    addParameter( "uiRoot", "w1" );
    addParameter( "org.eclipse.swt.events.widgetSelected", widgetId );
    return sendRequest();
  }
  
  Response sendRequest() throws IOException {
    URL url = createUrl();
    HttpURLConnection connection = createConnection( url );
    connection.connect();
    parseSessionId( connection );
    requestCounter++;
    return new Response( connection );
  }

  private URL createUrl() {
    // TODO [rh] replace hard-coded servet name (see also ServletEngine#addEntryPoint)
    HttpUrlBuilder urlBuilder = new HttpUrlBuilder( "localhost", servletEngine.getPort(), "rap" );
    urlBuilder.addParameters( parameters );
    if( requestCounter >= 0 ) {
      urlBuilder.addParameter( "requestCounter", String.valueOf( requestCounter ) );
    }
    urlBuilder.setSessionId( sessionId );
    return urlBuilder.toUrl();
  }

  private HttpURLConnection createConnection( URL url ) throws IOException {
    HttpURLConnection result = servletEngine.createConnection( url );
    result.setInstanceFollowRedirects( false );
    result.setAllowUserInteraction( false );
    result.setRequestMethod( "GET" );
    return result;
  }
  
  void addParameter( String name, String value ) {
    parameters.put( name, value );
  }
  
  
  private void clearParameters() {
    parameters.clear();
  }

  private void parseSessionId( HttpURLConnection connection ) {
    String cookieField = connection.getHeaderField( "Set-Cookie" );
    if( cookieField != null ) {
      String[] parts = cookieField.split( ";" );
      sessionId = parts[ 0 ].split( "=" )[ 1 ];
    }
  }
}
