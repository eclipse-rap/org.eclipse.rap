/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.uicallback;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingListener;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;


public class ServerPushManager_Test extends TestCase {
  public static final String SYS_PROP_SLEEP_TIME = "sleepTime";
  public static final String SYS_PROP_TIMER_EXEC_DELAY = "timerExecDelay";

  private static final int SLEEP_TIME;
  private static final int TIMER_EXEC_DELAY;

  private static final String ID_1 = "id_1";
  private static final String ID_2 = "id_2";
  private static final String RUN_ASYNC_EXEC = "run async exec|";
  private static final Runnable EMPTY_RUNNABLE = new NoOpRunnable();

  static {
    String sleepTimeProp = System.getProperty( SYS_PROP_SLEEP_TIME );
    SLEEP_TIME = sleepTimeProp == null ? 200 : Integer.parseInt( sleepTimeProp );
    String timerExecDelayProp = System.getProperty( SYS_PROP_TIMER_EXEC_DELAY );
    TIMER_EXEC_DELAY = timerExecDelayProp == null ? 5000 :Integer.parseInt( timerExecDelayProp );
  }

  private volatile String log = "";
  private Display display;
  private ServerPushManager manager;
  private ServerPushServiceHandler pushServiceHandler;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    log = "";
    display  = new Display();
    manager = ServerPushManager.getInstance();
    pushServiceHandler = new ServerPushServiceHandler();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testWakeClient() throws InterruptedException {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    final ServiceContext context[] = { ContextProvider.getContext() };
    Thread thread = new Thread( new Runnable() {
      public void run() {
        ContextProvider.setContext( context[ 0 ] );
        Fixture.fakeResponseWriter();
        ServerPushServiceHandler pushServiceHandler = new ServerPushServiceHandler();
        try {
          manager.activateServerPushFor( "foo" );
          pushServiceHandler.service( ContextProvider.getRequest(),
                                            ContextProvider.getResponse() );
        } catch( Throwable thr ) {
          uiCallBackServiceHandlerThrowable[ 0 ] = thr;
        }
      }
    } );
    thread.start();
    Thread.sleep( SLEEP_TIME );
    if( uiCallBackServiceHandlerThrowable[ 0 ] != null ) {
      uiCallBackServiceHandlerThrowable[ 0 ].printStackTrace();
    }
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertTrue( manager.isCallBackRequestBlocked() );

    manager.setHasRunnables( true );
    manager.wakeClient();
    thread.join();

    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( thread.isAlive() );
  }

  public void testWaitOnUIThread() throws Exception {
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator();
    callBackRequestSimulator.sendRequest();

    display.wake();

    assertTrue( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator.exceptionOccured() );
    manager.releaseBlockedRequest();
  }

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

  public void testCallBackRequestBlocking() throws Exception {
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator();
    callBackRequestSimulator.sendRequest();
    assertFalse( callBackRequestSimulator.exceptionOccured() );
    assertTrue( manager.isCallBackRequestBlocked() );
  }

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

