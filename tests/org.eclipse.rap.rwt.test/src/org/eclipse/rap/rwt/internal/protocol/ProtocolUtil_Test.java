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

    JsonArray result = ProtocolUtil.getJsonForImage( image ).asArray();

    assertEquals( 3, result.size() );
    assertTrue( result.get( 0 ).isString() );
    assertEquals( 100, result.get( 1 ).asInt() );
    assertEquals( 50, result.get( 2 ).asInt() );
  }

  @Test
  public void testJsonForImage_null() {
    assertEquals( JsonValue.NULL, ProtocolUtil.getJsonForImage( null ) );
  }

  @Test
  public void testIsClientMessageProcessed_No() {
    fakeNewJsonMessage();

    assertFalse( ProtocolUtil.isClientMessageProcessed() );
  }

  @Test
  public void testIsClientMessageProcessed_Yes() {
    fakeNewJsonMessage();

    ProtocolUtil.getClientMessage();

    assertTrue( ProtocolUtil.isClientMessageProcessed() );
  }

  @Test
  public void testGetClientMessage() {
    fakeNewJsonMessage();

    ClientMessage message = ProtocolUtil.getClientMessage();

    assertNotNull( message );
    assertFalse( message.getAllOperationsFor( "w3" ).isEmpty() );
  }

  @Test
  public void testGetClientMessage_SameInstance() {
    fakeNewJsonMessage();

    ClientMessage message1 = ProtocolUtil.getClientMessage();
    ClientMessage message2 = ProtocolUtil.getClientMessage();

    assertSame( message1, message2 );
  }

  @Test
  public void testReadProperyValue_MissingProperty() {
    fakeNewJsonMessage();

    assertNull( ProtocolUtil.readPropertyValueAsString( "w3", "p0" ) );
  }

  @Test
  public void testReadProperyValueAsString_String() {
    fakeNewJsonMessage();

    assertEquals( "foo", ProtocolUtil.readPropertyValueAsString( "w3", "p1" ) );
  }

  @Test
  public void testReadProperyValueAsString_Integer() {
    fakeNewJsonMessage();

    assertEquals( "123", ProtocolUtil.readPropertyValueAsString( "w3", "p2" ) );
  }

  @Test
  public void testReadProperyValueAsString_Boolean() {
    fakeNewJsonMessage();

    assertEquals( "true", ProtocolUtil.readPropertyValueAsString( "w3", "p3" ) );
  }

  @Test
  public void testReadProperyValueAsString_Null() {
    fakeNewJsonMessage();

    assertEquals( "null", ProtocolUtil.readPropertyValueAsString( "w3", "p4" ) );
  }

  @Test
  public void testReadPropertyValue_LastSetValue() {
    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( "w3", "p1", "foo" );
    Fixture.fakeSetProperty( "w3", "p1", "bar" );

    assertEquals( "bar", ProtocolUtil.readPropertyValueAsString( "w3", "p1" ) );
  }

  @Test
  public void testReadEventPropertyValue_MissingProperty() {
    fakeNewJsonMessage();

    assertNull( ProtocolUtil.readEventPropertyValueAsString( "w3", "widgetSelected", "item" ) );
  }

  @Test
  public void testReadEventPropertyValue() {
    fakeNewJsonMessage();

    String value = ProtocolUtil.readEventPropertyValueAsString( "w3", "widgetSelected", "detail" );
    assertEquals( "check", value );
  }

  @Test
  public void testWasEventSend_Send() {
    fakeNewJsonMessage();

    assertTrue( ProtocolUtil.wasEventSent( "w3", "widgetSelected" ) );
  }

  @Test
  public void testWasEventSend_NotSend() {
    fakeNewJsonMessage();

    assertFalse( ProtocolUtil.wasEventSent( "w3", "widgetDefaultSelected" ) );
  }

  @Test
  public void testReadPropertyValueAsPoint() {
    fakeNewJsonMessage();

    assertEquals( new Point( 1, 2 ), ProtocolUtil.readPropertyValueAsPoint( "w3", "p5" ) );
  }

  @Test
  public void testReadPropertyValueAsPoint_NotPoint() {
    fakeNewJsonMessage();

    try {
      ProtocolUtil.readPropertyValueAsPoint( "w3", "p6" );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testReadPropertyValueAsRectangle() {
    fakeNewJsonMessage();

    Rectangle expected = new Rectangle( 1, 2, 3, 4 );
    assertEquals( expected, ProtocolUtil.readPropertyValueAsRectangle( "w3", "p6" ) );
  }

  @Test
  public void testReadPropertyValueAsRectangle_NotRectangle() {
    fakeNewJsonMessage();

    try {
      ProtocolUtil.readPropertyValueAsRectangle( "w3", "p5" );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testReadPropertyValueAsIntArray() {
    fakeNewJsonMessage();

    int[] expected = new int[]{ 1, 2, 3, 4 };
    int[] actual = ProtocolUtil.readPropertyValueAsIntArray( "w3", "p6" );
    assertTrue( Arrays.equals( expected, actual ) );
  }

  @Test
  public void testReadPropertyValueAsBooleanArray() {
    fakeNewJsonMessage();

    boolean[] expected = new boolean[]{ true, false, true };
    boolean[] actual = ProtocolUtil.readPropertyValueAsBooleanArray( "w3", "p9" );
    assertTrue( Arrays.equals( expected, actual ) );
  }

  @Test
  public void testReadPropertyValueAsBooleanArray_NotBoolean() {
    fakeNewJsonMessage();

    try {
      ProtocolUtil.readPropertyValueAsBooleanArray( "w3", "p7" );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testReadPropertyValueAsStringArray() {
    fakeNewJsonMessage();

    String[] expected = new String[]{ "a", "b", "c" };
    String[] actual = ProtocolUtil.readPropertyValueAsStringArray( "w3", "p7" );
    assertTrue( Arrays.equals( expected, actual ) );
  }

  @Test
  public void testReadPropertyValueAsStringArray_NotString() {
    fakeNewJsonMessage();

    try {
      ProtocolUtil.readPropertyValueAsStringArray( "w3", "p6" );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testReadPropertyValue() {
    fakeNewJsonMessage();

    JsonValue expected = JsonValue.readFrom( "[\"a\", 2, true]" );
    JsonValue actual = ProtocolUtil.readPropertyValue( "w3", "p8" );
    assertEquals( expected, actual );
  }

  @Test
    public void testWasCallReceived() {
      fakeNewJsonMessage();

      assertTrue( ProtocolUtil.wasCallReceived( "w3", "resize" ) );
      assertFalse( ProtocolUtil.wasCallReceived( "w4", "resize" ) );
    }

  @Test
  public void testReadCallProperty() {
    fakeNewJsonMessage();

    assertEquals( "10", ProtocolUtil.readCallPropertyValueAsString( "w3", "resize", "width" ) );
  }

  @Test
  public void testReadCallProperty_MissingProperty() {
    fakeNewJsonMessage();

    assertNull( ProtocolUtil.readCallPropertyValueAsString( "w3", "resize", "left" ) );
  }

  @Test
  public void testReadCallProperty_MissingOperation() {
    fakeNewJsonMessage();

    assertNull( ProtocolUtil.readCallPropertyValueAsString( "w4", "resize", "left" ) );
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

  //////////////////
  // Helping methods

  private void fakeNewJsonMessage() {
    Fixture.fakeNewRequest();
    Fixture.fakeHeadParameter( "requestCounter", 21 );
    JsonObject setProperties1 = new JsonObject()
      .add( "p1", "foo" )
      .add( "p2", 123 );
    Fixture.fakeSetOperation( "w3", setProperties1  );
    JsonObject notifyParameters = new JsonObject().add( "detail", "check" );
    Fixture.fakeNotifyOperation( "w3", "widgetSelected", notifyParameters );
    JsonObject setProperites2 = new JsonObject()
      .add( "p3", true )
      .add( "p4", JsonValue.NULL )
      .add( "p5", createJsonArray( 1, 2 ) )
      .add( "p6", createJsonArray( 1, 2, 3, 4 ) )
      .add( "p7", createJsonArray( "a", "b", "c" ) )
      .add( "p8", new JsonArray().add( "a" ).add( 2 ).add( true ) )
      .add( "p9", createJsonArray( true, false, true ) );
    Fixture.fakeSetOperation( "w3", setProperites2  );
    JsonObject callParameters = new JsonObject().add( "width", 10 );
    Fixture.fakeCallOperation( "w3", "resize", callParameters );
  }

  @SuppressWarnings( "resource" )
  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( name );
    return new Image( display, stream );
  }

}
