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

import static org.eclipse.rap.rwt.internal.theme.CssColor.BLACK;
import static org.eclipse.rap.rwt.internal.theme.CssColor.WHITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class CssBorder_Test {

  @Test( expected = NullPointerException.class )
  public void testValueOf_null() {
    CssBorder.valueOf( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_empty1() {
    CssBorder.valueOf( "" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_empty2() {
    CssBorder.valueOf( " " );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_negative() {
    CssBorder.valueOf( "-1" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_tooManyArguments() {
    CssBorder.valueOf( "1 solid red 2" );
  }

  @Test
  public void testValueOf_widthOnly() {
    CssBorder border = CssBorder.valueOf( "1px" );

    assertEquals( CssBorder.NONE, border );
  }

  @Test
  public void testValueOf_withoutStyle() {
    CssBorder border = CssBorder.valueOf( "1px black" );

    assertEquals( CssBorder.NONE, border );
  }

  @Test
  public void testValueOf_colorOnly() {
    CssBorder border = CssBorder.valueOf( "black" );

    assertEquals( CssBorder.NONE, border );
  }

  @Test
  public void testValueOf_none() {
    CssBorder border = CssBorder.valueOf( "none" );

    assertEquals( CssBorder.NONE, border );
  }

  @Test
  public void testValueOf_hidden() {
    CssBorder border = CssBorder.valueOf( "hidden" );

    assertEquals( CssBorder.NONE, border );
  }

  @Test
  public void testValueOf_zeroWidth() {
    CssBorder border = CssBorder.valueOf( "0 solid black" );

    assertEquals( CssBorder.NONE, border );
  }

  @Test
  public void testValueOf_setsDefaults() {
    CssBorder border = CssBorder.valueOf( "solid" );
    assertEquals( 1, border.width );
    assertEquals( "solid", border.style );
    assertNull( border.color );

    border = CssBorder.valueOf( "2 dashed" );
    assertEquals( 2, border.width );
    assertEquals( "dashed", border.style );
    assertNull( border.color );
  }

  @Test
  public void testNoneBorder() {
    assertSame( CssBorder.NONE, CssBorder.valueOf( "none" ) );

    assertEquals( 0, CssBorder.NONE.width );
    assertEquals( "none", CssBorder.NONE.style );
    assertNull( CssBorder.NONE.color );
  }

  @Test
  public void testDefaultString() {
    CssBorder red = CssBorder.valueOf( "red" );
    assertEquals( "none", red.toDefaultString() );
    CssBorder border1 = CssBorder.valueOf( "3 solid red" );
    assertEquals( "3px solid #ff0000", border1.toDefaultString() );
    CssBorder border2 = CssBorder.valueOf( "1 dashed #ff0000" );
    assertEquals( "1px dashed #ff0000", border2.toDefaultString() );
  }

  @Test
  public void testEquals() {
    CssBorder border1 = CssBorder.create( 1, "solid", BLACK );
    CssBorder border2 = CssBorder.create( 1, "solid", BLACK );
    CssBorder border3 = CssBorder.create( 2, "dashed", WHITE );

    assertNotNull( border1 );
    assertTrue( border1.equals( border2 ) );
    assertFalse( border1.equals( border3 ) );
  }

}
