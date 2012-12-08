/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;


public class MeasurementListener implements PhaseListener {

  private final TextSizeRecalculation textSizeRecalculation;

  public MeasurementListener() {
    textSizeRecalculation = new TextSizeRecalculation();
  }

  //////////////////////////
  // interface PhaseListener

  public void beforePhase( PhaseEvent event ) {
    if( event.getPhaseId() == PhaseId.PREPARE_UI_ROOT ) {
      handleStartupProbeMeasurementResults();
    }
    if( event.getPhaseId() == PhaseId.PROCESS_ACTION ) {
      handleMeasurementResults();
    }
  }

  public void afterPhase( PhaseEvent event ) {
    if( event.getPhaseId() == PhaseId.RENDER ) {
      handleMeasurementRequests();
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.ANY;
  }

  //////////////////
  // helping methods

  private void handleStartupProbeMeasurementResults() {
    getMeasurementOperator().handleStartupProbeMeasurementResults();
  }

  private void handleMeasurementRequests() {
    getMeasurementOperator().handleMeasurementRequests();
  }

  private void handleMeasurementResults() {
    if( getMeasurementOperator().handleMeasurementResults() ) {
      textSizeRecalculation.execute();
    }
  }

  private MeasurementOperator getMeasurementOperator() {
    return MeasurementOperator.getInstance();
  }

}
