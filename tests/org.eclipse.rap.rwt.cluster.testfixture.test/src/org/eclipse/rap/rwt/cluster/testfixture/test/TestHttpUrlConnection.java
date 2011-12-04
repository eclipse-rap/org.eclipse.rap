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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class TestHttpUrlConnection extends HttpURLConnection {

  private final String responseContent;
  private String cookie;
  private final String contentType;


  public TestHttpUrlConnection( String responseContent ) {
    this( -1, null, responseContent );
  }

  public TestHttpUrlConnection( int responseCode, String contentType, String responseContent ) {
    super( getUrl() );
    this.responseCode = responseCode;
    this.contentType = contentType;
    this.responseContent = responseContent;
  }

  @Override
  public int getResponseCode() throws IOException {
    return responseCode;
  }

  @Override
  public String getContentType() {
    return contentType;
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
    } catch( MalformedURLException mue ) {
      throw new RuntimeException( mue );
    }
  }
}
