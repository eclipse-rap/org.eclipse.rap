/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.SettingStore;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RWT_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRequestThreadExecFromBackgroundThread() throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.requestThreadExec( new NoOpRunnable() );
      }
    };
    try {
      Fixture.runInThread( runnable );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @Test
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
    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread();
    assertNotNull( requestThread[ 0 ] );
  }

  @Test
  public void testRequestThreadExecWithoutDisplay() {
    Runnable runnable = new NoOpRunnable();
    try {
      RWT.requestThreadExec( runnable );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @Test
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

  @Test
  public void testRequestThreadExecWithNullRunnable() {
    new Display();
    try {
      RWT.requestThreadExec( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testRequestThreadExecDelegatesToLifeCycle() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    LifeCycleFactory lifeCycleFactory = getApplicationContext().getLifeCycleFactory();
    lifeCycleFactory.configure( TestLifeCycle.class );
    lifeCycleFactory.activate();
    new Display();

    RWT.requestThreadExec( new NoOpRunnable() );

    TestLifeCycle lifeCycle = ( TestLifeCycle )lifeCycleFactory.getLifeCycle();
    assertEquals( TestLifeCycle.REQUEST_THREAD_EXEC, lifeCycle.getInvocationLog() );
  }

  @Test
  public void testGetRequestFromBackgroundThread() throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.getRequest();
      }
    };

    try {
      Fixture.runInThread( runnable );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @Test
  public void testGetResponseFromBackgroundThread() throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.getResponse();
      }
    };

    try {
      Fixture.runInThread( runnable );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void testGetServiceStore_failsFromBackgroundThread() throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.getServiceStore();
      }
    };

    try {
      Fixture.runInThread( runnable );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @SuppressWarnings( "deprecation" )
  @Test
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
          RWT.getUISession( display ).exec( runnable );
        }
      } );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @Test
  public void testGetApplicationContext() {
    ApplicationContext result = RWT.getApplicationContext();

    assertNotNull( result );
    assertSame( ContextProvider.getApplicationContext(), result );
  }

  @Test
  public void testGetSettingStore() {
    ApplicationContextImpl applicationContext = ContextProvider.getApplicationContext();

    SettingStore result = RWT.getSettingStore();

    assertNotNull( result );
    assertSame( applicationContext.getSettingStoreManager().getStore(), result );
  }

  @Test
  public void testGetApplicationContext_failsInBackgroundThread() throws Throwable {
    try {
      Fixture.runInThread( new Runnable() {
        public void run() {
          RWT.getApplicationContext();
        }
      } );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @Test
  public void testGetApplicationContext_succeedsInBackgroundThreadWithContext() throws Throwable {
    final AtomicReference<ApplicationContext> result = new AtomicReference<ApplicationContext>();
    ApplicationContext applicationContext = RWT.getApplicationContext();
    final UISession currentUISession = RWT.getUISession();

    Fixture.runInThread( new Runnable() {
      public void run() {
        Fixture.createServiceContext();
        ContextProvider.getContext().setUISession( currentUISession );
        result.set( RWT.getApplicationContext() );
      }
    } );

    assertSame( applicationContext, result.get() );
  }

  @Test
  public void testGetUISession() {
    UISession result = RWT.getUISession();

    assertSame( ContextProvider.getUISession(), result );
  }

  @Test
  public void testGetUISession_failsInBackgroundThread() throws Throwable {
    try {
      Fixture.runInThread( new Runnable() {
        public void run() {
          RWT.getUISession();
        }
      } );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @Test
  public void testGetUISession_succeedsInBackgroundThreadWithContext() throws Throwable {
    final AtomicReference<UISession> result = new AtomicReference<UISession>();
    final UISession currentUISession = RWT.getUISession();

    Fixture.runInThread( new Runnable() {
      public void run() {
        Fixture.createServiceContext();
        ContextProvider.getContext().setUISession( currentUISession );
        result.set( RWT.getUISession() );
      }
    } );

    assertSame( currentUISession, result.get() );
  }

  @Test
  public void testGetUISessionForDisplay() {
    Display display = new Display();

    UISession result = RWT.getUISession( display );

    assertSame( RWT.getUISession(), result );
  }

  @Test
  public void testGetUISessionForDisplay_failsWithNullArgument() {
    try {
      RWT.getUISession( null );
      fail();
    } catch( NullPointerException exception ) {
      assertTrue( exception.getMessage().contains( "display" ) );
    }
  }

  @Test
  public void testGetUISessionForDisplay_fromBackgroundThread() throws Throwable {
    final AtomicReference<UISession> result = new AtomicReference<UISession>();
    final Display display = new Display();

    Fixture.runInThread( new Runnable() {
      public void run() {
        result.set( RWT.getUISession( display ) );
      }
    } );

    assertSame( RWT.getUISession(), result.get() );
  }

  @Test
  public void testGetUISessionForDisplay_alsoWorksWhenDisplayIsDisposed() {
    final Display display = new Display();
    display.dispose();

    UISession result = RWT.getUISession( display );

    assertSame( RWT.getUISession(), result );
  }

  @Test
  public void testGetClient() {
    Client client = RWT.getClient();

    assertNotNull( client );
  }

  @Test
  public void testGetLocale_getsLocaleFromUISession() {
    ContextProvider.getUISession().setLocale( Locale.ITALY );

    Locale result = RWT.getLocale();

    assertSame( Locale.ITALY, result );
  }

  @Test
  public void testSetLocale_setsLocaleOnUISession() {
    RWT.setLocale( Locale.ITALY );

    Locale result = ContextProvider.getUISession().getLocale();

    assertSame( Locale.ITALY, result );
  }

  private static class TestLifeCycle extends LifeCycle {
    static final String REQUEST_THREAD_EXEC = "requestThreadExec";

    private String invocationLog = "";

    public TestLifeCycle( ApplicationContextImpl applicationContext ) {
      super( applicationContext );
    }

    @Override
    public void execute() throws IOException {
    }

    @Override
    public void requestThreadExec( Runnable runnable ) {
      invocationLog += REQUEST_THREAD_EXEC;
    }

    @Override
    public void addPhaseListener( PhaseListener phaseListener ) {
    }

    @Override
    public void removePhaseListener( PhaseListener phaseListener ) {
    }

    @Override
    public void sleep() {
    }

    String getInvocationLog() {
      return invocationLog;
    }
  }

}
