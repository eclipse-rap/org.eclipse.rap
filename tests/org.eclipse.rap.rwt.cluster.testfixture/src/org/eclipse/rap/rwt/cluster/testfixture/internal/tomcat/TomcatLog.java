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
package org.eclipse.rap.rwt.cluster.testfixture.internal.tomcat;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


class TomcatLog {
  
  // Used to hold Logger instances in memory, otherwise they are GC'ed and configuration is lost
  private static final List<Logger> loggerHolder = new LinkedList<Logger>();

  static void silence() {
    configureLogger( "org.apache.catalina.core.StandardEngine" );
    configureLogger( "org.apache.catalina.ha.session.DeltaManager" );
    configureLogger( "org.apache.catalina.ha.tcp.SimpleTcpCluster" );
    configureLogger( "org.apache.catalina.ha.session.JvmRouteBinderValve" );
    configureLogger( "org.apache.catalina.realm.JAASRealm" );
    configureLogger( "org.apache.catalina.startup.DigesterFactory" );
    configureLogger( "org.apache.catalina.tribes.membership.McastService" );
    configureLogger( "org.apache.catalina.tribes.transport.ReceiverBase" );
    configureLogger( "org.apache.catalina.tribes.group.interceptors.TcpFailureDetector" );
    configureLogger( "org.apache.catalina.tribes.io.BufferPool" );
    configureLogger( "org.apache.coyote.http11.Http11Protocol" );
  }
  
  private static void configureLogger( String name ) {
    Logger logger = Logger.getLogger( name );
    loggerHolder.add( logger );
    logger.setLevel( Level.SEVERE );
  }
  
  private TomcatLog() {
    // prevent instantiation
  }
}
