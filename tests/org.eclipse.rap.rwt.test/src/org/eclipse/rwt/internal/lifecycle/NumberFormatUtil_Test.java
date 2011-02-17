/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.internal.util.NumberFormatUtil;

import junit.framework.TestCase;

public class NumberFormatUtil_Test extends TestCase {
  
  public void testParseInt_IntValues() {
    String input = "0";
    int result = NumberFormatUtil.parseInt( input );
    assertEquals( 0, result );
    input = "123";
    result = NumberFormatUtil.parseInt( input );
    assertEquals( 123, result );
    input = "-123";
    result = NumberFormatUtil.parseInt( input );
    assertEquals( -123, result );
  }
  
  public void testParseInt_DoubleValues() {
    String input = "123.00";
    int result = NumberFormatUtil.parseInt( input );
    assertEquals( 123, result );
    input = "456.789";
    result = NumberFormatUtil.parseInt( input );
    assertEquals( 457, result );
    input = "654.321";
    result = NumberFormatUtil.parseInt( input );
    assertEquals( 654, result );
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
    input = "-12345678987654321";
    try {
      NumberFormatUtil.parseInt( input );
      fail( "Should throw IllegalArgumentException" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }
  
}
