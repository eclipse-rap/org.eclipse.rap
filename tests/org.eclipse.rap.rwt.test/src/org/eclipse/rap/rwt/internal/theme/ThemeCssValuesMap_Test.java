/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.rap.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.rap.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rap.rwt.internal.theme.css.StyleSheet;
import org.junit.Before;
import org.junit.Test;


public class ThemeCssValuesMap_Test {

  private Theme theme;
  private ThemeableWidget[] themeableWidgets;

  @Before
  public void setUp() {
    theme = mock(Theme.class);
    themeableWidgets = new ThemeableWidget[] {
      themeableWidget(
        cssElement( "Button" ).addProperty( "color" ).addStyle( "BORDER" ).addState( "disabled" ) ),
      themeableWidget(
        cssElement( "Label" ).addProperty( "color" ).addProperty( "font" ),
        cssElement( "Label-Separator" ).addProperty( "background-color" ) )
    };
  }

  @Test
  public void testGetValues() {
    StyleSheet styleSheet = styleSheet( "Button { color: black }" );
    ThemeCssValuesMap valuesMap = new ThemeCssValuesMap( theme, styleSheet, themeableWidgets );

    ConditionalValue[] values = valuesMap.getValues( "Button", "color" );

    assertArrayEquals( new Object[] {
      new ConditionalValue( CssColor.valueOf( "black" ) )
    }, values );
  }

  @Test
  public void testGetValues_includesKnownStatesAndStyles() {
    StyleSheet styleSheet = styleSheet( "Button { color: black }",
                                        "Button[BORDER] { color: blue }",
                                        "Button[BORDER]:disabled { color: gray }" );
    ThemeCssValuesMap valuesMap = new ThemeCssValuesMap( theme, styleSheet, themeableWidgets );

    ConditionalValue[] values = valuesMap.getValues( "Button", "color" );

    assertArrayEquals( new Object[] {
      new ConditionalValue( CssColor.valueOf( "gray" ), ":disabled", "[BORDER" ),
      new ConditionalValue( CssColor.valueOf( "blue" ), "[BORDER" ),
      new ConditionalValue( CssColor.valueOf( "black" ) )
    }, values );

  }

  @Test
  public void testGetValues_ignoresUnknownStatesAndStyles() {
    StyleSheet styleSheet = styleSheet( "Button { color: black }",
                                        "Button[UNKNOWN] { color: blue }",
                                        "Button[BORDER]:unknown { color: gray }" );
    ThemeCssValuesMap valuesMap = new ThemeCssValuesMap( theme, styleSheet, themeableWidgets );

    ConditionalValue[] values = valuesMap.getValues( "Button", "color" );

    assertArrayEquals( new Object[] {
      new ConditionalValue( CssColor.valueOf( "black" ) )
    }, values );
  }

  @Test
  public void testGetValues_includesVariants() {
    StyleSheet styleSheet = styleSheet( "Button { color: black }",
                                        "Button.special { color: red }" );
    ThemeCssValuesMap valuesMap = new ThemeCssValuesMap( theme, styleSheet, themeableWidgets );

    ConditionalValue[] values = valuesMap.getValues( "Button", "color" );

    assertArrayEquals( new Object[] {
      new ConditionalValue( CssColor.valueOf( "red" ), ".special" ),
      new ConditionalValue( CssColor.valueOf( "black" ) )
    }, values );
  }

  @Test
  public void testGetValues_resortsToWildcardProperties() {
    StyleSheet styleSheet = styleSheet( "* { color: black }",
                                        "Button[BORDER] { color: blue }" );
    ThemeCssValuesMap valuesMap = new ThemeCssValuesMap( theme, styleSheet, themeableWidgets );

    ConditionalValue[] values = valuesMap.getValues( "Button", "color" );

    assertArrayEquals( new Object[] {
      new ConditionalValue( CssColor.valueOf( "blue" ), "[BORDER" ),
      new ConditionalValue( CssColor.valueOf( "black" ) )
    }, values );
  }

