package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import javax.servlet.ServletException;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.LifeCycleServiceHandlerSync;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.LifeCycleControl.LifeCycleLock;

/**
 * TODO [fappel]: documentation
 */
// TODO [fappel]: No request synchronization for the RAP lifecycle, since
// the client side js mechanism ensures that only one request
// at a time can be sent. Check other cases than page refresh
// etc. The old W4T synchronization mechanism caused problems
// with the jface dialog handling.
public final class RWTLifeCycleServiceHandlerSync
  extends LifeCycleServiceHandlerSync
{

  private static final String SKIP_RESPONSE
    = LifeCycleServiceHandler.class.getName() + ".SKIP_RESPONSE_WRITING";
  private static final ThreadLocal LOCK = new ThreadLocal();
  
  
  /**
   * The response handler is used to continue request processing in a new 
   * <code>Thread</code> if the <code>LifeCycle</code>'s execution was blocked.
   * This is necessary since otherwise the whole session would be blocked.
   */
  private static final class ResponseHandler implements Runnable {
    private final ServiceContext context = ContextProvider.getContext();
    private final Object requestThreadLock = LOCK.get();

    public void run() {
      // use the context of the blocked thread to finish the 
      // lifecycle
      ContextProvider.setContext( context );
      RWTLifeCycle.setThread( Thread.currentThread() );
      try {
        finishLifeCycle();
        LifeCycleServiceHandler.writeOutput();
        synchronized( requestThreadLock ) {
          requestThreadLock.notify();
        }
      } catch( final Throwable throwable ) {
        // TODO Auto-generated catch block
        throwable.printStackTrace();
      }
    }

    private void finishLifeCycle() {
      RWTLifeCycle lifeCycle 
        = ( RWTLifeCycle )RWT.getLifeCycle();
      lifeCycle.afterPhaseExecution( PhaseId.PROCESS_ACTION );
      try {
        lifeCycle.executePhase( PhaseId.RENDER );
      } catch( final Throwable throwable ) {
        handleException( throwable );
      } finally {
        lifeCycle.cleanUp();
      }
    }
  }
  
  private static class AbortRequestProcessingError extends Error {
    private static final long serialVersionUID = 1L;
  }


  public static void resume( final LifeCycleLock lock ) {
    lock.context = ContextProvider.getContext();
    synchronized( lock ) {
      lock.notify();
      try {
        IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
        String key = SKIP_RESPONSE;
        stateInfo.setAttribute( key, Thread.currentThread() );
        lock.wait();
      } catch( InterruptedException e ) {
        throw new AbortRequestProcessingError();
      }
    }
  }

  public static void block( final LifeCycleLock lock ) {
    Thread thread = new Thread( new ResponseHandler(), "ResponseOnBlockedLC" );
    thread.setDaemon( true );
    thread.start();
    synchronized( lock ) {
      try {
        lock.wait();
        // dispose the service context that is still stored
        // on the thread since it was blocked, before we could
        // add the context of the request that closed the window.
        ContextProvider.disposeContext();
        ContextProvider.setContext( lock.context );
        RWTLifeCycle.setThread( Thread.currentThread() );             
      } catch( final InterruptedException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  static void handleException( final Throwable throwable ) {
    if( throwable instanceof AbortRequestProcessingError ) {
      throw ( AbortRequestProcessingError )throwable;
    }
    // TODO: [fappel] introduce proper exception handling
    throwable.printStackTrace();
    if( throwable instanceof RuntimeException ) {
      throw ( RuntimeException )throwable;
    }
    String msg = "An error occured while executing RWTLifeCycle.";
    throw new RuntimeException( msg, throwable );
  }

  public void service() throws ServletException, IOException {
    synchronized( ContextProvider.getSession() ) {
      final ServletException[] seBuffer = new ServletException[ 1 ];
      final IOException[] ioeBuffer = new IOException[ 1 ];
      final Object lock = new Object();
      final ServiceContext context = ContextProvider.getContext();
  
      // TODO [fappel]: introduce thread pooling
      Thread lifeCycleWorker = new Thread( new Runnable() {
        public void run() {
          try {
            LOCK.set( lock );
            ContextProvider.setContext( context );
            doService();
          } catch( final ServletException se ) {
            seBuffer[ 0 ] = se;
          } catch( final IOException ioe ) {
            ioeBuffer[ 0 ] = ioe;
          } catch( AbortRequestProcessingError arpe ) {
            // do nothing
          } finally {
            ServiceContext context = ContextProvider.getContext();
            if( !context.isDisposed() ) {
              IServiceStateInfo stateInfo = context.getStateInfo();
              Thread thread = ( Thread )stateInfo.getAttribute( SKIP_RESPONSE );
              if( thread != null ) {
                ContextProvider.disposeContext();
                thread.interrupt();
              }
            }
            synchronized( lock ) {
              LOCK.set( null );
              lock.notify();
            }
          }
        }
      } );
      lifeCycleWorker.setDaemon( true );
      lifeCycleWorker.start();
      synchronized( lock ) {
        try {
          lock.wait();
        } catch( final InterruptedException e ) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if( seBuffer[ 0 ] != null ) {
        throw seBuffer[ 0 ];
      }
      if( ioeBuffer[ 0 ] != null ) {
        throw ioeBuffer[ 0 ];
      }
    }
  }
}