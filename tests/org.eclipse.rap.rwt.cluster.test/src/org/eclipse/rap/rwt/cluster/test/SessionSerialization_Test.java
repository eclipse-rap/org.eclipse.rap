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
package org.eclipse.rap.rwt.cluster.test;

import java.io.IOException;
import java.net.HttpURLConnection;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.test.entrypoints.WidgetsEntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineCluster;
import org.eclipse.rap.rwt.cluster.testfixture.server.JettyFactory;


public class SessionSerialization_Test extends TestCase {

  private IServletEngineCluster cluster;
  private IServletEngine primary;
  private IServletEngine secondary;
  private RWTClient client;

  public void testWidgetsAreSerializable() throws Exception {
    Response response = sendRequestToPrimary();
    assertEquals( HttpURLConnection.HTTP_OK, response.getResponseCode() );
    assertTrue( response.isValidJsonResponse() );

    response = switchToSecondary();
    assertEquals( HttpURLConnection.HTTP_OK, response.getResponseCode() );
    assertTrue( response.isValidJsonResponse() );
  }

  @Override
  protected void setUp() throws Exception {
    cluster = new JettyFactory().createServletEngineCluster();
    primary = cluster.addServletEngine();
    secondary = cluster.addServletEngine();
    cluster.start( WidgetsEntryPoint.class );
    client = new RWTClient( primary );
  }

  @Override
  protected void tearDown() throws Exception {
    cluster.stop();
  }

  private Response sendRequestToPrimary() throws IOException {
    client.sendStartupRequest();
    client.sendInitializationRequest();
    return client.sendDisplayResizeRequest( 600, 800 );
  }

  private Response switchToSecondary() throws IOException {
    cluster.removeServletEngine( primary );
    client.changeServletEngine( secondary );
    return client.sendDisplayResizeRequest( 500, 700 );
  }

}
