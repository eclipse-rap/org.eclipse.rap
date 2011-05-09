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
import org.eclipse.rap.rwt.cluster.testfixture.*;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.db.DatabaseServer;
import org.eclipse.rap.rwt.cluster.testfixture.server.ClusteredServletEngine;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;



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
    // trigger startup of swt_layout demo...
    // initial request
    RWTClient client = new RWTClient( primary );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    // rwt_initialize
    int requestCounter = 0;
    // click center button four times on primary
    for( int i = 1; i < 5; i++ ) {
      // center label
//      String clickRequest = new RequestBuilder( primary ).sessionId( sessionId )
//        .requestCounter( requestCounter )
//        .widgetSelected( "w5" )
//        .build();
//      String clickResponse = TestUtils.getResponseBodyAsString( clickRequest );
      Response clickResponse = client.sendWidgetSelectedRequest( "w5" );
      // + "&w1.focusControl=w6&w2.activeControl=w6"
System.out.println( clickResponse );
      String expectedLabelPart = "relocated " + i + "/1";
      assertTrue( "label update mismatch missing part: '" + expectedLabelPart + "'",
                  clickResponse.getContent().contains( expectedLabelPart ) );
      requestCounter++ ;
    }
    // click center button four times on secondary
    client.changeServletEngine( secondary );
    for( int i = 5; i < 9; i++ ) {
      // center label
      Response request = client.sendWidgetSelectedRequest( "w5" );
      String clickResponse = request.getContent();
//      String clickRequest = new RequestBuilder( secondary ).sessionId( sessionId )
//        .requestCounter( requestCounter )
//        .widgetSelected( "w5" )
//        .build();
      // + "&w1.focusControl=w6&w2.activeControl=w6"
      String expectedLabelPart = "relocated " + i + "/1";
      assertTrue( "label update mismatch", clickResponse.contains( expectedLabelPart ) );
      requestCounter++ ;
    }
    
    // get HttpSessions from running jetties
    Map primarySessions = primary.getSessions();
    assertTrue( "session not found on primary",
                primarySessions.containsKey( client.getSessionId() ) );
    Map secondarySessions = secondary.getSessions();
    assertTrue( "session not found on secondary",
                secondarySessions.containsKey( client.getSessionId() ) );
    HttpSession primarySession = ( HttpSession )primarySessions.get( client.getSessionId() );
    HttpSession secondarySession = ( HttpSession )secondarySessions.get( client.getSessionId() );
    ISessionStore primarySessionStore = ClusterFixture.getSessionStore( primarySession );
    assertNotNull( "session store not found on primary", primarySessionStore );
    ISessionStore secondarySessionStore = ClusterFixture.getSessionStore( secondarySession );
    assertNotNull( "session store not found on secondary", secondarySessionStore );
    Display primaryDisplay = ClusterFixture.getSessionDisplay( primarySession );
    assertNotNull( "Display not found on primary", primaryDisplay );
    Display secondaryDisplay = ClusterFixture.getSessionDisplay( primarySession );
    assertNotNull( "Display not found on secondary", secondaryDisplay );
    assertNotSame( primaryDisplay, secondaryDisplay );
    // TODO [cluster] add more assertions
  }
}
