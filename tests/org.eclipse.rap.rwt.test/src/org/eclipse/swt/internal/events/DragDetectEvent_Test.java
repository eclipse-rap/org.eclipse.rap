/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class DragDetectEvent_Test extends TestCase {

  public void testDragDetectEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    };
    shell.addListener( SWT.DragDetect, listener );
    assertTrue( shell.isListening( SWT.DragDetect ) );
    shell.notifyListeners( SWT.DragDetect, new Event() );
    assertEquals( 1, log.size() );
    Event event = ( Event )log.get( 0 );
    assertSame( display, event.display );
    assertSame( shell, event.widget );
    shell.removeListener( SWT.DragDetect, listener );
    assertFalse( shell.isListening( SWT.DragDetect ) );
    log.clear();
    shell.notifyListeners( SWT.DragDetect, new Event() );
    assertEquals( 0, log.size() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
