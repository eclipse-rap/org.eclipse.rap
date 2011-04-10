/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
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

  public static JsonValue valueOf( int value ) {
    return new JsonPrimitive( String.valueOf( value ) );
  }

  public static JsonValue valueOf( float value ) {
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
      result = new JsonPrimitive( quoteString( value ) );
    }
    return result;
  }

  public static String quoteString( String string ) {
    StringBuffer resultBuffer = new StringBuffer();
    resultBuffer.append( '"' );
    int length = string.length();
    for( int i = 0; i < length; i++ ) {
      char ch = string.charAt( i );
      if( ch == '"' || ch == '\\' ) {
        resultBuffer.append( '\\' );
      }
      resultBuffer.append( ch );
    }
    resultBuffer.append( '"' );
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
