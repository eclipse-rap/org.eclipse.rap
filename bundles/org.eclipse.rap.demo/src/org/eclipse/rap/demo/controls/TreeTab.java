/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.IWindowCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class TreeTab extends ExampleTab {

  private Tree tree;

  public TreeTab( final CTabFolder topFolder ) {
    super( topFolder, "Tree" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "CHECK" );
    createStyleButton( "MULTI" );
    createVisibilityButton();
    createEnablementButton();
    createImagesButton();
    createAddNodeButton();
    createDisposeNodeButton();
    createSelectNodeButton();
    createFontChooser();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    int style = getStyle();
    tree = new Tree( parent, style );
    tree.setLayoutData( new RowData( 200, 200 ) );
    for( int i = 0; i < 4; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      item.setText( "Node_" + ( i + 1 ) );
      if( i < 3 ) {
        TreeItem subitem = new TreeItem( item, SWT.NONE );
        subitem.setText( "Subnode_" + ( i + 1 ) );
      }
    }
    final Label lblTreeEvent = new Label( parent, SWT.NONE );
    lblTreeEvent.setLayoutData( new RowData( 200, 22 ) );
    Menu treeMenu = new Menu( tree );
    MenuItem treeMenuItem = new MenuItem( treeMenu, SWT.PUSH );
    treeMenuItem.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        IWindowCallback windowCallback = new IWindowCallback() {
          public void windowClosed( final int returnCode ) {
            // do nothing
          }
        };
        TreeItem item = tree.getSelection()[ 0 ];
        String itemText = "null";
        if( item != null ) {
          item.getText();
        }
        String message = "You requested a context menu for: " + itemText;
        MessageDialog.openInformation( tree.getShell(), 
                                       "Information", 
                                       message, 
                                       windowCallback );
      }
    } );
    treeMenuItem.setText( "TreeContextMenuItem" );
    tree.setMenu( treeMenu );
    tree.addTreeListener( new TreeListener() {
      public void treeCollapsed( final TreeEvent event ) {
        lblTreeEvent.setText( "Collapsed: "  + event.item.getText() );
      }
      public void treeExpanded( final TreeEvent event ) {
        lblTreeEvent.setText( "Expanded: "  + event.item.getText() );
      }
    } );
    tree.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        String msg = "Selected: ";
        if( ( getStyle() & SWT.CHECK ) != 0 ) {
          TreeItem item = ( TreeItem )event.item;
          msg += ( item.getChecked() ? "[x] " : "[ ] " );
        }
        msg += event.item.getText();
        switch( event.detail ) {
          case SWT.NONE:
            msg += ", detail: SWT.NONE";
            break;
          case SWT.CHECK:
            msg += ", detail: SWT.CHECK";
            break;
        }
        lblTreeEvent.setText( msg );
      }
      
      public void widgetDefaultSelected( final SelectionEvent event ) {
        String title = "Double Click";
        String message = "Double click on " + event.item.getText() + " received";
        MessageDialog.openInformation( getShell(), title, message, null );
      }
    } );
    registerControl( tree );
  }

  private void createImagesButton() {
    final Button button = new Button( styleComp, SWT.TOGGLE );
    button.setText( "Show Images" );
    button.setSelection( true );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Image image;
        if( button.getSelection() ) {
          image = Image.find( "resources/tree_item.gif", 
                              getClass().getClassLoader() );
        } else {
          image = null;
        }
        changeImage( tree, image );
      }
    } );
  }

  private void createAddNodeButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Add child item" );
    button.setLayoutData( new RowData( 100, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( tree.getSelectionCount() > 0 ) {
          TreeItem selection = tree.getSelection()[ 0 ];
          TreeItem treeItem = new TreeItem( selection, SWT.NONE );
          Object[] args = new Object[] { 
            new Integer( selection.getItemCount() ), 
            selection.getText()
          };
          String text = MessageFormat.format( "SubItem {0} of {1}", args );
          treeItem.setText( text  );
          treeItem.setChecked( true );
          Image image = Image.find( "resources/tree_item.gif", 
                                    getClass().getClassLoader() );
          treeItem.setImage( image );
        }
      }
    } );
  }
  
  private void createDisposeNodeButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Dispose Selected Item" );
    button.setLayoutData( new RowData( 100, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( tree.getSelectionCount() > 0 ) {
          TreeItem selection = tree.getSelection()[ 0 ];
          selection.dispose();
        }
      }
    } );
  }

  private void createSelectNodeButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Select First Item" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( tree.getItemCount() > 0 ) {
          tree.setSelection( tree.getItem( 0 ) );
        } 
      }
    } );
  }

  private static void changeImage( final Tree tree, final Image image ) {
    TreeItem[] items = tree.getItems();
    for( int i = 0; i < items.length; i++ ) {
      changeImage( items[ i ], image );
    }
  }
  
  private static void changeImage( final TreeItem item, final Image image ) {
    item.setImage( image );
    TreeItem[] items = item.getItems();
    for( int i = 0; i < items.length; i++ ) {
      changeImage( items[ i ], image );
    }
  }
}
