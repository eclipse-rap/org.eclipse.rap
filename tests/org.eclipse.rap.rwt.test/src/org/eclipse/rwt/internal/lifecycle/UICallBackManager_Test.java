/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.Fixture.TestResponse;
import org.eclipse.rwt.Fixture.TestServletOutputStream;
import org.eclipse.rwt.internal.lifecycle.UICallBackManager.SyncRunnable;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.Display;


public class UICallBackManager_Test extends TestCase {

  private static final int SLEEP_TIME = 200;
  private static final int TIMER_EXEC_DELAY = 1000;
  private static final String RUN_ASYNC_EXEC = "run async exec|";
  private static final String RUN_SYNC_EXEC = "run sync exec|";
  private static final String RUN_TIMER_EXEC = "timerExecCode|";
  private static String log = "";

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    log = "";
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
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

  public void testSyncRunnableWrapper() throws InterruptedException {
    final SyncRunnable[] syncRunnable = new SyncRunnable[ 1 ];
    Thread backgroundThread = new Thread( new Runnable() {
      public void run() {
        syncRunnable[ 0 ] = new SyncRunnable( new Runnable() {
          public void run() {
            log += RUN_SYNC_EXEC;
          }
        } );
        syncRunnable[ 0 ].block();
      }
    } );
    backgroundThread.start();
    Thread.sleep( SLEEP_TIME );
    assertTrue( backgroundThread.isAlive() );
    syncRunnable[ 0 ].run();
    Thread.sleep( SLEEP_TIME );
    assertFalse( backgroundThread.isAlive() );
    assertEquals( RUN_SYNC_EXEC, log );
  }

  public void testAddAsync() throws InterruptedException {
    final Throwable[] uiCallBackServiceHandlerThrowable = { null };
    final ServiceContext context[] = { ContextProvider.getContext() };
    Display display = new Display();

    // test initial blocking of uiCallBack thread
    Thread uiCallBackThread
      = simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    if( uiCallBackServiceHandlerThrowable[ 0 ] != null ) {
      uiCallBackServiceHandlerThrowable[ 0 ].printStackTrace();
    }
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertTrue( UICallBackManager.getInstance().isCallBackRequestBlocked() );

    // test unblocking in case of background addition of runnables
    simulateBackgroundAddition( context, display );
    assertFalse( UICallBackManager.getInstance().isCallBackRequestBlocked() );
    assertFalse( uiCallBackThread.isAlive() );
    assertEquals( "", log );

    // test runnables execution during lifecycle with interlocked additions
    fakeRequestParam( display );
    simulateBackgroundAdditionDuringLifeCycle( context, display );
    RWTFixture.executeLifeCycleFromServerThread();
    assertFalse( UICallBackManager.getInstance().isCallBackRequestBlocked() );
    assertEquals( RUN_ASYNC_EXEC + RUN_ASYNC_EXEC + RUN_ASYNC_EXEC, log );

    // test runnables addition while no uiCallBack thread is not blocked
    UICallBackManager.getInstance().notifyUIThreadEnd();
    log = "";
    simulateBackgroundAddition( context, display );
    uiCallBackThread
      = simulateUiCallBackThread( uiCallBackServiceHandlerThrowable, context );
    if( uiCallBackServiceHandlerThrowable[ 0 ] != null ) {
      uiCallBackServiceHandlerThrowable[ 0 ].printStackTrace();
    }
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
    RWTFixture.executeLifeCycleFromServerThread();
    if( uiCallBackServiceHandlerThrowable[ 0 ] != null ) {
      uiCallBackServiceHandlerThrowable[ 0 ].printStackTrace();
    }
    assertNull( uiCallBackServiceHandlerThrowable[ 0 ] );
    assertTrue( UICallBackManager.getInstance().isCallBackRequestBlocked() );
    assertEquals( RUN_ASYNC_EXEC + RUN_ASYNC_EXEC, log );
  }

