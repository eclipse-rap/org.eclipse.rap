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

import org.eclipse.rwt.LoggingPhaseListener;
import org.eclipse.rwt.LoggingPhaseListener.PhaseEventInfo;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Display;


public class PhaseExecutor_Test extends TestCase {

  private static class TestPhaseExecutor extends PhaseExecutor {

    private TestPhaseExecutor( PhaseListenerManager phaseListenerManager, IPhase[] phases ) {
      super( phaseListenerManager, phases );
    }

    Display getDisplay() {
      return null;
    }
  }

  private static class TestLifeCycle implements ILifeCycle {
    public void removePhaseListener( PhaseListener listener ) {
    }
    public void addPhaseListener( PhaseListener listener ) {
    }
  }
  
  private static class TestPhase implements IPhase {

    private final List<IPhase> executionLog;
    private final PhaseId phaseId;
    private final PhaseId nextPhaseId;
    
    TestPhase( List<IPhase> executionLog, PhaseId phaseId, PhaseId nextPhaseId ) {
      this.executionLog = executionLog;
      this.phaseId = phaseId;
      this.nextPhaseId = nextPhaseId;
    }

    public PhaseId getPhaseId() {
      return phaseId;
    }

    public PhaseId execute(Display display) throws IOException {
      executionLog.add( this );
      return nextPhaseId;
    }
  }

  private PhaseListenerManager phaseListenerManager;
  private TestLifeCycle lifeCycle;

  public void testExecute() throws IOException {
    List<IPhase> executionLog = new LinkedList<IPhase>();
    IPhase[] phases = new IPhase[] { 
      new TestPhase( executionLog, PhaseId.PREPARE_UI_ROOT, PhaseId.RENDER ), 
      new TestPhase( executionLog, PhaseId.RENDER, null ) 
    };
    PhaseExecutor phaseExecutor = new TestPhaseExecutor( phaseListenerManager, phases );
    phaseExecutor.execute( PhaseId.PREPARE_UI_ROOT );
    assertEquals( 2, executionLog.size() );
    assertSame( phases[ 0 ], executionLog.get( 0 ) );
    assertSame( phases[ 1 ], executionLog.get( 1 ) );
  }
  
  public void testExecuteNotifiesPhaseListener() throws IOException {
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    phaseListenerManager.addPhaseListener( phaseListener );
    IPhase[] phases = new IPhase[] { 
      new TestPhase( new LinkedList<IPhase>(), PhaseId.PREPARE_UI_ROOT, null ), 
    };
    PhaseExecutor phaseExecutor = new TestPhaseExecutor( phaseListenerManager, phases );
    phaseExecutor.execute( PhaseId.PREPARE_UI_ROOT );
    PhaseEventInfo[] loggedEvents = phaseListener.getLoggedEvents();
    assertEquals( 2, loggedEvents.length );
    PhaseEventInfo beforePrepareUIRoot = loggedEvents[ 0 ];
    assertTrue( beforePrepareUIRoot.before );
    assertEquals( PhaseId.PREPARE_UI_ROOT, beforePrepareUIRoot.phaseId );
    assertSame( lifeCycle, beforePrepareUIRoot.source );
    PhaseEventInfo afterPrepareUIRoot = loggedEvents[ 1 ];
    assertFalse( afterPrepareUIRoot.before );
    assertEquals( PhaseId.PREPARE_UI_ROOT, afterPrepareUIRoot.phaseId );
    assertSame( lifeCycle, afterPrepareUIRoot.source );
  }

  protected void setUp() throws Exception {
    lifeCycle = new TestLifeCycle();
    phaseListenerManager = new PhaseListenerManager( lifeCycle );
  }
}
