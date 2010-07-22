/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.theme.css.StyleRule;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.swt.widgets.Button;


public class ThemeManager_Test extends TestCase {

  public void testCreate() {
    ThemeManager manager = ThemeManager.getInstance();
    assertEquals( "org.eclipse.rap.rwt.theme.Default",
                  ThemeManager.DEFAULT_THEME_ID );
    Theme defaultTheme = manager.getTheme( ThemeManager.DEFAULT_THEME_ID );
    assertNotNull( defaultTheme );
    assertEquals( "RAP Default Theme", defaultTheme.getName() );
  }

  public void testRegisterResources() {
    ThemeManager manager = ThemeManager.getInstance();
    manager.registerResources();
    String[] themeIds = manager.getRegisteredThemeIds();
    assertNotNull( themeIds );
    assertTrue( themeIds.length > 0 );
  }

  public void testRegisterTheme() {
    ThemeManager.resetInstance();
    ThemeManager themeManager = ThemeManager.getInstance();
    StyleSheet emptyStyleSheet = new StyleSheet( new StyleRule[ 0 ] );
    Theme customTheme = new Theme( "custom.id", "foo", emptyStyleSheet );
    themeManager.registerTheme( customTheme );
    themeManager.initialize();
    assertTrue( themeManager.hasTheme( "custom.id" ) );
    List regThemeIds = Arrays.asList( themeManager.getRegisteredThemeIds() );
    assertTrue( regThemeIds.contains( "custom.id" ) );
    assertSame( customTheme, themeManager.getTheme( "custom.id" ) );
  }

  public void testRegisterThemeTwice() {
    ThemeManager.resetInstance();
    ThemeManager themeManager = ThemeManager.getInstance();
    StyleSheet emptyStyleSheet = new StyleSheet( new StyleRule[ 0 ] );
    Theme theme = new Theme( "id1", "foo", emptyStyleSheet );
    themeManager.registerTheme( theme );
    try {
      themeManager.registerTheme( theme );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testGetThemeableWidget() {
    ThemeManager manager = ThemeManager.getInstance();
    ThemeableWidget themeableWidget = manager.getThemeableWidget( Button.class );
    assertNotNull( themeableWidget );
    assertNotNull( themeableWidget.loader );
    assertEquals( Button.class, themeableWidget.widget );
    assertNotNull( themeableWidget.elements );
    assertTrue( themeableWidget.elements.length > 0 );
  }

  public void testDefaultThemeInitialized() {
    ThemeManager.resetInstance();
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    Theme defaultTheme = themeManager.getTheme( ThemeManager.DEFAULT_THEME_ID );
    assertNotNull( defaultTheme.getValuesMap() );
    assertTrue( defaultTheme.getValuesMap().getAllValues().length > 0 );
  }

  public void testCustomAndDefaultThemeInitialized() throws Exception {
    ThemeManager.resetInstance();
    ThemeManager themeManager = ThemeManager.getInstance();
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    Theme customTheme = new Theme( "custom.id", "Custom Theme", styleSheet );
    themeManager.registerTheme( customTheme );
    themeManager.initialize();
    Theme defaultTheme = themeManager.getTheme( ThemeManager.DEFAULT_THEME_ID );
    assertNotNull( defaultTheme.getValuesMap() );
    assertTrue( defaultTheme.getValuesMap().getAllValues().length > 0 );
    assertNotNull( customTheme.getValuesMap() );
    assertTrue( customTheme.getValuesMap().getAllValues().length > 0 );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
