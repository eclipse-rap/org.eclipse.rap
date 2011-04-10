/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;


public class ThemeStoreWriter_Test extends TestCase {

  public void testSetCurrentThemeId() throws Exception {
    ThemeCssElement element1 = new ThemeCssElement( "Button" );
    element1.addProperty( "color" );
    element1.addProperty( "background-image" );
    IThemeCssElement[] elements = new IThemeCssElement[] { element1 };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( elements );
    String themeId = "myTheme";
    String cssCode =   "Button { color: black; }\n"
                     + "Button[BORDER] { color: red; }\n"
                     + "Button { background-image: none;\n }"
                     + "Button[BORDER] { background-image: url( "
                     + Fixture.IMAGE_50x100
                     + " ); }\n";
    ResourceLoader loader = ThemeTestUtil.createResourceLoader( Fixture.class );
    ThemeTestUtil.registerCustomTheme( themeId, cssCode, loader );
    Theme theme = ThemeManager.getInstance().getTheme( themeId );
    storeWriter.addTheme( theme, true );
    String output = storeWriter.createJs();
    // register colors
    assertTrue( output.indexOf( "\"#000000\"" ) != -1 );
    assertTrue( output.indexOf( "\"#ff0000\"" ) != -1 );
    // register images, with sizes
    String expected;
    expected = "\"cd56ce7d\": [ 50, 100 ]";
    assertTrue( output.indexOf( expected ) != -1 );
    // conditional colors
    expected =   "\"color\": [ [ [ \"[BORDER\" ], "
               + "\"400339c0\" ], [ [], \"3fe41900\" ] ]";
    assertTrue( output.indexOf( expected ) != -1 );
    // conditional background-images
    expected =   "\"background-image\": "
               + "[ [ [ \"[BORDER\" ], \"cd56ce7d\" ], [ [], \"ffffffff\" ] ]";
    assertTrue( output.indexOf( expected ) != -1 );
  }

  public void testWriteAnimations() throws Exception {
    ThemeCssElement element1 = new ThemeCssElement( "Menu" );
    element1.addProperty( "animation" );
    IThemeCssElement[] elements = new IThemeCssElement[] { element1 };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( elements );
    String themeId = "myTheme";
    String cssCode = "Menu { animation: slideIn 2s ease-in, slideOut 2s ease-out; }\n";
    ResourceLoader loader = ThemeTestUtil.createResourceLoader( Fixture.class );
    ThemeTestUtil.registerCustomTheme( themeId, cssCode, loader );
    Theme theme = ThemeManager.getInstance().getTheme( themeId );
    storeWriter.addTheme( theme, true );
    String output = storeWriter.createJs();
    String expected =   "\"animations\": {\n"
                      + "\"2e5f3d63\": {\n"
                      + "\"slideIn\": [ 2000, \"easeIn\" ],\n"
                      + "\"slideOut\": [ 2000, \"easeOut\" ]\n"
                      + "}\n"
                      + "}";
    assertTrue( output.indexOf( expected ) != -1 );
    expected =   "\"Menu\": {\n"
               + "\"animation\": [ [ [], \"2e5f3d63\" ] ]\n"
               + "}";
    assertTrue( output.indexOf( expected ) != -1 );
  }

  public void testWriteVerticalGradient() throws Exception {
    ThemeCssElement element = new ThemeCssElement( "Button" );
    element.addProperty( "background-image" );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( elements );
    String themeId = "myTheme";
    String cssCode =   "Button { background-image: gradient(\n"
                     + "linear, left top, left bottom,\n"
                     + "from( #ffffff ),\n"
                     + "color-stop( 48%, #f0f0f0 ),\n"
                     + "color-stop( 52%, #e0e0e0 ),\n"
                     + "to( #ffffff )\n"
                     + "); }";
    ResourceLoader loader = ThemeTestUtil.createResourceLoader( Fixture.class );
    ThemeTestUtil.registerCustomTheme( themeId, cssCode, loader );
    Theme theme = ThemeManager.getInstance().getTheme( themeId );
    storeWriter.addTheme( theme, true );
    String output = storeWriter.createJs();
    String expected =   "\"gradients\": {\n"
                      + "\"96f80000\": {\n"
                      + "\"percents\": [ 0.0, 48.0, 52.0, 100.0 ],\n"
                      + "\"colors\": [ \"#ffffff\", \"#f0f0f0\", \"#e0e0e0\", \"#ffffff\" ],\n"
                      + "\"vertical\": true\n"
                      + "}\n"
                      + "}";
    assertTrue( output.indexOf( expected ) != -1 );
    expected =   "\"Button\": {\n"
               + "\"background-image\": [ [ [], \"96f80000\" ] ]\n"
               + "}";
    assertTrue( output.indexOf( expected ) != -1 );
  }

