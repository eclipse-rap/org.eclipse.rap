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

import org.eclipse.rap.examples.IExampleContribution;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;


abstract class DropDownNavigation extends Composite {

  private final Menu pullDownMenu;
  private final ExampleCategory category;

  public DropDownNavigation( Composite parent, ExampleCategory category ) {
    super( parent, SWT.NONE );
    this.category = category;
    pullDownMenu = createMenu( parent );
    setLayout( new FillLayout() );
    setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, true, false ) );
    createMenuItems();
    createDropDownToolItem();
  }

  public ExampleCategory getCategory() {
    return category;
  }

  private Menu createMenu( Composite parent ) {
    Menu menu = new Menu( parent.getShell(), SWT.POP_UP );
    menu.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    return menu;
  }

  private void createMenuItems() {
    for( String id : category.getContributionIds() ) {
      final IExampleContribution contribution = Examples.getInstance().getContribution( id );
      if( contribution != null ) {
        createMenuItem( contribution );
      }
    }
  }

  private void createMenuItem( final IExampleContribution contribution ) {
    MenuItem item = new MenuItem( pullDownMenu, SWT.PUSH | SWT.LEFT );
    item.setText( contribution.getTitle().replace( "&", "&&" ) );
    item.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    item.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        contributionSelected( contribution );
      }
    } );
  }

  private void createDropDownToolItem() {
    final ToolBar toolBar = new ToolBar( this, SWT.HORIZONTAL );
    toolBar.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    ToolItem toolItem = new ToolItem( toolBar, SWT.DROP_DOWN );
    toolItem.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    toolItem.setText( category.getName().replace( "&", "&&" ) );
    toolItem.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        if( event.detail == SWT.ARROW ) {
          openMenu( toolBar.toDisplay( event.x, event.y ) );
        } else {
          openFirstContribution();
        }
      }

    } );
  }

  private void openMenu( Point point ) {
    pullDownMenu.setLocation( point );
    pullDownMenu.setVisible( true );
  }

  private void openFirstContribution() {
    for( String id : category.getContributionIds() ) {
      IExampleContribution contribution = Examples.getInstance().getContribution( id );
      if( contribution != null ) {
        contributionSelected( contribution );
        break;
      }
    }
  }

  protected abstract void contributionSelected( IExampleContribution contribution );

}
