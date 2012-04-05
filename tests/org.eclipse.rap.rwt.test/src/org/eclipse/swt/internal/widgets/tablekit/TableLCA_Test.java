/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH abd others.
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.*;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.internal.widgets.tablekit.TableLCA.ItemMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;


@SuppressWarnings("deprecation")
public class TableLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private TableLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new TableLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( table );
    ControlLCATestUtil.testFocusListener( table );
    ControlLCATestUtil.testMouseListener( table );
    ControlLCATestUtil.testKeyListener( table );
    ControlLCATestUtil.testTraverseListener( table );
    ControlLCATestUtil.testMenuDetectListener( table );
    ControlLCATestUtil.testHelpListener( table );
  }

  public void testPreserveValues() {
    Table table = new Table( shell, SWT.BORDER );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    // control: enabled
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    table.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    table.setEnabled( true );
    // visible
    table.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    table.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( table );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    table.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // bounds
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    table.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    table.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    table.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    table.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // tooltip text
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( null, table.getToolTipText() );
    Fixture.clearPreserved();
    table.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( "some text", table.getToolTipText() );
  }

  public void testWidgetSelectedWithCheck() {
    final SelectionEvent[] events = new SelectionEvent[ 1 ];
    Table table = new Table( shell, SWT.CHECK );
    TableItem item1 = new TableItem( table, SWT.NONE );
    TableItem item2 = new TableItem( table, SWT.NONE );
    table.setSelection( 0 );
    table.addSelectionListener( new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        events[ 0 ] = event;
      }
      public void widgetDefaultSelected( SelectionEvent event ) {
        fail( "unexpected event: widgetDefaultSelected" );
      }
    } );
    // Simulate request that comes in after item2 was checked (but not selected)
    Fixture.fakeNewRequest( display );
    String tableId = WidgetUtil.getId( table );
    String item2Id = WidgetUtil.getId( item2 );
    Fixture.fakeRequestParam( item2Id + ".checked", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, item2Id);
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_DETAIL, "check" );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( item1, table.getSelection()[ 0 ] );
  }

  public void testWidgetDefaultSelected() {
    final SelectionEvent[] events = new SelectionEvent[ 1 ];
    Table table = new Table( shell, SWT.MULTI );
    TableItem item1 = new TableItem( table, SWT.NONE );
    TableItem item2 = new TableItem( table, SWT.NONE );
    table.setSelection( 0 );
    table.addSelectionListener( new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        fail( "unexpected event: widgetSelected" );
      }
      public void widgetDefaultSelected( SelectionEvent event ) {
        events[ 0 ] = event;
      }
    } );
    // Simulate request that comes in after item2 was checked (but not selected)
    Fixture.fakeNewRequest( display );
    String tableId = WidgetUtil.getId( table );
    String item2Id = WidgetUtil.getId( item2 );
    String itemParam = JSConst.EVENT_WIDGET_DEFAULT_SELECTED + ".item";
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, tableId );
    Fixture.fakeRequestParam( itemParam, item2Id );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( item1, table.getSelection()[ 0 ] );
    // Simulate request that comes when <Return> is pressed
    // with focused item is one of the selected
    events[ 0 ] = null;
    table.setSelection( 1 ); // Set focused item
    table.select( 0 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, tableId );
    Fixture.fakeRequestParam( itemParam, item2Id );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 2, table.getSelectionCount() );
    assertEquals( 1, table.getSelectionIndex() );
    // Simulate request that comes when <Return> is pressed
    // with focused item is not one of the selected
    events[ 0 ] = null;
    table.setSelection( 0 ); // Set focused item
    table.deselectAll();
    table.select( 1 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, tableId );
    Fixture.fakeRequestParam( itemParam, item2Id );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( 1, table.getSelectionIndex() );
    // Simulate request that comes when <Return> is pressed
    // and there is no selection
    events[ 0 ] = null;
    table.setSelection( 1 ); // Set focused item
    table.deselectAll();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, tableId );
    Fixture.fakeRequestParam( itemParam, item2Id );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 0, table.getSelectionCount() );
    assertEquals( -1, table.getSelectionIndex() );
  }

  public void testRedraw() {
    final Table[] table = { null };
    shell.setSize( 100, 100 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
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
    Fixture.fakeNewRequest( display );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId  );
    Fixture.executeLifeCycleFromServerThread();
    assertFalse( isItemVirtual( table[ 0 ], 0  ) );
  }

  public void testNoUnwantedResolveItems() {
    shell.setSize( 100, 100 );
    final Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 90, 90 );
    table.setItemCount( 1000 );
    shell.open();
    String tableId = WidgetUtil.getId( table );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_SET_DATA, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_SET_DATA_INDEX, "500,501,502,503" );
    Fixture.fakeRequestParam( tableId + ".topIndex", "500" );
    ILifeCycle lifeCycle = RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void beforePhase( PhaseEvent event ) {
        table.redraw();
      }

      public void afterPhase( PhaseEvent event ) {
      }

      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
    } );
    Fixture.executeLifeCycleFromServerThread();

    assertTrue( isItemVirtual( table, 499 ) );
    assertTrue( isItemVirtual( table, 800 ) );
    assertTrue( isItemVirtual( table, 999 ) );
  }

  public void testClearVirtual() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    shell.setSize( 100, 100 );
    shell.setLayout( new FillLayout() );
    final Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 100 );
    shell.layout();
    shell.open();
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    // precondition: all items are resolved (TableItem#cached == true)
    // resolve all items and ensure
    for( int i = 0; i < table.getItemCount(); i++ ) {
      table.getItem( i ).getText();
    }
    assertFalse( adapter.isItemVirtual( table.getItemCount() - 1 ) );
    //
    final int lastItemIndex = table.getItemCount() - 1;
    // fake one request that would initialize the UI
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    // run actual request
    Fixture.fakeNewRequest( display );
    ILifeCycle lifeCycle = RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( PhaseEvent event ) {
        table.clear( lastItemIndex );
      }
      public void afterPhase( PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
    } );
    Fixture.executeLifeCycleFromServerThread();
    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( table.getItem( lastItemIndex ), "clear" ) );
  }

  public void testSetDataEvent() {
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL );
    Listener listener = new Listener() {
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
    String tableId = WidgetUtil.getId( table );
    // Run test request
    assertTrue( isItemVirtual( table, 500 ) ); // ensure precondition
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".topItemIndex", "500" );
    Fixture.executeLifeCycleFromServerThread();
    // Remove SetData listener to not accidentially resolve item with asserts
    table.removeListener( SWT.SetData, listener );
    // assert request results
    assertFalse( isItemVirtual( table, 500 ) );
    assertFalse( isItemVirtual( table, 502 ) );
    assertTrue( isItemVirtual( table, 510 ) );
    assertEquals( "Item 500", table.getItem( 500 ).getText() );
    assertEquals( "Item 502", table.getItem( 502 ).getText() );
  }

  public void testReadSelection() {
    Table table = new Table( shell, SWT.MULTI );
    String tableId = WidgetUtil.getId( table );
    TableItem item1 = new TableItem( table, SWT.NONE );
    String item1Id = WidgetUtil.getId( item1 );
    TableItem item2 = new TableItem( table, SWT.NONE );
    String item2Id = WidgetUtil.getId( item2 );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".selection", item1Id + "," + item2Id );
    Fixture.executeLifeCycleFromServerThread();

    TableItem[] selectedItems = table.getSelection();
    assertEquals( 2, selectedItems.length );
    assertSame( item1, selectedItems[ 1 ] );
    assertSame( item2, selectedItems[ 0 ] );
  }

  public void testReadSelection_UnresolvedItem() {
    Table table = new Table( shell, SWT.MULTI | SWT.VIRTUAL );
    String tableId = WidgetUtil.getId( table );
    table.setItemCount( 3 );
    TableItem item = table.getItem( 0 );
    item.setText( "Item 1" );
    String itemId = WidgetUtil.getId( item );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".selection", itemId + "," + tableId + "#2" );
    Fixture.executeLifeCycleFromServerThread();

    int[] selectedIndices = table.getSelectionIndices();
    assertEquals( 2, selectedIndices.length );
    assertEquals( 0, selectedIndices[ 1 ] );
    assertEquals( 2, selectedIndices[ 0 ] );
    assertTrue( isItemVirtual( table, 2 ) );
  }

  public void testReadSelectionDisposedItem() {
    Table table = new Table( shell, SWT.MULTI );
    String tableId = WidgetUtil.getId( table );
    TableItem item1 = new TableItem( table, SWT.NONE );
    String item1Id = WidgetUtil.getId( item1 );
    new TableItem( table, SWT.NONE );
    item1.dispose();

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".selection", item1Id );
    Fixture.executeLifeCycleFromServerThread();

    TableItem[] selectedItems = table.getSelection();
    assertEquals( 0, selectedItems.length );
  }


  /*
   * Ensures that checkData calls with an invalid index are silently ignored.
   * This may happen, when the itemCount is reduced during a SetData event.
   * Queued SetData events may then have stale (out-of-bounds) indices.
   * See 235368: [table] [table] ArrayIndexOutOfBoundsException in virtual
   *     TableViewer
   *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=235368
   */
  public void testReduceItemCountInSetData() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        fail( "Must not trigger SetData event" );
      }
    } );

    Fixture.fakePhase( PhaseId.READ_DATA );
    table.setItemCount( 1 );
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    adapter.checkData( 0 );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    table.setItemCount( 0 );
    int eventCount = 0;
    while( ProcessActionRunner.executeNext() ) {
      eventCount++;
    }
    while( SetDataEvent.executeNext() ) {
      eventCount++;
    }
    assertEquals( 1, eventCount );
  }

  public void testGetItemMetrics() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Table table = new Table( shell, SWT.NONE );
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

  public void testGetItemMetricsWithCheckBox() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 200, 200 );
    shell.setLayout( new FillLayout() );
    Table table = new Table( shell, SWT.CHECK );
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

  public void testGetItemMetricsImageCutOffInSecondColumn() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 200, 200 );
    shell.setLayout( new FillLayout() );
    Table table = new Table( shell, SWT.NONE );
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

  public void testGetItemMetricsWithoutColumns() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Table table = new Table( shell, SWT.NONE );
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

  public void testGetItemMetricsWithEmptyTable() {
    Table table = new Table( shell, SWT.NONE );
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

  public void testReadFocusItem() {
    Table table = new Table( shell, SWT.MULTI );
    for( int i = 0; i < 5; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    String tableId = WidgetUtil.getId( table );
    // ensure that reading selection parameter does not override focusIndex
    Fixture.fakeRequestParam( tableId + ".focusItem", indexToId( table, 4 ) );
    String items = indicesToIds( table, new int[]{ 0, 1, 2, 3, 4 } );
    Fixture.fakeRequestParam( tableId + ".selection", items );
    TableLCA tableLCA = new TableLCA();
    tableLCA.readData( table );
    assertEquals( 4, tableAdapter.getFocusIndex() );
  }

  public void testReadUnresolvedFocusItem() {
    Table table = new Table( shell, SWT.MULTI );
    for( int i = 0; i < 5; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    String tableId = WidgetUtil.getId( table );
    // ensure that reading selection parameter does not override focusIndex
    Fixture.fakeRequestParam( tableId + ".focusItem", tableId + "#4" );
    String items = indicesToIds( table, new int[]{ 0, 1, 2, 3, 4 } );
    Fixture.fakeRequestParam( tableId + ".selection", items );
    TableLCA tableLCA = new TableLCA();
    tableLCA.readData( table );
    assertEquals( 4, tableAdapter.getFocusIndex() );
  }

  public void testReadDisposedFocusItem() {
    Table table = new Table( shell, SWT.MULTI );
    for( int i = 0; i < 5; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    String tableId = WidgetUtil.getId( table );
    // ensure that reading selection parameter does not override focusIndex
    String items = indicesToIds( table, new int[]{ 0, 1, 2, 3, 4 } );
    Fixture.fakeRequestParam( tableId + ".selection", items );
    
    Fixture.fakeRequestParam( tableId + ".focusItem", indexToId( table, 4 ) );
    TableLCA tableLCA = new TableLCA();
    table.getItem( 4 ).dispose();
    tableLCA.readData( table );
    
    assertEquals( -1, tableAdapter.getFocusIndex() );
  }

  public void testReadTopIndex() {
    Table table = new Table( shell, SWT.MULTI );
    table.setSize( 485, 485 );
    for( int i = 0; i < 115; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    String tableId = WidgetUtil.getId( table );
    int[] indices = new int[]{
      114,70,71,72,73,74,75,76,77,78,79,80,81,82,83,
      84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,
      99,100,101,102,103,104,105,106,107,108,109,
      110,111,112,113,0
    };
    String items = indicesToIds( table, indices );
    Fixture.fakeRequestParam( tableId + ".topIndex", "0" );
    Fixture.fakeRequestParam( tableId + ".selection", items );
    TableLCA tableLCA = new TableLCA();
    tableLCA.readData( table );
    assertEquals( 0, table.getTopIndex() );
  }

  public void testCellTooltipRequestForMissingCells() {
    Table table = new Table( shell, SWT.NONE );
    createTableItems( table, 3 );
    final StringBuilder log = new StringBuilder();
    ICellToolTipAdapter tableAdapter = table.getAdapter( ICellToolTipAdapter.class );
    tableAdapter.setCellToolTipProvider( new ICellToolTipProvider() {
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
    String itemId = WidgetUtil.getId( table.getItem( 0 ) );
    processCellToolTipRequest( table, itemId, 0 );
    String expected = "[" + itemId + ",0]";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    itemId = WidgetUtil.getId( table.getItem( 2 ) );
    processCellToolTipRequest( table, itemId, 0 );
    expected = "[" + itemId + ",0]";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    processCellToolTipRequest( table, "xyz", 0 );
    assertEquals( "", log.toString() );
    processCellToolTipRequest( table, itemId, 1 );
    assertEquals( "", log.toString() );
    createTableColumns( table, 2 );
    processCellToolTipRequest( table, itemId, 1 );
    expected = "[" + itemId + ",1]";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    processCellToolTipRequest( table, itemId, 2 );
    assertEquals( "", log.toString() );
  }

  public void testScrollbarsSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List<String> log = new ArrayList<String>();
    Table table = new Table( shell, SWT.NONE );
    for( int i = 0; i < 20; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( "scrollbarSelected" );
      }
    };
    table.getHorizontalBar().addSelectionListener( listener );
    Fixture.fakeNewRequest();
    String tableId = WidgetUtil.getId( table );
    Fixture.fakeRequestParam( tableId + ".scrollLeft", "10" );
    Fixture.readDataAndProcessAction( table );
    assertEquals( 1, log.size() );
    assertEquals( 10, table.getHorizontalBar().getSelection() );
    log.clear();
    table.getVerticalBar().addSelectionListener( listener );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( tableId + ".scrollLeft", "10" );
    Fixture.fakeRequestParam( tableId + ".topItemIndex", "10" );
    Fixture.readDataAndProcessAction( table );
    assertEquals( 2, log.size() );
    assertEquals( 10 * table.getItemHeight(), table.getVerticalBar().getSelection());
  }

  public void testSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List<Widget> log = new ArrayList<Widget>();
    Table table = new Table( shell, SWT.NONE );
    String tableId = WidgetUtil.getId( table );
    for( int i = 0; i < 5; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    TableItem item = table.getItem( 3 );
    String itemId = WidgetUtil.getId( item );
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( event.item );
      }
    };
    table.addSelectionListener( listener );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".selection", itemId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, itemId );
    Fixture.readDataAndProcessAction( table );

    assertEquals( 1, log.size() );
    assertSame( item, log.get( 0 ) );
  }

  public void testSelectionEvent_UnresolvedItem() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List<Widget> log = new ArrayList<Widget>();
    Table table = new Table( shell, SWT.VIRTUAL );
    String tableId = WidgetUtil.getId( table );
    table.setItemCount( 3 );
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( event.item );
      }
    };
    table.addSelectionListener( listener );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".selection", tableId + "#2" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, tableId + "#2" );
    Fixture.readDataAndProcessAction( table );

    assertEquals( 1, log.size() );
    assertSame( table.getItem( 2 ), log.get( 0 ) );
  }

  public void testRenderNonNegativeImageWidth() {
    Table table = new Table( shell, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    item.setImage( image );
    column.setWidth( 2 );
    ItemMetrics[] metrics = TableLCA.getItemMetrics( table );
    assertEquals( 1, metrics.length );
    assertEquals( 0, metrics[ 0 ].imageWidth );
  }

  // bug 360152
  public void testReadItemToolTipDoesNotResolveVirtualItems() {
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    ICellToolTipAdapter toolTipAdapter = CellToolTipUtil.getAdapter( table );
    ITableAdapter tableAdapter = table.getAdapter( ITableAdapter.class );
    ICellToolTipProvider toolTipProvider = mock( ICellToolTipProvider.class );
    toolTipAdapter.setCellToolTipProvider( toolTipProvider );
    table.setItemCount( 2 );
    TableItem item = table.getItem( 1 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_CELL_TOOLTIP_REQUESTED, WidgetUtil.getId( table ) );
    Fixture.fakeRequestParam( JSConst.EVENT_CELL_TOOLTIP_DETAILS, WidgetUtil.getId( item ) + ",0" );

    new TableLCA().readData( table );

    verify( toolTipProvider ).getToolTipText( item, 0 );
    assertEquals( 1, tableAdapter.getCreatedItems().length );
  }

  public void testRenderCreate() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.renderInitialization( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertEquals( "rwt.widgets.Tree", operation.getType() );
    assertEquals( "table", operation.getProperty( "appearance" ) );
    assertEquals( Integer.valueOf( 0 ), operation.getProperty( "indentionWidth" ) );
    assertEquals( Integer.valueOf( -1 ), operation.getProperty( "treeColumn" ) );
    assertFalse( operation.getPropertyNames().contains( "checkBoxMetrics" ) );
    assertEquals( Boolean.FALSE, operation.getProperty( "markupEnabled" ) );
  }

  public void testRenderCreateWithFixedColumns() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    table.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );

    lca.renderInitialization( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertEquals( Boolean.TRUE, operation.getProperty( "splitContainer" ) );
  }

  public void testRenderParent() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.renderInitialization( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertEquals( WidgetUtil.getId( table.getParent() ), operation.getParent() );
  }

  public void testRenderCreateWithVirtualNoScrollMulti() throws IOException {
    Table table = new Table( shell, SWT.VIRTUAL | SWT.NO_SCROLL | SWT.MULTI );

    lca.renderInitialization( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "VIRTUAL" ) );
    assertTrue( Arrays.asList( styles ).contains( "NO_SCROLL" ) );
    assertTrue( Arrays.asList( styles ).contains( "MULTI" ) );
  }

  public void testRenderCreateWithHideSelection() throws IOException {
    Table table = new Table( shell, SWT.HIDE_SELECTION );

    lca.renderInitialization( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "HIDE_SELECTION" ) );
  }

  public void testRenderCreateWithCheck() throws IOException, JSONException {
    Table table = new Table( shell, SWT.CHECK );

    lca.renderInitialization( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "CHECK" ) );
    JSONArray actual = ( JSONArray )operation.getProperty( "checkBoxMetrics" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[4,21]", actual ) );
  }

  public void testRenderInitialItemCount() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "itemCount" ) == -1 );
  }

  public void testRenderItemCount() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    table.setItemCount( 10 );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 10 ), message.findSetProperty( table, "itemCount" ) );
  }

  public void testRenderItemCountUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "itemCount" ) );
  }

  public void testRenderInitialItemHeight() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().contains( "itemHeight" ) );
  }

  public void testRenderItemHeight() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Font font = Graphics.getFont( "Arial", 26, SWT.NONE );

    table.setFont( font );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( table, "itemHeight" ) );
  }

  public void testRenderItemHeightUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Font font = Graphics.getFont( "Arial", 26, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setFont( font );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "itemHeight" ) );
  }

  public void testRenderInitialItemMetrics() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().contains( "itemMetrics" ) );
  }

  public void testRenderItemMetrics() throws IOException, JSONException {
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "foo" );

    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( table, "itemMetrics" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,0,26,3,0,3,20]", ( JSONArray )actual.get( 0 ) ) );
  }

  public void testRenderItemMetricsUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( "foo" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "itemMetrics" ) );
  }

  public void testRenderInitialColumnCount() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "columnCount" ) == -1 );
  }

  public void testRenderColumnCount() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    new TableColumn( table, SWT.NONE );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 1 ), message.findSetProperty( table, "columnCount" ) );
  }

  public void testRenderColumnCountUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    new TableColumn( table, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "columnCount" ) );
  }

  public void testRenderInitialFixedColumns() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "fixedColumns" ) == -1 );
  }
  
  public void testRenderFixedColumns() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    
    table.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );
    lca.renderChanges( table );
    
    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 1 ), message.findSetProperty( table, "fixedColumns" ) );
  }

  public void testRenderFixedColumnsUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setData( "fixedColumns", Integer.valueOf( 1 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "fixedColumns" ) );
  }

  public void testRenderInitialHeaderHeight() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "headerHeight" ) == -1 );
  }

  public void testRenderHeaderHeight() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    table.setHeaderVisible( true );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 31 ), message.findSetProperty( table, "headerHeight" ) );
  }

  public void testRenderHeaderHeightUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "headerHeight" ) );
  }

  public void testRenderInitialHeaderVisible() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "headerVisible" ) == -1 );
  }

  public void testRenderHeaderVisible() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    table.setHeaderVisible( true );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( table, "headerVisible" ) );
  }

  public void testRenderHeaderVisibleUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "headerVisible" ) );
  }

  public void testRenderInitialLinesVisible() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "linesVisible" ) == -1 );
  }

  public void testRenderLinesVisible() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    table.setLinesVisible( true );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( table, "linesVisible" ) );
  }

  public void testRenderLinesVisibleUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setLinesVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "linesVisible" ) );
  }

  public void testRenderInitialTopItemIndex() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "topItemIndex" ) == -1 );
  }

  public void testRenderTopItemIndex() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    createTableItems( table, 3 );

    table.setTopIndex( 2 );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 2 ), message.findSetProperty( table, "topItemIndex" ) );
  }

  public void testRenderTopItemIndexUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    createTableItems( table, 3 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setTopIndex( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "topItemIndex" ) );
  }

  public void testRenderInitialFocusItem() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "focusItem" ) == -1 );
  }

  public void testRenderFocusItem() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    createTableItems( table, 2 );
    TableItem item = new TableItem( table, SWT.NONE );

    table.getAdapter( ITableAdapter.class ).setFocusIndex( 2 );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( WidgetUtil.getId( item ), message.findSetProperty( table, "focusItem" ) );
  }

  public void testRenderFocusItemUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    createTableItems( table, 3 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.getAdapter( ITableAdapter.class ).setFocusIndex( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "focusItem" ) );
  }

  public void testRenderInitialScrollLeft() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "scrollLeft" ) == -1 );
  }

  public void testRenderScrollLeft() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    table.getAdapter( ITableAdapter.class ).setLeftOffset( 10 );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 10 ), message.findSetProperty( table, "scrollLeft" ) );
  }

  public void testRenderScrollLeftUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.getAdapter( ITableAdapter.class ).setLeftOffset( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "scrollLeft" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException, JSONException {
    Table table = new Table( shell, SWT.MULTI );
    createTableItems( table, 3 );

    table.setSelection( new int[] { 0, 2 } );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( table, "selection" );
    StringBuilder expected = new StringBuilder();
    expected.append( "[" );
    expected.append( WidgetUtil.getId( table.getItem( 2 ) ) );
    expected.append( "," );
    expected.append( WidgetUtil.getId( table.getItem( 0 ) ) );
    expected.append( "]" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected.toString(), actual ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    Table table = new Table( shell, SWT.MULTI );
    createTableItems( table, 3 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setSelection( new int[] { 0, 2 } );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "selection" ) );
  }

  public void testRenderInitialSortDirection() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "sortDirection" ) == -1 );
  }

  public void testRenderSortDirection() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );

    table.setSortColumn( column );
    table.setSortDirection( SWT.UP );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "up", message.findSetProperty( table, "sortDirection" ) );
  }

  public void testRenderSortDirectionUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setSortColumn( column );
    table.setSortDirection( SWT.UP );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "sortDirection" ) );
  }

  public void testRenderInitialSortColumn() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "sortColumn" ) == -1 );
  }

  public void testRenderSortColumn() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );

    table.setSortColumn( column );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( WidgetUtil.getId( column ), message.findSetProperty( table, "sortColumn" ) );
  }

  public void testRenderSortColumnUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setSortColumn( column );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "sortColumn" ) );
  }

  public void testRenderAddScrollBarsSelectionListener_Horizontal() throws Exception {
    Table table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.getHorizontalBar().addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( table, "scrollBarsSelection" ) );
  }

  public void testRenderRemoveScrollBarsSelectionListener_Horizontal() throws Exception {
    Table table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    SelectionListener listener = new SelectionAdapter() { };
    table.getHorizontalBar().addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.getHorizontalBar().removeSelectionListener( listener );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( table, "scrollBarsSelection" ) );
  }

  public void testRenderScrollBarsSelectionListenerUnchanged_Horizontal() throws Exception {
    Table table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.getHorizontalBar().addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( table, "scrollBarsSelection" ) );
  }

  public void testRenderAddScrollBarsSelectionListener_Vertical() throws Exception {
    Table table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.getVerticalBar().addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( table, "scrollBarsSelection" ) );
  }

  public void testRenderRemoveScrollBarsSelectionListener_Vertical() throws Exception {
    Table table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    SelectionListener listener = new SelectionAdapter() { };
    table.getVerticalBar().addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.getVerticalBar().removeSelectionListener( listener );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( table, "scrollBarsSelection" ) );
  }

  public void testRenderScrollBarsSelectionListenerUnchanged_Vertical() throws Exception {
    Table table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.getVerticalBar().addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( table, "scrollBarsSelection" ) );
  }

  public void testRenderInitialScrollBarsVisible() throws IOException {
    Table table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "scrollBarsVisible" ) == -1 );
  }

  public void testRenderScrollBarsVisible_Horizontal() throws IOException, JSONException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Table table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    TableColumn column = new TableColumn( table, SWT.NONE );

    column.setWidth( 25 );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( table, "scrollBarsVisible" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ true, false ]", actual ) );
  }

  public void testRenderScrollBarsVisible_Vertical() throws IOException, JSONException {
    Table table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    new TableColumn( table, SWT.NONE );

    table.setHeaderVisible( true );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( table, "scrollBarsVisible" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[ false, true ]", actual ) );
  }

  public void testRenderScrollBarsVisibleUnchanged() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Table table = new Table( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    TableColumn column = new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    column.setWidth( 25 );
    table.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "scrollBarsVisible" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( table, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Table table = new Table( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() { };
    table.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.removeSelectionListener( listener );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( table, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    Fixture.preserveWidgets();

    table.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( table, "selection" ) );
  }

  public void testRenderInitialAlwaysHideSelection() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "alwaysHideSelection" ) == -1 );
  }

  public void testRenderAlwaysHideSelection() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    table.setData( Table.ALWAYS_HIDE_SELECTION, Boolean.TRUE );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( table, "alwaysHideSelection" ) );
  }

  public void testRenderAlwaysHideSelectionUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setData( Table.ALWAYS_HIDE_SELECTION, Boolean.TRUE );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "alwaysHideSelection" ) );
  }

  public void testRenderInitialEnableCellToolTip() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( table );
    assertTrue( operation.getPropertyNames().indexOf( "enableCellToolTip" ) == -1 );
  }

  public void testRenderEnableCellToolTip() throws IOException {
    Table table = new Table( shell, SWT.NONE );

    table.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( table, "enableCellToolTip" ) );
  }

  public void testRenderEnableCellToolTipUnchanged() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );

    table.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    Fixture.preserveWidgets();
    lca.renderChanges( table );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "enableCellToolTip" ) );
  }

  public void testRenderCellToolTipText() {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    createTableItems( table, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
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

    String itemId = WidgetUtil.getId( table.getItem( 2 ) );
    processCellToolTipRequest( table, itemId, 0 );

    Message message = Fixture.getProtocolMessage();
    String expected = "[" + itemId + ",0]";
    assertEquals( expected, message.findSetProperty( table, "cellToolTipText" ) );
  }

  public void testRenderCellToolTipTextNull() {
    Table table = new Table( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( table );
    createTableItems( table, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( Item item, int columnIndex ) {
        adapter.setCellToolTipText( null );
      }
    } );

    String itemId = WidgetUtil.getId( table.getItem( 2 ) );
    processCellToolTipRequest( table, itemId, 0 );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( table, "cellToolTipText" ) );
  }

  public void testRenderMarkupEnabled() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    table.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    lca.render( table );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findCreateProperty( table, "markupEnabled" ) );
  }

  private static void createTableColumns( Table table, int count ) {
    for( int i = 0; i < count; i++ ) {
      new TableColumn( table, SWT.NONE );
    }
  }

  private static void createTableItems( Table table, int count ) {
    for( int i = 0; i < count; i++ ) {
      new TableItem( table, SWT.NONE );
    }
  }

  private static void processCellToolTipRequest( Table table, String itemId, int column ) {
    Fixture.fakeNewRequest( table.getDisplay() );
    String tableId = WidgetUtil.getId( table );
    Fixture.fakeRequestParam( JSConst.EVENT_CELL_TOOLTIP_REQUESTED, tableId );
    String cellString = itemId + "," + column;
    Fixture.fakeRequestParam( JSConst.EVENT_CELL_TOOLTIP_DETAILS, cellString );
    Fixture.executeLifeCycleFromServerThread();
  }

  private static boolean isItemVirtual( Table table, int index ) {
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    return tableAdapter.isItemVirtual( index );
  }

  private static String indicesToIds( Table table, int[] indices ) {
    String items = new String();
    for( int i = 0; i < indices.length; i++ ) {
      items += indexToId( table, indices[ i ] );
      if( i != indices.length - 1 ) {
        items += ",";
      }
    }
    return items;
  }

  private static String indexToId( Table table, int index ) {
    return WidgetUtil.getId( table.getItem( index ) );
  }

}
