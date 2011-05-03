/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.widgets.Display;



final class PrepareUIRoot implements IPhase {

  public PhaseId getPhaseId() {
    return PhaseId.PREPARE_UI_ROOT;
  }

  public PhaseId execute( Display display ) {
    String entryPointName = LifeCycleUtil.getEntryPoint();
    PhaseId result;
    if( entryPointName != null ) {
      createUI( entryPointName );      
      result = PhaseId.RENDER;
    } else {
      result = PhaseId.READ_DATA;
    }
    return result;
  }

  private static void createUI( String entryPointName ) {
    RWTFactory.getEntryPointManager().createUI( entryPointName );
  }
}
