/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.internal.util.StreamWritingUtil;
import org.eclipse.rwt.service.IServiceHandler;


/**
 *  <p>This class is used to deliver the concatenated Javascript libraries that
 *  are needed to run RWT on the client in one request.</p>
 *  <p>Depending on the 'Accept-Encoding' header of the request and the 
 *  configuration settings the content is either sent uncompressed or compressed. 
 *  The latter should be the normal case, since this reduces the size of the 
 *  content that has to be sent over the wire.</p>
 *  <p>As the filename of the delivered Javascript has a version postfix the
 *  browser is informed to store the Javascript in its cache.</p>
 */
public class JSLibraryServiceHandler implements IServiceHandler {

  public final static String HANDLER_ID = JSLibraryServiceHandler.class.getName();

  static final String EXPIRES_NEVER = "Sun, 17 Jan 2038 19:14:07 GMT";
  static final String REQUEST_PATTERN = "{0}?{1}={2}&hash={3}";
 
  private static final String EXPIRES = "Expires";
  
  public static String getRequestURL() {
    Object[] param = new Object[] { 
      RWT.getRequest().getServletPath().substring( 1 ),
      IServiceHandler.REQUEST_PARAM,
      JSLibraryServiceHandler.HANDLER_ID,
      RWTFactory.getJSLibraryConcatenator().getHashCode()
    };
    return MessageFormat.format( JSLibraryServiceHandler.REQUEST_PATTERN, param );
  }

  public void service() throws IOException, ServletException {
    setResponseHeaders();
    writeOutput();
  }

  private static void setResponseHeaders() {
    HttpServletResponse response = RWT.getResponse();
    response.setHeader( EXPIRES, EXPIRES_NEVER );
    response.setContentType( HTTP.CONTENT_TEXT_JAVASCRIPT );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
  }

  private static void writeOutput() throws IOException {
    ServletOutputStream out = RWT.getResponse().getOutputStream();
    StreamWritingUtil.writeBuffered( RWTFactory.getJSLibraryConcatenator().getUncompressed(), out );
  }
}
