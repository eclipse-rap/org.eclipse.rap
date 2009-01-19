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

import junit.framework.TestCase;

import org.eclipse.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.widgets.Button;


public class ThemeCssValuesMap_Test extends TestCase {

  public void testFont() throws Exception {
    ThemeCssValuesMap map = new ThemeCssValuesMap();
    initValuesMap( map );
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

  public void testColor() throws Exception {
    ThemeCssValuesMap map = new ThemeCssValuesMap();
    initValuesMap( map );
    ConditionalValue[] colorValues = map.getValues( "Button", "color" );
    assertNotNull( colorValues );
    // expected:
    // [ .special ] -> red
    // .special-blue -> blue
    // []           -> #705e42
    // TODO [rst] Should be only 3 when ConditionalValues optimizes correctly
    assertEquals( 4, colorValues.length );
    // 1
    assertEquals( 1, colorValues[ 0 ].constraints.length );
    assertEquals( ".special", colorValues[ 0 ].constraints[ 0 ] );
    assertEquals( QxColor.valueOf( "red" ), colorValues[ 0 ].value );
    // 4
    assertEquals( 0, colorValues[ 3 ].constraints.length );
    assertEquals( QxColor.valueOf( "#705e42" ), colorValues[ 3 ].value );
  }

  public void testBackground() throws Exception {
    ThemeCssValuesMap map = new ThemeCssValuesMap();
    initValuesMap( map );
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
    assertEquals( "[TOGGLE", backgroundValues[ 0 ].constraints[ 0 ] );
    assertEquals( ":pressed", backgroundValues[ 0 ].constraints[ 1 ] );
    assertEquals( QxColor.valueOf( "227, 221, 158" ),
                  backgroundValues[ 0 ].value );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  private static void initValuesMap( final ThemeCssValuesMap result )
    throws IOException
  {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    ThemeableWidget buttonWidget = manager.getThemeableWidget( Button.class );
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    result.initElement( buttonWidget.elements[ 0 ], styleSheet );
  }
}
