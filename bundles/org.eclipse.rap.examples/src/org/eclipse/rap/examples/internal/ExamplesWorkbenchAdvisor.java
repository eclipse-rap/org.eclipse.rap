/*******************************************************************************
 * Copyright (c) 2008, 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IMutableActivityManager;
import org.eclipse.ui.application.*;


public class ExamplesWorkbenchAdvisor extends WorkbenchAdvisor {

//  public void preStartup() {
//    IWorkbench workbench = PlatformUI.getWorkbench();
//    IMutableActivityManager activitySupport
//      = workbench.getActivitySupport().createWorkingCopy();
//    Set enabledActivityIds = new HashSet();
//    enabledActivityIds.add( "org.eclipse.rap.examples" );
//    activitySupport.setEnabledActivityIds( enabledActivityIds );
//  }

  public String getInitialWindowPerspectiveId() {
    return ExamplePerspective.ID;
  }
  
  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
    final IWorkbenchWindowConfigurer configurer )
  {
    return new ExamplesWorkbenchWindowAdvisor( configurer );
  }
}
