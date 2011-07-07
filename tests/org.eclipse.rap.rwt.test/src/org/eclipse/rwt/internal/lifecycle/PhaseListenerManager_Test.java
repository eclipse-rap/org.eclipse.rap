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

import java.util.*;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.LoggingPhaseListener.PhaseEventInfo;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;


public class PhaseListenerManager_Test extends TestCase {
  
  private static class TestError extends Error {
    private static final long serialVersionUID = 1L;
  }

  private static class TestLifeCycle implements ILifeCycle {
    public void removePhaseListener( PhaseListener listener ) {
    }
    public void addPhaseListener( PhaseListener listener ) {
    }
  }

  private static class EmptyPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
    public void beforePhase( PhaseEvent event ) {
    }
    public void afterPhase( PhaseEvent event ) {
    }
    public PhaseId getPhaseId() {
      return null;
    }
  }

  private static class ExceptionPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
    public void beforePhase( PhaseEvent event ) {
      throw new RuntimeException();
    }
    public void afterPhase( PhaseEvent event ) {
      throw new RuntimeException();
    }
    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }
  }

  private static class ErrorPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
  
    public void beforePhase( PhaseEvent event ) {
      throw new TestError();
    }
  
    public void afterPhase( PhaseEvent event ) {
      throw new TestError();
    }
  
    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }
  }


  private List<Throwable> exceptionsInServletLog;
  private PhaseListenerManager phaseListenerManager;
  private ILifeCycle lifeCycle;
  
  public void testAddPhaseListenerWithNullArgument() {
    try {
      phaseListenerManager.addPhaseListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testAddPhaseListener() {
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( null );
    phaseListenerManager.addPhaseListener( phaseListener );
    PhaseListener[] phaseListeners = phaseListenerManager.getPhaseListeners();
    assertEquals( 1, phaseListeners.length );
    assertSame( phaseListener, phaseListeners[ 0 ] );
  }

  public void testAddPhaseListenerTwice() {
    PhaseListener phaseListener = new EmptyPhaseListener();
    phaseListenerManager.addPhaseListener( phaseListener );
    phaseListenerManager.addPhaseListener( phaseListener );
    PhaseListener[] phaseListeners = phaseListenerManager.getPhaseListeners();
    assertEquals( 1, phaseListeners.length );
  }
  
  public void testAddPhaseListeners() {
    PhaseListener phaseListener1 = new EmptyPhaseListener();
    PhaseListener phaseListener2 = new EmptyPhaseListener();
    PhaseListener[] phaseListeners = new PhaseListener[] { phaseListener1, phaseListener2 };
    phaseListenerManager.addPhaseListeners( phaseListeners );
    PhaseListener[] returnedPhaseListeners = phaseListenerManager.getPhaseListeners();
    assertEquals( 2, returnedPhaseListeners.length );
  }
  
  public void testAddPhaseListenersWithNullArgument() {
    try {
      phaseListenerManager.addPhaseListeners( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRemovePhaseListenerWithNullArgument() {
    try {
      phaseListenerManager.removePhaseListener( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRemovePhaseListener() {
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( null );
    phaseListenerManager.addPhaseListener( phaseListener );
    phaseListenerManager.removePhaseListener( phaseListener );
    PhaseListener[] phaseListeners = phaseListenerManager.getPhaseListeners();
    assertEquals( 0, phaseListeners.length );
  }
  
  public void testRemovePhaseListenerTwice() {
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( null );
    phaseListenerManager.addPhaseListener( phaseListener );
    phaseListenerManager.removePhaseListener( phaseListener );
    phaseListenerManager.removePhaseListener( phaseListener );
    PhaseListener[] phaseListeners = phaseListenerManager.getPhaseListeners();
    assertEquals( 0, phaseListeners.length );
  }
  
  public void testRemovePhaseListenerWithUnknownPhaseListener() {
    phaseListenerManager.addPhaseListener( new LoggingPhaseListener( null ) );
    phaseListenerManager.removePhaseListener( new LoggingPhaseListener( null ) );
    PhaseListener[] phaseListeners = phaseListenerManager.getPhaseListeners();
    assertEquals( 1, phaseListeners.length );
  }
  
  public void testGetPhaseListenersReturnsSafeCopy() {
    phaseListenerManager.addPhaseListener( new LoggingPhaseListener( null ) );
    PhaseListener[] phaseListeners1 = phaseListenerManager.getPhaseListeners();
    phaseListeners1[ 0 ] = null;
    PhaseListener[] phaseListeners2 = phaseListenerManager.getPhaseListeners();
    assertNotNull( phaseListeners2[ 0 ] );
  }
  
  public void testAddRemovePhaseListenerConcurently() throws InterruptedException {
    final int threadCount = 120;
    final List<Thread> succeededThreads = Collections.synchronizedList( new LinkedList<Thread>() );
    Runnable runnable = new Runnable() {
      public void run() {
        EmptyPhaseListener phaseListener = new EmptyPhaseListener();
        phaseListenerManager.addPhaseListener( phaseListener );
        phaseListenerManager.getPhaseListeners();
        Thread.yield();
        phaseListenerManager.removePhaseListener( phaseListener );
        succeededThreads.add( Thread.currentThread() );
      }
    };
    Thread[] threads = Fixture.startThreads( threadCount, runnable );
    Fixture.joinThreads( threads );
    assertEquals( threadCount, succeededThreads.size() );
    assertEquals( 0, phaseListenerManager.getPhaseListeners().length );
  }

  public void testNotifyBeforePhaseWithSpecificListener() {
    PhaseId phase = PhaseId.READ_DATA;
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( phase );
    phaseListenerManager.addPhaseListener( phaseListener );
    phaseListenerManager.notifyBeforePhase( phase );
    assertEquals( 1, phaseListener.getLoggedEvents().length );
    PhaseEventInfo phaseEvent = phaseListener.getLoggedEvents()[ 0 ];
    assertSame( lifeCycle, phaseEvent.source ); 
    assertEquals( phase, phaseEvent.phaseId );
  }
  
  public void testNotifyBeforePhaseWithNonMatchingListener() {
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.RENDER );
    phaseListenerManager.addPhaseListener( phaseListener );
    phaseListenerManager.notifyBeforePhase( PhaseId.READ_DATA );
    assertEquals( 0, phaseListener.getLoggedEvents().length );
  }
  
  public void testNotifyBeforePhaseWithANYListener() {
    PhaseId phase = PhaseId.READ_DATA;
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    phaseListenerManager.addPhaseListener( phaseListener );
    phaseListenerManager.notifyBeforePhase( phase );
    assertEquals( 1, phaseListener.getLoggedEvents().length );
    PhaseEventInfo phaseEvent = phaseListener.getLoggedEvents()[ 0 ];
    assertSame( lifeCycle, phaseEvent.source ); 
    assertEquals( phase, phaseEvent.phaseId );
  }
  
  public void testExceptionsInBeforePhaseEvent() {
    phaseListenerManager.addPhaseListener( new ExceptionPhaseListener() );
    phaseListenerManager.addPhaseListener( new ExceptionPhaseListener() );
    phaseListenerManager.notifyBeforePhase( PhaseId.READ_DATA );
    assertEquals( 2, exceptionsInServletLog.size() );
  }

  public void testErrorInBeforePhaseEvent() {
    phaseListenerManager.addPhaseListener( new ErrorPhaseListener() );
    try {
      phaseListenerManager.notifyBeforePhase( PhaseId.READ_DATA );
      fail();
    } catch( TestError expected ) {
    }
  }

  public void testNotifyAfterPhaseWithSpecificListener() {
    PhaseId phase = PhaseId.READ_DATA;
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( phase );
    phaseListenerManager.addPhaseListener( phaseListener );
    phaseListenerManager.notifyAfterPhase( phase );
    assertEquals( 1, phaseListener.getLoggedEvents().length );
    assertSame( lifeCycle, phaseListener.getLoggedEvents()[ 0 ].source ); 
    assertEquals( phase, phaseListener.getLoggedEvents()[ 0 ].phaseId );
  }
  
  public void testNotifyAfterPhaseWithNonMatchingListener() {
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.RENDER );
    phaseListenerManager.addPhaseListener( phaseListener );
    phaseListenerManager.notifyAfterPhase( PhaseId.READ_DATA );
    assertEquals( 0, phaseListener.getLoggedEvents().length );
  }
  
  public void testNotifyAfterPhaseWithANYListener() {
    PhaseId phase = PhaseId.READ_DATA;
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    phaseListenerManager.addPhaseListener( phaseListener );
    phaseListenerManager.notifyAfterPhase( phase );
    assertEquals( 1, phaseListener.getLoggedEvents().length );
    PhaseEventInfo phaseEvent = phaseListener.getLoggedEvents()[ 0 ];
    assertSame( lifeCycle, phaseEvent.source ); 
    assertEquals( phase, phaseEvent.phaseId );
  }
  
  public void testExceptionsInAfterPhaseEvent() {
    phaseListenerManager.addPhaseListener( new ExceptionPhaseListener() );
    phaseListenerManager.addPhaseListener( new ExceptionPhaseListener() );
    phaseListenerManager.notifyAfterPhase( PhaseId.READ_DATA );
    assertEquals( 2, exceptionsInServletLog.size() );
  }

  public void testErrorInAfterPhaseEvent() {
    phaseListenerManager.addPhaseListener( new ErrorPhaseListener() );
    try {
      phaseListenerManager.notifyAfterPhase( PhaseId.READ_DATA );
      fail();
    } catch( TestError expected ) {
    }
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    lifeCycle = new TestLifeCycle();
    phaseListenerManager = new PhaseListenerManager( lifeCycle );
    exceptionsInServletLog = new LinkedList<Throwable>();
    setupServletContextLog();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void setupServletContextLog() {
    HttpSession session = ContextProvider.getSession().getHttpSession();
    TestServletContext servletContext = ( TestServletContext )session.getServletContext();
    servletContext.setLogger( new TestLogger() {
      public void log( String message, Throwable throwable ) {
        exceptionsInServletLog.add( throwable );
      }
    } );
  }
}
