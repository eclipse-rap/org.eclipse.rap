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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.rap.rwt.cluster.testfixture.internal.server.DelegatingServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.internal.server.RWTStartup;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.FileUtil;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rwt.engine.RWTServlet;
import org.eclipse.rwt.lifecycle.IEntryPoint;


public class JettyEngine implements IServletEngine {

  static {
    Log.setLog( new ServletEngineLogger() );
  }
  
  private final ISessionManagerProvider sessionManagerProvider;
  private final Server server;
  private final Map<String,HttpSession> sessions;
  private SessionManager sessionManager;
  
  public JettyEngine() {
    this( new SessionManagerProvider() );
  }
  
  public JettyEngine( int port ) {
    this( new SessionManagerProvider(), port );
  }
  
  JettyEngine( ISessionManagerProvider sessionManagerProvider ) {
    this( sessionManagerProvider, SocketUtil.getFreePort() );
  }
  
  JettyEngine( ISessionManagerProvider sessionManagerProvider, int port ) {
    this.sessionManagerProvider = sessionManagerProvider;
    this.server = new Server( port );
    this.server.setGracefulShutdown( 2000 );
    this.server.setHandler( new ContextHandlerCollection() );
    this.sessions = Collections.synchronizedMap( new HashMap<String,HttpSession>() );
  }
  
  public void start( Class<? extends IEntryPoint> entryPointClass ) throws Exception {
    createSessionManager();
    addEntryPoint( entryPointClass );
    server.start();
  }

  public void stop() throws Exception {
    stop( 0 );
  }
  
  public void stop( int timeout ) throws Exception {
    server.setGracefulShutdown( timeout );
    server.stop();
    cleanUp();
  }

  public int getPort() {
    return server.getConnectors()[ 0 ].getLocalPort();
  }
  
  public HttpSession[] getSessions() {
    Collection<HttpSession> values = sessions.values();
    return values.toArray( new HttpSession[ values.size() ] );
  }

  private void createSessionManager() {
    sessionManager = createSessionManager( sessionManagerProvider );
  }
  
  private SessionManager createSessionManager( ISessionManagerProvider sessionManagerProvider ) {
    SessionManager result = sessionManagerProvider.createSessionManager( server );
    SessionIdManager sessionIdManager = sessionManagerProvider.createSessionIdManager( server );
    result.setMaxInactiveInterval( 60 * 60 );
    result.setSessionIdManager( sessionIdManager );
    server.setSessionIdManager( sessionIdManager );
    return result;
  }

  private void addEntryPoint( Class<? extends IEntryPoint> entryPointClass ) {
    ServletContextHandler context = createServletContext( "/" );
    context.addServlet( new ServletHolder( new RWTServlet() ), IServletEngine.SERVLET_PATH );
    addServletContextFilter( context, new SessionTracker() );
    context.addEventListener( RWTStartup.createServletContextListener( entryPointClass ) );
  }

  private static void addServletContextFilter( ServletContextHandler context, Filter filter ) {
    FilterHolder filterHolder = new FilterHolder( filter );
    EnumSet<DispatcherType> dispatcherType = EnumSet.of( DispatcherType.REQUEST );
    context.addFilter( filterHolder, IServletEngine.SERVLET_PATH, dispatcherType );
  }

  private ServletContextHandler createServletContext( String path ) {
    SessionHandler sessionHandler = new SessionHandler( sessionManager );
    sessionManager.setSessionHandler( sessionHandler );
    ServletContextHandler result = new ServletContextHandler( getHandlerContainer(), path );
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
    Handler[] handlers = getHandlerContainer().getHandlers();
    if( handlers != null ) {
      for( Handler handler : handlers ) {
        if( handler instanceof ServletContextHandler ) {
          ServletContextHandler contextHandler = ( ServletContextHandler )handler;
          FileUtil.deleteDirectory( contextHandler.getBaseResource().getFile() );
        }
      }
    }
  }
  
  private HandlerContainer getHandlerContainer() {
    return ( HandlerContainer )server.getHandler();
  }

  private class SessionTracker implements Filter {
    
    public void init( FilterConfig filterConfig ) throws ServletException {
    }

    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
      throws IOException, ServletException
    {
      chain.doFilter( request, response );
      trackSession( request );
    }

    public void destroy() {
    }

    private void trackSession( ServletRequest request ) {
      HttpSession session = getSession( request );
      if( session != null ) {
        sessions.put( session.getId(), session );
      }
    }

    private HttpSession getSession( ServletRequest request ) {
      HttpServletRequest httpRequest = ( HttpServletRequest )request;
      return httpRequest.getSession( false );
    }
  }
}
