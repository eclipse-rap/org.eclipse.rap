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
package org.eclipse.swt.accessibility;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.widgets.*;


public class Accessible_Test extends TestCase {

  public void testIsSerializable() throws Exception {
    Display display = new Display();
    Control control = new Shell( display );
    Accessible accessible = new Accessible( control );

    Accessible deserializedAccessible = Fixture.serializeAndDeserialize( accessible );
    
    assertTrue( deserializedAccessible.getControl() instanceof Shell );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
