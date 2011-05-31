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

import org.eclipse.rap.rwt.cluster.testfixture.test.TestHttpUrlConnection;

import junit.framework.TestCase;


public class Response_Test extends TestCase {
  
  private static final String VALID_HTML
    = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">";
  private static final String VALID_JAVASCRIPT
    = "var req = org.eclipse.swt.Request.getInstance();foo();bar();";
  
  public void testConstructor() throws IOException {
    int responseCode = 1;
    String content = "content";
    TestHttpUrlConnection connection = new TestHttpUrlConnection( responseCode, content );

    Response response = new Response( connection );
    
    assertEquals( responseCode, response.getResponseCode() );
    assertEquals( content, response.getContentText() );
  }
  
  public void testIsValidJavascripWithInvalidContent() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 200, "" );

    Response response = new Response( connection );
    
    assertFalse( response.isValidJavascript() );
  }

  public void testIsValidJavascripWithResponseCode404() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 404, "" );

    Response response = new Response( connection );
    
    assertFalse( response.isValidJavascript() );
  }
  
  public void testIsValidJavascripWithValidContent() throws IOException {
    String content = VALID_JAVASCRIPT;
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 200, content );

    Response response = new Response( connection );
    
    assertTrue( response.isValidJavascript() );
  }
  
  public void testIsValidStartupPageWithStartupPage() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 200, VALID_HTML );

    Response response = new Response( connection );
    
    assertTrue( response.isValidStartupPage() );
  }

  public void testIsValidStartupPageWithInvalidHtml() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 200, "no html" );

    Response response = new Response( connection );
    
    assertFalse( response.isValidStartupPage() );
  }
  
  public void testIsValidStartupPageResponseCode404() throws IOException {
    TestHttpUrlConnection connection = new TestHttpUrlConnection( 404, VALID_HTML );

    Response response = new Response( connection );
    
    assertFalse( response.isValidStartupPage() );
  }
}
