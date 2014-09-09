/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.eclipse.rap.rwt.internal.theme.CssBorder;
import org.eclipse.rap.rwt.internal.theme.CssColor;
import org.eclipse.rap.rwt.internal.theme.ThemeTestUtil;
import org.junit.Test;


public class StyleSheet_Test {

  private static final String TEST_EXAMPLE_CSS = "TestExample.css";

  @Test
  public void testGetStyleRules() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_EXAMPLE_CSS );
    StyleRule[] rules = styleSheet.getStyleRules();
    assertNotNull( rules );
    assertEquals( 13, rules.length );
  }

  @Test
  public void testAllConstraintsAreSorted() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_EXAMPLE_CSS );
    ConditionalValue[] values = styleSheet.getValues( "Button", "border" );
    for( int i = 0; i < values.length; i++ ) {
      String[] constraints = values[ i ].constraints;
      String[] sortedConstraints = constraints;
      Arrays.sort( sortedConstraints );
      assertEquals( join( sortedConstraints ), join( constraints ) );
    }
  }

  @Test
  public void testGetConditionalValues_Optimized() throws Exception {
    // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=282461
    String css = "* { color: red }\n" + "Button { color: blue }\n";
    StyleSheet styleSheet = ThemeTestUtil.createStyleSheet( css );
    ConditionalValue[] values = styleSheet.getValues( "Button", "color" );
    assertEquals( 1, values.length );
    assertEquals( "", join( values[ 0 ].constraints ) );
    assertEquals( CssColor.create( 0, 0, 255 ), values[ 0 ].value );
  }

  @Test
  public void testGetConditionalValues_SortedAndOptimized() throws Exception {
    String css =   "Button:hover[BORDER].special { color: red }\n"
                 + "Button[BORDER]:hover.special { color: green }\n"
                 + "Button.special[BORDER]:hover { color: blue }\n";
    StyleSheet styleSheet = ThemeTestUtil.createStyleSheet( css );
    ConditionalValue[] values = styleSheet.getValues( "Button", "color" );
    assertEquals( 1, values.length );
    assertEquals( ".special,:hover,[BORDER", join( values[ 0 ].constraints ) );
    assertEquals( CssColor.create( 0, 0, 255 ), values[ 0 ].value );
  }

  @Test
  public void testGetConditionalValues() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_EXAMPLE_CSS );
    ConditionalValue[] values = styleSheet.getValues( "Button", "border" );
    CssColor color = CssColor.valueOf( "#1695d4" );
    assertEquals( 5, values.length );
    assertEquals( "[BORDER,[TOGGLE", join( values[ 0 ].constraints ) );
    assertEquals( CssBorder.create( 2, "solid", color ), values[ 0 ].value );
    assertEquals( "[BORDER,[PUSH", join( values[ 1 ].constraints ) );
    assertEquals( CssBorder.create( 2, "solid", color ), values[ 1 ].value );
    assertEquals( "[TOGGLE", join( values[ 2 ].constraints ) );
    assertEquals( CssBorder.create( 1, "solid", color ), values[ 2 ].value );
    assertEquals( "[PUSH", join( values[ 3 ].constraints ) );
    assertEquals( CssBorder.create( 1, "solid", color ), values[ 3 ].value );
    assertEquals( "[BORDER", join( values[ 4 ].constraints ) );
    assertEquals( CssBorder.create( 2, "outset", null ), values[ 4 ].value );
    values = styleSheet.getValues( "Button", "color" );
    assertEquals( 3, values.length );
    assertEquals( ".special", join( values[ 0 ].constraints ) );
    assertEquals( CssColor.create( 255, 0, 0 ), values[ 0 ].value );
    assertEquals( ".special-blue", join( values[ 1 ].constraints ) );
    assertEquals( CssColor.create( 0, 0, 255 ), values[ 1 ].value );
    assertEquals( CssColor.create( 112, 94, 66 ), values[ 2 ].value );
    assertEquals( "", join( values[ 2 ].constraints ) );
    values = styleSheet.getValues( "Button", "background-color" );
    assertEquals( 6, values.length );
    assertEquals( ":pressed,[TOGGLE", join( values[ 0 ].constraints ) );
    assertEquals( CssColor.create( 227, 221, 158 ), values[ 0 ].value );
    assertEquals( ":pressed,[PUSH", join( values[ 1 ].constraints ) );
    assertEquals( CssColor.create( 227, 221, 158 ), values[ 1 ].value );
    assertEquals( ".special", join( values[ 2 ].constraints ) );
    assertEquals( CssColor.TRANSPARENT, values[ 2 ].value );
    assertEquals( "[TOGGLE", join( values[ 3 ].constraints ) );
    assertEquals( CssColor.create( 157, 208, 234 ), values[ 3 ].value );
    assertEquals( "[PUSH", join( values[ 4 ].constraints ) );
    assertEquals( CssColor.create( 157, 208, 234 ), values[ 4 ].value );
    assertEquals( "", join( values[ 5 ].constraints ) );
    assertEquals( CssColor.create( 192, 192, 192 ), values[ 5 ].value );
  }

  private static String join( String[] array ) {
    StringBuilder result = new StringBuilder();
    for( int i = 0; i < array.length; i++ ) {
      if( i > 0 ) {
        result.append( "," );
      }
      result.append( array[ i ] );
    }
    return result.toString();
  }

}
