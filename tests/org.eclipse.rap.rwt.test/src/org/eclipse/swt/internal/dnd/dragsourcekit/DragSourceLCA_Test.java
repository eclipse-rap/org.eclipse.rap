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

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
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

  public void testRenderDetail() throws IOException, JSONException {
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

  public void testDisposeDragControl() {
    new DragSource( control, DND.DROP_MOVE );
    shell.open();
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    control.dispose();
    Fixture.getProtocolMessage();
    Fixture.executeLifeCycleFromServerThread();
    // expected: ... deregisterDragSource( "w3" ) ... "action": "destroy"
    Fixture.getProtocolMessage();
    String markup = Fixture.getAllMarkup();
    int unregisterPos = markup.indexOf( "deregisterDragSource" );
    int disposePos = markup.indexOf( "\"action\": \"destroy\"" );
    assertTrue( disposePos > -1 );
    assertTrue( unregisterPos > -1 );
    assertTrue( unregisterPos < disposePos );
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
    // expected: ... deregisterDragSource( "w3" ) ... "action": "destroy"
    Fixture.getProtocolMessage();
    String markup = Fixture.getAllMarkup();
    int unregisterPos = markup.indexOf( "deregisterDragSource" );
    int disposePos = markup.indexOf( "\"action\": \"destroy\"" );
    assertTrue( disposePos > -1 );
    assertTrue( unregisterPos > -1 );
    assertTrue( unregisterPos < disposePos );
  }

}
