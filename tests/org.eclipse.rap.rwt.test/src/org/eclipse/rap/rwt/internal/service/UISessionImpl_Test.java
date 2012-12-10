/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.lifecycle.ISessionShutdownAdapter;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestLogger;
import org.eclipse.rap.rwt.testfixture.TestServletContext;
import org.eclipse.rap.rwt.testfixture.TestSession;


public class UISessionImpl_Test extends TestCase {

  private static class EmptyUISessionListener implements UISessionListener {
    public void beforeDestroy( UISessionEvent event ) {
    }
  }

  private static class LoggingSessionBindingListener implements HttpSessionBindingListener {

    private String eventTypes;
    private final List<HttpSessionBindingEvent> eventLog;

    LoggingSessionBindingListener( ) {
      eventTypes = "";
      eventLog = new LinkedList<HttpSessionBindingEvent>();
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

  private HttpSession httpSession;
  private UISessionImpl uiSession;
  private List<Throwable> servletLogEntries;

  public void testConstructorWithNullArgument() {
    try {
      new UISessionImpl( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetId() {
    assertNotNull( uiSession.getId() );
  }

  public void testGetIdAfterSessionWasInvalidated() {
    String id = uiSession.getId();
    httpSession.invalidate();

    assertEquals( id, uiSession.getId() );
  }

  public void testGetHttpSession() {
    assertSame( httpSession, uiSession.getHttpSession() );
  }

  public void testIsBound() {
    assertTrue( uiSession.isBound() );
  }

  public void testIsBoundAfterSessionWasInvalidated() {
    httpSession.invalidate();
    assertFalse( uiSession.isBound() );
  }

  public void testGetAttributeWithNullName() {
    try {
      uiSession.getAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetAttributeWithNonExistingName() {
    Object attribute = uiSession.getAttribute( "does.not.exist" );
    assertNull( attribute );
  }

  public void testGetAttribute() {
    String attributeName = "name";
    Object attributeValue = new Object();
    uiSession.setAttribute( attributeName, attributeValue );

    Object returnedAttributeValue = uiSession.getAttribute( attributeName );
    Object otherAttributeValue = uiSession.getAttribute( "other.name" );

    assertSame( attributeValue, returnedAttributeValue );
    assertNull( otherAttributeValue );
  }

  public void testRemoveAttributeWithNullName() {
    try {
      uiSession.removeAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRemoveAttributeWithExistingAttribute() {
    String attributeName = "name";
    uiSession.setAttribute( attributeName, new Object() );

    uiSession.removeAttribute( attributeName );

    assertNull( uiSession.getAttribute( attributeName ) );
  }

  public void testRemoveAttributeWithNonExistingAttribute() {
    String attributeName = "does.not.exist";

    uiSession.removeAttribute( attributeName );

    assertNull( uiSession.getAttribute( attributeName ) );
  }

  public void testGetAttributeNames() {
    String attributeName = "name";
    uiSession.setAttribute( attributeName, new Object() );

    Enumeration attributeNames = uiSession.getAttributeNames();

    assertTrue( attributeNames.hasMoreElements() );
    assertSame( attributeNames.nextElement(), attributeName );
    assertFalse( attributeNames.hasMoreElements() );
  }

  public void testGetAttributeNamesReturnsSnapshot() {
    String attributeName = "name";
    uiSession.setAttribute( attributeName, new Object() );

    Enumeration attributeNames = uiSession.getAttributeNames();
    uiSession.setAttribute( "other.name", new Object() );

    assertTrue( attributeNames.hasMoreElements() );
    assertSame( attributeName, attributeNames.nextElement() );
    assertFalse( attributeNames.hasMoreElements() );
  }

  public void testSetAttributeWithNullName() {
    try {
      uiSession.setAttribute( null, new Object() );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testSetAttributeWithSessionBindingListener() {
    String attributeName = "name";
    LoggingSessionBindingListener attributeValue = new LoggingSessionBindingListener();
    uiSession.setAttribute( attributeName, attributeValue );

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
    uiSession.setAttribute( attributeName, attributeValue );
    attributeValue.clearEvents();

    uiSession.setAttribute( attributeName, null );

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
    uiSession.setAttribute( attributeName, new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
        throw new RuntimeException();
      }
      public void valueBound( HttpSessionBindingEvent event ) {
      }
    } );
    LoggingSessionBindingListener attributeValue = new LoggingSessionBindingListener();

    uiSession.setAttribute( attributeName, attributeValue );

    assertEquals( 1, attributeValue.getEvents().length );
  }

  public void testRemoveAttributeWithSessionBindingListener() {
    String attributeName = "name";
    LoggingSessionBindingListener attributeValue = new LoggingSessionBindingListener();
    uiSession.setAttribute( attributeName, attributeValue );
    attributeValue.clearEvents();

    uiSession.removeAttribute( attributeName );

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
    uiSession.setAttribute( attributeName, attributeValue );
    attributeValue.clearEvents();

    uiSession.setAttribute( attributeName, new Object() );

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
    uiSession.setAttribute( attributeName, attributeValue );
    attributeValue.clearEvents();

    uiSession.setAttribute( attributeName, attributeValue );

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
        attributeValueInUnbound[ 0 ] = uiSession.getAttribute( attributeName );
      }
      public void valueBound( HttpSessionBindingEvent event ) {
      }
    };
    uiSession.setAttribute( attributeName, attributeValue );

    uiSession.setAttribute( attributeName, "newValue" );

    assertNull( attributeValueInUnbound[ 0 ] );
  }

  public void testGetAttributeInValueBound() {
    final Object[] attributeValueInBound = { new Object() };
    final String attributeName = "name";
    HttpSessionBindingListener attributeValue = new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
      }
      public void valueBound( HttpSessionBindingEvent event ) {
        attributeValueInBound[ 0 ] = uiSession.getAttribute( attributeName );
      }
    };
    uiSession.setAttribute( attributeName, attributeValue );

    assertSame( attributeValue, attributeValueInBound[ 0 ] );
  }

  public void testEventOrderOnInvalidate() {
    final StringBuilder log = new StringBuilder();
    HttpSessionBindingListener attributeValue = new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
        log.append( VALUE_UNBOUND );
      }
      public void valueBound( HttpSessionBindingEvent event ) {
        log.append( VALUE_BOUND );
      }
    };
    uiSession.setAttribute( "name", attributeValue );
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        log.append( BEFORE_DESTROY );
      }
    } );
    log.setLength( 0 );

