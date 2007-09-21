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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

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
    private RuntimeException rtBuffer;
    private ServletException seBuffer;
    private IOException ioeBuffer;

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
        rtBuffer = rt;
      } catch( final ServletException se ) {
        seBuffer = se;
      } catch( final IOException ioe ) {
        ioeBuffer = ioe;
      } catch( final AbortRequestProcessingError arpe ) {
        // do nothing
      } finally {
        terminateResumeThread();
        synchronized( lock ) {
          LOCK.set( null );
          lock.notifyAll();
        }
      }
    }
    
    void handleException() throws ServletException, IOException {
      if( rtBuffer != null ) {
        throw rtBuffer;
      }
      if( seBuffer != null ) {
        throw seBuffer;
      }
      if( ioeBuffer != null ) {
        throw ioeBuffer;
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
        Object[] toTerminate = processResumeQueue();
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

    private void terminateResumed( final Object[] resumed ) {
      if( resumed != null ) {
        for( int i = 0; i < resumed.length; i++ ) {
          synchronized( resumed[ i ] ) {
            resumed[ i ].notify();
          }
        }
      }
    }

    private Object[] processResumeQueue() {
      Object[] result = null;
      List queue = getQueue();
      if( queue != null ) {
        result = queue.toArray();
        for( int i = 0; i < result.length; i++ ) {
          LifeCycleLock lock = ( LifeCycleLock )result[ i ];
          synchronized( lock ) {
            getData( lock ).waitForTermination = true;
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
      return result;
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
        lock.wait();
        // dispose the service context that is still stored
        // on the thread since it was blocked, before we could
        // add the context of the request that closed the window.
        ContextProvider.disposeContext();
        ContextProvider.setContext( getData( lock ).context );
        RWTLifeCycle.setThread( Thread.currentThread() );             
      } catch( final InterruptedException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public static Object newLockData() {
    return new LockData();
  }
  
  static void resumeBlocked() {
    List queue = getQueue();
    if( queue != null && queue.size() > 0 ) {
      LifeCycleLock lock = ( LifeCycleLock )queue.get( 0 );
      handleQueuedTermination( lock );
      handleResumeTermination( lock );
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
      final Object lock = aquireRequestLock();
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
        }
      }
      processor.handleException();
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
  
  private static Object aquireRequestLock() throws IOException {
    final Object result = new Object();
    ISessionStore session = ContextProvider.getSession();
    try {
      session.addSessionStoreListener( new SessionStoreListener() {
        public void beforeDestroy( final SessionStoreEvent event ) {
          synchronized( result ) {
            result.notify();
          }
        }
      } );
    } catch( final IllegalStateException ise ) {
      sendSessionExpired();
    }
    return result;
  }
  
  private static void terminateResumeThread() {
    ServiceContext context = ContextProvider.getContext();
    if( !context.isDisposed() ) {
      LifeCycleLock lock = dequeue();
      if( lock != null && !getData( lock ).waitForTermination ) {
        ContextProvider.disposeContext();
        getData( lock ).thread.interrupt();
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
}
