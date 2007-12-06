/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
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


public class UICallBackManager
  extends SessionSingletonBase
  implements SessionStoreListener
{

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
    private final Display display;
    RunnableBase( final Runnable runnable, final Display display ) {
      this.runnable = runnable;
      this.display = display;
    }

    public void run() {
      if( runnable != null ) {
        runnable.run();
      }
    }
  }
  
  static class SyncRunnable extends RunnableBase implements Runnable {
    SyncRunnable( final Runnable runnable, final Display display ) {
      super( runnable, display );
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
        runnables.notifyAll();
      }
    }
  }
  
  public void addAsync( final Runnable runnable, final Display display ) {
    synchronized( runnables ) {
      runnables.add( new RunnableBase( runnable, display ) );
      if( Thread.currentThread() != RWTLifeCycle.getThread() ) {
        sendUICallBack();
      }
    }
  }
  
  public void addSync( final Runnable runnable, final Display display ) {
    synchronized( runnable ) {
      if( Thread.currentThread() != RWTLifeCycle.getThread() ) {
        SyncRunnable syncRunnable = new SyncRunnable( runnable, display );
        runnables.add( syncRunnable );
        sendUICallBack();
        syncRunnable.block();
      } else {
        runnable.run();
      }
    }
  }
  
  public void notifyUIThreadStart() {
    if( RWTLifeCycle.getThread() != Thread.currentThread() ) {
      SWT.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
    uiThreadRunning = true;
    waitForUIThread = false;
  }

  public void notifyUIThreadEnd() {
    if( RWTLifeCycle.getThread() != Thread.currentThread() ) {
      SWT.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
    uiThreadRunning = false;
    synchronized( runnables ) {
      if( !runnables.isEmpty() ) {
        sendUICallBack();
      }
    }
  }

  void processRunnablesInUIThread() {
    if( RWTLifeCycle.getThread() != Thread.currentThread() ) {
      SWT.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
    Runnable toExecute = null;
    boolean finished = false;
    while( !finished ) {
      synchronized( runnables ) {
        finished = runnables.isEmpty();
        if( !finished ) {
          toExecute = ( Runnable )runnables.remove( 0 );
        }
      }
      if( toExecute != null ) {
        try {
          toExecute.run();
        } catch( final RuntimeException e ) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } finally {
          toExecute = null;
        }
      }
    }
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

  public void beforeDestroy( final SessionStoreEvent event ) {
    synchronized( runnables ) {
      if( runnables != null ) {
        RunnableBase[] toBeExecuted = new RunnableBase[ runnables.size() ]; 
        runnables.toArray( toBeExecuted );
        for( int i = 0; i < toBeExecuted.length; i++ ) {
          RunnableBase runnable = toBeExecuted[ i ];
          Display display = runnable.display;
          try {
            UICallBackServiceHandler.runNonUIThreadWithFakeContext( display, 
                                                                    runnable, 
                                                                    true );
          } catch( final RuntimeException re ) {
            // TODO Auto-generated catch block
            re.printStackTrace();
          }
        }
        runnables.notifyAll();
      }
      runnables.clear();
      runnables = null;
    }
  }
}
