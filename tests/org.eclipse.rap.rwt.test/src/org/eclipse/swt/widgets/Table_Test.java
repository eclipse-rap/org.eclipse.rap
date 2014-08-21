/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.IItemHolderAdapter;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.eclipse.swt.layout.FillLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Table_Test {

  private Display display;
  private Shell shell;
  private Table table;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    table = new Table( shell, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testInitialValues() {
    assertFalse( table.getHeaderVisible() );
    assertFalse( table.getLinesVisible() );
    assertEquals( 0, table.getSelectionCount() );
    assertEquals( 0, table.getSelectionIndices().length );
    assertEquals( 0, table.getSelection().length );
    assertEquals( 0, table.getTopIndex() );
    assertNull( table.getSortColumn() );
    assertEquals( SWT.NONE, table.getSortDirection() );
  }

  @Test
  public void testStyle() {
    assertTrue( ( table.getStyle() & SWT.H_SCROLL ) != 0 );
    assertTrue( ( table.getStyle() & SWT.V_SCROLL ) != 0 );
    assertTrue( ( table.getStyle() & SWT.SINGLE ) != 0 );

    table = new Table( shell, SWT.NO_SCROLL );
    assertTrue( ( table.getStyle() & SWT.NO_SCROLL ) != 0 );
    assertTrue( ( table.getStyle() & SWT.V_SCROLL ) == 0 );
    assertTrue( ( table.getStyle() & SWT.H_SCROLL ) == 0 );

    table = new Table( shell, SWT.SINGLE | SWT.MULTI );
    assertTrue( ( table.getStyle() & SWT.SINGLE ) != 0 );

    table = new Table( shell, SWT.SINGLE | SWT.SINGLE );
    assertTrue( ( table.getStyle() & SWT.SINGLE ) != 0 );

    table = new Table( shell, SWT.SINGLE | SWT.MULTI );
    assertTrue( ( table.getStyle() & SWT.SINGLE ) != 0 );
  }

  @Test
  public void testTableCreation() {
    assertEquals( 0, table.getItemCount() );
    assertEquals( 0, table.getItems().length );
    TableItem item0 = new TableItem( table, SWT.NONE );
    assertEquals( 1, table.getItemCount() );
    assertEquals( 1, table.getItems().length );
    assertEquals( item0, table.getItem( 0 ) );
    assertEquals( item0, table.getItems()[ 0 ] );
    try {
      table.getItem( 4 );
      fail( "Index out of bounds" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    assertSame( display, item0.getDisplay() );
    item0.dispose();
    assertEquals( 0, table.getItemCount() );
    assertEquals( 0, table.getItems().length );
    item0 = new TableItem( table, SWT.NONE );
    assertEquals( 1, table.getItemCount() );
    assertEquals( 0, table.getColumnCount() );
    assertEquals( 0, table.getColumns().length );
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    assertEquals( 1, table.getColumnCount() );
    assertEquals( 1, table.getColumns().length );
    assertEquals( column0, table.getColumn( 0 ) );
    assertEquals( column0, table.getColumns()[ 0 ] );
    try {
      table.getColumn( 4 );
      fail( "Index out of bounds" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    assertSame( display, column0.getDisplay() );
    column0.dispose();
    assertEquals( 0, table.getColumnCount() );
    assertEquals( 0, table.getColumns().length );

    // search operation indexOf
    column0 = new TableColumn( table, SWT.NONE );
    TableColumn tableColumn1 = new TableColumn( table, SWT.NONE );
    assertEquals( 1, table.indexOf( tableColumn1 ) );
    TableItem tableItem1 = new TableItem( table, SWT.NONE );
    assertEquals( 1, table.indexOf( tableItem1 ) );

    // column width property
    assertEquals( 0, column0.getWidth() );
    column0.setWidth( 100 );
    assertEquals( 100, column0.getWidth() );
  }

  @Test
  public void testHeaderHeight() {
    Table table = createTable( SWT.NONE, 1 );
    assertEquals( 0, table.getHeaderHeight() );
    table.setHeaderVisible( true );
    assertTrue( table.getHeaderHeight() > 0 );
  }

  @Test
  public void testHeaderHeightWithCustomUserFont() {
    Table table = createTable( SWT.NONE, 1 );
    table.setHeaderVisible( true );
    int headerHeight = table.getHeaderHeight();

    table.setFont( new Font( display, "Arial", 30, SWT.NORMAL ) );

    assertTrue( headerHeight < table.getHeaderHeight() );
  }

  @Test
  public void testMultiLineHeaderHeight() {
    Table table = createMultiLineHeaderTable();
    TableColumn column = table.getColumn( 1 );
    table.setHeaderVisible( true );

    column.setText( "Multi line\nHeader" );

    assertEquals( 52, table.getHeaderHeight() );
  }

  @Test
  public void testTableItemTexts() {
    TableItem item = new TableItem( table, SWT.NONE );
    String text0 = "text0";
    String text1 = "text1";
    String text2 = "text2";
    String text3 = "text3";
    String text4 = "text4";
    String text5 = "text5";

    // test text for first column, the same as setText(String)
    item.setText( text0 );
    assertEquals( text0, item.getText() );
    assertEquals( text0, item.getText( 0 ) );
    item.setText( 0, text1 );
    assertEquals( text1, item.getText() );
    assertEquals( text1, item.getText( 0 ) );
    try {
      item.setText( 0, null );
      fail( "Parameter index must not be null." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }

    // test text setting if no table column exists
    item.setText( 1, text1 );
    assertEquals( "", item.getText( 1 ) );
    item.setText( 2, text1 );
    assertEquals( "", item.getText( 2 ) );
    // test text setting if table column exists
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    item.setText( 2, text2 );
    assertEquals( text2, item.getText( 2 ) );

    // test text retrievment after last column was disposed
    column2.dispose();
    assertSame( "", item.getText( 2 ) );
    column2 = new TableColumn( table, SWT.NONE );
    assertSame( "", item.getText( 2 ) );
    new TableColumn( table, SWT.NONE );
    item.setText( 3, text3 );
    assertSame( text3, item.getText( 3 ) );
    new TableColumn( table, SWT.NONE );
    assertSame( "", item.getText( 4 ) );
    String[] texts = new String[]{
      text0, text1, text2, text3, text4, text5
    };

    // test setting multiple texts at once
    for( int i = 0; i < texts.length; i++ ) {
      item.setText( i, texts[ i ] );
    }
    assertEquals( text0, item.getText( 0 ) );
    assertEquals( text1, item.getText( 1 ) );
    assertEquals( text2, item.getText( 2 ) );
    assertEquals( text3, item.getText( 3 ) );
    assertEquals( text4, item.getText( 4 ) );
    assertEquals( "", item.getText( 5 ) );

    // test disposal of column that is not the last one
    column2.dispose();
    assertEquals( text0, item.getText( 0 ) );
    assertEquals( text1, item.getText( 1 ) );
    assertEquals( text3, item.getText( 2 ) );
    assertEquals( text4, item.getText( 3 ) );
    assertEquals( "", item.getText( 4 ) );
    column0.dispose();
    assertEquals( text1, item.getText( 0 ) );
    assertEquals( text3, item.getText( 1 ) );
    assertEquals( text4, item.getText( 2 ) );
    assertEquals( "", item.getText( 3 ) );
  }

  @Test
  public void testTopIndex() {
    createTableItems( table, 5 );

    table.setTopIndex( 1 );

    assertEquals( 1, table.getTopIndex() );
  }

  @Test
  public void testTopIndex_ValueOutOfBounds() {
    createTableItems( table, 5 );
    int previousTopIndex = table.getTopIndex();

    table.setTopIndex( 10000 );

    assertEquals( previousTopIndex, table.getTopIndex() );
  }

  @Test
  public void testTopIndex_AdjustAfterItemDispose() {
    TableItem[] items = createTableItems( table, 2 );

    table.setTopIndex( 1 );

    items[ 1 ].dispose();

    assertEquals( 0, table.getTopIndex() );
  }

  @Test
  public void testTopIndex_AfterRemoveAllItems() {
    createTableItems( table, 3 );

    table.removeAll();

    assertEquals( 0, table.getTopIndex() );
  }

  @Test
  public void testTopIndex_AdjustIfBigger() {
    int visibleItems = 3;
    table.setSize( 100, visibleItems * table.getItemHeight() );
    createTableItems( table, 20 );
    table.setTopIndex( 14 );

    for( int i = 10; i < 20; i++ ) {
      table.getItem( 10 ).dispose();
    }

    int itemCount = table.getItemCount();
    int visibleItemCount = table.getVisibleItemCount( false );
    assertEquals( itemCount - visibleItemCount, table.getTopIndex() );
  }

  @Test
  public void testTopIndex_OnResize() {
    createTableItems( table, 10 );
    int visibleItems = 3;
    table.setSize( 100, visibleItems * table.getItemHeight() );
    table.setTopIndex( 5 );

    table.setSize( 100, ( visibleItems + 3 ) * table.getItemHeight() );

    assertEquals( 4, table.getTopIndex() );
  }

  @Test
  public void testTopIndex_OnTemporaryResize() {
    table.setSize( 100, 100 );
    createTableItems( table, 10 );
    table.setTopIndex( 5 );

    markTemporaryResize();
    table.setSize( 1100, 1100 );

    assertEquals( 5, table.getTopIndex() );
  }

  @Test
  public void testTopIndex_InResizeEvent() {
    final int[] log = new int[ 1 ];
    createTableItems( table, 10 );
    int visibleItems = 3;
    table.setSize( 100, visibleItems * table.getItemHeight() );
    table.setTopIndex( 5 );
    table.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent e ) {
        log[ 0 ] = table.getTopIndex();
      }
    } );

    table.setSize( 100, ( visibleItems + 3 ) * table.getItemHeight() );

    assertEquals( 4, log[ 0 ] );
  }

  // see bug 384589: [CellEditor] Wrong row is edited when last row is visible
  @Test
  public void testTopIndex_AdjustOnSet() {
    createTableItems( table, 10 );
    int visibleItems = 3;
    table.setSize( 100, visibleItems * table.getItemHeight() );

    table.setTopIndex( 8 );

    int itemCount = table.getItemCount();
    int visibleItemCount = table.getVisibleItemCount( false );
    assertEquals( itemCount - visibleItemCount, table.getTopIndex() );
  }

  @Test
  public void testDispose() {
    Table table = createTable( SWT.SINGLE, 1 );
    TableColumn column = table.getColumn( 0 );
    TableItem item = new TableItem( table, SWT.NONE );
    table.dispose();
    assertTrue( table.isDisposed() );
    assertTrue( column.isDisposed() );
    assertTrue( item.isDisposed() );
  }

  @Test
  public void testDisposeSingleSelectedItem() {
    Table table = createTable( SWT.SINGLE, 1 );
    TableItem item = new TableItem( table, SWT.NONE );

    table.setSelection( new TableItem[] { item } );
    item.dispose();
    assertEquals( -1, table.getSelectionIndex() );
    assertEquals( 0, table.getItemCount() );
    assertEquals( 0, table.getSelectionCount() );
    assertEquals( 0, table.getSelection().length );
  }

  @Test
  public void testDisposeMultiSelectedItem() {
    Table table = createTable( SWT.MULTI, 1 );
    TableItem item0 = new TableItem( table, SWT.NONE );
    TableItem item1 = new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );

    table.setSelection( new TableItem[] { item0, item1 } );
    item0.dispose();
    assertEquals( table.indexOf( item1 ), table.getSelectionIndex() );
    assertEquals( 1, table.getSelectionIndices().length );
    assertEquals( 0, table.getSelectionIndices()[ 0 ] );
    assertEquals( 2, table.getItemCount() );
    assertEquals( 1, table.getSelectionCount() );

    table.removeAll();
    item0 = new TableItem( table, SWT.NONE );
    item1 = new TableItem( table, SWT.NONE );
    TableItem item2 = new TableItem( table, SWT.NONE );
    table.selectAll();
    table.remove( 1 );
    assertEquals( 2, table.getSelectionIndices().length );
    assertTrue( find( table.indexOf( item0 ), table.getSelectionIndices() ) );
    assertTrue( find( table.indexOf( item2 ), table.getSelectionIndices() ) );
  }

  @Test
  public void testDisposeInSetData() {
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 100 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        event.item.dispose();
      }
    } );
    try {
      // if dispose() is called while processing a SetData event
      // and the event was caused by a call to one of the getXXX methods
      // of TableItem an SWT.ERROR_WIDGET_DISPOSED is thrown
      table.getItem( 40 ).getTextBounds( 0 );
      fail( "disposing while in SetData must not be allowd" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_WIDGET_DISPOSED, e.code );
    }
  }

  @Test
  public void testGetAdapterWithTableAdapter() {
    Object adapter = table.getAdapter( ITableAdapter.class );
    assertNotNull( adapter );
  }

  @Test
  public void testGetAdapterWithCellToolTipAdapter() {
    Object adapter = table.getAdapter( ICellToolTipAdapter.class );
    assertNotNull( adapter );
  }

  @Test
  public void testGetAdapterWithItemHolderAdapter() {
    Object adapter = table.getAdapter( IItemHolderAdapter.class );
    assertNotNull( adapter );
  }

  @Test
  public void testReduceSetItemCountWithSelection() {
    // Create a table that is populated with setItemCount with all selected
    shell.setLayout( new FillLayout() );
    Table table = createTable( SWT.MULTI, 1 );
    shell.layout();
    shell.open();
    table.setItemCount( 4 );
    table.setSelection( 0, table.getItemCount() - 1 );
    // Name items to ease debugging
    for( int i = 0; i < table.getItemCount(); i++ ) {
      table.getItem( i ).setText( "Item " + i );
    }
    // reduce the number of items by half
    table.setItemCount( table.getItemCount() / 2 );
    // Ensure that the selection contains all the remaining items and all
    // indices returned are valid
    int[] selectionIndices = table.getSelectionIndices();
    for( int i = 0; i < selectionIndices.length; i++ ) {
      assertTrue( selectionIndices[ i ] >= 0 );
      assertTrue( selectionIndices[ i ] < table.getItemCount() );
    }
    assertEquals( table.getItemCount(), selectionIndices.length );
  }

  @Test
  public void testReduceSetItemCountWithSelectionVirtual() {
    // Create a table that is populated with setItemCount with all selected
    shell.setSize( 800, 800 );
    shell.setLayout( new FillLayout() );
    Table table = createTable( SWT.MULTI | SWT.VIRTUAL, 1 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        Item item = ( Item )event.item;
        item.setText( "Item " + event.index );
      }
    } );
    shell.layout();
    shell.open();
    table.setItemCount( 4 );
    table.setSelection( 0, table.getItemCount() - 1 );
    // reduce the number of items by half
    table.setItemCount( table.getItemCount() / 2 );
    // Ensure that the selection contains all the remaining items and all
    // indices returned are valid
    int[] selectionIndices = table.getSelectionIndices();
    for( int i = 0; i < selectionIndices.length; i++ ) {
      assertTrue( selectionIndices[ i ] >= 0 );
      assertTrue( selectionIndices[ i ] < table.getItemCount() );
    }
    assertEquals( table.getItemCount(), selectionIndices.length );
  }

  @Test
  public void testFocusIndex() {
    new TableColumn( table, SWT.NONE );
    TableItem item0 = new TableItem( table, SWT.NONE );
    TableItem item1 = new TableItem( table, SWT.NONE );
    TableItem item2 = new TableItem( table, SWT.NONE );
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;

    // Test initial value
    assertEquals( -1, tableAdapter.getFocusIndex() );

    // setSelection changes the focusIndex to the selected item
    table.setSelection( item0 );
    assertEquals( 0, tableAdapter.getFocusIndex() );

    table.setSelection( item1 );
    table.setSelection( new TableItem[] { item0 } );
    assertEquals( 0, tableAdapter.getFocusIndex() );

    // calling setSelection with an out-of-range value clears the selection but
    // does not affect the focusIndex
    table.setSelection( 0 );
    table.setSelection( -2 );
    assertEquals( 0, tableAdapter.getFocusIndex() );
    table.setSelection( 0 );
    table.setSelection( table.getItemCount() + 10 );
    assertEquals( 0, tableAdapter.getFocusIndex() );

    // Resetting the selection does not affect the focusIndex
    table.setSelection( item0 );
    table.deselectAll();
    assertEquals( 0, table.getSelectionCount() );
    assertEquals( 0, tableAdapter.getFocusIndex() );

    // Ensure that select does not change the focusIndex
    table.setSelection( item0 );
    table.select( table.indexOf( item1 ) );
    assertEquals( 0, tableAdapter.getFocusIndex() );

    // Disposing of the focused item also resets the focusIndex
    table.setSelection( item0 );
    item0.dispose();
    assertEquals( -1, tableAdapter.getFocusIndex() );

    // Disposing of the focused but un-selected item moves focus to the
    // selected item
    table.setSelection( item1 );
    table.select( table.indexOf( item2 ) );
    item1.dispose();
    assertEquals( table.indexOf( item2 ), tableAdapter.getFocusIndex() );

    // Insert an item before the focused one an verify that focus moves on
    table.removeAll();
    new TableItem( table, SWT.NONE );
    table.setSelection( 0 );
    assertEquals( 0, tableAdapter.getFocusIndex() ); // ensure precondition
    new TableItem( table, SWT.NONE, 0 );
    assertEquals( 1, table.getSelectionIndex() );
    assertEquals( 1, tableAdapter.getFocusIndex() );
  }

  @Test
  public void testFocusIndexVirtual() {
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 500, 500 );
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;

    table.setItemCount( 100 );
    table.setSelection( 99 );
    table.setSize( 501, 501 );
    table.setItemCount( 98 );
    assertEquals( 0, table.getSelectionCount() );
    assertEquals( -1, tableAdapter.getFocusIndex() );
  }

  @Test
  public void testRemoveAll() {
    Table table = createTable( SWT.NONE, 1 );
    TableItem preDisposedItem = new TableItem( table, SWT.NONE );
    TableItem item0 = new TableItem( table, SWT.NONE );
    TableItem item1 = new TableItem( table, SWT.NONE );

    preDisposedItem.dispose();
    table.setSelection( 1 );
    table.setTopIndex( 1 );
    table.removeAll();
    assertEquals( -1, table.getSelectionIndex() );
    assertEquals( 0, table.getItemCount() );
    assertEquals( 0, table.getTopIndex() );
    assertTrue( item0.isDisposed() );
    assertTrue( item1.isDisposed() );
  }

  @Test
  public void testRemoveAll_virtual() {
    shell.setSize( 100, 100 );
    shell.setLayout( new FillLayout() );
    Table table = createTable( SWT.MULTI | SWT.VIRTUAL, 1 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        Item item = ( Item )event.item;
        item.setText( "Item " + event.index );
      }
    } );
    shell.layout();
    shell.open();
    table.setItemCount( 10 );
    table.removeAll();
    assertEquals( 0, table.getItemCount() );
  }

  /*
   * Disposing the items in reverse order (like in GTK) avoids performance critical operations like
   * shifting item/data arrays, item index recalculation etc.
   * see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=396172
   */
  @Test
  public void testRemoveAll_disposeInReverseOrder() {
    final List<String> log = new ArrayList<String>();
    createTableItems( table, 5 );
    for( TableItem item : table.getItems() ) {
      item.addDisposeListener( new DisposeListener() {
        public void widgetDisposed( DisposeEvent event ) {
          TableItem item = ( TableItem )event.getSource();
          log.add( item.getText() );
        }
      } );
    }

    table.removeAll();

    String[] expected = { "item4", "item3", "item2", "item1", "item0" };
    assertArrayEquals( expected, log.toArray( new String[ 0 ] ) );
  }

  @Test
  public void testRemoveRange() {
    int number = 5;
    TableItem[] items = createTableItems( table, number );

    table.remove( 2, 3 );

    assertTrue( items[ 2 ].isDisposed() );
    assertTrue( items[ 3 ].isDisposed() );
    assertEquals( table.getItemCount(), 3 );
  }

  @Test
  public void testRemoveRangeWithInvalidRange() {
    try {
      table.remove( -1, 1 );
      fail( "No exception thrown for illegal index range" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveRangeVirtual() {
    shell.setSize( 100, 100 );
    shell.setLayout( new FillLayout() );
    Table table = createTable( SWT.MULTI | SWT.VIRTUAL, 1 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        Item item = ( Item )event.item;
        item.setText( "Item " + event.index );
      }
    } );
    shell.layout();
    shell.open();
    table.setItemCount( 10 );
    table.remove( 0, 9 );
    assertEquals( 0, table.getItemCount() );
  }

  @Test
  public void testRemove() {
    int number = 5;
    TableItem[] items = createTableItems( table, number );
    table.remove( 1 );
    assertTrue( items[ 1 ].isDisposed() );
    assertEquals( table.getItemCount(), 4 );
  }

  @Test
  public void testRemoveVirtual() {
    Table table = new Table( shell, SWT.MULTI | SWT.VIRTUAL );
    table.setSize( 100, 100 );
    table.setItemCount( 10 );
    table.remove( 0 );
    assertEquals( 9, table.getItemCount() );
    TableItem item5 = table.getItem( 5 );
    table.remove( 2 );
    assertSame( item5, table.getItem( 4 ) );
    assertEquals( 8, table.getItemCount() );
  }

  @Test
  public void testRemoveArrayVirtual() {
    shell.setSize( 100, 100 );
    shell.setLayout( new FillLayout() );
    Table table = createTable( SWT.MULTI | SWT.VIRTUAL, 1 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        Item item = ( Item )event.item;
        item.setText( "Item " + event.index );
      }
    } );
    shell.layout();
    shell.open();
    table.setItemCount( 10 );
    table.remove( new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 } );
    assertEquals( 0, table.getItemCount() );
  }

  @Test
  public void testRemoveWithSelectionListener() {
    // ensure that no selection event is fired if a selected item is removed
    final boolean eventFired[] = { false };
    for( int i = 0; i < 5; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    table.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        eventFired[ 0 ] = true;
      }
    } );
    table.select( 1 );
    table.remove( 1 );
    assertFalse( eventFired[ 0 ] );
  }

  @Test
  public void testRemoveArray() {
    int number = 15;
    TableItem[] items = createTableItems( table, number );
    try {
      table.remove( null );
      fail( "No exception thrown for tableItems == null" );
    } catch( IllegalArgumentException expected ) {
    }
    try {
      table.remove( new int[]{ 2, 1, 0, -100, 5, 5, 2, 1, 0, 0, 0 } );
      fail( "No exception thrown for illegal index arguments" );
    } catch( IllegalArgumentException expected ) {
    }
    try {
      table.remove( new int[]{ 2, 1, 0, number, 5, 5, 2, 1, 0, 0, 0 } );
      fail( "No exception thrown for illegal index arguments" );
    } catch( IllegalArgumentException expected ) {
    }
    table.remove( new int[]{} );
    table = new Table( shell, SWT.NONE );
    for( int i = 0; i < number; i++ ) {
      items[ i ] = new TableItem( table, 0 );
    }
    assertTrue( ":a:", !items[ 2 ].isDisposed() );
    table.remove( new int[]{ 2 } );
    assertTrue( ":b:", items[ 2 ].isDisposed() );
    assertEquals( number - 1, table.getItemCount() );
    assertTrue( ":c:", !items[ number - 1 ].isDisposed() );
    table.remove( new int[]{ number - 2 } );
    assertTrue( ":d:", items[ number - 1 ].isDisposed() );
    assertEquals( number - 2, table.getItemCount() );
    assertTrue( ":e:", !items[ 3 ].isDisposed() );
    table.remove( new int[]{ 2 } );
    assertTrue( ":f:", items[ 3 ].isDisposed() );
    assertEquals( number - 3, table.getItemCount() );
    assertTrue( ":g:", !items[ 0 ].isDisposed() );
    table.remove( new int[]{ 0 } );
    assertTrue( ":h:", items[ 0 ].isDisposed() );
    assertEquals( number - 4, table.getItemCount() );
  }

  @Test
  public void testSingleSelection() {
    Table table = new Table( shell, SWT.SINGLE );
    TableItem item1 = new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );

    // Test setSelection(int)
    table.deselectAll();
    table.setSelection( 0 );
    assertTrue( table.isSelected( 0 ) );

    table.setSelection( table.getItemCount() + 20 );
    assertEquals( 0, table.getSelectionCount() );

    // Test setSelection(int,int)
    table.deselectAll();
    table.setSelection( 0, 0 );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 0 );
    table.setSelection( 0, 2 );
    assertEquals( 0, table.getSelectionCount() );

    // Test setSelection(int[])
    table.deselectAll();
    table.setSelection( new int[]{ 0 } );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.setSelection( new int[]{ 0, 1 } );
    assertEquals( 0, table.getSelectionCount() );

    table.deselectAll();
    table.setSelection( 2 );
    table.setSelection( new int[]{ 777 } );
    assertEquals( 0, table.getSelectionCount() );

    // Test setSelection(TableItem)
    table.deselectAll();
    table.setSelection( item1 );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( item1, table.getSelection()[ 0 ] );

    try {
      table.deselectAll();
      table.setSelection( item1 );
      table.setSelection( ( TableItem )null );
    } catch( IllegalArgumentException e ) {
      // expected
      assertEquals( 1, table.getSelectionCount() );
      assertEquals( item1, table.getSelection()[ 0 ] );
    }

    table.deselectAll();
    table.setSelection( item1 );
    Table anotherTable = new Table( shell, SWT.NONE );
    TableItem anotherItem = new TableItem( anotherTable, SWT.NONE );
    table.setSelection( anotherItem );
    assertEquals( 0, table.getSelectionCount() );

    // Test select(int)
    table.deselectAll();
    table.select( 0 );
    assertTrue( table.isSelected( 0 ) );
    table.select( 1 );
    assertFalse( table.isSelected( 0 ) );
    assertTrue( table.isSelected( 1 ) );

    table.deselectAll();
    table.select( 0 );
    table.select( table.getItemCount() + 20 );
    assertTrue( table.isSelected( 0 ) );

    // Test select(int,int)
    table.deselectAll();
    table.select( 0, 0 );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( 0, 1 );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 2 ) );

    // Test select(int[])
    table.deselectAll();
    table.select( new int[]{ 0 } );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( new int[]{ 0, 1 } );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 2 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( new int[]{ 777 } );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 2 ) );
  }

  @Test
  public void testMultiSelection() {
    Table table = new Table( shell, SWT.MULTI );
    TableItem item1 = new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );

    // Test setSelection(int)
    table.deselectAll();
    table.setSelection( 0 );
    assertTrue( table.isSelected( 0 ) );

    table.setSelection( table.getItemCount() + 20 );
    assertEquals( 0, table.getSelectionCount() );

    // Test setSelection(int,int)
    table.deselectAll();
    table.setSelection( 0, 0 );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 1 );
    table.setSelection( 0, 2 );
    assertEquals( 3, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );
    assertTrue( table.isSelected( 1 ) );
    assertTrue( table.isSelected( 2 ) );
    assertFalse( table.isSelected( 3 ) );

    table.deselectAll();
    table.setSelection( 0 );
    table.setSelection( 1, 777 );
    assertFalse( table.isSelected( 0 ) );
    assertTrue( table.isSelected( 1 ) );
    assertTrue( table.isSelected( 2 ) );
    assertTrue( table.isSelected( 3 ) );

    // Test setSelection(int[])
    table.deselectAll();
    table.setSelection( new int[]{ 0 } );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.setSelection( new int[]{ 0, 1 } );
    assertEquals( 2, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );
    assertTrue( table.isSelected( 1 ) );
    assertFalse( table.isSelected( 2 ) );
    assertFalse( table.isSelected( 3 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.setSelection( new int[]{ 777 } );
    assertEquals( 0, table.getSelectionCount() );

    // Test setSelection(TableItem)
    table.deselectAll();
    table.setSelection( item1 );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( item1, table.getSelection()[ 0 ] );

    try {
      table.deselectAll();
      table.setSelection( item1 );
      table.setSelection( ( TableItem )null );
    } catch( IllegalArgumentException e ) {
      // expected
      assertEquals( 1, table.getSelectionCount() );
      assertEquals( item1, table.getSelection()[ 0 ] );
    }

    table.deselectAll();
    table.setSelection( item1 );
    Table anotherTable = new Table( shell, SWT.NONE );
    TableItem anotherItem = new TableItem( anotherTable, SWT.NONE );
    table.setSelection( anotherItem );
    assertEquals( 0, table.getSelectionCount() );

    // Test select(int)
    table.deselectAll();
    table.select( 0 );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );
    table.select( 1 );
    assertEquals( 2, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );
    assertTrue( table.isSelected( 1 ) );

    table.deselectAll();
    table.select( 0 );
    table.select( table.getItemCount() + 20 );
    assertTrue( table.isSelected( 0 ) );

    // Test select(int,int)
    table.deselectAll();
    table.select( 0, 0 );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( 0, 1 );
    assertEquals( 3, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );
    assertTrue( table.isSelected( 1 ) );
    assertTrue( table.isSelected( 2 ) );

    // Test select(int[])
    table.deselectAll();
    table.select( new int[]{ 0 } );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( new int[]{ 0, 1, 777 } );
    assertEquals( 3, table.getSelectionCount() );
    assertTrue( table.isSelected( 0 ) );
    assertTrue( table.isSelected( 1 ) );
    assertTrue( table.isSelected( 2 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( new int[]{ 777 } );
    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 2 ) );
  }

  @Test
  public void testSelectionReveals() {
    Table table = new Table( shell, SWT.BORDER | SWT.MULTI );
    table.setSize( 200, 200 );
    for( int i = 0; i < 128; i++ ) {
      TableItem item = new TableItem( table, SWT.NONE );
      item.setText( "Item " + i );
    }

    // test case precondition: table offers space for max. 30 visible items
    assertTrue( table.getVisibleItemCount( false ) < 30 );

    // calling setSelection makes the selected item visible
    table.setSelection( 95 );
    assertTrue( table.isItemVisible( 95 ) );

    // calling select does *not* make the selected item visible
    table.select( 0 );
    assertFalse( table.isItemVisible( 0 ) );
    assertTrue( table.isItemVisible( 95 ) );

    // selecting a range will make the lower end of the range visible
    table.setSelection( 0, 95 );
    assertTrue( table.isItemVisible( 0 ) );
    assertFalse( table.isItemVisible( 95 ) );
  }

  @Test
  public void testGetSelectionIndex() {
    // SWT.SINGLE
    Table singleTable = new Table( shell, SWT.SINGLE );
    new TableItem( singleTable, SWT.NONE );
    new TableItem( singleTable, SWT.NONE );

    singleTable.setSelection( 1 );
    assertEquals( 1, singleTable.getSelectionIndex() );

    // SWT.MULTI
    Table multiTable = new Table( shell, SWT.MULTI );
    new TableItem( multiTable, SWT.NONE );
    new TableItem( multiTable, SWT.NONE );
    new TableItem( multiTable, SWT.NONE );

    multiTable.setSelection( 1 );
    assertEquals( 1, multiTable.getSelectionIndex() );
    multiTable.select( 2 );
    assertEquals( 1, multiTable.getSelectionIndex() );
  }

  @Test
  public void testSelectAll_SINGLE() {
    Table table = createTable( SWT.SINGLE, 1 );
    createTableItems( table, 2 );
    table.deselectAll();
    table.selectAll();
    assertEquals( 0, table.getSelectionCount() );
    table.setSelection( 1 );
    assertEquals( 1, table.getSelectionCount() );
  }

  @Test
  public void testSelectAll_MULTI() {
    Table table = createTable( SWT.MULTI, 1 );
    createTableItems( table, 2 );
    table.selectAll();
    assertEquals( table.getItemCount(), table.getSelectionCount() );
  }

  @Test
  public void testDeselect() {
    Table table = createTable( SWT.SINGLE, 1 );
    createTableItems( table, 2 );

    table.setSelection( 0 );
    table.deselect( 0 );
    assertEquals( 0, table.getSelectionCount() );
  }

  @Test
  public void testDeselectMulti() {
    Table table = createTable( SWT.MULTI, 1 );
    createTableItems( table, 2 );
    table.selectAll();

    table.deselect( new int[] { 0, 2 } );

    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 1 ) );
  }

  @Test
  public void testDeselectArray() {
    Table table = createTable( SWT.SINGLE, 1 );
    createTableItems( table, 2 );
    table.setSelection( 1 );

    table.deselect( new int[ 0 ] );

    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 1 ) );
  }

  @Test
  public void testDeselectArrayWithWithIndicesOutsideSelection() {
    Table table = createTable( SWT.SINGLE, 1 );
    createTableItems( table, 2 );
    table.setSelection( 1 );

    table.deselect( new int[] { 1, 777 } );

    assertEquals( 0, table.getSelectionCount() );
  }

  @Test
  public void testDeselectRangeWithSelectionWithinRange() {
    Table table = createTable( SWT.SINGLE, 1 );
    createTableItems( table, 2 );
    table.setSelection( 1 );

    table.deselect( 0, 777 );

    assertEquals( 0, table.getSelectionCount() );
  }

  @Test
  public void testDeselectRangeWithSelectionOutsideRange() {
    Table table = createTable( SWT.SINGLE, 1 );
    createTableItems( table, 2 );
    table.setSelection( 1 );

    table.deselect( 4, 777 );

    assertEquals( 1, table.getSelectionCount() );
    assertTrue( table.isSelected( 1 ) );
  }

  @Test
  public void testDeselectAll() {
    Table table = createTable( SWT.SINGLE, 1 );
    createTableItems( table, 2 );

    table.setSelection( 1 );
    table.deselectAll();
    assertEquals( -1, table.getSelectionIndex() );
  }

  @Test
  public void testIsSelectedNonVirtual() {
    Table table = createTable( SWT.NONE, 1 );
    createTableItems( table, 2 );

    // initial state: no selection, isSelected returns alway false
    assertFalse( table.isSelected( 0 ) );
    assertFalse( table.isSelected( 1 ) );
    // test with indices that are out of range, must always return false
    assertFalse( table.isSelected( -3 ) );
    assertFalse( table.isSelected( table.getItemCount() + 100 ) );
    // select and verify that isSelected returns true
    table.setSelection( 0 );
    assertTrue( table.isSelected( 0 ) );
  }

  @Test
  public void testIsSelectedVirtual() {
    shell.setLayout( new FillLayout() );
    Table table = createTable( SWT.VIRTUAL, 1 );
    table.setItemCount( 1000 );
    shell.open();

    // initial state: no selection, isSelected returns alway false
    assertFalse( table.isSelected( 0 ) );
    assertFalse( table.isSelected( 1 ) );
    // test with indices that are out of range, must always return false
    assertFalse( table.isSelected( -3 ) );
    assertFalse( table.isSelected( table.getItemCount() + 100 ) );
    // select and verify that isSelected returns true
    table.setSelection( 0 );
    assertTrue( table.isSelected( 0 ) );
    // ensure that calling isSelected does not resolve a virtual item
    ITableAdapter tableAdapter
      = table.getAdapter( ITableAdapter.class );
    boolean selected = table.isSelected( 900 );
    assertFalse( selected );
    assertTrue( tableAdapter.isItemVirtual( 900 ) );
  }

  @Test
  public void testClearNonVirtual() throws IOException {
    Table table = createTable( SWT.CHECK, 1 );
    TableItem item = new TableItem( table, SWT.NONE );
    ITableAdapter tableAdapter = table.getAdapter( ITableAdapter.class );
    table.setSelection( item );
    item.setText( "abc" );
    item.setImage( createImage50x100() );
    item.setChecked( true );
    item.setGrayed( true );

    table.clear( table.indexOf( item ) );

    assertEquals( "", item.getText() );
    assertEquals( null, item.getImage() );
    assertFalse( item.getChecked() );
    assertFalse( item.getGrayed() );
    assertFalse( tableAdapter.isItemVirtual( table.indexOf( item ) ) );
    assertSame( item, table.getSelection()[ 0 ] );

  }

  @Test
  public void testClearWithIllegalArgument() {
    Table table = new Table( shell, SWT.CHECK );
    try {
      table.clear( 2 );
      fail( "Must throw exception when attempting to clear non-existing item" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testClearVirtual() {
    shell.setLayout( new FillLayout() );
    Table table = createTable( SWT.VIRTUAL | SWT.CHECK, 1 );
    table.setItemCount( 100 );
    shell.layout();
    shell.open();
    ITableAdapter tableAdapter = table.getAdapter( ITableAdapter.class );

    table.getItem( 0 ).getText();
    table.select( 0 );
    table.clear( 0 );
    assertTrue( tableAdapter.isItemVirtual( 0 ) );
    assertEquals( 0, table.getSelectionIndex() );
  }

  @Test
  public void testClearRange() throws IOException {
    Image image = createImage50x100();
    Table table = createTable( SWT.NONE, 1 );
    TableItem[] items = new TableItem[ 10 ];
    for( int i = 0; i < 10; i++ ) {
      items[ i ] = new TableItem( table, SWT.NONE );
      items[ i ].setText( "abc" );
      items[ i ].setImage( image );
    }
    table.clear( 2, 5 );
    for( int i = 0; i < 10; i++ ) {
      if( i >= 2 && i <= 5 ) {
        assertEquals( "", items[ i ].getText() );
        assertNull( items[ i ].getImage() );
      } else {
        assertEquals( "abc", items[ i ].getText() );
        assertSame( image, items[ i ].getImage() );
      }
    }
  }

  @Test
  public void testClearRangeWithIllegalArgument() {
    try {
      table.clear( 1, 11 );
      fail( "Must throw exception when attempting to clear non-existing items" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testClearIndices() throws IOException {
    new TableColumn( table, SWT.NONE );
    TableItem[] items = new TableItem[ 10 ];
    Image image = createImage50x100();
    for( int i = 0; i < 10; i++ ) {
      items[ i ] = new TableItem( table, SWT.NONE );
      items[ i ].setText( "abc" );
      items[ i ].setImage( image );
    }
    table.clear( new int[] { 1, 3, 5 } );
    for( int i = 0; i < 10; i++ ) {
      if( i == 1 || i == 3 || i == 5 ) {
        assertEquals( "", items[ i ].getText() );
        assertNull( items[ i ].getImage() );
      } else {
        assertEquals( "abc", items[ i ].getText() );
        assertSame( image, items[ i ].getImage() );
      }
    }
  }

  @Test
  public void testClearIndicesWithIllegalArgument() {
    try {
      table.clear( new int[] { 2, 4, 15 } );
      fail( "Must throw exception when attempting to clear non-existing items" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testShowItem() {
    Table table = createTable( SWT.NONE, 1 );
    table.setLinesVisible( false );
    table.setHeaderVisible( false );
    int itemCount = 300;
    for( int i = 0; i < itemCount; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    int itemHeight = table.getItemHeight();
    int visibleLines = 100;
    table.setSize( 100, visibleLines * itemHeight );
    assertEquals( visibleLines, table.getVisibleItemCount( false ) );

    table.showItem( table.getItem( 99 ) );
    assertEquals( 0, table.getTopIndex() );

    table.showItem( table.getItem( 100 ) );
    assertEquals( 1, table.getTopIndex() );

    table.showItem( table.getItem( 199 ) );
    assertEquals( 100, table.getTopIndex() );

    table.showItem( table.getItem( itemCount - 1 ) );
    assertEquals( 200, table.getTopIndex() );

    table.showItem( table.getItem( 42 ) );
    assertEquals( 42, table.getTopIndex() );

    table.showItem( table.getItem( 0 ) );
    assertEquals( 0, table.getTopIndex() );
  }

  @Test
  public void testSetSelectionBeforeSetSize() {
    // Calling setSelection() before setSize() should not change top index
    // See bug 272714, https://bugs.eclipse.org/bugs/show_bug.cgi?id=272714
    for( int i = 0; i < 10; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    table.setSelection( 0 );
    table.setSize( 100, 100 );
    assertEquals( 0, table.getTopIndex() );
    table.setSize( 0, 0 );
    table.setSelection( 3 );
    table.setSize( 100, 100 );
    assertEquals( 0, table.getTopIndex() );
    // table with one item and item height == 1
    table = new Table( shell, SWT.NONE );
    new TableItem( table, SWT.NONE );
    table.setSelection( 0 );
    table.setSize( 100, table.getItemHeight() + 4 );
    assertEquals( 0, table.getTopIndex() );
    table.setSize( 0, 0 );
    table.setSelection( 1 );
    table.setSize( 100, table.getItemHeight() + 4 );
    assertEquals( 0, table.getTopIndex() );
  }

  @Test
  public void testSortColumnAndDirection() {
    TableColumn column = new TableColumn( table, SWT.NONE );

    table.setSortColumn( column );
    assertSame( column, table.getSortColumn() );
    table.setSortColumn( null );
    assertNull( table.getSortColumn() );

  }

  @Test
  public void testDisposeCurrentSortColumn() {
    TableColumn column = new TableColumn( table, SWT.NONE );
    table.setSortColumn( column );
    table.setSortDirection( SWT.UP );

    column.dispose();

    assertNull( table.getSortColumn() );
    assertEquals( SWT.UP, table.getSortDirection() );
  }

  @Test
  public void testSetSortWolumnWithDisposedColumn() {
    TableColumn disposedColumn = new TableColumn( table, SWT.NONE );
    disposedColumn.dispose();
    try {
      table.setSortColumn( disposedColumn );
      fail( "Must not allow to set disposed of sort column" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSetSortDirection() {
    table.setSortDirection( SWT.NONE );
    assertEquals( SWT.NONE, table.getSortDirection() );
    table.setSortDirection( SWT.UP );
    assertEquals( SWT.UP, table.getSortDirection() );
    table.setSortDirection( SWT.DOWN );
    assertEquals( SWT.DOWN, table.getSortDirection() );
    table.setSortDirection( SWT.NONE );
    table.setSortDirection( 4711 );
    assertEquals( SWT.NONE, table.getSortDirection() );

  }

  @Test
  public void testGetColumnOrder() {
    // Test column order for table without columns
    assertEquals( 0, table.getColumnOrder().length );

    // Test column order for the first, newly created column
    TableColumn column = new TableColumn( table, SWT.NONE );
    assertEquals( 1, table.getColumnOrder().length );
    assertEquals( 0, table.getColumnOrder()[ 0 ] );
    table.getColumnOrder()[ 0 ] = 12345;
    assertEquals( 0, table.getColumnOrder()[ 0 ] );

    // Test column order when disposing of the one and only column
    column.dispose();
    assertEquals( 0, table.getColumnOrder().length );

    // Test creating a column for the now column-less table
    column = new TableColumn( table, SWT.NONE );
    assertEquals( 1, table.getColumnOrder().length );
    assertEquals( 0, table.getColumnOrder()[ 0 ] );

    // Test creating another column: must be added at the end
    TableColumn anotherColumn = new TableColumn( table, SWT.NONE );
    assertEquals( 2, table.getColumnOrder().length );
    assertEquals( 0, table.getColumnOrder()[ table.indexOf( column ) ] );
    assertEquals( 1, table.getColumnOrder()[ table.indexOf( anotherColumn ) ] );

    // Insert column1 between the already existing column0 and column2
    table = new Table( shell, SWT.NONE );
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    TableColumn column1 = new TableColumn( table, SWT.NONE, 1 );
    assertEquals( column0, table.getColumn( 0 ) );
    assertEquals( column1, table.getColumn( 1 ) );
    assertEquals( column2, table.getColumn( 2 ) );
    assertEquals( 3, table.getColumnOrder().length );
    assertEquals( 0, table.getColumnOrder()[ table.indexOf( column0 ) ] );
    assertEquals( 1, table.getColumnOrder()[ table.indexOf( column1 ) ] );
    assertEquals( 2, table.getColumnOrder()[ table.indexOf( column2 ) ] );
  }

  @Test
  public void testSetColumnOrder() {
    final StringBuilder log = new StringBuilder();
    ControlAdapter controlAdapter = new ControlAdapter() {
      @Override
      public void controlMoved( ControlEvent event ) {
        TableColumn column = ( TableColumn )event.widget;
        log.append( column.getText() + " moved|" );
      }
    };
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    column0.setText( "Col0" );
    column0.addControlListener( controlAdapter );
    int column0Index = table.indexOf( column0 );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setText( "Col1" );
    column1.addControlListener( controlAdapter );
    int column1Index = table.indexOf( column1 );

    // Precondition: changing the column order programmatically is allowed
    // even if the moveable property is false
    assertFalse( column0.getMoveable() );
    assertFalse( column1.getMoveable() );

    // Ensure that changing the column order fire controlMoved events
    table.setColumnOrder( new int[] { column1Index, column0Index } );
    assertEquals( 2, table.getColumnOrder().length );
    assertEquals( 1, table.getColumnOrder()[ column0Index ] );
    assertEquals( 0, table.getColumnOrder()[ column1Index ] );
    assertEquals( "Col1 moved|Col0 moved|", log.toString() );

    // Ensure that calling setColumnOrder with the same order as already is
    // does not fire controlModevd events
    log.setLength( 0 );
    table.setColumnOrder( table.getColumnOrder() );
    assertEquals( "", log.toString() );
  }

  @Test
  public void testDisposeOfOrderedColumn() {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    table.setColumnOrder( new int[] { 1, 0, 2 } );
    table.getColumn( 0 ).dispose();
    assertEquals( 2, table.getColumnOrder().length );
    assertEquals( 0, table.getColumnOrder()[ 0 ] );
    assertEquals( 1, table.getColumnOrder()[ 1 ] );

    clearColumns( table );
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    table.setColumnOrder( new int[] { 0, 1 } );
    table.getColumn( 0 ).dispose();
    assertEquals( 1, table.getColumnOrder().length );
    assertEquals( 0, table.getColumnOrder()[ 0 ] );

    clearColumns( table );
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    table.setColumnOrder( new int[] { 0, 1, 2 } );
    table.getColumn( 2 ).dispose();
    assertEquals( 2, table.getColumnOrder().length );
    assertEquals( 0, table.getColumnOrder()[ 0 ] );
    assertEquals( 1, table.getColumnOrder()[ 1 ] );
  }

  @Test
  public void testDisposeWithFontDisposeInDisposeListener() {
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    final Font font = new Font( display, "font-name", 10, SWT.NORMAL );
    table.setFont( font );
    table.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        font.dispose();
      }
    } );
    table.dispose();
  }

  // ensures that there is no endless loop in Table#setItemCount (see bug 346576)
  @Test
  public void testSetItemCountInDisposeListener() {
    final Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 10 );
    table.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent e ) {
        table.setItemCount( 0 );
      }
    } );
    table.dispose();
  }

  @Test
  public void testDisposeItemsWithSetItemCountInDisposeListener() {
    final Table table = new Table( shell, SWT.VIRTUAL );
    TableItem item1 = new TableItem( table, SWT.NONE );
    TableItem item2 = new TableItem( table, SWT.NONE );
    TableItem item3 = new TableItem( table, SWT.NONE );
    table.setItemCount( 10 );
    table.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent e ) {
        table.setItemCount( 0 );
      }
    } );
    table.dispose();
    assertTrue( item1.isDisposed() );
    assertTrue( item2.isDisposed() );
    assertTrue( item3.isDisposed() );
  }

  @Test
  public void testRedrawAfterDisposeVirtual() {
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 100, 100 );
    table.setItemCount( 150 );
    // dispose the first item, this must cause a "redraw" which in turn triggers
    // a SetData event to populate the newly appeared item at the bottom of the
    // table
    table.getItem( 0 ).dispose();
    assertTrue( display.needsRedraw( table ) );
  }

  @Test
  public void testSetColumnOrderWithInvalidArguments() {
    // Passing null is not allowed
    try {
      table.setColumnOrder( null );
      fail( "setColumnOrder must not accept null argument" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    // Passing in an array with more elements than columns is not allowed
    new TableColumn( table, SWT.NONE );
    try {
      table.setColumnOrder( new int[] { 0, 0 } );
      fail( "setColumnOrder must not accept more elements than columns" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Passing in an array with more elements than columns is not allowed
    new TableColumn( table, SWT.NONE );
    try {
      table.setColumnOrder( new int[] { 0, 0 } );
      fail( "setColumnOrder must not accept duplicate elements" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      table.setColumnOrder( new int[] { 0, 77 } );
      fail( "setColumnOrder must not accept elements out of range" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testSetItemCount_nonVirtual() {
    // Setting itemCount to a higher value than getItemCount() creates the
    // missing items
    table.setItemCount( 2 );
    assertEquals( 2, table.getItemCount() );
    assertNotNull( table.getItem( 0 ) );
    assertNotNull( table.getItem( 1 ) );

    // Passing in the same value as already set is ignored
    table.setItemCount( 2 );
    table.setItemCount( table.getItemCount() );
    assertEquals( 2, table.getItemCount() );

    // Passing in a negative value is the same as passing in zero
    table.setItemCount( 2 );
    table.setItemCount( -2 );
    assertEquals( 0, table.getItemCount() );

    // Setting itemCount to a lower value than getItemCount() disposes of
    // the superfluous items
    table.setItemCount( 2 );
    table.setItemCount( 1 );
    assertEquals( 1, table.getItemCount() );
    assertNotNull( table.getItem( 0 ) );
  }

  @Test
  public void testSetItemCount_virtual() {
    Table table = new Table( shell, SWT.VIRTUAL );

    table.setItemCount( 1 );
    assertEquals( 1, table.getItemCount() );
    Item[] items = ItemHolder.getItemHolder( table ).getItems();
    assertEquals( 0, items.length );

    new TableItem( table, SWT.NONE );
    assertEquals( 2, table.getItemCount() );
    items = ItemHolder.getItemHolder( table ).getItems();
    assertEquals( 1, items.length );
  }

  /*
   * Disposing the items in reverse order (like in GTK) avoids performance critical operations like
   * shifting item/data arrays, item index recalculation etc.
   * see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=396172
   */
  @Test
  public void testSetItemCount_disposeInReverseOrder() {
    final List<String> log = new ArrayList<String>();
    createTableItems( table, 30 );
    for( TableItem item : table.getItems() ) {
      item.addDisposeListener( new DisposeListener() {
        public void widgetDisposed( DisposeEvent event ) {
          TableItem item = ( TableItem )event.getSource();
          log.add( item.getText() );
        }
      } );
    }

    table.setItemCount( 25 );

    String[] expected = { "item29", "item28", "item27", "item26", "item25" };
    assertArrayEquals( expected, log.toArray( new String[ 0 ] ) );
  }

  @Test
  public void testSetItemCount_redrawsItems() {
    table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 100, 100 );
    Listener listener = mock( Listener.class );
    table.addListener( SWT.SetData, listener );

    table.setItemCount( 25 );
    display.readAndDispatch();

    verify( listener, times( 4 ) ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testClearAllAndSetItemCountWithSelection() {
    shell.setSize( 100, 100 );
    shell.open();
    Table table = new Table( shell, SWT.VIRTUAL | SWT.SINGLE );
    table.setSize( 90, 90 );
    table.setItemCount( 10 );
    // force items to be resolved
    for( int i = 0; i < table.getItemCount(); i++ ) {
      table.getItem( i ).getText();
    }

    table.setSelection( 0 );
    TableItem selectedItem = table.getSelection()[ 0 ];
    table.clearAll();
    table.setItemCount( table.getItemCount() - 1 );

    assertEquals( 0, table.getSelectionIndex() );
    assertEquals( selectedItem, table.getSelection()[ 0 ] );
  }

  // bug 303473
  @Test
  public void testItemImageSizeAfterClear() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );
    item.setImage( createImage50x100() );
    table.clearAll();
    assertEquals( new Point( 0, 0 ), table.getItemImageSize() );
  }

  @Test
  public void testItemImageSizeAfterRemovingAllItems() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );
    item.setImage( createImage50x100() );
    item.dispose();
    assertEquals( new Point( 0, 0 ), table.getItemImageSize() );
  }

  @Test
  public void testSetItemCountWithSetDataListener() {
    shell.setSize( 100, 100 );
    shell.open();
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 90, 90 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        fail( "SetItemCount must not fire SetData events" );
      }
    } );
    // Ensure that setItemCount itself does not fire setData events
    table.setItemCount( 200 );
    assertEquals( 200, table.getItemCount() );
  }

  @Test
  public void testResizeWithVirtualItems() {
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 0, 0 );
    table.setItemCount( 1 );
    shell.open();

    // Ensure that a virtual item is not "realized" as long as it is invisible
    assertEquals( 0, ItemHolder.getItemHolder( table ).getItems().length );

    // Enlarge the table so that the item will become visible
    table.setSize( 200, 200 );
    assertEquals( 1, ItemHolder.getItemHolder( table ).getItems().length );
  }

  @Test
  public void testItemImageSize() throws IOException {
    // Test initial itemImageSize
    assertEquals( new Point( 0, 0 ), table.getItemImageSize() );

    // Setting a null-image shouldn't change anything
    TableItem item = new TableItem( table, SWT.NONE );
    item.setImage( ( Image )null );
    assertEquals( new Point( 0, 0 ), table.getItemImageSize() );

    // Setting the first image also sets the itemImageSize for always and ever
    item.setImage( createImage50x100() );
    assertEquals( new Point( 50, 100 ), table.getItemImageSize() );

    // Ensure that the itemImageSize - once detemined - does not change anymore
    item.setImage( createImage100x50() );
    assertEquals( new Point( 50, 100 ), table.getItemImageSize() );

    // Ensure that the method returns the actual image size, not clipped by the
    // available width given by the column
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.setWidth( 20 ); // image width is 50
    assertEquals( new Point( 50, 100 ), table.getItemImageSize() );
  }

  @Test
  public void testHasColumnImages() throws IOException {
    TableItem item0 = new TableItem( table, SWT.NONE );

    // Test without columns
    assertFalse( table.hasColumnImages( 0 ) );
    item0.setImage( ( Image )null );
    assertFalse( table.hasColumnImages( 0 ) );
    item0.setImage( createImage50x100() );
    assertTrue( table.hasColumnImages( 0 ) );
    item0.setImage( ( Image )null );
    assertFalse( table.hasColumnImages( 0 ) );
    item0.setImage( createImage50x100() );
    item0.dispose();
    assertFalse( table.hasColumnImages( 0 ) );

    item0 = new TableItem( table, SWT.NONE );
    item0.setImage( createImage50x100() );
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setImage( createImage50x100() );
    assertTrue( table.hasColumnImages( 0 ) );
    item1.setImage( ( Image )null );
    assertTrue( table.hasColumnImages( 0 ) );

    // Dispose column that 'holds' images
    table.removeAll();
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    item0 = new TableItem( table, SWT.NONE );
    item0.setImage( 1, createImage50x100() );
    assertFalse( table.hasColumnImages( 0 ) );
    assertTrue( table.hasColumnImages( 1 ) );
    column0.dispose();
    assertTrue( table.hasColumnImages( 0 ) );
    item0.setImage( 0, createImage50x100() );
    assertTrue( table.hasColumnImages( 0 ) );
    item0.setImage( 0, null );
    assertFalse( table.hasColumnImages( 0 ) );
  }

  @Test
  public void testHasColumnImagesAfterColumnDispose() {
    Table table = createTable( SWT.NONE, 1 );
    table.getColumn( 0 ).dispose();
    // No assertion here,
    // call hasColumnImages() to make sure the internal structures are OK
    table.hasColumnImages( 0 );
  }

  @Test
  public void testGetItem() {
    TableItem item;
    // Illegal argument
    try {
      table.getItem( null );
      fail( "Must not alow null-argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // test with empty table
    table.setSize( 100, 100 );
    item = table.getItem( new Point( 200, 200 ) );
    assertNull( item );
    item = table.getItem( new Point( 50, 50 ) );
    assertNull( item );
    item = table.getItem( new Point( -10, -20 ) );
    assertNull( item );
    // test with populated table
    table.setSize( 100, 100 );
    for( int i = 0; i < 100; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    assertNotNull( table.getItem( new Point( 5, 5 ) ) );
    item = table.getItem( new Point( 50, 50 ) );
    assertNotNull( item );
    item = table.getItem( new Point( 200, 200 ) );
    assertNull( item );
    item = table.getItem( new Point( -10, -20 ) );
    assertNull( item );
    item = table.getItem( new Point( 0, 0 ) );
    assertNotNull( item );
    assertEquals( 0, table.indexOf( item ) );
    item = table.getItem( new Point( 0, table.getItemHeight() ) );
    assertNotNull( item );
    assertEquals( 0, table.indexOf( item ) );
    // test with headers
    table.setHeaderVisible( true );
    item = table.getItem( new Point( 0, 0 ) );
    assertNull( item );
    item = table.getItem( new Point( 2, table.getHeaderHeight() + 3 ) );
    assertEquals( 0, item.getParent().indexOf( item ) );
  }

  /*
   * 283263: ArrayIndexOutOfBoundsException when clicking on the Pixel Row just
   *         below the Table Header.
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=283263
   */
  @Test
  public void testGetItemBelowHeader() {
    table.setHeaderVisible( true );
    table.setSize( 100, 100 );
    new TableItem( table, SWT.NONE );
    TableItem item = table.getItem( new Point( 10, table.getHeaderHeight() ) );
    assertNotNull( item );
    assertEquals( 0, table.indexOf( item ) );
  }

  /*
   * Ensures that checkData calls with an invalid index are silently ignored.
   * This may happen, when the itemCount is reduced during a SetData event.
   * Queued SetData events may then have stale (out-of-bounds) indices.
   * See 235368: [table] [table] ArrayIndexOutOfBoundsException in virtual
   *     TableViewer
   *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=235368
   */
  @Test
  public void testCheckDataWithInvalidIndex() {
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 10 );
    table.getAdapter( ITableAdapter.class ).checkData( 99 );
    // No assert - the purpose of this test is to ensure that no
    // ArrayIndexOutOfBoundsException is thrown
  }

  @Test
  public void testComputeSizeNonVirtual() {
    // Test non virtual table
    Point expected = new Point( 22, 74 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    for( int i = 0; i < 10; i++ ) {
      new TableItem( table, SWT.NONE ).setText( "Item " + i );
    }
    new TableItem( table, SWT.NONE ).setText( "Long long item 100" );
    expected = new Point( 137, 296 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    table = new Table( shell, SWT.BORDER );
    for( int i = 0; i < 10; i++ ) {
      new TableItem( table, SWT.NONE ).setText( "Item " + i );
    }
    new TableItem( table, SWT.NONE ).setText( "Long long item 10" );
    expected = new Point( 132, 298 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    table.setHeaderVisible( true );
    assertEquals( 31, table.getHeaderHeight() );
    expected = new Point( 132, 329 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    TableColumn col1 = new TableColumn( table, SWT.NONE );
    col1.setText( "Col 1" );
    TableColumn col2 = new TableColumn( table, SWT.NONE );
    col2.setText( "Column 2" );
    TableColumn col3 = new TableColumn( table, SWT.NONE );
    col3.setText( "Wider Column" );
    expected = new Point( 76, 329 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    col1.pack();
    col2.pack();
    col3.pack();
    expected = new Point( 279, 329 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    col1.setWidth( 10 );
    col2.setWidth( 10 );
    assertEquals( 87, col3.getWidth() );
    expected = new Point( 119, 329 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    table = new Table( shell, SWT.CHECK );
    for( int i = 0; i < 10; i++ ) {
      new TableItem( table, SWT.NONE ).setText( "Item " + i );
    }
    new TableItem( table, SWT.NONE ).setText( "Long long item 10" );
    expected = new Point( 155, 296 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 310, 310 );
    assertEquals( expected, table.computeSize( 300, 300 ) );
  }

  @Test
  public void testComputeSizeVirtual() {
    Table table = new Table( shell, SWT.BORDER | SWT.VIRTUAL );
    table.setItemCount( 10 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TableItem item = ( TableItem )event.item;
        int tableIndex = item.getParent().indexOf( item );
        item.setText( "Item " + tableIndex );
      }
    } );
    // 12 + srollbar (16) + 2 * border (`)
    assertEquals( 260, table.getItemCount() * table.getItemHeight() );
    Point expected = new Point( 24, 272 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    table.setHeaderVisible( true );
    assertEquals( 31, table.getHeaderHeight() );
    expected = new Point( 24, 303 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    TableColumn col1 = new TableColumn( table, SWT.NONE );
    col1.setText( "Col 1" );
    TableColumn col2 = new TableColumn( table, SWT.NONE );
    col2.setText( "Column 2" );
    TableColumn col3 = new TableColumn( table, SWT.NONE );
    col3.setText( "Wider Column" );
    expected = new Point( 76, 303 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // first table item is auto resolved
    col1.pack();
    col2.pack();
    col3.pack();
    expected = new Point( 205, 303 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    col1.setWidth( 10 );
    col2.setWidth( 10 );
    assertEquals( 87, col3.getWidth() );
    expected = new Point( 119, 303 );
    assertEquals( expected, table.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 312, 312 );
    assertEquals( expected, table.computeSize( 300, 300 ) );
  }

  @Test
  public void testComputeSizeNoScroll() {
    Table table = new Table( shell, SWT.NO_SCROLL );
    Point actual = table.computeSize( 20, 20 );
    Point expected = new Point( 20, 20 );
    assertEquals( expected, actual );
  }

  @Test
  public void testGetVisibleItemCount() {
    Table table = new Table( shell, SWT.NO_SCROLL );
    createTableItems( table, 10 );
    int itemHeight = table.getItemHeight();
    table.setSize( 100, 5 * itemHeight );
    assertEquals( 5, table.getVisibleItemCount( true ) );
    assertEquals( 5, table.getVisibleItemCount( false ) );
  }

  @Test
  public void testGetVisibleItemCount_WithBorder() {
    Table table = new Table( shell, SWT.NO_SCROLL | SWT.BORDER );
    createTableItems( table, 10 );
    int itemHeight = table.getItemHeight();
    int borderWidth = table.getBorderWidth();
    table.setSize( 100, 5 * itemHeight + 2 * borderWidth );
    assertEquals( 5, table.getVisibleItemCount( true ) );
    assertEquals( 5, table.getVisibleItemCount( false ) );
  }

  @Test
  public void testGetVisibleItemCountWithPartiallyVisibleItem() {
    Table table = new Table( shell, SWT.NO_SCROLL );
    createTableItems( table, 10 );
    int itemHeight = table.getItemHeight();
    table.setSize( 100, ( 5 * itemHeight ) + ( itemHeight / 2 ) );
    assertEquals( 6, table.getVisibleItemCount( true ) );
    assertEquals( 5, table.getVisibleItemCount( false ) );
  }

  @Test
  public void testGetItemHeight() throws IOException {
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setText( "Item 1" );
    // default font size (11) + hardcoded minimal vertical padding (4)
    assertEquals( 26, table.getItemHeight() );
    TableItem item2 = new TableItem( table, SWT.NONE );
    item2.setImage( createImage100x50() );
    // vertical padding defaults to 0
    assertEquals( 62, table.getItemHeight() );
  }

  @Test
  public void testNeedsScrollBarWithoutColumn() {
    table.setSize( 200, 200 );
    assertFalse( table.needsHScrollBar() );
    assertFalse( table.needsVScrollBar() );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "Item" );
    assertFalse( table.needsHScrollBar() );
    assertFalse( table.needsVScrollBar() );
    item.setText( "Very very very very very long item text " );
    assertTrue( table.needsHScrollBar() );
    assertFalse( table.needsVScrollBar() );
    createTableItems( table, 100 );
    assertTrue( table.needsHScrollBar() );
    assertTrue( table.needsVScrollBar() );
    item.setText( "Item" );
    assertFalse( table.needsHScrollBar() );
    assertTrue( table.needsVScrollBar() );
  }

  @Test
  public void testNeedsScrollBarWithColumn() {
    TableColumn column = new TableColumn( table, SWT.LEFT );
    table.setSize( 200, 200 );
    column.setWidth( 10 );
    assertFalse( table.needsHScrollBar() );
    assertFalse( table.needsVScrollBar() );
    column.setWidth( 220 );
    assertTrue( table.needsHScrollBar() );
    assertFalse( table.needsVScrollBar() );
    column.setWidth( 5 );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "Very very very very very long item text " );
    assertFalse( table.needsHScrollBar() );
    assertFalse( table.needsVScrollBar() );
  }

  @Test
  public void testHasScrollBar_NO_SCROLL() {
    Table table = new Table( shell, SWT.NO_SCROLL );
    table.setSize( 200, 200 );
    assertFalse( table.hasVScrollBar() );
    assertFalse( table.hasHScrollBar() );
    TableColumn column = new TableColumn( table, SWT.LEFT );
    column.setWidth( 220 );
    assertFalse( table.hasVScrollBar() );
    assertFalse( table.hasHScrollBar() );
  }

  @Test
  public void testGetScrollBarWidth() {
    table.setSize( 10, 10 );
    TableColumn column = new TableColumn( table, SWT.LEFT );
    column.setWidth( 20 );
    createTableItems( table, 10 );
    assertTrue( table.getVScrollBarWidth() > 0 );
    assertTrue( table.getHScrollBarHeight() > 0 );
    Table noScrollTable = new Table( shell, SWT.NO_SCROLL );
    noScrollTable.setSize( 200, 200 );
    assertEquals( 0, noScrollTable.getVScrollBarWidth() );
    assertEquals( 0, noScrollTable.getHScrollBarHeight() );
  }

  @Test
  public void testUpdateScrollBarOnColumnChange() {
    table.setSize( 20, 20 );
    assertFalse( table.hasHScrollBar() );
    TableColumn column = new TableColumn( table, SWT.LEFT );
    column.setWidth( 25 );
    assertTrue( table.hasHScrollBar() );
    column.pack();
    assertFalse( table.hasHScrollBar() );
    column.setWidth( 25 );
    assertTrue( table.hasHScrollBar() );
    column.dispose();
    assertFalse( table.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnItemsChange() {
    table.setSize( 20, 20 );
    assertFalse( table.hasVScrollBar() );
    createTableItems( table, 20 );
    assertTrue( table.hasVScrollBar() );
    table.removeAll();
    assertFalse( table.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnResize() {
    table.setSize( 20, 20 );
    TableColumn column = new TableColumn( table, SWT.LEFT );
    column.setWidth( 25 );
    assertTrue( table.hasHScrollBar() );
    table.setSize( 30, 30 );
    assertFalse( table.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnItemWidthChange() throws IOException {
    table.setSize( 60, 60 );
    TableItem item = new TableItem( table, SWT.NONE );
    assertFalse( table.hasHScrollBar() );
    item.setText( "Very long long long long long long long long text" );
    assertTrue( table.hasHScrollBar() );
    item.setText( "" );
    assertFalse( table.hasHScrollBar() );
    Image image = createImage100x50();
    item.setImage( image );
    assertTrue( table.hasHScrollBar() );
    item.setImage( ( Image )null );
    assertFalse( table.hasHScrollBar() );
    item.setText( "Very long long long long long long long long text" );
    item.setImage( image );
    table.clearAll();
    assertFalse( table.hasHScrollBar() );
    // change font
    item.setText( "short" );
    assertFalse( table.hasHScrollBar() );
    Font bigFont = new Font( display, "Helvetica", 50, SWT.BOLD );
    item.setFont( bigFont );
    assertTrue( table.hasHScrollBar() );
    item.setFont( null );
    assertFalse( table.hasHScrollBar() );
    table.setFont( bigFont );
    assertTrue( table.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnHeaderVisibleChange() {
    int itemCount = 5;
    createTableItems( table, itemCount );
    table.setSize( 100, itemCount * table.getItemHeight() + 4 );
    assertFalse( table.hasVScrollBar() );
    table.setHeaderVisible( true );
    assertTrue( table.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnVirtualItemCountChange() {
    Table table = new Table( shell, SWT.VIRTUAL );
    int itemCount = 5;
    table.setSize( 100, itemCount * table.getItemHeight() + 4 );
    table.setItemCount( itemCount );
    assertFalse( table.hasVScrollBar() );
    table.setItemCount( itemCount * 2 );
    assertTrue( table.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarItemWidthChangeWithColumn() throws IOException {
    table.setSize( 20, 100 );
    TableColumn column = new TableColumn( table, SWT.LEFT );
    column.setWidth( 10 );
    TableItem item = new TableItem( table, SWT.NONE );
    assertFalse( table.hasHScrollBar() );
    item.setText( "Very long long long long long long long long text" );
    assertFalse( table.hasHScrollBar() );
    item.setImage( createImage100x50() );
    assertFalse( table.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarWithInterDependencyHFirst() {
    TableColumn column = new TableColumn( table, SWT.LEFT );
    column.setWidth( 20 );
    new TableItem( table, SWT.NONE );
    table.setSize( 30, table.getItemHeight() + 4 );
    assertFalse( table.needsVScrollBar() );
    assertFalse( table.needsHScrollBar() );
    assertFalse( table.hasHScrollBar() );
    assertFalse( table.hasVScrollBar() );
    column.setWidth( 40 );
    assertTrue( table.hasHScrollBar() );
    assertTrue( table.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarWithInterDependencyVFirst() {
    TableColumn column = new TableColumn( table, SWT.LEFT );
    column.setWidth( 26 );
    table.setSize( 30, 30 );
    assertFalse( table.hasHScrollBar() );
    assertFalse( table.hasVScrollBar() );
    createTableItems( table, 10 );
    assertTrue( table.hasHScrollBar() );
    assertTrue( table.hasVScrollBar() );
  }

  @Test
  public void testGetMeasureItemWithoutColumnsVirtual() {
    final String[] data = new String[ 1000 ];
    for( int i = 0; i < data.length; i++ ) {
      data[ i ] = "";
    }
    Listener setDataListener = new Listener() {
      public void handleEvent( Event event ) {
        TableItem item = ( TableItem )event.item;
        int index = item.getParent().indexOf( item );
        item.setText( data[ index ] );
      }
    };
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.addListener( SWT.SetData, setDataListener );
    table.setItemCount( data.length );
    table.setSize( 90, 90 );
    shell.open();
    int resolvedItemCount;
    TableItem measureItem;
    // Test with items that all have the same width
    resolvedItemCount = countResolvedItems( table );
    measureItem = table.getMeasureItem();
    assertNotNull( measureItem );
    assertEquals( resolvedItemCount, countResolvedItems( table ) );
    // Test with items that have ascending length
    data[ 0 ] = "a";
    for( int i = 1; i < data.length; i++ ) {
      data[ i ] = data[ i - 1 ] + "a";
    }
    table.getItem( 100 ).getText(); // resolves item
    resolvedItemCount = countResolvedItems( table );
    measureItem = table.getMeasureItem();
    int measureItemIndex = measureItem.getParent().indexOf( measureItem );
    assertEquals( 100, measureItemIndex );
    assertEquals( resolvedItemCount, countResolvedItems( table ) );
  }

  @Test
  public void testIndexOf() {
    shell.setSize( 100, 100 );
    TableItem item1 = new TableItem( table, SWT.NONE );
    assertEquals( 0, table.indexOf( item1 ) );
    TableItem item0 = new TableItem( table, SWT.NONE, 0 );
    assertEquals( 0, table.indexOf( item0 ) );
    assertEquals( 1, table.indexOf( item1 ) );
    table.removeAll();
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    TableItem itemBefore = new TableItem( table, SWT.NONE );
    // -> this is the place for 'newItem'
    TableItem itemAfter = new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    TableItem newItem = new TableItem( table, SWT.NONE, 3 );
    assertEquals( 3, table.indexOf( newItem ) );
    assertEquals( 2, table.indexOf( itemBefore ) );
    assertEquals( 4, table.indexOf( itemAfter ) );
    newItem.dispose();
    assertEquals( -1, table.indexOf( newItem ) );
    assertEquals( 2, table.indexOf( itemBefore ) );
    assertEquals( 3, table.indexOf( itemAfter ) );
    table.remove( table.getItemCount() - 1 );
    assertEquals( 3, table.indexOf( itemAfter ) );
  }

  @Test
  public void testIndexOfVirtual() {
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 10 );
    TableItem item = table.getItem( 0 );
    assertEquals( 0, table.indexOf( item ) );
    item = table.getItem( 5 );
    assertEquals( 5, table.indexOf( item ) );
    item = table.getItem( 9 );
    assertEquals( 9, table.indexOf( item ) );
    // ensure that updating null-items does not throw NPE
    table.remove( 5 );
  }

  @Test
  public void testShowColumn() {
    table.setSize( 325, 100 );
    for( int i = 0; i < 10; i++ ) {
      TableColumn column = new TableColumn( table, SWT.NONE );
      column.setWidth( 50 );
    }
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    assertEquals( 0, adapter.getLeftOffset() );
    table.showColumn( table.getColumn( 8 ) );
    assertEquals( 175, adapter.getLeftOffset() );
    table.showColumn( table.getColumn( 1 ) );
    assertEquals( 50, adapter.getLeftOffset() );
    table.showColumn( table.getColumn( 3 ) );
    assertEquals( 50, adapter.getLeftOffset() );

    table.getColumn( 3 ).dispose();
    table.setColumnOrder( new int[] { 8, 7, 0, 1, 2, 3, 6, 5, 4 } );
    table.showColumn( table.getColumn( 8 ) );
    assertEquals( 0, adapter.getLeftOffset() );
    table.showColumn( table.getColumn( 5 ) );
    assertEquals( 125, adapter.getLeftOffset() );
  }

  @Test
  public void testShowColumnWithReorderedColumns() {
    table.setSize( 325, 100 );
    for( int i = 0; i < 9; i++ ) {
      TableColumn column = new TableColumn( table, SWT.NONE );
      column.setWidth( 50 );
    }

    table.setColumnOrder( new int[] { 8, 7, 0, 1, 2, 3, 6, 5, 4 } );
    table.showColumn( table.getColumn( 8 ) );

    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    assertEquals( 0, adapter.getLeftOffset() );

    table.showColumn( table.getColumn( 5 ) );
    assertEquals( 125, adapter.getLeftOffset() );
  }

  @Test
  public void testShowColumnWithNullArgument() {
    try {
      table.showColumn( null );
      fail( "Null argument not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testShowColumnWithDisposedColumn() {
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.dispose();
    try {
      table.showColumn( column );
      fail( "Disposed column not allowed as argument" );
    } catch( IllegalArgumentException expeted ) {
    }
  }

  @Test
  public void testShowColumnWithForeignColumn() {
    int initialLeftOffset = 123456;
    Table table = createTable( SWT.NONE, 1 );
    table.leftOffset = initialLeftOffset;
    Table otherTable = new Table( shell, SWT.NONE );
    TableColumn otherColumn = new TableColumn( otherTable, SWT.NONE );

    table.showColumn( otherColumn );

    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    assertEquals( initialLeftOffset, adapter.getLeftOffset() );
  }

  @Test
  public void testShowFixedColumn() {
    shell.setSize( 800, 600 );
    Table table = createFixedColumnsTable();
    table.setSize( 300, 100 );
    for( int i = 0; i < 10; i++ ) {
      TableColumn column = new TableColumn( table, SWT.NONE );
      column.setWidth( 50 );
    }
    createTableItems( table, 10 );
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );

    adapter.setLeftOffset( 100 );
    table.showColumn( table.getColumn( 0 ) );

    assertEquals( 100, adapter.getLeftOffset() );
  }

  @Test
  public void testFixedColumnsNotSetWithRowTemplate() {
    Table table = createFixedColumnsTable();

    table.setData( RWT.ROW_TEMPLATE, new Template() );

    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    assertEquals( -1, adapter.getFixedColumns() );
  }

  @Test
  public void testShowColumnWithFixedColumns_ScrolledToLeft() {
    Table table = createFixedColumnsTable();
    int numColumns = 4;
    int columnWidth = 100;
    table.setSize( columnWidth * ( numColumns - 1 ), 100 );
    for( int i = 0; i < numColumns; i++ ) {
      TableColumn column = new TableColumn( table, SWT.NONE );
      column.setWidth( columnWidth );
    }
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 100 );

    table.showColumn( table.getColumn( 2 ) );

    assertEquals( 0, adapter.getLeftOffset() );
  }

  @Test
  public void testShowColumnWithFixedColumns_ScrolledToRight() {
    int numColumns = 4;
    int columnWidth = 100;
    Table table = createFixedColumnsTable();
    table.setSize( columnWidth  * ( numColumns - 1 ), 100 );
    for( int i = 0; i < numColumns; i++ ) {
      TableColumn column = new TableColumn( table, SWT.NONE );
      column.setWidth( columnWidth );
    }

    table.showColumn( table.getColumn( 3 ) );

    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    assertEquals( 100, adapter.getLeftOffset() );
  }

  @Test
  public void testScrollBars_NONE() {
    assertNotNull( table.getHorizontalBar() );
    assertNotNull( table.getVerticalBar() );
  }

  @Test
  public void testScrollBars_NO_SCROLL() {
    Table table = new Table( shell, SWT.NO_SCROLL );
    assertNull( table.getHorizontalBar() );
    assertNull( table.getVerticalBar() );
  }

  // 288634: [Table] TableItem images are not displayed if columns are created
  // after setInput
  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=288634
  @Test
  public void testUpdateColumnImageCount() throws IOException {
    shell.setSize( 100, 100 );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( new String[] { "col 1", "col 2", "col 3" } );
    Image image = createImage50x100();
    item.setImage( new Image[] { image, null, image } );
    assertTrue( table.hasColumnImages( 0 ) );
    TableColumn col1 = new TableColumn( table, SWT.NONE );
    col1.setText( "header 1" );
    col1.setWidth( 30 );
    TableColumn col2 = new TableColumn( table, SWT.NONE );
    col2.setText( "header 2" );
    col2.setWidth( 30 );
    TableColumn col3 = new TableColumn( table, SWT.NONE );
    col3.setText( "header 3" );
    col3.setWidth( 30 );
    assertTrue( table.hasColumnImages( 0 ) );
    assertFalse( table.hasColumnImages( 1 ) );
    assertFalse( table.hasColumnImages( 2 ) );
  }

  // 239024: {TableViewer] Missing text due to TableViewerColumn
  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=239024
  @Test
  public void testGetItemsPreferredWidth() {
    Table table = createTable( SWT.NONE, 2 );
    assertEquals( 12, table.getItemsPreferredWidth( 0 ) );
    assertEquals( 12, table.getItemsPreferredWidth( 1 ) );
  }

  @Test
  public void testGetItemsPreferredWidth_withCheck() {
    Table table = createTable( SWT.CHECK, 2 );
    // 33 = 21 ( check width ) + 12
    assertEquals( 37, table.getItemsPreferredWidth( 0 ) );
    assertEquals( 12, table.getItemsPreferredWidth( 1 ) );
  }

  @Test
  public void testGetItemsPreferredWidth_withMarkup() {
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "<b>foo</b>" );
    int width1 = table.getItemsPreferredWidth( 0 );

    item.clearTextWidths();
    table.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    int width2 = table.getItemsPreferredWidth( 0 );

    assertTrue( width1 > width2 );
  }

  @Test
  public void testRemoveArrayDuplicates() {
    createTableItems( table, 5 );
    assertEquals( 5, table.getItemCount() );
    table.remove( new int[]{ 1, 1 } );
    assertEquals( 4, table.getItemCount() );
    table.remove( new int[]{ 0, 2, 1, 1 } );
    assertEquals( 1, table.getItemCount() );
  }

  @Test
  public void testReskinDoesNotResolveVirtualItems() {
    final java.util.List<Event> eventLog = new LinkedList<Event>();
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 1000 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    redrawTable( table );
    eventLog.clear();

    table.reskin( SWT.ALL );

    assertEquals( 0, eventLog.size() );
  }

  @Test
  public void testTemporaryResizeDoesNotResolveVirtualItems() {
    final java.util.List<Event> eventLog = new LinkedList<Event>();
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 1000 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    redrawTable( table );
    eventLog.clear();

    markTemporaryResize();
    table.setSize( 1000, 1000 );

    assertEquals( 0, eventLog.size() );
  }

  @Test
  public void testIsSerializable() throws Exception {
    Table table = createTable( SWT.VIRTUAL, 1 );
    new TableItem( table, 0 );

    Table deserializedTable = Fixture.serializeAndDeserialize( table );

    assertEquals( 1, deserializedTable.getItemCount() );
    assertEquals( 1, deserializedTable.getColumnCount() );
  }

  @Test
  public void testSetCustomItemHeight() {
    table.setData( RWT.CUSTOM_ITEM_HEIGHT, new Integer( 123 ) );
    assertEquals( 123, table.getItemHeight() );
  }

  @Test
  public void testGetCustomItemHeight() {
    Integer itemHeight = new Integer( 123 );
    table.setData( RWT.CUSTOM_ITEM_HEIGHT, itemHeight );

    Object returnedItemHeight = table.getData( RWT.CUSTOM_ITEM_HEIGHT );

    assertEquals( itemHeight, returnedItemHeight );
  }

  @Test
  public void testResetCustomItemHeight() {
    int calculatedItemHeight = table.getItemHeight();
    table.setData( RWT.CUSTOM_ITEM_HEIGHT, new Integer( 123 ) );
    table.setData( RWT.CUSTOM_ITEM_HEIGHT, null );
    assertEquals( calculatedItemHeight, table.getItemHeight() );
  }

  @Test
  public void testDefaultCustomItemHeight() {
    assertEquals( 26, table.getItemHeight() );
  }

  @Test
  public void testSetCustomItemHeightWithNegativeValue() {
    try {
      table.setData( RWT.CUSTOM_ITEM_HEIGHT, new Integer( -1 ) );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSetCustomItemHeightWithNonIntegerValue() {
    try {
      table.setData( RWT.CUSTOM_ITEM_HEIGHT, new Object() );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSetCellToolTipText() {
    ICellToolTipAdapter adapter = table.getAdapter( ICellToolTipAdapter.class );

    adapter.setCellToolTipText( "foo" );

    assertEquals( "foo", adapter.getCellToolTipText() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetCellToolTipText_withToolTipMarkupEnabled_invalid() {
    table.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    ICellToolTipAdapter adapter = table.getAdapter( ICellToolTipAdapter.class );

    adapter.setCellToolTipText( "invalid xhtml: <<&>>" );
  }

  @Test
  public void testSetCellToolTipText_withToolTipMarkupEnabled_invalidWithDisabledValidation() {
    table.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    table.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );
    ICellToolTipAdapter adapter = table.getAdapter( ICellToolTipAdapter.class );

    try {
      adapter.setCellToolTipText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testMarkupTextWithoutMarkupEnabled() {
    table.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );
    TableItem item = new TableItem( table, SWT.NONE );

    try {
      item.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test( expected = IllegalArgumentException.class )
  public void testMarkupTextWithMarkupEnabled() {
    table.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    TableItem item = new TableItem( table, SWT.NONE );

    item.setText( "invalid xhtml: <<&>>" );
  }

  @Test
  public void testMarkupTextWithMarkupEnabled_ValidationDisabled() {
    table.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    table.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );
    TableItem item = new TableItem( table, SWT.NONE );

    try {
      item.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testDisableMarkupIsIgnored() {
    table.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    table.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    assertEquals( Boolean.TRUE, table.getData( RWT.MARKUP_ENABLED ) );
  }

  @Test
  public void testSetData() {
    table.setData( "foo", "bar" );

    assertEquals( "bar", table.getData( "foo" ) );
  }

  @Test
  public void testAddSelectionListener() {
    table.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( table.isListening( SWT.Selection ) );
    assertTrue( table.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    SelectionListener listener = mock( SelectionListener.class );
    table.addSelectionListener( listener );

    table.removeSelectionListener( listener );

    assertFalse( table.isListening( SWT.Selection ) );
    assertFalse( table.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddSelectionListenerWithNullArgument() {
    table.addSelectionListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveSelectionListenerWithNullArgument() {
    table.removeSelectionListener( null );
  }

  @Test
  public void testDisposeCellEditor() {
    Text cellEditor = new Text( table, SWT.NONE );

    table.dispose();

    assertTrue( cellEditor.isDisposed() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetPreloadedItems_invalidValue() {
    table.setData( RWT.PRELOADED_ITEMS, new Object() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetPreloadedItems_negativeValue() {
    table.setData( RWT.PRELOADED_ITEMS, Integer.valueOf( -1 ) );
  }

  @Test
  public void testResolvedItems_withoutPreloadedItemsSet() {
    table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 100, 100 );
    table.setItemCount( 200 );

    table.setTopIndex( 100 );
    redrawTable( table );

    assertEquals( 4, countResolvedItems( table ) );
  }

  @Test
  public void testResolvedItems_zeroPreloadedItems() {
    table = new Table( shell, SWT.VIRTUAL );
    table.setData( RWT.PRELOADED_ITEMS, Integer.valueOf( 0 ) );
    table.setSize( 100, 100 );
    table.setItemCount( 200 );

    table.setTopIndex( 100 );
    redrawTable( table );

    assertEquals( 4, countResolvedItems( table ) );
  }

  @Test
  public void testResolvedItems_10PreloadedItems() {
    table = new Table( shell, SWT.VIRTUAL );
    table.setData( RWT.PRELOADED_ITEMS, Integer.valueOf( 10 ) );
    table.setSize( 100, 100 );
    table.setItemCount( 200 );

    table.setTopIndex( 100 );
    redrawTable( table );

    // visible (4) + above visible area( 10 ) + below visible area (10)
    assertEquals( 24, countResolvedItems( table ) );
  }

  @Test
  public void testResolvedItems_300PreloadedItems() {
    table = new Table( shell, SWT.VIRTUAL );
    table.setData( RWT.PRELOADED_ITEMS, Integer.valueOf( 200 ) );
    table.setSize( 100, 100 );
    table.setItemCount( 200 );

    table.setTopIndex( 100 );
    redrawTable( table );

    // all items preloaded
    assertEquals( 200, countResolvedItems( table ) );
  }

  private Image createImage50x100() throws IOException {
    InputStream stream = Fixture.class.getClassLoader().getResourceAsStream( Fixture.IMAGE_50x100 );
    Image result = new Image( display, stream );
    stream.close();
    return result;
  }

  private Image createImage100x50() throws IOException {
    InputStream stream = Fixture.class.getClassLoader().getResourceAsStream( Fixture.IMAGE_100x50 );
    Image result = new Image( display, stream );
    stream.close();
    return result;
  }

  private Table createTable( int style, int columnCount ) {
    Table result = new Table( shell, style );
    for( int i = 0; i < columnCount; i++ ) {
      new TableColumn( result, SWT.NONE );
    }
    return result;
  }

  private static TableItem[] createTableItems( Table table, int number ) {
    TableItem[] result = new TableItem[ number ];
    for( int i = 0; i < number; i++ ) {
      result[ i ] = new TableItem( table, SWT.NONE );
      result[ i ].setText( "item" + i );
    }
    return result;
  }

  private static boolean find( int element, int[] array ) {
    boolean result = false;
    for( int i = 0; i < array.length; i++ ) {
      if( element == array[ i ] ) {
        result = true;
      }
    }
    return result;
  }

  private static int countResolvedItems( Table table ) {
    ITableAdapter tableAdapter = table.getAdapter( ITableAdapter.class );
    int result = 0;
    TableItem[] createdItems = tableAdapter.getCreatedItems();
    for( int i = 0; i < createdItems.length; i++ ) {
      int index = table.indexOf( createdItems[ i ] );
      if( !tableAdapter.isItemVirtual( index ) ) {
        result++;
      }
    }
    return result;
  }

  private static void clearColumns( Table table ) {
    while( table.getColumnCount() > 0 ) {
      table.getColumn( 0 ).dispose();
    }
  }

  private static void redrawTable( Table table ) {
    table.getAdapter( ITableAdapter.class ).checkData();
  }

  private Table createFixedColumnsTable() {
    Table result = new Table( shell, SWT.NONE );
    result.setData( RWT.FIXED_COLUMNS, new Integer( 2 ) );
    return result;
  }

  private Table createMultiLineHeaderTable() {
    Table result = new Table( shell, SWT.NONE );
    for( int i = 0; i < 3; i++ ) {
      TableColumn column = new TableColumn( result, SWT.NONE );
      column.setWidth( 50 );
      column.setText( "Column " + i );
    }
    return result;
  }

  private void markTemporaryResize() {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    String key = "org.eclipse.rap.rwt.internal.textsize.TextSizeRecalculation#temporaryResize";
    serviceStore.setAttribute( key, Boolean.TRUE );
  }

}
