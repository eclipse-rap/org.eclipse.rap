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

package org.eclipse.swt.internal.lifecycle;

import java.util.*;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.eclipse.swt.SWT;
import com.w4t.SessionSingletonBase;


public class UICallBackManager
  extends SessionSingletonBase
  implements HttpSessionBindingListener
{
  
  // Flag that indicates whether a callback thread is blocked.
  private boolean callBackRequestBlocked;
  private List runnables;
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
  
  final static class SyncRunnable implements Runnable {
    private final Runnable runnable;
    SyncRunnable( final Runnable runnable ) {
      this.runnable = runnable;
    }
    public void run() {
      runnable.run();
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
    runnables = new ArrayList();
  }

  boolean isCallBackRequestBlocked() {
    synchronized( runnables ) {
      return callBackRequestBlocked;
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
  
  public void addAsync( final Runnable runnable ) {
    synchronized( runnables ) {
      runnables.add( runnable );
      sendUICallBack();
    }
  }
  
  public void addSync( final Runnable runnable ) {
    synchronized( runnable ) {
      SyncRunnable syncRunnable = new SyncRunnable( runnable );
      runnables.add( syncRunnable );
      sendUICallBack();
      syncRunnable.block();
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

  void blockCallBackRequest() {
    synchronized( runnables ) {
      callBackRequestBlocked = true;
      try {
        if( mustBlockCallBackRequest() ) {
          runnables.wait();
        }
      } catch( final InterruptedException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        callBackRequestBlocked = false;
      }
      waitForUIThread = true;
    }
  }

  private boolean mustBlockCallBackRequest() {
    return    active 
           && ( waitForUIThread ||uiThreadRunning || runnables.isEmpty() );
  }
  
  
  ///////////////////////////////////////
  // interface HttpSessionBindingListener
  
  public void valueBound( final HttpSessionBindingEvent event ) {
    // do nothing
  }

  public void valueUnbound( final HttpSessionBindingEvent event ) {
    if( runnables != null ) {
      synchronized( runnables ) {
        runnables.notifyAll();
      }
      runnables.clear();
      runnables = null;
    }
  }
}
