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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.jstest.TestContribution;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.theme.QxAppearanceWriter;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.service.IServiceHandler;


@SuppressWarnings( "restriction" )
public class ClientResourcesServiceHandler implements IServiceHandler {

  private static final String PARAM_CONTRIBUTION = "contribution";
  private static final String PARAM_FILE = "file";
  private static final String APPEARANCE_NAME = "rap-appearance.js";
  private static final String JSON_PARSER_NAME = "json2.js";

  public static final String ID = "clientResources";

  private final TestContribution rwtContribution = new RWTContribution();

  public Map<String, TestContribution> getContributions() {
    return Activator.getContributions();
  }

  public void service() throws IOException, ServletException {
    HttpServletRequest request = RWT.getRequest();
    String fileParameter = request.getParameter( PARAM_FILE );
    String contributionParameter = request.getParameter( PARAM_CONTRIBUTION );
    if( fileParameter != null ) {
      if( APPEARANCE_NAME.equals( fileParameter ) ) {
        deliverAppearance();
      } else if( rwtContribution.getName().equals( contributionParameter ) ) {
        deliverResource( rwtContribution, fileParameter );
      } else {
        deliverResource( contributionParameter, fileParameter );
      }
    } else {
      deliverFilesList();
    }
  }

  private void deliverResource( String contributionName, String file ) throws IOException {
    boolean found = false;
    Map<String, TestContribution> contributions = getContributions();
    for( String name : contributions.keySet() ) {
      if( name.equals( contributionName ) ) {
        found = true;
        deliverResource( contributions.get( name ), file );
        break;
      }
    }
    if( !found ) {
      writeError( RWT.getResponse(), HttpServletResponse.SC_BAD_REQUEST, "Unknown contribution" );
    }
  }

  private void deliverFilesList() throws IOException {
    HttpServletResponse response = RWT.getResponse();
    response.setContentType( "text/javascript" );
    PrintWriter writer = response.getWriter();
    writer.write( "( function() {\n" );
    writeIncludeResource( writer, getContributions().get( "rwt-test" ), "/resource/TestSettings.js" );
    writeIncludeResources( writer, rwtContribution );
    writeIncludeResource( writer, ( TestContribution )null, APPEARANCE_NAME );
    writeIncludeResource( writer, rwtContribution, JSON_PARSER_NAME );
    writeIncludeResource( writer, getThemeLocation(), null );
    Collection<TestContribution> contributions = getContributions().values();
    for( TestContribution contribution : contributions ) {
      writeIncludeResources( writer, contribution );
    }
    writer.write( "} )();\n" );
  }

  private void deliverResource( TestContribution contribution, String resource )
    throws IOException
  {
    HttpServletResponse response = RWT.getResponse();
    response.setContentType( "text/javascript" );
    InputStream inputStream = contribution.getResourceAsStream( resource );
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

  private String getThemeLocation() {
    ThemeManager themeManager = RWTFactory.getThemeManager();
    Theme defaultTheme = themeManager.getTheme( ThemeManager.FALLBACK_THEME_ID );
    return defaultTheme.getRegisteredLocation();
  }

  private static void writeError( HttpServletResponse response, int statusCode, String message )
    throws IOException
  {
    response.setContentType( "text/html" );
    response.setStatus( statusCode );
    PrintWriter writer = response.getWriter();
    writer.write( "<html><h1>HTTP " + statusCode + "</h1><p>" + message + "</p></html>" );
  }

  private static void writeIncludeResources( PrintWriter writer, TestContribution contribution )
    throws IOException
  {
    String[] clientResources = contribution.getResources();
    for( String resource : clientResources ) {
      writeIncludeResource( writer, contribution, resource );
    }
  }

  private static void writeIncludeResource( PrintWriter writer,
                                            TestContribution contribution,
                                            String resource ) throws IOException
  {
    String location = getResourceLocation( contribution, resource );
    String hash = contribution == null ? "" : getResourceHash( contribution, resource );
    writeIncludeResource( writer, location, hash );
  }

  private static void writeIncludeResource( PrintWriter writer, String resource, String hash ) {
    writer.write( "document.write( '<script src=\"" );
    writer.write( resource );
    if( hash != null ) {
      writer.write( resource.contains( "?" ) ? "&" : "?" );
      writer.write( "nocache=" );
      writer.write( hash );
    }
    writer.write( "\" type=\"text/javascript\"></script>' );\n" );
  }

  private static String getResourceHash( TestContribution contribution, String resource )
    throws IOException
  {
    int hash = 0;
    char[] buffer = new char[ 8096 ];
    InputStream inputStream = contribution.getResourceAsStream( resource );
    if( inputStream != null ) {
      try {
        InputStreamReader reader = new InputStreamReader( inputStream, "UTF-8" );
        BufferedReader bufferedReader = new BufferedReader( reader );
        int read = bufferedReader.read( buffer );
        while( read != -1 ) {
          for( int i = 0; i < read; i++ ) {
            hash = 31 * hash + buffer[ i++ ];
          }
          read = bufferedReader.read( buffer );
        }
      } finally {
        inputStream.close();
      }
    }
    return Integer.toHexString( hash );
  }

  private static String getResourceLocation( TestContribution contribution, String resource )
  {
    StringBuilder url = new StringBuilder();
    url.append( RWT.getRequest().getContextPath() );
    url.append( RWT.getRequest().getServletPath() );
    url.append( '?' );
    url.append( REQUEST_PARAM );
    url.append( '=' );
    url.append( ID );
    url.append( '&' );
    url.append( PARAM_FILE );
    url.append( '=' );
    url.append( resource );
    if( contribution != null ) {
      url.append( '&' );
      url.append( PARAM_CONTRIBUTION );
      url.append( '=' );
      url.append( contribution.getName() );
    }
    return RWT.getResponse().encodeURL( url.toString() );
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
