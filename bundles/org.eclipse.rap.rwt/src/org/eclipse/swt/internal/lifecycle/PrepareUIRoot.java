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

package org.eclipse.swt.internal.lifecycle;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.swt.internal.graphics.FontSizeCalculator;
import org.eclipse.swt.widgets.Display;

import com.w4t.engine.lifecycle.PhaseId;
import com.w4t.engine.requests.RequestParams;
import com.w4t.engine.service.ContextProvider;


final class PrepareUIRoot implements IPhase {

  public PhaseId getPhaseID() {
    return PhaseId.PREPARE_UI_ROOT;
  }

  public PhaseId execute() {
    HttpServletRequest request = ContextProvider.getRequest();
    String startup = request.getParameter( RequestParams.STARTUP );
    PhaseId result;
    if( startup != null ) {
      FontSizeCalculator.readStartupProbes();
      EntryPointManager.createUI( startup );      
      result = PhaseId.RENDER;
    } else if( Display.getCurrent() == null ) {
      FontSizeCalculator.readStartupProbes();
      EntryPointManager.createUI( EntryPointManager.DEFAULT );
      result = PhaseId.RENDER;
    } else {
      result = PhaseId.READ_DATA;
    }
    return result;
  }
}
