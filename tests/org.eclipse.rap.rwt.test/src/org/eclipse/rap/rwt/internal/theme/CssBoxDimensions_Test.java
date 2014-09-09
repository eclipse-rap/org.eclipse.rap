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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class CssBoxDimensions_Test {

  @Test( expected = NullPointerException.class )
  public void testValueOf_nullArgument() {
    CssBoxDimensions.valueOf( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_emptyString() {
    CssBoxDimensions.valueOf( "" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_startWithSpace() {
    CssBoxDimensions.valueOf( " 23px" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_unsupportedUnit() {
    CssBoxDimensions.valueOf( "23em" );
  }

  @Test
  public void testZero() {
    assertSame( CssBoxDimensions.ZERO, CssBoxDimensions.valueOf( "0" ) );
    assertSame( CssBoxDimensions.ZERO, CssBoxDimensions.valueOf( "0 0" ) );
    assertEquals( 0, CssBoxDimensions.ZERO.top );
    assertEquals( 0, CssBoxDimensions.ZERO.right );
    assertEquals( 0, CssBoxDimensions.ZERO.bottom );
    assertEquals( 0, CssBoxDimensions.ZERO.left );
  }

  @Test
  public void test1Value() {
    CssBoxDimensions dim23px = CssBoxDimensions.valueOf( "23px" );
    assertEquals( 23, dim23px.top );
    assertEquals( 23, dim23px.right );
    assertEquals( 23, dim23px.bottom );
    assertEquals( 23, dim23px.left );
    CssBoxDimensions dimNeg1 = CssBoxDimensions.valueOf( "-1" );
    assertEquals( -1, dimNeg1.top );
    assertEquals( -1, dimNeg1.right );
    assertEquals( -1, dimNeg1.bottom );
    assertEquals( -1, dimNeg1.left );
  }

  @Test
  public void test2Values() {
    CssBoxDimensions dimensions = CssBoxDimensions.valueOf( "0 2" );
    assertEquals( 0, dimensions.top );
    assertEquals( 2, dimensions.right );
    assertEquals( 0, dimensions.bottom );
    assertEquals( 2, dimensions.left );
  }

  @Test
  public void test3Values() {
    CssBoxDimensions dimensions = CssBoxDimensions.valueOf( "1 2 3px" );
    assertEquals( 1, dimensions.top );
    assertEquals( 2, dimensions.right );
    assertEquals( 3, dimensions.bottom );
    assertEquals( 2, dimensions.left );
  }

  @Test
  public void test4Values() {
    CssBoxDimensions dimensions = CssBoxDimensions.valueOf( "0px 1px 2px 3px" );
    assertEquals( 0, dimensions.top );
    assertEquals( 1, dimensions.right );
    assertEquals( 2, dimensions.bottom );
    assertEquals( 3, dimensions.left );
  }

  @Test
  public void testDefaultString() {
    CssBoxDimensions dim0123 = CssBoxDimensions.create( 0, 1, 2, 3 );
    assertEquals( "0px 1px 2px 3px", dim0123.toDefaultString() );
    CssBoxDimensions dim123 = CssBoxDimensions.create( 1, 2, 3, 2 );
    assertEquals( "1px 2px 3px", dim123.toDefaultString() );
    CssBoxDimensions dim01 = CssBoxDimensions.create( 0, 1, 0, 1 );
    assertEquals( "0px 1px", dim01.toDefaultString() );
    CssBoxDimensions dim1 = CssBoxDimensions.create( 1, 1, 1, 1 );
    assertEquals( "1px", dim1.toDefaultString() );
  }

  @Test
  public void testHashCode() {
    CssBoxDimensions dim1 = CssBoxDimensions.create( 1, 1, 0, 0 );
    CssBoxDimensions dim2 = CssBoxDimensions.create( 0, 25, 0, 0 );
    assertTrue( dim1.hashCode() != dim2.hashCode() );
  }

  @Test
  public void testHashCode2() {
    CssBoxDimensions dim1 = CssBoxDimensions.create( 0, 0, 1, 0 );
    CssBoxDimensions dim2 = CssBoxDimensions.create( 0, 0, 0, 1 );
    assertTrue( dim1.hashCode() != dim2.hashCode() );
  }

}
