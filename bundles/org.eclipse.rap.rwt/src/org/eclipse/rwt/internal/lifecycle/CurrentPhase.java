/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.*;



public final class CurrentPhase {
  
  public static final class Listener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    public void beforePhase( final PhaseEvent event ) {
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      stateInfo.setAttribute( ATTR_CURRENT_PHASE, event.getPhaseId() );
    }

    public void afterPhase( final PhaseEvent event ) {
      // do nothing
    }
    
    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }
  }

  private static final String ATTR_CURRENT_PHASE 
    = CurrentPhase.class.getName() + "#value";

  private CurrentPhase() {
    // prevent instantiation
  }
  
  public static PhaseId get() {
    PhaseId result = null;
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    if( stateInfo != null ) {
      result = ( PhaseId )stateInfo.getAttribute( ATTR_CURRENT_PHASE );
    }
    return result;
  }
}
