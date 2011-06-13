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
package org.eclipse.rap.rwt.cluster.testfixture.internal.jetty;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.AbstractSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.rap.rwt.cluster.testfixture.internal.server.DelegatingServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.FileUtil;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.lifecycle.IEntryPoint;


@SuppressWarnings("restriction")
public class JettyEngine implements IServletEngine {
  private static final String SERVLET_NAME = "/rap";

  static {
    Log.setLog( new ServletEngineLogger() );
  }
  
  private final ISessionManagerProvider sessionManagerProvider;
  private final Server server;
  private final ContextHandlerCollection contextHandlers;
  private SessionManager sessionManager;
  
  public JettyEngine() {
    this( new SessionManagerProvider() );
  }
  
  JettyEngine( ISessionManagerProvider sessionManagerProvider ) {
    this( sessionManagerProvider, SocketUtil.getFreePort() );
  }
  
  JettyEngine( ISessionManagerProvider sessionManagerProvider, int port ) {
    this.sessionManagerProvider = sessionManagerProvider;
    this.server = new Server( port );
    this.contextHandlers = new ContextHandlerCollection();
    this.server.setHandler( contextHandlers );
  }
  
  public void start( Class<? extends IEntryPoint> entryPointClass ) throws Exception {
    createSessionManager();
    addEntryPoint( entryPointClass );
    server.start();
  }

  public void stop() throws Exception {
    server.stop();
    cleanUp();
  }

  public int getPort() {
    return server.getConnectors()[ 0 ].getLocalPort();
  }
  
  public HttpURLConnection createConnection( URL url ) throws IOException {
    return ( HttpURLConnection )url.openConnection();
  }

  @SuppressWarnings({ "deprecation", "unchecked" })
  public HttpSession[] getSessions() {
    Map sessionMap = ( ( AbstractSessionManager )sessionManager ).getSessionMap();
    Collection<HttpSession> sessions = sessionMap.values();
    return sessions.toArray( new HttpSession[ sessions.size() ] );
  }

  private void createSessionManager() {
    sessionManager = createSessionManager( sessionManagerProvider );
  }

  private void addEntryPoint( Class<? extends IEntryPoint> entryPointClass ) {
    ServletContextHandler context = createServletContext( "/" );
    context.addServlet( RWTDelegate.class.getName(), SERVLET_NAME );
    context.addFilter( RWTClusterSupport.class.getName(), SERVLET_NAME, FilterMapping.DEFAULT );
    context.addEventListener( new RWTServletContextListener() );
    context.setInitParameter( "org.eclipse.rwt.entryPoints", entryPointClass.getName() );
  }

  private SessionManager createSessionManager( ISessionManagerProvider sessionManagerProvider ) {
    SessionManager result = sessionManagerProvider.createSessionManager( server );
    SessionIdManager sessionIdManager = sessionManagerProvider.createSessionIdManager( server );
    result.setMaxInactiveInterval( 60 * 60 );
    result.setIdManager( sessionIdManager );
    server.setSessionIdManager( sessionIdManager );
    return result;
  }

  private ServletContextHandler createServletContext( String path ) {
    SessionHandler sessionHandler = new SessionHandler( sessionManager );
    sessionManager.setSessionHandler( sessionHandler );
    ServletContextHandler result = new ServletContextHandler( contextHandlers, path );
    result.setSessionHandler( sessionHandler );
    result.setBaseResource( createServletContextPath() );
    result.addServlet( DefaultServlet.class.getName(), "/" );
    return result;
  }

  private FileResource createServletContextPath() {
    File contextRoot = DelegatingServletEngine.getTempDir( this );
    try {
      return new FileResource( contextRoot.toURI().toURL() );
    } catch( Exception e ) {
      throw new RuntimeException( e );
    }
  }

  private void cleanUp() throws IOException {
    Handler[] handlers = contextHandlers.getHandlers();
    if( handlers != null ) {
      for( int i = 0; i < handlers.length; i++ ) {
        if( handlers[ i ] instanceof ServletContextHandler ) {
          ServletContextHandler contextHandler = ( ServletContextHandler )handlers[ i ];
          FileUtil.deleteDirectory( contextHandler.getBaseResource().getFile() );
        }
      }
    }
  }
}
