/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Utility class to provide commonly used encoding methods.
 */
public final class EncodingUtil {


  private static final Pattern DOUBLE_HYPHEN_PATTERN = Pattern.compile( "--" );
  private static final String UNIX_NEWLINE = "\\n";
  private static final String NBSP = "&nbsp;";

  /**
   * Escapes all double quote and backslash characters in the given input
   * string.
   *
   * @param input the string to process
   * @return a copy of the input string with all double quotes and backslashes
   *         replaced
   */
  public static String escapeDoubleQuoted( final String input ) {
    StringBuffer resultBuffer = new StringBuffer();
    int length = input.length();
    for( int i = 0; i < length; i++ ) {
      char ch = input.charAt( i );
      if( ch == '"' || ch == '\\' ) {
        resultBuffer.append( '\\' );
      }
      resultBuffer.append( ch );
    }
    return resultBuffer.toString();
  }

  /**
   * Escapes all leading and trailing spaces in the given input string.
   *
   * @param input the string to process
   * @return a copy of the input string with all leading and trailing spaces
   * replaced
   */
  public static String escapeLeadingTrailingSpaces( final String input ) {
    StringBuffer buffer = new StringBuffer();
    int beginIndex = 0;
    int endIndex = input.length();
    while( beginIndex < input.length() && input.charAt( beginIndex ) == ' ' ) {
      beginIndex++;
      buffer.append( NBSP );
    }
    while( endIndex > beginIndex && input.charAt( endIndex - 1 ) == ' ' ) {
      endIndex--;
    }
    buffer.append( input.substring( beginIndex, endIndex ) );
    int endCount = input.length() - endIndex;
    for( int i = 0; i < endCount; i++ ) {
      buffer.append( NBSP );
    }
    return buffer.toString();
  }

  /**
   * Replaces all newline characters in the specified input string with
   * <code>\n</code>. All common newline characters are replaced (Unix,
   * Windows, and MacOS).
   *
   * @param input the string to process
   * @return a copy of the input string with all newline characters replaced
   */
  public static String replaceNewLines( final String input ) {
    return replaceNewLines( input, UNIX_NEWLINE );
  }

  /**
   * Replaces all newline characters in the specified input string with the
   * given replacement string. All common newline characters are replaced (Unix,
   * Windows, and MacOS).
   *
   * @param input the string to process
   * @param replacement the string to replace line feeds with.
   * @return a copy of the input string with all newline characters replaced
   */
  public static String replaceNewLines( final String input,
                                        final String replacement )
  {
    StringBuffer resultBuffer = new StringBuffer();
    int length = input.length();
    int i = 0;
    while( i < length ) {
      char ch = input.charAt( i );
      if( ch == '\n' ) {
        resultBuffer.append( replacement );
      } else if( ch == '\r' ) {
        if( i + 1 < length ) {
          char next = input.charAt( i + 1 );
          if( ch == '\r' && next == '\n' ) {
            i++;
          }
        }
        resultBuffer.append( replacement );
      } else {
        resultBuffer.append( ch );
      }
      i++;
    }
    return resultBuffer.toString();
  }

  /**
   * Replaces white spaces in the specified input string with &amp;nbsp;.
   * For correct word wrapping, the last white space in a sequence of white
   * spaces is not replaced, if there is a different character following.
   * A single white space between words is not replaced whereas a single
   * leading white space is replaced.
   *
   * @param input the string to process
   * @return a copy of the input string with white spaces replaced
   */
  public static String replaceWhiteSpaces( final String input ) {
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < input.length(); i++ ) {
      if( input.charAt( i ) == ' ' ) {
        buffer.append( NBSP );
      } else {
        // Index should be greater then 1 for the case when the string begin
        // with single white space.
        if( i > 1 ){
          // Replaces back with ' ' the single white space between words
          // or the last white space in a white spaces sequence.
          if( input.charAt( i - 1 ) == ' ' ) {
            int start = buffer.length() - NBSP.length();
            buffer.replace( start, buffer.length(), " " );
          }
        }
        buffer.append( input.charAt( i ) );
      }
    }
    return buffer.toString();
  }

  public static String[] splitNewLines( final String input ) {
    int length = input.length();
    List resultList = new ArrayList();
    int start = 0;
    char last = 0;
    for( int i = 0; i < length; i++ ) {
      char ch = input.charAt( i );
      if( ch == '\n' ) {
        if( last != '\r' ) {
          resultList.add( input.substring( start, i ) );
        }
        start = i + 1;
      } else if( ch == '\r' ) {
        resultList.add( input.substring( start, i ) );
        start = i + 1;
      }
      last = ch;
    }
    resultList.add( input.substring( start, length ) );
    String[] result = new String[ resultList.size() ];
    resultList.toArray( result );
    return result;
  }

  public static String encodeHTMLEntities( final String text ) {
    String result = Entities.HTML40.escape( text );
    // Encode double-hyphens because they are not allowed inside comments
    Matcher matcher = EncodingUtil.DOUBLE_HYPHEN_PATTERN.matcher( result );
    result = matcher.replaceAll( "&#045;&#045;" );
    return result;
  }

  // Escape unicode characters \u2028 and \u2029 - see bug 304364
  public static String removeNonDisplayableChars( final String text ) {
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < text.length(); i++ ) {
      char ch = text.charAt( i );
      if( !isNonDisplayableChar( ch ) ) {
        buffer.append( ch );
      } 
    }
    return buffer.toString();
  }
  
  public static boolean isNonDisplayableChar( char ch ) {
    return ch == 0x2028 || ch == 0x2029;
  }

  private EncodingUtil() {
    // prevent instantiation
  }
}
