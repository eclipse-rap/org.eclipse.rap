/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;


/**
 * Simple generator for JSON values.
 */
public abstract class JsonValue {

  public static final JsonValue NULL = new JsonPrimitive( "null" );
  public static final JsonValue TRUE = new JsonPrimitive( "true" );
  public static final JsonValue FALSE = new JsonPrimitive( "false" );

  private static final int CONTROL_CHARACTERS_START = 0x0000;
  private static final int CONTROL_CHARACTERS_END = 0x001f;

  JsonValue() {
    // prevent instantiation from outside
  }

  public static JsonValue valueOf( int value ) {
    return new JsonPrimitive( String.valueOf( value ) );
  }

  public static JsonValue valueOf( long value ) {
    return new JsonPrimitive( String.valueOf( value ) );
  }

  // TODO not needed anymore, see double
  public static JsonValue valueOf( float value ) {
    return new JsonPrimitive( String.valueOf( value ) );
  }

  public static JsonValue valueOf( double value ) {
    return new JsonPrimitive( String.valueOf( value ) );
  }

  public static JsonValue valueOf( boolean value ) {
    return value ? TRUE : FALSE;
  }

  public static JsonValue valueOf( String value ) {
    JsonValue result;
    if( value == null ) {
      result = NULL;
    } else {
      result = new JsonPrimitive( quoteAndEscapeString( value ) );
    }
    return result;
  }

  static String quoteAndEscapeString( String string ) {
    StringBuilder resultBuffer = new StringBuilder();
    resultBuffer.append( '"' );
    resultBuffer.append( escapeString( string ) );
    resultBuffer.append( '"' );
    return resultBuffer.toString();
  }

  static String escapeString( String string ) {
    StringBuilder resultBuffer = new StringBuilder();
    int length = string.length();
    for( int i = 0; i < length; i++ ) {
      char ch = string.charAt( i );
      if( ch == '"' || ch == '\\' ) {
        resultBuffer.append( '\\' );
        resultBuffer.append( ch );
      } else if( ch == '\n' ) {
        resultBuffer.append( "\\n" );
      } else if( ch == '\r' ) {
        resultBuffer.append( "\\r" );
      } else if( ch == '\t' ) {
        resultBuffer.append( "\\t" );
      } else if( ch == '\u2028' ) {
        resultBuffer.append( "\\u2028" );
      } else if( ch == '\u2029' ) {
        resultBuffer.append( "\\u2029" );
      } else if( ch >= CONTROL_CHARACTERS_START && ch <= CONTROL_CHARACTERS_END ) {
        resultBuffer.append( "\\u00" );
        if( ch <= 9 ) {
          resultBuffer.append( "0" );
        }
        resultBuffer.append( Integer.toHexString( ch ) );
      } else {
        resultBuffer.append( ch );
      }
    }
    return resultBuffer.toString();
  }

  private static class JsonPrimitive extends JsonValue {

    private final String value;

    JsonPrimitive( String value ) {
      this.value = value;
    }

    public String toString() {
      return value;
    }
  }
}
