/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ColorDialog_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRGB() {
    RGB rgb = new RGB( 255, 0, 0 );
    ColorDialog dialog = new ColorDialog( shell );
    dialog.setRGB( rgb );
    assertEquals( rgb, dialog.getRGB() );
  }

  @Test
  public void testInitialRGBValue() {
    ColorDialog dialog = new ColorDialog( shell );
    assertNull( dialog.getRGB() );
  }

  @Test
  public void testOpen_JEE_COMPATIBILITY() {
    // Activate SimpleLifeCycle
    getApplicationContext().getLifeCycleFactory().deactivate();
    getApplicationContext().getLifeCycleFactory().activate();
    RGB rgb = new RGB( 255, 0, 0 );
    ColorDialog dialog = new ColorDialog( shell );
    dialog.setRGB( rgb );

    try {
      dialog.open();
      fail();
    } catch( UnsupportedOperationException expected ) {
      assertEquals( "Method not supported in JEE_COMPATIBILITY mode.", expected.getMessage() );
    }
  }

}
