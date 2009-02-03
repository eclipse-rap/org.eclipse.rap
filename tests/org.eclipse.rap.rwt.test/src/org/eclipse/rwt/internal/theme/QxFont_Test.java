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


public class QxFont_Test extends TestCase {

  public void testIllegalArguments() {
    try {
      QxFont.valueOf( null );
      fail( "null arguement should throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxFont.valueOf( "" );
      fail( "empty imput should throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testParse1() {
    String def = "bold 16 \"Bitstream Vera Sans\" , Helvetica,sans-serif";
    QxFont qxFont = QxFont.valueOf( def  );
    assertTrue( qxFont.bold );
    assertFalse( qxFont.italic );
    assertEquals( 16, qxFont.size );
    assertEquals( 3, qxFont.family.length );
    assertEquals( "Bitstream Vera Sans", qxFont.family[ 0 ] );
    assertEquals( "Helvetica", qxFont.family[ 1 ] );
    assertEquals( "sans-serif", qxFont.family[ 2 ] );
  }

  public void testParse2() {
    String def = "italic 11px 'Bitstream Vera Sans', Helvetica, sans-serif";
    QxFont qxFont = QxFont.valueOf( def  );
    assertFalse( qxFont.bold );
    assertTrue( qxFont.italic );
    assertEquals( 11, qxFont.size );
    assertEquals( 3, qxFont.family.length );
    assertEquals( "Bitstream Vera Sans", qxFont.family[ 0 ] );
    assertEquals( "Helvetica", qxFont.family[ 1 ] );
    assertEquals( "sans-serif", qxFont.family[ 2 ] );
  }

  public void testDefaultString() {
    String input = "bold 16 \"Bitstream Vera Sans\" , 'Helvetica',sans-serif";
    String exp = "bold 16px \"Bitstream Vera Sans\", Helvetica, sans-serif";
    QxFont qxFont = QxFont.valueOf( input );
    assertEquals( exp, qxFont.toDefaultString() );
  }
}
