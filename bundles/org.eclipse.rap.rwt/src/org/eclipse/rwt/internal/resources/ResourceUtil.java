/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.resources.IResourceManager;

public final class ResourceUtil {

  // TODO [rh] avoid passing around the same set of arguments again and again
  static byte[] read( String name,
                      String charset,
                      boolean compress,
                      IResourceManager resourceManager )
    throws IOException
  {
    byte[] result;
    if( charset != null ) {
      result = readText( name, charset, compress, resourceManager );
    } else {
      result = readBinary( name, resourceManager );
    }
    return result;
  }

  static byte[] read( InputStream is, String charset, boolean compress ) throws IOException {
    byte[] result;
    if( charset != null ) {
      result = readText( is, charset, compress );
    } else {
      result = readBinary( is );
    }
    return result;
  }

  static void write( File toWrite, byte[] content ) throws IOException {
    FileOutputStream fos = new FileOutputStream( toWrite );
    try {
      OutputStream out = new BufferedOutputStream( fos );
      try {
        out.write( content );
      } finally {
        out.close();
      }
    } finally {
      fos.close();
    }
    if( toWrite.getName().endsWith( "js" ) ) {
      RWTFactory.getJSLibraryConcatenator().appendJSLibrary( content );
    }
  }

  public static void useJsLibrary( String libraryName ) {
    ParamCheck.notNull( libraryName, "libraryName" );
    // TODO [rst] Add to concatenation buffer
  }

  private static byte[] readText( String name,
                                  String charset,
                                  boolean compress,
                                  IResourceManager resourceManager )
    throws IOException
  {
    // read resource
    InputStream is = openStream( name, resourceManager );
    byte[] result;
    try {
      result = readText( is, charset, compress );
    } finally {
      is.close();
    }
    return result;
  }

  static byte[] readText( InputStream is, String charset, boolean compress ) throws IOException {
    StringBuffer text = new StringBuffer();
    InputStreamReader reader = new InputStreamReader( is, charset );
    BufferedReader br = new BufferedReader( reader );
    char[] buffer = new char[ 8096 ];
    try {
      int readChars = br.read( buffer );
      while( readChars != -1 ) {
        text.append( buffer, 0, readChars );
        readChars = br.read( buffer );
      }
    } finally {
      br.close();
    }
    if( compress ) {
      compress( text );
    }
    return text.toString().getBytes( HTTP.CHARSET_UTF_8 );
  }

  private static byte[] readBinary( String name, IResourceManager resourceManager ) 
    throws IOException 
  {
    byte[] result;
    InputStream is = openStream( name, resourceManager );
    try {
      result = readBinary( is );
    } finally {
      is.close();
    }
    return result;
  }

  // TODO [rh] move to utility class (as is also used by Image)
  public static byte[] readBinary( InputStream stream ) throws IOException {
    ByteArrayOutputStream bufferedResult = new ByteArrayOutputStream();
    BufferedInputStream bufferedStream = new BufferedInputStream( stream );
    try {
      byte[] buffer = new byte[ 256 ];
      int read = bufferedStream.read( buffer );
      while( read != -1 ) {
        bufferedResult.write( buffer, 0, read );
        read = bufferedStream.read( buffer );
      }
    } finally {
      bufferedStream.close();
    }
    return bufferedResult.toByteArray();
  }

  private static InputStream openStream( String name, IResourceManager resourceManager ) 
    throws IOException 
  {
    ClassLoader loader = ResourceManagerImpl.class.getClassLoader();
    URL resource = loader.getResource( name );
    if( resource == null ) {
      resource = resourceManager.getResource( name );
    }
    if( resource == null ) {
      throw new IOException( "Resource to read not found: " + name );
    }
    URLConnection con = resource.openConnection();
    con.setUseCaches( false );
    return con.getInputStream();
  }

  static void compress( StringBuffer javaScript ) throws IOException {
    JSFile jsFile = new JSFile( javaScript.toString() );
    javaScript.setLength( 0 );
    javaScript.append( jsFile.compress() );
  }

  private ResourceUtil() {
    // prevent instantiation
  }
}
