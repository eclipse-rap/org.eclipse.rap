/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TimerExecTask_Test {

  private TimerExecScheduler scheduler;
  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = spy( new Display() );
    scheduler = spy( new TimerExecScheduler( display ) );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreation() {
    Runnable runnable = mock( Runnable.class );

    TimerExecTask task = new TimerExecTask( scheduler, runnable );

    assertEquals( runnable, task.getRunnable() );
  }

  @Test
  public void testCreation_activatesServerPush() {
    Runnable runnable = mock( Runnable.class );

    new TimerExecTask( scheduler, runnable );

    assertTrue( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testRun_removesIselfFromScheduler() {
    Runnable runnable = mock( Runnable.class );
    TimerExecTask task = new TimerExecTask( scheduler, runnable );

    task.run();

    verify( scheduler ).removeTask( same( task ) );
  }

  @Test
  public void testRun_addsRunnableToQueue() {
    Runnable runnable = mock( Runnable.class );
    TimerExecTask task = new TimerExecTask( scheduler, runnable );

    task.run();

    verify( display ).asyncExec( same( runnable ) );
  }

  @Test
  public void testRun_doesNotAddRunnableWhenDisplayDisposed() {
    // Ensure that runnables that were added via timerExec are *not* executed on session shutdown
    Runnable runnable = mock( Runnable.class );
    TimerExecTask task = new TimerExecTask( scheduler, runnable );
    display.dispose();

    task.run();

    verify( display, times( 0 ) ).asyncExec( any( Runnable.class ) );
  }

  @Test
  public void testRun_deactivatesServerPush() {
    Runnable runnable = mock( Runnable.class );
    TimerExecTask task = new TimerExecTask( scheduler, runnable );

    task.run();

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testCancel_deactivatesServerPush() {
    Runnable runnable = mock( Runnable.class );
    TimerExecTask task = new TimerExecTask( scheduler, runnable );

    task.cancel();

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

}
