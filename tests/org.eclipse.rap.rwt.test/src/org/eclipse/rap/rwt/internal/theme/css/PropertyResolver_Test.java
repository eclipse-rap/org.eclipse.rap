/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme.css;

import static org.eclipse.rap.rwt.internal.theme.ThemeTestUtil.RESOURCE_LOADER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.eclipse.rap.rwt.apache.batik.css.parser.Parser;
import org.eclipse.rap.rwt.internal.theme.CssAnimation;
import org.eclipse.rap.rwt.internal.theme.CssAnimation.Animation;
import org.eclipse.rap.rwt.internal.theme.CssBorder;
import org.eclipse.rap.rwt.internal.theme.CssBoxDimensions;
import org.eclipse.rap.rwt.internal.theme.CssColor;
import org.eclipse.rap.rwt.internal.theme.CssCursor;
import org.eclipse.rap.rwt.internal.theme.CssDimension;
import org.eclipse.rap.rwt.internal.theme.CssFloat;
import org.eclipse.rap.rwt.internal.theme.CssFont;
import org.eclipse.rap.rwt.internal.theme.CssIdentifier;
import org.eclipse.rap.rwt.internal.theme.CssImage;
import org.eclipse.rap.rwt.internal.theme.CssShadow;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.Before;
import org.junit.Test;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;


public class PropertyResolver_Test {

  private static Parser parser = new Parser();
  private PropertyResolver propertyResolver;

  @Before
  public void setUp() {
    propertyResolver = new PropertyResolver();
  }

  @Test
  public void testResolveProperty_withValidProperty() throws Exception {
    LexicalUnit unit = parseProperty( "white" );

    propertyResolver.resolveProperty( "color", unit, null );

    StylePropertyMap resolvedProperties = propertyResolver.getResolvedProperties();
    assertEquals( CssColor.WHITE, resolvedProperties.getValue( "color" ) );
  }

  @Test
  public void testResolveProperty_withInvalidPropertyName() throws Exception {
    LexicalUnit unit = parseProperty( "white" );

    try {
      propertyResolver.resolveProperty( "xy", unit, null );
      fail();
    } catch( IllegalArgumentException e ) {
      assertTrue( e.getMessage().contains( "property xy" ) );
    }
  }

  @Test
  public void testResolveProperty_withInvalidPropertyValue() throws Exception {
    LexicalUnit unit = parseProperty( "darkslategray" );

    try {
      propertyResolver.resolveProperty( "color", unit, null );
      fail();
    } catch( IllegalArgumentException e ) {
      assertTrue( e.getMessage().contains( "color darkslategray" ) );
    }
  }

