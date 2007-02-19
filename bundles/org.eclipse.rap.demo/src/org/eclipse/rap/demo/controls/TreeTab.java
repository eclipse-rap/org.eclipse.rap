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
import org.eclipse.rap.jface.dialogs.MessageDialog;
import org.eclipse.rap.jface.window.IWindowCallback;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TreeTab extends ExampleTab {

  private Tree tree;

  public TreeTab( final TabFolder folder ) {
    super( folder, "Tree" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "CHECK" );
    createVisibilityButton();
    createEnablementButton();
    createAddNodeButton();
    createFontChooser();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout( RWT.VERTICAL ) );
    int style = getStyle();
    tree = new Tree( parent, style );
    tree.setLayoutData( new RowData( 200, 200 ) );
    for( int i = 0; i < 4; i++ ) {
      TreeItem item = new TreeItem( tree, RWT.NONE );
      item.setText( "Node_" + ( i + 1 ) );
      if( i < 3 ) {
        TreeItem subitem = new TreeItem( item, RWT.NONE );
        subitem.setText( "Subnode_" + ( i + 1 ) );
      }
    }
    final Label lblTreeEvent = new Label( parent, RWT.NONE );
    lblTreeEvent.setLayoutData( new RowData( 200, 22 ) );
    Menu treeMenu = new Menu( tree );
    MenuItem treeMenuItem = new MenuItem( treeMenu, RWT.PUSH );
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
        String msg = "Selected: " + event.item.getText();
        switch( event.detail ) {
          case RWT.NONE:
            msg +=", detail: RWT.NONE";
            break;
          case RWT.CHECK:
            msg +=", detail: RWT.CHECK";
            break;
        }
        lblTreeEvent.setText( msg );
      }
    } );
    registerControl( tree );
  }

  private void createAddNodeButton() {
    Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Add child item" );
    button.setLayoutData( new RowData( 100, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( tree.getSelectionCount() > 0 ) {
          TreeItem selection = tree.getSelection()[ 0 ];
          TreeItem treeItem = new TreeItem( selection, RWT.NONE );
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
}
