/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.service.IServiceHandler;

public abstract class AbstractServiceHandler implements IServiceHandler {
  
  private static final int TIMEOUT = 600000;
  private static final String EXPIRATION_TIME_FORMAT
    = "EEE, dd MMM yyyy HH:mm:ss zzz";
  private static final SimpleDateFormat FORMATTER
    = new SimpleDateFormat( EXPIRATION_TIME_FORMAT, Locale.US );  
  private static final String EXPIRES = "Expires";
  private static final String ACCEPT_ENCODING = "Accept-Encoding";
  private static final String CONTENT_ENCODING = "Content-Encoding";
  private static final String ENCODING_GZIP = "gzip";


  IServiceAdapter getServiceAdapter( final Adaptable model ) {
    return ( IServiceAdapter )model.getAdapter( IServiceAdapter.class );
  }

  static boolean isAcceptEncoding() {
    String encodings = getRequest().getHeader( ACCEPT_ENCODING );
    return encodings != null && encodings.indexOf( ENCODING_GZIP ) != -1;
  }

  static PrintWriter getOutputWriter() throws IOException {
    PrintWriter result;
    if( isAcceptEncoding() && getInitProps().isCompression() ) {
      OutputStream out = getResponse().getOutputStream();
      GZIPOutputStream zipStream = new GZIPOutputStream( out );
      OutputStreamWriter utf8Writer 
        = new OutputStreamWriter( zipStream, HTML.CHARSET_NAME_UTF_8 );
      result = new PrintWriter( utf8Writer, false );
      getResponse().setHeader( CONTENT_ENCODING, ENCODING_GZIP );
    } else {
      result = getResponse().getWriter();
    }
    return result;
  }
  
  protected void setExpirationHeader() {
    // set an expiration date for the js-library to avoid reloading it
    // on every page request!
    // TODO: configuration of the expiration time
    FORMATTER.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
    Date date = new Date( System.currentTimeMillis() + TIMEOUT );
    String expirationTime = FORMATTER.format( date );
    getResponse().setHeader( EXPIRES, expirationTime );
  }

  static IInitialization getInitProps() {
    IConfiguration configuration = ConfigurationReader.getConfiguration();
    return configuration.getInitialization();
  }
  
  protected static HttpServletRequest getRequest() {
    return ContextProvider.getRequest();
  }
  
  protected static HttpServletResponse getResponse() {
    return ContextProvider.getResponse();
  }
}