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
package org.eclipse.swt.widgets;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;


public class Sash_Test extends TestCase {

  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testIsSerializable() throws Exception {
    Sash sash = new Sash( shell, SWT.NONE );
    sash.setSize( 1, 2 );
    
    Sash deserializedSash = Fixture.serializeAndDeserialize( sash );
    
    assertEquals( sash.getSize(), deserializedSash.getSize() );
  }

  public void testAddSelectionListener() {
    Sash sash = new Sash( shell, SWT.NONE );

    sash.addSelectionListener( mock( SelectionListener.class ) );
    
    assertTrue( sash.isListening( SWT.Selection ) );
    assertTrue( sash.isListening( SWT.DefaultSelection ) );
  }
  
  public void testRemoveSelectionListener() {
    Sash sash = new Sash( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    sash.addSelectionListener( listener );

    sash.removeSelectionListener( listener );
    
    assertFalse( sash.isListening( SWT.Selection ) );
    assertFalse( sash.isListening( SWT.DefaultSelection ) );
  }

  public void testAddSelectionListenerWithNullArgument() {
    Sash sash = new Sash( shell, SWT.NONE );
    
    try {
      sash.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRemoveSelectionListenerWithNullArgument() {
    Sash sash = new Sash( shell, SWT.NONE );
    
    try {
      sash.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }
}
