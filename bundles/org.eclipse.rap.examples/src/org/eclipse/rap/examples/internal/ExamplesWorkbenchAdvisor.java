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

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.*;
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

  public void preStartup() {
    RWT.getLifeCycle().addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void beforePhase( final PhaseEvent event ) {
      }

      public void afterPhase( final PhaseEvent event ) {
        String removeSplashJs
          = "var splashDiv = document.getElementById( \"splash\" );\n"
            + "    if( splashDiv != null ) {\n"
            + "      splashDiv.parentNode.removeChild( splashDiv );\n"
            + "    }\n";
        IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
        HtmlResponseWriter writer = stateInfo.getResponseWriter();
        writer.append( removeSplashJs );
        RWT.getLifeCycle().removePhaseListener( this );
      }

      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
  }
}
