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
package org.eclipse.rwt.internal.lifecycle;

import java.util.*;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public final class UICallBackManager
  extends SessionSingletonBase
  implements SessionStoreListener
{

  // List of RunnableBase or SyncRunnable objects as added by add(A)sync 
  private List runnables = new ArrayList();
  // locked contains a reference to the callback thread that is currently 
  // blocked. 
  private Set locked = new HashSet();
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
  
  static class RunnableBase implements Runnable {
    final Runnable runnable;
    RunnableBase( final Runnable runnable ) {
      this.runnable = runnable;
    }
    public void run() {
      if( runnable != null ) {
        runnable.run();
      }
    }
  }
  
  static class SyncRunnable extends RunnableBase implements Runnable {
    SyncRunnable( final Runnable runnable ) {
      super( runnable );
    }
    public void run() {
      super.run();
      synchronized( runnable ) {
        runnable.notifyAll();
      }
    }
    public void block() {
      synchronized( runnable ) {
        try {
          runnable.wait();
        } catch( final InterruptedException e ) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }
  
  public static UICallBackManager getInstance() {
    return ( UICallBackManager )getInstance( UICallBackManager.class );
  }
  
  
  private UICallBackManager() {
  }

  boolean isCallBackRequestBlocked() {
    synchronized( runnables ) {
      return !locked.isEmpty();
    }
  }
  

  public void setActive( final boolean active ) {
    synchronized( runnables ) {
      this.active = active;
    }
  }
  
  public void sendUICallBack() {
    synchronized( runnables ) {
      if( !uiThreadRunning || !active ) {
        sendImmediately();
      }
    }
  }

  public void sendImmediately() {
    synchronized( runnables ) {
      runnables.notifyAll();
    }
  }
  
  public void addAsync( final Runnable runnable, final Display display ) {
    synchronized( runnables ) {
      runnables.add( new RunnableBase( runnable ) );
      // TODO [fappel]: This may not work properly in case asyncExcec is
      //                called in before render of a PhaseListener
      if( Thread.currentThread() != display.getThread() ) {
        sendUICallBack();
      }
    }
  }
  
  public void addSync( final Runnable runnable, final Display display ) {
    // TODO [fappel]: the synchronized block should synchronize on runnables
    //                not runnable, but by doing so the application may run
    //                into a deadlock. This is because the SyncRunnable blocks
    //                the thread execution on a different lock.
    synchronized( runnable ) {
      if( Thread.currentThread() != display.getThread() ) {
        SyncRunnable syncRunnable = new SyncRunnable( runnable );
        runnables.add( syncRunnable );
        sendUICallBack();
        syncRunnable.block();
      } else {
        runnable.run();
      }
    }
  }
  
  void notifyUIThreadStart() {
    uiThreadRunning = true;
    waitForUIThread = false;
  }

  void notifyUIThreadEnd() {
    uiThreadRunning = false;
    synchronized( runnables ) {
      if( !runnables.isEmpty() ) {
        sendUICallBack();
      }
    }
  }

  boolean processNextRunnableInUIThread() {
    Runnable runnable = null;
    boolean hasRunnable = false;
    synchronized( runnables ) {
      hasRunnable = !runnables.isEmpty();
      if( hasRunnable ) {
        runnable = ( Runnable )runnables.remove( 0 );
      }
    }
    if( runnable != null ) {
      try {
        runnable.run();
      } catch( Throwable t ) {
        SWT.error( SWT.ERROR_FAILED_EXEC, t );
      }
    }
    return hasRunnable;
  }
  
  boolean blockCallBackRequest() {
    boolean result = false;
    synchronized( runnables ) {
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
          runnables.wait();
        }
      } catch( final InterruptedException ie ) {
        result = true;
      } finally {
        locked.remove( currentThread );
        if( !result ) {
          ContextProvider.getSession().removeSessionStoreListener( listener );
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
  
  
  ///////////////////////////////////////
  // interface SessionStoreListener

  // TODO [rh] revise this when bug #219465 is closed
  //      see https://bugs.eclipse.org/bugs/show_bug.cgi?id=219465
  public void beforeDestroy( final SessionStoreEvent event ) {
    synchronized( runnables ) {
      if( runnables != null ) {
        RunnableBase[] toBeExecuted = new RunnableBase[ runnables.size() ]; 
        runnables.toArray( toBeExecuted );
        for( int i = 0; i < toBeExecuted.length; i++ ) {
          RunnableBase runnable = toBeExecuted[ i ];
          runnable.run();
        }
        sendImmediately();
      }
      runnables.clear();
      runnables = null;
    }
  }
}
