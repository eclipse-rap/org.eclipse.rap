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

import org.eclipse.rap.rwt.internal.engine.PostDeserialization;
import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;


class TimerExecScheduler implements SerializableCompatibility {

  final Display display;
  final ServerPushManager serverPushManager;
  private final Collection<TimerExecTask> tasks;
  private transient Timer timer;

  TimerExecScheduler( Display display ) {
    this.display = display;
    serverPushManager = ServerPushManager.getInstance();
    tasks = new LinkedList<TimerExecTask>();
  }

  void schedule( int milliseconds, Runnable runnable ) {
    TimerExecTask task = new TimerExecTask( this, runnable );
    synchronized( display.getDeviceLock() ) {
      initializeTimer();
      tasks.add( task );
      timer.schedule( task, milliseconds );
    }
  }

  void cancel( Runnable runnable ) {
    TimerExecTask task = removeTask( runnable );
    if( task != null ) {
      task.cancel();
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

  private void initializeTimer() {
    if( timer == null ) {
      timer = createTimer();
    }
  }

  Timer createTimer() {
    return new Timer( "RWT timerExec scheduler", true );
  }

  private void rescheduleTasks() {
    synchronized( display.getDeviceLock() ) {
      if( tasks.size() > 0 ) {
        initializeTimer();
        for( TimerExecTask task : tasks ) {
          timer.schedule( task, new Date( task.scheduledExecutionTime() ) );
        }
      }
    }
  }

  private TimerExecTask removeTask( Runnable runnable ) {
    TimerExecTask result = null;
    synchronized( display.getDeviceLock() ) {
      Iterator<TimerExecTask> iter = tasks.iterator();
      while( result == null && iter.hasNext() ) {
        TimerExecTask task = iter.next();
        if( task.getRunnable() == runnable ) {
          removeTask( result );
          result = task;
        }
      }
    }
    return result;
  }

  void removeTask( TimerTask task ) {
    synchronized( display.getDeviceLock() ) {
      tasks.remove( task );
    }
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

  private class PostDeserializationValidation implements ObjectInputValidation {
    public void validateObject() throws InvalidObjectException {
      UISession uiSession = getUISession();
      PostDeserialization.addProcessor( uiSession, new Runnable() {
        public void run() {
          rescheduleTasks();
        }
      } );
    }

    private UISession getUISession() {
      IDisplayAdapter adapter = display.getAdapter( IDisplayAdapter.class );
      return adapter.getUISession();
    }
  }
}
