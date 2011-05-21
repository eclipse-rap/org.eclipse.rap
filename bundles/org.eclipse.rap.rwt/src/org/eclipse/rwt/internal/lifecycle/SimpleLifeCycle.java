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

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
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
    }
  
    public void terminateThread() {
    }
  
    public void switchThread() {
    }
  
    public void setServiceContext( ServiceContext serviceContext ) {
    }
  
    public Thread getThread() {
      return thread;
    }
  
    public Object getLock() {
      return null;
    }
  }

  private final PhaseListenerManager phaseListenerManager;

  public SimpleLifeCycle() {
    phaseListenerManager = new PhaseListenerManager( this );
    phaseListenerManager.addPhaseListeners( RWTFactory.getPhaseListenerRegistry().get() );
  }

  public void execute() throws IOException {
    attachThread();
    try {
      PhaseExecutor phaseExecutor = new SessionDisplayPhaseExecutor( phaseListenerManager );
      phaseExecutor.execute( PhaseId.PREPARE_UI_ROOT );
    } finally {
      detachThread();
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

  private static void attachThread() {
    IDisplayAdapter displayAdapter = getDisplayAdapter();
    if( displayAdapter != null ) {
      displayAdapter.attachThread();
    }
    IUIThreadHolder uiThreadHolder = new SimpleUIThreadHolder( Thread.currentThread() );
    LifeCycleUtil.setUIThread( RWT.getSessionStore(), uiThreadHolder );
  }

  private static void detachThread() {
    IDisplayAdapter displayAdapter = getDisplayAdapter();
    if( displayAdapter != null ) {
      displayAdapter.detachThread();
    }
    LifeCycleUtil.setUIThread( RWT.getSessionStore(), null );
  }
  
  private static IDisplayAdapter getDisplayAdapter() {
    IDisplayAdapter result = null;
    Display display = LifeCycleUtil.getSessionDisplay();
    if( display != null ) {
      result = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    }
    return result;
  }
}