  @Test
  public void testGetAllValues_includesValuesForAllElements() {
    StyleSheet styleSheet = styleSheet( "Button { color: black }",
                                        "Label { color: blue }",
                                        "Label-Separator { background-color: gray }" );
    ThemeCssValuesMap valuesMap = new ThemeCssValuesMap( theme, styleSheet, themeableWidgets );

    CssValue[] values = valuesMap.getAllValues();

    assertTrue( asList( values ).contains( CssColor.valueOf( "black" ) ) );
    assertTrue( asList( values ).contains( CssColor.valueOf( "blue" ) ) );
    assertTrue( asList( values ).contains( CssColor.valueOf( "gray" ) ) );
  }

  @Test
  public void testGetAllValues_includesValuesForAllStatesAndStyles() {
    StyleSheet styleSheet = styleSheet( "Button { color: black }",
                                        "Button[BORDER] { color: blue }",
                                        "Button[BORDER]:disabled { color: gray }" );
    ThemeCssValuesMap valuesMap = new ThemeCssValuesMap( theme, styleSheet, themeableWidgets );

    CssValue[] values = valuesMap.getAllValues();

    assertTrue( asList( values ).contains( CssColor.valueOf( "black" ) ) );
    assertTrue( asList( values ).contains( CssColor.valueOf( "blue" ) ) );
    assertTrue( asList( values ).contains( CssColor.valueOf( "gray" ) ) );
  }

  @Test
  public void testGetAllValues_includesValuesForAllProperties() {
    StyleSheet styleSheet = styleSheet( "Label { color: black; font: 12px Times }" );
    ThemeCssValuesMap valuesMap = new ThemeCssValuesMap( theme, styleSheet, themeableWidgets );

    CssValue[] values = valuesMap.getAllValues();

    assertTrue( asList( values ).contains( CssColor.valueOf( "black" ) ) );
    assertTrue( asList( values ).contains( CssFont.valueOf( "12px Times" ) ) );
  }

  @Test
  public void testGetAllValues_includesValuesForVariants() {
    StyleSheet styleSheet = styleSheet( "Button { color: black }",
                                        "Button.special { color: red }" );
    ThemeCssValuesMap valuesMap = new ThemeCssValuesMap( theme, styleSheet, themeableWidgets );

    CssValue[] values = valuesMap.getAllValues();

    assertTrue( asList( values ).contains( CssColor.valueOf( "red" ) ) );
  }

  @Test
  public void testGetAllValues_includesValuesForWildcard() {
    StyleSheet styleSheet = styleSheet( "Button { color: black }",
                                        "* { color: blue }" );
    ThemeCssValuesMap valuesMap = new ThemeCssValuesMap( theme, styleSheet, themeableWidgets );

    CssValue[] values = valuesMap.getAllValues();

    assertTrue( asList( values ).contains( CssColor.valueOf( "blue" ) ) );
  }

  private static ThemeableWidget themeableWidget( ThemeCssElement... elements ) {
    ThemeableWidget themeableWidget = new ThemeableWidget( null, null );
    themeableWidget.elements = elements;
    return themeableWidget;
  }

  private static ThemeCssElement cssElement( String name ) {
    return new ThemeCssElement( name );
  }

  private static StyleSheet styleSheet( String... lines ) {
    StringBuilder builder = new StringBuilder();
    for( String line : lines ) {
      builder.append( line );
      builder.append( '\n' );
    }
    String string = builder.toString();
    try {
      ByteArrayInputStream inputStream = new ByteArrayInputStream( string.getBytes( "UTF-8" ) );
      return CssFileReader.readStyleSheet( inputStream, "string", null );
    } catch( IOException exception ) {
      throw new RuntimeException( "Failed to parse stylesheet", exception );
    }
  }

}
