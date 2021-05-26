/*******************************************************************************
 * Copyright (c) 2011, 2021 EclipseSource and others.
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
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.FileUtil;


class JettyController {

  static {
    Log.setLog( new ServletEngineLogger() );
  }

  private final ISessionHandlerProvider sessionHandlerProvider;
  private final int port;
  private Server server;
  private SessionHandler sessionHandler;

  JettyController( ISessionHandlerProvider sessionHandlerProvider, int port ) {
    this.sessionHandlerProvider = sessionHandlerProvider;
    this.port = port;
  }

  void start() throws Exception {
    ensureServer();
    server.start();
  }

  void stop( int timeout ) throws Exception {
    ensureServer();
    // Jetty 9 doesn't like 0 timeout
    server.setStopTimeout( Math.max( 10, timeout ) );
    server.stop();
    cleanUp();
  }

  ServletContextHandler createServletContext( String path ) {
    ensureServer();
    ServletContextHandler result = new ServletContextHandler( getHandlerContainer(), path );
    result.setSessionHandler( sessionHandler );
    result.setBaseResource( createServletContextPath() );
    result.addServlet( DefaultServlet.class, "/" );
    return result;
  }

  Server getServer() {
    ensureServer();
    return server;
  }

  int getPort() {
    ensureServer();
    return ( ( ServerConnector )server.getConnectors()[ 0 ] ).getLocalPort();
  }

  private void ensureServer() {
    if( server == null ) {
      createServer();
      createSessionHandler();
    }
  }

  private void createServer() {
    server = new Server( port );
    server.setStopTimeout( 2000 );
    server.setStopAtShutdown( true );
    server.setHandler( new ContextHandlerCollection() );
  }

  private void createSessionHandler() {
    try {
      sessionHandler = sessionHandlerProvider.createSessionHandler( server );
      server.setSessionIdManager( sessionHandler.getSessionIdManager() );
    } catch( Exception e ) {
      throw new RuntimeException( e );
    }
  }

  private PathResource createServletContextPath() {
    File contextRoot = FileUtil.getTempDir( this.toString() );
    try {
      return new PathResource( contextRoot.toURI().toURL() );
    } catch( Exception e ) {
      throw new RuntimeException( e );
    }
  }

  private void cleanUp() throws IOException {
    for( ServletContextHandler servletContextHandler : getServletContextHandlers() ) {
      FileUtil.deleteDirectory( servletContextHandler.getBaseResource().getFile() );
    }
  }

  private ServletContextHandler[] getServletContextHandlers() {
    Collection<ServletContextHandler> contextHandlers = new LinkedList<ServletContextHandler>();
    for( Handler handler : getHandlers() ) {
      if( handler instanceof ServletContextHandler ) {
        ServletContextHandler contextHandler = ( ServletContextHandler )handler;
        contextHandlers.add( contextHandler );
      }
    }
    return contextHandlers.toArray( new ServletContextHandler[ contextHandlers.size() ] );
  }

  private Handler[] getHandlers() {
    Handler[] result = getHandlerContainer().getHandlers();
    if( result != null ) {
      result = new Handler[ 0 ];
    }
    return result;
  }

  private HandlerContainer getHandlerContainer() {
    return ( HandlerContainer )server.getHandler();
  }

}
