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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DatabaseServer_Test {

  private DatabaseServer databaseServer;

  @Before
  public void setUp() throws Exception {
    databaseServer = new DatabaseServer();
  }

  @After
  public void tearDown() throws Exception {
    databaseServer.stop();
  }

  @Test
  public void testGetConnectionUrl() {
    databaseServer.start();

    String connectionUrl = databaseServer.getConnectionUrl();

    assertNotNull( connectionUrl );
  }

  @Test
  public void testGetConnectionUrlWithStoppedDatabaseServer() {
    try {
      databaseServer.getConnectionUrl();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testStart() throws Exception {
    databaseServer.start();
    String connectionUrl = databaseServer.getConnectionUrl();

    Connection connection = DriverManager.getConnection( connectionUrl );

    assertNotNull( connection );
  }

  @Test
  public void testStartAndQuery() throws Exception {
    databaseServer.start();
    Connection connection = DriverManager.getConnection( databaseServer.getConnectionUrl() );

    String query = "select * from INFORMATION_SCHEMA.CATALOGS";
    PreparedStatement statement = connection.prepareStatement( query );
    ResultSet resultSet = statement.executeQuery();

    assertTrue( resultSet.first() );
  }

  @Test
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

  @Test
  public void testStopOnStoppedServer() {
    try {
      databaseServer.stop();
    } catch( Exception notExpected ) {
    }
  }

  @Test
  public void testGetDriver() {
    Driver driver1 = databaseServer.getDriver();
    Driver driver2 = databaseServer.getDriver();
    assertSame( driver1, driver2 );
  }

}
