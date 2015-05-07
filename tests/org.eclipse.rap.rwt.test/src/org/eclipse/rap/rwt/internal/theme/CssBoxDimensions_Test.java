/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.rap.rwt.theme.BoxDimensions;
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
    assertEquals( new BoxDimensions( 0, 0, 0, 0 ), CssBoxDimensions.ZERO.dimensions );
  }

  @Test
  public void test1Value() {
    CssBoxDimensions dim23px = CssBoxDimensions.valueOf( "23px" );
    assertEquals( new BoxDimensions( 23, 23, 23, 23 ), dim23px.dimensions );
    CssBoxDimensions dimNeg1 = CssBoxDimensions.valueOf( "-1" );
    assertEquals( new BoxDimensions( -1, -1, -1, -1 ), dimNeg1.dimensions );
  }

  @Test
  public void test2Values() {
    CssBoxDimensions boxDim = CssBoxDimensions.valueOf( "0 2" );
    assertEquals( new BoxDimensions( 0, 2, 0, 2 ), boxDim.dimensions );
  }

  @Test
  public void test3Values() {
    CssBoxDimensions boxDim = CssBoxDimensions.valueOf( "1 2 3px" );
    assertEquals( new BoxDimensions( 1, 2, 3, 2 ), boxDim.dimensions );
  }

  @Test
  public void test4Values() {
    CssBoxDimensions boxDim = CssBoxDimensions.valueOf( "0px 1px 2px 3px" );
    assertEquals( new BoxDimensions( 0, 1, 2, 3 ), boxDim.dimensions );
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
