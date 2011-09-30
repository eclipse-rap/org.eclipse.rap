/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.rwt.internal.engine.PostDeserialization;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;


final class TimerExecScheduler implements SerializableCompatibility {

  private final Display display;
  private final Collection<TimerExecTask> tasks;
  private transient Timer timer;

  TimerExecScheduler( Display display ) {
    this.display = display;
    this.tasks = new LinkedList<TimerExecTask>();
  }

  void schedule( int milliseconds, Runnable runnable ) {
    synchronized( display.getDeviceLock() ) {
      initializeTimer();
      TimerExecTask task = new TimerExecTask( runnable, milliseconds );
      tasks.add( task );
      timer.schedule( task, milliseconds );
    }
  }

  void cancel( Runnable runnable ) {
    synchronized( display.getDeviceLock() ) {
      TimerExecTask task = findTask( runnable );
      if( task != null ) {
        removeTask( task );
        task.cancel();
      }
    }
  }

  void dispose() {
    synchronized( display.getDeviceLock() ) {
      if( timer != null ) {
        timer.cancel();
      }
      tasks.clear();
    }
  }

  private TimerExecTask findTask( Runnable runnable ) {
    Iterator<TimerExecTask> iter = tasks.iterator();
    TimerExecTask result = null;
    while( result == null && iter.hasNext() ) {
      TimerExecTask task = iter.next();
      if( task.getRunnable() == runnable ) {
        result = task;
      }
    }
    return result;
  }

  private void initializeTimer() {
    if( timer == null ) {
      timer = new Timer( "RWT timerExec scheduler", true );
    }
  }

  private void rescheduleTasks() {
    synchronized( display.getDeviceLock() ) {
      if( tasks.size() > 0 ) {
        initializeTimer();
        for( TimerExecTask task : tasks ) {
          timer.schedule( task, task.getTime() );
        }
      }
    }
  }

  private void removeTask( TimerTask task ) {
    // code is synchronized by caller
    tasks.remove( task );
  }
  
  private void writeObject( ObjectOutputStream stream ) throws IOException {
    synchronized( display.getDeviceLock() ) {
      stream.defaultWriteObject();
    }
  }

  private void readObject( ObjectInputStream stream ) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    stream.registerValidation( new PostDeserializationValidation(), 0 );
  }

  /////////////////
  // Inner classes

  private class TimerExecTask extends TimerTask implements SerializableCompatibility {
    
    private final Runnable runnable;
    private final Date time;

    TimerExecTask( Runnable runnable, long milliseconds ) {
      this.runnable = runnable;
      this.time = new Date( System.currentTimeMillis() + milliseconds );
    }

    public void run() {
      synchronized( display.getDeviceLock() ) {
        removeTask( this );
        if( !display.isDisposed() ) {
          display.asyncExec( runnable );
        }
      }
    }

    Runnable getRunnable() {
      return runnable;
    }

    Date getTime() {
      return time;
    }
  }

  private class PostDeserializationValidation implements ObjectInputValidation {
    public void validateObject() throws InvalidObjectException {
      ISessionStore sessionStore = getSessionStore();
      PostDeserialization.addProcessor( sessionStore, new Runnable() {
        public void run() {
          rescheduleTasks();
        }
      } );
    }

    private ISessionStore getSessionStore() {
      IDisplayAdapter adapter = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
      return adapter.getSessionStore();
    }
  }
}
