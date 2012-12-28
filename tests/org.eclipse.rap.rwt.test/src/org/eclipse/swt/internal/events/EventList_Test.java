/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class EventList_Test {

  private static final int EARLY_TIME = 1;
  private static final int LATE_TIME = 100;

  private static final int FIRST_EVENT = 1;
  private static final int SECOND_EVENT = 2;
  private static final int UNKNOWN_EVENT = 47;

  private EventList eventList;

  @Before
  public void setUp() {
    Fixture.setUp();
    eventList = new EventList( new int[] { FIRST_EVENT, SECOND_EVENT } );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testAdd() {
    Event event = creatEvent( SWT.Arm );
    eventList.add( event );

    Event[] events = eventList.getAll();

    assertEquals( 1, events.length );
    assertSame( event, events[ 0 ] );
  }

  @Test
  public void testGetAll() {
    Event secondEvent = creatEvent( SECOND_EVENT );
    eventList.add( secondEvent );
    Event firstEvent = creatEvent( FIRST_EVENT );
    eventList.add( firstEvent );

    Event[] events = eventList.getAll();

    assertEquals( 2, events.length );
    assertSame( firstEvent, events[ 0 ] );
    assertSame( secondEvent, events[ 1 ] );
  }

  @Test
  public void testGetAllWithUnknownEventType() {
    Event unknownEvent = creatEvent( UNKNOWN_EVENT );
    eventList.add( unknownEvent );
    Event knownEvent = creatEvent( SECOND_EVENT );
    eventList.add( knownEvent );

    Event[] events = eventList.getAll();

    assertEquals( 2, events.length );
    assertSame( knownEvent, events[ 0 ] );
    assertSame( unknownEvent, events[ 1 ] );
  }

  @Test
  public void testGetAllWithSameEventType() {
    Event lateEvent = creatEvent( FIRST_EVENT );
    lateEvent.time = LATE_TIME;
    Event earlyEvent = creatEvent( FIRST_EVENT );
    earlyEvent.time = EARLY_TIME;
    eventList.add( lateEvent );
    eventList.add( earlyEvent );

    Event[] events = eventList.getAll();

    assertEquals( 2, events.length );
    assertSame( earlyEvent, events[ 0 ] );
    assertSame( lateEvent, events[ 1 ] );
  }


  @Test
  public void testRemoveExistingEvent() {
    Event event = creatEvent( FIRST_EVENT );
    eventList.add( event );

    eventList.remove( event );

    assertEquals( 0, eventList.getAll().length );
  }

  @Test
  public void testRemoveNonExistingEvent() {
    Event event = creatEvent( FIRST_EVENT );

    eventList.remove( event );

    assertEquals( 0, eventList.getAll().length );
  }

  @Test
  public void testRemoveEventWithNullArgument() {
    try {
      eventList.remove( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testGetInstance() {
    EventList instance = EventList.getInstance();

    assertNotNull( instance );
  }

  @Test
  public void testGetInstanceReturnsSame() {
    EventList instance1 = EventList.getInstance();
    EventList instance2 = EventList.getInstance();

    assertSame( instance1, instance2 );
  }

  @Test
  public void testGetInstanceHasRequestScope() {
    EventList instance1 = EventList.getInstance();
    simulateNewRequest();
    EventList instance2 = EventList.getInstance();

    assertNotSame( instance1, instance2 );
  }

  private Event creatEvent( int eventType ) {
    Event result = new Event();
    result.type = eventType;
    return result;
  }

  private static void simulateNewRequest() {
    ContextProvider.releaseContextHolder();
    Fixture.createServiceContext();
  }

}
