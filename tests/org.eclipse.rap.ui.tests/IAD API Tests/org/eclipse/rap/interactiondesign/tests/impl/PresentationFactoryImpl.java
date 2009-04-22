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

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.internal.provisional.action.CoolBarManager2;
import org.eclipse.jface.internal.provisional.action.ICoolBarManager2;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;
import org.eclipse.jface.internal.provisional.action.IToolBarManager2;
import org.eclipse.jface.internal.provisional.action.ToolBarManager2;
import org.eclipse.rap.ui.interactiondesign.IWindowComposer;
import org.eclipse.rap.ui.interactiondesign.PresentationFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;


public class PresentationFactoryImpl extends PresentationFactory {

  public ICoolBarManager2 createCoolBarManager() {
    return new CoolBarManager2();
  }

  public MenuManager createMenuBarManager() {
    return new MenuManager();
  }

  public MenuManager createPartMenuManager() {
    return new MenuManager();
  }

  public IToolBarContributionItem createToolBarContributionItem( 
    IToolBarManager toolBarManager,
    String id )
  {
    return new ToolBarContributionItem();
  }

  public IToolBarManager2 createToolBarManager() {
    return new ToolBarManager2();
  }

  public IToolBarManager2 createViewToolBarManager() {
    return new ToolBarManager2();
  }

  public IWindowComposer createWindowComposer() {
    return new IWindowComposer() {

      public Composite createWindowContents( Shell shell,
                                             IWorkbenchWindowConfigurer configurer )
      {
        return new Composite( shell, SWT.NONE );
      }

      public void postWindowOpen( IWorkbenchWindowConfigurer configurer ) {
        
      }

      public void preWindowOpen( IWorkbenchWindowConfigurer configurer ) {        
      }
      
    };
  }
}
