/*******************************************************************************
 * Copyright (c) 2010, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class CssAnimation_Test {

  private CssAnimation animation;

  @Before
  public void setUp() {
    animation = new CssAnimation();
  }

  @Test( expected = NullPointerException.class )
  public void testAddAnumation_nullName() {
    animation.addAnimation( null, 2, "ease" );
  }

  @Test( expected = NullPointerException.class )
  public void testAddAnumation_nullTimingFunction() {
    animation.addAnimation( "hoverIn", 2, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAnumation_invalidName() {
    animation.addAnimation( "abc", 2, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAnumation_invalidTimingFunction() {
    animation.addAnimation( "hoverIn", 2, "abc" );
  }

  @Test
  public void testCreate() {
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

  @Test
  public void testDefaultString() {
    animation.addAnimation( "fadeIn", 2000, "linear" );
    String expected = "fadeIn 2000ms linear";
    assertEquals( expected, animation.toDefaultString() );
    animation.addAnimation( "fadeOut", 400, "ease-in-out" );
    expected = "fadeIn 2000ms linear, fadeOut 400ms ease-in-out";
    assertEquals( expected, animation.toDefaultString() );
  }

  @Test
  public void testToString() {
    animation.addAnimation( "fadeIn", 2000, "linear" );
    String expected = "CssAnimation{ fadeIn 2000ms linear }";
    assertEquals( expected, animation.toString() );
  }

  @Test
  public void testToCamelCaseString() {
    assertEquals( "easeInOut", CssAnimation.toCamelCaseString( "ease-in-out" ) );
  }

  @Test
  public void testEquals() {
    animation.addAnimation( "fadeIn", 2000, "linear" );
    CssAnimation animation2 = new CssAnimation();
    animation2.addAnimation( "fadeIn", 2000, "linear" );
    assertEquals( animation, animation2 );
    assertEquals( animation.hashCode(), animation2.hashCode() );
  }

}
