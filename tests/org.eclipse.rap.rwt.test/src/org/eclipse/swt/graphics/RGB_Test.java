/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.Test;


public class RGB_Test {

  @Test
  public void testColorValues() {
    RGB rgbBlack = new RGB( 0, 0, 0 );
    assertEquals( 0, rgbBlack.red );
    assertEquals( 0, rgbBlack.green );
    assertEquals( 0, rgbBlack.blue );
    RGB rgbWhite = new RGB( 255, 255, 255 );
    assertEquals( 255, rgbWhite.red );
    assertEquals( 255, rgbWhite.green );
    assertEquals( 255, rgbWhite.blue );
    RGB rgbSalmon = new RGB( 250, 128, 114 );
    assertEquals( 250, rgbSalmon.red );
    assertEquals( 128, rgbSalmon.green );
    assertEquals( 114, rgbSalmon.blue );
  }

  @Test
  public void testEquality() {
    RGB rgbSalmon = new RGB( 250, 128, 114 );
    RGB rgbChocolate = new RGB( 210, 105, 30 );
    assertTrue( rgbSalmon.equals( new RGB( 250, 128, 114 ) ) );
    assertFalse( rgbSalmon.equals( rgbChocolate ) );
  }

  @Test
  public void testSerialization() throws Exception {
    RGB rgb = new RGB( 1, 2, 3 );
    RGB deserializedRGB = Fixture.serializeAndDeserialize( rgb );
    assertEquals( rgb, deserializedRGB );
  }

}
