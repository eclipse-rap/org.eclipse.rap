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
package org.eclipse.rap.rwt.cluster.testfixture.internal.tomcat;

import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Engine;
import org.apache.catalina.Session;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Tomcat;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.internal.server.RWTStartup;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.FileUtil;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.engine.RWTServlet;


public class TomcatEngine implements IServletEngine {

  static {
    TomcatLog.silence();
  }

  private final Tomcat tomcat;
  private final StandardContext context;

  public TomcatEngine() {
    this( SocketUtil.getFreePort() );
  }

  public TomcatEngine( int port ) {
    tomcat = new Tomcat();
    configureTomcat( port );
    context = ( StandardContext )tomcat.addContext( "/", tomcat.getHost().getAppBase() );
  }

  private void configureTomcat( int port ) {
    tomcat.setSilent( true );
    tomcat.setPort( port );
    tomcat.setBaseDir( getBaseDir().getAbsolutePath() );
    tomcat.getHost().setAppBase( getWebAppsDir().getAbsolutePath() );
    // Seems that this must be unique among all embedded Tomcats
    tomcat.getEngine().setName( "Tomcat on port " + port );
  }

  public void start( Class<? extends EntryPoint> entryPointClass ) throws Exception {
    prepareWebAppsDir();
    configureContext( entryPointClass );
    tomcat.start();
    configureSessionSweepInterval();
    configureSessionPersistence();
  }

  public void stop() throws Exception {
    stop( 0 );
  }

  public void stop( int timeout ) throws Exception {
    tomcat.getEngine().setCluster( null );
    tomcat.stop();
    tomcat.destroy();
    FileUtil.deleteDirectory( getBaseDir() );
  }

  public int getPort() {
    return tomcat.getConnector().getPort();
  }

  public HttpSession[] getSessions() {
    Session[] sessions = context.getManager().findSessions();
    HttpSession[] result = new HttpSession[ sessions.length ];
    for( int i = 0; i < sessions.length; i++ ) {
      result[ i ] = sessions[ i ].getSession();
    }
    return result;
  }

  Engine getEngine() {
    return tomcat.getEngine();
  }

  private boolean prepareWebAppsDir() {
    return new File( tomcat.getHost().getAppBase() ).mkdirs();
  }

  private void configureContext( Class<? extends EntryPoint> entryPointClass ) {
    if( tomcat.getEngine().getCluster() != null ) {
      context.setDistributable( true );
    }
    context.setSessionTimeout( -1 );
    context.setBackgroundProcessorDelay( 1 );
    Object listener = RWTStartup.createServletContextListener( entryPointClass );
    context.addApplicationLifecycleListener( listener );
    Wrapper rwtServlet = addServlet( "rwtServlet", new RWTServlet() );
    context.addServletMapping( IServletEngine.SERVLET_PATH, rwtServlet.getName() );
    Wrapper defaultServlet = addServlet( "defaultServlet", new DefaultServlet() );
    context.addServletMapping( "/", defaultServlet.getName() );
  }

  private void configureSessionSweepInterval() {
    ManagerBase manager = ( ManagerBase )context.getManager();
    manager.setProcessExpiresFrequency( 1 );
  }

  private void configureSessionPersistence() {
    if( context.getManager() instanceof StandardManager ) {
      StandardManager standardManager = ( StandardManager )context.getManager();
      standardManager.setPathname( null );
    }
  }

  private Wrapper addServlet( String name, HttpServlet servlet ) {
    return Tomcat.addServlet( context, name, servlet );
  }

  private File getBaseDir() {
    return FileUtil.getTempDir( toString() );
  }

  private File getWebAppsDir() {
    return new File( getBaseDir(), "webapps" );
  }
}
