/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.IPhase.IInterruptible;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.service.UIThreadListener;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;


@SuppressWarnings( "deprecation" )
public class RWTLifeCycle_Test {

  private static final String ERR_MSG = "TEST_ERROR";
  private static final String MY_ENTRY_POINT = "/myEntryPoint";
  private static final String BEFORE = "before ";
  private static final String AFTER = "after ";
  private static final String DISPLAY_CREATED = "display created";
  private static final String EXCEPTION_IN_RENDER = "Exception in render";

  private static StringBuffer log = new StringBuffer();
  private EntryPointManager entryPointManager;

  @Before
  public void setUp() {
    log.setLength( 0 );
    Fixture.setUp();
    Fixture.fakeNewRequest();
    Fixture.fakeResponseWriter();
    entryPointManager = getApplicationContext().getEntryPointManager();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testNoEntryPoint() throws IOException {
    RWTLifeCycle lifeCycle = getLifeCycle();
    try {
      lifeCycle.execute();
      fail( "Executing lifecycle without entry point must throw exception" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testDefaultEntryPoint() throws IOException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH,
                                TestEntryPointWithLog.class,
                                null );
    RWTLifeCycle lifeCycle = getLifeCycle();

    lifeCycle.execute();

    assertEquals( DISPLAY_CREATED, log.toString() );
  }

  @Test
  public void testParamOfExistingEntryPoint() throws IOException {
    fakeServletPath( MY_ENTRY_POINT );
    RWTLifeCycle lifeCycle = getLifeCycle();
    entryPointManager.register( MY_ENTRY_POINT, TestEntryPointWithLog.class, null );

    lifeCycle.execute();

    assertEquals( DISPLAY_CREATED, log.toString() );
  }

  @Test
  public void testParamOfNonExistingEntryPoint() throws IOException {
    RWTLifeCycle lifeCycle = getLifeCycle();
    fakeServletPath( "/not-registered" );
    try {
      lifeCycle.execute();
      fail( "Executing lifecycle with unknown entry point must fail." );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testPhases() throws IOException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH,
                                TestPhasesEntryPoint.class,
                                null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    PhaseListener listener = new PhaseListener() {
      private static final long serialVersionUID = 1L;

      @Override
      public void afterPhase( PhaseEvent event ) {
        log.append( AFTER + event.getPhaseId() + "|" );
      }

      @Override
      public void beforePhase( PhaseEvent event ) {
        log.append( BEFORE + event.getPhaseId() + "|" );
      }

      @Override
      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    };
    lifeCycle.addPhaseListener( listener );
    lifeCycle.execute();
    String expected =   BEFORE
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + AFTER
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + BEFORE
                      + PhaseId.RENDER
                      + "|"
                      + AFTER
                      + PhaseId.RENDER
                      + "|";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    lifeCycle.execute();
    expected =   BEFORE
               + PhaseId.PREPARE_UI_ROOT
               + "|"
               + AFTER
               + PhaseId.PREPARE_UI_ROOT
               + "|"
               + BEFORE
               + PhaseId.READ_DATA
               + "|"
               + AFTER
               + PhaseId.READ_DATA
               + "|"
               + BEFORE
               + PhaseId.PROCESS_ACTION
               + "|"
               + AFTER
               + PhaseId.PROCESS_ACTION
               + "|"
               + BEFORE
               + PhaseId.RENDER
               + "|"
               + AFTER
               + PhaseId.RENDER
               + "|";
    assertEquals( expected, log.toString() );
    lifeCycle.removePhaseListener( listener );
    log.setLength( 0 );
    lifeCycle.execute();
    assertEquals( "", log.toString() );
    log.setLength( 0 );
    lifeCycle.addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      @Override
      public void afterPhase( PhaseEvent event ) {
        log.append( AFTER + event.getPhaseId() + "|" );
      }

      @Override
      public void beforePhase( PhaseEvent event ) {
        log.append( BEFORE + event.getPhaseId() + "|" );
      }

      @Override
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    } );
    lifeCycle.execute();
    expected =   BEFORE
               + PhaseId.PREPARE_UI_ROOT
               + "|"
               + AFTER
               + PhaseId.PREPARE_UI_ROOT
               + "|";
    assertEquals( expected, log.toString() );
  }

  @Test
  public void testErrorInLifeCycle() throws IOException {
    Class<TestErrorInLifeCycleEntryPoint> type = TestErrorInLifeCycleEntryPoint.class;
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, type, null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    LifeCycleUtil.setSessionDisplay( null );
    try {
      lifeCycle.execute();
      fail();
    } catch( RuntimeException e ) {
      String msg = type.getName();
      assertEquals( msg, e.getMessage() );
      assertTrue( RWTLifeCycle.getUIThreadHolder().getThread().isAlive() );
    }
  }

  @Test
  public void testExceptionInPhaseListener() throws IOException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH,
                                TestEntryPoint.class,
                                null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.addPhaseListener( new ExceptionListenerTest() );
    lifeCycle.addPhaseListener( new ExceptionListenerTest() );
    lifeCycle.execute();
    String expected = BEFORE
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + BEFORE
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + AFTER
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + AFTER
                      + PhaseId.PREPARE_UI_ROOT
                      + "|";
    assertEquals( expected, log.toString() );
  }

