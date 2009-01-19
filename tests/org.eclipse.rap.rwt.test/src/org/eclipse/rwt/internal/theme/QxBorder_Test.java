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

public class QxBorder_Test extends TestCase {

  public void testIllegalArguments() {
    try {
      QxBorder.valueOf( null );
      fail( "null arguement should throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxBorder.valueOf( "" );
      fail( "empty imput should throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxBorder.valueOf( " " );
      fail( "empty imput should throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxBorder.valueOf( "-1" );
      fail( "negative width should throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxBorder.valueOf( "1 solid red 2" );
      fail( "one too many argument should throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testNone() {
    assertSame( QxBorder.NONE, QxBorder.valueOf( "none" ) );
    assertEquals( 0, QxBorder.NONE.width );
    assertNull( QxBorder.NONE.style );
    assertNull( QxBorder.NONE.color );
  }

  public void testDefaults() {
    QxBorder border1 = QxBorder.valueOf( "1px" );
    assertEquals( 1, border1.width );
    assertEquals( "solid", border1.style );
    assertNull( border1.color );
    QxBorder border2 = QxBorder.valueOf( "2" );
    assertEquals( 2, border2.width );
    assertEquals( "solid", border2.style );
    QxBorder black = QxBorder.valueOf( "black" );
    assertEquals( 1, black.width );
    assertEquals( "solid", black.style );
    assertEquals( "black", black.color );
    QxBorder dashedRed = QxBorder.valueOf( "1 dashed red" );
    assertEquals( 1, dashedRed.width );
    assertEquals( "dashed", dashedRed.style );
    assertEquals( "red", dashedRed.color );
  }

//  public void testQxColors() {
//    assertNull( QxBorder.NONE.getQxColors() );
//    // thin inset
//    QxBorder thinInset = QxBorder.valueOf( "1 inset" );
//    assertEquals( "solid", thinInset.getQxStyle() );
//    assertEquals( "[ \"rwt-shadow-color\", \"rwt-highlight-color\","
//                      + " \"rwt-highlight-color\", \"rwt-shadow-color\" ]",
//                  thinInset.getQxColors() );
//    // thin outset
//    QxBorder thinOutset = QxBorder.valueOf( "1px outset" );
//    assertEquals( "solid", thinOutset.getQxStyle() );
//    assertEquals( "[ \"rwt-highlight-color\", \"rwt-shadow-color\","
//                      + " \"rwt-shadow-color\", \"rwt-highlight-color\" ]",
//                  thinOutset.getQxColors() );
//    // inset
//    QxBorder inset = QxBorder.valueOf( "2px inset" );
//    assertEquals( "solid", inset.getQxStyle() );
//    assertEquals( "[ \"rwt-shadow-color\", \"rwt-highlight-color\","
//                      + " \"rwt-highlight-color\", \"rwt-shadow-color\" ]",
//                  inset.getQxColors() );
//    assertEquals( "[ \"rwt-darkshadow-color\", \"rwt-lightshadow-color\","
//                      + " \"rwt-lightshadow-color\", \"rwt-darkshadow-color\" ]",
//                  inset.getQxInnerColors() );
//    // outset
//    QxBorder outset = QxBorder.valueOf( "2 outset" );
//    assertEquals( "solid", outset.getQxStyle() );
//    assertEquals( "[ \"rwt-lightshadow-color\", \"rwt-darkshadow-color\","
//                      + " \"rwt-darkshadow-color\", \"rwt-lightshadow-color\" ]",
//                  outset.getQxColors() );
//    assertEquals( "[ \"rwt-highlight-color\", \"rwt-shadow-color\","
//                      + " \"rwt-shadow-color\", \"rwt-highlight-color\" ]",
//                  outset.getQxInnerColors() );
//    // groove
//    QxBorder groove = QxBorder.valueOf( "2 groove" );
//    assertEquals( "solid", groove.getQxStyle() );
//    assertEquals( "[ \"rwt-shadow-color\", \"rwt-highlight-color\","
//                      + " \"rwt-highlight-color\", \"rwt-shadow-color\" ]",
//                  groove.getQxColors() );
//    assertEquals( "[ \"rwt-highlight-color\", \"rwt-shadow-color\","
//                      + " \"rwt-shadow-color\", \"rwt-highlight-color\" ]",
//                  groove.getQxInnerColors() );
//    // ridge
//    QxBorder ridge = QxBorder.valueOf( "2 ridge" );
//    assertEquals( "solid", ridge.getQxStyle() );
//    assertEquals( "[ \"rwt-highlight-color\", \"rwt-shadow-color\","
//                      + " \"rwt-shadow-color\", \"rwt-highlight-color\" ]",
//                  ridge.getQxColors() );
//    assertEquals( "[ \"rwt-shadow-color\", \"rwt-highlight-color\","
//                      + " \"rwt-highlight-color\", \"rwt-shadow-color\" ]",
//                  ridge.getQxInnerColors() );
//  }

  public void testDefaultString() {
    QxBorder red = QxBorder.valueOf( "red" );
    assertEquals( "1px solid red", red.toDefaultString() );
    QxBorder border1 = QxBorder.valueOf( "3 solid red" );
    assertEquals( "3px solid red", border1.toDefaultString() );
    QxBorder border2 = QxBorder.valueOf( "1 dashed #ff0000" );
    assertEquals( "1px dashed #ff0000", border2.toDefaultString() );
  }

  public void testEquals() {
    QxBorder border1 = QxBorder.create( 0, null, null );
    QxBorder border2 = QxBorder.create( 1, null, null );
    assertNotNull( border1 );
    assertNull( border1.style );
    assertNull( border1.color );
    assertFalse( border1.equals( border2 ) );
  }
}
