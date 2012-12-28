/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Sash_Test {

  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testIsSerializable() throws Exception {
    Sash sash = new Sash( shell, SWT.NONE );
    sash.setSize( 1, 2 );

    Sash deserializedSash = Fixture.serializeAndDeserialize( sash );

    assertEquals( sash.getSize(), deserializedSash.getSize() );
  }

  @Test
  public void testAddSelectionListener() {
    Sash sash = new Sash( shell, SWT.NONE );

    sash.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( sash.isListening( SWT.Selection ) );
    assertTrue( sash.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    Sash sash = new Sash( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    sash.addSelectionListener( listener );

    sash.removeSelectionListener( listener );

    assertFalse( sash.isListening( SWT.Selection ) );
    assertFalse( sash.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testAddSelectionListenerWithNullArgument() {
    Sash sash = new Sash( shell, SWT.NONE );

    try {
      sash.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveSelectionListenerWithNullArgument() {
    Sash sash = new Sash( shell, SWT.NONE );

    try {
      sash.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

}
