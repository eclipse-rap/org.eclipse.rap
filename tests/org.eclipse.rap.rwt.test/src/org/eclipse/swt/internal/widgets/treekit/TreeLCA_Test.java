/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.internal.widgets.treekit.TreeLCA.ItemMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.json.JSONArray;
import org.json.JSONException;

@SuppressWarnings("deprecation")
public class TreeLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private Tree tree;
  private TreeLCA lca;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    tree = new Tree( shell, SWT.NONE );
    lca = new TreeLCA();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( tree );
    ControlLCATestUtil.testFocusListener( tree );
    ControlLCATestUtil.testMouseListener( tree );
    ControlLCATestUtil.testKeyListener( tree );
    ControlLCATestUtil.testTraverseListener( tree );
    ControlLCATestUtil.testMenuDetectListener( tree );
    ControlLCATestUtil.testHelpListener( tree );
  }

  public void testGetItemMetricsImageWidth() {
    Image image1 = Graphics.getImage( Fixture.IMAGE_100x50 );
    Image image2 = Graphics.getImage( Fixture.IMAGE_50x100 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
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
    assertEquals( 206, metrics[ 1 ].imageLeft );
    item2.setImage( image2 );
    item1.setImage( 1, image1 );
    metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 0, metrics[ 0 ].imageLeft );
    assertEquals( 206, metrics[ 1 ].imageLeft );
  }

  public void testGetItemMetricsCellLeft() {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
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
    assertEquals( 206, metrics[ 1 ].textLeft );
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
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 126, metrics[ 0 ].textLeft );
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
    ItemMetrics[] metrics = TreeLCA.getItemMetrics( tree );
    assertEquals( 63, metrics[ 0 ].textWidth );
  }

  public void testPreserveValues() {
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
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( null, tree.getToolTipText() );
    Fixture.clearPreserved();
    tree.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( tree );
    assertEquals( "some text", tree.getToolTipText() );
  }

  public void testSelectionEvent() {
    java.util.List<SelectionEvent> events = new LinkedList<SelectionEvent>();
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new LoggingSelectionListener( events ) );

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( ClientMessageConst.EVENT_PARAM_ITEM, getId( treeItem ) );
    Fixture.fakeNotifyOperation( getId( tree ),
                                 ClientMessageConst.EVENT_SELECTION,
                                 properties );
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

    Fixture.fakeNewRequest( display );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( ClientMessageConst.EVENT_PARAM_ITEM, getId( tree ) + "#" + 50 );
    Fixture.fakeNotifyOperation( getId( tree ),
                                 ClientMessageConst.EVENT_SELECTION,
                                 properties );
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
    item.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new LoggingSelectionListener( events ) );

    Fixture.fakeNewRequest( display );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( ClientMessageConst.EVENT_PARAM_ITEM, getId( item ) + "#" + 50 );
    Fixture.fakeNotifyOperation( getId( tree ),
                                 ClientMessageConst.EVENT_SELECTION,
                                 properties );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, events.size() );
    SelectionEvent event = events.get( 0 );
    assertEquals( tree, event.getSource() );
    assertSame( item.getItem( 50 ), event.item );
  }

  public void testDefaultSelectionEvent() {
    List<SelectionEvent> events = new LinkedList<SelectionEvent>();
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new LoggingSelectionListener( events ) );

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( ClientMessageConst.EVENT_PARAM_ITEM, getId( treeItem ) );
    Fixture.fakeNotifyOperation( getId( tree ),
                                 ClientMessageConst.EVENT_DEFAULT_SELECTION,
                                 properties );
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
    final List<Event> events = new LinkedList<Event>();
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addListener( SWT.DefaultSelection, new Listener() {
      public void handleEvent( Event event ) {
        events.add( event );
      }
    } );

    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( ClientMessageConst.EVENT_PARAM_ITEM, getId( treeItem ) );
    Fixture.fakeNotifyOperation( getId( tree ),
                                 ClientMessageConst.EVENT_DEFAULT_SELECTION,
                                 properties );
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
    Fixture.fakeSetParameter( getId( tree ), "scrollLeft", "undefined" );
    fakeSetTopItemIndex( tree, 80 );
    Fixture.readDataAndProcessAction( tree );

    ITreeAdapter adapter = tree.getAdapter( ITreeAdapter.class );
    assertEquals( 0, adapter.getScrollLeft() );
    assertEquals( 0, adapter.getTopItemIndex() );
  }

  public void testScrollbarsSelectionEvent() {
    List<SelectionEvent> events = new ArrayList<SelectionEvent>();
    SelectionListener listener = new LoggingSelectionListener( events );
    ScrollBar hScroll = tree.getHorizontalBar();
    hScroll.addSelectionListener( listener );

    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( tree ), "scrollLeft", Integer.valueOf( 10 ) );
    Fixture.fakeNotifyOperation( getId( hScroll ), "Selection", null );
    Fixture.readDataAndProcessAction( tree );

    assertEquals( 1, events.size() );
    assertEquals( 10, hScroll.getSelection() );

    events.clear();
    ScrollBar vScroll = tree.getVerticalBar();
    vScroll.addSelectionListener( listener );

    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( tree ), "topItemIndex", Integer.valueOf( 10 ) );
    Fixture.fakeNotifyOperation( getId( vScroll ), "Selection", null );
    Fixture.readDataAndProcessAction( tree );

    assertEquals( 1, events.size() );
    assertEquals( 10 * tree.getItemHeight(), vScroll.getSelection());
  }

  public void testCellTooltipRequestForMissingCells() {
    createTreeItems( tree, 3 );
    final StringBuilder log = new StringBuilder();
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( Item item, int columnIndex ) {
        StringBuilder buffer = new StringBuilder();
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

    assertEquals( "[" + itemId + ",0]", log.toString() );

    log.setLength( 0 );
    itemId = WidgetUtil.getId( tree.getItem( 2 ) );

    processCellToolTipRequest( tree, itemId, 0 );

    assertEquals( "[" + itemId + ",0]", log.toString() );

    log.setLength( 0 );

    processCellToolTipRequest( tree, "xyz", 0 );

    assertEquals( "", log.toString() );

    processCellToolTipRequest( tree, itemId, 1 );

    assertEquals( "", log.toString() );

    createTreeColumns( tree, 2 );

    processCellToolTipRequest( tree, itemId, 1 );

    assertEquals( "[" + itemId + ",1]", log.toString() );

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

    Message message = Fixture.getProtocolMessage();
    assertEquals( 7, countCreateOperations( "rwt.widgets.GridItem", message ) );
  }

  public void testVirtualReadSelection() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );

    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( tree ), "selection", new String[]{ getId( tree ) + "#" + 50 } );
    Fixture.readDataAndProcessAction( tree );

    assertEquals( 1, tree.getSelection().length );
    assertSame( tree.getItem( 50 ), tree.getSelection()[ 0 ] );
  }

  public void testVirtualReadSelectionWithSubitem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    // Important: parent item must be materialized
    item.setText( "item 1" );
    item.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );

    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( tree ), "selection", new String[]{ getId( item ) + "#" + 50 } );
    Fixture.readDataAndProcessAction( tree );

    assertEquals( 1, tree.getSelection().length );
    assertSame( item.getItem( 50 ), tree.getSelection()[ 0 ] );
  }

  public void testReadSelectionItem() {
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );

    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( tree ), "selection", new String[]{ getId( item1 ) } );
    Fixture.readDataAndProcessAction( tree );

    TreeItem[] selectedItems = tree.getSelection();
    assertEquals( 1, selectedItems.length );
  }

  public void testReadSelectionDisposedItem() {
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    item1.dispose();

    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( tree ), "selection", new String[]{ getId( item1 ) } );
    Fixture.executeLifeCycleFromServerThread();

    TreeItem[] selectedItems = tree.getSelection();
    assertEquals( 0, selectedItems.length );
  }

  private static int countCreateOperations( String type, Message message ) {
    int result = 0;
    int operations = message.getOperationCount();
    for( int i = 0; i < operations; i++ ) {
      Operation operation = message.getOperation( i );
      if( operation instanceof CreateOperation ) {
        if( type.equals( ( ( CreateOperation )operation ).getType() ) ) {
          result++;
        }
      }
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
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "item", itemId );
    parameters.put( "column", Integer.valueOf( column ) );
    Fixture.fakeCallOperation( getId( tree ), "renderToolTipText", parameters );
    Fixture.executeLifeCycleFromServerThread();
  }

  private static class LoggingSelectionListener extends SelectionAdapter {
    private final List<SelectionEvent> events;
    private LoggingSelectionListener( List<SelectionEvent> events ) {
      this.events = events;
    }
    @Override
    public void widgetSelected( SelectionEvent event ) {
      events.add( event );
    }
    @Override
    public void widgetDefaultSelected( SelectionEvent event ) {
      events.add( event );
    }
  }

  public void testRenderCreate() throws IOException, JSONException {
    lca.renderInitialization( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertEquals( "rwt.widgets.Grid", operation.getType() );
    assertEquals( "tree", operation.getProperty( "appearance" ) );
    assertEquals( Integer.valueOf( 16 ), operation.getProperty( "indentionWidth" ) );
    JSONArray actual = ( JSONArray )operation.getProperty( "selectionPadding" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[3,5]", actual ) );
    assertFalse( operation.getPropertyNames().contains( "checkBoxMetrics" ) );
    assertEquals( Boolean.FALSE, operation.getProperty( "markupEnabled" ) );
  }

  public void testRenderCreateWithFixedColumns() throws IOException {
    tree.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );

    lca.renderInitialization( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertEquals( Boolean.TRUE, operation.getProperty( "splitContainer" ) );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertEquals( WidgetUtil.getId( tree.getParent() ), operation.getParent() );
  }

  public void testRenderCreateWithVirtualNoScrollMulti() throws IOException {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.NO_SCROLL | SWT.MULTI );

    lca.renderInitialization( tree );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "VIRTUAL" ) );
    assertTrue( Arrays.asList( styles ).contains( "NO_SCROLL" ) );
    assertTrue( Arrays.asList( styles ).contains( "MULTI" ) );
    assertEquals( Boolean.TRUE, message.findListenProperty( tree, "SetData" ) );
  }

  public void testDontRenderSetDataListenerTwice() throws Exception {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.NO_SCROLL | SWT.MULTI );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( tree, "SetData" ) );
  }

  public void testDontRenderSetDataWithoutVirtual() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( tree, "SetData" ) );
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
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,21]", actual ) );
  }

  public void testRenderInitialItemCount() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "itemCount" ) == -1 );
  }

  public void testRenderItemCount() throws IOException {
    tree.setItemCount( 10 );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 10 ), message.findSetProperty( tree, "itemCount" ) );
  }

  public void testRenderItemCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "itemCount" ) );
  }

  public void testRenderInitialItemHeight() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( getId( tree ), "itemHeight" ) );
  }

  public void testRenderItemHeight() throws IOException {
    Font font = Graphics.getFont( "Arial", 26, SWT.NONE );

    tree.setFont( font );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 39 ), message.findSetProperty( tree, "itemHeight" ) );
  }

  public void testRenderItemHeightUnchanged() throws IOException {
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
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( getId( tree ), "itemMetrics" ) );
  }

  public void testRenderItemMetrics() throws IOException, JSONException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "foo" );

    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( tree, "itemMetrics" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,0,50,0,0,3,36]", ( JSONArray )actual.get( 0 ) ) );
  }

  public void testRenderItemMetricsUnchanged() throws IOException {
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
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "columnCount" ) == -1 );
  }

  public void testRenderColumnCount() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 1 ), message.findSetProperty( tree, "columnCount" ) );
  }

  public void testRenderColumnCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    new TreeColumn( tree, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "columnCount" ) );
  }

  public void testRenderInitialFixedColumns() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "fixedColumns" ) == -1 );
  }

  public void testRenderFixedColumns() throws IOException {
    new TreeColumn( tree, SWT.NONE );

    tree.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 1 ), message.findSetProperty( tree, "fixedColumns" ) );
  }

  public void testRenderFixedColumnsUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setData( "fixedColumns", Integer.valueOf( 1 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "fixedColumns" ) );
  }

  public void testRenderInitialTreeColumn() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "treeColumn" ) == -1 );
  }

  public void testRenderTreeColumn() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    tree.setColumnOrder( new int[]{ 1, 0 } );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 1 ), message.findSetProperty( tree, "treeColumn" ) );
  }

  public void testRenderTreeColumnUnchanged() throws IOException {
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
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "headerHeight" ) == -1 );
  }

  public void testRenderHeaderHeight() throws IOException {
    tree.setHeaderVisible( true );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 31 ), message.findSetProperty( tree, "headerHeight" ) );
  }

  public void testRenderHeaderHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "headerHeight" ) );
  }

  public void testRenderInitialHeaderVisible() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "headerVisible" ) == -1 );
  }

  public void testRenderHeaderVisible() throws IOException {
    tree.setHeaderVisible( true );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( tree, "headerVisible" ) );
  }

  public void testRenderHeaderVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "headerVisible" ) );
  }

  public void testRenderInitialLinesVisible() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "linesVisible" ) == -1 );
  }

  public void testRenderLinesVisible() throws IOException {
    tree.setLinesVisible( true );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( tree, "linesVisible" ) );
  }

  public void testRenderLinesVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setLinesVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "linesVisible" ) );
  }

  public void testRenderInitialTopItemIndex() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "topItemIndex" ) == -1 );
  }

  public void testRenderTopItemIndex() throws IOException {
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    tree.setTopItem( item );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 2 ), message.findSetProperty( tree, "topItemIndex" ) );
  }

  public void testRenderTopItemIndexUnchanged() throws IOException {
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
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "scrollLeft" ) == -1 );
  }

  public void testRenderScrollLeft() throws IOException {
    setScrollLeft( tree, 10 );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 10 ), message.findSetProperty( tree, "scrollLeft" ) );
  }

  public void testRenderScrollLeftUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    setScrollLeft( tree, 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "scrollLeft" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException, JSONException {
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item3 = new TreeItem( tree, SWT.NONE );

    tree.setSelection( new TreeItem[] { item1, item3 } );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( tree, "selection" );
    StringBuilder expected = new StringBuilder();
    expected.append( "[" );
    expected.append( WidgetUtil.getId( item1 ) );
    expected.append( "," );
    expected.append( WidgetUtil.getId( item3 ) );
    expected.append( "]" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected.toString(), actual ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item3 = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setSelection( new TreeItem[] { item1, item3 } );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "selection" ) );
  }

  public void testRenderAddScrollBarsSelectionListener_Horizontal() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    ScrollBar hScroll = tree.getHorizontalBar();
    Fixture.markInitialized( display );
    lca.render( tree );
    Fixture.preserveWidgets();

    hScroll.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( hScroll, "Selection" ) );
  }

  public void testRenderRemoveScrollBarsSelectionListener_Horizontal() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    ScrollBar hScroll = tree.getHorizontalBar();
    SelectionListener listener = new SelectionAdapter() { };
    hScroll.addSelectionListener( listener );
    Fixture.markInitialized( display );
    lca.render( tree );
    Fixture.fakeNewRequest();
    Fixture.preserveWidgets();

    hScroll.removeSelectionListener( listener );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( hScroll, "Selection" ) );
  }

  public void testRenderScrollBarsSelectionListenerUnchanged_Horizontal() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    ScrollBar hScroll = tree.getHorizontalBar();
    Fixture.markInitialized( display );
    lca.render( tree );
    Fixture.preserveWidgets();

    hScroll.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( hScroll, "Selection" ) );
  }

  public void testRenderAddScrollBarsSelectionListener_Vertical() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    ScrollBar vScroll = tree.getVerticalBar();
    Fixture.markInitialized( display );
    lca.render( tree );
    Fixture.preserveWidgets();

    vScroll.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( vScroll, "Selection" ) );
  }

  public void testRenderRemoveScrollBarsSelectionListener_Vertical() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    ScrollBar vScroll = tree.getVerticalBar();
    SelectionListener listener = new SelectionAdapter() { };
    vScroll.addSelectionListener( listener );
    Fixture.markInitialized( display );
    lca.render( tree );
    Fixture.fakeNewRequest();
    Fixture.preserveWidgets();

    vScroll.removeSelectionListener( listener );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( vScroll, "Selection" ) );
  }

  public void testRenderScrollBarsSelectionListenerUnchanged_Vertical() throws Exception {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    ScrollBar vScroll = tree.getVerticalBar();
    Fixture.markInitialized( display );
    lca.render( tree );
    Fixture.preserveWidgets();

    vScroll.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( vScroll, "Selection" ) );
  }

  public void testRenderInitialScrollBarsVisible() throws IOException {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree.getHorizontalBar(), "visibility" ) );
    assertNull( message.findSetOperation( tree.getVerticalBar(), "visibility" ) );
  }

  public void testRenderScrollBarsVisible_Horizontal() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    column.setWidth( 25 );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( tree.getHorizontalBar(), "visibility" ) );
    assertNull( message.findSetOperation( tree.getVerticalBar(), "visibility" ) );
  }

  public void testRenderScrollBarsVisible_Vertical() throws IOException {
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    new TreeColumn( tree, SWT.NONE );

    tree.setHeaderVisible( true );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree.getHorizontalBar(), "visibility" ) );
    assertEquals( Boolean.TRUE, message.findSetProperty( tree.getVerticalBar(), "visibility" ) );
  }

  public void testRenderScrollBarsVisibleUnchanged() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Tree tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    lca.render( tree );
    Fixture.fakeNewRequest();

    column.setWidth( 25 );
    tree.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree.getHorizontalBar(), "visibility" ) );
    assertNull( message.findSetOperation( tree.getVerticalBar(), "visibility" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( tree, "Selection" ) );
    assertNull( message.findListenOperation( tree, "DefaultSelection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    tree.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.removeListener( SWT.Selection, listener );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( tree, "Selection" ) );
    assertNull( message.findListenOperation( tree, "DefaultSelection" ) );
  }

  public void testRenderAddDefaultSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( tree, "DefaultSelection" ) );
    assertNull( message.findListenOperation( tree, "Selection" ) );
  }

  public void testRenderRemoveDefaultSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    tree.addListener( SWT.DefaultSelection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.removeListener( SWT.DefaultSelection, listener );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( tree, "DefaultSelection" ) );
    assertNull( message.findListenOperation( tree, "Selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( tree, "Selection" ) );
  }

  public void testRenderAddExpandListener() throws Exception {
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( tree, "Expand" ) );
  }

  public void testRenderAddCollapseListener() throws Exception {
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( tree, "Collapse" ) );
  }

  public void testRenderInitialEnableCellToolTip() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "enableCellToolTip" ) == -1 );
  }

  public void testRenderEnableCellToolTip() throws IOException {
    tree.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( tree, "enableCellToolTip" ) );
  }

  public void testRenderEnableCellToolTipUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "enableCellToolTip" ) );
  }

  public void testRenderCellToolTipText() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    createTreeItems( tree, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( Item item, int columnIndex ) {
        StringBuilder buffer = new StringBuilder();
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

  public void testRenderInitialSortDirection() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "sortDirection" ) == -1 );
  }

  public void testRenderSortDirection() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    tree.setSortColumn( column );
    tree.setSortDirection( SWT.UP );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "up", message.findSetProperty( tree, "sortDirection" ) );
  }

  public void testRenderSortDirectionUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setSortColumn( column );
    tree.setSortDirection( SWT.UP );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "sortDirection" ) );
  }

  public void testRenderInitialSortColumn() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "sortColumn" ) == -1 );
  }

  public void testRenderSortColumn() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    tree.setSortColumn( column );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( WidgetUtil.getId( column ), message.findSetProperty( tree, "sortColumn" ) );
  }

  public void testRenderSortColumnUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setSortColumn( column );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "sortColumn" ) );
  }

  public void testRenderInitialFocusItem() throws IOException {
    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getPropertyNames().indexOf( "focusItem" ) == -1 );
  }

  public void testRenderFocusItem() throws IOException {
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    tree.setSelection( item );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( WidgetUtil.getId( item ), message.findSetProperty( tree, "focusItem" ) );
  }

  public void testRenderFocusItemUnchanged() throws IOException {
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setSelection( item );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "focusItem" ) );
  }

  public void testRenderFocusItemOnEmptySelection() throws IOException {
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setSelection( item );
    Fixture.preserveWidgets();
    tree.setSelection( new TreeItem[ 0 ] );
    lca.renderChanges( tree );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "focusItem" ) );
  }

  public void testRenderMarkupEnabled() throws IOException {
    tree.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    lca.render( tree );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findCreateProperty( tree, "markupEnabled" ) );
  }

  public void testTreeEvent() {
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    final TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    new TreeItem( treeItem, SWT.NONE );
    final StringBuilder log = new StringBuilder();
    TreeListener listener = new TreeListener() {
      public void treeCollapsed( TreeEvent event ) {
        assertEquals( tree, event.getSource() );
        assertEquals( treeItem, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "collapsed" );
      }

      public void treeExpanded( TreeEvent event ) {
        assertEquals( tree, event.getSource() );
        assertEquals( treeItem, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "expanded" );
      }
    };
    tree.addTreeListener( listener );

    fakeTreeEvent( treeItem, ClientMessageConst.EVENT_EXPAND );
    Fixture.readDataAndProcessAction( tree );

    assertEquals( "expanded", log.toString() );

    log.setLength( 0 );

    Fixture.fakeNewRequest( display );
    fakeTreeEvent( treeItem, ClientMessageConst.EVENT_COLLAPSE );
    Fixture.readDataAndProcessAction( tree );

    assertEquals( "collapsed", log.toString() );
  }

  private static void setScrollLeft( Tree tree, int scrollLeft ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    treeAdapter.setScrollLeft( scrollLeft);
  }

  private static ITreeAdapter getTreeAdapter( Tree tree ) {
    Object adapter = tree.getAdapter( ITreeAdapter.class );
    return ( ITreeAdapter )adapter;
  }

  private static void fakeTreeEvent( TreeItem item, String eventName ) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( ClientMessageConst.EVENT_PARAM_ITEM, getId( item ) );
    Fixture.fakeNotifyOperation( getId( item.getParent() ), eventName, parameters );
  }

  private void fakeSetTopItemIndex( Tree tree, int index ) {
    Fixture.fakeSetParameter( getId( tree ), "topItemIndex", Integer.valueOf( index ) );
  }
}
