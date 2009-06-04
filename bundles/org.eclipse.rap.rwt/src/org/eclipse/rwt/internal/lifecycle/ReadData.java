/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.widgets.Display;


final class ReadData implements IPhase {

  public PhaseId getPhaseID() {
    return PhaseId.READ_DATA;
  }

  public PhaseId execute() {
    Display display = RWTLifeCycle.getSessionDisplay();
    DisplayUtil.getLCA( display ).readData( display );
    return PhaseId.PROCESS_ACTION;
  }
}
