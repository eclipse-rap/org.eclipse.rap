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

import java.util.ArrayList;

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

  private final Menu secondLevelNav;
  private final String category;

  public DropDownNavigation( Composite parent, IExampleContribution page ) {
    super( parent, SWT.NONE );
    category = page.getCategory();
    secondLevelNav = new Menu( parent.getShell(), SWT.POP_UP );
    secondLevelNav.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    createDropDownToolItem( page );
    addNavigationItem( page );
    setLayout( new FillLayout() );
    setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, true, false ) );
  }

  public String getCategory() {
    return category;
  }

  @SuppressWarnings("unchecked")
  public void addNavigationItem( final IExampleContribution page ) {
    MenuItem item = new MenuItem( secondLevelNav, SWT.PUSH | SWT.LEFT );
    item.setText( page.getTitle() );
    ArrayList<String> objectData = (ArrayList<String>) this.getData();
    objectData.add( page.getId() );
    item.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    item.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        onSelectContribution( page );
      }
    } );
  }

  private void createDropDownToolItem( final IExampleContribution page ) {
    final ToolBar toolBar = new ToolBar( this, SWT.HORIZONTAL );
    ArrayList<String> objectData = new ArrayList<String>();
    objectData.add( page.getId() );
    this.setData( objectData );
    toolBar.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    ToolItem toolItem = new ToolItem( toolBar, SWT.DROP_DOWN );
    toolItem.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    toolItem.setText( page.getCategory() );
    toolItem.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        if( event.detail == SWT.ARROW ) {
          Point point = toolBar.toDisplay( event.x, event.y );
          secondLevelNav.setLocation( point );
          secondLevelNav.setVisible( true );
        } else {
          onSelectContribution( page );
        }
      }
    } );
  }

  protected abstract void onSelectContribution( IExampleContribution page );

}
