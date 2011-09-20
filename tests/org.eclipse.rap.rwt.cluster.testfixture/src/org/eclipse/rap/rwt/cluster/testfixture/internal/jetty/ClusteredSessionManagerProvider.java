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

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.JDBCSessionIdManager;
import org.eclipse.jetty.server.session.JDBCSessionManager;

class ClusteredSessionManagerProvider implements ISessionManagerProvider {
  private static final long SCAVENGE_INTERVAL = 60 * 60; // 1 hour
  private static final int SAVE_INTERVAL = 1; // 1 sec

  private static AtomicInteger nodeCounter = new AtomicInteger();

  private final DatabaseServer databaseServer;

  ClusteredSessionManagerProvider( DatabaseServer databaseServer ) {
    this.databaseServer = databaseServer;
  }

  public SessionManager createSessionManager( Server server ) {
    JDBCSessionManager result = new JDBCSessionManager();
    result.setSaveInterval( SAVE_INTERVAL );
    return result;
  }

  public SessionIdManager createSessionIdManager( Server server ) {
    JDBCSessionIdManager result = new JDBCSessionIdManager( server );
    result.setScavengeInterval( SCAVENGE_INTERVAL );
    result.setWorkerName( generateNodeName() );
    String driverClassName = databaseServer.getDriverClassName();
    String connectionUrl = databaseServer.getConnectionUrl();
    result.setDriverInfo( driverClassName, connectionUrl );
    return result;
  }

  private String generateNodeName() {
    int nodeId = nodeCounter.getAndIncrement();
    return "node" + nodeId;
  }
}