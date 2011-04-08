/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class QxShadow_Test extends TestCase {
  
  public void testIllegalArguments_Inset() {
    try {
      QxShadow.create( true, 10, 10, 0, 0, QxColor.BLACK );
      fail( "Exception expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testIllegalArguments_NegativeBlur() {
    try {
      QxShadow.create( false, 10, 10, -10, 0, QxColor.BLACK );
      fail( "Exception expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testIllegalArguments_SpreadNotZero() {
    try {
      QxShadow.create( false, 10, 10, 0, 10, QxColor.BLACK );
      fail( "Exception expected" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testIllegalArguments_NullColor() {
    try {
      QxShadow.create( false, 10, 10, 0, 0, null );
      fail( "Exception expected" );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testCreate_WithoutOpacity() {
    QxShadow shadow = QxShadow.create( false, 10, 10, 0, 0, QxColor.BLACK );
    assertNotNull( shadow );
    assertFalse( shadow.inset );
    assertEquals( 10, shadow.offsetX );
    assertEquals( 10, shadow.offsetY );
    assertEquals( 0, shadow.blur );
    assertEquals( 0, shadow.spread );
    assertEquals( QxColor.BLACK.toDefaultString(), shadow.color );
    assertEquals( 1f, shadow.opacity, 0 );
  }

  public void testCreate_WithOpacity() {
    QxColor color = QxColor.valueOf( "0, 0, 0, 0.5" );
    QxShadow shadow = QxShadow.create( false, 10, 10, 0, 0, color );
    assertNotNull( shadow );
    assertFalse( shadow.inset );
    assertEquals( 10, shadow.offsetX );
    assertEquals( 10, shadow.offsetY );
    assertEquals( 0, shadow.blur );
    assertEquals( 0, shadow.spread );
    assertEquals( QxColor.BLACK.toDefaultString(), shadow.color );
    assertEquals( 0.5, shadow.opacity, 0 );
  }

  public void testNoneShadow() {
    QxShadow shadow = QxShadow.NONE;
    assertFalse( shadow.inset );
    assertEquals( 0, shadow.offsetX );
    assertEquals( 0, shadow.offsetY );
    assertEquals( 0, shadow.blur );
    assertEquals( 0, shadow.spread );
    assertNull( shadow.color );
    assertEquals( 0, shadow.opacity, 0 );
  }

  public void testToString() {
    QxShadow shadow = QxShadow.create( false, 10, 10, 0, 0, QxColor.BLACK );
    String expected = "QxShadow{ false, 10, 10, 0, 0, #000000, 1.0 }";
    assertEquals( expected, shadow.toString() );
  }

  public void testToDefaultString() {
    QxColor color = QxColor.valueOf( "0, 0, 0, 0.5" );
    QxShadow shadow = QxShadow.create( false, 10, 10, 0, 0, color );
    String expected = "10px 10px 0px 0px rgba( 0, 0, 0, 0.5 )";
    assertEquals( expected, shadow.toDefaultString() );
  }

  public void testToDefaultString_NoneShadow() {
    QxShadow shadow = QxShadow.NONE;
    String expected = "none";
    assertEquals( expected, shadow.toDefaultString() );
  }

  public void testEquals() {
    QxShadow shadow1 = QxShadow.create( false, 10, 10, 0, 0, QxColor.BLACK );
    QxShadow shadow2 = QxShadow.create( false, 10, 10, 0, 0, QxColor.BLACK );
    assertTrue( shadow1.equals( shadow2 ) );
  } 
}
