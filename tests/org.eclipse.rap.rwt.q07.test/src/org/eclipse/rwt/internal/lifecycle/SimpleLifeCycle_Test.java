/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.LoggingPhaseListener.PhaseEventInfo;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Display;


// TODO [rh] see if it is possible to move this test to org.eclipse.rwt.test
public class SimpleLifeCycle_Test extends TestCase {

  private static class ThreadRecordingPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;

    private final List threads;

    private ThreadRecordingPhaseListener() {
      this.threads = new LinkedList();
    }

    public void beforePhase( PhaseEvent event ) {
      threads.add( Display.getCurrent().getThread() );
    }

    public void afterPhase( PhaseEvent event ) {
      threads.add( Display.getCurrent().getThread() );
    }

    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }
    
    Thread[] getThreads() {
      Thread[] result = new Thread[ threads.size() ];
      threads.toArray( result );
      return result;
    }
  }

  private static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      new Display();
      return 0;
    }
  }

  private LifeCycle lifeCycle;

  public void testGetScope() {
    assertEquals( Scope.APPLICATION, lifeCycle.getScope() );
  }
  
  public void testPhaseOrderForInitialRequest() throws Exception {
    Fixture.fakeRequestParam( RequestParams.STARTUP, EntryPointManager.DEFAULT );
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

  public void testListenersFromPhaseListenerRegistryAreExecuted() throws Exception {
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    RWTFactory.getPhaseListenerRegistry().add( phaseListener );
    SimpleLifeCycle lifeCycle = new SimpleLifeCycle();
    lifeCycle.execute();
    assertTrue( phaseListener.getLoggedEvents().length > 0 );
  }
  
  public void testThreadIsAttachedInInitialRequest() throws IOException {
    Fixture.fakeRequestParam( RequestParams.STARTUP, EntryPointManager.DEFAULT );
    ThreadRecordingPhaseListener phaseListener = new ThreadRecordingPhaseListener( );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    Thread[] threads = phaseListener.getThreads();
    for( int i = 0; i < threads.length; i++ ) {
      assertSame( Thread.currentThread(), threads[ i ] );
    }
  }
  
  public void testThreadIsDetachedInInitialRequest() throws IOException {
    Fixture.fakeRequestParam( RequestParams.STARTUP, EntryPointManager.DEFAULT );
    lifeCycle.execute();
    assertNull( Display.getCurrent() );
    assertNull( LifeCycleUtil.getSessionDisplay().getThread() );
  }
  
  public void testThreadIsAttachedInSubsequentRequest() throws IOException {
    Fixture.fakeRequestParam( RequestParams.STARTUP, EntryPointManager.DEFAULT );
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

  public void testThreadIsDetachedInSubsequentRequest() throws IOException {
    Fixture.fakeRequestParam( RequestParams.STARTUP, EntryPointManager.DEFAULT );
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    lifeCycle.execute();
    assertNull( Display.getCurrent() );
    assertNull( LifeCycleUtil.getSessionDisplay().getThread() );
  }
  
  public void testPhaseListenersHaveApplicationScope() throws Exception {
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    newSession();
    lifeCycle.execute();
    assertTrue( phaseListener.getLoggedEvents().length > 0 );
  }
  
  public void testAddPhaseListener() throws Exception {
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    assertTrue( phaseListener.getLoggedEvents().length > 0 );
  }
  
  public void testRemovePhaseListener() throws Exception {
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.removePhaseListener( phaseListener );
    lifeCycle.execute();
    assertEquals( 0, phaseListener.getLoggedEvents().length );
  }
  
  public void testRequestThreadExecRunsRunnableOnCallingThread() {
    final Thread[] invocationThread = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        invocationThread[ 0 ] = Thread.currentThread();
      }
    };
    
    lifeCycle.requestThreadExec( runnable );
    
    assertSame( Thread.currentThread(), invocationThread[ 0 ] );
  }
  
  public void testGetUIThreadWhileLifeCycleInExecute() throws IOException {
    final Thread[] uiThread = { null };
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
      public void beforePhase( PhaseEvent event ) {
      }
      public void afterPhase( PhaseEvent event ) {
        uiThread[ 0 ] = LifeCycleUtil.getUIThread( ContextProvider.getSession() ).getThread();
      }
    } );
    
    lifeCycle.execute();
    
    assertSame( Thread.currentThread(), uiThread[ 0 ] );
  }
  
  public void testGetUIThreadAfterLifeCycleExecuted() throws IOException {
    lifeCycle.execute();
    
    IUIThreadHolder threadHolder = LifeCycleUtil.getUIThread( ContextProvider.getSession() );

    assertNull( threadHolder );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, TestEntryPoint.class );
    lifeCycle = new SimpleLifeCycle();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
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
  
  private static void newSession() {
    ContextProvider.disposeContext();
    Fixture.createServiceContext();
  }
}
