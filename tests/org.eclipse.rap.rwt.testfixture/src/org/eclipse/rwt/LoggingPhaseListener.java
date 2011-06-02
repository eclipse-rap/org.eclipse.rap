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
package org.eclipse.rwt;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.rwt.lifecycle.*;

public class LoggingPhaseListener implements PhaseListener {
  private static final long serialVersionUID = 1L;
  
  public static class PhaseEventInfo {
    
    public final boolean before;
    public final Object source;
    public final PhaseId phaseId;
    
    PhaseEventInfo( PhaseEvent event, boolean before ) {
      this.source = event.getSource();
      this.phaseId = event.getPhaseId();
      this.before = before;
    }
  }
  
  private final PhaseId phaseId;
  private final List<PhaseEventInfo> eventLog;
  
  public LoggingPhaseListener( PhaseId phaseId ) {
    this.phaseId = phaseId;
    this.eventLog = new LinkedList<PhaseEventInfo>();
  }
  
  public void beforePhase( PhaseEvent event ) {
    eventLog.add( new PhaseEventInfo( event, true ) );
  }
  
  public void afterPhase( PhaseEvent event ) {
    eventLog.add( new PhaseEventInfo( event, false ) );
  }
  
  public PhaseId getPhaseId() {
    return phaseId;
  }
  
  public PhaseEventInfo[] getLoggedEvents() {
    PhaseEventInfo[] result = new PhaseEventInfo[ eventLog.size() ];
    eventLog.toArray( result );
    return result;
  }
}