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

import junit.framework.TestCase;

import org.eclipse.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.swt.widgets.Button;


public class Theme_Test extends TestCase {

  private static final String TEST_SYNTAX_CSS = "TestExample.css";

  public void testCreateWithNullId() throws Exception {
    try {
      new Theme( null, "Test", null );
      fail();
    } catch( NullPointerException e ) {
      assertTrue( e.getMessage().indexOf( "id" ) != -1 );
    }
  }

  public void testCreateWithNullStyleSheet() throws Exception {
    try {
      new Theme( "some.id", "Test", null );
      fail();
    } catch( NullPointerException e ) {
      assertTrue( e.getMessage().indexOf( "stylesheet" ) != -1 );
    }
  }

  public void testCreate() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_SYNTAX_CSS );
    Theme theme = new Theme( "some.id", "Test", styleSheet );
    assertEquals( "some.id", theme.getId() );
    assertEquals( "Test", theme.getName() );
  }

  public void testAddStyleSheet() throws Exception {
    String defaultCss = "Button { background-color: black; color: #aaaaaa; }\n";
    StyleSheet defaultStyleSheet = ThemeTestUtil.createStyleSheet( defaultCss );
    Theme theme = new Theme( "some.id", "Test", defaultStyleSheet );
    String addedCss = "Button.SPECIAL { color: #bbbbbb; }\n";
    StyleSheet addedStyleSheet = ThemeTestUtil.createStyleSheet( addedCss );
    theme.addStyleSheet( addedStyleSheet );
    ThemeableWidget buttonWidget = createSimpleButtonWidget();
    theme.initialize( new ThemeableWidget[] { buttonWidget } );
    ThemeCssValuesMap valuesMap = theme.getValuesMap();
    ConditionalValue[] values = valuesMap.getValues( "Button", "color" );
    assertEquals( 2, values.length );
    assertEquals( "#bbbbbb", values[ 0 ].value.toDefaultString() );
    assertEquals( "#aaaaaa", values[ 1 ].value.toDefaultString() );
  }

  public void test_Uninitialized() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_SYNTAX_CSS );
    Theme theme = new Theme( "some.id", "Test", styleSheet );
    try {
      theme.getValuesMap();
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  public void testInitialize() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_SYNTAX_CSS );
    Theme theme = new Theme( "some.id", "Test", styleSheet );
    theme.initialize( new ThemeableWidget[ 0 ] );
    assertNotNull( theme.getValuesMap() );
  }

  public void testInitializeTwice() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_SYNTAX_CSS );
    Theme theme = new Theme( "some.id", "Test", styleSheet );
    theme.initialize( new ThemeableWidget[ 0 ] );
    try {
      theme.initialize( new ThemeableWidget[ 0 ] );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  public void testGetJsIdForDefaultTheme() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_SYNTAX_CSS );
    String defaultThemeId = ThemeManager.DEFAULT_THEME_ID;
    Theme defaultTheme = new Theme( defaultThemeId, "Default", styleSheet );
    assertEquals( "org.eclipse.swt.theme.Default", defaultTheme.getJsId() );
  }

  public void testGetJsId() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_SYNTAX_CSS );
    Theme theme1 = new Theme( "custom.id1", "Custom 1", styleSheet );
    assertTrue( theme1.getJsId().startsWith( "org.eclipse.swt.theme.Custom_" ) );
    Theme theme2 = new Theme( "custom.id2", "Custom 2", styleSheet );
    assertFalse( theme2.getJsId().equals( theme1.getJsId() ) );
  }

  private static ThemeableWidget createSimpleButtonWidget() {
    ThemeableWidget buttonWidget = new ThemeableWidget( Button.class, null );
    ThemeCssElement buttonElement = new ThemeCssElement( "Button" );
    buttonElement.addProperty( "color" );
    buttonElement.addProperty( "background-color" );
    buttonWidget.elements = new IThemeCssElement[] { buttonElement };
    return buttonWidget;
  }
}
