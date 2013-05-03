/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.dragsourcekit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.internal.dnd.IDNDAdapter;
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

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    Fixture.markInitialized( display );
    shell = new Shell( display );
    control = new Label( shell, SWT.NONE );
    lca = new DragSourceLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderCreate() throws IOException {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    lca.renderInitialization( source );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( source );
    assertEquals( "rwt.widgets.DragSource", operation.getType() );
    assertEquals( getId( control ), operation.getProperty( "control" ).asString() );
    JsonArray expected = new JsonArray().add( "DROP_COPY" ).add( "DROP_MOVE" );
    assertEquals( expected, operation.getProperty( "style" ) );
  }

  @Test
  public void testRenderTransfer() throws IOException {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();

    source.setTransfer( new Transfer[]{
      TextTransfer.getInstance(),
      HTMLTransfer.getInstance()
    } );
    lca.renderChanges( source );

    Message message = Fixture.getProtocolMessage();
    SetOperation setOperation = message.findSetOperation( source, "transfer" );
    JsonArray expected = new JsonArray()
      .add( Integer.toString( TextTransfer.getInstance().getSupportedTypes()[ 0 ].type ) )
      .add( Integer.toString( HTMLTransfer.getInstance().getSupportedTypes()[ 0 ].type ) );
    assertEquals( expected, setOperation.getProperty( "transfer" ) );
  }

  @Test
  public void testRenderDetail() throws IOException {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    Button targetControl = new Button( shell, SWT.PUSH );
    new DropTarget( targetControl, DND.DROP_MOVE | DND.DROP_COPY );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();
    IDNDAdapter adapter = source.getAdapter( IDNDAdapter.class );

    adapter.setDetailChanged( targetControl, DND.DROP_COPY );
    lca.renderChanges( source );

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( source, "changeDetail" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ).asString() );
    assertEquals( "DROP_COPY", call.getProperty( "detail" ).asString() );
  }

  @Test
  public void testRenderDetailNone() throws IOException {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    Button targetControl = new Button( shell, SWT.PUSH );
    new DropTarget( targetControl, DND.DROP_MOVE | DND.DROP_COPY );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();
    IDNDAdapter adapter = source.getAdapter( IDNDAdapter.class );

    adapter.setDetailChanged( targetControl, DND.DROP_NONE );
    lca.renderChanges( source );

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( source, "changeDetail" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ).asString() );
    assertEquals( "DROP_NONE", call.getProperty( "detail" ).asString() );
  }

  @Test
  public void testRenderFeedback() throws IOException {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    Button targetControl = new Button( shell, SWT.PUSH );
    new DropTarget( targetControl, DND.DROP_MOVE | DND.DROP_COPY );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();
    int feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
    IDNDAdapter adapter = source.getAdapter( IDNDAdapter.class );


    adapter.setFeedbackChanged( targetControl, feedback );
    lca.renderChanges( source );

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( source, "changeFeedback" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ).asString() );
    assertEquals( feedback, call.getProperty( "flags" ).asInt() );
    JsonArray expected = new JsonArray().add( "FEEDBACK_SCROLL" ).add( "FEEDBACK_SELECT" );
    assertEquals( expected, call.getProperty( "feedback" ) );
  }

  @Test
  public void testRenderDataType() throws IOException {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    Button targetControl = new Button( shell, SWT.PUSH );
    new DropTarget( targetControl, DND.DROP_MOVE | DND.DROP_COPY );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();
    IDNDAdapter adapter = source.getAdapter( IDNDAdapter.class );
    TransferData dataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ];

    adapter.setDataTypeChanged( targetControl, dataType );
    lca.renderChanges( source );

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( source, "changeDataType" );
    assertEquals( getId( targetControl ), call.getProperty( "control" ).asString() );
    assertEquals( dataType.type, call.getProperty( "dataType" ).asInt() );
  }

  @Test
  public void testRenderCancel() throws IOException {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    Button targetControl = new Button( shell, SWT.PUSH );
    new DropTarget( targetControl, DND.DROP_MOVE | DND.DROP_COPY );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();
    IDNDAdapter adapter = source.getAdapter( IDNDAdapter.class );

    adapter.cancel();
    lca.renderChanges( source );

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( source, "cancel" );
    assertNotNull( call );
  }

  @Test
  public void testDisposeDragControl() {
    DragSource dragSource = new DragSource( control, DND.DROP_MOVE );
    shell.open();
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    control.dispose();
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findDestroyOperation( control ) );
    assertNull( message.findDestroyOperation( dragSource ) );
  }

  @Test
  public void testDisposeDragSourceAndControl() {
    DragSource dragSource = new DragSource( control, DND.DROP_MOVE );
    shell.open();
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    dragSource.dispose();
    control.dispose();
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findDestroyOperation( control ) );
    assertNull( message.findDestroyOperation( dragSource ) );
  }

  @Test
  public void testRenderAddDragListener() throws Exception {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    Fixture.markInitialized( display );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();

    source.addDragListener( mock( DragSourceListener.class ) );
    lca.renderChanges( source );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( source, "DragStart" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( source, "DragEnd" ) );
  }

  @Test
  public void testRenderRemoveDragListener() throws Exception {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    DragSourceListener listener = mock( DragSourceListener.class );
    source.addDragListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();

    source.removeDragListener( listener );
    lca.renderChanges( source );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( source, "DragStart" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( source, "DragEnd" ) );
  }

  @Test
  public void testRenderDragListenerUnchanged() throws Exception {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    Fixture.markInitialized( display );
    Fixture.markInitialized( source );
    Fixture.preserveWidgets();

    source.addDragListener( mock( DragSourceListener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( source );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( source, "DragStart" ) );
    assertNull( message.findListenOperation( source, "DragEnd" ) );
  }

}
