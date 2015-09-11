/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.dnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.dnd.dragsourcekit.DragSourceLCA;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class DragSource_Test {

  @Rule
  public TestContext context = new TestContext();

  private Shell shell;
  private DragSource dragSource;

  @Before
  public void setUp() {
    Display display = new Display();
    shell = new Shell( display );
    dragSource = new DragSource( shell, DND.DROP_DEFAULT );
  }

  @Test
  public void testAddDragListener() {
    assertEquals( 0, dragSource.getDragListeners().length );

    dragSource.addDragListener( new DragSourceAdapter() );

    assertEquals( 1, dragSource.getDragListeners().length );
  }

  @Test
  public void testRemoveDragListener() {
    DragSourceAdapter listener = new DragSourceAdapter();
    dragSource.addDragListener( listener );

    dragSource.removeDragListener( listener );

    assertEquals( 0, dragSource.getDragListeners().length );
  }

  @Test
  public void testDisposeControl() {
    List list = new List( shell, SWT.None );
    DragSource dragSource = new DragSource( list, DND.DROP_MOVE );

    list.dispose();

    assertTrue( dragSource.isDisposed() );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( dragSource.getAdapter( WidgetLCA.class ) instanceof DragSourceLCA );
    assertSame( dragSource.getAdapter( WidgetLCA.class ), dragSource.getAdapter( WidgetLCA.class ) );
  }

}
