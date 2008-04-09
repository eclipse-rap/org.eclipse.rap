/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.*;

import javax.servlet.http.*;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.browser.Ie6up;
import org.eclipse.rwt.internal.lifecycle.IPhase.IInterruptible;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

public class RWTLifeCycle_Test extends TestCase {

  private static final String ERR_MSG = "TEST_ERROR";
  private static final String MY_ENTRY_POINT = "myEntryPoint";
  private static final String BEFORE = "before ";
  private static final String AFTER = "after ";
  private static final String DISPLAY_CREATED = "display created";
  private static final String EXCEPTION_IN_RENDER = "Exception in render";

  private static StringBuffer log = new StringBuffer();

  private PrintStream bufferedSystemErr;
  private ByteArrayOutputStream capturedSystemErr;


  private final class LoggingPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
    public void beforePhase( final PhaseEvent event ) {
      log.append( "before" + event.getPhaseId() );
    }
    public void afterPhase( final PhaseEvent event ) {
      log.append( "after" + event.getPhaseId() );
    }
    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }
  }

  public static final class MainStartup implements IEntryPoint {
    public int createUI() {
      log.append( "createUI" );
      return 0;
    }
  }

  public static final class ErrorStartup implements IEntryPoint {
    public int createUI() {
      throw new RuntimeException( ERR_MSG );
    }
  }

  private final class ExceptionListenerTest implements PhaseListener {

    private static final long serialVersionUID = 1L;

    public void afterPhase( final PhaseEvent event ) {
      log.append( AFTER + event.getPhaseId() + "|" );
      throw new RuntimeException();
    }

    public void beforePhase( final PhaseEvent event ) {
      log.append( BEFORE + event.getPhaseId() + "|" );
      throw new RuntimeException();
    }

    public PhaseId getPhaseId() {
      return PhaseId.PREPARE_UI_ROOT;
    }
  }

  public static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      new Display();
      return 0;
    }
  }

  public static class TestEntryPointWithLog implements IEntryPoint {
    public int createUI() {
      new Display();
      log.append( DISPLAY_CREATED );
      return 0;
    }
  }

  public static class SessionInvalidateWithEventLoopEntryPoint
    implements IEntryPoint
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
    implements IEntryPoint
  {
    public int createUI() {
      new Display();
      return 0;
    }
  }
  
  public static class ExceptionInRenderEntryPoint implements IEntryPoint {
    public static class BuggyShell extends Shell {
      public BuggyShell( final Display display ) {
        super( display );
      }
      public Object getAdapter( Class adapter ) {
        Object result;
        if( adapter.equals( ILifeCycleAdapter.class ) ) {
          result = new AbstractWidgetLCA() {
            public void preserveValues( Widget widget ) {
            }
            public void readData( Widget widget ) {
            }
            public void renderInitialization( Widget widget )
              throws IOException
            {
              throw new RuntimeException( EXCEPTION_IN_RENDER );
            }
            public void renderChanges( Widget widget ) throws IOException {
              throw new RuntimeException( EXCEPTION_IN_RENDER );
            }
            public void renderDispose( Widget widget ) throws IOException {
            }
          };
        } else {
          result = super.getAdapter( adapter );
        }
        return result;
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

  public void testNoEntryPoint() throws IOException {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    try {
      lifeCycle.execute();
      fail( "Executing lifecycle without entry point must throw exception" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDefaultEntryPoint() throws IOException {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPointWithLog.class );
    lifeCycle.execute();
    assertEquals( DISPLAY_CREATED, log.toString() );
  }

  public void testParamOfExistingEntryPoint() throws IOException {
    Fixture.fakeRequestParam( RequestParams.STARTUP, MY_ENTRY_POINT );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    EntryPointManager.register( MY_ENTRY_POINT, TestEntryPointWithLog.class );
    lifeCycle.execute();
    assertEquals( DISPLAY_CREATED, log.toString() );
    EntryPointManager.deregister( MY_ENTRY_POINT );
  }

  public void testParamOfNonExistingEntryPoint() throws IOException {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    Fixture.fakeRequestParam( RequestParams.STARTUP, "notRegistered" );
    try {
      lifeCycle.execute();
      fail( "Executing lifecycle with unknown entry point must fail." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testRWTLifeCyclePhases() throws IOException {
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPoint.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    PhaseListener listener = new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void afterPhase( final PhaseEvent event ) {
        log.append( AFTER + event.getPhaseId() + "|" );
      }

      public void beforePhase( final PhaseEvent event ) {
        log.append( BEFORE + event.getPhaseId() + "|" );
      }

      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    };
    lifeCycle.addPhaseListener( listener );
    lifeCycle.execute();
    String expected = BEFORE
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
    expected = BEFORE
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

      public void afterPhase( final PhaseEvent event ) {
        log.append( AFTER + event.getPhaseId() + "|" );
      }

      public void beforePhase( final PhaseEvent event ) {
        log.append( BEFORE + event.getPhaseId() + "|" );
      }

      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    } );
    lifeCycle.execute();
    expected = BEFORE
               + PhaseId.PREPARE_UI_ROOT
               + "|"
               + AFTER
               + PhaseId.PREPARE_UI_ROOT
               + "|";
    assertEquals( expected, log.toString() );
    Fixture.fakeRequestParam( RequestParams.STARTUP,
                              EntryPointManager.DEFAULT );
    try {
      lifeCycle.execute();
      fail();
    } catch( final IllegalStateException iae ) {
      // expected
    }
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testExceptionInPhaseListener() throws IOException {
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPoint.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
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
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testRender() throws IOException {
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPoint.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.execute();
    assertTrue( Fixture.getAllMarkup().length() > 0 );
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testPhaseListenerRegistration() throws IOException {
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPoint.class );
    final PhaseListener[] callbackHandler = new PhaseListener[ 1 ];
    PhaseListener listener = new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void beforePhase( PhaseEvent event ) {
        callbackHandler[ 0 ] = this;
      }

      public void afterPhase( final PhaseEvent event ) {
      }

      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    };
    PhaseListenerRegistry.add( listener );
    RWTLifeCycle lifeCycle1 = new RWTLifeCycle();
    lifeCycle1.execute();
    assertSame( callbackHandler[ 0 ], listener );
    callbackHandler[ 0 ] = null;
    RWTLifeCycle lifeCycle2 = new RWTLifeCycle();
    lifeCycle2.execute();
    assertSame( callbackHandler[ 0 ], listener );
    PhaseListenerRegistry.clear();
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testContinueLifeCycle() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void afterPhase( final PhaseEvent event ) {
        log.append( "after" + event.getPhaseId() );
      }
      public void beforePhase( final PhaseEvent event ) {
        log.append( "before" + event.getPhaseId() );
      }
      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    } );

    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute() throws IOException {
          fail( "Interruptible phase should never get executed." );
          return null;
        }
        public PhaseId getPhaseID() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      },
      new IPhase() {
        public PhaseId execute() throws IOException {
          log.append( "execute" + getPhaseID() );
          return null;
        }
        public PhaseId getPhaseID() {
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

  public void testCreateUIIfNecessary() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    int returnValue = RWTLifeCycle.createUI();
    assertEquals( -1, returnValue );

    EntryPointManager.register( EntryPointManager.DEFAULT, MainStartup.class );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute() throws IOException {
          return null;
        }
        public PhaseId getPhaseID() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      }
    } );

    returnValue = RWTLifeCycle.createUI();
    assertEquals( -1, returnValue );

    lifeCycle.continueLifeCycle();
    returnValue = RWTLifeCycle.createUI();
    assertEquals( 0, returnValue );
    EntryPointManager.deregister( EntryPointManager.DEFAULT );

    lifeCycle.continueLifeCycle();
    returnValue = RWTLifeCycle.createUI();
    assertEquals( -1, returnValue );
  }

  public void testReadAndDispatch() {
    boolean returnValue = RWTLifeCycle.readAndDispatch();
    assertFalse( returnValue );

    RWTFixture.fakePhase( PhaseId.READ_DATA );
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        log.append( "executed" );
      }
    } );

    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    returnValue = RWTLifeCycle.readAndDispatch();
    assertTrue( returnValue );
    assertEquals( "executed", log.toString() );

    log.setLength( 0 );
    returnValue = RWTLifeCycle.readAndDispatch();
    assertFalse( returnValue );
    assertEquals( "", log.toString() );

    RWTFixture.fakePhase( PhaseId.READ_DATA );
    log.setLength( 0 );
    Display display = new Display();
    Shell widget = new Shell( display ) {
      public boolean getVisible() {
        return true;
      }
    };
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "eventExecuted" );
      }
    };
    SelectionEvent.addListener( widget, listener );
    SelectionEvent event
      = new SelectionEvent( widget, null, SelectionEvent.WIDGET_SELECTED );
    // event is scheduled but not executed at this point as there is no life
    // cycle running
    event.processEvent();
    log.setLength( 0 );
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    returnValue = RWTLifeCycle.readAndDispatch();
    assertTrue( returnValue );
    assertEquals( "eventExecuted", log.toString() );
  }

  public void testNesedReadAndDispatch() {
    RWTFixture.fakePhase( PhaseId.READ_DATA );
    final Display display = new Display();
    Shell widget = new Shell( display ) {
      public boolean getVisible() {
        return true;
      }
    };
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        display.readAndDispatch();
      }
    };
    SelectionEvent.addListener( widget, listener );
    SelectionEvent event
      = new SelectionEvent( widget, null, SelectionEvent.WIDGET_SELECTED );
    event.processEvent();

    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    // This test ensures that nested calls of readAndDsipatch don't cause
    // an endless loop or a stack overflow - therefore no assert is needed
  }

  public void testBeginUIThread() throws InterruptedException, IOException {
    ServiceContext originContext = ContextProvider.getContext();
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    final boolean[] continueLoop = { true };
    final ServiceContext[] uiContext = new ServiceContext[ 1 ];
    Runnable runnable = new Runnable() {
      public void run() {
        while( continueLoop[ 0 ] ) {
          IUIThreadHolder uiThread = ( IUIThreadHolder )Thread.currentThread();
          uiThread.updateServiceContext();
          uiContext[ 0 ] = ContextProvider.getContext();
          log.append( "executedInUIThread" );
          try {
            uiThread.switchThread();
          } catch( InterruptedException e ) {
            e.printStackTrace();
          }
        }
      }
    };
    lifeCycle.uiRunnable = runnable;
    // simulates first request
    lifeCycle.executeUIThread();
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
    assertSame( secondContext, uiContext[ 0 ] );
    assertEquals( "executedInUIThread", log.toString() );
    assertTrue( getUIThread().isAlive() );
    // simulates request that ends event loop
    UIThread endingUIThread = getUIThread();
    continueLoop[ 0 ] = false;
    lifeCycle.executeUIThread();
    assertFalse( endingUIThread.isAlive() );
    assertNull( getUIThread() );
    // clean up
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( originContext );
  }

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

  public void testUIRunnable() throws InterruptedException {
    EntryPointManager.register( EntryPointManager.DEFAULT, MainStartup.class );
    final RWTLifeCycle lifeCycle
      = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute() throws IOException {
          return null;
        }
        public PhaseId getPhaseID() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      }
    } );
    lifeCycle.addPhaseListener( new LoggingPhaseListener() );
    UIThread thread = new UIThread( lifeCycle.uiRunnable );
    thread.setServiceContext( ContextProvider.getContext() );
    thread.start();
    thread.switchThread();

    String expected = "before"
                    + PhaseId.PREPARE_UI_ROOT
                    + "createUI"
                    + "after"
                    + PhaseId.PREPARE_UI_ROOT;
    assertEquals( expected, log.toString() );
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testSleep() throws Exception {
    final RWTLifeCycle lifeCycle 
      = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    final ServiceContext[] uiContext = { null };
    lifeCycle.addPhaseListener( new LoggingPhaseListener() );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute() throws IOException {
          return null;
        }
        public PhaseId getPhaseID() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      }
    } );
    Runnable runnable = new Runnable() {
      public void run() {
        try {
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
          e.printStackTrace();
        }
      }
    };
    UIThread uiThread = new UIThread( runnable );
    ISessionStore session = ContextProvider.getSession();
    session.setAttribute( RWTLifeCycle.UI_THREAD, uiThread );

    uiThread.setServiceContext( ContextProvider.getContext() );
    uiThread.start();
    uiThread.switchThread();

    String expected = "after" + PhaseId.PREPARE_UI_ROOT;
    assertEquals( expected, log.toString() );

    log.setLength( 0 );
    ServiceContext expectedContext = newContext();
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( expectedContext );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IPhase() {
        public PhaseId execute() throws IOException {
          log.append( "prepare" );
          return null;
        }
        public PhaseId getPhaseID() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      },
      new IInterruptible() {
        public PhaseId execute() throws IOException {
          return null;
        }
        public PhaseId getPhaseID() {
          return PhaseId.PROCESS_ACTION;
        }
      }
    } );
    uiThread.setServiceContext( expectedContext );
    uiThread.switchThread();

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
        public PhaseId execute() throws IOException {
          return null;
        }
        public PhaseId getPhaseID() {
          return PhaseId.PROCESS_ACTION;
        }
      }
    } );
    uiThread.switchThread();
    expected = "before"
             + PhaseId.PROCESS_ACTION
             + "readAndDispatch";
    assertEquals( expected, log.toString() );
    assertFalse( uiThread.isAlive() );
  }

  public void testGetSetPhaseOrder() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    IPhase[] phaseOrder = new IPhase[ 0 ];
    lifeCycle.setPhaseOrder( phaseOrder );
    assertSame( phaseOrder, lifeCycle.getPhaseOrder() );
    // create new context to ensure that phase order is stored in context
    ServiceContext bufferedContext = ContextProvider.getContext();
    ContextProvider.releaseContextHolder();
    RWTFixture.fakeContext();
    ContextProvider.getContext().setStateInfo( new ServiceStateInfo() );
    assertNull( lifeCycle.getPhaseOrder() );
    // clean up
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( bufferedContext );
  }

  public void testErrorHandlingInCreateUI() throws IOException {
    EntryPointManager.register( EntryPointManager.DEFAULT, ErrorStartup.class );
    try {
      ( ( RWTLifeCycle )LifeCycleFactory.getLifeCycle() ).execute();
      fail();
    } catch( final RuntimeException re ) {
      assertEquals( ERR_MSG, re.getMessage() );
    }
  }

  public void testSessionInvalidateWithRunningEventLoop() throws Exception {
    final ISessionStore session = ContextProvider.getSession();
    final String[] invalidateThreadName = { null };
    final boolean hasContext[] = new boolean[]{ false };
    final IServiceStateInfo stateInfo[] =  { null };
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( final SessionStoreEvent event ) {
        invalidateThreadName[ 0 ] = Thread.currentThread().getName();
        hasContext[ 0 ] = ContextProvider.hasContext();
        stateInfo[ 0 ] = ContextProvider.getStateInfo();
      }
    } );
    // Register and 'run' entry point with readAndDispatch/sleep loop
    Class entryPointClass = SessionInvalidateWithEventLoopEntryPoint.class;
    EntryPointManager.register( EntryPointManager.DEFAULT, entryPointClass );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.execute();
    // Store some values for later comparison
    IUIThreadHolder uiThreadHolder
      = ( IUIThreadHolder )session.getAttribute( RWTLifeCycle.UI_THREAD );
    String uiThreadName = uiThreadHolder.getThread().getName();
    // Invalidate session
    invalidateSession( session );
    //
    assertFalse( uiThreadHolder.getThread().isAlive() );
    assertFalse( session.isBound() );
    assertEquals( invalidateThreadName[ 0 ], uiThreadName );
    assertTrue( hasContext[ 0 ] );
    assertNotNull( stateInfo[ 0 ] );
    assertEquals( "", log.toString() );
  }
  
  public void testExceptionInRender() throws Exception {
    Fixture.fakeRequestParam( RequestParams.STARTUP,
                              EntryPointManager.DEFAULT );
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Class entryPointClass = ExceptionInRenderEntryPoint.class;
    EntryPointManager.register( EntryPointManager.DEFAULT, entryPointClass );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    try {
      lifeCycle.execute();
      // wait for UI thread to terminate
      synchronized( getUIThread().getLock() ) {
        getUIThread().join( 5000 );
      }
      fail( "Exception in render must be re-thrown by life cycle" );
    } catch( Throwable e ) {
      assertEquals( e.getMessage(), EXCEPTION_IN_RENDER );
    }
  }

  // TODO [rh] bring back to life, once bug #219465 is closed
  //      see https://bugs.eclipse.org/bugs/show_bug.cgi?id=219465
