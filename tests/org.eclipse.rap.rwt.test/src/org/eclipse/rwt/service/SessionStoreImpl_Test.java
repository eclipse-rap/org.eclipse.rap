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
package org.eclipse.rwt.service;

import java.io.Serializable;
import java.util.*;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.lifecycle.ISessionShutdownAdapter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.SessionStoreImpl;


public class SessionStoreImpl_Test extends TestCase {

  private static class EmptySessionStoreListener implements SessionStoreListener {
    public void beforeDestroy( SessionStoreEvent event ) {
    }
  }

  private static class LoggingSessionBindingListener implements HttpSessionBindingListener {

    private String eventTypes;
    private final List eventLog;

    LoggingSessionBindingListener( ) {
      this.eventTypes = "";
      this.eventLog = new LinkedList();
    }

    public void valueBound( HttpSessionBindingEvent event ) {
      eventLog.add( event );
      eventTypes += VALUE_BOUND;
    }

    public void valueUnbound( HttpSessionBindingEvent event ) {
      eventLog.add( event );
      eventTypes += VALUE_UNBOUND;
    }
    
    void clearEvents() {
      eventTypes = "";
      eventLog.clear();
    }
    
    String getEventTypes() {
      return eventTypes;
    }
    
    HttpSessionBindingEvent[] getEvents() {
      HttpSessionBindingEvent[] result = new HttpSessionBindingEvent[ eventLog.size() ];
      eventLog.toArray( result );
      return result;
    }
  }

  private static final String BEFORE_DESTROY = "beforeDestroy|";
  private static final String VALUE_BOUND = "valueBound";
  private static final String VALUE_UNBOUND = "valueUnbound";
  
  private TestSession httpSession;
  private SessionStoreImpl session;
  private List servletLogEntries;
  
