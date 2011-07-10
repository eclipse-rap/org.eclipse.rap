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
package org.eclipse.rap.rwt.cluster.test;

import java.net.HttpURLConnection;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.test.entrypoints.WidgetsEntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterFixture;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.internal.jetty.JettyCluster;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineCluster;



public class SessionSerialization_Test extends TestCase {

  private IServletEngineCluster cluster;
  private IServletEngine servletEngine;
  private RWTClient client;

  public void testIsSerializable() throws Exception {
    client.sendStartupRequest();
    client.sendInitializationRequest();
    Response response = client.sendDisplayResizeRequest( 600, 800 );
    
    assertEquals( HttpURLConnection.HTTP_OK, response.getResponseCode() );
    assertTrue( response.isValidJavascript() );
  }

  protected void setUp() throws Exception {
    ClusterFixture.setUp();
    cluster = new JettyCluster();
    servletEngine = cluster.addServletEngine();
    cluster.start( WidgetsEntryPoint.class );
    client = new RWTClient( servletEngine );
  }

  protected void tearDown() throws Exception {
    cluster.stop();
    ClusterFixture.tearDown();
  }
}
