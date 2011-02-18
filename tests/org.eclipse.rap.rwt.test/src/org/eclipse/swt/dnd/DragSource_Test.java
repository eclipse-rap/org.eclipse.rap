/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.dnd;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.widgets.*;


public class DragSource_Test extends TestCase {
  
  private Control control;

  public void testAddDragListener() {
    DragSource dragSource = new DragSource( control, DND.DROP_DEFAULT );
    assertEquals( 0, dragSource.getDragListeners().length );
    dragSource.addDragListener( new DragSourceAdapter() );
    assertEquals( 1, dragSource.getDragListeners().length );
  }

  public void testRemoveDragListener() {
    DragSource dragSource = new DragSource( control, DND.DROP_DEFAULT );
    DragSourceAdapter listener = new DragSourceAdapter();
    dragSource.addDragListener( listener );
    dragSource.removeDragListener( listener );
    assertEquals( 0, dragSource.getDragListeners().length );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    control = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
