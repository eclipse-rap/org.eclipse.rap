/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.dnd;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.widgets.*;


public class DragSourceEffect_Test extends TestCase {
  
  public void testConstructorWithInvalidArgument() {
    try {
      new DragSourceEffect( null );
      fail( "Must not allow null-argument" );
    } catch( Exception e ) {
      // expected
    }
  }
  
  public void testGetControl() {
    Display display = new Display();
    Control control = new Shell( display );
    DragSourceEffect dragSourceEffect = new DragSourceEffect( control );
    assertSame( control, dragSourceEffect.getControl() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
