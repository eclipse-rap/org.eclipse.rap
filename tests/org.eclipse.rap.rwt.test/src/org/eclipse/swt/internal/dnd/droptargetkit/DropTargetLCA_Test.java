/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
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
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
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

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    Fixture.markInitialized( display );
    shell = new Shell( display );
    control = new Label( shell, SWT.NONE );
    lca = new DropTargetLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderCreate() throws IOException {
    DropTarget target = new DropTarget( control, DND.DROP_MOVE | DND.DROP_COPY );
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
    DropTarget target = new DropTarget( control, DND.DROP_MOVE | DND.DROP_COPY );
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
    DropTarget target = new DropTarget( control, DND.DROP_COPY );
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
    DropTarget target = new DropTarget( control, DND.DROP_COPY );
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
    DropTarget target = new DropTarget( control, DND.DROP_COPY );
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
    DropTarget target = new DropTarget( control, DND.DROP_COPY );
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
    DropTarget target = new DropTarget( control, DND.DROP_COPY );
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

}