  public void testConstructorWithNullArgument() {
    try {
      new SessionStoreImpl( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testGetId() {
    assertNotNull( session.getId() );
  }
  
  public void testGetIdAfterSessionWasInvalidated() {
    String id = session.getId();
    httpSession.invalidate();
    
    assertEquals( id, session.getId() );
  }
  
  public void testGetHttpSession() {
    assertSame( httpSession, session.getHttpSession() );
  }
  
  public void testIsBound() {
    assertTrue( session.isBound() );
  }
  
  public void testIsBoundAfterSessionWasInvalidated() {
    httpSession.invalidate();
    assertFalse( session.isBound() );
  }
  
  public void testGetAttributeWithNullName() {
    try {
      session.getAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testGetAttributeWithNonExistingName() {
    Object attribute = session.getAttribute( "does.not.exist" );
    assertNull( attribute );
  }
  
  public void testGetAttribute() {
    String attributeName = "name";
    Object attributeValue = new Object();
    session.setAttribute( attributeName, attributeValue );
    
    Object returnedAttributeValue = session.getAttribute( attributeName );
    Object otherAttributeValue = session.getAttribute( "other.name" );

    assertSame( attributeValue, returnedAttributeValue );
    assertNull( otherAttributeValue );
  }
  
  public void testRemoveAttributeWithNullName() {
    try {
      session.removeAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRemoveAttributeWithExistingAttribute() {
    String attributeName = "name";
    session.setAttribute( attributeName, new Object() );

    session.removeAttribute( attributeName );
    
    assertNull( session.getAttribute( attributeName ) );
  }

  public void testRemoveAttributeWithNonExistingAttribute() {
    String attributeName = "does.not.exist";
    
    session.removeAttribute( attributeName );
    
    assertNull( session.getAttribute( attributeName ) );
  }
  
  public void testGetAttributeNames() {
    String attributeName = "name";
    session.setAttribute( attributeName, new Object() );
    
    Enumeration attributeNames = session.getAttributeNames();
    
    assertTrue( attributeNames.hasMoreElements() );
    assertSame( attributeNames.nextElement(), attributeName );
    assertFalse( attributeNames.hasMoreElements() );
  }
  
  public void testGetAttributeNamesReturnsSnapshot() {
    String attributeName = "name";
    session.setAttribute( attributeName, new Object() );
    
    Enumeration attributeNames = session.getAttributeNames();
    session.setAttribute( "other.name", new Object() );
    
    assertTrue( attributeNames.hasMoreElements() );
    assertSame( attributeName, attributeNames.nextElement() );
    assertFalse( attributeNames.hasMoreElements() );
  }
  
  public void testSetAttributeWithNullName() {
    try {
      session.setAttribute( null, new Object() );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testSetAttributeWithSessionBindingListener() {
    String attributeName = "name";
    LoggingSessionBindingListener attributeValue = new LoggingSessionBindingListener();
    session.setAttribute( attributeName, attributeValue );
    
    HttpSessionBindingEvent[] events = attributeValue.getEvents();
    assertEquals( 1, events.length );
    assertEquals( attributeName, events[ 0 ].getName() );
    assertSame( httpSession, events[ 0 ].getSource() );
    assertSame( httpSession, events[ 0 ].getSession() );
    assertSame( attributeValue, events[ 0 ].getValue() );
  }
  
  public void testSetAttributeToNullWithSessionBindingListener() {
    String attributeName = "name";
    LoggingSessionBindingListener attributeValue = new LoggingSessionBindingListener();
    session.setAttribute( attributeName, attributeValue );
    attributeValue.clearEvents();
    
    session.setAttribute( attributeName, null );
    
    HttpSessionBindingEvent[] events = attributeValue.getEvents();
    assertEquals( 1, events.length );
    assertEquals( attributeName, events[ 0 ].getName() );
    assertSame( httpSession, events[ 0 ].getSource() );
    assertSame( httpSession, events[ 0 ].getSession() );
    assertSame( attributeValue, events[ 0 ].getValue() );
    assertEquals( VALUE_UNBOUND, attributeValue.getEventTypes() );
  }
  
  public void testOverrideAttributeWithExceptionInUnbound() {
    String attributeName = "name";
    session.setAttribute( attributeName, new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
        throw new RuntimeException();
      }
      public void valueBound( HttpSessionBindingEvent event ) {
      }
    } );
    LoggingSessionBindingListener attributeValue = new LoggingSessionBindingListener();

    session.setAttribute( attributeName, attributeValue );
    
    assertEquals( 1, attributeValue.getEvents().length );
  }
  
  public void testRemoveAttributeWithSessionBindingListener() {
    String attributeName = "name";
    LoggingSessionBindingListener attributeValue = new LoggingSessionBindingListener();
    session.setAttribute( attributeName, attributeValue );
    attributeValue.clearEvents();
    
    session.removeAttribute( attributeName );
    
    HttpSessionBindingEvent[] events = attributeValue.getEvents();
    assertEquals( 1, events.length );
    assertEquals( attributeName, events[ 0 ].getName() );
    assertSame( httpSession, events[ 0 ].getSource() );
    assertSame( httpSession, events[ 0 ].getSession() );
    assertSame( attributeValue, events[ 0 ].getValue() );
    assertEquals( VALUE_UNBOUND, attributeValue.getEventTypes() );
  }
  
  public void testOverrideSessionBindingListenerAttribute() {
    String attributeName = "name";
    LoggingSessionBindingListener attributeValue = new LoggingSessionBindingListener();
    session.setAttribute( attributeName, attributeValue );
    attributeValue.clearEvents();
    
    session.setAttribute( attributeName, new Object() );
    
    HttpSessionBindingEvent[] events = attributeValue.getEvents();
    assertEquals( 1, events.length );
    assertEquals( attributeName, events[ 0 ].getName() );
    assertSame( httpSession, events[ 0 ].getSource() );
    assertSame( httpSession, events[ 0 ].getSession() );
    assertSame( attributeValue, events[ 0 ].getValue() );
    assertEquals( VALUE_UNBOUND, attributeValue.getEventTypes() );
  }
  
  public void testOverrideListenerAttributeWithSessionBindingListener() {
    String attributeName = "name";
    LoggingSessionBindingListener attributeValue = new LoggingSessionBindingListener();
    session.setAttribute( attributeName, attributeValue );
    attributeValue.clearEvents();
    
    session.setAttribute( attributeName, attributeValue );
    
    assertEquals( VALUE_UNBOUND + VALUE_BOUND, attributeValue.getEventTypes() );
    HttpSessionBindingEvent[] events = attributeValue.getEvents();
    assertEquals( 2, events.length );
    assertEquals( attributeName, events[ 0 ].getName() );
    assertSame( httpSession, events[ 0 ].getSource() );
    assertSame( httpSession, events[ 0 ].getSession() );
    assertSame( attributeValue, events[ 0 ].getValue() );
    assertEquals( attributeName, events[ 1 ].getName() );
    assertSame( httpSession, events[ 1 ].getSource() );
    assertSame( httpSession, events[ 1 ].getSession() );
    assertSame( attributeValue, events[ 1 ].getValue() );
  }
  
  public void testGetAttributeInValueUnbound() {
    final Object[] attributeValueInUnbound = { new Object() };
    final String attributeName = "name";
    HttpSessionBindingListener attributeValue = new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
        attributeValueInUnbound[ 0 ] = session.getAttribute( attributeName );
      }
      public void valueBound( HttpSessionBindingEvent event ) {
      }
    };
    session.setAttribute( attributeName, attributeValue );
    
    session.setAttribute( attributeName, "newValue" );

    assertNull( attributeValueInUnbound[ 0 ] );
  }
  
  public void testGetAttributeInValueBound() {
    final Object[] attributeValueInBound = { new Object() };
    final String attributeName = "name";
    HttpSessionBindingListener attributeValue = new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
      }
      public void valueBound( HttpSessionBindingEvent event ) {
        attributeValueInBound[ 0 ] = session.getAttribute( attributeName );
      }
    };
    session.setAttribute( attributeName, attributeValue );
    
    assertSame( attributeValue, attributeValueInBound[ 0 ] );
  }
  
  public void testEventOrderOnInvalidate() {
    final StringBuffer log = new StringBuffer();
    HttpSessionBindingListener attributeValue = new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
        log.append( VALUE_UNBOUND );
      }
      public void valueBound( HttpSessionBindingEvent event ) {
        log.append( VALUE_BOUND );
      }
    };
    session.setAttribute( "name", attributeValue );
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( SessionStoreEvent event ) {
        log.append( BEFORE_DESTROY );
      }
    } );
    log.setLength( 0 );
    
