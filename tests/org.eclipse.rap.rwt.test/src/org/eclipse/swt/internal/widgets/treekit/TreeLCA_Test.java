/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treekit;

import java.io.IOException;
import java.util.*;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.treekit.TreeLCA.ItemMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;

public class TreeLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private TreeLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new TreeLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetItemMetricsImageWidth() {
    Image image1 = Graphics.getImage( Fixture.IMAGE_100x50 );
    Image image2 = Graphics.getImage( Fixture.IMAGE_50x100 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 200 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "item1" );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    item2.setText( "item2" );
    TreeItem item3 = new TreeItem( tree, SWT.NONE );
    item3.setText( "item3" );
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].imageWidth );
    item2.setImage( image2 );
    item1.setImage( image1 );
    metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 50, metrics[ 0 ].imageWidth );
    item2.setImage( (Image) null );
    item1.setImage( (Image) null );
    metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].imageWidth );
  }

  public void testGetItemMetricsImageLeft() {
    Image image1 = Graphics.getImage( Fixture.IMAGE_100x50 );
    Image image2 = Graphics.getImage( Fixture.IMAGE_50x100 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setText( "column1" );
    column1.setWidth( 200 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 200 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "item1" );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    item2.setText( "item2" );
    TreeItem item3 = new TreeItem( tree, SWT.NONE );
    item3.setText( "item3" );
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].imageLeft );
    assertEquals( 203, metrics[ 1 ].imageLeft );
    item2.setImage( image2 );
    item1.setImage( 1, image1 );
    metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].imageLeft );
    assertEquals( 203, metrics[ 1 ].imageLeft );
  }

  public void testGetItemMetricsCellLeft() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setText( "column1" );
    column1.setWidth( 210 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 200 );
    TreeColumn column3 = new TreeColumn( tree, SWT.NONE );
    column3.setText( "column2" );
    column3.setWidth( 200 );
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].left );
    assertEquals( 210, metrics[ 1 ].left );
    assertEquals( 410, metrics[ 2 ].left );
  }

  public void testGetItemMetricsCellWidth() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setText( "column1" );
    column1.setWidth( 210 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 200 );
    TreeColumn column3 = new TreeColumn( tree, SWT.NONE );
    column3.setText( "column2" );
    column3.setWidth( 220 );
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 210, metrics[ 0 ].width );
    assertEquals( 200, metrics[ 1 ].width );
    assertEquals( 220, metrics[ 2 ].width );
  }

  public void testGetItemMetricsTextLeftWithImage() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 200 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 200 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( 1, "item12" );
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 203, metrics[ 1 ].textLeft );
    item1.setImage( 1, image );
    metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 306, metrics[ 1 ].textLeft );
  }

  public void testGetItemMetricsTextLeftWithCheckbox() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    Tree tree = new Tree( shell, SWT.CHECK );
    tree.setHeaderVisible( true );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 200 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "item" );
    item1.setImage( image );
    int expected = 123;
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( expected, metrics[ 0 ].textLeft );
  }

  public void testGetItemMetricsTextWidthWithCheckbox() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    Tree tree = new Tree( shell, SWT.CHECK );
    tree.setHeaderVisible( true );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 200 );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "item" );
    item1.setImage( image );
    int expected = 72;
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( expected, metrics[ 0 ].textWidth );
  }

  public void testPreserveValues() {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    TreeColumn child1 = new TreeColumn( tree, SWT.NONE, 0 );
    child1.setText( "child1" );
    TreeColumn child2 = new TreeColumn( tree, SWT.NONE, 1 );
    child2.setText( "child2" );
    // item metrics
    child1.setWidth( 150 );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    // item height
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    Fixture.clearPreserved();
    // control: enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    tree.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    tree.setEnabled( true );
    // visible
    tree.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    tree.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( tree );
    MenuItem menuItem = new MenuItem( menu, SWT.NONE );
    menuItem.setText( "1 Item" );
    tree.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    tree.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    // z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    tree.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    tree.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    tree.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( null, tree.getToolTipText() );
    Fixture.clearPreserved();
    tree.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( "some text", tree.getToolTipText() );
    Fixture.clearPreserved();
    // activate_listeners Focus_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    tree.addFocusListener( new FocusAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( tree, new ActivateAdapter() {} );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testSelectionEvent() {
    java.util.List<SelectionEvent> events = new LinkedList<SelectionEvent>();
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new LoggingSelectionListener( events ) );
    String treeId = WidgetUtil.getId( tree );
    String treeItemId = WidgetUtil.getId( treeItem );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, treeId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".item", treeItemId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, events.size() );
    SelectionEvent event = events.get( 0 );
    assertEquals( tree, event.getSource() );
    assertEquals( treeItem, event.item );
    assertEquals( true, event.doit );
    // ensure same behaviour as SWT: bounds are undefined in tree selection
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
  }

  public void testVirtualSelectionEvent() {
    java.util.List<SelectionEvent> events = new LinkedList<SelectionEvent>();
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new LoggingSelectionListener( events ) );
    String treeId = WidgetUtil.getId( tree );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, treeId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".item", treeId + "#" + 50 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, events.size() );
    SelectionEvent event = events.get( 0 );
    assertEquals( tree, event.getSource() );
    assertSame( tree.getItem( 50 ), event.item );
  }

  public void testVirtualSelectionEventWithSubitem() {
    java.util.List<SelectionEvent> events = new LinkedList<SelectionEvent>();
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    // Important: parent item must be materialized
    item.setText( "item 1" );
    String itemId = WidgetUtil.getId( item );
    item.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new LoggingSelectionListener( events ) );
    String treeId = WidgetUtil.getId( tree );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, treeId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".item", itemId + "#" + 50 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, events.size() );
    SelectionEvent event = events.get( 0 );
    assertEquals( tree, event.getSource() );
    assertSame( item.getItem( 50 ), event.item );
  }

  public void testDefaultSelectionEvent() {
    java.util.List<SelectionEvent> events = new LinkedList<SelectionEvent>();
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new LoggingSelectionListener( events ) );
    String treeId = WidgetUtil.getId( tree );
    String treeItemId = WidgetUtil.getId( treeItem );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, treeId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED + ".item", treeItemId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, events.size() );
    SelectionEvent event = events.get( 0 );
    assertEquals( tree, event.getSource() );
    assertEquals( treeItem, event.item );
    assertEquals( true, event.doit );
    // ensure same behaviour as SWT: bounds are undefined in tree selection
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
  }

  public void testDefaultSelectionEventUntyped() {
    final java.util.List<Event> events = new LinkedList<Event>();
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addListener( SWT.DefaultSelection, new Listener() {
      public void handleEvent( Event event ) {
        events.add( event );
      }
    } );
    String treeId = WidgetUtil.getId( tree );
    String treeItemId = WidgetUtil.getId( treeItem );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, treeId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED + ".item", treeItemId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, events.size() );
    Event event = events.get( 0 );
    assertEquals( treeItem, event.item );
    assertEquals( true, event.doit );
    // ensure same behaviour as SWT: bounds are undefined in tree selection
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
  }

  public void testInvalidScrollValues() {
    Tree tree = new Tree( shell, SWT.NONE );
    String treeId = WidgetUtil.getId( tree );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( treeId + ".scrollLeft", "undefined" );
    Fixture.fakeRequestParam( treeId + ".scrollTop", "80" );
    Fixture.readDataAndProcessAction( display );
    ITreeAdapter adapter = tree.getAdapter( ITreeAdapter.class );
    //assertEquals( 80, adapter.getScrollTop() );
    assertEquals( 0, adapter.getScrollLeft() );
  }

  public void testScrollbarsSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List<SelectionEvent> events = new ArrayList<SelectionEvent>();
    Tree tree = new Tree( shell, SWT.NONE );
    SelectionListener listener = new LoggingSelectionListener( events );
    tree.getHorizontalBar().addSelectionListener( listener );
    Fixture.fakeNewRequest();
    String tableId = WidgetUtil.getId( tree );
    Fixture.fakeRequestParam( tableId + ".scrollLeft", "10" );
    Fixture.readDataAndProcessAction( tree );
    assertEquals( 1, events.size() );
    assertEquals( 10, tree.getHorizontalBar().getSelection() );
    events.clear();
    tree.getVerticalBar().addSelectionListener( listener );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( tableId + ".scrollLeft", "10" );
    Fixture.fakeRequestParam( tableId + ".topItemIndex", "10" );
    Fixture.readDataAndProcessAction( tree );
    assertEquals( 2, events.size() );
    assertEquals( 10 * tree.getItemHeight(), tree.getVerticalBar().getSelection());
  }

  public void testCellTooltipRequestForMissingCells() {
    Tree tree = new Tree( shell, SWT.NONE );
    createTreeItems( tree, 3 );
    final StringBuffer log = new StringBuffer();
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( final Item item, final int columnIndex ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "[" );
        buffer.append( WidgetUtil.getId( item ) );
        buffer.append( "," );
        buffer.append( columnIndex );
        buffer.append( "]" );
        log.append( buffer.toString() );
      }
    } );
    String itemId = WidgetUtil.getId( tree.getItem( 0 ) );
    processCellToolTipRequest( tree, itemId, 0 );
    String expected = "[" + itemId + ",0]";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    itemId = WidgetUtil.getId( tree.getItem( 2 ) );
    processCellToolTipRequest( tree, itemId, 0 );
    expected = "[" + itemId + ",0]";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    processCellToolTipRequest( tree, "xyz", 0 );
    assertEquals( "", log.toString() );
    processCellToolTipRequest( tree, itemId, 1 );
    assertEquals( "", log.toString() );
    createTreeColumns( tree, 2 );
    processCellToolTipRequest( tree, itemId, 1 );
    expected = "[" + itemId + ",1]";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    processCellToolTipRequest( tree, itemId, 2 );
    assertEquals( "", log.toString() );
  }

  public void testCreateVirtualItems() {
    final Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 200, 200 );
    tree.addListener( SWT.SetData, new Listener() {

      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setText( "node " + tree.indexOf( item ) );
        item.setItemCount( 10 );
      }
    } );
    tree.setItemCount( 7 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.fakeNewRequest( display );

    Fixture.executeLifeCycleFromServerThread();

    assertEquals( 7, countOccurences( "TreeItem.createItem(", Fixture.getAllMarkup() ) );
  }

  public void testVirtualReadSelection() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    String treeId = WidgetUtil.getId( tree );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( treeId + ".selection", treeId + "#" + 50 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, tree.getSelection().length );
    assertSame( tree.getItem( 50 ), tree.getSelection()[ 0 ] );
  }

  public void testVirtualReadSelectionWithSubitem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    // Important: parent item must be materialized
    item.setText( "item 1" );
    String itemId = WidgetUtil.getId( item );
    item.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    String treeId = WidgetUtil.getId( tree );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( treeId + ".selection", itemId + "#" + 50 );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 1, tree.getSelection().length );
    assertSame( item.getItem( 50 ), tree.getSelection()[ 0 ] );
  }

  private static int countOccurences( String searchString, String stringToSearch ) {
    int result = 0;
    int index = stringToSearch.indexOf( searchString );
    while( index != -1 ) {
      result++;
      index = stringToSearch.indexOf( searchString, index + 1 );
    }
    return result;
  }

  private static void createTreeColumns( Tree tree, int count ) {
    for( int i = 0; i < count; i++ ) {
      new TreeColumn( tree, SWT.NONE );
    }
  }

  private static void createTreeItems( Tree tree, int count ) {
    for( int i = 0; i < count; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      for( int j = 0; j < count; j++ ) {
        new TreeItem( item, SWT.NONE );
      }
      item.setExpanded( true );
    }
  }

  private static void processCellToolTipRequest( Tree tree, String itemId, int column ) {
    Fixture.fakeNewRequest( tree.getDisplay() );
    String treeId = WidgetUtil.getId( tree );
    Fixture.fakeRequestParam( JSConst.EVENT_CELL_TOOLTIP_REQUESTED, treeId );
    String cellString = itemId + "," + column;
    Fixture.fakeRequestParam( JSConst.EVENT_CELL_TOOLTIP_DETAILS, cellString );
    Fixture.executeLifeCycleFromServerThread();
  }

  private static class LoggingSelectionListener extends SelectionAdapter {
    private final List<SelectionEvent> events;
    private LoggingSelectionListener( List<SelectionEvent> events ) {
      this.events = events;
    }
    public void widgetSelected( SelectionEvent event ) {
      events.add( event );
    }
    public void widgetDefaultSelected( SelectionEvent event ) {
      events.add( event );
    }
  }

  public void testRenderCreate() throws IOException, JSONException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.renderInitialization( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertEquals( "rwt.widgets.Tree", operation.getType() );
    assertEquals( "tree", operation.getProperty( "appearance" ) );
    assertEquals( Integer.valueOf( 16 ), operation.getProperty( "indentionWidth" ) );
    JSONArray actual = ( JSONArray )operation.getProperty( "selectionPadding" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[3,5]", actual ) );
    assertFalse( operation.getPropertyNames().contains( "checkBoxMetrics" ) );
  }

  public void testRenderParent() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.renderInitialization( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertEquals( WidgetUtil.getId( tree.getParent() ), operation.getParent() );
  }

  public void testRenderCreateWithVirtualNoScrollMulti() throws IOException {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.NO_SCROLL | SWT.MULTI );

    lca.renderInitialization( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "VIRTUAL" ) );
    assertTrue( Arrays.asList( styles ).contains( "NO_SCROLL" ) );
    assertTrue( Arrays.asList( styles ).contains( "MULTI" ) );
  }

  public void testRenderCreateWithFullSelection() throws IOException {
    Tree tree = new Tree( shell, SWT.FULL_SELECTION );

    lca.renderInitialization( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "FULL_SELECTION" ) );
    assertFalse( operation.getPropertyNames().contains( "selectionPadding" ) );
  }

  public void testRenderCreateWithCheck() throws IOException, JSONException {
    Tree tree = new Tree( shell, SWT.CHECK );

    lca.renderInitialization( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "CHECK" ) );
    JSONArray actual = ( JSONArray )operation.getProperty( "checkBoxMetrics" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,15]", actual ) );
  }

  public void testRenderInitialItemCount() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "itemCount" ) == -1 );
  }

  public void testRenderItemCount() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    tree.setItemCount( 10 );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 10 ), message.findSetProperty( tree, "itemCount" ) );
  }

  public void testRenderItemCountUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "itemCount" ) );
  }

  public void testRenderInitialItemHeight() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "itemHeight" ) == -1 );
  }

  public void testRenderItemHeight() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    Font font = Graphics.getFont( "Arial", 26, SWT.NONE );

    tree.setFont( font );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 32 ), message.findSetProperty( tree, "itemHeight" ) );
  }

  public void testRenderItemHeightUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    Font font = Graphics.getFont( "Arial", 26, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setFont( font );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "itemHeight" ) );
  }

  public void testRenderInitialItemMetrics() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "itemMetrics" ) != -1 );
  }

  public void testRenderItemMetrics() throws IOException, JSONException {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "foo" );

    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( tree, "itemMetrics" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,0,44,0,0,3,36]", ( JSONArray )actual.get( 0 ) ) );
  }

  public void testRenderItemMetricsUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "foo" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "itemMetrics" ) );
  }

  public void testRenderInitialColumnCount() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "columnCount" ) == -1 );
  }

  public void testRenderColumnCount() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    new TreeColumn( tree, SWT.NONE );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 1 ), message.findSetProperty( tree, "columnCount" ) );
  }

  public void testRenderColumnCountUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    new TreeColumn( tree, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "columnCount" ) );
  }

  public void testRenderInitialTreeColumn() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "treeColumn" ) == -1 );
  }

  public void testRenderTreeColumn() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    tree.setColumnOrder( new int[]{ 1, 0 } );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 1 ), message.findSetProperty( tree, "treeColumn" ) );
  }

  public void testRenderTreeColumnUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setColumnOrder( new int[]{ 1, 0 } );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "treeColumn" ) );
  }

  public void testRenderInitialHeaderHeight() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "headerHeight" ) == -1 );
  }

  public void testRenderHeaderHeight() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    tree.setHeaderVisible( true );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 14 ), message.findSetProperty( tree, "headerHeight" ) );
  }

  public void testRenderHeaderHeightUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "headerHeight" ) );
  }

  public void testRenderInitialHeaderVisible() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "headerVisible" ) == -1 );
  }

  public void testRenderHeaderVisible() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    tree.setHeaderVisible( true );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( tree, "headerVisible" ) );
  }

  public void testRenderHeaderVisibleUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "headerVisible" ) );
  }

  public void testRenderInitialLinesVisible() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "linesVisible" ) == -1 );
  }

  public void testRenderLinesVisible() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    tree.setLinesVisible( true );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( tree, "linesVisible" ) );
  }

  public void testRenderLinesVisibleUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setLinesVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "linesVisible" ) );
  }

  public void testRenderInitialTopItemIndex() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "topItemIndex" ) == -1 );
  }

  public void testRenderTopItemIndex() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    tree.setTopItem( item );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 2 ), message.findSetProperty( tree, "topItemIndex" ) );
  }

  public void testRenderTopItemIndexUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setTopItem( item );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "topItemIndex" ) );
  }

  public void testRenderInitialScrollLeft() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "scrollLeft" ) == -1 );
  }

  public void testRenderScrollLeft() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    setScrollLeft( tree, 10 );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 10 ), message.findSetProperty( tree, "scrollLeft" ) );
  }

  public void testRenderScrollLeftUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    setScrollLeft( tree, 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "scrollLeft" ) );
  }

  public void testRenderAddScrollBarsSelectionListener_Horizontal() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.getHorizontalBar().addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( tree, "scrollBarsSelection" ) );
  }

  public void testRenderRemoveScrollBarsSelectionListener_Horizontal() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    SelectionListener listener = new SelectionAdapter() { };
    tree.getHorizontalBar().addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.getHorizontalBar().removeSelectionListener( listener );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( tree, "scrollBarsSelection" ) );
  }

  public void testRenderScrollBarsSelectionListenerUnchanged_Horizontal() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.getHorizontalBar().addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( tree, "scrollBarsSelection" ) );
  }

  public void testRenderAddScrollBarsSelectionListener_Vertical() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.getVerticalBar().addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( tree, "scrollBarsSelection" ) );
  }

  public void testRenderRemoveScrollBarsSelectionListener_Vertical() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    SelectionListener listener = new SelectionAdapter() { };
    tree.getVerticalBar().addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.getVerticalBar().removeSelectionListener( listener );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( tree, "scrollBarsSelection" ) );
  }

  public void testRenderScrollBarsSelectionListenerUnchanged_Vertical() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.getVerticalBar().addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( tree, "scrollBarsSelection" ) );
  }

  public void testRenderInitialScrollBarsVisible() throws IOException {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "scrollBarsVisible" ) == -1 );
  }

  public void testRenderScrollBarsVisible_Horizontal() throws IOException, JSONException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    column.setWidth( 25 );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( tree, "scrollBarsVisible" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ true, false ]", actual ) );
  }

  public void testRenderScrollBarsVisible_Vertical() throws IOException, JSONException {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    new TreeColumn( tree, SWT.NONE );

    tree.setHeaderVisible( true );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( tree, "scrollBarsVisible" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ false, true ]", actual ) );
  }

  public void testRenderScrollBarsVisibleUnchanged() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    column.setWidth( 25 );
    tree.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "scrollBarsVisible" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( tree, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() { };
    tree.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.removeSelectionListener( listener );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( tree, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( tree, "selection" ) );
  }

  public void testRenderInitialEnableCellToolTip() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "enableCellToolTip" ) == -1 );
  }

  public void testRenderEnableCellToolTip() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );

    tree.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( tree, "enableCellToolTip" ) );
  }

  public void testRenderEnableCellToolTipUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "enableCellToolTip" ) );
  }

  public void testRenderCellToolTipText() {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    createTreeItems( tree, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( Item item, int columnIndex ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "[" );
        buffer.append( WidgetUtil.getId( item ) );
        buffer.append( "," );
        buffer.append( columnIndex );
        buffer.append( "]" );
        adapter.setCellToolTipText( buffer.toString() );
      }
    } );

    String itemId = WidgetUtil.getId( tree.getItem( 2 ) );
    processCellToolTipRequest( tree, itemId, 0 );

    Message message = Fixture.getProtocolMessage();
    String expected = "[" + itemId + ",0]";
    assertEquals( expected, message.findSetProperty( tree, "cellToolTipText" ) );
  }

  public void testRenderCellToolTipTextNull() {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    createTreeItems( tree, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( Item item, int columnIndex ) {
        adapter.setCellToolTipText( null );
      }
    } );

    String itemId = WidgetUtil.getId( tree.getItem( 2 ) );
    processCellToolTipRequest( tree, itemId, 0 );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "cellToolTipText" ) );
  }

  private static void setScrollLeft( Tree tree, int scrollLeft ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    treeAdapter.setScrollLeft( scrollLeft);
  }

  private static ITreeAdapter getTreeAdapter( Tree tree ) {
    Object adapter = tree.getAdapter( ITreeAdapter.class );
    return ( ITreeAdapter )adapter;
  }
}
