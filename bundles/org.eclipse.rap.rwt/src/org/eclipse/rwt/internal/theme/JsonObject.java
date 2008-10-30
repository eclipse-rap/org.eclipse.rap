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
 * Simple and incomplete renderer for JSON objects.
 */
public class JsonObject {

  private final StringBuffer buffer;
  private int count = 0;

  public JsonObject() {
    buffer = new StringBuffer();
    buffer.append( "{" );
  }

  public void append( final String key, final int value ) {
    doAppend( key, String.valueOf( value ) );
  }

  public void append( final String key, final boolean value ) {
    doAppend( key, String.valueOf( value ) );
  }

  public void append( final String key, final String value ) {
    doAppend( key, JsonUtil.toJson( value ) );
  }

  public void append( final String key, final JsonObject object ) {
    doAppend( key, JsonUtil.toJson( object ) );
  }

  public void append( final String key, final JsonArray array ) {
    doAppend( key, JsonUtil.toJson( array ) );
  }

  public void append( final String key, final JsonValue value ) {
    doAppend( key, value.toString() );
  }

  public String toString() {
    String tail = count == 0 ? "}" : "\n}";
    return buffer.toString() + tail;
  }

  private void doAppend( final String key, final String valueStr ) {
    buffer.append( count == 0 ? "\n" : ",\n" );
    buffer.append( JsonUtil.toJson( key ) );
    buffer.append( ": " );
    buffer.append( valueStr );
    count++;
  }
}
