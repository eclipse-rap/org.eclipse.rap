/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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

public final class ResourceUtil {

  private static final String ONE_LINE_COMMENT = "//";
  private static final String MULTI_LINE_COMMENT_START = "/*";
  private static final String MULTI_LINE_COMMENT_END = "*/";

  private static final String NEWLINE_MAC = "\r";
  private static final String NEWLINE_UNIX = "\n";
  private static final String NEWLINE_WIN = "\r\n";

  private static final char SINGLE_QUOTE = '\'';
  private static final char DOUBLE_QUOTE = '"';
  private static final char NO_QUOTE = '-';
  private static final char BACKSLASH = '\\';
  private static final char LINE_FEED = '\r';
  private static final char CARRIAGE_RETURN = '\n';

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

  public static void startJsConcatenation() {
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

    // Remove blanks from arithmetic/logic operations
    replace( javaScript, " = ", "=" );
    replace( javaScript, " == ", "==" );
    replace( javaScript, " === ", "===" );
    replace( javaScript, " + ", "+" );
    replace( javaScript, " - ", "-" );
    replace( javaScript, " * ", "*" );
    replace( javaScript, " / ", "/" );
    replace( javaScript, " > ", ">" );
    replace( javaScript, " < ", "<" );
    replace( javaScript, " <= ", "<=" );
    replace( javaScript, " >= ", ">=" );
    replace( javaScript, " != ", "!=" );
    replace( javaScript, " : ", ":" );
    replace( javaScript, " && ", "&&" );
    replace( javaScript, " || ", "||" );
    replace( javaScript, " ? ", "?" );

    // Always remove leading single blanks after removing blanks from
    // arithmetic/logic operations
    removeLeadingBlanks( javaScript );
    // Always remove multiple new lines after removing leading blanks
    removeMultipleNewLines( javaScript, NEWLINE_WIN );
    removeMultipleNewLines( javaScript, NEWLINE_UNIX );
    removeMultipleNewLines( javaScript, NEWLINE_MAC );

    // Remove blanks from brackets and some punctuation
    replace( javaScript, "( ", "(" );
    replace( javaScript, " )", ")" );
    replace( javaScript, "} ", "}" );
    replace( javaScript, " {", "{" );
    replace( javaScript, " }", "}" );
    replace( javaScript, "{ ", "{" );
    replace( javaScript, "[ ", "[" );
    replace( javaScript, " ]", "]" );
    replace( javaScript, ", ", "," );
    replace( javaScript, "; ", ";" );

    // Remove some new lines
    replace( javaScript, NEWLINE_WIN + "}", "}" );
    replace( javaScript, NEWLINE_UNIX + "}", "}" );
    replace( javaScript, NEWLINE_MAC + "}", "}" );

    replace( javaScript, NEWLINE_WIN + "{", "{" );
    replace( javaScript, NEWLINE_UNIX + "{", "{" );
    replace( javaScript, NEWLINE_MAC + "{", "{" );

    replace( javaScript, "}" + NEWLINE_WIN, "}" );
    replace( javaScript, "}" + NEWLINE_UNIX, "}" );
    replace( javaScript, "}" + NEWLINE_MAC, "}" );

    replace( javaScript, "{" + NEWLINE_WIN, "{" );
    replace( javaScript, "{" + NEWLINE_UNIX, "{" );
    replace( javaScript, "{" + NEWLINE_MAC, "{" );

    replace( javaScript, NEWLINE_WIN + "&&", "&&" );
    replace( javaScript, NEWLINE_UNIX + "&&", "&&" );
    replace( javaScript, NEWLINE_MAC + "&&", "&&" );

    replace( javaScript, NEWLINE_WIN + "||", "||" );
    replace( javaScript, NEWLINE_UNIX + "||", "||" );
    replace( javaScript, NEWLINE_MAC + "||", "||" );

    replace( javaScript, NEWLINE_WIN + "=", "=" );
    replace( javaScript, NEWLINE_UNIX + "=", "=" );
    replace( javaScript, NEWLINE_MAC + "=", "=" );

    replace( javaScript, NEWLINE_WIN + "+", "+" );
    replace( javaScript, NEWLINE_UNIX + "+", "+" );
    replace( javaScript, NEWLINE_MAC + "+", "+" );

    replace( javaScript, NEWLINE_WIN + "-", "-" );
    replace( javaScript, NEWLINE_UNIX + "-", "-" );
    replace( javaScript, NEWLINE_MAC + "-", "-" );

    replace( javaScript, NEWLINE_WIN + ":", ":" );
    replace( javaScript, NEWLINE_UNIX + ":", ":" );
    replace( javaScript, NEWLINE_MAC + ":", ":" );

    replace( javaScript, NEWLINE_WIN + "?", "?" );
    replace( javaScript, NEWLINE_UNIX + "?", "?" );
    replace( javaScript, NEWLINE_MAC + "?", "?" );

    replace( javaScript, "," + NEWLINE_WIN, "," );
    replace( javaScript, "," + NEWLINE_UNIX, "," );
    replace( javaScript, "," + NEWLINE_MAC, "," );

    replace( javaScript, ";" + NEWLINE_WIN, ";" );
    replace( javaScript, ";" + NEWLINE_UNIX, ";" );
    replace( javaScript, ";" + NEWLINE_MAC, ";" );

    replace( javaScript, ":" + NEWLINE_WIN, ":" );
    replace( javaScript, ":" + NEWLINE_UNIX, ":" );
    replace( javaScript, ":" + NEWLINE_MAC, ":" );
  }

