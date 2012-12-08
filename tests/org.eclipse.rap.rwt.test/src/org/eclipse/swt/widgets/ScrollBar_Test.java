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

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;


public class ScrollBar_Test extends TestCase {

  private Display display;
  private Shell shell;

  public void testAddSelectionListener() {
    ScrollBar scrollBar = new ScrollBar( shell, SWT.NONE );

    scrollBar.addSelectionListener( mock( SelectionListener.class ) );
    
    assertTrue( scrollBar.isListening( SWT.Selection ) );
    assertTrue( scrollBar.isListening( SWT.DefaultSelection ) );
  }
  
  public void testRemoveSelectionListener() {
    ScrollBar scrollBar = new ScrollBar( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    scrollBar.addSelectionListener( listener );

    scrollBar.removeSelectionListener( listener );
    
    assertFalse( scrollBar.isListening( SWT.Selection ) );
    assertFalse( scrollBar.isListening( SWT.DefaultSelection ) );
  }

  public void testAddSelectionListenerWithNullArgument() {
    ScrollBar scrollBar = new ScrollBar( shell, SWT.NONE );
    
    try {
      scrollBar.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRemoveSelectionListenerWithNullArgument() {
    ScrollBar scrollBar = new ScrollBar( shell, SWT.NONE );
    
    try {
      scrollBar.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }
  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
