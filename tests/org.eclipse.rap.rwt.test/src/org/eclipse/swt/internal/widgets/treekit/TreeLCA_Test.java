/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getLCA;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.internal.widgets.treekit.TreeLCA.ItemMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TreeLCA_Test {

  private Display display;
  private Shell shell;
  private Tree tree;
  private TreeLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    tree = new Tree( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    lca = new TreeLCA();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCommonControlProperties() throws IOException {
    ControlLCATestUtil.testCommonControlProperties( tree );
  }

  @Test
  public void testGetItemMetricsImageWidth() throws IOException {
    Image image1 = createImage( display, Fixture.IMAGE_100x50 );
    Image image2 = createImage( display, Fixture.IMAGE_50x100 );
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

  @Test
  public void testGetItemMetricsImageLeft() throws IOException {
    Image image1 = createImage( display, Fixture.IMAGE_100x50 );
    Image image2 = createImage( display, Fixture.IMAGE_50x100 );
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

  @Test
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

  @Test
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

  @Test
  public void testGetItemMetricsTextLeftWithImage() throws IOException {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Image image = createImage( display, Fixture.IMAGE_100x50 );
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

  @Test
  public void testGetItemMetricsTextLeftWithCheckbox() throws IOException {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Image image = createImage( display, Fixture.IMAGE_100x50 );
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

  @Test
  public void testGetItemMetricsTextWidthWithCheckbox() throws IOException {
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Image image = createImage( display, Fixture.IMAGE_100x50 );
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

  @Test
  public void testVirtualSelectionEvent() {
    java.util.List<SelectionEvent> events = new LinkedList<SelectionEvent>();
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    getRemoteObject( tree ).setHandler( new TreeOperationHandler( tree ) );
    tree.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new LoggingSelectionListener( events ) );

    Fixture.fakeNewRequest();
    JsonObject properties = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_ITEM, getId( tree ) + "#" + 50 );
    Fixture.fakeNotifyOperation( getId( tree ),
                                 ClientMessageConst.EVENT_SELECTION,
                                 properties );
    Fixture.readDataAndProcessAction( tree );

    assertEquals( 1, events.size() );
    SelectionEvent event = events.get( 0 );
    assertEquals( tree, event.getSource() );
    assertSame( tree.getItem( 50 ), event.item );
  }

  @Test
  public void testVirtualSelectionEventWithSubitem() {
    java.util.List<SelectionEvent> events = new LinkedList<SelectionEvent>();
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    getRemoteObject( tree ).setHandler( new TreeOperationHandler( tree ) );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    // Important: parent item must be materialized
    item.setText( "item 1" );
    item.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new LoggingSelectionListener( events ) );

    Fixture.fakeNewRequest();
    JsonObject properties = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_ITEM, getId( item ) + "#" + 50 );
    Fixture.fakeNotifyOperation( getId( tree ),
                                 ClientMessageConst.EVENT_SELECTION,
                                 properties );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, events.size() );
    SelectionEvent event = events.get( 0 );
    assertEquals( tree, event.getSource() );
    assertSame( item.getItem( 50 ), event.item );
  }

  @Test
  public void testCellTooltipRequestForMissingCells() {
    getRemoteObject( tree ).setHandler( new TreeOperationHandler( tree ) );
    createTreeItems( tree, 3 );
    final StringBuilder log = new StringBuilder();
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      @Override
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

  @Test
  public void testCreateVirtualItems() {
    final Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 200, 200 );
    tree.addListener( SWT.SetData, new Listener() {

      @Override
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setText( "node " + tree.indexOf( item ) );
        item.setItemCount( 10 );
      }
    } );
    tree.setItemCount( 7 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.fakeNewRequest();

    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 7, countCreateOperations( "rwt.widgets.GridItem", message ) );
  }

  @Test
  public void testVirtualReadSelection() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    getRemoteObject( tree ).setHandler( new TreeOperationHandler( tree ) );
    tree.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );

    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( tree ),
                             "selection",
                             createJsonArray( getId( tree ) + "#" + 50 ) );
    Fixture.readDataAndProcessAction( tree );

    assertEquals( 1, tree.getSelection().length );
    assertSame( tree.getItem( 50 ), tree.getSelection()[ 0 ] );
  }

  @Test
  public void testVirtualReadSelectionWithSubitem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    getRemoteObject( tree ).setHandler( new TreeOperationHandler( tree ) );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    // Important: parent item must be materialized
    item.setText( "item 1" );
    item.setItemCount( 100 );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );

    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( tree ),
                             "selection",
                             createJsonArray( getId( item ) + "#" + 50 ) );
    Fixture.readDataAndProcessAction( tree );

    assertEquals( 1, tree.getSelection().length );
    assertSame( item.getItem( 50 ), tree.getSelection()[ 0 ] );
  }

  @Test
  public void testReadSelectionDisposedItem() {
    Tree tree = new Tree( shell, SWT.MULTI );
    getRemoteObject( tree ).setHandler( new TreeOperationHandler( tree ) );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    item1.dispose();

    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( tree ), "selection", createJsonArray( getId( item1 ) ) );
    Fixture.readDataAndProcessAction( tree );

    TreeItem[] selectedItems = tree.getSelection();
    assertEquals( 0, selectedItems.length );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertEquals( "rwt.widgets.Grid", operation.getType() );
    assertEquals( "tree", operation.getProperties().get( "appearance" ).asString() );
    assertEquals( 16, operation.getProperties().get( "indentionWidth" ).asInt() );
    assertEquals( JsonArray.readFrom( "[3, 5]" ), operation.getProperties().get( "selectionPadding" ) );
    assertFalse( operation.getProperties().names().contains( "checkBoxMetrics" ) );
    assertEquals( JsonValue.FALSE, operation.getProperties().get( "markupEnabled" ) );
  }

  @Test
  public void testRenderCreateWithFixedColumns() throws IOException {
    tree.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );

    lca.renderInitialization( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "splitContainer" ) );
  }

  @Test
  public void testRenderCreate_setsOperationHandler() throws IOException {
    String id = getId( tree );

    lca.renderInitialization( tree );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof TreeOperationHandler );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertEquals( getId( tree.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderCreateWithVirtualNoScrollMulti() throws IOException {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.NO_SCROLL | SWT.MULTI );

    lca.renderInitialization( tree );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    List<String> styles = getStyles( operation );
    assertTrue( styles.contains( "VIRTUAL" ) );
    assertTrue( styles.contains( "NO_SCROLL" ) );
    assertTrue( styles.contains( "MULTI" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( tree, "SetData" ) );
  }

  @Test
  public void testDontRenderSetDataListenerTwice() throws Exception {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.NO_SCROLL | SWT.MULTI );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( tree, "SetData" ) );
  }

  @Test
  public void testDontRenderSetDataWithoutVirtual() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( tree, "SetData" ) );
  }

  @Test
  public void testRenderCreateWithFullSelection() throws IOException {
    Tree tree = new Tree( shell, SWT.FULL_SELECTION );

    lca.renderInitialization( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( getStyles( operation ).contains( "FULL_SELECTION" ) );
    assertFalse( operation.getProperties().names().contains( "selectionPadding" ) );
  }

  @Test
  public void testRenderCreateWithCheck() throws IOException {
    Tree tree = new Tree( shell, SWT.CHECK );

    lca.renderInitialization( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( getStyles( operation ).contains( "CHECK" ) );
    JsonArray exptected = JsonArray.readFrom( "[0, 21]" );
    assertEquals( exptected, operation.getProperties().get( "checkBoxMetrics" ) );
  }

  @Test
  public void testRenderInitialItemCount() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "itemCount" ) );
  }

  @Test
  public void testRenderItemCount() throws IOException {
    tree.setItemCount( 10 );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( tree, "itemCount" ).asInt() );
  }

  @Test
  public void testRenderItemCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "itemCount" ) );
  }

  @Test
  public void testRenderInitialItemHeight() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( getId( tree ), "itemHeight" ) );
  }

  @Test
  public void testRenderItemHeight() throws IOException {
    Font font = new Font( display, "Arial", 26, SWT.NONE );

    tree.setFont( font );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 39, message.findSetProperty( tree, "itemHeight" ).asInt() );
  }

  @Test
  public void testRenderItemHeightUnchanged() throws IOException {
    Font font = new Font( display, "Arial", 26, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setFont( font );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "itemHeight" ) );
  }

  @Test
  public void testRenderInitialItemMetrics() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( getId( tree ), "itemMetrics" ) );
  }

  @Test
  public void testRenderItemMetrics() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "foo" );

    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[[0, 0, 50, 0, 0, 3, 36]]" );
    assertEquals( expected, message.findSetProperty( tree, "itemMetrics" ) );
  }

  @Test
  public void testRenderItemMetricsUnchanged() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "foo" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "itemMetrics" ) );
  }

  @Test
  public void testRenderInitialColumnCount() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "columnCount" ) );
  }

  @Test
  public void testRenderColumnCount() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( tree, "columnCount" ).asInt() );
  }

  @Test
  public void testRenderColumnCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    new TreeColumn( tree, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "columnCount" ) );
  }

  @Test
  public void testRenderInitialColumnOrder() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertTrue( operation.getProperties().names().indexOf( "columnOrder" ) == -1 );
  }

  @Test
  public void testRenderColumnOrder() throws IOException {
    TreeColumn[] columns = createTreeColumns( tree, 3 );
    tree.setColumnOrder( new int[] { 2, 0, 1 } );

    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray()
      .add( getId( columns[ 2 ] ) )
      .add( getId( columns[ 0 ] ) )
      .add( getId( columns[ 1 ] ) );
    assertEquals( expected, message.findSetProperty( tree, "columnOrder" ) );
  }

  @Test
  public void testRenderColumnOrderUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    createTreeColumns( tree, 3 );
    tree.setColumnOrder( new int[] { 2, 0, 1 } );

    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "columnOrder" ) );
  }

  @Test
  public void testRenderInitialFixedColumns() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "fixedColumns" ) );
  }

  @Test
  public void testRenderFixedColumns() throws IOException {
    new TreeColumn( tree, SWT.NONE );

    tree.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( tree, "fixedColumns" ).asInt() );
  }

  @Test
  public void testRenderFixedColumnsUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setData( "fixedColumns", Integer.valueOf( 1 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "fixedColumns" ) );
  }

  @Test
  public void testRenderInitialTreeColumn() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "treeColumn" ) );
  }

  @Test
  public void testRenderTreeColumn() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    tree.setColumnOrder( new int[]{ 1, 0 } );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( tree, "treeColumn" ).asInt() );
  }

  @Test
  public void testRenderTreeColumnUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setColumnOrder( new int[]{ 1, 0 } );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "treeColumn" ) );
  }

  @Test
  public void testRenderInitialHeaderHeight() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "headerHeight" ) );
  }

  @Test
  public void testRenderHeaderHeight() throws IOException {
    tree.setHeaderVisible( true );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 31, message.findSetProperty( tree, "headerHeight" ).asInt() );
  }

  @Test
  public void testRenderHeaderHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "headerHeight" ) );
  }

  @Test
  public void testRenderInitialHeaderVisible() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "headerVisible" ) );
  }

  @Test
  public void testRenderHeaderVisible() throws IOException {
    tree.setHeaderVisible( true );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( tree, "headerVisible" ) );
  }

  @Test
  public void testRenderHeaderVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "headerVisible" ) );
  }

  @Test
  public void testRenderInitialLinesVisible() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "linesVisible" ) );
  }

  @Test
  public void testRenderLinesVisible() throws IOException {
    tree.setLinesVisible( true );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( tree, "linesVisible" ) );
  }

  @Test
  public void testRenderLinesVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setLinesVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "linesVisible" ) );
  }

  @Test
  public void testRenderInitialTopItemIndex() throws IOException {
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "topItemIndex" ) );
  }

  @Test
  public void testRenderTopItemIndex() throws IOException {
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    tree.setTopItem( item );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( tree, "topItemIndex" ).asInt() );
  }

  @Test
  public void testRenderTopItemIndex_afterAllItems() throws IOException {
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    tree.setTopItem( item );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation1 = message.findCreateOperation( item );
    CreateOperation operation2 = message.findCreateOperation( column );
    SetOperation operation3 = message.findSetOperation( tree, "topItemIndex" );
    assertNotNull( operation1 );
    assertNotNull( operation2 );
    assertNotNull( operation3 );
    List<Operation> operations = message.getOperations();
    assertTrue( operations.indexOf( operation1 ) < operations.indexOf( operation3 ) );
    assertTrue( operations.indexOf( operation2 ) < operations.indexOf( operation3 ) );
  }

  @Test
  public void testRenderTopItemIndexUnchanged() throws IOException {
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setTopItem( item );
    Fixture.preserveWidgets();
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "topItemIndex" ) );
  }

  @Test
  public void testRenderInitialScrollLeft() throws IOException {
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "scrollLeft" ) );
  }

  @Test
  public void testRenderScrollLeft() throws IOException {
    setScrollLeft( tree, 10 );

    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( tree, "scrollLeft" ).asInt() );
  }

  @Test
  public void testRenderScrollLeft_afterAllItems() throws IOException {
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    setScrollLeft( tree, 10 );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation1 = message.findCreateOperation( item );
    CreateOperation operation2 = message.findCreateOperation( column );
    SetOperation operation3 = message.findSetOperation( tree, "scrollLeft" );
    assertNotNull( operation1 );
    assertNotNull( operation2 );
    assertNotNull( operation3 );
    List<Operation> operations = message.getOperations();
    assertTrue( operations.indexOf( operation1 ) < operations.indexOf( operation3 ) );
    assertTrue( operations.indexOf( operation2 ) < operations.indexOf( operation3 ) );
  }

  @Test
  public void testRenderScrollLeftUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    setScrollLeft( tree, 10 );
    Fixture.preserveWidgets();
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "scrollLeft" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "selection" ) );
  }

  @Test
  public void testRenderSelection() throws IOException {
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item3 = new TreeItem( tree, SWT.NONE );

    tree.setSelection( new TreeItem[] { item1, item3 } );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray();
    expected.add( getId( item1 ) );
    expected.add( getId( item3 ) );
    assertEquals( expected, message.findSetProperty( tree, "selection" ) );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item3 = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setSelection( new TreeItem[] { item1, item3 } );
    Fixture.preserveWidgets();
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "selection" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( tree, "Selection" ) );
    assertNull( message.findListenOperation( tree, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    tree.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.removeListener( SWT.Selection, listener );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( tree, "Selection" ) );
    assertNull( message.findListenOperation( tree, "DefaultSelection" ) );
  }

  @Test
  public void testRenderAddDefaultSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( tree, "DefaultSelection" ) );
    assertNull( message.findListenOperation( tree, "Selection" ) );
  }

  @Test
  public void testRenderRemoveDefaultSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    tree.addListener( SWT.DefaultSelection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.removeListener( SWT.DefaultSelection, listener );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( tree, "DefaultSelection" ) );
    assertNull( message.findListenOperation( tree, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    Fixture.preserveWidgets();

    tree.addListener( SWT.Selection, mock( Listener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( tree, "Selection" ) );
  }

  @Test
  public void testRenderAddExpandListener() throws Exception {
    lca.renderInitialization( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( tree, "Expand" ) );
  }

  @Test
  public void testRenderAddCollapseListener() throws Exception {
    lca.renderInitialization( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( tree, "Collapse" ) );
  }

  @Test
  public void testRenderInitialEnableCellToolTip() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "enableCellToolTip" ) );
  }

  @Test
  public void testRenderEnableCellToolTip() throws IOException {
    tree.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( tree, "enableCellToolTip" ) );
  }

  @Test
  public void testRenderEnableCellToolTipUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "enableCellToolTip" ) );
  }

  @Test
  public void testRenderCellToolTipText() {
    getRemoteObject( tree ).setHandler( new TreeOperationHandler( tree ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    createTreeItems( tree, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      @Override
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
    JsonObject parameters = new JsonObject().add( "item", itemId ).add( "column", 0 );
    Fixture.fakeCallOperation( getId( tree ), "renderToolTipText", parameters );
    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    String expected = "[" + itemId + ",0]";
    assertEquals( expected, message.findSetProperty( tree, "cellToolTipText" ).asString() );
  }

  @Test
  public void testRenderCellToolTipText_resetsText() throws IOException {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipText( "foo" );

    lca.renderChanges( tree );

    assertNull( adapter.getCellToolTipText() );
  }

  @Test
  public void testRenderCellToolTipTextNull() {
    getRemoteObject( tree ).setHandler( new TreeOperationHandler( tree ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );
    createTreeItems( tree, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      @Override
      public void getToolTipText( Item item, int columnIndex ) {
        adapter.setCellToolTipText( null );
      }
    } );

    String itemId = WidgetUtil.getId( tree.getItem( 2 ) );
    processCellToolTipRequest( tree, itemId, 0 );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "cellToolTipText" ) );
  }

  @Test
  public void testRenderInitialSortDirection() throws IOException {
    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "sortDirection" ) );
  }

  @Test
  public void testRenderSortDirection() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    tree.setSortColumn( column );
    tree.setSortDirection( SWT.UP );
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "up", message.findSetProperty( tree, "sortDirection" ).asString() );
  }

  @Test
  public void testRenderSortDirectionUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setSortColumn( column );
    tree.setSortDirection( SWT.UP );
    Fixture.preserveWidgets();
    lca.renderChanges( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "sortDirection" ) );
  }

  @Test
  public void testRenderInitialSortColumn() throws IOException {
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "sortColumn" ) );
  }

  @Test
  public void testRenderSortColumn() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );

    tree.setSortColumn( column );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( column ), message.findSetProperty( tree, "sortColumn" ).asString() );
  }

  @Test
  public void testRenderSortColumnUnchanged() throws IOException {
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setSortColumn( column );
    Fixture.preserveWidgets();
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "sortColumn" ) );
  }

  @Test
  public void testRenderInitialFocusItem() throws IOException {
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( tree );
    assertFalse( operation.getProperties().names().contains( "focusItem" ) );
  }

  @Test
  public void testRenderFocusItem() throws IOException {
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    tree.setSelection( item );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( item ), message.findSetProperty( tree, "focusItem" ).asString() );
  }

  @Test
  public void testRenderFocusItemUnchanged() throws IOException {
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( tree );

    tree.setSelection( item );
    Fixture.preserveWidgets();
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "focusItem" ) );
  }

  @Test
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

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( tree, "focusItem" ) );
  }

  @Test
  public void testRenderMarkupEnabled() throws IOException {
    tree.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findCreateProperty( tree, "markupEnabled" ) );
  }

  @Test
  public void testRenderRowTemplate() throws IOException {
    Template rowTemplate = new Template();
    tree.setData( RWT.ROW_TEMPLATE, rowTemplate );

    lca.render( tree );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findCreateProperty( tree, "rowTemplate" ) );
  }

  private static void setScrollLeft( Tree tree, int scrollLeft ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    treeAdapter.setScrollLeft( scrollLeft);
  }

  private static ITreeAdapter getTreeAdapter( Tree tree ) {
    Object adapter = tree.getAdapter( ITreeAdapter.class );
    return ( ITreeAdapter )adapter;
  }

  private static TreeColumn[] createTreeColumns( Tree tree, int columns ) {
    TreeColumn[] result = new TreeColumn[ columns ];
    for( int i = 0; i < columns; i++ ) {
      result[ i ] = new TreeColumn( tree, SWT.NONE );
    }
    return result;
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

  private static int countCreateOperations( String type, TestMessage message ) {
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

  private static void processCellToolTipRequest( Tree tree, String itemId, int column ) {
    Fixture.fakeNewRequest();
    JsonObject parameters = new JsonObject()
      .add( "item", itemId )
      .add( "column", column );
    Fixture.fakeCallOperation( getId( tree ), "renderToolTipText", parameters );
    Fixture.readDataAndProcessAction( tree );
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

}
