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

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.swt.widgets.Button;


public class ThemeCssValuesMap_Test extends TestCase {

  public void testGetValues_Font() throws Exception {
    ThemeCssValuesMap map = getValuesMap();
    ConditionalValue[] fontValues = map .getValues( "Button", "font" );
    assertNotNull( fontValues );
    // expected:
    // [ [TOGGLE ] -> 11px 'Segoe UI', Tahoma, 'Lucida Sans Unicode'
    // [ [PUSH ]   -> 11px 'Segoe UI', Tahoma, 'Lucida Sans Unicode'
    // []          -> bold 12px Arial, Helvetica, sans-serif
    QxFont font1
      = QxFont.valueOf( "11px 'Segoe UI', Tahoma, 'Lucida Sans Unicode'" );
    QxFont font2 = QxFont.valueOf( "bold 12px Arial, Helvetica, sans-serif" );
    assertEquals( 3, fontValues.length );
    // 1
    assertEquals( 1, fontValues[ 0 ].constraints.length );
    assertEquals( "[TOGGLE", fontValues[ 0 ].constraints[ 0 ] );
    assertEquals( font1, fontValues[ 0 ].value );
    // 2
    assertEquals( 1, fontValues[ 1 ].constraints.length );
    assertEquals( "[PUSH", fontValues[ 1 ].constraints[ 0 ] );
    assertEquals( font1, fontValues[ 1 ].value );
    // 3
    assertEquals( 0, fontValues[ 2 ].constraints.length );
    assertEquals( font2, fontValues[ 2 ].value );
  }

  public void testGetValues_Color() throws Exception {
    ThemeCssValuesMap map = getValuesMap();
    ConditionalValue[] colorValues = map.getValues( "Button", "color" );
    assertNotNull( colorValues );
    // expected:
    // [ .special ] -> red
    // .special-blue -> blue
    // []           -> #705e42
    assertEquals( 3, colorValues.length );
    // first
    assertEquals( 1, colorValues[ 0 ].constraints.length );
    assertEquals( ".special", colorValues[ 0 ].constraints[ 0 ] );
    assertEquals( QxColor.valueOf( "red" ), colorValues[ 0 ].value );
    // last
    assertEquals( 0, colorValues[ 2 ].constraints.length );
    assertEquals( QxColor.valueOf( "#705e42" ), colorValues[ 2 ].value );
  }

  public void testGetValues_Background() throws Exception {
    ThemeCssValuesMap map = getValuesMap();
    ConditionalValue[] backgroundValues = map.getValues( "Button",
                                                         "background-color" );
    // ([TOGGLE, :pressed) -> rgb( 227, 221, 158 )
    // ([PUSH, :pressed)   -> rgb( 227, 221, 158 )
    // (.special)          -> transparent
    // ([TOGGLE)           -> #9dd0ea
    // ([PUSH)             -> #9dd0ea
    // ()                  -> #c0c0c0
    assertEquals( 6, backgroundValues.length );
    // 1
    assertEquals( 2, backgroundValues[ 0 ].constraints.length );
    assertEquals( ":pressed", backgroundValues[ 0 ].constraints[ 0 ] );
    assertEquals( "[TOGGLE", backgroundValues[ 0 ].constraints[ 1 ] );
    assertEquals( QxColor.valueOf( "227, 221, 158" ),
                  backgroundValues[ 0 ].value );
  }

  public void testGetAllValues() throws Exception {
    ThemeCssValuesMap map = getValuesMap();
    QxType[] values = map.getAllValues();
    assertNotNull( values );
    QxColor expected = QxColor.valueOf( "227, 221, 158" );
    assertTrue( Arrays.asList( values ).contains( expected ) );
    QxColor notExpected = QxColor.valueOf( "#123456" );
    assertFalse( Arrays.asList( values ).contains( notExpected ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private static ThemeCssValuesMap getValuesMap() throws IOException {
    ThemeManager manager = RWTFactory.getThemeManager();
    manager.activate();
    ThemeableWidget buttonWidget = manager.getThemeableWidget( Button.class );
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    ThemeableWidget[] themeableWidgets = new ThemeableWidget[] { buttonWidget };
    return new ThemeCssValuesMap( styleSheet, themeableWidgets );
  }
}
