/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal.gridcolumnkit;

import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridColumns;
import static org.eclipse.nebula.widgets.grid.internal.gridcolumnkit.GridColumnOperationHandler.moveColumn;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


@SuppressWarnings( {
  "restriction", "deprecation"
} )
public class GridColumnOperationHandler_Test {

  private Display display;
  private Shell shell;
  private Grid grid;
  private GridColumn column;
  private GridColumnOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    grid = new Grid( shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
    grid.setBounds( 0, 0, 100, 100 );
    column = new GridColumn( grid, SWT.NONE );
    column.setWidth( 50 );
    handler = new GridColumnOperationHandler( column );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleNotifySelection() {
    GridColumn spyColumn = spy( column );
    handler = new GridColumnOperationHandler( spyColumn );
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true );

    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyColumn ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    GridColumn spyColumn = spy( column );
    handler = new GridColumnOperationHandler( spyColumn );
    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true );

    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyColumn ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
  }

  @Test
  public void testHandleCallResize() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    JsonObject properties = new JsonObject().add( "width", 123 );

    handler.handleCall( "resize", properties );

    assertEquals( 123, column.getWidth() );
  }

  @Test
  public void testHandleCallMove() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    GridColumn column1 = new GridColumn( grid, SWT.NONE );
    column1.setWidth( 100 );
    JsonObject properties = new JsonObject().add( "left", 123 );

    handler.handleCall( "move", properties );

    assertArrayEquals( new int[] { 1, 0 }, grid.getColumnOrder() );
  }

  @Test
  public void testMoveColumn_1() {
    column.dispose();
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 0 ].setWidth( 10 );
    columns[ 1 ].setWidth( 20 );
    columns[ 2 ].setWidth( 30 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60 (as created)
    // Move Col 1 over Col 0 (left half), thereafter order should be:
    // Col 1, Col 0, Col 2
    grid.setColumnOrder( new int[]{
      0, 1, 2
    } );

    moveColumn( columns[ 1 ], 3 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
  }

  @Test
  public void testMoveColumn_2() {
    column.dispose();
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 0 ].setWidth( 10 );
    columns[ 1 ].setWidth( 20 );
    columns[ 2 ].setWidth( 30 );
    // Current order: Col 1: 0..20, Col 0: 21..30, Col 2: 31..60
    // Move Col 1 over Col 0 (right half), thereafter order should be:
    // Col 0, Col 1, Col 2
    grid.setColumnOrder( new int[]{
      1, 0, 2
    } );

    moveColumn( columns[ 1 ], 27 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
  }

  @Test
  public void testMoveColumn_3() {
    column.dispose();
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 0 ].setWidth( 10 );
    columns[ 1 ].setWidth( 20 );
    columns[ 2 ].setWidth( 30 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 over Col 1 (left half), thereafter order should be:
    // Col 0, Col 2, Col 1
    grid.setColumnOrder( new int[]{
      0, 1, 2
    } );

    moveColumn( columns[ 2 ], 13 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
  }

  @Test
  public void testMoveColumn_4() {
    column.dispose();
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 0 ].setWidth( 10 );
    columns[ 1 ].setWidth( 20 );
    columns[ 2 ].setWidth( 30 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 over Col 1 (right half), thereafter order should be:
    // Col 2, Col 0, Col 1
    grid.setColumnOrder( new int[]{
      0, 1, 2
    } );

    moveColumn( columns[ 2 ], 3 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
  }

  @Test
  public void testMoveColumn_5() {
    column.dispose();
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 0 ].setWidth( 10 );
    columns[ 1 ].setWidth( 20 );
    columns[ 2 ].setWidth( 30 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 way left of Col 0, thereafter order should be:
    // Col 2, Col 0, Col 1
    grid.setColumnOrder( new int[]{
      0, 1, 2
    } );

    moveColumn( columns[ 2 ], -30 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
  }

  @Test
  public void testMoveColumn_6() {
    column.dispose();
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 0 ].setWidth( 10 );
    columns[ 1 ].setWidth( 20 );
    columns[ 2 ].setWidth( 30 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 0 way right of Col 2, thereafter order should be:
    // Col 1, Col 2, Col 0
    grid.setColumnOrder( new int[]{
      0, 1, 2
    } );

    moveColumn( columns[ 0 ], 100 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 0, columnOrder[ 2 ] );
  }

  @Test
  public void testMoveColumn_7() {
    column.dispose();
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 0 ].setWidth( 10 );
    columns[ 1 ].setWidth( 20 );
    columns[ 2 ].setWidth( 30 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 1 onto itself (left half), order should stay unchanged:
    // Col 1, Col 2, Col 0
    grid.setColumnOrder( new int[]{
      0, 1, 2
    } );

    moveColumn( columns[ 1 ], 13 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
  }

  @Test
  public void testMoveColumn_8() {
    column.dispose();
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 0 ].setWidth( 10 );
    columns[ 1 ].setWidth( 20 );
    columns[ 2 ].setWidth( 30 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 0 over Col 2 (left half), order should be:
    // Col 1, Col 0, Col 2
    grid.setColumnOrder( new int[]{
      0, 1, 2
    } );

    moveColumn( columns[ 0 ], 33 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
  }

  @Test
  public void testMoveColumn_MoveIntoGroup() {
    column.dispose();
    createGridColumns( grid, 2, SWT.NONE );
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    createGridColumns( group, 2, SWT.NONE );
    grid.getColumn( 0 ).setWidth( 10 );
    grid.getColumn( 1 ).setWidth( 20 );
    grid.getColumn( 2 ).setWidth( 30 );
    grid.getColumn( 3 ).setWidth( 40 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60, Col 3: 61..100
    // Move Col 0 between Col 2 and Col 3 (inside the group)
    // Movement should be ignored
    grid.setColumnOrder( new int[]{
      0, 1, 2, 3
    } );

    moveColumn( grid.getColumn( 0 ), 55 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
  }

  @Test
  public void testMoveColumn_MoveOutsideGroup() {
    column.dispose();
    createGridColumns( grid, 2, SWT.NONE );
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    createGridColumns( group, 2, SWT.NONE );
    grid.getColumn( 0 ).setWidth( 10 );
    grid.getColumn( 1 ).setWidth( 20 );
    grid.getColumn( 2 ).setWidth( 30 );
    grid.getColumn( 3 ).setWidth( 40 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60, Col 3: 61..100
    // Move Col 3 between Col 0 and Col 1 (outside the group)
    // Movement should be ignored
    grid.setColumnOrder( new int[]{
      0, 1, 2, 3
    } );

    moveColumn( grid.getColumn( 3 ), 15 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
  }

  @Test
  public void testMoveColumn_WithInvisibleColumns() {
    column.dispose();
    GridColumn[] columns = createGridColumns( grid, 4, SWT.NONE );
    columns[ 0 ].setWidth( 10 );
    columns[ 1 ].setWidth( 20 );
    columns[ 2 ].setWidth( 30 );
    columns[ 2 ].setVisible( false );
    columns[ 3 ].setWidth( 40 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: not visible, Col 3: 31..70
    // Move Col 0 over Col 3 (left half), order should be:
    // Col 1, Col 2, Col 0, Col 3
    grid.setColumnOrder( new int[]{
      0, 1, 2, 3
    } );

    moveColumn( grid.getColumn( 0 ), 33 );

    int[] columnOrder = grid.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 0, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
  }

}
