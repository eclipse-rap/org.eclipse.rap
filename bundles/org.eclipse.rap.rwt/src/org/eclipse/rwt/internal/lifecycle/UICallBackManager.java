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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.SerializableLock;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.internal.SerializableCompatibility;


public final class UICallBackManager implements SerializableCompatibility {

  private static final int DEFAULT_REQUEST_CHECK_INTERVAL = 30000;

  private static final String FORCE_UI_CALLBACK
    = UICallBackManager.class.getName() + "#forceUICallBack";
  
  private static class UnblockSessionStoreListener
    implements SessionStoreListener, SerializableCompatibility
  {
    private transient final Thread currentThread;

    private UnblockSessionStoreListener( Thread currentThread ) {
      this.currentThread = currentThread;
    }

    public void beforeDestroy( SessionStoreEvent event ) {
      currentThread.interrupt();
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
  private boolean wakeCalled;
  private int requestCheckInterval;

  private UICallBackManager() {
    lock = new SerializableLock();
    idManager = new IdManager();
    blockedCallBackRequests = new HashSet<Thread>();
    uiThreadRunning = false;
    waitForUIThread = false;
    wakeCalled = false;
    requestCheckInterval = DEFAULT_REQUEST_CHECK_INTERVAL;
  }

  public boolean isCallBackRequestBlocked() {
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
      wakeCalled = true;
      lock.notifyAll();
    }
  }

  public void setHasRunnables( boolean hasRunnables ) {
    synchronized( lock ) {
      this.hasRunnables = hasRunnables;
    }
    if( hasRunnables && isUICallBackActive() ) {
      ContextProvider.getStateInfo().setAttribute( FORCE_UI_CALLBACK, Boolean.TRUE );
    }
  }
  
  public void setRequestCheckInterval( int requestCheckInterval ) {
    this.requestCheckInterval = requestCheckInterval;
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

  void blockCallBackRequest() {
    synchronized( lock ) {
      if( blockedCallBackRequests.isEmpty() && mustBlockCallBackRequest() ) {
        Thread currentThread = Thread.currentThread();
        SessionStoreListener listener = new UnblockSessionStoreListener( currentThread );
        ISessionStore sessionStore = ContextProvider.getSession();
        sessionStore.addSessionStoreListener( listener );
        blockedCallBackRequests.add( currentThread );
        try {
          boolean keepWaiting = true;
          wakeCalled = false;
          while( !wakeCalled && keepWaiting ) {
            lock.wait( requestCheckInterval );
            keepWaiting 
              = mustBlockCallBackRequest() && isConnectionAlive( ContextProvider.getResponse() );
          }
        } catch( InterruptedException ie ) {
          Thread.interrupted(); // Reset interrupted state, see bug 300254
        } finally {
          blockedCallBackRequests.remove( currentThread );
          sessionStore.removeSessionStoreListener( listener );
        }
      }
      waitForUIThread = true;
    }
  }

  private static boolean isConnectionAlive( HttpServletResponse response ) {
    boolean result;
    try {
      JavaScriptResponseWriter responseWriter = new JavaScriptResponseWriter( response );
      responseWriter.write( " " );
      result = !responseWriter.checkError();
    } catch( IOException ioe ) {
      result = false;
    }
    return result;
  }

  boolean mustBlockCallBackRequest() {
    boolean prevent = !waitForUIThread && !uiThreadRunning && hasRunnables;
    boolean isActive = !idManager.isEmpty();
    return isActive && !prevent;
  }

  boolean isUICallBackActive() {
    return !idManager.isEmpty();
  }

  public void activateUICallBacksFor( final String id ) {
    idManager.add( id );
  }

  public void deactivateUICallBacksFor( final String id ) {
    int size = idManager.remove( id );
    if( size == 0 ) {
      releaseBlockedRequest();
    }
  }

  public boolean needsActivation() {
    boolean result;
    if( isCallBackRequestBlocked() ) {
      result = false;
    } else {
      result = isUICallBackActive() || forceUICallBackForPendingRunnables();
    }
    return result;
  }

  private static boolean forceUICallBackForPendingRunnables() {
    return Boolean.TRUE.equals( ContextProvider.getStateInfo().getAttribute( FORCE_UI_CALLBACK ) );
  }
}
