/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;


import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.lifecycle.*;


public class TextSizeMeasurementListener implements PhaseListener {
  private static final long serialVersionUID = 1L;
  
  private TextSizeRecalculation textSizeRecalculation;

  public TextSizeMeasurementListener() {
    textSizeRecalculation = new TextSizeRecalculation();
  }
  
  
  //////////////////////////
  // interface PhaseListener
  
  public void afterPhase( PhaseEvent event ) {
    if( isRenderPhase( event ) ) {
      handleMeasurementRequests();
    }
    if( isProcessActionPhase( event ) ) {
      handleMeasurementResults();
    }
  }
  
  public void beforePhase( PhaseEvent event ) {
    if( isPrepareUIRoot( event ) ) {
      handleStartupProbeMeasurementResults();
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.ANY;
  }
  
  
  //////////////////
  // helping methods
  
  private void handleStartupProbeMeasurementResults() {
    getMeasurementOperator().readMeasuredFontProbeSizes();
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
    return ( MeasurementOperator )SessionSingletonBase.getInstance( MeasurementOperator.class );
  }

  private boolean isProcessActionPhase( PhaseEvent event ) {
    return event.getPhaseId() == PhaseId.PROCESS_ACTION;
  }

  private boolean isRenderPhase( PhaseEvent event ) {
    return event.getPhaseId() == PhaseId.RENDER;
  }

  private boolean isPrepareUIRoot( PhaseEvent event ) {
    return event.getPhaseId() == PhaseId.PREPARE_UI_ROOT;
  }
}