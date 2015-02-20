/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.testfixture.internal.TestRequest.DEFAULT_SERVLET_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ThemeUtil_Test {
  private static final String CUSTOM_THEME_ID = "customThemeId";
  private ThemeManager themeManager;

  @Before
  public void setUp() {
    Fixture.setUp();
    themeManager = getApplicationContext().getThemeManager();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRegisterThemeDoesNotChangeCurrentTheme() throws Exception {
    Theme theme = createTheme( CUSTOM_THEME_ID );

    ThemeTestUtil.registerTheme( theme );

    Theme defaultTheme = themeManager.getTheme( RWT.DEFAULT_THEME_ID );
    assertNotNull( defaultTheme );
    assertSame( defaultTheme, ThemeUtil.getCurrentTheme() );
  }

  @Test
  public void testSetCurrentTheme() throws Exception {
    Theme theme = createTheme( CUSTOM_THEME_ID );
    ThemeTestUtil.registerTheme( theme );

    ThemeUtil.setCurrentThemeId( ContextProvider.getUISession(), CUSTOM_THEME_ID );

    assertEquals( CUSTOM_THEME_ID, ThemeUtil.getCurrentThemeId() );
    assertSame( themeManager.getTheme( CUSTOM_THEME_ID ), ThemeUtil.getCurrentTheme() );
  }

  @Test
  public void testSetCurrentThemeToUnknownId() {
    ThemeUtil.setCurrentThemeId( ContextProvider.getUISession(), "unknown.theme" );

    assertEquals( "unknown.theme", ThemeUtil.getCurrentThemeId() );
  }

  @Test
  public void testGetThemeIdFor_withDefaultTheme() {
    registerEntryPoint( null );

    assertEquals( RWT.DEFAULT_THEME_ID, ThemeUtil.getThemeIdFor( DEFAULT_SERVLET_PATH ) );
  }

  @Test
  public void testGetThemeIdFor_withCustomTheme() throws IOException {
    ThemeTestUtil.registerTheme( createTheme( CUSTOM_THEME_ID ) );
    HashMap<String, String> properties = new HashMap<String,String>();
    properties.put( WebClient.THEME_ID, CUSTOM_THEME_ID );
    registerEntryPoint( properties );

    assertEquals( CUSTOM_THEME_ID, ThemeUtil.getThemeIdFor( DEFAULT_SERVLET_PATH ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetThemeIdFor_failsWithNonExistingThemeId() {
    HashMap<String, String> properties = new HashMap<String,String>();
    properties.put( WebClient.THEME_ID, "does.not.exist" );
    registerEntryPoint( properties );

    ThemeUtil.getThemeIdFor( DEFAULT_SERVLET_PATH );
  }

  @Test
  public void testGetCssValue_fromDefaultTheme() {
    String themeId = RWT.DEFAULT_THEME_ID;
    SimpleSelector selector = SimpleSelector.DEFAULT;

    CssType cssValue = ThemeUtil.getCssValue( themeId, "Button", "color", selector, null );

    assertEquals( "#4a4a4a", cssValue.toDefaultString() );
  }

  @Test
  public void testGetCssValue_fromCustomTheme() throws IOException {
    String themeId = CUSTOM_THEME_ID;
    SimpleSelector selector = SimpleSelector.DEFAULT;
    ThemeTestUtil.registerTheme( createTheme( themeId ) );

    CssType cssValue = ThemeUtil.getCssValue( themeId, "Button", "color", selector, null );

    assertEquals( "#705e42", cssValue.toDefaultString() );
  }

  private void registerEntryPoint( HashMap<String, String> properties ) {
    EntryPointManager entryPointManager = getApplicationContext().getEntryPointManager();
    EntryPointFactory factory = mock( EntryPointFactory.class );
    entryPointManager.register( DEFAULT_SERVLET_PATH, factory, properties );
  }

  private static Theme createTheme( String themeId ) throws IOException {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    return new Theme( themeId, "Custom Theme", styleSheet );
  }

}
