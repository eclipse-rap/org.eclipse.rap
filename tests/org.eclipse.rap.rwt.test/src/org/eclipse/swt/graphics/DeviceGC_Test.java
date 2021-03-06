/*******************************************************************************
 * Copyright (c) 2011, 2021 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DeviceGC_Test {

  private Display display;
  private GC gc;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    gc = new GC( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testInitialValues() {
    assertEquals( display.getSystemFont(), gc.getFont() );
    Color white = display.getSystemColor( SWT.COLOR_WHITE );
    assertEquals( white, gc.getBackground() );
    Color black = display.getSystemColor( SWT.COLOR_BLACK );
    assertEquals( black, gc.getForeground() );
  }

  @Test
  public void testSetAlpha() {
    gc.setAlpha( 123 );
    assertEquals( 123, gc.getAlpha() );
  }

  @Test
  public void testSetLineWidth() {
    gc.setLineWidth( 5 );
    assertEquals( 5, gc.getLineWidth() );
  }

  @Test
  public void testSetLineJoin() {
    gc.setLineJoin( SWT.JOIN_ROUND );
    assertEquals( SWT.JOIN_ROUND, gc.getLineJoin() );
  }

  @Test
  public void testSetLineCap() {
    gc.setLineCap( SWT.CAP_ROUND );
    assertEquals( SWT.CAP_ROUND, gc.getLineCap() );
  }

  @Test
  public void testSetLineStyle() {
    gc.setLineStyle( SWT.LINE_DOT );

    assertEquals( SWT.LINE_DOT, gc.getLineStyle() );
  }

  @Test
  public void testSetLineDash() {
    gc.setLineDash( new int[] { 1, 2, 3 } );

    assertTrue( Arrays.equals( new int[] { 1, 2, 3 }, gc.getLineDash() ) );
  }

  @Test
  public void testSetFont() {
    Font font = createFont();
    gc.setFont( font );
    assertEquals( font, gc.getFont() );
  }

  @Test
  public void testSetFontWithNullFont() {
    Font font = createFont();
    gc.setFont( font );
    gc.setFont( null );
    assertEquals( display.getSystemFont(), gc.getFont() );
  }

  @Test
  public void testSetBackground() {
    Color color = createColor();
    gc.setBackground( color );
    assertEquals( color, gc.getBackground() );
  }

  @Test
  public void testSetForeground() {
    Color color = createColor();
    gc.setForeground( color );
    assertEquals( color, gc.getForeground() );
  }

  @Test
  public void testGetClipping_withoutClipping() {
    assertEquals( display.getBounds(), gc.getClipping() );
  }

  @Test
  public void testGetClipping_withClipping_rectangle() {
    gc.setClipping( new Rectangle( 10, 10, 10, 10 ) );

    assertEquals( new Rectangle( 10, 10, 10, 10 ), gc.getClipping() );
  }

  @Test
  public void testGetClipping_withClipping_dimensions() {
    gc.setClipping( 10, 10, 10, 10 );

    assertEquals( new Rectangle( 10, 10, 10, 10 ), gc.getClipping() );
  }

  @Test
  public void testGetClipping_withClipping_path() {
    Path path = new Path( display );
    path.moveTo( 20, 20 );
    path.lineTo( 30, 30 );
    path.lineTo( 10, 40 );
    path.close();

    gc.setClipping( path );

    assertEquals( new Rectangle( 10, 20, 20, 20 ), gc.getClipping() );
  }

  @Test
  public void testGetClipping_afterClippingReset() {
    gc.setClipping( 10, 10, 10, 10 );

    gc.setClipping( ( Path )null );

    assertEquals( display.getBounds(), gc.getClipping() );
  }

  @Test
  public void testGetClipping_setClippingTwice() {
    gc.setClipping( 10, 10, 10, 10 );
    gc.setClipping( 20, 20, 20, 20 );

    assertEquals( new Rectangle( 20, 20, 20, 20 ), gc.getClipping() );
  }

  private Font createFont() {
    return new Font( display, "font-name", 11, SWT.NORMAL );
  }

  private Color createColor() {
    return new Color( display, 1, 2, 3 );
  }

}
