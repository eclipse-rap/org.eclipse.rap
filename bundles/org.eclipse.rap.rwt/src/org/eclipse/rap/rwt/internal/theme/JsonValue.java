/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;


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
    StringBuilder buffer = new StringBuilder( string.length() + 2 );
    appendQuotedAndEscapedString( buffer, string );
    return buffer.toString();
  }

  static void appendQuotedAndEscapedString( StringBuilder buffer, String string ) {
    buffer.append( '"' );
    appendEscapedString( buffer, string );
    buffer.append( '"' );
  }

  static void appendEscapedString( StringBuilder buffer, String string ) {
    char[] chars = string.toCharArray();
    int length = chars.length;
    for( int i = 0; i < length; i++ ) {
      char ch = chars[ i ];
      if( ch == '"' || ch == '\\' ) {
        buffer.append( '\\' );
        buffer.append( ch );
      } else if( ch == '\n' ) {
        buffer.append( '\\' );
        buffer.append( 'n' );
      } else if( ch == '\r' ) {
        buffer.append( '\\' );
        buffer.append( 'r' );
      } else if( ch == '\t' ) {
        buffer.append( '\\' );
        buffer.append( 't' );
      } else if( ch == '\u2028' ) {
        buffer.append( "\\u2028" );
      } else if( ch == '\u2029' ) {
        buffer.append( "\\u2029" );
      } else if( ch >= CONTROL_CHARACTERS_START && ch <= CONTROL_CHARACTERS_END ) {
        buffer.append( "\\u00" );
        if( ch <= 0x000f ) {
          buffer.append( '0' );
        }
        buffer.append( Integer.toHexString( ch ) );
      } else {
        buffer.append( ch );
      }
    }
  }

  private static class JsonPrimitive extends JsonValue {

    private final String value;

    JsonPrimitive( String value ) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }
}
