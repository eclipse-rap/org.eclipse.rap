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
public final class RWTLifeCycleServiceHandlerSync
  extends LifeCycleServiceHandlerSync
{

  private static final String SKIP_RESPONSE
    = LifeCycleServiceHandler.class.getName() + ".SKIP_RESPONSE_WRITING";
  private static final ThreadLocal LOCK = new ThreadLocal();
  
  
  /**
   * The <code>ServiceRunnable</code> triggers the actual lifecycle processing
   * of the lifecycle service handler. It should be processed in its own 
   * thread. After the thread has terminated the 
   * <code>{@link #handleException()}</code> method must be called to ensure
   * that any exception occured during service execution are rethrown.
   */
  private final class ServiceRunnable implements Runnable {
    private final ServiceContext context;
    private final Object lock;
    private RuntimeException rtBuffer;
    private ServletException seBuffer;
    private IOException ioeBuffer;

    private ServiceRunnable( final ServiceContext context, final Object lock ) {
      this.context = context;
      this.lock = lock;
    }
    
    public void run() {
      try {
        LOCK.set( lock );
        ContextProvider.setContext( context );
        doService();
      } catch( final RuntimeException rt ) {
        rtBuffer = rt;
      } catch( final ServletException se ) {
        seBuffer = se;
      } catch( final IOException ioe ) {
        ioeBuffer = ioe;
      } catch( AbortRequestProcessingError arpe ) {
        // do nothing
      } finally {
        terminateResumeThread();
        synchronized( lock ) {
          LOCK.set( null );
          lock.notify();
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
      } catch( final InterruptedException e ) {
        throw new AbortRequestProcessingError();
      }
    }
  }

  public static void block( final LifeCycleLock lock ) {
    String id = "ResponseOnBlockedLC" + lock.hashCode();
    Thread thread = new Thread( new ResponseHandler(), id );
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

  public void service() throws ServletException, IOException {
    synchronized( ContextProvider.getSession() ) {
      final Object lock = new Object();
      final ServiceContext context = ContextProvider.getContext();
  
      // TODO [fappel]: introduce thread pooling.
      // TODO [fappel]: dispose of thread in case it's locked and session
      //                gets invalidated.
      String id = "LifeCycleWorker." + lock.hashCode();
      ServiceRunnable serviceRunnable = new ServiceRunnable( context, lock );
      Thread lifeCycleWorker = new Thread( serviceRunnable, id );
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
      serviceRunnable.handleException();
    }
  }

  private static void terminateResumeThread() {
    ServiceContext context = ContextProvider.getContext();
    if( !context.isDisposed() ) {
      IServiceStateInfo stateInfo = context.getStateInfo();
      Thread thread = ( Thread )stateInfo.getAttribute( SKIP_RESPONSE );
      if( thread != null ) {
        ContextProvider.disposeContext();
        thread.interrupt();
      }
    }
  }
}