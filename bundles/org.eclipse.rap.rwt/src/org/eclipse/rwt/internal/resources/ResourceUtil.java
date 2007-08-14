/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.resources.IResourceManager;


/**
 * <p>Utility class used by <code>org.eclipse.rap.engine.util.ResourceManager</code> 
 * to read (including optional compression) and write resources.</p>
 */
final class ResourceUtil {
  
  private static final String ONE_LINE_COMMENT = "//";
  private static final String MULTI_LINE_COMMENT_START = "/*";
  private static final String MULTI_LINE_COMMENT_END = "*/";
  
  private static final String NEWLINE_MAC = "\r";
  private static final String NEWLINE_UNIX = "\n";
  private static final String NEWLINE_WIN = "\r\n";
  private static ByteArrayOutputStream jsConcatenationBuffer = null;


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
  
  static void write( final File toWrite, final int[] content )
    throws IOException
  {
    FileOutputStream fos = new FileOutputStream( toWrite );
    try {
      OutputStream out = new BufferedOutputStream( fos );
      try {
        for( int i = 0; i < content.length; i++ ) {
          out.write( content[ i ] );
          if(    jsConcatenationBuffer != null 
              && toWrite.getName().endsWith( "js" ) )
          {
            jsConcatenationBuffer.write( content[ i ] );
            if( i == content.length - 1 ) {
              jsConcatenationBuffer.write( '\n' );
            }
          }
        }
      } finally {
        out.close();
      }
    } finally {
      fos.close();
    }
  }
  
  static void startJsConcatenation() {
    jsConcatenationBuffer = new ByteArrayOutputStream();
  }
 
  static String getJsConcatenationContentAsString() {
    String result = "";
    try {
      result = jsConcatenationBuffer.toString( HTML.CHARSET_NAME_UTF_8 );
    } catch( UnsupportedEncodingException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    jsConcatenationBuffer = null;
    return result;
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

  /** <p>Reads the resource from the given <code>is</code> which must be encoded 
   * with the given <code>charset</code> and optionally compresses it.</p> 
   * <p>Compression only makes sense for JavaScript content.</p>
   * <p>The resulting <code>int[]</code>  represents  the content which is 
   * <strong>always</strong> <code>UTF-8</code>-encoded.</p> */
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
      OutputStreamWriter osw 
        = new OutputStreamWriter( baos, HTML.CHARSET_NAME_UTF_8 );
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
  
  private static int[] readBinary( final String name ) 
    throws IOException
  {
    int[] result;
    InputStream is = openStream( name );
    try {
      result = readBinary( is );
    } finally {
      is.close();
    }
    return result;
  }

  static int[] readBinary( final InputStream is ) throws IOException {
    int[] result;
    BufferedInputStream bis = new BufferedInputStream( is );
    try {
      bis.mark( Integer.MAX_VALUE );
      result = new int[ getAvailableCount( bis ) ];
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = bis.read();
      }
    } finally {
      bis.close();
    }
    return result;
  }
  
  /** <p>implementation since InputStream.available somehow did not work 
   *  properly...</p> */
  private static int getAvailableCount( final BufferedInputStream bis ) 
    throws IOException 
  {
    int result = 0;
    int streamElement = 0;
    while( streamElement != -1 ) {
      streamElement = bis.read();
      if( streamElement != -1 ) {
        result++;
      }
    }
    bis.reset();
    return result;
  }
  
  private static InputStream openStream( final String name ) 
    throws IOException 
  {
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
    InputStream result = con.getInputStream();
    return result;
  }
  
  static void compress( final StringBuffer javaScript ) {
    removeOneLineComments( javaScript );
    removeMultiLineComments( javaScript );
    removeMultipleBlanks( javaScript );
  }

  /** removes everything after the first occurrence of "//" until the one of the 
   * line. */
  private static void removeOneLineComments( final StringBuffer javaScript ) {
    // strip one-line comments
    int commentStart = javaScript.indexOf( ONE_LINE_COMMENT, 0 );
    while( commentStart != -1 ) {
      int lineEnd = nextNewLine( javaScript, commentStart );
      javaScript.delete( commentStart, lineEnd );
      commentStart = javaScript.indexOf( ONE_LINE_COMMENT, commentStart );
    }
  }
  
  static int nextNewLine( final StringBuffer buffer, final int currentPos ) {
    int result = buffer.indexOf( NEWLINE_WIN, currentPos );
    if( result == -1 ) {
      result = buffer.indexOf( NEWLINE_UNIX, currentPos );
    }
    if( result == -1 ) {
      result = buffer.indexOf( NEWLINE_MAC, currentPos );
    }
    if( result == -1 ) {
      result = buffer.length();
    }
    return result;
  }

  /** cuts off all blocks between starting comment marks ( "/*" ) and ending,
   * comment marks (as in the end of this comment) from the passed String. */
  private static void removeMultiLineComments( final StringBuffer javaScript ) {
    int index = javaScript.indexOf( MULTI_LINE_COMMENT_START );
    while( index != -1 ) {
      int end = javaScript.indexOf( MULTI_LINE_COMMENT_END );
      if( end != -1 ) {
        javaScript.delete( index, end + MULTI_LINE_COMMENT_END.length() );
      }
      index = javaScript.indexOf( MULTI_LINE_COMMENT_START, index );
    }
  }
  
   /** replaces all occurences of multiple blanks with only one blank in
    * the passed String. */
  private static void removeMultipleBlanks( final StringBuffer javaScript ) {
    int index = javaScript.indexOf( "  " );
    while( index != -1 ) {
      javaScript.delete( index, index + 1 );
      index = javaScript.indexOf( "  ", index );
    }
  }
  
  private ResourceUtil() {
    // prevent instantiation
  }
}
