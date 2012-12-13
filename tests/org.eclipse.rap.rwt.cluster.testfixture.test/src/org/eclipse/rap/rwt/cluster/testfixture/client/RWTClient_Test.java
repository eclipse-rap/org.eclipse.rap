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
package org.eclipse.rap.rwt.cluster.testfixture.client;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.cluster.testfixture.test.TestHttpUrlConnection;
import org.eclipse.rap.rwt.cluster.testfixture.test.TestServletEngine;


public class RWTClient_Test extends TestCase {

  private TestServletEngine servletEngine;
  private TestConnectionProvider connectionProvider;

  public void testConstructor() {
    RWTClient client = new RWTClient( servletEngine );

    assertSame( servletEngine, client.getServletEngine() );
  }

  public void testSendRequest() throws IOException {
    String responseContent = "responseContent";
    connectionProvider.setConnection( new TestHttpUrlConnection( responseContent ) );
    RWTClient client = new RWTClient( servletEngine, connectionProvider );

    Response response = client.sendPostRequest();

    assertEquals( responseContent, response.getContentText() );
  }

  public void testSendResourceRequest() throws IOException {
    connectionProvider.setConnection( new TestHttpUrlConnection( "responseContent" ) );
    RWTClient client = new RWTClient( servletEngine, connectionProvider );

    client.sendResourceRequest( "foo/bar.gif" );

    String connectionUrl = connectionProvider.getConnectionUrl().toExternalForm();
    assertEquals( "http://localhost:-1/foo/bar.gif", connectionUrl );
  }

  public void testSendUICallBackRequest() throws IOException {
    connectionProvider.setConnection( new TestHttpUrlConnection( "responseContent" ) );
    RWTClient client = new RWTClient( servletEngine, connectionProvider );

    client.sendUICallBackRequest( 2000 );

    String expectedUrl = "http://localhost:-1/rwt?"
                         + "servicehandler=org.eclipse.rap.serverpush";
    String connectionUrl = connectionProvider.getConnectionUrl().toExternalForm();
    assertEquals( expectedUrl, connectionUrl );
  }

  public void testRequestWithParameters() throws IOException {
    connectionProvider.setConnection( new TestHttpUrlConnection( "" ) );
    RWTClient client = new RWTClient( servletEngine, connectionProvider );
    Map<String,String> parameters = new HashMap<String,String>();
    parameters.put( "foo", "bar" );
    client.sendRequest( "GET", parameters );

    String connectionUrl = connectionProvider.getConnectionUrl().toExternalForm();
    assertEquals( "http://localhost:-1/rwt?foo=bar", connectionUrl );
  }

  public void testSessionIdWhenSetCookieHeaderWasSent() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( "" );
    connection.setCookie( "JSESSIONID=node0xyz.node0;Path=/" );
    connectionProvider.setConnection( connection );
    RWTClient client = new RWTClient( servletEngine, connectionProvider );

    client.sendPostRequest();

    assertEquals( "node0xyz.node0", client.getSessionId() );
  }

  public void testSessionIdWhenNoHeaderWasSent() throws IOException {
    connectionProvider.setConnection( new TestHttpUrlConnection( "" ) );
    RWTClient client = new RWTClient( servletEngine, connectionProvider );

    client.sendPostRequest();

    assertEquals( "", client.getSessionId() );
  }

  public void testSessionIdIsNotOverridden() throws IOException {
    TestHttpUrlConnection connection1 = new TestHttpUrlConnection( "" );
    connection1.setCookie( "JSESSIONID=xyz;Path=/" );
    connectionProvider.setConnection( connection1 );
    RWTClient client = new RWTClient( servletEngine, connectionProvider );
    client.sendPostRequest();

    TestHttpUrlConnection connection2 = new TestHttpUrlConnection( "" );
    connectionProvider.setConnection( connection2 );
    client.sendPostRequest();

    assertEquals( "xyz", client.getSessionId() );
  }

  public void testSessionIdAfterChangedServletEngine() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( "" );
    connection.setCookie( "JSESSIONID=xyz;Path=/" );
    connectionProvider.setConnection( connection );
    RWTClient client = new RWTClient( servletEngine, connectionProvider );
    client.sendPostRequest();
    String sessionIdBeforeChange = client.getSessionId();
    TestServletEngine otherServletEngine = new TestServletEngine();

    client.changeServletEngine( otherServletEngine );

    assertNotNull( sessionIdBeforeChange );
    assertEquals( sessionIdBeforeChange, client.getSessionId() );
  }

  public void testSessionIdIsSentWithRequest() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( "" );
    connection.setCookie( "JSESSIONID=xyz;Path=/" );
    connectionProvider.setConnection( connection );
    RWTClient client = new RWTClient( servletEngine, connectionProvider );
    client.sendPostRequest();

    client.sendPostRequest();

    String connectionUrl = connectionProvider.getConnectionUrl().toExternalForm();
    assertTrue( connectionUrl.endsWith( ";jsessionid=xyz" ) );
  }

  public void testChangeServletEngine() {
    RWTClient client = new RWTClient( servletEngine );
    TestServletEngine otherServletEngine = new TestServletEngine();
    client.changeServletEngine( otherServletEngine );

    assertSame( otherServletEngine, client.getServletEngine() );
  }

  @Override
  protected void setUp() throws Exception {
    servletEngine = new TestServletEngine();
    connectionProvider = new TestConnectionProvider();
  }

  private static class TestConnectionProvider implements IConnectionProvider {
    private URLConnection connection;
    private URL connectionUrl;

    public URLConnection createConnection( URL url ) throws IOException {
      connectionUrl = url;
      return connection;
    }

    void setConnection( URLConnection connection ) {
      this.connection = connection;
    }

    URL getConnectionUrl() {
      return connectionUrl;
    }
  }
}
