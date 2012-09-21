/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getId;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class FocusEvent_Test extends TestCase {

  private Display display;
  private Shell shell;
  private List<FocusEvent> events;
  private FocusAdapter listener;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    shell.open();
    events = new ArrayList<FocusEvent>();
    listener = new FocusAdapter() {
      @Override
      public void focusLost( FocusEvent event ) {
        events.add( event );
      }
      @Override
      public void focusGained( FocusEvent event ) {
        events.add( event );
      }
    };
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCopyFieldsFromUntypedEvent() {
    Button button = new Button( shell, SWT.PUSH );
    button.addFocusListener( listener );
    Object data = new Object();
    Event event = new Event();
    event.data = data;

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    button.notifyListeners( SWT.FocusIn, event );

    assertEquals( 1, events.size() );
    FocusEvent focusEvent = events.get( 0 );
    assertSame( button, focusEvent.getSource() );
    assertSame( button, focusEvent.widget );
    assertSame( display, focusEvent.display );
    assertSame( data, focusEvent.data );
    assertEquals( SWT.FocusIn, focusEvent.getID() );
  }

  public void testFocusLost() {
    Control unfocusControl = new Button( shell, SWT.PUSH );
    unfocusControl.setFocus();
    unfocusControl.addFocusListener( listener );
    Control focusControl = new Button( shell, SWT.PUSH );

    Fixture.fakeSetParameter( getId( display ), "focusControl", getId( focusControl ) );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, events.size() );
    FocusEvent event = events.get( 0 );
    assertEquals( FocusEvent.FOCUS_LOST, event.getID() );
    assertSame( unfocusControl, event.getSource() );
  }

  public void testFocusGained() {
    Control control = new Button( shell, SWT.PUSH );
    control.addFocusListener( listener );

    Fixture.fakeSetParameter( getId( display ), "focusControl", getId( control ) );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, events.size() );
    FocusEvent event = events.get( 0 );
    assertEquals( FocusEvent.FOCUS_GAINED, event.getID() );
    assertSame( control, event.getSource() );
  }

  public void testFocusGainedLostOrder() {
    Button button1 = new Button( shell, SWT.PUSH );
    button1.addFocusListener( listener );
    Button button2 = new Button( shell, SWT.PUSH );
    button2.addFocusListener( listener );
    button1.setFocus();
    events.clear();

    Fixture.fakeSetParameter( getId( display ), "focusControl", getId( button2 ) );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 2, events.size() );
    FocusEvent event1 = events.get( 0 );
    assertEquals( FocusEvent.FOCUS_LOST, event1.getID() );
    assertSame( button1, event1.widget );
    FocusEvent event2 = events.get( 1 );
    assertEquals( FocusEvent.FOCUS_GAINED, event2.getID() );
    assertSame( button2, event2.widget );
  }
}
