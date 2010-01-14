/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public final class TestResponse implements HttpServletResponse {

  private ServletOutputStream outStream;
  private String contentType;
  private Map cookies = new HashMap();
  private Map headers = new HashMap();
  private int errorStatus;
  private String redirect;

  public void addCookie( final Cookie arg0 ) {
    cookies.put( arg0.getName(), arg0 );
  }

  public Cookie getCookie( final String cookieName ) {
    return ( Cookie )cookies.get( cookieName );
  }

  public boolean containsHeader( final String arg0 ) {
    return false;
  }

  public String encodeURL( final String arg0 ) {
    return arg0;
  }

  public String encodeRedirectURL( final String arg0 ) {
    return arg0;
  }

  public String encodeUrl( final String arg0 ) {
    return arg0;
  }

  public String encodeRedirectUrl( final String arg0 ) {
    return arg0;
  }

  public void sendError( final int arg0, final String arg1 )
    throws IOException
  {
    errorStatus = arg0;
  }

  public void sendError( final int arg0 ) throws IOException {
    errorStatus = arg0;
  }

  public int getErrorStatus() {
    return errorStatus;
  }

  public void sendRedirect( final String arg0 ) throws IOException {
    redirect = arg0;
  }

  public String getRedirect() {
    return redirect;
  }

  public void setDateHeader( final String arg0, final long arg1 ) {
  }

  public void addDateHeader( final String arg0, final long arg1 ) {
  }

  public void setHeader( final String arg0, final String arg1 ) {
    headers.put( arg0, arg1 );
  }

  public String getHeader( final String name ) {
    return ( String )headers.get( name );
  }

  public void addHeader( final String arg0, final String arg1 ) {
  }

  public void setIntHeader( final String arg0, final int arg1 ) {
  }

  public void addIntHeader( final String arg0, final int arg1 ) {
  }

  public void setStatus( final int arg0 ) {
  }

  public void setStatus( final int arg0, final String arg1 ) {
  }

  public String getCharacterEncoding() {
    return null;
  }

  public ServletOutputStream getOutputStream() throws IOException {
    return outStream;
  }

  public void setOutputStream( final ServletOutputStream outStream ) {
    this.outStream = outStream;
  }

  public PrintWriter getWriter() throws IOException {
    return new PrintWriter( outStream );
  }

  public void setContentLength( final int arg0 ) {
  }

  public void setContentType( final String contentType ) {
    this.contentType = contentType;
  }

  public String getContentType() {
    return contentType;
  }

  public void setBufferSize( final int arg0 ) {
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

  public void setLocale( final Locale arg0 ) {
  }

  public Locale getLocale() {
    return null;
  }

  public void setCharacterEncoding( String charset ) {
  }
}