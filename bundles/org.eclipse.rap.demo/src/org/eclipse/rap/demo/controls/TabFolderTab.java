/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public class TabFolderTab extends ExampleTab {

  protected static final int MAX_ITEMS = 3;

  private boolean onDemandContent;
  private TabFolder folder;
  private TabItem[] tabItems;
  private Button[] tabRadios;

  public TabFolderTab( final CTabFolder topFolder ) {
    super( topFolder, "TabFolder" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "TOP", SWT.TOP );
    createStyleButton( "BOTTOM", SWT.BOTTOM );
    createVisibilityButton();
    createEnablementButton();
    createOnDemandButton( parent );
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    tabRadios = new Button[ MAX_ITEMS ];
    for( int i = 0; i < MAX_ITEMS; i++ ) {
      tabRadios[ i ] = createPropertyButton( "Select Tab " + i, SWT.RADIO );
      final int itemIndex = i;
      tabRadios[ i ].addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent event ) {
          folder.setSelection( itemIndex );
        }
      } );
    }
    tabRadios[ 0 ].setSelection( true );
    createChangeContentButton( parent );
    createInsertItemButton( parent );
    createDisposeItemButton( parent );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    folder = new TabFolder( parent, getStyle() );
    tabItems = new TabItem[ MAX_ITEMS ];
    folder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        TabItem item = ( TabItem )event.item;
        if( tabRadios != null ) {
          int index = item.getParent().indexOf( item );
          for( int i = 0; i < MAX_ITEMS; i++ ) {
            tabRadios[ i ].setSelection( index == i );
          }
        }
        createItemContent( item );
      }
    } );
    for( int i = 0; i < MAX_ITEMS; i++ ) {
      tabItems[ i ] = new TabItem( folder, SWT.NONE );
      tabItems[ i ].setText( "TabItem " + i );
      if( !onDemandContent ) {
        createItemContent( tabItems[ i ] );
      }
    }
    registerControl( folder );
  }

  private void createOnDemandButton( final Composite parent ) {
    Button button = new Button( parent, SWT.CHECK );
    button.setText( "Create Item Content on Demand" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Button button = ( Button )event.widget;
        onDemandContent = button.getSelection();
        createNew();
      }
    } );
  }

  private void createChangeContentButton( final Composite parent ) {
    Button btnChangeContent = new Button( parent, SWT.PUSH );
    btnChangeContent.setText( "Change Content for Selected Item" );
    btnChangeContent.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        TabItem item = folder.getSelection()[ 0 ];
        Label content = new Label( folder, SWT.NONE );
        int index = folder.indexOf( item );
        content.setText( "Alternate content for tab item " + index );
        item.setControl( content );
      }
    } );
  }

  private void createInsertItemButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Insert item before first item" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        TabItem tabItem = new TabItem( folder, SWT.NONE, 0 );
        tabItem.setText( "TabItem " + folder.indexOf( tabItem ) );
        if( !onDemandContent ) {
          createItemContent( tabItem );
        }
      }
    } );
  }
  
  private void createDisposeItemButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Dispose of selected item" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        TabItem[] selection = folder.getSelection();
        if( selection.length > 0 ) {
          selection[ 0 ].dispose();
        }
      }
    } );
  }

  private void createItemContent( final TabItem item ) {
    if( item.getControl() == null ) {
      TabFolder folder = item.getParent();
      Text content = new Text( folder, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY );
      int index = folder.indexOf( item );
      String text = "This is the content for item " + index;
      if( onDemandContent ) {
        text += "\nIt was created on demand, when the item was selected " 
             +  "for the first time through user interaction.";
      }
      content.setText( text );
      item.setControl( content );
    }
  }
}
