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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpSession;

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

  private HttpSession httpSession;
  private UISessionImpl uiSession;
  private List<Throwable> servletLogEntries;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    httpSession = new TestSession();
    uiSession = new UISessionImpl( httpSession );
    UISessionImpl.attachInstanceToSession( httpSession, uiSession );
    ContextProvider.getContext().setUISession( uiSession );
    servletLogEntries = new LinkedList<Throwable>();
    TestServletContext servletContext = ( TestServletContext )httpSession.getServletContext();
    servletContext.setLogger( new TestLogger() {
      public void log( String message, Throwable throwable ) {
        servletLogEntries.add( throwable );
      }
    } );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testConstructor_failsWithWithNullArgument() {
    try {
      new UISessionImpl( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetInstanceFromSession() {
    UISessionImpl result = UISessionImpl.getInstanceFromSession( httpSession );

    assertSame( result, uiSession );
  }

  public void testGetInstanceFromSession_returnsNullAfterInvalidate() {
    httpSession.invalidate();
    UISessionImpl result = UISessionImpl.getInstanceFromSession( httpSession );

    assertNull( result );
  }

  public void testGetId() {
    assertNotNull( uiSession.getId() );
  }

  public void testGetId_validAfterSessionWasInvalidated() {
    String id = uiSession.getId();
    httpSession.invalidate();

    assertEquals( id, uiSession.getId() );
  }

  public void testGetHttpSession() {
    assertSame( httpSession, uiSession.getHttpSession() );
  }

  public void testAttachHttpSession() {
    HttpSession anotherSession = new TestSession();
    uiSession.attachHttpSession( anotherSession );

    assertSame( anotherSession, uiSession.getHttpSession() );
  }

  public void testAttachHttpSession_failsWithNullArgument() {
    try {
      uiSession.attachHttpSession( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testAttachHttpSession_doesNotChangeId() {
    String initialId = uiSession.getId();
    TestSession anotherSession = new TestSession();
    anotherSession.setId( "some.other.id" );
    uiSession.attachHttpSession( anotherSession );

    String id = uiSession.getId();

    assertEquals( initialId, id );
  }

  public void testAttachHttpSession_doesNotTriggerListener() {
    final boolean[] wasCalled = { false };
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        wasCalled[ 0 ] = true;
      }
    } );

    uiSession.attachHttpSession( new TestSession() );
    assertFalse( wasCalled[ 0 ] );
  }

  public void testIsBound() {
    assertTrue( uiSession.isBound() );
  }

  public void testIsBound_isFalseAfterSessionWasInvalidated() {
    httpSession.invalidate();

    assertFalse( uiSession.isBound() );
  }

  public void testGetAttribute() {
    Object value = new Object();
    uiSession.setAttribute( "name", value );

    Object result = uiSession.getAttribute( "name" );

    assertSame( value, result );
  }

  public void testGetAttribute_failsWithNullName() {
    try {
      uiSession.getAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetAttribute_returnsNullWithNonExistingName() {
    Object attribute = uiSession.getAttribute( "does.not.exist" );

    assertNull( attribute );
  }

  public void testGetAttribute_returnsNullWhenUnbound() {
    uiSession.setAttribute( "name", null );
    httpSession.invalidate();

    Object result = uiSession.getAttribute( "name" );

    assertNull( result );
  }

  public void testSetAttribute_failsWithNullName() {
    try {
      uiSession.setAttribute( null, new Object() );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testSetAttribute_returnsFalseWhenUnbound() {
    httpSession.invalidate();

    boolean result = uiSession.setAttribute( "name", null );

    assertFalse( result );
  }

  public void testSetAttribute_whileDestroyingUISession() {
    final AtomicBoolean resultCaptor = new AtomicBoolean();
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        resultCaptor.set( uiSession.setAttribute( "name", new Object() ) );
      }
    } );

    httpSession.invalidate();

    assertTrue( resultCaptor.get() );
  }

  public void testRemoveAttribute_failsWithNullName() {
    try {
      uiSession.removeAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRemoveAttribute_removesExistingAttribute() {
    uiSession.setAttribute( "name", new Object() );

    uiSession.removeAttribute( "name" );

    assertNull( uiSession.getAttribute( "name" ) );
  }

  public void testRemoveAttribute_returnsNullForNonExistingAttribute() {
    uiSession.removeAttribute( "does.not.exist" );

    assertNull( uiSession.getAttribute( "does.not.exist" ) );
  }

  public void testRemoveAttribute_returnsFalseWhenUnbound() {
    uiSession.setAttribute( "name", null );
    httpSession.invalidate();

    boolean result = uiSession.removeAttribute( "name" );

    assertFalse( result );
  }

  public void testGetAttributeNames() {
    uiSession.setAttribute( "name", new Object() );

    Enumeration attributeNames = uiSession.getAttributeNames();

    assertTrue( attributeNames.hasMoreElements() );
    assertEquals( "name", attributeNames.nextElement() );
    assertFalse( attributeNames.hasMoreElements() );
  }

  public void testGetAttributeNames_returnsSnapshot() {
    uiSession.setAttribute( "name", new Object() );

    Enumeration attributeNames = uiSession.getAttributeNames();
    uiSession.setAttribute( "other.name", new Object() );

    assertTrue( attributeNames.hasMoreElements() );
    assertEquals( "name", attributeNames.nextElement() );
    assertFalse( attributeNames.hasMoreElements() );
  }

  public void testGetAttributeNames_isEmptyWhenUnbound() {
    uiSession.setAttribute( "name", "value" );
    httpSession.invalidate();

    Enumeration attributeNames = uiSession.getAttributeNames();

    assertNotNull( attributeNames );
    assertFalse( attributeNames.hasMoreElements() );
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

  public void testAddUISessionListener_failsWithNullArgument() {
    try {
      uiSession.addUISessionListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testAddUISessionListener_returnsFalseWhenUnbound() {
    httpSession.invalidate();
    EmptyUISessionListener listener = new EmptyUISessionListener();

    boolean added = uiSession.addUISessionListener( listener );

    assertFalse( added );
  }

  public void testAddUISessionListener_whileDestroyingUISession() {
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

  public void testRemoveUISessionListener_failsWithNullArgument() {
    try {
      uiSession.removeUISessionListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRemoveUISessionListener_returnsFalseWhenUnbound() {
    httpSession.invalidate();
    EmptyUISessionListener listener = new EmptyUISessionListener();

    boolean removed = uiSession.removeUISessionListener( listener );

    assertFalse( removed );
  }

  public void testBeforeDestroyEvent_hasServiceContext() {
    final AtomicBoolean resultCaptor = new AtomicBoolean();
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        resultCaptor.set( ContextProvider.hasContext() );
      }
    } );

    httpSession.invalidate();

    assertTrue( resultCaptor.get() );
  }

  public void testBeforeDestroyEvent_details() {
    final List<UISessionEvent> eventLog = new LinkedList<UISessionEvent>();
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        eventLog.add( event );
      }
    } );

    httpSession.invalidate();

    assertEquals( 1, eventLog.size() );
    assertSame( uiSession, eventLog.get( 0 ).getUISession() );
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

  public void testExec_failsWithNullArgument() {
    try {
      uiSession.exec( null );
      fail();
    } catch( NullPointerException exception ) {
      assertTrue( exception.getMessage().contains( "runnable" ) );
    }
  }

  public void testExec_executesRunnable() {
    Runnable runnable = mock( Runnable.class );

    uiSession.exec( runnable );

    verify( runnable ).run();
  }

  public void testExec_executesRunnableWithSameContextInUIThread() {
    ServiceContext context = ContextProvider.getContext();
    ContextTrackerRunnable runnable = new ContextTrackerRunnable();

    uiSession.exec( runnable );

    assertSame( context, runnable.getContext() );
  }

  public void testExec_executesRunnableWithFakeContextInBGThread() throws Throwable {
    ServiceContext context = ContextProvider.getContext();
    final ContextTrackerRunnable runnable = new ContextTrackerRunnable();

    Fixture.runInThread( new Runnable() {
      public void run() {
        uiSession.exec( runnable );
      }
    } );

    assertNotSame( context, runnable.getContext() );
    assertSame( uiSession, runnable.getUISession() );
  }

  public void testExec_executesRunnableWithFakeContextInDifferentUIThread() throws Throwable {
    ServiceContext context = ContextProvider.getContext();
    final ContextTrackerRunnable runnable = new ContextTrackerRunnable();

    Fixture.runInThread( new Runnable() {
      public void run() {
        Fixture.createServiceContext();
        uiSession.exec( runnable );
      }
    } );

    assertNotSame( context, runnable.getContext() );
    assertSame( uiSession, runnable.getUISession() );
  }

  private static class EmptyUISessionListener implements UISessionListener {
    public void beforeDestroy( UISessionEvent event ) {
    }
  }

  private static class ContextTrackerRunnable implements Runnable {

    private final AtomicReference<ServiceContext> context = new AtomicReference<ServiceContext>();
    private final AtomicReference<UISession> uiSession = new AtomicReference<UISession>();

    public void run() {
      context.set( ContextProvider.getContext() );
      uiSession.set( ContextProvider.getContext().getUISession() );
    }

    public ServiceContext getContext() {
      return context.get();
    }

    public UISession getUISession() {
      return uiSession.get();
    }

  }

}
