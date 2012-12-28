/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.graphics.FontUtil;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Font_Test {

  private Device device;

  @Before
  public void setUp() {
    Fixture.setUp();
    device = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testConstructor() {
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    FontData fontData = FontUtil.getData( font );

    assertEquals( "roman", fontData.getName() );
    assertEquals( 1, fontData.getHeight() );
    assertEquals( SWT.NORMAL, fontData.getStyle() );
    assertEquals( "", fontData.getLocale() );
  }

  @Test
  public void testConstructorWithNullDevice() {
    device.dispose();
    try {
      new Font( null, "roman", 1, SWT.NONE );
      fail( "The device must not be null" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testConstructorWithNullName() {
    try {
      new Font( device, null, 1, SWT.NONE );
      fail( "The font name must not be null" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testConstructorWithNullNameHashcodeClash() {
    // see http://bugs.eclipse.org/320282
    new Font( device, "", 1, SWT.NONE );
    try {
      new Font( device, null, 1, SWT.NONE );
      fail( "The font name must not be null" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testConstructorWithNullFontData() {
    try {
      new Font( device, ( FontData )null );
      fail( "Must not allow FontData to be null" );
    } catch( Exception expected ) {
    }
  }

  @Test
  public void testConstructorWithNullFontDataArray() {
    try {
      new Font( device, ( FontData[] )null );
      fail( "Must not allow FontData[] to be null" );
    } catch( Exception expected ) {
    }
  }

  @Test
  public void testConstructorWithEmptyFontDataArray() {
    try {
      new Font( device, new FontData[ 0 ] );
      fail( "Must not allow to pass empty FontData array" );
    } catch( Exception expected ) {
    }
  }

  @Test
  public void testConstructorWithNullFontDataInArray() {
    try {
      new Font( device, new FontData[] { null } );
      fail( "FontData array must not contain null" );
    } catch( Exception expected ) {
    }
  }

  @Test
  public void testConstructorWithIllegalFontSize() {
    try {
      new Font( device, "abc", -1, SWT.NONE );
      fail( "The font size must not be negative" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testConstructorWithBogusStyle() {
    Font font = new Font( device, "roman", 1, 1 << 3 );
    assertEquals( SWT.NORMAL, FontUtil.getData( font ).getStyle() );
  }

  @Test
  public void testConstructorCreatesSafeCopy() {
    FontData fontData = new FontData( "roman", 1, SWT.NORMAL );
    Font font = new Font( device, fontData );
    fontData.setHeight( 23 );
    assertEquals( 1, FontUtil.getData( font ).getHeight() );
  }

  @Test
  public void testGetFontData() {
    Font font = new Font( device, "roman", 13, SWT.ITALIC );
    FontData[] fontDatas = font.getFontData();
    assertEquals( 1, fontDatas.length );
    assertEquals( "roman", fontDatas[ 0 ].getName() );
    assertEquals( 13, fontDatas[ 0 ].getHeight() );
    assertEquals( SWT.ITALIC, fontDatas[ 0 ].getStyle() );
  }

  @Test
  public void testGetFontDataCreatesSafeCopy() {
    Font font = new Font( device, "foo", 13, SWT.ITALIC );
    FontUtil.getData( font ).setName( "bar" );
    assertEquals( "foo", FontUtil.getData( font ).getName() );
  }

  @Test
  public void testGetFontDataAfterDispose() {
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    font.dispose();
    try {
      font.getFontData();
      fail( "Must not allow to access fontData of disposed font" );
    } catch( Exception expected ) {
    }
  }

  @Test
  public void testDispose() {
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    assertFalse( font.isDisposed() );
    font.dispose();
    assertTrue( font.isDisposed() );
  }

  @Test
  public void testEquality() {
    Font font1 = new Font( device, "roman", 1, SWT.NORMAL );
    Font font2 = new Font( device, "roman", 1, SWT.NORMAL );
    assertTrue( font1.equals( font2 ) );
    assertTrue( font2.equals( font1 ) );
  }

  @Test
  public void testEqualityWithSharedFont() {
    Font font1 = Graphics.getFont( "roman", 1, SWT.NORMAL );
    Font font2 = new Font( device, "roman", 1, SWT.NORMAL );
    assertTrue( font1.equals( font2 ) );
    assertTrue( font2.equals( font1 ) );
  }

  @Test
  public void testSerializeSessionFont() throws Exception {
    Font font = new Font( device, "roman", 1, SWT.NORMAL );

    Font deserializedFont = Fixture.serializeAndDeserialize( font );

    assertTrue( font.isDisposed() == deserializedFont.isDisposed() );
    assertEquals( font.getFontData().length, deserializedFont.getFontData().length );
    assertNotNull( deserializedFont.getDevice() );
    assertNotSame( font.getDevice(), deserializedFont.getDevice() );
    assertFontDataEquals( font.getFontData()[ 0 ], deserializedFont.getFontData()[ 0 ] );
  }

  @Test
  public void testSerializeSharedFont() throws Exception {
    Font font = Graphics.getFont( "roman", 1, SWT.NORMAL );

    Font deserializedFont = Fixture.serializeAndDeserialize( font );

    assertTrue( font.isDisposed() == deserializedFont.isDisposed() );
    assertEquals( font.getFontData().length, deserializedFont.getFontData().length );
    assertEquals( font.getFontData()[ 0 ], deserializedFont.getFontData()[ 0 ] );
  }

  private static void assertFontDataEquals( FontData expected, FontData actual ) {
    assertEquals( expected.getName(), actual.getName() );
    assertEquals( expected.getHeight(), actual.getHeight() );
    assertEquals( expected.getStyle(), actual.getStyle() );
    assertEquals( expected.getLocale(), actual.getLocale() );
  }

}
