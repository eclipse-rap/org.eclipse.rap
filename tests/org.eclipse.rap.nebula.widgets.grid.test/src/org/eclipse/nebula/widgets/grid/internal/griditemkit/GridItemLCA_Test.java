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
package org.eclipse.nebula.widgets.grid.internal.griditemkit;

import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridColumns;
import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridItems;
import static org.eclipse.nebula.widgets.grid.GridTestUtil.loadImage;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("restriction")
public class GridItemLCA_Test {

  private Display display;
  private Shell shell;
  private Grid grid;
  private GridItem item;
  private GridItemLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    grid = new Grid( shell, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
    item = new GridItem( grid, SWT.NONE );
    lca = new GridItemLCA();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderCreate() throws IOException {
    GridItem[] items = createGridItems( grid, 3, 3 );

    lca.renderInitialization( items[ 8 ] );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( items[ 8 ] );
    assertEquals( "rwt.widgets.GridItem", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( item );

    lca.renderInitialization( item );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof GridItemOperationHandler );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( WidgetUtil.getId( item.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderParent_WithParentItem() throws IOException {
    GridItem subitem = new GridItem( item, SWT.NONE );

    lca.renderInitialization( subitem );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( subitem );
    assertEquals( WidgetUtil.getId( item ), getParent( operation ) );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( item );

    TestMessage message = Fixture.getProtocolMessage();
    DestroyOperation operation = ( DestroyOperation )message.getOperation( 0 );
    assertEquals( WidgetUtil.getId( item ), operation.getTarget() );
  }

  @Test
  public void testRenderDispose_WithDisposedGrid() throws IOException {
    grid.dispose();

    lca.renderDispose( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testRenderDispose_WithDisposedParentItem() throws IOException {
    GridItem[] items = createGridItems( grid, 3, 3 );
    items[ 0 ].dispose();

    lca.renderDispose( items[ 1 ] );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testRenderDispose_withDisposedParent_destroysRemoteObjects() throws IOException {
    lca.renderInitialization( item );
    RemoteObjectImpl remoteObject = RemoteObjectRegistry.getInstance().get( getId( item ) );
    grid.dispose();

    lca.renderDispose( item );

    assertTrue( remoteObject.isDestroyed() );
  }

  @Test
  public void testRenderInitialIndex() throws IOException {
    GridItem gridItem = new GridItem( grid, SWT.NONE );

    lca.render( gridItem );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( gridItem );
    assertEquals( 1, operation.getProperties().get( "index" ).asInt() );
  }

  @Test
  public void testRenderIndex() throws IOException {
    new GridItem( grid, SWT.NONE, 0 );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( item, "index" ).asInt() );
  }

  @Test
  public void testRenderIndex_VirtualAfterClear() throws IOException {
    grid = new Grid( shell, SWT.VIRTUAL );
    item = new GridItem( grid, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Fixture.preserveWidgets();

    new GridItem( grid, SWT.NONE, 0 );
    grid.clear( 1, false );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( item, "index" ).asInt() );
  }

  @Test
  public void testRenderIndexWithParentItem() throws IOException {
    GridItem rootItem = new GridItem( grid, SWT.NONE );
    new GridItem( rootItem, SWT.NONE );
    GridItem item = new GridItem( rootItem, SWT.NONE );

    new GridItem( rootItem, SWT.NONE, 0 );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( item, "index" ).asInt() );
  }

  @Test
  public void testRenderIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    new GridItem( grid, SWT.NONE, 0 );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "index" ) );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    item.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( item, "customVariant" ).asString() );
  }

  @Test
  public void testRenderInitialItemCount() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "itemCount" ) == -1 );
  }

  @Test
  public void testRenderItemCount() throws IOException {
    GridItem[] items = createGridItems( grid, 1, 10 );
    lca.renderChanges( items[ 0 ] );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( items[ 0 ], "itemCount" ).asInt() );
  }

  @Test
  public void testRenderItemCountUnchanged() throws IOException {
    GridItem[] items = createGridItems( grid, 1, 10 );
    Fixture.markInitialized( display );
    Fixture.markInitialized( items[ 0 ] );

    Fixture.preserveWidgets();
    lca.renderChanges( items[ 0 ] );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( items[ 0 ], "itemCount" ) );
  }

  @Test
  public void testRenderInitialHeight() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "height" ) == -1 );
  }

  @Test
  public void testRenderHeight() throws IOException {
    item.setHeight( 10 );

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( item, "height" ).asInt() );
  }

  @Test
  public void testRenderHeightUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setHeight( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "height" ) );
  }

  @Test
  public void testRenderInitialTexts() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "texts" ) == -1 );
  }

  @Test
  public void testRenderTexts() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    item.setText( 0, "item 0.0" );
    item.setText( 1, "item 0.1" );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[\"\", \"item 0.0\", \"item 0.1\"]" );
    assertEquals( expected, message.findSetProperty( item, "texts" ) );
  }

  @Test
  public void testRenderTextsWithRowHeader() throws IOException {
    grid.setRowHeaderVisible( true );
    createGridColumns( grid, 2, SWT.NONE );

    item.setHeaderText( "header 0" );
    item.setText( 0, "item 0.0" );
    item.setText( 1, "item 0.1" );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[\"header 0\", \"item 0.0\", \"item 0.1\"]" );
    assertEquals( expected, message.findSetProperty( item, "texts" ) );
  }

  @Test
  public void testRenderTextsUnchanged() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setText( 0, "item 0.0" );
    item.setText( 1, "item 0.1" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "texts" ) );
  }

  @Test
  public void testRenderTextsReset() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    item.setText( 1, "item 0.1" );
    Fixture.preserveWidgets();

    item.setText( 1, "" );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.NULL, message.findSetProperty( item, "texts" ) );
  }

  @Test
  public void testRenderInitialImages() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "images" ) == -1 );
  }

  @Test
  public void testRenderImages() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE1 );

    item.setImage( 1, image );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "images" );
    String expected = "[null, null, [\"rwt-resources/generated/90fb0bfe.gif\",58,12]]";
    assertEquals( JsonArray.readFrom( expected ), actual );
  }

  @Test
  public void testRenderImagesWithRowHeader() throws IOException {
    grid.setRowHeaderVisible( true );
    createGridColumns( grid, 2, SWT.NONE );
    Image image = loadImage( display, Fixture.IMAGE1 );

    item.setHeaderImage( image );
    item.setImage( 1, image );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "images" );
    String expected = "[[\"rwt-resources/generated/90fb0bfe.gif\",58,12], "
                    + "null, [\"rwt-resources/generated/90fb0bfe.gif\",58,12]]";
    assertEquals( JsonArray.readFrom( expected ), actual );
  }

  @Test
  public void testRenderImagesUnchanged() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = loadImage( display, Fixture.IMAGE1 );

    item.setImage( 1, image );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "images" ) );
  }

  @Test
  public void testRenderImagesReset() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = loadImage( display, Fixture.IMAGE1 );
    item.setImage( 1, image );
    Fixture.preserveWidgets();

    item.setImage( 1, null );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.NULL, message.findSetProperty( item, "images" ) );
  }

  @Test
  public void testRenderInitialBackground() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "background" ) == -1 );
  }

  @Test
  public void testRenderBackground() throws IOException {
    item.setBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "background" );
    assertEquals( JsonArray.readFrom( "[0,255,0,255]" ), actual );
  }

  @Test
  public void testRenderBackgroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "background" ) );
  }

  @Test
  public void testRenderInitialForeground() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "foreground" ) == -1 );
  }

  @Test
  public void testRenderForeground() throws IOException {
    item.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "foreground" );
    assertEquals( JsonArray.readFrom( "[0,255,0,255]" ), actual );
  }

  @Test
  public void testRenderForegroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "foreground" ) );
  }

  @Test
  public void testRenderInitialFont() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "font" ) == -1 );
  }

  @Test
  public void testRenderFont() throws IOException {
    item.setFont( new Font( display, "Arial", 20, SWT.BOLD ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "font" );
    assertEquals( JsonArray.readFrom( "[[\"Arial\"], 20, true, false]" ), actual );
  }

  @Test
  public void testRenderFontUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setFont( new Font( display, "Arial", 20, SWT.BOLD ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "font" ) );
  }

  @Test
  public void testRenderInitialCellBackgrounds() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "cellBackgrounds" ) == -1 );
  }

  @Test
  public void testRenderCellBackgrounds() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    item.setBackground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "cellBackgrounds" );
    assertEquals( JsonArray.readFrom( "[null, null, [0,255,0,255]]" ), actual );
  }

  @Test
  public void testRenderCellBackgroundsWithRowHeader() throws IOException {
    grid.setRowHeaderVisible( true );
    createGridColumns( grid, 2, SWT.NONE );

    item.setHeaderBackground( display.getSystemColor( SWT.COLOR_RED ) );
    item.setBackground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "cellBackgrounds" );
    assertEquals( JsonArray.readFrom( "[[255,0,0,255], null, [0,255,0,255]]" ), actual );
  }

  @Test
  public void testRenderCellBackgroundsUnchanged() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setBackground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellBackgrounds" ) );
  }

  @Test
  public void testRenderCellBackgroundsReset() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    item.setBackground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();

    item.setBackground( 1, null );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.NULL, message.findSetProperty( item, "cellBackgrounds" ) );
  }

  @Test
  public void testRenderInitialCellForegrounds() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "cellForegrounds" ) == -1 );
  }

  @Test
  public void testRenderCellForegrounds() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    item.setForeground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "cellForegrounds" );
    assertEquals( JsonArray.readFrom( "[null, null, [0,255,0,255]]" ), actual );
  }

  @Test
  public void testRenderCellForegroundsWithRowHeader() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    item.setHeaderForeground( display.getSystemColor( SWT.COLOR_RED ) );
    item.setForeground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "cellForegrounds" );
    assertEquals( JsonArray.readFrom( "[[255,0,0,255], null, [0,255,0,255]]" ), actual );
  }

  @Test
  public void testRenderCellForegroundsUnchanged() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setForeground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellForegrounds" ) );
  }

  @Test
  public void testRenderCellForegroundsReset() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    item.setForeground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();

    item.setForeground( 1, null );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.NULL, message.findSetProperty( item, "cellForegrounds" ) );
  }

  @Test
  public void testRenderInitialCellFonts() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "cellFonts" ) == -1 );
  }

  @Test
  public void testRenderCellFonts() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );

    item.setFont( 1, new Font( display, "Arial", 20, SWT.BOLD ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "cellFonts" );
    assertEquals( JsonArray.readFrom( "[null, null, [[\"Arial\"], 20, true, false]]" ), actual );
  }

  @Test
  public void testRenderCellFontsWithRowHeader() throws IOException {
    grid.setRowHeaderVisible( true );
    createGridColumns( grid, 2, SWT.NONE );

    Font font = new Font( display, "Arial", 20, SWT.BOLD );
    item.setHeaderFont( font );
    item.setFont( 1, font );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( item, "cellFonts" );
    JsonArray expected = JsonArray.readFrom( "[[[\"Arial\"], 20, true, false], "
                                           + "null, [[\"Arial\"], 20, true, false]]" );
    assertEquals( expected, actual );
  }

  @Test
  public void testRenderCellFontsUnchanged() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setFont( 1, new Font( display, "Arial", 20, SWT.BOLD ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellFonts" ) );
  }

  @Test
  public void testRenderCellFontsReset() throws IOException {
    createGridColumns( grid, 2, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    item.setFont( 1, new Font( display, "Arial", 20, SWT.BOLD ) );
    Fixture.preserveWidgets();

    item.setFont( 1, null );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.NULL, message.findSetProperty( item, "cellFonts" ) );
  }

  @Test
  public void testRenderInitialExpanded() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "expanded" ) == -1 );
  }

  @Test
  public void testRenderExpanded() throws IOException {
    new GridItem( item, SWT.NONE );

    item.setExpanded( true );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( item, "expanded" ) );
  }

  @Test
  public void testRenderExpandedUnchanged() throws IOException {
    new GridItem( item, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setExpanded( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "expanded" ) );
  }

  @Test
  public void testRenderInitialCellChecked() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "cellChecked" ) == -1 );
  }

  @Test
  public void testRenderCellChecked() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );

    item.setChecked( 1, true );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[false,false,true]" );
    assertEquals( expected, message.findSetProperty( item, "cellChecked" ) );
  }

  @Test
  public void testRenderCellCheckedUnchanged() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setChecked( 1, true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellChecked" ) );
  }

  @Test
  public void testRenderCellCheckedReset() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    item.setChecked( 1, true );
    Fixture.preserveWidgets();

    item.setChecked( 1, false );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.NULL, message.findSetProperty( item, "cellChecked" ) );
  }

  @Test
  public void testRenderInitialCellGrayed() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "cellGrayed" ) == -1 );
  }

  @Test
  public void testRenderCellGrayed() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );

    item.setGrayed( 1, true );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[false, false, true]" );
    assertEquals( expected, message.findSetProperty( item, "cellGrayed" ) );
  }

  @Test
  public void testRenderGrayedUnchanged() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setGrayed( 1, true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellGrayed" ) );
  }

  @Test
  public void testRenderGrayedReset() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    item.setGrayed( 1, true );
    Fixture.preserveWidgets();

    item.setGrayed( 1, false );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.NULL, message.findSetProperty( item, "cellGrayed" ) );
  }

  @Test
  public void testRenderInitialCellCheckable() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "cellCheckable" ) == -1 );
  }

  @Test
  public void testRenderCellCheckable() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );

    item.setCheckable( 1, false );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[true, true, false]" );
    assertEquals( expected, message.findSetProperty( item, "cellCheckable" ) );
  }

  @Test
  public void testRenderCellCheckableUnchanged() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setCheckable( 1, false );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellCheckable" ) );
  }

  @Test
  public void testRenderCellCheckableReset() throws IOException {
    grid = new Grid( shell, SWT.CHECK );
    createGridColumns( grid, 2, SWT.NONE );
    item = new GridItem( grid, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    item.setCheckable( 1, false );
    Fixture.preserveWidgets();

    item.setCheckable( 1, true );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.NULL, message.findSetProperty( item, "cellCheckable" ) );
  }

  @Test
  public void testRenderInitialColumnSpans() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getProperties().names().indexOf( "columnSpans" ) == -1 );
  }

  @Test
  public void testRenderColumnSpans() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );

    item.setColumnSpan( 1, 1 );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[0, 0, 1, 0]" );
    assertEquals( expected, message.findSetProperty( item, "columnSpans" ) );
  }

  @Test
  public void testRenderColumnSpansUnchanged() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setColumnSpan( 1, 1 );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "columnSpans" ) );
  }

  @Test
  public void testRenderColumnSpansReset() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    item.setColumnSpan( 1, 1 );

    Fixture.preserveWidgets();
    item.setColumnSpan( 1, 0 );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.NULL, message.findSetProperty( item, "columnSpans" ) );
  }

  @Test
  public void testRenderData() throws IOException {
    WidgetUtil.registerDataKeys( new String[]{ "foo", "bar" } );
    item.setData( "foo", "string" );
    item.setData( "bar", Integer.valueOf( 1 ) );

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonObject data = ( JsonObject )message.findSetProperty( item, "data" );
    assertEquals( "string", data.get( "foo" ).asString() );
    assertEquals( 1, data.get( "bar" ).asInt() );
  }

  @Test
  public void testRenderDataUnchanged() throws IOException {
    WidgetUtil.registerDataKeys( new String[]{ "foo" } );
    item.setData( "foo", "string" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testRender_onVirtual() throws IOException {
    grid = new Grid( shell, SWT.VIRTUAL );
    grid.setItemCount( 1 );
    // Ensure that nothing is written for an item that is virtual and whose
    // cached was false and remains unchanged while processing the life cycle
    GridItem item = grid.getItem( 0 );
    grid.clear( 0, false );
    Fixture.markInitialized( item );
    // Ensure that nothing else than the 'index' and 'cached' property gets preserved
    lca.preserveValues( item );
    RemoteAdapter adapter = WidgetUtil.getAdapter( item );

    assertEquals( Boolean.FALSE, adapter.getPreserved( "cached" ) );
    assertEquals( Integer.valueOf( 0 ), adapter.getPreserved( "index" ) );
    assertNull( adapter.getPreserved( "itemCount" ) );
    assertNull( adapter.getPreserved( "texts" ) );
    assertNull( adapter.getPreserved( "images" ) );
    assertNull( adapter.getPreserved( "cellChecked" ) );

    // ... and no operations are generated for a uncached item that was already
    // uncached when entering the life cycle
    lca.renderChanges( item );

    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );
  }

  @Test
  public void testRender_onVirtual_rendersOnlyChangedProperties() throws IOException {
    grid = new Grid( shell, SWT.VIRTUAL );
    grid.setItemCount( 1 );
    GridItem item = grid.getItem( 0 );
    Fixture.markInitialized( item );
    lca.preserveValues( item );
    item.setText( "foo" );

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( item, "texts" ) );
    assertNull( message.findSetOperation( item, "itemCount" ) );
    assertNull( message.findSetOperation( item, "height" ) );
    assertNull( message.findSetOperation( item, "images" ) );
    assertNull( message.findSetOperation( item, "cellChecked" ) );
    assertNull( message.findSetOperation( item, "cellGrayed" ) );
    assertNull( message.findSetOperation( item, "cellCheckable" ) );
    assertNull( message.findSetOperation( item, "font" ) );
    assertNull( message.findSetOperation( item, "foreground" ) );
    assertNull( message.findSetOperation( item, "background" ) );
    assertNull( message.findSetOperation( item, "cellFonts" ) );
    assertNull( message.findSetOperation( item, "cellBackgrounds" ) );
    assertNull( message.findSetOperation( item, "cellForegrounds" ) );
  }

  @Test
  public void testRender_onVirtual_preservesInitializedFlag() throws IOException {
    grid = new Grid( shell, SWT.VIRTUAL );
    grid.setItemCount( 1 );
    GridItem item = grid.getItem( 0 );
    Fixture.markInitialized( item );
    lca.preserveValues( item );
    item.setText( "foo" );

    lca.renderChanges( item );

    assertTrue( WidgetUtil.getAdapter( item ).isInitialized() );
  }

  @Test
  public void testRenderClear_onNonInitializedItem() throws IOException {
    grid = new Grid( shell, SWT.VIRTUAL );
    grid.setItemCount( 1 );
    GridItem item = grid.getItem( 0 );
    item.getText();

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( item, "clear" ) );
  }

  @Test
  public void testRenderClear_onInitializedItem() throws IOException {
    grid = new Grid( shell, SWT.VIRTUAL );
    grid.setItemCount( 1 );
    GridItem item = grid.getItem( 0 );
    item.getText();
    Fixture.markInitialized( item );

    lca.preserveValues( item );
    grid.clear( 0, false );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( item, "clear" ) );
  }

}
