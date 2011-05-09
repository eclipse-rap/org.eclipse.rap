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

import java.util.Map;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.test.entrypoints.ThreeButtonExample;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterFixture;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.db.DatabaseServer;
import org.eclipse.rap.rwt.cluster.testfixture.server.ClusteredServletEngine;



public class SessionFailoverTest extends TestCase {

  private ClusteredServletEngine primary;
  private ClusteredServletEngine secondary;
  private DatabaseServer db;

  protected void setUp() throws Exception {
    ClusterFixture.setUp();
    db = new DatabaseServer();
    db.start();
    primary = new ClusteredServletEngine( db );
    primary.addEntryPoint( ThreeButtonExample.class );
    secondary = new ClusteredServletEngine( db );
    secondary.addEntryPoint( ThreeButtonExample.class );
    primary.start();
    secondary.start();
  }

  protected void tearDown() throws Exception {
    primary.stop();
    secondary.stop();
    db.stop();
    ClusterFixture.tearDown();
  }

  public void testSessionFailoverBetweenButtonClicks() throws Exception {
    RWTClient client = new RWTClient( primary );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    // click center button four times on primary
    for( int i = 1; i < 5; i++ ) {
      Response clickResponse = client.sendWidgetSelectedRequest( "w5" );
      String expectedLabelPart = "relocated " + i + "/1";
      assertTrue( "label update mismatch missing part: '" + expectedLabelPart + "'",
                  clickResponse.getContent().contains( expectedLabelPart ) );
    }
    // click center button four times on secondary
    client.changeServletEngine( secondary );
    for( int i = 5; i < 9; i++ ) {
      Response request = client.sendWidgetSelectedRequest( "w5" );
      String clickResponse = request.getContent();
      String expectedLabelPart = "relocated " + i + "/1";
      assertTrue( "label update mismatch", clickResponse.contains( expectedLabelPart ) );
    }
    
    // session id
    Map primarySessions = primary.getSessions();
    Map secondarySessions = secondary.getSessions();
    assertTrue( primarySessions.containsKey( client.getSessionId() ) );
    assertTrue( secondarySessions.containsKey( client.getSessionId() ) );
    // HttpSession
    HttpSession primarySession = ( HttpSession )primarySessions.get( client.getSessionId() );
    HttpSession secondarySession = ( HttpSession )secondarySessions.get( client.getSessionId() );
    assertNotNull( ClusterFixture.getSessionStore( primarySession ) );
    assertNotNull( ClusterFixture.getSessionStore( secondarySession ) );
    // Display
    assertNotNull( ClusterFixture.getSessionDisplay( primarySession ) );
    assertNotNull( ClusterFixture.getSessionDisplay( primarySession ) );
    assertNotSame( ClusterFixture.getSessionDisplay( primarySession ), 
                   ClusterFixture.getSessionDisplay( primarySession ) );
  }
}
