/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


/**
 * <p>
 * <strong>IMPORTANT:</strong> This class is <em>not</em> part the public RAP
 * API. It may change or disappear without further notice. Use this class at
 * your own risk.
 * </p>
 */
public class TestResponse implements HttpServletResponse {

  private TestServletOutputStream outStream;
  private String contentType;
  private String characterEncoding;
  private final Map<String,Cookie> cookies;
  private final Map<String,String> headers;
  private int errorStatus;
  private int status;
  private String redirect;
  private PrintWriter printWriter;

  public TestResponse() {
    characterEncoding = "UTF-8";
    outStream = new TestServletOutputStream();
    cookies = new HashMap<String,Cookie>();
    headers = new HashMap<String,String>();
  }

  @Override
  public void addCookie( Cookie arg0 ) {
    cookies.put( arg0.getName(), arg0 );
  }

  public Cookie getCookie( String cookieName ) {
    return cookies.get( cookieName );
  }

  @Override
  public boolean containsHeader( String arg0 ) {
    return false;
  }

  @Override
  public String encodeURL( String arg0 ) {
    return arg0;
  }

  @Override
  public String encodeRedirectURL( String arg0 ) {
    return arg0;
  }

  public String encodeUrl( String arg0 ) {
    return arg0;
  }

  public String encodeRedirectUrl( String arg0 ) {
    return arg0;
  }

  @Override
  public void sendError( int code, String message )
    throws IOException
  {
    errorStatus = code;
    getWriter().write( "HTTP ERROR " + code + "\nReason: " + message );
  }

  @Override
  public void sendError( int code ) throws IOException {
    errorStatus = code;
    getWriter().write( "HTTP ERROR " + code );
  }

  public int getErrorStatus() {
    return errorStatus;
  }

  @Override
  public void sendRedirect( String arg0 ) throws IOException {
    redirect = arg0;
  }

  public String getRedirect() {
    return redirect;
  }

  @Override
  public void setDateHeader( String arg0, long arg1 ) {
    headers.put( arg0, new Date( arg1 ).toString() );
  }

  @Override
  public void addDateHeader( String arg0, long arg1 ) {
    headers.put( arg0, new Date( arg1 ).toString() );
  }

  @Override
  public void setHeader( String arg0, String arg1 ) {
    headers.put( arg0, arg1 );
  }

  @Override
  public String getHeader( String name ) {
    return headers.get( name );
  }

  @Override
  public void addHeader( String arg0, String arg1 ) {
    headers.put( arg0, arg1 );
  }

  @Override
  public void setIntHeader( String arg0, int arg1 ) {
  }

  @Override
  public void addIntHeader( String arg0, int arg1 ) {
  }

  @Override
  public void setStatus( int arg0 ) {
    status = arg0;
  }

  public void setStatus( int arg0, String arg1 ) {
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    return outStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if( printWriter == null ) {
      printWriter = new PrintWriter( new OutputStreamWriter( outStream, characterEncoding ) );
    }
    return printWriter;
  }

  @Override
  public void setContentLength( int arg0 ) {
  }

  @Override
  public void setContentLengthLong( long len ) {
  }

  @Override
  public void setContentType( String contentType ) {
    this.contentType = contentType;
    setHeader( "Content-Type", contentType );
  }

  @Override
  public String getContentType() {
    return getHeader( "Content-Type" );
  }

  @Override
  public void setCharacterEncoding( String charset ) {
    characterEncoding = charset;
    setHeader( "Content-Type", contentType + "; charset=" + charset );
  }

  @Override
  public String getCharacterEncoding() {
    return characterEncoding;
  }

  @Override
  public void setBufferSize( int arg0 ) {
  }

  @Override
  public int getBufferSize() {
    return 0;
  }

  @Override
  public void flushBuffer() throws IOException {
  }

  @Override
  public void resetBuffer() {
  }

  @Override
  public boolean isCommitted() {
    return false;
  }

  @Override
  public void reset() {
  }

  @Override
  public void setLocale( Locale arg0 ) {
  }

  @Override
  public Locale getLocale() {
    return null;
  }

  public String getContent() {
    String result = "";
    if( printWriter != null ) {
      printWriter.flush();
      ByteArrayOutputStream content = outStream.getContent();
      try {
        result = content.toString( characterEncoding );
      } catch( UnsupportedEncodingException exception ) {
        throw new RuntimeException( exception );
      }
    }
    return result;
  }

  public void clearContent() {
    outStream = new TestServletOutputStream();
    printWriter = null;
  }

  @Override
  public int getStatus() {
    return status;
  }

  @Override
  public Collection<String> getHeaders( String name ) {
    return null;
  }

  @Override
  public Collection<String> getHeaderNames() {
    return null;
  }

  @Override
  public void sendRedirect( String location, int sc, boolean clearBuffer ) throws IOException {
  }

}
