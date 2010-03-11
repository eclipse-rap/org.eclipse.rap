/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
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

  public void testReset() {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String id = manager.getDefaultThemeId();
    assertEquals( "org.eclipse.rap.rwt.theme.Default", id );
    Theme theme = manager.getTheme( id );
    assertNotNull( theme );
    assertEquals( "RAP Default Theme", theme.getName() );
    manager.reset();
    try {
      manager.hasTheme( "foo" );
      fail( "Theme manager de-initialized, should throw IllegalStateException" );
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  public void testRegisterResources() {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    manager.registerResources();
    String[] themeIds = manager.getRegisteredThemeIds();
    assertNotNull( themeIds );
    assertTrue( themeIds.length > 0 );
  }

  public void testRegisterTheme() throws Exception {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    StyleSheet emptyStyleSheet = new StyleSheet( new StyleRule[ 0 ] );
    Theme customTheme = new Theme( "custom.id", "foo", emptyStyleSheet );
    themeManager.registerTheme( customTheme );
    assertTrue( themeManager.hasTheme( "custom.id" ) );
    List regThemeIds = Arrays.asList( themeManager.getRegisteredThemeIds() );
    assertTrue( regThemeIds.contains( "custom.id" ) );
    assertEquals( customTheme, themeManager.getTheme( "custom.id" ) );
  }

  public void testRegisterThemeTwice() throws Exception {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
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
    manager.initialize();
    ThemeableWidget themeableWidget = manager.getThemeableWidget( Button.class );
    assertNotNull( themeableWidget );
    assertNotNull( themeableWidget.loader );
    assertEquals( Button.class, themeableWidget.widget );
    assertNotNull( themeableWidget.elements );
    assertTrue( themeableWidget.elements.length > 0 );
  }

  public void testDefaultThemeInitialized() throws Exception {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    String defaultThemeId = themeManager.getDefaultThemeId();
    Theme defaultTheme = themeManager.getTheme( defaultThemeId );
    assertNotNull( defaultTheme.getValuesMap() );
  }

  public void testCustomThemeInitialized() throws Exception {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    Theme customTheme = new Theme( "custom.id", "Custom Theme", styleSheet );
    themeManager.registerTheme( customTheme );
    ThemeCssValuesMap customValuesMap = customTheme.getValuesMap();
    assertNotNull( customValuesMap );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    ThemeManager.getInstance().reset();
    Fixture.tearDown();
  }
}
