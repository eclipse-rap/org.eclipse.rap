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

import org.junit.Test;


public class CssDimension_Test {

  @Test( expected = NullPointerException.class )
  public void testValueOf_nullArgument() {
    CssDimension.valueOf( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_emptyString() {
    CssDimension.valueOf( "" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_startWithSpace() {
    CssDimension.valueOf( " 23px" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_unsupportedUnit() {
    CssDimension.valueOf( "23em" );
  }

  @Test
  public void testZero() {
    assertSame( CssDimension.ZERO, CssDimension.valueOf( "0" ) );
    assertSame( CssDimension.ZERO, CssDimension.valueOf( "0px" ) );
    assertEquals( 0, CssDimension.ZERO.value );
  }

  @Test
  public void testValid() {
    CssDimension dim23 = CssDimension.valueOf( "23" );
    assertEquals( 23, dim23.value );
    CssDimension dim23px = CssDimension.valueOf( "23px" );
    assertEquals( 23, dim23px.value );
    CssDimension negative = CssDimension.valueOf( "-1" );
    assertEquals( -1, negative.value );
  }

  @Test
  public void testDefaultString() {
    assertEquals( "0px", CssDimension.ZERO.toDefaultString() );
    assertEquals( "23px", CssDimension.valueOf( "23" ).toDefaultString() );
  }

}
