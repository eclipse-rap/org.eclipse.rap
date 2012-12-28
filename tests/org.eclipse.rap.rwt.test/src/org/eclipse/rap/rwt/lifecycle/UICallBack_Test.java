/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.lifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "deprecation" )
public class UICallBack_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testActivateFromBackgroundThread() {
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.getUISession( display ).exec( new Runnable() {
          public void run() {
            UICallBack.activate( "id" );
          }
        } );
      }
    };

    Throwable exception = runInThread( runnable );

    assertTrue( exception instanceof SWTException );
  }

  @Test
  public void testActivateWithNullArgument() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    try {
      UICallBack.activate( null );
      fail( "Must not allow null-id" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testDeactivateWithNullArgument() {
    try {
      UICallBack.deactivate( null );
      fail( "Must not allow null-id" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testActivateFromNonUIThread() {
    Runnable runnable = new Runnable() {
      public void run() {
        UICallBack.activate( "someId" );
      }
    };

    Throwable exception = runInThread( runnable );

    assertTrue( exception instanceof SWTException );
    assertIsThreadInvalidAccess( exception );
  }

  @Test
  public void testActivateFromNonUIThreadWithFakeContext() {
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.getUISession( display ).exec( new Runnable() {
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

  @Test
  public void testDeactivateFromNonUIThread() {
    Runnable runnable = new Runnable() {
      public void run() {
        UICallBack.deactivate( "someId" );
      }
    };

    Throwable exception = runInThread( runnable );

    assertTrue( exception instanceof SWTException );
    assertIsThreadInvalidAccess( exception );
  }

  @Test
  public void testDeativateFromNonUIThreadWithFakeContext() {
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.getUISession( display ).exec( new Runnable() {
          public void run() {
            UICallBack.deactivate( "someId" );
          }
        } );
      }
    };

    Throwable exception = runInThread( runnable );

    assertNull( exception );
  }

  @Test
  public void testRunNonUIThreadWithFakeContextWithNullDisplay() {
    try {
      UICallBack.runNonUIThreadWithFakeContext( null, new NoOpRunnable() );
      fail( "must not accept null-argument" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRunNonUIThreadWithFakeContextWithNullRunnable() {
    try {
      UICallBack.runNonUIThreadWithFakeContext( new Display(), null );
      fail( "must not accept null-argument" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  private static void assertIsThreadInvalidAccess( Throwable exception ) {
    SWTException swtException = ( SWTException )exception;
    assertEquals( SWT.ERROR_THREAD_INVALID_ACCESS, swtException.code );
  }

  private static Throwable runInThread( Runnable runnable ) {
    Throwable result = null;
    try {
      Fixture.runInThread( runnable );
    } catch( Throwable throwable ) {
      result = throwable;
    }
    return result;
  }

}
