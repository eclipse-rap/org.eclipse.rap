/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

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
  private static final String RUN_ASYNC_EXEC = "run async exec|";
  private static final String RUN_SYNC_EXEC = "run sync exec|";
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
    UICallBackManager.getInstance().addAsync( runnable, display );
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

  private Thread simulateUiCallBackThread( 
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

  private void simulateBackgroundAddition( final ServiceContext[] context, 
                                           final Display display )
    throws InterruptedException
  {
    Thread backgroundThread = new Thread( new Runnable() {
      public void run() {        
        ContextProvider.setContext( context[ 0 ] );
        UICallBackManager instance = UICallBackManager.getInstance();
        instance.addAsync( new Runnable() {
          public void run() {
            log += RUN_ASYNC_EXEC;
          }
        }, display );
        instance.addAsync( new Runnable() {
          public void run() {
            log += RUN_ASYNC_EXEC;
          }
        }, display );
      }
    } );
    backgroundThread.start();
    Thread.sleep( SLEEP_TIME );
  }

  private void simulateBackgroundAdditionDuringLifeCycle( 
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
            UICallBackManager.getInstance().addAsync( new Runnable() {
              public void run() {
                log += RUN_ASYNC_EXEC;
              }
            }, display );
          }
        } );
        thread.start();
        try {
          thread.join();
        } catch( InterruptedException e ) {
          // TODO Auto-generated catch block
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
  
  private void simulateUICallBackThreadLockDuringLifeCycle( 
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

  private void fakeRequestParam( final Display display ) {
    Fixture.fakeResponseWriter();
    String id = "org.eclipse.swt.display";
    ContextProvider.getSession().setAttribute( id, display );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
  }
}