  @Test
  public void testRender() throws IOException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, TestEntryPoint.class, null );
    RWTLifeCycle lifeCycle = getLifeCycle();

    lifeCycle.execute();

    TestMessage message = Fixture.getProtocolMessage();
    assertTrue( message.getOperationCount() > 0 );
  }

  @Test
  public void testPhaseListenerRegistration() throws IOException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, TestEntryPoint.class, null );
    final AtomicReference<PhaseListener> callbackHandler = new AtomicReference<>();
    PhaseListener listener = new PhaseListener() {
      private static final long serialVersionUID = 1L;
      @Override
      public void beforePhase( PhaseEvent event ) {
        callbackHandler.set( this );
      }
      @Override
      public void afterPhase( PhaseEvent event ) {
      }
      @Override
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    };
    getApplicationContext().getPhaseListenerManager().addPhaseListener( listener );
    // Run lifecycle in session one
    RWTLifeCycle lifeCycle1 = getLifeCycle();
    lifeCycle1.execute();
    assertSame( listener, callbackHandler.get() );
    // Simulate new session and run lifecycle
    newSession();
    Fixture.fakeResponseWriter();
    callbackHandler.set( null );
    RWTLifeCycle lifeCycle2 = getLifeCycle();
    lifeCycle2.execute();
    assertSame( listener, callbackHandler.get() );
  }

  @Test
  public void testContinueLifeCycle() {
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      @Override
      public void afterPhase( PhaseEvent event ) {
        log.append( "after" + event.getPhaseId() );
      }
      @Override
      public void beforePhase( PhaseEvent event ) {
        log.append( "before" + event.getPhaseId() );
      }
      @Override
      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    } );

    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        @Override
        public PhaseId execute(Display display) throws IOException {
          fail( "Interruptible phase should never get executed." );
          return null;
        }
        @Override
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      },
      new IPhase() {
        @Override
        public PhaseId execute(Display display) throws IOException {
          log.append( "execute" + getPhaseId() );
          return null;
        }
        @Override
        public PhaseId getPhaseId() {
          return PhaseId.RENDER;
        }
      }
    } );
    lifeCycle.continueLifeCycle();
    assertEquals( "before" + PhaseId.PREPARE_UI_ROOT, log.toString() );
    log.setLength( 0 );
    lifeCycle.continueLifeCycle();
    String expected = "after"
                    + PhaseId.PREPARE_UI_ROOT
                    + "before"
                    + PhaseId.RENDER
                    + "execute"
                    + PhaseId.RENDER
                    + "after"
                    + PhaseId.RENDER;
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
  }

  @Test
  public void testCreateUIIfNecessary() {
    RWTLifeCycle lifeCycle = getLifeCycle();
    int returnValue = lifeCycle.createUI();
    assertEquals( -1, returnValue );

    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, MainStartup.class, null );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        @Override
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        @Override
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      }
    } );

    returnValue = lifeCycle.createUI();
    assertEquals( -1, returnValue );

    lifeCycle.continueLifeCycle();
    returnValue = lifeCycle.createUI();
    assertEquals( 0, returnValue );
    getApplicationContext().getEntryPointManager().deregisterAll();

    lifeCycle.continueLifeCycle();
    returnValue = lifeCycle.createUI();
    assertEquals( -1, returnValue );
  }

  @Test
  public void testReadAndDispatch() {
    Display display = new Display();
    boolean returnValue = Display.getCurrent().readAndDispatch();
    assertFalse( returnValue );

    Fixture.fakePhase( PhaseId.READ_DATA );
    ProcessActionRunner.add( new Runnable() {
      @Override
      public void run() {
        log.append( "executed" );
      }
    } );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    returnValue = Display.getCurrent().readAndDispatch();
    assertTrue( returnValue );
    assertEquals( "executed", log.toString() );

    log.setLength( 0 );
    returnValue = Display.getCurrent().readAndDispatch();
    assertFalse( returnValue );
    assertEquals( "", log.toString() );

    Fixture.fakePhase( PhaseId.READ_DATA );
    log.setLength( 0 );
    Shell widget = new Shell( display ) {
      private static final long serialVersionUID = 1L;
      @Override
      public boolean getVisible() {
        return true;
      }
    };
    Listener listener = new Listener() {
      @Override
      public void handleEvent( Event event ) {
        log.append( "eventExecuted" );
      }
    };
    widget.addListener( SWT.Selection, listener );
    // event is scheduled but not executed at this point as there is no life
    // cycle running
    widget.notifyListeners( SWT.Selection, new Event() );
    log.setLength( 0 );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    returnValue = Display.getCurrent().readAndDispatch();
    assertTrue( returnValue );
    assertEquals( "eventExecuted", log.toString() );
  }

  @Test
  public void testNestedReadAndDispatch() {
    Fixture.fakePhase( PhaseId.READ_DATA );
    final Display display = new Display();
    Shell widget = new Shell( display ) {
      private static final long serialVersionUID = 1L;
      @Override
      public boolean getVisible() {
        return true;
      }
    };
    Listener listener = new Listener() {
      @Override
      public void handleEvent( Event event ) {
        display.readAndDispatch();
      }
    };
    widget.addListener( SWT.Selection, listener );
    Event event = new Event();
    widget.notifyListeners( SWT.Selection, event );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    // This test ensures that nested calls of readAndDsipatch don't cause
    // an endless loop or a stack overflow - therefore no assert is needed
  }

  @Test
  public void testReadAndDispatchWithAsyncExec() {
    final java.util.List<Runnable> log = new ArrayList<Runnable>();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        log.add( this );
      }
    };
    Display display = new Display();
    display.asyncExec( runnable );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    boolean result = display.readAndDispatch();
    assertTrue( result );
    assertSame( runnable, log.get( 0 ) );
    assertFalse( display.readAndDispatch() );
  }

  @Test
  public void testBeginUIThread() throws Throwable {
    ServiceContext originContext = ContextProvider.getContext();
    RWTLifeCycle lifeCycle = getLifeCycle();
    final AtomicBoolean continueLoop = new AtomicBoolean( true );
    final AtomicReference<ServiceContext> uiContext = new AtomicReference<>();
    final AtomicReference<Throwable> error = new AtomicReference<>();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        while( continueLoop.get() ) {
          IUIThreadHolder uiThread = ( IUIThreadHolder )Thread.currentThread();
          synchronized( uiThread.getLock() ) {
          }
          uiThread.updateServiceContext();
          uiContext.set( ContextProvider.getContext() );
          log.append( "executedInUIThread" );
          try {
            uiThread.switchThread();
          } catch( Throwable e ) {
            synchronized( error ) {
              error.set( e );
            }
          }
        }
      }
    };
    lifeCycle.uiRunnable = runnable;
    // simulates first request
    lifeCycle.executeUIThread();
    synchronized( error ) {
      if( error.get() != null ) {
        throw error.get();
      }
    }
    assertSame( originContext, uiContext.get() );
    assertEquals( "executedInUIThread", log.toString() );
    assertTrue( getUIThread().isAlive() );
    // simulates subsequent request
    log.setLength( 0 );
    uiContext.set( null );
    ServiceContext secondContext = newContext( originContext.getUISession() );
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( secondContext );
    lifeCycle.executeUIThread();
    synchronized( error ) {
      if( error.get() != null ) {
        throw error.get();
      }
    }
    assertSame( secondContext, uiContext.get() );
    assertEquals( "executedInUIThread", log.toString() );
    assertTrue( getUIThread().isAlive() );
    // simulates request that ends event loop
    UIThread endingUIThread = getUIThread();
    continueLoop.set( false );
    lifeCycle.executeUIThread();
    synchronized( error ) {
      if( error.get() != null ) {
        throw error.get();
      }
    }
    assertFalse( endingUIThread.isAlive() );
    assertNull( getUIThread() );
    // clean up
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( originContext );
  }

  @Test
  public void testUpdateServiceContext() {
    UIThread thread = new UIThread( null );
    ServiceContext firstContext = ContextProvider.getContext();
    thread.setServiceContext( firstContext );
    thread.run();
    ServiceContext secondContext = newContext( firstContext.getUISession() );
    thread.setServiceContext( secondContext );
    thread.updateServiceContext();
    // As we don't start the UIThread, we can use the test-thread for assertion
    // instead of retrieving the actual context from inside the runnable
    assertSame( secondContext, ContextProvider.getContext() );
    // clean up
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( firstContext );
  }

  @Test
  public void testUIRunnable() throws InterruptedException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, MainStartup.class, null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        @Override
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        @Override
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      }
    } );
    lifeCycle.addPhaseListener( new LoggingPhaseListener() );
    UIThread thread = new UIThread( lifeCycle.uiRunnable );
    thread.setServiceContext( ContextProvider.getContext() );
    thread.start();
    // TODO [rh] Find more failsafe solution
    Thread.sleep( 200 );

    String expected = "before"
                    + PhaseId.PREPARE_UI_ROOT
                    + "createUI"
                    + "after"
                    + PhaseId.PREPARE_UI_ROOT;
    assertEquals( expected, log.toString() );
  }

  @Test
  public void testSleep() throws Throwable {
    final RWTLifeCycle lifeCycle = getLifeCycle();
    final AtomicReference<ServiceContext> uiContext = new AtomicReference<>();
    lifeCycle.addPhaseListener( new LoggingPhaseListener() );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        @Override
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        @Override
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      }
    } );
    final AtomicReference<Throwable> error = new AtomicReference<>();
    final AtomicReference<UIThread> uiThread = new AtomicReference<>();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          synchronized( uiThread.get().getLock() ) {
          }
          IUIThreadHolder uiThread = ( IUIThreadHolder )Thread.currentThread();
          uiThread.updateServiceContext();
          lifeCycle.continueLifeCycle();
          log.setLength( 0 );
          lifeCycle.sleep();
          uiContext.set( ContextProvider.getContext() );
          log.append( "readAndDispatch" );
          lifeCycle.sleep();
          log.append( "readAndDispatch" );
        } catch( Throwable e ) {
          error.set( e );
        }
      }
    };
    uiThread.set( new UIThread( runnable ) );
    LifeCycleUtil.setUIThread( ContextProvider.getUISession(), uiThread.get() );

    uiThread.get().setServiceContext( ContextProvider.getContext() );
    synchronized( uiThread.get().getLock() ) {
      uiThread.get().start();
      uiThread.get().switchThread();
    }

    if( error.get() != null ) {
      throw error.get();
    }
    String expected = "after" + PhaseId.PREPARE_UI_ROOT;
    assertEquals( expected, log.toString() );

    log.setLength( 0 );
    ServiceContext expectedContext = newContext( ContextProvider.getUISession() );
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( expectedContext );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IPhase() {
        @Override
        public PhaseId execute(Display display) throws IOException {
          log.append( "prepare" );
          return null;
        }
        @Override
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      },
      new IInterruptible() {
        @Override
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        @Override
        public PhaseId getPhaseId() {
          return PhaseId.PROCESS_ACTION;
        }
      }
    } );
    uiThread.get().setServiceContext( expectedContext );
    uiThread.get().switchThread();

    if( error.get() != null ) {
      throw error.get();
    }
    expected = "before"
             + PhaseId.PREPARE_UI_ROOT
             + "prepare"
             + "after"
             + PhaseId.PREPARE_UI_ROOT
             + "before"
             + PhaseId.PROCESS_ACTION
             + "readAndDispatch"
             + "after"
             + PhaseId.PROCESS_ACTION;
    assertEquals( expected, log.toString() );
    assertSame( expectedContext, uiContext.get() );

    log.setLength( 0 );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        @Override
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        @Override
        public PhaseId getPhaseId() {
          return PhaseId.PROCESS_ACTION;
        }
      }
    } );
    uiThread.get().switchThread();
    if( error.get() != null ) {
      throw error.get();
    }
    expected = "before"
             + PhaseId.PROCESS_ACTION
             + "readAndDispatch";
    assertEquals( expected, log.toString() );
    assertFalse( uiThread.get().isAlive() );
  }

  @Test
  public void testGetSetPhaseOrder() {
    RWTLifeCycle lifeCycle = getLifeCycle();
    IPhase[] phaseOrder = new IPhase[ 0 ];
    lifeCycle.setPhaseOrder( phaseOrder );
    assertSame( phaseOrder, lifeCycle.getPhaseOrder() );
    // create new context to ensure that phase order is stored in context
    ServiceContext bufferedContext = ContextProvider.getContext();
    ContextProvider.releaseContextHolder();
    Fixture.createServiceContext();
    assertNull( lifeCycle.getPhaseOrder() );
    // clean up
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( bufferedContext );
  }

  @Test
  public void testErrorHandlingInCreateUI() throws IOException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, ErrorStartup.class, null );
    try {
      getLifeCycle().execute();
      fail();
    } catch( RuntimeException re ) {
      assertEquals( ERR_MSG, re.getMessage() );
    }
  }

  @Test
  public void testSessionInvalidateWithRunningEventLoop() throws Throwable {
    final UISession uiSession = ContextProvider.getUISession();
    final AtomicReference<String> invalidateThreadName = new AtomicReference<>();
    final AtomicBoolean hasContext = new AtomicBoolean();
    final AtomicReference<ServiceStore> serviceStore = new AtomicReference<>();
    uiSession.addUISessionListener( new UISessionListener() {
      @Override
      public void beforeDestroy( UISessionEvent event ) {
        invalidateThreadName.set( Thread.currentThread().getName() );
        hasContext.set( ContextProvider.hasContext() );
        serviceStore.set( ContextProvider.getServiceStore() );
      }
    } );
    // Register and 'run' entry point with readAndDispatch/sleep loop
    Class<? extends EntryPoint> entryPointClass = SessionInvalidateWithEventLoopEntryPoint.class;
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, entryPointClass, null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.execute();
    // Store some values for later comparison
    IUIThreadHolder uiThreadHolder = LifeCycleUtil.getUIThread( uiSession );
    String uiThreadName = uiThreadHolder.getThread().getName();
    // Invalidate session
    invalidateSession( uiSession );
    //
    assertFalse( uiThreadHolder.getThread().isAlive() );
    assertFalse( uiSession.isBound() );
    assertEquals( invalidateThreadName.get(), uiThreadName );
    assertTrue( hasContext.get() );
    assertNotNull( serviceStore.get() );
    assertEquals( "", log.toString() );
  }

  @Test
  public void testExceptionInRender() {
    fakeServletPath( TestRequest.DEFAULT_SERVLET_PATH );
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH,
                                ExceptionInRenderEntryPoint.class,
                                null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    try {
      lifeCycle.execute();
      fail( "Exception in render must be re-thrown by life cycle" );
    } catch( Throwable e ) {
      assertEquals( EXCEPTION_IN_RENDER, e.getMessage() );
    }
  }

  @Test
  public void testSessionInvalidateWithoutRunningEventLoop() throws Throwable {
    final UISession uiSession = ContextProvider.getUISession();
    final AtomicReference<String> uiThreadName = new AtomicReference<>( "unknown" );
    final AtomicReference<String> invalidateThreadName = new AtomicReference<>( "unkown" );
    final AtomicBoolean hasContext = new AtomicBoolean();
    final AtomicReference<ServiceStore> serviceStore = new AtomicReference<>();
    uiSession.addUISessionListener( new UISessionListener() {
      @Override
      public void beforeDestroy( UISessionEvent event ) {
        invalidateThreadName.set( Thread.currentThread().getName() );
        hasContext.set( ContextProvider.hasContext() );
        serviceStore.set( ContextProvider.getServiceStore() );
      }
    } );
    // Register and 'run' entry point with readAndDispatch/sleep loop
    Class<? extends EntryPoint> entryPoint = SessionInvalidateWithoutEventLoopEntryPoint.class;
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, entryPoint, null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      @Override
      public void beforePhase( PhaseEvent event ) {
        uiThreadName.set( Thread.currentThread().getName() );
      }
      @Override
      public void afterPhase( PhaseEvent event ) {
      }
      @Override
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    } );
    lifeCycle.execute();
    // Invalidate session
    invalidateSession( uiSession );
    //
    assertFalse( uiSession.isBound() );
    assertEquals( uiThreadName.get(), invalidateThreadName.get() );
    assertTrue( hasContext.get() );
    assertNotNull( serviceStore.get() );
  }

  @Test
  public void testDisposeDisplayOnSessionTimeout() throws Throwable {
    UISession uiSession = ContextProvider.getUISession();
    ContextProvider.getContext().getApplicationContext();
    Class<? extends EntryPoint> clazz = DisposeDisplayOnSessionTimeoutEntryPoint.class;
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, clazz, null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.execute();
    invalidateSession( uiSession );
    assertEquals( "display disposed", log.toString() );
  }

  @Test
  public void testOrderOfDisplayDisposeAndSessionUnbound() throws Throwable {
    UISession uiSession = ContextProvider.getUISession();
    Class<? extends EntryPoint> clazz = TestOrderOfDisplayDisposeAndSessionUnboundEntryPoint.class;
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, clazz, null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.execute();
    invalidateSession( uiSession );
    assertEquals( "disposeEvent, beforeDestroy", log.toString() );
  }

  @Test
  public void testSwitchThreadCannotBeInterrupted() throws Exception {
    final AtomicReference<Throwable> errorInUIThread = new AtomicReference<>();
    errorInUIThread.set( new Exception( "did not run" ) );
    final AtomicReference<UIThread> uiThread = new AtomicReference<>();
    uiThread.set( new UIThread( new Runnable() {
      @Override
      public void run() {
        try {
          errorInUIThread.set( null );
          uiThread.get().switchThread();
        } catch( Throwable t ) {
          errorInUIThread.set( t );
        }
      }
    } ) );
    uiThread.get().start();
    Thread.sleep( 100 );
    uiThread.get().interrupt();
    assertNull( "switchThread must not unblock when thread is interrupted", errorInUIThread.get() );
    // unblock ui thread, see bug 351277
    synchronized( uiThread.get().getLock() ) {
      uiThread.get().getLock().notifyAll();
    }
  }

  @Test
  public void testGetUIThreadWhileLifeCycleInExecute() throws IOException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, TestEntryPoint.class, null );
    RWTLifeCycle lifeCycle = new RWTLifeCycle( getApplicationContext() );
    final AtomicReference<Thread> currentThread = new AtomicReference<>();
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
        currentThread.set( Thread.currentThread() );
        uiThread.set( LifeCycleUtil.getUIThread( ContextProvider.getUISession() ).getThread() );
      }
    } );

    lifeCycle.execute();

    assertSame( currentThread.get(), uiThread.get() );
  }

  @Test
  public void testGetUIThreadAfterLifeCycleExecuted() throws IOException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, TestEntryPoint.class, null );
    RWTLifeCycle lifeCycle = new RWTLifeCycle( getApplicationContext() );
    lifeCycle.execute();

    Thread uiThread = LifeCycleUtil.getUIThread( ContextProvider.getUISession() ).getThread();

    assertNotNull( uiThread );
  }

  @Test
  public void testNotifyUIThreadListeners() throws IOException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, TestPhasesEntryPoint.class, null );
    ApplicationContextImpl applicationContext = getApplicationContext();
    UIThreadListener listener = mock( UIThreadListener.class );
    applicationContext.addUIThreadListener( listener );
    RWTLifeCycle lifeCycle = new RWTLifeCycle( applicationContext );
    ArgumentCaptor<UISessionEvent> captor = ArgumentCaptor.forClass( UISessionEvent.class );
    InOrder inOrder = inOrder( listener );

    lifeCycle.execute(); // first request does not have process action
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
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, TestPhasesEntryPoint.class, null );
    ApplicationContextImpl applicationContext = getApplicationContext();
    UIThreadListener listener = mock( UIThreadListener.class );
    applicationContext.addUIThreadListener( listener );
    RWTLifeCycle lifeCycle = new RWTLifeCycle( applicationContext );

    lifeCycle.execute();
    lifeCycle.execute();
    lifeCycle.execute();

    verify( listener, times( 2 ) ).enterUIThread( any( UISessionEvent.class ) );
    verify( listener, times( 2 ) ).leaveUIThread( any( UISessionEvent.class ) );
  }

  @Test
  public void testNotifyUIThreadListeners_haveAccessToUISession() throws IOException {
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, TestPhasesEntryPoint.class, null );
    ApplicationContextImpl applicationContext = getApplicationContext();
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
    applicationContext.addUIThreadListener( listener );
    RWTLifeCycle lifeCycle = new RWTLifeCycle( applicationContext );

    lifeCycle.execute();
    lifeCycle.execute();
  }

  private static RWTLifeCycle getLifeCycle() {
    return ( RWTLifeCycle )getApplicationContext().getLifeCycleFactory().getLifeCycle();
  }

  private static void invalidateSession( final UISession uiSession ) throws Throwable {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        uiSession.getHttpSession().invalidate();
      }
    };
    runInThread( runnable );
  }

  private static ServiceContext newContext( UISession uiSession ) {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpServletResponse response = ContextProvider.getResponse();
    ApplicationContextImpl applicationContext = mock( ApplicationContextImpl.class );
    doReturn( Boolean.TRUE ).when( applicationContext ).isActive();
    ServiceContext result = new ServiceContext( request, response, applicationContext );
    result.setServiceStore( new ServiceStore() );
    result.setUISession( uiSession );
    return result;
  }

  private static void newSession() {
    ContextProvider.disposeContext();
    Fixture.createServiceContext();
    Fixture.fakeClient( new WebClient() );
  }

  private static UIThread getUIThread() {
    UISession uiSession = ContextProvider.getUISession();
    return ( UIThread )LifeCycleUtil.getUIThread( uiSession );
  }

  private static void fakeServletPath( String myEntryPoint ) {
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setServletPath( myEntryPoint );
  }

  private static class LoggingPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
    @Override
    public void beforePhase( PhaseEvent event ) {
      log.append( "before" + event.getPhaseId() );
    }
    @Override
    public void afterPhase( PhaseEvent event ) {
      log.append( "after" + event.getPhaseId() );
    }
    @Override
    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }
  }

  public static final class MainStartup implements EntryPoint {
    @Override
    public int createUI() {
      log.append( "createUI" );
      return 0;
    }
  }

  public static final class ErrorStartup implements EntryPoint {
    @Override
    public int createUI() {
      throw new RuntimeException( ERR_MSG );
    }
  }

  private final class ExceptionListenerTest implements PhaseListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void afterPhase( PhaseEvent event ) {
      log.append( AFTER + event.getPhaseId() + "|" );
      throw new RuntimeException();
    }

    @Override
    public void beforePhase( PhaseEvent event ) {
      log.append( BEFORE + event.getPhaseId() + "|" );
      throw new RuntimeException();
    }

    @Override
    public PhaseId getPhaseId() {
      return PhaseId.PREPARE_UI_ROOT;
    }
  }

  public static class TestEntryPoint implements EntryPoint {
    @Override
    public int createUI() {
      new Display();
      return 0;
    }
  }

  public static class TestPhasesEntryPoint implements EntryPoint {
    @Override
    public int createUI() {
      Display display = new Display();
      while( !display.isDisposed() ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      return 0;
    }
  }

  public static class TestErrorInLifeCycleEntryPoint implements EntryPoint {
    @Override
    public int createUI() {
      String msg = TestErrorInLifeCycleEntryPoint.class.getName();
      throw new RuntimeException( msg );
    }
  }

  public static class TestEntryPointWithLog implements EntryPoint {
    @Override
    public int createUI() {
      new Display();
      log.append( DISPLAY_CREATED );
      return 0;
    }
  }

  public static class DisposeDisplayOnSessionTimeoutEntryPoint implements EntryPoint
  {
    @Override
    public int createUI() {
      Display display = new Display();
      display.addListener( SWT.Dispose, new Listener() {
        @Override
        public void handleEvent( Event event ) {
          log.append( "display disposed" );
        }
      } );
      return 0;
    }
  }

  public static class SessionInvalidateWithEventLoopEntryPoint
    implements EntryPoint
  {
    @Override
    public int createUI() {
      Display display = new Display();
      Shell shell = new Shell( display );
      shell.open();
      while( !shell.isDisposed() ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      log.append( "regular end of createUI" );
      return 0;
    }
  }

  public static class SessionInvalidateWithoutEventLoopEntryPoint
    implements EntryPoint
  {
    @Override
    public int createUI() {
      new Display();
      return 0;
    }
  }

  public static class ExceptionInRenderEntryPoint implements EntryPoint {
    public static class BuggyShell extends Shell {
      private static final long serialVersionUID = 1L;
      public BuggyShell( Display display ) {
        super( display );
      }
      @SuppressWarnings("unchecked")
      @Override
      public <T> T getAdapter( Class<T> adapter ) {
        Object result;
        if( adapter.equals( WidgetLCA.class ) ) {
          result = new WidgetLCA() {
            @Override
            public void preserveValues( Widget widget ) {
            }
            @Override
            public void readData( Widget widget ) {
            }
            @Override
            public void renderInitialization( Widget widget )
              throws IOException
            {
              throw new RuntimeException( EXCEPTION_IN_RENDER );
            }
            @Override
            public void renderChanges( Widget widget ) throws IOException {
              throw new RuntimeException( EXCEPTION_IN_RENDER );
            }
            @Override
            public void renderDispose( Widget widget ) throws IOException {
            }
          };
        } else {
          result = super.getAdapter( adapter );
        }
        return ( T )result;
      }
    }

    @Override
    public int createUI() {
      Display display = new Display();
      Shell shell = new BuggyShell( display );
      shell.open();
      while( !shell.isDisposed() ) {
        try {
          if( !display.readAndDispatch() ) {
            display.sleep();
          }
        } catch( RuntimeException e ) {
          // continue loop
        }
      }
      log.append( "regular end of createUI" );
      return 0;
    }
  }

  public static final class TestOrderOfDisplayDisposeAndSessionUnboundEntryPoint
    implements EntryPoint
  {
    @Override
    public int createUI() {
      Display display = new Display();
      display.addListener( SWT.Dispose, new Listener() {
        @Override
        public void handleEvent( Event event ) {
          log.append( "disposeEvent, " );
        }
      } );
      UISession uiSession = RWT.getUISession();
      uiSession.addUISessionListener( new UISessionListener() {
        @Override
        public void beforeDestroy( UISessionEvent event ) {
          log.append( "beforeDestroy" );
        }
      } );
      return 0;
    }
  }

}
