/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.swt.internal.widgets.displaykit.ClientResourcesAdapter;


public class RwtResourcesServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private String[] clientResources;

  @Override
  public void init() throws ServletException {
    clientResources = ClientResourcesAdapter.getRegisteredClientResources();
  }

  @Override
  protected void doGet( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException
  {
    response.setCharacterEncoding( "UTF-8" );
    String path = request.getPathInfo();
    if( path == null ) {
      writeError( response, HttpServletResponse.SC_BAD_REQUEST, "Invalid path" );
    } else if( path.equals( "/load-rwt-resources" ) ) {
      writeLoadRwtResources( response );
    } else if( path.startsWith( "/" ) ) {
      writeResource( response, path );
    } else {
      writeError( response, HttpServletResponse.SC_BAD_REQUEST, "Invalid path" );
    }
  }

  private void writeLoadRwtResources( HttpServletResponse response ) throws IOException {
    response.setContentType( "application/javascript" );
    PrintWriter writer = response.getWriter();
    writer.write( "( function() {\n" );
    writeIncludeFunction( writer );
    writeResourcesArray( writer );
    writeIncludeResourcesLoop( writer );
    writer.write( "} )();\n" );
  }

  private void writeIncludeFunction( PrintWriter writer ) {
    writer.write( "var include = function( src ) {\n" );
    writer.write(   "  document.write( '<script src=\"' + src + '\""
                  + " type=\"application/javascript\"></script>' );\n" );
    writer.write( "};\n" );
  }

  private void writeResourcesArray( PrintWriter writer ) {
    writer.write( "var clientResources = [" );
    for( int i = 0; i < clientResources.length; i++ ) {
      if( i != 0 ) {
        writer.write( ',' );
      }
      writer.write( '"' );
      writer.write( clientResources[ i ] );
      writer.write( '"' );
    }
    writer.write( "];\n" );
  }

  private void writeIncludeResourcesLoop( PrintWriter writer ) {
    writer.write( "var ts = new Date().getTime();" );
    writer.write( "for( var i = 0; i < clientResources.length; i++ ) {\n" );
    writer.write( "  include( './rwt-resources/' + clientResources[ i ] + '?nocache=' + ts );\n" );
    writer.write( "}\n" );
  }

  private void writeResource( HttpServletResponse response, String resourceName )
    throws IOException
  {
    InputStream inputStream = ClientResourcesAdapter.getResourceAsStream( resourceName );
    if( inputStream != null ) {
      try {
        response.setContentType( getContentTypeForResource( resourceName ) );
        PrintWriter writer = response.getWriter();
        copyContents( inputStream, writer );
      } finally {
        inputStream.close();
      }
    } else {
      writeError( response, HttpServletResponse.SC_NOT_FOUND, "Resource not found" );
    }
  }

  private void writeError( HttpServletResponse response, int statusCode, String message )
    throws IOException
  {
    response.setContentType( "text/html" );
    response.setStatus( statusCode );
    PrintWriter writer = response.getWriter();
    writer.write( "<html><h1>HTTP " + statusCode + "</h1><p>" + message + "</p></html>" );
  }

  private static String getContentTypeForResource( String resourceName ) {
    String result = null;
    if( resourceName.endsWith( ".js" ) ) {
      result = "application/javascript";
    } else if( resourceName.endsWith( ".html" ) ) {
      result = "text/html";
    } else if( resourceName.endsWith( ".gif" ) ) {
      result = "image/gif";
    } else {
      System.err.println( resourceName );
    }
    return result;
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
