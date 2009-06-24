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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.*;


public class ExamplesWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

  public ExamplesWorkbenchWindowAdvisor(
    final IWorkbenchWindowConfigurer configurer )
  {
    super( configurer );
  }

  public ActionBarAdvisor createActionBarAdvisor(
    final IActionBarConfigurer configurer )
  {
    return new ExamplesActionBarAdvisor( configurer );
  }

  public void preWindowOpen() {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.setInitialSize( new Point( 800, 600 ) );
    configurer.setShowMenuBar( false );
    configurer.setShowCoolBar( true );
    configurer.setShowPerspectiveBar( false );
    configurer.setShowStatusLine( false );
    configurer.setShowProgressIndicator( false );
    configurer.setTitle( "RAP Workbench Demo" );
    configurer.setShellStyle( SWT.TITLE | SWT.MAX | SWT.RESIZE );
  }
}
