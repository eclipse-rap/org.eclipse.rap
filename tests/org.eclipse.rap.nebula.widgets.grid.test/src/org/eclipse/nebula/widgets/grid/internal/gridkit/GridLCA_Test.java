/*******************************************************************************
 * Copyright (c) 2012, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal.gridkit;

import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridColumns;
import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridItems;
import static org.eclipse.nebula.widgets.grid.GridTestUtil.loadImage;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.internal.IGridAdapter;
import org.eclipse.nebula.widgets.grid.internal.gridkit.GridLCA.ItemMetrics;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.CellToolTipUtil;
import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class GridLCA_Test {

  private Display display;
  private Shell shell;
  private Grid grid;
  private GridLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    grid = new Grid( shell, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
    lca = ( GridLCA )WidgetUtil.getLCA( grid );
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertEquals( "rwt.widgets.Grid", operation.getType() );
    assertEquals( "grid", operation.getProperties().get( "appearance" ).asString() );
    assertFalse( operation.getProperties().names().contains( "checkBoxMetrics" ) );
    assertTrue( getStyles( operation ).contains( "FULL_SELECTION" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertEquals( getId( grid.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderCreateWithVirtualMulti() throws IOException {
    grid = new Grid( shell, SWT.VIRTUAL | SWT.MULTI );

    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    List<String> styles = getStyles( operation );
    assertTrue( styles.contains( "VIRTUAL" ) );
    assertTrue( styles.contains( "MULTI" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( grid, "SetData" ) );
  }

  @Test
  public void testRenderCreate_rendersSplitContainer() throws IOException {
    lca.renderInitialization( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "splitContainer" ) );
  }

  @Test
  public void testRenderCreate_doesNotRenderSplitContainerWithRowTemplate() throws IOException {
    grid.setData( RWT.ROW_TEMPLATE, new Template() );

    lca.renderInitialization( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "splitContainer" ) == -1 );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( grid );

    lca.renderInitialization( grid );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof GridOperationHandler );
  }

  @Test
  public void testRenderInitialization_rendersRowTemplate() throws IOException {
    grid.setData( RWT.ROW_TEMPLATE, new Template() );

    lca.renderInitialization( grid );

    TestMessage message = Fixture.getProtocolMessage();

    assertNotNull( message.findCreateProperty( grid, "rowTemplate" ) );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    GridOperationHandler handler = spy( new GridOperationHandler( grid ) );
    getRemoteObject( getId( grid ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( grid ), "Help", new JsonObject() );
    lca.readData( grid );

    verify( handler ).handleNotifyHelp( grid, new JsonObject() );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( grid );

    TestMessage message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( getId( grid ), operation.getTarget() );
  }

  @Test
  public void testRenderInitialItemCount() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "itemCount" ) == -1 );
  }

  @Test
  public void testRenderItemCount() throws IOException {
    createGridItems( grid, 10, 3 );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( grid, "itemCount" ).asInt() );
  }

  @Test
  public void testRenderItemCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "itemCount" ) );
  }

  @Test
  public void testRenderInitialItemHeight() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( grid, "itemHeight" ) );
  }

  @Test
  public void testRenderItemHeight() throws IOException {
    grid.setItemHeight( 40 );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 40, message.findSetProperty( grid, "itemHeight" ).asInt() );
  }

  @Test
  public void testRenderItemHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setItemHeight( 40 );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "itemHeight" ) );
  }

  @Test
  public void testRenderInitialItemMetrics() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( grid, "itemMetrics" ) );
  }

  @Test
  public void testRenderItemMetrics() throws IOException {
    grid.setRowHeaderVisible( true, 10 );
    GridColumn column = new GridColumn( grid, SWT.NONE );
    column.setWidth( 50 );
    GridItem[] items = createGridItems( grid, 3, 1 );
    items[ 0 ].setHeaderText( "bar" );
    items[ 0 ].setText( "foo" );

    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = message.findSetProperty( grid, "itemMetrics" ).asArray();
    assertEquals( JsonArray.readFrom( "[0, 0, 10, 6, 0, 6, 0, 0, 0]" ), actual.get( 0 ) );
    assertEquals( JsonArray.readFrom( "[1, 10, 50, 10, 0, 10, 44, 10, 0]" ), actual.get( 1 ) );
  }

  @Test
  public void testRenderItemMetrics_WithCheck() throws IOException {
    createGridColumns( grid, 2, SWT.CHECK );
    GridItem[] items = createGridItems( grid, 3, 1 );
    items[ 0 ].setText( "foo" );

    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = message.findSetProperty( grid, "itemMetrics" ).asArray();
    assertEquals( JsonArray.readFrom( "[0, 0, 0, 6, 0, 6, 0, 0, 0]" ), actual.get( 0 ) );
    assertEquals( JsonArray.readFrom( "[1, 0, 20, 23, 0, 23, 0, 0, 21]" ), actual.get( 1 ) );
    assertEquals( JsonArray.readFrom( "[2, 20, 40, 49, 0, 49, 5, 26, 21]" ), actual.get( 2 ) );
  }

  @Test
  public void testRenderItemMetricsUnchanged() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );
    GridItem item = new GridItem( grid, SWT.NONE );
    item.setText( "foo" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "itemMetrics" ) );
  }

  @Test
  public void testRenderInitialColumnCount() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "columnCount" ) == -1 );
  }

  @Test
  public void testRenderColumnCount() throws IOException {
    new GridColumn( grid, SWT.NONE );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( grid, "columnCount" ).asInt() );
  }

  @Test
  public void testRenderColumnCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    new GridColumn( grid, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "columnCount" ) );
  }

  @Test
  public void testRenderInitialColumnOrder() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "columnOrder" ) == -1 );
  }

  @Test
  public void testRenderColumnOrder() throws IOException {
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    grid.setColumnOrder( new int[] { 2, 0, 1 } );

    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    GridColumn rowHeadersColumn = grid.getAdapter( IGridAdapter.class ).getRowHeadersColumn();
    JsonArray expected = new JsonArray()
      .add( getId( rowHeadersColumn ) )
      .add( getId( columns[ 2 ] ) )
      .add( getId( columns[ 0 ] ) )
      .add( getId( columns[ 1 ] ) );
    assertEquals( expected, message.findSetProperty( grid, "columnOrder" ) );
  }

  @Test
  public void testRenderColumnOrderUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );
    createGridColumns( grid, 3, SWT.NONE );
    grid.setColumnOrder( new int[] { 2, 0, 1 } );

    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "columnOrder" ) );
  }

  @Test
  public void testRenderInitialTreeColumn() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "treeColumn" ) == -1 );
  }

  @Test
  public void testRenderTreeColumn() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    grid.setColumnOrder( new int[]{ 1, 0 } );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( grid, "treeColumn" ).asInt() );
  }

  @Test
  public void testRenderTreeColumnUnchanged() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setColumnOrder( new int[]{ 1, 0 } );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "treeColumn" ) );
  }

  @Test
  public void testRenderInitialHeaderHeight() throws IOException {
    new GridColumn( grid, SWT.NONE );

    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "headerHeight" ) == -1 );
  }

  @Test
  public void testRenderHeaderHeight() throws IOException {
    new GridColumn( grid, SWT.NONE );
    grid.setHeaderVisible( true );

    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 31, message.findSetProperty( grid, "headerHeight" ).asInt() );
  }

  @Test
  public void testRenderHeaderHeightUnchanged() throws IOException {
    new GridColumn( grid, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "headerHeight" ) );
  }

  @Test
  public void testRenderInitialHeaderVisible() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "headerVisible" ) == -1 );
  }

  @Test
  public void testRenderHeaderVisible() throws IOException {
    grid.setHeaderVisible( true );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( grid, "headerVisible" ) );
  }

  @Test
  public void testRenderHeaderVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setHeaderVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "headerVisible" ) );
  }

  @Test
  public void testRenderInitialFooterHeight() throws IOException {
    new GridColumn( grid, SWT.NONE );

    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "footerHeight" ) == -1 );
  }

  @Test
  public void testRenderFooterHeight() throws IOException {
    new GridColumn( grid, SWT.NONE );
    grid.setFooterVisible( true );

    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 31, message.findSetProperty( grid, "footerHeight" ).asInt() );
  }

  @Test
  public void testRenderFooterHeightUnchanged() throws IOException {
    new GridColumn( grid, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setFooterVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "footerHeight" ) );
  }

  @Test
  public void testRenderInitialFooterVisible() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "footerVisible" ) == -1 );
  }

  @Test
  public void testRenderFooterVisible() throws IOException {
    grid.setFooterVisible( true );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( grid, "footerVisible" ) );
  }

  @Test
  public void testRenderFooterVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setFooterVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "footerVisible" ) );
  }

  @Test
  public void testRenderInitialLinesVisible() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( grid, "linesVisible" ) );
  }

  @Test
  public void testRenderLinesVisible() throws IOException {
    Fixture.markInitialized( grid );
    grid.setLinesVisible( false );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( grid, "linesVisible" ) );
  }

  @Test
  public void testRenderLinesVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setLinesVisible( true );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "linesVisible" ) );
  }

  @Test
  public void testRenderInitialAutoHeight() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "autoHeight" ) == -1 );
  }

  @Test
  public void testRenderAutoHeight() throws IOException {
    Fixture.markInitialized( grid );
    grid.setAutoHeight( true );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( grid, "autoHeight" ) );
  }

  @Test
  public void testRenderAutoHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setAutoHeight( true );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "autoHeight" ) );
  }

  @Test
  public void testRenderInitialIndentionWidth_gridAsTable() throws IOException {
    createGridItems( grid, 3, 0 );

    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "indentionWidth" ) == -1 );
  }

  @Test
  public void testRenderInitialIndentionWidth_gridAsTree() throws IOException {
    createGridItems( grid, 3, 3 );

    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 16, message.findSetProperty( grid, "indentionWidth" ).asInt() );
  }

  @Test
  public void testRenderIndentionWidth() throws IOException {
    Fixture.markInitialized( grid );

    createGridItems( grid, 3, 3 );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 16, message.findSetProperty( grid, "indentionWidth" ).asInt() );
  }

  @Test
  public void testRenderIndentionWidthUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    createGridItems( grid, 3, 3 );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "indentionWidth" ) );
  }


  @Test
  public void testRenderInitialTopItemIndex() throws IOException {
    grid.setSize( 100, 100 );

    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "topItemIndex" ) == -1 );
  }

  @Test
  public void testRenderTopItemIndex() throws IOException {
    grid.setSize( 100, 100 );
    createGridItems( grid, 10, 3 );
    grid.getItem( 4 ).setExpanded( true );

    grid.setTopIndex( 5 );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( grid, "topItemIndex" ).asInt() );
  }

  @Test
  public void testRenderTopItemIndexUnchanged() throws IOException {
    grid.setSize( 100, 100 );
    createGridItems( grid, 10, 3 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setTopIndex( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "topItemIndex" ) );
  }

  @Test
  public void testRenderInitialFocusItem() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "focusItem" ) == -1 );
  }

  @Test
  public void testRenderFocusItem() throws IOException {
    GridItem[] items = createGridItems( grid, 3, 0 );

    grid.setFocusItem( items[ 1 ] );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( items[ 1 ] ), message.findSetProperty( grid, "focusItem" ).asString() );
  }

  @Test
  public void testRenderFocusItemUnchanged() throws IOException {
    GridItem[] items = createGridItems( grid, 3, 0 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setFocusItem( items[ 1 ] );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "focusItem" ) );
  }

  @Test
  public void testRenderInitialFocusColumn() throws IOException {
    grid.setCellSelectionEnabled( true );
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "focusCell" ) == -1 );
  }

  @Test
  public void testRenderFocusColumn() throws IOException {
    grid.setCellSelectionEnabled( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );

    grid.setFocusColumn( columns[ 1 ] );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    int expected = grid.indexOf( columns[ 1 ] ) + 1;
    assertEquals( expected, message.findSetProperty( grid, "focusCell" ).asInt() );
  }

  @Test
  public void testRenderFocusColumnUnchanged() throws IOException {
    grid.setCellSelectionEnabled( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setFocusColumn( columns[ 1 ] );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "focusCell" ) );
  }

  @Test
  public void testRenderInitialScrollLeft() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "scrollLeft" ) == -1 );
  }

  @Test
  public void testRenderScrollLeft() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );
    grid.getHorizontalBar().setSelection( 10 );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( grid, "scrollLeft" ).asInt() );
  }

  @Test
  public void testRenderScrollLeftUnchanged() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.getHorizontalBar().setSelection( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "scrollLeft" ) );
  }

  @Test
  public void testRenderInitialSelectionType() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "selectionType" ) == -1 );
  }

  @Test
  public void testRenderSelectionType() throws IOException {
    grid.setCellSelectionEnabled( true );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "MULTI", message.findSetProperty( grid, "selectionType" ).asString() );
  }

  @Test
  public void testRenderSelectionTypeUnchanged() throws IOException {
    grid = new Grid( shell, SWT.MULTI );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setCellSelectionEnabled( true );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "selectionType" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "selection" ) == -1 );
  }

  @Test
  public void testRenderSelection() throws IOException {
    GridItem[] items = createGridItems( grid, 3, 3 );

    grid.setSelection( new int[] { 0, 4 } );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    Object expected = new JsonArray()
      .add( getId( items[ 0 ] ) )
      .add( getId( items[ 4 ] ) );
    assertEquals( expected, message.findSetProperty( grid, "selection" ) );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    createGridItems( grid, 3, 3 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setSelection( new int[] { 0, 4 } );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "selection" ) );
  }

  @Test
  public void testRenderInitialCellSelection() throws IOException {
    grid.setCellSelectionEnabled( true );

    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "cellSelection" ) == -1 );
  }

  @Test
  public void testRenderCellSelection() throws IOException {
    grid.setCellSelectionEnabled( true );
    createGridColumns( grid, 3, SWT.NONE );
    createGridItems( grid, 3, 3 );

    grid.setSelection( new int[] { 0, 4 } );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    String item0Id = getId( grid.getItem( 0 ) );
    String item4Id = getId( grid.getItem( 4 ) );
    Object expected = new JsonArray()
      .add( item0Id + "#1" )
      .add( item0Id + "#2" )
      .add( item0Id + "#3" )
      .add( item4Id + "#1")
      .add( item4Id + "#2" )
      .add( item4Id + "#3" );
    assertEquals( expected, message.findSetProperty( grid, "cellSelection" ) );
  }

  @Test
  public void testRenderCellSelectionUnchanged() throws IOException {
    grid.setCellSelectionEnabled( true );
    createGridColumns( grid, 3, SWT.NONE );
    createGridItems( grid, 3, 3 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setSelection( new int[] { 0, 4 } );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "cellSelection" ) );
  }

  @Test
  public void testRenderInitialCellSelectionEnabled() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "cellSelectionEnabled" ) == -1 );
  }

  @Test
  public void testRenderCellSelectionEnabled() throws IOException {
    Fixture.markInitialized( grid );
    grid.setCellSelectionEnabled( true );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( grid, "cellSelectionEnabled" ) );
  }

  @Test
  public void testRenderCellSelectionEnabledUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    grid.setCellSelectionEnabled( true );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "cellSelectionEnabled" ) );
  }

  @Test
  public void testRenderInitialSortDirection() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "sortDirection" ) == -1 );
  }

  @Test
  public void testRenderSortDirection() throws IOException {
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );

    columns[ 1 ].setSort( SWT.UP );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "up", message.findSetProperty( grid, "sortDirection" ).asString() );
  }

  @Test
  public void testRenderSortDirectionUnchanged() throws IOException {
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    columns[ 1 ].setSort( SWT.UP );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "sortDirection" ) );
  }

  @Test
  public void testRenderInitialSortColumn() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "sortColumn" ) == -1 );
  }

  @Test
  public void testRenderSortColumn() throws IOException {
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );

    columns[ 1 ].setSort( SWT.UP );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( getId( columns[ 1 ] ), message.findSetProperty( grid, "sortColumn" ).asString() );
  }

  @Test
  public void testRenderSortColumnUnchanged() throws IOException {
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    columns[ 1 ].setSort( SWT.UP );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "sortColumn" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( grid );
    Fixture.clearPreserved();

    grid.addSelectionListener( mock( SelectionListener.class ) );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( grid, "Selection" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( grid, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    SelectionListener listener = mock( SelectionListener.class );
    grid.addSelectionListener( listener );
    Fixture.markInitialized( grid );
    Fixture.clearPreserved();

    grid.removeSelectionListener( listener );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( grid, "Selection" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( grid, "DefaultSelection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( grid );
    Fixture.clearPreserved();

    grid.addSelectionListener( mock( SelectionListener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( grid, "Selection" ) );
    assertNull( message.findListenOperation( grid, "DefaultSelection" ) );
  }

  @Test
  public void testRenderInitialEnableCellToolTip() throws IOException {
    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( grid );
    assertTrue( operation.getProperties().names().indexOf( "enableCellToolTip" ) == -1 );
  }

  @Test
  public void testRenderEnableCellToolTip() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );
    GridItem item = new GridItem( grid, SWT.NONE );

    item.setToolTipText( 1, "foo" );
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( grid, "enableCellToolTip" ) );
  }

  @Test
  public void testRenderEnableCellToolTipUnchanged() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );
    GridItem item = new GridItem( grid, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    item.setToolTipText( 1, "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "enableCellToolTip" ) );
  }

  @Test
  public void testRenderCellToolTipText() {
    getRemoteObject( grid ).setHandler( new GridOperationHandler( grid ) );
    createGridColumns( grid, 3, SWT.NONE );
    GridItem item = new GridItem( grid, SWT.NONE );
    item.setToolTipText( 1, "foo" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );

    String itemId = getId( item );
    processCellToolTipRequest( grid, itemId, 2 );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( grid, "cellToolTipText" ).asString() );
  }

  @Test
  public void testRenderCellToolTipText_resetsText() throws IOException {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( grid );
    adapter.setCellToolTipText( "foo" );

    lca.renderChanges( grid );

    assertNull( adapter.getCellToolTipText() );
  }

  @Test
  public void testRenderCellToolTipTextNull() {
    getRemoteObject( grid ).setHandler( new GridOperationHandler( grid ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );
    createGridItems( grid, 5, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( grid );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      @Override
      public void getToolTipText( Item item, int columnIndex ) {
        adapter.setCellToolTipText( null );
      }
    } );

    String itemId = WidgetUtil.getId( grid.getItem( 2 ) );
    processCellToolTipRequest( grid, itemId, 0 );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( grid, "cellToolTipText" ) );
  }

  @Test
  public void testGetItemMetrics_CellLeft() {
    GridColumn[] columns = createGridColumns( grid, 2, SWT.NONE );
    columns[ 0 ].setWidth( 100 );
    columns[ 1 ].setWidth( 150 );

    ItemMetrics[] metrics = GridLCA.getItemMetrics( grid );

    assertEquals( 0, metrics[ 0 ].left );
    assertEquals( 0, metrics[ 1 ].left );
    assertEquals( 100, metrics[ 2 ].left );
  }

  @Test
  public void testGetItemMetrics_CellLeftWithRowHeader() {
    grid.setRowHeaderVisible( true, 10 );
    GridColumn[] columns = createGridColumns( grid, 2, SWT.NONE );
    columns[ 0 ].setWidth( 100 );
    columns[ 1 ].setWidth( 150 );

    ItemMetrics[] metrics = GridLCA.getItemMetrics( grid );

    assertEquals( 0, metrics[ 0 ].left );
    assertEquals( 10, metrics[ 1 ].left );
    assertEquals( 110, metrics[ 2 ].left );
  }

  @Test
  public void testGetItemMetrics_CellWidth() {
    grid.setRowHeaderVisible( true, 10 );
    GridColumn[] columns = createGridColumns( grid, 2, SWT.NONE );
    columns[ 0 ].setWidth( 100 );
    columns[ 1 ].setWidth( 150 );

    ItemMetrics[] metrics = GridLCA.getItemMetrics( grid );

    assertEquals( 10, metrics[ 0 ].width );
    assertEquals( 100, metrics[ 1 ].width );
    assertEquals( 150, metrics[ 2 ].width );
  }

  @Test
  public void testGetItemMetrics_ImageLeft() {
    Image image1 = loadImage( display, Fixture.IMAGE_100x50 );
    Image image2 = loadImage( display, Fixture.IMAGE_50x100 );
    GridColumn[] columns = createGridColumns( grid, 2, SWT.NONE );
    columns[ 0 ].setWidth( 100 );
    columns[ 1 ].setWidth( 150 );
    GridItem[] items = createGridItems( grid, 3, 1 );

    ItemMetrics[] metrics = GridLCA.getItemMetrics( grid );
    assertEquals( 0, metrics[ 1 ].imageLeft );
    assertEquals( 106, metrics[ 2 ].imageLeft );

    items[ 1 ].setImage( image2 );
    items[ 0 ].setImage( 1, image1 );

    metrics = GridLCA.getItemMetrics( grid );
    assertEquals( 0, metrics[ 1 ].imageLeft );
    assertEquals( 106, metrics[ 2 ].imageLeft );
  }

  @Test
  public void testGetItemMetrics_ImageWidth() {
    Image image1 = loadImage( display, Fixture.IMAGE_100x50 );
    Image image2 = loadImage( display, Fixture.IMAGE_50x100 );
    GridColumn[] columns = createGridColumns( grid, 2, SWT.NONE );
    columns[ 0 ].setWidth( 100 );
    columns[ 1 ].setWidth( 150 );
    GridItem[] items = createGridItems( grid, 3, 1 );

    ItemMetrics[] metrics = GridLCA.getItemMetrics( grid );
    assertEquals( 0, metrics[ 0 ].imageWidth );

    items[ 1 ].setImage( image2 );
    items[ 0 ].setImage( image1 );

    metrics = GridLCA.getItemMetrics( grid );
    assertEquals( 50, metrics[ 1 ].imageWidth );

    items[ 1 ].setImage( null );
    items[ 0 ].setImage( null );

    metrics = GridLCA.getItemMetrics( grid );
    assertEquals( 0, metrics[ 1 ].imageWidth );
  }

  @Test
  public void testGetItemMetrics_TextLeftWithImage() {
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    GridColumn[] columns = createGridColumns( grid, 2, SWT.NONE );
    columns[ 0 ].setWidth( 100 );
    columns[ 1 ].setWidth( 150 );
    GridItem[] items = createGridItems( grid, 3, 1 );

    ItemMetrics[] metrics = GridLCA.getItemMetrics( grid );
    assertEquals( 106, metrics[ 2 ].textLeft );

    items[ 0 ].setImage( 1, image );

    metrics = GridLCA.getItemMetrics( grid );
    assertEquals( 206, metrics[ 2 ].textLeft );
  }

  @Test
  public void testGetItemMetrics_TextLeftWithCheckbox() {
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    grid = new Grid( shell, SWT.CHECK );
    GridColumn[] columns = createGridColumns( grid, 1, SWT.NONE );
    columns[ 0 ].setWidth( 200 );
    GridItem[] items = createGridItems( grid, 3, 1 );
    items[ 0 ].setText( "item" );
    items[ 0 ].setImage( image );

    ItemMetrics[] metrics = GridLCA.getItemMetrics( grid );

    assertEquals( 123, metrics[ 1 ].textLeft );
  }

  @Test
  public void testGetItemMetrics_TextWidthWithCheckbox() {
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    grid = new Grid( shell, SWT.CHECK );
    GridColumn[] columns = createGridColumns( grid, 1, SWT.NONE );
    columns[ 0 ].setWidth( 200 );
    GridItem[] items = createGridItems( grid, 3, 1 );
    items[ 0 ].setText( "item" );
    items[ 0 ].setImage( image );

    ItemMetrics[] metrics = GridLCA.getItemMetrics( grid );

    assertEquals( 71, metrics[ 1 ].textWidth );
  }

  @Test
  public void testRenderMarkupEnabled() throws IOException {
    grid.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    lca.render( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findCreateProperty( grid, "markupEnabled" ) );
  }

  @Test
  public void testRenderAddExpandListener() throws Exception {
    lca.renderInitialization( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( grid, "Expand" ) );
  }

  @Test
  public void testRenderAddCollapseListener() throws Exception {
    lca.renderInitialization( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( grid, "Collapse" ) );
  }

  @Test
  public void testDontRenderSetDataListenerTwice() throws Exception {
    grid = new Grid( shell, SWT.VIRTUAL | SWT.MULTI );
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );
    Fixture.preserveWidgets();

    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( grid, "SetData" ) );
  }

  @Test
  public void testDontRenderSetDataWithoutVirtual() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( grid );
    Fixture.preserveWidgets();

    lca.renderChanges( grid );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( grid, "SetData" ) );
  }

  //////////////////
  // Helping methods

  private static void processCellToolTipRequest( Grid grid, String itemId, int column ) {
    Fixture.fakeNewRequest();
    JsonObject parameters = new JsonObject()
      .add( "item", itemId )
      .add( "column", column );
    Fixture.fakeCallOperation( getId( grid ), "renderToolTipText", parameters );
    Fixture.executeLifeCycleFromServerThread();
  }

}
