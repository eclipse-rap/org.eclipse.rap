/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.graphics;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.graphics.FontUtil;
import org.eclipse.swt.widgets.Display;


public class Font_Test extends TestCase {

  private Device device;

  public void testConstructor() {
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    FontData fontData = FontUtil.getData( font );
    assertEquals( "roman", fontData.getName() );
    assertEquals( 1, fontData.getHeight() );
    assertEquals( SWT.NORMAL, fontData.getStyle() );
    assertEquals( "", fontData.getLocale() );
  }
  
  public void testConstructorWithNullDevice() {
    device.dispose();
    try {
      new Font( null, "roman", 1, SWT.NONE );
      fail( "The device must not be null" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testConstructorWithNullName() {
    try {
      new Font( device, null, 1, SWT.NONE );
      fail( "The font name must not be null" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testConstructorWithNullNameHashcodeClash() {
    // see http://bugs.eclipse.org/320282
    new Font( device, "", 1, SWT.NONE );
    try {
      new Font( device, null, 1, SWT.NONE );
      fail( "The font name must not be null" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testConstructorWithNullFontData() {
    try {
      new Font( device, ( FontData )null );
      fail( "Must not allow FontData to be null" );
    } catch( Exception expected ) {
    }
  }
  
  public void testConstructorWithNullFontDataArray() {
    try {
      new Font( device, ( FontData[] )null );
      fail( "Must not allow FontData[] to be null" );
    } catch( Exception expected ) {
    }
  }

  public void testConstructorWithEmptyFontDataArray() {
    try {
      new Font( device, new FontData[ 0 ] );
      fail( "Must not allow to pass empty FontData array" );
    } catch( Exception expected ) {
    }
  }

  public void testConstructorWithNullFontDataInArray() {
    try {
      new Font( device, new FontData[] { null } );
      fail( "FontData array must not contain null" );
    } catch( Exception expected ) {
    }
  }

  public void testConstructorWithIllegalFontSize() {
    try {
      new Font( device, "abc", -1, SWT.NONE );
      fail( "The font size must not be negative" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testConstructorWithBogusStyle() {
    Font font = new Font( device, "roman", 1, 1 << 3 );
    assertEquals( SWT.NORMAL, FontUtil.getData( font ).getStyle() );
  }

  public void testConstructorCreatesSafeCopy() {
    FontData fontData = new FontData( "roman", 1, SWT.NORMAL );
    Font font = new Font( device, fontData );
    fontData.setHeight( 23 );
    assertEquals( 1, FontUtil.getData( font ).getHeight() );
  }

  public void testGetFontData() {
    Font font = new Font( device, "roman", 13, SWT.ITALIC );
    FontData[] fontDatas = font.getFontData();
    assertEquals( 1, fontDatas.length );
    assertEquals( "roman", fontDatas[ 0 ].getName() );
    assertEquals( 13, fontDatas[ 0 ].getHeight() );
    assertEquals( SWT.ITALIC, fontDatas[ 0 ].getStyle() );
  }
  
  public void testGetFontDataCreatesSafeCopy() {
    Font font = new Font( device, "foo", 13, SWT.ITALIC );
    FontUtil.getData( font ).setName( "bar" );
    assertEquals( "foo", FontUtil.getData( font ).getName() );
  }

  public void testGetFontDataAfterDispose() {
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    font.dispose();
    try {
      font.getFontData();
      fail( "Must not allow to access fontData of disposed font" );
    } catch( Exception expected ) {
    }
  }

  public void testDispose() {
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    assertFalse( font.isDisposed() );
    font.dispose();
    assertTrue( font.isDisposed() );
  }

  public void testEquality() {
    Font font1 = new Font( device, "roman", 1, SWT.NORMAL );
    Font font2 = new Font( device, "roman", 1, SWT.NORMAL );
    assertTrue( font1.equals( font2 ) );
    assertTrue( font2.equals( font1 ) );
  }

  public void testEqualityWithSharedFont() {
    Font font1 = Graphics.getFont( "roman", 1, SWT.NORMAL );
    Font font2 = new Font( device, "roman", 1, SWT.NORMAL );
    assertTrue( font1.equals( font2 ) );
    assertTrue( font2.equals( font1 ) );
  }
  
  public void testSerializeSessionFont() throws Exception {
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    
    Font deserializedFont = Fixture.serializeAndDeserialize( font );
    
    assertEquals( font.isDisposed(), deserializedFont.isDisposed() );
    assertEquals( font.getFontData().length, deserializedFont.getFontData().length );
    assertNotNull( deserializedFont.getDevice() );
    assertNotSame( font.getDevice(), deserializedFont.getDevice() );
    assertEquals( font.getFontData()[ 0 ], deserializedFont.getFontData()[ 0 ] );
  }

  public void testSerializeSharedFont() throws Exception {
    Font font = Graphics.getFont( "roman", 1, SWT.NORMAL );
    
    Font deserializedFont = Fixture.serializeAndDeserialize( font );
    
    assertEquals( font.isDisposed(), deserializedFont.isDisposed() );
    assertEquals( font.getFontData().length, deserializedFont.getFontData().length );
    assertEquals( font.getFontData()[ 0 ], deserializedFont.getFontData()[ 0 ] );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    device = new Display();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private static void assertEquals( FontData fontData, FontData deserializedFontData ) {
    assertEquals( fontData.getName(), deserializedFontData.getName() );
    assertEquals( fontData.getHeight(), deserializedFontData.getHeight() );
    assertEquals( fontData.getStyle(), deserializedFontData.getStyle() );
    assertEquals( fontData.getLocale(), deserializedFontData.getLocale() );
  }
}
