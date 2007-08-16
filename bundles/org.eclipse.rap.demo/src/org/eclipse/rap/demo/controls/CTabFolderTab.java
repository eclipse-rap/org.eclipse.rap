/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class CTabFolderTab extends ExampleTab {

  private CTabFolder folder;

  public CTabFolderTab( final CTabFolder parent ) {
    super( parent, "CTabFolder" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "TOP", SWT.TOP );
    createStyleButton( "BOTTOM", SWT.BOTTOM );
    createStyleButton( "CLOSE", SWT.CLOSE );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
    Button rbSelectTab0 = createPropertyButton( "Select Tab 1", SWT.RADIO );
    rbSelectTab0.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        folder.setSelection( 0 );
      }
    } );
    rbSelectTab0.setSelection( true );
    Button rbSelectTab1 = createPropertyButton( "Select Tab 2", SWT.RADIO );
    rbSelectTab1.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        folder.setSelection( 1 );
      }
    } );
    Button rbSelectTab2 = createPropertyButton( "Select Tab 3", SWT.RADIO );
    rbSelectTab2.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        folder.setSelection( 2 );
      }
    } );
    final Button cbMax = createPropertyButton( "Maximize visible", SWT.CHECK );
    rbSelectTab2.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        folder.setMaximizeVisible( cbMax.getSelection() );
      }
    } );
    final Button cbMin = createPropertyButton( "Minimize visible", SWT.CHECK );
    rbSelectTab2.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        folder.setMinimizeVisible( cbMin.getSelection() );
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    int style = getStyle();
    folder = new CTabFolder( parent, style );
    for( int i = 0; i < 3; i++ ) {
      CTabItem item = new CTabItem( folder, style );
      item.setText( "Tab " + ( i + 1 ) );
      Text content = new Text( folder, SWT.WRAP | SWT.MULTI );
      content.setText(   "Lorem ipsum dolor sit amet, consectetur adipisicing "
                       + "elit, sed do eiusmod tempor incididunt ut labore et "
                       + "dolore magna aliqua." );
      item.setControl( content );
    }
    Display display = Display.getCurrent();
    Color selectionBg = display.getSystemColor( SWT.COLOR_LIST_SELECTION );
    folder.setSelectionBackground( selectionBg );
    folder.setSelection( 0 );
    folder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
      }
      public void widgetDefaultSelected( SelectionEvent event ) {
      }
    });
    registerControl( folder );
  }

}