  public void testExceptionInAsyncExec() {
    final RuntimeException exception
      = new RuntimeException( "bad things happen" );
    Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        throw exception;
      }
    };
    UICallBackManager.getInstance().addAsync( display, runnable );
    try {
      UICallBackManager.getInstance().processNextRunnableInUIThread();
      String msg
        = "Exception that occurs in an asynExec runnable must be wrapped "
        + "in an SWTException";
      fail( msg );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_FAILED_EXEC, e.code );
      assertSame( exception, e.throwable );
    }
  }

  // Calling addAsync with a null-runnable must still cause the UI callback
  // to be triggered
  public void testAddAsyncWithNullRunnable() throws InterruptedException {
    final Display display = new Display();
    final Runnable addAsyncRunnable = new Runnable() {
      public void run() {
        UICallBackManager.getInstance().addAsync( display, null );
      }
    };
    Runnable threadRunnable = new Runnable() {
      public void run() {
        UICallBack.runNonUIThreadWithFakeContext( display, addAsyncRunnable );
      }
    };
    Thread thread = new Thread( threadRunnable );
    thread.start();
    thread.join();
    assertEquals( 1, UICallBackManager.getInstance().runnables.size() );
    // 'Execute' the null-runnable: must not cause exception
    UICallBackManager.getInstance().processNextRunnableInUIThread();
  }

  public void testAddTimer() throws Exception {
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        log += RUN_TIMER_EXEC;
      }
    };
    long time = System.currentTimeMillis() + TIMER_EXEC_DELAY;
    simulateTimerExecAddition( display, runnable, time );
    UICallBackManager callbackManager = UICallBackManager.getInstance();
    assertFalse( callbackManager.processNextRunnableInUIThread() );
    assertEquals( "", log.toString() );
    Thread.sleep( TIMER_EXEC_DELAY + 50 );
    assertTrue( callbackManager.processNextRunnableInUIThread() );
    assertEquals( RUN_TIMER_EXEC, log.toString() );
  }

  // Ensure that runnables that were added via addTimer but should be executed
  // in the future are *not* executed on session shutdown
  public void testShutdown() throws Exception {
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        log += RUN_TIMER_EXEC;
      }
    };
    long timeInTheFarFuture = System.currentTimeMillis() + 10000;
    simulateTimerExecAddition( display, runnable, timeInTheFarFuture );
    UICallBackManager callbackManager = UICallBackManager.getInstance();
    callbackManager.beforeDestroy( null );
    assertEquals( "", log.toString() );
  }

  public void testRemoveAddedTimer() throws Exception {
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        log += RUN_TIMER_EXEC;
      }
    };
    long timeInTheFarFuture = System.currentTimeMillis() + 10000;
    simulateTimerExecAddition( display, runnable, timeInTheFarFuture );
    UICallBackManager callbackManager = UICallBackManager.getInstance();
    callbackManager.addTimer( display, runnable, -1 );
    assertEquals( 0, callbackManager.runnables.size() );
    assertEquals( "", log );
  }

  // This test ensures that addSync doesn't cause deadlocks
  public void testAddSyncBlock() throws Exception {
    final Display display = new Display();
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
        UICallBackManager.getInstance().addSync( display, doNothing );
      }
    };
    // simulate a lot "parallel" bg-threads to provoke multi-threading problems
    List bgThreads = new ArrayList();
    for( int i = 0; i < 200; i++ ) {
      Thread bgThread = new Thread( bgRunnable, "Test-Bg-Thread " + i );
      bgThread.setDaemon( true );
      bgThread.start();
      UICallBackManager.getInstance().processNextRunnableInUIThread();
      bgThreads.add( bgThread );
    }
    // wait (hopefully long enough) until all bg-threads have done their work
    // (i.e. called addSync) and make sure all sync-runnables get executed
    Thread.sleep( 20 );
    while( UICallBackManager.getInstance().processNextRunnableInUIThread() ) {
      Thread.sleep( 20 );
    }
    // wait for all bgThreads to terminate
    for( int i = 0; i < bgThreads.size(); i++ ) {
      Thread bgThread = ( Thread )bgThreads.get( i );
      bgThread.join();
      UICallBackManager.getInstance().processNextRunnableInUIThread();
    }
    // sanity-check the test itself: all runnables must have been executed 
    assertTrue( UICallBackManager.getInstance().runnables.isEmpty() );
  }
  
  // This test ensures that SyncRunnable releases the blocked thread in case of 
  // an exception
  public void testAddSyncWithExceptionInRunnable() throws Exception {
    final Display display = new Display();
    final ServiceContext context = ContextProvider.getContext();
    // the code in bgRunnable simulates a bg-thread that calls Display#addSync
    // and causes an exception in the runnable
    Runnable bgRunnable = new Runnable() {
      public void run() {
        ContextProvider.setContext( context );
        Fixture.fakeResponseWriter();
        Runnable causeException = new Runnable() {
          public void run() {
            throw new RuntimeException();
          }
        };
        UICallBackManager.getInstance().addSync( display, causeException );
      }
    };
    
    Thread bgThread = new Thread( bgRunnable );
    bgThread.setDaemon( true );
    bgThread.start();
    Thread.sleep( SLEEP_TIME );
    try {
      UICallBackManager.getInstance().processNextRunnableInUIThread();
      fail( "Exception from causeException-runnable must end up here" );
    } catch( SWTException e ) {
      // expected
    }
    Thread.sleep( SLEEP_TIME );
    assertFalse( bgThread.isAlive() );
  }

  private static Thread simulateUiCallBackThread(
    final Throwable[] uiCallBackServiceHandlerThrowable,
    final ServiceContext[] context )
    throws InterruptedException
  {
    Thread uiCallBackThread = new Thread( new Runnable() {
      public void run() {
        ContextProvider.setContext( context[ 0 ] );
        Fixture.fakeResponseWriter();
        TestResponse response = ( TestResponse )context[ 0 ].getResponse();
        response.setOutputStream( new TestServletOutputStream() );
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
    uiCallBackThread.start();
    Thread.sleep( SLEEP_TIME );
    return uiCallBackThread;
  }

  private static void simulateTimerExecAddition( final Display display,
                                                 final Runnable runnable,
                                                 final long time )
    throws InterruptedException
  {
    final Runnable simulateRunnable = new Runnable() {
      public void run() {
        UICallBackManager.getInstance().addTimer( display, runnable, time );
      }
    };
    Runnable threadRunnable = new Runnable() {
      public void run() {
        UICallBack.runNonUIThreadWithFakeContext( display, simulateRunnable );
      }
    };
    Thread thread = new Thread( threadRunnable );
    thread.start();
    thread.join();
  }

  private static void simulateBackgroundAddition(
    final ServiceContext[] context,
    final Display display )
    throws InterruptedException
  {
    Thread backgroundThread = new Thread( new Runnable() {
      public void run() {
        ContextProvider.setContext( context[ 0 ] );
        UICallBackManager instance = UICallBackManager.getInstance();
        instance.addAsync( display, new Runnable() {
          public void run() {
            log += RUN_ASYNC_EXEC;
          }
        } );
        instance.addAsync( display, new Runnable() {
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
    final ServiceContext[] context,
    final Display display )
  {
    final RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void afterPhase( final PhaseEvent event ) {
        Thread thread = new Thread( new Runnable() {
          public void run() {
            ContextProvider.setContext( context[ 0 ] );
            UICallBackManager.getInstance().addAsync( display, new Runnable() {
              public void run() {
                log += RUN_ASYNC_EXEC;
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
        lifeCycle.removePhaseListener( this );
      }
      public void beforePhase( final PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
    } );
  }

  private static void simulateUICallBackThreadLockDuringLifeCycle(
    final ServiceContext[] context,
    final Throwable[] uiCallBackServiceHandlerThrowable )
  {
    final RWTLifeCycle lifeCycle
      = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;

      public void afterPhase( final PhaseEvent event ) {
        Thread uiCallBackThread = new Thread( new Runnable() {
          public void run() {
            ContextProvider.setContext( context[ 0 ] );
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
}
