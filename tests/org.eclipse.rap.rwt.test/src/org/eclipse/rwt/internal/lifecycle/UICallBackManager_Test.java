/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public class UICallBackManager_Test extends TestCase {
  public static final String SYS_PROP_SLEEP_TIME = "sleepTime";
  public static final String SYS_PROP_TIMER_EXEC_DELAY = "timerExecDelay";
 
  private static final int SLEEP_TIME;
  private static final int TIMER_EXEC_DELAY;

  private static final String ID_1 = "id_1";
  private static final String ID_2 = "id_2";
  private static final String RUN_ASYNC_EXEC = "run async exec|";
  private static final String RUN_TIMER_EXEC = "timerExecCode|";
  private static final Runnable EMPTY_RUNNABLE = new NoOpRunnable();
  
  static {
    String sleepTimeProp = System.getProperty( SYS_PROP_SLEEP_TIME );
    SLEEP_TIME = sleepTimeProp == null ? 200 : Integer.parseInt( sleepTimeProp );
    String timerExecDelayProp = System.getProperty( SYS_PROP_TIMER_EXEC_DELAY );
    TIMER_EXEC_DELAY = timerExecDelayProp == null ? 5000 :Integer.parseInt( timerExecDelayProp );
  }
  
  
  private static String log = "";

  private Display display;
  private UICallBackManager manager;
  private UICallBackServiceHandler uiCallBackServiceHandler;

  protected void setUp() throws Exception {
    Fixture.setUp();
    log = "";
    display  = new Display();
    manager = UICallBackManager.getInstance();
    uiCallBackServiceHandler = new UICallBackServiceHandler();
  }

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
        UICallBackServiceHandler uiCallBackServiceHandler = new UICallBackServiceHandler();
        try {
          manager.activateUICallBacksFor( "foo" );
          uiCallBackServiceHandler.service();
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

    manager.wakeClient();
    thread.join();
    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( thread.isAlive() );
  }
  
  public void testWaitOnUIThread() throws Exception {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    display.wake();
    assertTrue( manager.isCallBackRequestBlocked() );
    manager.releaseBlockedRequest();
  }
  
  public void testWaitOnBackgroundThread() throws Exception {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertTrue( manager.isCallBackRequestBlocked() );
    Thread thread = new Thread( new Runnable() {
      public void run() {
        display.wake();
      }
    } );
    thread.start();
    thread.join();
    assertFalse( manager.isCallBackRequestBlocked() );
  }

  // same test as above, but while UIThread running
  public void testWaitOnBackgroundThread_DuringLifecycle() throws Exception {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertTrue( manager.isCallBackRequestBlocked() );
    Thread thread = new Thread( new Runnable() {
      public void run() {
        display.wake();
      }
    } );
    // assume that UIThread is currently running the life cycle
    manager.notifyUIThreadStart();
    thread.start();
    thread.join();
    Thread.sleep( SLEEP_TIME );
    manager.notifyUIThreadEnd();
    assertFalse( manager.isCallBackRequestBlocked() );
  }

  public void testAsyncExecWhileLifeCycleIsRunning() {
    fakeRequestParam( display );
    Fixture.fakePhase( PhaseId.READ_DATA );
    simulateBackgroundAdditionDuringLifeCycle( display );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    assertEquals( 1, getDisplayAdapter().getAsyncRunnablesCount() );    
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( RUN_ASYNC_EXEC, log );
    assertFalse( manager.isCallBackRequestBlocked() );
  }
  
  public void testAsyncExecWithBackgroundAndLifeCycleRunnables() 
    throws Exception 
  {
    // test unblocking in case of background addition of runnables
    simulateBackgroundAddition( ContextProvider.getContext() );
    // test runnables execution during lifecycle with interlocked additions
    fakeRequestParam( display );
    Fixture.fakePhase( PhaseId.READ_DATA );
    simulateBackgroundAdditionDuringLifeCycle( display );
    Fixture.executeLifeCycleFromServerThread();
    assertFalse( manager.isCallBackRequestBlocked() );
    assertEquals( RUN_ASYNC_EXEC + RUN_ASYNC_EXEC + RUN_ASYNC_EXEC, log );
  }
  
  public void testCallBackRequestBlocking() throws Exception {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertTrue( manager.isCallBackRequestBlocked() );
  }

  public void testCallBackRequestReleasing() throws Exception {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    Thread uiCallBackThread
      = simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    simulateBackgroundAddition( context );
    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( uiCallBackThread.isAlive() );
    assertEquals( "", log );
  }
  
  public void testCallBackRequestIsReleasedOnSessionInvalidate() throws Exception {
    Throwable[] uiCallBackHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    Thread uiCallBackThread = simulateUiCallBackThread( uiCallBackHandlerThrowable, context );
    
    context.getSessionStore().getHttpSession().invalidate();
    uiCallBackThread.join();
    
    TestResponse response = ( TestResponse )context.getResponse();
    assertEquals( "", response.getContent().trim() );
    assertFalse( manager.isCallBackRequestBlocked() );
    assertFalse( uiCallBackThread.isAlive() );
    assertNull( uiCallBackHandlerThrowable[ 0 ] );
  }
  
  public void testMultipleCallBackRequests() throws Exception {
    manager.setRequestCheckInterval( 20 );
    ServiceContext context1 = ContextProvider.getContext();
    Throwable[] uiCallBackHandlerThrowable1 = { null };
    Thread uiCallBackThread1 = simulateUiCallBackThread( uiCallBackHandlerThrowable1, context1 );
    ServiceContext context2 = createServiceContext( context1.getSessionStore().getHttpSession() );
    Throwable[] uiCallBackHandlerThrowable2 = { null };
    Thread uiCallBackThread2 = simulateUiCallBackThread( uiCallBackHandlerThrowable2, context2 );
    
    Thread.sleep( SLEEP_TIME );
    
    assertTrue( manager.isCallBackRequestBlocked() );
    assertNull( uiCallBackHandlerThrowable1[ 0 ] );
    assertNull( uiCallBackHandlerThrowable2[ 0 ] );
    assertTrue( uiCallBackThread1.isAlive() );
    assertFalse( uiCallBackThread2.isAlive() );
    TestResponse response = ( TestResponse )context2.getResponse();
    assertEquals( HttpServletResponse.SC_CONFLICT, response.getErrorStatus() );
  }
  
  
  public void testAsyncExec() throws InterruptedException {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    // test runnables addition while no uiCallBack thread is not blocked
    Thread uiCallBackThread
      = simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    manager.notifyUIThreadEnd();
    simulateBackgroundAddition( context );
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertFalse( manager.isCallBackRequestBlocked() );
    // since no UI thread is running and
    // runnables available do not block
    assertFalse( uiCallBackThread.isAlive() );
    manager.notifyUIThreadStart();

    // test blocking of incomming uiCallBack thread while UI thread is running
    fakeRequestParam( display );
    simulateUICallBackThreadLockDuringLifeCycle( context, uiCallBackServiceHandlerThrowable );
    Fixture.executeLifeCycleFromServerThread();
    if( uiCallBackServiceHandlerThrowable[ 0 ] != null ) {
      uiCallBackServiceHandlerThrowable[ 0 ].printStackTrace();
    }
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertTrue( manager.isCallBackRequestBlocked() );
    assertEquals( RUN_ASYNC_EXEC + RUN_ASYNC_EXEC, log );
  }

  public void testExceptionInAsyncExec() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final RuntimeException exception
      = new RuntimeException( "bad things happen" );
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
    Runnable runnable = new Runnable() {
      public void run() {
        log += RUN_TIMER_EXEC;
      }
    };
    display.timerExec( TIMER_EXEC_DELAY, runnable );
    assertFalse( display.readAndDispatch() );
    assertEquals( "", log.toString() );
    Thread.sleep( TIMER_EXEC_DELAY + 50 );
    display.readAndDispatch();
    assertEquals( RUN_TIMER_EXEC, log.toString() );
  }
  
  public void testTimerExecWithIllegalArgument() {
    try {
      display.timerExec( 1, null );
      fail( "timerExec: runnable must not be null" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testTimerExecFromBackgroundThread() throws Exception {
    final Throwable[] exceptionInTimerExec = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          display.timerExec( TIMER_EXEC_DELAY, EMPTY_RUNNABLE );
        } catch( Throwable t ) {
          exceptionInTimerExec[ 0 ] = t;
        }
      }
    };
    Thread thread = new Thread( runnable );
    thread.setDaemon( true );
    thread.start();
    thread.join();
    assertNotNull( exceptionInTimerExec[ 0 ] );
    assertTrue( exceptionInTimerExec[ 0 ] instanceof SWTException );
    SWTException swtException = ( SWTException )exceptionInTimerExec[ 0 ];
    assertEquals( swtException.code, SWT.ERROR_THREAD_INVALID_ACCESS );
  }

  // Ensure that runnables that were added via addTimer but should be executed
  // in the future are *not* executed on session shutdown
  public void testNoTimerExecAfterSessionShutdown() throws Exception {
    Runnable runnable = new Runnable() {
      public void run() {
        log += RUN_TIMER_EXEC;
      }
    };
    display.timerExec( TIMER_EXEC_DELAY, runnable );
    display.dispose();
    Thread.sleep( SLEEP_TIME );
    assertEquals( "", log.toString() );
  }

  public void testRemoveAddedTimerExec() throws Exception {
    Runnable runnable = new Runnable() {
      public void run() {
        log += RUN_TIMER_EXEC;
      }
    };
    display.timerExec( TIMER_EXEC_DELAY, runnable );
    display.timerExec( -1, runnable );
    Thread.sleep( SLEEP_TIME );
    assertEquals( 0, getDisplayAdapter().getAsyncRunnablesCount() );
    assertEquals( "", log );
  }
  
  public void testRemoveNonExistingTimerExec() {
    display.timerExec( -1, EMPTY_RUNNABLE );
    // must not cause any exception
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
        Runnable doNothing = new NoOpRunnable();
        display.syncExec( doNothing );
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
    IDisplayAdapter adapter = getDisplayAdapter();
    assertEquals( 0, adapter.getAsyncRunnablesCount() );
  }
  
  // This test ensures that SyncRunnable releases the blocked thread in case of 
  // an exception
  public void testAddSyncWithExceptionInRunnable() throws Exception {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final ServiceContext context = ContextProvider.getContext();
    final SWTException[] exceptionInBgThread = { null };
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
          exceptionInBgThread[ 0 ] = e;
        }
      }
    };
    
    Thread bgThread = new Thread( bgRunnable );
    bgThread.setDaemon( true );
    bgThread.start();
    Thread.sleep( SLEEP_TIME );
    try {
      display.readAndDispatch();
      fail( "Exception from causeException-runnable must end up here" );
    } catch( SWTException e ) {
      // expected
    }
    Thread.sleep( SLEEP_TIME );
    assertNotNull( exceptionInBgThread[ 0 ] );
    assertFalse( bgThread.isAlive() );
  }

  public void testMustBlock() {
    assertFalse( manager.mustBlockCallBackRequest() );
  }

  public void testMustBlockWhenActive() {
    manager.activateUICallBacksFor( "foo" );
    assertTrue( manager.mustBlockCallBackRequest() );
  }
  
  public void testNeedActivationFromDifferentSession() throws Throwable {
    // test that on/off switching is managed in session scope
    manager.activateUICallBacksFor( ID_1 );
    final boolean[] otherSession = new boolean[ 1 ];
    Runnable runnable = new Runnable() {
      public void run() {
        Fixture.createServiceContext();
        new Display();
        otherSession[ 0 ] = UICallBackManager.getInstance().needsActivation();
      } 
    };
    Fixture.runInThread( runnable );
    assertFalse( otherSession[ 0 ] );
  }
  
  public void testNeedActivationWithoutActivateCall() throws Exception {
    boolean needsActivation = manager.needsActivation();
    assertFalse( needsActivation );
  }
  
  public void testNeedActivationAfterDeactivate() throws Exception {
    manager.deactivateUICallBacksFor( ID_1 );
    assertFalse( manager.needsActivation() );
  }
  
  public void testNeedActivationWithDifferentIds() throws Exception {
    manager.activateUICallBacksFor( ID_1 );
    manager.activateUICallBacksFor( ID_2 );
    assertTrue( manager.needsActivation() );
  }
  
  public void testNeedActivationAfterActivateTwoDeactivateOne() throws Exception {
    manager.activateUICallBacksFor( ID_1 );
    manager.activateUICallBacksFor( ID_2 );
    manager.deactivateUICallBacksFor( ID_1 );
    assertTrue( manager.needsActivation() );
  }
  
  public void testNeedActivateTwice() throws Exception {
    manager.activateUICallBacksFor( ID_1 );
    manager.deactivateUICallBacksFor( ID_1 );
    manager.activateUICallBacksFor( ID_2 );
    assertTrue( manager.needsActivation() );
  }

  public void testNeedActivationWithActivateDeactivateAndPendingRunnables() throws Exception {
    manager.activateUICallBacksFor( ID_1 );
    display.asyncExec( EMPTY_RUNNABLE );
    manager.deactivateUICallBacksFor( ID_1 );
    assertTrue( manager.needsActivation() );
  }

  public void testNeedActivationWithPendingRunnablesDoesntEnableUICallback() throws Exception {
    display.asyncExec( EMPTY_RUNNABLE );
    assertFalse( manager.needsActivation() );
  }
  
  private Thread simulateUiCallBackThread(
    final Throwable[] uiCallBackServiceHandlerThrowable,
    final ServiceContext context )
    throws InterruptedException
  {
    Thread uiCallBackThread = new Thread( new Runnable() {
      public void run() {
        ContextProvider.setContext( context );
        Fixture.fakeResponseWriter();
        try {
          manager.activateUICallBacksFor( "foo" );
          uiCallBackServiceHandler.service();
        } catch( Throwable thr ) {
          uiCallBackServiceHandlerThrowable[ 0 ] = thr;
        }
      }
    } );
    uiCallBackThread.start();
    Thread.sleep( SLEEP_TIME );
    return uiCallBackThread;
  }

  private void simulateBackgroundAddition( final ServiceContext context )
    throws InterruptedException
  {
    Thread backgroundThread = new Thread( new Runnable() {
      public void run() {
        ContextProvider.setContext( context );
        display.asyncExec( new Runnable() {
          public void run() {
            log += RUN_ASYNC_EXEC;
          }
        } );
        display.asyncExec( new Runnable() {
          public void run() {
            log += RUN_ASYNC_EXEC;
          }
        } );
      }
    } );
    backgroundThread.start();
    Thread.sleep( SLEEP_TIME );
  }

  private static void simulateBackgroundAdditionDuringLifeCycle(
    final Display display )
  {
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        Thread thread = new Thread( new Runnable() {
          public void run() {
            UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
              public void run() {
                display.asyncExec( new Runnable() {
                  public void run() {
                    log += RUN_ASYNC_EXEC;
                  }
                } );
              }
            } );
          }
        } );
        thread.start();
        try {
          thread.join();
        } catch( InterruptedException e ) {
          e.printStackTrace();
        }
      }
    } );
  }

  private void simulateUICallBackThreadLockDuringLifeCycle(
    final ServiceContext context,
    final Throwable[] uiCallBackServiceHandlerThrowable )
  {
    final ILifeCycle lifeCycle = RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void afterPhase( final PhaseEvent event ) {
        Thread uiCallBackThread = new Thread( new Runnable() {
          public void run() {
            ContextProvider.setContext( context );
            Fixture.fakeResponseWriter();
            try {
              uiCallBackServiceHandler.service();
            } catch( Throwable thr ) {
              uiCallBackServiceHandlerThrowable[ 0 ] = thr;
            }
          }
        } );
        uiCallBackThread.start();
        try {
          Thread.sleep( SLEEP_TIME );
        } catch( InterruptedException e ) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        lifeCycle.removePhaseListener( this );
      }

      public void beforePhase( final PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.READ_DATA;
      }
    } );
  }

  private static void fakeRequestParam( final Display display ) {
    Fixture.fakeResponseWriter();
    String id = "org.eclipse.swt.display";
    ContextProvider.getSession().setAttribute( id, display );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
  }

  private IDisplayAdapter getDisplayAdapter() {
    return ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
  }

  private static ServiceContext createServiceContext( HttpSession session ) throws IOException {
    TestRequest request = new TestRequest();
    TestResponse response = new TestResponse();
    request.setSession( session );
    ServiceContext result = new ServiceContext( request, response );
    ServiceStateInfo stateInfo = new ServiceStateInfo();
    result.setStateInfo( stateInfo );
    stateInfo.setResponseWriter( new JavaScriptResponseWriter( response ) );
    return result;
  }
}
