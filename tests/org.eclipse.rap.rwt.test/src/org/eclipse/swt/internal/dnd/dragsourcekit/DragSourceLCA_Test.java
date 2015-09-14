/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.dragsourcekit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.dnd.DNDUtil.cancel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DragSourceLCA_Test {

  private Control control;
  private Shell shell;
  private Display display;
  private DragSourceLCA lca;
  private DragSource source;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    Fixture.markInitialized( display );
    shell = new Shell( display );
    control = new Label( shell, SWT.NONE );
    source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    lca = DragSourceLCA.INSTANCE;
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( source );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( source );
    assertEquals( "rwt.widgets.DragSource", operation.getType() );
    assertEquals( getId( control ), operation.getProperties().get( "control" ).asString() );
    JsonArray expected = new JsonArray().add( "DROP_COPY" ).add( "DROP_MOVE" );
    assertEquals( expected, operation.getProperties().get( "style" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( source );
    lca.renderInitialization( source );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof DragSourceOperationHandler );
  }

  @Test
  public void testRenderTransfer() throws IOException {
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();

    source.setTransfer( new Transfer[]{
      TextTransfer.getInstance(),
      HTMLTransfer.getInstance()
    } );
    lca.renderChanges( source );

    TestMessage message = Fixture.getProtocolMessage();
    SetOperation setOperation = message.findSetOperation( source, "transfer" );
    JsonArray expected = new JsonArray()
      .add( Integer.toString( TextTransfer.getInstance().getSupportedTypes()[ 0 ].type ) )
      .add( Integer.toString( HTMLTransfer.getInstance().getSupportedTypes()[ 0 ].type ) );
    assertEquals( expected, setOperation.getProperties().get( "transfer" ) );
  }

  @Test
  public void testRenderCancel() throws IOException {
    Button targetControl = new Button( shell, SWT.PUSH );
    new DropTarget( targetControl, DND.DROP_MOVE | DND.DROP_COPY );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();

    cancel();
    lca.renderChanges( source );

    TestMessage message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( source, "cancel" );
    assertNotNull( call );
  }

  @Test
  public void testDisposeDragControl() {
    shell.open();
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    control.dispose();

    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findDestroyOperation( control ) );
    assertNull( message.findDestroyOperation( source ) );
  }

  @Test
  public void testDisposeDragSourceAndControl() {
    shell.open();
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    source.dispose();
    control.dispose();

    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    assertNotNull( message.findDestroyOperation( control ) );
    assertNull( message.findDestroyOperation( source ) );
  }

  @Test
  public void testRenderAddDragListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();

    source.addDragListener( mock( DragSourceListener.class ) );
    lca.renderChanges( source );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( source, "DragStart" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( source, "DragEnd" ) );
  }

  @Test
  public void testRenderRemoveDragListener() throws Exception {
    DragSourceListener listener = mock( DragSourceListener.class );
    source.addDragListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();

    source.removeDragListener( listener );
    lca.renderChanges( source );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( source, "DragStart" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( source, "DragEnd" ) );
  }

  @Test
  public void testRenderDragListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();

    source.addDragListener( mock( DragSourceListener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( source );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( source, "DragStart" ) );
    assertNull( message.findListenOperation( source, "DragEnd" ) );
  }

}
