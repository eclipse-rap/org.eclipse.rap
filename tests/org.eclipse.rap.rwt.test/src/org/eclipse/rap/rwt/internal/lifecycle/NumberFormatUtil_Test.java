/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.junit.Test;


public class NumberFormatUtil_Test {

  @Test
  public void testParseInt_IntValues() {
    assertEquals( 0, NumberFormatUtil.parseInt( "0" ) );
    assertEquals( 0, NumberFormatUtil.parseInt( "0.0" ) );
    assertEquals( 123, NumberFormatUtil.parseInt( "123" ) );
    assertEquals( -123, NumberFormatUtil.parseInt( "-123" ) );
    assertEquals( 123, NumberFormatUtil.parseInt( "123.00" ) );
  }

  @Test
  public void testParseInt_EmptyString() {
    String input = "";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw NumberFormatException" );
    } catch( NumberFormatException e ) {
      // expected
    }
  }

  @Test
  public void testParseInt_NotNumber() {
    String input = "abc";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw NumberFormatException" );
    } catch( NumberFormatException e ) {
      // expected
    }
  }

  @Test
  public void testParseInt_OutOfIntRange() {
    String input = "12345678987654321";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testParseInt_NegativeOutOfIntRange() {
    String input = "-12345678987654321";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testParseInt_FractionalValue() {
    String input = "6.01";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

}
