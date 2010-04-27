/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.*;
import java.util.List;


final class TimerExecScheduler {

  private final Display display;
  private List tasks;
  private Timer scheduler;

  public TimerExecScheduler( final Display display ) {
    this.display = display;
  }

  void schedule( final int milliseconds, final Runnable runnable ) {
    synchronized( display.getDeviceLock() ) {
      initialize();
      TimerTask task = new TimerExecTask( runnable );
      tasks.add( task );
      scheduler.schedule( task, milliseconds );
    }
  }

  void cancel( final Runnable runnable ) {
    synchronized( display.getDeviceLock() ) {
      if( tasks != null ) {
        Iterator iter = tasks.iterator();
        boolean found = false;
        while( !found && iter.hasNext() ) {
          TimerExecTask task = ( TimerExecTask )iter.next();
          if( task.getRunnable() == runnable ) {
            removeTask( task );
            task.cancel();
            found = true;
          }
        }
      }
    }
  }

  void dispose() {
    synchronized( display.getDeviceLock() ) {
      if( scheduler != null ) {
        scheduler.cancel();
      }
      if( tasks != null ) {
        tasks.clear();
      }
    }
  }
  
  private void initialize() {
    if( scheduler == null ) {
      scheduler = new Timer( true );
    }
    if( tasks == null ) {
      tasks = new LinkedList();
    }
  }

  private void removeTask( TimerTask task ) {
    // code is synchronized by caller
    tasks.remove( task );
  }

  /////////////////
  // Inner classes

  private final class TimerExecTask extends TimerTask {

    private final Runnable runnable;

    private TimerExecTask( final Runnable runnable ) {
      this.runnable = runnable;
    }

    public void run() {
      synchronized( display.getDeviceLock() ) {
        if( !display.isDisposed() ) {
          removeTask( this );
          display.asyncExec( runnable );
        }
      }
    }

    Runnable getRunnable() {
      return runnable;
    }
  }
}
