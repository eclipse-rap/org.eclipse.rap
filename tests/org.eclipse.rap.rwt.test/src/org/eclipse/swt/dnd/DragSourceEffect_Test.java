/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.dnd;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DragSourceEffect_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testConstructorWithInvalidArgument() {
    try {
      new DragSourceEffect( null );
      fail( "Must not allow null-argument" );
    } catch( Exception e ) {
      // expected
    }
  }

  @Test
  public void testGetControl() {
    Display display = new Display();
    Control control = new Shell( display );
    DragSourceEffect dragSourceEffect = new DragSourceEffect( control );
    assertSame( control, dragSourceEffect.getControl() );
  }

}