    httpSession.invalidate();

    assertEquals( BEFORE_DESTROY + VALUE_UNBOUND, log.toString() );
  }

  public void testSetAttributeForUnboundUISession() {
    httpSession.invalidate();

    boolean setAttribute = uiSession.setAttribute( "name", null );

    assertFalse( setAttribute );
  }

  public void testGetAttributeForUnboundUISession() {
    String attributeName = "name";
    uiSession.setAttribute( attributeName, null );
    httpSession.invalidate();

    Object attributeValue = uiSession.getAttribute( attributeName );

    assertNull( attributeValue );
  }

  public void testRemoveAttributeForUnboundUISession() {
    String attributeName = "name";
    uiSession.setAttribute( attributeName, null );
    httpSession.invalidate();

    boolean removeAttribute = uiSession.removeAttribute( attributeName );

    assertFalse( removeAttribute );
  }

  public void testGetAttributeNamesForUnboundUISession() {
    uiSession.setAttribute( "name", "value" );
    httpSession.invalidate();

    Enumeration attributeNames = uiSession.getAttributeNames();

    assertNotNull( attributeNames );
    assertFalse( attributeNames.hasMoreElements() );
  }

  public void testAddUISessionListenerWithNullArgument() {
    try {
      uiSession.addUISessionListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRemoveUISessionListenerWithNullArgument() {
    try {
      uiSession.removeUISessionListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testAddUISessionListenerForUnboundUISession() {
    httpSession.invalidate();
    EmptyUISessionListener listener = new EmptyUISessionListener();

    boolean added = uiSession.addUISessionListener( listener );

    assertFalse( added );
  }

  public void testRemoveUISessionListenerForUnboundUISession() {
    httpSession.invalidate();
    EmptyUISessionListener listener = new EmptyUISessionListener();

    boolean removed = uiSession.removeUISessionListener( listener );

    assertFalse( removed );
  }

  public void testAddUISessionListenerWhileDestroyingUISession() {
    final boolean[] aboutUnboundListener = { true };
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        UISessionListener listener = new EmptyUISessionListener();
        aboutUnboundListener[ 0 ] = uiSession.addUISessionListener( listener );
      }
    } );

    httpSession.invalidate();

    assertFalse( aboutUnboundListener[ 0 ] );
  }

  public void testSetAttributeWhileDestroyingUISession() {
    final boolean[] valueUnboundWasCalled = { false };
    final HttpSessionBindingListener httpSessionBindingListener = new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
        valueUnboundWasCalled[ 0 ] = true;
      }
      public void valueBound( HttpSessionBindingEvent event ) {
      }
    };
    final boolean[] setAttribute = { false };
    uiSession.setAttribute( "name", new HttpSessionBindingListener() {
      public void valueUnbound( HttpSessionBindingEvent event ) {
        setAttribute[ 0 ] = uiSession.setAttribute( "other.name", httpSessionBindingListener );
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
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        hasContext[ 0 ] = ContextProvider.hasContext();
      }
    } );

    httpSession.invalidate();

