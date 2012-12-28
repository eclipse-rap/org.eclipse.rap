/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Timer;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class TimerExecScheduler_Test {

  private TimerExecScheduler scheduler;
  private Display display;
  private Collection<Throwable> exceptions;
  private Timer timer;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    timer = mock( Timer.class );
    scheduler = new TimerExecScheduler( display ) {
      @Override
      Timer createTimer() {
        return timer;
      }
      @Override
      TimerExecTask createTask( Runnable runnable ) {
        TimerExecTask task = mock( TimerExecTask.class );
        when( task.getRunnable() ).thenReturn( runnable );
        return task;
      }
    };
    exceptions = Collections.synchronizedList( new LinkedList<Throwable>() );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testSchedule_schedulesRunnable() {
    Runnable runnable = mock( Runnable.class );

    scheduler.schedule( 23, runnable );

    ArgumentCaptor<TimerExecTask> taskCaptor = ArgumentCaptor.forClass( TimerExecTask.class );
    verify( timer ).schedule( taskCaptor.capture(), eq( 23L ) );
    assertSame( runnable, taskCaptor.getValue().getRunnable() );
  }

  @Test
  public void testSchedule_reschedulesSameRunnable() {
    Runnable runnable = mock( Runnable.class );

    scheduler.schedule( 23, runnable );
    scheduler.schedule( 42, runnable );

    ArgumentCaptor<TimerExecTask> taskCaptor = ArgumentCaptor.forClass( TimerExecTask.class );
    verify( timer ).schedule( taskCaptor.capture(), eq( 23L ) );
    verify( timer ).schedule( taskCaptor.capture(), eq( 42L ) );
    assertSame( taskCaptor.getAllValues().get( 0 ), taskCaptor.getAllValues().get( 1 ) );
  }

  @Test
  public void testCancel_cancelsTask() {
    Runnable runnable = mock( Runnable.class );
    scheduler.schedule( 23, runnable );

    scheduler.cancel( runnable );

    ArgumentCaptor<TimerExecTask> taskCaptor = ArgumentCaptor.forClass( TimerExecTask.class );
    verify( timer ).schedule( taskCaptor.capture(), eq( 23L ) );
    verify( taskCaptor.getValue() ).cancel();
  }

  @Test
  public void testCancel_cancelNonExistingTaskDoesNotFail() {
    scheduler.cancel( mock( Runnable.class ) );
  }

  @Test
  public void testCancel_removesTask() {
    Runnable runnable = mock( Runnable.class );
    scheduler.schedule( 23, runnable );

    scheduler.cancel( runnable );
    scheduler.schedule( 42, runnable );

    ArgumentCaptor<TimerExecTask> taskCaptor = ArgumentCaptor.forClass( TimerExecTask.class );
    verify( timer ).schedule( taskCaptor.capture(), eq( 23L ) );
    verify( timer ).schedule( taskCaptor.capture(), eq( 42L ) );
    assertNotSame( taskCaptor.getAllValues().get( 0 ), taskCaptor.getAllValues().get( 1 ) );
  }

  @Test
  public void testSerializationIsThreadSafe() throws Exception {
    scheduler = new TimerExecScheduler( display );
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          scheduler.schedule( 1, new NoOpRunnable() );
        } catch( Throwable thr ) {
          exceptions.add( thr );
        }
      }
    };

    Thread[] threads = Fixture.startThreads( 10, runnable );
    for( int i = 0; i < 5; i++ ) {
      Fixture.serialize( scheduler );
    }
    Fixture.joinThreads( threads );

    assertEquals( 0, exceptions.size() );
  }

}
