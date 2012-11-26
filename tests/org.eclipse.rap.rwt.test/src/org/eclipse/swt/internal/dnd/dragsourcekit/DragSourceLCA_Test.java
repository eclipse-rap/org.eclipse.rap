/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.dragsourcekit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
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
import org.json.JSONArray;
import org.json.JSONException;


public class DragSourceLCA_Test extends TestCase {

  private Control control;
  private Shell shell;
  private Display display;
  private DragSourceLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    Fixture.markInitialized( display );
    shell = new Shell( display );
    control = new Label( shell, SWT.NONE );
    lca = new DragSourceLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testRenderCreate() throws IOException, JSONException {
    DragSource source = new DragSource( control, DND.DROP_MOVE | DND.DROP_COPY );
    lca.renderInitialization( source );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( source );
    assertEquals( "rwt.widgets.DragSource", operation.getType() );
    assertEquals( WidgetUtil.getId( control ), operation.getProperty( "control" ) );
    String result = ( ( JSONArray )operation.getProperty( "style" ) ).join( "," );
    assertEquals( "\"DROP_COPY\",\"DROP_MOVE\"", result );
  }

  public void testRenderTransfer() throws IOException, JSONException {
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
    String result = ( ( JSONArray )setOperation.getProperty( "transfer" ) ).join( "," );
    String expected = "\"";
    expected += TextTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    expected += "\",\"";
    expected += HTMLTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    expected += "\"";
    assertEquals( expected, result );
  }

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
    assertEquals( WidgetUtil.getId( targetControl ), call.getProperty( "control" ) );
    assertEquals( "DROP_COPY", call.getProperty( "detail" ) );
  }

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
    assertEquals( WidgetUtil.getId( targetControl ), call.getProperty( "control" ) );
    assertEquals( "DROP_NONE", call.getProperty( "detail" ) );
  }

  public void testRenderFeedback() throws IOException, JSONException {
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
    assertEquals( WidgetUtil.getId( targetControl ), call.getProperty( "control" ) );
    assertEquals( new Integer( feedback ), call.getProperty( "flags" ) );
    JSONArray feedbackArr = ( JSONArray )call.getProperty( "feedback" );
    assertEquals( "\"FEEDBACK_SCROLL\",\"FEEDBACK_SELECT\"", feedbackArr.join( "," ) );
  }

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
    assertEquals( WidgetUtil.getId( targetControl ), call.getProperty( "control" ) );
    assertEquals( new Integer( dataType.type ), call.getProperty( "dataType" ) );
  }

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

}
