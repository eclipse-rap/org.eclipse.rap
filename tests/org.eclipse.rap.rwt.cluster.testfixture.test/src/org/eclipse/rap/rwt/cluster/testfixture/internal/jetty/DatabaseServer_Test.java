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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.TestCase;


public class DatabaseServer_Test extends TestCase {

  private DatabaseServer databaseServer;

  public void testGetConnectionUrl() {
    databaseServer.start();
    
    String connectionUrl = databaseServer.getConnectionUrl();
    
    assertNotNull( connectionUrl );
  }
  
  public void testGetConnectionUrlWithStoppedDatabaseServer() {
    try {
      databaseServer.getConnectionUrl();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testStart() throws Exception {
    databaseServer.start();
    String connectionUrl = databaseServer.getConnectionUrl();
    
    Connection connection = DriverManager.getConnection( connectionUrl );
    
    assertNotNull( connection );
  }
  
  public void testStartAndQuery() throws Exception {
    databaseServer.start();
    Connection connection = DriverManager.getConnection( databaseServer.getConnectionUrl() );
    
    String query = "select * from INFORMATION_SCHEMA.CATALOGS";
    PreparedStatement statement = connection.prepareStatement( query );
    ResultSet resultSet = statement.executeQuery();

    assertTrue( resultSet.first() );
  }
  
  public void testStop() throws Exception {
    databaseServer.start();
    String connectionUrl = databaseServer.getConnectionUrl();
    Connection connection = DriverManager.getConnection( connectionUrl );
    connection.close();
    
    databaseServer.stop();
    
    try {
      DriverManager.getConnection( connectionUrl );
      fail();
    } catch( SQLException expected ) {
    }
  }
  
  public void testStopOnStoppedServer() {
    try {
      databaseServer.stop();
    } catch( Exception notExpected ) {
    }
  }
  
  protected void setUp() throws Exception {
    databaseServer = new DatabaseServer();
    Class.forName( databaseServer.getDriverClassName() );
  }
  
  protected void tearDown() throws Exception {
    databaseServer.stop();
  }
}
