/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
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
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_FOCUS_IN;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonOperationHandler;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TypedEvent_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
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
    event.time = 9;
    event.data = new Object();

    TestTypedEvent typedEvent = new TestTypedEvent( event );

    assertSame( event.widget, typedEvent.getSource() );
    EventTestHelper.assertFieldsEqual( typedEvent, event );
  }

  @Test
  public void testObjectConstructor() {
    Object source = new Object();
    TypedEvent typedEvent = new TypedEvent( source );

    assertSame( source, typedEvent.getSource() );
  }

  @Test
  public void testEventIsFiredInProcessActionPhase() {
    Button button = new Button( shell, SWT.PUSH );
    getRemoteObject( button ).setHandler( new ButtonOperationHandler( button ) );
    Fixture.markInitialized( button );
    final AtomicReference<PhaseId> log = new AtomicReference<PhaseId>();
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        log.set( CurrentPhase.get() );
      }
    } );

    Fixture.fakeNewRequest();
    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( button );

    assertEquals( PhaseId.PROCESS_ACTION, log.get() );
  }

  @Test
  public void testFireFocusEventBeforeMouseEvent() {
    final java.util.List<TypedEvent> eventLog = new ArrayList<TypedEvent>();
    Button button = new Button( shell, SWT.PUSH );
    getRemoteObject( button ).setHandler( new ButtonOperationHandler( button ) );
    Fixture.markInitialized( button );
    button.addMouseListener( new MouseAdapter() {
      @Override
      public void mouseDown( MouseEvent event ) {
        eventLog.add( event );
      }
    } );
    button.addFocusListener( new FocusAdapter() {
      @Override
      public void focusGained( FocusEvent event ) {
        eventLog.add( event );
      }
    } );
    Fixture.fakeNewRequest();
    fakeMouseDownRequest( button, 1, 2 );
    Fixture.fakeSetProperty( getId( display ), "focusControl", getId( button ) );
    Fixture.fakeNotifyOperation( getId( button ), EVENT_FOCUS_IN, null );

    Fixture.executeLifeCycleFromServerThread( );

    assertEquals( FocusEvent.class, eventLog.get( 0 ).getClass() );
    assertEquals( MouseEvent.class, eventLog.get( 1 ).getClass() );
  }

  @Test
  public void testSourceConstructor() {
    TypedEvent event = new TypedEvent( shell );

    assertSame( shell, event.getSource() );
    assertNull( event.widget );
    assertNull( event.display );
  }

  @Test
  public void testEventConstructorWithNullWidget() {
    Event event = new Event();
    try {
      new TypedEvent( event );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSourceConstructorWithNullWidget() {
    try {
      new TypedEvent( ( Object )null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  private static void fakeMouseDownRequest( Widget widget, int x, int y ) {
    JsonObject parameters = new JsonObject()
      .add( ClientMessageConst.EVENT_PARAM_BUTTON, 1 )
      .add( ClientMessageConst.EVENT_PARAM_X, x )
      .add( ClientMessageConst.EVENT_PARAM_Y, y )
      .add( ClientMessageConst.EVENT_PARAM_TIME, 0 );
    Fixture.fakeNotifyOperation( getId( widget ), ClientMessageConst.EVENT_MOUSE_DOWN, parameters );
  }

  private static class TestTypedEvent extends TypedEvent {

    public TestTypedEvent( Event event ) {
      super( event );
    }

  }

}
