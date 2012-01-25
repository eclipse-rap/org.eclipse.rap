/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.widgets.Display;


final class PrepareUIRoot implements IPhase {

  public PhaseId getPhaseId() {
    return PhaseId.PREPARE_UI_ROOT;
  }

  public PhaseId execute( Display display ) {
    PhaseId result;
    if( LifeCycleUtil.isStartup() ) {
      String entryPointName = EntryPointUtil.findEntryPoint();
      EntryPointUtil.createUI( entryPointName );
      result = PhaseId.RENDER;
    } else {
      result = PhaseId.READ_DATA;
    }
    return result;
  }
}
