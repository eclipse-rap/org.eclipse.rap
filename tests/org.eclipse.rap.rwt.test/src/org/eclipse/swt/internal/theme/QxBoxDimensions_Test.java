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


public class QxBoxDimensions_Test extends TestCase {

  public void testInvalid() throws Exception {
    try {
      new QxBoxDimensions( null );
      fail( "NPE expected" );
    } catch( final NullPointerException e ) {
      // expected
    }
    try {
      new QxBoxDimensions( "" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    try {
      new QxBoxDimensions( " 23px" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    try {
      new QxBoxDimensions( "23em" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testValid1() throws Exception {
    QxBoxDimensions dimensions;
    dimensions = new QxBoxDimensions( "0" );
    assertEquals( 0, dimensions.top );
    assertEquals( 0, dimensions.right );
    assertEquals( 0, dimensions.bottom );
    assertEquals( 0, dimensions.left );
    dimensions = new QxBoxDimensions( "23px" );
    assertEquals( 23, dimensions.top );
    assertEquals( 23, dimensions.right );
    assertEquals( 23, dimensions.bottom );
    assertEquals( 23, dimensions.left );
    dimensions = new QxBoxDimensions( "-1" );
    assertEquals( -1, dimensions.top );
    assertEquals( -1, dimensions.right );
    assertEquals( -1, dimensions.bottom );
    assertEquals( -1, dimensions.left );
  }

  public void testValid2() throws Exception {
    QxBoxDimensions dimensions;
    dimensions = new QxBoxDimensions( "0 2" );
    assertEquals( 0, dimensions.top );
    assertEquals( 2, dimensions.right );
    assertEquals( 0, dimensions.bottom );
    assertEquals( 2, dimensions.left );
  }

  public void testValid3() throws Exception {
    QxBoxDimensions dimensions;
    dimensions = new QxBoxDimensions( "1 2 3px" );
    assertEquals( 1, dimensions.top );
    assertEquals( 2, dimensions.right );
    assertEquals( 3, dimensions.bottom );
    assertEquals( 2, dimensions.left );
  }

  public void testValid4() throws Exception {
    QxBoxDimensions dimensions;
    dimensions = new QxBoxDimensions( "0px 1px 2px 3px" );
    assertEquals( 0, dimensions.top );
    assertEquals( 1, dimensions.right );
    assertEquals( 2, dimensions.bottom );
    assertEquals( 3, dimensions.left );
  }

  public void testDefaultString() throws Exception {
    QxBoxDimensions dimensions;
    dimensions = new QxBoxDimensions( "0px 1px 2px 3px" );
    assertEquals( "0px 1px 2px 3px", dimensions.toDefaultString() );
  }
}
