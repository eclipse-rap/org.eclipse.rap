/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class Device_Test {

  @Rule
  public TestContext context = new TestContext();

  private Device device;

  @Before
  public void setUp() {
    device = new TestDevice();
  }

  @Test
  public void testGetClientArea() {
    Rectangle clientArea = device.getClientArea();
    assertNotNull( clientArea );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), clientArea );
  }

  @Test
  public void testGetBounds() {
    Rectangle bounds = device.getBounds();
    assertNotNull( bounds );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), bounds );
  }

  @Test
  public void testGetDPIReturnsSafeCopy() {
    Point dpi1 = device.getDPI();
    dpi1.x = -123;
    dpi1.y = -456;
    Point dpi2 = device.getDPI();
    assertFalse( dpi1.equals( dpi2 ) );
  }

  @Test
  public void testSetSystemColor() {
    Color blue = device.getSystemColor( SWT.COLOR_BLUE );

    assertEquals( new Color( device, 0, 0, 255 ), blue );
  }

  @Test
  public void testSetSystemColor_returnsSameInstance() {
    Color blue1 = device.getSystemColor( SWT.COLOR_BLUE );
    Color blue2 = device.getSystemColor( SWT.COLOR_BLUE );

    assertSame( blue1, blue2 );
  }

  @Test
  public void testSetSystemColor_transparent() {
    Color transparent = device.getSystemColor( SWT.COLOR_TRANSPARENT );

    assertEquals( new Color( device, 0, 0, 0, 0 ), transparent );
  }

  private static final class TestDevice extends Device {
    private static final long serialVersionUID = 1L;
  }

}
