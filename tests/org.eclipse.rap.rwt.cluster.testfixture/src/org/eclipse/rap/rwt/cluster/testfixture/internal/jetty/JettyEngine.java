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
package org.eclipse.rap.rwt.cluster.testfixture.internal.jetty;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.internal.server.RWTStartup;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.engine.RWTServlet;


public class JettyEngine implements IServletEngine {

  private final JettyController jettyController;
  private final SessionTracker sessionTracker;
  
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
    this.sessionTracker = new SessionTracker();
  }
  
  public void start( Class<? extends EntryPoint> entryPointClass ) throws Exception {
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
    return sessionTracker.getSessions();
  }

  private void addEntryPoint( Class<? extends EntryPoint> entryPointClass ) {
    ServletContextHandler context = jettyController.createServletContext( "/" );
    context.addServlet( new ServletHolder( new RWTServlet() ), IServletEngine.SERVLET_PATH );
    addServletContextFilter( context, sessionTracker );
    context.addEventListener( RWTStartup.createServletContextListener( entryPointClass ) );
  }

  private static void addServletContextFilter( ServletContextHandler context, Filter filter ) {
    FilterHolder filterHolder = new FilterHolder( filter );
    EnumSet<DispatcherType> dispatcherType = EnumSet.of( DispatcherType.REQUEST );
    context.addFilter( filterHolder, IServletEngine.SERVLET_PATH, dispatcherType );
  }
}
