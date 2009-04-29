/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.internal.progress;

import java.lang.reflect.Field;
import java.util.*;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;

// RAP [fappel]:
public class JobManagerAdapter
  extends ProgressProvider
  implements IJobChangeListener
{
  
  private static JobManagerAdapter _instance;
  private final Map jobs;
  private final ProgressManager defaultProgressManager;
  final Object lock;

  public static synchronized JobManagerAdapter getInstance() {
    if( _instance == null ) {
      _instance = new JobManagerAdapter();
    }
    return _instance;
  }
  
  
  private JobManagerAdapter() {
    // To avoid deadlocks we have to use the same synchronisation lock.
    // If anyone has a better idea - you're welcome.
    IJobManager jobManager = Job.getJobManager();
    Class clazz = jobManager.getClass();
    try {
      Field jobManagerLock = clazz.getDeclaredField( "lock" );
      jobManagerLock.setAccessible( true );
      lock = jobManagerLock.get( jobManager );
    } catch( final Throwable thr ) {
      String msg = "Could not initialize synchronization lock.";
      throw new IllegalStateException( msg );
    }
    jobs = new HashMap();
    defaultProgressManager = new ProgressManager();
    Job.getJobManager().setProgressProvider( this );
    Job.getJobManager().addJobChangeListener( this );
  }
  
  
  ///////////////////////////////
  // ProgressProvider
  
  public IProgressMonitor createMonitor( final Job job ) {
    ProgressManager manager = findProgressManager( job );
    return manager.createMonitor( job );
  }
  
  public IProgressMonitor createMonitor( final Job job,
                                         final IProgressMonitor group,
                                         final int ticks )
  {
    ProgressManager manager = findProgressManager( job );
    return manager.createMonitor( job, group, ticks );
  }
  
  public IProgressMonitor createProgressGroup() {
    return defaultProgressManager.createProgressGroup();
  }
  
  
  ///////////////////////////////
  // interface IJobChangeListener
  
  public void aboutToRun( final IJobChangeEvent event ) {
    ProgressManager manager = findProgressManager( event.getJob() );
    manager.changeListener.aboutToRun( event );
  }

  public void awake( final IJobChangeEvent event ) {
    ProgressManager manager = findProgressManager( event.getJob() );
    manager.changeListener.awake( event );
  }

  public void done( final IJobChangeEvent event ) {
    final ProgressManager[] manager = new ProgressManager[ 1 ];
    Display display = null;
    synchronized( lock ) {
      try {
        manager[ 0 ] = findProgressManager( event.getJob() );
        display = ( Display )jobs.get( event.getJob() );
        if( display != null ) {
          display.asyncExec( new Runnable() {
            public void run() {
              Job job = event.getJob();
              String id = String.valueOf( job.hashCode() );
              UICallBack.deactivate( id );
            }
          } );
        }
      } finally {
        Job job = event.getJob();
        if( !job.shouldSchedule() ) {
          jobs.remove( job );
        }
      }
    }
    if( display != null ) {
      display.asyncExec( new Runnable() {
        public void run() {
          manager[ 0 ].changeListener.done( event );
        }
      } );
//    } else {
//      manager[ 0 ].changeListener.done( event );      
    }
  }

  public void running( final IJobChangeEvent event ) {
    ProgressManager manager = findProgressManager( event.getJob() );
    manager.changeListener.running( event );
  }

  public void scheduled( final IJobChangeEvent event ) {
    ProgressManager manager;
    synchronized( lock ) {
      if( ContextProvider.hasContext() ) {
        jobs.put( event.getJob(), RWTLifeCycle.getSessionDisplay() );
        bindToSession( event.getJob() );
        String id = String.valueOf( event.getJob().hashCode() );
        UICallBack.activate( id );
      }
      manager = findProgressManager( event.getJob() );
    }
    manager.changeListener.scheduled( event );
  }

  public void sleeping( final IJobChangeEvent event ) {
    ProgressManager manager = findProgressManager( event.getJob() );
    manager.changeListener.sleeping( event );
  }

  
  //////////////////
  // helping methods
  
  private ProgressManager findProgressManager( final Job job ) {
    synchronized( lock ) {
      final ProgressManager result[] = new ProgressManager[ 1 ];
      Display display = ( Display )jobs.get( job );
      if( display != null ) {
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
          public void run() {
            result[ 0 ] = ProgressManager.getInstance();
          }
        } );
        if( result[ 0 ] == null ) {
          String msg = "ProgressManager must not be null.";
          throw new IllegalStateException( msg );
        }
      } else {
        result[ 0 ] = defaultProgressManager;
      }
      return result[ 0 ];
    }
  }
  
  private void bindToSession( final Object keyToRemove ) {
    ISessionStore session = RWT.getSessionStore();
    HttpSessionBindingListener watchDog = new HttpSessionBindingListener() {
      public void valueBound( final HttpSessionBindingEvent event ) {
      }
      public void valueUnbound( final HttpSessionBindingEvent event ) {
        try {
          handleWatchDog( keyToRemove );
        } finally {
          synchronized( lock ) {
            jobs.remove( keyToRemove );
          }
        }
      }

      private void handleWatchDog( final Object keyToRemove ) {
        // ////////////////////////////////////////////////////////////////////
        // TODO [fappel]: Very ugly hack to avoid a memory leak.
        // As a job can not be removed from the
        // running set directly, I use reflection. Jobs
        // can be catched in the set on session timeouts.
        // Don't know a proper solution yet.
        // Note that this is still under investigation.
        if( keyToRemove instanceof Job ) {
          final Job jobToRemove = ( Job )keyToRemove;
          Display display = ( Display )jobs.get( jobToRemove );
          if( display != null ) {
            UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
              public void run() {
                jobToRemove.cancel();
                jobToRemove.addJobChangeListener( new JobCanceler() );
              }
            } );
          }
          try {
            IJobManager jobManager = Job.getJobManager();
            Class clazz = jobManager.getClass();
            Field running = clazz.getDeclaredField( "running" );
            running.setAccessible( true );
            Set set = ( Set )running.get( jobManager );
            synchronized( lock ) {
              set.remove( keyToRemove );
              // still sometimes job get catched - use the job marker adapter
              // to check whether they can be eliminated
              Object[] runningJobs = set.toArray();
              for( int i = 0; i < runningJobs.length; i++ ) {
                Job toCheck = ( Job )runningJobs[ i ];
                IJobMarker marker
                  = ( IJobMarker )toCheck.getAdapter( IJobMarker.class );
                if( marker != null && marker.canBeRemoved() ) {
                  set.remove( toCheck );
                }
              }
            }
          } catch( final Throwable thr ) {
            // TODO [fappel]: exception handling
            thr.printStackTrace();
          }
        }
      }
    };
    session.setAttribute( String.valueOf( watchDog.hashCode() ), watchDog );
  }
}
