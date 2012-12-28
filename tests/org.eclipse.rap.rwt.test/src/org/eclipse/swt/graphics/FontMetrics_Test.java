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
package org.eclipse.swt.graphics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FontMetrics_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetHeight() {
    Display display = new Display();
    GC gc = new GC( display );
    gc.setFont( new Font( display, "Arial", 10, SWT.NORMAL ) );
    FontMetrics fontMetrics10 = gc.getFontMetrics();
    int height10 = fontMetrics10.getHeight();
    gc.setFont( new Font( display, "Arial", 30, SWT.NORMAL ) );
    FontMetrics fontMetrics30 = gc.getFontMetrics();
    int height30 = fontMetrics30.getHeight();
    assertTrue( height30 > height10 );
  }

  @Test
  public void testEquals() {
    Display display = new Display();
    GC gc = new GC( display );
    gc.setFont( new Font( display, "Arial", 10, SWT.NORMAL ) );
    FontMetrics fontMetrics = gc.getFontMetrics();
    FontMetrics equalFontMetrics = gc.getFontMetrics();
    assertTrue( equalFontMetrics.equals( fontMetrics ) );
    gc.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );
    FontMetrics differentFontMetrics = gc.getFontMetrics();
    assertFalse( differentFontMetrics.equals( fontMetrics ) );
  }

}
