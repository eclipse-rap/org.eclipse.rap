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
package org.eclipse.rap.rwt.cluster.testfixture.test;

import java.io.*;
import java.net.*;


public class TestHttpUrlConnection extends HttpURLConnection {

  private final String responseContent;
  private String cookie;

  
  public TestHttpUrlConnection( String responseContent ) {
    this( -1, responseContent );
  }
  
  public TestHttpUrlConnection( int responseCode, String responseContent ) {
    super( getUrl() );
    this.responseContent = responseContent;
    this.responseCode = responseCode;
  }
  
  public int getResponseCode() throws IOException {
    return responseCode;
  }

  public void setCookie( String cookie ) {
    this.cookie = cookie;
  }

  public boolean usingProxy() {
    return false;
  }

  public void disconnect() {
  }

  public void connect() throws IOException {
  }
  
  public String getHeaderField( String name ) {
    String result = null;
    if( name.equals( "Set-Cookie" ) ) {
      result = cookie;
    }
    return result;
  }
  
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream( responseContent.getBytes( "utf-8" ) );
  }

  private static URL getUrl() {
    try {
      return new URL( "http://localhost/path" );
    } catch( MalformedURLException e ) {
      throw new RuntimeException( e );
    }
  }
}
