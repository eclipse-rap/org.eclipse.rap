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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rwt.internal.lifecycle.UICallBackServiceHandler;
import org.eclipse.rwt.service.IServiceHandler;


@SuppressWarnings("restriction")
public class RWTClient {

  private IServletEngine servletEngine;
  private final Map<String,String> parameters;
  private String sessionId;
  private int requestCounter;

  public RWTClient( IServletEngine servletEngine ) {
    this.servletEngine = servletEngine;
    this.sessionId = "";
    this.requestCounter = -2;
    this.parameters = new HashMap<String,String>();
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
  
  public Response sendResourceRequest( String resourceLocation ) throws IOException {
    clearParameters();
    URL url = createUrl( resourceLocation );
    HttpURLConnection connection = createConnection( url, 0 );
    return new Response( connection );
  }
  
  public Response sendUICallBackRequest( int timeout ) throws IOException {
    clearParameters();
    addParameter( IServiceHandler.REQUEST_PARAM, UICallBackServiceHandler.HANDLER_ID );
    URL url = createUrl( IServletEngine.SERVLET_NAME );
    HttpURLConnection connection = createConnection( url, timeout );
    return new Response( connection );
  }
  
  Response sendRequest() throws IOException {
    if( requestCounter >= 0 ) {
      addParameter( "requestCounter", String.valueOf( requestCounter ) );
    }
    URL url = createUrl( IServletEngine.SERVLET_NAME );
    HttpURLConnection connection = createConnection( url, 0 );
    parseSessionId( connection );
    requestCounter++;
    return new Response( connection );
  }

  private URL createUrl( String path ) {
    int port = servletEngine.getPort();
    HttpUrlBuilder urlBuilder = new HttpUrlBuilder( "localhost", port, path );
    urlBuilder.addParameters( parameters );
    urlBuilder.setSessionId( sessionId );
    return urlBuilder.toUrl();
  }

  private HttpURLConnection createConnection( URL url, int timeout ) throws IOException {
    HttpURLConnection result = servletEngine.createConnection( url );
    result.setInstanceFollowRedirects( false );
    result.setAllowUserInteraction( false );
    result.setRequestMethod( "GET" );
    result.setConnectTimeout( timeout );
    result.setReadTimeout( timeout );
    result.connect();
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
