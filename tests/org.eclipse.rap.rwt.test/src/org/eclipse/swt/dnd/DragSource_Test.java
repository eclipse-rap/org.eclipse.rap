/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class DragSource_Test extends TestCase {
  
  public void testAddDragListener() {
    Display display = new Display();
    Shell shell = new Shell( display );
    DragSource dragSource = new DragSource( shell, DND.DROP_DEFAULT );
    assertEquals( 0, dragSource.getDragListeners().length );
    dragSource.addDragListener( new DragSourceAdapter() );
    assertEquals( 1, dragSource.getDragListeners().length );
  }

  public void testRemoveDragListener() {
    Display display = new Display();
    Shell shell = new Shell( display );
    DragSource dragSource = new DragSource( shell, DND.DROP_DEFAULT );
    DragSourceAdapter listener = new DragSourceAdapter();
    dragSource.addDragListener( listener );
    dragSource.removeDragListener( listener );
    assertEquals( 0, dragSource.getDragListeners().length );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
