/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.IConfiguration;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rwt.lifecycle.PhaseListener;
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
    
    String getInvocationLog() {
      return invocationLog;
    }
  }

  private static class EmptyRunnable implements Runnable {
    public void run() {
    }
  }

  public void testGetApplicationStore() {
    Fixture.setUp();
    
    IApplicationStore applicationStore = RWT.getApplicationStore();
  
    assertSame( applicationStore, RWTFactory.getApplicationStore() );
  }

  public void testRequestThreadExecFromBackgroundThread() throws Throwable {
    Fixture.setUp();
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.requestThreadExec( new Runnable() {
          public void run() {
          }
        } );
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
    Fixture.setUp();
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
    Fixture.setUp();
    Runnable runnable = new EmptyRunnable();
    try {
      RWT.requestThreadExec( runnable );
      fail();
    } catch( SWTException expected ) {
      assertEquals( SWT.ERROR_THREAD_INVALID_ACCESS, expected.code );
    }
  }
  
  public void testRequestThreadExecWithDisposedDisplay() {
    Fixture.setUp();
    Display display = new Display();
    display.dispose();
    Runnable runnable = new EmptyRunnable();
    try {
      RWT.requestThreadExec( runnable );
      fail();
    } catch( SWTException expected ) {
      assertEquals( SWT.ERROR_DEVICE_DISPOSED, expected.code );
    }
  }
  
  public void testRequestThreadExecWithNullRunnable() {
    Fixture.setUp();
    new Display();
    try {
      RWT.requestThreadExec( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRequestThreadExecDelegatesToLifeCycle() {
    System.setProperty( IConfiguration.PARAM_LIFE_CYCLE, TestLifeCycle.class.getName() );
    Fixture.setUp();
    new Display();
    
    RWT.requestThreadExec( new EmptyRunnable() );
    
    TestLifeCycle lifeCycle = ( TestLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    assertEquals( TestLifeCycle.REQUEST_THREAD_EXEC, lifeCycle.getInvocationLog() );
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
    System.getProperties().remove( IConfiguration.PARAM_LIFE_CYCLE );
  }
}
