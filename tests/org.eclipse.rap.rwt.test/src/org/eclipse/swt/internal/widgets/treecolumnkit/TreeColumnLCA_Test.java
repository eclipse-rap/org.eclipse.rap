/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treecolumnkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.registerDataKeys;
import static org.eclipse.rap.rwt.testfixture.Fixture.getProtocolMessage;
import static org.eclipse.rap.rwt.testfixture.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.eclipse.swt.internal.widgets.treecolumnkit.TreeColumnLCA.getLeft;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TreeColumnLCA_Test {

  private Display display;
  private Shell shell;
  private Tree tree;
  private TreeColumn column;
  private TreeColumnLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    tree = new Tree( shell, SWT.NONE );
    column = new TreeColumn( tree, SWT.NONE );
    lca = new TreeColumnLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testPreserveValues() throws IOException {
    column = new TreeColumn( tree, SWT.CENTER );
    Fixture.markInitialized( display );
    // text
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( column );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    column.setText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    // image
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    Fixture.clearPreserved();
    Image image = createImage( display, Fixture.IMAGE1 );
    column.setImage( image );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertSame( image, adapter.getPreserved( Props.IMAGE ) );
    Fixture.clearPreserved();
    // tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( null, column.getToolTipText() );
    Fixture.clearPreserved();
    column.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    assertEquals( "some text", column.getToolTipText() );
    Fixture.clearPreserved();
    // alignment
    column.setAlignment( SWT.LEFT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    String alignment = ( String )adapter.getPreserved( TreeColumnLCA.PROP_ALIGNMENT );
    assertEquals( "left", alignment );
    Fixture.clearPreserved();
    column.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    alignment = ( String )adapter.getPreserved( TreeColumnLCA.PROP_ALIGNMENT );
    assertEquals( "right", alignment );
    Fixture.clearPreserved();
    column.setAlignment( SWT.CENTER );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    alignment = ( String )adapter.getPreserved( TreeColumnLCA.PROP_ALIGNMENT );
    assertEquals( "center", alignment );
    Fixture.clearPreserved();
    // left,resizable,moveable,selection_listeners,width
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    Object left = adapter.getPreserved( TreeColumnLCA.PROP_LEFT );
    assertEquals( new Integer( getLeft( column ) ), left );
    Object resizable = adapter.getPreserved( TreeColumnLCA.PROP_RESIZABLE );
    assertEquals( Boolean.TRUE, resizable );
    Object moveable = adapter.getPreserved( TreeColumnLCA.PROP_MOVEABLE );
    assertEquals( Boolean.FALSE, moveable );
    Fixture.clearPreserved();
    column.setMoveable( true );
    column.setResizable( false );
    column.setWidth( 30 );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    column.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( column );
    left = adapter.getPreserved( TreeColumnLCA.PROP_LEFT );
    assertEquals( new Integer( getLeft( column ) ), left );
    resizable = adapter.getPreserved( TreeColumnLCA.PROP_RESIZABLE );
    assertEquals( Boolean.FALSE, resizable );
    moveable = adapter.getPreserved( TreeColumnLCA.PROP_MOVEABLE );
    assertEquals( Boolean.TRUE, moveable );
    Object width = adapter.getPreserved( TreeColumnLCA.PROP_WIDTH );
    assertEquals( new Integer( 30 ), width );
  }

  @Test
  public void testGetLeft() {
    column.setWidth( 10 );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setWidth( 10 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setWidth( 10 );
    // Test with natural column order
    assertEquals( 0, getLeft( column ) );
    assertEquals( 10, getLeft( column1 ) );
    assertEquals( 20, getLeft( column2 ) );
    // Test with reverted column order
    tree.setColumnOrder( new int[]{
      2, 1, 0
    } );
    assertEquals( 0, getLeft( column2 ) );
    assertEquals( 10, getLeft( column1 ) );
    assertEquals( 20, getLeft( column ) );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( "rwt.widgets.GridColumn", operation.getType() );
  }

  @Test
  public void testRenderCreateWithAligment() throws IOException {
    column = new TreeColumn( tree, SWT.RIGHT );

    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "style" ) );
    assertEquals( "right", message.findCreateProperty( column, "alignment" ).asString() );
  }

  @Test
  public void testRenderCreate_setsOperationHandler() throws IOException {
    String id = getId( column );

    lca.renderInitialization( column );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof TreeColumnOperationHandler );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( getId( column.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderInitialIndex() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.findCreateProperty( column, "index" ).asInt() );
  }

  @Test
  public void testRenderIndex() throws IOException {
    new TreeColumn( tree, SWT.NONE, 0 );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findSetProperty( column, "index" ).asInt() );
  }

  @Test
  public void testRenderIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    new TreeColumn( tree, SWT.NONE, 0 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "index" ) );
  }

  @Test
  public void testRenderIntialToolTipMarkupEnabled() throws IOException {
    column.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );

    lca.renderChanges( column );

    TestMessage message = getProtocolMessage();
    assertTrue( "foo", message.findSetProperty( column, "toolTipMarkupEnabled" ).asBoolean() );
  }

  @Test
  public void testRenderToolTipMarkupEnabled() throws IOException {
    column.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    Fixture.markInitialized( column );

    lca.renderChanges( column );

    TestMessage message = getProtocolMessage();
    assertNull( message.findSetOperation( column, "toolTipMarkupEnabled" ) );
  }

  @Test
  public void testRenderInitialToolTip() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "toolTip" ) );
  }

  @Test
  public void testRenderToolTip() throws IOException {
    column.setToolTipText( "foo" );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( column, "toolTip" ).asString() );
  }

  @Test
  public void testRenderToolTipUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setToolTipText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "toolTip" ) );
  }

  @Test
  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "customVariant" ) );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    column.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( column, "customVariant" ).asString() );
  }

  @Test
  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setData( RWT.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "customVariant" ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    column.setText( "foo" );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( column, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "text" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "image" ) );
  }

  @Test
  public void testRenderImage() throws IOException {
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    column.setImage( image );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 100 ).add( 50 );
    assertEquals( expected, message.findSetProperty( column, "image" ) );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Image image = createImage( display, Fixture.IMAGE_100x50 );

    column.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "image" ) );
  }

  @Test
  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Image image = createImage( display, Fixture.IMAGE_100x50 );
    column.setImage( image );

    Fixture.preserveWidgets();
    column.setImage( null );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( column, "image" ) );
  }

  @Test
  public void testRenderInitialLeft() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "left" ) );
  }

  @Test
  public void testRenderLeft() throws IOException {
    TreeColumn col2 = new TreeColumn( tree, SWT.NONE, 0 );
    col2.setWidth( 50 );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 50, message.findSetProperty( column, "left" ).asInt() );
  }

  @Test
  public void testRenderLeftUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    TreeColumn col2 = new TreeColumn( tree, SWT.NONE, 0 );
    col2.setWidth( 50 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "left" ) );
  }

  @Test
  public void testRenderInitialWidth() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "width" ) );
  }

  @Test
  public void testRenderWidth() throws IOException {
    column.setWidth( 50 );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 50, message.findSetProperty( column, "width" ).asInt() );
  }

  @Test
  public void testRenderWidthUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setWidth( 50 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "width" ) );
  }

  @Test
  public void testRenderInitialResizable() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "resizable" ) );
  }

  @Test
  public void testRenderResizable() throws IOException {
    column.setResizable( false );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( column, "resizable" ) );
  }

  @Test
  public void testRenderResizableUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setResizable( false );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "resizable" ) );
  }

  @Test
  public void testRenderInitialMoveable() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "moveable" ) );
  }

  @Test
  public void testRenderMoveable() throws IOException {
    column.setMoveable( true );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( column, "moveable" ) );
  }

  @Test
  public void testRenderMoveableUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setMoveable( true );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "moveable" ) );
  }

  @Test
  public void testRenderInitialAlignment() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "alignment" ) );
  }

  @Test
  public void testRenderAlignment() throws IOException {
    column.setAlignment( SWT.RIGHT );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "right", message.findSetProperty( column, "alignment" ).asString() );
  }

  @Test
  public void testRenderAlignmentUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "alignment" ) );
  }

  @Test
  public void testRenderInitialFixed() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "fixed" ) );
  }

  @Test
  public void testRenderFixed() throws IOException {
    tree.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( column, "fixed" ) );
  }

  @Test
  public void testRenderFixedUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    tree.setData( "fixedColumns", Integer.valueOf( 1 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "fixed" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Fixture.preserveWidgets();

    column.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( column, "Selection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    column.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Fixture.preserveWidgets();

    column.removeListener( SWT.Selection, listener );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( column, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Fixture.preserveWidgets();

    column.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( column, "selection" ) );
  }

  @Test
  public void testRenderInitialFont() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertFalse( operation.getProperties().names().contains( "font" ) );
  }

  @Test
  public void testRenderFont() throws IOException {
    tree.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = JsonArray.readFrom( "[[\"Arial\"], 12, false, false ]" );
    assertEquals( expected, message.findSetProperty( column, "font" ) );
  }

  @Test
  public void testRenderFontUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    tree.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "font" ) );
  }

  @Test
  public void testResetFont() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    tree.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    Fixture.preserveWidgets();
    tree.setFont( null );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( column, "font" ) );
  }

  @Test
  public void testRenderData() throws IOException {
    registerDataKeys( new String[]{ "foo", "bar" } );
    column.setData( "foo", "string" );
    column.setData( "bar", Integer.valueOf( 1 ) );

    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    JsonObject data = ( JsonObject )message.findSetProperty( column, "data" );
    assertEquals( "string", data.get( "foo" ).asString() );
    assertEquals( 1, data.get( "bar" ).asInt() );
  }

  @Test
  public void testRenderDataUnchanged() throws IOException {
    registerDataKeys( new String[]{ "foo" } );
    column.setData( "foo", "string" );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

}
