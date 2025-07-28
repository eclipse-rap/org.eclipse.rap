/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.canvas;

import static org.eclipse.rap.rwt.addons.internal.canvas.ClientCanvasOperator.DRAWINGS_PROPERTY;
import static org.eclipse.rap.rwt.addons.internal.canvas.ClientCanvasOperator.DRAWING_EVENT;
import static org.eclipse.rap.rwt.addons.internal.canvas.ClientCanvasTestUtil.createDrawings;
import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getLCA;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.widgets.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;
import java.util.Arrays;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.addons.internal.canvas.ClientCanvasOperator;
import org.eclipse.rap.rwt.addons.internal.canvas.ClientDrawListenerAdapter;
import org.eclipse.rap.rwt.addons.internal.canvas.DrawingsCache;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TypedListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings({
  "restriction",
  "deprecation"
})
public class ClientCanvas_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private ClientCanvas clientCanvas;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    Shell shell = new Shell( display );
    clientCanvas = new ClientCanvas( shell, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHasDrawingListeners() {
    ClientDrawListenerAdapter adapter = clientCanvas.getAdapter( ClientDrawListenerAdapter.class );

    assertNotNull( adapter );
  }

  @Test
  public void testIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( ClientCanvas.class ) );
  }

  @Test
  public void testClientDrawListenerIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( ClientDrawListener.class ) );
  }

  @Test
  public void testDrawingCacheIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( DrawingsCache.class ) );
  }

  @SuppressWarnings("serial")
  private class CheckPaintListener implements PaintListener {

    private boolean wasCalled;

    public synchronized boolean wasCalled() {
      return wasCalled;
    }

    @Override
    public synchronized void paintControl( PaintEvent event ) {
      wasCalled = true;
    }
  }

  @Test
  public void testRedrawsNotOnClientDrawingOnly() {
    CheckPaintListener listener = new CheckPaintListener();
    clientCanvas.addPaintListener( listener );

    fakeDrawEvent();

    assertFalse( listener.wasCalled() );
  }

  @Test
  public void testAddDispatchListener() {
    Object[] listeners = filterClientListeners( clientCanvas.getListeners( SWT.Paint ) );

    assertEquals( 1, listeners.length );
  }

  @Test
  public void testAddsDispatchListenerLast() {
    PaintListener listener = mock( PaintListener.class );

    clientCanvas.addPaintListener( listener );

    Object[] listeners = filterClientListeners( clientCanvas.getListeners( SWT.Paint ) );

    assertEquals( listener, ( ( TypedListener )listeners[ 0 ] ).getEventListener() );
    assertEquals( 2, listeners.length );
  }

  @Test
  public void testCachesDrawings() {
    PaintListener paintListener = mock( PaintListener.class );
    clientCanvas.addPaintListener( paintListener );

    fakeDrawEvent();
    fakeNewRequest();
    fakeDrawEvent();

    assertEquals( 1, clientCanvas.getAdapter( DrawingsCache.class ).getCachedDrawings().size() );
  }

  @Test
  public void testClearTriggersRedraw() {
    CheckPaintListener listener = new CheckPaintListener();
    clientCanvas.addPaintListener( listener );

    fakeDrawEvent();
    clientCanvas.clear();

    assertTrue( listener.wasCalled() );
  }

  @Test
  public void testHasUndo() {
    fakeDrawEvent();

    assertTrue( clientCanvas.hasUndo() );
    assertFalse( clientCanvas.hasRedo() );
  }

  @Test
  public void testUndo() {
    fakeDrawEvent();

    clientCanvas.undo();

    assertFalse( clientCanvas.hasUndo() );
    assertTrue( clientCanvas.hasRedo() );
  }

  @Test
  public void testUndoRedraws() {
    CheckPaintListener listener = new CheckPaintListener();
    clientCanvas.addPaintListener( listener );
    fakeDrawEvent();

    clientCanvas.undo();

    assertTrue( listener.wasCalled() );
  }

  @Test
  public void testHasRedo() {
    fakeDrawEvent();

    clientCanvas.undo();

    assertTrue( clientCanvas.hasRedo() );
  }

  @Test
  public void testRedo() {
    fakeDrawEvent();
    clientCanvas.undo();

    clientCanvas.redo();

    assertTrue( clientCanvas.hasUndo() );
    assertFalse( clientCanvas.hasRedo() );
  }

  @Test
  public void testRedoRedraws() {
    CheckPaintListener listener = new CheckPaintListener();
    clientCanvas.addPaintListener( listener );
    fakeDrawEvent();
    clientCanvas.undo();

    clientCanvas.redo();

    assertTrue( listener.wasCalled() );
  }

  @Test
  public void testDrawDeletesRedoStack() {
    clientCanvas.undo();
    fakeDrawEvent();

    assertFalse( clientCanvas.hasRedo() );
  }

  @Test
  public void testFiresDrawingReceived() {
    ClientDrawListener listener = mock( ClientDrawListener.class );
    clientCanvas.addClientDrawListener( listener );

    fakeDrawEvent();

    verify( listener ).receivedDrawing();
  }

  @Test
  public void testFiresDrawingReceivedOnUndo() {
    ClientDrawListener listener = mock( ClientDrawListener.class );
    clientCanvas.addClientDrawListener( listener );

    fakeDrawEvent();
    clientCanvas.undo();

    verify( listener, times( 2 ) ).receivedDrawing();
  }

  @Test
  public void testFiresDrawingReceivedOnRedo() {
    ClientDrawListener listener = mock( ClientDrawListener.class );
    clientCanvas.addClientDrawListener( listener );

    fakeDrawEvent();
    clientCanvas.undo();
    clientCanvas.redo();

    verify( listener, times( 3 ) ).receivedDrawing();
  }

  @Test
  public void testFiresDrawingReceivedOnClear() {
    ClientDrawListener listener = mock( ClientDrawListener.class );
    clientCanvas.addClientDrawListener( listener );

    fakeDrawEvent();
    clientCanvas.clear();

    verify( listener, times( 2 ) ).receivedDrawing();
  }

  @Test
  public void testClearDeletsRedo() {
    ClientDrawListener listener = mock( ClientDrawListener.class );
    clientCanvas.addClientDrawListener( listener );

    fakeDrawEvent();
    clientCanvas.undo();
    assertTrue( clientCanvas.hasRedo() );
    clientCanvas.clear();
    assertFalse( clientCanvas.hasRedo() );
  }

  @Test
  public void testFiresDrawingReceivedOnRemoved() {
    ClientDrawListener listener = mock( ClientDrawListener.class );
    clientCanvas.addClientDrawListener( listener );
    clientCanvas.removeClientDrawListener( listener );

    fakeDrawEvent();

    verify( listener, never() ).receivedDrawing();
  }

  @Test
  public void testOperationHandlerIsExchanged() throws Exception {
    getLCA( display ).render( display );

    RemoteObjectImpl remoteObject = RemoteObjectRegistry.getInstance().get( getId( clientCanvas ) );
    OperationHandler handler = remoteObject.getHandler();
    assertTrue( handler instanceof ClientCanvasOperator );
  }

  private void fakeDrawEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    ClientCanvasOperator handler = new ClientCanvasOperator( clientCanvas );
    getRemoteObject( clientCanvas ).setHandler( handler );
    JsonObject properties = new JsonObject();
    properties.add( DRAWINGS_PROPERTY, createDrawings( 2 ) );
    handler.handleNotify( DRAWING_EVENT, properties );
  }

  private static TestRequest fakeNewRequest() {
    TestRequest request = Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    return request;
  }

  private static Object[] filterClientListeners( Listener[] listeners ) {
    return Arrays.stream( listeners ).filter( el -> !( el instanceof ClientListener ) ).toArray();
  }

}
