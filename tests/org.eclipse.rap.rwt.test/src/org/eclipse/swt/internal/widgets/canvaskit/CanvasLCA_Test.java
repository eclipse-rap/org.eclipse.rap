/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.canvaskit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.rap.rwt.internal.protocol.IClientObjectAdapter;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.internal.graphics.GCAdapter;
import org.eclipse.swt.internal.graphics.GCOperation.DrawLine;
import org.eclipse.swt.internal.graphics.GCOperation.SetProperty;
import org.eclipse.swt.internal.graphics.IGCAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.json.JSONArray;
import org.json.JSONException;


public class CanvasLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private CanvasLCA lca;
  private Canvas canvas;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new CanvasLCA();
    Fixture.fakeNewRequest( display );
    canvas = new Canvas( shell, SWT.NONE );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( canvas );
    ControlLCATestUtil.testFocusListener( canvas );
    ControlLCATestUtil.testMouseListener( canvas );
    ControlLCATestUtil.testKeyListener( canvas );
    ControlLCATestUtil.testTraverseListener( canvas );
    ControlLCATestUtil.testMenuDetectListener( canvas );
    ControlLCATestUtil.testHelpListener( canvas );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( canvas );

    Message message = Fixture.getProtocolMessage();
    CreateOperation canvasCreate = message.findCreateOperation( canvas );
    assertEquals( "rwt.widgets.Canvas", canvasCreate.getType() );
    assertEquals( WidgetUtil.getId( shell ), canvasCreate.getProperty( "parent" ) );
    String canvasId = WidgetUtil.getId( canvas );
    CreateOperation gcCreate = message.findCreateOperation( getGcId( canvas ) );
    assertEquals( "rwt.widgets.GC", gcCreate.getType() );
    assertEquals( canvasId, gcCreate.getProperty( "parent" ) );
  }

  public void testWriqteSingleGCOperation() throws IOException, JSONException {
    canvas.setSize( 50, 50 );
    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
    Fixture.markInitialized( display );
    Fixture.markInitialized( canvas );
    Fixture.preserveWidgets();
    GCAdapter adapter = ( GCAdapter )canvas.getAdapter( IGCAdapter.class );

    adapter.addGCOperation( new DrawLine( 1, 2, 3, 4 ) );
    new CanvasLCA().renderChanges( canvas );

    CallOperation init = getGCOperation( canvas, "init" );
    assertEquals( new Integer( 50 ), init.getProperty( "width" ) );
    assertEquals( new Integer( 50 ), init.getProperty( "height" ) );
    JSONArray fontArray = ( JSONArray )init.getProperty( "font" );
    assertEquals( "Arial", fontArray.getJSONArray( 0 ).getString( 0 ) );
    assertEquals( 11, fontArray.getInt( 1 ) );
    assertFalse( fontArray.getBoolean( 2 ) );
    assertFalse( fontArray.getBoolean( 3 ) );
    String fillStyle = ( ( JSONArray )init.getProperty( "fillStyle" ) ).join( "," );
    String strokeStyle = ( ( JSONArray )init.getProperty( "strokeStyle" ) ).join( "," );
    assertEquals( "255,255,255,255", fillStyle );
    assertEquals( "74,74,74,255", strokeStyle );
    CallOperation draw = getGCOperation( canvas, "draw" );
    JSONArray operations = ( JSONArray )draw.getProperty( "operations" );
    assertEquals( 4, operations.length() );
  }

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
    JSONArray operations = ( JSONArray )draw.getProperty( "operations" );
    assertEquals( 8, operations.length() );
  }

  // see bug 323080
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
    JSONArray operations = ( JSONArray )draw.getProperty( "operations" );
    assertEquals( 8, operations.length() );
    assertEquals( 0, adapter.getGCOperations().length );
  }

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
    assertEquals( new Integer( 150 ), init.getProperty( "width" ) );
    assertEquals( new Integer( 150 ), init.getProperty( "height" ) );
    CallOperation draw = getGCOperation( canvas, "draw" );
    JSONArray operations = ( JSONArray )draw.getProperty( "operations" );
    assertEquals( 8, operations.length() );
  }

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
    JSONArray operations = ( JSONArray )draw.getProperty( "operations" );
    assertEquals( 8, operations.length() );
  }

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

//  TODO [tb] : re-enable
//  public void testRenderOperations_DisposedFont() throws IOException {
//    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
//    canvas.setSize( 50, 50 );
//    canvas.setFont( new Font( display, "Arial", 11, SWT.NORMAL ) );
//    Fixture.markInitialized( display );
//    Fixture.markInitialized( canvas );
//    Fixture.preserveWidgets();
//
//    canvas.addPaintListener( new PaintListener() {
//      public void paintControl( PaintEvent event ) {
//        Font font = new Font( display, "Verdana", 18, SWT.BOLD );
//        event.gc.setFont( font );
//        event.gc.drawLine( 1, 2, 3, 4 );
//        font.dispose();
//      }
//    } );
//
//    Fixture.fakeResponseWriter();
//    canvas.redraw();
//    new CanvasLCA().renderChanges( canvas );
//    String expected
//      = "var gc = org.eclipse.rwt.protocol.ObjectRegistry.getObject( \"w2#gc\" );"
//      + "gc.init( 50, 50, \"11px Arial\", \"#ffffff\", \"#4a4a4a\" );"
//      + "gc.setProperty( \"font\", \"bold 18px Verdana\" );"
//      + "gc.drawLine( 1, 2, 3, 4 );";
//    assertTrue( Fixture.getAllMarkup().contains( expected ) );
//  }

  private static CallOperation getGCOperation( Canvas canvas, String method ) {
    Message message = Fixture.getProtocolMessage();
    String id = getGcId( canvas );
    return message.findCallOperation( id, method );
  }

  static String getGcId( Widget widget ) {
    WidgetAdapter adapter = ( WidgetAdapter )widget.getAdapter( IWidgetAdapter.class );
    Adaptable gcForClient = adapter.getGCForClient();
    return gcForClient.getAdapter( IClientObjectAdapter.class ).getId();
  }

}