//  public void testSessionInvalidateWithoutRunningEventLoop() throws Exception {
//    final ISessionStore session = ContextProvider.getSession();
//    final String[] uiThreadName = { "unknown-ui-thread" };
//    final String[] invalidateThreadName = { "unkown-invalidate-thread" };
//    final boolean hasContext[] = new boolean[]{ false };
//    final IServiceStateInfo stateInfo[] =  { null };
//    session.addSessionStoreListener( new SessionStoreListener() {
//      public void beforeDestroy( final SessionStoreEvent event ) {
//        invalidateThreadName[ 0 ] = Thread.currentThread().getName();
//        hasContext[ 0 ] = ContextProvider.hasContext();
//        stateInfo[ 0 ] = ContextProvider.getStateInfo();
//      }
//    } );
//    // Register and 'run' entry point with readAndDispatch/sleep loop
//    Class entryPointClass = SessionInvalidateWithoutEventLoopEntryPoint.class;
//    EntryPointManager.register( EntryPointManager.DEFAULT, entryPointClass );
//    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
//    lifeCycle.addPhaseListener( new PhaseListener() {
//      private static final long serialVersionUID = 1L;
//      public void beforePhase( final PhaseEvent event ) {
//        uiThreadName[ 0 ] = Thread.currentThread().getName();
//      }
//      public void afterPhase( final PhaseEvent event ) {
//      }
//      public PhaseId getPhaseId() {
//        return PhaseId.PREPARE_UI_ROOT;
//      }
//    } );
//    lifeCycle.execute();
//    // Invalidate session
//    invalidateSession( session );
//    //
//    assertFalse( session.isBound() );
//    assertEquals( uiThreadName[ 0 ], invalidateThreadName[ 0 ] );
//    assertTrue( hasContext[ 0 ] );
//    assertNotNull( stateInfo[ 0 ] );
//  }
  
  private static void invalidateSession( final ISessionStore session ) 
    throws InterruptedException 
  {
    Thread serverThread = new Thread( new Runnable() {
      public void run() {
        session.getHttpSession().invalidate();
      }
    }, "SessionInvalidateThread" );
    serverThread.start();
    serverThread.join();
  }

  private static ServiceContext newContext() {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpServletResponse response = ContextProvider.getResponse();
    ServiceContext result = new ServiceContext( request, response );
    result.setStateInfo( new ServiceStateInfo() );
    return result;
  }

  private static UIThread getUIThread() {
    ISessionStore session = ContextProvider.getSession();
    return ( UIThread )session.getAttribute( RWTLifeCycle.UI_THREAD );
  }


  protected void setUp() throws Exception {
    bufferedSystemErr = System.err;
    capturedSystemErr = new ByteArrayOutputStream();
    System.setErr( new PrintStream( capturedSystemErr ) );
    log.setLength( 0 );
    RWTFixture.setUp();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    System.setErr( bufferedSystemErr );
  }
}
