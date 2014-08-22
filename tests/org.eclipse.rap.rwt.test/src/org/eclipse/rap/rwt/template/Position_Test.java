/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.template;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.junit.Test;


public class Position_Test {

  @Test
  public void testToJson_withPercentage() {
    Position position = new Position( 3.14f, 42 );

    JsonValue json = position.toJson();

    assertEquals( new JsonArray().add( 3.14f ).add( 42 ), json );
  }

  @Test
  public void testToJson_withoutPercentage() {
    Position position = new Position( 0, 42 );

    JsonValue json = position.toJson();

    assertEquals( JsonValue.valueOf( 42 ), json );
  }

  @Test
  public void testToString() {
    Position position = new Position( 3.14f, 42 );

    String string = position.toString();

    assertThat( string, containsString( "Position" ) );
    assertThat( string, containsString( "3.14%" ) );
    assertThat( string, containsString( "42px" ) );
  }

  @Test
  public void testEquals_equalsItself() {
    Position position = new Position( 3.14f, 42 );

    assertTrue( position.equals( position ) );
  }

  @Test
  public void testEquals_notEqualsNull() {
    Position position = new Position( 3.14f, 42 );

    assertFalse( position.equals( null ) );
  }

  @Test
  public void testEquals_notEqualsDifferentOffset() {
    Position position1 = new Position( 3.14f, 42 );
    Position position2 = new Position( 3.14f, 23 );

    assertFalse( position1.equals( position2 ) );
  }

  @Test
  public void testEquals_notEqualsDifferentPercentage() {
    Position position1 = new Position( 3.14f, 42 );
    Position position2 = new Position( 3.141f, 42 );

    assertFalse( position1.equals( position2 ) );
  }

  @Test
  public void testEquals_equalsWithSameValues() {
    Position position1 = new Position( 3.14f, 42 );
    Position position2 = new Position( 3.14f, 42 );

    assertTrue( position1.equals( position2 ) );
  }

  @Test
  public void testHashCode_equalsForEqualObjects() {
    Position position1 = new Position( 3.14f, 42 );
    Position position2 = new Position( 3.14f, 42 );

    assertEquals( position1.hashCode(), position2.hashCode() );
  }

  @Test
  public void testHashCode_differsForDifferentPercentage() {
    Position position1 = new Position( 3.14f, 42 );
    Position position2 = new Position( 3.141f, 42 );

    assertNotEquals( position1.hashCode(), position2.hashCode() );
  }

  @Test
  public void testHashCode_differsForDifferentOffset() {
    Position position1 = new Position( 3.14f, 42 );
    Position position2 = new Position( 3.141f, 42 );

    assertNotEquals( position1.hashCode(), position2.hashCode() );
  }

}
