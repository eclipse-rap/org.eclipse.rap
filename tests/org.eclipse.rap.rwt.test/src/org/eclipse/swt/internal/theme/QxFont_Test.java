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

package org.eclipse.swt.internal.theme;

import junit.framework.TestCase;

public class QxFont_Test extends TestCase {

  public void testParse1() throws Exception {
    String def = "bold 16 \"Bitstream Vera Sans\" , Helvetica,sans-serif";
    QxFont qxFont = new QxFont( def  );
    assertTrue( qxFont.bold );
    assertFalse( qxFont.italic );
    assertEquals( 16, qxFont.size );
    assertEquals( 3, qxFont.family.length );
    assertEquals( "Bitstream Vera Sans", qxFont.family[ 0 ] );
    assertEquals( "Helvetica", qxFont.family[ 1 ] );
    assertEquals( "sans-serif", qxFont.family[ 2 ] );
  }

  public void testParse2() throws Exception {
    String def = "italic 11px 'Bitstream Vera Sans', Helvetica, sans-serif";
    QxFont qxFont = new QxFont( def  );
    assertFalse( qxFont.bold );
    assertTrue( qxFont.italic );
    assertEquals( 11, qxFont.size );
    assertEquals( 3, qxFont.family.length );
    assertEquals( "Bitstream Vera Sans", qxFont.family[ 0 ] );
    assertEquals( "Helvetica", qxFont.family[ 1 ] );
    assertEquals( "sans-serif", qxFont.family[ 2 ] );
  }

  public void testDefaultString() throws Exception {
    String input = "bold 16 \"Bitstream Vera Sans\" , 'Helvetica',sans-serif";
    String exp = "bold 16px \"Bitstream Vera Sans\", Helvetica, sans-serif";
    QxFont qxFont = new QxFont( input );
    assertEquals( exp, qxFont.toDefaultString() );
  }
}
