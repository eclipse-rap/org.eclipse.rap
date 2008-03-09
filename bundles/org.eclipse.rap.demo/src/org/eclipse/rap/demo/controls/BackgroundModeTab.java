/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class BackgroundModeTab extends ExampleTab {

  private static final String DEFAULT_HTML
    = "<html>"
    + "<head></head>"
    + "<body><p>Hello World</p></body>"
    + "</html>";

  private int backgroundMode;

  public BackgroundModeTab( final CTabFolder topFolder ) {
    super( topFolder, "BackgroundMode" );
  }

  protected void createStyleControls( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Background Mode" );
    group.setLayout( new GridLayout() );
    final Button noneButton = new Button( group, SWT.RADIO );
    noneButton.setText( "SWT.INHERIT_NONE" );
    final Button defaultButton = new Button( group, SWT.RADIO );
    defaultButton.setText( "SWT.INHERIT_DEFAULT" );
    final Button forceButton = new Button( group, SWT.RADIO );
    forceButton.setText( "SWT.INHERIT_FORCE" );
    SelectionAdapter selectionAdapter = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        if( defaultButton.getSelection() ) {
          backgroundMode = SWT.INHERIT_DEFAULT;
        } else if( forceButton.getSelection() ) {
          backgroundMode = SWT.INHERIT_FORCE;
        } else {
          backgroundMode = SWT.INHERIT_NONE;
        }
        createNew();
      }
    };
    noneButton.addSelectionListener( selectionAdapter );
    defaultButton.addSelectionListener( selectionAdapter );
    forceButton.addSelectionListener( selectionAdapter );
    noneButton.setSelection( true );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    parent.setBackground( BG_COLOR_BROWN );
    parent.setBackgroundMode( backgroundMode );
    new Label( parent, SWT.NONE ).setText( "Label" );
    new Button( parent, SWT.PUSH ).setText( "Push Button" );
    new Button( parent, SWT.RADIO ).setText( "Radio Button" );
    new Button( parent, SWT.CHECK ).setText( "Check Box" );
    new Text( parent, SWT.SINGLE | SWT.BORDER ).setText( "text" );
    Text multiText = new Text( parent, SWT.MULTI | SWT.BORDER );
    multiText.setText( "Multiline Text" );
    multiText.setLayoutData( new RowData( 80, 60 ) );
    Combo combo = new Combo( parent, SWT.NONE );
    combo.add( "Item 1" );
    combo.add( "Item 2" );
    combo.add( "Item 3" );
    List list = new List( parent, SWT.BORDER );
    list.add( "Item 1" );
    list.add( "Item 2" );
    list.add( "Item 3" );
    TabFolder tabFolder = new TabFolder( parent, SWT.NONE );
    tabFolder.setLayoutData( new RowData( 80, 60 ) );
    TabItem tabItem = new TabItem( tabFolder, SWT.NONE );
    tabItem.setText( "Tab Item 1" );
    Label tabItemControl = new Label( tabFolder, SWT.NONE );
    tabItemControl.setText( "TabItem Content" );
    tabItem.setControl( tabItemControl );
    new Browser( parent, SWT.NONE ).setText( DEFAULT_HTML );
    Table table = new Table( parent, SWT.NONE );
    table.setLayoutData( new RowData( 90, 140 ) );
    table.setHeaderVisible( true );
    TableColumn tableColumn;
    tableColumn = new TableColumn( table, SWT.NONE );
    tableColumn.setText( "Column 1" );
    tableColumn.setWidth( 80 );
    for( int i = 0; i < 3; i++ ) {
      TableItem tableItem = new TableItem( table, SWT.NONE );
      tableItem.setText( "Item " + i );
    }
    Tree tree = new Tree( parent, SWT.NONE );
    TreeItem item;
    item = new TreeItem( tree, SWT.NONE );
    item.setText( "Item 1" );
    item = new TreeItem( tree, SWT.NONE );
    item.setText( "Item 2" );
    item = new TreeItem( tree, SWT.NONE );
    item.setText( "Item 3" );
    item = new TreeItem( item, SWT.NONE );
    item.setText( "SubItem" );
    new Composite( parent, SWT.NONE ).setBackground( BG_COLOR_GREEN );
    CTabFolder tabFolder2 = new CTabFolder( parent, SWT.NONE );
    CTabItem tabItem2 = new CTabItem( tabFolder2, SWT.NONE );
    tabItem2.setText( "Item 1" );
    Label ctabItemControl = new Label( tabFolder2, SWT.NONE );
    ctabItemControl.setText( "Content control of item 1" );
    tabItem2.setControl( ctabItemControl );
    tabItem2 = new CTabItem( tabFolder2, SWT.NONE );
    tabItem2.setText( "Item 2" );
  }
}
