/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.*;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.service.IServiceHandler;

public abstract class AbstractServiceHandler implements IServiceHandler {

  private static final String ACCEPT_ENCODING = "Accept-Encoding";
  private static final String CONTENT_ENCODING = "Content-Encoding";
  private static final String ENCODING_GZIP = "gzip";


  static boolean isAcceptEncoding() {
    String encodings = getRequest().getHeader( ACCEPT_ENCODING );
    return encodings != null && encodings.indexOf( ENCODING_GZIP ) != -1;
  }

  static PrintWriter getOutputWriter() throws IOException {
    OutputStreamWriter utf8Writer;
    OutputStream out = getResponse().getOutputStream();
    if( isAcceptEncoding() && getInitProps().isCompression() ) {
      GZIPOutputStream zipStream = new GZIPOutputStream( out );
      utf8Writer = new OutputStreamWriter( zipStream, HTML.CHARSET_NAME_UTF_8 );
      getResponse().setHeader( CONTENT_ENCODING, ENCODING_GZIP );
    } else {
      utf8Writer = new OutputStreamWriter( out, HTML.CHARSET_NAME_UTF_8 );
    }
    return new PrintWriter( utf8Writer, false );
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