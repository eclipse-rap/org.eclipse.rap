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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Resource_Test {

  private Device device;

  @Before
  public void setUp() {
    Fixture.setUp();
    device = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testDispose() {
    Resource resource = new TestResource( device );
    assertFalse( resource.isDisposed() );
    resource.dispose();
    assertTrue( resource.isDisposed() );
  }

  @Test
  public void testDisplayDispose() {
    Resource resource = new TestResource( device );
    device.dispose();
    assertFalse( resource.isDisposed() );
  }

  @Test
  public void testGetDevice() {
    Resource resource = new TestResource( device );
    assertSame( device, resource.getDevice() );
  }

  @Test
  public void testGetDeviceForFactoryResource() {
    Resource resource = new TestResource( null );
    assertSame( device, resource.getDevice() );
  }

  private static class TestResource extends Resource {
    private static final long serialVersionUID = 1L;
    TestResource( Device device ) {
      super( device );
    }
  }

}
