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
import org.eclipse.rap.rwt.browser.Browser;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class FocusTab extends ExampleTab {

  private static final String DEFAULT_HTML 
    = "<html>" 
    + "<head></head>"
    + "<body><p>Hello World</p></body>"
    + "</html>";

  private Button button;
  private Button radio;
  private Button check;
  private Combo combo;
  private List list;
  private TabFolder tabFolder;
  private Browser browser;
  private Table table;
  private Tree tree;
  private Composite composite;

  public FocusTab( final TabFolder parent ) {
    super( parent, "Focus" );
  }

  protected void createStyleControls() {
    createFocusButton( "Focus Push Button", button );
    createFocusButton( "Focus Radio Button", radio );
    createFocusButton( "Focus Check Box", check );
    createFocusButton( "Focus Combo", combo );
    createFocusButton( "Focus List", list );
    createFocusButton( "Focus TabFolder", tabFolder );
    createFocusButton( "Focus Browser", browser );
    createFocusButton( "Focus Table", table );
    createFocusButton( "Focus Tree", tree );
    createFocusButton( "Focus Composite", composite );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    button = new Button( parent, RWT.PUSH );
    button.setText( "Push Button" );
    radio = new Button( parent, RWT.RADIO );
    radio.setText( "Radio Button" );
    check = new Button( parent, RWT.CHECK );
    check.setText( "Check Box" );
    combo = new Combo( parent, RWT.NONE );
    combo.add( "Item 1" );
    combo.add( "Item 2" );
    combo.add( "Item 3" );
    list = new List( parent, RWT.BORDER );
    list.add( "Item 1" );
    list.add( "Item 2" );
    list.add( "Item 3" );
    tabFolder = new TabFolder( parent, RWT.NONE );
    TabItem tabItem = new TabItem( tabFolder, RWT.NONE );
    tabItem.setText( "Tab Item 1" );
    Label tabItemControl = new Label( tabFolder, RWT.NONE );
    tabItem.setControl( tabItemControl );
    browser = new Browser( parent, RWT.NONE );
    browser.setText( DEFAULT_HTML );
    table = new Table( parent, RWT.NONE );
    TableColumn tableColumn;
    tableColumn = new TableColumn( table, RWT.NONE );
    tableColumn.setText( "Column 1" );
    tableColumn = new TableColumn( table, RWT.NONE );
    tableColumn.setText( "Column 2" );
    tableColumn = new TableColumn( table, RWT.NONE );
    tableColumn.setText( "Column 3" );
    for( int i = 0; i < 3; i++ ) {
      TableItem tableItem = new TableItem( table, RWT.NONE );
      tableItem.setText( "Item " + i );
    }
    tree = new Tree( parent, RWT.NONE );
    TreeItem item;
    item = new TreeItem( tree, RWT.NONE );
    item.setText( "Item 1" );
    item = new TreeItem( tree, RWT.NONE );
    item.setText( "Item 2" );
    item = new TreeItem( tree, RWT.NONE );
    item.setText( "Item 3" );
    item = new TreeItem( item, RWT.NONE );
    item.setText( "SubItem" );
    composite = new Composite( parent, RWT.NONE );
    Color black = Display.getCurrent().getSystemColor( RWT.COLOR_BLACK );
    composite.setBackground( black );
  }
  
  private void createFocusButton( final String text, 
                                  final Control targetControl ) 
  {
    Button button = new Button( styleComp, RWT.PUSH );
    button.setText( text );
    button.setLayoutData( new RowData( 140, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        targetControl.forceFocus();
      }
    } );
  }
}
