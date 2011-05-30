/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.widgets.Display;


public class Resource_Test extends TestCase {
  
  private static class TestResource extends Resource {
    TestResource( Device device ) {
      super( device );
    }
  }

  private Device device;
  
  public void testDispose() {
    Resource resource = new TestResource( device );
    assertFalse( resource.isDisposed() );
    resource.dispose();
    assertTrue( resource.isDisposed() );
  }
  
  public void testDisplayDispose() {
    Resource resource = new TestResource( device );
    device.dispose();
    assertFalse( resource.isDisposed() );
  }
  
  public void testGetDevice() {
    Resource resource = new TestResource( device );
    assertSame( device, resource.getDevice() );
  }

  public void testGetDeviceForFactoryResource() {
    Resource resource = new TestResource( null );
    assertSame( device, resource.getDevice() );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    device = new Display();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
