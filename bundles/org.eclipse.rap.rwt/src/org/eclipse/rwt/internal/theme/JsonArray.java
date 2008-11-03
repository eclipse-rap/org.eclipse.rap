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


/**
 * Simple genearator for JSON arrays.
 */
public final class JsonArray extends JsonValue {

  private final StringBuffer buffer;

  private int count = 0;

  public JsonArray() {
    buffer = new StringBuffer();
    buffer.append( "[" );
  }

  public void append( final int value ) {
    append( valueOf( value ) );
  }

  public void append( final boolean value ) {
    append( valueOf( value ) );
  }

  public void append( final String value ) {
    append( valueOf( value ) );
  }

  public void append( final JsonValue value ) {
    if( value != null ) {
      doAppend( value.toString() );
    } else {
      doAppend( "null" );
    }
  }

  public String toString() {
    String tail = count == 0 ? "]" : " ]";
    return buffer.toString() + tail;
  }

  public static JsonArray valueOf( final String[] array ) {
    JsonArray result = new JsonArray();
    for( int i = 0; i < array.length; i++ ) {
      result.append( array[ i ] );
    }
    return result;
  }

  private void doAppend( final String valueStr ) {
    buffer.append( count == 0 ? " " : ", " );
    buffer.append( valueStr );
    count++;
  }
}
