/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
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
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonThemeAdapter;
import org.eclipse.swt.internal.widgets.listkit.ListThemeAdapter;
import org.eclipse.swt.internal.widgets.shellkit.ShellThemeAdapter;
import org.eclipse.swt.widgets.*;


public class ThemeManager_Test extends TestCase {

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

  public void testRegisterThemeFile() throws Exception {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String themeId = "test.valid.theme";
    String themeName = "Valid Test Theme";
    String themeFile = "resources/theme/theme-valid.properties";
    loadThemeFile( manager, themeId, themeName, themeFile );
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
    loadThemeFile( manager, themeId, themeName, themeFile );
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
      loadThemeFile( manager, "test.theme", "Test", themeFile );
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
      loadThemeFile( manager, "test.theme", "Test", themeFile );
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
    loadThemeFile( manager, "test.theme", "Test", themeFile );
    try {
      manager.registerResources();
      fail( "IAE expected for undefined key" );
    } catch( final IllegalArgumentException e ) {
      // expected
      assertTrue( e.getMessage().indexOf( "not found for theme" ) != -1 );
    }
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    ThemeManager.getInstance().reset();
    RWTFixture.tearDown();
  }

  static void loadThemeFile( final ThemeManager manager,
                             final String themeId,
                             final String themeName,
                             final String themeFile ) throws IOException
  {
    final ClassLoader classLoader = ThemeManager_Test.class.getClassLoader();
    ResourceLoader resLoader = new ResourceLoader() {
      public InputStream getResourceAsStream( final String resourceName )
        throws IOException
      {
        return classLoader.getResourceAsStream( resourceName );
      }
    };
    manager.registerTheme( themeId, themeName, themeFile, resLoader );
  }
}
