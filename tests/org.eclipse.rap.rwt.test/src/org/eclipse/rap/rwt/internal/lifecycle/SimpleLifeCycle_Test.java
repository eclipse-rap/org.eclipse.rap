/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.testfixture.internal.ConcurrencyTestUtil.runInThread;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.service.UIThreadListener;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.LoggingPhaseListener;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.rap.rwt.testfixture.internal.LoggingPhaseListener.PhaseEventInfo;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;


public class SimpleLifeCycle_Test {

  private LifeCycle lifeCycle;

  @Before
  public void setUp() {
    Fixture.setUp();
    lifeCycle = new SimpleLifeCycle( getApplicationContext() );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testPhaseOrderForInitialRequest() throws Exception {
    registerEntryPoint( TestEntryPoint.class );
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    PhaseEventInfo[] loggedEvents = phaseListener.getLoggedEvents();
    assertEquals( 4, loggedEvents.length );
    assertBeforePhaseEvent( loggedEvents[ 0 ], PhaseId.PREPARE_UI_ROOT );
    assertAfterPhaseEvent( loggedEvents[ 1 ], PhaseId.PREPARE_UI_ROOT );
    assertBeforePhaseEvent( loggedEvents[ 2 ], PhaseId.RENDER );
    assertAfterPhaseEvent( loggedEvents[ 3 ], PhaseId.RENDER );
  }

  @Test
  public void testPhaseOrderForSubsequentRequest() throws Exception {
    new Display();
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    PhaseEventInfo[] loggedEvents = phaseListener.getLoggedEvents();
    assertEquals( 8, loggedEvents.length );
    assertBeforePhaseEvent( loggedEvents[ 0 ], PhaseId.PREPARE_UI_ROOT );
    assertAfterPhaseEvent( loggedEvents[ 1 ], PhaseId.PREPARE_UI_ROOT );
    assertBeforePhaseEvent( loggedEvents[ 2 ], PhaseId.READ_DATA );
    assertAfterPhaseEvent( loggedEvents[ 3 ], PhaseId.READ_DATA );
    assertBeforePhaseEvent( loggedEvents[ 4 ], PhaseId.PROCESS_ACTION );
    assertAfterPhaseEvent( loggedEvents[ 5 ], PhaseId.PROCESS_ACTION );
    assertBeforePhaseEvent( loggedEvents[ 6 ], PhaseId.RENDER );
    assertAfterPhaseEvent( loggedEvents[ 7 ], PhaseId.RENDER );
  }

  @Test
  public void testThreadIsAttachedInInitialRequest() throws IOException {
    registerEntryPoint( TestEntryPoint.class );
    ThreadRecordingPhaseListener phaseListener = new ThreadRecordingPhaseListener( );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    Thread[] threads = phaseListener.getThreads();
    for( int i = 0; i < threads.length; i++ ) {
      assertSame( Thread.currentThread(), threads[ i ] );
    }
  }

  @Test
  public void testThreadIsDetachedInInitialRequest() throws IOException {
    registerEntryPoint( TestEntryPoint.class );
    lifeCycle.execute();
    assertNull( Display.getCurrent() );
    assertNull( LifeCycleUtil.getSessionDisplay().getThread() );
  }

  @Test
  public void testThreadIsAttachedInSubsequentRequest() throws IOException {
    registerEntryPoint( TestEntryPoint.class );
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    ThreadRecordingPhaseListener phaseListener = new ThreadRecordingPhaseListener( );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    Thread[] threads = phaseListener.getThreads();
    for( int i = 0; i < threads.length; i++ ) {
      assertSame( Thread.currentThread(), threads[ i ] );
    }
  }

  @Test
  public void testThreadIsDetachedInSubsequentRequest() throws IOException {
    registerEntryPoint( TestEntryPoint.class );
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setServerName( "/rap" );

    lifeCycle.execute();
    Fixture.fakeNewRequest();
    lifeCycle.execute();
    assertNull( Display.getCurrent() );
    assertNull( LifeCycleUtil.getSessionDisplay().getThread() );
  }

  // bug 361753
  @Test
  public void testDefaultDisplayIsAvailableInInitialRequest() throws IOException {
    registerEntryPoint( DefaultDisplayEntryPoint.class );
    Fixture.fakeNewRequest();

    lifeCycle.execute();

    assertNotNull( LifeCycleUtil.getSessionDisplay( ContextProvider.getUISession() ) );
  }

  @Test
  public void testPhaseListenersHaveApplicationScope() throws Exception {
    registerEntryPoint( TestEntryPoint.class );
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    newSession();
    lifeCycle.execute();
    assertTrue( phaseListener.getLoggedEvents().length > 0 );
  }

  @Test
  public void testAddPhaseListener() throws Exception {
    registerEntryPoint( TestEntryPoint.class );
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    assertTrue( phaseListener.getLoggedEvents().length > 0 );
  }

  @Test
  public void testRemovePhaseListener() throws Exception {
    registerEntryPoint( TestEntryPoint.class );
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.removePhaseListener( phaseListener );
    lifeCycle.execute();
    assertEquals( 0, phaseListener.getLoggedEvents().length );
  }

  @Test
  public void testRequestThreadExecRunsRunnableOnCallingThread() {
    final AtomicReference<Thread> invocationThread = new AtomicReference<>();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        invocationThread.set( Thread.currentThread() );
      }
    };

