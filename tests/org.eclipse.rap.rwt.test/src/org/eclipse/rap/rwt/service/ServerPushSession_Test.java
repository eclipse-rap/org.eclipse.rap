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
package org.eclipse.rap.rwt.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServerPushSession_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
    new Display();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreate_failsFromBackgroundThread() throws Throwable {
    try {
      Fixture.runInThread( new Runnable() {
        public void run() {
          new ServerPushSession();
        }
      } );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @Test
  public void testStart_failsFromBackgroundThread() throws Throwable {
    final ServerPushSession pushSession = new ServerPushSession();

    try {
      Fixture.runInThread( new Runnable() {
        public void run() {
          pushSession.start();
        }
      } );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @Test
  public void testStart_failsFromBackgroundThreadWithContext() throws Throwable {
    final UISession uiSession = ContextProvider.getUISession();
    final ServerPushSession pushSession = new ServerPushSession();

    try {
      Fixture.runInThread( new Runnable() {
        public void run() {
          uiSession.exec( new Runnable() {
            public void run() {
              pushSession.start();
            }
          } );
        }
      } );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @Test
  public void testStart_failsFromAnotherUIThread() throws Throwable {
    final ServerPushSession pushSession = new ServerPushSession();

    try {
      Fixture.runInThread( new Runnable() {
        public void run() {
          Fixture.createServiceContext();
          Fixture.fakePhase( PhaseId.PROCESS_ACTION );
          new Display();
          pushSession.start();
        }
      } );
      fail();
    } catch( IllegalStateException exception ) {
      assertEquals( "Invalid thread access", exception.getMessage() );
    }
  }

  @Test
  public void testStart_succeedsFromUIThread() {
    new ServerPushSession().start();

    assertTrue( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testStart_canBeCalledTwice() {
    ServerPushSession pushSession = new ServerPushSession();
    pushSession.start();
    pushSession.start();

    assertTrue( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testStart_isReentrant() {
    ServerPushSession pushSession = new ServerPushSession();
    pushSession.start();
    pushSession.start();
    pushSession.stop();

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testStop_succeedsFromBackgroundThread() throws Throwable {
    final ServerPushSession pushSession = new ServerPushSession();
    pushSession.start();

    Fixture.runInThread( new Runnable() {
      public void run() {
        pushSession.stop();
      }
    } );

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testStop_succeedsFromBackgroundThreadWithContext() throws Throwable {
    final UISession uiSession = ContextProvider.getUISession();
    final ServerPushSession pushSession = new ServerPushSession();
    pushSession.start();

    Fixture.runInThread( new Runnable() {
      public void run() {
        uiSession.exec( new Runnable() {
          public void run() {
            pushSession.stop();
          }
        } );
      }
    } );

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testStop_succeedsFromUIThread() {
    ServerPushSession pushSession = new ServerPushSession();
    pushSession.start();
    pushSession.stop();

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testStop_doesNothingIfNotStarted() {
    ServerPushSession pushSession = new ServerPushSession();
    pushSession.stop();
  }

  @Test
  public void testStop_canBeCalledTwice() {
    ServerPushSession pushSession = new ServerPushSession();
    pushSession.start();
    pushSession.stop();
    pushSession.stop();

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testAllowsRestart() {
    ServerPushSession pushSession = new ServerPushSession();
    pushSession.start();
    pushSession.stop();
    pushSession.start();

    assertTrue( ServerPushManager.getInstance().isServerPushActive() );
  }

}
