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

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.graphics.Device;


public class DeviceSerialization_Test extends TestCase {
  
  private static class TestDevice extends Device {
    private static final long serialVersionUID = 1L;
    Object getDeviceLock() {
      return deviceLock;
    }
  }

  private TestDevice device;

  public void testDeviceLockIsSerializable() throws Exception {
    TestDevice device = new TestDevice();
    
    TestDevice deserializedDevice = serializeAndDeserialize( device );
    
    assertNotNull( deserializedDevice.getDeviceLock() );
  }
  
  public void testDisposedIsSerializable() throws Exception {
    TestDevice device = new TestDevice();
    
    TestDevice deserializedDevice = serializeAndDeserialize( device );
    
    assertFalse( deserializedDevice.isDisposed() );
  }
  
  public void testDPIAndColorDepthIsSerializable() throws Exception {
    Fixture.fakeRequestParam( "w1.dpi.x", "1" );
    Fixture.fakeRequestParam( "w1.dpi.y", "2" );
    Fixture.fakeRequestParam( "w1.colorDepth", "32" );
    TestDevice device = new TestDevice();

    TestDevice deserializedDevice = serializeAndDeserialize( device );
    
    assertEquals( 1, deserializedDevice.getDPI().x );
    assertEquals( 2, deserializedDevice.getDPI().y );
    assertEquals( 32, deserializedDevice.getDepth() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    device = new TestDevice();
  }

  protected void tearDown() throws Exception {
    device.dispose();
    Fixture.tearDown();
  }

  private static TestDevice serializeAndDeserialize( Device device ) throws Exception {
    byte[] bytes = Fixture.serialize( device );
    return ( TestDevice )Fixture.deserialize( bytes );
  }
}
