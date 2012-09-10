/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class ToolBarTab extends ExampleTab {

  private ToolBar toolBar;
  private ToolItem dropDownItem;
  private int count = 0;

  public ToolBarTab( CTabFolder topFolder ) {
    super( topFolder, "ToolBar" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
    createStyleButton( "VERTICAL", SWT.VERTICAL );
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "FLAT", SWT.FLAT );
    createStyleButton( "NO_RADIO_GROUP", SWT.NO_RADIO_GROUP );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
    createNewItemButton();
  }

  private void createNewItemButton() {
    Group group = new Group( styleComp, SWT.NONE );
    group.setLayout( new GridLayout( 2, false ) );
    group.setText( "New Item" );
    Label label = new Label( group, SWT.NONE );
    label.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );
    label.setText( "Index:" );
    final Text index = new Text( group, SWT.SINGLE | SWT.LEAD | SWT.BORDER );
    index.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    index.setText( "0" );
    Button addItemButton = new Button( group, SWT.PUSH );
    GridData gridData = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
    gridData.horizontalSpan = 2;
    addItemButton.setLayoutData( gridData );
    addItemButton.setText( "Add Item" );
    addItemButton.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        int newIndex = Integer.parseInt( index.getText() );
        ToolItem toolItem = new ToolItem( toolBar, SWT.RADIO, newIndex );
        toolItem.setText( "Item" );
      }
    } );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new RowLayout() );
    Image imageNewFile = loadImage( "resources/newfile_wiz.gif" );
    Image imagenewFolder = loadImage( "resources/newfolder_wiz.gif" );
    Image imageNewProj = loadImage( "resources/newprj_wiz.gif" );
    Image imageSearch = loadImage( "resources/search_src.gif" );
    toolBar = new ToolBar( parent, getStyle() );
    addContextMenu( toolBar );
    registerControl( toolBar );
    ToolItem item1 = new ToolItem( toolBar, SWT.PUSH );
    item1.setText( "new" );
    item1.setImage( imageNewFile );
    ToolItem item2 = new ToolItem( toolBar, SWT.PUSH );
    item2.setText( "open" );
    item2.setEnabled( false );
    item2.setImage( imagenewFolder );
    new ToolItem( toolBar, SWT.SEPARATOR );
    dropDownItem = new ToolItem( toolBar, SWT.DROP_DOWN );
    dropDownItem.setText( "select" );
    dropDownItem.setImage( imageNewProj );
    new ToolItem( toolBar, SWT.SEPARATOR );

    // Text item
    ToolItem itemText = new ToolItem( toolBar, SWT.SEPARATOR );
    Text text = new Text( toolBar, SWT.BORDER );
    text.setText( "A Text Field" );
    itemText.setControl( text );
    itemText.setWidth( 100 );

    ToolItem item4 = new ToolItem( toolBar, SWT.CHECK );
    item4.setImage( imageSearch );
    item4.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        log( "check changed" + event );
      }
    } );
    ToolItem item5 = new ToolItem( toolBar, SWT.RADIO );
    item5.setImage( imageSearch );
    ToolItem item6 = new ToolItem( toolBar, SWT.RADIO );
    SelectionAdapter radioSelectionListener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        log( "radio changed - " + event );
      }
    };
    item6.setImage( imageSearch );
    item5.addSelectionListener( radioSelectionListener);
    item6.addSelectionListener( radioSelectionListener);
    final Menu dropDownMenu = new Menu( toolBar.getShell(), SWT.POP_UP );
    for( int i = 0; i < 5; i++ ) {
      MenuItem item = new MenuItem( dropDownMenu, SWT.PUSH );
      item.setText( "Item " + count++ );
    }
    dropDownItem.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        if( event.detail == SWT.ARROW ) {
          Point point = toolBar.toDisplay( event.x, event.y );
          dropDownMenu.setLocation( point );
          dropDownMenu.setVisible( true );
        }
      }
    } );
    ToolItem withoutImageIcon = new ToolItem( toolBar, SWT.PUSH );
    withoutImageIcon.setText( "w/o <image>" );
  }

  private static void addContextMenu( ToolBar toolbar ) {
    Menu menu = new Menu( toolbar );
    MenuItem item = new MenuItem( menu, SWT.PUSH );
    item.setText( "ToolBar context menu item" );
    toolbar.setMenu( menu );
  }
}
