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
package org.eclipse.rap.rwt.cluster.testfixture.client;


import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.testfixture.test.TestHttpUrlConnection;
import org.eclipse.rap.rwt.cluster.testfixture.test.TestServletEngine;


public class RWTClient_Test extends TestCase {
  
  private TestServletEngine servletEngine;

  public void testConstructor() {
    RWTClient client = new RWTClient( servletEngine );
  
    assertSame( servletEngine, client.getServletEngine() );
  }

  public void testSendRequest() throws IOException {
    RWTClient client = new RWTClient( servletEngine );
    String responseContent = "responseContent";
    servletEngine.setConnection( new TestHttpUrlConnection( responseContent ) );

    Response response = client.sendRequest();
    
    assertEquals( responseContent, response.getContentText() );
  }
  
  public void testSendResourceRequest() throws IOException {
    RWTClient client = new RWTClient( servletEngine );
    servletEngine.setConnection( new TestHttpUrlConnection( "responseContent" ) );

    client.sendResourceRequest( "foo/bar.gif" );
    
    String connectionUrl = servletEngine.getConnectionUrl().toExternalForm();
    assertEquals( "http://localhost:-1/foo/bar.gif", connectionUrl );
  }
  
  public void testRequestWithParameters() throws IOException {
    RWTClient client = new RWTClient( servletEngine );
    servletEngine.setConnection( new TestHttpUrlConnection( "" ) );
    client.addParameter( "foo", "bar" );
    client.sendRequest();
    
    String connectionUrl = servletEngine.getConnectionUrl().toExternalForm();
    assertEquals( "http://localhost:-1/rap?foo=bar", connectionUrl );
  }
  
  public void testSessionIdWhenSetCookieHeaderWasSent() throws IOException {
    RWTClient client = new RWTClient( servletEngine );
    TestHttpUrlConnection connection = new TestHttpUrlConnection( "" );
    connection.setCookie( "JSESSIONID=node0xyz.node0;Path=/" );
    servletEngine.setConnection( connection );
    
    client.sendRequest();
    
    assertEquals( "node0xyz.node0", client.getSessionId() );
  }
  
  public void testSessionIdWhenNoHeaderWasSent() throws IOException {
    RWTClient client = new RWTClient( servletEngine );
    TestHttpUrlConnection connection = new TestHttpUrlConnection( "" );
    servletEngine.setConnection( connection );
    
    client.sendRequest();
    
    assertEquals( "", client.getSessionId() );
  }
  
  public void testSessionIdIsNotOverridden() throws IOException {
    RWTClient client = new RWTClient( servletEngine );
    TestHttpUrlConnection connection1 = new TestHttpUrlConnection( "" );
    connection1.setCookie( "JSESSIONID=xyz;Path=/" );
    servletEngine.setConnection( connection1 );
    client.sendRequest();

    TestHttpUrlConnection connection2 = new TestHttpUrlConnection( "" );
    servletEngine.setConnection( connection2 );
    client.sendRequest();
    
    assertEquals( "xyz", client.getSessionId() );
  }
  
  public void testSessionIdAfterChangedServletEngine() throws IOException {
    RWTClient client = new RWTClient( servletEngine );
    TestHttpUrlConnection connection = new TestHttpUrlConnection( "" );
    connection.setCookie( "JSESSIONID=xyz;Path=/" );
    servletEngine.setConnection( connection );
    client.sendRequest();
    String sessionIdBeforeChange = client.getSessionId();
    TestServletEngine otherServletEngine = new TestServletEngine();

    client.changeServletEngine( otherServletEngine );
    
    assertNotNull( sessionIdBeforeChange );
    assertEquals( sessionIdBeforeChange, client.getSessionId() );
  }
  
  public void testSessionIdIsSentWithRequest() throws IOException {
    RWTClient client = new RWTClient( servletEngine );
    TestHttpUrlConnection connection = new TestHttpUrlConnection( "" );
    connection.setCookie( "JSESSIONID=xyz;Path=/" );
    servletEngine.setConnection( connection );
    client.sendRequest();

    client.sendRequest();
    
    String connectionUrl = servletEngine.getConnectionUrl().toExternalForm();
    assertTrue( connectionUrl.endsWith( ";jsessionid=xyz" ) );
  }
  
  public void testChangeServletEngine() {
    RWTClient client = new RWTClient( servletEngine );
    TestServletEngine otherServletEngine = new TestServletEngine();
    client.changeServletEngine( otherServletEngine );
    
    assertSame( otherServletEngine, client.getServletEngine() );
  }
  
  protected void setUp() throws Exception {
    servletEngine = new TestServletEngine();
  }
}
