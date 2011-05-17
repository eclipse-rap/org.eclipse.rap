/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;


public class UICallBack_Test extends TestCase {

  public void testActivateFromBackgroundThread() {
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
          public void run() {
            UICallBack.activate( "id" );
          }
        } );
      }
    };
    
    Throwable exception = runInThread( runnable );
    
    assertTrue( exception instanceof SWTException );
  }

  public void testActivateWithNullArgument() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    try {
      UICallBack.activate( null );
      fail( "Must not allow null-id" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testDeactivateWithNullArgument() {
    try {
      UICallBack.deactivate( null );
      fail( "Must not allow null-id" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testActivateFromNonUIThread() throws Exception {
    Runnable runnable = new Runnable() {
      public void run() {
        UICallBack.activate( "someId" );
      }
    };
    
    Throwable exception = runInThread( runnable );
    
    assertTrue( exception instanceof SWTException );
    assertIsThreadInvalidAccess( exception );
  }

  public void testActivateFromNonUIThreadWithFakeContext() throws Exception {
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
          public void run() {
            UICallBack.activate( "someId" );
          }
        } );
      }
    };
    
    Throwable exception = runInThread( runnable );
    
    assertTrue( exception instanceof SWTException );
    assertIsThreadInvalidAccess( exception );
  }
  
  public void testDeactivateFromNonUIThread() throws Exception {
    Runnable runnable = new Runnable() {
      public void run() {
        UICallBack.deactivate( "someId" );
      }
    };
    
    Throwable exception = runInThread( runnable );

    assertTrue( exception instanceof SWTException );
    assertIsThreadInvalidAccess( exception );
  }

  public void testDeativateFromNonUIThreadWithFakeContext() throws Exception {
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
          public void run() {
            UICallBack.deactivate( "someId" );
          }
        } );
      }
    };
    
    Throwable exception = runInThread( runnable );
    
    assertNull( exception );
  }
  
  public void testRunNonUIThreadWithFakeContextWithNullDisplay() {
    try {
      UICallBack.runNonUIThreadWithFakeContext( null, new Runnable() {
        public void run() {
        }
      } );
      fail( "must not accept null-argument" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testRunNonUIThreadWithFakeContextWithNullRunnable() {
    try {
      UICallBack.runNonUIThreadWithFakeContext( new Display(), null );
      fail( "must not accept null-argument" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private static void assertIsThreadInvalidAccess( Throwable exception ) {
    SWTException swtException = ( SWTException )exception;
    assertEquals( SWT.ERROR_THREAD_INVALID_ACCESS, swtException.code );
  }

  private static Throwable runInThread( final Runnable runnable ) {
    Throwable result = null;
    try {
      Fixture.runInThread( runnable );
    } catch( Throwable throwable ) {
      result = throwable;
    }
    return result;
  }
}
