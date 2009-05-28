/******************************************************************************* 
* Copyright (c) 2008 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.internal.provisional.action.ICoolBarManager2;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;
import org.eclipse.jface.internal.provisional.action.IToolBarManager2;
import org.eclipse.rap.internal.design.example.business.managers.BusinessContribItem;
import org.eclipse.rap.internal.design.example.business.managers.BusinessCoolBarManager;
import org.eclipse.rap.internal.design.example.business.managers.BusinessMenuBarManager;
import org.eclipse.rap.internal.design.example.business.managers.BusinessPartMenuManager;
import org.eclipse.rap.internal.design.example.business.managers.BusinessToolBarManager;
import org.eclipse.rap.internal.design.example.business.managers.BusinessViewToolBarManager;
import org.eclipse.rap.ui.interactiondesign.IWindowComposer;
import org.eclipse.rap.ui.interactiondesign.PresentationFactory;


public class BusinessPresentationFactory extends PresentationFactory {


  public ICoolBarManager2 createCoolBarManager() {
    return new BusinessCoolBarManager();
  }

  public MenuManager createMenuBarManager() {
    return new BusinessMenuBarManager();
  }

  public MenuManager createPartMenuManager() {
    return new BusinessPartMenuManager();
  }

  public IToolBarContributionItem createToolBarContributionItem( 
    final IToolBarManager toolBarManager,
    final String id )
  {
    return new BusinessContribItem( toolBarManager, id );
  }

  public IToolBarManager2 createToolBarManager() {
    return new BusinessToolBarManager();
  }

  public IToolBarManager2 createViewToolBarManager() {
    return new BusinessViewToolBarManager();
  }

  public IWindowComposer createWindowComposer() {    
    return new BusinessWindowComposer();
  }
  
}
