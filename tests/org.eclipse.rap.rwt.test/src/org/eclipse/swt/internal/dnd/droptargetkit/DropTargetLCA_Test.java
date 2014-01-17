/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.droptargetkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.dnd.DNDUtil.setDataTypeChanged;
import static org.eclipse.swt.internal.dnd.DNDUtil.setDetailChanged;
import static org.eclipse.swt.internal.dnd.DNDUtil.setFeedbackChanged;
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
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DropTargetLCA_Test {

  private Control control;
  private Shell shell;
  private Display display;
  private DropTargetLCA lca;
  private DropTarget target;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    Fixture.markInitialized( display );
    shell = new Shell( display );
    control = new Label( shell, SWT.NONE );
    target = new DropTarget( control, DND.DROP_MOVE | DND.DROP_COPY );
    lca = new DropTargetLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( target );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( target );
    assertEquals( "rwt.widgets.DropTarget", operation.getType() );
    assertEquals( getId( control ), operation.getProperty( "control" ).asString() );
    JsonArray expected = new JsonArray().add( "DROP_COPY" ).add( "DROP_MOVE" );
    assertEquals( expected, operation.getProperty( "style" ) );
  }

  @Test
  public void testRenderTransfer() throws IOException {
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();

    target.setTransfer( new Transfer[]{
      TextTransfer.getInstance(),
      HTMLTransfer.getInstance()
    } );
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    SetOperation setOperation = message.findSetOperation( target, "transfer" );
    JsonArray expected = new JsonArray();
    expected.add( Integer.toString( TextTransfer.getInstance().getSupportedTypes()[ 0 ].type ) );
    expected.add( Integer.toString( HTMLTransfer.getInstance().getSupportedTypes()[ 0 ].type ) );
    assertEquals( expected, setOperation.getProperty( "transfer" ) );
  }

  @Test
  public void testDisposeDropControl() {
    shell.open();
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    control.dispose();
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findDestroyOperation( control ) );
    assertNull( message.findDestroyOperation( target ) );
  }

  @Test
  public void testDisposeDroptargetAndControl() {
    shell.open();
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    target.dispose();
    control.dispose();
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findDestroyOperation( control ) );
    assertNull( message.findDestroyOperation( target ) );
  }

  @Test
  public void testRenderAddDropListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();

    target.addDropListener( mock( DropTargetListener.class ) );
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( target, "DragEnter" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( target, "DragOver" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( target, "DragLeave" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( target, "DragOperationChanged" ) );
    assertEquals( JsonValue.TRUE, message.findListenProperty( target, "DropAccept" ) );
  }

  @Test
  public void testRenderRemoveDropListener() throws Exception {
    DropTargetListener listener = mock( DropTargetListener.class );
    target.addDropListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();

    target.removeDropListener( listener );
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( target, "DragEnter" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( target, "DragOver" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( target, "DragLeave" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( target, "DragOperationChanged" ) );
    assertEquals( JsonValue.FALSE, message.findListenProperty( target, "DropAccept" ) );
  }

  @Test
  public void testRenderDropListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();

    target.addDropListener( mock( DropTargetListener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( target, "DragEnter" ) );
    assertNull( message.findListenOperation( target, "DragOver" ) );
    assertNull( message.findListenOperation( target, "DragLeave" ) );
    assertNull( message.findListenOperation( target, "DragOperationChanged" ) );
    assertNull( message.findListenOperation( target, "DropAccept" ) );
  }

  @Test
  public void testRenderDataType() throws IOException {
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();
    TransferData dataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ];

    setDataTypeChanged( control, dataType );
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( target, "changeDataType" );
    assertEquals( dataType.type, call.getProperty( "dataType" ).asInt() );
  }

  @Test
  public void testRenderDataType_isIgnoredForOtherControl() throws IOException {
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();
    TransferData dataType = TextTransfer.getInstance().getSupportedTypes()[ 0 ];

    setDataTypeChanged( mock( Control.class ), dataType );
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( target, "changeDataType" ) );
  }

  @Test
  public void testRenderFeedback() throws IOException {
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();
    int feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;

    setFeedbackChanged( control, feedback );
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( target, "changeFeedback" );
    assertEquals( feedback, call.getProperty( "flags" ).asInt() );
    JsonArray expected = new JsonArray().add( "FEEDBACK_SCROLL" ).add( "FEEDBACK_SELECT" );
    assertEquals( expected, call.getProperty( "feedback" ) );
  }

  @Test
  public void testRenderFeedback_isIgnoredForOtherControl() throws IOException {
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();
    int feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;

    setFeedbackChanged( mock( Control.class ), feedback );
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( target, "changeFeedback" ) );
  }

  @Test
  public void testRenderDetail() throws IOException {
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();

    setDetailChanged( control, DND.DROP_COPY );
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( target, "changeDetail" );
    assertEquals( "DROP_COPY", call.getProperty( "detail" ).asString() );
  }

  @Test
  public void testRenderDetail_isIgnoredForOtherControl() throws IOException {
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();

    setDetailChanged( mock( Control.class ), DND.DROP_COPY );
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( target, "changeDetail" ) );
  }

  @Test
  public void testRenderDetailNone() throws IOException {
    Fixture.markInitialized( target );
    Fixture.preserveWidgets();

    setDetailChanged( control, DND.DROP_NONE );
    lca.renderChanges( target );

    Message message = Fixture.getProtocolMessage();
    CallOperation call = message.findCallOperation( target, "changeDetail" );
    assertEquals( "DROP_NONE", call.getProperty( "detail" ).asString() );
  }

}
