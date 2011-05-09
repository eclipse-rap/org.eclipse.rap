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
package org.eclipse.rap.rwt.cluster.testfixture.server;

import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.testfixture.ClusterFixture;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.test.TestEntryPoint;


public class ServletEngineTest extends TestCase {

  private List startedEngines;

  public void testPortsAreUnique() throws Exception {
    ServletEngine engine1 = startServletEngine( null );
    ServletEngine engine2 = startServletEngine( null );
  
    assertFalse( engine1.getPort() == engine2.getPort() );
  }
  
  public void testCreateConnection() throws IOException {
    ServletEngine engine = new ServletEngine();
    
    URL url = new URL( "http://localhost:123/"  );
    HttpURLConnection connection = engine.createConnection( url );

    assertEquals( url, connection.getURL() );
  }
  
  public void testEntryPoint() throws Exception {
    ServletEngine engine = startServletEngine( TestEntryPoint.class );
    RWTClient client = new RWTClient( engine );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    
    assertTrue( TestEntryPoint.wasCreateUIInvoked() );
  }
  
  public void testStartupSequence() throws Exception {
    ServletEngine servletEngine = startServletEngine( TestEntryPoint.class );
    RWTClient client = new RWTClient( servletEngine );

    Response startupPage = client.sendStartupRequest();
    assertTrue( startupPage.isValidStartupPage() );
    assertTrue( client.getSessionId().length() > 0 );

    Response initialJavascript = client.sendInitializationRequest();
    assertTrue( initialJavascript.isValidJavascript() );
    
    Response subsequentRequest = client.sendDisplayResizeRequest( 300, 300 );
    assertTrue( subsequentRequest.isValidJavascript() );
    
    Map sessions = servletEngine.getSessions();
    assertEquals( 1, sessions.size() );
    HttpSession httpSession = ( HttpSession )sessions.values().iterator().next();
    assertNotNull( ClusterFixture.getSessionDisplay( httpSession ) );
  }

  
  public void testServletEngineIsolation() throws Exception {
    ServletEngine engine1 = startServletEngine( TestEntryPoint.class );
    ServletEngine engine2 = startServletEngine( TestEntryPoint.class );

    sendRequest( engine1 );
    sendRequest( engine2 );
    
    assertEquals( 1, engine1.getSessions().size() );
    assertEquals( 1, engine2.getSessions().size() );
    String sessionId1 = ( String )engine1.getSessions().keySet().iterator().next();
    String sessionId2 = ( String )engine2.getSessions().keySet().iterator().next();
    assertFalse( sessionId1.equals( sessionId2 ) );
  }

  protected void setUp() throws Exception {
    System.setProperty( "lifecycle", "org.eclipse.rwt.internal.lifecycle.SimpleLifeCycle" );
    TestEntryPoint.reset();
    startedEngines = new LinkedList();
  }

  protected void tearDown() throws Exception {
    stopEngines();
    TestEntryPoint.reset();
    System.getProperties().remove( "lifecycle" );
  }

  private void stopEngines() throws Exception {
    while( startedEngines.size() > 0 ) {
      ServletEngine engine = ( ServletEngine )startedEngines.get( 0 );
      engine.stop();
      startedEngines.remove( 0 );
    }
  }

  private ServletEngine startServletEngine( Class entryPoint ) throws Exception {
    ServletEngine result = new ServletEngine();
    if( entryPoint != null ) {
      result.addEntryPoint( entryPoint );
    }
    result.start();
    startedEngines.add( result );
    return result;
  }

  private static void sendRequest( ServletEngine servletEngine ) throws IOException {
    new RWTClient( servletEngine ).sendStartupRequest();
  }
}
