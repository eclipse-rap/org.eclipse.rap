/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import java.util.List;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExampleContribution;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public abstract class Navigation {

  private final Composite composite;

  public Navigation( Composite parent ) {
    composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayoutWithoutMargin( 9, false ) );
    composite.setData( RWT.CUSTOM_VARIANT, "navigation" );
    createNavigationControls( composite );
  }

  public Control getControl() {
    return composite;
  }

  private void createNavigationControls( Composite parent ) {
    List<ExampleCategory> categories = Examples.getInstance().getCategories();
    for( ExampleCategory category : categories ) {
      createNavigationDropDown( parent, category );
    }
  }

  private void createNavigationDropDown( Composite parent, ExampleCategory category ) {
    new DropDownNavigation( parent, category ) {
      @Override
      protected void contributionSelected( IExampleContribution contribution ) {
        Navigation.this.selectContribution( contribution );
      }
    };
  }

  protected abstract void selectContribution( IExampleContribution contribution );

  public void selectNavigationEntry( IExampleContribution contribution ) {
    Control[] children = composite.getChildren();
    for( Control control : children ) {
      if( control instanceof DropDownNavigation ) {
        changeSelectedDropDownEntry( contribution, (DropDownNavigation) control );
      }
    }
  }

  private void changeSelectedDropDownEntry( IExampleContribution contribution,
                                            DropDownNavigation navEntry ) {
    boolean belongsToDropDownNav = contributionBelongsToDropDownNav( contribution, navEntry );
    ToolItem item = ( (ToolBar) navEntry.getChildren()[ 0 ] ).getItem( 0 );
    if( belongsToDropDownNav ) {
      item.setData( RWT.CUSTOM_VARIANT, "selected" );
    } else {
      item.setData( RWT.CUSTOM_VARIANT, "navigation" );
    }
  }

  private boolean contributionBelongsToDropDownNav( IExampleContribution contribution,
                                                    DropDownNavigation navEntry )
  {
    ExampleCategory category = navEntry.getCategory();
    return category.getContributionIds().contains( contribution.getId() );
  }

}
