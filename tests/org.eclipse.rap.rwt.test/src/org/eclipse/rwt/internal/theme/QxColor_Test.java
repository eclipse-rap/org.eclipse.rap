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


public class QxColor_Test extends TestCase {

  public void testIllegalArguments() throws Exception {
    try {
      QxColor.valueOf( "#0000" );
      fail( "Exception expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxColor.valueOf( "#xyz" );
      fail( "Exception expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxColor.valueOf( "grey" );
      fail( "Exception expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void test6HexNotation() throws Exception {
    QxColor color1 = QxColor.valueOf( "#0023ff" );
    assertEquals( 0, color1.red );
    assertEquals( 35, color1.green );
    assertEquals( 255, color1.blue );
    assertFalse( color1.transparent );
    QxColor color2 = QxColor.valueOf( "#efeFEF" );
    assertEquals( 239, color2.red );
    assertEquals( 239, color2.green );
    assertEquals( 239, color2.blue );
  }

  public void test3HexNotation() throws Exception {
    QxColor color1 = QxColor.valueOf( "#03f" );
    assertEquals( 0, color1.red );
    assertEquals( 51, color1.green );
    assertEquals( 255, color1.blue );
    assertFalse( color1.transparent );
    QxColor color2 = QxColor.valueOf( "#ccc" );
    assertEquals( 204, color2.red );
    assertEquals( 204, color2.green );
    assertEquals( 204, color2.blue );
    assertEquals( QxColor.valueOf( "#ffffff"), QxColor.valueOf( "#fff" ) );
  }

  public void testNamedColors() throws Exception {
    QxColor color1 = QxColor.valueOf( "red" );
    assertEquals( 255, color1.red );
    assertEquals( 0, color1.green );
    assertEquals( 0, color1.blue );
    assertFalse( color1.transparent );
    QxColor color2 = QxColor.valueOf( "blue" );
    assertEquals( 0, color2.red );
    assertEquals( 0, color2.green );
    assertEquals( 255, color2.blue );
  }

  public void testCommaSeparatedValues() throws Exception {
    QxColor color = QxColor.valueOf( "100, 23, 42" );
    assertEquals( 100, color.red );
    assertEquals( 23, color.green );
    assertEquals( 42, color.blue );
    assertFalse( color.transparent );
  }

  public void testTransparent() throws Exception {
    assertTrue( QxColor.TRANSPARENT.transparent );
  }

  public void testShared() throws Exception {
    assertSame( QxColor.WHITE, QxColor.valueOf( "white" ) );
    assertSame( QxColor.WHITE, QxColor.valueOf( "255, 255, 255" ) );
    assertSame( QxColor.WHITE, QxColor.valueOf( "#ffffff" ) );
    assertSame( QxColor.BLACK, QxColor.valueOf( "Black" ) );
    assertSame( QxColor.BLACK, QxColor.valueOf( "0, 0, 0" ) );
    assertSame( QxColor.BLACK, QxColor.valueOf( "#000" ) );
    assertSame( QxColor.TRANSPARENT, QxColor.valueOf( "transparent" ) );
  }

  public void testToString() throws Exception {
    QxColor color = QxColor.valueOf( "100, 23, 42" );
    assertEquals( "QxColor{ 100, 23, 42 }", color.toString() );
  }

  public void testDefaultString() throws Exception {
    QxColor color = QxColor.valueOf( "100, 23, 42" );
    assertEquals( "#64172a", color.toDefaultString() );
  }
}
