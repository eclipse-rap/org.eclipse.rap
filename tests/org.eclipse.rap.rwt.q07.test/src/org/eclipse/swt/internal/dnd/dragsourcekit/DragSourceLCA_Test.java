/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.dragsourcekit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.widgets.*;


public class DragSourceLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  
  public void testDisposeDragControl() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    new DragSource( dragSourceControl, DND.DROP_MOVE );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    dragSourceControl.dispose();
    Fixture.executeLifeCycleFromServerThread();
    // expected: ... wm.dispose( "w3" ); ... deregisterDragSource( "w3" )
    String markup = Fixture.getAllMarkup();
    int unregisterPos = markup.indexOf( "deregisterDragSource" );
    int disposePos = markup.indexOf( "wm.dispose" );
    assertTrue( disposePos > -1 );
    assertTrue( unregisterPos > -1 );
    assertTrue( unregisterPos < disposePos );
  }

  public void testDisposeDragsourceAndControl() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Control dragSourceControl = new Label( shell, SWT.NONE );
    DragSource dragSource = new DragSource( dragSourceControl, DND.DROP_MOVE );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    dragSource.dispose();
    dragSourceControl.dispose();
    Fixture.executeLifeCycleFromServerThread();
    // expected: ... wm.dispose( "w3" ); ... deregisterDragSource( "w3" )
    String markup = Fixture.getAllMarkup();
    int unregisterPos = markup.indexOf( "deregisterDragSource" );
    int disposePos = markup.indexOf( "wm.dispose" );
    assertTrue( disposePos > -1 );
    assertTrue( unregisterPos > -1 );
    assertTrue( unregisterPos < disposePos );
  }
}
