/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public class UICallBackManager_Test extends TestCase {

  private static final int SLEEP_TIME = 200;
  private static final int TIMER_EXEC_DELAY = 1000;
  private static final String RUN_ASYNC_EXEC = "run async exec|";
  private static final String RUN_TIMER_EXEC = "timerExecCode|";
  private static final Runnable EMPTY_RUNNABLE = new Runnable() {
    public void run() {
    }
  };
  private static String log = "";

  private Display display;

  protected void setUp() throws Exception {
    Fixture.setUp();
    log = "";
    display  = new Display();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testWaitFor() throws InterruptedException {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    final ServiceContext context[] = { ContextProvider.getContext() };
    Thread thread = new Thread( new Runnable() {
      public void run() {
        ContextProvider.setContext( context[ 0 ] );
        Fixture.fakeResponseWriter();
        UICallBackServiceHandler uiCallBackServiceHandler
          = new UICallBackServiceHandler();
        try {
          UICallBackManager.getInstance().setActive( true );
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
    assertTrue( UICallBackManager.getInstance().isCallBackRequestBlocked() );

    UICallBackManager.getInstance().sendUICallBack();
    Thread.sleep( SLEEP_TIME );
    assertFalse( UICallBackManager.getInstance().isCallBackRequestBlocked() );
    Thread.sleep( SLEEP_TIME );
    assertFalse( thread.isAlive() );
  }
  
  public void testWaitOnUIThread() throws Exception {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    display.wake();
    assertTrue( UICallBackManager.getInstance().isCallBackRequestBlocked() );
    UICallBackManager.getInstance().sendImmediately();
  }
  
  public void testWaitOnBackgroundThread() throws Exception {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertTrue( UICallBackManager.getInstance().isCallBackRequestBlocked() );
    Thread thread = new Thread( new Runnable() {
      public void run() {
        display.wake();
      }
    } );
    thread.start();
    thread.join();
    Thread.sleep( SLEEP_TIME );
    assertFalse( UICallBackManager.getInstance().isCallBackRequestBlocked() );
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
    assertFalse( UICallBackManager.getInstance().isCallBackRequestBlocked() );
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
    assertFalse( UICallBackManager.getInstance().isCallBackRequestBlocked() );
    assertEquals( RUN_ASYNC_EXEC + RUN_ASYNC_EXEC + RUN_ASYNC_EXEC, log );
  }
  
  public void testCallBackRequestBlocking() throws Exception {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertTrue( UICallBackManager.getInstance().isCallBackRequestBlocked() );
  }

  public void testCallBackRequestReleasing() throws Exception {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    Thread uiCallBackThread
      = simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    simulateBackgroundAddition( context );
    assertFalse( UICallBackManager.getInstance().isCallBackRequestBlocked() );
    assertFalse( uiCallBackThread.isAlive() );
    assertEquals( "", log );
  }
  
  public void testAsyncExec() throws InterruptedException {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    ServiceContext context = ContextProvider.getContext();
    // test runnables addition while no uiCallBack thread is not blocked
    Thread uiCallBackThread
      = simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    UICallBackManager.getInstance().notifyUIThreadEnd();
    simulateBackgroundAddition( context );
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertFalse( UICallBackManager.getInstance().isCallBackRequestBlocked() );
    // since no UI thread is running and
    // runnables available do not block
    assertFalse( uiCallBackThread.isAlive() );
    UICallBackManager.getInstance().notifyUIThreadStart();

    // test blocking of incomming uiCallBack thread while UI thread is running
    fakeRequestParam( display );
    simulateUICallBackThreadLockDuringLifeCycle(
      context,
      uiCallBackServiceHandlerThrowable );
    Fixture.executeLifeCycleFromServerThread();
    if( uiCallBackServiceHandlerThrowable[ 0 ] != null ) {
      uiCallBackServiceHandlerThrowable[ 0 ].printStackTrace();
    }
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertTrue( UICallBackManager.getInstance().isCallBackRequestBlocked() );
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
          display.timerExec( 5000, EMPTY_RUNNABLE );
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
    display.timerExec( 200, runnable );
    display.dispose();
    Thread.sleep( 200 );
    assertEquals( "", log.toString() );
  }

  public void testRemoveAddedTimerExec() throws Exception {
    Runnable runnable = new Runnable() {
      public void run() {
        log += RUN_TIMER_EXEC;
      }
    };
    display.timerExec( 200, runnable );
    display.timerExec( -1, runnable );
    Thread.sleep( 2000 );
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
        Runnable doNothing = new Runnable() {
          public void run() {
          }
        };
        display.syncExec( doNothing );
      }
    };
    // simulate a lot "parallel" bg-threads to provoke multi-threading problems
    List bgThreads = new ArrayList();
    for( int i = 0; i < 200; i++ ) {
      Thread bgThread = new Thread( bgRunnable, "Test-Bg-Thread " + i );
      bgThread.setDaemon( true );
      bgThread.start();
      display.readAndDispatch();
      bgThreads.add( bgThread );
    }
    // wait (hopefully long enough) until all bg-threads have done their work
    // (i.e. called addSync) and make sure all sync-runnables get executed
    Thread.sleep( 20 );
    while( display.readAndDispatch() ) {
      Thread.sleep( 20 );
    }
    // wait for all bgThreads to terminate
    for( int i = 0; i < bgThreads.size(); i++ ) {
      Thread bgThread = ( Thread )bgThreads.get( i );
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
  
  private Thread simulateUiCallBackThread(
    final Throwable[] uiCallBackServiceHandlerThrowable,
    final ServiceContext context )
    throws InterruptedException
  {
    Thread uiCallBackThread = new Thread( new Runnable() {
      public void run() {
        ContextProvider.setContext( context );
        Fixture.fakeResponseWriter();
        TestResponse response = ( TestResponse )context.getResponse();
        response.setOutputStream( new TestServletOutputStream() );
        UICallBackServiceHandler uiCallBackServiceHandler = new UICallBackServiceHandler();
        try {
          UICallBackManager.getInstance().setActive( true );
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

  private static void simulateUICallBackThreadLockDuringLifeCycle(
    final ServiceContext context,
    final Throwable[] uiCallBackServiceHandlerThrowable )
  {
    final RWTLifeCycle lifeCycle
      = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;

      public void afterPhase( final PhaseEvent event ) {
        Thread uiCallBackThread = new Thread( new Runnable() {
          public void run() {
            ContextProvider.setContext( context );
            Fixture.fakeResponseWriter();
            UICallBackServiceHandler uiCallBackServiceHandler
              = new UICallBackServiceHandler();
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
}
