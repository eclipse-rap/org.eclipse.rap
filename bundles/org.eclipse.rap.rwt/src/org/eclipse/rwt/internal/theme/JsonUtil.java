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

import java.util.regex.Pattern;


/**
 * Utility for creating JSON structures.
 */
public final class JsonUtil {

  public static final String NULL = "null";
  private static final Pattern PATTERN_BS = Pattern.compile( "\\\\" );
  private static final String REPL_BS = "\\\\\\\\";
  private static final Pattern PATTERN_QUOTE = Pattern.compile( "\"" );
  private static final String REPL_QUOTE = "\\\\\\\"";

  private JsonUtil() {
    // prevent instantiation
  }

  public static String toJson( final String string ) {
    String result;
    if( string != null ) {
      String replaced = string;
      replaced = PATTERN_BS.matcher( replaced ).replaceAll( REPL_BS );
      replaced = PATTERN_QUOTE.matcher( replaced ).replaceAll( REPL_QUOTE );
      result = "\"" + replaced + "\"";
    } else {
      result = NULL;
    }
    return result;
  }

  public static String toJson( final JsonObject object ) {
    String result;
    if( object != null ) {
      result = object.toString();
    } else {
      result = NULL;
    }
    return result;
  }

  public static String toJson( final JsonArray array ) {
    String result;
    if( array != null ) {
      result = array.toString();
    } else {
      result = NULL;
    }
    return result;
  }
}
