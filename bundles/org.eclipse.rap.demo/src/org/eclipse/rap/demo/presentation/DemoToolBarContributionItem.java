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

package org.eclipse.rap.demo.presentation;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;

class DemoToolBarContributionItem
  extends ContributionItem
  implements IToolBarContributionItem
{

  public int getCurrentHeight() {
    return 0;
  }

  public int getCurrentWidth() {
    return 0;
  }

  public int getMinimumItemsToShow() {
    return 0;
  }

  public IToolBarManager getToolBarManager() {
    return null;
  }

  public boolean getUseChevron() {
    return false;
  }

  public void setCurrentHeight( int currentHeight ) {
  }

  public void setCurrentWidth( int currentWidth ) {
  }

  public void setMinimumItemsToShow( int minimumItemsToShow ) {
  }

  public void setUseChevron( boolean value ) {
  }
}