/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class JsonArray_Test extends TestCase {

  public void testAppend() {
    JsonArray array = new JsonArray();
    assertEquals( "[]", array.toString() );
    array.append( "a" );
    assertEquals( "[ \"a\" ]", array.toString() );
    array.append( 23 );
    assertEquals( "[ \"a\", 23 ]", array.toString() );
    array.append( false );
    assertEquals( "[ \"a\", 23, false ]", array.toString() );
    array.append( ( String )null );
    assertEquals( "[ \"a\", 23, false, null ]", array.toString() );
  }

  public void testAppendArray() {
    JsonArray array = new JsonArray();
    array.append( 1 );
    array.append( new JsonArray() );
    assertEquals( "[ 1, [] ]", array.toString() );
    array.append( ( JsonArray )null );
    assertEquals( "[ 1, [], null ]", array.toString() );
  }

  public void testAppendObject() {
    JsonArray array = new JsonArray();
    array.append( 1 );
    array.append( new JsonObject() );
    assertEquals( "[ 1, {} ]", array.toString() );
    array.append( ( JsonObject )null );
    assertEquals( "[ 1, {}, null ]", array.toString() );
  }

  public void testValueOf() {
    assertEquals( "[]", JsonArray.valueOf( new String[ 0 ] ).toString() );
    JsonArray expected = new JsonArray();
    expected.append( "A" );
    expected.append( "B" );
    String[] array = new String[] { "A", "B" };
    assertEquals( expected.toString(), JsonArray.valueOf( array ).toString() );
  }
}
