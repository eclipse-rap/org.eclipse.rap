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

  private static final Pattern PATTERN_BS = Pattern.compile( "\\\\" );
  private static final String REPL_BS = "\\\\\\\\";
  private static final Pattern PATTERN_QUOTE = Pattern.compile( "\"" );
  private static final String REPL_QUOTE = "\\\\\\\"";

  private JsonUtil() {
    // prevent instantiation
  }

  public static String quoteString( final String string ) {
    String result = string;
    result = PATTERN_BS.matcher( result ).replaceAll( REPL_BS );
    result = PATTERN_QUOTE.matcher( result ).replaceAll( REPL_QUOTE );
    return "\"" + result + "\"";
  }
}
