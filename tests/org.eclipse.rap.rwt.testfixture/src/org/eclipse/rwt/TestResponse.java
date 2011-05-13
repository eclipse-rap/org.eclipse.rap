/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.*;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


public final class TestResponse implements HttpServletResponse {

  private TestServletOutputStream outStream;
  private String contentType;
  private String characterEncoding;
  private Map cookies;
  private Map headers;
  private int errorStatus;
  private String redirect;
  private PrintWriter printWriter;

  public TestResponse() {
    characterEncoding = "UTF-8";
    outStream = new TestServletOutputStream();
    cookies = new HashMap();
    headers = new HashMap();
  }

  public void addCookie( Cookie arg0 ) {
    cookies.put( arg0.getName(), arg0 );
  }

  public Cookie getCookie( String cookieName ) {
    return ( Cookie )cookies.get( cookieName );
  }

  public boolean containsHeader( String arg0 ) {
    return false;
  }

  public String encodeURL( String arg0 ) {
    return arg0;
  }

  public String encodeRedirectURL( String arg0 ) {
    return arg0;
  }

  public String encodeUrl( String arg0 ) {
    return arg0;
  }

  public String encodeRedirectUrl( String arg0 ) {
    return arg0;
  }

  public void sendError( int code, String message )
    throws IOException
  {
    errorStatus = code;
    getWriter().write( "HTTP ERROR " + code + "\nReason: " + message );
  }

  public void sendError( int code ) throws IOException {
    errorStatus = code;
    getWriter().write( "HTTP ERROR " + code );
  }

  public int getErrorStatus() {
    return errorStatus;
  }

  public void sendRedirect( String arg0 ) throws IOException {
    redirect = arg0;
  }

  public String getRedirect() {
    return redirect;
  }

  public void setDateHeader( String arg0, long arg1 ) {
  }

  public void addDateHeader( String arg0, long arg1 ) {
  }

  public void setHeader( String arg0, String arg1 ) {
    headers.put( arg0, arg1 );
  }

  public String getHeader( String name ) {
    return ( String )headers.get( name );
  }

  public void addHeader( String arg0, String arg1 ) {
  }

  public void setIntHeader( String arg0, int arg1 ) {
  }

  public void addIntHeader( String arg0, int arg1 ) {
  }

  public void setStatus( int arg0 ) {
  }

  public void setStatus( int arg0, String arg1 ) {
  }

  public ServletOutputStream getOutputStream() throws IOException {
    return outStream;
  }

  public PrintWriter getWriter() throws IOException {
    if( printWriter == null ) {
      printWriter = new PrintWriter( new OutputStreamWriter( outStream, characterEncoding ) );
    }
    return printWriter;
  }

  public void setContentLength( int arg0 ) {
  }

  public void setContentType( String contentType ) {
    this.contentType = contentType;
    setHeader( "Content-Type", contentType );
  }

  public String getContentType() {
    return contentType;
  }

  public void setCharacterEncoding( String charset ) {
    characterEncoding = charset;
    setHeader( "Content-Type", contentType + "; charset=" + charset );
  }

  public String getCharacterEncoding() {
    return characterEncoding;
  }

  public void setBufferSize( int arg0 ) {
  }

  public int getBufferSize() {
    return 0;
  }

  public void flushBuffer() throws IOException {
  }

  public void resetBuffer() {
  }

  public boolean isCommitted() {
    return false;
  }

  public void reset() {
  }

  public void setLocale( Locale arg0 ) {
  }

  public Locale getLocale() {
    return null;
  }

  public String getContent() {
    String result;
    printWriter.flush();
    ByteArrayOutputStream content = outStream.getContent();
    try {
      result = content.toString( characterEncoding );
    } catch( UnsupportedEncodingException exception ) {
      throw new RuntimeException( exception );
    }
    return result;
  }

  public void clearContent() {
    outStream = new TestServletOutputStream();
    printWriter = null;
  }
}
