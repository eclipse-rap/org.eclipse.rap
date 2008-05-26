/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class TableTab extends ExampleTab {

  private static final int INITIAL_ITEMS = 1;

  private Table table;
  private boolean headerVisible = true;
  private boolean linesVisible;
  private boolean updateVirtualItemsDelayed;
  private boolean columnImages;
  private boolean columnsMoveable;
  private int columnsWidthImages = 0;
  private int columns = 5;
  private final Image columnImage;
  private Image itemImage;
  private final Image smallImage;
  private final Image largeImage;

  public TableTab( final CTabFolder folder ) {
    super( folder, "Table" );
    columnImage = Graphics.getImage( "resources/shell.gif",
                              getClass().getClassLoader() );
    smallImage = Graphics.getImage( "resources/newfile_wiz.gif",
                             getClass().getClassLoader() );
    largeImage = Graphics.getImage( "resources/big_image.png",
                             getClass().getClassLoader() );
    itemImage = smallImage;
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "CHECK", SWT.CHECK );
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "VIRTUAL", SWT.VIRTUAL );
    createVisibilityButton();
    createEnablementButton();
    createHeaderVisibleButton();
    createLinesVisibleButton();
    createColumnsMoveableButton();
    createColumnImagesButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createAddItemsButton();
    createInsertItemButton();
    createSelectItemButton();
    createDisposeFirstColumnButton();
    createDisposeSelectionButton();
    createRecreateButton();
    createTopIndexButton();
    createShowSelectionButton();
    createChangeCheckButton();
    createChangeGrayButton();
    createChangeColumnsControl();
    createRevertColumnOrderButton();
    createPackColumnsButton();
    createChangeItemControl();
    createChangeItemCountControl();
    createImagesControl();
    createAlignmentControl();
    createBackgroundControl();
    createSelectAtPointControl();
    createQueryTopIndex();
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
        log( "click: " + event.item );
      }
      public void widgetDefaultSelected( final SelectionEvent event ) {
        log( "double-click: " + event.item );
      }
    } );
    if( ( style & SWT.VIRTUAL ) != 0 ) {
      table.addListener( SWT.SetData, new Listener() {
        public void handleEvent( final Event event ) {
          if( event.type == SWT.SetData ) {
            if( updateVirtualItemsDelayed ) {
              final Display display = event.display;
              Job job = new Job( "Delayed Table Item Update" ) {
                protected IStatus run( final IProgressMonitor monitor ) {
                  display.asyncExec( new Runnable() {
                    public void run() {
                      updateItem( ( TableItem )event.item );
                    }
                  } );
                  return Status.OK_STATUS;
                }
              };
              job.schedule( 1000 );
            } else {
              updateItem( ( TableItem )event.item );
            }
          }
        }
      } );
    }
    for( int i = 0; i < columns; i++ ) {
      final TableColumn column = new TableColumn( table, SWT.NONE );
      column.setText( "Col " + i );
      if( columnImages ) {
        column.setImage( columnImage );
      }
      column.setWidth( i == 0 ? 50 : 100 );
      column.setMoveable( columnsMoveable );
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

  private void createColumnsMoveableButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Moveable Columns" );
    button.setSelection( columnsMoveable );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        columnsMoveable = button.getSelection();
        TableColumn[] columns = table.getColumns();
        for( int i = 0; i < columns.length; i++ ) {
          columns[ i ].setMoveable( columnsMoveable );
        }
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
    updateItem( result );
    return result;
  }

  private void updateItem( final TableItem item ) {
    int index = item.getParent().indexOf( item );
    if( columns == 0 ) {
      item.setText( "Item " + index );
      if( columnsWidthImages >= 1 ) {
        item.setImage( itemImage );
      }
    } else {
      for( int i = 0; i < columns; i++ ) {
        item.setText( i, "Item" + index + "-" + i );
        if( i < columnsWidthImages ) {
          item.setImage( i, itemImage );
        }
      }
    }
  }

  private void createAddItemsButton() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Add" );
    final Text text = new Text( composite, SWT.BORDER );
    Util.textSizeAdjustment( label, text );
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
          MessageDialog.openInformation( getShell(), "Information", msg );
        } else {
          for( int i = 0; i < count; i++ ) {
            addItem();
          }
        }
      }
    } );
  }

  private void createInsertItemButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Insert On Selected Item" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int[] selectionIndices = getTable().getSelectionIndices();
        if( selectionIndices.length > 0 ) {
          int index = selectionIndices[ 0 ];
          TableItem item = new TableItem( getTable(), SWT.NONE, index );
          updateItem( item );
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

  private void createDisposeFirstColumnButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Dispose first Column" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( getTable().getColumnCount() > 0 ) {
          int firstColumn = getTable().getColumnOrder()[ 0 ];
          getTable().getColumn( firstColumn ).dispose();
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

  private void createRecreateButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Recreate Last Item" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int count = getTable().getItemCount();
        if( count > 0 ) {
          TableItem item = getTable().getItem( count - 1 );
          item.dispose();
          item = addItem();
          item.setText( "Recreated" );
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
    Util.textSizeAdjustment( label, text );
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
    Util.textSizeAdjustment( lblIndex, txtIndex );
    txtIndex.setText( "0" );
    Label lblText = new Label( composite, SWT.NONE );
    lblText.setText( "Text" );
    final Text txtText = new Text( composite, SWT.BORDER );
    Util.textSizeAdjustment( lblText, txtText );
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

  private void createChangeItemCountControl() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label lblItemCount = new Label( composite, SWT.NONE );
    lblItemCount.setText( "ItemCount" );
    final Text txtItemCount = new Text( composite, SWT.BORDER );
    Util.textSizeAdjustment( lblItemCount, txtItemCount );
    txtItemCount.setText( String.valueOf( getTable().getItemCount() ) );
    Button btnChange = new Button( composite, SWT.PUSH );
    btnChange.setText( "Change" );
    btnChange.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int itemCount = -1;
        try {
          itemCount = Integer.parseInt( txtItemCount.getText() );
        } catch( NumberFormatException e ) {
          // ignore invalid item count
        }
        getTable().setItemCount( itemCount );
        getTable().redraw();
      }
    } );
    final Button cbDelayedUpdate = new Button( composite, SWT.CHECK );
    GridData gridData 
      = new GridData( SWT.LEFT, SWT.CENTER, true, false, 3, SWT.DEFAULT );
    cbDelayedUpdate.setLayoutData( gridData );
    cbDelayedUpdate.setText( "Update virtual items delayed" );
    cbDelayedUpdate.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        updateVirtualItemsDelayed = cbDelayedUpdate.getSelection(); 
      }
    } );
  }

  private void createRevertColumnOrderButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Revert Column Order" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int[] columnOrder = table.getColumnOrder();
        int columnIndex = table.getColumnCount() - 1;
        for( int i = 0; i < columnOrder.length; i++ ) {
          columnOrder[ i ] = columnIndex;
          columnIndex--;
        }
        table.setColumnOrder( columnOrder );
      }
    } );
  }

  private void createPackColumnsButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Pack Columns" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        TableColumn[] columns = getTable().getColumns();
        for( int i = 0; i < columns.length; i++ ) {
          columns[ i ].pack();
        }
      }
    } );
  }

  private void createImagesControl() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label lblImages = new Label( composite , SWT.NONE );
    lblImages.setText( "Images:" );
    final Button rbSmall = new Button( composite, SWT.RADIO );
    rbSmall.setSelection( itemImage == smallImage );
    rbSmall.setText( "Small" );
    Button rbLarge = new Button( composite, SWT.RADIO );
    rbLarge.setSelection( itemImage == largeImage );
    rbLarge.setText( "Large" );
    Label lblOn = new Label( composite, SWT.NONE );
    lblOn.setText( "On" );
    final Spinner spnCount = new Spinner( composite , SWT.BORDER );
    Button btnChange = new Button( composite , SWT.PUSH );
    btnChange.setText( "Columns" );
    btnChange.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        columnsWidthImages = spnCount.getSelection();
        if( rbSmall.getSelection() ) {
          itemImage = smallImage;
        } else {
          itemImage = largeImage;
        }
        for( int i = 0; i < table.getItemCount(); i++ ) {
          for( int c = 0; c < table.getColumnCount(); c++ ) {
            TableItem item = table.getItem( i );
            if( c < columnsWidthImages ) {
              item.setImage( c, itemImage );
            } else {
              item.setImage( c, null );
            }
          }
        }
      }
    } );
  }

  private void createAlignmentControl() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    composite.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Alignment" );
    final Combo combo = new Combo( composite, SWT.READ_ONLY );
    combo.add( "SWT.LEFT" );
    combo.add( "SWT.CENTER" );
    combo.add( "SWT.RIGHT" );
    combo.select( 0 );
    combo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int alignment = SWT.LEFT;
        if( combo.getSelectionIndex() == 1 ) {
          alignment = SWT.CENTER;
        } else if( combo.getSelectionIndex() == 2 ) {
          alignment = SWT.RIGHT;
        }
        TableColumn[] columns = getTable().getColumns();
        for( int i = 0; i < columns.length; i++ ) {
          columns[ i ].setAlignment( alignment );
        }
      }
    } );
  }

  private void createBackgroundControl() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Change Item Appearance" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Display display = getTable().getDisplay();
        FontData fontData = getTable().getFont().getFontData()[ 0 ];
        String fontName = fontData.getName();
        Font font = Graphics.getFont( fontName, fontData.getHeight(), SWT.BOLD );
        Color background = display.getSystemColor( SWT.COLOR_DARK_GREEN );
        Color foreground = display.getSystemColor( SWT.COLOR_DARK_CYAN );
        TableItem[] items = getTable().getItems();
        for( int i = 0; i < items.length; i++ ) {
          if( i % 2 == 0 ) {
            items[ i ].setBackground( background );
          } else {
            items[ i ].setForeground( foreground );
            items[ i ].setFont( font );
          }
        }
      }
    } );
  }

  private void createSelectAtPointControl() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    composite.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    Label lblSelectAt = new Label( composite, SWT.NONE );
    lblSelectAt.setText( "Select at X" );
    final Text txtX = new Text( composite, SWT.BORDER );
    txtX.setText( "0" );
    Label lblY = new Label( composite, SWT.NONE );
    lblY.setText( "Y" );
    final Text txtY = new Text( composite, SWT.BORDER );
    txtY.setText( "0" );
    Button btnSelect = new Button( composite, SWT.PUSH );
    btnSelect.setText( "OK" );
    btnSelect.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        try {
          int x = Integer.parseInt( txtX.getText() );
          int y = Integer.parseInt( txtY.getText() );
          Point point = new Point( x, y );
          TableItem item = getTable().getItem( point );
          if( item != null ) {
            getTable().setSelection( item );
          } else {
            Shell shell = getTable().getShell();
            String msg = "No table item at this coordinate.";
            MessageDialog.openInformation( shell, "Information", msg );
          }
        } catch( NumberFormatException e ) {
          Shell shell = getTable().getShell();
          String msg = "Invalid x or y coordinate.";
          MessageDialog.openError( shell, "Error", msg );
        }
      }
    } );
  }

  private void createQueryTopIndex() {
    Button btn = new Button( styleComp, SWT.PUSH );
    btn.setText( "Query topIndex" );
    btn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        Shell shell = styleComp.getShell();
        String msg = "Current topIndex: " + getTable().getTopIndex();
        MessageDialog.openInformation( shell, "Information", msg );
      }
    } );
  }

  private Table getTable() {
    return table;
  }
}