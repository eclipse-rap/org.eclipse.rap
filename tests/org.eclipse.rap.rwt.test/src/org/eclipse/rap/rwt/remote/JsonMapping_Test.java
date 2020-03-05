/*******************************************************************************
 * Copyright (c) 2014, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.remote;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JsonMapping_Test {

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

  @Test
  public void testToJson_Widget() {
    Shell shell = new Shell( display );

    JsonValue result = JsonMapping.toJson( shell );

    assertEquals( JsonValue.valueOf( getId( shell ) ), result );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToJson_Widget_disposed() {
    Shell shell = new Shell( display );
    shell.dispose();

    JsonMapping.toJson( shell );
  }

  @Test
  public void testToJson_Widget_null() {
    JsonValue result = JsonMapping.toJson( (Widget)null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testToJson_Point() {
    JsonValue result = JsonMapping.toJson( new Point( 1, 2 ) );

    assertEquals( JsonValue.readFrom( "[1, 2]" ), result );
  }

  @Test
  public void testToJson_Point_null() {
    JsonValue result = JsonMapping.toJson( (Point) null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testToJson_PointsArray() {
    JsonValue result = JsonMapping.toJson( new Point[] { new Point( 1, 2 ), new Point( 3, 4 ) } );

    assertEquals( JsonValue.readFrom( "[[1, 2],[3, 4]]" ), result );
  }

  @Test
  public void testToJson_PointsArray_null() {
    JsonValue result = JsonMapping.toJson( (Point[]) null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testToJson_Rectangle() {
    JsonValue result = JsonMapping.toJson( new Rectangle( 1, 2, 3, 4 ) );

    assertEquals( JsonValue.readFrom( "[1, 2, 3, 4]" ), result );
  }

  @Test
  public void testToJson_Rectangle_null() {
    JsonValue result = JsonMapping.toJson( (Rectangle) null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testToJson_Color() {
    Color color = new Color( display, 1, 2, 3 );

    JsonValue result = JsonMapping.toJson( color );

    assertEquals( JsonValue.readFrom( "[1, 2, 3, 255]" ), result );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToJson_Color_disposed() {
    Color color = new Color( display, 1, 2, 3 );
    color.dispose();

    JsonMapping.toJson( color );
  }

  @Test
  public void testToJson_Color_null() {
    JsonValue result = JsonMapping.toJson( ( Color )null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testToJson_ColorWithAlpha() {
    Color color = new Color( display, 1, 2, 3 );

    JsonValue result = JsonMapping.toJson( color, 4 );

    assertEquals( JsonValue.readFrom( "[1, 2, 3, 4]" ), result );
  }

  @Test
  public void testToJson_ColorWithAlpha_null() {
    JsonValue result = JsonMapping.toJson( ( Color )null, 23 );

    assertEquals( JsonValue.NULL, result );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToJson_ColorWithAlpha_rejectsNegativeAlpha() {
    JsonMapping.toJson( (Color)null, -1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToJson_ColorWithAlpha_rejectsExceedingAlpha() {
    JsonMapping.toJson( (Color)null, 256 );
  }

  @Test
  public void testToJson_RGB() {
    RGB rgb = new RGB( 1, 2, 3 );

    JsonValue result = JsonMapping.toJson( rgb );

    assertEquals( JsonValue.readFrom( "[1, 2, 3, 255]" ), result );
  }

  @Test
  public void testToJson_RGB_null() {
    JsonValue result = JsonMapping.toJson( (RGB)null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testToJson_RGBWithAlpha() {
    RGB rgb = new RGB( 1, 2, 3 );

    JsonValue result = JsonMapping.toJson( rgb, 4 );

    assertEquals( JsonValue.readFrom( "[1, 2, 3, 4]" ), result );
  }

  @Test
  public void testToJson_RGBWithAlpha_null() {
    JsonValue result = JsonMapping.toJson( (RGB)null, 23 );

    assertEquals( JsonValue.NULL, result );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToJson_RGBWithAlpha_rejectsNegativeAlpha() {
    JsonMapping.toJson( (RGB)null, -1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToJson_RGBWithAlpha_rejectsExceedingAlpha() {
    JsonMapping.toJson( (RGB)null, 256 );
  }

  @Test
  public void testToJson_Image() throws IOException {
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    JsonValue result = JsonMapping.toJson( image );

    JsonArray array = result.asArray();
    assertTrue( array.get( 0 ).isString() );
    array.set( 0, "" );
    assertEquals( new JsonArray().add( "" ).add( 100 ).add( 50 ), array );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToJson_Image_disposed() throws IOException {
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );
    image.dispose();

    JsonMapping.toJson( image );
  }

  @Test
  public void testToJson_Image_null() {
    assertEquals( JsonValue.NULL, JsonMapping.toJson( (Image)null ) );
  }

  @Test
  public void testToJson_Font() {
    Font font = new Font( display, "Arial", 23, SWT.NONE );

    JsonValue result = JsonMapping.toJson( font );

    assertEquals( JsonValue.readFrom( "[[\"Arial\"], 23, false, false ]" ), result );
  }

  @Test
  public void testToJson_Font_bold() {
    Font font = new Font( display, "Arial", 23, SWT.BOLD );

    JsonValue result = JsonMapping.toJson( font );

    assertEquals( JsonValue.readFrom( "[[\"Arial\"], 23, true, false ]" ), result );
  }

  @Test
  public void testToJson_Font_italic() {
    Font font = new Font( display, "Arial", 23, SWT.ITALIC );

    JsonValue result = JsonMapping.toJson( font );

    assertEquals( JsonValue.readFrom( "[[\"Arial\"], 23, false, true ]" ), result );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToJson_Font_disposed() {
    Font font = new Font( display, "Arial", 23, SWT.ITALIC );
    font.dispose();

    JsonMapping.toJson( font );
  }

  @Test
  public void testToJson_Font_null() {
    JsonValue result = JsonMapping.toJson( (Font) null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testToJson_FontData() {
    FontData fontData = new FontData( "Arial", 23, SWT.NONE );

    JsonValue result = JsonMapping.toJson( fontData );

    assertEquals( JsonValue.readFrom( "[[\"Arial\"], 23, false, false ]" ), result );
  }

  @Test
  public void testToJson_FontData_bold() {
    FontData fontData = new FontData( "Arial", 23, SWT.BOLD );

    JsonValue result = JsonMapping.toJson( fontData );

    assertEquals( JsonValue.readFrom( "[[\"Arial\"], 23, true, false ]" ), result );
  }

  @Test
  public void testToJson_FontData_italic() {
    FontData fontData = new FontData( "Arial", 23, SWT.ITALIC );

    JsonValue result = JsonMapping.toJson( fontData );

    assertEquals( JsonValue.readFrom( "[[\"Arial\"], 23, false, true ]" ), result );
  }

  @Test
  public void testToJson_FontData_null() {
    JsonValue result = JsonMapping.toJson( (FontData) null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testReadPoint() {
    JsonValue value = createJsonArray( 1, 2 );

    Point result = JsonMapping.readPoint( value );

    assertEquals( new Point( 1, 2 ), result );
  }

  @Test
  public void testReadPoint_jsonNull() {
    Point point = JsonMapping.readPoint( JsonValue.NULL );

    assertNull( point );
  }

  @Test( expected = NullPointerException.class )
  public void testReadPoint_null() {
    JsonMapping.readPoint( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadPoint_illegalValue() {
    JsonMapping.readPoint( JsonValue.FALSE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadPoint_wrongArraySize() {
    JsonMapping.readPoint( createJsonArray( 1, 2, 3 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadPoint_wrongElementType() {
    JsonMapping.readPoint( new JsonArray().add( 1 ).add( true ) );
  }

  @Test
  public void testReadRectangle() {
    JsonValue value = createJsonArray( 1, 2, 3, 4 );

    Rectangle result = JsonMapping.readRectangle( value );

    assertEquals( new Rectangle( 1, 2, 3, 4 ), result );
  }

  @Test
  public void testReadRectangle_jsonNull() {
    Rectangle rectangle = JsonMapping.readRectangle( JsonValue.NULL );

    assertNull( rectangle );
  }

  @Test( expected = NullPointerException.class )
  public void testReadRectangle_null() {
    JsonMapping.readRectangle( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadRectangle_illegalValue() {
    JsonMapping.readRectangle( JsonValue.FALSE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadRectangle_arrayTooShort() {
    JsonMapping.readRectangle( createJsonArray( 1, 2, 3, 4, 5 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadRectangle_arrayTooLong() {
    JsonMapping.readRectangle( createJsonArray( 1, 2, 3, 4, 5 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadRectangle_illegalElementType() {
    JsonMapping.readRectangle( new JsonArray().add( 1 ).add( true ) );
  }

  @Test
  public void testReadRGB() {
    JsonValue value = createJsonArray( 0, 1, 2 );

    RGB result = JsonMapping.readRGB( value );

    assertEquals( new RGB( 0, 1, 2 ), result );
  }

  @Test
  public void testReadRGB_withAlpha() {
    JsonValue value = createJsonArray( 0, 1, 2, 3 );

    RGB result = JsonMapping.readRGB( value );

    assertEquals( new RGB( 0, 1, 2 ), result );
  }

  @Test
  public void testReadRGB_jsonNull() {
    RGB result = JsonMapping.readRGB( JsonValue.NULL );

    assertNull( result );
  }

  @Test( expected = NullPointerException.class )
  public void testReadRGB_null() {
    JsonMapping.readRGB( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadRGB_illegalValue() {
    JsonMapping.readRGB( JsonValue.FALSE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadRGB_arrayTooShort() {
    JsonMapping.readRGB( createJsonArray( 1, 2 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadRGB_arrayTooLong() {
    JsonMapping.readRGB( createJsonArray( 1, 2, 3, 4, 5 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testReadRGB_illegalElementType() {
    JsonMapping.readRGB( createJsonArray( -1, 2, 3 ) );
  }

}