  static void replace( final StringBuffer javaScript,
                       final String strToFind,
                       final String strToReplace )
  {
    int index = javaScript.indexOf( strToFind, 0 );
    while( index != -1 ) {
      if( !isInsideString( javaScript, index ) ) {
        javaScript.replace( index, index + strToFind.length(), strToReplace );
      } else {
        index += strToFind.length();
      }
      index = javaScript.indexOf( strToFind, index + strToReplace.length() );
    }
  }

  static void removeOneLineComments( final StringBuffer javaScript ) {
    // strip one-line comments
    int commentStart = javaScript.indexOf( ONE_LINE_COMMENT, 0 );
    while( commentStart != -1 ) {
      int lineEnd = nextNewLine( javaScript, commentStart );
      if( !isInsideString( javaScript, commentStart ) ) {
        javaScript.delete( commentStart, lineEnd );
      } else {
        commentStart += 2;
      }
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

  static void removeMultiLineComments( final StringBuffer javaScript ) {
    int index = javaScript.indexOf( MULTI_LINE_COMMENT_START );
    while( index != -1 ) {
      int end = javaScript.indexOf( MULTI_LINE_COMMENT_END );
      if( end != -1 ) {
        if( !isInsideString( javaScript, index ) ) {
          javaScript.delete( index, end + MULTI_LINE_COMMENT_END.length() );
        } else {
          index += 2;
        }
      }
      index = javaScript.indexOf( MULTI_LINE_COMMENT_START, index );
    }
  }

  static void removeMultipleBlanks( final StringBuffer javaScript ) {
    int index = javaScript.indexOf( "  " );
    while( index != -1 ) {
      if( !isInsideString( javaScript, index ) ) {
        javaScript.delete( index, index + 1 );
      } else {
        index += 1;
      }
      index = javaScript.indexOf( "  ", index );
    }
  }

  static void removeLeadingBlanks( final StringBuffer javaScript ) {
    int index = javaScript.indexOf( " " );
    while( index != -1 ) {
      char prev = '-';
      if( index > 0 ) {
        prev = javaScript.charAt( index - 1 );
      }
      if( prev == LINE_FEED || prev == CARRIAGE_RETURN || index == 0 ) {
        javaScript.delete( index, index + 1 );
      } else {
        index += 1;
      }
      index = javaScript.indexOf( " ", index );
    }
  }

  static void removeMultipleNewLines( final StringBuffer javaScript,
                                      final String newline ) {
    String doubleNewLine = newline + newline;
    int index = javaScript.indexOf( doubleNewLine );
    while( index != -1 ) {
      javaScript.delete( index, index + newline.length() );
      index = javaScript.indexOf( doubleNewLine, index );
    }
  }

  static boolean isInsideString( final StringBuffer javaScript,
                                 final int position )
  {
    String line = getLineAtPosition( javaScript, position );
    int pos = getPositionInLine( javaScript, position );
    char quoteChar = NO_QUOTE;
    char prevChar = NO_QUOTE;
    for( int i = 0; i < pos; i++ ) {
      char ch = line.charAt( i );
      if( ch == DOUBLE_QUOTE && quoteChar == NO_QUOTE ) {
        quoteChar = DOUBLE_QUOTE;
      } else if(    ch == DOUBLE_QUOTE
                 && quoteChar == DOUBLE_QUOTE
                 && prevChar != BACKSLASH )
      {
        quoteChar = NO_QUOTE;
      } else if( ch == SINGLE_QUOTE && quoteChar == NO_QUOTE ) {
        quoteChar = SINGLE_QUOTE;
      } else if(    ch == SINGLE_QUOTE
                 && quoteChar == SINGLE_QUOTE
                 && prevChar != BACKSLASH )
      {
        quoteChar = NO_QUOTE;
      }
      prevChar = ch;
    }

    return quoteChar != NO_QUOTE;
  }

  static String getLineAtPosition( final StringBuffer javaScript,
                                   final int position )
  {
    String line = "";
    if( position >= 0 && position < javaScript.length() ) {
      int start = position;
      int end = position;
      char ch = javaScript.charAt( start );
      while( ch != LINE_FEED && ch != CARRIAGE_RETURN && start > 0 ) {
        start--;
        ch = javaScript.charAt( start );
      }
      ch = javaScript.charAt( end );
      while(    ch != LINE_FEED
             && ch != CARRIAGE_RETURN
             && end < javaScript.length() )
      {
        ch = javaScript.charAt( end );
        end++;
      }
      line = javaScript.substring( start, end );
      if( line.startsWith( NEWLINE_MAC ) || line.startsWith( NEWLINE_UNIX ) ) {
        line = line.substring( 1 );
      }
      if( line.endsWith( NEWLINE_MAC ) || line.endsWith( NEWLINE_UNIX ) ) {
        line = line.substring( 0, line.length() - 1 );
      }
    }
    return line;
  }

  static int getPositionInLine( final StringBuffer javaScript,
                                final int position )
  {
    int pos = 0;
    if( position >= 0 && position < javaScript.length() ) {
      int start = position;
      char ch = javaScript.charAt( start );
      while( ch != LINE_FEED && ch != CARRIAGE_RETURN && start > 0 ) {
        start--;
        pos++;
        ch = javaScript.charAt( start );
      }
      if( ch == LINE_FEED || ch == CARRIAGE_RETURN ) {
        pos--;
      }
    }
    return pos;
  }

  private ResourceUtil() {
    // prevent instantiation
  }
}
