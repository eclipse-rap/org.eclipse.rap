/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.events;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class MenuDetectEvent_Test extends TestCase {

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCopyFieldsFromUntypedEvent() {
    final List<MenuDetectEvent> log = new ArrayList<MenuDetectEvent>();
    Button button = new Button( shell, SWT.PUSH );
    button.addMenuDetectListener( new MenuDetectListener() {
      public void menuDetected( final MenuDetectEvent event ) {
        log.add( event );
      }
    } );
    Object data = new Object();
    Event event = new Event();
    event.x = 10;
    event.y = 20;
    event.doit = true;
    event.data = data;
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    button.notifyListeners( SWT.MenuDetect, event );
    MenuDetectEvent menuDetectEvent = log.get( 0 );
    assertSame( button, menuDetectEvent.getSource() );
    assertSame( button, menuDetectEvent.widget );
    assertSame( display, menuDetectEvent.display );
    assertSame( data, menuDetectEvent.data );
    assertEquals( event.x, menuDetectEvent.x );
    assertEquals( event.y, menuDetectEvent.y );
    assertEquals( event.doit, menuDetectEvent.doit );
    assertEquals( SWT.MenuDetect, menuDetectEvent.getID() );
  }
}
