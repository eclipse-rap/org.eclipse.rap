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
package org.eclipse.rap.rwt.cluster.testfixture.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.internal.uicallback.ServerPushServiceHandler;
import org.eclipse.swt.dnd.Transfer;


@SuppressWarnings("restriction")
public class RWTClient {
  // Keep "text" in sync with TextTransfer#TYPE_NAME
  private static final String TEXT_TRANSFER_DATA_TYPE
    = String.valueOf( Transfer.registerType( "text" ) );

  private IServletEngine servletEngine;
  private final IConnectionProvider connectionProvider;
  private final long startTime;
  private String sessionId;
  private int requestCounter;

  public RWTClient( IServletEngine servletEngine ) {
    this( servletEngine, new DefaultConnectionProvider() );
  }

  RWTClient( IServletEngine servletEngine, IConnectionProvider connectionProvider ) {
    this.servletEngine = servletEngine;
    this.connectionProvider = connectionProvider;
    startTime = System.currentTimeMillis();
    sessionId = "";
    requestCounter = -2;
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
    return sendRequest( "GET", new HashMap<String,String>() );
  }

  public Response sendInitializationRequest() throws IOException {
    Map<String,String> parameters = new HashMap<String,String>();
    parameters.put( "rwt_initialize", "true" );
    parameters.put( "uiRoot", "w1" );
    parameters.put( "w1.bounds.width", "800" );
    parameters.put( "w1.bounds.height", "600" );
    parameters.put( "w1.dpi.x", "96" );
    parameters.put( "w1.dpi.y", "96" );
    parameters.put( "w1.colorDepth", "32" );
    parameters.put( "w1.cursorLocation.x", "0" );
    parameters.put( "w1.cursorLocation.y", "0" );
    return sendPostRequest( parameters );
  }

  public Response sendDisplayResizeRequest( int width, int height ) throws IOException {
    Map<String, String> parameters = createDefaultParameters();
    parameters.put( "w1.bounds.width", String.valueOf( width ) );
    parameters.put( "w1.bounds.height", String.valueOf( height ) );
    return sendPostRequest( parameters );
  }

  public Response sendWidgetSelectedRequest( String widgetId ) throws IOException {
    Map<String, String> parameters = createDefaultParameters();
    parameters.put( "org.eclipse.swt.events.widgetSelected", widgetId );
    return sendPostRequest( parameters );
  }

  public Response sendShellCloseRequest( String shellId ) throws IOException {
    Map<String, String> parameters = createDefaultParameters();
    parameters.put( "org.eclipse.swt.widgets.Shell_close", shellId );
    return sendPostRequest( parameters );
  }

  public Response sendDragStartRequest( String widgetId ) throws IOException {
    Map<String, String> parameters = createDefaultParameters();
    parameters.put( "org.eclipse.swt.dnd.dragStart", widgetId );
    parameters.put( "org.eclipse.swt.dnd.dragStart.x", "100" );
    parameters.put( "org.eclipse.swt.dnd.dragStart.y", "100" );
    parameters.put( "org.eclipse.swt.dnd.dragStart.time", createTimeParam() );
    return sendPostRequest( parameters );
  }

  public Response sendDragFinishedRequest( String sourceWidgetId, String targetWidgetId )
    throws IOException
  {
    Map<String, String> parameters = createDefaultParameters();
    parameters.put( "org.eclipse.swt.dnd.dropAccept", targetWidgetId );
    parameters.put( "org.eclipse.swt.dnd.dropAccept.x", "100" );
    parameters.put( "org.eclipse.swt.dnd.dropAccept.y", "100" );
    parameters.put( "org.eclipse.swt.dnd.dropAccept.item", null );
    parameters.put( "org.eclipse.swt.dnd.dropAccept.operation", "move" );
    parameters.put( "org.eclipse.swt.dnd.dropAccept.feedback", "0" );
    parameters.put( "org.eclipse.swt.dnd.dropAccept.dataType", TEXT_TRANSFER_DATA_TYPE );
    parameters.put( "org.eclipse.swt.dnd.dropAccept.source", sourceWidgetId );
    parameters.put( "org.eclipse.swt.dnd.dropAccept.time", createTimeParam() );
    parameters.put( "org.eclipse.swt.dnd.dragFinished", sourceWidgetId );
    parameters.put( "org.eclipse.swt.dnd.dragFinished.x", "100" );
    parameters.put( "org.eclipse.swt.dnd.dragFinished.y", "100" );
    parameters.put( "org.eclipse.swt.dnd.dragFinished.time", createTimeParam() );
    return sendPostRequest( parameters );
  }

  public Response sendResourceRequest( String resourceLocation ) throws IOException {
    URL url = createUrl( resourceLocation, new HashMap<String,String>() );
    HttpURLConnection connection = createConnection( "GET", url, 0 );
    return new Response( connection );
  }

  public Response sendUICallBackRequest( int timeout ) throws IOException {
    Map<String,String> parameters = new HashMap<String,String>();
    parameters.put( ServiceManagerImpl.REQUEST_PARAM, ServerPushServiceHandler.HANDLER_ID );
    URL url = createUrl( IServletEngine.SERVLET_NAME, parameters );
    HttpURLConnection connection = createConnection( "GET", url, timeout );
    return new Response( connection );
  }

  Response sendPostRequest() throws IOException {
    return sendPostRequest( new HashMap<String,String>() );
  }

  Response sendPostRequest( Map<String, String> parameters ) throws IOException {
    return sendRequest( "POST", parameters );
  }

  Response sendRequest( String method, Map<String,String> parameters ) throws IOException {
    if( requestCounter >= 0 ) {
      parameters.put( "requestCounter", String.valueOf( requestCounter ) );
    }
    URL url = createUrl( IServletEngine.SERVLET_NAME, parameters );
    HttpURLConnection connection = createConnection( method, url, 0 );
    parseSessionId( connection );
    requestCounter++;
    return new Response( connection );
  }

  private URL createUrl( String path, Map<String,String> parameters ) {
    int port = servletEngine.getPort();
    HttpUrlBuilder urlBuilder = new HttpUrlBuilder( "localhost", port, path );
    urlBuilder.addParameters( parameters );
    urlBuilder.setSessionId( sessionId );
    return urlBuilder.toUrl();
  }

  private HttpURLConnection createConnection( String method, URL url, int timeout )
    throws IOException
  {
    HttpURLConnection result = ( HttpURLConnection )connectionProvider.createConnection( url );
    result.setInstanceFollowRedirects( false );
    result.setAllowUserInteraction( false );
    result.setRequestMethod( method );
    result.setConnectTimeout( timeout );
    result.setReadTimeout( timeout );
    result.connect();
    return result;
  }

  private Map<String, String> createDefaultParameters() {
    Map<String,String> result = new HashMap<String,String>();
    result.put( "uiRoot", "w1" );
    return result;
  }

  private String createTimeParam() {
    return String.valueOf( System.currentTimeMillis() - startTime );
  }

  private void parseSessionId( HttpURLConnection connection ) {
    String cookieField = connection.getHeaderField( "Set-Cookie" );
    if( cookieField != null ) {
      String[] parts = cookieField.split( ";" );
      sessionId = parts[ 0 ].split( "=" )[ 1 ];
    }
  }

  private static class DefaultConnectionProvider implements IConnectionProvider {
    public URLConnection createConnection( URL url ) throws IOException {
      return url.openConnection();
    }
  }
}
