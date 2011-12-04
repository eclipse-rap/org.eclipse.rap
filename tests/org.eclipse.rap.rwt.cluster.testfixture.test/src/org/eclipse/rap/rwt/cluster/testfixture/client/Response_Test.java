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


public class Response_Test extends TestCase {

  private static final String TYPE_HTML = "text/html;charset=UTF-8";
  private static final String TYPE_JSON = "application/json;charset=UTF-8";
  private static final String VALID_HTML
    = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">";
  private static final String VALID_JSON = "{}";

  public void testConstructor() throws IOException {
    int responseCode = 1;
    String content = "content";
    TestHttpUrlConnection connection = new TestHttpUrlConnection( responseCode, TYPE_HTML, content );
    Response response = new Response( connection );

    assertEquals( responseCode, response.getResponseCode() );
    assertEquals( TYPE_HTML, response.getContentType() );
    assertEquals( content, response.getContentText() );
  }

  public void testIsValidJavascriptWithValidContent() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 200, TYPE_JSON, VALID_JSON );

    Response response = new Response( connection );

    assertTrue( response.isValidJsonResponse() );
  }

  public void testIsValidJavascripWithInvalidContent() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 200, TYPE_JSON, "" );

    Response response = new Response( connection );

    assertFalse( response.isValidJsonResponse() );
  }

  public void testIsValidJavascripWithWrongContentType() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 200, TYPE_HTML, VALID_JSON );

    Response response = new Response( connection );

    assertFalse( response.isValidJsonResponse() );
  }

  public void testIsValidJavascripWithResponseCode404() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 404, TYPE_JSON, VALID_JSON );

    Response response = new Response( connection );

    assertFalse( response.isValidJsonResponse() );
  }

  public void testIsValidStartupPageWithStartupPage() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 200, TYPE_HTML, VALID_HTML );

    Response response = new Response( connection );

    assertTrue( response.isValidStartupPage() );
  }

  public void testIsValidStartupPageWithInvalidHtml() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 200, TYPE_HTML, "no html" );

    Response response = new Response( connection );

    assertFalse( response.isValidStartupPage() );
  }

  public void testIsValidStartupPageWithWrongContentType() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 200, TYPE_JSON, VALID_HTML );

    Response response = new Response( connection );

    assertFalse( response.isValidStartupPage() );
  }

  public void testIsValidStartupPageResponseCode404() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 404, TYPE_HTML, VALID_HTML );

    Response response = new Response( connection );

    assertFalse( response.isValidStartupPage() );
  }
}
