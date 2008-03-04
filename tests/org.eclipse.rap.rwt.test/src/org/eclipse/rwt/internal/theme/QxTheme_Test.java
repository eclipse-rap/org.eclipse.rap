/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

public class QxTheme_Test extends TestCase {

  public void testColor() throws Exception {
    QxTheme theme = new QxTheme( "my.theme.Foo", "Foo Color Theme", QxTheme.COLOR );
    theme.appendColor( "default.background", QxColor.valueOf( "#ffce67" ) );
    theme.appendColor( "default.foreground", QxColor.valueOf( "#ffce67" ) );
    theme.appendColor( "another.background", QxColor.valueOf( "#ffce67" ) );
    String code = theme.getJsCode();
    String defStr = "qx.Theme.define( \"my.theme.FooColors\",";
    assertContains( defStr, code );
    assertContains( "title : \"Foo Color Theme\"", code );
    assertContains( "[ 255, 206, 103 ]", code );
    assertTrue( code.endsWith( "} );\n" ) );
  }

  public void testDefaultBorder() throws Exception {
    QxTheme theme = new QxTheme( "my.theme.Default", "", QxTheme.BORDER );
    theme.appendBorder( "default.border",
                        QxBorder.valueOf( "1px solid #121212" ) );
    theme.appendBorder( "another.border",
                        QxBorder.valueOf( "2px outset #ffffff" ) );
    theme.appendBorder( "no.color.border",
                        QxBorder.valueOf( "2px outset" ) );
    theme.appendBorder( "one.border",
                        QxBorder.valueOf( "1px" ) );
    theme.appendBorder( "no.color.border",
                        QxBorder.valueOf( "2px outset" ) );
    theme.appendBorder( "zero.border",
                        QxBorder.NONE );
    String code = theme.getJsCode();
    String defStr = "qx.Theme.define( \"my.theme.DefaultBorders\",";
    assertContains( defStr, code );
    assertTrue( code.indexOf( "null" ) == -1 );
    assertTrue( code.endsWith( "} );\n" ) );
  }

  public void testNoValues() throws Exception {
    QxTheme theme = new QxTheme( "my.theme.Default", "", QxTheme.APPEARANCE );
    String code = theme.getJsCode();
    String defStr = "qx.Theme.define( \"my.theme.DefaultAppearances\",";
    assertContains( defStr, code );
    assertTrue( code.endsWith( "} );\n" ) );
  }

  public void testTailAlreadyWritten() throws Exception {
    QxTheme theme = new QxTheme( "my.theme.Foo", "Foo Theme", QxTheme.COLOR );
    theme.appendColor( "foo", QxColor.valueOf( "#f00" ) );
    theme.getJsCode();
    theme.getJsCode(); // calling getJsCode twice is ok
    try {
      theme.appendColor( "bar", QxColor.valueOf( "#ba1" ) );
      fail( "ISE expected" );
    } catch( final IllegalStateException e ) {
      // expected
    }
  }

  protected static void assertContains( final String expected, final String actual ) {
    String msg = "String '" + expected + "' not contained in '" + actual + "'";
    assertTrue( msg, actual.indexOf( expected ) != -1 );
  }
}
