/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.LifeCycleControl.LifeCycleLock;
import org.eclipse.rwt.service.*;


public class RWTLifeCycleBlockControl {
  
  private static final ThreadLocal LOCK = new ThreadLocal();
  private static final String SKIP_RESPONSE
    = LifeCycleServiceHandler.class.getName() + ".SKIP_RESPONSE_WRITING";
  private static final String THROWABLE
    = RWTLifeCycleBlockControl.class.getName() + ".Throwable";
  
  
  private static class LockData {
    ServiceContext context;
    Thread thread;
    boolean onResume;
    boolean waitForTermination;
  }
  
  private static class AbortRequestProcessingError extends Error {
    private static final long serialVersionUID = 1L;
  }

  private final static class ServiceHandlerProcessor implements Runnable {
    private final ServiceContext context;
    private final Object lock;
    private final IServiceHandler serviceHandler;

    private ServiceHandlerProcessor( final ServiceContext context, 
                                     final Object lock,
                                     final IServiceHandler serviceHandler )
    {
      this.context = context;
      this.lock = lock;
      this.serviceHandler = serviceHandler;
    }

    public void run() {
      try {
        LOCK.set( lock );
        ContextProvider.setContext( context );
        serviceHandler.service();
      } catch( final RuntimeException rt ) {
        bufferThrowable( rt );
      } catch( final ServletException se ) {
        bufferThrowable( se );
      } catch( final IOException ioe ) {
        bufferThrowable( ioe );
      } catch( final AbortRequestProcessingError arpe ) {
        // do nothing
      } catch( final Throwable thr ) {
        bufferThrowable( thr );
      }
      finally {
        terminateResumeThread();
        synchronized( lock ) {
          LOCK.set( null );
          lock.notifyAll();
        }
      }
    }
    
    private void bufferThrowable( final Throwable thr ) {
      try {
        ISessionStore session = ContextProvider.getSession();
        HttpSession httpSession = session.getHttpSession();
        try {
          httpSession.setAttribute( THROWABLE, thr );
        } catch( final IllegalStateException ise ) {
          // ignore exceptions on invalidated sessions
        }
      } catch( final RuntimeException re ) {
        thr.printStackTrace();
        throw re;
      }
    }
    
    void handleException( final HttpSession session )
      throws ServletException, IOException
    {
      Object thr = session.getAttribute( THROWABLE );
      session.removeAttribute( THROWABLE );
      if( thr != null ) {
        if( thr instanceof RuntimeException ) {
          throw ( RuntimeException )thr;
        }
        if( thr instanceof ServletException ) {
          throw ( ServletException )thr;
        }
        if( thr instanceof IOException ) {
          throw ( IOException )thr;
        }
        if( thr instanceof Error ) {
          throw ( Error )thr;
        }
        String txt = "Unknown Error occured [{0}]: {1}";
        Object[] params = new Object[] { 
          thr.getClass().getName(), 
          ( ( Throwable )thr ).getMessage()
        };
        String msg = MessageFormat.format( txt, params );
        throw new IllegalStateException( msg );
      }
    }
  }
  
  /**
   * The response handler is used to continue request processing in a new 
   * <code>Thread</code> if the <code>LifeCycle</code>'s execution was blocked.
   * This is necessary since otherwise the whole session would be blocked.
   */
  private static final class ResponseHandler implements Runnable {
    private final ServiceContext context = ContextProvider.getContext();
    private final Object requestThreadLock;
    
    private ResponseHandler( final Object requestThreadLock ) {
      this.requestThreadLock = requestThreadLock;
    }

