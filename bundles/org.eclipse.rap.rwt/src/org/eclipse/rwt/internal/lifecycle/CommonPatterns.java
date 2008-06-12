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
}
