/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.IApplicationStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;


public class RWT_Test extends TestCase {
  
  private static class TestLifeCycle extends LifeCycle {
    static final String REQUEST_THREAD_EXEC = "requestThreadExec";
    
    private String invocationLog = "";

    public void execute() throws IOException {
    }

    public void requestThreadExec( Runnable runnable ) {
      invocationLog += REQUEST_THREAD_EXEC;
    }

    public void addPhaseListener( PhaseListener phaseListener ) {
    }

    public void removePhaseListener( PhaseListener phaseListener ) {
    }
    
    public void sleep() {
    }
    
    String getInvocationLog() {
      return invocationLog;
    }
  }

  public void testGetApplicationStore() {
    IApplicationStore applicationStore = RWT.getApplicationStore();
  
    assertSame( applicationStore, RWTFactory.getApplicationStore() );
  }

  public void testRequestThreadExecFromBackgroundThread() throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.requestThreadExec( new NoOpRunnable() );
      }
    };
    try {
      Fixture.runInThread( runnable );
      fail();
    } catch( Exception expected ) {
      assertTrue( expected instanceof SWTException );
      SWTException swtException = ( SWTException )expected;
      assertEquals( SWT.ERROR_THREAD_INVALID_ACCESS, swtException.code );
    }
  }
  
  public void testRequestThreadExec() {
    final Thread[] requestThread = { null };
    Display display = new Display();
    // use asyncExec to run code during executeLifeCycleFromServerThread
    display.asyncExec( new Runnable() { 
      public void run() {
        RWT.requestThreadExec( new Runnable() {
          public void run() {
            requestThread[ 0 ] = Thread.currentThread();
          }
        } );
      }
    } );
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    assertNotNull( requestThread[ 0 ] );
  }

  public void testRequestThreadExecWithoutDisplay() {
    Runnable runnable = new NoOpRunnable();
    try {
      RWT.requestThreadExec( runnable );
      fail();
    } catch( SWTException expected ) {
      assertEquals( SWT.ERROR_THREAD_INVALID_ACCESS, expected.code );
    }
  }
  
  public void testRequestThreadExecWithDisposedDisplay() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    display.dispose();
    Runnable runnable = new NoOpRunnable();
    try {
      RWT.requestThreadExec( runnable );
      fail();
    } catch( SWTException expected ) {
      assertEquals( SWT.ERROR_DEVICE_DISPOSED, expected.code );
    }
  }
  
  public void testRequestThreadExecWithNullRunnable() {
    new Display();
    try {
      RWT.requestThreadExec( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRequestThreadExecDelegatesToLifeCycle() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RWTFactory.getLifeCycleFactory().configure( TestLifeCycle.class );
    RWTFactory.getLifeCycleFactory().activate();
    new Display();
    
    RWT.requestThreadExec( new NoOpRunnable() );
    
    TestLifeCycle lifeCycle = ( TestLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    assertEquals( TestLifeCycle.REQUEST_THREAD_EXEC, lifeCycle.getInvocationLog() );
  }
  
  public void testGetRequestFromBackgroundThread() throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.getRequest();
      }
    };
    
    try {
      Fixture.runInThread( runnable );
      fail();
    } catch( SWTException expected ) {
    }
  }
  
  public void testGetResponseFromBackgroundThread() throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.getResponse();
      }
    };
    
    try {
      Fixture.runInThread( runnable );
      fail();
    } catch( SWTException expected ) {
    }
  }
  
  public void testGetServiceStoreFromBackgroundThread() throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.getServiceStore();
      }
    };
    
    try {
      Fixture.runInThread( runnable );
      fail();
    } catch( SWTException expected ) {
    }
  }
  
  public void testGetServiceStoreFromSessionThread() throws Throwable {
    final Display display = new Display();
    final Runnable runnable = new Runnable() {
      public void run() {
        RWT.getServiceStore();
      }
    };
    
    try {
      Fixture.runInThread( new Runnable() {
        public void run() {
          UICallBack.runNonUIThreadWithFakeContext( display, runnable );
        }
      } );
      fail();
    } catch( SWTException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
