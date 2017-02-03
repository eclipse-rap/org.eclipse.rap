/*******************************************************************************
 * Copyright (c) 2011, 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.internal.jetty;

import java.sql.DriverManager;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DatabaseAdaptor;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.JDBCSessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;

class ClusteredSessionHandlerProvider implements ISessionHandlerProvider {

  private static AtomicInteger nodeCounter = new AtomicInteger();

  private final DatabaseServer databaseServer;

  ClusteredSessionHandlerProvider( DatabaseServer databaseServer ) {
    this.databaseServer = databaseServer;
  }

  @Override
  public SessionHandler createSessionHandler( Server server ) {
    SessionHandler handler = new SessionHandler();
    DefaultSessionCache sessionCache = new DefaultSessionCache( handler );
    JDBCSessionDataStore sessionDataStore = new JDBCSessionDataStore();
    sessionCache.setSessionDataStore( sessionDataStore );
    DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
    databaseAdaptor.setDriverInfo( databaseServer.getDriver(), databaseServer.getConnectionUrl() );
    sessionDataStore.setDatabaseAdaptor( databaseAdaptor );
    DefaultSessionIdManager sessionIdManager = new CleanJDBCSessionIdManager( server );
    sessionIdManager.setWorkerName( generateNodeName() );
    handler.setSessionCache( sessionCache );
    handler.setSessionIdManager( sessionIdManager );
    return handler;
  }

  private String generateNodeName() {
    int nodeId = nodeCounter.getAndIncrement();
    return "node" + nodeId;
  }

  private class CleanJDBCSessionIdManager extends DefaultSessionIdManager {

    public CleanJDBCSessionIdManager( Server server ) {
      super( server );
    }

    @Override
    public void doStop() throws Exception {
      super.doStop();
      DriverManager.deregisterDriver( databaseServer.getDriver() );
    }

  }

}
