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


public class QxDimension_Test extends TestCase {

  public void testIllegalArguments() throws Exception {
    try {
      QxDimension.valueOf( null );
      fail( "NPE expected" );
    } catch( final NullPointerException e ) {
      // expected
    }
    try {
      QxDimension.valueOf( "" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    try {
      QxDimension.valueOf( " 23px" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    try {
      QxDimension.valueOf( "23em" );
      fail( "IAE expected" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testZero() throws Exception {
    assertSame( QxDimension.ZERO, QxDimension.valueOf( "0" ) );
    assertSame( QxDimension.ZERO, QxDimension.valueOf( "0px" ) );
    assertEquals( 0, QxDimension.ZERO.value );
  }

  public void testValid() throws Exception {
    QxDimension dim23 = QxDimension.valueOf( "23" );
    assertEquals( 23, dim23.value );
    QxDimension dim23px = QxDimension.valueOf( "23px" );
    assertEquals( 23, dim23px.value );
    QxDimension negative = QxDimension.valueOf( "-1" );
    assertEquals( -1, negative.value );
  }

  public void testDefaultString() throws Exception {
    assertEquals( "0px", QxDimension.ZERO.toDefaultString() );
    assertEquals( "23px", QxDimension.valueOf( "23" ).toDefaultString() );
  }
}
