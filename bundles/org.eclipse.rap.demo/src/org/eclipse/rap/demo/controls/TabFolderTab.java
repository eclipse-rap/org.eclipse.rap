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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class TabFolderTab extends ExampleTab {

  protected static final int MAX_ITEMS = 3;
  
  private TabFolder folder;
  private TabItem[] tabItems;
  private Button[] tabRadios;

  public TabFolderTab( final CTabFolder topFolder ) {
    super( topFolder, "TabFolder" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "TOP" );
    createStyleButton( "BOTTOM" );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
    tabRadios = new Button[ MAX_ITEMS ];
    for( int i = 0; i < MAX_ITEMS; i++ ) {
      String title = "Select Tab " + ( i + 1 );
      tabRadios[ i ] = createPropertyButton( title, SWT.RADIO );
      final int j = i;
      tabRadios[ i ].addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent event ) {
          folder.setSelection( j );
        }
      } );
    }
    tabRadios[ 0 ].setSelection( true );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    int style = getStyle();
    folder = new TabFolder( parent, style );
    tabItems = new TabItem[ 3 ];
    for( int i = 0; i < 3; i++ ) {
      tabItems[ i ] = new TabItem( folder, style );
      tabItems[ i ].setText( "Tab " + ( i + 1 ) );
      Text content = new Text( folder, SWT.WRAP | SWT.MULTI );
      content.setText( "Lorem ipsum dolor sit amet, consectetur adipisicing "
        + "elit, sed do eiusmod tempor incididunt ut labore et "
        + "dolore magna aliqua." );
      tabItems[ i ].setControl( content );
    }
    folder.setSelection( 0 );
    folder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        for( int i = 0; i < MAX_ITEMS; i++ ) {
          tabRadios[ i ].setSelection( event.item == tabItems[ i ] );
        }
      }
    } );
    registerControl( folder );
  }

}
