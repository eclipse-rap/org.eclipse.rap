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

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


public class ThemeUtil_Test extends TestCase {

  public void testSetCurrentThemeId() throws Exception {
    ThemeManager.resetInstance();
    ThemeManager manager = ThemeManager.getInstance();
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    Theme theme = new Theme( "custom.id", "Custom Theme", styleSheet );
    manager.registerTheme( theme );
    manager.initialize();
    ThemeUtil.setCurrentThemeId( "custom.id" );
    assertEquals( "custom.id", ThemeUtil.getCurrentThemeId() );
  }

  public void testSetCurrentThemeIdInvalid() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    try {
      ThemeUtil.setCurrentThemeId( "woo.doo.schick.schnack" );
      fail( "should throw IAE for invalid theme ids" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testGetDefaultTheme() throws Exception {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    assertNotNull( ThemeUtil.getDefaultTheme() );
  }

  public void testGetTheme() throws Exception {
    ThemeManager.resetInstance();
    ThemeManager themeManager = ThemeManager.getInstance();
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    Theme customTheme = new Theme( "custom.id", "Custom Theme", styleSheet );
    themeManager.registerTheme( customTheme );
    themeManager.initialize();
    assertNotNull( ThemeUtil.getTheme() );
    assertSame( ThemeUtil.getDefaultTheme(), ThemeUtil.getTheme() );
    ThemeUtil.setCurrentThemeId( "custom.id" );
    assertNotSame( ThemeUtil.getDefaultTheme(), ThemeUtil.getTheme() );
    assertSame( customTheme, ThemeUtil.getTheme() );
  }

  public void testGetCssValue() throws IOException {
    ThemeManager.resetInstance();
    ThemeManager themeManager = ThemeManager.getInstance();
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    Theme customTheme = new Theme( "custom.id", "Custom Theme", styleSheet );
    themeManager.registerTheme( customTheme );
    themeManager.initialize();
    ThemeUtil.setCurrentThemeId( "custom.id" );
    SimpleSelector selector = new SimpleSelector( new String[] { ".special" } );
    QxType cssValue = ThemeUtil.getCssValue( "Button", "color", selector );
    assertEquals( "#ff0000", ( ( QxColor )cssValue ).toDefaultString() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
