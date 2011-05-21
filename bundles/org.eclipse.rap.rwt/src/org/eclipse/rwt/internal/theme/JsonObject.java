/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
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
 * Simple generator for JSON objects.
 */
public final class JsonObject extends JsonValue {

  private final StringBuffer buffer;
  private int count = 0;

  public JsonObject() {
    buffer = new StringBuffer();
    buffer.append( "{" );
  }

  public void append( String key, int value ) {
    append( key, valueOf( value ) );
  }

  public void append( String key, float value ) {
    append( key, valueOf( value ) );
  }

  public void append( String key, boolean value ) {
    append( key, valueOf( value ) );
  }

  public void append( String key, String value ) {
    append( key, valueOf( value ) );
  }

  public void append( String key, JsonValue value ) {
    if( value != null ) {
      doAppend( key, value.toString() );
    } else {
      doAppend( key, "null" );
    }
  }

  public String toString() {
    String tail = count == 0 ? "}" : "\n}";
    return buffer.toString() + tail;
  }

  private void doAppend( String key, String valueStr ) {
    buffer.append( count == 0 ? "\n" : ",\n" );
    buffer.append( quoteString( key ) );
    buffer.append( ": " );
    buffer.append( valueStr );
    count++;
  }
}
