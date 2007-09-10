package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.LifeCycleServiceHandlerSync;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.internal.util.URLHelper;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.LifeCycleControl.LifeCycleLock;
import org.eclipse.rwt.service.ISessionStore;

/**
 * TODO [fappel]: documentation
 */
public final class RWTLifeCycleServiceHandlerSync
  extends LifeCycleServiceHandlerSync
{
  private static final String RESTART = "restart";
  private static final String SKIP_RESPONSE
    = LifeCycleServiceHandler.class.getName() + ".SKIP_RESPONSE_WRITING";
  private static final String RESTART_VALUE
    = RWTLifeCycleServiceHandlerSync.class.getName() + ".RESTART_VALUE";
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
        LifeCycleServiceHandler.initializeStateInfo();
        RWTRequestVersionControl.determine();
        if( isCloseRequested() ) {
          // notification that the user has unloaded the client document
          // nothing to do here
        } else if( LifeCycleServiceHandler.isSessionRestart() ) {
          // user requests reload of startup page
          if( isRestartAllowed() ) {
            // reload is requested after the unload notification and/or the
            // user explicitly requests a restart
            RWTRequestVersionControl.increase();
            markAsExpired();
            doService();
          } else {
            // user tries to load another instance of the startup document in
            // the current session, which can't be handled by RAP. Send
            // multiple clients warning page.
            sendMultipleClientsPerSessionPage();
          }
          resetCloseRequestedState();
        } else {
          // standard request service handling
          if( RWTRequestVersionControl.check() ) {
            doService();
          } else {
            int sc = HttpServletResponse.SC_PRECONDITION_FAILED;
            ContextProvider.getResponse().setStatus( sc );
            sendMultipleClientsPerSessionPage();            
          }
        }
        RWTRequestVersionControl.store();
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

    private boolean isCloseRequested() {
      boolean result = false;
      HttpServletRequest request = ContextProvider.getRequest();
      String value = request.getParameter( RequestParams.CLOSE_REQUESTED );
      if( value != null ) {
        HttpSession session = ContextProvider.getSession().getHttpSession();
        session.setAttribute( RequestParams.CLOSE_REQUESTED, value );
        result = true;
      }
      return result;
    }

    private void resetCloseRequestedState() {
      ISessionStore session = ContextProvider.getSession();
      session.setAttribute( RequestParams.CLOSE_REQUESTED, null );
    }

    private boolean isRestartAllowed() {
      ISessionStore session = ContextProvider.getSession();
      HttpSession httpSession = session.getHttpSession();
      HttpServletRequest request = ContextProvider.getRequest();
      String closeRequested = RequestParams.CLOSE_REQUESTED;
      String restart = request.getParameter( RESTART );
      String expected = ( String )session.getAttribute( RESTART_VALUE );
      session.removeAttribute( RESTART_VALUE );
      return    httpSession.getAttribute( closeRequested ) != null
             || restart != null && restart.equals( expected );
    }

    
    private void sendMultipleClientsPerSessionPage() throws IOException {
      // TODO [fappel]: I18n, branding of this page...
      markAsExpired();
      
      LifeCycleServiceHandler.initializeStateInfo();
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      HtmlResponseWriter out = stateInfo.getResponseWriter();
      out.startDocument();
      out.startElement( HTML.HEAD, null );
      out.startElement( HTML.TITLE, null );
      String title = "RAP Multiple Clients Warning";
      out.writeText( title, null );
      out.endElement( HTML.TITLE );
      out.endElement( HTML.HEAD );
      out.startElement( HTML.BODY, null );
      out.startElement( "H3", null );
      out.writeText( title, null );
      out.endElement( "H3" );
      out.startElement( "P", null );
      StringBuffer msg = new StringBuffer();
      msg.append( "RAP does not support multiple browser-instances or " );
      msg.append( "browser-tabs per session. If you have already closed " );
      msg.append( "the browser/tab with which you have started the session," );
      msg.append( "you may click the link below to restart the session from " );
      msg.append( "scratch." );
      out.writeText( msg, null );
      out.endElement( "P" );
      out.startElement( HTML.A, null );
      StringBuffer url = new StringBuffer();
      url.append( URLHelper.getURLString( false ) );
      String entryPoint = EntryPointManager.getCurrentEntryPoint();
      URLHelper.appendFirstParam( url, RequestParams.STARTUP, entryPoint );
      // TODO [fappel]: think about a better solution. The restart value
      //                avoids that FireFox loads the page from cache...
      String restartValue = String.valueOf( new Object().hashCode() );
      ContextProvider.getSession().setAttribute( RESTART_VALUE, restartValue );
      URLHelper.appendParam( url, RESTART, restartValue );
      out.writeAttribute( HTML.HREF, url, null );
      out.writeText( "Click here to restart session.", null );
      out.endElement( HTML.A );
      out.endElement( HTML.BODY );
      out.endDocument();
      LifeCycleServiceHandler.writeOutput();
    }

    private void markAsExpired() {
      HttpServletResponse response = ContextProvider.getResponse();
      response.setHeader( "Pragma", "no-cache" );
      response.setHeader( "Cache-Control", "no-cache" );
      response.setDateHeader( "Expires", 0 );
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

  static void terminateResumeThread() {
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