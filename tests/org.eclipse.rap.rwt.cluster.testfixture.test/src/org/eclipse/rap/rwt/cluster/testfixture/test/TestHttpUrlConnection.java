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
package org.eclipse.rap.rwt.cluster.testfixture.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class TestHttpUrlConnection extends HttpURLConnection {

  private final String responseContent;
  private String responseCookie;
  private String requestCookie;
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

  public void setCookieToResponse( String cookie ) {
    responseCookie = cookie;
  }

  public String getCookieFromRequest() {
    return requestCookie;
  }

  @Override
  public boolean usingProxy() {
    return false;
  }

  @Override
  public void disconnect() {
  }

  @Override
  public void connect() throws IOException {
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return new ByteArrayOutputStream();
  }

  @Override
  public void setRequestProperty( String key, String value ) {
    super.setRequestProperty( key, value );
    if( key.equals( "Cookie" ) ) {
      requestCookie = value;
    }
  }

  @Override
  public String getHeaderField( String name ) {
    String result = null;
    if( name.equals( "Set-Cookie" ) ) {
      result = responseCookie;
    }
    return result;
  }

  @Override
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
