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
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TableTab extends ExampleTab {

  private static final int COLUMNS = 5;
  private static final int INITIAL_ITEMS = 1;
  protected static final int ADD_ITEMS = 300;
  
  private Table table;
  private boolean headerVisible = true;
  private boolean linesVisible;

  public TableTab( final TabFolder folder ) {
    super( folder, "Table" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createVisibilityButton();
    createEnablementButton();
    createHeaderVisibleButton();
    createLinesVisibleButton();
    createAddItemsButton();
    createSelectItemButton();
    crateDisposeFirstColumnButton();
    createDisposeSelectionButton();
    createTopIndexButton();
    createShowSelectionButton();
  }

  protected void createExampleControls( final Composite top ) {
    top.setLayout( new FillLayout() );
    int style = getStyle();
    table = new Table( top, style );
    table.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        System.out.println( "table-widgetSelected: " + event.item.getText() );
      }
    } );
    for( int i = 0; i < COLUMNS; i++ ) {
      final TableColumn column = new TableColumn( table, RWT.NONE );
      column.setText( "Col " + i );
      column.setWidth( i == 0 ? 50 : 100 );
      column.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent event ) {
          System.out.println( "column selected: " + column.getText() );
        }
      } );
    }
    for( int i = 0; i < INITIAL_ITEMS; i++ ) {
      addItem();
//      TableItem item = addItem();
//      Text text = new Text( table, RWT.NONE );
//      text.setBounds( item.getBounds() );
//      text.setText( "on top of a table" );
//      text.moveAbove( table );
    }
    table.setSelection( 0 );
    table.setHeaderVisible( headerVisible );
    table.setLinesVisible( linesVisible );
    
    registerControl( table );
  }

  private void createHeaderVisibleButton() {
    final Button button = new Button( styleComp, RWT.CHECK );
    button.setText( "headerVisible" );
    button.setSelection( headerVisible );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        headerVisible = button.getSelection();
        table.setHeaderVisible( headerVisible );
      }
    } );
  }

  private void createLinesVisibleButton() {
    final Button button = new Button( styleComp, RWT.CHECK );
    button.setText( "linesVisible" );
    button.setSelection( linesVisible );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        linesVisible = button.getSelection();
        table.setLinesVisible( linesVisible );
      }
    } );
  }

  private TableItem addItem() {
    TableItem result = new TableItem( table, RWT.NONE );
    for( int i = 0; i < COLUMNS; i++ ) {
      int itemCount = result.getParent().getItemCount() - 1;
      result.setText( i, "Item" + itemCount + "-" + i );
    }
    return result;
  }

  private void createAddItemsButton() {
    Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Add " + ADD_ITEMS + " Items" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        for( int i = 0; i < ADD_ITEMS; i++ ) {
          addItem();
        }
      }
    } );
  }
  
  private void createSelectItemButton() {
    Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Select first Item" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( table.getItemCount() > 0 ) {
          table.setSelection( 0 );
        }
      }
    } );
  }

  private void crateDisposeFirstColumnButton() {
    Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Dispose first Column" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( table.getColumnCount() > 0 ) {
          table.getColumn( 0 ).dispose();
        }
      }
    } );
  }

  private void createDisposeSelectionButton() {
    Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Dispose Selected Item" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        TableItem[] items = table.getSelection();
        for( int i = 0; i < items.length; i++ ) {
          items[ i ].dispose();
        }
      }
    } );
  }
  
  private void createTopIndexButton() {
    Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Set topIndex = 100" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        table.setTopIndex( 100 );
      }
    } );
  }
  private void createShowSelectionButton() {
    Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "showSelection" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        table.showSelection();
      }
    } );
  }
}