  @Test
  public void testColor() throws Exception {
    CssColor transparent = PropertyResolver.readColor( parseProperty( "transparent" ) );
    assertEquals( CssColor.TRANSPARENT, transparent );
    CssColor white = PropertyResolver.readColor( parseProperty( "white" ) );
    assertEquals( CssColor.WHITE, white );
    CssColor black = PropertyResolver.readColor( parseProperty( "Black" ) );
    assertEquals( CssColor.BLACK, black );
    CssColor yellow = PropertyResolver.readColor( parseProperty( "yellow" ) );
    assertEquals( CssColor.create( 255, 255, 0 ), yellow );
    CssColor fb0 = PropertyResolver.readColor( parseProperty( "#fb0" ) );
    CssColor ffbb00 = PropertyResolver.readColor( parseProperty( "#ffbb00" ) );
    assertEquals( fb0, ffbb00 );
    assertEquals( CssColor.create( 255, 187, 0 ), ffbb00 );
    CssColor color1 = PropertyResolver.readColor( parseProperty( "rgb( 0, 127, 255 )" ) );
    assertEquals( CssColor.create( 0, 127, 255 ), color1 );
    CssColor color2 = PropertyResolver.readColor( parseProperty( "rgb( -10, 127, 300 )" ) );
    assertEquals( color1, color2 );
    CssColor colorP1 = PropertyResolver.readColor( parseProperty( "rgb( 0%, 50%, 100% )" ) );
    assertEquals( color1, colorP1 );
    CssColor colorP2 = PropertyResolver.readColor( parseProperty( "rgb( -10%, 50%, 110% )" ) );
    assertEquals( colorP1, colorP2 );
    CssColor inherit = PropertyResolver.readColor( parseProperty( "inherit" ) );
    assertEquals( CssColor.TRANSPARENT, inherit );
    try {
      PropertyResolver.readColor( parseProperty( "rgb( 0%, 50, 100 )" ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testColorWithTurkishLocale() throws Exception {
    Locale originalLocale = Locale.getDefault();
    try {
      Locale.setDefault( new Locale( "tr", "TR" ) );
      CssColor white = PropertyResolver.readColor( parseProperty( "WHITE" ) );
      assertEquals( CssColor.WHITE, white );
    } finally {
      Locale.setDefault( originalLocale );
    }
  }

  @Test
  public void testColorWithAlpha() throws Exception {
    String input = "rgba( 1, 2, 3, 0.25 )";
    CssColor result = PropertyResolver.readColor( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.red );
    assertEquals( 2, result.green );
    assertEquals( 3, result.blue );
    assertEquals( 0.25, result.alpha, 0 );
  }

  @Test
  public void testColorWithAlpha_Percents() throws Exception {
    String input = "rgba( 0%, 50%, 100%, 0.25 )";
    CssColor result = PropertyResolver.readColor( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 0, result.red );
    assertEquals( 127, result.green );
    assertEquals( 255, result.blue );
    assertEquals( 0.25, result.alpha, 0 );
  }

  @Test
  public void testColorWithAlpha_NoTransparency() throws Exception {
    String input = "rgba( 0, 0, 0, 1 )";
    CssColor result = PropertyResolver.readColor( parseProperty( input ) );
    assertSame( CssColor.BLACK, result );
  }

  @Test
  public void testColorWithAlpha_NormalizeNegativeAlpha() throws Exception {
    String input = "rgba( 1, 2, 3, -0.1 )";
    CssColor result = PropertyResolver.readColor( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.red );
    assertEquals( 2, result.green );
    assertEquals( 3, result.blue );
    assertEquals( 0f, result.alpha, 0 );
  }

  @Test
  public void testColorWithAlpha_NormalizePositiveAlpha() throws Exception {
    String input = "rgba( 1, 2, 3, 1.1 )";
    CssColor result = PropertyResolver.readColor( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.red );
    assertEquals( 2, result.green );
    assertEquals( 3, result.blue );
    assertEquals( 1f, result.alpha, 0 );
  }

  @Test
  public void testColorWithAlpha_NormalizeColorValue() throws Exception {
    String input = "rgba( -10, 127, 300, 0.25 )";
    CssColor result = PropertyResolver.readColor( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 0, result.red );
    assertEquals( 127, result.green );
    assertEquals( 255, result.blue );
    assertEquals( 0.25, result.alpha, 0 );
  }

  @Test
  public void testColorWithAlpha_MixedValues() throws Exception {
    String input = "rgba( 0%, 50, 100, 0.25 )";
    try {
      PropertyResolver.readColor( parseProperty( input ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testDimension() throws Exception {
    CssDimension zero = PropertyResolver.readDimension( parseProperty( "0px" ) );
    assertNotNull( zero );
    assertEquals( CssDimension.ZERO, zero );
    CssDimension dim2 = PropertyResolver.readDimension( parseProperty( "2px" ) );
    assertNotNull( dim2 );
    assertEquals( CssDimension.create( 2 ), dim2 );
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

  @Test
  public void testDimension_ZeroWithoutUnit() throws Exception {
    CssDimension zero = PropertyResolver.readDimension( parseProperty( "0" ) );
    assertNotNull( zero );
    assertEquals( CssDimension.ZERO, zero );
  }

  @Test
  public void testBoxDimensions() throws Exception {
    LexicalUnit zeroUnit = parseProperty( "0px" );
    CssBoxDimensions zero = PropertyResolver.readBoxDimensions( zeroUnit );
    assertNotNull( zero );
    assertEquals( CssBoxDimensions.ZERO, zero );
    LexicalUnit unit1234 = parseProperty( "1px 2px 3px 4px" );
    CssBoxDimensions bdim1234 = PropertyResolver.readBoxDimensions( unit1234 );
    assertNotNull( bdim1234 );
    assertEquals( CssBoxDimensions.create( 1, 2, 3, 4 ), bdim1234 );
    LexicalUnit unit123 = parseProperty( "1px 2px 3px" );
    CssBoxDimensions bdim123 = PropertyResolver.readBoxDimensions( unit123 );
    assertNotNull( bdim123 );
    assertEquals( CssBoxDimensions.create( 1, 2, 3, 2 ), bdim123 );
    LexicalUnit unit12 = parseProperty( "1px 2px" );
    CssBoxDimensions bdim12 = PropertyResolver.readBoxDimensions( unit12 );
    assertNotNull( bdim12 );
    assertEquals( CssBoxDimensions.create( 1, 2, 1, 2 ), bdim12 );
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

  @Test
  public void testBoxDimension_ZeroWithoutUnit() throws Exception {
    CssBoxDimensions zero = PropertyResolver.readBoxDimensions( parseProperty( "0" ) );
    assertNotNull( zero );
    assertEquals( CssBoxDimensions.ZERO, zero );

    CssBoxDimensions withZero
      = PropertyResolver.readBoxDimensions( parseProperty( "0 1px 2px 3px" ) );
    assertNotNull( withZero );
    assertEquals( CssBoxDimensions.create( 0, 1, 2, 3 ), withZero );
  }

  @Test
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

  @Test
  public void testBorderWidth_ZeroWithoutUnit() throws Exception {
    int zero = PropertyResolver.readBorderWidth( parseProperty( "0" ) );
    assertEquals( 0, zero );
  }

  @Test
  public void testBorderStyle() throws Exception {
    String nonsense = PropertyResolver.readBorderStyle( parseProperty( "nonsense" ) );
    assertNull( nonsense );
    String none = PropertyResolver.readBorderStyle( parseProperty( "none" ) );
    assertEquals( "none", none );
  }

  @Test
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
    CssBorder border1 = PropertyResolver.readBorder( parseProperty( input ) );
    assertEquals( CssBorder.create( 1, "solid", CssColor.valueOf( "#0000ff" ) ), border1 );
    input = "1px solid rgb( 0, 0, 255 )";
    CssBorder border2 = PropertyResolver.readBorder( parseProperty( input ) );
    assertEquals( border1, border2 );
    input = "rgb( 0, 0, 255 ) solid 1px";
    CssBorder border3 = PropertyResolver.readBorder( parseProperty( input ) );
    assertEquals( border1, border3 );
  }

  @Test
  public void testFontStyle() throws Exception {
    String normal = "normal";
    assertEquals( normal, PropertyResolver.readFontStyle( parseProperty( normal ) ) );
    String italic = "italic";
    assertEquals( italic, PropertyResolver.readFontStyle( parseProperty( italic ) ) );
    String oblique = "oblique";
    assertNull( PropertyResolver.readFontStyle( parseProperty( oblique ) ) );
    String inherit = "inherit";
    assertNull( PropertyResolver.readFontStyle( parseProperty( inherit ) ) );
  }

  @Test
  public void testFontWeight() throws Exception {
    String normal = "normal";
    assertEquals( normal, PropertyResolver.readFontWeight( parseProperty( normal ) ) );
    String bold = "bold";
    assertEquals( bold, PropertyResolver.readFontWeight( parseProperty( bold ) ) );
    String bolder = "bolder";
    assertNull( PropertyResolver.readFontWeight( parseProperty( bolder ) ) );
    String inherit = "inherit";
    assertNull( PropertyResolver.readFontWeight( parseProperty( inherit ) ) );
  }

  @Test
  public void testFontSize() throws Exception {
    assertEquals( 0, PropertyResolver.readFontSize( parseProperty( "0px" ) ) );
    assertEquals( 2, PropertyResolver.readFontSize( parseProperty( "2px" ) ) );
    assertEquals( -1, PropertyResolver.readFontSize( parseProperty( "2em" ) ) );
    assertEquals( -1, PropertyResolver.readFontSize( parseProperty( "-1px" ) ) );
    assertEquals( -1, PropertyResolver.readFontSize( parseProperty( "-2px" ) ) );
  }

  @Test
  public void testFontSize_ZeroWithoutUnit() throws Exception {
    assertEquals( 0, PropertyResolver.readFontSize( parseProperty( "0" ) ) );
  }

  @Test
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

  @Test
  public void testFont() throws Exception {
    // http://www.w3.org/TR/CSS21/fonts.html#font-shorthand
    String input1 = "9px Helvetica";
    CssFont exp1 = CssFont.create( new String[] { "Helvetica" }, 9, false, false );
    CssFont res1 = PropertyResolver.readFont( parseProperty( input1 ) );
    assertEquals( exp1, res1 );
    String input2 = "bold 12px Helvetica";
    CssFont exp2 = CssFont.create( new String[] { "Helvetica" }, 12, true, false );
    CssFont res2 = PropertyResolver.readFont( parseProperty( input2 ) );
    assertEquals( exp2, res2 );
    String input3 = "bold italic 8px Helvetica, sans-serif";
    String[] family3 = new String[] { "Helvetica", "sans-serif" };
    CssFont exp3 = CssFont.create( family3, 8, true, true );
    CssFont res3 = PropertyResolver.readFont( parseProperty( input3 ) );
    assertEquals( exp3, res3 );
    String input4 = "8px Courier New, sans-serif";
    String[] family4 = new String[] { "Courier New", "sans-serif" };
    CssFont exp4 = CssFont.create( family4, 8, false, false );
    CssFont res4 = PropertyResolver.readFont( parseProperty( input4 ) );
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

  @Test
  public void testBackgroundImage() throws Exception {
    // background-image: none;
    String input = "none";
    CssImage res1 = PropertyResolver.readBackgroundImage( parseProperty( input ),
                                                         RESOURCE_LOADER );
    assertEquals( CssImage.NONE, res1 );
    // background-image: url( "path" );
    input = "url( \"" + Fixture.IMAGE_50x100 + "\" )";
    CssImage res2 = PropertyResolver.readBackgroundImage( parseProperty( input ),
                                                         RESOURCE_LOADER );
    CssImage expected = CssImage.valueOf( Fixture.IMAGE_50x100,
                                        RESOURCE_LOADER );
    assertEquals( expected, res2 );
    // background-image: url( path );
    input = "url( " + Fixture.IMAGE_50x100 + " )";
    CssImage res3 = PropertyResolver.readBackgroundImage( parseProperty( input ),
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

  @Test
  public void testGradient() throws Exception {
    String input1 = "gradient( linear, left top, left bottom, "
                  + "from( #0000FF ), "
                  + "color-stop( 50%, #00FF00 ), "
                  + "to( #0000FF ) )";
    CssImage res1 = PropertyResolver.readBackgroundImage( parseProperty( input1 ), RESOURCE_LOADER );
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
    CssImage res2 = PropertyResolver.readBackgroundImage( parseProperty( input2 ), RESOURCE_LOADER );
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
    CssImage res3 = PropertyResolver.readBackgroundImage( parseProperty( input3 ), RESOURCE_LOADER );
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
    CssImage res4 = PropertyResolver.readBackgroundImage( parseProperty( input4 ), RESOURCE_LOADER );
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

  @Test
  public void testGradient_Horizontal() throws Exception {
    String input = "gradient( linear, left top, right top, "
                  + "from( #0000FF ), "
                  + "color-stop( 50%, #00FF00 ), "
                  + "to( #0000FF ) )";
    CssImage res = PropertyResolver.readBackgroundImage( parseProperty( input ), RESOURCE_LOADER );
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

  @Test
  public void testGradient_InvalidValues() throws Exception {
    String input = "gradient( radial, left top, left bottom, "
                 + "from( #0000FF ), "
                 + "to( #00FF00 ) )";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ), RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "gradient( linear, 10 10, left bottom, "
          + "from( #0000FF ), "
          + "to( #00FF00 ) )";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ), RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "gradient( linear, left top, 10 10, "
          + "from( #0000FF ), "
          + "to( #00FF00 ) )";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ), RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "gradient( linear, left top, left bottom )";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ), RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    input = "gradient( linear, left, right, "
          + "from( blue ), "
          + "to( white ) )";
    try {
      PropertyResolver.readBackgroundImage( parseProperty( input ), RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testFloat() throws Exception {
    CssFloat zero = PropertyResolver.readFloat( parseProperty( "0" ) );
    assertNotNull( zero );
    assertEquals( 0.0, zero.value, 0.001 );
    CssFloat one = PropertyResolver.readFloat( parseProperty( "1" ) );
    assertNotNull( one );
    assertEquals( 1.0, one.value, 0.001 );
    CssFloat floatValue = PropertyResolver.readFloat( parseProperty( "0.62" ) );
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

  @Test
  public void testCursor() throws Exception {
    // Test predefined cursor
    String input = "default";
    CssCursor res1 = PropertyResolver.readCursor( parseProperty( input ), RESOURCE_LOADER );
    assertNotNull( res1 );
    assertEquals( input, res1.value );

    input = "pointer";
    res1 = PropertyResolver.readCursor( parseProperty( input ), RESOURCE_LOADER );
    assertNotNull( res1 );
    assertEquals( input, res1.value );

    input = "wait";
    res1 = PropertyResolver.readCursor( parseProperty( input ), RESOURCE_LOADER );
    assertNotNull( res1 );
    assertEquals( input, res1.value );

    input = "crosshair";
    res1 = PropertyResolver.readCursor( parseProperty( input ), RESOURCE_LOADER );
    assertNotNull( res1 );
    assertEquals( input, res1.value );

    // Test custom cursor
    CssCursor expected = CssCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    // cursor: url( "path" );
    input = "url( \"" + Fixture.IMAGE_50x100 + "\" )";
    CssCursor res2 = PropertyResolver.readCursor( parseProperty( input ), RESOURCE_LOADER );
    assertNotNull( res2 );
    assertEquals( expected, res2 );
    // cursor: url( path );
    input = "url( " + Fixture.IMAGE_50x100 + " )";
    CssCursor res3 = PropertyResolver.readCursor( parseProperty( input ), RESOURCE_LOADER );
    assertNotNull( res3 );
    assertEquals( expected, res3 );
  }

  @Test
  public void testAnimation() throws Exception {
    String input = "slideIn 2s ease-in";
    CssAnimation result = PropertyResolver.readAnimation( parseProperty( input ) );
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

  @Test
  public void testShadow_XYOffsetOnlyNotation() throws Exception {
    String input = "1px 2px";
    CssShadow result = PropertyResolver.readShadow( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.offsetX );
    assertEquals( 2, result.offsetY );
    assertEquals( 0, result.blur );
    assertEquals( 0, result.spread );
    assertEquals( "#000000", result.color );
    assertEquals( 1f, result.opacity, 0 );
  }

  @Test
  public void testShadow_OffsetXYBlurNotation() throws Exception {
    String input = "1px 2px 3px";
    CssShadow result = PropertyResolver.readShadow( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.offsetX );
    assertEquals( 2, result.offsetY );
    assertEquals( 3, result.blur );
    assertEquals( 0, result.spread );
    assertEquals( "#000000", result.color );
    assertEquals( 1f, result.opacity, 0 );
  }

  @Test
  public void testShadow_XYOffsetBlurSpreadNotation() throws Exception {
    String input = "1px 2px 0 0";
    CssShadow result = PropertyResolver.readShadow( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.offsetX );
    assertEquals( 2, result.offsetY );
    assertEquals( 0, result.blur );
    assertEquals( 0, result.spread );
    assertEquals( "#000000", result.color );
    assertEquals( 1f, result.opacity, 0 );
  }

  @Test
  public void testShadow_FullNotation_NamedColor() throws Exception {
    String input = "1px 2px 0px 0 red";
    CssShadow result = PropertyResolver.readShadow( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.offsetX );
    assertEquals( 2, result.offsetY );
    assertEquals( 0, result.blur );
    assertEquals( 0, result.spread );
    assertEquals( "#ff0000", result.color );
    assertEquals( 1f, result.opacity, 0 );
  }

  @Test
  public void testShadow_FullNotation_HexColor() throws Exception {
    String input = "1px 2px 0px 0 #FF0000";
    CssShadow result = PropertyResolver.readShadow( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.offsetX );
    assertEquals( 2, result.offsetY );
    assertEquals( 0, result.blur );
    assertEquals( 0, result.spread );
    assertEquals( "#ff0000", result.color );
    assertEquals( 1f, result.opacity, 0 );
  }

  @Test
  public void testShadow_FullNotation_RgbColor() throws Exception {
    String input = "1px 2px 3px 0 rgb( 1, 2, 3 )";
    CssShadow result = PropertyResolver.readShadow( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.offsetX );
    assertEquals( 2, result.offsetY );
    assertEquals( 3, result.blur );
    assertEquals( 0, result.spread );
    assertEquals( "#010203", result.color );
    assertEquals( 1f, result.opacity, 0 );
  }

  @Test
  public void testShadow_FullNotation_RgbaColor() throws Exception {
    String input = "1px 2px 0px 0 rgba( 1, 2, 3, 0.25 )";
    CssShadow result = PropertyResolver.readShadow( parseProperty( input ) );
    assertNotNull( result );
    assertEquals( 1, result.offsetX );
    assertEquals( 2, result.offsetY );
    assertEquals( 0, result.blur );
    assertEquals( 0, result.spread );
    assertEquals( "#010203", result.color );
    assertEquals( 0.25, result.opacity, 0 );
  }

  @Test
  public void testShadow_WithoutOffsetY() throws Exception {
    try {
      PropertyResolver.readShadow( parseProperty( "1px" ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testShadow_MissingRgbaParameters() throws Exception {
    String input = "1px 2px 0px 0 rgba( 1, 2, 0.25 )";
    try {
      PropertyResolver.readShadow( parseProperty( input ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testShadow_NonZeroSpread() throws Exception {
    String input = "1px 2px 3px 3px rgba( 1, 2, 3, 0.25 )";
    try {
      PropertyResolver.readShadow( parseProperty( input ) );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testIsColorProperty() {
    assertFalse( PropertyResolver.isColorProperty( "border" ) );
    assertTrue( PropertyResolver.isColorProperty( "color" ) );
    assertTrue( PropertyResolver.isColorProperty( "background-color" ) );
    assertTrue( PropertyResolver.isColorProperty( "background-gradient-color" ) );
    assertTrue( PropertyResolver.isColorProperty( "rwt-selectionmarker-color" ) );
  }

  @Test
  public void testIsBorderProperty() {
    assertTrue( PropertyResolver.isBorderProperty( "border" ) );
    assertTrue( PropertyResolver.isBorderProperty( "border-left" ) );
    assertTrue( PropertyResolver.isBorderProperty( "border-bottom" ) );
  }

  @Test
  public void testIsFontProperty() {
    assertTrue( PropertyResolver.isFontProperty( "font" ) );
  }

  @Test
  public void testIsDimensionProperty() {
    assertTrue( PropertyResolver.isDimensionProperty( "spacing" ) );
    assertTrue( PropertyResolver.isDimensionProperty( "width" ) );
    assertTrue( PropertyResolver.isDimensionProperty( "height" ) );
    assertTrue( PropertyResolver.isDimensionProperty( "min-height" ) );
  }

  @Test
  public void testIsBoxDimProperty() {
    assertTrue( PropertyResolver.isBoxDimensionProperty( "padding" ) );
    assertTrue( PropertyResolver.isBoxDimensionProperty( "margin" ) );
  }

  @Test
  public void testIsImageProperty() {
    assertTrue( PropertyResolver.isImageProperty( "background-image" ) );
  }

  @Test
  public void testIsCursorProperty() {
    assertTrue( PropertyResolver.isCursorProperty( "cursor" ) );
  }

  @Test
  public void testIsAnimationProperty() {
    assertTrue( PropertyResolver.isAnimationProperty( "animation" ) );
  }

  @Test
  public void testIsShadowProperty() {
    assertTrue( PropertyResolver.isShadowProperty( "box-shadow" ) );
    assertTrue( PropertyResolver.isShadowProperty( "text-shadow" ) );
  }

  @Test
  public void testIsBackgroundRepeat() {
    assertTrue( PropertyResolver.isBackgroundRepeatProperty( "background-repeat" ) );
  }

  @Test
  public void testBackgroundRepeat_Valid() throws Exception {
    CssIdentifier identifier = PropertyResolver.readBackgroundRepeat( parseProperty( "repeat" ) );

    assertEquals( "repeat", identifier.value );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBackgroundRepeat_Invalid() throws Exception {
    PropertyResolver.readBackgroundRepeat( parseProperty( "foo" ) );
  }

  @Test
  public void testIsBackgroundPosition() {
    assertTrue( PropertyResolver.isBackgroundPositionProperty( "background-position" ) );
  }

  @Test
  public void testBackgroundPosition_Valid() throws Exception {
    LexicalUnit input = parseProperty( "left top" );
    CssIdentifier identifier = PropertyResolver.readBackgroundPosition( input );

    assertEquals( "left top", identifier.value );
  }

  @Test
  public void testBackgroundPosition_ValidOneKeyword() throws Exception {
    LexicalUnit input = parseProperty( "left" );
    CssIdentifier identifier = PropertyResolver.readBackgroundPosition( input );

    assertEquals( "left center", identifier.value );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testBackgroundposition_Invalid() throws Exception {
    PropertyResolver.readBackgroundRepeat( parseProperty( "foo bar" ) );
  }

  @Test
  public void testIsTextDecorationProperty() {
    assertTrue( PropertyResolver.isTextDecorationProperty( "text-decoration" ) );
  }

  @Test
  public void testTextDecoration_Valid() throws Exception {
    CssIdentifier identifier = PropertyResolver.readTextDecoration( parseProperty( "underline" ) );

    assertEquals( "underline", identifier.value );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testTextDecoration_Invalid() throws Exception {
    PropertyResolver.readTextDecoration( parseProperty( "foo" ) );
  }

  @Test
  public void testIsTextOverflowProperty() {
    assertTrue( PropertyResolver.isTextOverflowProperty( "text-overflow" ) );
  }

  @Test
  public void testTextOverflow_Valid() throws Exception {
    CssIdentifier identifier = PropertyResolver.readTextOverflow( parseProperty( "ellipsis" ) );

    assertEquals( "ellipsis", identifier.value );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testTextOverflow_Invalid() throws Exception {
    PropertyResolver.readTextOverflow( parseProperty( "foo" ) );
  }

  @Test
  public void testIsTextAlignProperty() {
    assertTrue( PropertyResolver.isTextAlignProperty( "text-align" ) );
  }

  @Test
  public void testTextAlign_Valid() throws Exception {
    CssIdentifier identifier = PropertyResolver.readTextAlign( parseProperty( "center" ) );

    assertEquals( "center", identifier.value );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testTextAlign_Invalid() throws Exception {
    PropertyResolver.readTextAlign( parseProperty( "foo" ) );
  }

  private static LexicalUnit parseProperty( String input )
    throws CSSException, IOException
  {
    InputSource inputSource = new InputSource();
    InputStream byteStream = new ByteArrayInputStream( input.getBytes() );
    inputSource.setByteStream( byteStream );
    return parser.parsePropertyValue( inputSource );
  }

}
