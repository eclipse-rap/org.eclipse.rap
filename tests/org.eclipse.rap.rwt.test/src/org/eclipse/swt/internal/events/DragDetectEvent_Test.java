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
package org.eclipse.swt.internal.events;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.EventTestHelper;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public class DragDetectEvent_Test extends TestCase {

  private Display display;
  private Shell shell;

  public void testUntypedEventConstructor() throws Exception {
    Event event = new Event();
    event.display = display;
    event.widget = mock( Widget.class );
    event.data = new Object();
    event.x = 10;
    event.y = 20;
    event.button = 2;
    event.stateMask = 23;
    
    DragDetectEvent dragEvent = new DragDetectEvent( event );
    
    EventTestHelper.assertFieldsEqual( dragEvent, event );
  }
  
  public void testDragDetectEvent() {
    final java.util.List<Event> log = new ArrayList<Event>();
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
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
