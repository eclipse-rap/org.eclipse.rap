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

  static int[] read( final String name,
                     final String charset,
                     final boolean compress )
    throws IOException
  {
    int[] result;
    if( charset != null ) {
      result = readText( name, charset, compress );
    } else {
      result = readBinary( name );
    }
    return result;
  }

  static int[] read( final InputStream is,
                     final String charset,
                     final boolean compress )
    throws IOException
  {
    int[] result;
    if( charset != null ) {
      result = readText( is, charset, compress );
    } else {
      result = readBinary( is );
    }
    return result;
  }

  static void write( final File toWrite, final int[] content ) throws IOException {
    FileOutputStream fos = new FileOutputStream( toWrite );
    try {
      OutputStream out = new BufferedOutputStream( fos );
      try {
        for( int i = 0; i < content.length; i++ ) {
          out.write( content[ i ] );
        }
      } finally {
        out.close();
      }
    } finally {
      fos.close();
    }
    RWTFactory.getJSLibraryConcatenator().appendJSLibrary( toWrite, content );
  }

  public static void useJsLibrary( String libraryName ) {
    ParamCheck.notNull( libraryName, "libraryName" );
    // TODO [rst] Add to concatenation buffer
  }

  private static int[] readText( final String name,
                                 final String charset,
                                 final boolean compress )
    throws IOException
  {
    // read resource
    InputStream is = openStream( name );
    int[] result;
    try {
      result = readText( is, charset, compress );
    } finally {
      is.close();
    }
    return result;
  }

  static int[] readText( final InputStream is,
                         final String charset,
                         final boolean compress )
    throws UnsupportedEncodingException, IOException
  {
    StringBuffer buffer = new StringBuffer();
    InputStreamReader reader = new InputStreamReader( is, charset );
    BufferedReader br = new BufferedReader( reader );
    try {
      int character = br.read();
      while( character != -1 ) {
        buffer.append( ( char )character );
        character = br.read();
      }
    } finally {
      br.close();
    }
    // compress (JavaScript-) buffer if requested
    if( compress ) {
      compress( buffer );
    }
    // write just read resource to byte array stream
    byte[] bytes;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      OutputStreamWriter osw = new OutputStreamWriter( baos, HTTP.CHARSET_UTF_8 );
      try {
        osw.write( buffer.toString() );
        osw.flush();
      } finally {
        osw.close();
      }
      bytes = baos.toByteArray();
    } finally {
      baos.close();
    }
    // convert byte[] to int[] and return
    int[] result = new int[ bytes.length ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = ( bytes[ i ] & 0x0ff );
    }
    return result;
  }

  private static int[] readBinary( final String name ) throws IOException {
    int[] result;
    InputStream is = openStream( name );
    try {
      result = readBinary( is );
    } finally {
      is.close();
    }
    return result;
  }

  static int[] readBinary( final InputStream stream ) throws IOException {
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
    byte[] bytes = bufferedResult.toByteArray();
    int[] result = new int[ bytes.length ];
    for( int i = 0; i < bytes.length; i++ ) {
      result[ i ] = bytes[ i ];
    }
    return result;
  }

  private static InputStream openStream( final String name ) throws IOException {
    ClassLoader loader = ResourceManagerImpl.class.getClassLoader();
    URL resource = loader.getResource( name );
    if( resource == null ) {
      IResourceManager manager = ResourceManagerImpl.getInstance();
      resource = manager.getResource( name );
    }
    if( resource == null ) {
      throw new IOException( "Resource to read not found: " + name );
    }
    URLConnection con = resource.openConnection();
    con.setUseCaches( false );
    return con.getInputStream();
  }

  static void compress( final StringBuffer javaScript ) throws IOException {
    JSFile jsFile = new JSFile( javaScript.toString() );
    javaScript.setLength( 0 );
    javaScript.append( jsFile.compress() );
  }

  private ResourceUtil() {
    // prevent instantiation
  }
}
