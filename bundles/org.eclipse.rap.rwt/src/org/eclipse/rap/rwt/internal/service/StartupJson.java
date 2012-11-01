/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.textsize.MeasurementUtil;
import org.eclipse.rap.rwt.internal.theme.JsonValue;
import org.eclipse.rap.rwt.internal.util.HTTP;


public class StartupJson {

  private static final String DISPLAY_TYPE = "rwt.widgets.Display";
  private static final String PROPERTY_URL = "url";

  private StartupJson() {
    // prevent instantiation
  }

  static void send() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    setResponseHeaders( response );
    PrintWriter writer = response.getWriter();
    writer.write( get() );
  }

  static String get() {
    ProtocolMessageWriter writer = new ProtocolMessageWriter();
    appendCreateDisplay( "w1", writer );
    MeasurementUtil.appendStartupTextSizeProbe( writer );
    return writer.createMessage();
  }

  private static void setResponseHeaders( HttpServletResponse response ) {
    response.setContentType( HTTP.CONTENT_TYPE_JSON );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
    response.addHeader( "Cache-Control", "max-age=0, no-cache, must-revalidate, no-store" );
  }

  private static void appendCreateDisplay( String id, ProtocolMessageWriter writer ) {
    writer.appendCreate( id, DISPLAY_TYPE );
    writer.appendHead( PROPERTY_URL, JsonValue.valueOf( getUrl() ) );
  }

  private static String getUrl() {
    HttpServletRequest request = ContextProvider.getRequest();
    String url = request.getServletPath().substring( 1 );
    return ContextProvider.getResponse().encodeURL( url );
  }
}
