/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.graphics.Color;


public class ThemeUtil_Test extends TestCase {

  private static final ResourceLoader LOADER
    = ThemeTestUtil.createResourceLoader( ThemeUtil_Test.class );

  public void testSetCurrentThemeId() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String validThemeId = "test.valid.theme";
    String themeName = "Valid Test Theme";
    String themeFile = "resources/theme/TestExample.css";
    manager.registerTheme( validThemeId, themeName, themeFile, LOADER );
    ThemeUtil.setCurrentThemeId( validThemeId );
    assertEquals( validThemeId, ThemeUtil.getCurrentThemeId() );
    try {
      ThemeUtil.setCurrentThemeId( "woo.doo.schick.schnack" );
      fail( "should throw IAE for invalid theme ids" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testGetTheme() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    Theme defTheme = ThemeUtil.getDefaultTheme();
    assertNotNull( defTheme );
    Theme currentTheme = ThemeUtil.getTheme();
    assertNotNull( currentTheme );
    assertSame( defTheme, currentTheme );
    String validThemeId = "test.valid.theme";
    String themeName = "Valid Test Theme";
    String themeFile = "resources/theme/TestExample.css";
    manager.registerTheme( validThemeId, themeName, themeFile, LOADER );
    ThemeUtil.setCurrentThemeId( validThemeId );
    assertSame( defTheme, ThemeUtil.getDefaultTheme() );
    Theme customTheme = ThemeUtil.getTheme();
    assertNotSame( defTheme, customTheme );
    assertEquals( themeName, customTheme.getName() );
  }

  public void testGetCssValue() throws IOException {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    // register custom theme
    String fileName = "resources/theme/TestExample.css";
    themeManager.registerTheme( "customId", "Custom Theme", fileName, LOADER );
    ThemeUtil.setCurrentThemeId( "customId" );
    SimpleSelector selector = new SimpleSelector( new String[] { ".special" } );
    QxType cssValue = ThemeUtil.getCssValue( "Button", "color", selector );
    Color color = QxColor.createColor( ( QxColor )cssValue );
    assertEquals( Graphics.getColor( 255, 0, 0 ), color );
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
