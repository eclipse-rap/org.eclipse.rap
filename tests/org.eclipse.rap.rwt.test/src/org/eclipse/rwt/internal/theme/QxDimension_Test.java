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

package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class QxDimension_Test extends TestCase {

  public void testInvalid() throws Exception {
    try {
      new QxDimension( null );
      fail( "NPE expected" );
    } catch( final NullPointerException e ) {
      // expected
    }
    try {
      new QxDimension( "" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    try {
      new QxDimension( " 23px" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    try {
      new QxDimension( "23em" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testValid() throws Exception {
    QxDimension dimension;
    dimension = new QxDimension( "0" );
    assertEquals( 0, dimension.value );
    dimension = new QxDimension( "23" );
    assertEquals( 23, dimension.value );
    dimension = new QxDimension( "23px" );
    assertEquals( 23, dimension.value );
    dimension = new QxDimension( "-1" );
    assertEquals( -1, dimension.value );
  }

  public void testDefaultString() throws Exception {
    QxDimension dimension;
    dimension = new QxDimension( "0" );
    assertEquals( "0px", dimension.toDefaultString() );
  }
}
