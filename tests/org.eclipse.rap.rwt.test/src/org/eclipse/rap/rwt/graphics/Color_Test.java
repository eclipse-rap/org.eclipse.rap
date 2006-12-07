/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.graphics;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.internal.graphics.IColor;

public class Color_Test extends TestCase {

  public void testColorFromRGB() {
    Color salmon = Color.getColor( new RGB( 250, 128, 114 ) );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
  }

  public void testColorFromInt() {
    Color salmon = Color.getColor( 250, 128, 114 );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
  }

  public void testEquality() {
    Color salmon = Color.getColor( 250, 128, 114 );
    Color salmon2 = Color.getColor( 250, 128, 114 );
    Color chocolate = Color.getColor( 210, 105, 30 );
    assertTrue( salmon.equals( salmon2 ) );
    assertFalse( salmon.equals( chocolate ) );
  }

  public void testIdentity() {
    Color salmon1 = Color.getColor( 250, 128, 114 );
    Color salmon2 = Color.getColor( 250, 128, 114 );
    assertTrue( salmon1 == salmon2 );
  }

  public void testHTMLString() {
    Color salmon = Color.getColor( 250, 128, 114 );
    assertEquals( "#fa8072", ( ( IColor )salmon ).toColorValue() );
    Color chocolate = Color.getColor( 210, 105, 30 );
    assertEquals( "#d2691e",  ( ( IColor )chocolate ).toColorValue() );
  }
  
  public void testGetRGB() {
    RGB rgbSalmon = new RGB( 250, 128, 114 );
    assertEquals( rgbSalmon, Color.getColor( rgbSalmon ).getRGB() );
  }

}
