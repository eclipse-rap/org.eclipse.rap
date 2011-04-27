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
package org.eclipse.rwt;

import org.eclipse.rwt.lifecycle.PhaseEvent;
import org.eclipse.rwt.lifecycle.PhaseId;


public class PhaseListenerUtil {

  public static PhaseEvent createReadDataEvent() {
    return createPhaseEvent( PhaseId.READ_DATA );
  }

  public static PhaseEvent createPrepareUIRootEvent() {
    return createPhaseEvent( PhaseId.PREPARE_UI_ROOT );
  }

  public static PhaseEvent createRenderEvent() {
    return createPhaseEvent( PhaseId.RENDER );
  }

  public static PhaseEvent createProcessActionEvent() {
    return createPhaseEvent( PhaseId.PROCESS_ACTION );
  }

  private static PhaseEvent createPhaseEvent( PhaseId phaseId ) {
    return new PhaseEvent( RWT.getLifeCycle(), phaseId );
  }
  
  private PhaseListenerUtil() {
    // prevent instance creation
  }
}
