/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.IPhase.IInterruptible;
import org.eclipse.rwt.internal.lifecycle.UIThread.UIThreadTerminatedError;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.internal.uicallback.UICallBackManager;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.service.IServiceStore;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;


public class RWTLifeCycle extends LifeCycle {

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
      public PhaseId execute( Display display ) throws IOException {
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
      public PhaseId execute( Display display ) throws IOException {
        return null;
      }
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    },
    new ReadData(),
    new IInterruptible() {
      public PhaseId execute( Display display ) throws IOException {
        new ProcessAction().execute( display );
        return null;
      }
      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
    },
    new Render()
  };

  private final EntryPointManager entryPointManager;
  private final PhaseListenerManager phaseListenerManager;
  Runnable uiRunnable;

  public RWTLifeCycle( EntryPointManager entryPointManager ) {
    super( entryPointManager );
    this.entryPointManager = entryPointManager;
    phaseListenerManager = new PhaseListenerManager( this );
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

  public void addPhaseListener( PhaseListener listener ) {
    phaseListenerManager.addPhaseListener( listener );
  }

  public void removePhaseListener( PhaseListener listener ) {
    phaseListenerManager.removePhaseListener( listener );
  }

  public void requestThreadExec( Runnable runnable ) {
    setRequestThreadRunnable( runnable );
    getUIThreadHolder().switchThread();
  }

  private static void setRequestThreadRunnable( Runnable runnable ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( REQUEST_THREAD_RUNNABLE, runnable );
  }

  private static Runnable getRequestThreadRunnable() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    return ( Runnable )serviceStore.getAttribute( REQUEST_THREAD_RUNNABLE );
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
        // A non-null currentPhase indicates that an IInterruptible phase was executed before. In
        // this case we now need to execute the AfterPhase events
        phaseListenerManager.notifyAfterPhase( phaseOrder[ phaseIndex ].getPhaseId() );
        start = currentPhase.intValue() + 1;
      }
      boolean interrupted = false;
      for( int i = start; !interrupted && i < phaseOrder.length; i++ ) {
        IPhase phase = phaseOrder[ i ];
        phaseListenerManager.notifyBeforePhase( phase.getPhaseId() );
        if( phase instanceof IInterruptible ) {
          // IInterruptible phases return control to the user code, thus they don't call
          // Phase#execute()
          IServiceStore serviceStore = ContextProvider.getServiceStore();
          serviceStore.setAttribute( CURRENT_PHASE, new Integer( i ) );
          interrupted = true;
        } else {
          try {
            phase.execute( LifeCycleUtil.getSessionDisplay() );
          } catch( Throwable e ) {
            // Wrap exception in a ThreadDeath-derived error to break out of the application
            // call stack
            throw new PhaseExecutionError( e );
          }
          phaseListenerManager.notifyAfterPhase( phase.getPhaseId() );
        }
      }
      if( !interrupted ) {
        ContextProvider.getServiceStore().setAttribute( CURRENT_PHASE, null );
      }
    }
  }


  int createUI() {
    int result = -1;
    if( ZERO.equals( getCurrentPhase() ) ) {
      String startup = getEntryPoint();
      if( startup != null ) {
        result = entryPointManager.createUI( startup );
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
      LifeCycleUtil.setUIThread( session, null );
    }
    handleUIThreadException();
  }

  private static void handleUIThreadException() throws IOException {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    Throwable throwable = ( Throwable )serviceStore.getAttribute( UI_THREAD_THROWABLE );
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
    LifeCycleUtil.setUIThread( session, result );
    setShutdownAdapter( ( ISessionShutdownAdapter )result );
    return result;
  }

  private static Integer getCurrentPhase() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    return ( Integer )serviceStore.getAttribute( CURRENT_PHASE );
  }

  private static String getEntryPoint() {
    return LifeCycleUtil.getEntryPoint();
  }

  private static void setShutdownAdapter( ISessionShutdownAdapter adapter ) {
    ISessionStore sessionStore = ContextProvider.getSession();
    SessionStoreImpl sessionStoreImpl = ( SessionStoreImpl )sessionStore;
    sessionStoreImpl.setShutdownAdapter( adapter );
  }

  public void setPhaseOrder( IPhase[] phaseOrder ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( PHASE_ORDER, phaseOrder );
  }

  IPhase[] getPhaseOrder() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    return ( IPhase[] )serviceStore.getAttribute( PHASE_ORDER );
  }

  public static IUIThreadHolder getUIThreadHolder() {
    return LifeCycleUtil.getUIThread( ContextProvider.getSession() );
  }

  private static final class PhaseExecutionError extends ThreadDeath {
    public PhaseExecutionError( Throwable cause ) {
      initCause( cause );
    }
  }

  private final class UIThreadController implements Runnable {
    public void run() {
      IUIThreadHolder uiThread = ( IUIThreadHolder )Thread.currentThread();
      try {
        // [rh] sync exception handling and switchThread (see bug 316676)
        synchronized( uiThread.getLock() ) {
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
            IServiceStore serviceStore = ContextProvider.getServiceStore();
            serviceStore.setAttribute( UI_THREAD_THROWABLE, thr );
          }
          // In any case: wait for the thread to be terminated by session timeout
          uiThread.switchThread();
        }
      } catch( UIThreadTerminatedError e ) {
        // If we get here, the session is being invalidated, see UIThread#terminateThread()
        ( ( ISessionShutdownAdapter )uiThread ).processShutdown();
      }
    }
  }
}