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

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonThemeAdapter;
import org.eclipse.swt.internal.widgets.listkit.ListThemeAdapter;
import org.eclipse.swt.internal.widgets.shellkit.ShellThemeAdapter;
import org.eclipse.swt.widgets.*;


public class ThemeManager_Test extends TestCase {

  private static final ResourceLoader LOADER
    = ThemeTestUtil.createResourceLoader( ThemeManager_Test.class );

  public void testThemeAdapters() throws Exception {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    IThemeAdapter themeAdapter;
    // Control
    themeAdapter = themeManager.getThemeAdapter( Control.class );
    assertNotNull( themeAdapter );
    assertTrue( themeAdapter instanceof IControlThemeAdapter );
    // List
    themeAdapter = themeManager.getThemeAdapter( List.class );
    assertNotNull( themeAdapter );
    assertTrue( themeAdapter instanceof ListThemeAdapter );
    // Button
    themeAdapter = themeManager.getThemeAdapter( Button.class );
    assertNotNull( themeAdapter );
    assertTrue( themeAdapter instanceof ButtonThemeAdapter );
    // Shell
    themeAdapter = themeManager.getThemeAdapter( Shell.class );
    assertNotNull( themeAdapter );
    assertTrue( themeAdapter instanceof ShellThemeAdapter );
  }

  public void testReset() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String id = manager.getDefaultThemeId();
    assertEquals( "org.eclipse.swt.theme.Default", id );
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

  public void testRegisterResources() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    manager.registerResources();
    String[] themeIds = manager.getRegisteredThemeIds();
    assertNotNull( themeIds );
    assertTrue( themeIds.length > 0 );
  }

  public void testStripTemplate() throws Exception {
    String content;
    String template;
    content = "Line 1\r\n// BEGIN TEMPLATE (bla)\r\nLine3\r\n";
    template = AppearancesUtil.stripTemplate( content );
    assertTrue( template.indexOf( "BEGIN TEMPLATE" ) == -1 );
    content = "Line 1\r// BEGIN TEMPLATE (bla)\rLine3\r";
    template = AppearancesUtil.stripTemplate( content );
    assertTrue( template.indexOf( "BEGIN TEMPLATE" ) == -1 );
    content = "Line 1\n// BEGIN TEMPLATE (bla)\nLine3\n";
    template = AppearancesUtil.stripTemplate( content );
    assertTrue( template.indexOf( "BEGIN TEMPLATE" ) == -1 );
  }

  // === TESTS FOR REGISTERING THEME FILES ===

  public void testRegisterThemeNull() throws Exception {
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    try {
      themeManager.registerTheme( null, "foo", null, null );
      fail( "Null id must throw NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      themeManager.registerTheme( "", "foo", null, null );
      fail( "Empty id must throw IlleaglArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  // == PROPERTY FILES ==

  public void testRegisterThemeFile() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String themeId = "test.valid.theme";
    String themeName = "Valid Test Theme";
    String themeFile = "resources/theme/theme-valid.properties";
    manager.registerTheme( themeId, themeName, themeFile, LOADER );
    String[] themeIds = manager.getRegisteredThemeIds();
    assertNotNull( themeIds );
    assertEquals( 2, themeIds.length );
    Theme theme = manager.getTheme( themeId );
    assertNotNull( theme );
    assertEquals( themeName, theme.getName() );
    String[] keys = theme.getKeys();
    assertNotNull( keys );
    assertTrue( theme.definesKey( "button.background" ) );
    assertTrue( theme.definesKey( "progressbar.bgimage" ) );
  }

  public void testRegisterThemeFile_EmptyKeys() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String themeId = "test.empty.theme";
    String themeName = "Empty Test Theme";
    String themeFile = "resources/theme/theme-empty.properties";
    manager.registerTheme( themeId, themeName, themeFile, LOADER );
    String[] themeIds = manager.getRegisteredThemeIds();
    assertNotNull( themeIds );
    assertEquals( 2, themeIds.length );
    Theme theme = manager.getTheme( themeId );
    assertNotNull( theme );
    assertEquals( themeName, theme.getName() );
    String[] keys = theme.getKeys();
    assertNotNull( keys );
    // theme file contains only empty values
    for( int i = 0; i < keys.length; i++ ) {
      String key = keys[ i ];
      assertFalse( theme.definesKey( key ) );
    }
  }

  public void testRegisterThemeFile_UndefinedKeys() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String themeFile = "resources/theme/theme-undefined.properties";
    try {
      manager.registerTheme( "test.theme", "Test", themeFile, LOADER );
      fail( "IAE expected for undefined key" );
    } catch( final IllegalArgumentException e ) {
      // expected
      assertTrue( e.getMessage(),
                  e.getMessage().indexOf( "Invalid key" ) != -1 );
    }
  }

  public void testRegisterThemeFile_InvalidValues() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String themeFile = "resources/theme/theme-invalid.properties";
    try {
      manager.registerTheme( "test.theme", "Test", themeFile, LOADER );
      fail( "IAE expected for invalid key" );
    } catch( final IllegalArgumentException e ) {
      // expected
      assertTrue( e.getMessage().indexOf( "Illegal" ) != -1 );
    }
  }

  public void testRegisterThemeFile_MissingImage() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String themeFile = "resources/theme/theme-missing-image.properties";
    manager.registerTheme( "test.theme", "Test", themeFile, LOADER );
    try {
      manager.registerResources();
      fail( "IAE expected for undefined key" );
    } catch( final IllegalArgumentException e ) {
      // expected
      assertTrue( e.getMessage().indexOf( "not found for theme" ) != -1 );
    }
  }

  // == CSS FILES ==

  public void testRegisterCssThemeFile() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String themeId = "TestExample";
    String themeName = "Test Example Theme";
    String themeFile = "resources/theme/TestExample.css";
    manager.registerTheme( themeId, themeName, themeFile, LOADER );
    String[] themeIds = manager.getRegisteredThemeIds();
    assertNotNull( themeIds );
    assertEquals( 2, themeIds.length );
    Theme theme = manager.getTheme( themeId );
    assertNotNull( theme );
    assertEquals( themeName, theme.getName() );
    assertNotNull( theme.getStyleSheet() );
  }

  public void testGetThemeableWidget() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    ThemeableWidget themeableWidget = manager.getThemeableWidget( Button.class );
    assertNotNull( themeableWidget );
    assertNotNull( themeableWidget.loader );
    assertEquals( Button.class, themeableWidget.widget );
    assertNotNull( themeableWidget.elements );
    assertTrue( themeableWidget.elements.length > 0 );
  }

  public void testDefaultTheme() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    Theme theme = manager.getTheme( manager.getDefaultThemeId() );
    String[] keys = theme.getKeysWithVariants();
    Arrays.sort( keys );
    for( int i = 0; i < keys.length; i++ ) {
      String key = keys[ i ];
//      System.out.println( key + ": " + theme.getValue( key ) );
    }
  }

  public void testValuesMap() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    Theme defTheme = manager.getTheme( manager.getDefaultThemeId() );
    ThemeCssValuesMap valuesMap = defTheme.getValuesMap();
    assertNotNull( valuesMap );
    
    String themeId = "custom";
    String themeFile = "resources/theme/TestExample.css";
    manager.registerTheme( themeId, "Custom Theme", themeFile, LOADER );
    Theme customTheme = manager.getTheme( themeId );
    ThemeCssValuesMap customValuesMap = customTheme.getValuesMap();
    assertNotNull( customValuesMap );
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
