/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.droptargetkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
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
  public void testRenderCreate() throws IOException, JSONException {
    DropTarget target = new DropTarget( control, DND.DROP_MOVE | DND.DROP_COPY );
    lca.renderInitialization( target );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( target );
    assertEquals( "rwt.widgets.DropTarget", operation.getType() );
    assertEquals( WidgetUtil.getId( control ), operation.getProperty( "control" ) );
    String result = ( ( JSONArray )operation.getProperty( "style" ) ).join( "," );
    assertEquals( "\"DROP_COPY\",\"DROP_MOVE\"", result );
  }

  @Test
  public void testRenderTransfer() throws IOException, JSONException {
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
    String result = ( ( JSONArray )setOperation.getProperty( "transfer" ) ).join( "," );
    String expected = "\"";
    expected += TextTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    expected += "\",\"";
    expected += HTMLTransfer.getInstance().getSupportedTypes()[ 0 ].type;
    expected += "\"";
    assertEquals( expected, result );
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

}
