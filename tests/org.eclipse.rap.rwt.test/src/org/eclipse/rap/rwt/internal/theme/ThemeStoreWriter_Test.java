/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ThemeStoreWriter_Test {
  private static final String THEME_WRITE_IMAGES = "themeWriteImages";
  private static final String THEME_WRITE_COLORS = "themeWriteColors";
  private static final String THEME_WRITE_SHADOW = "themeWriteShadow";
  private static final String THEME_WRITE_HORIZONTAL_GRADIENT = "themeWriteHorizontalGradient";
  private static final String THEME_WRITE_VERTICAL_GRADIENT = "themeWriteVerticalGradient";
  private static final String THEME_ANIMATIONS = "themeAnimations";
  private static final String THEME_SET_CURRENT_THEME_ID = "themeSetCurrentThemeId";

  // static field used for performance improvements of test initialization
  private static Map<String,Theme> themes;

  @Before
  public void setUp() throws Exception {
    Fixture.setUp();
    initializeThemesOnFirstSetUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testSetCurrentThemeId() {
    ThemeCssElement element = new ThemeCssElement( "Button" );
    element.addProperty( "color" );
    element.addProperty( "background-image" );
    Theme theme = getTheme( THEME_SET_CURRENT_THEME_ID );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( theme, elements );
    String output = storeWriter.createJson();
    // register colors
    assertTrue( output.contains( "\"#000000\"" ) );
    assertTrue( output.contains( "\"#ff0000\"" ) );
    // register images, with sizes
    String expected;
    expected = "\"ba873d77.png\": [ 50, 100 ]";
    assertTrue( output.contains( expected ) );
    // conditional colors
    expected =   "\"color\": [ [ [ \"[BORDER\" ], "
               + "\"400339c0\" ], [ [], \"3fe41900\" ] ]";
    assertTrue( output.contains( expected ) );
    // conditional background-images
    expected =   "\"background-image\": "
               + "[ [ [ \"[BORDER\" ], \"ba873d77.png\" ], [ [], \"a505df1b\" ] ]";
    assertTrue( output.contains( expected ) );
  }

  @Test
  public void testWriteAnimations() {
    ThemeCssElement element = new ThemeCssElement( "Menu" );
    element.addProperty( "animation" );
    Theme theme = getTheme( THEME_ANIMATIONS );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( theme, elements );
    String output = storeWriter.createJson();
    String expected =   "\"animations\": {\n"
                      + "\"46ab28c4\": {\n"
                      + "\"slideIn\": [ 2000, \"easeIn\" ],\n"
                      + "\"slideOut\": [ 2000, \"easeOut\" ]\n"
                      + "}\n"
                      + "}";
    assertTrue( output.contains( expected ) );
    expected =   "\"Menu\": {\n"
               + "\"animation\": [ [ [], \"46ab28c4\" ] ]\n"
               + "}";
    assertTrue( output.contains( expected ) );
  }

  @Test
  public void testWriteVerticalGradient() {
    ThemeCssElement element = new ThemeCssElement( "Button" );
    element.addProperty( "background-image" );
    Theme theme = getTheme( THEME_WRITE_VERTICAL_GRADIENT );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( theme, elements );
    String output = storeWriter.createJson();
    String expected =   "\"gradients\": {\n"
                      + "\"2eb911d6\": {\n"
                      + "\"percents\": [ 0.0, 48.0, 52.0, 100.0 ],\n"
                      + "\"colors\": [ \"#ffffff\", \"#f0f0f0\", \"#e0e0e0\", \"#ffffff\" ],\n"
                      + "\"vertical\": true\n"
                      + "}\n"
                      + "}";
    assertTrue( output.contains( expected ) );
    expected =   "\"Button\": {\n"
               + "\"background-image\": [ [ [], \"2eb911d6\" ] ]\n"
               + "}";
    assertTrue( output.contains( expected ) );
  }

  @Test
  public void testWriteHorizontalGradient() {
    ThemeCssElement element = new ThemeCssElement( "Button" );
    element.addProperty( "background-image" );
    Theme theme = getTheme( THEME_WRITE_HORIZONTAL_GRADIENT );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( theme, elements );
    String output = storeWriter.createJson();
    String expected =   "\"gradients\": {\n"
                      + "\"2762759\": {\n"
                      + "\"percents\": [ 0.0, 48.0, 52.0, 100.0 ],\n"
                      + "\"colors\": [ \"#ffffff\", \"#f0f0f0\", \"#e0e0e0\", \"#ffffff\" ],\n"
                      + "\"vertical\": false\n"
                      + "}\n"
                      + "}";
    assertTrue( output.contains( expected ) );
    expected =   "\"Button\": {\n"
               + "\"background-image\": [ [ [], \"2762759\" ] ]\n"
               + "}";
    assertTrue( output.contains( expected ) );
  }

  @Test
  public void testWriteShadow() {
    ThemeCssElement element = new ThemeCssElement( "Shell" );
    element.addProperty( "box-shadow" );
    Theme theme = getTheme( THEME_WRITE_SHADOW );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( theme, elements );
    String output = storeWriter.createJson();
    String expected =   "\"shadows\": {\n"
                      + "\"2aedfabd\": [ false, 10, 10, 3, 0, \"#000000\", 0.5 ]\n"
                      + "}\n";
    assertTrue( output.contains( expected ) );
    expected =   "\"Shell\": {\n"
               + "\"box-shadow\": [ [ [], \"2aedfabd\" ] ]\n"
               + "}";
    assertTrue( output.contains( expected ) );
  }

  @Test
  public void testWriteColors() {
    ThemeCssElement element = new ThemeCssElement( "Button" );
    element.addProperty( "color" );
    element.addProperty( "background-color" );
    Theme theme = getTheme( THEME_WRITE_COLORS );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( theme, elements );
    String output = storeWriter.createJson();
    String expected =   "\"colors\": {\n"
                      + "\"ffffffff\": \"undefined\",\n"
                      + "\"400339c0\": \"#ff0000\",\n"
                      + "\"3ffe9078\": \"#cecece\"\n"
                      + "}";
    assertTrue( output.contains( expected ) );
    expected =
        "\"Button\": {\n"
      + "\"color\": [ [ [ \".special\" ], \"ffffffff\" ], [ [], \"400339c0\" ] ],\n"
      + "\"background-color\": [ [ [ \".special\" ], \"3ffe9078\" ], [ [], \"ffffffff\" ] ]\n"
      + "}";
    assertTrue( output.contains( expected ) );
  }

  @Test
  public void testWriteImages() {
    ThemeCssElement element = new ThemeCssElement( "Button" );
    element.addProperty( "background-image" );
    Theme theme = getTheme( THEME_WRITE_IMAGES );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( theme, elements );
    String output = storeWriter.createJson();
    String expectedImages =   "\"images\": {\n"
                            + "\"c84ae54c.png\": [ 100, 50 ]\n"
                            + "}";
    assertTrue( output.contains( expectedImages ) );
    String expectedGradients =   "\"gradients\": {\n"
                               + "\"154e1724\": {\n"
                               + "\"percents\": [ 0.0, 100.0 ],\n"
                               + "\"colors\": [ \"#000000\", \"#ffffff\" ],\n"
                               + "\"vertical\": true\n"
                               + "}";
    assertTrue( output.contains( expectedGradients ) );
    String expected =
        "\"Button\": {\n"
      + "\"background-image\": [ [ [ \".special\" ], \"154e1724\" ], [ [], \"c84ae54c.png\" ] ]\n"
      + "}";
    assertTrue( output.contains( expected ) );
  }

  private void initializeThemesOnFirstSetUp() throws Exception {
    if( themes == null ) {
      themes = new HashMap<String,Theme>();
      registerThemeForTestSetCurrentThemeId();
      registerThemeForTestWriteAnimations();
      registerThemeForTestWriteColors();
      registerThemeForTestWriteHorizontalGradient();
      registerThemeForTestWriteImages();
      registerThemeForTestWriteShadow();
      registerThemeForTestWriteVerticalGradient();
    }
  }

  private void registerThemeForTestWriteImages() throws IOException {
    String cssCode =   "Button { background-image: url( " + Fixture.IMAGE_100x50 + " ); }\n"
    + "Button.special { background-image: gradient( linear, left top, left bottom,\n"
    + "  from( #000000 ),\n"
    + "  to( #ffffff )\n"
    + "); }\n";
    registerTheme( THEME_WRITE_IMAGES, cssCode );
  }

  private void registerThemeForTestWriteColors() throws IOException {
    String cssCode =   "Button { color: red; background-color: transparent; }\n"
                     + "Button.special { color: inherit; background-color: #cecece; }\n";
    registerTheme( THEME_WRITE_COLORS, cssCode );
  }

  private void registerThemeForTestWriteShadow() throws IOException {
    String cssCode = "Shell { box-shadow: 10px 10px 3px 0 rgba( 0, 0, 0, 0.5 ); }\n";
    registerTheme( THEME_WRITE_SHADOW, cssCode );
  }

  private void registerThemeForTestWriteHorizontalGradient() throws IOException {
    String cssCode =   "Button { background-image: gradient(\n"
                     + "linear, left top, right top,\n"
                     + "from( #ffffff ),\n"
                     + "color-stop( 48%, #f0f0f0 ),\n"
                     + "color-stop( 52%, #e0e0e0 ),\n"
                     + "to( #ffffff )\n"
                     + "); }";
    registerTheme( THEME_WRITE_HORIZONTAL_GRADIENT, cssCode );
  }

  private void registerThemeForTestWriteVerticalGradient() throws IOException {
    String cssCode =   "Button { background-image: gradient(\n"
                     + "linear, left top, left bottom,\n"
                     + "from( #ffffff ),\n"
                     + "color-stop( 48%, #f0f0f0 ),\n"
                     + "color-stop( 52%, #e0e0e0 ),\n"
                     + "to( #ffffff )\n"
                     + "); }";
    registerTheme( THEME_WRITE_VERTICAL_GRADIENT, cssCode );
  }

  private void registerThemeForTestSetCurrentThemeId() throws IOException {
    String cssCode =   "Button { color: black; }\n"
                     + "Button[BORDER] { color: red; }\n"
                     + "Button { background-image: none;\n }"
                     + "Button[BORDER] { background-image: url( "
                     + Fixture.IMAGE_50x100
                     + " ); }\n";
    registerTheme( THEME_SET_CURRENT_THEME_ID, cssCode );
  }


  private void registerThemeForTestWriteAnimations() throws Exception {
    String cssCode = "Menu { animation: slideIn 2s ease-in, slideOut 2s ease-out; }\n";
    registerTheme( THEME_ANIMATIONS, cssCode );
  }

  private void registerTheme( String themeId, String cssCode ) throws IOException {
    Theme theme = ThemeTestUtil.createTheme( themeId, cssCode, ThemeTestUtil.RESOURCE_LOADER );
    theme.initialize( getApplicationContext().getThemeManager().getAllThemeableWidgets() );
    themes.put( themeId, theme );
  }

  private Theme getTheme( String themeId ) {
    return themes.get( themeId );
  }

}
