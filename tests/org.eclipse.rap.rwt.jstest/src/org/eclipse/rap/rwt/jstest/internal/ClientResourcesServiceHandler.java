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
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.jstest.TestContribution;
import org.eclipse.rap.rwt.service.ServiceHandler;


public class ClientResourcesServiceHandler implements ServiceHandler {

  private static final String PARAM_CONTRIBUTION = "contribution";
  private static final String PARAM_FILE = "file";
  private static final String PARAM_NOCACHE = "nocache";
  public static final String ID = "clientResources";

  public Map<String, TestContribution> getContributions() {
    return Activator.getContributions();
  }

  public void service( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    String fileParameter = request.getParameter( PARAM_FILE );
    String contributionParameter = request.getParameter( PARAM_CONTRIBUTION );
    if( fileParameter != null ) {
      deliverResource( response, contributionParameter, fileParameter );
    } else {
      deliverFilesList( response );
    }
  }

  private void deliverResource( HttpServletResponse response, String contributionName, String file )
    throws IOException
  {
    TestContribution contribution = getContributions().get( contributionName );
    if( contribution != null ) {
      deliverResource( response, contribution, file );
    } else {
      writeError( response, HttpServletResponse.SC_BAD_REQUEST, "Unknown contribution" );
    }
  }

  private void deliverFilesList( HttpServletResponse response ) throws IOException {
    response.setContentType( "text/javascript" );
    response.setCharacterEncoding( "UTF-8" );
    PrintWriter writer = response.getWriter();
    writer.write( "( function() {\n" );
    Collection<TestContribution> contributions = getContributions().values();
    for( TestContribution contribution : contributions ) {
      writeIncludeResources( writer, contribution );
    }
    writer.write( "} )();\n" );
  }

  private void deliverResource( HttpServletResponse response,
                                TestContribution contribution,
                                String resource ) throws IOException
  {
    response.setContentType( "text/javascript" );
    response.setCharacterEncoding( "UTF-8" );
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
    writeIncludeResource( writer, location );
  }

  private static void writeIncludeResource( PrintWriter writer, String resource ) {
    writer.write( "document.write( '<script src=\"" );
    writer.write( resource );
    writer.write( "\" type=\"text/javascript\"></script>' );\n" );
  }

  private static String getResourceLocation( TestContribution contribution, String resource )
    throws IOException
  {
    StringBuilder url = new StringBuilder( RWT.getServiceManager().getServiceHandlerUrl( ID ) );
    appendParameter( url, PARAM_CONTRIBUTION, contribution.getName() );
    appendParameter( url, PARAM_FILE, resource );
    appendParameter( url, PARAM_NOCACHE, getResourceHash( contribution, resource ) );
    return url.toString();
  }

  private static void appendParameter( StringBuilder stringBuilder, String name, String value ) {
    stringBuilder.append( '&' );
    stringBuilder.append( name );
    stringBuilder.append( '=' );
    stringBuilder.append( value );
  }

  private static String getResourceHash( TestContribution contribution, String resource )
    throws IOException
  {
    int hash = 0;
    InputStream inputStream = contribution.getResourceAsStream( resource );
    if( inputStream != null ) {
      try {
        hash = getHashCode( inputStream );
      } finally {
        inputStream.close();
      }
    }
    return Integer.toHexString( hash );
  }

  private static int getHashCode( InputStream inputStream ) throws IOException {
    int hash = 0;
    byte[] buffer = new byte[ 8096 ];
    int read = inputStream.read( buffer );
    while( read != -1 ) {
      for( int i = 0; i < read; i++ ) {
        hash = 31 * hash + buffer[ i++ ];
      }
      read = inputStream.read( buffer );
    }
    return hash;
  }

  private static void copyContents( InputStream inputStream, PrintWriter writer )
    throws IOException
  {
    try {
      Reader reader = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
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
