/*******************************************************************************
 * Copyright (c) 2008, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.widgets.JSExecutor;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.*;


@SuppressWarnings("restriction")
public class ExamplesWorkbenchAdvisor extends WorkbenchAdvisor {

  public String getInitialWindowPerspectiveId() {
    return ExamplePerspective.ID;
  }

  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
    final IWorkbenchWindowConfigurer configurer )
  {
    return new ExamplesWorkbenchWindowAdvisor( configurer );
  }

  public void preStartup() {
    final Display display = Display.getCurrent();
    RWT.getLifeCycle().addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void beforePhase( final PhaseEvent event ) {
      }

      public void afterPhase( final PhaseEvent event ) {
        if( Display.getCurrent() == display ) {
          removeSplash();
          RWT.getLifeCycle().removePhaseListener( this );
        }
      }

      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
  }

  private void removeSplash() {
    String removeSplashJs =   "var splashDiv = document.getElementById( \"splash\" );\n"
                            + "if( splashDiv != null ) {\n"
                            + "  splashDiv.parentNode.removeChild( splashDiv );\n"
                            + "}\n";
    JSExecutor.executeJS( removeSplashJs );
  }
}
