/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.IPhase.IInterruptible;
import org.eclipse.rwt.internal.lifecycle.UIThread.UIThreadTerminatedError;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.textsize.TextSizeDetermination;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.service.ISessionStore;

public class RWTLifeCycle extends LifeCycle {

  public static final String UI_THREAD
    = RWTLifeCycle.class.getName() + ".uiThread";
  private static final Integer ZERO = new Integer( 0 );

  private static final String CURRENT_PHASE
    = RWTLifeCycle.class.getName() + ".currentPhase";
  private static final String PHASE_ORDER
    = RWTLifeCycle.class.getName() + ".phaseOrder";
  private static final String UI_THREAD_THROWABLE
    = UIThreadController.class.getName() + "#UIThreadThrowable";
  private static final String REQUEST_THREAD_RUNNABLE
    = RWTLifeCycle.class.getName() + "#requestThreadRunnable";

  private static final IPhase[] PHASE_ORDER_STARTUP = new IPhase[] {
    new IInterruptible() {
      public PhaseId execute() throws IOException {
        return null;
      }
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    },
    new Render()
  };

  private static final IPhase[] PHASE_ORDER_SUBSEQUENT = new IPhase[] {
    new IPhase() {
      public PhaseId execute() throws IOException {
        return null;
      }
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    },
    new ReadData(),
    new IInterruptible() {
      public PhaseId execute() throws IOException {
        new ProcessAction().execute();
        return null;
      }
      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
    },
    new Render()
  };

  private static final class PhaseExecutionError extends ThreadDeath {
    private static final long serialVersionUID = 1L;
    public PhaseExecutionError( final Throwable cause ) {
      initCause( cause );
    }
  }

  private final class UIThreadController implements Runnable {
    public void run() {
      IUIThreadHolder uiThread = ( IUIThreadHolder )Thread.currentThread();
      // [rh] sync exception handling and switchThread (see bug 316676)
      synchronized( uiThread.getLock() ) {
        try {
          try {
            uiThread.updateServiceContext();
            UICallBackManager.getInstance().notifyUIThreadStart();
            continueLifeCycle();
            createUI();
            continueLifeCycle();
            UICallBackManager.getInstance().notifyUIThreadEnd();
          } catch( UIThreadTerminatedError thr ) {
            throw thr;
          } catch( Throwable thr ) {
            IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
            stateInfo.setAttribute( UI_THREAD_THROWABLE, thr );
          }
          // In any case: wait for the thread to be terminated by session timeout
          uiThread.switchThread();
        } catch( UIThreadTerminatedError e ) {
          // If we get here, the session is being invalidated, see
          // UIThread#terminateThread()
          ( ( ISessionShutdownAdapter )uiThread ).processShutdown();
        }
      }
    }
  }

  Runnable uiRunnable;
  private final PhaseListenerManager phaseListenerManager;

  public RWTLifeCycle() {
    phaseListenerManager = new PhaseListenerManager( this );
    phaseListenerManager.addPhaseListeners( RWTFactory.getPhaseListenerRegistry().get() );
    uiRunnable = new UIThreadController();
  }

  public void execute() throws IOException {
    if( getEntryPoint() != null ) {
      setPhaseOrder( PHASE_ORDER_STARTUP );
    } else {
      setPhaseOrder( PHASE_ORDER_SUBSEQUENT );
    }
    Runnable runnable = null;
    do {
      setRequestThreadRunnable( null );
      executeUIThread();
      runnable = getRequestThreadRunnable();
      if( runnable != null ) {
        runnable.run();
      }
    } while( runnable != null );
  }

  public void addPhaseListener( final PhaseListener listener ) {
    phaseListenerManager.addPhaseListener( listener );
  }

  public void removePhaseListener( final PhaseListener listener ) {
    phaseListenerManager.removePhaseListener( listener );
  }

  public Scope getScope() {
    return Scope.APPLICATION;
  }

  public void requestThreadExec( Runnable runnable ) {
    setRequestThreadRunnable( runnable );
    switchThread();
  }

  private static void setRequestThreadRunnable( final Runnable runnable ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( REQUEST_THREAD_RUNNABLE, runnable );
  }
  
