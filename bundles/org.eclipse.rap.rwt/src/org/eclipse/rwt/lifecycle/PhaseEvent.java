/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

import java.util.EventObject;


/**
 * <p>This event is sent to the <code>PhaseListener</code> before and after
 * a lifecycle phase is executed.</p>
 * <p>The <code>getSource()</code> method returns an instance of type 
 * <code>ILifeCycle</code>.</p>
 * <p>This class is not intended to be instantiated by clients.</p>
 * 
 * @since 1.0
 */
public class PhaseEvent extends EventObject {
  
  private static final long serialVersionUID = 1L;
  
  private final PhaseId phaseId;

  /**
   * <p>Creates a PhaseEvent for the given lifecycle with the given 
   * <code>phaseId</code>.</p>
   * @param source the lifecycle which causes this event
   * @param phaseId the id of the phase that will be or was executed.
   */
  public PhaseEvent( final ILifeCycle source, final PhaseId phaseId ) {
    super( source );
    this.phaseId = phaseId;
  }

  /**
   * <p>Returns the id of the phase that will be or was executed.</p> 
   * 
   * @return the {@link PhaseId} of this event
   */
  public PhaseId getPhaseId() {
    return phaseId;
  }
}
