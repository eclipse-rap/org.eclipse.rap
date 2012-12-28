/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.FontUtil;
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
  public void testColorToArray() {
    Color red = display.getSystemColor( SWT.COLOR_RED );

    int[] array = ProtocolUtil.getColorAsArray( red, false );

    checkColorArray( 255, 0, 0, 255, array );
  }

  @Test
  public void testColorToArray_RGB() {
    RGB red = new RGB( 255, 0, 0 );

    int[] array = ProtocolUtil.getColorAsArray( red, false );

    checkColorArray( 255, 0, 0, 255, array );
  }

  @Test
  public void testColorToArray_Transparent() {
    Color red = display.getSystemColor( SWT.COLOR_RED );

    int[] array = ProtocolUtil.getColorAsArray( red, true );

    checkColorArray( 255, 0, 0, 0, array );
  }

  @Test
  public void testColorToArray_Null() {
    assertNull( ProtocolUtil.getColorAsArray( ( Color )null, false ) );
  }

  @Test
  public void testFontAsArray() {
    Font font = new Font( display, "Arial", 22, SWT.NONE );

    Object[] array = ProtocolUtil.getFontAsArray( font );

    checkFontArray( new String[] { "Arial" }, 22, false, false, array );
  }

  @Test
  public void testFontAsArray_FontData() {
    Font font = new Font( display, "Arial", 22, SWT.NONE );

    Object[] array = ProtocolUtil.getFontAsArray( FontUtil.getData( font ) );

    checkFontArray( new String[] { "Arial" }, 22, false, false, array );
  }

  @Test
  public void testFontAsArray_Bold() {
    Font font = new Font( display, "Arial", 22, SWT.BOLD );

    Object[] array = ProtocolUtil.getFontAsArray( font );

    checkFontArray( new String[] { "Arial" }, 22, true, false, array );
  }

  @Test
  public void testFontAsArray_Italic() {
    Font font = new Font( display, "Arial", 22, SWT.ITALIC );

    Object[] array = ProtocolUtil.getFontAsArray( font );

    checkFontArray( new String[] { "Arial" }, 22, false, true, array );
  }

  @Test
  public void testFontAsArray_Null() {
    assertNull( ProtocolUtil.getFontAsArray( ( Font )null ) );
  }

  @Test
  public void testImageAsArray() {
    Image image = createImage( Fixture.IMAGE_100x50 );

    Object[] array = ProtocolUtil.getImageAsArray( image );

    assertNotNull( array[ 0 ] );
    assertEquals( Integer.valueOf( 100 ), array[ 1 ] );
    assertEquals( Integer.valueOf( 50 ), array[ 2 ] );
  }

  @Test
  public void testImageAsArray_Null() {
    assertNull( ProtocolUtil.getImageAsArray( null ) );
  }

  private void checkColorArray( int red, int green, int blue, int alpha, int[] array ) {
    assertEquals( red, array[ 0 ] );
    assertEquals( green, array[ 1 ] );
    assertEquals( blue, array[ 2 ] );
    assertEquals( alpha, array[ 3 ] );
  }

  private void checkFontArray( String[] names,
                               int size,
                               boolean bold,
                               boolean italic,
                               Object[] array )
  {
    Arrays.equals( names, ( String[] )array[ 0 ] );
    assertEquals( Integer.valueOf( size ), array[ 1 ] );
    assertEquals( Boolean.valueOf( bold ), array[ 2 ] );
    assertEquals( Boolean.valueOf( italic ), array[ 3 ] );
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
    assertTrue( message.getAllOperationsFor( "w3" ).length > 0 );
  }

  @Test
  public void testGetClientMessage_SameInstance() {
    fakeNewJsonMessage();

    ClientMessage message1 = ProtocolUtil.getClientMessage();
    ClientMessage message2 = ProtocolUtil.getClientMessage();

    assertSame( message1, message2 );
  }

  @Test
  public void testReadHeaderPropertyValue() {
    fakeNewJsonMessage();

    assertEquals( "21", ProtocolUtil.readHeadPropertyValue( "requestCounter" ) );
  }

  @Test
  public void testReadHeaderPropertyValue_MissingProperty() {
    fakeNewJsonMessage();

    assertNull( ProtocolUtil.readHeadPropertyValue( "abc" ) );
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
    Fixture.fakeSetParameter( "w3", "p1", "foo" );
    Fixture.fakeSetParameter( "w3", "p1", "bar" );

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

    Object[] expected = new Object[]{ "a", new Integer( 2 ), Boolean.TRUE };
    Object[] actual = ( Object[] )ProtocolUtil.readPropertyValue( "w3", "p8" );
    assertTrue( Arrays.equals( expected, actual ) );
  }

  @Test
  public void testWasCallSent() {
    fakeNewJsonMessage();

    assertTrue( ProtocolUtil.wasCallSend( "w3", "resize" ) );
    assertFalse( ProtocolUtil.wasCallSend( "w4", "resize" ) );
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

  //////////////////
  // Helping methods

  private void fakeNewJsonMessage() {
    Fixture.fakeNewRequest();
    Fixture.fakeHeadParameter( "requestCounter", Integer.valueOf( 21 ) );
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "p1", "foo" );
    parameters.put( "p2", Integer.valueOf( 123 ) );
    Fixture.fakeSetOperation( "w3", parameters  );
    parameters = new HashMap<String, Object>();
    parameters.put( "detail", "check" );
    Fixture.fakeNotifyOperation( "w3", "widgetSelected", parameters );
    parameters = new HashMap<String, Object>();
    parameters.put( "p3", Boolean.TRUE );
    parameters.put( "p4", null );
    parameters.put( "p5", new int[] { 1, 2 } );
    parameters.put( "p6", new int[] { 1, 2, 3, 4 } );
    parameters.put( "p7", new String[] { "a", "b", "c" } );
    parameters.put( "p8", new Object[]{ "a", new Integer( 2 ), Boolean.TRUE } );
    parameters.put( "p9", new boolean[] { true, false, true } );
    Fixture.fakeSetOperation( "w3", parameters  );
    parameters = new HashMap<String, Object>();
    parameters.put( "width", Integer.valueOf( 10 ) );
    Fixture.fakeCallOperation( "w3", "resize", parameters );
  }

  @SuppressWarnings( "resource" )
  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( name );
    return new Image( display, stream );
  }

}
