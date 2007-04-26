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

import java.io.IOException;
import org.eclipse.swt.lifecycle.DisplayUtil;
import org.eclipse.swt.widgets.Display;
import com.w4t.engine.lifecycle.PhaseId;


final class Render implements IPhase {

  public PhaseId getPhaseID() {
    return PhaseId.RENDER;
  }

  public PhaseId execute() throws IOException {
    Display display = Display.getCurrent();
    DisplayUtil.getLCA( display ).render( display );
    return null;
  }
}