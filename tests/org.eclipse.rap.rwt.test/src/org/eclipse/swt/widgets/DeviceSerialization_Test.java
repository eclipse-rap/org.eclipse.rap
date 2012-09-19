/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.Device;


public class DeviceSerialization_Test extends TestCase {

  private static class TestDevice extends Device {
    Object getDeviceLock() {
      return deviceLock;
    }
  }

  private TestDevice device;

  public void testDeviceLockIsSerializable() throws Exception {
    TestDevice device = new TestDevice();

    TestDevice deserializedDevice = Fixture.serializeAndDeserialize( device );

    assertNotNull( deserializedDevice.getDeviceLock() );
  }

  public void testDisposedIsSerializable() throws Exception {
    TestDevice device = new TestDevice();

    TestDevice deserializedDevice = Fixture.serializeAndDeserialize( device );

    assertFalse( deserializedDevice.isDisposed() );
  }

  public void testDPIAndColorDepthIsSerializable() throws Exception {
    Fixture.fakeSetParameter( "w1", "dpi.x", Integer.valueOf( 1 ) );
    Fixture.fakeSetParameter( "w1", "dpi.y", Integer.valueOf( 2 ) );
    Fixture.fakeSetParameter( "w1", "colorDepth", Integer.valueOf( 32 ) );
    TestDevice device = new TestDevice();

    TestDevice deserializedDevice = Fixture.serializeAndDeserialize( device );

    assertEquals( 1, deserializedDevice.getDPI().x );
    assertEquals( 2, deserializedDevice.getDPI().y );
    assertEquals( 32, deserializedDevice.getDepth() );
  }

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    device = new TestDevice();
    Fixture.fakeNewRequest();
  }

  @Override
  protected void tearDown() throws Exception {
    device.dispose();
    Fixture.tearDown();
  }
}
