/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import junit.framework.TestCase;

public class Device_Test extends TestCase {

  public void testGetClientArea() {
    Device device = new Device(){};
    Rectangle clientArea = device.getClientArea();
    assertNotNull( clientArea );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), clientArea );
  }

  public void testGetBounds() {
    Device device = new Device(){};
    Rectangle bounds = device.getBounds();
    assertNotNull( bounds );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), bounds );
  }
}