    lifeCycle.requestThreadExec( runnable );

    assertSame( Thread.currentThread(), invocationThread.get() );
  }

  @Test
  public void testGetUIThreadWhileLifeCycleInExecute() throws IOException {
    new Display();
    final AtomicReference<Thread> uiThread = new AtomicReference<>();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      @Override
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
      @Override
      public void beforePhase( PhaseEvent event ) {
      }
      @Override
      public void afterPhase( PhaseEvent event ) {
        uiThread.set( LifeCycleUtil.getUIThread( ContextProvider.getUISession() ).getThread() );
      }
    } );

    lifeCycle.execute();

    assertSame( Thread.currentThread(), uiThread.get() );
  }

  @Test
  public void testGetUIThreadAfterLifeCycleExecuted() throws IOException {
    registerEntryPoint( TestEntryPoint.class );
    lifeCycle.execute();

    IUIThreadHolder threadHolder = LifeCycleUtil.getUIThread( ContextProvider.getUISession() );

    assertNull( threadHolder );
  }

  @Test
  public void testInvalidateDisposesDisplay() throws Throwable {
    final UISession uiSession = ContextProvider.getUISession();
    Display display = new Display();
    lifeCycle.execute();

    runInThread( new Runnable() {
      @Override
      public void run() {
        uiSession.getHttpSession().invalidate();
      }
    } );

    assertTrue( display.isDisposed() );
  }

  @Test
  public void testSessionRestartDisposesDisplay() throws IOException {
    final UISession uiSession = ContextProvider.getUISession();
    Display display = new Display();
    lifeCycle.execute();

    uiSession.getHttpSession().invalidate();

    assertTrue( display.isDisposed() );
  }

  @Test
  public void testSleep() {
    try {
      lifeCycle.sleep();
      fail();
    } catch( UnsupportedOperationException expected ) {
      assertTrue( expected.getMessage().contains( "Display#sleep()" ) );
    }
  }

  @Test
  public void testNotifyUIThreadListeners() throws IOException {
    UIThreadListener listener = mock( UIThreadListener.class );
    ContextProvider.getApplicationContext().addUIThreadListener( listener );
    new Display();
    ArgumentCaptor<UISessionEvent> captor = ArgumentCaptor.forClass( UISessionEvent.class );
    InOrder inOrder = inOrder( listener );

    lifeCycle.execute();

    inOrder.verify( listener ).enterUIThread( captor.capture() );
    inOrder.verify( listener ).leaveUIThread( captor.capture() );
    inOrder.verifyNoMoreInteractions();
    for( UISessionEvent event : captor.getAllValues() ) {
      assertSame( ContextProvider.getUISession(), event.getUISession() );
    }
  }

  @Test
  public void testNotifyUIThreadListeners_twice() throws IOException {
    UIThreadListener listener = mock( UIThreadListener.class );
    ContextProvider.getApplicationContext().addUIThreadListener( listener );
    new Display();

    lifeCycle.execute();
    lifeCycle.execute();

    verify( listener, times( 2 ) ).enterUIThread( any( UISessionEvent.class ) );
    verify( listener, times( 2 ) ).leaveUIThread( any( UISessionEvent.class ) );
  }

  @Test
  public void testNotifyUIThreadListeners_haveAccessToUISession() throws IOException {
    UIThreadListener listener = new UIThreadListener() {
      @Override
      public void enterUIThread( UISessionEvent event ) {
        assertNotNull( RWT.getUISession() );
      }
      @Override
      public void leaveUIThread( UISessionEvent event ) {
        assertNotNull( RWT.getUISession() );
      }
    };
    ContextProvider.getApplicationContext().addUIThreadListener( listener );
    new Display();

    lifeCycle.execute();
    lifeCycle.execute();
  }

  @Test
  public void testContextOnShutdownFromBackgroundThread() throws Exception {
    final AtomicBoolean hasContext = new AtomicBoolean();
    // Activate SimpleLifeCycle
    getApplicationContext().getLifeCycleFactory().deactivate();
    getApplicationContext().getLifeCycleFactory().activate();
    registerEntryPoint( TestEntryPoint.class );
    final UISessionImpl uiSession = ( UISessionImpl )RWT.getUISession();
    uiSession.addUISessionListener( new UISessionListener() {
      @Override
      public void beforeDestroy( UISessionEvent event ) {
        hasContext.set( ContextProvider.hasContext() );
      }
    } );
    // Initialize shutdown adapter
    getApplicationContext().getLifeCycleFactory().getLifeCycle().execute();

    Thread thread = new Thread( new Runnable() {
      @Override
      public void run() {
        uiSession.getShutdownAdapter().interceptShutdown();
        // Prevents NPE in tearDown
        uiSession.setShutdownAdapter( null );
      }
    } );
    thread.setDaemon( true );
    thread.start();
    thread.join();

    assertTrue( hasContext.get() );
  }

  private void assertBeforePhaseEvent( PhaseEventInfo beforePrepareUIRoot, PhaseId phaseId ) {
    assertTrue( beforePrepareUIRoot.before );
    assertEquals( phaseId, beforePrepareUIRoot.phaseId );
    assertSame( lifeCycle, beforePrepareUIRoot.source );
  }

  private void assertAfterPhaseEvent( PhaseEventInfo beforePrepareUIRoot, PhaseId phaseId ) {
    assertFalse( beforePrepareUIRoot.before );
    assertEquals( phaseId, beforePrepareUIRoot.phaseId );
    assertSame( lifeCycle, beforePrepareUIRoot.source );
  }

  private static void registerEntryPoint( Class<? extends EntryPoint> type ) {
    getApplicationContext().getEntryPointManager().register( "/rap", type, null );
  }

  private static void newSession() {
    ContextProvider.disposeContext();
    Fixture.createServiceContext();
    Fixture.fakeClient( new WebClient() );
  }

  private static class ThreadRecordingPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;

    private final List<Thread> threads;

    private ThreadRecordingPhaseListener() {
      threads = new LinkedList<Thread>();
    }

    @Override
    public void beforePhase( PhaseEvent event ) {
      threads.add( Display.getCurrent().getThread() );
    }

    @Override
    public void afterPhase( PhaseEvent event ) {
      threads.add( Display.getCurrent().getThread() );
    }

    @Override
    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }

    Thread[] getThreads() {
      Thread[] result = new Thread[ threads.size() ];
      threads.toArray( result );
      return result;
    }
  }

  private static class TestEntryPoint implements EntryPoint {
    @Override
    public int createUI() {
      new Display();
      return 0;
    }
  }

  private static class DefaultDisplayEntryPoint implements EntryPoint {
    @Override
    public int createUI() {
      Display.getDefault();
      return 0;
    }
  }

}
