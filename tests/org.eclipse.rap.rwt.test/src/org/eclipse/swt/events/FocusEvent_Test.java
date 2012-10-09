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
import static org.mockito.Mockito.mock;

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
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testUntypedEventConstructor() throws Exception {
    Event event = new Event();
    event.display = display;
    event.widget = mock( Widget.class );
    event.data = new Object();
    
    FocusEvent focusEvent = new FocusEvent( event );
    
    EventTestHelper.assertFieldsEqual( focusEvent, event );
  }

  public void testFocusLost() {
    Button unfocusControl = new Button( shell, SWT.PUSH );
    unfocusControl.setText( "unfocusControl" );
    unfocusControl.setFocus();
    unfocusControl.addFocusListener( listener );
    Button focusControl = new Button( shell, SWT.PUSH );
    focusControl.setText( "focusControl" );
    
    Fixture.fakeSetParameter( getId( display ), "focusControl", getId( focusControl ) );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, events.size() );
    FocusEvent event = events.get( 0 );
    assertEquals( SWT.FocusOut, event.getID() );
    assertSame( unfocusControl, event.getSource() );
  }

  public void testFocusGained() {
    Control control = new Button( shell, SWT.PUSH );
    control.addFocusListener( listener );

    Fixture.fakeSetParameter( getId( display ), "focusControl", getId( control ) );
    Fixture.readDataAndProcessAction( display );

    assertEquals( 1, events.size() );
    FocusEvent event = events.get( 0 );
    assertEquals( SWT.FocusIn, event.getID() );
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
    assertEquals( SWT.FocusOut, event1.getID() );
    assertSame( button1, event1.widget );
    FocusEvent event2 = events.get( 1 );
    assertEquals( SWT.FocusIn, event2.getID() );
    assertSame( button2, event2.widget );
  }
}
