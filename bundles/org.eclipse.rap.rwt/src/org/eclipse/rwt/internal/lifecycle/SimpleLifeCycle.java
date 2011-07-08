/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public class SimpleLifeCycle extends LifeCycle {

  private static final IPhase[] PHASES = new IPhase[] {
    new PrepareUIRoot(),
    new ReadData(),
    new ProcessAction(),
    new Render()
  };

  private static class SessionDisplayPhaseExecutor extends PhaseExecutor {
    
    SessionDisplayPhaseExecutor( PhaseListenerManager phaseListenerManager ) {
      super( phaseListenerManager, PHASES );
    }
    
    Display getDisplay() {
      return LifeCycleUtil.getSessionDisplay();
    }
  }

  private static class SimpleUIThreadHolder implements IUIThreadHolder {
    private final Thread thread;
  
    public SimpleUIThreadHolder( Thread thread ) {
      this.thread = thread;
    }
  
    public void updateServiceContext() {
      throw new UnsupportedOperationException();
    }
  
    public void terminateThread() {
      throw new UnsupportedOperationException();
    }
  
    public void switchThread() {
      throw new UnsupportedOperationException();
    }
  
    public void setServiceContext( ServiceContext serviceContext ) {
      throw new UnsupportedOperationException();
    }
  
    public Thread getThread() {
      return thread;
    }
  
    public Object getLock() {
      throw new UnsupportedOperationException();
    }
  }

  private static class SimpleSessionShutdownAdapter implements ISessionShutdownAdapter {
    private Runnable shutdownCallback;
    private ISessionStore sessionStore;

    public void setShutdownCallback( Runnable shutdownCallback ) {
      this.shutdownCallback = shutdownCallback;
    }
  
    public void setSessionStore( ISessionStore sessionStore ) {
      this.sessionStore = sessionStore;
    }
  
    public void interceptShutdown() {
      final Display display = LifeCycleUtil.getSessionDisplay( sessionStore );
      if( isDisplayActive( display ) && isApplicationContextActive() ) {
        FakeContextUtil.runNonUIThreadWithFakeContext( display, new Runnable() {
          public void run() {
            attachThread( display );
            CurrentPhase.set( PhaseId.PROCESS_ACTION );
            display.dispose();
          }
        } );
      }
      shutdownCallback.run();
    }

    public void processShutdown() {
      throw new UnsupportedOperationException();
    }
    
    private static boolean isDisplayActive( Display display ) {
      return display != null && !display.isDisposed();
    }
  
    private boolean isApplicationContextActive() {
      ApplicationContext applicationContext = ApplicationContextUtil.get( sessionStore );
      return applicationContext != null && applicationContext.isActivated();
    }
  }

  private final PhaseListenerManager phaseListenerManager;

  public SimpleLifeCycle() {
    phaseListenerManager = new PhaseListenerManager( this );
    phaseListenerManager.addPhaseListeners( RWTFactory.getPhaseListenerRegistry().get() );
  }

  public void execute() throws IOException {
    installSessionShutdownAdapter();
    attachThread( LifeCycleUtil.getSessionDisplay() );
    try {
      PhaseExecutor phaseExecutor = new SessionDisplayPhaseExecutor( phaseListenerManager );
      phaseExecutor.execute( PhaseId.PREPARE_UI_ROOT );
    } finally {
      detachThread( LifeCycleUtil.getSessionDisplay() );
    }
  }
  
  public void requestThreadExec( Runnable runnable ) {
    runnable.run();
  }

  public void addPhaseListener( PhaseListener phaseListener ) {
    phaseListenerManager.addPhaseListener( phaseListener );
  }

  public void removePhaseListener( PhaseListener phaseListener ) {
    phaseListenerManager.removePhaseListener( phaseListener );
  }
  
  public void sleep() {
    String msg = "The " + getClass().getSimpleName() + " does not support Display#sleep().";
    throw new UnsupportedOperationException( msg );
  }

  private static void installSessionShutdownAdapter() {
    SessionStoreImpl sessionStore = ( SessionStoreImpl )ContextProvider.getSession();
    if( sessionStore.getShutdownAdapter() == null ) {
      sessionStore.setShutdownAdapter( new SimpleSessionShutdownAdapter() );
    }
  }

  private static void attachThread( Display display ) {
    if( display != null ) {
      IDisplayAdapter displayAdapter = getDisplayAdapter( display );
      displayAdapter.attachThread();
      IUIThreadHolder uiThreadHolder = new SimpleUIThreadHolder( Thread.currentThread() );
      LifeCycleUtil.setUIThread( displayAdapter.getSessionStore(), uiThreadHolder );
    }
  }

  private static void detachThread( Display display ) {
    if( display != null ) {
      IDisplayAdapter displayAdapter = getDisplayAdapter( display );
      displayAdapter.detachThread();
      LifeCycleUtil.setUIThread( displayAdapter.getSessionStore(), null );
    }
  }
  
  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    return ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
  }
}
