/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class FocusEvent_Test {

  private Display display;
  private Shell shell;
  private FocusListener focusListener;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    shell.open();
    focusListener = mock( FocusListener.class );
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testUntypedEventConstructor() {
    Event event = new Event();
    event.display = display;
    event.widget = mock( Widget.class );
    event.data = new Object();

    FocusEvent focusEvent = new FocusEvent( event );

    EventTestHelper.assertFieldsEqual( focusEvent, event );
  }

  @Test
  public void testFocusLost() {
    Button unfocusControl = new Button( shell, SWT.PUSH );
    unfocusControl.setText( "unfocusControl" );
    unfocusControl.setFocus();
    unfocusControl.addFocusListener( focusListener );
    Button focusControl = new Button( shell, SWT.PUSH );
    focusControl.setText( "focusControl" );

    Fixture.fakeSetProperty( getId( display ), "focusControl", getId( focusControl ) );
    Fixture.readDataAndProcessAction( display );

    verify( focusListener, never() ).focusGained( any( FocusEvent.class ) );
    ArgumentCaptor<FocusEvent> captor = ArgumentCaptor.forClass( FocusEvent.class );
    verify( focusListener ).focusLost( captor.capture() );
    assertEquals( unfocusControl, captor.getValue().widget );
  }

  @Test
  public void testFocusGained() {
    Control control = new Button( shell, SWT.PUSH );
    control.addFocusListener( focusListener );

    Fixture.fakeSetProperty( getId( display ), "focusControl", getId( control ) );
    Fixture.readDataAndProcessAction( display );

    verify( focusListener, never() ).focusLost( any( FocusEvent.class ) );
    ArgumentCaptor<FocusEvent> captor = ArgumentCaptor.forClass( FocusEvent.class );
    verify( focusListener ).focusGained( captor.capture() );
    assertEquals( control, captor.getValue().widget );
  }

  @Test
  public void testFocusGainedLostOrder() {
    Button button1 = new Button( shell, SWT.PUSH );
    Button button2 = new Button( shell, SWT.PUSH );
    button1.setFocus();
    button1.addFocusListener( focusListener );
    button2.addFocusListener( focusListener );

    Fixture.fakeSetProperty( getId( display ), "focusControl", getId( button2 ) );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<FocusEvent> captor1 = ArgumentCaptor.forClass( FocusEvent.class );
    verify( focusListener ).focusLost( captor1.capture() );
    assertEquals( button1, captor1.getValue().widget );
    ArgumentCaptor<FocusEvent> captor2 = ArgumentCaptor.forClass( FocusEvent.class );
    verify( focusListener ).focusGained( captor2.capture() );
    assertEquals( button2, captor2.getValue().widget );
  }

  @Test
  public void testFocusTraverseOrder() {
    Listener listener = mock( Listener.class );
    Button button1 = new Button( shell, SWT.PUSH );
    Button button2 = new Button( shell, SWT.PUSH );
    button1.setFocus();
    button1.addListener( SWT.Traverse, listener );
    button1.addListener( SWT.FocusOut, listener );
    button2.addListener( SWT.FocusIn, listener );

    Fixture.fakeSetProperty( getId( display ), "focusControl", getId( button2 ) );
    JsonObject properties = new JsonObject().add( ClientMessageConst.EVENT_PARAM_KEY_CODE, 9 )
      .add( ClientMessageConst.EVENT_PARAM_CHAR_CODE, 0 )
      .add( ClientMessageConst.EVENT_PARAM_MODIFIER, SWT.None );
    Fixture.fakeNotifyOperation( getId( button1 ), ClientMessageConst.EVENT_TRAVERSE, properties );
    Fixture.fakeNotifyOperation( getId( button1 ), "FocusOut", null );
    Fixture.fakeNotifyOperation( getId( button2 ), "FocusIn", null );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener, times( 3 ) ).handleEvent( captor.capture() );
    List<Event> events = captor.getAllValues();
    assertEquals( SWT.Traverse, events.get( 0 ).type );
    assertEquals( SWT.FocusOut, events.get( 1 ).type );
    assertEquals( SWT.FocusIn, events.get( 2 ).type );
  }

}
