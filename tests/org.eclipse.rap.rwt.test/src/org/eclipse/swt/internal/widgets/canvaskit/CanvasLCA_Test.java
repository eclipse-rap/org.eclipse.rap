/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.canvaskit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.rap.rwt.internal.protocol.IClientObjectAdapter;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.GCAdapter;
import org.eclipse.swt.internal.graphics.GCOperation.DrawLine;
import org.eclipse.swt.internal.graphics.GCOperation.SetProperty;
import org.eclipse.swt.internal.graphics.IGCAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapterImpl;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CanvasLCA_Test {

  private Display display;
  private Shell shell;
  private CanvasLCA lca;
  private Canvas canvas;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new CanvasLCA();
    Fixture.fakeNewRequest();
    canvas = new Canvas( shell, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( canvas );
    ControlLCATestUtil.testFocusListener( canvas );
    ControlLCATestUtil.testMouseListener( canvas );
    ControlLCATestUtil.testKeyListener( canvas );
    ControlLCATestUtil.testTraverseListener( canvas );
    ControlLCATestUtil.testMenuDetectListener( canvas );
    ControlLCATestUtil.testHelpListener( canvas );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( canvas );

    Message message = Fixture.getProtocolMessage();
    CreateOperation canvasCreate = message.findCreateOperation( canvas );
    assertEquals( "rwt.widgets.Canvas", canvasCreate.getType() );
    assertEquals( getId( shell ), canvasCreate.getProperty( "parent" ).asString() );
    String canvasId = getId( canvas );
    CreateOperation gcCreate = message.findCreateOperation( getGcId( canvas ) );
    assertEquals( "rwt.widgets.GC", gcCreate.getType() );
    assertEquals( canvasId, gcCreate.getProperty( "parent" ).asString() );
  }

  @Test
  public void testWriqteSingleGCOperation() throws IOException {
    canvas.setSize( 50, 50 );
    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( canvas );
    Fixture.preserveWidgets();
    GCAdapter adapter = ( GCAdapter )canvas.getAdapter( IGCAdapter.class );

    adapter.addGCOperation( new DrawLine( 1, 2, 3, 4 ) );
    new CanvasLCA().renderChanges( canvas );

    CallOperation init = getGCOperation( canvas, "init" );
    assertEquals( 50, init.getProperty( "width" ).asInt() );
    assertEquals( 50, init.getProperty( "height" ).asInt() );
    JsonArray expectedFont = JsonArray.readFrom( "[[\"Arial\"], 11, false, false]" );
    assertEquals( expectedFont, init.getProperty( "font" ) );
    JsonArray expectedFillStyle = JsonArray.readFrom( "[255, 255, 255, 255]" );
    assertEquals( expectedFillStyle, init.getProperty( "fillStyle" ) );
    JsonArray expectedStrokeStyle = JsonArray.readFrom( "[74, 74, 74, 255]" );
    assertEquals( expectedStrokeStyle, init.getProperty( "strokeStyle" ) );
    CallOperation draw = getGCOperation( canvas, "draw" );
    assertEquals( 4, draw.getProperty( "operations" ).asArray().size() );
  }

  @Test
  public void testWriteMultipleGCOperations() throws IOException {
    canvas.setSize( 50, 50 );
    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( canvas );
    Fixture.preserveWidgets();
    GCAdapter adapter = ( GCAdapter )canvas.getAdapter( IGCAdapter.class );

    adapter.addGCOperation( new DrawLine( 1, 2, 3, 4 ) );
    adapter.addGCOperation( new DrawLine( 5, 6, 7, 8 ) );
    new CanvasLCA().renderChanges( canvas );

    CallOperation draw = getGCOperation( canvas, "draw" );
    assertEquals( 8, draw.getProperty( "operations" ).asArray().size() );
  }

  // see bug 323080
  @Test
  public void testMultipleGC_SetFont() throws IOException {
    canvas.setSize( 50, 50 );
    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( canvas );
    Fixture.preserveWidgets();

    GC gc = new GC( canvas );
    gc.setFont( new Font( display, "Tahoma", 16, SWT.BOLD ) );
    gc.dispose();
    gc = new GC( canvas );
    gc.setFont( new Font( display, "Tahoma", 16, SWT.BOLD ) );
    gc.dispose();
    gc = new GC( canvas );
    gc.setFont( new Font( display, "Tahoma", 16, SWT.BOLD ) );
    gc.dispose();
    new CanvasLCA().renderChanges( canvas );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testTrimTrailingSetOperations() throws IOException {
    canvas.setSize( 50, 50 );
    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( canvas );
    Fixture.preserveWidgets();
    GCAdapter adapter = ( GCAdapter )canvas.getAdapter( IGCAdapter.class );
    adapter.addGCOperation( new DrawLine( 1, 2, 3, 4 ) );
    adapter.addGCOperation( new DrawLine( 5, 6, 7, 8 ) );
    Font font = new Font( display, "Arial", 15, SWT.NORMAL );

    adapter.addGCOperation( new SetProperty( font.getFontData()[ 0 ] ) );
    SetProperty operation = new SetProperty( SetProperty.LINE_WIDTH, 5 );
    adapter.addGCOperation( operation );
    new CanvasLCA().renderChanges( canvas );

    CallOperation draw = getGCOperation( canvas, "draw" );
    assertEquals( 8, draw.getProperty( "operations" ).asArray().size() );
    assertEquals( 0, adapter.getGCOperations().length );
  }

  @Test
  public void testNoDrawOperations() throws IOException {
    canvas.setSize( 50, 50 );
    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( canvas );
    Fixture.preserveWidgets();
    GCAdapter adapter = ( GCAdapter )canvas.getAdapter( IGCAdapter.class );
    Font font = new Font( display, "Arial", 15, SWT.NORMAL );

    adapter.addGCOperation( new SetProperty( font.getFontData()[ 0 ] ) );
    SetProperty operation = new SetProperty( SetProperty.LINE_WIDTH, 5 );
    adapter.addGCOperation( operation );
    new CanvasLCA().renderChanges( canvas );

    assertNull( getGCOperation( canvas, "draw" ) );
    assertEquals( 0, adapter.getGCOperations().length );
  }

  @Test
  public void testRenderOperations_empty() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    canvas.setSize( 50, 50 );
    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( canvas );
    Fixture.preserveWidgets();
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( PaintEvent event ) {
        event.gc.drawLine( 1, 2, 3, 4 );
        event.gc.drawLine( 5, 6, 7, 8 );
      }
    } );
    Fixture.fakeResponseWriter();

    new CanvasLCA().renderChanges( canvas );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testRenderOperations_resize() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Canvas canvas = new Canvas( shell, SWT.NONE );
    canvas.setSize( 50, 50 );
    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( canvas );
    Fixture.preserveWidgets();
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( PaintEvent event ) {
        event.gc.drawLine( 1, 2, 3, 4 );
        event.gc.drawLine( 5, 6, 7, 8 );
      }
    } );
    Fixture.fakeResponseWriter();

    canvas.setSize( 150, 150 );
    new CanvasLCA().renderChanges( canvas );

    CallOperation init = getGCOperation( canvas, "init" );
    assertEquals( 150, init.getProperty( "width" ).asInt() );
    assertEquals( 150, init.getProperty( "height" ).asInt() );
    CallOperation draw = getGCOperation( canvas, "draw" );
    assertEquals( 8, draw.getProperty( "operations" ).asArray().size() );
  }

  @Test
  public void testRenderOperations_redraw() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    canvas.setSize( 50, 50 );
    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( canvas );
    Fixture.preserveWidgets();
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( PaintEvent event ) {
        event.gc.drawLine( 1, 2, 3, 4 );
        event.gc.drawLine( 5, 6, 7, 8 );
      }
    } );
    Fixture.fakeResponseWriter();

    canvas.redraw();
    new CanvasLCA().renderChanges( canvas );

    CallOperation draw = getGCOperation( canvas, "draw" );
    assertEquals( 8, draw.getProperty( "operations" ).asArray().size() );
  }

  @Test
  public void testClearDrawing() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    canvas.setSize( 50, 50 );
    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
    canvas.getAdapter( IGCAdapter.class );
    Fixture.markInitialized( display );
    Fixture.markInitialized( canvas );
    Fixture.preserveWidgets();
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( PaintEvent event ) {
      }
    } );
    Fixture.fakeResponseWriter();

    canvas.redraw();
    new CanvasLCA().renderChanges( canvas );

    assertNotNull( getGCOperation( canvas, "init" ) );
    assertNull( getGCOperation( canvas, "draw" ) );
  }

  @Test
  public void testRenderClientArea() {
    canvas.setSize( 110, 120 );

    lca.renderClientArea( canvas );

    Message message = Fixture.getProtocolMessage();
    Rectangle clientArea = canvas.getClientArea();
    assertEquals( clientArea, toRectangle( message.findSetProperty( canvas, "clientArea" ) ) );
  }

  @Test
  public void testRenderClientArea_SizeZero() {
    canvas.setSize( 0, 0 );

    lca.renderClientArea( canvas );

    Message message = Fixture.getProtocolMessage();
    Rectangle clientArea = new Rectangle( 0, 0, 0, 0 );
    assertEquals( clientArea, toRectangle( message.findSetProperty( canvas, "clientArea" ) ) );
  }

  @Test
  public void testRenderClientArea_SizeUnchanged() {
    Fixture.markInitialized( canvas );
    canvas.setSize( 110, 120 );

    lca.preserveValues( canvas );
    lca.renderClientArea( canvas );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( canvas, "clientArea" ) );
  }

  private Rectangle toRectangle( Object property ) {
    JsonArray jsonArray = ( JsonArray )property;
    Rectangle result = new Rectangle(
      jsonArray.get( 0 ).asInt(),
      jsonArray.get( 1 ).asInt(),
      jsonArray.get( 2 ).asInt(),
      jsonArray.get( 3 ).asInt()
    );
    return result;
  }

  private static CallOperation getGCOperation( Canvas canvas, String method ) {
    Message message = Fixture.getProtocolMessage();
    String id = getGcId( canvas );
    return message.findCallOperation( id, method );
  }

  static String getGcId( Widget widget ) {
    WidgetAdapterImpl adapter = ( WidgetAdapterImpl )widget.getAdapter( WidgetAdapter.class );
    Adaptable gcForClient = adapter.getGCForClient();
    return gcForClient.getAdapter( IClientObjectAdapter.class ).getId();
  }

}
