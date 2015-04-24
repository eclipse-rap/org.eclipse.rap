/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.util;

import java.util.ArrayList;
import java.util.List;


public final class EncodingUtil {

  private static final String UNIX_NEWLINE = "\\n";

  public static String replaceNewLines( String input ) {
    return replaceNewLines( input, UNIX_NEWLINE );
  }

  public static String replaceNewLines( String input, String replacement ) {
    StringBuilder resultBuffer = null;
    int length = input.length();
    int start = 0;
    int i = 0;
    while( i < length ) {
      char ch = input.charAt( i );
      if( ch == '\n' || ch == '\r' ) {
        if (resultBuffer == null) {
          resultBuffer = new StringBuilder();
        }
        resultBuffer.append( input, start, i );
        resultBuffer.append( replacement );
        if( ch == '\r' && i + 1 < length && input.charAt( i + 1 ) == '\n' ) {
          i++;
        }
        start = i + 1;
      }
      i++;
    }
    if (resultBuffer != null) {
      resultBuffer.append( input, start, i );
      return resultBuffer.toString();
    }
    return input;
  }

  public static String[] splitNewLines( String input ) {
    int length = input.length();
    List<String> resultList = new ArrayList<String>();
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

  private EncodingUtil() {
    // prevent instantiation
  }

}
