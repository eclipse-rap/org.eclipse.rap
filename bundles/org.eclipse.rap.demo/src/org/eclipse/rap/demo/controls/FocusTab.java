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
import org.eclipse.rap.rwt.custom.CTabFolder;
import org.eclipse.rap.rwt.custom.CTabItem;
import org.eclipse.rap.rwt.events.*;
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

  private Text text;

  private Text multiText;

  private List log;

  private Label label;

  public FocusTab( final TabFolder parent ) {
    super( parent, "Focus" );
  }

  protected void createStyleControls() {
    createFocusButton( "Focus Label", label );
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
    Label label = new Label( styleComp, RWT.NONE );
    label.setText( "Log" );
    log = new List( styleComp, RWT.BORDER | RWT.V_SCROLL | RWT.H_SCROLL );
    log.setLayoutData( new RowData( 300, 160 ) );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    label = new Label( parent, RWT.NONE );
    label.setText( "Even a label can gain focus" );
    addFocusListener( label );
    button = new Button( parent, RWT.PUSH );
    button.setText( "Push Button" );
    addFocusListener( button );
    radio = new Button( parent, RWT.RADIO );
    radio.setText( "Radio Button" );
    addFocusListener( radio );
    check = new Button( parent, RWT.CHECK );
    check.setText( "Check Box" );
    addFocusListener( check );
    text = new Text( parent, RWT.SINGLE | RWT.BORDER );
    text.setText( "text" );
    addFocusListener( text );
    multiText = new Text( parent, RWT.MULTI | RWT.BORDER );
    multiText.setText( "Multiline Text" );
    multiText.setLayoutData( new RowData( 80, 60 ) );
    addFocusListener( multiText );
    combo = new Combo( parent, RWT.NONE );
    combo.add( "Item 1" );
    combo.add( "Item 2" );
    combo.add( "Item 3" );
    addFocusListener( combo );
    list = new List( parent, RWT.BORDER );
    list.add( "Item 1" );
    list.add( "Item 2" );
    list.add( "Item 3" );
    addFocusListener( list );
    tabFolder = new TabFolder( parent, RWT.NONE );
    tabFolder.setLayoutData( new RowData( 80, 60 ) );
    TabItem tabItem = new TabItem( tabFolder, RWT.NONE );
    tabItem.setText( "Tab Item 1" );
    Label tabItemControl = new Label( tabFolder, RWT.NONE );
    tabItemControl.setText( "TabItem Content" );
    tabItem.setControl( tabItemControl );
    addFocusListener( tabFolder );
    browser = new Browser( parent, RWT.NONE );
    browser.setText( DEFAULT_HTML );
    addFocusListener( browser );
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
    addFocusListener( table );
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
    addFocusListener( tree );
    composite = new Composite( parent, RWT.NONE );
    Color black = Display.getCurrent().getSystemColor( RWT.COLOR_BLACK );
    composite.setBackground( black );
    addFocusListener( composite );
    CTabFolder tabFolder2 = new CTabFolder( parent, RWT.NONE );
    CTabItem tabItem2 = new CTabItem( tabFolder2, RWT.NONE );
    tabItem2.setText( "Item 1" );
    Label ctabItemControl = new Label( tabFolder2, RWT.NONE );
    ctabItemControl.setText( "Content control of item 1" );
    tabItem2.setControl( ctabItemControl );
    tabItem2 = new CTabItem( tabFolder2, RWT.NONE );
    tabItem2.setText( "Item 2" );
    addFocusListener( tabFolder2 );
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
  
  private void addFocusListener( final Control control ) {
    control.addFocusListener( new FocusListener() {
      public void focusGained( final FocusEvent event ) {
        String msg = "focusGained: " + event.getSource();
        log.add( msg, 0 );
      }
      public void focusLost( final FocusEvent event ) {
        String msg = "focusLost: " + event.getSource();
        log.add( msg, 0 );
      }
    } );
  }
}
