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

import org.eclipse.jface.action.*;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;

class DemoToolBarContributionItem
  extends ContributionItem
  implements IToolBarContributionItem
{

  private final class ToolBarManager
    extends ContributionManager
    implements IToolBarManager
  {

    public void add( final IAction action ) {
    }

    public void add( final IContributionItem item ) {
    }

    public void appendToGroup( final String groupName, final IAction action ) {
    }

    public void appendToGroup( final String groupName,
                               final IContributionItem item )
    {
    }

    public IContributionItem find( final String id ) {
      return null;
    }

    public IContributionItem[] getItems() {
      return new IContributionItem[ 0 ];
    }

    public IContributionManagerOverrides getOverrides() {
      return null;
    }

    public void insertAfter( final String id, final IAction action ) {
    }

    public void insertAfter( final String id, final IContributionItem item ) {
    }

    public void insertBefore( final String id, final IAction action ) {
    }

    public void insertBefore( final String id, final IContributionItem item ) {
    }

    public boolean isDirty() {
      return false;
    }

    public boolean isEmpty() {
      return false;
    }

    public void markDirty() {
    }

    public void prependToGroup( final String groupName, final IAction action ) {
    }

    public void prependToGroup( final String groupName,
                                final IContributionItem item )
    {
    }

    public IContributionItem remove( final String id ) {
      return null;
    }

    public IContributionItem remove( final IContributionItem item ) {
      return null;
    }

    public void removeAll() {
    }

    public void update( final boolean force ) {
    }
  }

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
    return new ToolBarManager();
  }

  public boolean getUseChevron() {
    return false;
  }

  public void setCurrentHeight( final int currentHeight ) {
  }

  public void setCurrentWidth( final int currentWidth ) {
  }

  public void setMinimumItemsToShow( final int minimumItemsToShow ) {
  }

  public void setUseChevron( final boolean value ) {
  }
}