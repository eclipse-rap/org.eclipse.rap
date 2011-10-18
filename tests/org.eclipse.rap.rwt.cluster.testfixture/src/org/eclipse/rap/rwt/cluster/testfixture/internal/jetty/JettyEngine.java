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

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.rap.rwt.cluster.testfixture.internal.server.RWTStartup;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rwt.engine.RWTServlet;
import org.eclipse.rwt.lifecycle.IEntryPoint;


public class JettyEngine implements IServletEngine {

  private final Map<String,HttpSession> sessions;
  private final JettyController jettyController;
  
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
    this.jettyController = new JettyController( sessionManagerProvider, port );
    this.sessions = Collections.synchronizedMap( new HashMap<String,HttpSession>() );
  }
  
  public void start( Class<? extends IEntryPoint> entryPointClass ) throws Exception {
    addEntryPoint( entryPointClass );
    jettyController.start();
  }

  public void stop() throws Exception {
    stop( 0 );
  }
  
  public void stop( int timeout ) throws Exception {
    jettyController.stop( timeout );
  }

  public int getPort() {
    return jettyController.getPort();
  }
  
  public HttpSession[] getSessions() {
    Collection<HttpSession> values = sessions.values();
    return values.toArray( new HttpSession[ values.size() ] );
  }

  private void addEntryPoint( Class<? extends IEntryPoint> entryPointClass ) {
    ServletContextHandler context = jettyController.createServletContext( "/" );
    context.addServlet( new ServletHolder( new RWTServlet() ), IServletEngine.SERVLET_PATH );
    addServletContextFilter( context, new SessionTracker() );
    context.addEventListener( RWTStartup.createServletContextListener( entryPointClass ) );
  }

  private static void addServletContextFilter( ServletContextHandler context, Filter filter ) {
    FilterHolder filterHolder = new FilterHolder( filter );
    EnumSet<DispatcherType> dispatcherType = EnumSet.of( DispatcherType.REQUEST );
    context.addFilter( filterHolder, IServletEngine.SERVLET_PATH, dispatcherType );
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
