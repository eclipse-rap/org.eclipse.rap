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

import org.eclipse.swt.internal.theme.QxColor;

public class QxColor_Test extends TestCase {
  
  public void test6HexNotation() throws Exception {
    QxColor color;
    color = new QxColor( "#0023ff" );
    assertEquals( 0, color.red );
    assertEquals( 35, color.green );
    assertEquals( 255, color.blue );
    color = new QxColor( "#efeFEF" );
    assertEquals( 239, color.red );
    assertEquals( 239, color.green );
    assertEquals( 239, color.blue );
  }
  
  public void test3HexNotation() throws Exception {
    QxColor color;
    color = new QxColor( "#03f" );
    assertEquals( 0, color.red );
    assertEquals( 51, color.green );
    assertEquals( 255, color.blue );
    color = new QxColor( "#ccc" );
    assertEquals( 204, color.red );
    assertEquals( 204, color.green );
    assertEquals( 204, color.blue );
    assertEquals( new QxColor( "#ffffff"), new QxColor( "#fff" ) );
  }
  
  public void testIllegalHexNotation() throws Exception {
    try {
      new QxColor( "#0000" );
      fail( "Exception expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      new QxColor( "#xyz" );
      fail( "Exception expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testNamedColors() throws Exception {
    QxColor color;
    color = new QxColor( "white" );
    assertEquals( 255, color.red );
    assertEquals( 255, color.green );
    assertEquals( 255, color.blue );
    assertEquals( "white", color.name );
    color = new QxColor( "Black" );
    assertEquals( 0, color.red );
    assertEquals( 0, color.green );
    assertEquals( 0, color.blue );
    assertEquals( "black", color.name );
  }
  
  public void testCommaSeparatedValues() throws Exception {
    QxColor color;
    color = new QxColor( "100, 23, 42" );
    assertEquals( 100, color.red );
    assertEquals( 23, color.green );
    assertEquals( 42, color.blue );
    assertEquals( "#64172a", color.name );
  }
  
  public void testToString() throws Exception {
    QxColor color;
    color = new QxColor( "100, 23, 42" );
    assertEquals( "QxColor {100, 23, 42}", color.toString() );
  }
  
  public void testInvalidNames() throws Exception {
    try {
      new QxColor( "grey" );
      fail( "Exception expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
}
