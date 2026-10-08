/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class ConcurrencyTestUtil {

  public static void runInThread( final Runnable runnable ) throws Throwable {
    final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
    Runnable exceptionGuard = new Runnable() {
      public void run() {
        try {
          runnable.run();
        } catch( Throwable throwable ) {
          exception.set( throwable );
        }
      }
    };
    Thread thread = new Thread( exceptionGuard );
    thread.setDaemon( true );
    thread.start();
    thread.join();
    if( exception.get() != null ) {
      throw exception.get();
    }
  }

  public static Thread[] startThreads( int threadCount, Runnable runnable ) {
    List<Thread> threads = new ArrayList<Thread>();
    for( int i = 0; i < threadCount; i++ ) {
      Thread thread = new Thread( runnable );
      thread.setDaemon( true );
      thread.start();
      threads.add( thread );
      Thread.yield();
    }
    Thread[] result = new Thread[ threads.size() ];
    threads.toArray( result );
    return result;
  }

  public static void joinThreads( Thread[] threads ) throws InterruptedException {
    for( int i = 0; i < threads.length; i++ ) {
      Thread thread = threads[ i ];
      thread.join();
    }
  }

}
