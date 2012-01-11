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
package org.eclipse.rap.rwt.jstest.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.theme.QxAppearanceWriter;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.swt.internal.widgets.displaykit.ClientResourcesAdapter;


@SuppressWarnings( "restriction" )
public class ClientResourcesServiceHandler implements IServiceHandler {

  private static final String APPEARANCE_NAME = "rap-appearance.js";
  private static final String JSON_PARSER_NAME = "rap-json2.js";
  public static final String ID = "clientResources";

  public void service() throws IOException, ServletException {
    HttpServletRequest request = RWT.getRequest();
    String file = request.getParameter( "file" );
    if( file != null ) {
      if( APPEARANCE_NAME.equals( file ) ) {
        deliverAppearance();
      } else if( JSON_PARSER_NAME.equals( file ) ) {
        deliverResource( "json2.js" );
      } else {
        deliverResource( file );
      }
    } else {
      deliverFilesList();
    }
  }

  private void deliverFilesList() throws IOException {
    HttpServletResponse response = RWT.getResponse();
    response.setContentType( "text/javascript" );
    PrintWriter writer = response.getWriter();
    writer.write( "( function() {\n" );
    String[] clientResources = ClientResourcesAdapter.getRegisteredClientResources();
    for( String resource : clientResources ) {
      writeIncludeResource( writer, getResourceLocation( resource ), true );
    }
    writeIncludeResource( writer, getResourceLocation( APPEARANCE_NAME ), false );
    writeIncludeResource( writer, getResourceLocation( JSON_PARSER_NAME ), false );
    writeIncludeResource( writer, getThemeLocation(), false );
    writer.write( "} )();\n" );
  }

  private void deliverResource( String resourceName ) throws IOException {
    HttpServletResponse response = RWT.getResponse();
    response.setContentType( "text/javascript" );
    InputStream inputStream = ClientResourcesAdapter.getResourceAsStream( resourceName );
    if( inputStream != null ) {
      try {
        PrintWriter writer = response.getWriter();
        copyContents( inputStream, writer );
      } finally {
        inputStream.close();
      }
    } else {
      writeError( response, HttpServletResponse.SC_NOT_FOUND, "Resource not found" );
    }
  }

  private void deliverAppearance() throws IOException {
    HttpServletResponse response = RWT.getResponse();
    response.setContentType( "text/javascript" );
    ThemeManager themeManager = RWTFactory.getThemeManager();
    List<String> customAppearances = themeManager.getAppearances();
    String appearanceCode = QxAppearanceWriter.createQxAppearanceTheme( customAppearances );
    PrintWriter writer = response.getWriter();
    writer.write( appearanceCode );
  }

  private String getResourceLocation( String resource ) {
     StringBuilder url = new StringBuilder();
     url.append( RWT.getRequest().getContextPath() );
     url.append( RWT.getRequest().getServletPath() );
     url.append( '?' );
     url.append( REQUEST_PARAM );
     url.append( '=' );
     url.append( ID );
     url.append( "&file=" );
     url.append( resource );
     return RWT.getResponse().encodeURL( url.toString() );
  }

  private String getThemeLocation() {
    ThemeManager themeManager = RWTFactory.getThemeManager();
    Theme defaultTheme = themeManager.getTheme( ThemeManager.FALLBACK_THEME_ID );
    return defaultTheme.getRegisteredLocation();
  }

  private void writeError( HttpServletResponse response, int statusCode, String message )
    throws IOException
  {
    response.setContentType( "text/html" );
    response.setStatus( statusCode );
    PrintWriter writer = response.getWriter();
    writer.write( "<html><h1>HTTP " + statusCode + "</h1><p>" + message + "</p></html>" );
  }

  private static void writeIncludeResource( PrintWriter writer, String resource, boolean nocache ) {
    writer.write( "document.write( '<script src=\"" );
    writer.write( resource );
    if( nocache ) {
      writer.write( resource.contains( "?" ) ? "&" : "?" );
      writer.write( "nocache=" );
      writer.write( Long.toString( System.currentTimeMillis() ) );
    }
    writer.write( "\" type=\"text/javascript\"></script>' );\n" );
  }

  private static void copyContents( InputStream inputStream, PrintWriter writer )
    throws IOException
  {
    try {
      BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
      char[] buffer = new char[ 8192 ];
      int count = reader.read( buffer );
      while( count != -1 ) {
        writer.write( buffer, 0, count );
        count = reader.read( buffer );
      }
    } catch( UnsupportedEncodingException unexpected ) {
      throw new RuntimeException( unexpected );
    }
  }

}
