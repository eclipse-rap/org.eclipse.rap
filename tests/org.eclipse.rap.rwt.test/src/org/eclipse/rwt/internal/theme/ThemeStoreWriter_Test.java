/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;


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
                     + RWTFixture.IMAGE_50x100
                     + " ); }\n";
    ResourceLoader loader
      = ThemeTestUtil.createResourceLoader( RWTFixture.class );
    ThemeTestUtil.registerCustomTheme( themeId, cssCode, loader );
    Theme theme = ThemeManager.getInstance().getTheme( themeId );
    storeWriter.addTheme( theme, true );
    String output = storeWriter.createJs();
    // register colors
    assertTrue( output.indexOf( "\"#000000\"" ) != -1 );
    assertTrue( output.indexOf( "\"#ff0000\"" ) != -1 );
    // register images, with sizes
    String expected;
    expected = "\"ffffffff\": null";
    assertTrue( output.indexOf( expected ) != -1 );
    expected = "\"cd56ce7d\": [ \"cd56ce7d\", 50, 100 ]";
    assertTrue( output.indexOf( expected ) != -1 );
    // conditional colors
    expected = "\"color\": [ [ [ \"[BORDER\" ], \"ff\" ], [ [], \"0\" ] ]";
    assertTrue( output.indexOf( expected ) != -1 );
    // conditional background-images
    expected = "\"background-image\": "
               + "[ [ [ \"[BORDER\" ], \"cd56ce7d\" ], [ [], \"ffffffff\" ] ]";
    assertTrue( output.indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    ThemeManager.getInstance().reset();
    RWTFixture.tearDown();
  }
}
