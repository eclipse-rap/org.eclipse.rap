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
package org.eclipse.nebula.widgets.grid.internal.gridcolumnkit;

import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridColumns;
import static org.eclipse.nebula.widgets.grid.GridTestUtil.loadImage;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.testfixture.internal.Fixture.getProtocolMessage;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
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
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("restriction")
public class GridColumnLCA_Test {

  private Display display;
  private Shell shell;
  private Grid grid;
  private GridColumn column;
  private GridColumnLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    grid = new Grid( shell, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
    column = new GridColumn( grid, SWT.NONE );
    lca = ( GridColumnLCA )WidgetUtil.getLCA( column );
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( "rwt.widgets.GridColumn", operation.getType() );
    assertFalse( operation.getProperties().names().contains( "group" ) );
  }

  @Test
  public void testRenderCreateWithAligment() throws IOException {
    column = new GridColumn( grid, SWT.RIGHT );

    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "style" ) == -1 );
    assertEquals( "right", message.findCreateProperty( column, "alignment" ).asString() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( column );

    lca.renderInitialization( column );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof GridColumnOperationHandler );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( WidgetUtil.getId( column.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderGroup() throws IOException {
    GridColumnGroup group = new GridColumnGroup( grid, SWT.NONE );
    column = new GridColumn( group, SWT.NONE );

    lca.renderInitialization( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertEquals( getId( group ), operation.getProperties().get( "group" ).asString() );
  }

  @Test
  public void testRenderDispose() throws IOException {
    lca.renderDispose( column );

    TestMessage message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( column ), operation.getTarget() );
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
    assertTrue( operation.getProperties().names().indexOf( "toolTip" ) == -1 );
  }

  @Test
  public void testRenderToolTip() throws IOException {
    column.setHeaderTooltip( "foo" );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( column, "toolTip" ).asString() );
  }

  @Test
  public void testRenderToolTipUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setHeaderTooltip( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "toolTip" ) );
  }

  @Test
  public void testRenderCustomVariant() throws IOException {
    column.setData( RWT.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( column, "customVariant" ).asString() );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "text" ) == -1 );
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
    Image image = loadImage( display, Fixture.IMAGE_100x50 );

    column.setImage( image );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JsonValue actual = message.findSetProperty( column, "image" );
    assertEquals( JsonArray.readFrom( expected ), actual );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );

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
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    column.setImage( image );

    Fixture.preserveWidgets();
    column.setImage( null );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( column, "image" ) );
  }

  @Test
  public void testRenderInitialIndex() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 1, message.findCreateProperty( column, "index" ).asInt() );
  }

  @Test
  public void testRenderIndex() throws IOException {
    new GridColumn( grid, SWT.NONE, 0 );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( column, "index" ).asInt() );
  }

  @Test
  public void testRenderIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    new GridColumn( grid, SWT.NONE, 0 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "index" ) );
  }

  @Test
  public void testRenderInitialLeft() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "left" ) == -1 );
  }

  @Test
  public void testRenderLeft() throws IOException {
    GridColumn column2 = new GridColumn( grid, SWT.NONE, 0 );
    column2.setWidth( 50 );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 50, message.findSetProperty( column, "left" ).asInt() );
  }

  @Test
  public void testRenderLeftWithRowHeader() throws IOException {
    grid.setRowHeaderVisible( true, 10 );
    GridColumn column2 = new GridColumn( grid, SWT.NONE, 0 );
    column2.setWidth( 50 );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 60, message.findSetProperty( column, "left" ).asInt() );
  }

  @Test
  public void testRenderLeftUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    GridColumn column2 = new GridColumn( grid, SWT.NONE, 0 );
    column2.setWidth( 50 );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "left" ) );
  }

  @Test
  public void testRenderInitialWidth() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findCreateProperty( column, "width" ).asInt() );
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
  public void testRenderInitialAlignment() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "alignment" ) == -1 );
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
  public void testRenderInitialResizable() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "resizable" ) == -1 );
  }

  @Test
  public void testRenderResizable() throws IOException {
    column.setResizeable( false );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( column, "resizable" ) );
  }

  @Test
  public void testRenderResizableUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setResizeable( false );
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
    assertTrue( operation.getProperties().names().indexOf( "moveable" ) == -1 );
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
  public void testRenderInitialVisible() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "visibility" ) == -1 );
  }

  @Test
  public void testRenderVisible() throws IOException {
    column.setVisible( false );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( column, "visibility" ) );
  }

  @Test
  public void testRenderVisibleUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setVisible( false );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "visibility" ) );
  }

  @Test
  public void testRenderInitialCheck() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "check" ) == -1 );
  }

  @Test
  public void testRenderCheck() throws IOException {
    column = new GridColumn( grid, SWT.CHECK );

    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( column, "check" ) );
  }

  @Test
  public void testRenderCheckUnchanged() throws IOException {
    column = new GridColumn( grid, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "check" ) );
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

    column.addListener( SWT.Selection, mock( Listener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( column, "Selection" ) );
  }

  @Test
  public void testReadWidth() {
    final List<Event> events = new LinkedList<Event>();
    GridColumn[] columns = createGridColumns( grid, 2, SWT.NONE );
    columns[ 0 ].addListener( SWT.Resize, new LoggingControlListener( events ) );
    columns[ 1 ].addListener( SWT.Move, new LoggingControlListener( events ) );

    // Simulate request that initializes widgets
    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread();
    // Simulate request that changes column width
    int newWidth = columns[ 0 ].getWidth() + 2;
    int newLeft = column.getWidth() + newWidth;
    Fixture.fakeNewRequest();
    JsonObject parameters = new JsonObject().add( "width", newWidth );
    Fixture.fakeCallOperation( getId( columns[ 0 ] ), "resize", parameters );
    Fixture.executeLifeCycleFromServerThread();

    assertEquals( 2, events.size() );
    Event event = events.get( 0 );
    assertSame( columns[ 0 ], event.widget );
    assertEquals( newWidth, columns[ 0 ].getWidth() );
    event = events.get( 1 );
    assertSame( columns[ 1 ], event.widget );
    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( newWidth, message.findSetProperty( columns[ 0 ], "width" ).asInt() );
    assertEquals( newLeft, message.findSetProperty( columns[ 1 ], "left" ).asInt() );
  }

  @Test
  public void testReadLeft() {
    final List<Event> events = new LinkedList<Event>();
    GridColumn[] columns = createGridColumns( grid, 2, SWT.NONE );
    column.addListener( SWT.Move, new LoggingControlListener( events ) );
    columns[ 0 ].addListener( SWT.Move, new LoggingControlListener( events ) );
    columns[ 1 ].addListener( SWT.Move, new LoggingControlListener( events ) );

    // Simulate request that initializes widgets
    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread();
    // Simulate request that changes column left
    int newLeft = 3;
    Fixture.fakeNewRequest();
    JsonObject parameters = new JsonObject().add( "left", newLeft );
    Fixture.fakeCallOperation( getId( columns[ 0 ] ), "move", parameters );
    Fixture.executeLifeCycleFromServerThread();

    assertEquals( 2, events.size() );
    Event event = events.get( 0 );
    assertSame( columns[ 0 ], event.widget );
    event = events.get( 1 );
    assertSame( column, event.widget );
    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 20, message.findSetProperty( column, "left" ).asInt() );
    assertEquals( 0, message.findSetProperty( columns[ 0 ], "left" ).asInt() );
  }

  @Test
  public void testRenderInitialFont() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "font" ) == -1 );
  }

  @Test
  public void testRenderParentFont() throws IOException {
    grid.setFont( new Font( display, "Arial", 20, SWT.BOLD ) );

    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findCreateProperty( column, "font" );
    assertEquals( JsonArray.readFrom( "[[\"Arial\"], 20, true, false]" ), actual );
  }

  @Test
  public void testRenderFont() throws IOException {
    column.setHeaderFont( new Font( display, "Arial", 20, SWT.BOLD ) );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( column, "font" );
    assertEquals( JsonArray.readFrom( "[[\"Arial\"], 20, true, false]" ), actual );
  }

  @Test
  public void testRenderFontUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setHeaderFont( new Font( display, "Arial", 20, SWT.BOLD ) );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "font" ) );
  }

  @Test
  public void testRenderInitialFooterFont() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "footerFont" ) == -1 );
  }

  @Test
  public void testRenderFooterFont() throws IOException {
    column.setFooterFont( new Font( display, "Arial", 20, SWT.BOLD ) );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    JsonValue actual = message.findSetProperty( column, "footerFont" );
    assertEquals( JsonArray.readFrom( "[[\"Arial\"], 20, true, false]" ), actual );
  }

  @Test
  public void testRenderFooterFontUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setFooterFont( new Font( display, "Arial", 20, SWT.BOLD ) );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "footerFont" ) );
  }

  @Test
  public void testRenderInitialFooterText() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "footerText" ) == -1 );
  }

  @Test
  public void testRenderFooterText() throws IOException {
    column.setFooterText( "foo" );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( column, "footerText" ).asString() );
  }

  @Test
  public void testRenderFooterTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setFooterText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "footerText" ) );
  }

  @Test
  public void testRenderInitialFooterImage() throws IOException {
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "footerImage" ) );
  }

  @Test
  public void testRenderFooterImage() throws IOException {
    Image image = loadImage( display, Fixture.IMAGE_100x50 );

    column.setFooterImage( image );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = JsonArray.readFrom( "[\"" + imageLocation + "\", 100, 50 ]" );
    assertEquals( expected, message.findSetProperty( column, "footerImage" ) );
  }

  @Test
  public void testRenderFooterImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );

    column.setFooterImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "footerImage" ) );
  }

  @Test
  public void testRenderFooterImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    Image image = loadImage( display, Fixture.IMAGE_100x50 );
    column.setFooterImage( image );

    Fixture.preserveWidgets();
    column.setFooterImage( null );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( column, "footerImage" ) );
  }

  @Test
  public void testRenderInitialFooterSpan() throws IOException {
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "footerSpan" ) );
  }

  @Test
  public void testRenderFooterSpan() throws IOException {
    createGridColumns( grid, 3, SWT.NONE );
    column.setData( "footerSpan", Integer.valueOf( 2 ) );

    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( column, "footerSpan" ).asInt() );
  }

  @Test
  public void testRenderFooterSpanUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );
    createGridColumns( grid, 3, SWT.NONE );
    column.setData( "footerSpan", Integer.valueOf( 2 ) );

    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "footerSpan" ) );
  }

  @Test
  public void testRenderInitialWordWrap() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "wordWrap" ) == -1 );
  }

  @Test
  public void testRenderWordWrap() throws IOException {
    column.setWordWrap( true );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( column, "wordWrap" ) );
  }

  @Test
  public void testRenderWordWrapUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setWordWrap( true );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "wordWrap" ) );
  }

  @Test
  public void testRenderInitialHeaderWordWrap() throws IOException {
    lca.render( column );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( column );
    assertTrue( operation.getProperties().names().indexOf( "headerWordWrap" ) == -1 );
  }

  @Test
  public void testRenderHeaderWordWrap() throws IOException {
    column.setHeaderWordWrap( true );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( column, "headerWordWrap" ) );
  }

  @Test
  public void testRenderHeaderWordWrapUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    column.setHeaderWordWrap( true );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "headerWordWrap" ) );
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
    grid.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( column, "fixed" ) );
  }

  @Test
  public void testRenderFixedUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( column );

    grid.setData( RWT.FIXED_COLUMNS, Integer.valueOf( 1 ) );
    Fixture.preserveWidgets();
    lca.renderChanges( column );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( column, "fixed" ) );
  }

  //////////////////
  // Helping classes

  private static class LoggingControlListener implements Listener {
    private final List<Event> events;
    private LoggingControlListener( List<Event> events ) {
      this.events = events;
    }
    @Override
    public void handleEvent( Event event ) {
      events.add( event );
    }
  }

}
