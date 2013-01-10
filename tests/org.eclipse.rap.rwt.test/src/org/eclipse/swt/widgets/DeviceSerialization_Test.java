/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.Device;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DeviceSerialization_Test {

  private TestDevice device;

  @Before
  public void setUp() {
    Fixture.setUp();
    device = new TestDevice();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    device.dispose();
    Fixture.tearDown();
  }

  @Test
  public void testDeviceLockIsSerializable() throws Exception {
    TestDevice device = new TestDevice();

    TestDevice deserializedDevice = Fixture.serializeAndDeserialize( device );

    assertNotNull( deserializedDevice.getDeviceLock() );
  }

  @Test
  public void testDisposedIsSerializable() throws Exception {
    TestDevice device = new TestDevice();

    TestDevice deserializedDevice = Fixture.serializeAndDeserialize( device );

    assertFalse( deserializedDevice.isDisposed() );
  }

  private static class TestDevice extends Device {
    Object getDeviceLock() {
      return deviceLock;
    }
  }

}
