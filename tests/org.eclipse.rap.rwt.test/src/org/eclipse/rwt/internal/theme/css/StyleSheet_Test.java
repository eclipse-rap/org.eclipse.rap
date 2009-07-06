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
package org.eclipse.rwt.internal.theme.css;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.swt.graphics.Rectangle;


public class StyleSheet_Test extends TestCase {

  private static final String TEST_EXAMPLE_CSS = "TestExample.css";

  public void testGetStyleRules() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_EXAMPLE_CSS );
    StyleRule[] rules = styleSheet.getStyleRules();
    assertNotNull( rules );
    assertEquals( 13, rules.length );
  }

  public void testGetConditionalValues() throws Exception {
    // TODO [rst] Commented lines due to missing optimization
    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=282461
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_EXAMPLE_CSS );
    ConditionalValue[] values = styleSheet.getValues( "Button", "border" );
    assertEquals( 5, values.length );
    assertEquals( "[TOGGLE,[BORDER", join( values[ 0 ].constraints ) );
    assertEquals( QxBorder.create( 2, "solid", "#1695d4" ), values[ 0 ].value );
    assertEquals( "[PUSH,[BORDER", join( values[ 1 ].constraints ) );
    assertEquals( QxBorder.create( 2, "solid", "#1695d4" ), values[ 1 ].value );
    assertEquals( "[TOGGLE", join( values[ 2 ].constraints ) );
    assertEquals( QxBorder.create( 1, "solid", "#1695d4" ), values[ 2 ].value );
    assertEquals( "[PUSH", join( values[ 3 ].constraints ) );
    assertEquals( QxBorder.create( 1, "solid", "#1695d4" ), values[ 3 ].value );
    assertEquals( "[BORDER", join( values[ 4 ].constraints ) );
    assertEquals( QxBorder.create( 2, "outset", null ), values[ 4 ].value );
    values = styleSheet.getValues( "Button", "color" );
//    assertEquals( 3, values.length );
    assertEquals( ".special", join( values[ 0 ].constraints ) );
    assertEquals( QxColor.create( 255, 0, 0 ), values[ 0 ].value );
    assertEquals( ".special-blue", join( values[ 1 ].constraints ) );
    assertEquals( QxColor.create( 0, 0, 255 ), values[ 1 ].value );
//    assertEquals( QxColor.create( 112, 94, 66 ), values[ 2 ].value );
//    assertEquals( "", join( values[ 2 ].constraints ) );
    values = styleSheet.getValues( "Button", "background-color" );
//    assertEquals( 6, values.length );
    assertEquals( "[TOGGLE,:pressed", join( values[ 0 ].constraints ) );
    assertEquals( QxColor.create( 227, 221, 158 ), values[ 0 ].value );
    assertEquals( "[PUSH,:pressed", join( values[ 1 ].constraints ) );
    assertEquals( QxColor.create( 227, 221, 158 ), values[ 1 ].value );
    assertEquals( ".special", join( values[ 2 ].constraints ) );
    assertEquals( QxColor.TRANSPARENT, values[ 2 ].value );
    assertEquals( "[TOGGLE", join( values[ 3 ].constraints ) );
    assertEquals( QxColor.create( 157, 208, 234 ), values[ 3 ].value );
    assertEquals( "[PUSH", join( values[ 4 ].constraints ) );
    assertEquals( QxColor.create( 157, 208, 234 ), values[ 4 ].value );
    assertEquals( "", join( values[ 5 ].constraints ) );
    assertEquals( QxColor.create( 192, 192, 192 ), values[ 5 ].value );
  }

  public void testMergeBorderRadius() throws Exception {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( TEST_EXAMPLE_CSS );
    ConditionalValue[] values = styleSheet.getValues( "Composite", "border" );
//    assertEquals( 1, values.length );
    assertEquals( "[BORDER", join( values[ 0 ].constraints ) );
    QxBorder expected = QxBorder.create( 2, "solid", "#1695d4" );
    expected.radius = new Rectangle( 5, 10, 5, 10 );
    assertEquals( expected, values[ 0 ].value );
    values = styleSheet.getValues( "Composite", "border-radius" );
    assertEquals( "[BORDER", join( values[ 0 ].constraints ) );
    assertEquals( QxBoxDimensions.create( 5, 10, 5, 10 ), values[ 0 ].value );
  }

  private static String join( final String[] array ) {
    StringBuffer result = new StringBuffer();
    for( int i = 0; i < array.length; i++ ) {
      if( i > 0 ) {
        result.append( "," );
      }
      result.append( array[ i ] );
    }
    return result.toString();
  }
}
