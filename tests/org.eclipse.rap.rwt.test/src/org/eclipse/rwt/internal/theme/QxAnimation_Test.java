/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class QxAnimation_Test extends TestCase {

  public void testIllegalArguments() {
    QxAnimation animation = new QxAnimation();
    try {
      animation.addAnimation( null, 2, "ease" );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      animation.addAnimation( "hoverIn", 2, null );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      animation.addAnimation( "abc", 2, "ease" );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      animation.addAnimation( "hoverIn", 2, "abc" );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testCreate() {
    QxAnimation animation = new QxAnimation();
    animation.addAnimation( "fadeIn", 2000, "linear" );
    assertEquals( 1, animation.animations.length );
    assertEquals( "fadeIn", animation.animations[ 0 ].name );
    assertEquals( 2000, animation.animations[ 0 ].duration );
    assertEquals( "linear", animation.animations[ 0 ].timingFunction );
    animation.addAnimation( "fadeOut", 400, "ease-in-out" );
    assertEquals( 2, animation.animations.length );
    assertEquals( "fadeIn", animation.animations[ 0 ].name );
    assertEquals( 2000, animation.animations[ 0 ].duration );
    assertEquals( "linear", animation.animations[ 0 ].timingFunction );
    assertEquals( "fadeOut", animation.animations[ 1 ].name );
    assertEquals( 400, animation.animations[ 1 ].duration );
    assertEquals( "ease-in-out", animation.animations[ 1 ].timingFunction );
  }

  public void testDefaultString() {
    QxAnimation animation = new QxAnimation();
    animation.addAnimation( "fadeIn", 2000, "linear" );
    String expected = "fadeIn 2000ms linear";
    assertEquals( expected, animation.toDefaultString() );
    animation.addAnimation( "fadeOut", 400, "ease-in-out" );
    expected = "fadeIn 2000ms linear, fadeOut 400ms ease-in-out";
    assertEquals( expected, animation.toDefaultString() );
  }

  public void testToString() {
    QxAnimation animation = new QxAnimation();
    animation.addAnimation( "fadeIn", 2000, "linear" );
    String expected = "QxAnimation{ fadeIn 2000ms linear }";
    assertEquals( expected, animation.toString() );
  }

  public void testToCamelCaseString() {
    assertEquals( "easeInOut", QxAnimation.toCamelCaseString( "ease-in-out" ) );
  }

  public void testEquals() {
    QxAnimation animation1 = new QxAnimation();
    animation1.addAnimation( "fadeIn", 2000, "linear" );
    QxAnimation animation2 = new QxAnimation();
    animation2.addAnimation( "fadeIn", 2000, "linear" );
    assertEquals( animation1, animation2 );
    assertEquals( animation1.hashCode(), animation2.hashCode() );
  }
}
