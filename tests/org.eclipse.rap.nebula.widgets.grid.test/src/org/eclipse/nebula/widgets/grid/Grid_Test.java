/*******************************************************************************
 * Copyright (c) 2012, 2023 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid;

import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridColumns;
import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridItems;
import static org.eclipse.nebula.widgets.grid.GridTestUtil.loadImage;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.grid.internal.IGridAdapter;
import org.eclipse.nebula.widgets.grid.internal.NullScrollBarProxy;
import org.eclipse.nebula.widgets.grid.internal.ScrollBarProxyAdapter;
import org.eclipse.nebula.widgets.grid.internal.gridkit.GridLCA;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ItemProvider;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class Grid_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private Grid grid;
  private ScrollBar verticalBar;
  private ScrollBar horizontalBar;
  private List<Event> eventLog;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    grid = new Grid( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    grid.setSize( 200, 200 );
    verticalBar = grid.getVerticalBar();
    horizontalBar = grid.getHorizontalBar();
    eventLog = new ArrayList<Event>();
  }

  @Test
  public void testGridCreation() {
    grid = new Grid( shell, SWT.NONE );
    assertNotNull( grid );
    assertTrue( grid.getHorizontalScrollBarProxy() instanceof NullScrollBarProxy );
    assertNull( grid.getHorizontalBar() );
    assertTrue( grid.getVerticalScrollBarProxy() instanceof NullScrollBarProxy );
    assertNull( grid.getVerticalBar() );
    assertEquals( 0, grid.getRootItemCount() );
  }

  @Test
  public void testGridCreationWithScrollBars() {
    grid = new Grid( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    assertTrue( grid.getHorizontalScrollBarProxy() instanceof ScrollBarProxyAdapter );
    assertFalse( grid.getHorizontalBar().isVisible() );
    assertTrue( grid.getVerticalScrollBarProxy() instanceof ScrollBarProxyAdapter );
    assertFalse( grid.getVerticalBar().isVisible() );
  }

  @Test
  public void testStyle() {
    Grid grid = new Grid( shell, SWT.NONE );
    assertTrue( ( grid.getStyle() & SWT.DOUBLE_BUFFERED ) != 0 );

    grid = new Grid( shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
    assertTrue( ( grid.getStyle() & SWT.H_SCROLL ) != 0 );
    assertTrue( ( grid.getStyle() & SWT.V_SCROLL ) != 0 );
    assertTrue( ( grid.getStyle() & SWT.BORDER ) != 0 );

    grid = new Grid( shell, SWT.SINGLE | SWT.MULTI );
    assertTrue( ( grid.getStyle() & SWT.SINGLE ) != 0 );
    assertTrue( ( grid.getStyle() & SWT.MULTI ) != 0 );

    grid = new Grid( shell, SWT.VIRTUAL | SWT.CHECK );
    assertTrue( ( grid.getStyle() & SWT.VIRTUAL ) != 0 );
    assertTrue( ( grid.getStyle() & SWT.CHECK ) != 0 );
  }

  @Test
  public void testGetRootItemCount() {
    createGridItems( grid, 5, 1 );

    assertEquals( 5, grid.getRootItemCount() );
  }

  @Test
  public void testGetItemCount() {
    createGridItems( grid, 5, 1 );

    assertEquals( 10, grid.getItemCount() );
  }

  @Test
  public void testSetItemCount_MoreItems() {
    createGridItems( grid, 3, 3 );

    grid.setItemCount( 15 );

    assertEquals( 15, grid.getItemCount() );
    assertEquals( 6, grid.getRootItemCount() );
  }

  @Test
  public void testSetItemCount_LessItems() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    grid.setItemCount( 6 );

    assertEquals( 6, grid.getItemCount() );
    assertEquals( 2, grid.getRootItemCount() );
    assertEquals( 1, items[ 4 ].getItemCount() );
  }

  @Test
  public void testSetItemCount_NoChange() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    grid.setItemCount( 12 );

    assertTrue( Arrays.equals( items, grid.getItems() ) );
  }

  @Test
  public void testSetItemCount_WithSelectedCells() {
    createGridItems( grid, 3, 3 );
    createGridColumns( grid, 3, SWT.NONE );
    grid.setCellSelectionEnabled( true );
    grid.setCellSelection( new Point( 1, 1 ) );

    grid.setItemCount( 1 );

    assertEquals( 0, grid.getCellSelectionCount() );
  }

  @Test
  public void testGetRootItems() {
    GridItem[] items = createGridItems( grid, 3, 1 );

    GridItem[] rootItems = grid.getRootItems();
    assertSame( items[ 0 ], rootItems[ 0 ] );
    assertSame( items[ 2 ], rootItems[ 1 ] );
    assertSame( items[ 4 ], rootItems[ 2 ] );
  }

  @Test
  public void testGetItems() {
    GridItem[] items = createGridItems( grid, 3, 1 );

    assertTrue( Arrays.equals( items, grid.getItems() ) );
  }

  @Test
  public void testGetRootItem() {
    GridItem[] items = createGridItems( grid, 3, 1 );

    assertSame( items[ 2 ], grid.getRootItem( 1 ) );
    assertSame( items[ 4 ], grid.getRootItem( 2 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetRootItem_InvalidIndex() {
    createGridItems( grid, 3, 1 );

    grid.getRootItem( 10 );
  }

  @Test
  public void testGetItem() {
    GridItem[] items = createGridItems( grid, 3, 1 );

    assertSame( items[ 1 ], grid.getItem( 1 ) );
    assertSame( items[ 4 ], grid.getItem( 4 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetItem_InvalidIndex() {
    createGridItems( grid, 3, 1 );

    grid.getItem( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetItemByPoint_NullArgument() {
    createGridItems( grid, 3, 1 );

    grid.getItem( null );
  }

  @Test
  public void testGetItemByPoint() {
    GridItem[] items = createGridItems( grid, 10, 0 );

    assertSame( items[ 2 ], grid.getItem( new Point( 10, 60 ) ) );
  }

  @Test
  public void testGetItemByPoint_WithHeaderVisible() {
    grid.setHeaderVisible( true );
    createGridColumns( grid, 1, SWT.NONE );
    GridItem[] items = createGridItems( grid, 10, 0 );

    assertSame( items[ 1 ], grid.getItem( new Point( 10, 60 ) ) );
  }

  @Test
  public void testGetItemByPoint_WithinHeader() {
    grid.setHeaderVisible( true );
    createGridColumns( grid, 1, SWT.NONE );
    createGridItems( grid, 10, 0 );

    assertNull( grid.getItem( new Point( 10, 20 ) ) );
  }

  @Test
  public void testIndexOf() {
    GridItem[] items = createGridItems( grid, 3, 1 );

    assertEquals( 1, grid.indexOf( items[ 1 ] ) );
    assertEquals( 4, grid.indexOf( items[ 4 ] ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testIndexOf_NullArgument() {
    grid.indexOf( ( GridItem )null );
  }

  @Test
  public void testIndexOf_DifferentParent() {
    Grid otherGrid = new Grid( shell, SWT.NONE );
    GridItem item = new GridItem( otherGrid, SWT.NONE );

    assertEquals( -1, grid.indexOf( item ) );
  }

  @Test
  public void testIndexOf_AfterDispose() {
    GridItem[] items = createGridItems( grid, 3, 1 );

    items[ 2 ].dispose();

    assertEquals( 1, grid.indexOf( items[ 1 ] ) );
    assertEquals( 2, grid.indexOf( items[ 4 ] ) );
  }

  @Test
  public void testGetColumnCount() {
    createGridColumns( grid, 5, SWT.NONE );

    assertEquals( 5, grid.getColumnCount() );
  }

  @Test
  public void testGetColumns() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    assertTrue( Arrays.equals( columns, grid.getColumns() ) );
  }

  @Test
  public void testGetColumn() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    assertSame( columns[ 1 ], grid.getColumn( 1 ) );
    assertSame( columns[ 4 ], grid.getColumn( 4 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetColumn_InvalidIndex() {
    createGridColumns( grid, 3, SWT.NONE );

    grid.getColumn( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetColumnByPoint_NullArgument() {
    createGridColumns( grid, 5, SWT.NONE );

    grid.getColumn( null );
  }

  @Test
  public void testGetColumnByPoint() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    assertSame( columns[ 1 ], grid.getColumn( new Point( 30, 10 ) ) );
    assertSame( columns[ 4 ], grid.getColumn( new Point( 240, 10 ) ) );
  }

  @Test
  public void testGetColumnByPoint_WithSpanning() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );
    GridItem[] items = createGridItems( grid, 3, 0 );
    items[ 0 ].setColumnSpan( 1, 1 );

    assertSame( columns[ 1 ], grid.getColumn( new Point( 100, 10 ) ) );
  }

  @Test
  public void testIndexOfColumn() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    assertEquals( 1, grid.indexOf( columns[ 1 ] ) );
    assertEquals( 4, grid.indexOf( columns[ 4 ] ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testIndexOfColumn_NullArgument() {
    grid.indexOf( ( GridColumn )null );
  }

  @Test
  public void testIndexOfColumn_DifferentParent() {
    Grid otherGrid = new Grid( shell, SWT.NONE );
    GridColumn column = new GridColumn( otherGrid, SWT.NONE );

    assertEquals( -1, grid.indexOf( column ) );
  }

  @Test
  public void testIndexOfColumn_AfterDispose() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    columns[ 2 ].dispose();

    assertEquals( 3, grid.indexOf( columns[ 4 ] ) );
  }

  @Test
  public void testDispose() {
    grid.dispose();

    assertTrue( grid.isDisposing() );
    assertTrue( grid.isDisposed() );
  }

  @Test
  public void testDispose_fireDisposeEventOnlyOnce() {
    Listener listener = mock( Listener.class );
    grid.addListener( SWT.Dispose, listener );

    grid.dispose();

    verify( listener, times( 1 ) ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testDispose_WithItems() {
    GridItem[] items = createGridItems( grid, 1, 1 );

    grid.dispose();

    assertTrue( items[ 0 ].isDisposed() );
    assertTrue( items[ 1 ].isDisposed() );
  }

  @Test
  public void testDispose_WithColumns() {
    GridColumn[] columns = createGridColumns( grid, 2, SWT.NONE );

    grid.dispose();

    assertTrue( columns[ 0 ].isDisposed() );
    assertTrue( columns[ 1 ].isDisposed() );
  }

  @Test
  public void testSendDisposeEvent() {
    DisposeListener listener = mock( DisposeListener.class );
    grid.addDisposeListener( listener );

    grid.dispose();

    verify( listener ).widgetDisposed( any( DisposeEvent.class ) );
  }

  @Test
  public void testAddRemoveSelectionListener() {
    SelectionListener listener = mock( SelectionListener.class );
    grid.addSelectionListener( listener );

    assertTrue( grid.isListening( SWT.Selection ) );
    assertTrue( grid.isListening( SWT.DefaultSelection ) );

    grid.removeSelectionListener( listener );
    assertFalse( grid.isListening( SWT.Selection ) );
    assertFalse( grid.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testAddRemoveTreeListener() {
    TreeListener listener = mock( TreeListener.class );
    grid.addTreeListener( listener );

    assertTrue( grid.isListening( SWT.Expand ) );
    assertTrue( grid.isListening( SWT.Collapse ) );

    grid.removeTreeListener( listener );
    assertFalse( grid.isListening( SWT.Expand ) );
    assertFalse( grid.isListening( SWT.Collapse ) );
  }

  @Test
  public void testClearAll() {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setText( "foo" );
    items[ 1 ].setText( "bar" );
    items[ 4 ].setText( "root" );

    // Note: The parameter allChildren has no effect as all items (not only rootItems) are cleared
    grid.clearAll( false );

    assertEquals( "", items[ 0 ].getText() );
    assertEquals( "", items[ 1 ].getText() );
    assertEquals( "", items[ 4 ].getText() );
  }

  @Test
  public void testClearByIndex() {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setText( "foo" );
    items[ 1 ].setText( "bar" );
    items[ 4 ].setText( "root" );

    grid.clear( 0, false );

    assertEquals( "", items[ 0 ].getText() );
    assertEquals( "bar", items[ 1 ].getText() );
    assertEquals( "root", items[ 4 ].getText() );
  }

  @Test
  public void testClearByIndex_AllChildren() {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setText( "foo" );
    items[ 1 ].setText( "bar" );
    items[ 4 ].setText( "root" );

    grid.clear( 0, true );

    assertEquals( "", items[ 0 ].getText() );
    assertEquals( "", items[ 1 ].getText() );
    assertEquals( "root", items[ 4 ].getText() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testClearByIndex_InvalidIndex() {
    createGridItems( grid, 3, 3 );

    grid.clear( 20, false );
  }

  @Test
  public void testClearByIndexRange() {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setText( "foo" );
    items[ 1 ].setText( "bar" );
    items[ 3 ].setText( "sub" );
    items[ 4 ].setText( "root" );

    grid.clear( 0, 2, false );

    assertEquals( "", items[ 0 ].getText() );
    assertEquals( "", items[ 1 ].getText() );
    assertEquals( "sub", items[ 3 ].getText() );
    assertEquals( "root", items[ 4 ].getText() );
  }

  @Test
  public void testClearByIndexRange_AllChildren() {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setText( "first" );
    items[ 1 ].setText( "foo" );
    items[ 4 ].setText( "root" );
    items[ 5 ].setText( "bar" );
    items[ 7 ].setText( "sub" );

    grid.clear( 1, 4, true );

    assertEquals( "first", items[ 0 ].getText() );
    assertEquals( "", items[ 1 ].getText() );
    assertEquals( "", items[ 4 ].getText() );
    assertEquals( "", items[ 5 ].getText() );
    assertEquals( "", items[ 7 ].getText() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testClearByIndexRange_InvalidIndex1() {
    createGridItems( grid, 3, 3 );

    grid.clear( -1, 4, false );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testClearByIndexRange_InvalidIndex2() {
    createGridItems( grid, 3, 3 );

    grid.clear( 1, 20, false );
  }

  @Test
  public void testClearByIndexRange_InvalidIndex3() {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setText( "foo" );
    items[ 1 ].setText( "bar" );
    items[ 3 ].setText( "sub" );
    items[ 4 ].setText( "root" );

    grid.clear( 4, 1, false );

    assertEquals( "foo", items[ 0 ].getText() );
    assertEquals( "bar", items[ 1 ].getText() );
    assertEquals( "sub", items[ 3 ].getText() );
    assertEquals( "root", items[ 4 ].getText() );
  }

  @Test
  public void testClearByIndices() {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setText( "foo" );
    items[ 1 ].setText( "bar" );
    items[ 3 ].setText( "sub" );
    items[ 4 ].setText( "root" );

    grid.clear( new int[] { 0, 1 }, false );

    assertEquals( "", items[ 0 ].getText() );
    assertEquals( "", items[ 1 ].getText() );
    assertEquals( "sub", items[ 3 ].getText() );
    assertEquals( "root", items[ 4 ].getText() );
  }

  @Test
  public void testClearByIndices_AllChildren() {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setText( "foo" );
    items[ 1 ].setText( "bar" );
    items[ 3 ].setText( "sub" );
    items[ 4 ].setText( "root" );

    grid.clear( new int[] { 0, 1 }, true );

    assertEquals( "", items[ 0 ].getText() );
    assertEquals( "", items[ 1 ].getText() );
    assertEquals( "", items[ 3 ].getText() );
    assertEquals( "root", items[ 4 ].getText() );
  }

  @Test
  public void testClearByIndices_NullArgument() {
    createGridItems( grid, 3, 3 );

    try {
      grid.clear( null, false );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testClearByIndices_InvalidIndex() {
    createGridItems( grid, 3, 3 );

    try {
      grid.clear( new int[] { 0, 20 }, false );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSendSetDataEventAfterClear() {
    grid = new Grid( shell, SWT.VIRTUAL );
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setText( "foo" );
    // Mark SetData event as fired
    items[ 0 ].getText();
    grid.addListener( SWT.SetData, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        GridItem item = ( GridItem )event.item;
        item.setText( "bar" );
      }
    } );

    grid.clear( 0, false );

    assertEquals( "bar", items[ 0 ].getText() );
  }

  @Test
  public void testClearWithColumns() {
    grid = new Grid( shell, SWT.VIRTUAL );
    GridItem[] items = createGridItems( grid, 3, 3 );
    createGridColumns( grid, 3, SWT.NONE );
    items[ 1 ].setText( 0, "item 1.0" );
    items[ 1 ].setText( 1, "item 1.1" );
    items[ 1 ].setText( 2, "item 1.2" );

    grid.clear( 1, false );

    assertEquals( "", items[ 1 ].getText( 0 ) );
    assertEquals( "", items[ 1 ].getText( 1 ) );
    assertEquals( "", items[ 1 ].getText( 2 ) );
  }

  @Test
  public void testGetSelectionEnabled_Initial() {
    assertTrue( grid.getSelectionEnabled() );
  }

  @Test
  public void testGetSelectionEnabled() {
    grid.setSelectionEnabled( false );

    assertFalse( grid.getSelectionEnabled() );
  }

  @Test
  public void testSetSelectionEnabled_ClearSelectedItems() {
    createGridItems( grid, 3, 0 );
    grid.select( 0 );

    grid.setSelectionEnabled( false );

    assertEquals( 0, grid.getSelectionCount() );
  }

  @Test
  public void testGetCellSelectionEnabled_Initial() {
    assertFalse( grid.getCellSelectionEnabled() );
  }

  @Test
  public void testGetCellSelectionEnabled() {
    grid.setCellSelectionEnabled( true );

    assertTrue( grid.getSelectionEnabled() );
  }

  @Test
  public void testSetCellSelectionEnabled_ClearSelectedItems() {
    createGridItems( grid, 3, 0 );
    grid.select( 0 );

    grid.setCellSelectionEnabled( true );

    assertEquals( 0, grid.getSelectionCount() );
  }

  @Test
  public void testSetCellSelectionEnabled_ClearSelectedCells() {
    createGridItems( grid, 3, 0 );
    grid.setCellSelectionEnabled( true );
    grid.select( 0 );

    grid.setCellSelectionEnabled( false );

    assertEquals( 0, grid.getCellSelectionCount() );
  }

  @Test
  public void testGetCellSelection_afterColumnDisposal() {
    createGridColumns( grid, 3, SWT.NONE );
    createGridItems( grid, 3, 0 );
    grid.setCellSelectionEnabled( true );
    Point[] cells = new Point[] { new Point( 0, 0 ), new Point( 1, 1 ), new Point( 2, 2 ) };
    grid.setCellSelection( cells );

    grid.getColumn( 1 ).dispose();

    Point[] expected = new Point[] { new Point( 0, 0 ), new Point( 1, 2 ) };
    assertTrue( Arrays.equals( expected, grid.getCellSelection() ) );
  }

  @Test
  public void testIsCellSelectionEnabled_Initial() {
    assertFalse( grid.isCellSelectionEnabled() );
  }

  @Test
  public void testIsCellSelectionEnabled() {
    grid.setCellSelectionEnabled( true );

    assertTrue( grid.isCellSelectionEnabled() );
  }

  @Test
  public void testGetSelectionCount_Initial() {
    assertEquals( 0, grid.getSelectionCount() );
  }

  @Test
  public void testGetSelectionCount() {
    createGridItems( grid, 3, 0 );

    grid.select( 0 );

    assertEquals( 1, grid.getSelectionCount() );
  }

  @Test
  public void testGetCellSelectionCount_Initial() {
    grid.setCellSelectionEnabled( true );
    createGridItems( grid, 3, 0 );
    createGridColumns( grid, 3, SWT.NONE );

    assertEquals( 0, grid.getCellSelectionCount() );
  }

  @Test
  public void testGetCellSelectionCount() {
    grid.setCellSelectionEnabled( true );
    createGridItems( grid, 3, 0 );
    createGridColumns( grid, 3, SWT.NONE );

    grid.select( 0 );

    assertEquals( 3, grid.getCellSelectionCount() );
  }

  @Test
  public void testIsCellSelected() {
    grid.setCellSelectionEnabled( true );
    createGridItems( grid, 3, 0 );
    createGridColumns( grid, 3, SWT.NONE );

    grid.selectCell( new Point( 2, 1 ) );

    assertTrue( grid.isCellSelected( new Point( 2, 1 ) ) );
    assertFalse( grid.isCellSelected( new Point( 2, 2 ) ) );
  }

  @Test
  public void testGetCell() {
    grid.setCellSelectionEnabled( true );
    createGridItems( grid, 3, 0 );
    createGridColumns( grid, 3, SWT.NONE );

    assertEquals( new Point( 1, 2 ), grid.getCell( new Point( 50, 70 ) ) );
  }

  @Test
  public void testSelectColumn() {
    grid.setCellSelectionEnabled( true );
    createGridItems( grid, 3, 0 );
    createGridColumns( grid, 3, SWT.NONE );

    grid.selectColumn( 1 );

    Point[] expected = new Point[] {
      new Point( 1, 0 ),
      new Point( 1, 1 ),
      new Point( 1, 2 )
    };
    assertTrue( Arrays.equals( expected, grid.getCellSelection() ) );
  }

  @Test
  public void testSelectColumnGroup() {
    grid.setCellSelectionEnabled( true );
    createGridItems( grid, 3, 0 );
    createGridColumns( grid, 3, SWT.NONE );
    GridColumnGroup gridColumnGroup = new GridColumnGroup( grid, SWT.NONE );
    createGridColumns( gridColumnGroup, 2, SWT.NONE );

    grid.selectColumnGroup( 0 );

    Point[] expected = new Point[] {
      new Point( 3, 0 ),
      new Point( 3, 1 ),
      new Point( 3, 2 ),
      new Point( 4, 0 ),
      new Point( 4, 1 ),
      new Point( 4, 2 )
    };
    assertTrue( Arrays.equals( expected, grid.getCellSelection() ) );
  }

  @Test
  public void testGetSelection_Initial() {
    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testGetSelection() {
    createGridItems( grid, 3, 0 );

    grid.select( 0 );

    assertSame( grid.getItem( 0 ), grid.getSelection()[ 0 ] );
  }

  @Test
  public void testGetSelection_AfterDisposeItem() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 1, 3 );

    items[ 2 ].dispose();

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testGetSelection_WithCellSelection_Initial() {
    grid.setCellSelectionEnabled( true );

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testGetSelection_WithCellSelection() {
    createGridItems( grid, 3, 0 );
    createGridColumns( grid, 3, 0 );
    grid.setCellSelectionEnabled( true );

    grid.selectCell( new Point( 2, 2 ) );

    assertSame( grid.getItem( 2 ), grid.getSelection()[ 0 ] );
  }

  @Test
  public void testSelectByIndex_Single() {
    grid = new Grid( shell, SWT.SINGLE );
    GridItem[] items = createGridItems( grid, 3, 0 );

    grid.select( 0 );
    grid.select( 2 );

    GridItem[] expected = new GridItem[]{ items[ 2 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectByIndex_Multi() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 3, 0 );

    grid.select( 0 );
    grid.select( 2 );

    GridItem[] expected = new GridItem[]{ items[ 0 ], items[ 2 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectByIndex_WithSelectionDisabled() {
    grid.setSelectionEnabled( false );
    createGridItems( grid, 3, 0 );

    grid.select( 0 );

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSelectByIndex_WithInvalidIndex() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 3, 0 );

    grid.select( 0 );
    grid.select( 5 );

    GridItem[] expected = new GridItem[]{ items[ 0 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectByIndex_Twice() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 3, 0 );

    grid.select( 0 );
    grid.select( 0 );

    GridItem[] expected = new GridItem[]{ items[ 0 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectByIndex_WithCellSelection_Single() {
    grid = new Grid( shell, SWT.SINGLE );
    createGridColumns( grid, 3, 0 );
    grid.setCellSelectionEnabled( true );
    createGridItems( grid, 3, 0 );

    grid.select( 0 );
    grid.select( 2 );

    Point[] expected = new Point[]{ new Point( 0, 0 ) };
    assertTrue( Arrays.equals( expected, grid.getCellSelection() ) );
  }

  @Test
  public void testSelectByIndex_WithCellSelection_Multi() {
    grid = new Grid( shell, SWT.MULTI );
    createGridColumns( grid, 3, 0 );
    grid.setCellSelectionEnabled( true );
    createGridItems( grid, 3, 0 );

    grid.select( 0 );
    grid.select( 2 );

    Point[] expected = new Point[] {
      new Point( 0, 0 ),
      new Point( 1, 0 ),
      new Point( 2, 0 ),
      new Point( 0, 2 ),
      new Point( 1, 2 ),
      new Point( 2, 2 )
    };
    assertTrue( Arrays.equals( expected, grid.getCellSelection() ) );
  }

  @Test
  public void testSelectByRange_Single() {
    grid = new Grid( shell, SWT.SINGLE );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.select( 1, 1 );

    GridItem[] expected = new GridItem[]{ items[ 1 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectByRange_SingleWithDifferentSrartEnd() {
    grid = new Grid( shell, SWT.SINGLE );
    createGridItems( grid, 5, 0 );

    grid.select( 1, 3 );

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSelectByRange_Multi() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.select( 1, 3 );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectByRange_WithSelectionDisabled() {
    grid = new Grid( shell, SWT.MULTI );
    grid.setSelectionEnabled( false );
    createGridItems( grid, 5, 0 );

    grid.select( 1, 3 );

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSelectByRange_StartBiggerThanEnd() {
    grid = new Grid( shell, SWT.MULTI );
    createGridItems( grid, 5, 0 );

    grid.select( 3, 1 );

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSelectByRange_WithCellSelection_Single() {
    grid = new Grid( shell, SWT.SINGLE );
    grid.setCellSelectionEnabled( true );
    createGridColumns( grid, 3, 0 );
    createGridItems( grid, 5, 0 );

    grid.select( 1, 1 );

    Point[] expected = new Point[]{ new Point( 0, 1 ) };
    assertTrue( Arrays.equals( expected, grid.getCellSelection() ) );
  }

  @Test
  public void testSelectByRange_WithCellSelection_Multi() {
    grid = new Grid( shell, SWT.MULTI );
    grid.setCellSelectionEnabled( true );
    createGridColumns( grid, 3, 0 );
    createGridItems( grid, 5, 0 );

    grid.select( 1, 3 );

    Point[] expected = new Point[] {
      new Point( 0, 1 ),
      new Point( 1, 1 ),
      new Point( 2, 1 ),
      new Point( 0, 2 ),
      new Point( 1, 2 ),
      new Point( 2, 2 ),
      new Point( 0, 3 ),
      new Point( 1, 3 ),
      new Point( 2, 3 )
    };
    assertTrue( Arrays.equals( expected, grid.getCellSelection() ) );
  }

  @Test
  public void testSelectByIndices_Single() {
    grid = new Grid( shell, SWT.SINGLE );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.select( new int[] { 1 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectByIndices_SingleWithMultipleIndices() {
    grid = new Grid( shell, SWT.SINGLE );
    createGridItems( grid, 5, 0 );

    grid.select( new int[] { 1, 2, 3 } );

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSelectByIndices_Multi() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.select( new int[] { 1, 2, 3 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSelectByIndices_NullArgument() {
    grid = new Grid( shell, SWT.MULTI );

    grid.select( null );
  }

  @Test
  public void testSelectByIndices_InvalidIndex() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.select( new int[] { 1, 55, 2, 3 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectByIndices_DuplicateIndex() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.select( new int[] { 1, 2, 2, 3 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectAll_Single() {
    grid = new Grid( shell, SWT.SINGLE );
    createGridItems( grid, 5, 0 );

    grid.selectAll();

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSelectAll_Multi() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 3, 0 );

    grid.selectAll();

    GridItem[] expected = new GridItem[]{ items[ 0 ], items[ 1 ], items[ 2 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectAll_AfterSelect() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 3, 0 );

    grid.select( 1 );
    grid.selectAll();

    GridItem[] expected = new GridItem[]{ items[ 0 ], items[ 1 ], items[ 2 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSelectAllCells_WithCollapsedItems() {
    grid = new Grid( shell, SWT.MULTI );
    grid.setCellSelectionEnabled( true );
    createGridColumns( grid, 3, SWT.NONE );
    GridItem[] items = createGridItems( grid, 3, 1 );
    items[ 1 ].setExpanded( false );

    grid.selectAllCells();

    Point[] expected = new Point[] {
      new Point( 0, 0 ), new Point( 1, 0 ), new Point( 2, 0 ),
      new Point( 0, 2 ), new Point( 1, 2 ), new Point( 2, 2 ),
      new Point( 0, 4 ), new Point( 1, 4 ), new Point( 2, 4 ),
    };
    assertTrue( Arrays.equals( expected, grid.getCellSelection() ) );
  }

  @Test
  public void testSelectAllCells_WithExpandedItems() {
    grid = new Grid( shell, SWT.MULTI );
    grid.setCellSelectionEnabled( true );
    createGridColumns( grid, 3, SWT.NONE );
    GridItem[] items = createGridItems( grid, 3, 1 );
    items[ 2 ].setExpanded( true );

    grid.selectAllCells();

    Point[] expected = new Point[] {
      new Point( 0, 0 ), new Point( 1, 0 ), new Point( 2, 0 ),
      new Point( 0, 2 ), new Point( 1, 2 ), new Point( 2, 2 ),
      new Point( 0, 3 ), new Point( 1, 3 ), new Point( 2, 3 ),
      new Point( 0, 4 ), new Point( 1, 4 ), new Point( 2, 4 ),
    };
    assertTrue( Arrays.equals( expected, grid.getCellSelection() ) );
  }

  @Test
  public void testSetSelectionByIndex() {
    GridItem[] items = createGridItems( grid, 3, 0 );

    grid.setSelection( 1 );

    GridItem[] expected = new GridItem[]{ items[ 1 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByIndex_ClearPreviousSelection() {
    GridItem[] items = createGridItems( grid, 3, 0 );
    grid.select( 0 );

    grid.setSelection( 1 );

    GridItem[] expected = new GridItem[]{ items[ 1 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByIndex_WithSelectionDisabled() {
    grid.setSelectionEnabled( false );
    createGridItems( grid, 3, 0 );

    grid.setSelection( 0 );

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByIndex_WithInvalidIndex() {
    createGridItems( grid, 3, 0 );

    grid.setSelection( 5 );

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByRange_Single() {
    grid = new Grid( shell, SWT.SINGLE );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 0 );

    grid.setSelection( 1, 1 );

    GridItem[] expected = new GridItem[]{ items[ 1 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByRange_SingleWithDifferentSrartEnd() {
    grid = new Grid( shell, SWT.SINGLE );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 0 );

    grid.setSelection( 1, 3 );

    GridItem[] expected = new GridItem[]{ items[ 0 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByRange_Multi() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 0 );

    grid.setSelection( 1, 3 );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByRange_WithSelectionDisabled() {
    grid = new Grid( shell, SWT.MULTI );
    grid.setSelectionEnabled( false );
    createGridItems( grid, 5, 0 );

    grid.setSelection( 1, 3 );

    assertTrue( Arrays.equals( new Grid[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByRange_StartBiggerThanEnd() {
    grid = new Grid( shell, SWT.MULTI );
    createGridItems( grid, 5, 0 );
    grid.select( 0 );

    grid.setSelection( 3, 1 );

    assertTrue( Arrays.equals( new Grid[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByIndices_Single() {
    grid = new Grid( shell, SWT.SINGLE );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 0 );

    grid.setSelection( new int[] { 1 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByIndices_SingleWithMultipleIndices() {
    grid = new Grid( shell, SWT.SINGLE );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 0 );

    grid.setSelection( new int[] { 1, 2, 3 } );

    GridItem[] expected = new GridItem[]{ items[ 0 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByIndices_Multi() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 0 );

    grid.setSelection( new int[] { 1, 2, 3 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetSelectionByIndices_NullArgument() {
    grid.setSelection( ( int[] )null );
  }

  @Test
  public void testSetSelectionByIndices_InvalidIndex() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.setSelection( new int[] { 1, 55, 2, 3 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByIndices_DuplicateIndex() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.setSelection( new int[] { 1, 2, 2, 3 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByItems_Single() {
    grid = new Grid( shell, SWT.SINGLE );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.setSelection( new GridItem[]{ items[ 1 ] } );

    GridItem[] expected = new GridItem[]{ items[ 1 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByItems_SingleWithMultipleItems() {
    grid = new Grid( shell, SWT.SINGLE );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.setSelection( new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] } );

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByItems_WithSelectionDisabled() {
    grid.setSelectionEnabled( false );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.setSelection( new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] } );

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByItems_Multi() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 0 );

    grid.setSelection( new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetSelectionByItems_NullArgument() {
    grid.setSelection( ( GridItem[] )null );
  }

  @Test
  public void testSetSelectionByItems_NullItem() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );

    grid.setSelection( new GridItem[]{ items[ 1 ], null, items[ 3 ] } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testSetSelectionByItems_ItemWithDifferentParent() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    Grid otherGrid = new Grid( shell, SWT.NONE );
    GridItem otherItem = new GridItem( otherGrid, SWT.NONE );

    grid.setSelection( new GridItem[]{ items[ 1 ], otherItem, items[ 3 ] } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetSelectionByItems_DisposedItem() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    items[ 2 ].dispose();

    grid.setSelection( new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] } );
  }

  @Test
  public void testDeselectByIndex() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 1, 3 );

    grid.deselect( 2 );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testDeselectByIndex_InvalidIndex() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 1, 3 );

    grid.deselect( 10 );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 2 ], items[ 3 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testDeselectByRange() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 1, 4 );

    grid.deselect( 2, 3 );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 4 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testDeselectByRange_OutOfItemsSize() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 1, 4 );

    grid.deselect( 2, 12 );

    GridItem[] expected = new GridItem[]{ items[ 1 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testDeselectByIndices() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 1, 4 );

    grid.deselect( new int[]{ 2, 3 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 4 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testDeselectByIndices_NullArgument() {
    try {
      grid.deselect( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testDeselectByIndices_DuplicateIndex() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 1, 4 );

    grid.deselect( new int[]{ 2, 3, 2 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 4 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testDeselectByIndices_InvalidIndex() {
    grid = new Grid( shell, SWT.MULTI );
    GridItem[] items = createGridItems( grid, 5, 0 );
    grid.select( 1, 4 );

    grid.deselect( new int[]{ 2, 3, 14 } );

    GridItem[] expected = new GridItem[]{ items[ 1 ], items[ 4 ] };
    assertTrue( Arrays.equals( expected, grid.getSelection() ) );
  }

  @Test
  public void testDeselectAll() {
    grid = new Grid( shell, SWT.MULTI );
    createGridItems( grid, 5, 0 );
    grid.select( 1, 4 );

    grid.deselectAll();

    assertTrue( Arrays.equals( new GridItem[ 0 ], grid.getSelection() ) );
  }

  @Test
  public void testRemoveByIndex() {
    createGridItems( grid, 3, 3 );

    grid.remove( 4 );

    assertEquals( 8, grid.getItemCount() );
    assertEquals( 2, grid.getRootItemCount() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveByIndex_InvalidIndex() {
    grid.remove( 50 );
  }

  @Test
  public void testRemoveByIndex_RemoveFromSelection() {
    createGridItems( grid, 3, 3 );
    grid.select( 6 );

    grid.remove( 4 );

    assertEquals( 0, grid.getSelectionCount() );
  }

  @Test
  public void testRemoveByRange() {
    createGridItems( grid, 3, 3 );

    grid.remove( 3, 9 );

    assertEquals( 3, grid.getItemCount() );
    assertEquals( 1, grid.getRootItemCount() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveByRange_InvalidRange() {
    createGridItems( grid, 3, 3 );

    grid.remove( 3, 60 );
  }

  @Test
  public void testRemoveByIndices() {
    createGridItems( grid, 3, 3 );

    grid.remove( new int[]{ 3, 5, 8 } );

    assertEquals( 6, grid.getItemCount() );
    assertEquals( 2, grid.getRootItemCount() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveByIndices_NullArgument() {
    grid.remove( null );
  }

  @Test
  public void testRemoveByIndices_DuplicateIndex() {
    createGridItems( grid, 3, 3 );

    grid.remove( new int[]{ 3, 5, 3 } );

    assertEquals( 10, grid.getItemCount() );
    assertEquals( 3, grid.getRootItemCount() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveByIndices_InvalidIndex() {
    createGridItems( grid, 3, 3 );

    grid.remove(new int[]{ 3, 5, 100 } );
  }

  @Test
  public void testRemoveAll() {
    createGridItems( grid, 3, 3 );

    grid.removeAll();

    assertEquals( 0, grid.getItemCount() );
    assertEquals( 0, grid.getRootItemCount() );
  }

  @Test
  public void testGetSelectionIndex() {
    grid = new Grid( shell, SWT.MULTI );
    createGridItems( grid, 3, 3 );
    int indicies[] = new int[]{ 3, 4, 1, 7 };

    grid.setSelection( indicies );

    assertEquals( 3, grid.getSelectionIndex() );
  }

  @Test
  public void testGetSelectionIndex_WithoutSelection() {
    assertEquals( -1, grid.getSelectionIndex() );
  }

  @Test
  public void testGetSelectionIndicies() {
    grid = new Grid( shell, SWT.MULTI );
    createGridItems( grid, 3, 3 );
    int indicies[] = new int[]{ 3, 4, 1, 7 };

    grid.setSelection( indicies );

    assertTrue( Arrays.equals( indicies, grid.getSelectionIndices() ) );
  }

  @Test
  public void testGetSelectionIndicies_WithoutSelection() {
    assertTrue( Arrays.equals( new int[ 0 ], grid.getSelectionIndices() ) );
  }

  @Test
  public void testIsSelectedByIndex_Initial() {
    createGridItems( grid, 3, 0 );

    assertFalse( grid.isSelected( 0 ) );
    assertFalse( grid.isSelected( 1 ) );
    assertFalse( grid.isSelected( 2 ) );
  }

  @Test
  public void testIsSelectedByIndex() {
    createGridItems( grid, 3, 0 );

    grid.select( 1 );

    assertFalse( grid.isSelected( 0 ) );
    assertTrue( grid.isSelected( 1 ) );
    assertFalse( grid.isSelected( 2 ) );
  }

  @Test
  public void testIsSelectedByIndex_InvalidIndex() {
    createGridItems( grid, 3, 0 );

    assertFalse( grid.isSelected( 5 ) );
  }

  @Test
  public void testIsSelectedByItem_Initial() {
    GridItem[] items = createGridItems( grid, 3, 0 );

    assertFalse( grid.isSelected( items[ 0 ] ) );
    assertFalse( grid.isSelected( items[ 1 ] ) );
    assertFalse( grid.isSelected( items[ 2 ] ) );
  }

  @Test
  public void testIsSelectedByItem() {
    GridItem[] items = createGridItems( grid, 3, 0 );

    grid.select( 1 );

    assertFalse( grid.isSelected( items[ 0 ] ) );
    assertTrue( grid.isSelected( items[ 1 ] ) );
    assertFalse( grid.isSelected( items[ 2 ] ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testIsSelectedByItem_NullArgument() {
    grid.isSelected( null );
  }

  @Test
  public void testIsSelectedByItem_DisposedItem() {
    GridItem[] items = createGridItems( grid, 3, 0 );
    grid.select( 1 );

    items[ 1 ].dispose();

    assertFalse( grid.isSelected( items[ 1 ] ) );
  }

  @Test
  public void testGetHeaderVisible_Initial() {
    assertFalse( grid.getHeaderVisible() );
  }

  @Test
  public void testSetHeaderVisible() {
    grid.setHeaderVisible( true );

    assertTrue( grid.getHeaderVisible() );
  }

  @Test
  public void testGetFooterVisible_Initial() {
    assertFalse( grid.getFooterVisible() );
  }

  @Test
  public void testSetFooterVisible() {
    grid.setFooterVisible( true );

    assertTrue( grid.getFooterVisible() );
  }

  @Test
  public void testGetLinesVisible_Initial() {
    assertTrue( grid.getLinesVisible() );
  }

  @Test
  public void testGetLinesVisible() {
    grid.setLinesVisible( false );

    assertFalse( grid.getLinesVisible() );
  }

  @Test
  public void testGetFocusItem_Initial() {
    assertNull( grid.getFocusItem() );
  }

  @Test
  public void testSetFocusItem() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    grid.setFocusItem( items[ 4 ] );

    assertSame( items[ 4 ], grid.getFocusItem() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFocusItem_NullArgument() {
    grid.setFocusItem( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFocusItem_DisposedItem() {
    GridItem item = new GridItem( grid, SWT.NONE );
    item.dispose();

    grid.setFocusItem( item );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFocusItem_WithOtherParent() {
    Grid otherGrid = new Grid( shell, SWT.NONE );
    GridItem item = new GridItem( otherGrid, SWT.NONE );

    grid.setFocusItem( item );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFocusItem_InvisibleItem() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    grid.setFocusItem( items[ 2 ] );
  }

  @Test
  public void testGetFocusColumn_Initial() {
    assertNull( grid.getFocusColumn() );
  }

  @Test
  public void testSetFocusColumn() {
    grid.setCellSelectionEnabled( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );

    grid.setFocusColumn( columns[ 1 ] );

    assertSame( columns[ 1 ], grid.getFocusColumn() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFocusColumn_NullArgument() {
    grid.setFocusColumn( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFocusColumn_DisposedItem() {
    GridColumn column = new GridColumn( grid, SWT.NONE );
    column.dispose();

    grid.setFocusColumn( column );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFocusColumn_WithOtherParent() {
    Grid otherGrid = new Grid( shell, SWT.NONE );
    GridColumn column = new GridColumn( otherGrid, SWT.NONE );

    grid.setFocusColumn( column );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFocusColumn_InvisibleItem() {
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 1 ].setVisible( false );

    grid.setFocusColumn( columns[ 1 ] );
  }

  @Test
  public void testGetFocusColumn_afterColumnDisposal() {
    createGridColumns( grid, 3, SWT.NONE );
    GridColumn column = grid.getColumn( 1 );
    grid.setFocusColumn( column );

    column.dispose();

    assertNull( grid.getFocusColumn() );
  }

  @Test
  public void testGetFocusCell_DisabledCellSelection() {
    assertNull( grid.getFocusCell() );
  }

  @Test
  public void testGetFocusCell_initial() {
    grid.setCellSelectionEnabled( true );

    assertEquals( new Point( -1, -1 ), grid.getFocusCell() );
  }

  @Test
  public void testGetFocusCell() {
    grid.setCellSelectionEnabled( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    GridItem[] items = createGridItems( grid, 3, 0 );

    grid.setFocusItem( items[ 1 ] );
    grid.setFocusColumn( columns[ 1 ] );

    assertEquals( new Point( 1, 1 ), grid.getFocusCell() );
  }

  @Test
  public void testGetColumnOrder_Initial() {
    createGridColumns( grid, 5, SWT.NONE );

    assertTrue( Arrays.equals( new int[]{ 0, 1, 2, 3, 4 }, grid.getColumnOrder() ) );
  }

  @Test
  public void testSetColumnOrder() {
    createGridColumns( grid, 5, SWT.NONE );
    int[] order = new int[]{ 4, 1, 3, 2, 0 };

    grid.setColumnOrder( order );

    assertTrue( Arrays.equals( order, grid.getColumnOrder() ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetColumnOrder_NullArgument() {
    grid.setColumnOrder( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetColumnOrder_DifferentArraySize() {
    createGridColumns( grid, 5, SWT.NONE );
    int[] order = new int[]{ 4, 1, 3, 2, 0, 6 };

    grid.setColumnOrder( order );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetColumnOrder_InvalidColumnIndex() {
    createGridColumns( grid, 5, SWT.NONE );
    int[] order = new int[]{ 4, 1, 33, 2, 0 };

    grid.setColumnOrder( order );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetColumnOrder_DuplicateColumnIndex() {
    createGridColumns( grid, 5, SWT.NONE );
    int[] order = new int[]{ 3, 1, 3, 2, 0 };

    grid.setColumnOrder( order );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetColumnOrder_MoveToColumnGroup() {
    createGridColumns( grid, 2, SWT.NONE );
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    createGridColumns( group, 2, SWT.NONE );
    createGridColumns( grid, 2, SWT.NONE );

    grid.setColumnOrder( new int[]{ 1, 2, 0, 3, 4, 5 } );
  }

  @Test
  public void testSetColumnOrder_MoveInSameColumnGroup() {
    createGridColumns( grid, 2, SWT.NONE );
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    createGridColumns( group, 2, SWT.NONE );
    createGridColumns( grid, 2, SWT.NONE );

    int[] order = new int[]{ 0, 1, 3, 2, 4, 5 };
    grid.setColumnOrder( order );

    assertTrue( Arrays.equals( order, grid.getColumnOrder() ) );
  }

  @Test
  public void testGetColumnOrder_AfterColumnAdd() {
    createGridColumns( grid, 5, SWT.NONE );
    grid.setColumnOrder( new int[]{ 4, 1, 3, 2, 0 } );

    new GridColumn( grid, SWT.NONE, 2 );

    int[] expected = new int[]{ 5, 1, 2, 4, 3, 0 };
    assertTrue( Arrays.equals( expected, grid.getColumnOrder() ) );
  }

  @Test
  public void testGetColumnOrder_AfterColumnRemove() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );
    grid.setColumnOrder( new int[]{ 4, 1, 3, 2, 0 } );

    columns[ 3 ].dispose();

    int[] expected = new int[]{ 3, 1, 2, 0 };
    assertTrue( Arrays.equals( expected, grid.getColumnOrder() ) );
  }

  @Test
  public void testGetColumnOrder_UpdatePrimaryCheckColumn() {
    grid = new Grid( shell, SWT.CHECK );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );

    grid.setColumnOrder( new int[] { 2, 0, 1 } );

    assertTrue( columns[ 2 ].isCheck() );
  }

  @Test
  public void testGetNextVisibleItem_CollapsedItem() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    assertSame( items[ 8 ], grid.getNextVisibleItem( items[ 4 ] ) );
  }

  @Test
  public void testGetNextVisibleItem_ExpandedItem() {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 4 ].setExpanded( true );

    assertSame( items[ 5 ], grid.getNextVisibleItem( items[ 4 ] ) );
  }

  @Test
  public void testGetNextVisibleItem_NullArgument() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    assertSame( items[ 0 ], grid.getNextVisibleItem( null ) );
  }

  @Test
  public void testGetNextVisibleItem_LastItem() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    assertNull( grid.getNextVisibleItem( items[ 11 ] ) );
  }

  @Test
  public void testGetNextVisibleItem_AllNextNotVisible() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    assertNull( grid.getNextVisibleItem( items[ 8 ] ) );
  }

  @Test
  public void testGetPreviousVisibleItem_CollapsedItem() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    assertSame( items[ 0 ], grid.getPreviousVisibleItem( items[ 4 ] ) );
  }

  @Test
  public void testGetPreviousVisibleItem_ExpandedItem() {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setExpanded( true );

    assertSame( items[ 3 ], grid.getPreviousVisibleItem( items[ 4 ] ) );
  }

  @Test
  public void testGetPreviousVisibleItem_NullArgument() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    assertSame( items[ 8 ], grid.getPreviousVisibleItem( null ) );
  }

  @Test
  public void testGetPreviousVisibleItem_FirstItem() {
    GridItem[] items = createGridItems( grid, 3, 3 );

    assertNull( grid.getPreviousVisibleItem( items[ 0 ] ) );
  }

  @Test
  public void testGetNextVisibleColumn_NextNotVisible() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );
    columns[ 3 ].setVisible( false );

    assertSame( columns[ 4 ], grid.getNextVisibleColumn( columns[ 2 ] ) );
  }

  @Test
  public void testGetNextVisibleColumn_NextVisible() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    assertSame( columns[ 3 ], grid.getNextVisibleColumn( columns[ 2 ] ) );
  }

  @Test
  public void testGetNextVisibleColumn_NullArgument() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    assertSame( columns[ 0 ], grid.getNextVisibleColumn( null ) );
  }

  @Test
  public void testGetNextVisibleColumn_LastColumn() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    assertNull( grid.getNextVisibleColumn( columns[ 4 ] ) );
  }

  @Test
  public void testGetNextVisibleColumn_AllNextNotVisible() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );
    columns[ 3 ].setVisible( false );
    columns[ 4 ].setVisible( false );

    assertNull( grid.getNextVisibleColumn( columns[ 2 ] ) );
  }

  @Test
  public void testGetNextVisibleColumn_WithColumnOrder() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );
    grid.setColumnOrder( new int[]{ 4, 0, 2, 1, 3 } );

    assertSame( columns[ 1 ], grid.getNextVisibleColumn( columns[ 2 ] ) );
  }

  @Test
  public void testGetPreviousVisibleColumn_PreviousNotVisible() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );
    columns[ 1 ].setVisible( false );

    assertSame( columns[ 0 ], grid.getPreviousVisibleColumn( columns[ 2 ] ) );
  }

  @Test
  public void testGetPreviousVisibleColumn_PreviousVisible() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    assertSame( columns[ 1 ], grid.getPreviousVisibleColumn( columns[ 2 ] ) );
  }

  @Test
  public void testGetPreviousVisibleColumn_NullArgument() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    assertSame( columns[ 4 ], grid.getPreviousVisibleColumn( null ) );
  }

  @Test
  public void testGetPreviousVisibleColumn_FirstColumn() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );

    assertNull( grid.getPreviousVisibleColumn( columns[ 0 ] ) );
  }

  @Test
  public void testGetPreviousVisibleColumn_WithColumnOrder() {
    GridColumn[] columns = createGridColumns( grid, 5, SWT.NONE );
    grid.setColumnOrder( new int[]{ 4, 0, 2, 1, 3 } );

    assertSame( columns[ 0 ], grid.getPreviousVisibleColumn( columns[ 2 ] ) );
  }

  @Test
  public void testGetItemHeight_Initial() {
    assertEquals( 27, grid.getItemHeight() );
  }

  @Test
  public void testGetItemHeight() {
    grid.setItemHeight( 30 );

    assertEquals( 30, grid.getItemHeight() );
  }

  @Test
  public void testGetItemHeight_AfterFontChange() {
    // fill the cache
    grid.getItemHeight();
    Font font = new Font( display, "Arial", 20, SWT.BOLD );

    grid.setFont( font );

    assertEquals( 33, grid.getItemHeight() );
  }

  @Test
  public void testGetItemHeight_MinHeight() {
    Font font = new Font( display, "Arial", 8, SWT.NORMAL );
    fakeCellPadding( grid, 0, 0, 0, 0 );
    grid.setFont( font );

    assertEquals( 16, grid.getItemHeight() );
  }

  @Test
  public void testGetItemHeight_WithGridCheck() {
    grid = new Grid( shell, SWT.CHECK );

    assertEquals( 30, grid.getItemHeight() );
  }

  @Test
  public void testGetItemHeight_WithItemImage() {
    createGridColumns( grid, 3, SWT.NONE );
    GridItem item = new GridItem( grid, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    // fill the cache
    grid.getItemHeight();

    item.setImage( 1, image );

    assertEquals( 63, grid.getItemHeight() );
  }

  @Test
  public void testGetItemHeight_AfterClearAll() {
    createGridColumns( grid, 3, SWT.NONE );
    GridItem item = new GridItem( grid, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    item.setImage( 1, image );

    grid.clearAll( true );

    assertEquals( 27, grid.getItemHeight() );
  }

  @Test
  public void testGetHeaderHeight_Initial() {
    createGridColumns( grid, 3, SWT.NONE );

    assertEquals( 0, grid.getHeaderHeight() );
  }

  @Test
  public void testGetHeaderHeight() {
    grid.setHeaderVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    columns[ 0 ].setImage( image );
    columns[ 1 ].setText( "foo" );

    assertEquals( 67, grid.getHeaderHeight() );
  }

  @Test
  public void testGetHeaderHeight_WithColumnGroup() {
    grid.setHeaderVisible( true );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    GridColumn[] columns = createGridColumns( grid, 1, SWT.NONE );
    columns[ 0 ].setImage( image );
    columns[ 0 ].setText( "foo" );
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    group.setImage( image );
    group.setText( "foo" );
    createGridColumns( group, 1, SWT.NONE );

    assertEquals( 134, grid.getHeaderHeight() );
    assertEquals( 67, grid.getGroupHeaderHeight() );
  }

  @Test
  public void testGetHeaderHeight_DifferentColumnHeaderFonts() {
    grid.setHeaderVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 0 ].setHeaderFont( new Font( display, "Arial", 10, SWT.NORMAL ) );
    columns[ 2 ].setHeaderFont( new Font( display, "Arial", 20, SWT.NORMAL ) );

    assertEquals( 37, grid.getHeaderHeight() );
  }

  @Test
  public void testGetHeaderHeight_AfterColumnDispose() {
    grid.setHeaderVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    columns[ 0 ].setImage( image );
    columns[ 1 ].setText( "foo" );
    // fill the cache
    grid.getHeaderHeight();

    columns[ 0 ].dispose();

    assertEquals( 31, grid.getHeaderHeight() );
  }

  @Test
  public void testGetHeaderHeight_AfterTextChange() {
    grid.setHeaderVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 1 ].setText( "foo" );
    // fill the cache
    grid.getHeaderHeight();

    columns[ 1 ].setText( "foo\nbar" );

    assertEquals( 52, grid.getHeaderHeight() );
  }

  @Test
  public void testGetHeaderHeight_AfterImageChange() {
    grid.setHeaderVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    columns[ 0 ].setImage( image );
    columns[ 1 ].setText( "foo" );
    // fill the cache
    grid.getHeaderHeight();

    columns[ 0 ].setImage( null );

    assertEquals( 31, grid.getHeaderHeight() );
  }

  @Test
  public void testGetHeaderHeight_AfterFontChange() {
    grid.setHeaderVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 1 ].setText( "foo" );
    // fill the cache
    grid.getHeaderHeight();

    columns[ 1 ].setHeaderFont( new Font( display, "Arial", 20, SWT.NORMAL ) );

    assertEquals( 37, grid.getHeaderHeight() );
  }

  @Test
  public void testGetHeaderHeight_withWordWrap_withAutoHeight() {
    grid.setHeaderVisible( true );
    grid.setAutoHeight( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 1 ].setHeaderWordWrap( true );
    columns[ 1 ].setText( "foo bar" );

    assertEquals( 52, grid.getHeaderHeight() );
  }

  @Test
  public void testGetHeaderHeight_withWordWrap_withoutAutoHeight() {
    grid.setHeaderVisible( true );
    grid.setAutoHeight( false );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 1 ].setHeaderWordWrap( true );
    columns[ 1 ].setText( "foo bar" );

    assertEquals( 31, grid.getHeaderHeight() );
  }

  @Test
  public void testGetFooterHeight_Initial() {
    createGridColumns( grid, 3, SWT.NONE );

    assertEquals( 0, grid.getFooterHeight() );
  }

  @Test
  public void testGetFooterHeight() {
    grid.setFooterVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    columns[ 0 ].setFooterImage( image );
    columns[ 1 ].setFooterText( "foo" );

    assertEquals( 67, grid.getFooterHeight() );
  }

  @Test
  public void testGetFooterHeight_DifferentColumnFooterFonts() {
    grid.setFooterVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 0 ].setFooterFont( new Font( display, "Arial", 10, SWT.NORMAL ) );
    columns[ 2 ].setFooterFont( new Font( display, "Arial", 20, SWT.NORMAL ) );

    assertEquals( 37, grid.getFooterHeight() );
  }

  @Test
  public void testGetFooterHeight_AfterColumnDispose() {
    grid.setFooterVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    columns[ 0 ].setFooterImage( image );
    columns[ 1 ].setFooterText( "foo" );
    // fill the cache
    grid.getFooterHeight();

    columns[ 0 ].dispose();

    assertEquals( 31, grid.getFooterHeight() );
  }

  @Test
  public void testGetFooterHeight_AfterTextChange() {
    grid.setFooterVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 1 ].setFooterText( "foo" );
    // fill the cache
    grid.getFooterHeight();

    columns[ 1 ].setFooterText( "foo\nbar" );

    assertEquals( 52, grid.getFooterHeight() );
  }

  @Test
  public void testGetFooterHeight_AfterImageChange() {
    grid.setFooterVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    columns[ 0 ].setFooterImage( image );
    columns[ 1 ].setFooterText( "foo" );
    // fill the cache
    grid.getFooterHeight();

    columns[ 0 ].setFooterImage( null );

    assertEquals( 31, grid.getFooterHeight() );
  }

  @Test
  public void testGetFooterHeight_AfterFontChange() {
    grid.setFooterVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 1 ].setFooterText( "foo" );
    // fill the cache
    grid.getFooterHeight();

    columns[ 1 ].setFooterFont( new Font( display, "Arial", 20, SWT.NORMAL ) );

    assertEquals( 37, grid.getFooterHeight() );
  }

  @Test
  public void testGetFooterHeight_wrapsText_withAutoHeight() {
    grid.setFooterVisible( true );
    grid.setAutoHeight( true );
    GridColumn column = new GridColumn( grid, SWT.NONE );
    column.setWidth( 40 );
    column.setHeaderWordWrap( true );
    column.setFooterText( "foo bar" );

    assertEquals( 52, grid.getFooterHeight() );
  }

  @Test
  public void testGetFooterHeight_doesNotWrapText_withoutAutoHeight() {
    grid.setFooterVisible( true );
    grid.setAutoHeight( false );
    GridColumn column = new GridColumn( grid, SWT.NONE );
    column.setWidth( 40 );
    column.setHeaderWordWrap( true );
    column.setFooterText( "foo bar" );

    assertEquals( 31, grid.getFooterHeight() );
  }

  @Test
  public void testGetFooterHeight_wrapsText_withFooterSpan() {
    grid.setFooterVisible( true );
    grid.setAutoHeight( true );
    GridColumn column1 = new GridColumn( grid, SWT.NONE );
    column1.setWidth( 20 );
    GridColumn column2 = new GridColumn( grid, SWT.NONE );
    column2.setWidth( 20 );
    column1.setHeaderWordWrap( true );
    column1.setFooterText( "foo bar" );
    column1.setData( "footerSpan", Integer.valueOf( 2 ) );

    assertEquals( 52, grid.getFooterHeight() );
  }

  @Test
  public void testGetFooterHeight_doesNotWrapText_withFooterSpanAndEnoughSpace() {
    grid.setFooterVisible( true );
    grid.setAutoHeight( true );
    GridColumn column1 = new GridColumn( grid, SWT.NONE );
    column1.setWidth( 20 );
    GridColumn column2 = new GridColumn( grid, SWT.NONE );
    column2.setWidth( 100 );
    column1.setHeaderWordWrap( true );
    column1.setFooterText( "foofoo bar" );
    column1.setData( "footerSpan", Integer.valueOf( 2 ) );

    assertEquals( 35, grid.getFooterHeight() );
  }

  @Test
  public void testGetGroupHeaderHeight_Initial() {
    createGridColumns( grid, 1, SWT.NONE );
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    createGridColumns( group, 1, SWT.NONE );

    assertEquals( 0, grid.getGroupHeaderHeight() );
  }

  @Test
  public void testGetGroupHeaderHeight() {
    grid.setHeaderVisible( true );
    createGridColumns( grid, 1, SWT.NONE );
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    createGridColumns( group, 1, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    group.setImage( image );
    group.setText( "foo" );

    assertEquals( 67, grid.getGroupHeaderHeight() );
  }

  @Test
  public void testGetGroupHeaderHeight_wrapsText_withAutoHeight() {
    grid.setHeaderVisible( true );
    grid.setAutoHeight( true );
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    GridColumn column1 = new GridColumn( group, SWT.NONE );
    column1.setWidth( 20 );
    GridColumn column2 = new GridColumn( group, SWT.NONE );
    column2.setWidth( 20 );
    group.setHeaderWordWrap( true );
    group.setText( "foo bar" );

    assertEquals( 52, grid.getGroupHeaderHeight() );
  }

  @Test
  public void testGetGroupHeaderHeight_doesNotWrapText_withoutAutoHeight() {
    grid.setHeaderVisible( true );
    grid.setAutoHeight( false );
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    GridColumn column1 = new GridColumn( group, SWT.NONE );
    column1.setWidth( 20 );
    GridColumn column2 = new GridColumn( group, SWT.NONE );
    column2.setWidth( 20 );
    group.setHeaderWordWrap( true );
    group.setText( "foo bar" );

    assertEquals( 31, grid.getGroupHeaderHeight() );
  }

  @Test
  public void testComputeSize() {
    grid = new Grid( shell, SWT.NONE );
    createGridColumns( grid, 3, SWT.NONE );
    createGridItems( grid, 3, 3 );
    int itemHeight = grid.getItemHeight();

    Point preferredSize = grid.computeSize( SWT.DEFAULT, SWT.DEFAULT );

    assertEquals( 120, preferredSize.x );
    assertEquals( 3 * itemHeight, preferredSize.y );
  }

  @Test
  public void testComputeSize_WithScrollBars() {
    createGridColumns( grid, 3, SWT.NONE );
    createGridItems( grid, 3, 3 );
    int itemHeight = grid.getItemHeight();
    int scrollbarSize = 10;

    Point preferredSize = grid.computeSize( SWT.DEFAULT, SWT.DEFAULT );

    assertEquals( 120 + scrollbarSize, preferredSize.x );
    assertEquals( 3 * itemHeight + scrollbarSize, preferredSize.y );
  }

  @Test
  public void testComputeSize_WithBorder() {
    grid = new Grid( shell, SWT.BORDER );
    createGridColumns( grid, 3, SWT.NONE );
    createGridItems( grid, 3, 3 );
    int itemHeight = grid.getItemHeight();
    int borderWidth = grid.getBorderWidth();

    Point preferredSize = grid.computeSize( SWT.DEFAULT, SWT.DEFAULT );

    assertEquals( 120 + 2 * borderWidth, preferredSize.x );
    assertEquals( 3 * itemHeight + 2 * borderWidth, preferredSize.y );
  }

  @Test
  public void testComputeSize_WithExpandedItems() {
    grid = new Grid( shell, SWT.NONE );
    createGridColumns( grid, 3, SWT.NONE );
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].setExpanded( true );
    items[ 4 ].setExpanded( true );
    int itemHeight = grid.getItemHeight();

    Point preferredSize = grid.computeSize( SWT.DEFAULT, SWT.DEFAULT );

    assertEquals( 120, preferredSize.x );
    assertEquals( 9 * itemHeight, preferredSize.y );
  }

  @Test
  public void testUpdateScrollBars_Initial() {
    doFakeRedraw();

    assertFalse( verticalBar.getVisible() );
    assertEquals( 0, verticalBar.getSelection() );
    assertEquals( 1, verticalBar.getMaximum() );
    assertFalse( horizontalBar.getVisible() );
    assertEquals( 0, horizontalBar.getSelection() );
    assertEquals( 1, horizontalBar.getMaximum() );
  }

  @Test
  public void testUpdateScrollBars() {
    createGridColumns( grid, 5, SWT.NONE );
    createGridItems( grid, 20, 3 );

    doFakeRedraw();

    assertTrue( verticalBar.getVisible() );
    assertEquals( 0, verticalBar.getSelection() );
    assertEquals( 20, verticalBar.getMaximum() );
    assertTrue( horizontalBar.getVisible() );
    assertEquals( 0, horizontalBar.getSelection() );
    assertEquals( 300, horizontalBar.getMaximum() );
  }

  @Test
  public void testUpdateScrollBars_OnColumnChange() {
    createGridColumns( grid, 4, SWT.NONE );

    GridColumn column = new GridColumn( grid, SWT.NONE );
    doFakeRedraw();
    assertTrue( horizontalBar.getVisible() );

    column.dispose();
    doFakeRedraw();
    assertFalse( horizontalBar.getVisible() );
  }

  @Test
  public void testUpdateScrollBars_OnColumnWidthChange() {
    createGridColumns( grid, 4, SWT.NONE );

    grid.getColumn( 3 ).setWidth( 90 );
    doFakeRedraw();
    assertTrue( horizontalBar.getVisible() );

    grid.getColumn( 3 ).setWidth( 70 );
    doFakeRedraw();
    assertFalse( horizontalBar.getVisible() );
  }

  @Test
  public void testUpdateScrollBars_OnItemExpandChange() {
    createGridItems( grid, 3, 10 );

    grid.getItem( 0 ).setExpanded( true );
    doFakeRedraw();
    assertTrue( verticalBar.getVisible() );

    grid.getItem( 0 ).setExpanded( false );
    doFakeRedraw();
    assertFalse( verticalBar.getVisible() );
  }

  @Test
  public void testUpdateScrollBars_OnResize() {
    createGridColumns( grid, 5, SWT.NONE );
    createGridItems( grid, 10, 3 );

    grid.setSize( 500, 500 );
    doFakeRedraw();

    assertFalse( verticalBar.getVisible() );
    assertFalse( horizontalBar.getVisible() );
  }

  @Test
  public void testUpdateScrollBars_OnHeaderVisible() {
    createGridColumns( grid, 1, SWT.NONE );
    createGridItems( grid, 7, 3 );

    grid.setHeaderVisible( true );
    doFakeRedraw();

    assertTrue( verticalBar.getVisible() );
  }

  @Test
  public void testUpdateScrollBars_OnFooterVisible() {
    createGridColumns( grid, 1, SWT.NONE );
    createGridItems( grid, 7, 3 );

    grid.setFooterVisible( true );
    doFakeRedraw();

    assertTrue( verticalBar.getVisible() );
  }

  @Test
  public void testUpdateScrollBars_OnCollapseColumnGroup() {
    grid.setSize( 90, 100 );
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    GridColumn[] columns = createGridColumns( group, 3, SWT.NONE );
    columns[ 0 ].setDetail( false );
    columns[ 1 ].setSummary( false );

    group.setExpanded( false );
    doFakeRedraw();

    assertFalse( horizontalBar.getVisible() );
  }

  @Test
  public void testGetTopIndex_Initial() {
    createGridItems( grid, 20, 3 );

    assertEquals( 0, grid.getTopIndex() );
  }

  @Test
  public void testSetTopIndex() {
    createGridItems( grid, 20, 3 );

    grid.setTopIndex( 4 );

    assertEquals( 4, grid.getTopIndex() );
  }

  @Test
  public void testSetTopIndex_InvisibleSubItem() {
    createGridItems( grid, 20, 3 );

    grid.setTopIndex( 3 );

    assertEquals( 0, grid.getTopIndex() );
  }

  @Test
  public void testSetTopIndex_VisibleSubItem() {
    createGridItems( grid, 20, 3 );
    grid.getItem( 4 ).setExpanded( true );

    grid.setTopIndex( 6 );

    assertEquals( 6, grid.getTopIndex() );
  }

  @Test
  public void testSetTopIndex_AdjustTopIndex() {
    createGridItems( grid, 20, 0 );

    grid.setTopIndex( 18 );

    assertEquals( 13, grid.getTopIndex() );
  }

  @Test
  public void testGetTopIndex_OnItemAdd() {
    createGridItems( grid, 20, 3 );
    grid.setTopIndex( 12 );

    new GridItem( grid, SWT.NONE, 0 );

    assertEquals( 9, grid.getTopIndex() );
  }

  @Test
  public void testGetTopIndex_DifferentItemHeight() {
    GridItem[] items = createGridItems( grid, 20, 0 );
    items[ 16 ].setHeight( grid.getItemHeight() * 2  );

    grid.setTopIndex( 18 );

    assertEquals( 14, grid.getTopIndex() );
  }

  @Test
  public void testAdjustTopIndexOnResize() {
    createGridItems( grid, 15, 3 );
    grid.setTopIndex( 4 );

    grid.setSize( 500, 500 );

    assertEquals( 0, grid.getTopIndex() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testShowItem_NullArgument() {
    grid.showItem( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testShowItem_DisposedItem() {
    GridItem item = new GridItem( grid, SWT.NONE );
    item.dispose();

    grid.showItem( item );
  }

  @Test
  public void testShowItem_ScrollDown() {
    GridItem[] items = createGridItems( grid, 20, 3 );

    grid.showItem( items[ 40 ] );

    assertEquals( 40, grid.getTopIndex() );
  }

  @Test
  public void testShowItem_ScrollUp() {
    GridItem[] items = createGridItems( grid, 20, 3 );
    grid.setTopIndex( 12 );

    grid.showItem( items[ 4 ] );

    assertEquals( 4, grid.getTopIndex() );
  }

  @Test
  public void testShowItem_NoScroll() {
    GridItem[] items = createGridItems( grid, 20, 3 );
    grid.setTopIndex( 12 );

    grid.showItem( items[ 14 ] );

    assertEquals( 12, grid.getTopIndex() );
  }

  @Test
  public void testShowItem_SubItemScrollDown() {
    GridItem[] items = createGridItems( grid, 20, 3 );

    grid.showItem( items[ 41 ] );

    assertEquals( 41, grid.getTopIndex() );
    assertTrue( items[ 40 ].isExpanded() );
  }

  @Test
  public void testShowItem_SubItemScrollUp() {
    GridItem[] items = createGridItems( grid, 20, 3 );
    grid.setTopIndex( 12 );

    grid.showItem( items[ 5 ] );

    assertEquals( 5, grid.getTopIndex() );
    assertTrue( items[ 4 ].isExpanded() );
  }

  @Test
  public void testShowItem_FireExpandEvent() {
    grid.addListener( SWT.Expand, new LoggingListener() );
    GridItem[] items = createGridItems( grid, 20, 3 );

    grid.showItem( items[ 41 ] );

    assertEquals( 1, eventLog.size() );
    assertSame( items[ 40 ], eventLog.get( 0 ).item );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testShowColumn_NullArgument() {
    grid.showColumn( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testShowColumn_DisposedColumn() {
    GridColumn column = new GridColumn( grid, SWT.NONE );
    column.dispose();

    grid.showColumn( column );
  }

  @Test
  public void testShowColumn_ScrollRight() {
    GridColumn[] columns = createGridColumns( grid, 10, SWT.NONE );

    grid.showColumn( columns[ 4 ] );

    assertEquals( 100, horizontalBar.getSelection() );
  }

  @Test
  public void testShowColumn_ScrollLeft() {
    GridColumn[] columns = createGridColumns( grid, 10, SWT.NONE );
    horizontalBar.setSelection( 150 );

    grid.showColumn( columns[ 2 ] );

    assertEquals( 60, horizontalBar.getSelection() );
  }

  @Test
  public void testShowColumn_NoScroll() {
    GridColumn[] columns = createGridColumns( grid, 10, SWT.NONE );
    horizontalBar.setSelection( 30 );

    grid.showColumn( columns[ 2 ] );

    assertEquals( 30, horizontalBar.getSelection() );
  }

  @Test
  public void testShowColumn_FireExpandEvent() {
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    group.addListener( SWT.Collapse, new LoggingListener() );
    GridColumn[] columns = createGridColumns( group, 10, SWT.NONE );
    columns[ 0 ].setDetail( false );
    columns[ 1 ].setSummary( false );

    grid.showColumn( columns[ 0 ] );

    assertEquals( 1, eventLog.size() );
    assertSame( group, eventLog.get( 0 ).widget );
  }

  @Test
  public void testShowSelection() {
    grid = new Grid( shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
    grid.setSize( 200, 200 );
    createGridItems( grid, 20, 3 );
    grid.setSelection( new int[]{ 4, 8, 24 } );
    grid.setTopIndex( 12 );

    grid.showSelection();

    assertEquals( 4, grid.getTopIndex() );
  }

  @Test
  public void testGetOrigin() {
    GridColumn[] columns = createGridColumns( grid, 10, SWT.NONE );
    GridItem[] items = createGridItems( grid, 20, 3 );
    horizontalBar.setSelection( 150 );
    grid.setTopIndex( 40 );

    Point expected = new Point( -30, 2 * grid.getItemHeight() );
    assertEquals( expected, grid.getOrigin( columns[ 3 ], items[ 48 ] ) );
  }

  @Test
  public void testGetOrigin_SubItems() {
    GridColumn[] columns = createGridColumns( grid, 10, SWT.NONE );
    GridItem[] items = createGridItems( grid, 20, 3 );
    items[ 40 ].setExpanded( true );
    horizontalBar.setSelection( 150 );
    grid.setTopIndex( 40 );

    Point expected = new Point( -30, 5 * grid.getItemHeight() );
    assertEquals( expected, grid.getOrigin( columns[ 3 ], items[ 48 ] ) );
  }

  @Test
  public void testGetOrigin_HeaderVisible() {
    GridColumn[] columns = createGridColumns( grid, 10, SWT.NONE );
    GridItem[] items = createGridItems( grid, 20, 3 );
    horizontalBar.setSelection( 150 );
    grid.setHeaderVisible( true );
    grid.setTopIndex( 40 );

    Point expected = new Point( -30, 2 * grid.getItemHeight() + grid.getHeaderHeight() );
    assertEquals( expected, grid.getOrigin( columns[ 3 ], items[ 48 ] ) );
  }

  @Test
  public void testGetOrigin_RowHeaderVisible() {
    GridColumn[] columns = createGridColumns( grid, 10, SWT.NONE );
    GridItem[] items = createGridItems( grid, 20, 3 );
    horizontalBar.setSelection( 150 );
    grid.setRowHeaderVisible( true, 10 );
    grid.setTopIndex( 40 );

    Point expected = new Point( -20, 2 * grid.getItemHeight() );
    assertEquals( expected, grid.getOrigin( columns[ 3 ], items[ 48 ] ) );
  }

  @Test
  public void testIsShown() {
    GridItem[] items = createGridItems( grid, 20, 0 );
    grid.setTopIndex( 5 );

    assertTrue( grid.isShown( items[ 6 ] ) );
  }

  @Test
  public void testIsShown_HiddenItem() {
    GridItem[] items = createGridItems( grid, 20, 0 );
    grid.setTopIndex( 5 );

    assertFalse( grid.isShown( items[ 4 ] ) );
  }

  @Test
  public void testIsShown_InvisibleItem() {
    GridItem[] items = createGridItems( grid, 20, 3 );
    grid.setTopIndex( 20 );

    assertFalse( grid.isShown( items[ 22 ] ) );
  }

  @Test
  public void testIsShown_PartlyVisibleItem() {
    GridItem[] items = createGridItems( grid, 20, 0 );
    grid.setTopIndex( 7 );

    assertTrue( grid.isShown( items[ 13 ] ) );
    assertFalse( grid.isShown( items[ 14 ] ) );
  }

  @Test
  public void testGetAdapter_IGridAdapter() {
    assertNotNull( grid.getAdapter( IGridAdapter.class ) );
  }

  @Test
  public void testGetAdapter_ItemProvider() {
    assertNotNull( grid.getAdapter( ItemProvider.class ) );
  }

  @Test
  public void testItemProvider_visitedItems() {
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    GridColumn column = new GridColumn( group, SWT.NONE );
    GridColumn rowHeader = grid.getRowHeadersColumn();
    GridItem item = new GridItem( grid, SWT.NONE );

    List<Item> items = getVisitedItems();

    assertEquals( Arrays.asList( group, rowHeader, column, item ), items );
  }

  @Test
  public void testGetAdapter_ICellToolTipAdapter() {
    assertNotNull( grid.getAdapter( ICellToolTipAdapter.class ) );
  }

  @Test
  public void testICellToolTipAdapter_hasCellToolTipProvider() {
    assertNotNull( grid.getAdapter( ICellToolTipAdapter.class ).getCellToolTipProvider() );
  }

  @Test
  public void testICellToolTipAdapter_GetCellToolTipText() {
    createGridColumns( grid, 3, SWT.NONE );
    GridItem[] items = createGridItems( grid, 3, 0 );
    items[ 1 ].setToolTipText( 1, "foo" );

    ICellToolTipAdapter cellToolTipAdapter = grid.getAdapter( ICellToolTipAdapter.class );
    cellToolTipAdapter.getCellToolTipProvider().getToolTipText( items[ 1 ], 1 );

    assertEquals( "foo", cellToolTipAdapter.getCellToolTipText() );
  }

  @Test
  public void testColumnGroup_Initial() {
    assertEquals( 0, grid.getColumnGroupCount() );
  }

  @Test
  public void testGetColumnGroupCount_AddGroup() {
    new GridColumnGroup( grid, SWT.NONE );

    assertEquals( 1, grid.getColumnGroupCount() );
  }

  @Test
  public void testGetColumnGroupCount_RemoveGroup() {
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );

    group.dispose();

    assertEquals( 0, grid.getColumnGroupCount() );
  }

  @Test
  public void testGetColumnGroups() {
    GridColumnGroup group1 = new GridColumnGroup( grid, SWT.NONE );
    GridColumnGroup group2 = new GridColumnGroup( grid, SWT.NONE );

    GridColumnGroup[] expected = new GridColumnGroup[] { group1,  group2 };
    assertTrue( Arrays.equals( expected, grid.getColumnGroups() ) );
  }

  @Test
  public void testGetColumnGroup() {
    GridColumnGroup group1 = new GridColumnGroup( grid, SWT.NONE );
    GridColumnGroup group2 = new GridColumnGroup( grid, SWT.NONE );

    assertSame( group1, grid.getColumnGroup( 0 ) );
    assertSame( group2, grid.getColumnGroup( 1 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetColumnGroup_InvalidIndex() {
    grid.getColumnGroup( 3 );
  }

  @Test
  public void testDisposeColumnGroupOnGridDispose() {
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );

    grid.dispose();

    assertTrue( group.isDisposed() );
  }

  @Test
  public void testCheckBoxLeftOffset() {
    GridColumn[] columns = createGridColumns( grid, 2, SWT.CHECK );
    columns[ 0 ].setWidth( 100 );
    columns[ 1 ].setWidth( 100 );
    createGridItems( grid, 1, 1 );

    assertEquals( 0, getCheckBoxOffset( 0 ) );
    assertEquals( 6, getCheckBoxOffset( 1 ) );
  }

  @Test
  public void testCheckBoxLeftOffset_CenteredWithoutContent() {
    GridColumn[] columns = createGridColumns( grid, 2, SWT.CHECK | SWT.CENTER );
    columns[ 0 ].setWidth( 100 );
    columns[ 1 ].setWidth( 100 );
    createGridItems( grid, 1, 1 );

    assertEquals( 0, getCheckBoxOffset( 0 ) );
    assertEquals( 39, getCheckBoxOffset( 1 ) );
  }

  @Test
  public void testCheckBoxLeftOffset_CenteredWithContent() {
    GridColumn[] columns = createGridColumns( grid, 2, SWT.CHECK | SWT.CENTER );
    columns[ 0 ].setWidth( 100 );
    columns[ 1 ].setWidth( 100 );
    createGridItems( grid, 1, 1 );
    grid.getRootItem( 0 ).setText( 1, "foo" );

    assertEquals( 0, getCheckBoxOffset( 0 ) );
    assertEquals( 6, getCheckBoxOffset( 1 ) );
  }

  @Test
  public void testGetBottomIndex_SameItemHeight() {
    createGridItems( grid, 20, 0 );

    grid.setTopIndex( 4 );

    assertEquals( 11, grid.getBottomIndex() );
  }

  @Test
  public void testGetBottomIndex_DifferentItemHeight() {
    GridItem[] items = createGridItems( grid, 20, 0 );
    items[ 6 ].setHeight( grid.getItemHeight() * 2  );

    grid.setTopIndex( 4 );

    assertEquals( 10, grid.getBottomIndex() );
  }

  @Test
  public void testMarkupTextWithoutMarkupEnabled() {
    grid.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );
    GridItem item = new GridItem( grid, SWT.NONE );

    try {
      item.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test( expected = IllegalArgumentException.class )
  public void testMarkupTextWithMarkupEnabled() {
    grid.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    GridItem item = new GridItem( grid, SWT.NONE );

    item.setText( "invalid xhtml: <<&>>" );
  }

  @Test
  public void testMarkupTextWithMarkupEnabled_ValidationDisabled() {
    grid.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    grid.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );
    GridItem item = new GridItem( grid, SWT.NONE );

    try {
      item.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testSetMarkupEnabled_onDirtyWidget() {
    new GridItem( grid, SWT.NONE );

    try {
      grid.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
      fail();
    } catch( SWTException expected ) {
      assertTrue( expected.throwable instanceof IllegalStateException );
    }
  }

  @Test
  public void testSetMarkupEnabled_onDirtyWidget_onceEnabledBefore() {
    grid.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    new GridItem( grid, SWT.NONE );

    grid.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
  }

  @Test
  public void testDisableMarkupIsIgnored() {
    grid.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    grid.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    assertEquals( Boolean.TRUE, grid.getData( RWT.MARKUP_ENABLED ) );
  }

  @Test
  public void testResolvedItems() {
    grid.setSize( 200, 100 );
    grid.setItemCount( 100 );

    doFakeRedraw();

    assertEquals( 100, countResolvedGridItems() );
  }

  @Test
  public void testResolvedItems_onVirtual() {
    grid = new Grid( shell, SWT.V_SCROLL | SWT.VIRTUAL );
    grid.setSize( 200, 100 );
    grid.setItemCount( 100 );

    doFakeRedraw();

    assertEquals( 4, countResolvedGridItems() );
  }

  @Test
  public void testResolvedItems_onVirtual_afterTopIndexChange() {
    grid = new Grid( shell, SWT.V_SCROLL | SWT.VIRTUAL );
    grid.setSize( 200, 100 );
    grid.setItemCount( 100 );
    doFakeRedraw();

    grid.setTopIndex( 50 );
    doFakeRedraw();

    assertEquals( 8, countResolvedGridItems() );
  }

  @Test
  public void testResolvedItems_onVirtual_afterItemDisposal() {
    grid = new Grid( shell, SWT.V_SCROLL | SWT.VIRTUAL );
    grid.setSize( 200, 100 );
    grid.setItemCount( 100 );
    doFakeRedraw();

    grid.getItem( 1 ).dispose();
    doFakeRedraw();

    assertEquals( 4, countResolvedGridItems() );
  }

  @Test
  public void testResolvedItems_onVirtual_afterClear() {
    grid = new Grid( shell, SWT.V_SCROLL | SWT.VIRTUAL );
    grid.setSize( 200, 100 );
    grid.setItemCount( 100 );
    doFakeRedraw();

    grid.setTopIndex( 50 );
    doFakeRedraw();

    grid.clearAll( true );
    doFakeRedraw();

    assertEquals( 8, countResolvedGridItems() );
  }

  @Test
  public void testResolvedItems_onVirtual_afterAddingHiddenItemsInSetData() {
    grid = new Grid( shell, SWT.V_SCROLL | SWT.VIRTUAL );
    grid.setSize( 200, 100 );
    grid.setItemCount( 100 );
    grid.addListener( SWT.SetData, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        GridItem item = ( GridItem )event.item;
        if( event.index == 0 && item.getParentItem() == null ) {
          for( int i = 0; i < 5; i++ ) {
            new GridItem( item, SWT.NONE );
          }
        }
      }
    } );

    doFakeRedraw();

    assertEquals( 4, countResolvedGridItems() );
    assertTrue( grid.getItem( 0 ).isResolved() );
    assertTrue( grid.getItem( 6 ).isResolved() );
    assertTrue( grid.getItem( 7 ).isResolved() );
    assertTrue( grid.getItem( 8 ).isResolved() );
  }

  @Test
  public void testResolvedItems_onVirtual_afterAddingVisibleItemsInSetData() {
    grid = new Grid( shell, SWT.V_SCROLL | SWT.VIRTUAL );
    grid.setSize( 200, 100 );
    grid.setItemCount( 100 );
    grid.addListener( SWT.SetData, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        GridItem item = ( GridItem )event.item;
        if( event.index == 0 && item.getParentItem() == null ) {
          item.setExpanded( true );
          for( int i = 0; i < 5; i++ ) {
            new GridItem( item, SWT.NONE );
          }
        }
      }
    } );

    doFakeRedraw();

    assertEquals( 4, countResolvedGridItems() );
    assertTrue( grid.getItem( 0 ).isResolved() );
    assertTrue( grid.getItem( 1 ).isResolved() );
    assertTrue( grid.getItem( 2 ).isResolved() );
    assertTrue( grid.getItem( 3 ).isResolved() );
  }

  @Test
  public void testRemoveAll_disposeInReverseOrder() {
    final List<String> log = new ArrayList<String>();
    createGridItems( grid, 5, 0 );
    for( GridItem item : grid.getItems() ) {
      item.addDisposeListener( new DisposeListener() {
        @Override
        public void widgetDisposed( DisposeEvent event ) {
          GridItem item = ( GridItem )event.getSource();
          log.add( item.getText() );
        }
      } );
    }

    grid.removeAll();

    String[] expected = { "root_4", "root_3", "root_2", "root_1", "root_0" };
    assertArrayEquals( expected, log.toArray( new String[ 0 ] ) );
  }

  @Test
  public void testSetItemCount_disposeInReverseOrder() {
    final List<String> log = new ArrayList<String>();
    createGridItems( grid, 5, 0 );
    for( GridItem item : grid.getItems() ) {
      item.addDisposeListener( new DisposeListener() {
        @Override
        public void widgetDisposed( DisposeEvent event ) {
          GridItem item = ( GridItem )event.getSource();
          log.add( item.getText() );
        }
      } );
    }

    grid.setItemCount( 0 );

    String[] expected = { "root_4", "root_3", "root_2", "root_1", "root_0" };
    assertArrayEquals( expected, log.toArray( new String[ 0 ] ) );
  }

  @Test
  public void testGetMaxContentWidth_resolveOnlyVisibleItems() {
    grid = new Grid( shell, SWT.V_SCROLL | SWT.VIRTUAL );
    GridColumn column = new GridColumn( grid, SWT.NONE );
    grid.setSize( 200, 100 );
    grid.setItemCount( 100 );

    grid.getMaxContentWidth( column );

    assertEquals( 4, countResolvedGridItems() );
  }

  @Test
  public void testIsAutoHeght_Initial() {
    assertFalse( grid.isAutoHeight() );
  }

  @Test
  public void testSetAutoHeght() {
    grid.setAutoHeight( true );

    assertTrue( grid.isAutoHeight() );
  }

  @Test
  public void testSetAutoHeght_invalidatesHeaderFooterHeight() {
    grid.getHeaderHeight();
    grid.getFooterHeight();

    grid.setAutoHeight( true );

    assertFalse( grid.layoutCache.hasHeaderHeight() );
    assertFalse( grid.layoutCache.hasFooterHeight() );
  }

  @Test
  public void testGetImageWidth_withoutCellPadding() {
    fakeCellPadding( grid, 0, 0, 0, 0 );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 1 ].setWidth( 100 );
    GridItem item = new GridItem( grid, SWT.NONE );
    item.setImage( 1, loadImage( display, Fixture.IMAGE_100x50 ) );

    int imageWidth = grid.getAdapter( IGridAdapter.class ).getImageWidth( 1 );

    assertEquals( 100, imageWidth );
  }

  @Test
  public void testGetImageWidth_withCellPadding() {
    fakeCellPadding( grid, 0, 0, 0, 5 );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    columns[ 1 ].setWidth( 100 );
    GridItem item = new GridItem( grid, SWT.NONE );
    item.setImage( 1, loadImage( display, Fixture.IMAGE_100x50 ) );

    int imageWidth = grid.getAdapter( IGridAdapter.class ).getImageWidth( 1 );

    assertEquals( 95, imageWidth );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( grid.getAdapter( WidgetLCA.class ) instanceof GridLCA );
    assertSame( grid.getAdapter( WidgetLCA.class ), grid.getAdapter( WidgetLCA.class ) );
  }

  @Test
  public void testRedraw_onVirtual_withoutItems() {
    grid = new Grid( shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL );

    try {
      doFakeRedraw();
    } catch( Exception notExpected ) {
      fail();
    }
  }

  @Test
  public void testGetIndentionWidth_asTable() {
    createGridItems( grid, 3, 0 );

    int indentationWidth = grid.getAdapter( IGridAdapter.class ).getIndentationWidth();

    assertEquals( 0, indentationWidth );
  }

  @Test
  public void testGetIndentionWidth_asTree() {
    createGridItems( grid, 3, 3 );

    int indentationWidth = grid.getAdapter( IGridAdapter.class ).getIndentationWidth();

    assertEquals( 16, indentationWidth );

  }

  private int countResolvedGridItems() {
    int counter = 0;
    for( Item item : getVisitedItems() ) {
      if( item instanceof GridItem ) {
        counter++;
      }
    }
    return counter;
  }

  private List<Item> getVisitedItems() {
    final List<Item> items = new ArrayList<>();
    grid.getAdapter( ItemProvider.class ).provideItems( new WidgetTreeVisitor() {
      @Override
      public boolean visit( Widget widget ) {
        items.add( ( Item )widget );
        return true;
      }
    });
    return items;
  }

  private void doFakeRedraw() {
    grid.getAdapter( IGridAdapter.class ).doRedraw();
  }

  private void fakeCellPadding( Grid grid, int top, int right, int bottom, int left ) {
    grid.layoutCache.cellPadding = new BoxDimensions( top, right, bottom, left );
  }

  private int getCheckBoxOffset( int index ) {
    return grid.getAdapter( IGridAdapter.class ).getCheckBoxOffset( index );
  }

  private class LoggingListener implements Listener {

    @Override
    public void handleEvent( Event event ) {
      eventLog.add( event );
    }
  }

}
