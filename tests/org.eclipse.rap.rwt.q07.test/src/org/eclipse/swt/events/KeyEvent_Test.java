/*******************************************************************************
 * Copyright (c) 2010 EclipseSource
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


public class KeyEvent_Test extends TestCase {

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  public void testCopyFieldsFromUntypedEvent() {
    final List log = new ArrayList();
    Button button = new Button( shell, SWT.PUSH );
    button.addKeyListener( new KeyAdapter() {
      public void keyPressed( final KeyEvent event ) {
        log.add( event );
      }
    } );
    Object data = new Object();
    Event event = new Event();
    event.stateMask = 23;
    event.keyCode = 42;
    event.character = 'f';
    event.doit = true;
    event.data = data;
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    button.notifyListeners( SWT.KeyDown, event );
    KeyEvent keyEvent = ( KeyEvent )log.get( 0 );
    assertSame( button, keyEvent.getSource() );
    assertSame( button, keyEvent.widget );
    assertSame( display, keyEvent.display );
    assertSame( data, keyEvent.data );
    assertEquals( 23, keyEvent.stateMask );
    assertEquals( 42, keyEvent.keyCode );
    assertEquals( 'f', keyEvent.character );
    assertEquals( true, keyEvent.doit );
    assertEquals( SWT.KeyDown, keyEvent.getID() );
  }
}
