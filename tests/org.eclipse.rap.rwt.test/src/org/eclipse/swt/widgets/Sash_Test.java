/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.internal.widgets.sashkit.SashLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class Sash_Test {

  @Rule
  public TestContext context = new TestContext();

  private Composite shell;
  private Sash sash;

  @Before
  public void setUp() {
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
    sash = new Sash( shell, SWT.NONE );
  }

  @Test
  public void testIsSerializable() throws Exception {
    sash.setSize( 1, 2 );

    Sash deserializedSash = serializeAndDeserialize( sash );

    assertEquals( sash.getSize(), deserializedSash.getSize() );
  }

  @Test
  public void testAddSelectionListener() {
    sash.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( sash.isListening( SWT.Selection ) );
    assertTrue( sash.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddSelectionListener_withNullArgument() {
    sash.addSelectionListener( null );
  }

  @Test
  public void testRemoveSelectionListener() {
    SelectionListener listener = mock( SelectionListener.class );
    sash.addSelectionListener( listener );

    sash.removeSelectionListener( listener );

    assertFalse( sash.isListening( SWT.Selection ) );
    assertFalse( sash.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveSelectionListener_withNullArgument() {
    sash.removeSelectionListener( null );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( sash.getAdapter( WidgetLCA.class ) instanceof SashLCA );
    assertSame( sash.getAdapter( WidgetLCA.class ), sash.getAdapter( WidgetLCA.class ) );
  }

}
