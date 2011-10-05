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

import java.sql.SQLException;
import java.text.MessageFormat;

import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.h2.tools.Server;


class DatabaseServer {
  private static final String CONNECTION_URL_PATTERN
    = "jdbc:h2:tcp://localhost:{0}/mem:sessions;DB_CLOSE_DELAY=-1";

  private final org.h2.Driver driver;
  private Server server;
  
  DatabaseServer() {
    driver = new org.h2.Driver();
  }

  void start() {
    int port = SocketUtil.getFreePort();
    try {
      String[] args = new String[] {
        "-tcp", 
        "-tcpAllowOthers", 
        "true", 
        "-tcpPort", 
        String.valueOf( port )
      };
      server = Server.createTcpServer( args );
      server.start();
    } catch( SQLException sqle ) {
      throw new RuntimeException( "Failed to start H2 database.", sqle );
    }
  }

  void stop() {
    if( isRunning() ) {
      server.stop();
      server = null;
    }
  }

  java.sql.Driver getDriver() {
    return driver;
  }
  
  String getConnectionUrl() {
    checkRunning();
    String pattern = CONNECTION_URL_PATTERN;
    return MessageFormat.format( pattern, String.valueOf( server.getPort() ) );
  }

  private void checkRunning() {
    if( !isRunning() ) {
      throw new IllegalStateException( "Database server is not running." );
    }
  }

  private boolean isRunning() {
    return server != null;
  }
}
