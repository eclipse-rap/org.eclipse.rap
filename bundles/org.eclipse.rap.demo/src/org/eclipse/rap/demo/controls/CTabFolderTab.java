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
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class CTabFolderTab extends ExampleTab {

//  private static final int[] BACKGROUND_COLORS 
//    = { SWT.COLOR_RED, SWT.COLOR_GREEN, SWT.COLOR_BLUE, SWT.COLOR_DARK_GRAY };
  
  private CTabFolder folder;
  private boolean unselectedCloseVisible;
  private boolean topRightControl;
  private boolean minVisible;
  private boolean maxVisible;


  public CTabFolderTab( final CTabFolder parent ) {
    super( parent, "CTabFolder" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "FLAT", SWT.BORDER );
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "TOP", SWT.TOP );
    createStyleButton( "BOTTOM", SWT.BOTTOM );
    createStyleButton( "CLOSE", SWT.CLOSE );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
    createTabHeightControl( styleComp );
    createTopRightControl( styleComp );
    for( int i = 0; i < 3; i++ ) {
      final int index = i;
      String rbText = "Select Tab " + i;
      Button rbSelectTab = createPropertyButton( rbText, SWT.RADIO );
      rbSelectTab.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent event ) {
          folder.setSelection( index );
        }
      } );
    }
    final Button cbMin = createPropertyButton( "Minimize visible", SWT.CHECK );
    cbMin.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        minVisible = cbMin.getSelection();
        updateProperties();
      }
    } );
    final Button cbMax = createPropertyButton( "Maximize visible", SWT.CHECK );
    cbMax.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        maxVisible = cbMax.getSelection();
        updateProperties();
      }
    } );
    String text = "UnselectedCloseVisible";
    Button cbUnselectedCloseVisible = createPropertyButton( text, SWT.CHECK );
    cbUnselectedCloseVisible.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Button button = ( Button )event.widget;
        unselectedCloseVisible = button.getSelection();
        updateProperties();
      }
    } );
    text = "Switch tabPosition";
    Button btnSwitchTabPosition = createPropertyButton( text, SWT.PUSH );
    btnSwitchTabPosition.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        int tabPosition = folder.getTabPosition();
        if( tabPosition == SWT.TOP ) {
          tabPosition = SWT.BOTTOM;
        } else {
          tabPosition = SWT.TOP;
        }
        folder.setTabPosition( tabPosition );
      }
    } );
    Button btnAddTabItem = new Button( parent, SWT.PUSH );
    btnAddTabItem.setText( "Add Item (SWT.NONE)" );
    btnAddTabItem.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createTabItem( SWT.NONE );
      }
    } );
    Button btnAddCloseableTabItem = new Button( parent, SWT.PUSH );
    btnAddCloseableTabItem.setText( "Add Item (SWT.CLOSE)" );
    btnAddCloseableTabItem.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createTabItem( SWT.CLOSE );
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    Display display = parent.getDisplay();
    parent.setBackground( BG_COLOR_BROWN );
    FillLayout layout = new FillLayout();
    layout.marginHeight = 5;
    layout.marginWidth = 5;
    parent.setLayout( layout );
    folder = new CTabFolder( parent, getStyle() );
    for( int i = 0; i < 3; i++ ) {
      createTabItem( SWT.NONE );
    }
    Color selectionBg = display.getSystemColor( SWT.COLOR_LIST_SELECTION );
    folder.setSelectionBackground( selectionBg );
    folder.setSelection( 0 );
    folder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
      }
      public void widgetDefaultSelected( final SelectionEvent event ) {
      }
    });
    registerControl( folder );
    updateTopRightControl();
    updateProperties();
  }

  private void createTabItem( final int style ) {
//    Display display = folder.getDisplay();
    CTabItem item = new CTabItem( folder, style );
    item.setText( "Tab " + folder.getItemCount() );
// TODO [rh] re-activate as soon as mis-calculations of clientArea are solved    
//    Text content = new Text( folder, SWT.WRAP | SWT.MULTI );
//    int colorIndex = folder.getItemCount() - 1;
//    if( colorIndex >= BACKGROUND_COLORS.length ) {
//      colorIndex = BACKGROUND_COLORS.length - 1;
//    }
//    Color color = display.getSystemColor( BACKGROUND_COLORS[ colorIndex ] );
//    content.setBackground( color );
//    content.setText( "" );
//    item.setControl( content );
  }

  private void createTabHeightControl( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "TabHeight" );
    final Text text = new Text( parent, SWT.BORDER );
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        String input = text.getText().trim();
        int tabHeight = folder.getTabHeight();
        try {
          tabHeight = Integer.parseInt( input );
          text.setText( String.valueOf( tabHeight ) );
        } catch( NumberFormatException e ) {
          String msg = "Invalid tab height: " + input;
          MessageDialog.openError( getShell(), "Error", msg );
        }
        folder.setTabHeight( tabHeight );
      }
    } );
  }

  private void createTopRightControl( final Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "TopRight Control" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        topRightControl = button.getSelection();
        updateTopRightControl();
      }
    } );
  }
  
  private void updateProperties() {
    folder.setMinimizeVisible( minVisible );
    folder.setMaximizeVisible( maxVisible );
    folder.setUnselectedCloseVisible( unselectedCloseVisible );
  }

  private void updateTopRightControl() {
    if( topRightControl ) {
      Label label = new Label( folder, SWT.NONE );
      label.setText( "topRight" );
      Display display = label.getDisplay();
      label.setBackground( display.getSystemColor( SWT.COLOR_DARK_CYAN ) );
      folder.setTopRight( label );
    } else {
      folder.setTopRight( null );
    }
  }
}
