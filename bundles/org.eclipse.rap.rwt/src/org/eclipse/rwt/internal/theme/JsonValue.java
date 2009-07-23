/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.regex.Pattern;

/**
 * Simple generator for JSON values.
 */
public abstract class JsonValue {

  private static final Pattern PATTERN_BS = Pattern.compile( "\\\\" );
  private static final String REPL_BS = "\\\\\\\\";
  private static final Pattern PATTERN_QUOTE = Pattern.compile( "\"" );
  private static final String REPL_QUOTE = "\\\\\\\"";

  public static final JsonValue NULL = new JsonPrimitive( "null" );
  public static final JsonValue TRUE = new JsonPrimitive( "true" );
  public static final JsonValue FALSE = new JsonPrimitive( "false" );

  public static JsonValue valueOf( final int value ) {
    return new JsonPrimitive( String.valueOf( value ) );
  }

  public static JsonValue valueOf( final float value ) {
    return new JsonPrimitive( String.valueOf( value ) );
  }

  public static JsonValue valueOf( final boolean value ) {
    return value ? TRUE : FALSE;
  }

  public static JsonValue valueOf( final String value ) {
    JsonValue result;
    if( value == null ) {
      result = NULL;
    } else {
      result = new JsonPrimitive( quoteString( value ) );
    }
    return result;
  }

  public static String quoteString( final String string ) {
    String replaced = string;
    replaced = PATTERN_BS.matcher( replaced ).replaceAll( REPL_BS );
    replaced = PATTERN_QUOTE.matcher( replaced ).replaceAll( REPL_QUOTE );
    return "\"" + replaced + "\"";
  }

  private static class JsonPrimitive extends JsonValue {

    private final String value;

    public JsonPrimitive( final String value ) {
      this.value = value;
    }

    public String toString() {
      return value;
    }
  }
}
