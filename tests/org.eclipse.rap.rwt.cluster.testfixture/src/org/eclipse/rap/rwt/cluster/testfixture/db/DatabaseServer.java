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
package org.eclipse.rap.rwt.cluster.testfixture.db;

import java.sql.SQLException;

import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.h2.tools.Server;


public class DatabaseServer {

  private Server server;
  private int port;
  
  public DatabaseServer() {
    port = -1;
  }

  public void start() {
    port = SocketUtil.getFreePort();
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
    } catch( SQLException e ) {
      throw new RuntimeException( "Failed to start H2 database.", e );
    }
  }

  public void stop() {
    server.stop();
    port = -1;
  }

  public String getDriverClassName() {
    return "org.h2.Driver";
  }
  
  public String getConnectionUrl() {
    return "jdbc:h2:tcp://localhost:" + port + "/sessions";    
  }
}
