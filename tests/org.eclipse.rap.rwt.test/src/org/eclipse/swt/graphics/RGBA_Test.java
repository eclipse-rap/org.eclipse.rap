/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class RGBA_Test {

  @Test
  public void testColorValues_transparent() {
    RGBA rgba = new RGBA( 0, 0, 0, 0 );
    assertEquals( 0, rgba.rgb.red );
    assertEquals( 0, rgba.rgb.green );
    assertEquals( 0, rgba.rgb.blue );
    assertEquals( 0, rgba.alpha );
  }

  @Test
  public void testColorValues_white() {
    RGBA rgba = new RGBA( 255, 255, 255, 255 );
    assertEquals( 255, rgba.rgb.red );
    assertEquals( 255, rgba.rgb.green );
    assertEquals( 255, rgba.rgb.blue );
    assertEquals( 255, rgba.alpha );
  }

  @Test
  public void testColorValues_salmon() {
    RGBA rgba = new RGBA( 250, 128, 114, 128 );
    assertEquals( 250, rgba.rgb.red );
    assertEquals( 128, rgba.rgb.green );
    assertEquals( 114, rgba.rgb.blue );
    assertEquals( 128, rgba.alpha );
  }

  @Test
  public void testEquality() {
    RGBA rgbaSalmon = new RGBA( 250, 128, 114, 128 );
    RGBA rgbaChocolate = new RGBA( 210, 105, 30, 200 );
    assertTrue( rgbaSalmon.equals( new RGBA( 250, 128, 114, 128 ) ) );
    assertFalse( rgbaSalmon.equals( rgbaChocolate ) );
  }

  @Test
  public void testSerialization() throws Exception {
    RGBA rgba = new RGBA( 1, 2, 3, 4 );
    RGBA deserializedRGBA = serializeAndDeserialize( rgba );
    assertEquals( rgba, deserializedRGBA );
  }

}
