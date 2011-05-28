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
    ThemeManager manager = new ThemeManager();
    assertEquals( "org.eclipse.rap.rwt.theme.Default", ThemeManager.DEFAULT_THEME_ID );
    manager.activate();
    Theme defaultTheme = manager.getTheme( ThemeManager.DEFAULT_THEME_ID );
    assertNotNull( defaultTheme );
    assertEquals( "RAP Default Theme", defaultTheme.getName() );
  }

  public void testRegisterResources() {
    ThemeManager manager = new ThemeManager();
    manager.activate();
    manager.registerResources();
    String[] themeIds = manager.getRegisteredThemeIds();
    assertNotNull( themeIds );
    assertTrue( themeIds.length > 0 );
  }

  public void testRegisterTheme() {
    ThemeManager manager = new ThemeManager();
    StyleSheet emptyStyleSheet = new StyleSheet( new StyleRule[ 0 ] );
    Theme customTheme = new Theme( "custom.id", "foo", emptyStyleSheet );
    manager.registerTheme( customTheme );
    manager.activate();
    assertTrue( manager.hasTheme( "custom.id" ) );
    List regThemeIds = Arrays.asList( manager.getRegisteredThemeIds() );
    assertTrue( regThemeIds.contains( "custom.id" ) );
    assertSame( customTheme, manager.getTheme( "custom.id" ) );
  }

  public void testRegisterThemeTwice() {
    ThemeManager manager = new ThemeManager();
    StyleSheet emptyStyleSheet = new StyleSheet( new StyleRule[ 0 ] );
    Theme theme = new Theme( "id1", "foo", emptyStyleSheet );
    manager.registerTheme( theme );
    try {
      manager.registerTheme( theme );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testGetThemeableWidget() {
    ThemeManager manager = new ThemeManager();
    manager.activate();
    ThemeableWidget themeableWidget = manager.getThemeableWidget( Button.class );
    assertNotNull( themeableWidget );
    assertNotNull( themeableWidget.loader );
    assertEquals( Button.class, themeableWidget.widget );
    assertNotNull( themeableWidget.elements );
    assertTrue( themeableWidget.elements.length > 0 );
  }

  public void testDefaultThemeInitialized() {
    ThemeManager manager = new ThemeManager();
    manager.activate();
    Theme defaultTheme = manager.getTheme( ThemeManager.DEFAULT_THEME_ID );
    assertNotNull( defaultTheme.getValuesMap() );
    assertTrue( defaultTheme.getValuesMap().getAllValues().length > 0 );
  }

  public void testCustomAndDefaultThemeInitialized() throws Exception {
    ThemeManager manager = new ThemeManager();
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    Theme customTheme = new Theme( "custom.id", "Custom Theme", styleSheet );
    manager.registerTheme( customTheme );
    manager.activate();
    Theme defaultTheme = manager.getTheme( ThemeManager.DEFAULT_THEME_ID );
    assertNotNull( defaultTheme.getValuesMap() );
    assertTrue( defaultTheme.getValuesMap().getAllValues().length > 0 );
    assertNotNull( customTheme.getValuesMap() );
    assertTrue( customTheme.getValuesMap().getAllValues().length > 0 );
  }
  
  public void testActivateAndDeactivate() {
    ThemeManager themeManager = new ThemeManager();
    
    Theme beforeActivate = getTheme( themeManager );
    themeManager.activate();
    Theme afterActivate = getTheme( themeManager );
    themeManager.deactivate();
    Theme afterDeactivate = getTheme( themeManager );
    
    assertNull( beforeActivate );
    assertNotNull( afterActivate );
    assertNull( afterDeactivate );
  }

  protected void setUp() {
    Fixture.setUp();
  }

  protected void tearDown() {
    Fixture.tearDown();
  }

  private Theme getTheme( ThemeManager themeManager ) {
    return themeManager.getTheme( ThemeManager.DEFAULT_THEME_ID );
  }
}