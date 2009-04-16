/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
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
import java.text.MessageFormat;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.resources.JsConcatenator;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.IServiceHandler;

/**
 *  <p>This class is used to deliver the concatenated javascript libraries that
 *  are needed to run RWT on the client site in one request.</p>
 *  <p>Depending on the 'Accept-Encoding' header of the request the content
 *  is either sent uncompressed or compressed. The latter should be the normal
 *  case, since this reduces the size of the content that has to
 *  be sent over the wire.</p>
 *  <p>As the filename of the delivered javascript has a version postfix the
 *  browser is informed to store the javascript in its cache.</p>
 */
public class JSLibraryServiceHandler implements IServiceHandler {

  public static final String HANDLER_ID
    = JSLibraryServiceHandler.class.getName();
  static final String EXPIRES_NEVER = "Sun, 17 Jan 2038 19:14:07 GMT";

  private static final String REQUEST_PATTERN = "{0}?{1}={2}&hash={3}";
  private static String hashCode;
  private static byte[] unCompressed;
  private static byte[] compressed; 

  public static String getRequestURL() throws IOException {
    initializeOutput();
    Object[] param = new Object[] { 
      RWT.getRequest().getServletPath().substring( 1 ),
      IServiceHandler.REQUEST_PARAM,
      HANDLER_ID,
      hashCode
    };
    return MessageFormat.format( REQUEST_PATTERN, param );
  }
  
  public void service() throws IOException, ServletException {
    initializeOutput();
    HttpServletResponse response = RWT.getResponse();
    response.setHeader( HTML.CONTENT_TYPE, HTML.CONTENT_TEXT_JAVASCRIPT );
    response.setHeader( HTML.EXPIRES, EXPIRES_NEVER );
    if( isAcceptEncoding() ) {
      writeCompressedOutput();
    } else {
      writeUnCompressedOutput();
    }
  }

  private static void writeCompressedOutput() throws IOException {
    RWT.getResponse().setHeader( HTML.CONTENT_ENCODING, HTML.ENCODING_GZIP );
    HttpServletResponse response = RWT.getResponse();
    OutputStream out = new BufferedOutputStream( response.getOutputStream() );
    write( out, compressed );
  }

  private static void writeUnCompressedOutput() throws IOException {
    HttpServletResponse response = RWT.getResponse();
    OutputStream out = new BufferedOutputStream( response.getOutputStream() );
    write( out, unCompressed );
  }
  
  private static void write( final OutputStream out, final byte[] content )
    throws IOException
  {
    for( int i = 0; i < content.length; i++ ) {
      out.write( content[ i ] );
    }
    out.flush();
  }

  private static void initializeOutput()
    throws IOException
  {
    // Note [fappel]: We do not close all streams or writers, since this is 
    //                not so crucial here as we only do in-memory opperations.
    synchronized( JSLibraryServiceHandler.class ) {
      if( unCompressed == null ) {
        String content = getJsConcatenator().getContent();
        unCompressed = content.getBytes( HTML.CHARSET_NAME_UTF_8 );
        hashCode = "H" + content.hashCode();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipStream = new GZIPOutputStream( baos );
        write( gzipStream, unCompressed );
        gzipStream.close();
        compressed = baos.toByteArray();
      }
    }
  }
  
  private static boolean isAcceptEncoding() {
    String encodings = RWT.getRequest().getHeader( HTML.ACCEPT_ENCODING );
    return encodings != null && encodings.indexOf( HTML.ENCODING_GZIP ) != -1;
  }
  
  private static JsConcatenator getJsConcatenator() {
    IResourceManager manager = ResourceManager.getInstance();
    Adaptable adaptable = ( Adaptable )manager;
    Object adapter = adaptable.getAdapter( JsConcatenator.class );
    return ( JsConcatenator )adapter;
  }
}
