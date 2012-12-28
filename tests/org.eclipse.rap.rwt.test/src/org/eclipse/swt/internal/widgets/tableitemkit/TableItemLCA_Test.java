/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tableitemkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class TableItemLCA_Test {

  private Display display;
  private Shell shell;
  private Table table;
  private TableItemLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    table = new Table( shell, SWT.NONE );
    lca = new TableItemLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testPreserveValues() {
    Table table = new Table( shell, SWT.BORDER );
    new TableColumn( table, SWT.CENTER );
    new TableColumn( table, SWT.CENTER );
    new TableColumn( table, SWT.CENTER );
    TableItem item1 = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( item1 );
    Image[] images1 = TableItemLCA.getImages( item1 );
    Image[] images2 = ( Image[] )adapter.getPreserved( TableItemLCA.PROP_IMAGES );
    assertEquals( images1[ 0 ], images2[ 0 ] );
    assertEquals( images1[ 1 ], images2[ 1 ] );
    assertEquals( images1[ 2 ], images2[ 2 ] );
    assertNull( adapter.getPreserved( TableItemLCA.PROP_BACKGROUND ) );
    assertNull( adapter.getPreserved( TableItemLCA.PROP_FOREGROUND ) );
    assertNull( adapter.getPreserved( TableItemLCA.PROP_FONT ) );
    Color[] preservedCellBackgrounds
      = ( Color[] )adapter.getPreserved( TableItemLCA.PROP_CELL_BACKGROUNDS );
    assertNull( preservedCellBackgrounds[ 0 ] );
    assertNull( preservedCellBackgrounds[ 1 ] );
    assertNull( preservedCellBackgrounds[ 2 ] );
    Color[] preservedCellForegrounds
      = ( Color[] )adapter.getPreserved( TableItemLCA.PROP_CELL_FOREGROUNDS );
    assertNull( preservedCellForegrounds[ 0 ] );
    assertNull( preservedCellForegrounds[ 1 ] );
    assertNull( preservedCellForegrounds[ 2 ] );
    Font[] preservedCellFonts = ( Font[] )adapter.getPreserved( TableItemLCA.PROP_CELL_FONTS );
    assertNull( preservedCellFonts[ 0 ] );
    assertNull( preservedCellFonts[ 1 ] );
    assertNull( preservedCellFonts[ 2 ] );
    Fixture.clearPreserved();
    item1.setText( 0, "item11" );
    item1.setText( 1, "item12" );
    item1.setText( 2, "item13" );
    Font font1 = Graphics.getFont( "font1", 10, 1 );
    item1.setFont( 0, font1 );
    Font font2 = Graphics.getFont( "font2", 8, 1 );
    item1.setFont( 1, font2 );
    Font font3 = Graphics.getFont( "font3", 6, 1 );
    item1.setFont( 2, font3 );
    Image image1 = Graphics.getImage( Fixture.IMAGE1 );
    Image image2 = Graphics.getImage( Fixture.IMAGE2 );
    Image image3 = Graphics.getImage( Fixture.IMAGE3 );
    item1.setImage( new Image[]{
      image1, image2, image3
    } );
    Color background1 = Graphics.getColor( 234, 230, 54 );
    item1.setBackground( 0, background1 );
    Color background2 = Graphics.getColor( 145, 222, 134 );
    item1.setBackground( 1, background2 );
    Color background3 = Graphics.getColor( 143, 134, 34 );
    item1.setBackground( 2, background3 );
    Color foreground1 = Graphics.getColor( 77, 77, 54 );
    item1.setForeground( 0, foreground1 );
    Color foreground2 = Graphics.getColor( 156, 45, 134 );
    item1.setForeground( 1, foreground2 );
    Color foreground3 = Graphics.getColor( 88, 134, 34 );
    item1.setForeground( 2, foreground3 );
    table.setSelection( 0 );
    ITableAdapter tableAdapter = table.getAdapter( ITableAdapter.class );
    tableAdapter.setFocusIndex( 0 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item1 );
    images2 = ( Image[] )adapter.getPreserved( TableItemLCA.PROP_IMAGES );
    assertEquals( image1, images2[ 0 ] );
    assertEquals( image2, images2[ 1 ] );
    assertEquals( image3, images2[ 2 ] );
    preservedCellFonts = ( Font[] )adapter.getPreserved( TableItemLCA.PROP_CELL_FONTS );
    assertEquals( font1, preservedCellFonts[ 0 ] );
    assertEquals( font2, preservedCellFonts[ 1 ] );
    assertEquals( font3, preservedCellFonts[ 2 ] );
    preservedCellBackgrounds
      = ( Color[] )adapter.getPreserved( TableItemLCA.PROP_CELL_BACKGROUNDS );
    assertEquals( background1, preservedCellBackgrounds[ 0 ] );
    assertEquals( background2, preservedCellBackgrounds[ 1 ] );
    assertEquals( background3, preservedCellBackgrounds[ 2 ] );
    preservedCellForegrounds
      = ( Color[] )adapter.getPreserved( TableItemLCA.PROP_CELL_FOREGROUNDS );
    assertEquals( foreground1, preservedCellForegrounds[ 0 ] );
    assertEquals( foreground2, preservedCellForegrounds[ 1 ] );
    assertEquals( foreground3, preservedCellForegrounds[ 2 ] );
    Fixture.clearPreserved();
  }

  @Test
  public void testCheckPreserveValues() {
    Table table = new Table( shell, SWT.CHECK );
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Object checked = adapter.getPreserved( TableItemLCA.PROP_CHECKED );
    assertEquals( Boolean.FALSE, checked );
    Object grayed = adapter.getPreserved( TableItemLCA.PROP_GRAYED );
    assertEquals( Boolean.FALSE, grayed );
    Fixture.clearPreserved();
    item.setChecked( true );
    item.setGrayed( true );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item );
    checked = adapter.getPreserved( TableItemLCA.PROP_CHECKED );
    grayed = adapter.getPreserved( TableItemLCA.PROP_GRAYED );
    assertEquals( Boolean.TRUE, checked );
    assertEquals( Boolean.TRUE, grayed );
    Fixture.clearPreserved();
  }

  @Test
  public void testItemTextWithoutColumn() throws IOException, JSONException {
    TableItem item = new TableItem( table, SWT.NONE );
    // Ensure that even though there are no columns, the first text of an item
    // will be rendered
    Fixture.fakeResponseWriter();
    TableItemLCA tableItemLCA = new TableItemLCA();
    Fixture.markInitialized( item );
    tableItemLCA.preserveValues( item );
    item.setText( "newText" );
    tableItemLCA.renderChanges( item );
    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "texts" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"newText\"]", actual ) );
  }

  @Test
  public void testDisposeSelected() {
    final boolean[] executed = { false };
    final Table table = new Table( shell, SWT.CHECK );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    table.setSelection( 2 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        table.remove( 1, 2 );
        executed[ 0 ] = true;
      }
    } );

    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( display );

    assertTrue( executed[ 0 ] );
  }

  @Test
  public void testDispose() throws IOException {
    Table table = new Table( shell, SWT.CHECK );
    TableItem tableItem = new TableItem( table, SWT.NONE );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( tableItem );
    Fixture.markInitialized( table );
    Fixture.markInitialized( tableItem );
    Fixture.fakeResponseWriter();

    tableItem.dispose();
    lca.renderDispose( tableItem );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findDestroyOperation( tableItem ) );
  }

  @Test
  public void testDisposeTable() throws IOException {
    Table table = new Table( shell, SWT.CHECK );
    TableItem tableItem = new TableItem( table, SWT.NONE );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( tableItem );
    Fixture.markInitialized( table );
    Fixture.markInitialized( tableItem );
    Fixture.fakeResponseWriter();

    table.dispose();
    lca.renderDispose( tableItem );

    // when the whole table is disposed of, the tableitem's dispose must not be rendered
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findDestroyOperation( tableItem ) );
    assertTrue( tableItem.isDisposed() );
  }

  @Test
  public void testWriteChangesForVirtualItem() throws IOException {
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 100 );
    // Ensure that nothing is written for an item that is virtual and whose
    // cached was false and remains unchanged while processing the life cycle
    TableItem item = table.getItem( 0 );
    table.clear( 0 );
    TableItemLCA lca = new TableItemLCA();
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( item );
    // Ensure that nothing else than the 'checked' property gets preserved
    lca.preserveValues( item );
    WidgetAdapter itemAdapter = WidgetUtil.getAdapter( item );

    assertEquals( Boolean.FALSE, itemAdapter.getPreserved( TableItemLCA.PROP_CACHED ) );
    assertNull( itemAdapter.getPreserved( TableItemLCA.PROP_TEXTS ) );
    assertNull( itemAdapter.getPreserved( TableItemLCA.PROP_IMAGES ) );
    assertNull( itemAdapter.getPreserved( TableItemLCA.PROP_CHECKED ) );

    // ... and no markup is generated for a uncached item that was already
    // uncached when entering the life cycle
    lca.renderChanges( item );

    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );
  }

  @Test
  public void testDynamicColumns() {
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setBackground( 0, display.getSystemColor( SWT.COLOR_BLACK ) );
    // Create another column after setting a cell background
    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=277089
    new TableColumn( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
  }

  @Test
  public void testRenderCreate() throws IOException {
    new TableItem( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.GridItem", operation.getType() );
    assertEquals( Integer.valueOf( 1 ), operation.getProperty( "index" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );

    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( WidgetUtil.getId( item.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderInitialTexts() throws IOException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "texts" ) == -1 );
  }

  @Test
  public void testRenderTexts() throws IOException, JSONException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    item.setText( new String[] { "item 0.0", "item 0.1" } );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "texts" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"item 0.0\",\"item 0.1\"]", actual ) );
  }

  @Test
  public void testRenderTextsUnchanged() throws IOException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setText( new String[] { "item 0.0", "item 0.1" } );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "texts" ) );
  }

  @Test
  public void testRenderInitialImages() throws IOException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "images" ) == -1 );
  }

  @Test
  public void testRenderImages() throws IOException, JSONException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Image image = Graphics.getImage( Fixture.IMAGE1 );

    item.setImage( new Image[] { null, image } );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "images" );
    String expected = "[\"rwt-resources/generated/90fb0bfe.gif\",58,12]";
    assertEquals( JSONObject.NULL, actual.get( 0 ) );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual.getJSONArray( 1 ) ) );
  }

  @Test
  public void testRenderImagesUnchanged() throws IOException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = Graphics.getImage( Fixture.IMAGE1 );

    item.setImage( new Image[] { null, image } );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "images" ) );
  }

  @Test
  public void testRenderInitialBackground() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "background" ) == -1 );
  }

  @Test
  public void testRenderBackground() throws IOException, JSONException {
    TableItem item = new TableItem( table, SWT.NONE );

    item.setBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "background" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", actual ) );
  }

  @Test
  public void testRenderBackgroundUnchanged() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "background" ) );
  }

  @Test
  public void testRenderInitialForeground() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "foreground" ) == -1 );
  }

  @Test
  public void testRenderForeground() throws IOException, JSONException {
    TableItem item = new TableItem( table, SWT.NONE );

    item.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "foreground" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", actual ) );
  }

  @Test
  public void testRenderForegroundUnchanged() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "foreground" ) );
  }

  @Test
  public void testRenderInitialFont() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "font" ) == -1 );
  }

  @Test
  public void testRenderFont() throws IOException, JSONException {
    TableItem item = new TableItem( table, SWT.NONE );

    item.setFont( Graphics.getFont( "Arial", 20, SWT.BOLD ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "font" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"Arial\"]", actual.getJSONArray( 0 ) ) );
    assertEquals( Integer.valueOf( 20 ), actual.get( 1 ) );
    assertEquals( Boolean.TRUE, actual.get( 2 ) );
    assertEquals( Boolean.FALSE, actual.get( 3 ) );
  }

  @Test
  public void testRenderFontUnchanged() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setFont( Graphics.getFont( "Arial", 20, SWT.BOLD ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "font" ) );
  }

  @Test
  public void testRenderInitialCellBackgrounds() throws IOException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "cellBackgrounds" ) == -1 );
  }

  @Test
  public void testRenderCellBackgrounds() throws IOException, JSONException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    item.setBackground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "cellBackgrounds" );
    assertEquals( JSONObject.NULL, actual.get( 0 ) );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", actual.getJSONArray( 1 ) ) );
  }

  @Test
  public void testRenderCellBackgroundsUnchanged() throws IOException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setBackground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellBackgrounds" ) );
  }

  @Test
  public void testRenderInitialCellForegrounds() throws IOException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "cellForegrounds" ) == -1 );
  }

  @Test
  public void testRenderCellForegrounds() throws IOException, JSONException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    item.setForeground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "cellForegrounds" );
    assertEquals( JSONObject.NULL, actual.get( 0 ) );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", actual.getJSONArray( 1 ) ) );
  }

  @Test
  public void testRenderCellForegroundsUnchanged() throws IOException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setForeground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellForegrounds" ) );
  }

  @Test
  public void testRenderInitialCellFonts() throws IOException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "cellFonts" ) == -1 );
  }

  @Test
  public void testRenderCellFonts() throws IOException, JSONException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );

    item.setFont( 1, Graphics.getFont( "Arial", 20, SWT.BOLD ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "cellFonts" );
    assertEquals( JSONObject.NULL, actual.get( 0 ) );
    JSONArray cellFont = actual.getJSONArray( 1 );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"Arial\"]", cellFont.getJSONArray( 0 ) ) );
    assertEquals( Integer.valueOf( 20 ), cellFont.get( 1 ) );
    assertEquals( Boolean.TRUE, cellFont.get( 2 ) );
    assertEquals( Boolean.FALSE, cellFont.get( 3 ) );
  }

  @Test
  public void testRenderCellFontsUnchanged() throws IOException {
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setFont( 1, Graphics.getFont( "Arial", 20, SWT.BOLD ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellFonts" ) );
  }

  @Test
  public void testRenderInitialChecked() throws IOException {
    table = new Table( shell, SWT.CHECK );
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "checked" ) == -1 );
  }

  @Test
  public void testRenderChecked() throws IOException {
    table = new Table( shell, SWT.CHECK );
    TableItem item = new TableItem( table, SWT.NONE );

    item.setChecked( true );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( item, "checked" ) );
  }

  @Test
  public void testRenderCheckedUnchanged() throws IOException {
    table = new Table( shell, SWT.CHECK );
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setChecked( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "checked" ) );
  }

  @Test
  public void testRenderInitialGrayed() throws IOException {
    table = new Table( shell, SWT.CHECK );
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "grayed" ) == -1 );
  }

  @Test
  public void testRenderGrayed() throws IOException {
    table = new Table( shell, SWT.CHECK );
    TableItem item = new TableItem( table, SWT.NONE );

    item.setGrayed( true );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( item, "grayed" ) );
  }

  @Test
  public void testRenderGrayedUnchanged() throws IOException {
    table = new Table( shell, SWT.CHECK );
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setGrayed( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "grayed" ) );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );

    item.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( item, "customVariant" ) );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    TableItem item = new TableItem( table, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "customVariant" ) );
  }

  @Test
  public void testRenderClear() throws IOException {
    table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 1 );
    TableItem item = table.getItem( 0 );

    table.clear( 0 );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( item, "clear" ) );
  }
}
