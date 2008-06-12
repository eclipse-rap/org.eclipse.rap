/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
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


public class QxBoxDimensions_Test extends TestCase {

  public void testIllegalArguments() throws Exception {
    try {
      QxBoxDimensions.valueOf( ( String )null );
      fail( "NPE expected" );
    } catch( final NullPointerException e ) {
      // expected
    }
    try {
      QxBoxDimensions.valueOf( "" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    try {
      QxBoxDimensions.valueOf( " 23px" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    try {
      QxBoxDimensions.valueOf( "23em" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testZero() throws Exception {
    assertSame( QxBoxDimensions.ZERO, QxBoxDimensions.valueOf( "0" ) );
    assertSame( QxBoxDimensions.ZERO, QxBoxDimensions.valueOf( "0 0" ) );
    assertEquals( 0, QxBoxDimensions.ZERO.top );
    assertEquals( 0, QxBoxDimensions.ZERO.right );
    assertEquals( 0, QxBoxDimensions.ZERO.bottom );
    assertEquals( 0, QxBoxDimensions.ZERO.left );
  }

  public void test1Value() throws Exception {
    QxBoxDimensions dim23px = QxBoxDimensions.valueOf( "23px" );
    assertEquals( 23, dim23px.top );
    assertEquals( 23, dim23px.right );
    assertEquals( 23, dim23px.bottom );
    assertEquals( 23, dim23px.left );
    QxBoxDimensions dimNeg1 = QxBoxDimensions.valueOf( "-1" );
    assertEquals( -1, dimNeg1.top );
    assertEquals( -1, dimNeg1.right );
    assertEquals( -1, dimNeg1.bottom );
    assertEquals( -1, dimNeg1.left );
  }

  public void test2Values() throws Exception {
    QxBoxDimensions dimensions = QxBoxDimensions.valueOf( "0 2" );
    assertEquals( 0, dimensions.top );
    assertEquals( 2, dimensions.right );
    assertEquals( 0, dimensions.bottom );
    assertEquals( 2, dimensions.left );
  }

  public void test3Values() throws Exception {
    QxBoxDimensions dimensions = QxBoxDimensions.valueOf( "1 2 3px" );
    assertEquals( 1, dimensions.top );
    assertEquals( 2, dimensions.right );
    assertEquals( 3, dimensions.bottom );
    assertEquals( 2, dimensions.left );
  }

  public void test4Values() throws Exception {
    QxBoxDimensions dimensions = QxBoxDimensions.valueOf( "0px 1px 2px 3px" );
    assertEquals( 0, dimensions.top );
    assertEquals( 1, dimensions.right );
    assertEquals( 2, dimensions.bottom );
    assertEquals( 3, dimensions.left );
  }

  public void testDefaultString() throws Exception {
    QxBoxDimensions dim0123 = QxBoxDimensions.create( 0, 1, 2, 3 );
    assertEquals( "0px 1px 2px 3px", dim0123.toDefaultString() );
    QxBoxDimensions dim123 = QxBoxDimensions.create( 1, 2, 3, 2 );
    assertEquals( "1px 2px 3px", dim123.toDefaultString() );
    QxBoxDimensions dim01 = QxBoxDimensions.create( 0, 1, 0, 1 );
    assertEquals( "0px 1px", dim01.toDefaultString() );
    QxBoxDimensions dim1 = QxBoxDimensions.create( 1, 1, 1, 1 );
    assertEquals( "1px", dim1.toDefaultString() );
  }
}
