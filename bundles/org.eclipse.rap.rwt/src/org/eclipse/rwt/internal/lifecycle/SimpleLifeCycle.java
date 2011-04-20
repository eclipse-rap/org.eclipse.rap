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

import org.eclipse.rwt.internal.engine.RWTFactory;
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
  
  private final PhaseListenerManager phaseListenerManager;

  public SimpleLifeCycle() {
    phaseListenerManager = new PhaseListenerManager( this );
    phaseListenerManager.addPhaseListeners( RWTFactory.getPhaseListenerRegistry().get() );
  }

  public Scope getScope() {
    return Scope.APPLICATION;
  }

  public void execute() throws IOException {
    attachThread();
    try {
      PhaseExecutor phaseExecutor = new PhaseExecutor( phaseListenerManager, PHASES );
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
  }

  private static void detachThread() {
    IDisplayAdapter displayAdapter = getDisplayAdapter();
    if( displayAdapter != null ) {
      displayAdapter.detachThread();
    }
  }

  private static IDisplayAdapter getDisplayAdapter() {
    IDisplayAdapter displayAdapter = null;
    Display display = RWTLifeCycle.getSessionDisplay();
    if( display != null ) {
      displayAdapter = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    }
    return displayAdapter;
  }
}
