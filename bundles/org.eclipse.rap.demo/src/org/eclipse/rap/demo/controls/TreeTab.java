/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class TreeTab extends ExampleTab {

  private Tree tree;
  private boolean showImages;
  private final Image treeImage;

  public TreeTab( final CTabFolder topFolder ) {
    super( topFolder, "Tree" );
    treeImage = Graphics.getImage( "resources/tree_item.gif",
                                   getClass().getClassLoader() );
    showImages = true;
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "CHECK", SWT.CHECK );
    createStyleButton( "MULTI", SWT.MULTI );
    createVisibilityButton();
    createEnablementButton();
    createImagesButton( parent );
    createAddNodeButton( parent );
    createDisposeNodeButton( parent );
    createSelectAllButton( parent );
    createDeselectAllButton( parent );
    createSelectButton( parent );
    createDeselectButton( parent );
    createSetSelectionButton( parent );
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    final Button itemFgButton
      = createPropertyButton( "Custom foreground on 1st item", SWT.CHECK );
    itemFgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        TreeItem item = tree.getItem( 0 );
        Display display = parent.getDisplay();
        Color green = display.getSystemColor( SWT.COLOR_GREEN );
        item.setForeground( itemFgButton.getSelection() ? green  : null );
      }
    } );
    final Button itemBgButton
      = createPropertyButton( "Custom background on 1st item", SWT.CHECK );
    itemBgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        TreeItem item = tree.getItem( 0 );
        Display display = parent.getDisplay();
        Color red = display.getSystemColor( SWT.COLOR_DARK_RED );
        item.setBackground( itemBgButton.getSelection() ? red  : null );
      }
    } );
    final Button itemGrayButton2 = createPropertyButton( "Gray out 2nd item",
        SWT.CHECK );
    itemGrayButton2.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        tree.getItem( 1 ).setGrayed( itemGrayButton2.getSelection() );
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    int style = getStyle();
    tree = new Tree( parent, style );
    tree.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    TreeColumn col1 = new TreeColumn( tree, SWT.NONE );
    col1.setText( "Col 1" );
    col1.setWidth( 150 );
    TreeColumn col2 = new TreeColumn( tree, SWT.NONE );
    col2.setText( "Col 2" );
    col2.setWidth( 150 );
    TreeColumn col3 = new TreeColumn( tree, SWT.NONE );
    col3.setText( "Col 3" );
    col3.setWidth( 150 );
    for( int i = 0; i < 10; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      item.setText( "Node_" + ( i + 1 ) + ".1" );
      item.setText( 1, "Node_" + ( i + 1 ) + ".2" );
      item.setText( 2, "Node_" + ( i + 1 ) + ".3" );
      if( i % 2 == 0 ) {
        TreeItem subitem = new TreeItem( item, SWT.NONE );
        subitem.setText( "Subnode_" + ( i + 1 ) + ".1" );
        subitem.setText( 1, "Subnode_" + ( i + 1 ) + ".2" );
        subitem.setText( 2, "Subnode_" + ( i + 1 ) + ".3" );
      }
    }
    if( showImages ) {
      changeImage( tree, treeImage );
    }
    final Label lblTreeEvent = new Label( parent, SWT.NONE );
    Menu treeMenu = new Menu( tree );
    MenuItem treeMenuItem = new MenuItem( treeMenu, SWT.PUSH );
    treeMenuItem.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        TreeItem item = tree.getSelection()[ 0 ];
        String itemText = "null";
        if( item != null ) {
          item.getText();
        }
        String message = "You requested a context menu for: " + itemText;
        MessageDialog.openInformation( tree.getShell(),
                                       "Information",
                                       message );
      }
    } );
    treeMenuItem.setText( "TreeContextMenuItem" );
    tree.setMenu( treeMenu );
    tree.addTreeListener( new TreeListener() {
      public void treeCollapsed( final TreeEvent event ) {
        Item item = ( Item )event.item;
        lblTreeEvent.setText( "Collapsed: "  + item.getText() );
      }
      public void treeExpanded( final TreeEvent event ) {
        Item item = ( Item )event.item;
        lblTreeEvent.setText( "Expanded: "  + item.getText() );
      }
    } );
    tree.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        String msg = "Selected: ";
        TreeItem item = ( TreeItem )event.item;
        if( ( getStyle() & SWT.CHECK ) != 0 ) {
          msg += ( item.getChecked() ? "[x] " : "[ ] " );
        }
        msg += item.getText();
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
        String title = "Widget Default Selected";
        Item item = ( Item )event.item;
        String message = "Widget default selected on "
          + item.getText()
          + " received";
        MessageDialog.openInformation( getShell(), title, message );
      }
    } );
    tree.setSelection( tree.getItem( 0 ) );
    tree.setHeaderVisible( true );

    registerControl( tree );
  }

  private void createImagesButton( final Composite parent ) {
    final Button button = new Button( parent, SWT.TOGGLE );
    button.setText( "Show Images" );
    button.setSelection( true );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showImages = button.getSelection();
        changeImage( tree, showImages ? treeImage : null );
      }
    } );
  }

  private void createAddNodeButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Add child item" );
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
          if( showImages ) {
            treeItem.setImage( treeImage );
          }
        }
      }
    } );
  }

  private void createDisposeNodeButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Dispose Selected Item" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( tree.getSelectionCount() > 0 ) {
          TreeItem selection = tree.getSelection()[ 0 ];
          selection.dispose();
        }
      }
    } );
  }

  private void createSelectAllButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Select All" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        tree.selectAll();
      }
    } );
  }

  private void createDeselectAllButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Deselect All" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        tree.deselectAll();
      }
    } );
  }

  private void createSelectButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Select second node" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( tree.getItemCount() > 1 ) {
          tree.select( tree.getItem( 1 ) );
        }
      }
    } );
  }

  private void createDeselectButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Deselect second node" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( tree.getItemCount() > 1 ) {
          tree.deselect( tree.getItem( 1 ) );
        }
      }
    } );
  }

  private void createSetSelectionButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Set selection to first node" );
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
