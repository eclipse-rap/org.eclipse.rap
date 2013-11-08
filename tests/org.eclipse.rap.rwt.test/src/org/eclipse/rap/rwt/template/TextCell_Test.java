/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.template.TextCell;
import org.junit.Before;
import org.junit.Test;


public class TextCell_Test {

  private Template template;

  @Before
  public void setUp() {
    template = new Template();
  }

  @Test
  public void testHasType() {
    TextCell cell = new TextCell( template );

    String type = cell.getType();

    assertEquals( "text", type );
  }

  @Test
  public void testSetText() {
    TextCell cell = new TextCell( template );

    cell.setText( "foo" );

    assertEquals( "foo", cell.getText() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetText_failsWithNullText() {
    TextCell cell = new TextCell( template );

    cell.setText( null );
  }

  @Test
  public void testSetText_returnsCell() {
    TextCell cell = new TextCell( template );

    TextCell actualCell = cell.setText( "foo" );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetWrap_withTrue() {
    TextCell cell = new TextCell( template );

    cell.setWrap( true );

    assertTrue( cell.isWrap() );
  }

  @Test
  public void testSetWrap_withFalse() {
    TextCell cell = new TextCell( template );

    cell.setWrap( false );

    assertFalse( cell.isWrap() );
  }

  @Test
  public void testSetWrap_returnsCell() {
    TextCell cell = new TextCell( template );

    TextCell actualCell = cell.setWrap( true );

    assertSame( cell, actualCell );
  }

  @Test
  public void testToJson_containsType() {
    TextCell cell = new TextCell( template );

    JsonObject json = cell.toJson();

    assertEquals( "text", json.get( "type" ).asString() );
  }

  @Test
  public void testToJson_doesNotContainOtherPropertiesByDefault() {
    TextCell cell = new TextCell( template );

    JsonObject json = cell.toJson();

    assertEquals( 1, json.size() );
  }

  @Test
  public void testToJson_containsText() {
    TextCell cell = new TextCell( template );

    cell.setText( "Hello" );
    JsonObject json = cell.toJson();

    assertEquals( "Hello", json.get( "text" ).asString() );
  }

  @Test
  public void testToJson_containsWrap() {
    TextCell cell = new TextCell( template );

    cell.setWrap( true );
    JsonObject json = cell.toJson();

    assertTrue( json.get( "wrap" ).asBoolean() );
  }

}
