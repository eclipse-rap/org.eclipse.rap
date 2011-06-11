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
package org.eclipse.rap.rwt.cluster.testfixture.internal.server;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.test.TestEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPoint;


public class DelegatingServletEngine_Test extends TestCase {
  
  private static class TestServletEngine implements IServletEngine {
    static final String START = "start";
    static final String STOP = "stop";
    static final String GET_PORT = "getPort";
    static final String GET_SESSIONS = "getSessions";
    static final String CREATE_CONNECTION = "createConnection";
    
    List<String> invocations;
    
    public TestServletEngine() {
      invocations = new LinkedList<String>();
    }
    
    public void start( Class<? extends IEntryPoint> entryPointClass ) throws Exception {
      invocations.add( START );
    }

    public void stop() throws Exception {
      invocations.add( STOP );
    }

    public int getPort() {
      invocations.add( GET_PORT );
      return 0;
    }

    public HttpSession[] getSessions() {
      invocations.add( GET_SESSIONS );
      return null;
    }

    public HttpURLConnection createConnection( URL url ) throws IOException {
      invocations.add( CREATE_CONNECTION );
      return null;
    }
  }
  
  private TestServletEngine testServletEngine;

  public void testGetDelegate() {
    TestServletEngine delegate = new TestServletEngine();
    
    DelegatingServletEngine engine = new DelegatingServletEngine( delegate );
    
    assertSame( delegate, engine.getDelegate() );
  }
  
  public void testStartWithNullEntryPointClass() throws Exception {
    IServletEngine engine = new DelegatingServletEngine( testServletEngine );
    try {
      engine.start( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testStartDelegates() throws Exception {
    IServletEngine engine = new DelegatingServletEngine( testServletEngine );
    
    engine.start( TestEntryPoint.class );
    
    assertEquals( TestServletEngine.START, testServletEngine.invocations.get( 0 ) );
  }
  
  public void testStartMultipleTimes() throws Exception {
    IServletEngine engine = startServletEngine( TestEntryPoint.class );
    try {
      engine.start( TestEntryPoint.class );
      fail();
    } catch( IllegalStateException e ) {
    }
  }
  
  public void testGetSessionsDelegates() throws Exception {
    IServletEngine engine = new DelegatingServletEngine( testServletEngine );
    engine.start( TestEntryPoint.class );

    engine.getSessions();
    
    assertTrue( testServletEngine.invocations.contains( TestServletEngine.START ) );
  }
  
  public void testGetSessionsAfterStop() throws Exception {
    IServletEngine engine = startServletEngine( TestEntryPoint.class );
    engine.stop();
    
    try {
      engine.getSessions();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testStopDelegates() throws Exception {
    IServletEngine engine = new DelegatingServletEngine( testServletEngine );
    engine.start( TestEntryPoint.class );

    engine.stop();
    
    assertTrue( testServletEngine.invocations.contains( TestServletEngine.STOP ) );
  }
  
  public void testGetPortDelegates() throws Exception {
    IServletEngine engine = new DelegatingServletEngine( testServletEngine );
    engine.start( TestEntryPoint.class );
    
    engine.getPort();
    
    assertTrue( testServletEngine.invocations.contains( TestServletEngine.GET_PORT ) );
  }
  
  public void testCreateConnectionDoesNotDelegate() throws Exception {
    IServletEngine engine = new DelegatingServletEngine( testServletEngine );
    URL url = new URL( "http://localhost:123/"  );
    
    HttpURLConnection connection = engine.createConnection( url );
    
    assertNotNull( connection );
    assertTrue( testServletEngine.invocations.isEmpty() );
  }
  
  private IServletEngine startServletEngine( Class<? extends IEntryPoint> entryPoint )
    throws Exception
  {
    IServletEngine result = new DelegatingServletEngine( testServletEngine );
    result.start( entryPoint );
    return result;
  }
  
  protected void setUp() throws Exception {
    testServletEngine = new TestServletEngine();
  }
}
