/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.util.*;

import org.eclipse.rwt.internal.service.ContextProvider;

class RWTLifeCycleThreadPool {
  private final static RWTLifeCycleThreadPool _instance
    = new RWTLifeCycleThreadPool( 1 );

  private final List threads = new ArrayList();
  private final LinkedList queue;
  private int busyThreads = 0;
  
  
  private class PoolRunnable implements Runnable {
    private final Runnable runnable;
    private final Object lock;

    private PoolRunnable( final Runnable runnable, final Object lock ) {
      this.runnable = runnable;
      this.lock = lock;
    }
    
    public void run() {
      if( lock != null ) {
        synchronized( lock ) {
        }
      }
      runnable.run();
    }
  }
  
  private class PoolWorker extends Thread {
    public PoolWorker() {
      super( "RWT Request Worker " + threads.size() );
    }
    
    public void run() {
      Runnable runnable;
      while( true ) {
        synchronized( queue ) {
          while( queue.isEmpty() ) {
            try {
              queue.wait();
            } catch( final InterruptedException ignored ) {
            }
          }
          runnable = ( Runnable )queue.removeFirst();
          busyThreads++;
          if( busyThreads >= threads.size() ) {
            createWorker();
          }
        }
        // If we don't catch RuntimeException,
        // the pool could leak threads
        try {
          runnable.run();
        } catch( final RuntimeException re ) {
          // TODO [fappel] exception handling
          re.printStackTrace();
        } finally {
          synchronized( queue ) {
            busyThreads--;
          }
          ContextProvider.releaseContextHolder();
        }
      }
    }
  }
  

  private RWTLifeCycleThreadPool( final int initialSize ) {
    queue = new LinkedList();
    for( int i = 0; i < initialSize; i++ ) {
      createWorker();
    }
  }

  private void createWorker() {
    PoolWorker poolWorker = new PoolWorker();
    threads.add( poolWorker );
    poolWorker.setDaemon( true );
    poolWorker.start();
  }

  static void execute( final Runnable runnable, final Object lock ) {
    _instance.doExecute( runnable, lock ); 
  }
  
  private void doExecute( final Runnable runnable, final Object lock ) {
    synchronized( queue ) {
      queue.addLast( new PoolRunnable( runnable, lock ) );
      queue.notify();
    }
  }
}
