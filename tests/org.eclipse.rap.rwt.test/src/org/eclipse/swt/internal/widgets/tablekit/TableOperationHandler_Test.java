/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tablekit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SET_DATA;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class TableOperationHandler_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private Table table;
  private TableOperationHandler handler;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    table = new Table( shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
    table.setBounds( 0, 0, 100, 100 );
    handler = new TableOperationHandler( table );
  }

  @Test
  public void testHandleSetSelection_single() {
    createTableItems( table, 3 );
    TableItem item = table.getItem( 1 );

    JsonArray selection = new JsonArray().add( getId( item ) );
    handler.handleSet( new JsonObject().add( "selection", selection ) );

    assertArrayEquals( new TableItem[] { item }, table.getSelection() );
  }

  @Test
  public void testHandleSetSelection_multi() {
    createTableItems( table, 3 );
    TableItem item1 = table.getItem( 0 );
    TableItem item2 = table.getItem( 2 );

    JsonArray selection = new JsonArray().add( getId( item1 ) ).add( getId( item2 ) );
    handler.handleSet( new JsonObject().add( "selection", selection ) );

    assertArrayEquals( new TableItem[] { item2, item1 }, table.getSelection() );
  }

  @Test
  public void testHandleSetSelection_unresolvedItem() {
    table = new Table( shell, SWT.MULTI | SWT.VIRTUAL );
    handler = new TableOperationHandler( table );
    table.setItemCount( 3 );
    TableItem item = table.getItem( 0 );
    item.setText( "Item 1" );

    JsonArray selection = new JsonArray().add( getId( item ) ).add( getId( table ) + "#2" );
    handler.handleSet( new JsonObject().add( "selection", selection ) );

    assertArrayEquals( new int[] { 2, 0 }, table.getSelectionIndices() );
    assertTrue( isItemVirtual( table, 2 ) );
  }

  @Test
  public void testHandleSetSelection_disposedItem() {
    createTableItems( table, 3 );
    TableItem item1 = table.getItem( 0 );
    TableItem item2 = table.getItem( 2 );
    item1.dispose();

    JsonArray selection = new JsonArray().add( getId( item1 ) ).add( getId( item2 ) );
    handler.handleSet( new JsonObject().add( "selection", selection ) );

    assertArrayEquals( new TableItem[] { item2 }, table.getSelection() );
  }

  @Test
  public void testHandleSetScrollLeft() {
    createTableItems( table, 1 );
    TableItem item = table.getItem( 0 );
    item.setText( "very long text that makes horizontal bar visible" );

    handler.handleSet( new JsonObject().add( "scrollLeft", 1 ) );

    assertEquals( 1, getTableAdapter( table ).getLeftOffset() );
  }

  @Test
  public void testHandleSetScrollLeft_setsHorizontalScrollBarSelection() {
    createTableItems( table, 1 );
    TableItem item = table.getItem( 0 );
    item.setText( "very long text that makes horizontal bar visible" );

    handler.handleSet( new JsonObject().add( "scrollLeft", 1 ) );

    assertEquals( 1, table.getHorizontalBar().getSelection() );
  }

  @Test
  public void testHandleSetTopItemIndex() {
    createTableItems( table, 10 );

    handler.handleSet( new JsonObject().add( "topItemIndex", 1 ) );

    assertEquals( 1, table.getTopIndex() );
  }

  @Test
  public void testHandleSetTopItemIndex_setsVerticalScrollBarSelection() {
    createTableItems( table, 10 );

    handler.handleSet( new JsonObject().add( "topItemIndex", 1 ) );

    assertEquals( table.getItemHeight(), table.getVerticalBar().getSelection() );
  }

  @Test
  public void testHandleSetFocusItem() {
    createTableItems( table, 3 );
    TableItem item = table.getItem( 1 );

    handler.handleSet( new JsonObject().add( "focusItem", getId( item ) ) );

    assertEquals( 1, getTableAdapter( table ).getFocusIndex() );
  }

  @Test
  public void testHandleSetFocusItem_unresolvedItem() {
    table = new Table( shell, SWT.MULTI | SWT.VIRTUAL );
    handler = new TableOperationHandler( table );
    table.setItemCount( 3 );

    handler.handleSet( new JsonObject().add( "focusItem", getId( table ) + "#2" ) );

    assertEquals( 2, getTableAdapter( table ).getFocusIndex() );
  }

  @Test
  public void testHandleSetFocusItem_disposedItem() {
    createTableItems( table, 3 );
    TableItem item = table.getItem( 1 );
    item.dispose();

    handler.handleSet( new JsonObject().add( "focusItem", getId( item ) ) );

    assertEquals( -1, getTableAdapter( table ).getFocusIndex() );
  }

  @Test
  public void testHandleCallRenderToolTipText() {
    TableItem item = new TableItem( table, SWT.NONE );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( Item item, int columnIndex ) {
        StringBuilder buffer = new StringBuilder();
        buffer.append( getId( item ) );
        buffer.append( "," );
        buffer.append( columnIndex );
        adapter.setCellToolTipText( buffer.toString() );
      }
    } );

    JsonObject properties = new JsonObject().add( "item", getId( item ) ).add( "column", 0 );
    handler.handleCall( "renderToolTipText", properties );

    assertEquals( getId( item ) + ",0", CellToolTipUtil.getAdapter( table ).getCellToolTipText() );
  }

  @Test
  public void testHandleCallRenderToolTipText_disposedItem() {
    TableItem item = new TableItem( table, SWT.NONE );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( Item item, int columnIndex ) {
        StringBuilder buffer = new StringBuilder();
        buffer.append( getId( item ) );
        buffer.append( "," );
        buffer.append( columnIndex );
        adapter.setCellToolTipText( buffer.toString() );
      }
    } );
    item.dispose();

    JsonObject properties = new JsonObject().add( "item", getId( item ) ).add( "column", 0 );
    handler.handleCall( "renderToolTipText", properties );

    assertNull( CellToolTipUtil.getAdapter( table ).getCellToolTipText() );
  }

  @Test
  public void testHandleCallRenderToolTipText_invalidColumn() {
    TableItem item = new TableItem( table, SWT.NONE );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
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

    assertNull( CellToolTipUtil.getAdapter( table ).getCellToolTipText() );
  }

  @Test
  public void testHandleNotifySelection() {
    handler = new TableOperationHandler( table );
    TableItem item = new TableItem( table, SWT.NONE );
    Listener listener = mock( Listener.class );
    table.addListener( SWT.Selection, listener );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "item", getId( item ) );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertEquals( item, event.item );
  }

  @Test
  public void testHandleNotifySelection_unresolvedItem() {
    table = new Table( shell, SWT.MULTI | SWT.VIRTUAL );
    handler = new TableOperationHandler( table );
    table.setItemCount( 3 );
    Listener listener = mock( Listener.class );
    table.addListener( SWT.Selection, listener );

    JsonObject properties = new JsonObject().add( "item", getId( table ) + "#2" );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( table.getItem( 2 ), event.item );
  }

  @Test
  public void testHandleNotifySelection_withDetail_hyperlink() {
    handler = new TableOperationHandler( table );
    TableItem item = new TableItem( table, SWT.NONE );
    Listener listener = mock( Listener.class );
    table.addListener( SWT.Selection, listener );

    JsonObject properties = new JsonObject()
      .add( "item", getId( item ) )
      .add( "detail", "hyperlink" )
      .add( "text", "foo" );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( item, event.item );
    assertEquals( RWT.HYPERLINK, event.detail );
    assertEquals( "foo", event.text );
  }

  @Test
  public void testHandleNotifySelection_withDetail_check() {
    handler = new TableOperationHandler( table );
    TableItem item = new TableItem( table, SWT.NONE );
    Listener listener = mock( Listener.class );
    table.addListener( SWT.Selection, listener );

    JsonObject properties = new JsonObject()
      .add( "item", getId( item ) )
      .add( "detail", "check" );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( item, event.item );
    assertEquals( SWT.CHECK, event.detail );
  }

  @Test
  public void testHandleNotifyDefaultSelection() {
    handler = new TableOperationHandler( table );
    TableItem item = new TableItem( table, SWT.NONE );
    Listener listener = mock( Listener.class );
    table.addListener( SWT.DefaultSelection, listener );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "item", getId( item ) );
    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertEquals( item, event.item );
  }

  @Test
  public void testHandleNotifyDefaultSelection_disposedItem_withoutFocusItem() {
    handler = new TableOperationHandler( table );
    createTableItems( table, 3 );
    TableItem disposedItem = table.getItem( 2 );
    disposedItem.dispose();
    Listener listener = mock( Listener.class );
    table.addListener( SWT.DefaultSelection, listener );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "item", getId( disposedItem ) );
    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertNull( event.item );
  }

  @Test
  public void testHandleNotifyDefaultSelection_disposedItem_withFocusItem() {
    handler = new TableOperationHandler( table );
    createTableItems( table, 3 );
    TableItem disposedItem = table.getItem( 2 );
    disposedItem.dispose();
    TableItem item = table.getItem( 0 );
    getTableAdapter( table ).setFocusIndex( 0 );
    Listener listener = mock( Listener.class );
    table.addListener( SWT.DefaultSelection, listener );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "item", getId( disposedItem ) );
    handler.handleNotify( EVENT_DEFAULT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertSame( item, event.item );
  }

  @Test
  public void testHandleNotifyMouseDown_skippedOnHeader() {
    table.setHeaderVisible( true );
    handler = new TableOperationHandler( table );
    Listener listener = mock( Listener.class );
    table.addListener( SWT.MouseDown, listener );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "button", 1 )
      .add( "x", 2 )
      .add( "y", 10 )
      .add( "time", 4 );
    handler.handleNotify( EVENT_MOUSE_DOWN, properties );

    verify( listener, never() ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testHandleNotifySetData() {
    table = mock( Table.class );
    handler = new TableOperationHandler( table );

    handler.handleNotify( EVENT_SET_DATA, new JsonObject() );

    verify( table, never() ).notifyListeners( eq( SWT.SetData ), any( Event.class ) );
  }

  private static void createTableItems( Table table, int number ) {
    for( int i = 0; i < number; i++ ) {
      TableItem item = new TableItem( table, SWT.NONE );
      item.setText( "item " + i );
    }
  }

  private static boolean isItemVirtual( Table table, int index ) {
    return getTableAdapter( table ).isItemVirtual( index );
  }

  private static ITableAdapter getTableAdapter( Table table ) {
    return table.getAdapter( ITableAdapter.class );
  }

}
