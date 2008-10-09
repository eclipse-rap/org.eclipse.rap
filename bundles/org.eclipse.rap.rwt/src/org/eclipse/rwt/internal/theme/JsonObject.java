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

  public void append( final String key, final String value ) {
    String keyStr = JsonUtil.quoteString( key );
    String valueStr = JsonUtil.quoteString( value );
    doAppend( keyStr, valueStr );
  }

  public void append( final String key, final JsonObject object ) {
    String keyStr = JsonUtil.quoteString( key );
    String valueStr = object.toString();
    doAppend( keyStr, valueStr );
  }

  public void append( final String key, final JsonArray array ) {
    String keyStr = JsonUtil.quoteString( key );
    String valueStr = array.toString();
    doAppend( keyStr, valueStr );
  }
  
  public String toString() {
    String tail = count == 0 ? "}" : "\n}";
    return buffer.toString() + tail;
  }

  private void doAppend( final String keyStr, final String valueStr ) {
    buffer.append( count == 0 ? "\n" : ",\n" );
    buffer.append( keyStr );
    buffer.append( ": " );
    buffer.append( valueStr );
    count++;
  }
}