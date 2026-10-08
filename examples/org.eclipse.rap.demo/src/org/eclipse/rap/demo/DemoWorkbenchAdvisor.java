/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.demo;

import org.eclipse.ui.application.*;


public class DemoWorkbenchAdvisor extends WorkbenchAdvisor {

  public void initialize( IWorkbenchConfigurer configurer ) {
    getWorkbenchConfigurer().setSaveAndRestore( true );
    super.initialize( configurer );
  }

  public String getInitialWindowPerspectiveId() {
    return "org.eclipse.rap.demo.perspective";
  }
  
  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
    final IWorkbenchWindowConfigurer windowConfigurer )
  {
    return new DemoWorkbenchWindowAdvisor( windowConfigurer );
  }
}
