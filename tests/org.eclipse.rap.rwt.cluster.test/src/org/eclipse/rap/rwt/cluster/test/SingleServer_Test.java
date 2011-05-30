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

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.test.entrypoints.ThreeButtonExample;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterFixture;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.ServletEngine;



public class SingleServer_Test extends TestCase {

  private IServletEngine servletEngine;
  private RWTClient client;
  
  protected void setUp() throws Exception {
    ClusterFixture.setUp();
    servletEngine = new ServletEngine();
    servletEngine.start( ThreeButtonExample.class );
    client = new RWTClient( servletEngine );
  }
  
  protected void tearDown() throws Exception {
    servletEngine.stop();
    ClusterFixture.tearDown();
  }
  
  public void testStartupSequence() throws IOException {
    Response startupPage = client.sendStartupRequest();
    assertTrue( startupPage.isValidStartupPage() );
    assertTrue( client.getSessionId().length() > 0 );

    Response initialJavascript = client.sendInitializationRequest();
    assertTrue( initialJavascript.isValidJavascript() );
    
    Response subsequentRequest = client.sendWidgetSelectedRequest( "w5" );
    assertTrue( subsequentRequest.isValidJavascript() );
    
    Map sessions = servletEngine.getSessions();
    assertEquals( 1, sessions.size() );
    HttpSession httpSession = ( HttpSession )sessions.values().iterator().next();
    assertNotNull( ClusterFixture.getSessionDisplay( httpSession ) );
  }
}

