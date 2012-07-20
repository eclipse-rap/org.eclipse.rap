/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class ThemeUtil_Test extends TestCase {
  private static final String CUSTOM_THEME_ID = "customThemeId";

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testDefaultTheme() throws Exception {
    assertNotNull( ThemeUtil.getDefaultTheme() );
    assertSame( ThemeUtil.getDefaultTheme(), ThemeUtil.getCurrentTheme() );
  }

  public void testRegisterThemeDoesNotChangeCurrentTheme() throws Exception {
    Theme theme = createTheme( CUSTOM_THEME_ID );
    ThemeTestUtil.registerTheme( theme );

    assertNotNull( ThemeUtil.getDefaultTheme() );
    assertSame( ThemeUtil.getDefaultTheme(), ThemeUtil.getCurrentTheme() );
  }

  public void testSetCurrentTheme() throws Exception {
    Theme theme = createTheme( CUSTOM_THEME_ID );
    ThemeTestUtil.registerTheme( theme );
    ThemeUtil.setCurrentThemeId( CUSTOM_THEME_ID );

    assertEquals( CUSTOM_THEME_ID, ThemeUtil.getCurrentThemeId() );
    assertSame( RWTFactory.getThemeManager().getTheme( CUSTOM_THEME_ID ),
                ThemeUtil.getCurrentTheme() );
  }

  public void testSetCurrentThemeToUnknownId() throws Exception {
    try {
      ThemeUtil.setCurrentThemeId( "unknown.theme" );
      fail( "should throw IAE for invalid theme ids" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  private static Theme createTheme( String themeId ) throws IOException {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    return new Theme( themeId, "Custom Theme", styleSheet );
  }

}
