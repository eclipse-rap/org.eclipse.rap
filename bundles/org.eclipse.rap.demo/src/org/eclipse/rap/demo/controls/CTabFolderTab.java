/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.CTabFolder;
import org.eclipse.rap.rwt.custom.CTabItem;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.widgets.*;

public class CTabFolderTab extends ExampleTab {

  private CTabFolder folder;

  public CTabFolderTab( final TabFolder parent ) {
    super( parent, "CTabFolder" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "TOP" );
    createStyleButton( "BOTTOM" );
    createStyleButton( "CLOSE" );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
    Button rbSelectTab0 = createPropertyButton( "Select Tab 1", RWT.RADIO );
    rbSelectTab0.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        folder.setSelection( 0 );
      }
    } );
    rbSelectTab0.setSelection( true );
    Button rbSelectTab1 = createPropertyButton( "Select Tab 2", RWT.RADIO );
    rbSelectTab1.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        folder.setSelection( 1 );
      }
    } );
    Button rbSelectTab2 = createPropertyButton( "Select Tab 3", RWT.RADIO );
    rbSelectTab2.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        folder.setSelection( 2 );
      }
    } );
    final Button cbMax = createPropertyButton( "Maximize visible", RWT.CHECK );
    rbSelectTab2.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        folder.setMaximizeVisible( cbMax.getSelection() );
      }
    } );
    final Button cbMin = createPropertyButton( "Minimize visible", RWT.CHECK );
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
    folder.setLayoutData( new RowData( 250, 200 ) );
    for( int i = 0; i < 3; i++ ) {
      CTabItem item = new CTabItem( folder, style );
      item.setText( "Tab " + ( i + 1 ) );
      Text content = new Text( folder, RWT.WRAP | RWT.MULTI );
      content.setText(   "Lorem ipsum dolor sit amet, consectetur adipisicing "
                       + "elit, sed do eiusmod tempor incididunt ut labore et "
                       + "dolore magna aliqua." );
      item.setControl( content );
    }
    folder.setSelection( 0 );
    folder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        System.out.println( "widgetSelected: " + event );
      }
      public void widgetDefaultSelected( SelectionEvent event ) {
        System.out.println( "widgetDefaultSelected: " + event );
      }
    });
    registerControl( folder );
  }

}
