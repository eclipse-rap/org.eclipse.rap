/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
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

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.*;


public class AbstractThemeAdapter_Test extends TestCase {

  public void testGetPrimaryElement() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Label label = new Label( shell, SWT.NONE );
    CustomWidget customWidget = new CustomWidget( shell, SWT.NONE );
    assertEquals( "Label", AbstractThemeAdapter.getPrimaryElement( label ) );
    assertEquals( "CustomWidget",
                  AbstractThemeAdapter.getPrimaryElement( customWidget ) );
  }

  public void testGetCssValues() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display );
    CustomWidget custom = new CustomWidget( shell, SWT.NONE );
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.initialize();
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    Theme theme = new Theme( "customId", "Custom Theme", styleSheet );
    themeManager.registerTheme( theme );
    AbstractThemeAdapter adapter = new AbstractThemeAdapter() {

      protected void configureMatcher( final WidgetMatcher matcher ) {
      }
    };
    // create theme adapter
    ThemeableWidget widget = new ThemeableWidget( CustomWidget.class, null );
    IThemeCssElement element = new ThemeCssElement( "CustomWidget" );
    widget.elements = new IThemeCssElement[] { element };
    // check default values
    Color defaultColor = adapter.getCssColor( "CustomWidget", "color", custom );
    assertNotNull( defaultColor );
    int defaultBorderWidth
      = adapter.getCssBorderWidth( "CustomWidget", "border", custom );
    // switch theme
    ThemeUtil.setCurrentThemeId( "customId" );
    // color is redefined
    Color customColor = adapter.getCssColor( "CustomWidget", "color", custom );
    assertFalse( defaultColor.equals( customColor ) );
    // borderWidth is not
    int customBorderWidth
      = adapter.getCssBorderWidth( "CustomWidget", "border", custom );
    assertTrue( defaultBorderWidth == customBorderWidth );
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
