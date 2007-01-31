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
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TreeTab extends ExampleTab {

  private Tree tree;

  public TreeTab( final TabFolder folder ) {
    super( folder, "Tree" );
  }

  void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "CHECK" );
    createVisibilityButton();
    createEnablementButton();
    createLinesVisibleCheck();
    createAddNodeButton();
    createFontChooser();
  }

  void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    int style = getStyle();
    tree = new Tree( parent, style );
    tree.setLinesVisible( true );
    tree.setLayoutData( new RowData( 200, 200 ) );
    for( int i = 0; i < 4; i++ ) {
      TreeItem item = new TreeItem( tree, RWT.NONE );
      item.setText( "Node_" + ( i + 1 ) );
      if( i < 3 ) {
        TreeItem subitem = new TreeItem( item, RWT.NONE );
        subitem.setText( "Subnode_" + ( i + 1 ) );
      }
    }
    Menu treeMenu = new Menu( tree );
    MenuItem treeMenuItem = new MenuItem( treeMenu, RWT.PUSH );
    treeMenuItem.setText( "TreeContextMenuItem" );
    tree.setMenu( treeMenu );
    registerControl( tree );
  }

  private void createLinesVisibleCheck() {
    final Button button = new Button( styleComp, RWT.CHECK );
    button.setSelection( tree.getLinesVisible() );
    button.setLayoutData( new RowData( 100, 20 ) );
    button.setText( "Lines visible" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        tree.setLinesVisible( button.getSelection() );
      }
    } );
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
          Image image = Image.find( "resources/tree_item.gif", 
                                    getClass().getClassLoader() );
          treeItem.setImage( image );
        }
      }
    } );
  }
}