  private static Runnable getRequestThreadRunnable() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return ( Runnable )stateInfo.getAttribute( REQUEST_THREAD_RUNNABLE );
  }


  //////////////////////////
  // readAndDispatch & sleep

  void continueLifeCycle() {
    int start = 0;
    IPhase[] phaseOrder = getPhaseOrder();
    if( phaseOrder != null ) {
      Integer currentPhase = getCurrentPhase();
      if( currentPhase != null ) {
        int phaseIndex = currentPhase.intValue();
        // A non-null currentPhase indicates that an IInterruptible phase
        // was executed before. In this case we now need to execute the
        // AfterPhase events
        phaseListenerManager.notifyAfterPhase( phaseOrder[ phaseIndex ].getPhaseId() );
        start = currentPhase.intValue() + 1;
      }
      boolean interrupted = false;
      for( int i = start; !interrupted && i < phaseOrder.length; i++ ) {
        IPhase phase = phaseOrder[ i ];
        phaseListenerManager.notifyBeforePhase( phase.getPhaseId() );
        if( phase instanceof IInterruptible ) {
          // IInterruptible phases return control to the user code, thus
          // they don't call Phase#execute()
          IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
          stateInfo.setAttribute( CURRENT_PHASE, new Integer( i ) );
          interrupted = true;
        } else {
          try {
            phase.execute();
          } catch( Throwable e ) {
            // Wrap exception in a ThreadDeath-derived error to break out of
            // the application call stack
            throw new PhaseExecutionError( e );
          }
          phaseListenerManager.notifyAfterPhase( phase.getPhaseId() );
        }
      }
      if( !interrupted ) {
        ContextProvider.getStateInfo().setAttribute( CURRENT_PHASE, null );
      }
    }
  }


  static int createUI() {
    int result = -1;
    if( ZERO.equals( getCurrentPhase() ) ) {
      String startup = getEntryPoint();
      if( startup != null ) {
        TextSizeDetermination.readStartupProbes();
        result = RWTFactory.getEntryPointManager().createUI( startup );
      }
    }
    return result;
  }

  void executeUIThread() throws IOException {
    ServiceContext context = ContextProvider.getContext();
    ISessionStore session = ContextProvider.getSession();
    IUIThreadHolder uiThread = getUIThreadHolder();
    if( uiThread == null ) {
      uiThread = createUIThread();
      // The serviceContext MUST be set before thread.start() is called
      uiThread.setServiceContext( context );
      synchronized( uiThread.getLock() ) {
        uiThread.getThread().start();
        uiThread.switchThread();
      }
    } else {
      uiThread.setServiceContext( context );
      uiThread.switchThread();
    }
    // TODO [rh] consider moving this to UIThreadController#run
    if( !uiThread.getThread().isAlive() ) {
      session.setAttribute( UI_THREAD, null );
    }
    handleUIThreadException();
  }

  private static void handleUIThreadException() throws IOException {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Throwable throwable
      = ( Throwable )stateInfo.getAttribute( UI_THREAD_THROWABLE );
    if( throwable != null ) {
      if( throwable instanceof PhaseExecutionError ) {
        throwable = throwable.getCause();
      }
      if( throwable instanceof IOException ) {
        throw ( IOException )throwable;
      } else if( throwable instanceof RuntimeException ) {
        throw ( RuntimeException )throwable;
      } else if( throwable instanceof Error ) {
        throw ( Error )throwable;
      } else {
        throw new RuntimeException( throwable );
      }
    }
  }

  public void sleep() {
    continueLifeCycle();
    IUIThreadHolder uiThread = getUIThreadHolder();
    UICallBackManager.getInstance().notifyUIThreadEnd();
    uiThread.switchThread();
    uiThread.updateServiceContext();
    UICallBackManager.getInstance().notifyUIThreadStart();
    continueLifeCycle();
  }

  private IUIThreadHolder createUIThread() {
    ISessionStore session = ContextProvider.getSession();
    IUIThreadHolder result = new UIThread( uiRunnable );
    result.getThread().setDaemon( true );
    result.getThread().setName( "UIThread [" + session.getId() + "]" );
    session.setAttribute( UI_THREAD, result );
    setShutdownAdapter( ( ISessionShutdownAdapter )result );
    return result;
  }

  private static void switchThread() {
    ISessionStore session = ContextProvider.getSession();
    IUIThreadHolder uiThreadHolder
      = ( IUIThreadHolder )session.getAttribute( UI_THREAD );
    uiThreadHolder.switchThread();
  }
  
  private static Integer getCurrentPhase() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return ( Integer )stateInfo.getAttribute( CURRENT_PHASE );
  }

  private static String getEntryPoint() {
    return LifeCycleUtil.getEntryPoint();
  }

  private static void setShutdownAdapter( ISessionShutdownAdapter adapter ) {
    ISessionStore sessionStore = ContextProvider.getSession();
    SessionStoreImpl sessionStoreImpl = ( SessionStoreImpl )sessionStore;
    sessionStoreImpl.setShutdownAdapter( adapter );
  }

  public void setPhaseOrder( final IPhase[] phaseOrder ) {
    IServiceStateInfo stateInfo = ContextProvider.getContext().getStateInfo();
    stateInfo.setAttribute( PHASE_ORDER, phaseOrder );
  }

  IPhase[] getPhaseOrder() {
    IServiceStateInfo stateInfo = ContextProvider.getContext().getStateInfo();
    return ( IPhase[] )stateInfo.getAttribute( PHASE_ORDER );
  }

  public static IUIThreadHolder getUIThreadHolder() {
    ISessionStore session = ContextProvider.getSession();
    return ( IUIThreadHolder )session.getAttribute( UI_THREAD );
  }
}