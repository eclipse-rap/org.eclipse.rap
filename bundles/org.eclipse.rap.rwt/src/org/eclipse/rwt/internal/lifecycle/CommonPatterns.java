/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Utility class to provide common pattern matching and replacement methods.
 */
public final class CommonPatterns {

  private CommonPatterns() {
    // prevent instantiation
  }

  /**
   * Pattern that matches line feeds on Windows, UNIX, and MacOS platform.
   */
  public static final Pattern NEWLINE_PATTERN
    = Pattern.compile( "\\r\\n|\\r|\\n" );

  /**
   * String to replace line feed matches with <code>\n</code>.
   */
  private static final String NEWLINE_REPLACEMENT = "\\\\n";

  /**
   * Pattern for escaping double quoted strings, matches <code>&quot;</code>
   * and <code>\</code>.
   */
  private static final Pattern DOUBLE_QUOTE_PATTERN
    = Pattern.compile( "(\"|\\\\)" );

  /**
   * Replacement string that prepends all matches with <code>\</code>.
   */
  private static final String DOUBLE_QUOTE_REPLACEMENT = "\\\\$1";

  /**
   * Replacement string that is used for all leading and trailing spaces.
   */
  private static final String LEADING_TRAILING_SPACES_REPLACEMENT = "&nbsp;";

  private static final String SPACES_REPLACEMENT = "&nbsp;";

  /**
   * Escapes all double quote and backslash characters in the given input
   * string.
   *
   * @param input the string to process
   * @return a copy of the input string with all double quotes and backslashes
   *         replaced
   */
  public static String escapeDoubleQuoted( final String input ) {
    Matcher matcher = DOUBLE_QUOTE_PATTERN.matcher( input );
    return matcher.replaceAll( DOUBLE_QUOTE_REPLACEMENT );
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
      buffer.append( LEADING_TRAILING_SPACES_REPLACEMENT );
    }
    while( endIndex > beginIndex && input.charAt( endIndex - 1 ) == ' ' ) {
      endIndex--;
    }
    buffer.append( input.substring( beginIndex, endIndex ) );
    int endCount = input.length() - endIndex;
    for( int i = 0; i < endCount; i++ ) {
      buffer.append( LEADING_TRAILING_SPACES_REPLACEMENT );
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
    return replaceNewLines( input, NEWLINE_REPLACEMENT );
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
    return NEWLINE_PATTERN.matcher( input  ).replaceAll( replacement );
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
        buffer.append( SPACES_REPLACEMENT );
      } else {
        // Index should be greater then 1 for the case when the string begin
        // with single white space.
        if( i > 1 ){
          // Replaces back with ' ' the single white space between words
          // or the last white space in a white spaces sequence.
          if( input.charAt( i - 1 ) == ' ' ) {
            int start = buffer.length() - SPACES_REPLACEMENT.length();
            buffer.replace( start, buffer.length(), " " );
          }
        }
        buffer.append( input.charAt( i ) );
      }      
    }
    return buffer.toString();
  }
}
