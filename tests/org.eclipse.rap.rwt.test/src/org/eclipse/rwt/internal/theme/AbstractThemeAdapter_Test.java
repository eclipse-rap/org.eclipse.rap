/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.awt.Button;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.*;


public class AbstractThemeAdapter_Test extends TestCase {

  private static final ResourceLoader LOADER
    = ThemeTestUtil.createResourceLoader( AbstractThemeAdapter_Test.class );

  public void testWithElement() throws Exception {
    AbstractThemeAdapter adapter = new AbstractThemeAdapter() {

      protected void configureMatcher( final WidgetMatcher matcher ) {
      }
    };
    ThemeableWidget widget = new ThemeableWidget( Composite.class, null );
    IThemeCssElement element = new ThemeCssElement( "MyWidget" );
    widget.elements = new IThemeCssElement[] { element  };
    adapter.init( widget );
    assertEquals( "MyWidget", adapter.getPrimaryElement() );
  }

  public void testDerivedElementName() throws Exception {
    AbstractThemeAdapter adapter = new AbstractThemeAdapter() {

      protected void configureMatcher( final WidgetMatcher matcher ) {
      }
    };
    ThemeableWidget widget = new ThemeableWidget( Button.class, null );
    adapter.init( widget );
    assertEquals( "Button", adapter.getPrimaryElement() );
  }

  public void testGetCssValues() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display );
    Canvas canvas = new Canvas( shell, SWT.NONE );
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    // register custom theme
    String fileName = "resources/theme/TestExample.css";
    themeManager.registerTheme( "customId", "Custom Theme", fileName, LOADER );
    AbstractThemeAdapter adapter = new AbstractThemeAdapter() {

      protected void configureMatcher( final WidgetMatcher matcher ) {
      }
    };
    // create theme adapter
    ThemeableWidget widget = new ThemeableWidget( Canvas.class, null );
    IThemeCssElement element = new ThemeCssElement( "MyWidget" );
    widget.elements = new IThemeCssElement[] { element };
    adapter.init( widget );
    // check default values
    Color defaultColor = adapter.getCssColor( "MyWidget", "color", canvas );
    assertNotNull( defaultColor );
    int defaultBorderWidth
      = adapter.getCssBorderWidth( "MyWidget", "border", canvas );
    // switch theme
    ThemeUtil.setCurrentThemeId( "customId" );
    // color is redefined
    Color customColor = adapter.getCssColor( "MyWidget", "color", canvas );
    assertFalse( defaultColor.equals( customColor ) );
    // borderWidth is not
    int customBorderWidth
      = adapter.getCssBorderWidth( "MyWidget", "border", canvas );
    assertTrue( defaultBorderWidth == customBorderWidth );
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
