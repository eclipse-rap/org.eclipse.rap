/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.ITableItemAdapter;
import org.eclipse.swt.layout.FillLayout;


public class TableItem_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testCreation() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    // Add one item
    TableItem item1 = new TableItem( table, SWT.NONE );
    assertEquals( 1, table.getItemCount() );
    assertSame( item1, table.getItem( 0 ) );
    // Insert an item before first item
    TableItem item0 = new TableItem( table, SWT.NONE, 0 );
    assertEquals( 2, table.getItemCount() );
    assertSame( item0, table.getItem( 0 ) );
    // Try to add an item with an index which is out of bounds
    try {
      new TableItem( table, SWT.NONE, table.getItemCount() + 8 );
      String msg
        = "Index out of bounds expected when creating an item with "
        + "index > itemCount";
      fail( msg );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Try to add an item with a negative index
    try {
      new TableItem( table, SWT.NONE, -1 );
      String msg
        = "Index out of bounds expected when creating an item with "
        + "index == -1";
      fail( msg );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testParent() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    // Test creating column with valid parent
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    assertSame( table, item.getParent() );
    // Test creating column without parent
    try {
      new TableItem( null, SWT.NONE );
      fail( "Must not allow to create TableColumn withh null-parent." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testBounds() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    
    // bounds for out-of-range item on table without columns
    assertEquals( new Rectangle( 0, 0, 0, 0 ), item.getBounds( 123 ) );

    // without columns
    item.setText( "some text" );
    assertTrue( item.getBounds().width > 0 );

    TableColumn column0 = new TableColumn( table, SWT.NONE );
    column0.setWidth( 11 );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setWidth( 22 );

    // simple case: bounds for first and only item
    item.setText( "" );
    Rectangle bounds = item.getBounds();
    assertEquals( 0, bounds.x );
    assertEquals( 0, bounds.y );
    assertTrue( bounds.height > 0 );
    assertEquals( column0.getWidth(), bounds.width );

    // bounds for item in second column
    item.setText( 1, "abc" );
    bounds = item.getBounds( 1 );
    assertTrue( bounds.x >= column0.getWidth() );
    assertEquals( 0, bounds.y );
    assertTrue( bounds.height > 0 );
    assertEquals( column1.getWidth(), bounds.width );

    // bounds for out-of-range item
    bounds = item.getBounds( table.getColumnCount() + 100 );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), bounds );
    
    // bounds for table with visible headers
    table.setHeaderVisible( true );
    bounds = item.getBounds();
    assertTrue( bounds.y >= table.getHeaderHeight() );
  }
  
  public void testBoundsWithScroll() {
    final int tableWidth = 100;
    final int tableHeight = 100;
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    table.setSize( tableWidth, tableHeight );
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    column0.setWidth( tableWidth / 2 );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setWidth( tableWidth / 2 + 30 );
    int itemCount = tableHeight / table.getItemHeight() + 10;
    for( int i = 0; i < itemCount; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    
    Rectangle item0Bounds = table.getItem( 0 ).getBounds();
    // scroll item 0 out of view, now item 1 is on the same position as item 0
    // was before
    table.setTopIndex( 1 );
    assertEquals( item0Bounds, table.getItem( 1 ).getBounds() );
    
    // ensure that horizontal scrolling is detected
    table.setTopIndex( 0 );
    Rectangle column0Bounds = table.getItem( 0 ).getBounds( 0 );
    ITableAdapter adapter
      = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( column0.getWidth() );
    assertEquals( column0Bounds.x, table.getItem( 0 ).getBounds( 1 ).x );
  }
  
  public void testTextBounds() {
    // Test setup
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setWidth( 50 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setWidth( 50 );
    item.setText( 0, "col1" );
    item.setText( 1, "col2" );
    
    Rectangle textBounds1 = item.getTextBounds( 0 );
    Rectangle textBounds2 = item.getTextBounds( 1 );
    assertTrue( textBounds1.x + textBounds1.width <= textBounds2.x );
  }
  
  public void testTextBoundsWithInvalidIndex() {
    // Test setup
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "abc" );
    // without columns
    assertEquals( new Rectangle( 0, 0, 0, 0 ), item.getTextBounds( 123 ) );
    // with column
    new TableColumn( table, SWT.NONE );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), item.getTextBounds( 123 ) );
  }
  
  public void testTextBoundsWithImageAndColumns() {
    // Test setup
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.setWidth( 200 );
    
    Image image = Graphics.getImage( RWTFixture.IMAGE_100x50 );
    item.setImage( 0, image );
    assertTrue( item.getTextBounds( 0 ).x > image.getBounds().width );
    item.setImage( 0, null );
    assertTrue( item.getTextBounds( 0 ).x < image.getBounds().width );
  }

  public void testTextBoundsWithChangedFont() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "abc" );
    Rectangle origBounds = item.getTextBounds( 0 );
    item.setFont( Graphics.getFont( "Helvetica", 50, SWT.BOLD ) );
    Rectangle actualBounds = item.getTextBounds( 0 );
    assertTrue( actualBounds.width > origBounds.width );
    item.setFont( null );
    actualBounds = item.getTextBounds( 0 );
    assertEquals( origBounds, actualBounds );
  }

  public void testTextBoundsWithCheckboxTable() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.CHECK );
    TableColumn column = new TableColumn( table, SWT.LEFT );
    column.setWidth( 100 );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "rama rama ding dong" );
    Rectangle textBounds = item.getTextBounds( 0 );
    // Item 0 must share the first column with the check box
    assertTrue( textBounds.width < 85 );
  }

  public void testImageBoundsWithoutColumns() {
    // Test setup
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    // Common variables
    Rectangle bounds;

    // Asking for the bounds of a non-existing image returns an empty rectangle
    bounds = item.getImageBounds( 1 );
    assertEquals( 0, bounds.width );
    assertEquals( 0, bounds.height );
    bounds = item.getImageBounds( 100 );
    assertEquals( 0, bounds.width );
    assertEquals( 0, bounds.height );

    // A zero-width rectangle is returned when asking for an unset image of the
    // imaginary first column
    bounds = item.getImageBounds( 0 );
    assertEquals( 0, bounds.y );
    assertEquals( 0, bounds.width );
    assertTrue( bounds.height > 0 );

    // Set an actual image - its size rules the bounds returned
    item.setImage( 0, Graphics.getImage( RWTFixture.IMAGE_100x50 ) );
    bounds = item.getImageBounds( 0 );
    assertEquals( 50, bounds.height );
    assertEquals( 100, bounds.width );
  }

  public void testImageBoundsWithColumns() {
    // Test setup
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );

    // Common variables
    Rectangle bounds;

    // Asking for the bounds of a non-existing image returns an empty rectangle
    bounds = item.getImageBounds( -1 );
    assertEquals( 0, bounds.width );
    assertEquals( 0, bounds.height );
    bounds = item.getImageBounds( 100 );
    assertEquals( 0, bounds.width );
    assertEquals( 0, bounds.height );

    // Bounds of an image of a column that provides enough space are ruled by
    // the images size
    column.setWidth( 1000 );
    item.setImage( 0, Graphics.getImage( RWTFixture.IMAGE_100x50 ) );
    bounds = item.getImageBounds( 0 );
    assertEquals( 50, bounds.height );
    assertEquals( 100, bounds.width );

    // A column width that is smaller than the images width does not clip the
    // image bounds
    column.setWidth( 20 );
    item.setImage( 0, Graphics.getImage( RWTFixture.IMAGE_100x50 ) );
    bounds = item.getImageBounds( 0 );
    assertEquals( 50, bounds.height );
    assertEquals( 100, bounds.width );
    
    // ImageBounds for item without an image
    column.setWidth( 20 );
    item.setImage( 0, null );
    bounds = item.getImageBounds( 0 );
    assertEquals( 50, bounds.height );
    assertEquals( 0, bounds.width );
  }

  public void testBoundsWithCheckedTable() {
    Display display = new Display();
    Shell shell = new Shell( display );
    // without columns
    Table table = new Table( shell, SWT.CHECK );
    TableItem item = new TableItem( table, SWT.NONE );
    assertTrue( item.getBounds().x > 0 );
    assertTrue( item.getBounds().width >= 0 );
    // with columns
    table = new Table( shell, SWT.CHECK );
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.setWidth( 100 );
    item = new TableItem( table, SWT.NONE );
    assertTrue( item.getBounds().x >= getCheckWidth( table ) );
    assertTrue( item.getBounds( 0 ).x >= getCheckWidth( table ) );
    assertTrue( item.getBounds( 0 ).width < 100 );
    // with re-ordered columns
    table = new Table( shell, SWT.CHECK );
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    table.setColumnOrder( new int[] { 1, 0 } );
    item = new TableItem( table, SWT.NONE );
    assertTrue( item.getBounds( 1 ).x >= getCheckWidth( table ) );
  }

  public void testBoundsWidthReorderedColumns() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableColumn column0 = new TableColumn( table, SWT.NONE );
    column0.setWidth( 1 );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setWidth( 2 );
    TableItem item = new TableItem( table, SWT.NONE );

    table.setColumnOrder( new int[] { 1, 0 } );
    assertEquals( 0, item.getBounds( 1 ).x );
    assertEquals( item.getBounds( 1 ).width, item.getBounds( 0 ).x );
    assertEquals( column0.getWidth(),
                  item.getBounds( table.indexOf( column0 ) ).width );
    assertEquals( column1.getWidth(),
                  item.getBounds( table.indexOf( column1 ) ).width );
  }

  public void testInvalidBounds() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "col1" );
    item.setText( 1, "col2" );

    assertEquals( new Rectangle( 0, 0, 0, 0 ), item.getBounds( 1 ) );
  }

  public void testText() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );

    // Test with no columns at all
    TableItem item = new TableItem( table, SWT.NONE );
    assertEquals( "", item.getText() );
    assertEquals( "", item.getText( 123 ) );
    item.setText( 5, "abc" );
    assertEquals( "", item.getText( 5 ) );
    item.setText( "yes" );
    assertEquals( "yes", item.getText() );
    item = new TableItem( table, SWT.NONE );
    item.setImage( Graphics.getImage( RWTFixture.IMAGE1 ) );
    assertEquals( "", item.getText() );

    // Test with columns
    table.removeAll();
    new TableColumn( table, SWT.NONE );
    item = new TableItem( table, SWT.NONE );
    assertEquals( "", item.getText() );
    assertEquals( "", item.getText( 123 ) );
    item.setText( 1, "abc" );
    assertEquals( "", item.getText( 1 ) );
    item.setText( 5, "abc" );
    assertEquals( "", item.getText( 5 ) );
    item = new TableItem( table, SWT.NONE );
    item.setImage( Graphics.getImage( RWTFixture.IMAGE1 ) );
    assertEquals( "", item.getText() );
  }

  public void testImage() {
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );

    // Test with no columns at all
    TableItem item = new TableItem( table, SWT.NONE );
    assertEquals( null, item.getImage() );
    assertEquals( null, item.getImage( 123 ) );
    item.setImage( 5, image );
    assertEquals( null, item.getImage( 5 ) );
    item.setImage( image );
    assertSame( image, item.getImage() );

    // Test with columns
    table.removeAll();
    new TableColumn( table, SWT.NONE );
    item = new TableItem( table, SWT.NONE );
    assertEquals( null, item.getImage() );
    assertEquals( null, item.getImage( 123 ) );
    item.setImage( 1, image );
    assertEquals( null, item.getImage( 1 ) );
    item.setImage( 5, image );
    assertEquals( null, item.getImage( 5 ) );
    item.setImage( image );
    assertSame( image, item.getImage() );
  }

  public void testCheckedAndGrayed() {
    Display display = new Display();
    Shell shell = new Shell( display );

    // Ensure that checked and grayed only work with SWT.CHECK
    Table simpleTable = new Table( shell, SWT.NONE );
    TableItem simpleItem = new TableItem( simpleTable, SWT.NONE );
    assertTrue( ( simpleTable.getStyle() & SWT.CHECK ) == 0 );
    assertEquals( false, simpleItem.getChecked() );
    assertEquals( false, simpleItem.getGrayed() );
    simpleItem.setChecked( true );
    assertEquals( false, simpleItem.getChecked() );
    simpleItem.setGrayed( true );
    assertEquals( false, simpleItem.getGrayed() );

    // Test checked and grayed with a SWT.CHECK table
    Table checkedTable = new Table( shell, SWT.CHECK );
    TableItem checkedItem = new TableItem( checkedTable, SWT.NONE );
    assertEquals( false, checkedItem.getChecked() );
    assertEquals( false, checkedItem.getGrayed() );
    checkedItem.setChecked( true );
    assertEquals( true, checkedItem.getChecked() );
    checkedItem.setGrayed( true );
    assertEquals( true, checkedItem.getGrayed() );
  }

  public void testClearVirtual() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 100, 20 );
    table.setItemCount( 101 );

    TableItem item = table.getItem( 100 );
    assertEquals( false, item.cached );

    item.getText();
    assertEquals( true, item.cached );

    table.clear( 100 );
    assertEquals( false, item.cached );
  }

  public void testFont() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Font rowFont = Graphics.getFont( "row-font", 10, SWT.NORMAL );

    // Test initial value
    assertEquals( table.getFont(), item.getFont() );

    // Test setting font for an item that is out of column bounds
    Font font = Graphics.getFont( "Arial", 10, SWT.NORMAL );
    item.setFont( 100, font );
    assertEquals( table.getFont(), item.getFont( 100 ) );

    // Test setFont() - becomes default for all cell-fonts
    item.setFont( rowFont );
    assertEquals( rowFont, item.getFont() );
    assertEquals( rowFont, item.getFont( 0 ) );

    // Test setting and resetting font for a specific cell
    Font cellFont = Graphics.getFont( "cell-font", 10, SWT.NORMAL );
    item.setFont( 0, cellFont );
    assertEquals( cellFont, item.getFont( 0 ) );
    item.setFont( 0, null );
    assertEquals( rowFont, item.getFont( 0 ) );

    // Resetting item font returns the tables' font
    item.setFont( null );
    assertEquals( table.getFont(), item.getFont() );
  }

  public void testBackground() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Color rowBackground = Graphics.getColor( 1, 1, 1 );

    // Test initial value
    assertEquals( table.getBackground(), item.getBackground() );

    // Test setting background for an item that is out of column bounds
    Color color = Graphics.getColor( 2, 2, 2 );
    item.setBackground( 100, color );
    assertEquals( table.getBackground(), item.getBackground( 100 ) );

    // Test setBackground() - becomes default for all cell-fonts
    item.setBackground( rowBackground );
    assertEquals( rowBackground, item.getBackground() );
    assertEquals( rowBackground, item.getBackground( 0 ) );

    // Test setting and resetting background for a specific cell
    Color cellBackground = Graphics.getColor( 3, 3, 3 );
    item.setBackground( 0, cellBackground );
    assertEquals( cellBackground, item.getBackground( 0 ) );
    item.setBackground( 0, null );
    assertEquals( rowBackground, item.getBackground( 0 ) );

    // Resetting item background returns the tables' background
    item.setBackground( null );
    assertEquals( table.getBackground(), item.getBackground() );
  }

  public void testForeground() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Color rowForeground = Graphics.getColor( 1, 1, 1 );

    // Test initial value
    assertEquals( table.getForeground(), item.getForeground() );

    // Test setting foreground for an item that is out of column bounds
    Color color = Graphics.getColor( 2, 2, 2 );
    item.setForeground( 100, color );
    assertEquals( table.getForeground(), item.getForeground( 100 ) );

    // Test setForeground() - becomes default for all cell-fonts
    item.setForeground( rowForeground );
    assertEquals( rowForeground, item.getForeground() );
    assertEquals( rowForeground, item.getForeground( 0 ) );

    // Test setting and resetting foreground for a specific cell
    Color cellForeground = Graphics.getColor( 3, 3, 3 );
    item.setForeground( 0, cellForeground );
    assertEquals( cellForeground, item.getForeground( 0 ) );
    item.setForeground( 0, null );
    assertEquals( rowForeground, item.getForeground( 0 ) );

    // Resetting item foreground returns the tables' foreground
    item.setForeground( null );
    assertEquals( table.getForeground(), item.getForeground() );
  }
  
  /* Calling a setter like setImage, setBackground, ... on a virtual item
   * that hasn't been 'touched' yet, marks the item as cached without firing 
   * a SetData event.
   * This may lead to items e.g. without proper text as no SetData event gets
   * fired when the item becomes visible. SWT (on Windows) behaves the same. */
  public void testSetterWithVirtual() {
    // set up virtual table with unresolved items
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List eventLog = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 90, 90 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    } );
    shell.open();
    table.setItemCount( 1000 );
    // ensure precondition
    ITableAdapter adapter
      = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    assertTrue( adapter.isItemVirtual( 999 ) );
    // change background color and ensure that no SetData event was fired
    eventLog.clear();
    TableItem item = table.getItem( 999 );
    item.setBackground( display.getSystemColor( SWT.COLOR_RED ) );
    assertEquals( 0, eventLog.size() );
  }
  
  public void testDisposeVirtual() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL | SWT.MULTI );
    table.setItemCount( 100 );
    shell.layout();
    shell.open();
    // force item to get resolved and dispose of it
    TableItem item = table.getItem( 0 );
    item.getText();
    item.dispose();
    assertEquals( 99, table.getItemCount() );
    // select all items and dispose of them
    table.selectAll();
    TableItem[] selection = table.getSelection();
    for( int i = 0; i < selection.length; i++ ) {
      selection[ i ].dispose();
    }
    assertEquals( 0, table.getItemCount() );
  }
  
  public void testSetItemCountDisposeOrder() {
    final java.util.List log = new ArrayList();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    for( int i = 0; i < 30; i++ ) {
      final TableItem item = new TableItem( table, SWT.NONE );
      item.setData( new Integer( i ) );
      item.addDisposeListener( new DisposeListener() {
        public void widgetDisposed( DisposeEvent event ) {
          log.add( item.getData() );
        }
      } );
    }
    table.setItemCount( 25 );
    assertEquals( 5, log.size() );
    assertEquals( new Integer( 25 ), log.get( 0 ) );
    assertEquals( new Integer( 26 ), log.get( 1 ) );
    assertEquals( new Integer( 27 ), log.get( 2 ) );
    assertEquals( new Integer( 28 ), log.get( 3 ) );
    assertEquals( new Integer( 29 ), log.get( 4 ) );
  }

  /////////////////////////
  // TableItemAdapter Tests

  public void testGetBackground() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Color tableColor = display.getSystemColor( SWT.COLOR_YELLOW );
    Color itemColor = display.getSystemColor( SWT.COLOR_RED );
    Color cellColor = display.getSystemColor( SWT.COLOR_BLUE );
    Object adapter = item.getAdapter( ITableItemAdapter.class );
    ITableItemAdapter tableItemAdapter = ( ITableItemAdapter )adapter;
    // simple case: no explicit colors at all
    Color[] backgrounds = tableItemAdapter.getCellBackgrounds();
    assertNull( backgrounds[ 0 ] );
    // set background on table but not on item
    table.setBackground( tableColor );
    backgrounds = tableItemAdapter.getCellBackgrounds();
    assertNull( backgrounds[ 0 ] );
    // set background on item
    item.setBackground( itemColor );
    backgrounds = tableItemAdapter.getCellBackgrounds();
    assertNull( backgrounds[ 0 ] );
    // set a cell color
    item.setBackground( 0, cellColor );
    backgrounds = tableItemAdapter.getCellBackgrounds();
    assertSame( cellColor, backgrounds[ 0 ] );
  }

  public void testGetForegrounds() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Color tableColor = display.getSystemColor( SWT.COLOR_YELLOW );
    Color itemColor = display.getSystemColor( SWT.COLOR_RED );
    Color cellColor = display.getSystemColor( SWT.COLOR_BLUE );
    Object adapter = item.getAdapter( ITableItemAdapter.class );
    ITableItemAdapter tableItemAdapter = ( ITableItemAdapter )adapter;
    // simple case: no explicit colors at all
    Color[] foregrounds = tableItemAdapter.getCellForegrounds();
    assertNull( foregrounds[ 0 ] );
    // set foreground on table but not on item
    table.setForeground( tableColor );
    foregrounds = tableItemAdapter.getCellForegrounds();
    assertNull( foregrounds[ 0 ] );
    // set foreground on item
    item.setForeground( itemColor );
    foregrounds = tableItemAdapter.getCellForegrounds();
    assertNull( foregrounds[ 0 ] );
    // set foreground on cell
    item.setForeground( 0, cellColor );
    foregrounds = tableItemAdapter.getCellForegrounds();
    assertSame( cellColor, foregrounds[ 0 ] );
  }

  public void testGetFont() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Font tableFont = Graphics.getFont( "TableFont", 11, SWT.ITALIC );
    Font itemFont = Graphics.getFont( "ItemFont", 12, SWT.BOLD );
    Font cellFont = Graphics.getFont( "CellFont", 13, SWT.NORMAL );
    Object adapter = item.getAdapter( ITableItemAdapter.class );
    ITableItemAdapter tableItemAdapter = ( ITableItemAdapter )adapter;
    // simple case: no explicit fonts at all
    Font[] fonts = tableItemAdapter.getCellFonts();
    assertNull( fonts[ 0 ] );
    // set font on table but not on item
    table.setFont( tableFont );
    fonts = tableItemAdapter.getCellFonts();
    assertNull( fonts[ 0 ] );
    // set font on item
    item.setFont( itemFont );
    fonts = tableItemAdapter.getCellFonts();
    assertNull( fonts[ 0 ] );
    // set a cell font
    item.setFont( 0, cellFont );
    fonts = tableItemAdapter.getCellFonts();
    assertSame( cellFont, fonts[ 0 ] );
  }

  /////////////////
  // helper methods

  private static int getCheckWidth( final Table table ) {
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    int checkWidth = tableAdapter.getCheckWidth();
    return checkWidth;
  }
}
