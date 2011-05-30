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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.test.entrypoints.FontEntryPoint;
import org.eclipse.rap.rwt.cluster.test.entrypoints.ThreeButtonExample;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterFixture;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.db.DatabaseServer;
import org.eclipse.rap.rwt.cluster.testfixture.server.ClusteredServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.engine.ApplicationContextUtil;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class SessionFailover_Test extends TestCase {

  private DatabaseServer db;
  private IServletEngine primary;
  private IServletEngine secondary;
  private RWTClient client;

  public void testThreeButonExample() throws Exception {
    primary.start( ThreeButtonExample.class );
    secondary.start( ThreeButtonExample.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    // click center button four times on primary
    clickCenterButton( 1, 4 );
    // click center button four times on secondary
    client.changeServletEngine( secondary );
    clickCenterButton( 5, 8 );
    Map primarySessions = primary.getSessions();
    Map secondarySessions = secondary.getSessions();
    // number of sessions
    assertEquals( 1, primarySessions.size() );
    assertEquals( 1, secondarySessions.size() );
    // HttpSessions
    HttpSession primarySession = ClusterFixture.getFirstSession( primary );
    assertSessionIsIntact( primarySession, client );
    HttpSession secondarySession = ClusterFixture.getFirstSession( secondary );
    assertSessionIsIntact( secondarySession, client );
    assertEquals( primarySession.getId(), secondarySession.getId() );
    // Displays
    Display primaryDisplay = ClusterFixture.getSessionDisplay( primarySession );
    Display secondaryDisplay = ClusterFixture.getSessionDisplay( secondarySession );
    assertNotSame( primaryDisplay, secondaryDisplay );
  }

  public void testFontEntryPoint() throws Exception {
    primary.start( FontEntryPoint.class );
    secondary.start( FontEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    
    client.changeServletEngine( secondary );
    client.sendDisplayResizeRequest( 400, 600 );

    prepareExamination();
    Shell primaryShell = getFirstShell( primary );
    Shell secondaryShell = getFirstShell( secondary );
    Font primaryFont = primaryShell.getFont();
    Font secondaryFont = secondaryShell.getFont();
    assertEquals( primaryFont, secondaryFont );
    assertNotSame( primaryFont, secondaryFont );
    assertSame( primaryShell.getDisplay(), primaryFont.getDevice() );
    assertSame( secondaryShell.getDisplay(), secondaryFont.getDevice() );
  }

  protected void setUp() throws Exception {
    ClusterFixture.setUp();
    db = new DatabaseServer();
    db.start();
    primary = new ClusteredServletEngine( db );
    secondary = new ClusteredServletEngine( db );
    client = new RWTClient( primary );
  }

  protected void tearDown() throws Exception {
    primary.stop();
    secondary.stop();
    db.stop();
    ClusterFixture.tearDown();
  }
  
  private void prepareExamination() {
    attachCurrentThreadToDisplay( primary );
    attachCurrentThreadToDisplay( secondary );
  }

  private static void attachCurrentThreadToDisplay( IServletEngine servletEngine ) {
    HttpSession session = ClusterFixture.getFirstSession( servletEngine );
    Display display = ClusterFixture.getSessionDisplay( session );
    getDisplayAdapter( display ).attachThread();
  }

  private static void assertSessionIsIntact( HttpSession session, RWTClient client ) {
    assertTrue( client.getSessionId().startsWith( session.getId() ) );
    ISessionStore sessionStore = ClusterFixture.getSessionStore( session );
    Display display = ClusterFixture.getSessionDisplay( session );
    assertNotNull( sessionStore );
    assertNotNull( display );
    assertSame( sessionStore, getDisplayAdapter( display ).getSessionStore() );
    assertNotNull( sessionStore.getHttpSession() );
    ServletContext servletContext = session.getServletContext();
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    assertNotNull( applicationContext );
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    return( ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class ) );
  }

  private static Shell getFirstShell( IServletEngine servletEngine ) {
    HttpSession session = ClusterFixture.getFirstSession( servletEngine );
    Display display = ClusterFixture.getSessionDisplay( session );
    return display.getShells()[ 0 ];
  }

  private void clickCenterButton( int start, int end ) throws IOException {
    for( int i = start; i <= end; i++ ) {
      Response response = client.sendWidgetSelectedRequest( "w5" );
      assertTrue( response.isValidJavascript() );
      String expectedLabelPart = "relocated " + i + "/1";
      String msg = "label update mismatch, missing part: '" + expectedLabelPart + "'";
      assertTrue( msg, response.getContent().contains( expectedLabelPart ) );
    }
  }
}
