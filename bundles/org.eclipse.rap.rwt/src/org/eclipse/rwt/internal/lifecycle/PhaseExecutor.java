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

import org.eclipse.rwt.lifecycle.PhaseId;


class PhaseExecutor {

  private final PhaseListenerManager phaseListenerManager;
  private final IPhase[] phases;

  PhaseExecutor( PhaseListenerManager phaseListenerManager, IPhase[] phases ) {
    this.phaseListenerManager = phaseListenerManager;
    this.phases = phases;
  }

  void execute( PhaseId startPhaseId ) throws IOException {
    PhaseId currentPhaseId = startPhaseId;
    while( currentPhaseId != null ) {
      IPhase currentPhase = findPhase( currentPhaseId );
      phaseListenerManager.notifyBeforePhase( currentPhaseId );
      PhaseId nextPhaseId = currentPhase.execute();
      phaseListenerManager.notifyAfterPhase( currentPhaseId );
      currentPhaseId = nextPhaseId;
    }
  }

  private IPhase findPhase( PhaseId phaseId ) {
    IPhase result = null;
    for( int i = 0; result == null && i < phases.length; i++ ) {
      if( phases[ i ].getPhaseId().equals( phaseId ) ) {
        result = phases[ i ];
      }
    }
    return result;
  }
}
