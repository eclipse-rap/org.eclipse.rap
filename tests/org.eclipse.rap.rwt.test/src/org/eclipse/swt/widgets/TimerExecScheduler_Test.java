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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;


public class TimerExecScheduler_Test extends TestCase {

  private TimerExecScheduler scheduler;
  private Display display;
  private Collection<Throwable> exceptions;

  public void testSerializationIsThreadSafe() throws Exception {
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

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    scheduler = new TimerExecScheduler( display, ServerPushManager.getInstance() );
    exceptions = Collections.synchronizedList( new LinkedList<Throwable>() );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
