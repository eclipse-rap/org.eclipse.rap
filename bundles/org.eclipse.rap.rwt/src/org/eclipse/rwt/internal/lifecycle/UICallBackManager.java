/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
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
import org.eclipse.rwt.internal.util.SerializableLock;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.internal.SerializableCompatibility;


public final class UICallBackManager implements SerializableCompatibility {
  private static final long serialVersionUID = 1L;

  static final class IdManager implements SerializableCompatibility {
    private static final long serialVersionUID = 1L;
  
    private final Set<String> ids;
    private final SerializableLock lock;
  
    private IdManager() {
      ids = new HashSet<String>();
      lock = new SerializableLock();
    }
  
    int add( String id ) {
      synchronized( lock ) {
        ids.add( id );
        return ids.size();
      }
    }
  
    int remove( String id ) {
      synchronized( lock ) {
        ids.remove( id );
        return ids.size();
      }
    }
  
    boolean isEmpty() {
      synchronized( lock ) {
        return ids.isEmpty();
      }
    }
  }

  public static UICallBackManager getInstance() {
    return ( UICallBackManager )SessionSingletonBase.getInstance( UICallBackManager.class );
  }
  
  private final IdManager idManager;

  // synchronization object to control access to the runnables List
  final SerializableLock lock;
  // contains a reference to the callback request thread that is currently
  // blocked.
  private final Set<Thread> blockedCallBackRequests;
  // Flag that indicates whether a request is processed. In that case no
  // notifications are sent to the client.
  private boolean uiThreadRunning;
  // Flag that indicates that a notification was sent to the client. If the new
  // callback thread returns earlier than the UI Thread the callback thread
  // must be blocked although the runnables are not empty
  private boolean waitForUIThread;
  // indicates whether the display has runnables to execute
  private boolean hasRunnables;

  private UICallBackManager() {
    lock = new SerializableLock();
    idManager = new IdManager();
    blockedCallBackRequests = new HashSet<Thread>();
    uiThreadRunning = false;
    waitForUIThread = false;
  }

  boolean isCallBackRequestBlocked() {
    synchronized( lock ) {
      return !blockedCallBackRequests.isEmpty();
    }
  }

  public void wakeClient() {
    synchronized( lock ) {
      if( !uiThreadRunning ) {
        releaseBlockedRequest();
      }
    }
  }

  public void releaseBlockedRequest() {
    synchronized( lock ) {
      lock.notifyAll();
    }
  }

  public void setHasRunnables( final boolean hasRunnables ) {
    synchronized( lock ) {
      this.hasRunnables = hasRunnables;
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
      if( hasRunnables ) {
        wakeClient();
      }
    }
  }

  boolean hasRunnables() {
    synchronized( lock ) {
      return hasRunnables;
    }
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

  boolean mustBlockCallBackRequest() {
    boolean noPendingCallbackRequests = blockedCallBackRequests.isEmpty();
    boolean prevent = !waitForUIThread && !uiThreadRunning && hasRunnables;
    boolean isActive = !idManager.isEmpty();
    return isActive && noPendingCallbackRequests && !prevent;
  }

  boolean isUICallBackActive() {
    boolean result = !idManager.isEmpty();
    if( !result ) {
      result = hasRunnables();
    }
    return result;
  }

  public void activateUICallBacksFor( final String id ) {
    int size = idManager.add( id );
    if( size == 1 ) {
      UICallBackServiceHandler.registerUICallBackActivator();
    }
  }

  public void deactivateUICallBacksFor( final String id ) {
    // release blocked callback handler request
    int size = idManager.remove( id );
    if( size == 0 ) {
      releaseBlockedRequest();
    }
  }
}