    public void run() {
      // use the context of the blocked thread to finish the 
      // lifecycle
      try {
        ContextProvider.setContext( context );
        Object[] toTerminate = processResumeQueue( 0 );
        RWTLifeCycle.setThread( Thread.currentThread() );
        finishLifeCycle();
        LifeCycleServiceHandler.writeOutput();
        synchronized( requestThreadLock ) {
          requestThreadLock.notifyAll();
        }
        terminateResumed( toTerminate );
      } catch( final Throwable throwable ) {
        // TODO Auto-generated catch block
        throwable.printStackTrace();
      } finally {
        terminateResumeThread();
      }
    }

    private void finishLifeCycle() throws IOException {
      RWTLifeCycle lifeCycle 
        = ( RWTLifeCycle )RWT.getLifeCycle();
      lifeCycle.afterPhaseExecution( PhaseId.PROCESS_ACTION );
      try {
        lifeCycle.executePhase( PhaseId.RENDER );
      } finally {
        lifeCycle.cleanUp();
      }
    }
  }

  

  public static void resume( final LifeCycleLock lock ) {
    getData( lock ).context = ContextProvider.getContext();
    getData( lock ).thread = Thread.currentThread();
    enqueue( lock );
  }

  public static void block( final LifeCycleLock lock ) {
    synchronized( lock ) {
      try {
        ResponseHandler responseHandler = new ResponseHandler( LOCK.get() );
        RWTLifeCycleThreadPool.execute( responseHandler, lock );
        // watchdog in case of session timeout
        final Thread blocked = Thread.currentThread();
        ISessionStore session = ContextProvider.getSession();
        SessionStoreListener watchDog = new SessionStoreListener() {
          public void beforeDestroy( final SessionStoreEvent event ) {
            blocked.interrupt();
          }
        };
        session.addSessionStoreListener( watchDog );
        
        lock.wait();
        session.removeSessionStoreListener( watchDog );
        // dispose the service context that is still stored
        // on the thread since it was blocked, before we could
        // add the context of the request that closed the window.
        ContextProvider.disposeContext();
        ContextProvider.setContext( getData( lock ).context );
        RWTLifeCycle.setThread( blocked );             
      } catch( final InterruptedException ie ) {
        throw new AbortRequestProcessingError();
      }
    }
  }

  public static Object newLockData() {
    return new LockData();
  }
  
  static void resumeBlocked() {
    ServiceContext context = ContextProvider.getContext();
    if( !context.isDisposed() ) {  
      List queue = getQueue();
      if( queue != null && queue.size() > 0 ) {
        LifeCycleLock lock = ( LifeCycleLock )queue.get( 0 );
        handleQueuedTermination( lock );
        handleResumeTermination( lock );
        if( !ContextProvider.getContext().isDisposed() ) {
          processResumeQueue( 1 );
          if( !ContextProvider.getContext().isDisposed() ) {
            RWTLifeCycle.setThread( Thread.currentThread() );
          }
        }
      }
    }
  }

  private static void handleResumeTermination( final LifeCycleLock lock ) {
    Thread thread = Thread.currentThread();
    if( !getData( lock ).onResume && getData( lock ).thread == thread ) {
      synchronized( lock ) {
        lock.notifyAll();
        try {
          getData( lock ).onResume = true;
          lock.wait();
        } catch( final InterruptedException e ) {
          throw new AbortRequestProcessingError();
        }
      }
    }
  }

  private static void handleQueuedTermination( final LifeCycleLock lock ) {
    if( getData( lock ).waitForTermination ) {
      synchronized( lock ) {
        lock.notifyAll();
        try {
          dequeue();
          lock.wait();
        } catch( InterruptedException e ) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      throw new AbortRequestProcessingError();
    }
  }
  
  static void service( final IServiceHandler serviceHandler )
    throws IOException, ServletException
  {
    ISessionStore session = ContextProvider.getSession();
    synchronized( session ) {
      final SessionStoreListener lock = aquireRequestLock();
      final ServiceContext context = ContextProvider.getContext();
      ServiceHandlerProcessor processor
        = new ServiceHandlerProcessor( context, lock, serviceHandler );
      synchronized( lock ) {
        try {
          RWTLifeCycleThreadPool.execute( processor, lock  );
          lock.wait();
        } catch( final InterruptedException e ) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } finally {
          if( session.isBound() ) {
            session.removeSessionStoreListener( lock );
          }
        }
      }
      processor.handleException( session.getHttpSession() );
    }
  }
  
  
  //////////////////
  // helping methods
  
