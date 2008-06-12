/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.widgets.Display;



final class PrepareUIRoot implements IPhase {

  public PhaseId getPhaseID() {
    return PhaseId.PREPARE_UI_ROOT;
  }

  public PhaseId execute() {
    HttpServletRequest request = ContextProvider.getRequest();
    String startup = request.getParameter( RequestParams.STARTUP );
    PhaseId result;
    if( startup != null ) {
      TextSizeDetermination.readStartupProbes();
      EntryPointManager.createUI( startup );      
      result = PhaseId.RENDER;
    } else if( Display.getCurrent() == null ) {
      TextSizeDetermination.readStartupProbes();
      EntryPointManager.createUI( EntryPointManager.DEFAULT );
      result = PhaseId.RENDER;
    } else {
      result = PhaseId.READ_DATA;
    }
    return result;
  }
}
