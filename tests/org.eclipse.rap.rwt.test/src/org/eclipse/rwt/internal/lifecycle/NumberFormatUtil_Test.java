/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.internal.util.NumberFormatUtil;

import junit.framework.TestCase;

public class NumberFormatUtil_Test extends TestCase {

  public void testParseInt_IntValues() {
    assertEquals( 0, NumberFormatUtil.parseInt( "0" ) );
    assertEquals( 0, NumberFormatUtil.parseInt( "0.0" ) );
    assertEquals( 123, NumberFormatUtil.parseInt( "123" ) );
    assertEquals( -123, NumberFormatUtil.parseInt( "-123" ) );
    assertEquals( 123, NumberFormatUtil.parseInt( "123.00" ) );
  }

  public void testParseInt_EmptyString() {
    String input = "";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw NumberFormatException" );
    } catch( final NumberFormatException e ) {
      // expected
    }
  }

  public void testParseInt_NotNumber() {
    String input = "abc";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw NumberFormatException" );
    } catch( final NumberFormatException e ) {
      // expected
    }
  }

  public void testParseInt_OutOfIntRange() {
    String input = "12345678987654321";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw IllegalArgumentException" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testParseInt_NegativeOutOfIntRange() {
    String input = "-12345678987654321";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw IllegalArgumentException" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testParseInt_FractionalValue() {
    String input = "6.01";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw IllegalArgumentException" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }
}
