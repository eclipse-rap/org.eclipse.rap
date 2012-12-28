/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.fail;

import org.junit.Test;


public class QxDimension_Test {

  @Test
  public void testIllegalArguments() {
    try {
      QxDimension.valueOf( null );
      fail( "NPE expected" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxDimension.valueOf( "" );
      fail( "IAE expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxDimension.valueOf( " 23px" );
      fail( "IAE expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxDimension.valueOf( "23em" );
      fail( "IAE expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testZero() {
    assertSame( QxDimension.ZERO, QxDimension.valueOf( "0" ) );
    assertSame( QxDimension.ZERO, QxDimension.valueOf( "0px" ) );
    assertEquals( 0, QxDimension.ZERO.value );
  }

  @Test
  public void testValid() {
    QxDimension dim23 = QxDimension.valueOf( "23" );
    assertEquals( 23, dim23.value );
    QxDimension dim23px = QxDimension.valueOf( "23px" );
    assertEquals( 23, dim23px.value );
    QxDimension negative = QxDimension.valueOf( "-1" );
    assertEquals( -1, negative.value );
  }

  @Test
  public void testDefaultString() {
    assertEquals( "0px", QxDimension.ZERO.toDefaultString() );
    assertEquals( "23px", QxDimension.valueOf( "23" ).toDefaultString() );
  }

}