  public void testCallBackRequestNotBlockedWhenRunnablesExist() throws Throwable {
    ServiceContext context = ContextProvider.getContext();
    display.asyncExec( mock( Runnable.class ) );
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator( context );
    callBackRequestSimulator.sendRequest();

    callBackRequestSimulator.waitForRequest();

    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator.isRequestRunning() );
  }

  public void testCallBackRequestIsReleasedOnSessionInvalidate() throws Exception {
    ServiceContext context = ContextProvider.getContext();
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator( context );
    callBackRequestSimulator.sendRequest();

    context.getUISession().getHttpSession().invalidate();
    callBackRequestSimulator.waitForRequest();

    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( callBackRequestSimulator.isRequestRunning() );
    assertFalse( callBackRequestSimulator.exceptionOccured() );
  }

  public void testCallBackRequestIsReleasedWhenSessionExpires() {
    HttpSession httpSession = ContextProvider.getUISession().getHttpSession();
    httpSession.setMaxInactiveInterval( 1 );
    HttpSessionBindingListener sessionListener = mock( HttpSessionBindingListener.class );
    httpSession.setAttribute( "listener", sessionListener );
    manager.setRequestCheckInterval( 10 );

    manager.activateServerPushFor( "id" );

    // must not block
    manager.processRequest( ContextProvider.getResponse() );
  }

  public void testMultipleCallBackRequests() throws Exception {
    manager.setRequestCheckInterval( 20 );
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

  public void testCallBackRequestTerminatsWhenConnectionBreaks() throws Exception {
    manager.setRequestCheckInterval( 20 );
    TestResponse response = new TestResponse() {
      @Override
      public PrintWriter getWriter() throws IOException {
        PrintWriter failingWriter = mock( PrintWriter.class );
        when( new Boolean( failingWriter.checkError() ) ).thenReturn( Boolean.TRUE );
        return failingWriter;
      }
    };
    ServiceContext context2 = createServiceContext( response );
    CallBackRequestSimulator callBackRequestSimulator = new CallBackRequestSimulator( context2 );
    callBackRequestSimulator.sendRequest();

    Thread.sleep( SLEEP_TIME );

    assertFalse( manager.isCallBackRequestBlocked() );
    callBackRequestSimulator.waitForRequest();
    assertFalse( callBackRequestSimulator.isRequestRunning() );
  }

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

  public void testExceptionInAsyncExec() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final RuntimeException exception = new RuntimeException( "bad things happen" );
    Runnable runnable = new Runnable() {
      public void run() {
        throw exception;
      }
    };
    display.asyncExec( runnable );
    try {
      display.readAndDispatch();
      String msg
        = "Exception that occurs in an asynExec runnable must be wrapped "
        + "in an SWTException";
      fail( msg );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_FAILED_EXEC, e.code );
      assertSame( exception, e.throwable );
    }
  }

  public void testTimerExec() throws Exception {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Runnable runnable = mock( Runnable.class );
    display.timerExec( TIMER_EXEC_DELAY, runnable );
    Thread.sleep( TIMER_EXEC_DELAY + 50 );

    display.readAndDispatch();

    verify( runnable ).run();
  }

  // Ensure that runnables that were added via timerExec but should be executed
  // in the future are *not* executed on session shutdown
  public void testNoTimerExecAfterSessionShutdown() throws Exception {
    Runnable runnable = mock( Runnable.class );
    display.timerExec( TIMER_EXEC_DELAY, runnable );
    display.dispose();
    Thread.sleep( SLEEP_TIME );
    verifyZeroInteractions( runnable );
  }

  public void testRemoveAddedTimerExec() throws Exception {
    Runnable runnable = mock( Runnable.class );
    display.timerExec( TIMER_EXEC_DELAY, runnable );
    display.timerExec( -1, runnable );
    Thread.sleep( SLEEP_TIME );
    assertFalse( manager.hasRunnables() );
    verifyZeroInteractions( runnable );
  }

  public void testTimerExecActivatesServerPush() {
    display.timerExec( TIMER_EXEC_DELAY, mock( Runnable.class ) );

    assertTrue( ServerPushManager.getInstance().isServerPushActive() );
  }

  public void testDispatchingTimerExecRunnableDeactivatesServerPush() throws Exception {
    display.timerExec( TIMER_EXEC_DELAY, mock( Runnable.class ) );

    Thread.sleep( TIMER_EXEC_DELAY + 50 );

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  // This test ensures that addSync doesn't cause deadlocks
  public void testSyncExecBlock() throws Exception {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final ServiceContext context = ContextProvider.getContext();
    // the code in bgRunnable simulates a bg-thread that calls Display#addSync
    Runnable bgRunnable = new Runnable() {
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
  public void testAddSyncWithExceptionInRunnable() throws Exception {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final ServiceContext context = ContextProvider.getContext();
    final AtomicReference<SWTException> exceptionInBgThread = new AtomicReference<SWTException>();
    // the code in bgRunnable simulates a bg-thread that calls Display#addSync
    // and causes an exception in the runnable
    Runnable bgRunnable = new Runnable() {
      public void run() {
        ContextProvider.setContext( context );
        Fixture.fakeResponseWriter();
        Runnable causeException = new Runnable() {
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

  public void testMustBlockCallBackRequest() {
    assertFalse( manager.mustBlockCallBackRequest() );
  }

  public void testMustBlockCallBackRequestWhenActive() {
    manager.activateServerPushFor( "foo" );
    assertTrue( manager.mustBlockCallBackRequest() );
  }

  public void testMustBlockCallBackRequestWhenActiveAndRunnablesPending() {
    manager.activateServerPushFor( "foo" );
    manager.setHasRunnables( true );
    assertFalse( manager.mustBlockCallBackRequest() );
  }

  public void testMustBlockCallBackRequestWhenDeactivatedAndRunnablesPending() {
    manager.setHasRunnables( true );
    assertFalse( manager.mustBlockCallBackRequest() );
  }

  public void testNeedActivationFromDifferentSession() throws Throwable {
    // test that on/off switching is managed in session scope
    manager.activateServerPushFor( ID_1 );
    final boolean[] otherSession = new boolean[ 1 ];
    Runnable runnable = new Runnable() {
      public void run() {
        Fixture.createServiceContext();
        new Display();
        otherSession[ 0 ] = ServerPushManager.getInstance().needsActivation();
      }
    };
    Fixture.runInThread( runnable );
    assertFalse( otherSession[ 0 ] );
  }

  public void testNeedActivationWithoutActivateCall() {
    boolean needsActivation = manager.needsActivation();
    assertFalse( needsActivation );
  }

  public void testNeedActivationAfterDeactivate() {
    manager.deactivateServerPushFor( ID_1 );
    assertFalse( manager.needsActivation() );
  }

  public void testNeedActivationWithDifferentIds() {
    manager.activateServerPushFor( ID_1 );
    manager.activateServerPushFor( ID_2 );
    assertTrue( manager.needsActivation() );
  }

  public void testNeedActivationAfterActivateTwoDeactivateOne() {
    manager.activateServerPushFor( ID_1 );
    manager.activateServerPushFor( ID_2 );
    manager.deactivateServerPushFor( ID_1 );
    assertTrue( manager.needsActivation() );
  }

  public void testNeedActivateTwice() {
    manager.activateServerPushFor( ID_1 );
    manager.deactivateServerPushFor( ID_1 );
    manager.activateServerPushFor( ID_2 );
    assertTrue( manager.needsActivation() );
  }

  public void testNeedActivationWithActivateDeactivateAndPendingRunnables() {
    manager.activateServerPushFor( ID_1 );
    display.asyncExec( EMPTY_RUNNABLE );
    manager.deactivateServerPushFor( ID_1 );
    assertTrue( manager.needsActivation() );
  }

  public void testNeedActivationWithPendingRunnablesDoesntEnableServerPush() {
    display.asyncExec( EMPTY_RUNNABLE );
    assertFalse( manager.needsActivation() );
  }

  public void testIsSessionExpiredWithInfiniteSessionTimeout() {
    ContextProvider.getUISession().getHttpSession().setMaxInactiveInterval( -1 );

    boolean sessionExpired = ServerPushManager.isSessionExpired( 1, 2 );

    assertFalse( sessionExpired );
  }

  public void testIsSessionExpiredWhenSessionTimedOut() {
    ContextProvider.getUISession().getHttpSession().setMaxInactiveInterval( 10 );

    boolean sessionExpired = ServerPushManager.isSessionExpired( 1, 20000 );

    assertTrue( sessionExpired );
  }

  public void testIsSessionExpiredWhenSessionActive() {
    ContextProvider.getUISession().getHttpSession().setMaxInactiveInterval( 10 );

    boolean sessionExpired = ServerPushManager.isSessionExpired( 1, 9000 );

    assertFalse( sessionExpired );
  }

  public void testSetHasRunnablesWithoutStateInfo() {
    // Service handlers don't have a state info
    manager.activateServerPushFor( "foo" );
    Fixture.replaceServiceStore( null );

    try {
      manager.setHasRunnables( true );
    } catch( NullPointerException notExpected ) {
      fail();
    }
  }

  private void simulateBackgroundAddition( final ServiceContext serviceContext ) throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        ContextProvider.setContext( serviceContext );
        display.asyncExec( new AsyncExecRunnable() );
        display.asyncExec( new AsyncExecRunnable() );
      }
    };
    Fixture.runInThread( runnable );
  }

  private void simulateAsyncExecDuringLifeCycle() {
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        Runnable target = new Runnable() {
          public void run() {
            display.asyncExec( new AsyncExecRunnable() );
          }
        };
        try {
          Fixture.runInThread( target );
        } catch( Throwable e ) {
          e.printStackTrace();
        }
      }
    } );
  }

  private void callDisplayWake() throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        display.wake();
      }
    };
    Fixture.runInThread( runnable );
  }

  private void fakeNewRequest() {
    Fixture.fakeNewRequest( display );
    ContextProvider.getUISession().setAttribute( "org.eclipse.swt.display", display );
  }

  private static ServiceContext createServiceContext( TestResponse response ) {
    HttpSession httpSession = ContextProvider.getContext().getUISession().getHttpSession();
    TestRequest request = new TestRequest();
    request.setSession( httpSession );
    ServiceContext result = new ServiceContext( request, response );
    result.setServiceStore( new ServiceStore() );
    return result;
  }

  private class AsyncExecRunnable implements Runnable {
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
        public void run() {
          ContextProvider.setContext( serviceContext );
          Fixture.fakeResponseWriter();
          try {
            manager.activateServerPushFor( "foo" );
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
      requestThread.join();
    }

    boolean isRequestRunning() {
      return requestThread.isAlive();
    }

    boolean exceptionOccured() {
      return exception != null;
    }
  }
}
