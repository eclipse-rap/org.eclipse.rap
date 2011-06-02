/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.widgets.*;


public class DragDetectEvent_Test extends TestCase {

  private Display display;
  private Shell shell;

  public void testCopyFieldsFromUntypedEvent() {
    final List<DragDetectEvent> log = new ArrayList<DragDetectEvent>();
    Button button = new Button( shell, SWT.PUSH );
    button.addDragDetectListener( new DragDetectListener() {
      public void dragDetected( final DragDetectEvent event ) {
        log.add( event );
      }
    } );
    Object data = new Object();
    Event event = new Event();
    event.x = 10;
    event.y = 20;
    event.button = 2;
    event.stateMask = 23;
    event.data = data;
    event.time = 4711;
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    button.notifyListeners( SWT.DragDetect, event );
    DragDetectEvent dragDetectEvent = log.get( 0 );
    assertSame( button, dragDetectEvent.getSource() );
    assertSame( button, dragDetectEvent.widget );
    assertSame( display, dragDetectEvent.display );
    assertSame( data, dragDetectEvent.data );
    assertEquals( 10, dragDetectEvent.x );
    assertEquals( 20, dragDetectEvent.y );
    assertEquals( 2, dragDetectEvent.button );
    assertEquals( 23, dragDetectEvent.stateMask );
    assertEquals( 4711, dragDetectEvent.time );
    assertEquals( SWT.DragDetect, dragDetectEvent.getID() );
  }

  public void testDragDetectEvent() {
    final java.util.List<Event> log = new ArrayList<Event>();
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    };
    shell.addListener( SWT.DragDetect, listener );
    assertTrue( shell.isListening( SWT.DragDetect ) );
    shell.notifyListeners( SWT.DragDetect, new Event() );
    assertEquals( 1, log.size() );
    Event event = log.get( 0 );
    assertSame( display, event.display );
    assertSame( shell, event.widget );
    shell.removeListener( SWT.DragDetect, listener );
    assertFalse( shell.isListening( SWT.DragDetect ) );
    log.clear();
    shell.notifyListeners( SWT.DragDetect, new Event() );
    assertEquals( 0, log.size() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
