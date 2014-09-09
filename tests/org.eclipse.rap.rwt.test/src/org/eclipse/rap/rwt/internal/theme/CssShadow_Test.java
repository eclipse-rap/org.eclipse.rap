/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class CssShadow_Test {

  @Test( expected = IllegalArgumentException.class )
  public void testCreate_negativeBlur() {
    CssShadow.create( false, 10, 10, -10, 0, CssColor.BLACK );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreate_spreadNotZero() {
    CssShadow.create( false, 10, 10, 0, 10, CssColor.BLACK );
  }

  @Test( expected = NullPointerException.class )
  public void testCreate_nullColor() {
    CssShadow.create( false, 10, 10, 0, 0, null );
  }

  @Test
  public void testCreate_WithoutOpacity() {
    CssShadow shadow = CssShadow.create( false, 10, 10, 0, 0, CssColor.BLACK );
    assertNotNull( shadow );
    assertFalse( shadow.inset );
    assertEquals( 10, shadow.offsetX );
    assertEquals( 10, shadow.offsetY );
    assertEquals( 0, shadow.blur );
    assertEquals( 0, shadow.spread );
    assertEquals( CssColor.BLACK.toDefaultString(), shadow.color );
    assertEquals( 1f, shadow.opacity, 0 );
  }

  @Test
  public void testCreate_WithOpacity() {
    CssColor color = CssColor.valueOf( "0, 0, 0, 0.5" );
    CssShadow shadow = CssShadow.create( false, 10, 10, 0, 0, color );
    assertNotNull( shadow );
    assertFalse( shadow.inset );
    assertEquals( 10, shadow.offsetX );
    assertEquals( 10, shadow.offsetY );
    assertEquals( 0, shadow.blur );
    assertEquals( 0, shadow.spread );
    assertEquals( CssColor.BLACK.toDefaultString(), shadow.color );
    assertEquals( 0.5, shadow.opacity, 0 );
  }

  @Test
  public void testNoneShadow() {
    CssShadow shadow = CssShadow.NONE;
    assertFalse( shadow.inset );
    assertEquals( 0, shadow.offsetX );
    assertEquals( 0, shadow.offsetY );
    assertEquals( 0, shadow.blur );
    assertEquals( 0, shadow.spread );
    assertNull( shadow.color );
    assertEquals( 0, shadow.opacity, 0 );
  }

  @Test
  public void testToString() {
    CssShadow shadow = CssShadow.create( false, 10, 10, 0, 0, CssColor.BLACK );
    String expected = "CssShadow{ false, 10, 10, 0, 0, #000000, 1.0 }";
    assertEquals( expected, shadow.toString() );
  }

  @Test
  public void testToDefaultString() {
    CssColor color = CssColor.valueOf( "0, 0, 0, 0.5" );
    CssShadow shadow = CssShadow.create( false, 10, 10, 0, 0, color );
    String expected = "10px 10px 0px 0px rgba( 0, 0, 0, 0.5 )";
    assertEquals( expected, shadow.toDefaultString() );
  }

  @Test
  public void testToDefaultString_NoneShadow() {
    CssShadow shadow = CssShadow.NONE;
    String expected = "none";
    assertEquals( expected, shadow.toDefaultString() );
  }

  @Test
  public void testEquals() {
    CssShadow shadow1 = CssShadow.create( false, 10, 10, 0, 0, CssColor.BLACK );
    CssShadow shadow2 = CssShadow.create( false, 10, 10, 0, 0, CssColor.BLACK );
    assertTrue( shadow1.equals( shadow2 ) );
  }

}
