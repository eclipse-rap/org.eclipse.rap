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

package org.eclipse.rap.demo;

import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.ui.entrypoint.*;


public class DemoWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

  public DemoWorkbenchWindowAdvisor(
    final IWorkbenchWindowConfigurer configurer )
  {
    super( configurer );
  }
  
  public ActionBarAdvisor createActionBarAdvisor(
    final IActionBarConfigurer configurer )
  {
    return new DemoActionBarAdvisor( configurer );
  }
  
  public void preWindowOpen() {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.setInitialSize( new Point( 800, 600 ) );
    configurer.setShowCoolBar( true );
    configurer.setTitle( "Workbench Demo" );
  }
}
