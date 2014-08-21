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

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Tree_Test {

  private Display display;
  private Shell shell;
  private Tree tree;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    tree = new Tree( shell, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetItemsAndGetItemCount() {
    assertEquals( 0, tree.getItemCount() );
    assertEquals( 0, tree.getItems().length );

    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertEquals( 1, tree.getItemCount() );
    assertEquals( 1, tree.getItems().length );
    assertSame( item, tree.getItems()[ 0 ] );
  }

  @Test
  public void testImage() throws IOException {
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    Image image = createImage( display, Fixture.IMAGE1 );
    item1.setImage( image );
    assertSame( image, item1.getImage() );

    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    image = createImage( display, Fixture.IMAGE2 );
    item2.setImage( image );
    assertSame( image, item2.getImage() );
  }

  @Test
  public void testStyle() {
    assertTrue( ( tree.getStyle() & SWT.SINGLE ) != 0 );
    assertTrue( ( tree.getStyle() & SWT.H_SCROLL ) != 0 );
    assertTrue( ( tree.getStyle() & SWT.V_SCROLL ) != 0 );

    Tree tree2 = new Tree( shell, SWT.SINGLE );
    assertTrue( ( tree2.getStyle() & SWT.SINGLE ) != 0 );
    assertTrue( ( tree2.getStyle() & SWT.H_SCROLL ) != 0 );
    assertTrue( ( tree2.getStyle() & SWT.V_SCROLL ) != 0 );

    Tree tree3 = new Tree( shell, SWT.MULTI );
    assertTrue( ( tree3.getStyle() & SWT.MULTI ) != 0 );
    assertTrue( ( tree3.getStyle() & SWT.H_SCROLL ) != 0 );
    assertTrue( ( tree3.getStyle() & SWT.V_SCROLL ) != 0 );

    Tree tree4 = new Tree( shell, SWT.SINGLE | SWT.MULTI );
    assertTrue( ( tree4.getStyle() & SWT.SINGLE ) != 0 );
    assertTrue( ( tree4.getStyle() & SWT.H_SCROLL ) != 0 );
    assertTrue( ( tree4.getStyle() & SWT.V_SCROLL ) != 0 );
  }

  @Test
  public void testItemHierarchy() {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );

    assertEquals( 1, item.getItems().length );
    assertEquals( null, item.getParentItem() );
    assertEquals( tree, item.getParent() );
    assertEquals( subItem, item.getItems()[ 0 ] );
    assertEquals( tree, subItem.getParent() );
    assertEquals( item, subItem.getParentItem() );
    assertEquals( 0, subItem.getItems().length );
  }

  @Test
  public void testDispose() {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    tree.dispose();

    assertTrue( item.isDisposed() );
    assertTrue( subItem.isDisposed() );
    assertTrue( column.isDisposed() );
    assertEquals( 0, ItemHolder.getItemHolder( tree ).getItems().length );
    assertEquals( 0, ItemHolder.getItemHolder( item ).getItems().length );
    assertEquals( 0, ItemHolder.getItemHolder( subItem ).getItems().length );
  }

  @Test
  public void testDisposeWithFontDisposeInDisposeListener() {
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    final Font font = new Font( display, "font-name", 10, SWT.NORMAL );
    tree.setFont( font );
    tree.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        font.dispose();
      }
    } );
    tree.dispose();
  }

  @Test
  public void testDisposeSelected() {
    tree = new Tree( shell, SWT.MULTI );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    TreeItem otherItem = new TreeItem( tree, SWT.NONE );
    tree.setSelection( new TreeItem[] { subItem, otherItem } );
    item.dispose();

    assertTrue( item.isDisposed() );
    assertTrue( subItem.isDisposed() );
    assertFalse( otherItem.isDisposed() );
    assertEquals( 1, tree.getSelectionCount() );
    assertSame( otherItem, tree.getSelection()[ 0 ] );
  }

  @Test
  public void testIndexOf() {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertEquals( 0, tree.indexOf( item ) );

    item.dispose();
    try {
      tree.indexOf( item );
      fail( "Must not allow to call indexOf for disposed item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    try {
      tree.indexOf( ( TreeItem )null );
      fail( "Must not allow to call indexOf for null" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }

    Tree anotherTree = new Tree( shell, SWT.NONE );
    TreeItem anotherItem = new TreeItem( anotherTree, SWT.NONE );
    assertEquals( -1, tree.indexOf( anotherItem ) );
  }

  @Test
  public void testIndexOfColumns() {
    TreeColumn col = new TreeColumn( tree, SWT.NONE );
    assertEquals( 0, tree.indexOf( col ) );

    col.dispose();
    try {
      tree.indexOf( col );
      fail( "Must not allow to call indexOf for disposed item" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    try {
      tree.indexOf( ( TreeColumn )null );
      fail( "Must not allow to call indexOf for null" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  @Test
  public void testIndexOfAfterDispose() {
    for( int i = 0; i < 10; i++ ) {
      new TreeItem( tree, SWT.NONE );
    }
    TreeItem item8 = tree.getItem( 8 );

    tree.getItem( 3 ).dispose();

    assertEquals( 7, tree.indexOf( item8 ) );
  }

  @Test
  public void testIndexOfAfterInsert() {
    for( int i = 0; i < 10; i++ ) {
      new TreeItem( tree, SWT.NONE );
    }
    TreeItem item8 = tree.getItem( 8 );

    new TreeItem( tree, SWT.NONE, 3 );

    assertEquals( 9, tree.indexOf( item8 ) );
  }

  @Test
  public void testGetItemCountAfterInsert() {
    tree.setItemCount( 10 );

    new TreeItem( tree, SWT.NONE );

    assertEquals( 11, tree.getItemCount() );
  }

  @Test
  public void testGetItemCountAfterRemove() {
    tree.setItemCount( 10 );

    tree.getItem( 3 ).dispose();

    assertEquals( 9, tree.getItemCount() );
  }

  @Test
  public void testExpandCollapse() {
    final StringBuilder log = new StringBuilder();
    tree.addTreeListener( new TreeListener() {
      public void treeCollapsed( TreeEvent e ) {
        log.append( "collapsed" );
      }
      public void treeExpanded( TreeEvent e ) {
        log.append( "expanded" );
      }
    } );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    TreeItem childlessItem = new TreeItem( tree, SWT.NONE );

    // ensure initial state
    assertFalse( item.getExpanded() );

    // ensure changing works
    item.setExpanded( true );
    assertTrue( item.getExpanded() );

    // test that items without sub-items cannot be expanded
    childlessItem.setExpanded( true );
    assertFalse( childlessItem.getExpanded() );

    // test that an item is still expanded when it has no more sub items
    item.setExpanded( true );
    subItem.dispose();
    assertTrue( item.getExpanded() );

    // ensure that calling setExpanded does not raise any events
    assertEquals( "", log.toString() );
  }

  @Test
  public void testSelectionForSingle() {
    tree = new Tree( shell, SWT.SINGLE );

    // Verify that an empty tree has an empty selection
    assertEquals( 0, tree.getItemCount() );
    assertTrue( Arrays.equals( new TreeItem[ 0 ], tree.getSelection() ) );
    assertEquals( 0, tree.getSelectionCount() );

    // Verify that adding a TreeItem does not change the current selection
    TreeItem treeItem1 = new TreeItem( tree, SWT.NONE );
    assertEquals( 1, tree.getItemCount() );
    assertTrue( Arrays.equals( new TreeItem[ 0 ], tree.getSelection() ) );
    assertEquals( 0, tree.getSelectionCount() );

    // Test selecting a single treItem
    tree.setSelection( treeItem1 );
    assertEquals( 1, tree.getSelectionCount() );
    TreeItem[] expected = new TreeItem[] { treeItem1 };
    assertTrue( Arrays.equals( expected, tree.getSelection() ) );

    // Verify that getSelection returns a safe copy
    tree.setSelection( treeItem1 );
    TreeItem[] selection = tree.getSelection();
    selection[ 0 ] = null;
    assertSame( treeItem1, tree.getSelection()[ 0 ] );

    // Test that null-argument leads to an exception
    try {
      tree.setSelection( ( TreeItem )null );
      fail( "must not allow setSelection( null )" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }

    // Test de-selecting all items
    tree.setSelection( new TreeItem[ 0 ] );
    assertEquals( 0, tree.getSelectionCount() );
    assertEquals( 0, tree.getSelection().length );

    // Test selecting a single treeItem with setSelection(TreeItem[])
    tree.setSelection( new TreeItem[] { treeItem1 } );
    assertEquals( 1, tree.getSelectionCount() );
    assertSame( treeItem1, tree.getSelection()[ 0 ] );

    // Test that setSelection(TreeItem[]) copies the argument
    TreeItem[] newSelection = new TreeItem[] { treeItem1 };
    tree.setSelection( newSelection );
    newSelection[ 0 ] = null;
    assertSame( treeItem1, tree.getSelection()[ 0 ] );

    // Test that calling setSelection(TreeItem[]) with a null-argument leads to an exception
    try {
      tree.setSelection( ( org.eclipse.swt.widgets.TreeItem[] )null );
      fail( "must not allow setSelection( null )" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }

    // Test calling setSelection(TreeItem[]) with more than one element
    TreeItem treeItem2 = new TreeItem( tree, SWT.NONE );
    tree.setSelection( new TreeItem[] { treeItem2, treeItem1, null } );
    assertEquals( 0, tree.getSelectionCount() );
    assertTrue( Arrays.equals( new TreeItem[ 0 ], tree.getSelection() ) );

    // Test calling setSelection(TreeItem[]) with one null-element
    // -> must not change current selection
    tree.setSelection( new TreeItem[] { treeItem1 } );
    tree.setSelection( new TreeItem[] { null } );
    assertEquals( 1, tree.getSelectionCount() );
    assertSame( treeItem1, tree.getSelection()[ 0 ] );

    // Verify that selecting a disposed of item throws an exception
    try {
      tree.setSelection( treeItem1 );
      TreeItem disposedItem = new TreeItem( tree, SWT.NONE );
      disposedItem.dispose();
      tree.setSelection( disposedItem );
      fail( "Must not allow to select diposed of tree item." );
    } catch( IllegalArgumentException e ) {
      // ensure that the previously set selection is not changed
      assertSame( treeItem1, tree.getSelection()[ 0 ] );
    }
  }

  @Test
  public void testSelectionForMulti() {
    tree = new Tree( shell, SWT.MULTI );

    // Verify that an empty tree has an empty selection
    assertEquals( 0, tree.getItemCount() );
    assertTrue( Arrays.equals( new TreeItem[ 0 ], tree.getSelection() ) );
    assertEquals( 0, tree.getSelectionCount() );

    // Verify that adding a TreeItem does not change the current selection
    TreeItem treeItem1 = new TreeItem( tree, SWT.NONE );
    assertEquals( 1, tree.getItemCount() );
    assertTrue( Arrays.equals( new TreeItem[ 0 ], tree.getSelection() ) );
    assertEquals( 0, tree.getSelectionCount() );

    // Test de-selecting all items
    tree.setSelection( new TreeItem[ 0 ] );
    assertEquals( 0, tree.getSelectionCount() );
    assertEquals( 0, tree.getSelection().length );

    // Ensure that setSelection( item ) does the same as setSelection( new TreeItem[] { item } )
    tree.setSelection( treeItem1 );
    TreeItem selected1 = tree.getSelection()[ 0 ];
    tree.setSelection( new TreeItem[] { treeItem1 } );
    TreeItem selected2 = tree.getSelection()[ 0 ];
    assertSame( selected1, selected2 );

    // Select two treeItems
    TreeItem treeItem2 = new TreeItem( tree, SWT.NONE );
    tree.setSelection( new TreeItem[] { treeItem1, treeItem2 } );
    assertEquals( 2, tree.getSelectionCount() );
    TreeItem[] expected = new TreeItem[] { treeItem1, treeItem2 };
    assertTrue( Arrays.equals( expected, tree.getSelection() ) );

    // Test calling setSelection(TreeItem[]) with one null-element
    // -> must not change current selection
    tree.setSelection( new TreeItem[] { treeItem1 } );
    tree.setSelection( new TreeItem[] { null } );
    assertEquals( 1, tree.getSelectionCount() );
    assertSame( treeItem1, tree.getSelection()[ 0 ] );

    // Test calling setSelection(TreeItem[]) with only null-elements
    // -> must not change current selection
    tree.setSelection( new TreeItem[] { treeItem1 } );
    tree.setSelection( new TreeItem[] { null, null } );
    assertEquals( 1, tree.getSelectionCount() );
    assertSame( treeItem1, tree.getSelection()[ 0 ] );

    // Select two treeItems, with some null-elements in between
    tree.setSelection( new TreeItem[ 0 ] );
    tree.setSelection( new TreeItem[] { null, treeItem1, null, treeItem2 } );
    assertEquals( 2, tree.getSelectionCount() );
    expected = new TreeItem[] { treeItem1, treeItem2 };
    assertTrue( Arrays.equals( expected, tree.getSelection() ) );

    // Verify that selecting a disposed of item throws an exception
    try {
      tree.setSelection( treeItem1 );
      TreeItem disposedItem = new TreeItem( tree, SWT.NONE );
      disposedItem.dispose();
      tree.setSelection( new TreeItem[]{
        treeItem1, disposedItem
      } );
      fail( "Must not allow to select diposed of tree item(s)." );
    } catch( IllegalArgumentException e ) {
      // ensure that the previously set selection is not changed
      assertSame( treeItem1, tree.getSelection()[ 0 ] );
    }
  }

  @Test
  public void testSelectAllForSingle() {
    tree = new Tree( shell, SWT.SINGLE );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( item1, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    tree.setSelection( item2 );
    tree.selectAll();

    assertSame( item2, tree.getSelection()[ 0 ] );
  }

  @Test
  public void testSelectAllForMulti() {
    tree = new Tree( shell, SWT.MULTI );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    TreeItem item11 = new TreeItem( item1, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    tree.selectAll();

    assertTrue( contains( tree.getSelection(), item1 ) );
    assertTrue( contains( tree.getSelection(), item11 ) );
    assertTrue( contains( tree.getSelection(), item2 ) );
  }

  @Test
  public void testDeselectAll() {
    tree = new Tree( shell, SWT.MULTI );
    TreeItem treeItem1 = new TreeItem( tree, SWT.NONE );
    TreeItem treeItem2 = new TreeItem( tree, SWT.NONE );
    tree.setSelection( new TreeItem[] { treeItem1, treeItem2 } );
    tree.deselectAll();

    assertEquals( 0, tree.getSelectionCount() );
    assertEquals( 0, tree.getSelection().length );
  }

  @Test
  public void testSelectForSingle() {
    tree = new Tree( shell, SWT.SINGLE );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( item1, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    tree.setSelection( item2 );
    tree.select( item1 );

    assertTrue( contains( tree.getSelection(), item1 ) );
    assertFalse( contains( tree.getSelection(), item2 ) );
  }

  @Test
  public void testSelectForMulti() {
    tree = new Tree( shell, SWT.MULTI );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    TreeItem item11 = new TreeItem( item1, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    tree.select( item1 );
    tree.select( item2 );

    assertTrue( contains( tree.getSelection(), item1 ) );
    assertFalse( contains( tree.getSelection(), item11 ) );
    assertTrue( contains( tree.getSelection(), item2 ) );
  }

  @Test
  public void testDeselectForSingle() {
    tree = new Tree( shell, SWT.SINGLE );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( item1, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );

    tree.select( item2 );
    assertTrue( contains( tree.getSelection(), item2 ) );

    tree.deselect( item2 );
    assertFalse( contains( tree.getSelection(), item2 ) );
  }

  @Test
  public void testDeselectForMulti() {
    tree = new Tree( shell, SWT.MULTI );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    TreeItem item11 = new TreeItem( item1, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    tree.selectAll();
    tree.deselect( item11 );

    assertTrue( contains( tree.getSelection(), item1 ) );
    assertFalse( contains( tree.getSelection(), item11 ) );
    assertTrue( contains( tree.getSelection(), item2 ) );
  }

  @Test
  public void testRemoveAllOnEmptyTree() {
    tree = new Tree( shell, SWT.MULTI );

    tree.removeAll();

    assertEquals( 0, tree.getItemCount() );
    assertEquals( 0, tree.getSelection().length );
  }

  @Test
  public void testRemoveAll() {
    tree = new Tree( shell, SWT.MULTI );
    new TreeItem( tree, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    TreeItem item2_1 = new TreeItem( item2, SWT.NONE );
    tree.setSelection( item2 );

    tree.removeAll();

    assertEquals( 0, tree.getItemCount() );
    assertEquals( 0, tree.getSelection().length );
    assertTrue( item2.isDisposed() );
    assertTrue( item2_1.isDisposed() );
  }

  @Test
  public void testVirtualRemoveAll() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    TreeItem item = tree.getItem( 99 );
    assertFalse( item.isDisposed() );

    tree.removeAll();

    assertTrue( item.isDisposed() );
  }

  @Test
  public void testInitialGetTopItemIndex() {
    ITreeAdapter adapter = getTreeAdapter( tree );

    assertEquals( 0, adapter.getTopItemIndex() );
  }

  @Test
  public void testShowItemFlat() {
    tree.setBounds( 0, 0, 200, 200 );
    createTreeItems( tree, 100 );

    tree.showItem( tree.getItem( 70 ) );

    assertEquals( 64, getTreeAdapter( tree ).getTopItemIndex() );
  }

  @Test
  public void testShowItemOnZeroBounds() {
    createTreeItems( tree, 100 );

    tree.showItem( tree.getItem( 70 ) );

    assertEquals( 0, getTreeAdapter( tree ).getTopItemIndex() );
  }

  @Test
  public void testGetParentItem() {
    tree = new Tree( shell, SWT.SINGLE );

    assertNull( tree.getParentItem() );
  }

  @Test
  public void testGetColumnCount() {
    tree = new Tree( shell, SWT.SINGLE );
    assertEquals( 0, tree.getColumnCount() );

    TreeColumn column0 = new TreeColumn( tree, SWT.NONE );
    assertEquals( 1, tree.getColumnCount() );

    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    assertEquals( 2, tree.getColumnCount() );

    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    assertEquals( 3, tree.getColumnCount() );

    column0.dispose();
    assertEquals( 2, tree.getColumnCount() );

    column1.dispose();
    assertEquals( 1, tree.getColumnCount() );

    column2.dispose();
    assertEquals( 0, tree.getColumnCount() );
  }

  @Test
  public void testGetColumnI() {
    tree = new Tree( shell, SWT.SINGLE );

    try {
      tree.getColumn( 0 );
      fail( "No exception thrown for index out of range" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    TreeColumn column0 = new TreeColumn( tree, SWT.LEFT );
    try {
      tree.getColumn( 1 );
      fail( "No exception thrown for index out of range" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    assertEquals( column0, tree.getColumn( 0 ) );

    TreeColumn column1 = new TreeColumn( tree, SWT.LEFT );
    assertEquals( column1, tree.getColumn( 1 ) );

    column1.dispose();
    try {
      tree.getColumn( 1 );
      fail( "No exception thrown for index out of range" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    column0.dispose();
    try {
      tree.getColumn( 0 );
      fail( "No exception thrown for index out of range" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testGetColumns() {
    tree = new Tree( shell, SWT.SINGLE );
    assertEquals( 0, tree.getColumns().length );

    TreeColumn column0 = new TreeColumn( tree, SWT.LEFT );
    TreeColumn[] columns = tree.getColumns();
    assertEquals( 1, columns.length );
    assertEquals( column0, columns[ 0 ] );

    column0.dispose();
    assertEquals( 0, tree.getColumns().length );

    column0 = new TreeColumn( tree, SWT.LEFT );
    TreeColumn column1 = new TreeColumn( tree, SWT.RIGHT, 1 );
    columns = tree.getColumns();
    assertEquals( 2, columns.length );
    assertEquals( column0, columns[ 0 ] );
    assertEquals( column1, columns[ 1 ] );

    column0.dispose();
    columns = tree.getColumns();
    assertEquals( 1, columns.length );
    assertEquals( column1, columns[ 0 ] );

    column1.dispose();
    assertEquals( 0, tree.getColumns().length );
  }

  @Test
  public void testSetHeaderVisible() {
    tree = new Tree( shell, SWT.SINGLE );
    assertFalse( tree.getHeaderVisible() );

    tree.setHeaderVisible( true );
    assertTrue( tree.getHeaderVisible() );

    tree.setHeaderVisible( false );
    assertFalse( tree.getHeaderVisible() );
  }

  @Test
  public void testGetHeaderHeight() {
    tree = new Tree( shell, SWT.SINGLE );
    assertEquals( 0, tree.getHeaderHeight() );

    tree.setHeaderVisible( true );
    assertTrue( tree.getHeaderHeight() > 0 );

    tree.setHeaderVisible( false );
    assertEquals( 0, tree.getHeaderHeight() );
  }

  @Test
  public void testHeaderHeightWithCustomUserFont() {
    tree = new Tree( shell, SWT.SINGLE );
    tree.setHeaderVisible( true );
    int headerHeight = tree.getHeaderHeight();

    tree.setFont( new Font( display, "Arial", 30, SWT.NORMAL ) );

    assertTrue( headerHeight < tree.getHeaderHeight() );
  }

  @Test
  public void testMultiLineHeaderHeight() {
    tree = new Tree( shell, SWT.SINGLE );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    tree.setHeaderVisible( true );

    column.setText( "Multi line\nHeader" );

    assertEquals( 52, tree.getHeaderHeight() );
  }

  @Test
  public void testSetSortColumn() {
    tree = new Tree( shell, SWT.SINGLE );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    assertEquals( null, tree.getSortColumn() );

    tree.setSortColumn( column );
    assertEquals( column, tree.getSortColumn() );

    tree.setSortColumn( null );
    assertEquals( null, tree.getSortColumn() );
  }

  @Test
  public void testSetSortDirection() {
    tree = new Tree( shell, SWT.SINGLE );
    assertEquals( SWT.NONE, tree.getSortDirection() );

    tree.setSortDirection( SWT.UP );
    assertEquals( SWT.UP, tree.getSortDirection() );

    tree.setSortDirection( SWT.DOWN );
    assertEquals( SWT.DOWN, tree.getSortDirection() );
  }

  @Test
  public void testShowSelection() {
    tree = new Tree( shell, SWT.SINGLE );
    TreeItem item;
    tree.showSelection();
    item = new TreeItem( tree, 0 );
    tree.setSelection( new TreeItem[] { item } );
    tree.showSelection();

    // TODO [fappel]: What does this test? I wonder since there is no assert statement.
  }

  @Test
  public void testResizeListener() {
    tree = new Tree( shell, SWT.VIRTUAL | SWT.BORDER );
    final List<ControlEvent> log = new ArrayList<ControlEvent>();
    tree.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent event ) {
        log.add( event );
      }
    } );
    tree.setSize( 100, 160 );

    assertEquals( 1, log.size() );
  }

  @Test
  public void testUpdateScrollBarOnColumnChange() {
    tree.setSize( 20, 20 );
    assertFalse( tree.hasHScrollBar() );

    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    column.setWidth( 25 );
    assertTrue( tree.hasHScrollBar() );

    column.pack();
    assertFalse( tree.hasHScrollBar() );

    column.setWidth( 25 );
    assertTrue( tree.hasHScrollBar() );

    column.dispose();
    assertFalse( tree.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnItemsChange() {
    tree.setSize( 20, 20 );
    assertFalse( tree.hasVScrollBar() );

    for( int i = 0; i < 20; i++ ) {
      new TreeItem( tree, SWT.NONE );
    }
    assertTrue( tree.hasVScrollBar() );

    tree.removeAll();
    assertFalse( tree.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnItemExpand() {
    tree.setSize( 50, tree.getItemHeight() + 4 );
    assertFalse( tree.hasVScrollBar() );

    TreeItem item = new TreeItem( tree, SWT.NONE );
    new TreeItem( item, SWT.NONE );
    assertFalse( tree.hasVScrollBar() );

    item.setExpanded( true );
    assertTrue( tree.hasVScrollBar() );

    item.setExpanded( false );
    assertFalse( tree.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnResize() {
    tree.setSize( 20, 20 );
    TreeColumn column = new TreeColumn( tree, SWT.LEFT );

    column.setWidth( 25 );
    assertTrue( tree.hasHScrollBar() );

    tree.setSize( 30, 30 );
    assertFalse( tree.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnItemWidthChange() throws IOException {
    tree.setSize( 60, 60 );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertFalse( tree.hasHScrollBar() );

    item.setText( "Very long long long long long long long long text" );
    assertTrue( tree.hasHScrollBar() );

    item.setText( "" );
    assertFalse( tree.hasHScrollBar() );

    Image image = createImage( display, Fixture.IMAGE_100x50 );
    item.setImage( image );
    assertTrue( tree.hasHScrollBar() );

    item.setImage( ( Image )null );
    assertFalse( tree.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnClearAll() {
    tree.setSize( 100, 100 );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "Very long long long long long long long long text" );

    tree.clearAll( true );

    assertFalse( tree.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnHeaderVisibleChange() {
    int itemCount = 5;
    for( int i = 0; i < itemCount ; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      item.setText( "Item" );
    }
    int itemHeight = tree.getItemHeight();

    tree.setSize( 100, itemCount * itemHeight + 4 );
    assertFalse( tree.hasVScrollBar() );

    tree.setHeaderVisible( true );
    assertTrue( tree.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarOnVirtualItemCountChange() {
    tree = new Tree( shell, SWT.VIRTUAL );
    int itemCount = 5;
    int itemHeight = tree.getItemHeight();
    tree.setSize( 100, itemCount * itemHeight + 4 );

    tree.setItemCount( itemCount );
    assertFalse( tree.hasVScrollBar() );

    tree.setItemCount( itemCount * 2 );
    assertTrue( tree.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarItemWidthChangeWithColumn() throws IOException {
    tree.setSize( 20, 20 );
    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    column.setWidth( 10 );

    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertFalse( tree.hasHScrollBar() );

    item.setText( "Very long long long long long long long long text" );
    assertFalse( tree.hasHScrollBar() );

    Image image = createImage( display, Fixture.IMAGE_100x50 );
    item.setImage( image );
    assertFalse( tree.hasHScrollBar() );
  }

  @Test
  public void testUpdateScrollBarWithInterDependencyHFirst() {
    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    column.setWidth( 20 );
    new TreeItem( tree, SWT.NONE );
    int itemHeight = tree.getItemHeight();

    tree.setSize( 30, itemHeight + 4 );
    assertFalse( tree.needsVScrollBar() );
    assertFalse( tree.needsHScrollBar() );
    assertFalse( tree.hasHScrollBar() );
    assertFalse( tree.hasVScrollBar() );

    column.setWidth( 40 );
    assertTrue( tree.hasHScrollBar() );
    assertTrue( tree.hasVScrollBar() );
  }

  @Test
  public void testUpdateScrollBarWithInterDependencyVFirst() {
    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    column.setWidth( 26 );

    tree.setSize( 30, 30 );
    assertFalse( tree.hasHScrollBar() );
    assertFalse( tree.hasVScrollBar() );

    for( int i = 0; i < 10; i++ ) {
      new TreeItem( tree, SWT.NONE );
    }
    assertTrue( tree.hasHScrollBar() );
    assertTrue( tree.hasVScrollBar() );
  }

  @Test
  public void testComputeSizeWithColumns() {
    Point expected = new Point( 74, 74 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    for( int i = 0; i < 10; i++ ) {
      new TreeItem( tree, SWT.NONE ).setText( "Item " + i );
    }
    new TreeItem( tree, SWT.NONE ).setText( "Long long item 100" );
    expected = new Point( 145, 307 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    tree = new Tree( shell, SWT.BORDER );
    for( int i = 0; i < 10; i++ ) {
      new TreeItem( tree, SWT.NONE ).setText( "Item " + i );
    }
    new TreeItem( tree, SWT.NONE ).setText( "Long long item 100" );
    expected = new Point( 147, 309 );
    assertEquals( 1, tree.getBorderWidth() );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    tree.setHeaderVisible( true );
    assertEquals( 31, tree.getHeaderHeight() );
    expected = new Point( 147, 340 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    TreeColumn col1 = new TreeColumn( tree, SWT.NONE );
    col1.setText( "Col 1" );
    TreeColumn col2 = new TreeColumn( tree, SWT.NONE );
    col2.setText( "Column 2" );
    TreeColumn col3 = new TreeColumn( tree, SWT.NONE );
    col3.setText( "Wider Column" );
    expected = new Point( 76, 340 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    col1.pack();
    col2.pack();
    col3.pack();
    expected = new Point( 330, 340 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    col1.setWidth( 10 );
    col2.setWidth( 10 );
    assertEquals( 97, col3.getWidth() );
    expected = new Point( 129, 340 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    tree = new Tree( shell, SWT.CHECK );
    for( int i = 0; i < 10; i++ ) {
      new TreeItem( tree, SWT.NONE ).setText( "Item " + i );
    }
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "Long long item 100" );
    TreeItem subitem = new TreeItem( item, SWT.NONE );
    subitem.setText( "Subitem 1" );
    expected = new Point( 168, 307 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    item.setExpanded( true );
    expected = new Point( 168, 334 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    expected = new Point( 310, 310 );
    assertEquals( expected, tree.computeSize( 300, 300 ) );
  }

  @Test
  public void testComputeSizeWithIndention() {
    Point expected = new Point( 74, 74 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    TreeItem item2 = new TreeItem( item1, SWT.NONE );
    TreeItem item3 = new TreeItem( item2, SWT.NONE );
    TreeItem item4 = new TreeItem( item3, SWT.NONE );
    item1.setText( "Item 1" );
    item2.setText( "Item 2" );
    item3.setText( "Item 3" );
    item4.setText( "Item 4" );
    item1.setExpanded( true );
    item2.setExpanded( true );
    item3.setExpanded( true );
    expected = new Point( 112, 118 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testImageCutOff() throws IOException {
    tree.setSize( 200, 200 );
    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    column.setWidth( 20 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setImage( createImage( display, Fixture.IMAGE_100x50 ) );

    assertTrue( tree.getItemImageSize( 0 ).x <= 20 );
  }

  @Test
  public void testImageCutOffMultiColumn() throws IOException {
    tree.setSize( 500, 500 );
    TreeColumn column1 = new TreeColumn( tree, SWT.LEFT );
    column1.setWidth( 200 );
    TreeColumn column2 = new TreeColumn( tree, SWT.LEFT );
    column2.setWidth( 200 );

    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setImage( 0, createImage( display, Fixture.IMAGE_100x50 ) );
    item1.setImage( 1, createImage( display, Fixture.IMAGE_100x50 ) );
    assertEquals( 100, tree.getItemImageSize( 0 ).x );
    assertEquals( 100, tree.getItemImageSize( 1 ).x );

    column2.setWidth( 50 );
    assertEquals( 100, tree.getItemImageSize( 0 ).x );
    assertTrue( tree.getItemImageSize( 1 ).x <= 50 );
  }

  @Test
  public void testImageCutOffAndRestore() throws IOException {
    tree.setSize( 200, 200 );
    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setImage( createImage( display, Fixture.IMAGE_100x50) );

    column.setWidth( 200 );
    assertEquals( 100, tree.getItemImageSize( 0 ).x );

    column.setWidth( 20 );
    assertTrue( tree.getItemImageSize( 0 ).x <= 20 );

    column.setWidth( 200 );
    assertEquals( 100, tree.getItemImageSize( 0 ).x );
  }

  @Test
  public void testHideColumn() {
    tree.setSize( 200, 200 );
    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    column.setWidth( 0 );

    assertEquals(  0, tree.getItemImageSize( 0 ).x );
    assertEquals(  0, tree.getTextWidth( 0 ) );
  }

  @Test
  public void testHideColumnWidthImage() throws IOException {
    tree.setSize( 200, 200 );
    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setImage( createImage( display, Fixture.IMAGE1 ) );
    column.setWidth( 0 );

    assertEquals( 0, tree.getItemImageSize( 0 ).x );
    assertEquals( 0, tree.getTextWidth( 0 ) );
  }

  //////////
  // VIRTUAL

  @Test
  public void testVirtualGetItemOutOfBounds() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 10 );

    try {
      tree.getItem( 10 );
      fail();
    } catch( IllegalArgumentException ex ) {
      // expected
    }
  }

  @Test
  public void testVirtualInitalSetDataEvents() {
    tree = new Tree( shell, SWT.VIRTUAL );
    final List<Widget> log = new ArrayList<Widget>();
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        log.add( event.item );
      }
    } );
    tree.setItemCount( 20 );

    assertEquals( 0, log.size() );
    // TODO [tb] : doesn't work if called before setItemCount. Use fakeRedraw?
    tree.setSize( 100, 160 );
    assertTrue( log.contains( tree.getItem( 0 ) ) );
    assertTrue( log.contains( tree.getItem( 1 ) ) );
    assertFalse( log.contains( tree.getItem( 19 ) ) );
  }

  @Test
  public void testSetDataEvents() {
    tree = new Tree( shell, SWT.VIRTUAL | SWT.BORDER );
    tree.addListener( SWT.SetData, createSetDataListener() );
    tree.setItemCount( 20 );

    TreeItem item = tree.getItem( 3 );
    assertEquals( "node 3", item.getText() );
    assertEquals( 10, item.getItemCount() );
  }

  @Test
  public void testVirtualNoSetDataEventForCollapsedItems() {
    tree = new Tree( shell, SWT.VIRTUAL );
    final List<Event> log = new ArrayList<Event>();
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        log.add( event );
      }
    } );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    item.setItemCount( 10 );
    tree.setSize( 100, 160 );

    assertFalse( item.getItems()[ 0 ].isCached() );
    assertEquals( 1, log.size() );
  }

  @Test
  public void testVirtualItemIsResolved() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 10 );

    TreeItem item = tree.getItem( 9 );

    assertNotNull( item );
    assertFalse( item.isDisposed() );
  }

  @Test
  public void testVirtualWithSmallerItemsCount() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 10 );
    TreeItem item3 = tree.getItem( 3 );
    TreeItem item9 = tree.getItem( 9 );

    tree.setItemCount( 5 );

    assertEquals( 5, tree.getItemCount() );
    assertFalse( item3.isDisposed() );
    assertTrue( item9.isDisposed() );
  }

  @Test
  public void testVirtualClearAllNonRecursive() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 100, 160 );
    tree.addListener( SWT.SetData, createSetDataListener() );
    tree.setItemCount( 1 );
    tree.getItem( 0 ).getText(); // force set data
    tree.getItem( 0 ).setText( "custom" );
    tree.getItem( 0 ).getItem( 0 ).setText( "custom" );

    tree.clearAll( false );

    assertEquals( "node 0", tree.getItem( 0 ).getText() );
    assertEquals( "custom", tree.getItem( 0 ).getItem( 0 ).getText() );
  }

  @Test
  public void testVirtualClearAllRecursive() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 100, 160 );
    tree.addListener( SWT.SetData, createSetDataListener() );
    tree.setItemCount( 1 );
    tree.getItem( 0 ).getText(); // force set data
    tree.getItem( 0 ).setText( "custom" );
    tree.getItem( 0 ).getItem( 0 ).setText( "custom" );

    tree.clearAll( true );

    assertEquals( "node 0", tree.getItem( 0 ).getText() );
    assertEquals( "node 0 - 0", tree.getItem( 0 ).getItem( 0 ).getText() );
  }

  @Test
  public void testVirtualClearNonRecursive() {
    tree = new Tree( shell, SWT.VIRTUAL | SWT.BORDER );
    tree.setSize( 100, 160 );
    tree.addListener( SWT.SetData, createSetDataListener() );
    tree.setItemCount( 2 );
    tree.getItem( 0 ).getText(); // force set data
    tree.getItem( 0 ).setText( "custom" );
    tree.getItem( 0 ).getItem( 0 ).setText( "custom" );
    tree.getItem( 1 ).setText( "custom" );

    tree.clear( 0, false );

    assertEquals( "node 0", tree.getItem( 0 ).getText() );
    assertEquals( "custom", tree.getItem( 0 ).getItem( 0 ).getText() );
    assertEquals( "custom", tree.getItem( 1 ).getText() );
  }

  @Test
  public void testVirtualClearRecursive() {
    tree = new Tree( shell, SWT.VIRTUAL | SWT.BORDER );
    tree.setSize( 100, 160 );
    tree.addListener( SWT.SetData, createSetDataListener() );
    tree.setItemCount( 2 );
    tree.getItem( 0 ).getText(); // force set data
    tree.getItem( 0 ).setText( "custom" );
    tree.getItem( 0 ).getItem( 0 ).setText( "custom" );
    tree.getItem( 1 ).setText( "custom" );

    tree.clear( 0, true );

    assertEquals( "node 0", tree.getItem( 0 ).getText() );
    assertEquals( "node 0 - 0", tree.getItem( 0 ).getItem( 0 ).getText() );
    assertEquals( "custom", tree.getItem( 1 ).getText() );
  }

  @Test
  public void testClear_onVirtual_redrawsTree() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    new TreeItem( tree, SWT.NONE );

    tree.clear( 0, false );

    assertTrue( display.needsRedraw( tree ) );
  }

  @Test
  public void testClearAll_onVirtual_redrawsTree() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );

    tree.clearAll( false );

    assertTrue( display.needsRedraw( tree ) );
  }

  @Test
  public void testVirtualComputeSize() {
    tree = new Tree( shell, SWT.BORDER | SWT.VIRTUAL );
    tree.setItemCount( 10 );
    tree.addListener( SWT.SetData, new Listener() {

      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        int treeIndex = item.getParent().indexOf( item );
        item.setText( "Item " + treeIndex );
      }
    } );

    // DEFAULT_WIDTH + scrollbar (16) + 2 * border (1)
    Point expected = new Point( 76, 282 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    tree.setHeaderVisible( true );
    assertEquals( 31, tree.getHeaderHeight() );
    expected = new Point( 76, 313 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    TreeColumn col1 = new TreeColumn( tree, SWT.NONE );
    col1.setText( "Col 1" );
    TreeColumn col2 = new TreeColumn( tree, SWT.NONE );
    col2.setText( "Column 2" );
    TreeColumn col3 = new TreeColumn( tree, SWT.NONE );
    col3.setText( "Wider Column" );
    expected = new Point( 76, 313 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    col1.pack();
    col2.pack();
    col3.pack();
    expected = new Point( 229, 313 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    col1.setWidth( 10 );
    col2.setWidth( 10 );
    assertEquals( 97, col3.getWidth() );
    expected = new Point( 129, 313 );
    assertEquals( expected, tree.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    expected = new Point( 312, 312 );
    assertEquals( expected, tree.computeSize( 300, 300 ) );
  }

  @Test
  public void testVirtualScrollThrowsSetData() {
    final LoggingListener log = new LoggingListener();
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );
    tree.addListener( SWT.SetData, log );
    log.clear();
    ITreeAdapter adapter = getTreeAdapter( tree );
    adapter.setTopItemIndex( 30 );

    assertSame( tree.getItem( 30 ), log.get( 0 ).item );
    assertSame( tree.getItem( 31 ), log.get( 1 ).item );
  }

  @Test
  public void testVirtualShowItemThrowsSetData() {
    final LoggingListener log = new LoggingListener();
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );
    tree.addListener( SWT.SetData, log );
    log.clear();

    tree.showItem( tree.getItem( 30 ) );
    assertTrue( log.getItems().contains( tree.getItem( 30 ) ) );
  }

  @Test
  public void testVirtualSetFlatIndexOnShowItem() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );

    TreeItem item = tree.getItem( 30 );
    tree.showItem( item );

    assertEquals( 30, item.getFlatIndex() );
  }

  @Test
  public void testVirtualSetFlatIndexOnSetTopItem() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );

    TreeItem item = tree.getItem( 30 );
    tree.setTopItem( item );

    assertEquals( 30, item.getFlatIndex() );
  }

  @Test
  public void testTopItem() {
    tree.setSize( 300, 85 );
    TreeItem[] items = new TreeItem[ 60 ];
    int counter = 0;
    for( int i = 0; i < 10; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      item.setText( "item" + i );
      items[ counter++ ] = item;
      for( int j = 0; j < 5; j++ ) {
        TreeItem subitem = new TreeItem( item, SWT.NONE );
        subitem.setText( "subitem" + i + ", " + j );
        items[ counter++ ] = subitem;
      }
    }
    assertEquals( items[ 0 ], tree.getTopItem() );

    tree.setTopItem( items[ 4 ] );
    assertEquals( items[ 4 ], tree.getTopItem() );
    assertTrue( items[ 0 ].getExpanded() );

    tree.setTopItem( items[ 20 ] );
    assertEquals( items[ 20 ], tree.getTopItem() );
    assertTrue( items[ 18 ].getExpanded() );

    tree.setTopItem( items[ 1 ] );
    assertEquals( items[ 1 ], tree.getTopItem() );

    tree.setTopItem( items[ 58 ] );
    assertEquals( items[ 57 ], tree.getTopItem() );
    assertTrue( items[ 54 ].getExpanded() );
  }

  @Test
  public void testTopItemOnResize() {
    createTreeItems( tree, 10 );
    int visibleItems = 3;
    tree.setSize( 100, visibleItems * tree.getItemHeight() );
    tree.setTopItem( tree.getItem( 5 ) );

    tree.setSize( 100, ( visibleItems + 3 ) * tree.getItemHeight() );

    assertEquals( tree.getItem( 4 ), tree.getTopItem() );
  }

  @Test
  public void testTopItemOnItemDispose() {
    createTreeItems( tree, 10 );
    int visibleItems = 3;
    tree.setSize( 100, visibleItems * tree.getItemHeight() );
    tree.setTopItem( tree.getItem( 7 ) );

    tree.getItem( 8 ).dispose();

    assertEquals( tree.getItem( 6 ), tree.getTopItem() );
  }

  @Test
  public void testTopItemOnTemporaryResize() {
    tree.setSize( 100, 100 );
    createTreeItems( tree, 10 );
    tree.setTopItem( tree.getItem( 5 ) );

    markTemporaryResize();
    tree.setSize( 1100, 1100 );

    assertEquals( tree.getItem( 5 ), tree.getTopItem() );
  }

  @Test
  public void testTopItemInResizeEvent() {
    final TreeItem[] log = new TreeItem[ 1 ];
    createTreeItems( tree, 10 );
    int visibleItems = 3;
    tree.setSize( 100, visibleItems * tree.getItemHeight() );
    tree.setTopItem( tree.getItem( 5 ) );
    tree.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent e ) {
        log[ 0 ] = tree.getTopItem();
      }
    } );

    tree.setSize( 100, ( visibleItems + 3 ) * tree.getItemHeight() );

    assertSame( tree.getItem( 4 ), log[ 0 ] );
  }

  @Test
  public void testSetTopItemTwice() {
    tree.setSize( 300, 85 );
    TreeItem[] items = new TreeItem[ 60 ];
    int counter = 0;
    for( int i = 0; i < 10; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      item.setText( "item" + i );
      items[ counter++ ] = item;
      for( int j = 0; j < 5; j++ ) {
        TreeItem subitem = new TreeItem( item, SWT.NONE );
        subitem.setText( "subitem" + i + ", " + j );
        items[ counter++ ] = subitem;
      }
    }

    tree.setTopItem( items[ 4 ] );
    tree.setTopItem( items[ 20 ] );

    assertEquals( items[ 20 ], tree.getTopItem() );
    assertTrue( items[ 18 ].getExpanded() );
  }

  @Test
  public void testScrollBars() {
    assertNotNull( tree.getHorizontalBar() );
    assertNotNull( tree.getVerticalBar() );

    tree = new Tree( shell, SWT.NO_SCROLL );
    assertNull( tree.getHorizontalBar() );
    assertNull( tree.getVerticalBar() );
  }

  @Test
  public void testHasScrollBar_NO_SCROLL() {
    tree = new Tree( shell, SWT.NO_SCROLL );
    tree.setSize( 200, 200 );
    assertFalse( tree.hasVScrollBar() );
    assertFalse( tree.hasHScrollBar() );

    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    column.setWidth( 220 );

    assertFalse( tree.hasVScrollBar() );
    assertFalse( tree.hasHScrollBar() );
  }

  @Test
  public void testCellWidthWithoutColumns() {
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "First item text." );
    TreeItem item2 = new TreeItem( item1, SWT.NONE );
    item2.setText( "Second item text is longer." );
    TreeItem item3 = new TreeItem( item2, SWT.NONE );
    item3.setText( "Third item text is longer than the second item text." );

    ITreeAdapter adapter = getTreeAdapter( tree );
    int cellWidth1 = adapter.getCellWidth( 0 );
    assertTrue( cellWidth1 > 0 );

    item1.setExpanded( true );
    int cellWidth2 = adapter.getCellWidth( 0 );
    assertTrue( cellWidth2 > cellWidth1 );

    item2.setExpanded( true );
    int cellWidth3 = adapter.getCellWidth( 0 );
    assertTrue( cellWidth3 > cellWidth2 );
  }

  @Test
  public void testGetItemByPoint() {
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    tree.setSize( 100, 100 );

    TreeItem result = tree.getItem( new Point( 5, 5 ) );

    assertSame( item1, result );
  }

  @Test
  public void testPreferredWidthBufferHandlingOfTreeItem() throws IOException {
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    assertEquals( 14, item1.getPreferredWidthBuffer( 0 ) );

    item1.setText( "text" );
    assertEquals( 41, item1.getPreferredWidthBuffer( 0 ) );

    item1.setText( 0, "anotherText" );
    assertEquals( 88, item1.getPreferredWidthBuffer( 0 ) );

    // unfortunately tree doesn't allow to set images with different sizes
    // therefore the size of the first image gets cached -> we only test
    // the setImage(int,image) method
    item1.setImage( 0, createImage( display, Fixture.IMAGE1 ) );
    assertEquals( 146, item1.getPreferredWidthBuffer( 0 ) );

    tree.setFont( new Font( display, "arial", 40, SWT.BOLD ) );
    assertEquals( 378, item1.getPreferredWidthBuffer( 0 ) );
  }

  @Test
  public void testChanged() {
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "text" );

    tree.changed( tree.getChildren() );

    assertFalse( item1.hasPreferredWidthBuffer( 0 ) );
  }

  @Test
  public void testIsSerializable() throws Exception {
    new TreeItem( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    Tree deserializedTree = Fixture.serializeAndDeserialize( tree );

    assertEquals( 1, deserializedTree.getItemCount() );
    assertEquals( 1, deserializedTree.getColumnCount() );
  }

  @Test
  public void testLayoutCacheIsSerializable() throws Exception {
    tree.setHeaderVisible( true );
    new TreeColumn( tree, SWT.NONE );
    int headerHeight = tree.getHeaderHeight();

    Tree deserializedTree = Fixture.serializeAndDeserialize( tree );

    assertEquals( headerHeight, deserializedTree.getHeaderHeight() );
  }

  @Test
  public void testGetPreferredCellWidth_withColumns() {
    createColumns( tree, 3 );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( 0, "short" );
    item.setText( 1, "very long text" );

    int width1 = tree.getPreferredCellWidth( item, 0, false );
    int width2 = tree.getPreferredCellWidth( item, 1, false );
    assertTrue( width2 > width1 );
  }

  @Test
  public void testGetPreferredCellWidth_withMarkup() {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "<b>foo</b>" );
    int width1 = tree.getPreferredCellWidth( item, 0, false );

    item.clearPreferredWidthBuffers( false );
    tree.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    int width2 = tree.getPreferredCellWidth( item, 0, false );

    assertTrue( width1 > width2 );
  }

  @Test
  public void testGetVisualCellWidth_withMarkup() {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "<b>foo</b>" );
    int width1 = tree.getVisualCellWidth( item, 0 );

    item.clearPreferredWidthBuffers( false );
    tree.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    int width2 = tree.getVisualCellWidth( item, 0 );

    assertTrue( width1 > width2 );
  }

  @Test
  public void testGetVisualTextWidth_withMarkup() {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "<b>foo</b>" );
    int width1 = tree.getVisualTextWidth( item, 0 );

    item.clearPreferredWidthBuffers( false );
    tree.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    int width2 = tree.getVisualTextWidth( item, 0 );

    assertTrue( width1 > width2 );
  }

  @Test
  public void testSetItemCountDoesNotResolveVirtualItems() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 300, 50 );

    tree.setItemCount( 1 );

    assertFalse( tree.getItem( 0 ).isCached() );
  }

  @Test
  public void testTemporaryResizeDoesNotResolveVirtualItems() {
    final java.util.List<Event> eventLog = new LinkedList<Event>();
    shell.setSize( 100, 100 );
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 1000 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        eventLog.add( event );
      }
    } );
    redrawTree( tree );
    eventLog.clear();

    markTemporaryResize();
    tree.setSize( 1000, 1000 );

    assertEquals( 0, eventLog.size() );
  }

  @Test
  public void testVirtualMaterializeItemOnScroll() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 300, 300 );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    item.setItemCount( 50 );

    TreeItem subItem = item.getItem( 20 );
    tree.setTopItem( subItem );

    assertTrue( item.getItem( 25 ).isCached() );
  }

  @Test
  public void testVirtualMaterializeItemOnItemCountChange() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 300, 300 );
    tree.setItemCount( 100 );
    tree.setTopItem( tree.getItem( 80 ) );

    tree.setItemCount( 50 );
    redrawTree( tree );

    assertTrue( tree.getItem( 45 ).isCached() );
  }

  @Test
  public void testGetCreatedItems_DoesNotContainNullItems() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );

    assertTrue( tree.getCreatedItems().length < 10 );
  }

  @Test
  public void testSetCustomItemHeight() {
    tree.setData( RWT.CUSTOM_ITEM_HEIGHT, new Integer( 123 ) );
    assertEquals( 123, tree.getItemHeight() );
  }

  @Test
  public void testGetCustomItemHeight() {
    Integer itemHeight = new Integer( 123 );
    tree.setData( RWT.CUSTOM_ITEM_HEIGHT, itemHeight );

    Object returnedItemHeight = tree.getData( RWT.CUSTOM_ITEM_HEIGHT );

    assertEquals( itemHeight, returnedItemHeight );
  }

  @Test
  public void testResetCustomItemHeight() {
    int calculatedItemHeight = tree.getItemHeight();
    tree.setData( RWT.CUSTOM_ITEM_HEIGHT, new Integer( 123 ) );
    tree.setData( RWT.CUSTOM_ITEM_HEIGHT, null );
    assertEquals( calculatedItemHeight, tree.getItemHeight() );
  }

  @Test
  public void testDefaultCustomItemHeight() {
    assertEquals( 27, tree.getItemHeight() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetCustomItemHeightWithNegativeValue() {
    tree.setData( RWT.CUSTOM_ITEM_HEIGHT, new Integer( -1 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetCustomItemHeightWithNonIntegerValue() {
    tree.setData( RWT.CUSTOM_ITEM_HEIGHT, new Object() );
  }

  @Test
  public void testSetCellToolTipText() {
    ICellToolTipAdapter adapter = tree.getAdapter( ICellToolTipAdapter.class );

    adapter.setCellToolTipText( "foo" );

    assertEquals( "foo", adapter.getCellToolTipText() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetCellToolTipText_withToolTipMarkupEnabled_invalid() {
    tree.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    ICellToolTipAdapter adapter = tree.getAdapter( ICellToolTipAdapter.class );

    adapter.setCellToolTipText( "invalid xhtml: <<&>>" );
  }

  @Test
  public void testSetCellToolTipText_withToolTipMarkupEnabled_invalidWithDisabledValidation() {
    tree.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    tree.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );
    ICellToolTipAdapter adapter = tree.getAdapter( ICellToolTipAdapter.class );

    try {
      adapter.setCellToolTipText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testMarkupTextWithoutMarkupEnabled() {
    tree.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    try {
      item.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test( expected = IllegalArgumentException.class )
  public void testMarkupTextWithMarkupEnabled() {
    tree.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setText( "invalid xhtml: <<&>>" );
  }

  @Test
  public void testMarkupTextWithMarkupEnabled_ValidationDisabled() {
    tree.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    tree.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    try {
      item.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testDisableMarkupIsIgnored() {
    tree.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    tree.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    assertEquals( Boolean.TRUE, tree.getData( RWT.MARKUP_ENABLED ) );
  }

  @Test
  public void testSetData() {
    tree.setData( "foo", "bar" );

    assertEquals( "bar", tree.getData( "foo" ) );
  }

  // see bug 371860
  @Test
  public void testClearSetDataOrder() {
    final List<String> log = new ArrayList<String>();
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 100, 160 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        int i = item.getParent().indexOf( item );
        item.setText( "item " + i );
        log.add( "item" + i + "#SetData" );
      }
    } );
    TreeItem item0 = new TreeItem( tree, SWT.NONE );
    item0.setText( "item0" );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "item1" );

    log.add( "clear item0" );
    tree.clear( 0, false );
    log.add( "clear item1" );
    tree.clear( 1, false );
    log.add( "setItemCount" );
    tree.setItemCount( 1 );
    display.readAndDispatch();  // redraw the tree

    assertEquals( 4, log.size() );
    assertEquals( "clear item0", log.get( 0 ) );
    assertEquals( "clear item1", log.get( 1 ) );
    assertEquals( "setItemCount", log.get( 2 ) );
    assertEquals( "item0#SetData", log.get( 3 ) );
  }

  @Test
  public void testShowColumn() {
    tree.setSize( 325, 100 );
    for( int i = 0; i < 10; i++ ) {
      TreeColumn column = new TreeColumn( tree, SWT.NONE );
      column.setWidth( 50 );
    }
    ITreeAdapter adapter = getTreeAdapter( tree );
    assertEquals( 0, adapter.getScrollLeft() );
    tree.showColumn( tree.getColumn( 8 ) );
    assertEquals( 175, adapter.getScrollLeft() );
    tree.showColumn( tree.getColumn( 1 ) );
    assertEquals( 50, adapter.getScrollLeft() );
    tree.showColumn( tree.getColumn( 3 ) );
    assertEquals( 50, adapter.getScrollLeft() );

    tree.getColumn( 3 ).dispose();
    tree.setColumnOrder( new int[] { 8, 7, 0, 1, 2, 3, 6, 5, 4 } );
    tree.showColumn( tree.getColumn( 8 ) );
    assertEquals( 0, adapter.getScrollLeft() );
    tree.showColumn( tree.getColumn( 5 ) );
    assertEquals( 125, adapter.getScrollLeft() );
  }

  @Test
  public void testShowColumnWithReorderedColumns() {
    tree.setSize( 325, 100 );
    for( int i = 0; i < 9; i++ ) {
      TreeColumn column = new TreeColumn( tree, SWT.NONE );
      column.setWidth( 50 );
    }

    tree.setColumnOrder( new int[] { 8, 7, 0, 1, 2, 3, 6, 5, 4 } );
    tree.showColumn( tree.getColumn( 8 ) );

    ITreeAdapter adapter = getTreeAdapter( tree );
    assertEquals( 0, adapter.getScrollLeft() );

    tree.showColumn( tree.getColumn( 5 ) );
    assertEquals( 125, adapter.getScrollLeft() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testShowColumnWithNullArgument() {
    tree.showColumn( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testShowColumnWithDisposedColumn() {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.dispose();
    tree.showColumn( column );
  }

  @Test
  public void testShowColumnWithForeignColumn() {
    int initialLeftOffset = 123456;
    tree.setSize( 325, 100 );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setWidth( 50 );
    ITreeAdapter adapter = getTreeAdapter( tree );
    adapter.setScrollLeft( initialLeftOffset );
    Tree otherTree = new Tree( shell, SWT.NONE );
    TreeColumn otherColumn = new TreeColumn( otherTree, SWT.NONE );

    tree.showColumn( otherColumn );

    assertEquals( initialLeftOffset, adapter.getScrollLeft() );
  }

  @Test
  public void testShowFixedColumn() {
    shell.setSize( 800, 600 );
    Tree tree = createFixedColumnsTree();
    tree.setSize( 300, 100 );
    for( int i = 0; i < 10; i++ ) {
      TreeColumn column = new TreeColumn( tree, SWT.NONE );
      column.setWidth( 50 );
    }
    createTreeItems( tree, 10 );
    ITreeAdapter adapter = getTreeAdapter( tree );

    adapter.setScrollLeft( 100 );
    tree.showColumn( tree.getColumn( 0 ) );

    assertEquals( 100, adapter.getScrollLeft() );
  }

  @Test
  public void testFixedColumnsNotSetWithRowTemplate() {
    Tree tree = createFixedColumnsTree();

    tree.setData( RWT.ROW_TEMPLATE, new Template() );

    ITreeAdapter adapter = tree.getAdapter( ITreeAdapter.class );
    assertEquals( -1, adapter.getFixedColumns() );
  }

  @Test
  public void testShowColumnWithFixedColumns_ScrolledToLeft() {
    int numColumns = 4;
    int columnWidth = 100;
    Tree tree = createFixedColumnsTree();
    tree.setSize( columnWidth * ( numColumns - 1 ), 100 );
    for( int i = 0; i < numColumns; i++ ) {
      TreeColumn column = new TreeColumn( tree, SWT.NONE );
      column.setWidth( columnWidth );
    }
    ITreeAdapter adapter = getTreeAdapter( tree );
    adapter.setScrollLeft( 100 );

    tree.showColumn( tree.getColumn( 2 ) );

    assertEquals( 0, adapter.getScrollLeft() );
  }

  @Test
  public void testShowColumnWithFixedColumns_ScrolledToRight() {
    int numColumns = 4;
    int columnWidth = 100;
    Tree tree = createFixedColumnsTree();
    tree.setSize( columnWidth  * ( numColumns - 1 ), 100 );
    for( int i = 0; i < numColumns; i++ ) {
      TreeColumn column = new TreeColumn( tree, SWT.NONE );
      column.setWidth( columnWidth );
    }

    tree.showColumn( tree.getColumn( 3 ) );

    ITreeAdapter adapter = getTreeAdapter( tree );
    assertEquals( 100, adapter.getScrollLeft() );
  }

  @Test
  public void testAddTreeListener() {
    tree.addTreeListener( mock( TreeListener.class ) );

    assertTrue( tree.isListening( SWT.Expand ) );
    assertTrue( tree.isListening( SWT.Collapse ) );
  }

  @Test
  public void testRemoveTreeListener() {
    TreeListener listener = mock( TreeListener.class );
    tree.addTreeListener( listener );

    tree.removeTreeListener( listener );

    assertFalse( tree.isListening( SWT.Expand ) );
    assertFalse( tree.isListening( SWT.Collapse ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddTreeListenerWithNullArgument() {
    tree.addTreeListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveTreeListenerWithNullArgument() {
    tree.removeTreeListener( null );
  }

  @Test
  public void testAddSelectionListener() {
    tree.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( tree.isListening( SWT.Selection ) );
    assertTrue( tree.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    SelectionListener listener = mock( SelectionListener.class );
    tree.addSelectionListener( listener );

    tree.removeSelectionListener( listener );

    assertFalse( tree.isListening( SWT.Selection ) );
    assertFalse( tree.isListening( SWT.DefaultSelection ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddSelectionListenerWithNullArgument() {
    tree.addSelectionListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveSelectionListenerWithNullArgument() {
    tree.removeSelectionListener( null );
  }

  @Test
  public void testDisposeCellEditor() {
    Text cellEditor = new Text( tree, SWT.NONE );

    tree.dispose();

    assertTrue( cellEditor.isDisposed() );
  }

  @Test
  public void testGetVisibleRowCount() {
    tree = new Tree( shell, SWT.NO_SCROLL );
    createTreeItems( tree, 10 );
    int itemHeight = tree.getItemHeight();
    tree.setSize( 100, 5 * itemHeight );
    assertEquals( 5, tree.getVisibleRowCount( true ) );
    assertEquals( 5, tree.getVisibleRowCount( false ) );
  }

  @Test
  public void testGetVisibleRowCount_WithBorder() {
    tree = new Tree( shell, SWT.NO_SCROLL | SWT.BORDER );
    createTreeItems( tree, 10 );
    int itemHeight = tree.getItemHeight();
    int borderWidth = tree.getBorderWidth();
    tree.setSize( 100, 5 * itemHeight + 2 * borderWidth );
    assertEquals( 5, tree.getVisibleRowCount( true ) );
    assertEquals( 5, tree.getVisibleRowCount( false ) );
  }

  @Test
  public void testGetVisibleItemCountWithPartiallyVisibleItem() {
    tree = new Tree( shell, SWT.NO_SCROLL );
    createTreeItems( tree, 10 );
    int itemHeight = tree.getItemHeight();
    tree.setSize( 100, ( 5 * itemHeight ) + ( itemHeight / 2 ) );
    assertEquals( 6, tree.getVisibleRowCount( true ) );
    assertEquals( 5, tree.getVisibleRowCount( false ) );
  }

  @Test
  public void testClearCachedHeightsOnResize() {
    tree.setSize( 200, 200 );
    Tree spyTree = spy( tree );

    spyTree.setSize( 210, 200 );

    verify( spyTree ).clearCachedHeights();
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetPreloadedItems_invalidValue() {
    tree.setData( RWT.PRELOADED_ITEMS, "abc" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetPreloadedItems_negativeValue() {
    tree.setData( RWT.PRELOADED_ITEMS, Integer.valueOf( -1 ) );
  }

  @Test
  public void testResolvedItems_withoutPreloadedItemsSet() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 100, 100 );
    tree.setItemCount( 200 );

    getTreeAdapter( tree ).setTopItemIndex( 100 );

    assertEquals( 4, countResolvedItems( tree ) );
  }

  @Test
  public void testResolvedItems_zeroPreloadedItems() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setData( RWT.PRELOADED_ITEMS, Integer.valueOf( 0 ) );
    tree.setSize( 100, 100 );
    tree.setItemCount( 200 );

    getTreeAdapter( tree ).setTopItemIndex( 100 );

    assertEquals( 4, countResolvedItems( tree ) );
  }

  @Test
  public void testResolvedItems_10PreloadedItems() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setData( RWT.PRELOADED_ITEMS, Integer.valueOf( 10 ) );
    tree.setSize( 100, 100 );
    tree.setItemCount( 200 );

    getTreeAdapter( tree ).setTopItemIndex( 100 );

    // visible (4) + above visible area( 10 ) + below visible area (10)
    assertEquals( 24, countResolvedItems( tree ) );
  }

  @Test
  public void testResolvedItems_10PreloadedItems_withSubitems() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setData( RWT.PRELOADED_ITEMS, Integer.valueOf( 10 ) );
    tree.setSize( 100, 100 );
    tree.setItemCount( 200 );
    tree.getItem( 105 ).setItemCount( 50 );
    tree.getItem( 105 ).setExpanded( true );
    // 15 items has been resolved at this point
    // visible (4) + below visible area (10) + item( 105 )

    getTreeAdapter( tree ).setTopItemIndex( 100 );

    // already resolved (15) + visible (4) + above visible area( 10 ) + below visible area (9)
    // item(105) was already resolved
    assertEquals( 38, countResolvedItems( tree ) );
  }

  @Test
  public void testResolvedItems_300PreloadedItems() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setData( RWT.PRELOADED_ITEMS, Integer.valueOf( 300 ) );
    tree.setSize( 100, 100 );
    tree.setItemCount( 200 );

    getTreeAdapter( tree ).setTopItemIndex( 100 );
    redrawTree( tree );

    // all items preloaded
    assertEquals( 200, countResolvedItems( tree ) );
  }

  /////////
  // Helper

  private Tree createFixedColumnsTree() {
    Tree result = new Tree( shell, SWT.NONE );
    result.setData( RWT.FIXED_COLUMNS, new Integer( 2 ) );
    return result;
  }

  private static void createColumns( Tree tree, int count ) {
    for( int i = 0; i < count; i++ ) {
      new TreeColumn( tree, SWT.NONE );
    }
  }

  private static TreeItem[] createTreeItems( Tree tree, int number ) {
    TreeItem[] result = new TreeItem[ number ];
    for( int i = 0; i < number; i++ ) {
      result[ i ] = new TreeItem( tree, 0 );
      result[ i ].setText( "item" + i );
    }
    return result;
  }

  private static Listener createSetDataListener() {
    return new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        Tree parent = item.getParent();
        TreeItem parentItem = item.getParentItem();
        String text;
        if( parentItem == null ) {
          text = "node " + parent.indexOf( item );
        } else {
          text = parentItem.getText() + " - " + parentItem.indexOf( item );
        }
        item.setText( text );
        item.setItemCount( 10 );
      }
    };
  }

  private static boolean contains( TreeItem[] items, TreeItem item ) {
    boolean result = false;
    for( int i = 0; !result && i < items.length; i++ ) {
      if( item == items[ i ] ) {
        result = true;
      }
    }
    return result;
  }

  private static int countResolvedItems( Tree tree ) {
    return countResolvedItems( getTreeAdapter( tree ), tree.getItems() );
  }

  private static int countResolvedItems( ITreeAdapter treeAdapter, TreeItem[] items ) {
    int result = 0;
    for( TreeItem item : items ) {
      if( treeAdapter.isCached( item ) ) {
        result++;
      }
      if( item.getExpanded() ) {
        result += countResolvedItems( treeAdapter, item.getItems() );
      }
    }
    return result;
  }

  private static void redrawTree( Tree tree ) {
    getTreeAdapter( tree ).checkData();
  }

  private static ITreeAdapter getTreeAdapter( Tree tree ) {
    return tree.getAdapter( ITreeAdapter.class );
  }

  private void markTemporaryResize() {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    String key = "org.eclipse.rap.rwt.internal.textsize.TextSizeRecalculation#temporaryResize";
    serviceStore.setAttribute( key, Boolean.TRUE );
  }
}
