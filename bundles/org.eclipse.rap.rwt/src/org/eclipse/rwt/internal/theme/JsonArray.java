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
 * Simple and incomplete renderer for JSON arrays.
 */
public class JsonArray {

  private final StringBuffer buffer;
  private int count = 0;

  public JsonArray() {
    buffer = new StringBuffer();
    buffer.append( "[" );
  }

  public void append( final String value ) {
    doAppend( JsonUtil.quoteString( value ) );
  }

  public void append( final JsonObject object ) {
    doAppend( object.toString() );
  }

  public void append( final JsonArray array ) {
    doAppend( array.toString() );
  }

  public String toString() {
    String tail = count == 0 ? "]" : " ]";
    return buffer.toString() + tail;
  }

  private void doAppend( final String valueStr ) {
    buffer.append( count == 0 ? " " : ", " );
    buffer.append( valueStr );
    count++;
  }
}
