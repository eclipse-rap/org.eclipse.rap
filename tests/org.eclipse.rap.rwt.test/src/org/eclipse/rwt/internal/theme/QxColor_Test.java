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
    QxColor color;
    color = QxColor.valueOf( "#0023ff" );
    assertEquals( 0, color.red );
    assertEquals( 35, color.green );
    assertEquals( 255, color.blue );
    color = QxColor.valueOf( "#efeFEF" );
    assertEquals( 239, color.red );
    assertEquals( 239, color.green );
    assertEquals( 239, color.blue );
  }

  public void test3HexNotation() throws Exception {
    QxColor color;
    color = QxColor.valueOf( "#03f" );
    assertEquals( 0, color.red );
    assertEquals( 51, color.green );
    assertEquals( 255, color.blue );
    color = QxColor.valueOf( "#ccc" );
    assertEquals( 204, color.red );
    assertEquals( 204, color.green );
    assertEquals( 204, color.blue );
    assertEquals( QxColor.valueOf( "#ffffff"), QxColor.valueOf( "#fff" ) );
  }

  public void testNamedColors() throws Exception {
    QxColor color;
    color = QxColor.valueOf( "white" );
    assertEquals( 255, color.red );
    assertEquals( 255, color.green );
    assertEquals( 255, color.blue );
    color = QxColor.valueOf( "Black" );
    assertEquals( 0, color.red );
    assertEquals( 0, color.green );
    assertEquals( 0, color.blue );
  }

  public void testCommaSeparatedValues() throws Exception {
    QxColor color;
    color = QxColor.valueOf( "100, 23, 42" );
    assertEquals( 100, color.red );
    assertEquals( 23, color.green );
    assertEquals( 42, color.blue );
  }

  public void testToString() throws Exception {
    QxColor color;
    color = QxColor.valueOf( "100, 23, 42" );
    assertEquals( "QxColor{ 100, 23, 42 }", color.toString() );
  }

  public void testDefaultString() throws Exception {
    QxColor color;
    color = QxColor.valueOf( "100, 23, 42" );
    assertEquals( "#64172a", color.toDefaultString() );
  }
}
