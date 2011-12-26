/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.uicallback;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.SerializableLock;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.internal.SerializableCompatibility;


public final class UICallBackManager implements SerializableCompatibility {

  private static final int DEFAULT_REQUEST_CHECK_INTERVAL = 30000;

  private static final String FORCE_UI_CALLBACK
    = UICallBackManager.class.getName() + "#forceUICallBack";

  public static UICallBackManager getInstance() {
    return ( UICallBackManager )SessionSingletonBase.getInstance( UICallBackManager.class );
  }

  private final CallBackActivationTracker callBackActivationTracker;

  private final SerializableLock lock;
  // Flag that indicates whether a request is processed. In that case no
  // notifications are sent to the client.
  private boolean uiThreadRunning;
  // indicates whether the display has runnables to execute
  private boolean hasRunnables;
  private int requestCheckInterval;
  private transient CallBackRequestTracker callBackRequestTracker;

  private UICallBackManager() {
    lock = new SerializableLock();
    callBackActivationTracker = new CallBackActivationTracker();
    uiThreadRunning = false;
    requestCheckInterval = DEFAULT_REQUEST_CHECK_INTERVAL;
    callBackRequestTracker = new CallBackRequestTracker();
  }

  public boolean isCallBackRequestBlocked() {
    synchronized( lock ) {
      return !callBackRequestTracker.hasActive();
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

  public void setHasRunnables( boolean hasRunnables ) {
    synchronized( lock ) {
      this.hasRunnables = hasRunnables;
    }
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    if( serviceStore != null && hasRunnables && isUICallBackActive() ) {
      serviceStore.setAttribute( FORCE_UI_CALLBACK, Boolean.TRUE );
    }
  }

  public void setRequestCheckInterval( int requestCheckInterval ) {
    this.requestCheckInterval = requestCheckInterval;
  }

  public void notifyUIThreadStart() {
    synchronized( lock ) {
      uiThreadRunning = true;
    }
  }

  public void notifyUIThreadEnd() {
    synchronized( lock ) {
      uiThreadRunning = false;
      if( hasRunnables ) {
        wakeClient();
      }
    }
  }

  public void activateUICallBacksFor( String id ) {
    callBackActivationTracker.activate( id );
  }

  public void deactivateUICallBacksFor( String id ) {
    callBackActivationTracker.deactivate( id );
    if( !callBackActivationTracker.isActive() ) {
      releaseBlockedRequest();
    }
  }

  boolean hasRunnables() {
    synchronized( lock ) {
      return hasRunnables;
    }
  }

  boolean processRequest( HttpServletResponse response ) {
    boolean result = true;
    synchronized( lock ) {
      if( isCallBackRequestBlocked() ) {
        releaseBlockedRequest();
      }
      if( mustBlockCallBackRequest() ) {
        long requestStartTime = System.currentTimeMillis();
        callBackRequestTracker.activate( Thread.currentThread() );
        SessionTerminationListener listener = attachSessionTerminationListener();
        try {
          boolean canRelease = false;
          while( !canRelease ) {
            lock.wait( requestCheckInterval );
            canRelease = canReleaseBlockedRequest( response, requestStartTime );
          }
          result = callBackRequestTracker.isActive( Thread.currentThread() );
          if( isSessionExpired( requestStartTime ) ) {
            result = false;
          }
        } catch( InterruptedException ie ) {
          result = false;
          Thread.interrupted(); // Reset interrupted state, see bug 300254
        } finally {
          listener.detach();
          callBackRequestTracker.deactivate( Thread.currentThread() );
        }
      }
    }
    return result;
  }

  private boolean canReleaseBlockedRequest( HttpServletResponse response, long requestStartTime ) {
    boolean result = false;
    if( !mustBlockCallBackRequest() ) {
      result = true;
    } else if( isSessionExpired( requestStartTime ) ) {
      result = true;
    } else if( !isConnectionAlive( response ) ) {
      result = true;
    } else if( !callBackRequestTracker.isActive( Thread.currentThread() ) ) {
      result = true;
    }
    return result;
  }

  boolean mustBlockCallBackRequest() {
    return isUICallBackActive() && !hasRunnables;
  }

  boolean isUICallBackActive() {
    return callBackActivationTracker.isActive();
  }

  boolean needsActivation() {
    return isUICallBackActive() || forceUICallBackForPendingRunnables();
  }

  private Object readResolve() {
    callBackRequestTracker = new CallBackRequestTracker();
    return this;
  }

  private static SessionTerminationListener attachSessionTerminationListener() {
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    SessionTerminationListener result = new SessionTerminationListener( sessionStore );
    result.attach();
    return result;
  }

  private static boolean isSessionExpired( long requestStartTime ) {
    return isSessionExpired( requestStartTime, System.currentTimeMillis() );
  }

  static boolean isSessionExpired( long requestStartTime, long currentTime ) {
    boolean result = false;
    HttpSession httpSession = ContextProvider.getSessionStore().getHttpSession();
    int maxInactiveInterval = httpSession.getMaxInactiveInterval();
    if( maxInactiveInterval > 0 ) {
      result = currentTime > requestStartTime + maxInactiveInterval * 1000;
    }
    return result;
  }

  private static boolean isConnectionAlive( HttpServletResponse response ) {
    boolean result;
    try {
      PrintWriter writer = response.getWriter();
      writer.write( " " );
      result = !writer.checkError();
    } catch( IOException ioe ) {
      result = false;
    }
    return result;
  }

  private static boolean forceUICallBackForPendingRunnables() {
    boolean result = false;
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    if( serviceStore != null ) {
      result = Boolean.TRUE.equals( serviceStore.getAttribute( FORCE_UI_CALLBACK ) );
    }
    return result;
  }

  private static class SessionTerminationListener
    implements SessionStoreListener, SerializableCompatibility
  {
    private transient final Thread currentThread;
    private transient final ISessionStore sessionStore;

    private SessionTerminationListener( ISessionStore sessionStore ) {
      this.sessionStore = sessionStore;
      currentThread = Thread.currentThread();
    }

    public void attach() {
      sessionStore.addSessionStoreListener( this );
    }

    public void detach() {
      sessionStore.removeSessionStoreListener( this );
    }

    public void beforeDestroy( SessionStoreEvent event ) {
      currentThread.interrupt();
    }
  }
}
