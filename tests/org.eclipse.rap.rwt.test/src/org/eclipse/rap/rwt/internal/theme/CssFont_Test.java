/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class CssFont_Test {

  @Test( expected = NullPointerException.class )
  public void testValueOf_nullArgument() {
    CssFont.valueOf( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_emptyString() {
    CssFont.valueOf( "" );
  }

  @Test
  public void testParse1() {
    String def = "bold 16 \"Bitstream Vera Sans\" , Helvetica,sans-serif";
    CssFont qxFont = CssFont.valueOf( def  );
    assertTrue( qxFont.bold );
    assertFalse( qxFont.italic );
    assertEquals( 16, qxFont.size );
    assertEquals( 3, qxFont.family.length );
    assertEquals( "Bitstream Vera Sans", qxFont.family[ 0 ] );
    assertEquals( "Helvetica", qxFont.family[ 1 ] );
    assertEquals( "sans-serif", qxFont.family[ 2 ] );
  }

  @Test
  public void testParse2() {
    String def = "italic 11px 'Bitstream Vera Sans', Helvetica, sans-serif";
    CssFont qxFont = CssFont.valueOf( def  );
    assertFalse( qxFont.bold );
    assertTrue( qxFont.italic );
    assertEquals( 11, qxFont.size );
    assertEquals( 3, qxFont.family.length );
    assertEquals( "Bitstream Vera Sans", qxFont.family[ 0 ] );
    assertEquals( "Helvetica", qxFont.family[ 1 ] );
    assertEquals( "sans-serif", qxFont.family[ 2 ] );
  }

  @Test
  public void testDefaultString() {
    String input = "bold 16 \"Bitstream Vera Sans\" , 'Helvetica',sans-serif";
    String exp = "bold 16px \"Bitstream Vera Sans\", Helvetica, sans-serif";
    CssFont qxFont = CssFont.valueOf( input );
    assertEquals( exp, qxFont.toDefaultString() );
  }

}
