/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Rüdiger Herrmann - bug 335112
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCAFacade;


public class ThemeManagerHolder {
  private ThemeManager instance;
  
  public ThemeManager getInstance() {
    if( instance == null ) {
      instance = new ThemeManager();
    }
    return instance;
  }
  
  public void resetInstance() {
    instance = null;
  }

  public void activate() {
    getInstance().initializeThemeableWidgets();
    getInstance().initialize();
    
    // TODO [SystemStart]: move this to where the actual system initialization takes place
    RWTFactory.getJSLibraryConcatenator().startJSConcatenation();
    DisplayLCAFacade.registerResources();
    RWTFactory.getJSLibraryConcatenator().finishJSConcatenation();
  }

  public void deactivate() {
    resetInstance();
  }
}