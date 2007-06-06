/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class TableTab extends ExampleTab {

  private static final int INITIAL_ITEMS = 1;
  
  private Table table;
  private boolean headerVisible = true;
  private boolean linesVisible;
  private boolean columnImages;
  private int columnsWidthImages = 0;
  private int columns = 5;
  private final Image columnImage;
  private final Image smallImage; 
//  private final Image bigImage; 

  public TableTab( final CTabFolder folder ) {
    super( folder, "Table" );
    columnImage = Image.find( "resources/shell.gif",
                              getClass().getClassLoader() );
    smallImage = Image.find( "resources/newfile_wiz.gif", 
                             getClass().getClassLoader() );
//    bigImage = Image.find( "resources/big_image.png", 
//                           getClass().getClassLoader() );
  }

  protected void createStyleControls() {
    createStyleButton( "MULTI" );
    createStyleButton( "CHECK" );
    createStyleButton( "BORDER" );
    createVisibilityButton();
    createEnablementButton();
    createHeaderVisibleButton();
    createLinesVisibleButton();
    createColumnImagesButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createAddItemsButton();
    createSelectItemButton();
    crateDisposeFirstColumnButton();
    createDisposeSelectionButton();
    createTopIndexButton();
    createShowSelectionButton();
    createChangeCheckButton();
    createChangeGrayButton();
    createChangeColumnsControl();
    createChangeItemControl();
    // TODO [rh] enable as soone as images are working properly
//    createImagesControl();
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
      if( columnImages ) {
        column.setImage( columnImage );
      }
      column.setWidth( i == 0 ? 50 : 100 );
      column.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent event ) {
          Table table = column.getParent();
          if( table.getSortColumn() == column ) {
            if( table.getSortDirection() == SWT.UP ) {
              table.setSortDirection( SWT.DOWN );
            } else {
              table.setSortDirection( SWT.UP );
            }
          } else {
            table.setSortDirection( SWT.UP );
            table.setSortColumn( column );
          }
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

  private void createColumnImagesButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Column images" );
    button.setSelection( columnImages );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        columnImages = button.getSelection();
        TableColumn[] columns = getTable().getColumns();
        for( int i = 0; i < columns.length; i++ ) {
          if( columnImages ) {
            columns[ i ].setImage( columnImage );
          } else {
            columns[ i ].setImage( null );
          }
        }
      }
    } );
  }

  private TableItem addItem() {
    TableItem result = new TableItem( table, SWT.NONE );
    int itemCount = result.getParent().getItemCount() - 1;
    if( columns == 0 ) {
      result.setText( "Item " + itemCount );
      if( columnsWidthImages == 1 ) {
        result.setImage( smallImage );
      }
    } else {
      for( int i = 0; i < columns; i++ ) {
        result.setText( i, "Item" + itemCount + "-" + i );
        if( i < columnsWidthImages ) {
          result.setImage( i, smallImage );
        }
      }
    }
    return result;
  }

  private void createAddItemsButton() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Add" );
    final Text text = new Text( composite, SWT.BORDER );
    text.setLayoutData( new GridData( 30, SWT.DEFAULT ) );
    text.setText( "1" );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Item(s)" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int count = -1;
        try {
          count = Integer.parseInt( text.getText() );
        } catch( NumberFormatException e ) {
          // 
        }
        if( count < 0 ) {
          String msg = "Invalid number of TableItems: " + text.getText();
          MessageDialog.openInformation( getShell(), "Information", msg, null );
        } else {
          for( int i = 0; i < count; i++ ) {
            addItem();
          }
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

  private void createChangeGrayButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Change grayed for selection" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        TableItem[] selection = table.getSelection();
        for( int i = 0; i < selection.length; i++ ) {
          selection[ i ].setGrayed( !selection[ i ].getGrayed() );
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
    composite.setLayout( new GridLayout( 4, false ) );
    Label lblIndex = new Label( composite, SWT.NONE );
    lblIndex.setText( "Index" );
    final Text txtIndex = new Text( composite, SWT.BORDER );
    txtIndex.setText( "0" );
    Label lblText = new Label( composite, SWT.NONE );
    lblText.setText( "Text" );
    final Text txtText = new Text( composite, SWT.BORDER );
    Button button = new Button( composite, SWT.PUSH );
    GridData gridData 
      = new GridData( SWT.BEGINNING, SWT.CENTER, false, false, 4, SWT.DEFAULT );
    button.setLayoutData( gridData );
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
  
//  private void createImagesControl() {
//    Composite composite = new Composite( styleComp, SWT.NONE );
//    composite.setLayout( new RowLayout( SWT.HORIZONTAL ) );
//    Label label = new Label( composite , SWT.NONE );
//    label.setText( "Columns width images" );
//    final Spinner spinner = new Spinner( composite , SWT.BORDER );
//    spinner.setLayoutData( new RowData( 50, 20 ) );
//    Button button = new Button( composite , SWT.PUSH );
//    button.setText( "Change" );
//    button.addSelectionListener( new SelectionAdapter() {
//      public void widgetSelected( final SelectionEvent event ) {
//        int count = spinner.getSelection();
//        for( int i = 0; i < table.getItemCount(); i++ ) {
//          for( int c = 0; c < table.getColumnCount(); c++ ) {
//            TableItem item = table.getItem( i );
//            if( c < count ) {
//              item.setImage( c, bigImage );
//            } else {
//              item.setImage( c, null );
//            }
//          }
//        }
//      }
//    } );
//  }

  private Table getTable() {
    return table;
  }
}