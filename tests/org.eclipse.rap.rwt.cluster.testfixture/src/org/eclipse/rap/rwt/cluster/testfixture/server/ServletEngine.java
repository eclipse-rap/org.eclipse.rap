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
package org.eclipse.rap.rwt.cluster.testfixture.server;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.eclipse.rwt.internal.engine.RWTDelegate;
import org.eclipse.rwt.internal.engine.RWTServletContextListener;


public class ServletEngine implements IServletEngine {
  
  static {
    Log.setLog( new SilentLogger() );
  }

  private final Server server;
  private final ContextHandlerCollection contextHandlers;
  private final SessionManager sessionManager;

  public ServletEngine() {
    this( new SessionManagerProvider() );
  }
  
  ServletEngine( ISessionManagerProvider sessionManagerProvider ) {
    this.server = new Server( SocketUtil.getFreePort() );
    this.contextHandlers = new ContextHandlerCollection();
    this.server.setHandler( contextHandlers );
    this.sessionManager = createSessionManager( sessionManagerProvider );
  }

  public void start() throws Exception {
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

  public void addEntryPoint( Class entryPointClass ) {
    ServletContextHandler servletContext = createServletContext( "/" );
    servletContext.addServlet( new ServletHolder( new RWTDelegate() ), "/rap" );
    servletContext.addEventListener( new RWTServletContextListener() );
    servletContext.setInitParameter( "org.eclipse.rwt.entryPoints", entryPointClass.getName() );
  }

  public Map getSessions() {
    return ( ( AbstractSessionManager )sessionManager ).getSessionMap();
  }

  private SessionManager createSessionManager( ISessionManagerProvider sessionManagerProvider ) {
    SessionManager result = sessionManagerProvider.createSessionManager( server );
    SessionIdManager sessionIdManager = sessionManagerProvider.createSessionIdManager( server );
    result.setIdManager( sessionIdManager );
    server.setSessionIdManager( sessionIdManager );
    return result;
  }

  private ServletContextHandler createServletContext( String path ) {
    SessionHandler sessionHandler = new SessionHandler( sessionManager );
    sessionManager.setSessionHandler( sessionHandler );
    ServletContextHandler result = new ServletContextHandler( contextHandlers, path );
    result.setSessionHandler( sessionHandler );
    result.setBaseResource( createContextPath() );
    return result;
  }

  private static FileResource createContextPath() {
    String tempDir = System.getProperty( "java.io.tmpdir" );
    try {
      return new FileResource( new File( tempDir, "temp-context-root" ).toURI().toURL() );
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
          deleteDirectory( contextHandler.getBaseResource().getFile() );
        }
      }
    }
  }

  private static void deleteDirectory( File directory ) {
    if( directory.isDirectory() ) {
      File[] files = directory.listFiles();
      for( int i = 0; i < files.length; i++ ) {
        deleteDirectory( files[ i ] );
      }
    }
    directory.delete();
  }

  private static class SessionManagerProvider implements ISessionManagerProvider {

    public SessionManager createSessionManager( Server server ) {
      HashSessionManager result = new HashSessionManager();
      result.setUsingCookies( true );
      result.setMaxInactiveInterval( 30 * 60 );
      return result;
    }

    public SessionIdManager createSessionIdManager( Server server ) {
      return new HashSessionIdManager();
    }
  }
}
