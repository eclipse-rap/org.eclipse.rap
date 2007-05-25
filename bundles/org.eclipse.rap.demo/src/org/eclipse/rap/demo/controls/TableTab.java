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
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class TableTab extends ExampleTab {

  private static final int INITIAL_ITEMS = 1;
  protected static final int ADD_ITEMS = 300;
  
  private Table table;
  private boolean headerVisible = true;
  private boolean linesVisible;
  private int columns = 5;

  public TableTab( final TabFolder folder ) {
    super( folder, "Table" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "CHECK" );
    createVisibilityButton();
    createEnablementButton();
    createHeaderVisibleButton();
    createLinesVisibleButton();
    createFontChooser();
    createAddItemsButton();
    createSelectItemButton();
    crateDisposeFirstColumnButton();
    createDisposeSelectionButton();
    createTopIndexButton();
    createShowSelectionButton();
    createChangeCheckButton();
    createChangeColumnsControl();
    createChangeItemControl();
  }

  protected void createExampleControls( final Composite parent ) {
    FillLayout layout = new FillLayout();
    layout.marginHeight = 5;
    layout.marginWidth = 5;
    parent.setLayout( layout );
    int style = getStyle();
    table = new Table( parent, style );
    table.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        // 
      }
    } );
    for( int i = 0; i < columns; i++ ) {
      final TableColumn column = new TableColumn( table, SWT.NONE );
      column.setText( "Col " + i );
      column.setWidth( i == 0 ? 50 : 100 );
      column.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent event ) {
          // 
        }
      } );
    }
    for( int i = 0; i < INITIAL_ITEMS; i++ ) {
      addItem();
    }
    table.setSelection( 0 );
    table.setHeaderVisible( headerVisible );
    table.setLinesVisible( linesVisible );
    Menu menu = new Menu( table );
    menu.addMenuListener( new MenuListener() {
      public void menuShown( MenuEvent e ) {
System.out.println( e );        
      }
      public void menuHidden( MenuEvent e ) {
System.out.println( e );        
      }
    } );
    MenuItem menuItem = new MenuItem( menu, SWT.NONE );
    menuItem.setText( "Menu for Table" );
    table.setMenu( menu );
    registerControl( table );
  }

  private void createHeaderVisibleButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
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
    final Button button = new Button( styleComp, SWT.CHECK );
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
    TableItem result = new TableItem( table, SWT.NONE );
    int itemCount = result.getParent().getItemCount() - 1;
    if( columns == 0 ) {
      result.setText(  "Item " + itemCount );
    } else {
      for( int i = 0; i < columns; i++ ) {
        result.setText( i, "Item" + itemCount + "-" + i );
      }
    }
    return result;
  }

  private void createAddItemsButton() {
    Button btnAddOne = new Button( styleComp, SWT.PUSH );
    btnAddOne.setText( "Add 1 Item" );
    btnAddOne.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        addItem();
      }
    } );
    Button btnAddMany = new Button( styleComp, SWT.PUSH );
    btnAddMany.setText( "Add " + ADD_ITEMS + " Items" );
    btnAddMany.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        for( int i = 0; i < ADD_ITEMS; i++ ) {
          addItem();
        }
      }
    } );
  }
  
  private void createSelectItemButton() {
    Button button = new Button( styleComp, SWT.PUSH );
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
    Button button = new Button( styleComp, SWT.PUSH );
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
    Button button = new Button( styleComp, SWT.PUSH );
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
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Set topIndex = 100" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        table.setTopIndex( 100 );
      }
    } );
  }
  private void createShowSelectionButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "showSelection" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        table.showSelection();
      }
    } );
  }
  
  private void createChangeCheckButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Change checked for selection" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        TableItem[] selection = table.getSelection();
        for( int i = 0; i < selection.length; i++ ) {
          selection[ i ].setChecked( !selection[ i ].getChecked() );
        }
      }
    } );
  }

  private void createChangeColumnsControl() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    composite.setLayout( new RowLayout(  SWT.HORIZONTAL ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Columns" );
    final Text text = new Text( composite, SWT.BORDER );
    text.setLayoutData( new RowData( 40, 20 ) );
    text.setText( String.valueOf( columns ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        try {
          columns = Integer.parseInt( text.getText() );
        } catch( NumberFormatException e ) {
          // ignore invalid column count
        }
        text.setText( String.valueOf( columns ) );
        createNew();
      }
    } );
  }
  
  private void createChangeItemControl() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    composite.setLayout( new RowLayout(  SWT.HORIZONTAL ) );
    Label lblIndex = new Label( composite, SWT.NONE );
    lblIndex.setText( "Index" );
    final Text txtIndex = new Text( composite, SWT.BORDER );
    txtIndex.setText( "0" );
    Label lblText = new Label( composite, SWT.NONE );
    lblText.setText( "Text" );
    final Text txtText = new Text( composite, SWT.BORDER );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        try {
          int index = Integer.parseInt( txtIndex.getText() );
          TableItem[] selection = getTable().getSelection();
          if( selection.length > 0 ) {
            selection[ 0 ].setText( index, txtText.getText() );
          }
        } catch( NumberFormatException e ) {
          // ignore invalid index number
        }
      }
    } );
    getTable().addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        try {
          int index = Integer.parseInt( txtIndex.getText() );
          TableItem[] selection = getTable().getSelection();
          if( selection.length > 0 ) {
            txtText.setText( selection[ 0 ].getText( index ) );
          }
        } catch( NumberFormatException e ) {
          // ignore invalid index number
        }
      }
    } );
  }
  
  private Table getTable() {
    return table;
  }
}
