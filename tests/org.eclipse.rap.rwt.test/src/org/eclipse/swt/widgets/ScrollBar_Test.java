/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.internal.widgets.scrollbarkit.ScrollBarLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ScrollBar_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private ScrollBar scrollBar;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    scrollBar = new ScrollBar( shell, SWT.NONE );
  }

  @Test
  public void testAddSelectionListener() {
    scrollBar.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( scrollBar.isListening( SWT.Selection ) );
    assertTrue( scrollBar.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddSelectionListener_withNullArgument() {
    scrollBar.addSelectionListener( null );
  }

  @Test
  public void testRemoveSelectionListener() {
    SelectionListener listener = mock( SelectionListener.class );
    scrollBar.addSelectionListener( listener );

    scrollBar.removeSelectionListener( listener );

    assertFalse( scrollBar.isListening( SWT.Selection ) );
    assertFalse( scrollBar.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveSelectionListener_withNullArgument() {
    scrollBar.removeSelectionListener( null );
  }

  @Test
  public void testDispose_nullsOutHorizontalScrollBarInParent() {
    Scrollable parent = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    ScrollBar hScrollBar = parent.getHorizontalBar();

    hScrollBar.dispose();

    assertNull( parent.getHorizontalBar() );
    assertNotNull( parent.getVerticalBar() );
  }

  @Test
  public void testDispose_nullsOutVerticalScrollBarInParent() {
    Scrollable parent = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    ScrollBar vScrollBar = parent.getVerticalBar();

    vScrollBar.dispose();

    assertNotNull( parent.getHorizontalBar() );
    assertNull( parent.getVerticalBar() );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( scrollBar.getAdapter( WidgetLCA.class ) instanceof ScrollBarLCA );
    assertSame( scrollBar.getAdapter( WidgetLCA.class ), scrollBar.getAdapter( WidgetLCA.class ) );
  }

}
