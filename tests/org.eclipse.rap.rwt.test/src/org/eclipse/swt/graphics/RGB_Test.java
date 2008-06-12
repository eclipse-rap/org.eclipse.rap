/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.graphics;

import junit.framework.TestCase;

public class RGB_Test extends TestCase {

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

  public void testEquality() {
    RGB rgbSalmon = new RGB( 250, 128, 114 );
    RGB rgbChocolate = new RGB( 210, 105, 30 );
    assertTrue( rgbSalmon.equals( new RGB( 250, 128, 114 ) ) );
    assertFalse( rgbSalmon.equals( rgbChocolate ) );
  }

}
