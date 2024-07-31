/*******************************************************************************
 * Copyright (c) 2007, 2023 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.serverpush;

import static org.eclipse.rap.rwt.testfixture.internal.ConcurrencyTestUtil.runInThread;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingListener;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.rap.rwt.testfixture.internal.TestResponse;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServerPushManager_Test {

  public static final String SYS_PROP_SLEEP_TIME = "sleepTime";

  private static final int SLEEP_TIME;
  private static final int REQUEST_WAIT_TIMEOUT = 1000;

  private static final Object HANDLE_1 = new Object();
  private static final Object HANDLE_2 = new Object();
  private static final String RUN_ASYNC_EXEC = "run async exec|";
  private static final Runnable EMPTY_RUNNABLE = new NoOpRunnable();

  static {
    String sleepTimeProp = System.getProperty( SYS_PROP_SLEEP_TIME );
    SLEEP_TIME = sleepTimeProp == null ? 50 : Integer.parseInt( sleepTimeProp );
  }

  private volatile String log = "";
  private Display display;
  private ServerPushManager manager;
  private ServerPushServiceHandler pushServiceHandler;

  @Before
  public void setUp() {
    Fixture.setUp();
    log = "";
    display  = new Display();
    manager = ServerPushManager.getInstance();
    pushServiceHandler = new ServerPushServiceHandler();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testWakeClient() throws InterruptedException {
    final AtomicReference<Throwable> serverPushServiceHandlerThrowable = new AtomicReference<>();
    final ServiceContext context = ContextProvider.getContext();
    Thread thread = new Thread( new Runnable() {
      @Override
      public void run() {
        ContextProvider.setContext( context );
        Fixture.fakeResponseWriter();
        ServerPushServiceHandler pushServiceHandler = new ServerPushServiceHandler();
        try {
          manager.activateServerPushFor( HANDLE_1 );
          pushServiceHandler.service( ContextProvider.getRequest(), ContextProvider.getResponse() );
        } catch( Throwable thr ) {
          serverPushServiceHandlerThrowable.set( thr );
        }
      }
    } );
    thread.start();
    Thread.sleep( SLEEP_TIME );
    if( serverPushServiceHandlerThrowable.get() != null ) {
      serverPushServiceHandlerThrowable.get().printStackTrace();
    }
    assertNull( serverPushServiceHandlerThrowable.get() );
    assertTrue( manager.isCallBackRequestBlocked() );

    manager.setHasRunnables( true );
    manager.wakeClient();
    thread.join();

    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( thread.isAlive() );
  }

  @Test
  public void testWaitOnUIThread() throws Exception {
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator();
    callBackRequestSimulator.sendRequest();

    display.wake();

    assertTrue( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator.exceptionOccured() );
    manager.releaseBlockedRequest();
  }

  @Test
  public void testWaitOnBackgroundThread() throws Throwable {
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator();
    callBackRequestSimulator.sendRequest();
    assertTrue( manager.isCallBackRequestBlocked() );

    callDisplayWake();
    callBackRequestSimulator.waitForRequest();

    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator.exceptionOccured() );
  }

  // same test as above, but while UIThread running
  @Test
  public void testWaitOnBackgroundThreadDuringLifecycle() throws Throwable {
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator();
    callBackRequestSimulator.sendRequest();
    assertTrue( manager.isCallBackRequestBlocked() );

    // assume that UIThread is currently running the life cycle
    manager.notifyUIThreadStart();
    callDisplayWake();
    manager.notifyUIThreadEnd();
    callBackRequestSimulator.waitForRequest();

    assertFalse( callBackRequestSimulator.exceptionOccured() );
    assertFalse( manager.isCallBackRequestBlocked() );
  }

  @Test
  public void testAsyncExecWhileLifeCycleIsRunning() {
    fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
    simulateAsyncExecDuringLifeCycle();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    assertTrue( manager.hasRunnables() );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( RUN_ASYNC_EXEC, log );
    assertFalse( manager.isCallBackRequestBlocked() );
  }

  @Test
  public void testAsyncExecWithBackgroundAndLifeCycleRunnables() throws Throwable {
    // test unblocking in case of background addition of runnables
    simulateBackgroundAddition( ContextProvider.getContext() );
    // test runnables execution during lifecycle with interlocked additions
    fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
    simulateAsyncExecDuringLifeCycle();
    Fixture.executeLifeCycleFromServerThread();
    assertFalse( manager.isCallBackRequestBlocked() );
    assertEquals( RUN_ASYNC_EXEC + RUN_ASYNC_EXEC + RUN_ASYNC_EXEC, log );
  }

  @Test
  public void testCallBackRequestBlocking() throws Exception {
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator();
    callBackRequestSimulator.sendRequest();
    assertFalse( callBackRequestSimulator.exceptionOccured() );
    assertTrue( manager.isCallBackRequestBlocked() );
  }

  @Test
  public void testCallBackRequestReleasing() throws Throwable {
    ServiceContext context = ContextProvider.getContext();
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator( context );
    callBackRequestSimulator.sendRequest();

    simulateBackgroundAddition( context );
    callBackRequestSimulator.waitForRequest();

    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator.isRequestRunning() );
    assertEquals( "", log );
  }

  @Test
  public void testCallBackRequestNotBlockedWhenRunnablesExist() throws Throwable {
    ServiceContext context = ContextProvider.getContext();
    display.asyncExec( mock( Runnable.class ) );
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator( context );
    callBackRequestSimulator.sendRequest();

    callBackRequestSimulator.waitForRequest();

    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator.isRequestRunning() );
  }

  @Test
  public void testCallBackRequestIsReleased_onSessionInvalidation() throws Exception {
    ServiceContext context = ContextProvider.getContext();
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator( context );
    callBackRequestSimulator.sendRequest();

    context.getUISession().getHttpSession().invalidate();
    callBackRequestSimulator.waitForRequest();

    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator.isRequestRunning() );
    assertFalse( callBackRequestSimulator.exceptionOccured() );
  }

  /*
   * See bug 422472: ServerPush request is not released when web application is stopped
   */
  @Test
  public void testCallBackRequestIsReleased_onApplicationContextDeactivation() throws Exception {
    ServiceContext context = ContextProvider.getContext();
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator( context );
    callBackRequestSimulator.sendRequest();

    context.getApplicationContext().deactivate();
    callBackRequestSimulator.waitForRequest();

    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator.isRequestRunning() );
    assertFalse( callBackRequestSimulator.exceptionOccured() );
  }

  @Test
  public void testCallBackRequestIsReleasedWhenSessionExpires() {
    HttpSession httpSession = ContextProvider.getUISession().getHttpSession();
    httpSession.setMaxInactiveInterval( 1 );
    HttpSessionBindingListener sessionListener = mock( HttpSessionBindingListener.class );
    httpSession.setAttribute( "listener", sessionListener );
    manager.setRequestCheckInterval( 10 );

    manager.activateServerPushFor( HANDLE_1 );

    // must not block
    manager.processRequest( ContextProvider.getResponse() );
  }

  @Test
  public void testMultipleCallBackRequests() throws Exception {
    manager.setRequestCheckInterval( 500 );
    ServiceContext context1 = ContextProvider.getContext();
    CallBackRequestSimulator callBackRequestSimulator1 = new CallBackRequestSimulator( context1 );
    callBackRequestSimulator1.sendRequest();
    ServiceContext context2 = createServiceContext( new TestResponse() );
    CallBackRequestSimulator callBackRequestSimulator2 = new CallBackRequestSimulator( context2 );

    callBackRequestSimulator2.sendRequest();
    callBackRequestSimulator1.waitForRequest();

    assertTrue( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator1.exceptionOccured() );
    assertFalse( callBackRequestSimulator2.exceptionOccured() );
    assertFalse( callBackRequestSimulator1.isRequestRunning() );
    assertTrue( callBackRequestSimulator2.isRequestRunning() );
  }

  @Test
  public void testAsyncExec() throws Throwable {
    Throwable[] serverPushServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    // test runnables addition while no server push thread is not blocked
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator( context );
    callBackRequestSimulator.sendRequest();

    manager.notifyUIThreadEnd();
    simulateBackgroundAddition( context );
    fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread();
    // let request thread finish off and die
    callBackRequestSimulator.requestThread.join( 100 );

    assertNull( serverPushServiceHandlerThrowable[ 0 ] );
    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator.isRequestRunning() );
    assertEquals( RUN_ASYNC_EXEC + RUN_ASYNC_EXEC, log );
  }

  // This test ensures that addSync doesn't cause deadlocks
  @Test
  public void testSyncExecBlock() throws Exception {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final ServiceContext context = ContextProvider.getContext();
    // the code in bgRunnable simulates a bg-thread that calls Display#addSync
    Runnable bgRunnable = new Runnable() {
      @Override
      public void run() {
        ContextProvider.setContext( context );
        Fixture.fakeResponseWriter();
        display.syncExec( EMPTY_RUNNABLE );
      }
    };
    // simulate a lot "parallel" bg-threads to provoke multi-threading problems
    List<Thread> bgThreads = new ArrayList<Thread>();
    for( int i = 0; i < 200; i++ ) {
      Thread bgThread = new Thread( bgRunnable, "Test-Bg-Thread " + i );
      bgThread.setDaemon( true );
      bgThread.start();
      display.readAndDispatch();
      bgThreads.add( bgThread );
    }
    // wait (hopefully long enough) until all bg-threads have done their work
    // (i.e. called addSync) and make sure all sync-runnables get executed
    Thread.sleep( SLEEP_TIME );
    while( display.readAndDispatch() ) {
      Thread.sleep( SLEEP_TIME );
    }
    // wait for all bgThreads to terminate
    for( int i = 0; i < bgThreads.size(); i++ ) {
      Thread bgThread = bgThreads.get( i );
      bgThread.join();
      display.readAndDispatch();
    }
    assertFalse( manager.hasRunnables() );
  }

  // This test ensures that SyncRunnable releases the blocked thread in case of
  // an exception
  @Test
  public void testAddSyncWithExceptionInRunnable() throws Exception {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final ServiceContext context = ContextProvider.getContext();
    final AtomicReference<SWTException> exceptionInBgThread = new AtomicReference<SWTException>();
    // the code in bgRunnable simulates a bg-thread that calls Display#addSync
    // and causes an exception in the runnable
    Runnable bgRunnable = new Runnable() {
      @Override
      public void run() {
        ContextProvider.setContext( context );
        Fixture.fakeResponseWriter();
        Runnable causeException = new Runnable() {
          @Override
          public void run() {
            throw new RuntimeException( "Exception in sync-runnable" );
          }
        };
        try {
          display.syncExec( causeException );
        } catch( SWTException e ) {
          exceptionInBgThread.set( e );
        }
      }
    };

    Thread bgThread = new Thread( bgRunnable );
    bgThread.setDaemon( true );
    bgThread.start();
    try {
      while( !display.readAndDispatch() ) {
        Thread.yield();
        Thread.sleep( SLEEP_TIME );
      }
      fail( "Exception from causeException-runnable must end up here" );
    } catch( SWTException expected ) {
    }
    Thread.sleep( SLEEP_TIME );
    assertNotNull( exceptionInBgThread.get() );
    assertFalse( bgThread.isAlive() );
  }

  @Test
  public void testMustBlockCallBackRequest() {
    assertFalse( manager.mustBlockCallBackRequest( 0, 1 ) );
  }

  @Test
  public void testMustBlockCallBackRequestWhenActive() {
    manager.activateServerPushFor( HANDLE_1 );
    assertTrue( manager.mustBlockCallBackRequest( 0, 1 ) );
  }

  @Test
  public void testMustBlockCallBackRequestWhenActiveAndRunnablesPending() {
    manager.activateServerPushFor( HANDLE_1 );
    manager.setHasRunnables( true );
    assertFalse( manager.mustBlockCallBackRequest( 0, 1 ) );
  }

  @Test
  public void testMustBlockCallBackRequestWhenDeactivatedAndRunnablesPending() {
    manager.setHasRunnables( true );
    assertFalse( manager.mustBlockCallBackRequest( 0, 1 ) );
  }

  @Test
  public void testMustBlockCallBackRequestWhenAfterCheckInterval() {
    manager.activateServerPushFor( HANDLE_1 );
    assertFalse( manager.mustBlockCallBackRequest( 0, 65000 ) );
  }

  @Test
  public void testNeedActivationFromDifferentSession() throws Throwable {
    // test that on/off switching is managed in session scope
    manager.activateServerPushFor( HANDLE_1 );
    final AtomicBoolean otherSession = new AtomicBoolean();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        Fixture.createServiceContext();
        new Display();
        otherSession.set( ServerPushManager.getInstance().needsActivation() );
      }
    };
    runInThread( runnable );
    assertFalse( otherSession.get() );
  }

  @Test
  public void testNeedActivationWithoutActivateCall() {
    boolean needsActivation = manager.needsActivation();
    assertFalse( needsActivation );
  }

  @Test
  public void testNeedActivationAfterDeactivate() {
    manager.deactivateServerPushFor( HANDLE_1 );
    assertFalse( manager.needsActivation() );
  }

  @Test
  public void testNeedActivationWithDifferentIds() {
    manager.activateServerPushFor( HANDLE_1 );
    manager.activateServerPushFor( HANDLE_2 );
    assertTrue( manager.needsActivation() );
  }

  @Test
  public void testNeedActivationAfterActivateTwoDeactivateOne() {
    manager.activateServerPushFor( HANDLE_1 );
    manager.activateServerPushFor( HANDLE_2 );
    manager.deactivateServerPushFor( HANDLE_1 );
    assertTrue( manager.needsActivation() );
  }

  @Test
  public void testNeedActivateTwice() {
    manager.activateServerPushFor( HANDLE_1 );
    manager.deactivateServerPushFor( HANDLE_1 );
    manager.activateServerPushFor( HANDLE_2 );
    assertTrue( manager.needsActivation() );
  }

  @Test
  public void testNeedActivationWithActivateDeactivateAndPendingRunnables() {
    manager.activateServerPushFor( HANDLE_1 );
    display.asyncExec( EMPTY_RUNNABLE );
    manager.deactivateServerPushFor( HANDLE_1 );
    assertTrue( manager.needsActivation() );
  }

  @Test
  public void testNeedActivationWithPendingRunnablesDoesntEnableServerPush() {
    display.asyncExec( EMPTY_RUNNABLE );
    assertFalse( manager.needsActivation() );
  }

  @Test
  public void testIsSessionExpiredWithInfiniteSessionTimeout() {
    ContextProvider.getUISession().getHttpSession().setMaxInactiveInterval( -1 );

    boolean sessionExpired = ServerPushManager.isSessionExpired( 1, 2 );

    assertFalse( sessionExpired );
  }

  @Test
  public void testIsSessionExpiredWhenSessionTimedOut() {
    ContextProvider.getUISession().getHttpSession().setMaxInactiveInterval( 10 );

    boolean sessionExpired = ServerPushManager.isSessionExpired( 1, 20000 );

    assertTrue( sessionExpired );
  }

  @Test
  public void testIsSessionExpiredWhenSessionActive() {
    ContextProvider.getUISession().getHttpSession().setMaxInactiveInterval( 10 );

    boolean sessionExpired = ServerPushManager.isSessionExpired( 1, 9000 );

    assertFalse( sessionExpired );
  }

  @Test
  public void testIsSessionExpiredWhenUISessionInactive() {
    UISessionImpl uiSession = ( UISessionImpl )ContextProvider.getUISession();
    uiSession.shutdown();

    boolean sessionExpired = ServerPushManager.isSessionExpired( 1, 2 );

    assertTrue( sessionExpired );
  }

  @Test
  public void testSetHasRunnablesWithoutStateInfo() {
    // Service handlers don't have a state info
    manager.activateServerPushFor( HANDLE_1 );
    Fixture.replaceServiceStore( null );

    try {
      manager.setHasRunnables( true );
    } catch( NullPointerException notExpected ) {
      fail();
    }
  }

  @Test
  public void testResponseHeaders() throws IOException {
    TestResponse response = ( TestResponse )ContextProvider.getResponse();

    pushServiceHandler.service( ContextProvider.getRequest(), response );

    assertNotNull( response.getHeader( "Cache-Control" ) );
    assertNotNull( response.getHeader( "Pragma" ) );
    assertNotNull( response.getHeader( "Expires" ) );
  }

  private void simulateBackgroundAddition( final ServiceContext serviceContext ) throws Throwable {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        ContextProvider.setContext( serviceContext );
        display.asyncExec( new AsyncExecRunnable() );
        display.asyncExec( new AsyncExecRunnable() );
      }
    };
    runInThread( runnable );
  }

  private void simulateAsyncExecDuringLifeCycle() {
    ProcessActionRunner.add( new Runnable() {
      @Override
      public void run() {
        Runnable target = new Runnable() {
          @Override
          public void run() {
            display.asyncExec( new AsyncExecRunnable() );
          }
        };
        try {
          runInThread( target );
        } catch( Throwable e ) {
          e.printStackTrace();
        }
      }
    } );
  }

  private void callDisplayWake() throws Throwable {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        display.wake();
      }
    };
    runInThread( runnable );
  }

  private void fakeNewRequest() {
    Fixture.fakeNewRequest();
    ContextProvider.getUISession().setAttribute( "org.eclipse.swt.display", display );
  }

  private static ServiceContext createServiceContext( TestResponse response ) {
    UISession uiSession = ContextProvider.getContext().getUISession();
    TestRequest request = new TestRequest();
    ApplicationContextImpl applicationContext = mock( ApplicationContextImpl.class );
    ServiceContext serviceContext = new ServiceContext( request, response, applicationContext );
    serviceContext.setServiceStore( new ServiceStore() );
    serviceContext.setUISession( uiSession );
    return serviceContext;
  }

  private class AsyncExecRunnable implements Runnable {
    @Override
    public void run() {
      log += RUN_ASYNC_EXEC;
    }
  }

  private class CallBackRequestSimulator {
    private final ServiceContext serviceContext;
    private volatile Thread requestThread;
    private volatile Throwable exception;

    CallBackRequestSimulator() {
      serviceContext = ContextProvider.getContext();
    }

    CallBackRequestSimulator( ServiceContext serviceContext ) {
      this.serviceContext = serviceContext;
    }

    void sendRequest() throws InterruptedException {
      requestThread = new Thread( new Runnable() {
        @Override
        public void run() {
          ContextProvider.setContext( serviceContext );
          Fixture.fakeResponseWriter();
          try {
            manager.activateServerPushFor( HANDLE_1 );
            pushServiceHandler.service( ContextProvider.getRequest(),
                                        ContextProvider.getResponse() );
          } catch( Throwable thr ) {
            exception = thr;
          }
        }
      } );
      requestThread.start();
      Thread.sleep( SLEEP_TIME );
    }

    void waitForRequest() throws InterruptedException {
      requestThread.join( REQUEST_WAIT_TIMEOUT );
    }

    boolean isRequestRunning() {
      return requestThread.isAlive();
    }

    boolean exceptionOccured() {
      return exception != null;
    }
  }

}
