/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.presentation;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.internal.provisional.action.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.internal.provisional.presentations.IActionBarPresentationFactory;
import org.eclipse.ui.presentations.*;


public class PresentationFactory
  extends AbstractPresentationFactory
  implements IActionBarPresentationFactory
{

  public StackPresentation createEditorPresentation(
    final Composite parent,
    final IStackPresentationSite site )
  {
    return new StackPresentationImpl( site, parent );
  }

  public StackPresentation createStandaloneViewPresentation(
    final Composite parent,
    final IStackPresentationSite site,
    final boolean showTitle )
  {
    return new StackPresentationImpl( site, parent );
  }

  public StackPresentation createViewPresentation(
    final Composite parent,
    final IStackPresentationSite site )
  {
    return new StackPresentationImpl( site, parent );
  }

  public ICoolBarManager2 createCoolBarManager() {
    return new DemoCoolBarManager();
  }

  public IToolBarContributionItem createToolBarContributionItem(
    final IToolBarManager toolBarManager, final String id )
  {
    return new DemoToolBarContributionItem();
  }

  public IToolBarManager2 createToolBarManager() {
    return new DemoToolBarManager();
  }

  public IToolBarManager2 createViewToolBarManager() {
    return new DemoToolBarManager();
  }
}
