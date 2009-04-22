/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.interactiondesign.tests.impl;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;


public class EntryPointImpl implements IEntryPoint {

  public EntryPointImpl() {
  }

  public int createUI() {
    WorkbenchAdvisor worbenchAdvisor = new WorkbenchAdvisor() {

      public String getInitialWindowPerspectiveId() {
        return "org.eclipse.rap.ui.interactiondesign.test.perspective";
      }
      
    };

    Display display = PlatformUI.createDisplay();
    return PlatformUI.createAndRunWorkbench( display, worbenchAdvisor );
  }
}
