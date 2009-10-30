/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public class Font_Test extends TestCase {

  public void testGetFont() {
    Font font = Graphics.getFont( "roman", 1, SWT.NORMAL );
    assertEquals( "roman", font.getFontData()[ 0 ].getName() );
    assertEquals( 1, font.getFontData()[ 0 ].getHeight() );
    assertEquals( SWT.NORMAL, font.getFontData()[ 0 ].getStyle() );
    Font sameFont = Graphics.getFont( "roman", 1, SWT.NORMAL );
    assertSame( font, sameFont );
    Font otherFont = Graphics.getFont( "arial", 2, SWT.NORMAL );
    assertTrue( otherFont != font );
    Font boldFont = Graphics.getFont( "arial", 11, SWT.BOLD );
    assertTrue( ( boldFont.getFontData()[ 0 ].getStyle() & SWT.BOLD ) != 0 ) ;
    Font italicFont = Graphics.getFont( "arial", 11, SWT.ITALIC );
    int italicFontStyle = italicFont.getFontData()[ 0 ].getStyle();
    assertTrue( ( italicFontStyle & SWT.ITALIC ) != 0 );

    sameFont = Graphics.getFont( new FontData( "roman", 1, SWT.NORMAL ) );
    assertSame( font, sameFont );
    assertSame( font.getFontData()[ 0 ], font.getFontData()[ 0 ] );

    Font arial13Normal = Graphics.getFont( "arial", 13, SWT.NORMAL );
    Font arial12Bold = Graphics.getFont( "arial", 12, SWT.BOLD );
    assertNotSame( arial13Normal, arial12Bold );
  }

  public void testGetFontData() {
    // Derive bold font from regular font
    Font regularFont = Graphics.getFont( "roman", 1, SWT.NORMAL );
    FontData[] fontDatas = regularFont.getFontData();
    fontDatas[ 0 ] = new FontData( fontDatas[ 0 ].getName(),
                                   fontDatas[ 0 ].getHeight(),
                                   fontDatas[ 0 ].getStyle() | SWT.BOLD );
    Font boldFont = Graphics.getFont( fontDatas[ 0 ] );
    // Ensure bold font is actually bold
    assertEquals( boldFont.getFontData()[ 0 ].getStyle(), SWT.BOLD );
    // Ensure that the font we derived from stays the same
    assertEquals( regularFont.getFontData()[ 0 ].getStyle(), SWT.NORMAL );
  }
  
  public void testGetFontDataAfterDispose() {
    Font font = new Font( new Display(), "roman", 1, SWT.NORMAL );
    font.dispose();
    try {
      font.getFontData();
      fail( "Must not allow to access fontData of disposed font" );
    } catch( Exception e ) {
      // expected
    }
  }
  
  public void testGetFontWithIllegalArguments() {
    try {
      Graphics.getFont( null, 1, SWT.NONE );
      fail( "The font name must not be null" );
    } catch( IllegalArgumentException e ) {
      // Expected
    }
    try {
      Graphics.getFont( "abc", -1, SWT.NONE );
      fail( "The font size must not be negative" );
    } catch( IllegalArgumentException e ) {
      // Expected
    }
    Font font = Graphics.getFont( "roman", 1, 1 << 3 );
    assertEquals( SWT.NORMAL, font.getFontData()[ 0 ].getStyle() );
  }
  
  public void testConstructor() {
    Device device = new Display();
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    FontData fontData = font.getFontData()[ 0 ];
    assertEquals( "roman", fontData.getName() );
    assertEquals( 1, fontData.getHeight() );
    assertEquals( SWT.NORMAL, fontData.getStyle() );
  }
  
  public void testConstructorWithNullDevice() {
    Device device = new Display();
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    assertSame( Display.getCurrent(), font.getDevice() );
  }
  
  public void testConstructorWithIllegalArguments() {
    try {
      new Font( null, "roman", 1, SWT.NONE );
      fail( "The device must not be null" );
    } catch( IllegalArgumentException e ) {
      // Expected
    }
    Device device = new Display();
    try {
      new Font( device, null, 1, SWT.NONE );
      fail( "The font name must not be null" );
    } catch( IllegalArgumentException e ) {
      // Expected
    }
    try {
      new Font( device, "abc", -1, SWT.NONE );
      fail( "The font size must not be negative" );
    } catch( IllegalArgumentException e ) {
      // Expected
    }
    try {
      new Font( device, ( FontData )null );
      fail( "Must not allow FontData to be null" );
    } catch( Exception e ) {
      // expected
    }
    try {
      new Font( device, ( FontData[] )null );
      fail( "Must not allow FontData[] to be null" );
    } catch( Exception e ) {
      // expected
    }
    try {
      new Font( device, new FontData[ 0 ] );
      fail( "Must not allow to pass empty FontData array" );
    } catch( Exception e ) {
      // expected
    }
    try {
      new Font( device, new FontData[] { null } );
      fail( "FontData array must not contain null" );
    } catch( Exception e ) {
      // expected
    }
    Font font = new Font( device, "roman", 1, 1 << 3 );
    assertEquals( SWT.NORMAL, font.getFontData()[ 0 ].getStyle() );
  }
  
  public void testConstructorCreatesSafeCopy() {
    Device device = new Display();
    FontData fontData = new FontData( "roman", 1, SWT.NORMAL );
    FontData[] fontDatas = new FontData[] { fontData };
    Font font = new Font( device, fontDatas );
    fontDatas[ 0 ] = null;
    assertSame( fontData, font.getFontData()[ 0 ] );
  }
  
  public void testDispose() {
    Display device = new Display();
    Font font = new Font( device, "roman", 1, SWT.NORMAL );
    assertFalse( font.isDisposed() );
    font.dispose();
    assertTrue( font.isDisposed() );
  }
  
  public void testDisposeFactoryCreated() {
    Font font = Graphics.getFont( "roman", 1, SWT.NORMAL );
    try {
      font.dispose();
      fail( "It is not allowed to dispose of a factory-created color" );
    } catch( IllegalStateException e ) {
      assertFalse( font.isDisposed() );
    }
  }
  
  public void testEquality() {
    Device device = new Display();
    Font font1 = Graphics.getFont( "roman", 1, SWT.NORMAL );
    Font font2 = new Font( device, "roman", 1, SWT.NORMAL );
    assertTrue( font1.equals( font2 ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
