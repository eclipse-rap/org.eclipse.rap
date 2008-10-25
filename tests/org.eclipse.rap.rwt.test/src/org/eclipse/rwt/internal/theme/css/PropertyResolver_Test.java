/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme.css;

import java.io.*;

import junit.framework.TestCase;

import org.apache.batik.css.parser.Parser;
import org.eclipse.rwt.internal.theme.*;
import org.w3c.css.sac.*;

public class PropertyResolver_Test extends TestCase {

  private static Parser parser = new Parser();

  private ResourceLoader dummyResourceLoader = new ResourceLoader() {

    public InputStream getResourceAsStream( final String resourceName )
      throws IOException
    {
      return null;
    }
  };

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
    QxColor mixed
      = PropertyResolver.readColor( parseProperty( "rgb( 0%, 50, 100 )" ) );
    assertNull( mixed );
    QxColor inherit = PropertyResolver.readColor( parseProperty( "inherit" ) );
    assertNull( inherit );
  }

  public void testDimension() throws Exception {
    QxDimension zero = PropertyResolver.readDimension( parseProperty( "0px" ) );
    assertNotNull( zero );
    assertEquals( QxDimension.ZERO, zero );
    QxDimension dim2 = PropertyResolver.readDimension( parseProperty( "2px" ) );
    assertNotNull( dim2 );
    assertEquals( QxDimension.create( 2 ), dim2 );
    QxDimension invalid = PropertyResolver.readDimension( parseProperty( "2em" ) );
    assertNull( invalid );
  }

  public void testBoxdimensions() throws Exception {
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
    LexicalUnit illegalUnit = parseProperty( "2" );
    QxBoxDimensions illegal1 = PropertyResolver.readBoxDimensions( illegalUnit );
    assertNull( illegal1 );
    LexicalUnit illegalUnit2 = parseProperty( "2em" );
    QxBoxDimensions illegal2 = PropertyResolver.readBoxDimensions( illegalUnit2 );
    assertNull( illegal2 );
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

  public void testBorderStyle() throws Exception {
    String nonsense = PropertyResolver.readBorderStyle( parseProperty( "nonsense" ) );
    assertNull( nonsense );
    String none = PropertyResolver.readBorderStyle( parseProperty( "none" ) );
    assertEquals( "none", none );
  }

  public void testBorder() throws Exception {
    String input = "1";
    QxBorder illegal1 = PropertyResolver.readBorder( parseProperty( input ) );
    assertNull( illegal1 );
    input = "1px red blue green";
    QxBorder illegal2 = PropertyResolver.readBorder( parseProperty( input ) );
    assertNull( illegal2 );
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

  public void testUrl() throws Exception {
    String expected = "http://example.org";
    String input1 = "url( 'http://example.org' )";
    String res1 = PropertyResolver.readUrl( parseProperty( input1 ) );
    assertEquals( expected, res1 );
    String input2 = "url( \"http://example.org\" )";
    String res2 = PropertyResolver.readUrl( parseProperty( input2 ) );
    assertEquals( expected, res2 );
    String input3 = "url(http://example.org)";
    String res3 = PropertyResolver.readUrl( parseProperty( input3 ) );
    assertEquals( expected, res3 );
    String illegal = "2";
    String resIllegal = PropertyResolver.readUrl( parseProperty( illegal ) );
    assertNull( resIllegal );
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
    String invalid1 = "Helvetica";
    assertNull( PropertyResolver.readFont( parseProperty( invalid1 ) ) );
    String invalid2 = "bold Helvetica";
    assertNull( PropertyResolver.readFont( parseProperty( invalid2 ) ) );
    String invalid3 = "Helvetica 8px";
    assertNull( PropertyResolver.readFont( parseProperty( invalid3 ) ) );
  }

  public void testBackgroundImage() throws Exception {
    String input1 = "none";
    QxImage res1 = PropertyResolver.readBackgroundImage( parseProperty( input1 ),
                                                         dummyResourceLoader );
    assertEquals( QxImage.NONE, res1 );
    String input2 = "url( \"sample.png\" )";
    QxImage res2 = PropertyResolver.readBackgroundImage( parseProperty( input2 ),
                                                         dummyResourceLoader );
    assertEquals( QxImage.valueOf( "sample.png", dummyResourceLoader  ), res2 );
    String input3 = "url( sample.png )";
    QxImage res3 = PropertyResolver.readBackgroundImage( parseProperty( input3 ),
                                                         dummyResourceLoader );
    assertEquals( QxImage.valueOf( "sample.png", dummyResourceLoader  ), res3 );
    String input4 = "'sample.png'";
    QxImage res4 = PropertyResolver.readBackgroundImage( parseProperty( input4 ),
                                                         dummyResourceLoader );
    assertNull( res4 );
  }

  public void testGetType() throws Exception {
    assertEquals( "color", PropertyResolver.getType( "color" ) );
    assertEquals( "color", PropertyResolver.getType( "background-color" ) );
    assertEquals( "color",
                  PropertyResolver.getType( "background-gradient-color" ) );
    assertEquals( "color",
                  PropertyResolver.getType( "rwt-selectionmarker-color" ) );
    assertEquals( "border", PropertyResolver.getType( "border" ) );
    assertEquals( "font", PropertyResolver.getType( "font" ) );
    assertEquals( "boxdim", PropertyResolver.getType( "padding" ) );
    assertEquals( "boxdim", PropertyResolver.getType( "margin" ) );
    assertEquals( "image", PropertyResolver.getType( "background-image" ) );
    assertNull( PropertyResolver.getType( "unknown" ) );
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
      assertTrue( e.getMessage().indexOf( "value darkslategray" ) != -1 );
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
