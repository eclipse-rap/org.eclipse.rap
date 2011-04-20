/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public final class UICallBackManager {

  public static UICallBackManager getInstance() {
    Object inst = SessionSingletonBase.getInstance( UICallBackManager.class );
    return ( UICallBackManager )inst;
  }

  // synchronization object to control access to the runnables List
  private final Object lock;
  // contains a reference to the callback request thread that is currently
  // blocked.
  private final Set blockedCallBackRequests;
  // Flag that indicates whether a request is processed. In that case no
  // notifications are sent to the client.
  private boolean uiThreadRunning;
  // Flag that indicates that a notification was sent to the client. If the new
  // callback thread returns earlier than the UI Thread the callback thread
  // must be blocked although the runnables are not empty
  private boolean waitForUIThread;
  // Flag that indicates whether the UICallBack mechanism is active. If not
  // no callback thread must be blocked.
  private boolean active;

  private UICallBackManager() {
    lock = new Object();
    blockedCallBackRequests = new HashSet();
    uiThreadRunning = false;
    waitForUIThread = false;
    active = false;
  }

  boolean isCallBackRequestBlocked() {
    synchronized( lock ) {
      return !blockedCallBackRequests.isEmpty();
    }
  }

  public void setActive( final boolean active ) {
    synchronized( lock ) {
      this.active = active;
    }
  }

  public void sendUICallBack() {
    synchronized( lock ) {
      if( !uiThreadRunning || !active ) {
        sendImmediately();
      }
    }
  }

  public void sendImmediately() {
    synchronized( lock ) {
      lock.notifyAll();
    }
  }

  void notifyUIThreadStart() {
    synchronized( lock ) {
      uiThreadRunning = true;
      waitForUIThread = false;
    }
  }

  void notifyUIThreadEnd() {
    synchronized( lock ) {
      uiThreadRunning = false;
      if( hasRunnables() ) {
        sendUICallBack();
      }
    }
  }

  boolean hasRunnables() {
    boolean result = false;
    Display display = LifeCycleUtil.getSessionDisplay();
    if( display != null && !display.isDisposed() ) {
      IDisplayAdapter adapter
        = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
      result = adapter.getAsyncRunnablesCount() > 0;
    }
    return result;
  }
  
  boolean blockCallBackRequest() {
    boolean result = false;
    synchronized( lock ) {
      final Thread currentThread = Thread.currentThread();
      SessionStoreListener listener = new SessionStoreListener() {
        public void beforeDestroy( final SessionStoreEvent event ) {
          currentThread.interrupt();
        }
      };
      try {
        if( mustBlockCallBackRequest() ) {
          blockedCallBackRequests.add( currentThread );
          ISessionStore session = ContextProvider.getSession();
          session.addSessionStoreListener( listener );
          lock.wait();
        }
      } catch( InterruptedException ie ) {
        result = true;
        Thread.interrupted(); // Reset interrupted state, see bug 300254
      } finally {
        blockedCallBackRequests.remove( currentThread );
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
           && blockedCallBackRequests.isEmpty()
           && (    waitForUIThread 
                || uiThreadRunning 
                || !hasRunnables() );
  }
}
