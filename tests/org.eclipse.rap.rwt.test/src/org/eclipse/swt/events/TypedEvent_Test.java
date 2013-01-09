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
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TypedEvent_Test {

  private static final String EVENT_FIRED = "eventFired|";
  private static final String AFTER_RENDER = "after" + PhaseId.RENDER + "|";
  private static final String BEFORE_RENDER = "before" + PhaseId.RENDER + "|";
  private static final String AFTER_PROCESS_ACTION = "after" + PhaseId.PROCESS_ACTION + "|";
  private static final String BEFORE_PROCESS_ACTION = "before" + PhaseId.PROCESS_ACTION + "|";
  private static final String AFTER_READ_DATA = "after" + PhaseId.READ_DATA + "|";
  private static final String BEFORE_READ_DATA = "before" + PhaseId.READ_DATA + "|";
  private static final String AFTER_PREPARE_UI_ROOT = "after" + PhaseId.PREPARE_UI_ROOT + "|";
  private static final String BEFORE_PREPARE_UI_ROOT = "before" + PhaseId.PREPARE_UI_ROOT + "|";

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
  public void testPhase() {
    final StringBuilder log = new StringBuilder();
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        log.append( EVENT_FIRED );
      }
    } );
    Fixture.fakeNewRequest();
    Fixture.fakeNotifyOperation( getId( button ), ClientMessageConst.EVENT_SELECTION, null );
    LifeCycle lifeCycle = getApplicationContext().getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( PhaseEvent event ) {
        log.append( "before" + event.getPhaseId() + "|" );
      }
      public void afterPhase( PhaseEvent event ) {
        log.append( "after" + event.getPhaseId() + "|" );
      }
      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    } );

    Fixture.executeLifeCycleFromServerThread( );

    String expected
      = BEFORE_PREPARE_UI_ROOT
      + AFTER_PREPARE_UI_ROOT
      + BEFORE_READ_DATA
      + AFTER_READ_DATA
      + BEFORE_PROCESS_ACTION
      + EVENT_FIRED
      + AFTER_PROCESS_ACTION
      + BEFORE_RENDER
      + AFTER_RENDER;
    assertEquals( expected, log.toString() );
  }

  @Test
  public void testFireFocusEventBeforeMouseEvent() {
    final java.util.List<TypedEvent> eventLog = new ArrayList<TypedEvent>();
    Button button = new Button( shell, SWT.PUSH );
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
    Fixture.fakeSetParameter( getId( display ), "focusControl", getId( button ) );

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
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( ClientMessageConst.EVENT_PARAM_BUTTON, Integer.valueOf( 1 ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_X, Integer.valueOf( x ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_Y, Integer.valueOf( y ) );
    parameters.put( ClientMessageConst.EVENT_PARAM_TIME, Integer.valueOf( 0 ) );
    Fixture.fakeNotifyOperation( getId( widget ), ClientMessageConst.EVENT_MOUSE_DOWN, parameters );
  }

  private static class TestTypedEvent extends TypedEvent {

    public TestTypedEvent( Event event ) {
      super( event );
    }

  }

}
