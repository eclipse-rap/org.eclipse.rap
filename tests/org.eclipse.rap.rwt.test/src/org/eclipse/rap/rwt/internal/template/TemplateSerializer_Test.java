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
package org.eclipse.rap.rwt.internal.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TemplateSerializer_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithNullTemplate() {
    new TemplateSerializer( null );
  }

  @Test
  public void testSerializesEmptyTemplate() {
    TemplateSerializer serializer = new TemplateSerializer( new RowTemplate() );

    JsonArray cells = serializer.toJson().asArray();

    assertNotNull( cells );
    assertTrue( cells.isEmpty() );
  }

  @Test
  public void testSerializesEmptyCell() {
    RowTemplate template = new RowTemplate();
    new CellImpl( template, "foo" );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    assertEquals( 1, cells.size() );
  }

  @Test
  public void testSerializesCellType() {
    RowTemplate template = new RowTemplate();
    new CellImpl( template, "foo" );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject cell = cells.get( 0 ).asObject();
    assertEquals( cell.get( "type" ).asString(), "foo" );
  }

  @Test
  public void testSerializesCellName() {
    RowTemplate template = new RowTemplate();
    new CellImpl( template, "foo" ).setName( "bar" );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject cell = cells.get( 0 ).asObject();
    assertEquals( cell.get( CellImpl.PROPERTY_NAME ).asString(), "bar" );
  }

  @Test
  public void testSerializesSelectable() {
    RowTemplate template = new RowTemplate();
    new CellImpl( template, "foo" ).setSelectable( true );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject cell = cells.get( 0 ).asObject();
    assertTrue( cell.get( CellImpl.PROPERTY_SELECTABLE ).asBoolean() );
  }

  @Test
  public void testSerializesAllCells() {
    RowTemplate template = new RowTemplate();
    new CellImpl( template, "foo" );
    new CellImpl( template, "foo" );
    new CellImpl( template, "foo" );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    assertEquals( 3, cells.size() );
  }

  @Test
  public void testCellWith_Left_Top() {
    RowTemplate template = new RowTemplate();
    Cell cell = new CellImpl( template, "foo" );
    cell.setLeft( 23 );
    cell.setTop( 42 );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    assertEquals( 23, actualCell.get( CellImpl.PROPERTY_LEFT ).asInt() );
    assertEquals( 42, actualCell.get( CellImpl.PROPERTY_TOP ).asInt() );
  }

  @Test
  public void testCellWith_Left_Top_Wight_Height() {
    RowTemplate template = new RowTemplate();
    Cell cell = new CellImpl( template, "foo" );
    cell.setLeft( 23 );
    cell.setTop( 42 );
    cell.setWidth( 100 );
    cell.setHeight( 200 );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    assertEquals( 23, actualCell.get( CellImpl.PROPERTY_LEFT ).asInt() );
    assertEquals( 42, actualCell.get( CellImpl.PROPERTY_TOP ).asInt() );
    assertEquals( 100, actualCell.get( CellImpl.PROPERTY_WIDTH ).asInt() );
    assertEquals( 200, actualCell.get( CellImpl.PROPERTY_HEIGHT ).asInt() );
  }

  @Test
  public void testCellWith_Right_Bottom() {
    RowTemplate template = new RowTemplate();
    Cell cell = new CellImpl( template, "foo" );
    cell.setRight( 23 );
    cell.setBottom( 42 );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    assertEquals( 42, actualCell.get( CellImpl.PROPERTY_BOTTOM ).asInt() );
    assertEquals( 23, actualCell.get( CellImpl.PROPERTY_RIGHT ).asInt() );
  }

  @Test
  public void testCellWith_Right_Bottom_Height_Width() {
    RowTemplate template = new RowTemplate();
    Cell cell = new CellImpl( template, "foo" );
    cell.setRight( 23 );
    cell.setBottom( 42 );
    cell.setWidth( 100 );
    cell.setHeight( 200 );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.asArray().get( 0 ).asObject();
    assertEquals( 23, actualCell.get( CellImpl.PROPERTY_RIGHT ).asInt() );
    assertEquals( 42, actualCell.get( CellImpl.PROPERTY_BOTTOM ).asInt() );
    assertEquals( 100, actualCell.get( CellImpl.PROPERTY_WIDTH ).asInt() );
    assertEquals( 200, actualCell.get( CellImpl.PROPERTY_HEIGHT ).asInt() );
  }

  @Test
  public void testSerializesAllAttribute() {
    RowTemplate template = new RowTemplate();
    CellImpl cell = new CellImpl( template, "foo" );
    cell.addAttribute( "foo", "bar" );
    cell.addAttribute( "foo1", "bar1" );
    cell.addAttribute( "foo2", "bar2" );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.asArray().get( 0 ).asObject();
    assertEquals( "bar", actualCell.get( "foo" ).asString() );
    assertEquals( "bar1", actualCell.get( "foo1" ).asString() );
    assertEquals( "bar2", actualCell.get( "foo2" ).asString() );
  }

  @Test
  public void testSerializesTextAttribute() {
    RowTemplate template = new RowTemplate();
    CellImpl cell = new CellImpl( template, "foo" );
    cell.addAttribute( "foo", "bar" );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    assertEquals( "bar", actualCell.get( "foo" ).asString() );
  }

  @Test
  public void testSerializesImageAttribute() {
    RowTemplate template = new RowTemplate();
    CellImpl cell = new CellImpl( template, "foo" );
    cell.addAttribute( "foo", createImage( Fixture.IMAGE_100x50 ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    JsonArray image = actualCell.get( "foo" ).asArray();
    assertEquals( 3, image.size() );
    assertTrue( image.get( 0 ).isString() );
    assertEquals( 100, image.get( 1 ).asInt() );
    assertEquals( 50, image.get( 2 ).asInt() );
  }

  @Test
  public void testSerializesColorAttribute() {
    RowTemplate template = new RowTemplate();
    CellImpl cell = new CellImpl( template, "foo" );
    cell.addAttribute( "foo", new Color( display, 255, 255, 255 ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    JsonArray color = actualCell.get( "foo" ).asArray();
    assertEquals( 4, color.size() );
    assertEquals( 255, color.get( 0 ).asInt() );
    assertEquals( 255, color.get( 1 ).asInt() );
    assertEquals( 255, color.get( 2 ).asInt() );
    assertEquals( 255, color.get( 3 ).asInt() );
  }

  @Test
  public void testSerializesRGBAttribute() {
    RowTemplate template = new RowTemplate();
    CellImpl cell = new CellImpl( template, "foo" );
    cell.addAttribute( "foo", new RGB( 255, 255, 255 ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    JsonArray color = actualCell.get( "foo" ).asArray();
    assertEquals( 4, color.size() );
    assertEquals( 255, color.get( 0 ).asInt() );
    assertEquals( 255, color.get( 1 ).asInt() );
    assertEquals( 255, color.get( 2 ).asInt() );
    assertEquals( 255, color.get( 3 ).asInt() );
  }

  @Test
  public void testSerializesFontAttribute() {
    RowTemplate template = new RowTemplate();
    CellImpl cell = new CellImpl( template, "foo" );
    cell.addAttribute( "foo", new Font( display, "Arial", 22, SWT.NONE ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    JsonArray font = actualCell.get( "foo" ).asArray();
    assertEquals( 4, font.size() );
    assertEquals( "Arial", font.get( 0 ).asArray().get( 0 ).asString() );
    assertEquals( 22, font.get( 1 ).asInt() );
    assertFalse( font.get( 2 ).asBoolean() );
    assertFalse( font.get( 3 ).asBoolean() );
  }

  @Test
  public void testSerializesFontDataAttribute() {
    RowTemplate template = new RowTemplate();
    CellImpl cell = new CellImpl( template, "foo" );
    cell.addAttribute( "foo", new Font( display, "Arial", 22, SWT.NONE ).getFontData()[ 0 ] );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    JsonArray font = actualCell.get( "foo" ).asArray();
    assertEquals( 4, font.size() );
    assertEquals( "Arial", font.get( 0 ).asArray().get( 0 ).asString() );
    assertEquals( 22, font.get( 1 ).asInt() );
    assertFalse( font.get( 2 ).asBoolean() );
    assertFalse( font.get( 3 ).asBoolean() );
  }

  @Test
  public void testSerializesPointAttribute() {
    RowTemplate template = new RowTemplate();
    CellImpl cell = new CellImpl( template, "foo" );
    cell.addAttribute( "foo", new Point( 23, 42 ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    JsonArray point = actualCell.get( "foo" ).asArray();
    assertEquals( 2, point.size() );
    assertEquals( 23, point.get( 0 ).asInt() );
    assertEquals( 42, point.get( 1 ).asInt() );
  }

  @Test
  public void testSerializesRectangleAttribute() {
    RowTemplate template = new RowTemplate();
    CellImpl cell = new CellImpl( template, "foo" );
    cell.addAttribute( "foo", new Rectangle( 23, 42, 123, 142 ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonArray cells = serializer.toJson().asArray();

    JsonObject actualCell = cells.get( 0 ).asObject();
    JsonArray point = actualCell.get( "foo" ).asArray();
    assertEquals( 4, point.size() );
    assertEquals( 23, point.get( 0 ).asInt() );
    assertEquals( 42, point.get( 1 ).asInt() );
    assertEquals( 123, point.get( 2 ).asInt() );
    assertEquals( 142, point.get( 3 ).asInt() );
  }

  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    return new Image( display, loader.getResourceAsStream( name ) );
  }
}
