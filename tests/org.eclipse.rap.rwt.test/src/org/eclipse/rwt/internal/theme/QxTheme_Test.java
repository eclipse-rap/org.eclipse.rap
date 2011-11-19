/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
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


public class QxTheme_Test extends TestCase {

  public void testNoValues() {
    QxTheme theme;
    theme = new QxTheme( "my.theme.Default", "", QxTheme.APPEARANCE, null );
    String code = theme.getJsCode();
    String defStr = "qx.Theme.define( \"my.theme.DefaultAppearances\",";
    assertContains( defStr, code );
    assertTrue( code.endsWith( "} );\n" ) );
  }

  public void testTailAlreadyWritten() {
    QxTheme theme;
    theme = new QxTheme( "my.theme.Foo", "Foo Theme", QxTheme.APPEARANCE, null );
    theme.appendValues( "foo" );
    theme.getJsCode();
    theme.getJsCode(); // calling getJsCode twice is ok
    try {
      theme.appendValues( "bar" );
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
