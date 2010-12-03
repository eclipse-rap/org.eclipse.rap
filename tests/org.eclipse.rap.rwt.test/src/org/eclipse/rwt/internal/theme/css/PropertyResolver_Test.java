/*******************************************************************************
 * Copyright (c) 2008, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme.css;

import java.io.*;
import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.apache.batik.css.parser.Parser;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.theme.QxAnimation.Animation;
import org.w3c.css.sac.*;


public class PropertyResolver_Test extends TestCase {

  private static Parser parser = new Parser();

  private static ResourceLoader RESOURCE_LOADER
    = ThemeTestUtil.createResourceLoader( Fixture.class );

  public void testColor() throws Exception {
    QxColor transparent
      = PropertyResolver.readColor( parseProperty( "transparent" ) );
    assertEquals( QxColor.TRANSPARENT, transparent );
    QxColor white = PropertyResolver.readColor( parseProperty( "white" ) );
    assertEquals( QxColor.WHITE, white );
    QxColor black = PropertyResolver.readColor( parseProperty( "Black" ) );
    assertEquals( QxColor.BLACK, black );
    QxColor yellow = PropertyResolver.readColor( parseProperty( "yellow" ) );
    assertEquals( QxColor.create( 255, 255, 0 ), yellow );
    QxColor fb0 = PropertyResolver.readColor( parseProperty( "#fb0" ) );
    QxColor ffbb00 = PropertyResolver.readColor( parseProperty( "#ffbb00" ) );
    assertEquals( fb0, ffbb00 );
    assertEquals( QxColor.create( 255, 187, 0 ), ffbb00 );
    QxColor color1
      = PropertyResolver.readColor( parseProperty( "rgb( 0, 127, 255 )" ) );
    assertEquals( QxColor.create( 0, 127, 255 ), color1 );
    QxColor color2
      = PropertyResolver.readColor( parseProperty( "rgb( -10, 127, 300 )" ) );
    assertEquals( color1, color2 );
    QxColor colorP1
      = PropertyResolver.readColor( parseProperty( "rgb( 0%, 50%, 100% )" ) );
    assertEquals( color1, colorP1 );
    QxColor colorP2
      = PropertyResolver.readColor( parseProperty( "rgb( -10%, 50%, 110% )" ) );
    assertEquals( colorP1, colorP2 );
    QxColor inherit = PropertyResolver.readColor( parseProperty( "inherit" ) );
    assertEquals( QxColor.TRANSPARENT, inherit );
    try {
      PropertyResolver.readColor( parseProperty( "rgb( 0%, 50, 100 )" ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testColorWithTurkishLocale() throws Exception {
    Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault( new Locale( "tr", "TR" ) );
      QxColor white = PropertyResolver.readColor( parseProperty( "WHITE" ) );
      assertEquals( QxColor.WHITE, white );
    } finally {
      Locale.setDefault( originalLocale );
    }
  }

  public void testDimension() throws Exception {
    QxDimension zero = PropertyResolver.readDimension( parseProperty( "0px" ) );
    assertNotNull( zero );
    assertEquals( QxDimension.ZERO, zero );
    QxDimension dim2 = PropertyResolver.readDimension( parseProperty( "2px" ) );
    assertNotNull( dim2 );
    assertEquals( QxDimension.create( 2 ), dim2 );
    try {
      PropertyResolver.readDimension( parseProperty( "2em" ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      PropertyResolver.readDimension( parseProperty( "2" ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDimension_ZeroWithoutUnit() throws Exception {
    QxDimension zero = PropertyResolver.readDimension( parseProperty( "0" ) );
    assertNotNull( zero );
    assertEquals( QxDimension.ZERO, zero );
  }

  public void testBoxDimensions() throws Exception {
    LexicalUnit zeroUnit = parseProperty( "0px" );
    QxBoxDimensions zero = PropertyResolver.readBoxDimensions( zeroUnit );
    assertNotNull( zero );
    assertEquals( QxBoxDimensions.ZERO, zero );
    LexicalUnit unit1234 = parseProperty( "1px 2px 3px 4px" );
    QxBoxDimensions bdim1234 = PropertyResolver.readBoxDimensions( unit1234 );
    assertNotNull( bdim1234 );
    assertEquals( QxBoxDimensions.create( 1, 2, 3, 4 ), bdim1234 );
    LexicalUnit unit123 = parseProperty( "1px 2px 3px" );
    QxBoxDimensions bdim123 = PropertyResolver.readBoxDimensions( unit123 );
    assertNotNull( bdim123 );
    assertEquals( QxBoxDimensions.create( 1, 2, 3, 2 ), bdim123 );
    LexicalUnit unit12 = parseProperty( "1px 2px" );
    QxBoxDimensions bdim12 = PropertyResolver.readBoxDimensions( unit12 );
    assertNotNull( bdim12 );
    assertEquals( QxBoxDimensions.create( 1, 2, 1, 2 ), bdim12 );
    LexicalUnit illegalUnit1 = parseProperty( "2" );
    try {
      PropertyResolver.readBoxDimensions( illegalUnit1 );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
    LexicalUnit illegalUnit2 = parseProperty( "2em" );
    try {
      PropertyResolver.readBoxDimensions( illegalUnit2 );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
    LexicalUnit illegalUnit3 = parseProperty( "2 3px 4px 5px" );
    try {
      PropertyResolver.readBoxDimensions( illegalUnit3 );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testBoxDimension_ZeroWithoutUnit() throws Exception {
    QxBoxDimensions zero
      = PropertyResolver.readBoxDimensions( parseProperty( "0" ) );
    assertNotNull( zero );
    assertEquals( QxBoxDimensions.ZERO, zero );

    QxBoxDimensions withZero
      = PropertyResolver.readBoxDimensions( parseProperty( "0 1px 2px 3px" ) );
    assertNotNull( withZero );
    assertEquals( QxBoxDimensions.create( 0, 1, 2, 3 ), withZero );
  }

  public void testBorderWidth() throws Exception {
    int zero = PropertyResolver.readBorderWidth( parseProperty( "0px" ) );
    assertEquals( 0, zero );
    int width = PropertyResolver.readBorderWidth( parseProperty( "2px" ) );
    assertEquals( 2, width );
    int thin = PropertyResolver.readBorderWidth( parseProperty( "thin" ) );
    assertEquals( PropertyResolver.THIN_VALUE, thin );
    int medium = PropertyResolver.readBorderWidth( parseProperty( "medium" ) );
    assertEquals( PropertyResolver.MEDIUM_VALUE, medium );
    int thick = PropertyResolver.readBorderWidth( parseProperty( "thick" ) );
    assertEquals( PropertyResolver.THICK_VALUE, thick );
    int illegal = PropertyResolver.readBorderWidth( parseProperty( "1" ) );
    assertEquals( -1, illegal );
    int illegal2 = PropertyResolver.readBorderWidth( parseProperty( "fat" ) );
    assertEquals( -1, illegal2 );
    int negative = PropertyResolver.readBorderWidth( parseProperty( "-1px" ) );
    assertEquals( -1, negative );
  }

  public void testBorderWidth_ZeroWithoutUnit() throws Exception {
    int zero = PropertyResolver.readBorderWidth( parseProperty( "0" ) );
    assertEquals( 0, zero );
  }

  public void testBorderStyle() throws Exception {
    String nonsense = PropertyResolver.readBorderStyle( parseProperty( "nonsense" ) );
    assertNull( nonsense );
    String none = PropertyResolver.readBorderStyle( parseProperty( "none" ) );
    assertEquals( "none", none );
  }

  public void testBorder() throws Exception {
    String input = "1";
    try {
      PropertyResolver.readBorder( parseProperty( input ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "1px red blue green";
    try {
      PropertyResolver.readBorder( parseProperty( input ) );
      fail();
    } catch( Exception e ) {
      // expected
    }
    input = "1 solid blue";
    try {
      PropertyResolver.readBorder( parseProperty( input ) );
      fail();
    } catch( Exception e ) {
      // expected
    }
    input = "1px solid blue";
    QxBorder border1 = PropertyResolver.readBorder( parseProperty( input ) );
    assertEquals( QxBorder.create( 1, "solid", "#0000ff" ), border1 );
    input = "1px solid rgb( 0, 0, 255 )";
    QxBorder border2 = PropertyResolver.readBorder( parseProperty( input ) );
    assertEquals( border1, border2 );
    input = "rgb( 0, 0, 255 ) solid 1px";
    QxBorder border3 = PropertyResolver.readBorder( parseProperty( input ) );
    assertEquals( border1, border3 );
  }

  public void testFontStyle() throws Exception {
    String normal = "normal";
    assertEquals( normal,
                  PropertyResolver.readFontStyle( parseProperty( normal ) ) );
    String italic = "italic";
    assertEquals( italic,
                  PropertyResolver.readFontStyle( parseProperty( italic ) ) );
    String oblique = "oblique";
    assertNull( PropertyResolver.readFontStyle( parseProperty( oblique ) ) );
    String inherit = "inherit";
    assertNull( PropertyResolver.readFontStyle( parseProperty( inherit ) ) );
  }

  public void testFontWeight() throws Exception {
    String normal = "normal";
    assertEquals( normal,
                  PropertyResolver.readFontWeight( parseProperty( normal ) ) );
    String bold = "bold";
    assertEquals( bold,
                  PropertyResolver.readFontWeight( parseProperty( bold ) ) );
    String bolder = "bolder";
    assertNull( PropertyResolver.readFontWeight( parseProperty( bolder ) ) );
    String inherit = "inherit";
    assertNull( PropertyResolver.readFontWeight( parseProperty( inherit ) ) );
  }

  public void testFontSize() throws Exception {
    assertEquals( 0, PropertyResolver.readFontSize( parseProperty( "0px" ) ) );
    assertEquals( 2, PropertyResolver.readFontSize( parseProperty( "2px" ) ) );
    assertEquals( -1, PropertyResolver.readFontSize( parseProperty( "2em" ) ) );
    assertEquals( -1, PropertyResolver.readFontSize( parseProperty( "-1px" ) ) );
    assertEquals( -1, PropertyResolver.readFontSize( parseProperty( "-2px" ) ) );
  }

  public void testFontSize_ZeroWithoutUnit() throws Exception {
    assertEquals( 0, PropertyResolver.readFontSize( parseProperty( "0" ) ) );
  }

  public void testFontFamily() throws Exception {
    String input1 = "Helvetica";
    String[] res1 = PropertyResolver.readFontFamily( parseProperty( input1 ) );
    assertNotNull( res1 );
    assertEquals( 1, res1.length );
    assertEquals( "Helvetica", res1[ 0 ] );
    String input2 = "Helvetica, sans-serif";
    String[] res2 = PropertyResolver.readFontFamily( parseProperty( input2 ) );
    assertNotNull( res2 );
    assertEquals( 2, res2.length );
    assertEquals( "sans-serif", res2[ 1 ] );
    String input3 = "\"New Century Schoolbook\", serif";
    String[] res3 = PropertyResolver.readFontFamily( parseProperty( input3 ) );
    assertNotNull( res3 );
    assertEquals( 2, res3.length );
    assertEquals( "New Century Schoolbook", res3[ 0 ] );
    String input4 = "'New Century Schoolbook', Times, serif";
    String[] res4 = PropertyResolver.readFontFamily( parseProperty( input4 ) );
    assertNotNull( res4 );
    assertEquals( 3, res4.length );
  }

  public void testFont() throws Exception {
    // http://www.w3.org/TR/CSS21/fonts.html#font-shorthand
    String input1 = "9px Helvetica";
    QxFont exp1 = QxFont.create( new String[] { "Helvetica" }, 9, false, false );
    QxFont res1 = PropertyResolver.readFont( parseProperty( input1 ) );
    assertEquals( exp1, res1 );
    String input2 = "bold 12px Helvetica";
    QxFont exp2 = QxFont.create( new String[] { "Helvetica" }, 12, true, false );
    QxFont res2 = PropertyResolver.readFont( parseProperty( input2 ) );
    assertEquals( exp2, res2 );
    String input3 = "bold italic 8px Helvetica, sans-serif";
    String[] family3 = new String[] { "Helvetica", "sans-serif" };
    QxFont exp3 = QxFont.create( family3, 8, true, true );
    QxFont res3 = PropertyResolver.readFont( parseProperty( input3 ) );
    assertEquals( exp3, res3 );
    String input4 = "8px Courier New, sans-serif";
    String[] family4 = new String[] { "Courier New", "sans-serif" };
    QxFont exp4 = QxFont.create( family4, 8, false, false );
    QxFont res4 = PropertyResolver.readFont( parseProperty( input4 ) );
    assertEquals( exp4, res4 );
    try {
      PropertyResolver.readFont( parseProperty( "Helvetica" ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      PropertyResolver.readFont( parseProperty( "bold Helvetica" ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      PropertyResolver.readFont( parseProperty( "8px" ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      PropertyResolver.readFont( parseProperty( "Helvetica 8px" ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      PropertyResolver.readFont( parseProperty( "9 Helvetica" ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testBackgroundImage() throws Exception {
    // background-image: none;
    String input = "none";
    QxImage res1 = PropertyResolver.readBackgroundImage( parseProperty( input ),
                                                         RESOURCE_LOADER );
    assertEquals( QxImage.NONE, res1 );
    // background-image: url( "path" );
    input = "url( \"" + Fixture.IMAGE_50x100 + "\" )";
    QxImage res2 = PropertyResolver.readBackgroundImage( parseProperty( input ),
                                                         RESOURCE_LOADER );
    QxImage expected = QxImage.valueOf( Fixture.IMAGE_50x100,
                                        RESOURCE_LOADER );
    assertEquals( expected, res2 );
    // background-image: url( path );
    input = "url( " + Fixture.IMAGE_50x100 + " )";
    QxImage res3 = PropertyResolver.readBackgroundImage( parseProperty( input ),
                                                         RESOURCE_LOADER );
    assertEquals( expected, res3 );
    // background-image: "path";
    input = "\"" + Fixture.IMAGE_50x100 + "\"";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ),
                                            RESOURCE_LOADER );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testGradient() throws Exception {
    String input1 = "gradient( linear, left top, left bottom, "
                  + "from( #0000FF ), "
                  + "color-stop( 50%, #00FF00 ), "
                  + "to( #0000FF ) )";
    QxImage res1 = PropertyResolver.readBackgroundImage( parseProperty( input1 ),
                                                         RESOURCE_LOADER );
    assertNotNull( res1 );
    assertTrue( res1.none );
    assertNull( res1.path );
    assertNull( res1.loader );
    assertNotNull( res1.gradientColors );
    assertEquals( 3, res1.gradientColors.length );
    assertEquals( "#0000ff", res1.gradientColors[ 0 ] );
    assertEquals( "#00ff00", res1.gradientColors[ 1 ] );
    assertEquals( "#0000ff", res1.gradientColors[ 2 ] );
    assertNotNull( res1.gradientPercents );
    assertEquals( 3, res1.gradientPercents.length );
    assertEquals( 0f, res1.gradientPercents[ 0 ], 0 );
    assertEquals( 50f, res1.gradientPercents[ 1 ], 0 );
    assertEquals( 100f, res1.gradientPercents[ 2 ], 0 );
    assertTrue( res1.vertical );
    String input2 = "gradient( linear, left top, left bottom, "
                  + "color-stop( 0%, #0000FF ), "
                  + "color-stop( 50%, #00FF00 ), "
                  + "color-stop( 100%, #0000FF ) )";
    QxImage res2 = PropertyResolver.readBackgroundImage( parseProperty( input2 ),
                                                         RESOURCE_LOADER );
    assertNotNull( res2 );
    assertTrue( res2.none );
    assertNull( res2.path );
    assertNull( res2.loader );
    assertNotNull( res2.gradientColors );
    assertEquals( 3, res2.gradientColors.length );
    assertEquals( "#0000ff", res2.gradientColors[ 0 ] );
    assertEquals( "#00ff00", res2.gradientColors[ 1 ] );
    assertEquals( "#0000ff", res2.gradientColors[ 2 ] );
    assertNotNull( res2.gradientPercents );
    assertEquals( 3, res2.gradientPercents.length );
    assertEquals( 0f, res2.gradientPercents[ 0 ], 0 );
    assertEquals( 50f, res2.gradientPercents[ 1 ], 0 );
    assertEquals( 100f, res2.gradientPercents[ 2 ], 0 );
    assertTrue( res2.vertical );
    String input3 = "gradient( linear, left top, left bottom, "
                  + "color-stop( 50%, #00FF00 ) )";
    QxImage res3 = PropertyResolver.readBackgroundImage( parseProperty( input3 ),
                                                         RESOURCE_LOADER );
    assertNotNull( res3 );
    assertTrue( res3.none );
    assertNull( res3.path );
    assertNull( res3.loader );
    assertNotNull( res3.gradientColors );
    assertEquals( 3, res3.gradientColors.length );
    assertEquals( "#00ff00", res3.gradientColors[ 0 ] );
    assertEquals( "#00ff00", res3.gradientColors[ 1 ] );
    assertEquals( "#00ff00", res3.gradientColors[ 2 ] );
    assertNotNull( res3.gradientPercents );
    assertEquals( 3, res3.gradientPercents.length );
    assertEquals( 0f, res3.gradientPercents[ 0 ], 0 );
    assertEquals( 50f, res3.gradientPercents[ 1 ], 0 );
    assertEquals( 100f, res3.gradientPercents[ 2 ], 0 );
    assertTrue( res3.vertical );
    String input4 = "gradient( linear, left top, left bottom, "
                  + "from( #0000FF ), "
                  + "to( #00FF00 ) )";
    QxImage res4 = PropertyResolver.readBackgroundImage( parseProperty( input4 ),
                                                         RESOURCE_LOADER );
    assertNotNull( res4 );
    assertTrue( res4.none );
    assertNull( res4.path );
    assertNull( res4.loader );
    assertNotNull( res4.gradientColors );
    assertEquals( 2, res4.gradientColors.length );
    assertEquals( "#0000ff", res4.gradientColors[ 0 ] );
    assertEquals( "#00ff00", res4.gradientColors[ 1 ] );
    assertNotNull( res4.gradientPercents );
    assertEquals( 2, res4.gradientPercents.length );
    assertEquals( 0f, res4.gradientPercents[ 0 ], 0 );
    assertEquals( 100f, res4.gradientPercents[ 1 ], 0 );
    assertTrue( res4.vertical );
  }

  public void testGradient_Horizontal() throws Exception {
    String input = "gradient( linear, left top, right top, "
                  + "from( #0000FF ), "
                  + "color-stop( 50%, #00FF00 ), "
                  + "to( #0000FF ) )";
    QxImage res = PropertyResolver.readBackgroundImage( parseProperty( input ),
                                                        RESOURCE_LOADER );
    assertNotNull( res );
    assertTrue( res.none );
    assertNull( res.path );
    assertNull( res.loader );
    assertNotNull( res.gradientColors );
    assertEquals( 3, res.gradientColors.length );
    assertEquals( "#0000ff", res.gradientColors[ 0 ] );
    assertEquals( "#00ff00", res.gradientColors[ 1 ] );
    assertEquals( "#0000ff", res.gradientColors[ 2 ] );
    assertNotNull( res.gradientPercents );
    assertEquals( 3, res.gradientPercents.length );
    assertEquals( 0f, res.gradientPercents[ 0 ], 0 );
    assertEquals( 50f, res.gradientPercents[ 1 ], 0 );
    assertEquals( 100f, res.gradientPercents[ 2 ], 0 );
    assertFalse( res.vertical );
  }

  public void testGradient_InvalidValues() throws Exception {
    String input = "gradient( radial, left top, left bottom, "
                 + "from( #0000FF ), "
                 + "to( #00FF00 ) )";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ),
                                            RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "gradient( linear, 10 10, left bottom, "
          + "from( #0000FF ), "
          + "to( #00FF00 ) )";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ),
                                            RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "gradient( linear, left top, 10 10, "
          + "from( #0000FF ), "
          + "to( #00FF00 ) )";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ),
                                            RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "gradient( linear, left top, left bottom )";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ),
                                            RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "gradient( linear, left, right, "
          + "from( blue ), "
          + "to( white ) )";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ),
                                            RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testFloat() throws Exception {
    QxFloat zero = PropertyResolver.readFloat( parseProperty( "0" ) );
    assertNotNull( zero );
    assertEquals( 0.0, zero.value, 0.001 );
    QxFloat one = PropertyResolver.readFloat( parseProperty( "1" ) );
    assertNotNull( one );
    assertEquals( 1.0, one.value, 0.001 );
    QxFloat floatValue = PropertyResolver.readFloat( parseProperty( "0.62" ) );
    assertNotNull( floatValue );
    assertEquals( 0.62, floatValue.value, 0.001 );
    assertEquals( floatValue.toDefaultString(), "0.62" );
    floatValue = null;
    try {
      floatValue = PropertyResolver.readFloat( parseProperty( "asdf" ) );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    assertNull( floatValue );
    try {
      floatValue = PropertyResolver.readFloat( parseProperty( "-0.4" ) );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    assertNull( floatValue );
    try {
      floatValue = PropertyResolver.readFloat( parseProperty( "1.0001" ) );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    assertNull( floatValue );
  }

  public void testCursor() throws Exception {
    // Test predefined cursor
    String input = "default";
    QxCursor res1 = PropertyResolver.readCursor( parseProperty( input ),
                                                 RESOURCE_LOADER );
    assertNotNull( res1 );
    assertEquals( input, res1.value );

    input = "pointer";
    res1 = PropertyResolver.readCursor( parseProperty( input ),
                                        RESOURCE_LOADER );
    assertNotNull( res1 );
    assertEquals( input, res1.value );

    input = "wait";
    res1 = PropertyResolver.readCursor( parseProperty( input ),
                                        RESOURCE_LOADER );
    assertNotNull( res1 );
    assertEquals( input, res1.value );

    input = "crosshair";
    res1 = PropertyResolver.readCursor( parseProperty( input ),
                                        RESOURCE_LOADER );
    assertNotNull( res1 );
    assertEquals( input, res1.value );

    // Test custom cursor
    QxCursor expected = QxCursor.valueOf( Fixture.IMAGE_50x100,
                                          RESOURCE_LOADER );
    // cursor: url( "path" );
    input = "url( \"" + Fixture.IMAGE_50x100 + "\" )";
    QxCursor res2 = PropertyResolver.readCursor( parseProperty( input ),
                                                 RESOURCE_LOADER );
    assertNotNull( res2 );
    assertEquals( expected, res2 );
    // cursor: url( path );
    input = "url( " + Fixture.IMAGE_50x100 + " )";
    QxCursor res3 = PropertyResolver.readCursor( parseProperty( input ),
                                                 RESOURCE_LOADER );
    assertNotNull( res3 );
    assertEquals( expected, res3 );
  }

  public void testAnimation() throws Exception {
    String input = "slideIn 2s ease-in";
    QxAnimation result
      = PropertyResolver.readAnimation( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.animations.length );
    Animation animation = result.animations[ 0 ];
    assertEquals( "slideIn", animation.name );
    assertEquals( 2000, animation.duration );
    assertEquals( "ease-in", animation.timingFunction );

    input = "slideIn 2s ease-in, slideOut 200ms ease-out";
    result = PropertyResolver.readAnimation( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 2, result.animations.length );
    animation = result.animations[ 0 ];
    assertEquals( "slideIn", animation.name );
    assertEquals( 2000, animation.duration );
    assertEquals( "ease-in", animation.timingFunction );
    animation = result.animations[ 1 ];
    assertEquals( "slideOut", animation.name );
    assertEquals( 200, animation.duration );
    assertEquals( "ease-out", animation.timingFunction );

    input = "none";
    result = PropertyResolver.readAnimation( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 0, result.animations.length );

    input = "slideIn";
    try {
      result = PropertyResolver.readAnimation( parseProperty( input ) );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "slideIn 2s";
    try {
      result = PropertyResolver.readAnimation( parseProperty( input ) );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "slideIn abc ease-in";
    try {
      result = PropertyResolver.readAnimation( parseProperty( input ) );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "slideIn 3s ease-in, slideOut";
    try {
      result = PropertyResolver.readAnimation( parseProperty( input ) );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testIsColorProperty() {
    assertFalse( PropertyResolver.isColorProperty( "border" ) );
    assertTrue( PropertyResolver.isColorProperty( "color" ) );
    assertTrue( PropertyResolver.isColorProperty( "background-color" ) );
    assertTrue( PropertyResolver.isColorProperty( "background-gradient-color" ) );
    assertTrue( PropertyResolver.isColorProperty( "rwt-selectionmarker-color" ) );
  }

  public void testIsBorderProperty() {
    assertTrue( PropertyResolver.isBorderProperty( "border" ) );
  }

  public void testIsFontProperty() {
    assertTrue( PropertyResolver.isFontProperty( "font" ) );
  }

  public void testIsBoxDimProperty() {
    assertTrue( PropertyResolver.isBoxDimensionProperty( "padding" ) );
    assertTrue( PropertyResolver.isBoxDimensionProperty( "margin" ) );
  }

  public void testIsImageProperty() {
    assertTrue( PropertyResolver.isImageProperty( "background-image" ) );
  }

  public void testIsCursorProperty() {
    assertTrue( PropertyResolver.isCursorProperty( "cursor" ) );
  }

  public void testIsAnimationProperty() {
    assertTrue( PropertyResolver.isAnimationProperty( "animation" ) );
  }

  public void testResolveProperty() throws Exception {
    LexicalUnit unit = parseProperty( "white" );
    QxType value = PropertyResolver.resolveProperty( "color", unit, null );
    assertEquals( QxColor.WHITE, value );
    try {
      PropertyResolver.resolveProperty( "xy", unit, null );
      fail();
    } catch( IllegalArgumentException e ) {
      assertTrue( e.getMessage().indexOf( "property xy" ) != -1 );
    }
    try {
      LexicalUnit unit2 = parseProperty( "darkslategray" );
      PropertyResolver.resolveProperty( "color", unit2 , null );
      fail();
    } catch( IllegalArgumentException e ) {
      assertTrue( e.getMessage().indexOf( "color darkslategray" ) != -1 );
    }
  }

  private static LexicalUnit parseProperty( final String input )
    throws CSSException, IOException
  {
    InputSource inputSource = new InputSource();
    InputStream byteStream = new ByteArrayInputStream( input.getBytes() );
    inputSource.setByteStream( byteStream );
    return parser.parsePropertyValue( inputSource );
  }
}
