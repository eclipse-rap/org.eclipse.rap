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
package org.eclipse.swt.internal.widgets.tablecolumnkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.eclipse.swt.internal.widgets.tablecolumnkit.TableColumnOperationHandler.moveColumn;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class TableColumnOperationHandler_Test {

  private Display display;
  private Shell shell;
  private Table table;
  private TableColumn column;
  private TableColumnOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    table = new Table( shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
    table.setBounds( 0, 0, 100, 100 );
    column = new TableColumn( table, SWT.NONE );
    column.setWidth( 50 );
    handler = new TableColumnOperationHandler( column );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleNotifySelection() {
    TableColumn spyColumn = spy( column );
    handler = new TableColumnOperationHandler( spyColumn );
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
    TableColumn spyColumn = spy( column );
    handler = new TableColumnOperationHandler( spyColumn );
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
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setWidth( 100 );
    JsonObject properties = new JsonObject().add( "left", 123 );

    handler.handleCall( "move", properties );

    assertArrayEquals( new int[] { 1, 0 }, table.getColumnOrder() );
  }

  @Test
  public void testMoveColumn() {
    column.setText( "Col 0" );
    column.setWidth( 10 );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setText( "Col 1" );
    column1.setWidth( 20 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setText( "Col 2" );
    column2.setWidth( 30 );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60 (as created)
    // Move Col 1 over Col 0 (left half), thereafter order should be:
    // Col 1, Col 0, Col 2
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    moveColumn( column1, 3 );
    int[] columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    // Current order: Col 1: 0..20, Col 0: 21..30, Col 2: 31..60
    // Move Col 1 over Col 0 (right half), thereafter order should be:
    // Col 0, Col 1, Col 2
    table.setColumnOrder( new int[]{
      1, 0, 2
    } );
    moveColumn( column1, 27 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 over Col 1 (left half), thereafter order should be:
    // Col 0, Col 2, Col 1
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    moveColumn( column2, 13 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 over Col 1 (right half), thereafter order should be:
    // Col 2, Col 0, Col 1
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    moveColumn( column2, 3 );
    columnOrder = table.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 2 way left of Col 0, thereafter order should be:
    // Col 2, Col 0, Col 1
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    moveColumn( column2, -30 );
    columnOrder = table.getColumnOrder();
    assertEquals( 2, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 1, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 0 way right of Col 2, thereafter order should be:
    // Col 1, Col 2, Col 0
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    moveColumn( column, 100 );
    columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 2, columnOrder[ 1 ] );
    assertEquals( 0, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 1 onto itself (left half), order should stay unchanged:
    // Col 1, Col 2, Col 0
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    moveColumn( column1, 13 );
    columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    // Current order: Col 0: 0..10, Col 1: 11..30, Col 2: 31..60
    // Move Col 0 over Col 2 (left half), order should be:
    // Col 1, Col 0, Col 2
    table.setColumnOrder( new int[]{
      0, 1, 2
    } );
    moveColumn( column, 33 );
    columnOrder = table.getColumnOrder();
    assertEquals( 1, columnOrder[ 0 ] );
    assertEquals( 0, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
  }

  @Test
  public void testMoveColumn_withFixedColumnTarget() {
    table = createFixedColumnsTable( shell );
    table.setSize( 200, 200 );
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 80 );
    TableColumn column3 = table.getColumn( 3 );

    moveColumn( column3, 105 );

    int[] columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
  }

  @Test
  public void testMoveColumn_withFixedColumnSource() {
    table = createFixedColumnsTable( shell );
    table.setSize( 200, 200 );
    TableColumn column0 = table.getColumn( 0 );

    moveColumn( column0, 105 );

    int[] columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
  }

  @Test
  public void testMoveColumn_withFixedColumnRightHalfTarget() {
    table = createFixedColumnsTable( shell );
    table.setSize( 200, 200 );
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 100 );
    TableColumn column3 = table.getColumn( 3 );

    moveColumn( column3, 145 );

    int[] columnOrder = table.getColumnOrder();
    assertEquals( 0, columnOrder[ 0 ] );
    assertEquals( 1, columnOrder[ 1 ] );
    assertEquals( 2, columnOrder[ 2 ] );
    assertEquals( 3, columnOrder[ 3 ] );
  }

  private static Table createFixedColumnsTable( Composite parent ) {
    Table tree = new Table( parent, SWT.NONE );
    tree.setData( RWT.FIXED_COLUMNS, new Integer( 1 ) );
    for( int i = 0; i < 10; i++ ) {
      TableColumn column = new TableColumn( tree, SWT.NONE );
      column.setWidth( 50 );
      column.setText( "Column " + i );
    }
    return tree;
  }

}