  public void testWriteHorizontalGradient() throws Exception {
    ThemeCssElement element = new ThemeCssElement( "Button" );
    element.addProperty( "background-image" );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( elements );
    String themeId = "myTheme";
    String cssCode =   "Button { background-image: gradient(\n"
                     + "linear, left top, right top,\n"
                     + "from( #ffffff ),\n"
                     + "color-stop( 48%, #f0f0f0 ),\n"
                     + "color-stop( 52%, #e0e0e0 ),\n"
                     + "to( #ffffff )\n"
                     + "); }";
    ResourceLoader loader = ThemeTestUtil.createResourceLoader( Fixture.class );
    ThemeTestUtil.registerCustomTheme( themeId, cssCode, loader );
    Theme theme = ThemeManager.getInstance().getTheme( themeId );
    storeWriter.addTheme( theme, true );
    String output = storeWriter.createJs();
    String expected =   "\"gradients\": {\n"
                      + "\"df000025\": {\n"
                      + "\"percents\": [ 0.0, 48.0, 52.0, 100.0 ],\n"
                      + "\"colors\": [ \"#ffffff\", \"#f0f0f0\", \"#e0e0e0\", \"#ffffff\" ],\n"
                      + "\"vertical\": false\n"
                      + "}\n"
                      + "}";
    assertTrue( output.indexOf( expected ) != -1 );
    expected =   "\"Button\": {\n"
               + "\"background-image\": [ [ [], \"df000025\" ] ]\n"
               + "}";
    assertTrue( output.indexOf( expected ) != -1 );
  }

  public void testWriteShadow() throws Exception {
    ThemeCssElement element = new ThemeCssElement( "Shell" );
    element.addProperty( "box-shadow" );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( elements );
    String themeId = "myTheme";
    String cssCode = "Shell { box-shadow: 10px 10px 3px 0 rgba( 0, 0, 0, 0.5 ); }\n";
    ResourceLoader loader = ThemeTestUtil.createResourceLoader( Fixture.class );
    ThemeTestUtil.registerCustomTheme( themeId, cssCode, loader );
    Theme theme = ThemeManager.getInstance().getTheme( themeId );
    storeWriter.addTheme( theme, true );
    String output = storeWriter.createJs();
    String expected =   "\"shadows\": {\n"
                      + "\"2aedfabd\": [ false, 10, 10, 3, 0, \"#000000\", 0.5 ]\n"
                      + "}\n";
    assertTrue( output.indexOf( expected ) != -1 );
    expected =   "\"Shell\": {\n"
               + "\"box-shadow\": [ [ [], \"2aedfabd\" ] ]\n"
               + "}";
    assertTrue( output.indexOf( expected ) != -1 );
  }

  public void testWriteColors() throws Exception {
    ThemeCssElement element = new ThemeCssElement( "Button" );
    element.addProperty( "color" );
    element.addProperty( "background-color" );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( elements );
    String themeId = "myTheme";
    String cssCode =   "Button { color: red; background-color: transparent; }\n"
                     + "Button.special { color: inherit; background-color: #cecece; }\n";
    ResourceLoader loader = ThemeTestUtil.createResourceLoader( Fixture.class );
    ThemeTestUtil.registerCustomTheme( themeId, cssCode, loader );
    Theme theme = ThemeManager.getInstance().getTheme( themeId );
    storeWriter.addTheme( theme, true );
    String output = storeWriter.createJs();
    String expected =   "\"colors\": {\n"
                      + "\"3ffe9078\": \"#cecece\",\n"
                      + "\"ffffffff\": \"undefined\",\n"
                      + "\"400339c0\": \"#ff0000\"\n"
                      + "}";
    assertTrue( output.indexOf( expected ) != -1 );
    expected =   "\"Button\": {\n"
               + "\"color\": [ [ [ \".special\" ], \"ffffffff\" ], [ [], \"400339c0\" ] ],\n"
               + "\"background-color\": [ [ [ \".special\" ], \"3ffe9078\" ], [ [], \"ffffffff\" ] ]\n"
               + "}";
    assertTrue( output.indexOf( expected ) != -1 );
  }

  public void testWriteImages() throws Exception {
    ThemeCssElement element = new ThemeCssElement( "Button" );
    element.addProperty( "background-image" );
    IThemeCssElement[] elements = new IThemeCssElement[] { element };
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( elements );
    String themeId = "myTheme";
    String cssCode =   "Button { background-image: url( " + Fixture.IMAGE_100x50 + " ); }\n"
                     + "Button.special { background-image: gradient( linear, left top, left bottom,\n"
                     + "  from( #000000 ),\n"
                     + "  to( #ffffff )\n"
                     + "); }\n";
    ResourceLoader loader = ThemeTestUtil.createResourceLoader( Fixture.class );
    ThemeTestUtil.registerCustomTheme( themeId, cssCode, loader );
    Theme theme = ThemeManager.getInstance().getTheme( themeId );
    storeWriter.addTheme( theme, true );
    String output = storeWriter.createJs();
    String expectedImages =   "\"images\": {\n"
                            + "\"793f156b\": [ 100, 50 ]\n"
                            + "}";
    assertTrue( output.indexOf( expectedImages ) != -1 );
    String expectedGradients =   "\"gradients\": {\n"
                               + "\"714a0c00\": {\n"
                               + "\"percents\": [ 0.0, 100.0 ],\n"
                               + "\"colors\": [ \"#000000\", \"#ffffff\" ],\n"
                               + "\"vertical\": true\n"
                               + "}";
    assertTrue( output.indexOf( expectedGradients ) != -1 );
    String expected =   "\"Button\": {\n"
                      + "\"background-image\": [ [ [ \".special\" ], \"714a0c00\" ], [ [], \"793f156b\" ] ]\n"
                      + "}";
    assertTrue( output.indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
