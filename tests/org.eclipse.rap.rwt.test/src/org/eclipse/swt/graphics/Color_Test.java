/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.graphics.Graphics;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings( "deprecation" )
public class Color_Test {

  @Rule
  public TestContext context = new TestContext();

  private Device device;

  @Before
  public void setUp() {
    device = new Display();
  }

  @Test
  public void testColorFromRGB() {
    Color salmon = new Color( device, new RGB( 250, 128, 114 ) );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
    assertEquals( 255, salmon.getAlpha() );
  }

  @Test
  public void testColorFromRGBWithAlpha() {
    Color salmon = new Color( device, new RGB( 250, 128, 114 ), 128 );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
    assertEquals( 128, salmon.getAlpha() );
  }

  @Test
  public void testColorFromRGBA() {
    Color salmon = new Color( device, new RGBA( 250, 128, 114, 128 ) );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
    assertEquals( 128, salmon.getAlpha() );
  }

  @Test
  public void testColorFromInt() {
    Color salmon = new Color( device, 250, 128, 114 );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
    assertEquals( 255, salmon.getAlpha() );
  }

  @Test
  public void testColorFromIntWithAlpha() {
    Color salmon = new Color( device, 250, 128, 114, 64 );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
    assertEquals( 64, salmon.getAlpha() );
  }

  @Test
  public void testColorFromConstant() {
    Color color = device.getSystemColor( SWT.COLOR_RED );
    assertEquals( 255, color.getRed() );
    assertEquals( 0, color.getGreen() );
    assertEquals( 0, color.getBlue() );
    assertEquals( 255, color.getAlpha() );
  }

  @Test
  public void testEquality() {
    Color salmon1 = new Color( device, 250, 128, 114 );
    Color salmon2 = new Color( device, 250, 128, 114 );
    Color chocolate = new Color( device, 210, 105, 30 );
    assertTrue( salmon1.equals( salmon2 ) );
    assertFalse( salmon1.equals( chocolate ) );
    salmon1 = new Color( device, 250, 128, 114 );
    salmon2 = new Color( device, 250, 128, 114 );
    assertTrue( salmon1.equals( salmon2 ) );
    salmon1 = new Color( device, 250, 128, 114 );
    salmon2 = Graphics.getColor( 250, 128, 114 );
    assertTrue( salmon1.equals( salmon2 ) );
  }

  @Test
  public void testEquality_withAlpha() {
    Color salmon1 = new Color( device, 250, 128, 114 );
    Color salmon2 = new Color( device, 250, 128, 114, 255 );

    assertTrue( salmon1.equals( salmon2 ) );
  }

  @Test
  public void testIdentity() {
    Color salmon1 = Graphics.getColor( 250, 128, 114 );
    Color salmon2 = Graphics.getColor( 250, 128, 114 );
    assertSame( salmon1, salmon2 );
    salmon1 = new Color( device, 250, 128, 114 );
    salmon2 = Graphics.getColor( 250, 128, 114 );
    assertNotSame( salmon1, salmon2 );
  }

  @Test
  public void testGetRGB() {
    RGB rgbSalmon = new RGB( 250, 128, 114 );
    assertEquals( rgbSalmon, new Color( device, rgbSalmon ).getRGB() );
  }

  @Test
  public void testGetRGBA() {
    RGBA rgbSalmon = new RGBA( 250, 128, 114, 128 );

    assertEquals( rgbSalmon, new Color( device, rgbSalmon ).getRGBA() );
  }

  @Test
  public void testConstructor() {
    Color color = new Color( null, 0, 0, 0 );
    assertSame( Display.getCurrent(), color.getDevice() );

    color = new Color( null, new RGB( 0, 0, 0 ) );
    assertSame( Display.getCurrent(), color.getDevice() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructor_withNullDevice() {
    device.dispose();

    new Color( null, 0, 0, 0 );
  }

  @Test
  public void testConstructor_withDisposedDevice() {
    device.dispose();

    new Color( device, 0, 0, 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructor_withNegativeRedValue() {
    new Color( device, -1, 0, 0, 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructor_withInvalidRedValue() {
    new Color( device, 300, 0, 0, 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructor_withNegativeGreenValue() {
    new Color( device, 0, -1, 0, 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructor_withInvalidGreenValue() {
    new Color( device, 0, 300, 0, 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructor_withNegativeBlueValue() {
    new Color( device, 0, 0, -1, 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructor_withInvalidBlueValue() {
    new Color( device, 0, 0, 300, 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructor_withNegativeAlphaValue() {
    new Color( device, 0, 0, 0, -1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructor_withInvalidAlphaValue() {
    new Color( device, 0, 0, 0, 300 );
  }

  @Test
  public void testDispose() {
    Color color = new Color( device, new RGB( 0, 0, 0 ) );

    color.dispose();

    assertTrue( color.isDisposed() );
  }

  @Test( expected = IllegalStateException.class )
  public void testDisposeFactoryCreated() {
    Color color = Graphics.getColor( new RGB( 0, 0, 0 ) );

    color.dispose();
  }

  @Test( expected = SWTException.class )
  public void testGetRed_afterDispose() {
    Color color = new Color( device, 0, 0, 0 );
    color.dispose();

    color.getRed();
  }

  @Test( expected = SWTException.class )
  public void testGetGreen_afterDispose() {
    Color color = new Color( device, 0, 0, 0 );
    color.dispose();

    color.getGreen();
  }

  @Test( expected = SWTException.class )
  public void testGetBlue_afterDispose() {
    Color color = new Color( device, 0, 0, 0 );
    color.dispose();

    color.getBlue();
  }

  @Test( expected = SWTException.class )
  public void testGetAlpha_afterDispose() {
    Color color = new Color( device, 0, 0, 0 );
    color.dispose();

    color.getAlpha();
  }

  @Test( expected = SWTException.class )
  public void testGetRGB_afterDispose() {
    Color color = new Color( device, 0, 0, 0 );
    color.dispose();

    color.getRGB();
  }

  @Test( expected = SWTException.class )
  public void testGetRGBA_afterDispose() {
    Color color = new Color( device, 0, 0, 0 );
    color.dispose();

    color.getRGBA();
  }

  @Test
  public void testSerializeSessionColor() throws Exception {
    Color color = new Color( device, 1, 2, 3 );

    Color deserializedColor = serializeAndDeserialize( color );

    assertEquals( color.getRGB(), deserializedColor.getRGB() );
    assertEquals( color.getRGBA(), deserializedColor.getRGBA() );
    assertFalse( deserializedColor.isDisposed() );
    assertNotNull( deserializedColor.getDevice() );
    assertNotSame( color.getDevice(), deserializedColor.getDevice() );
  }

  @Test
  public void testSerializeSharedColor() throws Exception {
    Color color = Graphics.getColor( 1, 2, 3 );

    Color deserializedColor = serializeAndDeserialize( color );

    assertEquals( color.getRGB(), deserializedColor.getRGB() );
    assertFalse( deserializedColor.isDisposed() );
  }

}
