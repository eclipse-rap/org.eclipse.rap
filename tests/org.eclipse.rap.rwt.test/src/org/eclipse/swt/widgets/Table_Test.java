/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;

import com.w4t.engine.lifecycle.PhaseId;

public class Table_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Table table = new Table( shell, SWT.NONE );
    
    assertEquals( false, table.getHeaderVisible() );
    assertEquals( false, table.getLinesVisible() );
    assertEquals( 0, table.getSelectionCount() );
    assertEquals( 0, table.getSelectionIndices().length );
    assertEquals( 0, table.getSelection().length );
    assertEquals( 0, table.getTopIndex() );
    assertNull( table.getSortColumn() );
    assertEquals( SWT.NONE, table.getSortDirection() );
  }
  
  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Table table = new Table( shell, SWT.NONE );
    assertTrue( ( table.getStyle() & SWT.H_SCROLL ) != 0 );
    assertTrue( ( table.getStyle() & SWT.V_SCROLL ) != 0 );
    assertTrue( ( table.getStyle() & SWT.SINGLE ) != 0 );

    table = new Table( shell, SWT.SINGLE | SWT.MULTI );
    assertTrue( ( table.getStyle() & SWT.SINGLE ) != 0 );

    table = new Table( shell, SWT.SINGLE | SWT.SINGLE );
    assertTrue( ( table.getStyle() & SWT.SINGLE ) != 0 );

    table = new Table( shell, SWT.SINGLE | SWT.MULTI );
    assertTrue( ( table.getStyle() & SWT.SINGLE ) != 0 );
  }

  public void testTableCreation() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Table table = new Table( shell, SWT.NONE );
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
    } catch( final IllegalArgumentException iae ) {
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
    } catch( final IllegalArgumentException iae ) {
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
  
  public void testHeaderHeight() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    assertEquals( 0, table.getHeaderHeight() );
    table.setHeaderVisible( true );
    assertTrue( table.getHeaderHeight() > 0 );
  }
  
  public void testTableItemTexts() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Table table = new Table( shell, SWT.NONE );
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
    } catch( final NullPointerException npe ) {
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
  
  public void testTopIndex() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Table table = new Table( shell, SWT.NONE );
    new TableItem( table, SWT.NONE );
    TableItem lastItem = new TableItem( table, SWT.NONE );

    // Set a value which is out of bounds
    int previousTopIndex = table.getTopIndex();
    table.setTopIndex( 10000 );
    assertEquals( previousTopIndex, table.getTopIndex() );
    
    // Set topIndex to the second item
    table.setTopIndex( 1 );
    assertEquals( 1, table.getTopIndex() );
    
    // Remove last item (whose index equals topIndex) -> must adjust topIndex
    table.setTopIndex( table.indexOf( lastItem ) );
    lastItem.dispose();
    assertEquals( 0, table.getTopIndex() );
    
    // Ensure that topIndex stays at least 0 even if all items are removed
    table.removeAll();
    TableItem soleItem = new TableItem( table, SWT.NONE );
    soleItem.dispose();
    assertEquals( 0, table.getTopIndex() );
  }
  
  public void testDisposeSingleSelectedItem() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.SINGLE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    
    table.setSelection( new TableItem[] { item } );
    item.dispose();
    assertEquals( -1, table.getSelectionIndex() );
    assertEquals( 0, table.getItemCount() );
    assertEquals( 0, table.getSelectionCount() );
    assertEquals( 0, table.getSelection().length );
  }
  
  public void testDisposeMultiSelectedItem() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.MULTI );
    new TableColumn( table, SWT.NONE );
    TableItem item0 = new TableItem( table, SWT.NONE );
    TableItem item1 = new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    
    table.setSelection( new TableItem[] { item0, item1 } );
    item0.dispose();
    assertEquals( table.indexOf( item1 ), table.getSelectionIndex() );
    assertEquals( 2, table.getItemCount() );
    assertEquals( 1, table.getSelectionCount() );
  }
  
  public void testFocusIndex() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
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
  }
  
  public void testRemoveAll() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
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
  
  public void testRemoveRange() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    int number = 5;
    TableItem[] items = new TableItem[ number ];
    for( int i = 0; i < number; i++ )
      items[ i ] = new TableItem( table, 0 );
    try {
      table.remove( -number, number + 100 );
      fail( "No exception thrown for illegal index range" );
    } catch( IllegalArgumentException e ) {
    }
    table = new Table( shell, SWT.NONE );
    items = new TableItem[ number ];
    for( int i = 0; i < number; i++ )
      items[ i ] = new TableItem( table, 0 );
    table.remove( 2, 3 );
    assertTrue( items[ 2 ].isDisposed() );
    assertTrue( items[ 3 ].isDisposed() );
    assertEquals( table.getItemCount(), 3 );
  }

  public void testRemove() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    int number = 5;
    TableItem[] items = new TableItem[ number ];
    for( int i = 0; i < number; i++ )
      items[ i ] = new TableItem( table, 0 );
    table.remove( 1 );
    assertTrue( items[ 1 ].isDisposed() );
    assertEquals( table.getItemCount(), 4 );
  }

  public void testRemoveArray() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    int number = 15;
    TableItem[] items = new TableItem[ number ];
    for( int i = 0; i < number; i++ )
      items[ i ] = new TableItem( table, 0 );
    try {
      table.remove( null );
      fail( "No exception thrown for tableItems == null" );
    } catch( NullPointerException e ) {
      // Illegal Argument exception in SWT
      // NPE in RWT - see SWT error code to exception switch
    }
    try {
      table.remove( new int[]{
        2, 1, 0, -100, 5, 5, 2, 1, 0, 0, 0
      } );
      fail( "No exception thrown for illegal index arguments" );
    } catch( IllegalArgumentException e ) {
    }
    try {
      table.remove( new int[]{
        2, 1, 0, number, 5, 5, 2, 1, 0, 0, 0
      } );
      fail( "No exception thrown for illegal index arguments" );
    } catch( IllegalArgumentException e ) {
    }
    table.remove( new int[]{} );
    table = new Table( shell, SWT.NONE );
    for( int i = 0; i < number; i++ )
      items[ i ] = new TableItem( table, 0 );
    assertTrue( ":a:", !items[ 2 ].isDisposed() );
    table.remove( new int[]{
      2
    } );
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
  
  public void testSingleSelection() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.SINGLE );
    TableItem item1 = new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    
    // Test setSelection(int)
    table.deselectAll();
    table.setSelection( 0 );
    assertEquals( true, table.isSelected( 0 ) );

    table.setSelection( table.getItemCount() + 20 );
    assertEquals( 0, table.getSelectionCount() );
    
    // Test setSelection(int,int)
    table.deselectAll();
    table.setSelection( 0, 0 );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 0 );
    table.setSelection( 0, 2 );
    assertEquals( 0, table.getSelectionCount() );
    
    // Test setSelection(int[])
    table.deselectAll();
    table.setSelection( new int[]{ 0 } );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );

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
    } catch( NullPointerException e ) {
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
    assertEquals( true, table.isSelected( 0 ) );
    table.select( 1 );
    assertEquals( false, table.isSelected( 0 ) );
    assertEquals( true, table.isSelected( 1 ) );

    table.deselectAll();
    table.select( 0 );
    table.select( table.getItemCount() + 20 );
    assertEquals( true, table.isSelected( 0 ) );
    
    // Test select(int,int)
    table.deselectAll();
    table.select( 0, 0 );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( 0, 1 );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 2 ) );
    
    // Test select(int[])
    table.deselectAll();
    table.select( new int[]{ 0 } );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( new int[]{ 0, 1 } );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 2 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( new int[]{ 777 } );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 2 ) );
  }
  
  public void testMultiSelection() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.MULTI );
    TableItem item1 = new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    
    // Test setSelection(int)
    table.deselectAll();
    table.setSelection( 0 );
    assertEquals( true, table.isSelected( 0 ) );

    table.setSelection( table.getItemCount() + 20 );
    assertEquals( 0, table.getSelectionCount() );
    
    // Test setSelection(int,int)
    table.deselectAll();
    table.setSelection( 0, 0 );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 1 );
    table.setSelection( 0, 2 );
    assertEquals( 3, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );
    assertEquals( true, table.isSelected( 1 ) );
    assertEquals( true, table.isSelected( 2 ) );
    assertEquals( false, table.isSelected( 3 ) );
    
    table.deselectAll();
    table.setSelection( 0 );
    table.setSelection( 1, 777 );
    assertEquals( false, table.isSelected( 0 ) );
    assertEquals( true, table.isSelected( 1 ) );
    assertEquals( true, table.isSelected( 2 ) );
    assertEquals( true, table.isSelected( 3 ) );
    
    // Test setSelection(int[])
    table.deselectAll();
    table.setSelection( new int[]{ 0 } );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.setSelection( new int[]{ 0, 1 } );
    assertEquals( 2, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );
    assertEquals( true, table.isSelected( 1 ) );
    assertEquals( false, table.isSelected( 2 ) );
    assertEquals( false, table.isSelected( 3 ) );

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
    } catch( NullPointerException e ) {
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
    assertEquals( true, table.isSelected( 0 ) );
    table.select( 1 );
    assertEquals( 2, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );
    assertEquals( true, table.isSelected( 1 ) );

    table.deselectAll();
    table.select( 0 );
    table.select( table.getItemCount() + 20 );
    assertEquals( true, table.isSelected( 0 ) );
    
    // Test select(int,int)
    table.deselectAll();
    table.select( 0, 0 );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( 0, 1 );
    assertEquals( 3, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );
    assertEquals( true, table.isSelected( 1 ) );
    assertEquals( true, table.isSelected( 2 ) );
    
    // Test select(int[])
    table.deselectAll();
    table.select( new int[]{ 0 } );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( new int[]{ 0, 1, 777 } );
    assertEquals( 3, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 0 ) );
    assertEquals( true, table.isSelected( 1 ) );
    assertEquals( true, table.isSelected( 2 ) );

    table.deselectAll();
    table.setSelection( 2 );
    table.select( new int[]{ 777 } );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 2 ) );
  }
  
  public void testGetSelectionIndex() {
    Display display = new Display();
    Shell shell = new Shell( display );
    
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
  
  public void testSelectAll() {
    Display display = new Display();
    Shell shell = new Shell( display );
    
    // SWT.SINGLE
    Table singleTable = new Table( shell, SWT.SINGLE );
    new TableColumn( singleTable, SWT.NONE );
    new TableItem( singleTable, SWT.NONE );
    new TableItem( singleTable, SWT.NONE );
    singleTable.deselectAll();
    singleTable.selectAll();
    assertEquals( 0, singleTable.getSelectionCount() );
    singleTable.setSelection( 1 );
    assertEquals( 1, singleTable.getSelectionCount() );
    
    // SWT.MULTI
    Table multiTable = new Table( shell, SWT.MULTI );
    new TableColumn( multiTable, SWT.NONE );
    new TableItem( multiTable, SWT.NONE );
    new TableItem( multiTable, SWT.NONE );
    multiTable.selectAll();
    assertEquals( multiTable.getItemCount(), multiTable.getSelectionCount() );
  }
  
  public void testDeselect() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.SINGLE );
    new TableColumn( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    
    // deselect(int)
    table.setSelection( 0 );
    table.deselect( 0 );
    assertEquals( 0, table.getSelectionCount() );
    
    // deselect(int,int)
    table.setSelection( 1 );
    table.deselect( 0, 777 );
    assertEquals( 0, table.getSelectionCount() );
    table.setSelection( 1 );
    table.deselect( 4, 777 );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 1 ) );
    
    // deselect(int[])
    table.setSelection( 1 );
    table.deselect( new int[ 0 ] );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( true, table.isSelected( 1 ) );

    table.setSelection( 1 );
    table.deselect( new int[] { 1, 777 } );
    assertEquals( 0, table.getSelectionCount() );
}

  public void testDeselectAll() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );

    table.setSelection( 1 );
    table.deselectAll();
    assertEquals( -1, table.getSelectionIndex() );
  }
  
  public void testClear() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.CHECK );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    item.setText( "abc" );
    item.setImage( Image.find( RWTFixture.IMAGE1 ) );
    item.setChecked( true );
    item.setGrayed( true );
    table.clear( table.indexOf( item ) );
    assertEquals( "", item.getText() );
    assertEquals( null, item.getImage() );
    assertEquals( false, item.getChecked() );
    assertEquals( false, item.getGrayed() );
    
    // Test clear with illegal arguments
    try {
      table.clear( 2 );
      fail( "Must throw exception when attempting to clear non-existing item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testClearRange() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem[] items = new TableItem[ 10 ];
    for( int i = 0; i < 10; i++ ) {
      items[ i ] = new TableItem( table, SWT.NONE );
      items[ i ].setText( "abc" );
      items[ i ].setImage( Image.find( RWTFixture.IMAGE1 ) );
    }
    table.clear( 2, 5 );
    Image img = Image.find( RWTFixture.IMAGE1 );
    for( int i = 0; i < 10; i++ ) {
      if( i >= 2 && i <= 5 ) {
        assertEquals( "", items[ i ].getText() );
        assertEquals( null, items[ i ].getImage() );
      } else {
        assertEquals( "abc", items[ i ].getText() );
        assertEquals( img, items[ i ].getImage() );
      }
    }
    // Test clear with illegal arguments (end > items)
    try {
      table.clear( 1, 11 );
      fail( "Must throw exception when attempting to clear non-existing items" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testClearIndices() {
	  Display display = new Display();
	  Shell shell = new Shell( display );
	  Table table = new Table( shell, SWT.NONE );
	  new TableColumn( table, SWT.NONE );

	  TableItem[] items = new TableItem[10];
	  for (int i = 0; i < 10; i++) {
		  items[i] = new TableItem( table, SWT.NONE );
		  items[i].setText( "abc" );
		  items[i].setImage( Image.find( RWTFixture.IMAGE1 ) );
	  }
	  
	  table.clear( new int[]{ 1, 3, 5 } );
    Image img = Image.find( RWTFixture.IMAGE1 );
    for( int i = 0; i < 10; i++ ) {
      if( i == 1 || i == 3 || i == 5 ) {
        assertEquals( "", items[ i ].getText() );
        assertEquals( null, items[ i ].getImage() );
      } else {
        assertEquals( "abc", items[ i ].getText() );
        assertEquals( img, items[ i ].getImage() );
      }
    }
    // Test clear with illegal arguments
    try {
      table.clear( new int[]{ 2, 4, 15 } );
      fail( "Must throw exception when attempting to clear non-existing items" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testShowItem() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    table.setLinesVisible( false );
    table.setHeaderVisible( false );
    new TableColumn( table, SWT.NONE );
    int itemCount = 300;
    for( int i = 0; i < itemCount; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    int itemHeight = table.getItem( 0 ).getBounds().height;
    int visibleLines = 100;
    table.setSize( 100, visibleLines * itemHeight );
    
    table.showItem( table.getItem( 100 ) );
    assertEquals( 4, table.getTopIndex() );
    
    table.showItem( table.getItem( 0 ) );
    assertEquals( 0, table.getTopIndex() );
    
    table.showItem( table.getItem( itemCount - 1 ) );
    assertEquals( 203, table.getTopIndex() );
    
    table.showItem( table.getItem( 0 ) );
    assertEquals( 0, table.getTopIndex() );
  }
  
  public void testSortColumnAndDirection() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );
    
    // Simple case set == get
    table.setSortColumn( column );
    assertSame( column, table.getSortColumn() );
    table.setSortColumn( null );
    assertNull( table.getSortColumn() );
    
    // Dispose sortColumn
    table.setSortColumn( column );
    table.setSortDirection( SWT.UP );
    column.dispose();
    assertNull( table.getSortColumn() );
    assertEquals( SWT.UP, table.getSortDirection() );
    
    // Try to set a disposed of column
    TableColumn disposedColumn = new TableColumn( table, SWT.NONE );
    disposedColumn.dispose();
    try {
      table.setSortColumn( disposedColumn );
      fail( "Must not allow to set disposed of sort column" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    
    // Test sortDirection
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
  
  public void testGetColumnOrder() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    
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
  
  public void testSetColumnOrder() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    ControlAdapter controlAdapter = new ControlAdapter() {
      public void controlMoved( final ControlEvent event ) {
        TableColumn column = ( TableColumn )event.widget;
        log.append( column.getText() + " moved|" );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
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
    assertEquals( false, column0.getMoveable() );
    assertEquals( false, column1.getMoveable() );
    
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

  public void testDisposeOfOrderedColumn() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );

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

  public void testSetColumnOrderWithInvalidArguments() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    // Passing null is not allowed
    try {
      table.setColumnOrder( null );
      fail( "setColumnOrder must not accept null argument" );
    } catch( NullPointerException e ) {
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
  
  public void testSetItemCountNonVirtual() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    
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
  
  public void testSetItemCountVirtual() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.VIRTUAL );
    
    table.setItemCount( 1 );
    assertEquals( 1, table.getItemCount() );
    Item[] items = ItemHolder.getItems( table );
    assertEquals( 0, items.length );
    
    new TableItem( table, SWT.NONE );
    assertEquals( 2, table.getItemCount() );
    items = ItemHolder.getItems( table );
    assertEquals( 1, items.length );
  }
  
  public void testItemCountWhileSetDataEvent() {
    final boolean[] eventHandled = { false };
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setSize( 100, 100 );
    final Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 90, 90 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( final Event event ) {
        eventHandled[ 0 ] = true;
        assertEquals( 200, table.getItemCount() );
      }
    } );
    table.setItemCount( 200 );
    assertTrue( eventHandled[ 0 ] );
    assertEquals( 200, table.getItemCount() );
  }
  
  public void testResizeWithVirtualItems() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 0, 0 );
    table.setItemCount( 1 );
    shell.open();
    
    // Ensure that a virtual item is not "realized" as long as it is invisible
    assertEquals( 0, ItemHolder.getItems( table ).length );
    
    // Enlarge the table so that the item will become visible
    table.setSize( 200, 200 );
    assertEquals( 1, ItemHolder.getItems( table ).length );
  }
  
  public void testItemImageSize() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );

    // Test initial itemImageSize
    assertEquals( new Point( 0, 0 ), table.getItemImageSize() );
    
    // Setting a null-image shouldn't change anything
    TableItem item = new TableItem( table, SWT.NONE );
    item.setImage( ( Image )null );
    assertEquals( new Point( 0, 0 ), table.getItemImageSize() );
    
    // Setting the first image also sets the itemImageSize for always and ever
    item.setImage( Image.find( RWTFixture.IMAGE_50x100 ) );
    assertEquals( new Point( 50, 100 ), table.getItemImageSize() );

    // Ensure that the itemImageSize - once detemined - does not change anymore
    item.setImage( Image.find( RWTFixture.IMAGE_100x50 ) );
    assertEquals( new Point( 50, 100 ), table.getItemImageSize() );
    
    // Ensure that the method returns the actual image size, not clipped by the
    // available width given by the column 
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.setWidth( 20 ); // image width is 50
    assertEquals( new Point( 50, 100 ), table.getItemImageSize() );
  }

  private static void clearColumns( final Table table ) {
    while( table.getColumnCount() > 0 ) {
      table.getColumn( 0 ).dispose();
    }
  }
}
