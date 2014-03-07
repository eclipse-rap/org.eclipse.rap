/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ProtocolUtil_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetJsonForColor() {
    Color red = display.getSystemColor( SWT.COLOR_RED );

    JsonValue result = ProtocolUtil.getJsonForColor( red, false );

    assertEquals( JsonValue.readFrom( "[ 255, 0, 0, 255]" ), result );
  }

  @Test
  public void testGetJsonForColor_transparent() {
    Color red = display.getSystemColor( SWT.COLOR_RED );

    JsonValue result = ProtocolUtil.getJsonForColor( red, true );

    assertEquals( JsonValue.readFrom( "[ 255, 0, 0, 0]" ), result );
  }

  @Test
  public void testGetJsonForColor_null() {
    JsonValue result = ProtocolUtil.getJsonForColor( ( Color )null, false );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testGetJsonForColor_RGB() {
    RGB red = new RGB( 255, 0, 0 );

    JsonValue result = ProtocolUtil.getJsonForColor( red, false );

    assertEquals( JsonValue.readFrom( "[ 255, 0, 0, 255]" ), result );
  }

  @Test
  public void testGetJsonForFont() {
    Font font = new Font( display, "Arial", 22, SWT.NONE );

    JsonValue result = ProtocolUtil.getJsonForFont( font );

    assertEquals( JsonValue.readFrom( "[[\"Arial\"], 22, false, false ]" ), result );
  }

  @Test
  public void testGetJsonForFont_bold() {
    Font font = new Font( display, "Arial", 22, SWT.BOLD );

    JsonValue result = ProtocolUtil.getJsonForFont( font );

    assertEquals( JsonValue.readFrom( "[[\"Arial\"], 22, true, false ]" ), result );
  }

  @Test
  public void testGetJsonForFont_italic() {
    Font font = new Font( display, "Arial", 22, SWT.ITALIC );

    JsonValue result = ProtocolUtil.getJsonForFont( font );

    assertEquals( JsonValue.readFrom( "[[\"Arial\"], 22, false, true ]" ), result );
  }

  @Test
  public void testGetJsonForFont_null() {
    JsonValue result = ProtocolUtil.getJsonForFont( (Font) null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testGetJsonForFont_fontData() {
    Font font = new Font( display, "Arial", 22, SWT.NONE );

    JsonValue result = ProtocolUtil.getJsonForFont( font.getFontData()[0] );

    assertEquals( JsonValue.readFrom( "[[\"Arial\"], 22, false, false ]" ), result );
  }

  @Test
  public void testGetJsonForFont_fontData_null() {
    JsonValue result = ProtocolUtil.getJsonForFont( (FontData) null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testGetJsonForPoint() {
    JsonValue result = ProtocolUtil.getJsonForPoint( new Point( 23, 42 ) );

    assertEquals( JsonValue.readFrom( "[ 23, 42 ]" ), result );
  }

  @Test
  public void testGetJsonForPoint_null() {
    JsonValue result = ProtocolUtil.getJsonForPoint( (Point) null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testGetJsonForRectangle() {
    JsonValue result = ProtocolUtil.getJsonForRectangle( new Rectangle( 1, 2, 3, 4 ) );

    assertEquals( JsonValue.readFrom( "[ 1, 2, 3, 4 ]" ), result );
  }

  @Test
  public void testGetJsonForRectangle_null() {
    JsonValue result = ProtocolUtil.getJsonForRectangle( (Rectangle) null );

    assertEquals( JsonValue.NULL, result );
  }

  @Test
  public void testGetJsonForImage() {
    Image image = createImage( Fixture.IMAGE_100x50 );

    JsonValue result = ProtocolUtil.getJsonForImage( image );

    JsonArray array = result.asArray();
    assertTrue( array.get( 0 ).isString() );
    array.set( 0, "" );
    assertEquals( new JsonArray().add( "" ).add( 100 ).add( 50 ), array );
  }

  @Test
  public void testJsonForImage_null() {
    assertEquals( JsonValue.NULL, ProtocolUtil.getJsonForImage( null ) );
  }

  @Test
  public void testIsClientMessageProcessed_No() {
    assertFalse( ProtocolUtil.isClientMessageProcessed() );
  }

  @Test
  public void testIsClientMessageProcessed_Yes() {
    ProtocolUtil.getClientMessage();

    assertTrue( ProtocolUtil.isClientMessageProcessed() );
  }

  @Test
  public void testGetClientMessage() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.TRUE );

    ClientMessage message = ProtocolUtil.getClientMessage();

    assertNotNull( message );
    assertFalse( message.getAllOperationsFor( "w3" ).isEmpty() );
  }

  @Test
  public void testGetClientMessage_SameInstance() {
    ClientMessage message1 = ProtocolUtil.getClientMessage();
    ClientMessage message2 = ProtocolUtil.getClientMessage();

    assertSame( message1, message2 );
  }

  @Test
  public void testReadProperyValue_MissingProperty() {
    assertNull( ProtocolUtil.readPropertyValueAsString( "w3", "p0" ) );
  }

  @Test
  public void testReadProperyValueAsString_string() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.valueOf( "foo" ) );

    assertEquals( "foo", ProtocolUtil.readPropertyValueAsString( "w3", "prop" ) );
  }

  @Test
  public void testReadProperyValueAsString_int() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.valueOf( 23 ) );

    assertEquals( "23", ProtocolUtil.readPropertyValueAsString( "w3", "prop" ) );
  }

  @Test
  public void testReadProperyValueAsString_boolean() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.TRUE );

    assertEquals( "true", ProtocolUtil.readPropertyValueAsString( "w3", "prop" ) );
  }

  @Test
  public void testReadProperyValueAsString_null() {
    Fixture.fakeSetProperty( "w3", "prop", JsonValue.NULL );

    assertEquals( "null", ProtocolUtil.readPropertyValueAsString( "w3", "prop" ) );
  }

  @Test
  public void testReadPropertyValue_LastSetValue() {
    Fixture.fakeSetProperty( "w3", "p1", "foo" );
    Fixture.fakeSetProperty( "w3", "p1", "bar" );

    assertEquals( "bar", ProtocolUtil.readPropertyValueAsString( "w3", "p1" ) );
  }

  @Test
  public void testReadEventPropertyValue_MissingProperty() {
    assertNull( ProtocolUtil.readEventPropertyValueAsString( "w3", "widgetSelected", "item" ) );
  }

  @Test
  public void testReadEventPropertyValue() {
    Fixture.fakeNotifyOperation( "w3", "widgetSelected", new JsonObject().add( "detail", "check" ) );

    String value = ProtocolUtil.readEventPropertyValueAsString( "w3", "widgetSelected", "detail" );

    assertEquals( "check", value );
  }

  @Test
  public void testWasEventSent_sent() {
    Fixture.fakeNotifyOperation( "w3", "widgetSelected", new JsonObject() );

    assertTrue( ProtocolUtil.wasEventSent( "w3", "widgetSelected" ) );
  }

  @Test
  public void testWasEventSend_notSent() {
    Fixture.fakeNotifyOperation( "w3", "widgetSelected", new JsonObject() );

    assertFalse( ProtocolUtil.wasEventSent( "w3", "widgetDefaultSelected" ) );
  }

  @Test
  public void testReadPropertyValueAsPoint() {
    Fixture.fakeSetProperty( "w3", "prop", createJsonArray( 1, 2 ) );

    assertEquals( new Point( 1, 2 ), ProtocolUtil.readPropertyValueAsPoint( "w3", "prop" ) );
  }

  @Test( expected = IllegalStateException.class )
  public void testReadPropertyValueAsPoint_notPoint() {
    Fixture.fakeSetProperty( "w3", "prop", createJsonArray( 1, 2, 3, 4 ) );

    ProtocolUtil.readPropertyValueAsPoint( "w3", "prop" );
  }

  @Test
  public void testReadPropertyValueAsRectangle() {
    Fixture.fakeSetProperty( "w3", "prop", createJsonArray( 1, 2, 3, 4 ) );

    Rectangle expected = new Rectangle( 1, 2, 3, 4 );
    assertEquals( expected, ProtocolUtil.readPropertyValueAsRectangle( "w3", "prop" ) );
  }

  @Test( expected = IllegalStateException.class )
  public void testReadPropertyValueAsRectangle_notRectangle() {
    Fixture.fakeSetProperty( "w3", "prop", createJsonArray( 1, 2 ) );

    ProtocolUtil.readPropertyValueAsRectangle( "w3", "prop" );
  }

  @Test
  public void testReadPropertyValueAsIntArray() {
    Fixture.fakeSetProperty( "w3", "prop", createJsonArray( 1, 2, 3, 4 ) );

    int[] result = ProtocolUtil.readPropertyValueAsIntArray( "w3", "prop" );

    int[] expected = { 1, 2, 3, 4 };
    assertArrayEquals( expected, result );
  }

  @Test
  public void testReadPropertyValueAsBooleanArray() {
    Fixture.fakeSetProperty( "w3", "prop", createJsonArray( true, false, true ) );

    boolean[] result = ProtocolUtil.readPropertyValueAsBooleanArray( "w3", "prop" );

    boolean[] expected = { true, false, true };
    assertTrue( Arrays.equals( expected, result ) );
  }

  @Test( expected = IllegalStateException.class )
  public void testReadPropertyValueAsBooleanArray_NotBoolean() {
    Fixture.fakeSetProperty( "w3", "prop", createJsonArray( "a", "b", "c" ) );

    ProtocolUtil.readPropertyValueAsBooleanArray( "w3", "prop" );
  }

  @Test
  public void testReadPropertyValueAsStringArray() {
    Fixture.fakeSetProperty( "w3", "prop", createJsonArray( "a", "b", "c" ) );

    String[] result = ProtocolUtil.readPropertyValueAsStringArray( "w3", "prop" );

    String[] expected = { "a", "b", "c" };
    assertTrue( Arrays.equals( expected, result ) );
  }

  @Test( expected = IllegalStateException.class )
  public void testReadPropertyValueAsStringArray_NotString() {
    Fixture.fakeSetProperty( "w3", "prop", createJsonArray( 1, 2, 3, 4 ) );

    ProtocolUtil.readPropertyValueAsStringArray( "w3", "prop" );
  }

  @Test
  public void testReadPropertyValue() {
    Fixture.fakeSetProperty( "w3", "prop", new JsonArray().add( "a" ).add( 2 ).add( true ) );

    JsonValue result = ProtocolUtil.readPropertyValue( "w3", "prop" );

    JsonValue expected = JsonValue.readFrom( "[\"a\", 2, true]" );
    assertEquals( expected, result );
  }

  @Test
  public void testWasCallReceived() {
    Fixture.fakeCallOperation( "w3", "resize", null );

    assertTrue( ProtocolUtil.wasCallReceived( "w3", "resize" ) );
    assertFalse( ProtocolUtil.wasCallReceived( "w4", "resize" ) );
  }

  @Test
  public void testReadCallProperty() {
    Fixture.fakeCallOperation( "w3", "resize", new JsonObject().add( "width", 10 ) );

    assertEquals( "10", ProtocolUtil.readCallPropertyValueAsString( "w3", "resize", "width" ) );
  }

  @Test
  public void testReadCallProperty_missingProperty() {
    Fixture.fakeCallOperation( "w3", "resize", null );

    assertNull( ProtocolUtil.readCallPropertyValueAsString( "w3", "resize", "width" ) );
  }

  @Test
  public void testReadCallProperty_missingOperation() {
    assertNull( ProtocolUtil.readCallPropertyValueAsString( "w3", "resize", "width" ) );
  }

  @Test
  public void testToPoint() {
    JsonValue value = new JsonArray().add( 23 ).add( 42 );

    Point result = ProtocolUtil.toPoint( value );

    assertEquals( new Point( 23, 42 ), result );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToPoint_withNull() {
    ProtocolUtil.toPoint( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToPoint_withIllegalValue() {
    ProtocolUtil.toPoint( JsonValue.valueOf( true ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToPoint_withWrongArraySize() {
    ProtocolUtil.toPoint( new JsonArray().add( 1 ).add( 2 ).add( 3 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToPoint_withWrongElementType() {
    ProtocolUtil.toPoint( new JsonArray().add( 1 ).add( true ) );
  }

  @Test
  public void testToRectangle() {
    JsonValue value = new JsonArray().add( 1 ).add( 2 ).add( 3 ).add( 4 );

    Rectangle result = ProtocolUtil.toRectangle( value );

    assertEquals( new Rectangle( 1, 2, 3, 4 ), result );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToRectangle_withNull() {
    ProtocolUtil.toRectangle( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToRectangle_withIllegalValue() {
    ProtocolUtil.toRectangle( JsonValue.valueOf( true ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToRectangle_withWrongArraySize() {
    ProtocolUtil.toRectangle( new JsonArray().add( 1 ).add( 2 ).add( 3 ).add( 4 ).add( 5 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testToRectangle_withWrongElementType() {
    ProtocolUtil.toRectangle( new JsonArray().add( 1 ).add( true ) );
  }

  @SuppressWarnings( "resource" )
  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( name );
    return new Image( display, stream );
  }

}
