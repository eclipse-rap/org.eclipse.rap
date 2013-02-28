/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.service.ClientInfo;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.ISessionShutdownAdapter;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestLogger;
import org.eclipse.rap.rwt.testfixture.TestServletContext;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class UISessionImpl_Test {

  private HttpSession httpSession;
  private UISessionImpl uiSession;
  private List<Throwable> servletLogEntries;
  private Locale localeBuffer;

  @Before
  public void setUp() {
    localeBuffer = Locale.getDefault();
    Locale.setDefault( Locale.ENGLISH );
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

  @After
  public void tearDown() {
    Fixture.tearDown();
    Locale.setDefault( localeBuffer );
  }

  @Test
  public void testConstructor_failsWithWithNullArgument() {
    try {
      new UISessionImpl( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testGetInstanceFromSession() {
    UISessionImpl result = UISessionImpl.getInstanceFromSession( httpSession );

    assertSame( result, uiSession );
  }

  @Test
  public void testGetInstanceFromSession_returnsNullAfterInvalidate() {
    httpSession.invalidate();
    UISessionImpl result = UISessionImpl.getInstanceFromSession( httpSession );

    assertNull( result );
  }

  @Test
  public void testGetId() {
    assertNotNull( uiSession.getId() );
  }

  @Test
  public void testGetId_validAfterSessionWasInvalidated() {
    String id = uiSession.getId();
    httpSession.invalidate();

    assertEquals( id, uiSession.getId() );
  }

  @Test
  public void testGetHttpSession() {
    assertSame( httpSession, uiSession.getHttpSession() );
  }

  @Test
  public void testAttachHttpSession() {
    HttpSession anotherSession = new TestSession();
    uiSession.attachHttpSession( anotherSession );

    assertSame( anotherSession, uiSession.getHttpSession() );
  }

  @Test
  public void testAttachHttpSession_failsWithNullArgument() {
    try {
      uiSession.attachHttpSession( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testAttachHttpSession_doesNotChangeId() {
    String initialId = uiSession.getId();
    TestSession anotherSession = new TestSession();
    anotherSession.setId( "some.other.id" );
    uiSession.attachHttpSession( anotherSession );

    String id = uiSession.getId();

    assertEquals( initialId, id );
  }

  @Test
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

  @Test
  public void testIsBound() {
    assertTrue( uiSession.isBound() );
  }

  @Test
  public void testIsBound_isFalseAfterSessionWasInvalidated() {
    httpSession.invalidate();

    assertFalse( uiSession.isBound() );
  }

  @Test
  public void testGetAttribute() {
    Object value = new Object();
    uiSession.setAttribute( "name", value );

    Object result = uiSession.getAttribute( "name" );

    assertSame( value, result );
  }

  @Test
  public void testGetAttribute_failsWithNullName() {
    try {
      uiSession.getAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testGetAttribute_returnsNullWithNonExistingName() {
    Object attribute = uiSession.getAttribute( "does.not.exist" );

    assertNull( attribute );
  }

  @Test
  public void testGetAttribute_returnsNullWhenUnbound() {
    uiSession.setAttribute( "name", null );
    httpSession.invalidate();

    Object result = uiSession.getAttribute( "name" );

    assertNull( result );
  }

  @Test
  public void testSetAttribute_failsWithNullName() {
    try {
      uiSession.setAttribute( null, new Object() );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testSetAttribute_returnsFalseWhenUnbound() {
    httpSession.invalidate();

    boolean result = uiSession.setAttribute( "name", null );

    assertFalse( result );
  }

  @Test
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

  @Test
  public void testRemoveAttribute_failsWithNullName() {
    try {
      uiSession.removeAttribute( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testRemoveAttribute_removesExistingAttribute() {
    uiSession.setAttribute( "name", new Object() );

    uiSession.removeAttribute( "name" );

    assertNull( uiSession.getAttribute( "name" ) );
  }

  @Test
  public void testRemoveAttribute_returnsNullForNonExistingAttribute() {
    uiSession.removeAttribute( "does.not.exist" );

    assertNull( uiSession.getAttribute( "does.not.exist" ) );
  }

  @Test
  public void testRemoveAttribute_returnsFalseWhenUnbound() {
    uiSession.setAttribute( "name", null );
    httpSession.invalidate();

    boolean result = uiSession.removeAttribute( "name" );

    assertFalse( result );
  }

  @Test
  public void testGetAttributeNames() {
    uiSession.setAttribute( "name", new Object() );

    Enumeration attributeNames = uiSession.getAttributeNames();

    assertTrue( attributeNames.hasMoreElements() );
    assertEquals( "name", attributeNames.nextElement() );
    assertFalse( attributeNames.hasMoreElements() );
  }

  @Test
  public void testGetAttributeNames_returnsSnapshot() {
    uiSession.setAttribute( "name", new Object() );

    Enumeration attributeNames = uiSession.getAttributeNames();
    uiSession.setAttribute( "other.name", new Object() );

    assertTrue( attributeNames.hasMoreElements() );
    assertEquals( "name", attributeNames.nextElement() );
    assertFalse( attributeNames.hasMoreElements() );
  }

  @Test
  public void testGetAttributeNames_isEmptyWhenUnbound() {
    uiSession.setAttribute( "name", "value" );
    httpSession.invalidate();

    Enumeration attributeNames = uiSession.getAttributeNames();

    assertNotNull( attributeNames );
    assertFalse( attributeNames.hasMoreElements() );
  }

  @Test
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

  @Test
  public void testAddUISessionListener_failsWithNullArgument() {
    try {
      uiSession.addUISessionListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testAddUISessionListener_returnsFalseWhenUnbound() {
    httpSession.invalidate();
    EmptyUISessionListener listener = new EmptyUISessionListener();

    boolean added = uiSession.addUISessionListener( listener );

    assertFalse( added );
  }

  @Test
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

  @Test
  public void testRemoveUISessionListener_failsWithNullArgument() {
    try {
      uiSession.removeUISessionListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testRemoveUISessionListener_returnsFalseWhenUnbound() {
    httpSession.invalidate();
    EmptyUISessionListener listener = new EmptyUISessionListener();

    boolean removed = uiSession.removeUISessionListener( listener );

    assertFalse( removed );
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
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

  @Test
  public void testOverrideAtributeWithNull() {
    String attributeName = "name";
    uiSession.setAttribute( attributeName, new Object() );

    uiSession.setAttribute( attributeName, null );

    assertNull( uiSession.getAttribute( attributeName ) );
  }

  @Test
  public void testOverrideSerializableAttributeWithNonSerializable() {
    String attributeName = "name";
    Serializable serializableAttribute = new String();
    uiSession.setAttribute( attributeName, serializableAttribute );

    Object overridingAtribute = new Object();
    uiSession.setAttribute( attributeName, overridingAtribute );

    assertSame( overridingAtribute, uiSession.getAttribute( attributeName ) );
  }

  @Test
  public void testOverrideNonSerializableAttributeWithSerializable() {
    String attributeName = "name";
    Object nonSerializableAttribute = new Object();
    uiSession.setAttribute( attributeName, nonSerializableAttribute );

    Serializable overridingAtribute = new String();
    uiSession.setAttribute( attributeName, overridingAtribute );

    assertSame( overridingAtribute, uiSession.getAttribute( attributeName ) );
  }

  @Test
  public void testGetClient() {
    Client client = mock( Client.class );
    ClientSelector clientSelector = mock( ClientSelector.class );
    when( clientSelector.getSelectedClient( any( UISession.class ) ) ).thenReturn( client );
    ApplicationContextImpl applicationContext = mock( ApplicationContextImpl.class );
    when( applicationContext.getClientSelector() ).thenReturn( clientSelector );
    ApplicationContextUtil.set( uiSession, applicationContext );

    Client result = uiSession.getClient();

    assertSame( client, result );
  }

  @Test
  public void testGetConnection_returnsAnObject() {
    Connection result = uiSession.getConnection();

    assertNotNull( result );
  }

  @Test
  public void testGetConnection_returnsSameObject() {
    Connection result = uiSession.getConnection();

    assertSame( uiSession.getConnection(), result );
  }

  @Test
  public void testSetLocale_canBeResetWithNull() {
    ApplicationContextUtil.set( uiSession, ApplicationContextUtil.getInstance() );
    Fixture.fakeClient( mockClientWithLocale( null ) );
    uiSession.setLocale( Locale.ITALIAN );

    uiSession.setLocale( null );

    assertSame( Locale.getDefault(), uiSession.getLocale() );
  }

  @Test
  public void testGetLocale_returnsSetLocale() {
    uiSession.setLocale( Locale.UK );

    Locale locale = uiSession.getLocale();

    assertSame( Locale.UK, locale );
  }

  @Test
  public void testGetLocale_fallsBackToClientLocale() {
    ApplicationContextUtil.set( uiSession, ApplicationContextUtil.getInstance() );
    Fixture.fakeClient( mockClientWithLocale( Locale.ITALIAN ) );

    Locale locale = uiSession.getLocale();

    assertSame( Locale.ITALIAN, locale );
  }

  @Test
  public void testGetLocale_fallsBackToSystemLocale_withoutClientInfo() {
    ApplicationContextUtil.set( uiSession, ApplicationContextUtil.getInstance() );
    Fixture.fakeClient( mock( Client.class ) );

    Locale locale = uiSession.getLocale();

    assertSame( Locale.getDefault(), locale );
  }

  @Test
  public void testGetLocale_fallsBackToSystemLocale_withoutClientLocale() {
    ApplicationContextUtil.set( uiSession, ApplicationContextUtil.getInstance() );
    Fixture.fakeClient( mockClientWithLocale( null ) );

    Locale locale = uiSession.getLocale();

    assertSame( Locale.getDefault(), locale );
  }

  @Test
  public void testGetLocale_worksInBackgroundThread() throws Throwable {
    ApplicationContextUtil.set( uiSession, ApplicationContextUtil.getInstance() );
    Fixture.fakeClient( mock( Client.class ) );
    final AtomicReference<Locale> localeCaptor = new AtomicReference<Locale>();

    Fixture.runInThread( new Runnable() {
      public void run() {
        localeCaptor.set( uiSession.getLocale() );
      }
    } );

    assertNotNull( localeCaptor.get() );
  }

  @Test
  public void testExec_failsWithNullArgument() {
    try {
      uiSession.exec( null );
      fail();
    } catch( NullPointerException exception ) {
      assertTrue( exception.getMessage().contains( "runnable" ) );
    }
  }

  @Test
  public void testExec_executesRunnable() {
    Runnable runnable = mock( Runnable.class );

    uiSession.exec( runnable );

    verify( runnable ).run();
  }

  @Test
  public void testExec_executesRunnableWithSameContextInUIThread() {
    ServiceContext context = ContextProvider.getContext();
    ContextTrackerRunnable runnable = new ContextTrackerRunnable();

    uiSession.exec( runnable );

    assertSame( context, runnable.getContext() );
  }

  @Test
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

  @Test
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

  private static Client mockClientWithLocale( Locale locale ) {
    Client client = mock( Client.class );
    ClientInfo clientInfo = mock( ClientInfo.class );
    when( clientInfo.getLocale() ).thenReturn( locale );
    when( client.getService( same( ClientInfo.class ) ) ).thenReturn( clientInfo  );
    return client;
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