    httpSession.invalidate();

    assertEquals( BEFORE_DESTROY + VALUE_UNBOUND, log.toString() );
  }
  
  public void testSetAttributeForUnboundSessionStore() {
    httpSession.invalidate();
    
    boolean setAttribute = session.setAttribute( "name", null );
    
    assertFalse( setAttribute );
  }
  
  public void testGetAttributeForUnboundSessionStore() {
    String attributeName = "name";
    session.setAttribute( attributeName, null );
    httpSession.invalidate();
    
    Object attributeValue = session.getAttribute( attributeName );

    assertNull( attributeValue );
  }
  
  public void testRemoveAttributeForUnboundSessionStore() {
    String attributeName = "name";
    session.setAttribute( attributeName, null );
    httpSession.invalidate();
    
    boolean removeAttribute = session.removeAttribute( attributeName );
    
    assertFalse( removeAttribute );
  }
  
  public void testGetAttributeNamesForUnboundSessionStore() {
    ISessionStore session = new SessionStoreImpl( httpSession );
    session.setAttribute( "name", "value" );
    httpSession.invalidate();

    Enumeration attributeNames = session.getAttributeNames();
    
    assertNotNull( attributeNames );
    assertFalse( attributeNames.hasMoreElements() );
  }
  
  public void testAddSessionStoreListenerWithNullArgument() {
    try {
      session.addSessionStoreListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRemoveSessionStoreListenerWithNullArgument() {
    try {
      session.removeSessionStoreListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testAddSessionStoreListenerForUnboundSessionStore() {
    httpSession.invalidate();
    EmptySessionStoreListener listener = new EmptySessionStoreListener();
    
    boolean addSessionStoreListener = session.addSessionStoreListener( listener );

    assertFalse( addSessionStoreListener );
  }
  
  public void testRemoveSessionStoreListenerForUnboundSessionStore() {
    httpSession.invalidate();
    EmptySessionStoreListener listener = new EmptySessionStoreListener();
    
    boolean removeSessionStoreListener = session.removeSessionStoreListener( listener );
    
    assertFalse( removeSessionStoreListener );
  }
  
  public void testAddSessionStoreListenerWhileDestroyingSessionStore() {
    final boolean[] aboutUnboundListener = { true };
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( SessionStoreEvent event ) {
        SessionStoreListener listener = new EmptySessionStoreListener();
        aboutUnboundListener[ 0 ] = session.addSessionStoreListener( listener );
      }
    } );
    
    httpSession.invalidate();
    
    assertFalse( aboutUnboundListener[ 0 ] );
  }
  
  public void testSetAttributeWhileDestroyingSessionStore() {
    final boolean[] valueUnboundWasCalled = { false };
    final HttpSessionBindingListener httpSessionBindingListener = new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
        valueUnboundWasCalled[ 0 ] = true;
      }
      public void valueBound( HttpSessionBindingEvent event ) {
      }
    };
    final boolean[] setAttribute = { false };
    session.setAttribute( "name", new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
        setAttribute[ 0 ] = session.setAttribute( "other.name", httpSessionBindingListener );
      }
      public void valueBound( HttpSessionBindingEvent event ) {
      }
    } );
    
    httpSession.invalidate();
    
    assertTrue( setAttribute[ 0 ] );
    assertFalse( valueUnboundWasCalled[ 0 ] );
  }
  
  public void testServiceContextAvailableInBeforeDestroyEvent() {
    final boolean[] hasContext = { false };
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( final SessionStoreEvent event ) {
        hasContext[ 0 ] = ContextProvider.hasContext();
      }
    } );
    
    httpSession.invalidate();
    
    assertTrue( hasContext[ 0 ] );
  }
  
  public void testDestroyEventDetails() {
    final List eventLog = new LinkedList();
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( SessionStoreEvent event ) {
        eventLog.add( event );
      }
    } );
    
    httpSession.invalidate();
    
    assertEquals( 1, eventLog.size() );
    SessionStoreEvent event = ( SessionStoreEvent )eventLog.get( 0 );
    assertSame( session, event.getSessionStore() );
  }
  
  public void testShutdownCallback() {
    final boolean[] interceptShutdownWasCalled = { false };
    final Runnable[] shutdownCallback = { null };
    final boolean[] listenerWasCalled = { false };
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( final SessionStoreEvent event ) {
        listenerWasCalled[ 0 ] = true;
      }
    } );
    session.setShutdownAdapter( new ISessionShutdownAdapter() {
      public void setSessionStore( final ISessionStore sessionStore ) {
      }
      public void setShutdownCallback( final Runnable callback ) {
        shutdownCallback[ 0 ] = callback;
      }
      public void interceptShutdown() {
        interceptShutdownWasCalled[ 0 ] = true;
      }
      public void processShutdown() {
      }
    } );
    
    session.valueUnbound( null );
    
    assertTrue( interceptShutdownWasCalled[ 0 ] );
    assertTrue( session.isBound() );
    assertFalse( listenerWasCalled[ 0 ] );
    shutdownCallback[ 0 ].run();
    assertTrue( listenerWasCalled[ 0 ] );
    assertFalse( session.isBound() );
  }
  
  public void testExceptionHandlingInSessionStoreListeners() {
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( SessionStoreEvent event ) {
        throw new RuntimeException();
      }
    } );
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( SessionStoreEvent event ) {
        throw new RuntimeException();
      }
    } );
    
    httpSession.invalidate();
    
    assertEquals( 2, servletLogEntries.size() );
  }
  
  public void testOverrideAtributeWithNull() {
    String attributeName = "name";
    session.setAttribute( attributeName, new Object() );
  
    session.setAttribute( attributeName, null );
    
    assertNull( session.getAttribute( attributeName ) );
  }

  public void testOverrideSerializableAttributeWithNonSerializable() {
    String attributeName = "name";
    Serializable serializableAttribute = new String();
    session.setAttribute( attributeName, serializableAttribute );

    Object overridingAtribute = new Object();
    session.setAttribute( attributeName, overridingAtribute );
    
    assertSame( overridingAtribute, session.getAttribute( attributeName ) );
  }

  public void testOverrideNonSerializableAttributeWithSerializable() {
    String attributeName = "name";
    Object nonSerializableAttribute = new Object();
    session.setAttribute( attributeName, nonSerializableAttribute );

    Serializable overridingAtribute = new String();
    session.setAttribute( attributeName, overridingAtribute );
    
    assertSame( overridingAtribute, session.getAttribute( attributeName ) );
  }
  
  public void testGetAttributeNamesIsThreadSafe() throws InterruptedException {
    final Exception[] exception = { null };
    List threads = new LinkedList();
    for( int i = 0; i < 100; i++ ) {
      Thread thread = new Thread( new Runnable() {
        public void run() {
          try {
            Object object = new Object();
            session.setAttribute( object.toString(), object );
            Enumeration attributeNames = session.getAttributeNames();
            while( attributeNames.hasMoreElements() ) {
              attributeNames.nextElement();
            }
          } catch( Exception e ) {
            exception[ 0 ] = e;
          }
        }
      } );
      thread.start();
      threads.add( thread );
    }
    while( threads.size() > 0 ) {
      Thread thread = ( Thread )threads.remove( 0 );
      thread.join();
    }
    assertNull( exception[ 0 ] );
  }
  
  protected void setUp() throws Exception {
    httpSession = new TestSession();
    session = new SessionStoreImpl( httpSession );  
    servletLogEntries = new LinkedList();
    TestServletContext servletContext = ( TestServletContext )httpSession.getServletContext();
    servletContext.setLogger( new TestLogger() {
      public void log( String message, Throwable throwable ) {
        servletLogEntries.add( throwable );
      }
    } );
  }
}
