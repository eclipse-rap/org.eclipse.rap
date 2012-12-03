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
package org.eclipse.rap.rwt.cluster.testfixture.internal.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterTestHelper;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineFactory;
import org.eclipse.rap.rwt.cluster.testfixture.test.TestEntryPoint;


public abstract class ServletEngineTestBase extends TestCase {

  private List<IServletEngine> startedEngines;
  private PrintStream bufferedSystemErr;
  private ByteArrayOutputStream redirectedSystemErr;

  protected abstract IServletEngineFactory getServletEngineFactory();

  public void testPortsAreUnique() throws Exception {
    IServletEngine engine1 = startServletEngine( TestEntryPoint.class );
    IServletEngine engine2 = startServletEngine( TestEntryPoint.class );

    assertFalse( engine1.getPort() == engine2.getPort() );
  }

  public void testSpecifyPort() throws Exception {
    int freePort = SocketUtil.getFreePort();
    IServletEngine engine = getServletEngineFactory().createServletEngine( freePort );
    startedEngines.add( engine );

    engine.start( TestEntryPoint.class );

    assertEquals( freePort, engine.getPort() );
  }

  public void testEntryPoint() throws Exception {
    IServletEngine engine = startServletEngine( TestEntryPoint.class );
    RWTClient client = new RWTClient( engine );
    client.sendStartupRequest();
    client.sendInitializationRequest();

    assertTrue( TestEntryPoint.wasCreateUIInvoked() );
  }

  public void testExceptionDuringRequest() throws Exception {
    IServletEngine engine = startServletEngine( TestEntryPoint.class );
    TestEntryPoint.setRunnable( new Runnable() {
      public void run() {
        throw new RuntimeException();
      }
    } );
    RWTClient client = new RWTClient( engine );
    client.sendStartupRequest();

    try {
      client.sendInitializationRequest();
    } catch( IOException ioe ) {
      assertTrue( ioe.getMessage().contains( "500" ) );
    }
  }

  public void testStartupSequence() throws Exception {
    IServletEngine servletEngine = startServletEngine( TestEntryPoint.class );
    RWTClient client = new RWTClient( servletEngine );

    Response startupPage = client.sendStartupRequest();
    assertTrue( startupPage.isValidStartupPage() );
    assertTrue( client.getSessionId().length() > 0 );

    Response initialJavascript = client.sendInitializationRequest();
    assertTrue( initialJavascript.isValidJsonResponse() );

    Response subsequentRequest = client.sendDisplayResizeRequest( 300, 300 );
    assertTrue( subsequentRequest.isValidJsonResponse() );

    HttpSession[] sessions = servletEngine.getSessions();
    assertEquals( 1, sessions.length );
    assertNotNull( ClusterTestHelper.getSessionDisplay( sessions[ 0 ] ) );
  }


  public void testServletEngineIsolation() throws Exception {
    IServletEngine engine1 = startServletEngine( TestEntryPoint.class );
    IServletEngine engine2 = startServletEngine( TestEntryPoint.class );

    sendStartupRequest( engine1 );
    sendStartupRequest( engine2 );

    assertEquals( 1, engine1.getSessions().length );
    assertEquals( 1, engine2.getSessions().length );
    HttpSession session1 = engine1.getSessions()[ 0 ];
    HttpSession session2 = engine2.getSessions()[ 0 ];
    assertNotSame( session1, session2 );
    String sessionId1 = session1.getId();
    String sessionId2 = session2.getId();
    assertFalse( sessionId1.equals( sessionId2 ) );
    assertNotSame( session1.getServletContext(), session2.getServletContext() );
  }

  public void testSessionAttributeNeedNotBeSerializable() throws Exception {
    IServletEngine engine = startServletEngine( TestEntryPoint.class );
    sendStartupRequest( engine );
    HttpSession session = engine.getSessions()[ 0 ];

    String name = "name";
    Object nonSerializable = new Object();
    session.setAttribute( name, nonSerializable );

    assertSame( nonSerializable, session.getAttribute( name ) );
  }

  protected void setUp() throws Exception {
    TestEntryPoint.reset();
    startedEngines = new LinkedList<IServletEngine>();
    bufferedSystemErr = System.err;
    redirectedSystemErr = new ByteArrayOutputStream();
    System.setErr( new PrintStream( redirectedSystemErr, true ) );
  }

  protected void tearDown() throws Exception {
    System.setErr( bufferedSystemErr );
    stopEngines();
    TestEntryPoint.reset();
  }

  private void stopEngines() throws Exception {
    while( startedEngines.size() > 0 ) {
      IServletEngine engine = startedEngines.get( 0 );
      engine.stop();
      startedEngines.remove( 0 );
    }
  }

  private IServletEngine startServletEngine( Class<? extends EntryPoint> entryPoint )
    throws Exception
  {
    IServletEngine result = getServletEngineFactory().createServletEngine();
    result.start( entryPoint );
    startedEngines.add( result );
    return result;
  }

  private static void sendStartupRequest( IServletEngine servletEngine ) throws IOException {
    new RWTClient( servletEngine ).sendStartupRequest();
  }
}
