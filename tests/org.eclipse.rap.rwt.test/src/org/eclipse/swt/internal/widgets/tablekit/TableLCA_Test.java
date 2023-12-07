/*******************************************************************************
 * Copyright (c) 2002, 2018 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tablekit;

import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getLCA;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.IOException;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonOperationHandler;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.internal.widgets.tablekit.TableLCA.ItemMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TableLCA_Test {

  private Display display;
  private Shell shell;
  private Table table;
  private TableLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    lca = TableLCA.INSTANCE;
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCommonControlProperties() throws IOException {
    ControlLCATestUtil.testCommonControlProperties( table );
  }

  @Test
  public void testRedraw() {
    final Table[] table = { null };
    shell.setSize( 100, 100 );
    Button button = new Button( shell, SWT.PUSH );
    getRemoteObject( button ).setHandler( new ButtonOperationHandler( button ) );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        table[ 0 ] = new Table( shell, SWT.VIRTUAL );
        table[ 0 ].setItemCount( 500 );
        table[ 0 ].setSize( 90, 90 );
        assertFalse( isItemVirtual( table[ 0 ], 0 ) );
        table[ 0 ].clearAll();
        table[ 0 ].redraw();
      }
    } );
    shell.open();

    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( display );

    assertFalse( isItemVirtual( table[ 0 ], 0  ) );
  }

  @Test
  public void testNoUnwantedResolveItems() {
    shell.setSize( 100, 100 );
    table = new Table( shell, SWT.VIRTUAL );
    Fixture.markInitialized( table );
    getRemoteObject( table ).setHandler( new TableOperationHandler( table ) );
    table.setSize( 90, 90 );
    table.setItemCount( 1000 );
    shell.open();

    fakeSetTopItemIndex( table, 500 );
    Fixture.executeLifeCycleFromServerThread();

    assertTrue( isItemVirtual( table, 499 ) );
    assertTrue( isItemVirtual( table, 800 ) );
    assertTrue( isItemVirtual( table, 999 ) );
  }

  @Test
  public void testSetDataEvent() {
    shell.setSize( 100, 100 );
    table = new Table( shell, SWT.VIRTUAL );
    Fixture.markInitialized( table );
    getRemoteObject( table ).setHandler( new TableOperationHandler( table ) );
    Listener listener = new Listener() {
      @Override
      public void handleEvent( Event event ) {
        Item item = ( Item )event.item;
        item.setText( "Item " + event.index );
      }
    };
    table.addListener( SWT.SetData, listener );
    table.setSize( 90, 90 );
    table.setItemCount( 1000 );
    shell.layout();
    shell.open();
    assertTrue( isItemVirtual( table, 500 ) ); // ensure precondition

    fakeSetTopItemIndex( table, 500 );
    Fixture.executeLifeCycleFromServerThread();

    // Remove SetData listener to not accidentially resolve item with asserts
    table.removeListener( SWT.SetData, listener );
    assertFalse( isItemVirtual( table, 500 ) );
    assertFalse( isItemVirtual( table, 502 ) );
    assertTrue( isItemVirtual( table, 510 ) );
    assertEquals( "Item 500", table.getItem( 500 ).getText() );
    assertEquals( "Item 502", table.getItem( 502 ).getText() );
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
  public void testReduceItemCountInSetData() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    shell.setSize( 100, 100 );
    table = new Table( shell, SWT.VIRTUAL );
    Listener setDataListener = mock( Listener.class );
    table.addListener( SWT.SetData, setDataListener );

    Fixture.fakePhase( PhaseId.READ_DATA );
    table.setItemCount( 1 );
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    adapter.checkData( 0 );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    table.setItemCount( 0 );
    while( display.readAndDispatch() ) {
    }
    verifyNoInteractions( setDataListener );
  }

  @Test
  public void testGetItemMetrics() throws IOException {
    Image image = createImage( display, Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    table.setHeaderVisible( true );
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 200 );
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setText( "item1" );
    TableItem item2 = new TableItem( table, SWT.NONE );
    item2.setText( "item2" );
    TableItem item3 = new TableItem( table, SWT.NONE );
    item3.setText( "item3" );

    item2.setImage( image );
    ItemMetrics[] metrics = TableLCA.getItemMetrics( table );
    assertTrue( metrics[ 0 ].imageWidth > 0 );

    item1.setImage( image );
    metrics = TableLCA.getItemMetrics( table );
    int defaultLeftPadding = 3;
    assertEquals( defaultLeftPadding, metrics[ 0 ].imageLeft );
    assertTrue( metrics[ 0 ].imageWidth > 0 );

    // spacing must be respected
    int defaultSpacing = 3;
    int expected =   metrics[ 0 ].imageLeft
                   + metrics[ 0 ].imageWidth
                   + defaultSpacing;
    assertEquals( expected, metrics[ 0 ].textLeft );

    // left offset must be compensated
    ITableAdapter adapter
      = table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 10 );
    metrics = TableLCA.getItemMetrics( table );
    assertEquals( 0, metrics[ 0 ].left );
    assertEquals( defaultLeftPadding, metrics[ 0 ].imageLeft );
    expected =   metrics[ 0 ].imageLeft
               + metrics[ 0 ].imageWidth
               + defaultSpacing;
    assertEquals( expected, metrics[ 0 ].textLeft );

    // image must not exceed right column border
    column.setWidth( 12 );
    metrics = TableLCA.getItemMetrics( table );
    assertEquals( 9, metrics[ 0 ].imageWidth );
  }

  @Test
  public void testGetItemMetricsWithCheckBox() throws IOException {
    Image image = createImage( display, Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 200, 200 );
    shell.setLayout( new FillLayout() );
    table = new Table( shell, SWT.CHECK );
    table.setHeaderVisible( true );
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 30 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 400 );
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 5 );
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setText( "item1" );
    TableItem item2 = new TableItem( table, SWT.NONE );
    item2.setText( "item2" );
    TableItem item3 = new TableItem( table, SWT.NONE );
    item3.setText( "item3" );
    item2.setImage( image );
    ItemMetrics[] metrics = TableLCA.getItemMetrics( table );
    assertEquals( 28, metrics[ 0 ].imageLeft );
    assertEquals( 2, metrics[ 0 ].imageWidth );
  }

  @Test
  public void testGetItemMetricsImageCutOffInSecondColumn() throws IOException {
    Image image = createImage( display, Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 200, 200 );
    shell.setLayout( new FillLayout() );
    table.setHeaderVisible( true );
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 400 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 30 );
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 5 );
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setText( "item1" );
    TableItem item2 = new TableItem( table, SWT.NONE );
    item2.setText( "item2" );
    TableItem item3 = new TableItem( table, SWT.NONE );
    item3.setText( "item3" );
    item2.setImage( 1, image );
    ItemMetrics[] metrics = TableLCA.getItemMetrics( table );
    assertEquals( 403, metrics[ 1 ].imageLeft );
    assertEquals( 27, metrics[ 1 ].imageWidth );
  }

  @Test
  public void testGetItemMetricsWithoutColumns() throws IOException {
    Image image = createImage( display, Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    table.setHeaderVisible( true );
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setText( "item1" );
    TableItem item2 = new TableItem( table, SWT.NONE );
    item2.setText( "item2" );
    TableItem item3 = new TableItem( table, SWT.NONE );
    item3.setText( "item3" );

    ItemMetrics[] metrics = TableLCA.getItemMetrics( table );
    assertEquals( 0, metrics[ 0 ].imageWidth );

    item2.setImage( image );
    metrics = TableLCA.getItemMetrics( table );
    assertTrue( metrics[ 0 ].imageWidth > 0 );
    int defaultLeftPadding = 3;
    assertEquals( defaultLeftPadding, metrics[ 0 ].imageLeft );
    assertTrue( metrics[ 0 ].imageWidth > 0 );

    // spacing must be respected
    int defaultSpacing = 3;
    int expected =   metrics[ 0 ].imageLeft
                   + metrics[ 0 ].imageWidth
                   + defaultSpacing;
    assertEquals( expected, metrics[ 0 ].textLeft );

    // left offset must be compensated
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 10 );
    metrics = TableLCA.getItemMetrics( table );
    assertEquals( 0, metrics[ 0 ].left );
    assertEquals( defaultLeftPadding, metrics[ 0 ].imageLeft );
    expected =   metrics[ 0 ].imageLeft
               + metrics[ 0 ].imageWidth
               + defaultSpacing;
    assertEquals( expected, metrics[ 0 ].textLeft );
  }

  @Test
  public void testGetItemMetricsWithEmptyTable() {
    table.setHeaderVisible( true );
    for( int i = 0; i < 3; i++ ) {
      TableColumn column = new TableColumn( table, SWT.NONE );
      column.setText( "column" + i );
      column.setWidth( 100 );
    }

    ItemMetrics[] metrics = TableLCA.getItemMetrics( table );

    assertEquals( 100, metrics[ 1 ].left );
    assertEquals( 100, metrics[ 1 ].width );
  }

  @Test
  public void testRenderNonNegativeImageWidth() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Image image = createImage( display, Fixture.IMAGE1 );
    item.setImage( image );
    column.setWidth( 2 );
    ItemMetrics[] metrics = TableLCA.getItemMetrics( table );
    assertEquals( 1, metrics.length );
    assertEquals( 0, metrics[ 0 ].imageWidth );
  }

  // bug 360152
  @Test
  public void testReadItemToolTipDoesNotResolveVirtualItems() {
    table = new Table( shell, SWT.VIRTUAL );
    getRemoteObject( table ).setHandler( new TableOperationHandler( table ) );
    table.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    ICellToolTipAdapter toolTipAdapter = CellToolTipUtil.getAdapter( table );
    ITableAdapter tableAdapter = table.getAdapter( ITableAdapter.class );
    ICellToolTipProvider toolTipProvider = mock( ICellToolTipProvider.class );
    toolTipAdapter.setCellToolTipProvider( toolTipProvider );
    table.setItemCount( 2 );
    TableItem item = table.getItem( 1 );

    fakeCellToolTipRequest( table, getId( item ), 0 );
    Fixture.readDataAndProcessAction( table );

    verify( toolTipProvider ).getToolTipText( item, 0 );
    assertEquals( 1, tableAdapter.getCreatedItems().length );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertEquals( "rwt.widgets.Grid", operation.getType() );
    assertEquals( "table", operation.getProperties().get( "appearance" ).asString() );
    assertEquals( 0, operation.getProperties().get( "indentionWidth" ).asInt() );
    assertEquals( -1, operation.getProperties().get( "treeColumn" ).asInt() );
    assertFalse( operation.getProperties().names().contains( "checkBoxMetrics" ) );
    assertEquals( JsonValue.FALSE, operation.getProperties().get( "markupEnabled" ) );
  }

  @Test
  public void testRenderCreateWithFixedColumns() throws IOException {
    table.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );

    lca.renderInitialization( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "splitContainer" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( table );

    lca.renderInitialization( table );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof TableOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    TableOperationHandler handler = spy( new TableOperationHandler( table ) );
    getRemoteObject( getId( table ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( table ), "Help", new JsonObject() );
    lca.readData( table );

    verify( handler ).handleNotifyHelp( table, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertEquals( getId( table.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderCreateWithVirtualNoScrollMulti() throws IOException {
    table = new Table( shell, SWT.VIRTUAL | SWT.NO_SCROLL | SWT.MULTI );

    lca.renderInitialization( table );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation create = message.findCreateOperation( table );
    List<String> styles = getStyles( create );
    assertTrue( styles.contains( "VIRTUAL" ) );
    assertTrue( styles.contains( "NO_SCROLL" ) );
    assertTrue( styles.contains( "MULTI" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( table, "SetData" ) );
  }

  @Test
  public void testDontRenderSetDataListenerTwice() throws Exception {
    table = new Table( shell, SWT.VIRTUAL | SWT.NO_SCROLL | SWT.MULTI );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( table, "SetData" ) );
  }

  @Test
  public void testDontRenderSetDataWithoutVirtual() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( table, "SetData" ) );
  }

  @Test
  public void testRenderCreateWithHideSelection() throws IOException {
    table = new Table( shell, SWT.HIDE_SELECTION );

    lca.renderInitialization( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( getStyles( operation ).contains( "HIDE_SELECTION" ) );
  }

  @Test
  public void testRenderCreateWithCheck() throws IOException {
    table = new Table( shell, SWT.CHECK );

    lca.renderInitialization( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( getStyles( operation ).contains( "CHECK" ) );
    JsonArray expected = JsonArray.readFrom( "[4, 21]" );
    assertEquals( expected, operation.getProperties().get( "checkBoxMetrics" ) );
  }

  @Test
  public void testRenderInitialItemCount() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "itemCount" ) );
  }

  @Test
  public void testRenderItemCount() throws IOException {
    table.setItemCount( 10 );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( table, "itemCount" ).asInt() );
  }

  @Test
  public void testRenderItemCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "itemCount" ) );
  }

  @Test
  public void testRenderInitialColumnOrder() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getProperties().names().indexOf( "columnOrder" ) == -1 );
  }

  @Test
  public void testRenderColumnOrder() throws IOException {
    TableColumn[] columns = createTableColumns( table, 3, SWT.NONE );
    table.setColumnOrder( new int[] { 2, 0, 1 } );

    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray()
      .add( getId( columns[ 2 ] ) )
      .add( getId( columns[ 0 ] ) )
      .add( getId( columns[ 1 ] ) );
    assertEquals( expected, message.findSetProperty( table, "columnOrder" ) );
  }

  @Test
  public void testRenderColumnOrderUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    createTableColumns( table, 3, SWT.NONE );
    table.setColumnOrder( new int[] { 2, 0, 1 } );

    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "columnOrder" ) );
  }

  @Test
  public void testRenderInitialItemHeight() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( table, "itemHeight" ) );
  }

  @Test
  public void testRenderItemHeight() throws IOException {
    Font font = new Font( display, "Arial", 26, SWT.NONE );

    table.setFont( font );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( table, "itemHeight" ) );
  }

  @Test
  public void testRenderItemHeightUnchanged() throws IOException {
    Font font = new Font( display, "Arial", 26, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setFont( font );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "itemHeight" ) );
  }

  @Test
  public void testRenderInitialItemMetrics() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( table, "itemMetrics" ) );
  }

  @Test
  public void testRenderItemMetrics() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "foo" );

    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[[0, 0, 26, 3, 0, 3, 20]]" );
    assertEquals( expected, message.findSetProperty( table, "itemMetrics" ) );
  }

  @Test
  public void testRenderItemMetricsUnchanged() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "foo" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "itemMetrics" ) );
  }

  @Test
  public void testRenderInitialColumnCount() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "columnCount" ) );
  }

  @Test
  public void testRenderColumnCount() throws IOException {
    new TableColumn( table, SWT.NONE );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( table, "columnCount" ).asInt() );
  }

  @Test
  public void testRenderColumnCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    new TableColumn( table, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "columnCount" ) );
  }

  @Test
  public void testRenderInitialFixedColumns() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "fixedColumns" ) );
  }

  @Test
  public void testRenderFixedColumns() throws IOException {
    new TableColumn( table, SWT.NONE );

    table.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( table, "fixedColumns" ).asInt() );
  }

  @Test
  public void testRenderFixedColumnsUnchanged() throws IOException {
    new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setData( "fixedColumns", Integer.valueOf( 1 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "fixedColumns" ) );
  }

  @Test
  public void testRenderInitialHeaderHeight() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "headerHeight" ) );
  }

  @Test
  public void testRenderHeaderHeight() throws IOException {
    table.setHeaderVisible( true );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 31, message.findSetProperty( table, "headerHeight" ).asInt() );
  }

  @Test
  public void testRenderHeaderHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "headerHeight" ) );
  }

  @Test
  public void testRenderInitialHeaderVisible() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "headerVisible" ) );
  }

  @Test
  public void testRenderHeaderVisible() throws IOException {
    table.setHeaderVisible( true );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( table, "headerVisible" ) );
  }

  @Test
  public void testRenderHeaderVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "headerVisible" ) );
  }

  @Test
  public void testRenderInitialLinesVisible() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "linesVisible" ) );
  }

  @Test
  public void testRenderLinesVisible() throws IOException {
    table.setLinesVisible( true );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( table, "linesVisible" ) );
  }

  @Test
  public void testRenderLinesVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setLinesVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "linesVisible" ) );
  }

  @Test
  public void testRenderInitialHeaderBackground() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "headerBackground" ) );
  }

  @Test
  public void testRenderHeaderBackground() throws IOException {
    Color color = new Color( display, 1, 2, 3 );
    table.setHeaderBackground( color );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray().add( 1 ).add( 2 ).add( 3 ).add( 255 );
    assertEquals( expected, message.findSetProperty( table, "headerBackground" ) );
  }

  @Test
  public void testRenderHeaderBackgroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setHeaderBackground( new Color( display, 1, 2, 3 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "headerBackground" ) );
  }

  @Test
  public void testRenderInitialHeaderForeground() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "headerForeground" ) );
  }

  @Test
  public void testRenderHeaderForeground() throws IOException {
    Color color = new Color( display, 1, 2, 3 );
    table.setHeaderForeground( color );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray().add( 1 ).add( 2 ).add( 3 ).add( 255 );
    assertEquals( expected, message.findSetProperty( table, "headerForeground" ) );
  }

  @Test
  public void testRenderHeaderForegroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setHeaderForeground( new Color( display, 1, 2, 3 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "headerForeground" ) );
  }

  @Test
  public void testRenderInitialTopItemIndex() throws IOException {
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "topItemIndex" ) );
  }

  @Test
  public void testRenderTopItemIndex() throws IOException {
    createTableItems( table, 3 );

    table.setTopIndex( 2 );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( table, "topItemIndex" ).asInt() );
  }

  @Test
  public void testRenderTopItemIndex_afterAllItems() throws IOException {
    createTableItems( table, 3 );
    TableColumn column = new TableColumn( table, SWT.NONE );

    table.setTopIndex( 2 );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation1 = message.findCreateOperation( table.getItem( 2 ) );
    CreateOperation operation2 = message.findCreateOperation( column );
    SetOperation operation3 = message.findSetOperation( table, "topItemIndex" );
    assertNotNull( operation1 );
    assertNotNull( operation2 );
    assertNotNull( operation3 );
    List<Operation> operations = message.getOperations();
    assertTrue( operations.indexOf( operation1 ) < operations.indexOf( operation3 ) );
    assertTrue( operations.indexOf( operation2 ) < operations.indexOf( operation3 ) );
  }

  @Test
  public void testRenderTopItemIndexUnchanged() throws IOException {
    createTableItems( table, 3 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setTopIndex( 2 );
    Fixture.preserveWidgets();
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "topItemIndex" ) );
  }

  @Test
  public void testRenderInitialFocusItem() throws IOException {
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "focusItem" ) );
  }

  @Test
  public void testRenderFocusItem() throws IOException {
    createTableItems( table, 2 );
    TableItem item = new TableItem( table, SWT.NONE );

    table.getAdapter( ITableAdapter.class ).setFocusIndex( 2 );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( item ), message.findSetProperty( table, "focusItem" ).asString() );
  }

  @Test
  public void testRenderFocusItemUnchanged() throws IOException {
    createTableItems( table, 3 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.getAdapter( ITableAdapter.class ).setFocusIndex( 2 );
    Fixture.preserveWidgets();
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "focusItem" ) );
  }

  @Test
  public void testRenderInitialScrollLeft() throws IOException {
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "scrollLeft" ) );
  }

  @Test
  public void testRenderScrollLeft() throws IOException {
    table.getAdapter( ITableAdapter.class ).setLeftOffset( 10 );

    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( table, "scrollLeft" ).asInt() );
  }

  @Test
  public void testRenderScrollLeft_afterAllItems() throws IOException {
    createTableItems( table, 3 );
    TableColumn column = new TableColumn( table, SWT.NONE );

    table.getAdapter( ITableAdapter.class ).setLeftOffset( 10 );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation1 = message.findCreateOperation( table.getItem( 2 ) );
    CreateOperation operation2 = message.findCreateOperation( column );
    SetOperation operation3 = message.findSetOperation( table, "scrollLeft" );
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
    Fixture.markInitialized( table );

    table.getAdapter( ITableAdapter.class ).setLeftOffset( 10 );
    Fixture.preserveWidgets();
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "scrollLeft" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "selection" ) );
  }

  @Test
  public void testRenderSelection() throws IOException {
    table = new Table( shell, SWT.MULTI );
    createTableItems( table, 3 );

    table.setSelection( new int[] { 0, 2 } );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray();
    expected.add( getId( table.getItem( 2 ) ) );
    expected.add( getId( table.getItem( 0 ) ) );
    assertEquals( expected, message.findSetProperty( table, "selection" ) );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    table = new Table( shell, SWT.MULTI );
    createTableItems( table, 3 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setSelection( new int[] { 0, 2 } );
    Fixture.preserveWidgets();
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "selection" ) );
  }

  @Test
  public void testRenderInitialSortDirection() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "sortDirection" ) );
  }

  @Test
  public void testRenderSortDirection() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    table.setSortColumn( column );
    table.setSortDirection( SWT.UP );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "up", message.findSetProperty( table, "sortDirection" ).asString() );
  }

  @Test
  public void testRenderSortDirectionUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setSortColumn( column );
    table.setSortDirection( SWT.UP );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "sortDirection" ) );
  }

  @Test
  public void testRenderInitialSortColumn() throws IOException {
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "sortColumn" ) );
  }

  @Test
  public void testRenderSortColumn() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );

    table.setSortColumn( column );
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( column ), message.findSetProperty( table, "sortColumn" ).asString() );
  }

  @Test
  public void testRenderSortColumnUnchanged() throws IOException {
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setSortColumn( column );
    Fixture.preserveWidgets();
    getLCA( display ).render( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "sortColumn" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( table, "Selection" ) );
    assertNull( message.findListenOperation( table, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    table.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.removeListener( SWT.Selection, listener );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( table, "Selection" ) );
    assertNull( message.findListenOperation( table, "DefaultSelection" ) );
  }

  @Test
  public void testRenderAddDefaultSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( table, "DefaultSelection" ) );
    assertNull( message.findListenOperation( table, "Selection" ) );
  }

  @Test
  public void testRenderRemoveDefaultSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    table.addListener( SWT.DefaultSelection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.removeListener( SWT.DefaultSelection, listener );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( table, "DefaultSelection" ) );
    assertNull( message.findListenOperation( table, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.addListener( SWT.Selection, mock( Listener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( table, "Selection" ) );
  }

  @Test
  public void testRenderInitialAlwaysHideSelection() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "alwaysHideSelection" ) );
  }

  @Test
  public void testRenderAlwaysHideSelection() throws IOException {
    table.setData( Table.ALWAYS_HIDE_SELECTION, Boolean.TRUE );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( table, "alwaysHideSelection" ) );
  }

  @Test
  public void testRenderAlwaysHideSelectionUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setData( Table.ALWAYS_HIDE_SELECTION, Boolean.TRUE );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "alwaysHideSelection" ) );
  }

  @Test
  public void testRenderInitialEnableCellToolTip() throws IOException {
    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertFalse( operation.getProperties().names().contains( "enableCellToolTip" ) );
  }

  @Test
  public void testRenderEnableCellToolTip() throws IOException {
    table.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( table, "enableCellToolTip" ) );
  }

  @Test
  public void testRenderEnableCellToolTipUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "enableCellToolTip" ) );
  }

  @Test
  public void testRenderCellToolTipText() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    getRemoteObject( table ).setHandler( new TableOperationHandler( table ) );
    createTableItems( table, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
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

    String itemId = WidgetUtil.getId( table.getItem( 2 ) );
    fakeCellToolTipRequest( table, itemId, 0 );
    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    String expected = "[" + itemId + ",0]";
    assertEquals( expected, message.findSetProperty( table, "cellToolTipText" ).asString() );
  }

  @Test
  public void testRenderCellToolTipText_resetsText() throws IOException {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    adapter.setCellToolTipText( "foo" );

    lca.renderChanges( table );

    assertNull( adapter.getCellToolTipText() );
  }

  @Test
  public void testRenderCellToolTipText_null() {
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    getRemoteObject( table ).setHandler( new TableOperationHandler( table ) );
    createTableItems( table, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      @Override
      public void getToolTipText( Item item, int columnIndex ) {
        adapter.setCellToolTipText( null );
      }
    } );

    String itemId = WidgetUtil.getId( table.getItem( 2 ) );
    fakeCellToolTipRequest( table, itemId, 0 );
    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "cellToolTipText" ) );
  }

  @Test
  public void testRenderMarkupEnabled() throws IOException {
    table.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findCreateProperty( table, "markupEnabled" ) );
  }

  @Test
  public void testRenderRowTemplate() throws IOException {
    Template rowTemplate = new Template();
    table.setData( RWT.ROW_TEMPLATE, rowTemplate );

    lca.render( table );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findCreateProperty( table, "rowTemplate" ) );
  }

  private static TableColumn[] createTableColumns( Table table, int columns, int style ) {
    TableColumn[] result = new TableColumn[ columns ];
    for( int i = 0; i < columns; i++ ) {
      result[ i ] = new TableColumn( table, style );
      result[ i ].setText( "col_" + i );
    }
    return result;
  }

  private static void createTableItems( Table table, int count ) {
    for( int i = 0; i < count; i++ ) {
      new TableItem( table, SWT.NONE );
    }
  }

  private static void fakeCellToolTipRequest( Table table, String itemId, int column ) {
    Fixture.fakeNewRequest();
    JsonObject parameters = new JsonObject()
      .add( "item", itemId )
      .add( "column", column );
    Fixture.fakeCallOperation( getId( table ), "renderToolTipText", parameters );
  }

  private static boolean isItemVirtual( Table table, int index ) {
    return table.getAdapter( ITableAdapter.class ).isItemVirtual( index );
  }

  private static void fakeSetTopItemIndex( Table table, int index ) {
    Fixture.fakeSetProperty( getId( table ), "topItemIndex", index );
  }

}
