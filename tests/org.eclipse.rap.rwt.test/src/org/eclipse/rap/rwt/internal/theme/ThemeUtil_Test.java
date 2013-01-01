/*******************************************************************************
 * Copyright (c) 2007, 2013 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rap.rwt.testfixture.Fixture;
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

  private static Theme createTheme( String themeId ) throws IOException {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    return new Theme( themeId, "Custom Theme", styleSheet );
  }

}
