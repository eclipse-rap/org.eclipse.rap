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
  private Server server;
  
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
    if( server != null ) {
      server.stop();
      server = null;
    }
  }

  String getDriverClassName() {
    return org.h2.Driver.class.getName();
  }
  
  String getConnectionUrl() {
    String pattern = "jdbc:h2:tcp://localhost:{0}/mem:sessions;DB_CLOSE_DELAY=-1";
    int port = server == null ? -1 : server.getPort();
    Object[] args = new Object[] { String.valueOf( port ) };
    return MessageFormat.format( pattern, args );
  }
}
