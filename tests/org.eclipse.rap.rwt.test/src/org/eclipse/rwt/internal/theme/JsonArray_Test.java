/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class JsonArray_Test extends TestCase {

  public void testToStringWhenEmpty() {
    JsonArray array = new JsonArray();
    
    assertEquals( "[]", array.toString() );
  }

  public void testAppendOnce() {
    JsonArray array = new JsonArray();

    array.append( 23 );
    
    assertEquals( "[ 23 ]", array.toString() );
  }

  public void testAppendMultiple() {
    JsonArray array = new JsonArray();

    array.append( 23 );
    array.append( 3.14f );
    array.append( "foo" );
    array.append( false );
    array.append( ( String )null );

    assertEquals( "[ 23, 3.14, \"foo\", false, null ]", array.toString() );
  }

  public void testAppendAfterToString() {
    JsonArray array = new JsonArray();
    
    array.append( 23 );
    array.toString();
    array.append( false );

    assertEquals( "[ 23, false ]", array.toString() );
  }

  public void testAppendArray() {
    JsonArray array = new JsonArray();

    array.append( new JsonArray() );
    array.append( ( JsonArray )null );

    assertEquals( "[ [], null ]", array.toString() );
  }

  public void testAppendObject() {
    JsonArray array = new JsonArray();

    array.append( new JsonObject() );
    array.append( ( JsonObject )null );

    assertEquals( "[ {}, null ]", array.toString() );
  }

  public void testValueOfEmptyArray() {
    assertEquals( "[]", JsonArray.valueOf( new int[ 0 ] ).toString() );
    assertEquals( "[]", JsonArray.valueOf( new float[ 0 ] ).toString() );
    assertEquals( "[]", JsonArray.valueOf( new String[ 0 ] ).toString() );
  }

  public void testValueOfIntArray() {
    JsonArray expected = new JsonArray();
    expected.append( 23 );
    expected.append( 42 );

    JsonArray created = JsonArray.valueOf( new int[] { 23, 42 } );

    assertEquals( expected.toString(), created.toString() );
  }

  public void testValueOfFloatArray() {
    JsonArray expected = new JsonArray();
    expected.append( 23f );
    expected.append( 3.14f );
    
    JsonArray created = JsonArray.valueOf( new float[] { 23f, 3.14f } );
    
    assertEquals( expected.toString(), created.toString() );
  }

  public void testValueOfStringArray() {
    JsonArray expected = new JsonArray();
    expected.append( "A" );
    expected.append( "B" );

    JsonArray created = JsonArray.valueOf( new String[] { "A", "B" } );

    assertEquals( expected.toString(), created.toString() );
  }
}
