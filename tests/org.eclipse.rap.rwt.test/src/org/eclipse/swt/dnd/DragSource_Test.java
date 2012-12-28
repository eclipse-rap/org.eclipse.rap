/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
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
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DragSource_Test {

  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testAddDragListener() {
    DragSource dragSource = new DragSource( shell, DND.DROP_DEFAULT );
    assertEquals( 0, dragSource.getDragListeners().length );

    dragSource.addDragListener( new DragSourceAdapter() );

    assertEquals( 1, dragSource.getDragListeners().length );
  }

  @Test
  public void testRemoveDragListener() {
    DragSource dragSource = new DragSource( shell, DND.DROP_DEFAULT );
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
}
