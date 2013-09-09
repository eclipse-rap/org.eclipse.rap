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

    JsonObject object = serializer.toJson();

    assertNotNull( object );
    assertTrue( object.isEmpty() );
  }

  @Test
  public void testSerializesEmptyCell() {
    RowTemplate template = new RowTemplate();
    new Cell( template );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonArray cells = object.get( "cells" ).asArray();
    assertEquals( 1, cells.size() );
  }

  @Test
  public void testSerializesAllCells() {
    RowTemplate template = new RowTemplate();
    new Cell( template );
    new Cell( template );
    new Cell( template );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonArray cells = object.get( "cells" ).asArray();
    assertEquals( 3, cells.size() );
  }

  @Test
  public void testCellWith_Left_Top() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.setLeft( 23 );
    cell.setTop( 42 );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray layout = actualCell.get( "layout" ).asArray();
    assertEquals( 6, layout.size() );
    assertEquals( 42, layout.get( 0 ).asInt() );
    assertEquals( JsonObject.NULL, layout.get( 1 ) );
    assertEquals( JsonObject.NULL, layout.get( 2 ) );
    assertEquals( 23, layout.get( 3 ).asInt() );
    assertEquals( JsonObject.NULL, layout.get( 4 ) );
    assertEquals( JsonObject.NULL, layout.get( 5 ) );
  }

  @Test
  public void testCellWith_Left_Top_Wight_Height() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.setLeft( 23 );
    cell.setTop( 42 );
    cell.setWidth( 100 );
    cell.setHeight( 200 );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray layout = actualCell.get( "layout" ).asArray();
    assertEquals( 6, layout.size() );
    assertEquals( 42, layout.get( 0 ).asInt() );
    assertEquals( JsonObject.NULL, layout.get( 1 ) );
    assertEquals( JsonObject.NULL, layout.get( 2 ) );
    assertEquals( 23, layout.get( 3 ).asInt() );
    assertEquals( 100, layout.get( 4 ).asInt() );
    assertEquals( 200, layout.get( 5 ).asInt() );
  }

  @Test
  public void testCellWith_Right_Bottom() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.setRight( 23 );
    cell.setBottom( 42 );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray layout = actualCell.get( "layout" ).asArray();
    assertEquals( 6, layout.size() );
    assertEquals( JsonObject.NULL, layout.get( 0 ) );
    assertEquals( 23, layout.get( 1 ).asInt() );
    assertEquals( 42, layout.get( 2 ).asInt() );
    assertEquals( JsonObject.NULL, layout.get( 3 ) );
    assertEquals( JsonObject.NULL, layout.get( 4 ) );
    assertEquals( JsonObject.NULL, layout.get( 5 ) );
  }

  @Test
  public void testCellWith_Right_Bottom_Height_Width() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.setRight( 23 );
    cell.setBottom( 42 );
    cell.setWidth( 100 );
    cell.setHeight( 200 );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray layout = actualCell.get( "layout" ).asArray();
    assertEquals( 6, layout.size() );
    assertEquals( JsonObject.NULL, layout.get( 0 ) );
    assertEquals( 23, layout.get( 1 ).asInt() );
    assertEquals( 42, layout.get( 2 ).asInt() );
    assertEquals( JsonObject.NULL, layout.get( 3 ) );
    assertEquals( 100, layout.get( 4 ).asInt() );
    assertEquals( 200, layout.get( 5 ).asInt() );
  }

  @Test
  public void testSerializesAllAttribute() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.addAttribute( "foo", "bar" );
    cell.addAttribute( "foo1", "bar1" );
    cell.addAttribute( "foo2", "bar2" );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonObject attributes = actualCell.get( "attributes" ).asObject();
    assertEquals( "bar", attributes.get( "foo" ).asString() );
    assertEquals( "bar1", attributes.get( "foo1" ).asString() );
    assertEquals( "bar2", attributes.get( "foo2" ).asString() );
  }

  @Test
  public void testSerializesTextAttribute() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.addAttribute( "foo", "bar" );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonObject attributes = actualCell.get( "attributes" ).asObject();
    assertEquals( "bar", attributes.get( "foo" ).asString() );
  }

  @Test
  public void testSerializesImageAttribute() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.addAttribute( "foo", createImage( Fixture.IMAGE_100x50 ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray image = actualCell.get( "attributes" ).asObject().get( "foo" ).asArray();
    assertEquals( 3, image.size() );
    assertTrue( image.get( 0 ).isString() );
    assertEquals( 100, image.get( 1 ).asInt() );
    assertEquals( 50, image.get( 2 ).asInt() );
  }

  @Test
  public void testSerializesColorAttribute() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.addAttribute( "foo", new Color( display, 255, 255, 255 ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray color = actualCell.get( "attributes" ).asObject().get( "foo" ).asArray();
    assertEquals( 4, color.size() );
    assertEquals( 255, color.get( 0 ).asInt() );
    assertEquals( 255, color.get( 1 ).asInt() );
    assertEquals( 255, color.get( 2 ).asInt() );
    assertEquals( 255, color.get( 3 ).asInt() );
  }

  @Test
  public void testSerializesRGBAttribute() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.addAttribute( "foo", new RGB( 255, 255, 255 ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray color = actualCell.get( "attributes" ).asObject().get( "foo" ).asArray();
    assertEquals( 4, color.size() );
    assertEquals( 255, color.get( 0 ).asInt() );
    assertEquals( 255, color.get( 1 ).asInt() );
    assertEquals( 255, color.get( 2 ).asInt() );
    assertEquals( 255, color.get( 3 ).asInt() );
  }

  @Test
  public void testSerializesFontAttribute() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.addAttribute( "foo", new Font( display, "Arial", 22, SWT.NONE ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray font = actualCell.get( "attributes" ).asObject().get( "foo" ).asArray();
    assertEquals( 4, font.size() );
    assertEquals( "Arial", font.get( 0 ).asArray().get( 0 ).asString() );
    assertEquals( 22, font.get( 1 ).asInt() );
    assertFalse( font.get( 2 ).asBoolean() );
    assertFalse( font.get( 3 ).asBoolean() );
  }

  @Test
  public void testSerializesFontDataAttribute() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.addAttribute( "foo", new Font( display, "Arial", 22, SWT.NONE ).getFontData()[ 0 ] );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray font = actualCell.get( "attributes" ).asObject().get( "foo" ).asArray();
    assertEquals( 4, font.size() );
    assertEquals( "Arial", font.get( 0 ).asArray().get( 0 ).asString() );
    assertEquals( 22, font.get( 1 ).asInt() );
    assertFalse( font.get( 2 ).asBoolean() );
    assertFalse( font.get( 3 ).asBoolean() );
  }

  @Test
  public void testSerializesPointAttribute() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.addAttribute( "foo", new Point( 23, 42 ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray point = actualCell.get( "attributes" ).asObject().get( "foo" ).asArray();
    assertEquals( 2, point.size() );
    assertEquals( 23, point.get( 0 ).asInt() );
    assertEquals( 42, point.get( 1 ).asInt() );
  }

  @Test
  public void testSerializesRectangleAttribute() {
    RowTemplate template = new RowTemplate();
    Cell cell = new Cell( template );
    cell.addAttribute( "foo", new Rectangle( 23, 42, 123, 142 ) );
    TemplateSerializer serializer = new TemplateSerializer( template );

    JsonObject object = serializer.toJson();

    JsonObject actualCell = object.get( "cells" ).asArray().get( 0 ).asObject();
    JsonArray point = actualCell.get( "attributes" ).asObject().get( "foo" ).asArray();
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
