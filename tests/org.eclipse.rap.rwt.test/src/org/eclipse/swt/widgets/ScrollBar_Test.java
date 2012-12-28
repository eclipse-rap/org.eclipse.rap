/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ScrollBar_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testAddSelectionListener() {
    ScrollBar scrollBar = new ScrollBar( shell, SWT.NONE );

    scrollBar.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( scrollBar.isListening( SWT.Selection ) );
    assertTrue( scrollBar.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    ScrollBar scrollBar = new ScrollBar( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    scrollBar.addSelectionListener( listener );

    scrollBar.removeSelectionListener( listener );

    assertFalse( scrollBar.isListening( SWT.Selection ) );
    assertFalse( scrollBar.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testAddSelectionListenerWithNullArgument() {
    ScrollBar scrollBar = new ScrollBar( shell, SWT.NONE );

    try {
      scrollBar.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveSelectionListenerWithNullArgument() {
    ScrollBar scrollBar = new ScrollBar( shell, SWT.NONE );

    try {
      scrollBar.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

}
