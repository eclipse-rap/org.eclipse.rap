/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
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

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Device_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetClientArea() {
    Device device = new TestDevice();
    Rectangle clientArea = device.getClientArea();
    assertNotNull( clientArea );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), clientArea );
  }

  @Test
  public void testGetBounds() {
    Device device = new TestDevice();
    Rectangle bounds = device.getBounds();
    assertNotNull( bounds );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), bounds );
  }

  @Test
  public void testGetDPIReturnsSafeCopy() {
    Device device = new TestDevice();
    Point dpi1 = device.getDPI();
    dpi1.x = -123;
    dpi1.y = -456;
    Point dpi2 = device.getDPI();
    assertFalse( dpi1.equals( dpi2 ) );
  }

  private static final class TestDevice extends Device {
    private static final long serialVersionUID = 1L;
  }

}
