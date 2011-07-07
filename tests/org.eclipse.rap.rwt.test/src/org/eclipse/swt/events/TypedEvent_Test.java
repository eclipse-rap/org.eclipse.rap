/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.widgets.*;


public class TypedEvent_Test extends TestCase {

  private static final String EVENT_FIRED
    = "eventFired|";
  private static final String AFTER_RENDER
    = "after" + PhaseId.RENDER + "|";
  private static final String BEFORE_RENDER
    = "before" + PhaseId.RENDER + "|";
  private static final String AFTER_PROCESS_ACTION
    = "after" + PhaseId.PROCESS_ACTION + "|";
  private static final String BEFORE_PROCESS_ACTION
    = "before" + PhaseId.PROCESS_ACTION + "|";
  private static final String AFTER_READ_DATA
    = "after" + PhaseId.READ_DATA + "|";
  private static final String BEFORE_READ_DATA
    = "before" + PhaseId.READ_DATA + "|";
  private static final String AFTER_PREPARE_UI_ROOT
    = "after" + PhaseId.PREPARE_UI_ROOT + "|";
  private static final String BEFORE_PREPARE_UI_ROOT
    = "before" + PhaseId.PREPARE_UI_ROOT + "|";

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCopyFieldsFromUntypedEvent() {
    final List<HelpEvent> log = new ArrayList<HelpEvent>();
    Button button = new Button( shell, SWT.PUSH );
    button.addHelpListener( new HelpListener() {
      public void helpRequested( final HelpEvent event ) {
        log.add( event );
      }
    } );
    Object data = new Object();
    Event event = new Event();
    event.data = data;
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    button.notifyListeners( SWT.Help, event );
    TypedEvent typedEvent = log.get( 0 );
    assertSame( button, typedEvent.getSource() );
    assertSame( button, typedEvent.widget );
    assertSame( display, typedEvent.display );
    assertSame( data, typedEvent.data );
    assertEquals( SWT.Help, typedEvent.getID() );
  }

  public void testPhase() {
    final StringBuffer log = new StringBuffer();
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( EVENT_FIRED );
      }
    } );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( final PhaseEvent event ) {
        log.append( "before" + event.getPhaseId() + "|" );
      }
      public void afterPhase( final PhaseEvent event ) {
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

  public void testMultipleEventsInOneRequest() {
    // Ensure that two events get fired in the order as it is specified in
    // TypedEvent
    final java.util.List<TypedEvent> eventLog = new ArrayList<TypedEvent>();
    Shell shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        eventLog.add( event );
      }
    } );
    ActivateEvent.addListener( button, new ActivateAdapter() {
      public void activated( ActivateEvent event ) {
        eventLog.add( event );
      }
    } );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, buttonId );

    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( ActivateEvent.class, eventLog.get( 0 ).getClass() );
    assertEquals( SelectionEvent.class, eventLog.get( 1 ).getClass() );

    // Ensure that the focus events are fired before the mouse events.
    // This is important for cell editors activation/deactivation.
    // See bug 262167:
    // [Table] Selection activates the CellEditor differently every two rows
    eventLog.clear();
    button = new Button( shell, SWT.PUSH );
    button.addMouseListener( new MouseAdapter() {
      public void mouseDown( final MouseEvent event ) {
        eventLog.add( event );
      }
    } );
    button.addFocusListener( new FocusAdapter() {
      public void focusGained( final FocusEvent event ) {
        eventLog.add( event );
      }
    } );
    buttonId = WidgetUtil.getId( button );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN, buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_BUTTON, "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_X, "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_Y, "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_TIME, "0" );
    String focusedControlParam = DisplayUtil.getId( display ) + ".focusControl";
    Fixture.fakeRequestParam( focusedControlParam, buttonId );

    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( FocusEvent.class, eventLog.get( 0 ).getClass() );
    assertEquals( MouseEvent.class, eventLog.get( 1 ).getClass() );
  }
  
  public void testSourceConstructor() {
    TypedEvent event = new TypedEvent( shell );
    assertSame( shell, event.widget );
    assertSame( shell.getDisplay(), event.display );
  }

  public void testEventConstructor() {
    Event event = new Event();
    event.widget = shell;
    event.display = shell.getDisplay();
    event.data = new Object();
    TypedEvent typedEvent = new TypedEvent( event );
    assertSame( event.widget, typedEvent.widget );
    assertSame( event.display, typedEvent.display );
    assertSame( event.data, typedEvent.data );
  }
  
  public void testEventConstructorWithNullWidget() {
    Event event = new Event();
    try {
      new TypedEvent( event );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testSourceConstructorWithNullWidget() {
    try {
      new TypedEvent( ( Object )null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testSourceIdConstructorWithNullWidget() {
    try {
      new TypedEvent( null, SWT.Arm );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  
}
