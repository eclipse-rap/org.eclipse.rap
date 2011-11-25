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
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;
import org.eclipse.rwt.internal.uicallback.UICallBackManager;


public class TimerExecScheduler_Test extends TestCase {
  
  private TimerExecScheduler scheduler;
  private Display display;
  private volatile Throwable exception;

  public void testSerializationIsThreadSafe() throws InterruptedException {
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          scheduler.schedule( 1, new NoOpRunnable() );
          Fixture.serialize( scheduler );
        } catch( Throwable thr ) {
          exception = thr;
        }
      }
    };
    
    Thread[] threads = Fixture.startThreads( 10, runnable );
    Fixture.joinThreads( threads );
    
    assertNull( exception );
  }

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    scheduler = new TimerExecScheduler( display, UICallBackManager.getInstance() );
  }
  
  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
