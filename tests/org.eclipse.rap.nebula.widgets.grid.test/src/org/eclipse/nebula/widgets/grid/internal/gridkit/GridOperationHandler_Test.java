/*******************************************************************************
 * Copyright (c) 2013, 2021 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal.gridkit;

import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridColumns;
import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridItems;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_COLLAPSE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_EXPAND;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SET_DATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


@SuppressWarnings( "restriction" )
public class GridOperationHandler_Test {

  private Display display;
  private Shell shell;
  private Grid grid;
  private GridOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    grid = new Grid( shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
    grid.setBounds( 0, 0, 100, 100 );
    handler = new GridOperationHandler( grid );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetSelection_single() {
    createGridItems( grid, 3, 3 );
    GridItem item = grid.getItem( 1 );

    JsonArray selection = new JsonArray().add( getId( item ) );
    handler.handleSet( new JsonObject().add( "selection", selection ) );

    GridItem[] selectedItems = grid.getSelection();
    assertEquals( 1, selectedItems.length );
    assertSame( item, selectedItems[ 0 ] );
  }

  @Test
  public void testHandleSetSelection_multi() {
    createGridItems( grid, 3, 3 );
    GridItem item1 = grid.getItem( 0 );
    GridItem item2 = grid.getItem( 2 );

    JsonArray selection = new JsonArray().add( getId( item1 ) ).add( getId( item2 ) );
    handler.handleSet( new JsonObject().add( "selection", selection ) );

    GridItem[] selectedItems = grid.getSelection();
    assertEquals( 2, selectedItems.length );
    assertSame( item1, selectedItems[ 0 ] );
    assertSame( item2, selectedItems[ 1 ] );
  }

  @Test
  public void testHandleSetCellSelection() {
    grid.setCellSelectionEnabled( true );
    createGridItems( grid, 3, 3 );
    createGridColumns( grid, 3, SWT.NONE );
    GridItem item1 = grid.getItem( 0 );
    GridItem item2 = grid.getItem( 2 );

    JsonArray cellSelection = new JsonArray()
      .add( new JsonArray().add( getId( item1 ) ).add( 1 ) )
      .add( new JsonArray().add( getId( item2 ) ).add( 2 ) );
    handler.handleSet( new JsonObject().add( "cellSelection", cellSelection ) );

    Point[] selectedCells = grid.getCellSelection();
    assertEquals( 2, selectedCells.length );
    assertEquals( new Point( 0, 0 ), selectedCells[ 0 ] );
    assertEquals( new Point( 1, 2 ), selectedCells[ 1 ] );
  }

  @Test
  public void testHandleSetCellSelection_withDisposedItem() {
    grid.setCellSelectionEnabled( true );
    createGridItems( grid, 3, 3 );
    createGridColumns( grid, 3, SWT.NONE );
    GridItem item1 = grid.getItem( 0 );
    GridItem item2 = grid.getItem( 2 );

    JsonArray cellSelection = new JsonArray()
      .add( new JsonArray().add( getId( item1 ) ).add( 1 ) )
      .add( new JsonArray().add( getId( item2 ) ).add( 2 ) );
    item2.dispose();
    handler.handleSet( new JsonObject().add( "cellSelection", cellSelection ) );

    Point[] selectedCells = grid.getCellSelection();
    assertEquals( 1, selectedCells.length );
    assertEquals( new Point( 0, 0 ), selectedCells[ 0 ] );
  }

  @Test
  public void testHandleSetSelection_disposedItem() {
    createGridItems( grid, 3, 3 );
    GridItem item1 = grid.getItem( 0 );
    item1.dispose();
    GridItem item2 = grid.getItem( 2 );

    JsonArray selection = new JsonArray().add( getId( item1 ) ).add( getId( item2 ) );
    handler.handleSet( new JsonObject().add( "selection", selection ) );

    GridItem[] selectedItems = grid.getSelection();
    assertEquals( 1, selectedItems.length );
    assertSame( item2, selectedItems[ 0 ] );
  }

  @Test
  public void testHandleSetScrollLeft() {
    createGridItems( grid, 3, 3 );
    GridItem item = grid.getItem( 0 );
    item.setText( "very long text that makes horizontal bar visible" );

    handler.handleSet( new JsonObject().add( "scrollLeft", 1 ) );

    assertEquals( 1, grid.getHorizontalBar().getSelection() );
  }

  @Test
  public void testHandleSetTopItemIndex() {
    GridItem[] items = createGridItems( grid, 10, 3 );
    items[ 4 ].setExpanded( true );

    handler.handleSet( new JsonObject().add( "topItemIndex", 3 ) );

    assertEquals( 3, grid.getVerticalBar().getSelection() );
    assertEquals( 6, grid.getTopIndex() );
  }

  @Test
  public void testHandleSetFocusItem() {
    GridItem[] items = createGridItems( grid, 3, 1 );

    handler.handleSet( new JsonObject().add( "focusItem", getId( items[ 2 ] ) ) );

    assertSame( items[ 2 ], grid.getFocusItem() );
  }

  @Test
  public void testHandleSetFocusColumn() {
    grid.setCellSelectionEnabled( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );

    handler.handleSet( new JsonObject().add( "focusCell", 3 ) );

    assertSame( columns[ 2 ], grid.getFocusColumn() );
  }

  @Test
  public void testHandleCallRenderToolTipText() {
    new GridColumn( grid, SWT.NONE );
    GridItem item = new GridItem( grid, SWT.NONE );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( grid );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      @Override
      public void getToolTipText( Item item, int columnIndex ) {
        StringBuilder buffer = new StringBuilder();
        buffer.append( getId( item ) );
        buffer.append( "," );
        buffer.append( columnIndex );
        adapter.setCellToolTipText( buffer.toString() );
      }
    } );

    JsonObject properties = new JsonObject().add( "item", getId( item ) ).add( "column", 1 );
    handler.handleCall( "renderToolTipText", properties );

    assertEquals( getId( item ) + ",0", CellToolTipUtil.getAdapter( grid ).getCellToolTipText() );
  }

  @Test
  public void testHandleNotifySelection() {
    Grid spyGrid = spy( grid );
    handler = new GridOperationHandler( spyGrid );
    GridItem item = new GridItem( spyGrid, SWT.NONE );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "item", getId( item ) );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyGrid ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertEquals( item, event.item );
  }

  @Test
  public void testHandleNotifySelection_withDetail_hyperlink() {
    Grid spyGrid = spy( grid );
    handler = new GridOperationHandler( spyGrid );
    GridItem item = new GridItem( spyGrid, SWT.NONE );

    JsonObject properties = new JsonObject()
      .add( "item", getId( item ) )
      .add( "detail", "hyperlink" )
      .add( "text", "foo" );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyGrid ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( item, event.item );
    assertEquals( RWT.HYPERLINK, event.detail );
    assertEquals( "foo", event.text );
  }

  @Test
  public void testHandleNotifySelection_withDetail_check() {
    Grid spyGrid = spy( grid );
    handler = new GridOperationHandler( spyGrid );
    GridItem item = new GridItem( spyGrid, SWT.NONE );

    JsonObject properties = new JsonObject()
      .add( "item", getId( item ) )
      .add( "detail", "check" )
      .add( "index", 3 );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyGrid ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( item, event.item );
    assertEquals( SWT.CHECK, event.detail );
    assertEquals( 3, event.index );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    Grid spyGrid = spy( grid );
    handler = new GridOperationHandler( spyGrid );
    GridItem item = new GridItem( spyGrid, SWT.NONE );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "item", getId( item ) );
    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyGrid ).notifyListeners( eq( SWT.DefaultSelection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertEquals( item, event.item );
  }

  @Test
  public void testHandleNotifyExpand() {
    Grid spyGrid = spy( grid );
    handler = new GridOperationHandler( spyGrid );
    GridItem item = new GridItem( spyGrid, SWT.NONE );

    JsonObject properties = new JsonObject().add( "item", getId( item ) );
    handler.handleNotify( EVENT_EXPAND, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyGrid ).notifyListeners( eq( SWT.Expand ), captor.capture() );
    assertEquals( item, captor.getValue().item );
  }

  @Test
  public void testHandleNotifyExpand_withDisposedItem() {
    Grid spyGrid = spy( grid );
    handler = new GridOperationHandler( spyGrid );
    GridItem item = new GridItem( spyGrid, SWT.NONE );
    item.dispose();

    JsonObject properties = new JsonObject().add( "item", getId( item ) );
    handler.handleNotify( EVENT_EXPAND, properties );

    verify( spyGrid, never() ).notifyListeners( eq( SWT.Expand ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyCollapse() {
    Grid spyGrid = spy( grid );
    handler = new GridOperationHandler( spyGrid );
    GridItem item = new GridItem( spyGrid, SWT.NONE );

    JsonObject properties = new JsonObject().add( "item", getId( item ) );
    handler.handleNotify( EVENT_COLLAPSE, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( spyGrid ).notifyListeners( eq( SWT.Collapse ), captor.capture() );
    assertEquals( item, captor.getValue().item );
  }

  @Test
  public void testHandleNotifyCollapse_withDisposedItem() {
    Grid spyGrid = spy( grid );
    handler = new GridOperationHandler( spyGrid );
    GridItem item = new GridItem( spyGrid, SWT.NONE );
    item.dispose();

    JsonObject properties = new JsonObject().add( "item", getId( item ) );
    handler.handleNotify( EVENT_COLLAPSE, properties );

    verify( spyGrid, never() ).notifyListeners( eq( SWT.Collapse ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifySetData() {
    Grid spyGrid = spy( grid );
    handler = new GridOperationHandler( spyGrid );

    handler.handleNotify( EVENT_SET_DATA, new JsonObject() );

    verify( spyGrid, never() ).notifyListeners( eq( SWT.SetData ), any( Event.class ) );
  }

}
