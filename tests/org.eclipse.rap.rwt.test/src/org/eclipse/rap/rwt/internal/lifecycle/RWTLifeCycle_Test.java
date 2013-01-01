/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.lifecycle.IPhase.IInterruptible;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetLifeCycleAdapter;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


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
    entryPointManager.register( EntryPointManager.DEFAULT_PATH,
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
    entryPointManager.register( EntryPointManager.DEFAULT_PATH,
                                TestPhasesEntryPoint.class,
                                null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    PhaseListener listener = new PhaseListener() {
      private static final long serialVersionUID = 1L;

      public void afterPhase( PhaseEvent event ) {
        log.append( AFTER + event.getPhaseId() + "|" );
      }

      public void beforePhase( PhaseEvent event ) {
        log.append( BEFORE + event.getPhaseId() + "|" );
      }

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

      public void afterPhase( PhaseEvent event ) {
        log.append( AFTER + event.getPhaseId() + "|" );
      }

      public void beforePhase( PhaseEvent event ) {
        log.append( BEFORE + event.getPhaseId() + "|" );
      }

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
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, type, null );
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
    entryPointManager.register( EntryPointManager.DEFAULT_PATH,
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
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, TestEntryPoint.class, null );
    RWTLifeCycle lifeCycle = getLifeCycle();

    lifeCycle.execute();

    Message message = Fixture.getProtocolMessage();
    assertEquals( 1, message.getRequestCounter() );
  }

  @Test
  public void testPhaseListenerRegistration() throws IOException {
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, TestEntryPoint.class, null );
    final PhaseListener[] callbackHandler = new PhaseListener[ 1 ];
    PhaseListener listener = new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( PhaseEvent event ) {
        callbackHandler[ 0 ] = this;
      }
      public void afterPhase( PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    };
    getApplicationContext().getPhaseListenerRegistry().add( listener );
    getApplicationContext().getLifeCycleFactory().activate();
    // Run lifecycle in session one
    RWTLifeCycle lifeCycle1 = getLifeCycle();
    lifeCycle1.execute();
    assertSame( listener, callbackHandler[ 0 ] );
    // Simulate new session and run lifecycle
    newSession();
    Fixture.fakeResponseWriter();
    callbackHandler[ 0 ] = null;
    RWTLifeCycle lifeCycle2 = getLifeCycle();
    lifeCycle2.execute();
    assertSame( listener, callbackHandler[ 0 ] );
  }

  @Test
  public void testContinueLifeCycle() {
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void afterPhase( PhaseEvent event ) {
        log.append( "after" + event.getPhaseId() );
      }
      public void beforePhase( PhaseEvent event ) {
        log.append( "before" + event.getPhaseId() );
      }
      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    } );

    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          fail( "Interruptible phase should never get executed." );
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      },
      new IPhase() {
        public PhaseId execute(Display display) throws IOException {
          log.append( "execute" + getPhaseId() );
          return null;
        }
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

    entryPointManager.register( EntryPointManager.DEFAULT_PATH, MainStartup.class, null );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
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
    final boolean[] continueLoop = { true };
    final ServiceContext[] uiContext = new ServiceContext[ 1 ];
    final Throwable[] error = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        while( continueLoop[ 0 ] ) {
          IUIThreadHolder uiThread = ( IUIThreadHolder )Thread.currentThread();
          synchronized( uiThread.getLock() ) {
          }
          uiThread.updateServiceContext();
          uiContext[ 0 ] = ContextProvider.getContext();
          log.append( "executedInUIThread" );
          try {
            uiThread.switchThread();
          } catch( Throwable e ) {
            synchronized( error ) {
              error[ 0 ] = e;
            }
          }
        }
      }
    };
    lifeCycle.uiRunnable = runnable;
    // simulates first request
    lifeCycle.executeUIThread();
    synchronized( error ) {
      if( error[ 0 ] != null ) {
        throw error[ 0 ];
      }
    }
    assertSame( originContext, uiContext[ 0 ] );
    assertEquals( "executedInUIThread", log.toString() );
    assertTrue( getUIThread().isAlive() );
    // simulates subsequent request
    log.setLength( 0 );
    uiContext[ 0 ] = null;
    ServiceContext secondContext = newContext();
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( secondContext );
    lifeCycle.executeUIThread();
    synchronized( error ) {
      if( error[ 0 ] != null ) {
        throw error[ 0 ];
      }
    }
    assertSame( secondContext, uiContext[ 0 ] );
    assertEquals( "executedInUIThread", log.toString() );
    assertTrue( getUIThread().isAlive() );
    // simulates request that ends event loop
    UIThread endingUIThread = getUIThread();
    continueLoop[ 0 ] = false;
    lifeCycle.executeUIThread();
    synchronized( error ) {
      if( error[ 0 ] != null ) {
        throw error[ 0 ];
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
    ServiceContext secondContext = newContext();
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
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, MainStartup.class, null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
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
    final ServiceContext[] uiContext = { null };
    lifeCycle.addPhaseListener( new LoggingPhaseListener() );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      }
    } );
    final Throwable[] error = { null };
    final UIThread[] uiThread = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          synchronized( uiThread[ 0 ].getLock() ) {
          }
          IUIThreadHolder uiThread = ( IUIThreadHolder )Thread.currentThread();
          uiThread.updateServiceContext();
          lifeCycle.continueLifeCycle();
          log.setLength( 0 );
          lifeCycle.sleep();
          uiContext[ 0 ] = ContextProvider.getContext();
          log.append( "readAndDispatch" );
          lifeCycle.sleep();
          log.append( "readAndDispatch" );
        } catch( Throwable e ) {
          error[ 0 ] = e;
        }
      }
    };
    uiThread[ 0 ] = new UIThread( runnable );
    LifeCycleUtil.setUIThread( ContextProvider.getUISession(), uiThread[ 0 ] );

    uiThread[ 0 ].setServiceContext( ContextProvider.getContext() );
    synchronized( uiThread[ 0 ].getLock() ) {
      uiThread[ 0 ].start();
      uiThread[ 0 ].switchThread();
    }

    if( error[ 0 ] != null ) {
      throw error[ 0 ];
    }
    String expected = "after" + PhaseId.PREPARE_UI_ROOT;
    assertEquals( expected, log.toString() );

    log.setLength( 0 );
    ServiceContext expectedContext = newContext();
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( expectedContext );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IPhase() {
        public PhaseId execute(Display display) throws IOException {
          log.append( "prepare" );
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      },
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PROCESS_ACTION;
        }
      }
    } );
    uiThread[ 0 ].setServiceContext( expectedContext );
    uiThread[ 0 ].switchThread();

    if( error[ 0 ] != null ) {
      throw error[ 0 ];
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
    assertSame( expectedContext, uiContext[ 0 ] );

    log.setLength( 0 );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PROCESS_ACTION;
        }
      }
    } );
    uiThread[ 0 ].switchThread();
    if( error[ 0 ] != null ) {
      throw error[ 0 ];
    }
    expected = "before"
             + PhaseId.PROCESS_ACTION
             + "readAndDispatch";
    assertEquals( expected, log.toString() );
    assertFalse( uiThread[ 0 ].isAlive() );
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
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, ErrorStartup.class, null );
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
    final String[] invalidateThreadName = { null };
    final boolean hasContext[] = new boolean[]{ false };
    final ServiceStore serviceStore[] =  { null };
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        invalidateThreadName[ 0 ] = Thread.currentThread().getName();
        hasContext[ 0 ] = ContextProvider.hasContext();
        serviceStore[ 0 ] = ContextProvider.getServiceStore();
      }
    } );
    // Register and 'run' entry point with readAndDispatch/sleep loop
    Class<? extends EntryPoint> entryPointClass = SessionInvalidateWithEventLoopEntryPoint.class;
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, entryPointClass, null );
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
    assertEquals( invalidateThreadName[ 0 ], uiThreadName );
    assertTrue( hasContext[ 0 ] );
    assertNotNull( serviceStore[ 0 ] );
    assertEquals( "", log.toString() );
  }

  @Test
  public void testExceptionInRender() {
    fakeServletPath( EntryPointManager.DEFAULT_PATH );
    entryPointManager.register( EntryPointManager.DEFAULT_PATH,
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
    final String[] uiThreadName = { "unknown-ui-thread" };
    final String[] invalidateThreadName = { "unkown-invalidate-thread" };
    final boolean hasContext[] = new boolean[]{ false };
    final ServiceStore serviceStore[] = { null };
    uiSession.addUISessionListener( new UISessionListener() {
      public void beforeDestroy( UISessionEvent event ) {
        invalidateThreadName[ 0 ] = Thread.currentThread().getName();
        hasContext[ 0 ] = ContextProvider.hasContext();
        serviceStore[ 0 ] = ContextProvider.getServiceStore();
      }
    } );
    // Register and 'run' entry point with readAndDispatch/sleep loop
    Class<? extends EntryPoint> entryPoint = SessionInvalidateWithoutEventLoopEntryPoint.class;
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, entryPoint, null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( PhaseEvent event ) {
        uiThreadName[ 0 ] = Thread.currentThread().getName();
      }
      public void afterPhase( PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    } );
    lifeCycle.execute();
    // Invalidate session
    invalidateSession( uiSession );
    //
    assertFalse( uiSession.isBound() );
    assertEquals( uiThreadName[ 0 ], invalidateThreadName[ 0 ] );
    assertTrue( hasContext[ 0 ] );
    assertNotNull( serviceStore[ 0 ] );
  }

  @Test
  public void testDisposeDisplayOnSessionTimeout() throws Throwable {
    UISession uiSession = ContextProvider.getUISession();
    ContextProvider.getContext().getApplicationContext();
    Class<? extends EntryPoint> clazz = DisposeDisplayOnSessionTimeoutEntryPoint.class;
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, clazz, null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.execute();
    invalidateSession( uiSession );
    assertEquals( "display disposed", log.toString() );
  }

  @Test
  public void testOrderOfDisplayDisposeAndSessionUnbound() throws Throwable {
    UISession uiSession = ContextProvider.getUISession();
    Class<? extends EntryPoint> clazz = TestOrderOfDisplayDisposeAndSessionUnboundEntryPoint.class;
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, clazz, null );
    RWTLifeCycle lifeCycle = getLifeCycle();
    lifeCycle.execute();
    invalidateSession( uiSession );
    assertEquals( "disposeEvent, beforeDestroy", log.toString() );
  }

  @Test
  public void testSwitchThreadCannotBeInterrupted() throws Exception {
    final Throwable[] errorInUIThread = { new Exception( "did not run" ) };
    final UIThread[] uiThread = { null };
    uiThread[ 0 ] = new UIThread( new Runnable() {
      public void run() {
        try {
          synchronized( errorInUIThread ) {
            errorInUIThread[ 0 ] = null;
          }
          uiThread[ 0 ].switchThread();
        } catch( Throwable t ) {
          synchronized( errorInUIThread ) {
            errorInUIThread[ 0 ] = t;
          }
        }
      }
    } );
    uiThread[ 0 ].start();
    Thread.sleep( 100 );
    uiThread[ 0 ].interrupt();
    synchronized( errorInUIThread ) {
      assertNull( "switchThread must not unblock when thread is interrupted",
                  errorInUIThread[ 0 ] );
    }
    // unblock ui thread, see bug 351277
    synchronized( uiThread[ 0 ].getLock() ) {
      uiThread[ 0 ].getLock().notifyAll();
    }
  }

  @Test
  public void testGetUIThreadWhileLifeCycleInExecute() throws IOException {
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, TestEntryPoint.class, null );
    RWTLifeCycle lifeCycle = new RWTLifeCycle( ApplicationContextUtil.getInstance() );
    final Thread[] currentThread = { null };
    final Thread[] uiThread = { null };
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
      public void beforePhase( PhaseEvent event ) {
      }
      public void afterPhase( PhaseEvent event ) {
        currentThread[ 0 ] = Thread.currentThread();
        uiThread[ 0 ] = LifeCycleUtil.getUIThread( ContextProvider.getUISession() ).getThread();
      }
    } );

    lifeCycle.execute();

    assertSame( currentThread[ 0 ], uiThread[ 0 ] );
  }

  @Test
  public void testGetUIThreadAfterLifeCycleExecuted() throws IOException {
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, TestEntryPoint.class, null );
    RWTLifeCycle lifeCycle = new RWTLifeCycle( ApplicationContextUtil.getInstance() );
    lifeCycle.execute();

    Thread uiThread = LifeCycleUtil.getUIThread( ContextProvider.getUISession() ).getThread();

    assertNotNull( uiThread );
  }

  private static RWTLifeCycle getLifeCycle() {
    return ( RWTLifeCycle )getApplicationContext().getLifeCycleFactory().getLifeCycle();
  }

  private static void invalidateSession( final UISession uiSession ) throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        uiSession.getHttpSession().invalidate();
      }
    };
    Fixture.runInThread( runnable );
  }

  private static ServiceContext newContext() {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpServletResponse response = ContextProvider.getResponse();
    ServiceContext result = new ServiceContext( request, response );
    result.setServiceStore( new ServiceStore() );
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
    public void beforePhase( PhaseEvent event ) {
      log.append( "before" + event.getPhaseId() );
    }
    public void afterPhase( PhaseEvent event ) {
      log.append( "after" + event.getPhaseId() );
    }
    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }
  }

  public static final class MainStartup implements EntryPoint {
    public int createUI() {
      log.append( "createUI" );
      return 0;
    }
  }

  public static final class ErrorStartup implements EntryPoint {
    public int createUI() {
      throw new RuntimeException( ERR_MSG );
    }
  }

  private final class ExceptionListenerTest implements PhaseListener {

    private static final long serialVersionUID = 1L;

    public void afterPhase( PhaseEvent event ) {
      log.append( AFTER + event.getPhaseId() + "|" );
      throw new RuntimeException();
    }

    public void beforePhase( PhaseEvent event ) {
      log.append( BEFORE + event.getPhaseId() + "|" );
      throw new RuntimeException();
    }

    public PhaseId getPhaseId() {
      return PhaseId.PREPARE_UI_ROOT;
    }
  }

  public static class TestEntryPoint implements EntryPoint {
    public int createUI() {
      new Display();
      return 0;
    }
  }

  public static class TestPhasesEntryPoint implements EntryPoint {
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
    public int createUI() {
      String msg = TestErrorInLifeCycleEntryPoint.class.getName();
      throw new RuntimeException( msg );
    }
  }

  public static class TestEntryPointWithLog implements EntryPoint {
    public int createUI() {
      new Display();
      log.append( DISPLAY_CREATED );
      return 0;
    }
  }

  public static class DisposeDisplayOnSessionTimeoutEntryPoint implements EntryPoint
  {
    public int createUI() {
      Display display = new Display();
      display.addListener( SWT.Dispose, new Listener() {
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
        if( adapter.equals( WidgetLifeCycleAdapter.class ) ) {
          result = new AbstractWidgetLCA() {
            @Override
            public void preserveValues( Widget widget ) {
            }
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
    public int createUI() {
      Display display = new Display();
      display.addListener( SWT.Dispose, new Listener() {
        public void handleEvent( Event event ) {
          log.append( "disposeEvent, " );
        }
      } );
      UISession uiSession = RWT.getUISession();
      uiSession.addUISessionListener( new UISessionListener() {
        public void beforeDestroy( UISessionEvent event ) {
          log.append( "beforeDestroy" );
        }
      } );
      return 0;
    }
  }
}
