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
package org.eclipse.swt.internal.widgets.treeitemkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getAdapter;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.registerDataKeys;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.IWidgetColorAdapter;
import org.eclipse.swt.internal.widgets.treekit.TreeOperationHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TreeItemLCA_Test {

  private Display display;
  private Shell shell;
  private Tree tree;
  private TreeItem item;
  private TreeItemLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    tree = new Tree( shell, SWT.NONE );
    item = new TreeItem( tree, SWT.NONE );
    lca = TreeItemLCA.INSTANCE;
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testPreserveValues() throws IOException {
    Fixture.markInitialized( display );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    new TreeColumn( tree, SWT.NONE, 0 );
    new TreeColumn( tree, SWT.NONE, 1 );
    new TreeColumn( tree, SWT.NONE, 2 );
    item.setText( "qwert" );
    new TreeItem( item, SWT.NONE, 0 );
    Image image = createImage( display, Fixture.IMAGE1 );
    item.setImage( image );
    item.setExpanded( true );
    Fixture.preserveWidgets();
    RemoteAdapter adapter = getAdapter( item );
    String[] texts = ( String[] )adapter.getPreserved( TreeItemLCA.PROP_TEXTS );
    assertEquals( "qwert", texts[ 0 ] );
    assertEquals( Boolean.TRUE, adapter.getPreserved( TreeItemLCA.PROP_EXPANDED ) );
    Image[] images = ( Image[] )adapter.getPreserved( TreeItemLCA.PROP_IMAGES );
    assertEquals( image, images[ 0 ] );
    IWidgetColorAdapter colorAdapter = item.getAdapter( IWidgetColorAdapter.class );
    Object background = adapter.getPreserved( "background" );
    assertEquals( colorAdapter.getUserBackground(), background );
    Object foreground = adapter.getPreserved( "foreground" );
    assertEquals( colorAdapter.getUserForeground(), foreground );
    Font[] fonts = ( Font[] )adapter.getPreserved( "font" );
    assertNull( fonts );
    Color[] backgrounds = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_BACKGROUNDS );
    assertNull( backgrounds );
    Color[] foregrounds = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_FOREGROUNDS );
    assertNull( foregrounds );
    Fixture.clearPreserved();
    item.setText( 0, "item11" );
    item.setText( 1, "item12" );
    item.setText( 2, "item13" );
    Image image1 = createImage( display, Fixture.IMAGE1 );
    Image image2 = createImage( display, Fixture.IMAGE2 );
    Image image3 = createImage( display, Fixture.IMAGE3 );
    item.setImage( 0, image1 );
    item.setImage( 1, image2 );
    item.setImage( 2, image3 );
    tree.setSelection( item );
    background =new Color( display, 234, 113, 34 );
    item.setBackground( ( Color )background );
    foreground =new Color( display, 122, 232, 45 );
    item.setForeground( ( Color )foreground );
    Font font1 = new Font( display, "font1", 10, 1 );
    item.setFont( 0, font1 );
    Font font2 = new Font( display, "font1", 8, 1 );
    item.setFont( 1, font2 );
    Font font3 = new Font( display, "font1", 6, 1 );
    item.setFont( 2, font3 );
    Color background1 = new Color( display, 234, 230, 54 );
    item.setBackground( 0, background1 );
    Color background2 = new Color( display, 145, 222, 134 );
    item.setBackground( 1, background2 );
    Color background3 = new Color( display, 143, 134, 34 );
    item.setBackground( 2, background3 );
    Color foreground1 = new Color( display, 77, 77, 54 );
    item.setForeground( 0, foreground1 );
    Color foreground2 = new Color( display, 156, 45, 134 );
    item.setForeground( 1, foreground2 );
    Color foreground3 = new Color( display, 88, 134, 34 );
    item.setForeground( 2, foreground3 );
    Fixture.preserveWidgets();
    adapter = getAdapter( item );
    texts = ( String[] )adapter.getPreserved( TreeItemLCA.PROP_TEXTS );
    assertEquals( "item11", texts[ 0 ] );
    assertEquals( "item12", texts[ 1 ] );
    assertEquals( "item13", texts[ 2 ] );
    images = ( Image[] )adapter.getPreserved( TreeItemLCA.PROP_IMAGES );
    assertEquals( image1, images[ 0 ] );
    assertEquals( image2, images[ 1 ] );
    assertEquals( image3, images[ 2 ] );
    assertEquals( background, adapter.getPreserved( "background" ) );
    assertEquals( foreground, adapter.getPreserved( "foreground" ) );
    fonts = ( Font[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_FONTS );
    assertEquals( font1, fonts[ 0 ] );
    assertEquals( font2, fonts[ 1 ] );
    assertEquals( font3, fonts[ 2 ] );
    backgrounds = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_BACKGROUNDS );
    assertEquals( background1, backgrounds[ 0 ] );
    assertEquals( background2, backgrounds[ 1 ] );
    assertEquals( background3, backgrounds[ 2 ] );
    foregrounds = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_FOREGROUNDS );
    assertEquals( foreground1, foregrounds[ 0 ] );
    assertEquals( foreground2, foregrounds[ 1 ] );
    assertEquals( foreground3, foregrounds[ 2 ] );
  }

  @Test
  public void testCheckPreserveValues() {
    tree = new Tree( shell, SWT.CHECK );
    item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    RemoteAdapter adapter = getAdapter( item );
    assertEquals( Boolean.FALSE, adapter.getPreserved( TreeItemLCA.PROP_CHECKED ) );
    assertEquals( Boolean.FALSE, adapter.getPreserved( TreeItemLCA.PROP_GRAYED ) );
    Fixture.clearPreserved();
    item.setChecked( true );
    item.setGrayed( true );
    Fixture.preserveWidgets();
    adapter = getAdapter( item );
    assertEquals( Boolean.TRUE, adapter.getPreserved( TreeItemLCA.PROP_CHECKED ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( TreeItemLCA.PROP_GRAYED ) );
  }

  @Test
  public void testLcaDoesNotMaterializeItem() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );
    TreeItem treeItem = tree.getItem( 99 );
    shell.open();

    Fixture.executeLifeCycleFromServerThread();

    assertFalse( tree.getAdapter( ITreeAdapter.class ).isCached( treeItem ) );
  }

  @Test
  public void testExpandedPropertyNotRenderedBack() {
    getRemoteObject( item ).setHandler( new TreeItemOperationHandler( item ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    new TreeItem( item, SWT.NONE );
    item.setExpanded( false );

    Fixture.fakeSetProperty( getId( item ), TreeItemLCA.PROP_EXPANDED, true  );
    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, TreeItemLCA.PROP_EXPANDED ) );
  }

  @Test
  public void testGetBoundsWithScrolling() {
    getRemoteObject( tree ).setHandler( new TreeOperationHandler( tree ) );
    TreeItem rootItem2 = new TreeItem( tree, 0 );
    TreeItem rootItem3 = new TreeItem( tree, 0 );

    tree.getAdapter( ITreeAdapter.class ).checkData();

    assertEquals( 0, item.getBounds().y );
    assertEquals( 27, rootItem2.getBounds().y );
    assertEquals( 54, rootItem3.getBounds().y );

    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( tree ), "scrollLeft", 0 );
    Fixture.fakeSetProperty( getId( tree ), "topItemIndex", 2 );
    Fixture.readDataAndProcessAction( display );

    assertEquals( -54, item.getBounds().y );
    assertEquals( -27, rootItem2.getBounds().y );
    assertEquals( 0, rootItem3.getBounds().y );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.GridItem", operation.getType() );
  }

  @Test
  public void testRenderCreate_setsOperationHandler() throws IOException {
    String id = getId( item );

    lca.renderInitialization( item );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof TreeItemOperationHandler );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( item );

    TestMessage message = Fixture.getProtocolMessage();
    DestroyOperation operation = ( DestroyOperation )message.getOperation( 0 );
    assertEquals( WidgetUtil.getId( item ), operation.getTarget() );
  }

  @Test
  public void testRenderDisposeWithDisposedTree() throws IOException {
    tree.dispose();

    lca.renderDispose( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testRenderDisposeWithDisposedParentItem() throws IOException {
    TreeItem subitem = new TreeItem( item, SWT.NONE );
    item.dispose();

    lca.renderDispose( subitem );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testRenderDispose_withDisposedParent_destroysRemoteObjects() throws IOException {
    lca.renderInitialization( item );
    RemoteObjectImpl remoteObject = RemoteObjectRegistry.getInstance().get( getId( item ) );
    tree.dispose();

    lca.renderDispose( item );

    assertTrue( remoteObject.isDestroyed() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( getId( item.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderInitialIndex() throws IOException {
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );

    lca.render( treeItem );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( treeItem );
    assertEquals( 1, operation.getProperties().get( "index" ).asInt() );
  }

  @Test
  public void testRenderIndex() throws IOException {
    new TreeItem( tree, SWT.NONE, 0 );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( item, "index" ).asInt() );
  }

  @Test
  public void testRenderIndex_VirtualAfterClear() throws IOException {
    tree = new Tree( shell, SWT.VIRTUAL );
    item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Fixture.preserveWidgets();

    new TreeItem( tree, SWT.NONE, 0 );
    tree.clear( 1, false );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( item, "index" ).asInt() );
  }

  @Test
  public void testRenderIndexWithParentItem() throws IOException {
    TreeItem rootItem = new TreeItem( tree, SWT.NONE );
    new TreeItem( rootItem, SWT.NONE );
    TreeItem item = new TreeItem( rootItem, SWT.NONE );

    new TreeItem( rootItem, SWT.NONE, 0 );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( item, "index" ).asInt() );
  }

  @Test
  public void testRenderIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    new TreeItem( tree, SWT.NONE, 0 );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "index" ) );
  }

  @Test
  public void testRenderInitialItemCount() throws IOException {
    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "itemCount" ) );
  }

  @Test
  public void testRenderItemCount() throws IOException {
    item.setItemCount( 10 );

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( item, "itemCount" ).asInt() );
  }

  @Test
  public void testRenderItemCountUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "itemCount" ) );
  }

  @Test
  public void testRenderInitialTexts() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "texts" ) );
  }

  @Test
  public void testRenderTexts() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    item.setText( new String[] { "item 0.0", "item 0.1" } );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray().add( "item 0.0").add( "item 0.1" );
    assertEquals( expected, message.findSetProperty( item, "texts" ) );
  }

  @Test
  public void testRenderTextsUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setText( new String[] { "item 0.0", "item 0.1" } );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "texts" ) );
  }

  @Test
  public void testRenderTextsReset() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
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
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "images" ) );
  }

  @Test
  public void testRenderImages() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    Image image = createImage( display, Fixture.IMAGE1 );

    item.setImage( new Image[] { null, image } );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray()
      .add( JsonValue.NULL )
      .add( new JsonArray().add( "rwt-resources/generated/90fb0bfe.gif" ).add( 58 ).add( 12 ) );
    assertEquals( expected, message.findSetProperty( item, "images" ) );
  }

  @Test
  public void testRenderImagesUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = createImage( display, Fixture.IMAGE1 );

    item.setImage( new Image[] { null, image } );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "images" ) );
  }

  @Test
  public void testRenderImagesReset() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = createImage( display, Fixture.IMAGE1 );
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
    assertFalse( operation.getProperties().names().contains( "background" ) );
  }

  @Test
  public void testRenderBackground() throws IOException {
    item.setBackground( display.getSystemColor( SWT.COLOR_GREEN ) );

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[0, 255, 0, 255]" );
    assertEquals( expected, message.findSetProperty( item, "background" ) );
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
    assertFalse( operation.getProperties().names().contains( "foreground" ) );
  }

  @Test
  public void testRenderForeground() throws IOException {
    item.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[0, 255, 0, 255]" );
    assertEquals( expected, message.findSetProperty( item, "foreground" ) );
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
    assertFalse( operation.getProperties().names().contains( "font" ) );
  }

  @Test
  public void testRenderFont() throws IOException {
    item.setFont( new Font( display, "Arial", 20, SWT.BOLD ) );

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    Object expected = JsonArray.readFrom( "[[\"Arial\"], 20, true, false]" );
    assertEquals( expected, message.findSetProperty( item, "font" ) );
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
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "cellBackgrounds" ) );
  }

  @Test
  public void testRenderCellBackgrounds() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    item.setBackground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[null, [0, 255, 0, 255]]" );
    assertEquals( expected, message.findSetProperty( item, "cellBackgrounds" ) );
  }

  @Test
  public void testRenderCellBackgroundsUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
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
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
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
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "cellForegrounds" ) );
  }

  @Test
  public void testRenderCellForegrounds() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    item.setForeground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[null, [0, 255, 0, 255]]" );
    assertEquals( expected, message.findSetProperty( item, "cellForegrounds" ) );
  }

  @Test
  public void testRenderCellForegroundsUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
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
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
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
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "cellFonts" ) );
  }

  @Test
  public void testRenderCellFonts() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );

    item.setFont( 1, new Font( display, "Arial", 20, SWT.BOLD ) );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[null, [[\"Arial\"], 20, true, false]]" );
    assertEquals( expected, message.findSetProperty( item, "cellFonts" ) );
  }

  @Test
  public void testRenderCellFontsUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
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
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
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
    assertFalse( operation.getProperties().names().contains( "expanded" ) );
  }

  @Test
  public void testRenderExpanded() throws IOException {
    new TreeItem( item, SWT.NONE );

    item.setExpanded( true );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( item, "expanded" ) );
  }

  @Test
  public void testRenderExpandedUnchanged() throws IOException {
    new TreeItem( item, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setExpanded( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "expanded" ) );
  }

  @Test
  public void testRenderInitialChecked() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "checked" ) );
  }

  @Test
  public void testRenderChecked() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    item = new TreeItem( tree, SWT.NONE );

    item.setChecked( true );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( item, "checked" ) );
  }

  @Test
  public void testRenderCheckedUnchanged() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setChecked( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "checked" ) );
  }

  @Test
  public void testRenderInitialGrayed() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertFalse( operation.getProperties().names().contains( "grayed" ) );
  }

  @Test
  public void testRenderGrayed() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    item = new TreeItem( tree, SWT.NONE );

    item.setGrayed( true );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( item, "grayed" ) );
  }

  @Test
  public void testRenderGrayedUnchanged() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setGrayed( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "grayed" ) );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    item.setData( RWT.CUSTOM_VARIANT, "blue" );

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( item, "customVariant" ).asString() );
  }

  @Test
  public void testRenderData() throws IOException {
    registerDataKeys( new String[]{ "foo", "bar" } );
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
    registerDataKeys( new String[]{ "foo" } );
    item.setData( "foo", "string" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    Fixture.preserveWidgets();
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testRender_onVirtual_rendersOnlyChangedProperties() throws IOException {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    Fixture.markInitialized( item );
    lca.preserveValues( item );
    item.setText( "foo" );

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( item, "texts" ) );
    assertNull( message.findSetOperation( item, "itemCount" ) );
    assertNull( message.findSetOperation( item, "height" ) );
    assertNull( message.findSetOperation( item, "images" ) );
    assertNull( message.findSetOperation( item, "checked" ) );
    assertNull( message.findSetOperation( item, "grayed" ) );
    assertNull( message.findSetOperation( item, "font" ) );
    assertNull( message.findSetOperation( item, "foreground" ) );
    assertNull( message.findSetOperation( item, "background" ) );
    assertNull( message.findSetOperation( item, "cellFonts" ) );
    assertNull( message.findSetOperation( item, "cellBackgrounds" ) );
    assertNull( message.findSetOperation( item, "cellForegrounds" ) );
  }

  @Test
  public void testRender_onVirtual_preservesInitializedFlag() throws IOException {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    Fixture.markInitialized( item );
    lca.preserveValues( item );
    item.setText( "foo" );

    lca.renderChanges( item );

    assertTrue( getAdapter( item ).isInitialized() );
  }

  @Test
  public void testRenderClear_onNonInitializedItem() throws IOException {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    item.getText();

    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( item, "clear" ) );
  }

  @Test
  public void testRenderClear_onInitializedItem() throws IOException {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    item.getText();
    Fixture.markInitialized( item );

    lca.preserveValues( item );
    tree.clear( 0, false );
    lca.renderChanges( item );

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( item, "clear" ) );
  }

}
