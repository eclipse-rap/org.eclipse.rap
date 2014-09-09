/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.graphics.Color;
import org.junit.Rule;
import org.junit.Test;


public class CssColor_Test {

  @Rule
  public TestContext context = new TestContext();

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_invalidHexColorFormat() {
    CssColor.valueOf( "#0000" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_invalidHexColor() {
    CssColor.valueOf( "#xyz" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_invalidNamedColor() {
    CssColor.valueOf( "grey" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_outOfRangeAlpha() {
    CssColor.valueOf( "1, 2, 3, 1.01" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_negativeAlpha() {
    CssColor.valueOf( "1, 2, 3, -0.01" );
  }

  @Test
  public void test6HexNotation() {
    CssColor color1 = CssColor.valueOf( "#0023ff" );
    assertEquals( 0, color1.red );
    assertEquals( 35, color1.green );
    assertEquals( 255, color1.blue );
    assertEquals( 1f, color1.alpha, 0 );
    CssColor color2 = CssColor.valueOf( "#efeFEF" );
    assertEquals( 239, color2.red );
    assertEquals( 239, color2.green );
    assertEquals( 239, color2.blue );
  }

  @Test
  public void test3HexNotation() {
    CssColor color1 = CssColor.valueOf( "#03f" );
    assertEquals( 0, color1.red );
    assertEquals( 51, color1.green );
    assertEquals( 255, color1.blue );
    assertEquals( 1f, color1.alpha, 0 );
    CssColor color2 = CssColor.valueOf( "#ccc" );
    assertEquals( 204, color2.red );
    assertEquals( 204, color2.green );
    assertEquals( 204, color2.blue );
    assertEquals( CssColor.valueOf( "#ffffff"), CssColor.valueOf( "#fff" ) );
  }

  @Test
  public void testNamedColors() {
    CssColor color1 = CssColor.valueOf( "red" );
    assertEquals( 255, color1.red );
    assertEquals( 0, color1.green );
    assertEquals( 0, color1.blue );
    assertEquals( 1f, color1.alpha, 0 );
    CssColor color2 = CssColor.valueOf( "blue" );
    assertEquals( 0, color2.red );
    assertEquals( 0, color2.green );
    assertEquals( 255, color2.blue );
  }

  @Test
  public void testCommaSeparatedValues() {
    CssColor color = CssColor.valueOf( "100, 23, 42" );
    assertEquals( 100, color.red );
    assertEquals( 23, color.green );
    assertEquals( 42, color.blue );
    assertEquals( 1f, color.alpha, 0 );
  }

  @Test
  public void testCommaSeparatedValues_WithAlpha() {
    CssColor color = CssColor.valueOf( "100, 23, 42, 0.5" );
    assertEquals( 100, color.red );
    assertEquals( 23, color.green );
    assertEquals( 42, color.blue );
    assertEquals( 0.5, color.alpha, 0 );
  }

  @Test
  public void testTransparent() {
    assertTrue( CssColor.TRANSPARENT.isTransparent() );
  }

  @Test
  public void testShared() {
    assertSame( CssColor.WHITE, CssColor.valueOf( "white" ) );
    assertSame( CssColor.WHITE, CssColor.valueOf( "255, 255, 255" ) );
    assertSame( CssColor.WHITE, CssColor.valueOf( "#ffffff" ) );
    assertSame( CssColor.BLACK, CssColor.valueOf( "Black" ) );
    assertSame( CssColor.BLACK, CssColor.valueOf( "0, 0, 0" ) );
    assertSame( CssColor.BLACK, CssColor.valueOf( "#000" ) );
    assertSame( CssColor.TRANSPARENT, CssColor.valueOf( "transparent" ) );
  }

  @Test
  public void testToString() {
    CssColor color = CssColor.valueOf( "100, 23, 42" );
    assertEquals( "CssColor{ 100, 23, 42, 1.0 }", color.toString() );
  }

  @Test
  public void testToString_WithAlpha() {
    CssColor color = CssColor.valueOf( "100, 23, 42, 0.5" );
    assertEquals( "CssColor{ 100, 23, 42, 0.5 }", color.toString() );
  }

  @Test
  public void testDefaultString() {
    CssColor color = CssColor.valueOf( "100, 23, 42" );
    assertEquals( "#64172a", color.toDefaultString() );
  }

  @Test
  public void testDefaultString_Transparent() {
    CssColor color = CssColor.valueOf( "100, 23, 42, 0" );
    assertEquals( "transparent", color.toDefaultString() );
  }

  @Test
  public void testDefaultString_WithAlpha() {
    CssColor color = CssColor.valueOf( "100, 23, 42, 0.5" );
    assertEquals( "rgba(100,23,42,0.5)", color.toDefaultString() );
  }

  @Test
  public void testWithTurkishLocale() {
    Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault( new Locale( "tr", "TR" ) );
      assertSame( CssColor.WHITE, CssColor.valueOf( "WHITE" ) );
    } finally {
      Locale.setDefault( originalLocale );
    }
  }

  @Test
  public void testCreateColor_WithoutAlpha() {
    CssColor color = CssColor.valueOf( "100, 23, 42" );
    Color result = CssColor.createColor( color );
    assertNotNull( result );
    assertEquals( 100, result.getRed() );
    assertEquals( 23, result.getGreen() );
    assertEquals( 42, result.getBlue() );
  }

  @Test
  public void testCreateColor_WithAlpha() {
    CssColor color = CssColor.valueOf( "100, 23, 42, 0.5" );
    Color result = CssColor.createColor( color );
    assertNotNull( result );
    assertEquals( 100, result.getRed() );
    assertEquals( 23, result.getGreen() );
    assertEquals( 42, result.getBlue() );
  }

  @Test
  public void testCreateColor_FullyTransparent() {
    CssColor color = CssColor.valueOf( "100, 23, 42, 0" );
    Color result = CssColor.createColor( color );
    assertNull( result );
  }

}
