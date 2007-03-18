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
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ToolBarTab extends ExampleTab {

  private ToolBar toolBar;
  private ToolItem dropDownItem;
  private int count = 0;

  public ToolBarTab( final TabFolder folder ) {
    super( folder, "ToolBar" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "FLAT" );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    ClassLoader loader = getClass().getClassLoader();
    Image imageNewFile = Image.find( "resources/newfile_wiz.gif", loader );
    Image imagenewFolder = Image.find( "resources/newfolder_wiz.gif", loader );
    Image imageNewProj = Image.find( "resources/newprj_wiz.gif", loader );
    Image imageSearch = Image.find( "resources/search_src.gif", loader );
    toolBar = new ToolBar( parent, getStyle() );
    registerControl( toolBar );
    ToolItem item1 = new ToolItem( toolBar, RWT.PUSH );
    item1.setText( "new" );
    item1.setImage( imageNewFile );
    ToolItem item2 = new ToolItem( toolBar, RWT.PUSH );
    item2.setText( "open" );
    item2.setImage( imagenewFolder );
    new ToolItem( toolBar, RWT.SEPARATOR );
    dropDownItem = new ToolItem( toolBar, RWT.DROP_DOWN );
    dropDownItem.setText( "select" );
    dropDownItem.setImage( imageNewProj );
    new ToolItem( toolBar, RWT.SEPARATOR );
    ToolItem item4 = new ToolItem( toolBar, RWT.CHECK );
    item4.setImage( imageSearch );
    ToolItem item5 = new ToolItem( toolBar, RWT.RADIO );
    item5.setImage( imageSearch );
    ToolItem item6 = new ToolItem( toolBar, RWT.RADIO );
    item6.setImage( imageSearch );
    final Menu dropDownMenu = new Menu( toolBar.getShell(), RWT.POP_UP );
    for( int i = 0; i < 5; i++ ) {
      MenuItem item = new MenuItem( dropDownMenu, RWT.PUSH );
      item.setText( "Item " + count++ );
    }
    dropDownItem.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( event.detail == RWT.ARROW ) {
          Point point = new Point( event.x, event.y );
          dropDownMenu.setLocation( point );
          dropDownMenu.setVisible( true );
        }
      }
    } );
  }
}
