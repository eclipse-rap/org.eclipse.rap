/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import java.io.IOException;

import javax.servlet.http.*;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.textsize.TextSizeProbeStore.Probe;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;


final class MeasurementHandler implements PhaseListener, HttpSessionBindingListener {
  private static final long serialVersionUID = 1L;
  
  private final Display display;
  private final RecalculationEnforcement recalculationEnforcement;

  private MeasurementItem[] calculationItems;
  private boolean renderDone;
  private Probe[] probes;


  MeasurementHandler() {
    display = LifeCycleUtil.getSessionDisplay();
    recalculationEnforcement = new RecalculationEnforcement( display );
  }

  //////////////////////////
  // interface PhaseListener

  public void beforePhase( PhaseEvent event ) {
  }

  public void afterPhase( PhaseEvent event ) {
    if( beforeMeasurement( event ) ) {
      writeMeasurementContent();
    }
    if( afterMeasurement( event ) ) {
      applyMeasurementResults();
      MeasurementUtil.deregister();
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.ANY;
  }

  ///////////////////////////////////////
  // interface HttpSessionBindingListener

  public void valueBound( HttpSessionBindingEvent event ) {
  }

  public void valueUnbound( HttpSessionBindingEvent event ) {
    UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
      public void run() {
        ILifeCycle lifeCycle = RWTFactory.getLifeCycleFactory().getLifeCycle();
        lifeCycle.removePhaseListener( MeasurementHandler.this );
      }
    } );
  }

  //////////////////
  // helping methods
  
  static void readProbedFonts( Probe[] probes ) {
    boolean hasProbes = probes != null;
    HttpServletRequest request = ContextProvider.getRequest();
    for( int i = 0; hasProbes && i < probes.length; i++ ) {
      Probe probe = probes[ i ];
      String name = String.valueOf( probe.getFontData().hashCode() );
      String value = request.getParameter( name );
      if( value != null ) {
        Point size = getSize( value );
        TextSizeProbeResults.getInstance().createProbeResult( probe, size );
      }
    }
  }


  void readMeasuredStrings() {
    boolean hasItems = calculationItems != null;
    HttpServletRequest request = ContextProvider.getRequest();
    for( int i = 0; hasItems && i < calculationItems.length; i++ ) {
      MeasurementItem item = calculationItems[ i ];
      String name = String.valueOf( item.hashCode() );
      String value = request.getParameter( name );
      // TODO [fappel]: Workaround for background process problem
      if( value != null ) {
        Point size = getSize( value );
        TextSizeDataBase.store( item.getFontData(),
                                item.getTextToMeasure(),
                                item.getWrapWidth(),
                                size );
      }
    }
  }

  private boolean afterMeasurement( PhaseEvent event ) {
    return    requestBelongsToHandler() 
           && renderDone 
           && event.getPhaseId() == PhaseId.PROCESS_ACTION;
  }

  private boolean requestBelongsToHandler() {
    return display == LifeCycleUtil.getSessionDisplay();
  }

  private boolean beforeMeasurement( PhaseEvent event ) {
    return    requestBelongsToHandler()
           && event.getPhaseId() == PhaseId.RENDER;
  }

  private void applyMeasurementResults() {
    readProbedFonts( probes );
    readMeasuredStrings();
    recalculationEnforcement.execute();
  }


  private void writeMeasurementContent() {
    try {
      probes = TextSizeDeterminationFacade.writeFontProbing();
      calculationItems = TextSizeDeterminationFacade.writeStringMeasurements();
      renderDone = true;
    } catch( IOException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private static Point getSize( String value ) {
    String[] split = value.split( "," );
    return new Point( Integer.parseInt( split[ 0 ] ), Integer.parseInt( split[ 1 ] ) );
  }
}