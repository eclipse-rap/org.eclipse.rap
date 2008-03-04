/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AppearancesUtil {

  private static final Pattern PATTERN_REPLACE
    = Pattern.compile( "THEME_VALUE\\(\\s*\"(.*?)\"\\s*\\)" );

  public static String readAppearanceFile( final InputStream inStream )
    throws IOException
  {
    String result;
    StringBuffer sb = new StringBuffer();
    InputStreamReader reader = new InputStreamReader( inStream, "UTF-8" );
    BufferedReader br = new BufferedReader( reader );
    for( int i = 0; i < 100; i++ ) {
      int character = br.read();
      while( character != -1 ) {
        sb.append( ( char )character );
        character = br.read();
      }
    }
    result = stripTemplate( sb.toString() );
    return result;
  }

  /**
   * Replaces all THEME_VALUE() macros in a given template with the actual
   * values from the specified theme.
   *
   * @param template the template string that contains the macros to replace
   * @param theme the theme to get the values from
   * @return the template string with all macros replaced
   */
  static String substituteMacros( final String template, final Theme theme ) {
    Matcher matcher = PATTERN_REPLACE.matcher( template );
    StringBuffer sb = new StringBuffer();
    while( matcher.find() ) {
      String key = matcher.group( 1 );
      QxType result = theme.getValue( key );
      String repl;
      if( result instanceof QxBoolean ) {
        QxBoolean bool = ( QxBoolean )result;
        repl = String.valueOf( bool.value );
      } else if( result instanceof QxDimension ) {
        QxDimension dim = ( QxDimension )result;
        repl = String.valueOf( dim.value );
      } else if( result instanceof QxBoxDimensions ) {
        QxBoxDimensions boxdim = ( QxBoxDimensions )result;
        repl = boxdim.toJsArray();
      } else {
        String mesg = "Only boolean values, dimensions, and box dimensions"
                      + " can be substituted in appearance templates";
        throw new IllegalArgumentException( mesg );
      }
      matcher.appendReplacement( sb, repl );
    }
    matcher.appendTail( sb );
    return sb.toString();
  }

  static String stripTemplate( final String input ) {
    Pattern startPattern = Pattern.compile( "BEGIN TEMPLATE.*(\\r|\\n)" );
    Pattern endPattern = Pattern.compile( "(\\r|\\n).*?END TEMPLATE" );
    int beginIndex = 0;
    int endIndex = input.length();
    Matcher matcher;
    matcher = startPattern.matcher( input );
    if( matcher.find() ) {
      beginIndex = matcher.end();
    }
    matcher = endPattern.matcher( input );
    if( matcher.find() ) {
      endIndex = matcher.start();
    }
    return input.substring( beginIndex, endIndex );
  }
}
