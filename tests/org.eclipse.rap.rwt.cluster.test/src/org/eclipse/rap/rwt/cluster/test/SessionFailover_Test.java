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
import org.eclipse.rap.rwt.cluster.testfixture.db.DatabaseServer;
import org.eclipse.rap.rwt.cluster.testfixture.server.ClusteredServletEngine;
import org.eclipse.swt.widgets.Display;



public class SessionFailover_Test extends TestCase {

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
    clickCenterButton( client, 1, 4 );
    // click center button four times on secondary
    client.changeServletEngine( secondary );
    clickCenterButton( client, 5, 8 );
    Map primarySessions = primary.getSessions();
    Map secondarySessions = secondary.getSessions();
    // number of sessions
    assertEquals( 1, primarySessions.size() );
    assertEquals( 1, secondarySessions.size() );
    // session id equality
    String primarySessionId = ( String )primarySessions.keySet().iterator().next();
    String secondarySessionId = ( String )secondarySessions.keySet().iterator().next();
    assertEquals( primarySessionId, secondarySessionId );
    assertTrue( client.getSessionId().startsWith( primarySessionId ) );
    // HttpSessions
    HttpSession primarySession = ( HttpSession )primarySessions.get( primarySessionId );
    HttpSession secondarySession = ( HttpSession )secondarySessions.get( secondarySessionId );
    assertNotNull( ClusterFixture.getSessionStore( primarySession ) );
    assertNotNull( ClusterFixture.getSessionStore( secondarySession ) );
    // Displays
    Display primaryDisplay = ClusterFixture.getSessionDisplay( primarySession );
    Display secondaryDisplay = ClusterFixture.getSessionDisplay( secondarySession );
    assertNotNull( primaryDisplay );
    assertNotNull( secondaryDisplay );
    assertNotSame( primaryDisplay, secondaryDisplay );
  }

  private static void clickCenterButton( RWTClient client, int start, int end ) throws IOException {
    for( int i = start; i <= end; i++ ) {
      Response response = client.sendWidgetSelectedRequest( "w5" );
      assertTrue( response.isValidJavascript() );
      String expectedLabelPart = "relocated " + i + "/1";
      assertTrue( "label update mismatch, missing part: '" + expectedLabelPart + "'",
                  response.getContent().contains( expectedLabelPart ) );
    }
  }
}
