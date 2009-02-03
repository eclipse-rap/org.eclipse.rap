/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
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

  public void testAppendUri() {
    QxTheme theme = new QxTheme( "theme.Foo", "Foo Widgets", QxTheme.WIDGET );
    theme.appendUri( "some.uri" );
    String code = theme.getJsCode();
    String defStr = "qx.Theme.define( \"theme.FooWidgets\",";
    assertContains( defStr, code );
    assertContains( "title : \"Foo Widgets\"", code );
    assertContains( "uri : \"some.uri\"", code );
    assertTrue( code.endsWith( "} );\n" ) );
  }

  public void testNoValues() {
    QxTheme theme = new QxTheme( "my.theme.Default", "", QxTheme.APPEARANCE );
    String code = theme.getJsCode();
    String defStr = "qx.Theme.define( \"my.theme.DefaultAppearances\",";
    assertContains( defStr, code );
    assertTrue( code.endsWith( "} );\n" ) );
  }

  public void testTailAlreadyWritten() {
    QxTheme theme = new QxTheme( "my.theme.Foo", "Foo Theme", QxTheme.COLOR );
    theme.appendUri( "foo" );
    theme.getJsCode();
    theme.getJsCode(); // calling getJsCode twice is ok
    try {
      theme.appendUri( "bar" );
      fail( "ISE expected" );
    } catch( final IllegalStateException e ) {
      // expected
    }
  }

  protected static void assertContains( final String expected,
                                        final String actual )
  {
    String msg = "String '" + expected + "' not contained in '" + actual + "'";
    assertTrue( msg, actual.indexOf( expected ) != -1 );
  }
}
