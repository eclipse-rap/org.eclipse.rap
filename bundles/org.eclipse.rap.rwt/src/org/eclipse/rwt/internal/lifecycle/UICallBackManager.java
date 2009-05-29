/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.util.*;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public final class UICallBackManager
  implements SessionStoreListener
{

  public static UICallBackManager getInstance() {
    Object inst = SessionSingletonBase.getInstance( UICallBackManager.class );
    return ( UICallBackManager )inst;
  }
  
  private static Timer sendTimer;

  // List of RunnableBase or SyncRunnable objects as added by add(A)sync 
  List runnables;
  // synchronziation-object to control access to the runnables List
  private final Object runnablesLock;
  // locked contains a reference to the callback thread that is currently 
  // blocked. 
  private final Set locked;
  // Flag that indicates whether a request is processed. In that case no
  // notifications are sent to the client.
  private boolean uiThreadRunning;
  // Flag that indicates that a notification was sent to the client. If the new
  // callback thread returns earlier than the UI Thread the callback thread
  // must be blocked although the runnbles are not empty
  private boolean waitForUIThread;
  // Flag that indicates whether the UICallBack mechanism is active. If not
  // no callback thread must be blocked.
  private boolean active;
  
  private UICallBackManager() {
    runnables = new ArrayList();
    runnablesLock = new Object();
    locked = new HashSet();
    uiThreadRunning = false;
    waitForUIThread = false;
    active = false;
  }

  boolean isCallBackRequestBlocked() {
    synchronized( runnablesLock ) {
      return !locked.isEmpty();
    }
  }
  

  public void setActive( final boolean active ) {
    synchronized( runnablesLock ) {
      this.active = active;
    }
  }
  
  public void sendUICallBack( final long time ) {
    synchronized( UICallBackManager.class ) {
      if( sendTimer == null ) {
        sendTimer = new Timer( true );
      }
    }
    TimerTask task = new TimerTask() {
      public void run() {
        sendUICallBack();
      }
    };
    sendTimer.schedule( task, new Date( time ) );
  }

  public void sendUICallBack() {
    synchronized( runnablesLock ) {
      if( !uiThreadRunning || !active ) {
        sendImmediately();
      }
    }
  }

  public void sendImmediately() {
    synchronized( runnablesLock ) {
      runnablesLock.notifyAll();
    }
  }
  
  public void addAsync( final Display display, final Runnable runnable ) {
    synchronized( runnablesLock ) {
      runnables.add( new RunnableBase( runnable ) );
      // TODO [fappel]: This may not work properly in case asyncExcec is
      //                called in before render of a PhaseListener
      if( Thread.currentThread() != display.getThread() ) {
        sendUICallBack();
      }
    }
  }
  
  public void addSync( final Display display, final Runnable runnable ) {
    if( Thread.currentThread() == display.getThread() ) {
      runnable.run();
    } else {
      SyncRunnable syncRunnable = new SyncRunnable( runnable );
      synchronized( runnablesLock ) {
        runnables.add( syncRunnable );
      }
      sendUICallBack();
      syncRunnable.block();
    }
  }
  
  public void addTimer( final Display display, 
                        final Runnable runnable, 
                        final long time ) 
  {
    if( time < 0 ) {
      removeTimer( runnable );
    } else {
      synchronized( runnablesLock ) {
        TimerRunnable timerRunnable = new TimerRunnable( runnable, time );
        runnables.add( timerRunnable );
        if( Thread.currentThread() != display.getThread() ) {
          sendUICallBack( time );
        }
      }
    }
  }


  private void removeTimer( final Runnable runnable ) {
    synchronized( runnablesLock ) {
      Iterator iter = runnables.iterator();
      boolean found = false;
      while( !found && iter.hasNext() ) {
        RunnableBase next = ( RunnableBase )iter.next();
        if( next.equalsRunnable( runnable ) ) {
          runnables.remove( next );
          found = true;
        }
      }
    }
  }

  void notifyUIThreadStart() {
    uiThreadRunning = true;
    waitForUIThread = false;
  }

  void notifyUIThreadEnd() {
    uiThreadRunning = false;
    synchronized( runnablesLock ) {
      if( !runnables.isEmpty() ) {
        sendUICallBack();
      }
    }
  }

  boolean processNextRunnableInUIThread() {
    RunnableBase runnable = null;
    synchronized( runnablesLock ) {
      Iterator iter = runnables.iterator();
      while( runnable == null && iter.hasNext() ) {
        RunnableBase next = ( RunnableBase )iter.next();
        if( next.canRun() ) {
          runnable = next;
          runnables.remove( runnable );
        }
      }
    }
    if( runnable != null ) {
      try {
        runnable.run();
      } catch( Throwable t ) {
        SWT.error( SWT.ERROR_FAILED_EXEC, t );
      }
    }
    return runnable != null;
  }
  
  boolean blockCallBackRequest() {
    boolean result = false;
    synchronized( runnablesLock ) {
      final Thread currentThread = Thread.currentThread();
      SessionStoreListener listener = new SessionStoreListener() {
        public void beforeDestroy( final SessionStoreEvent event ) {
          currentThread.interrupt();
        }
      };
      try {
        if( mustBlockCallBackRequest() ) {
          locked.add( currentThread );
          ISessionStore session = ContextProvider.getSession();
          session.addSessionStoreListener( listener );
          runnablesLock.wait();
        }
      } catch( final InterruptedException ie ) {
        result = true;
      } finally {
        locked.remove( currentThread );
        if( !result ) {
          // TODO [rh] remove the try/catch block once this bug 278258 is fixed
          //      (Rework ISessionStore#add/removeSessionStoreListener)
          try {
            ContextProvider.getSession().removeSessionStoreListener( listener );
          } catch( IllegalStateException e ) {
            // ignore - the session store is (about to be) unbound, this means
            // the listener is/will be removed anyway
          }
        }
      }
      waitForUIThread = true;
    }
    return result;
  }

  private boolean mustBlockCallBackRequest() {
    return    active 
           && locked.isEmpty()
           && (    waitForUIThread 
                || uiThreadRunning 
                || runnables.isEmpty() );
  }
  
  
  /////////////////////////////////
  // interface SessionStoreListener

  // TODO [rh] revise this when bug #219465 is closed
  //      see https://bugs.eclipse.org/bugs/show_bug.cgi?id=219465
  public void beforeDestroy( final SessionStoreEvent event ) {
    synchronized( runnablesLock ) {
      if( runnables != null ) {
        RunnableBase[] toBeExecuted = new RunnableBase[ runnables.size() ]; 
        runnables.toArray( toBeExecuted );
        for( int i = 0; i < toBeExecuted.length; i++ ) {
          RunnableBase runnable = toBeExecuted[ i ];
          if( runnable.canRun() ) {
            runnable.run();
          }
        }
        sendImmediately();
      }
      runnables.clear();
      runnables = null;
    }
  }

  /////////////////////////////////////////////////////
  // Runnable wrapper classes for sync/async/timer exec

  static class RunnableBase {
    private final Runnable runnable;
    RunnableBase( final Runnable runnable ) {
      this.runnable = runnable;
    }
    boolean canRun() {
      return true;
    }
    void run() {
      if( runnable != null ) {
        runnable.run();
      }
    }
    boolean equalsRunnable( final Runnable runnable ) {
      return this.runnable == runnable;
    }
  }
  
  static final class SyncRunnable extends RunnableBase {
    private final Object lock;
    private boolean terminated;
    SyncRunnable( final Runnable runnable ) {
      super( runnable );
      lock = new Object();
    }
    void run() {
      try {
        super.run();
      } finally {
        notifyBlocked();
      }
    }
    private void notifyBlocked() {
      synchronized( lock ) {
        terminated = true;
        lock.notifyAll();
      }
    }
    void block() {
      synchronized( lock ) {
        if( !terminated ) {
          try {
            lock.wait();
          } catch( final InterruptedException e ) {
            // stop waiting
          }
        }
      }
    }
  }
  
  static final class TimerRunnable extends RunnableBase {
    private final long time;
    TimerRunnable( final Runnable runnable, final long time ) {
      super( runnable );
      this.time = time;
    }
    boolean canRun() {
      return System.currentTimeMillis() >= time;
    }
  }
}
