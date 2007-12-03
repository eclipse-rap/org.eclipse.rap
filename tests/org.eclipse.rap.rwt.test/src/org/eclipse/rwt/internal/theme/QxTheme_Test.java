/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
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
  
  public void testXColor() throws Exception {
    QxTheme theme = new QxTheme( "my.theme.Foo", "Foo Color Theme", QxTheme.COLOR );
    theme.appendColor( "default.background", new QxColor( "#ffce67" ) );
    theme.appendColor( "default.foreground", new QxColor( "#ffce67" ) );
    theme.appendColor( "another.background", new QxColor( "#ffce67" ) );
    String code = theme.getJsCode();
//    System.out.println( code );
    String defStr = "qx.Theme.define( \"my.theme.FooColors\",";
    assertContains( defStr, code );
    assertContains( "title : \"Foo Color Theme\"", code );
    assertContains( "[ 255, 206, 103 ]", code );
    assertTrue( code.endsWith( "} );\n" ) );
  }
  
  public void testDefaultBorder() throws Exception {
    QxTheme theme = new QxTheme( "my.theme.Default", "", QxTheme.BORDER );
    theme.appendBorder( "default.border",
                        new QxBorder( "1 solid #121212" ) );
    theme.appendBorder( "another.border",
                        new QxBorder( "2 outset #ffffff" ) );
    String code = theme.getJsCode();
    String defStr = "qx.Theme.define( \"my.theme.DefaultBorders\",";
    assertContains( defStr, code );
    assertTrue( code.endsWith( "} );\n" ) );
  }
  
  public void testTailAlreadyWritten() throws Exception {
    QxTheme theme = new QxTheme( "my.theme.Foo", "Foo Theme", QxTheme.COLOR );
    theme.appendColor( "foo", new QxColor( "#f00" ) );
    theme.getJsCode();
    theme.getJsCode(); // calling getJsCode twice is ok
    try {
      theme.appendColor( "bar", new QxColor( "#ba1" ) );
      fail( "ISE expected" );
    } catch( final IllegalStateException e ) {
      // expected
    }
  }
  
  protected static void assertContains( final String expected, final String actual ) {
    assertTrue( actual.indexOf( expected ) != -1 );
  }
}