    assertTrue( hasContext[ 0 ] );
  }

  public void testDestroyEventDetails() {
    final List<UISessionEvent> eventLog = new LinkedList<UISessionEvent>();
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        eventLog.add( event );
      }
    } );

    httpSession.invalidate();

    assertEquals( 1, eventLog.size() );
    UISessionEvent event = eventLog.get( 0 );
    assertSame( uiSession, event.getUISession() );
  }

  public void testShutdownCallback() {
    final boolean[] interceptShutdownWasCalled = { false };
    final Runnable[] shutdownCallback = { null };
    final boolean[] listenerWasCalled = { false };
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        listenerWasCalled[ 0 ] = true;
      }
    } );
    uiSession.setShutdownAdapter( new ISessionShutdownAdapter() {
      public void setUISession( UISession uiSession ) {
      }
      public void setShutdownCallback( Runnable callback ) {
        shutdownCallback[ 0 ] = callback;
      }
      public void interceptShutdown() {
        interceptShutdownWasCalled[ 0 ] = true;
      }
      public void processShutdown() {
      }
    } );

    uiSession.valueUnbound( null );

    assertTrue( interceptShutdownWasCalled[ 0 ] );
    assertTrue( uiSession.isBound() );
    assertFalse( listenerWasCalled[ 0 ] );
    shutdownCallback[ 0 ].run();
    assertTrue( listenerWasCalled[ 0 ] );
    assertFalse( uiSession.isBound() );
  }

  public void testExceptionHandlingInUISessionListeners() {
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        throw new RuntimeException();
      }
    } );
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        throw new RuntimeException();
      }
    } );

    httpSession.invalidate();

    assertEquals( 2, servletLogEntries.size() );
  }

  public void testOverrideAtributeWithNull() {
    String attributeName = "name";
    uiSession.setAttribute( attributeName, new Object() );

    uiSession.setAttribute( attributeName, null );

    assertNull( uiSession.getAttribute( attributeName ) );
  }

  public void testOverrideSerializableAttributeWithNonSerializable() {
    String attributeName = "name";
    Serializable serializableAttribute = new String();
    uiSession.setAttribute( attributeName, serializableAttribute );

    Object overridingAtribute = new Object();
    uiSession.setAttribute( attributeName, overridingAtribute );

    assertSame( overridingAtribute, uiSession.getAttribute( attributeName ) );
  }

  public void testOverrideNonSerializableAttributeWithSerializable() {
    String attributeName = "name";
    Object nonSerializableAttribute = new Object();
    uiSession.setAttribute( attributeName, nonSerializableAttribute );

    Serializable overridingAtribute = new String();
    uiSession.setAttribute( attributeName, overridingAtribute );

    assertSame( overridingAtribute, uiSession.getAttribute( attributeName ) );
  }

  public void testGetAttributeNamesIsThreadSafe() throws InterruptedException {
    final Throwable[] exception = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          Object object = new Object();
          uiSession.setAttribute( object.toString(), object );
          Enumeration attributeNames = uiSession.getAttributeNames();
          while( attributeNames.hasMoreElements() ) {
            attributeNames.nextElement();
          }
        } catch( Throwable e ) {
          exception[ 0 ] = e;
        }
      }
    };
    Thread[] threads = Fixture.startThreads( 100, runnable );
    Fixture.joinThreads( threads );
    assertNull( exception[ 0 ] );
  }

  public void testAttachHttpSessionWithNullArgument() {
    try {
      uiSession.attachHttpSession( null );
    } catch( NullPointerException expected ) {
    }
  }

  public void testAttachHttpSession() {
    HttpSession anotherSession = new TestSession();
    uiSession.attachHttpSession( anotherSession );

    assertSame( anotherSession, uiSession.getHttpSession() );
  }

  public void testAttachSessionDoesNotChangeId() {
    String initialId = uiSession.getId();
    TestSession anotherSession = new TestSession();
    anotherSession.setId( "some.other.id" );
    uiSession.attachHttpSession( anotherSession );

    String id = uiSession.getId();

    assertEquals( initialId, id );
  }

  public void testAttachSessionDoesNotTriggerListener() {
    final boolean[] wasCalled = { false };
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        wasCalled[ 0 ] = true;
      }
    } );

    uiSession.attachHttpSession( new TestSession() );
    assertFalse( wasCalled[ 0 ] );
  }

  public void testGetInstanceFromSession() {
    UISessionImpl result = UISessionImpl.getInstanceFromSession( httpSession );

    assertSame( result, uiSession );
  }

  public void testGetInstanceFromSessionAfterInvalidate() {
    httpSession.invalidate();
    UISessionImpl result = UISessionImpl.getInstanceFromSession( httpSession );

    assertNull( result );
  }

  @Override
  protected void setUp() throws Exception {
    httpSession = new TestSession();
    uiSession = new UISessionImpl( httpSession );
    UISessionImpl.attachInstanceToSession( httpSession, uiSession );
    servletLogEntries = new LinkedList<Throwable>();
    TestServletContext servletContext = ( TestServletContext )httpSession.getServletContext();
    servletContext.setLogger( new TestLogger() {
      public void log( String message, Throwable throwable ) {
        servletLogEntries.add( throwable );
      }
    } );
  }
}
