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
package org.eclipse.swt.internal.widgets.treeitemkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.IWidgetColorAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("deprecation")
public class TreeItemLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private Tree tree;
  private TreeItemLCA lca;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    tree = new Tree( shell, SWT.NONE );
    lca = new TreeItemLCA();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    Fixture.markInitialized( display );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    new TreeColumn( tree, SWT.NONE, 0 );
    new TreeColumn( tree, SWT.NONE, 1 );
    new TreeColumn( tree, SWT.NONE, 2 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    treeItem.setText( "qwert" );
    new TreeItem( treeItem, SWT.NONE, 0 );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    treeItem.setImage( image );
    treeItem.setExpanded( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    String[] texts = ( String[] )adapter.getPreserved( TreeItemLCA.PROP_TEXTS );
    assertEquals( "qwert", texts[ 0 ] );
    assertEquals( Boolean.TRUE, adapter.getPreserved( TreeItemLCA.PROP_EXPANDED ) );
    Image[] images = ( Image[] )adapter.getPreserved( TreeItemLCA.PROP_IMAGES );
    assertEquals( image, images[ 0 ] );
    IWidgetColorAdapter colorAdapter = treeItem.getAdapter( IWidgetColorAdapter.class );
    Object background = adapter.getPreserved( TreeItemLCA.PROP_BACKGROUND );
    assertEquals( colorAdapter.getUserBackground(), background );
    Object foreground = adapter.getPreserved( TreeItemLCA.PROP_FOREGROUND );
    assertEquals( colorAdapter.getUserForeground(), foreground );
    Font[] fonts = ( Font[] )adapter.getPreserved( TreeItemLCA.PROP_FONT );
    assertNull( fonts );
    Color[] backgrounds = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_BACKGROUNDS );
    assertTrue( Arrays.equals( new Color[ 3 ], backgrounds ) );
    Color[] foregrounds = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_FOREGROUNDS );
    assertTrue( Arrays.equals( new Color[ 3 ], foregrounds ) );
    Fixture.clearPreserved();
    treeItem.setText( 0, "item11" );
    treeItem.setText( 1, "item12" );
    treeItem.setText( 2, "item13" );
    treeItem.setImage( 0, Graphics.getImage( Fixture.IMAGE1 ) );
    treeItem.setImage( 1, Graphics.getImage( Fixture.IMAGE2 ) );
    treeItem.setImage( 2, Graphics.getImage( Fixture.IMAGE3 ) );
    tree.setSelection( treeItem );
    background = Graphics.getColor( 234, 113, 34 );
    treeItem.setBackground( ( Color )background );
    foreground = Graphics.getColor( 122, 232, 45 );
    treeItem.setForeground( ( Color )foreground );
    Font font1 = Graphics.getFont( "font1", 10, 1 );
    treeItem.setFont( 0, font1 );
    Font font2 = Graphics.getFont( "font1", 8, 1 );
    treeItem.setFont( 1, font2 );
    Font font3 = Graphics.getFont( "font1", 6, 1 );
    treeItem.setFont( 2, font3 );
    Color background1 = Graphics.getColor( 234, 230, 54 );
    treeItem.setBackground( 0, background1 );
    Color background2 = Graphics.getColor( 145, 222, 134 );
    treeItem.setBackground( 1, background2 );
    Color background3 = Graphics.getColor( 143, 134, 34 );
    treeItem.setBackground( 2, background3 );
    Color foreground1 = Graphics.getColor( 77, 77, 54 );
    treeItem.setForeground( 0, foreground1 );
    Color foreground2 = Graphics.getColor( 156, 45, 134 );
    treeItem.setForeground( 1, foreground2 );
    Color foreground3 = Graphics.getColor( 88, 134, 34 );
    treeItem.setForeground( 2, foreground3 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( treeItem );
    texts = ( String[] )adapter.getPreserved( TreeItemLCA.PROP_TEXTS );
    assertEquals( "item11", texts[ 0 ] );
    assertEquals( "item12", texts[ 1 ] );
    assertEquals( "item13", texts[ 2 ] );
    images = ( Image[] )adapter.getPreserved( TreeItemLCA.PROP_IMAGES );
    assertEquals( Graphics.getImage( Fixture.IMAGE1 ), images[ 0 ] );
    assertEquals( Graphics.getImage( Fixture.IMAGE2 ), images[ 1 ] );
    assertEquals( Graphics.getImage( Fixture.IMAGE3 ), images[ 2 ] );
    assertEquals( background, adapter.getPreserved( TreeItemLCA.PROP_BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( TreeItemLCA.PROP_FOREGROUND ) );
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

  public void testCheckPreserveValues() {
    tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    assertEquals( Boolean.FALSE, adapter.getPreserved( TreeItemLCA.PROP_CHECKED ) );
    assertEquals( Boolean.FALSE, adapter.getPreserved( TreeItemLCA.PROP_GRAYED ) );
    Fixture.clearPreserved();
    treeItem.setChecked( true );
    treeItem.setGrayed( true );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( treeItem );
    assertEquals( Boolean.TRUE, adapter.getPreserved( TreeItemLCA.PROP_CHECKED ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( TreeItemLCA.PROP_GRAYED ) );
  }

  public void testLcaDoesNotMaterializeItem() {
    tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );
    TreeItem treeItem = tree.getItem( 99 );
    shell.open();
    Fixture.executeLifeCycleFromServerThread();
    ITreeAdapter adapter = tree.getAdapter( ITreeAdapter.class );
    assertFalse( adapter.isCached( treeItem ) );
  }

  public void testExpandCollapse() {
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    new TreeItem( treeItem, SWT.NONE );
    treeItem.setExpanded( false );

    Fixture.fakeSetParameter( getId( treeItem ), TreeItemLCA.PROP_EXPANDED, Boolean.TRUE  );
    Fixture.readDataAndProcessAction( treeItem );

    assertEquals( true, treeItem.getExpanded() );

    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( treeItem ), TreeItemLCA.PROP_EXPANDED, Boolean.FALSE  );
    Fixture.readDataAndProcessAction( treeItem );

    assertEquals( false, treeItem.getExpanded() );
  }

  public void testExpandedPropertyNotRenderedBack() {
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( treeItem );
    new TreeItem( treeItem, SWT.NONE );
    treeItem.setExpanded( false );

    Fixture.fakeSetParameter( getId( treeItem ), TreeItemLCA.PROP_EXPANDED, Boolean.TRUE  );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( treeItem, TreeItemLCA.PROP_EXPANDED ) );
  }

  public void testChecked() {
    tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );

    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( treeItem ), "checked", "true" );
    Fixture.readDataAndProcessAction( display );

    assertEquals( true, treeItem.getChecked() );
  }

  public void testGetBoundsWithScrolling() {
    TreeItem rootItem = new TreeItem( tree, 0 );
    TreeItem rootItem2 = new TreeItem( tree, 0 );
    TreeItem rootItem3 = new TreeItem( tree, 0 );

    tree.getAdapter( ITreeAdapter.class ).checkData();

    assertEquals( 0, rootItem.getBounds().y );
    assertEquals( 27, rootItem2.getBounds().y );
    assertEquals( 54, rootItem3.getBounds().y );

    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( tree ), "scrollLeft", "0" );
    Fixture.fakeSetParameter( getId( tree ), "topItemIndex", "2" );
    Fixture.readDataAndProcessAction( display );

    assertEquals( -54, rootItem.getBounds().y );
    assertEquals( -27, rootItem2.getBounds().y );
    assertEquals( 0, rootItem3.getBounds().y );
  }

  public void testPreserveInitialItemCount() {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );

    Fixture.preserveWidgets();

    assertEquals( new Integer( 0 ), getPreservedProperty( item, TreeItemLCA.PROP_ITEM_COUNT ) );
  }

  public void testPreserveItemCount() {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );

    item.setItemCount( 10 );
    Fixture.preserveWidgets();

    assertEquals( new Integer( 10 ), getPreservedProperty( item, TreeItemLCA.PROP_ITEM_COUNT ) );
  }

  public void testRenderCreate() throws IOException {
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( "rwt.widgets.GridItem", operation.getType() );
    assertEquals( Integer.valueOf( 1 ), operation.getProperty( "index" ) );
  }

  public void testRenderDispose() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.renderDispose( item );

    Message message = Fixture.getProtocolMessage();
    DestroyOperation operation = ( DestroyOperation )message.getOperation( 0 );
    assertEquals( WidgetUtil.getId( item ), operation.getTarget() );
  }

  public void testRenderDisposeWithDisposedTree() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    tree.dispose();

    lca.renderDispose( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  public void testRenderDisposeWithDisposedParentItem() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subitem = new TreeItem( item, SWT.NONE );
    item.dispose();

    lca.renderDispose( subitem );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  public void testRenderParent() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.renderInitialization( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertEquals( WidgetUtil.getId( item.getParent() ), operation.getParent() );
  }

  public void testRenderInitialItemCount() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "itemCount" ) == -1 );
  }

  public void testRenderItemCount() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setItemCount( 10 );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Integer.valueOf( 10 ), message.findSetProperty( item, "itemCount" ) );
  }

  public void testRenderItemCountUnchanged() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setItemCount( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "itemCount" ) );
  }

  public void testRenderInitialTexts() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "texts" ) == -1 );
  }

  public void testRenderTexts() throws IOException, JSONException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setText( new String[] { "item 0.0", "item 0.1" } );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "texts" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"item 0.0\",\"item 0.1\"]", actual ) );
  }

  public void testRenderTextsUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setText( new String[] { "item 0.0", "item 0.1" } );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "texts" ) );
  }

  public void testRenderInitialImages() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "images" ) == -1 );
  }

  public void testRenderImages() throws IOException, JSONException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Image image = Graphics.getImage( Fixture.IMAGE1 );

    item.setImage( new Image[] { null, image } );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "images" );
    String expected = "[\"rwt-resources/generated/90fb0bfe.gif\",58,12]";
    assertEquals( JSONObject.NULL, actual.get( 0 ) );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual.getJSONArray( 1 ) ) );
  }

  public void testRenderImagesUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Image image = Graphics.getImage( Fixture.IMAGE1 );

    item.setImage( new Image[] { null, image } );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "images" ) );
  }

  public void testRenderInitialBackground() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "background" ) == -1 );
  }

  public void testRenderBackground() throws IOException, JSONException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "background" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", actual ) );
  }

  public void testRenderBackgroundUnchanged() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "background" ) );
  }

  public void testRenderInitialForeground() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "foreground" ) == -1 );
  }

  public void testRenderForeground() throws IOException, JSONException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "foreground" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", actual ) );
  }

  public void testRenderForegroundUnchanged() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "foreground" ) );
  }

  public void testRenderInitialFont() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "font" ) == -1 );
  }

  public void testRenderFont() throws IOException, JSONException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setFont( Graphics.getFont( "Arial", 20, SWT.BOLD ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "font" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"Arial\"]", actual.getJSONArray( 0 ) ) );
    assertEquals( Integer.valueOf( 20 ), actual.get( 1 ) );
    assertEquals( Boolean.TRUE, actual.get( 2 ) );
    assertEquals( Boolean.FALSE, actual.get( 3 ) );
  }

  public void testRenderFontUnchanged() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setFont( Graphics.getFont( "Arial", 20, SWT.BOLD ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "font" ) );
  }

  public void testRenderInitialCellBackgrounds() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "cellBackgrounds" ) == -1 );
  }

  public void testRenderCellBackgrounds() throws IOException, JSONException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setBackground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "cellBackgrounds" );
    assertEquals( JSONObject.NULL, actual.get( 0 ) );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", actual.getJSONArray( 1 ) ) );
  }

  public void testRenderCellBackgroundsUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setBackground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellBackgrounds" ) );
  }

  public void testRenderInitialCellForegrounds() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "cellForegrounds" ) == -1 );
  }

  public void testRenderCellForegrounds() throws IOException, JSONException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setForeground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( item, "cellForegrounds" );
    assertEquals( JSONObject.NULL, actual.get( 0 ) );
    assertTrue( ProtocolTestUtil.jsonEquals( "[0,255,0,255]", actual.getJSONArray( 1 ) ) );
  }

  public void testRenderCellForegroundsUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setForeground( 1, display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellForegrounds" ) );
  }

  public void testRenderInitialCellFonts() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "cellFonts" ) == -1 );
  }

  public void testRenderCellFonts() throws IOException, JSONException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );

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

  public void testRenderCellFontsUnchanged() throws IOException {
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setFont( 1, Graphics.getFont( "Arial", 20, SWT.BOLD ) );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "cellFonts" ) );
  }

  public void testRenderInitialExpanded() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "expanded" ) == -1 );
  }

  public void testRenderExpanded() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    new TreeItem( item, SWT.NONE );

    item.setExpanded( true );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( item, "expanded" ) );
  }

  public void testRenderExpandedUnchanged() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    new TreeItem( item, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setExpanded( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "expanded" ) );
  }

  public void testRenderInitialChecked() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "checked" ) == -1 );
  }

  public void testRenderChecked() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setChecked( true );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( item, "checked" ) );
  }

  public void testRenderCheckedUnchanged() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setChecked( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "checked" ) );
  }

  public void testRenderInitialGrayed() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "grayed" ) == -1 );
  }

  public void testRenderGrayed() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setGrayed( true );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( item, "grayed" ) );
  }

  public void testRenderGrayedUnchanged() throws IOException {
    tree = new Tree( shell, SWT.CHECK );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setGrayed( true );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "grayed" ) );
  }

  public void testRenderInitialCustomVariant() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    lca.render( item );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( item );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  public void testRenderCustomVariant() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );

    item.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( item, "customVariant" ) );
  }

  public void testRenderCustomVariantUnchanged() throws IOException {
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );

    item.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( item );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( item, "customVariant" ) );
  }

  private static Object getPreservedProperty( Widget widget, String property ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    return adapter.getPreserved( property );
  }

}
