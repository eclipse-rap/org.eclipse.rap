/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
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

  public void testConstructor() {
    Device device = new Display();
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    FontData fontData = FontUtil.getData( font );
    assertEquals( "roman", fontData.getName() );
    assertEquals( 1, fontData.getHeight() );
    assertEquals( SWT.NORMAL, fontData.getStyle() );
    assertEquals( "", fontData.getLocale() );
  }
  
  public void testConstructorWithNullDevice() {
    try {
      new Font( null, "roman", 1, SWT.NONE );
      fail( "The device must not be null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testConstructorWithNullName() {
    Device device = new Display();
    try {
      new Font( device, null, 1, SWT.NONE );
      fail( "The font name must not be null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testConstructorWithNullNameHashcodeClash() {
    // see http://bugs.eclipse.org/320282
    Device device = new Display();
    new Font( device, "", 1, SWT.NONE );
    try {
      new Font( device, null, 1, SWT.NONE );
      fail( "The font name must not be null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testConstructorWithNullFontData() {
    Device device = new Display();
    try {
      new Font( device, ( FontData )null );
      fail( "Must not allow FontData to be null" );
    } catch( Exception e ) {
      // expected
    }
  }
  
  public void testConstructorWithNullFontDataArray() {
    Device device = new Display();
    try {
      new Font( device, ( FontData[] )null );
      fail( "Must not allow FontData[] to be null" );
    } catch( Exception e ) {
      // expected
    }
  }

  public void testConstructorWithEmptyFontDataArray() {
    Device device = new Display();
    try {
      new Font( device, new FontData[ 0 ] );
      fail( "Must not allow to pass empty FontData array" );
    } catch( Exception e ) {
      // expected
    }
  }

  public void testConstructorWithNullFontDataInArray() {
    Device device = new Display();
    try {
      new Font( device, new FontData[] { null } );
      fail( "FontData array must not contain null" );
    } catch( Exception e ) {
      // expected
    }
  }

  public void testConstructorWithIllegalFontSize() {
    Device device = new Display();
    try {
      new Font( device, "abc", -1, SWT.NONE );
      fail( "The font size must not be negative" );
    } catch( IllegalArgumentException e ) {
      // Expected
    }
  }

  public void testConstructorWithBogusStyle() {
    Device device = new Display();
    Font font = new Font( device, "roman", 1, 1 << 3 );
    assertEquals( SWT.NORMAL, FontUtil.getData( font ).getStyle() );
  }

  public void testConstructorCreatesSafeCopy() {
    Device device = new Display();
    FontData fontData = new FontData( "roman", 1, SWT.NORMAL );
    Font font = new Font( device, fontData );
    fontData.setHeight( 23 );
    assertEquals( 1, FontUtil.getData( font ).getHeight() );
  }

  public void testGetFontData() {
    Display display = new Display();
    Font font = new Font( display, "roman", 13, SWT.ITALIC );
    FontData[] fontDatas = font.getFontData();
    assertEquals( 1, fontDatas.length );
    assertEquals( "roman", fontDatas[ 0 ].getName() );
    assertEquals( 13, fontDatas[ 0 ].getHeight() );
    assertEquals( SWT.ITALIC, fontDatas[ 0 ].getStyle() );
  }

  public void testGetFontDataCreatesSafeCopy() {
    Display display = new Display();
    Font font = new Font( display, "foo", 13, SWT.ITALIC );
    FontUtil.getData( font ).setName( "bar" );
    assertEquals( "foo", FontUtil.getData( font ).getName() );
  }

  public void testGetFontDataAfterDispose() {
    Display display = new Display();
    Font font = new Font( display, "roman", 1, SWT.NORMAL );
    font.dispose();
    try {
      font.getFontData();
      fail( "Must not allow to access fontData of disposed font" );
    } catch( Exception e ) {
      // expected
    }
  }

  public void testDispose() {
    Display device = new Display();
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    assertFalse( font.isDisposed() );
    font.dispose();
    assertTrue( font.isDisposed() );
  }

  public void testEquality() {
    Device device = new Display();
    Font font1 = new Font( device, "roman", 1, SWT.NORMAL );
    Font font2 = new Font( device, "roman", 1, SWT.NORMAL );
    assertTrue( font1.equals( font2 ) );
    assertTrue( font2.equals( font1 ) );
  }

  public void testEqualityWithSharedFont() {
    Device device = new Display();
    Font font1 = Graphics.getFont( "roman", 1, SWT.NORMAL );
    Font font2 = new Font( device, "roman", 1, SWT.NORMAL );
    assertTrue( font1.equals( font2 ) );
    assertTrue( font2.equals( font1 ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
