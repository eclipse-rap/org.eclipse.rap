/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import org.eclipse.rwt.Fixture;

import junit.framework.TestCase;

public class Device_Test extends TestCase {

  private static final class TestDevice extends Device {
  }

  public void testGetClientArea() {
    Device device = new TestDevice();
    Rectangle clientArea = device.getClientArea();
    assertNotNull( clientArea );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), clientArea );
  }

  public void testGetBounds() {
    Device device = new TestDevice();
    Rectangle bounds = device.getBounds();
    assertNotNull( bounds );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), bounds );
  }
  
  public void testGetDPIReturnsSafeCopy() {
    Device device = new TestDevice();
    Point dpi1 = device.getDPI();
    dpi1.x = -123;
    dpi1.y = -456;
    Point dpi2 = device.getDPI();
    assertFalse( dpi1.equals( dpi2 ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