  private static void sendSessionExpired() throws IOException {
    LifeCycleServiceHandler.initializeStateInfo();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter out = stateInfo.getResponseWriter();
    out.append( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " );
    out.append( "Transitional//EN\">" );
    out.startDocument();
    out.startElement( HTML.HEAD, null );
    out.startElement( HTML.TITLE, null );
    String text = "Session has expired!";
    out.writeText( text, null );
    out.endElement( HTML.TITLE );
    out.endElement( HTML.HEAD );
    out.startElement( HTML.BODY, null );
    out.writeText( text, null );
    out.endElement( HTML.BODY );
    out.endDocument();
  }
  
  private static SessionStoreListener aquireRequestLock() throws IOException {
    final SessionStoreListener[] result = new SessionStoreListener[ 1 ];
    result[ 0 ] = new SessionStoreListener() {
      public void beforeDestroy( final SessionStoreEvent event ) {
        synchronized( result[ 0 ] ) {
          if( !LifeCycleServiceHandler.isSessionRestart() ) {
            result[ 0 ].notify();
          }
        }
      }
    };
    ISessionStore session = ContextProvider.getSession();
    try {
      session.addSessionStoreListener( result[ 0 ] );
    } catch( final IllegalStateException ise ) {
      sendSessionExpired();
    }
    return result[ 0 ];
  }
  
  private static void terminateResumeThread() {
    ServiceContext context = ContextProvider.getContext();
    if( !context.isDisposed() ) {
      List queue = getQueue();
      if( queue != null ) {
        Object[] locks = queue.toArray();
        for( int i = 0; i < locks.length; i++ ) {
          LifeCycleLock lock = ( LifeCycleLock )locks[ i ];
          if( getData( lock ).waitForTermination ) {
            synchronized( lock ) {
              lock.notify();
            }
          } else {
            ContextProvider.disposeContext();
            getData( lock ).thread.interrupt();
          }
        }
      }
    }
  }

  private static LifeCycleLock dequeue() {
    List queue = getQueue();
    LifeCycleLock result = null;
    if( queue != null && queue.size() > 0 ) {
      result = ( LifeCycleLock )queue.remove( 0 );
    }
    return result;
  }

  private static List getQueue() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return ( List )stateInfo.getAttribute( SKIP_RESPONSE );
  }
  
  private static void enqueue( final LifeCycleLock lock ) {
    List queue = getQueue();
    if( queue == null ) {
      queue = new ArrayList();
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      stateInfo.setAttribute( SKIP_RESPONSE, queue );
    }
    queue.add( lock );
  }
  
  private static LockData getData( final LifeCycleLock lock ) {
    return ( LockData )lock.data;
  }
  

  private static void terminateResumed( final Object[] resumed ) {
    if( resumed != null ) {
      for( int i = 0; i < resumed.length; i++ ) {
        synchronized( resumed[ i ] ) {
          resumed[ i ].notify();
        }
      }
    }
  }
  private static Object[] processResumeQueue( final int start ) {
    Object[] result = null;
    List queue = getQueue();
    if( queue != null ) {
      result = queue.toArray();
      for( int i = start; i < result.length; i++ ) {
        LifeCycleLock lock = ( LifeCycleLock )result[ i ];
        LockData data = getData( lock );
        if( !data.waitForTermination && !data.onResume ) {
          synchronized( lock ) {
            data.waitForTermination = true;
            lock.notify();
            try {
              lock.wait();
            } catch( final InterruptedException e ) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
      }
    }
    return result;
  }
}
