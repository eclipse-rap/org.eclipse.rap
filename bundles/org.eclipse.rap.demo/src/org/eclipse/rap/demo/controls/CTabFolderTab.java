/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class CTabFolderTab extends ExampleTab {

//  private static final int[] BACKGROUND_COLORS
//    = { SWT.COLOR_RED, SWT.COLOR_GREEN, SWT.COLOR_BLUE, SWT.COLOR_DARK_GRAY };

  private CTabFolder folder;
  private boolean unselectedCloseVisible;
  private boolean showTopRightControl;
  private boolean minVisible;
  private boolean maxVisible;
  private int selFgIndex;
  private int selBgIndex;
  private int tabHeight = -1;

  public CTabFolderTab( final CTabFolder parent ) {
    super( parent, "CTabFolder" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "FLAT", SWT.FLAT );
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "TOP", SWT.TOP );
    createStyleButton( "BOTTOM", SWT.BOTTOM );
    createStyleButton( "CLOSE", SWT.CLOSE );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
    createFgColorButton();
    createBgColorButton();
    createSelectionFgColorButton();
    createSelectionBgColorButton();
    createBgImageButton();
    createTabHeightControl( styleComp );
    createTopRightControl( styleComp );
    for( int i = 0; i < 3; i++ ) {
      final int index = i;
      String rbText = "Select " + folder.getItem( index ).getText();
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
        boolean isTop = folder.getTabPosition() == SWT.TOP;
        folder.setTabPosition( isTop ? SWT.BOTTOM : SWT.TOP );
      }
    } );
    Button borderVisibleButton
      = createPropertyButton( "Switch borderVisible", SWT.PUSH );
    borderVisibleButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        folder.setBorderVisible( !folder.getBorderVisible() );
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
    GridLayout layout = new GridLayout();
    layout.marginHeight = 5;
    layout.marginWidth = 5;
    parent.setLayout( layout );
    folder = new CTabFolder( parent, getStyle() );
    folder.setLayoutData( new GridData( 300, 300 ) );
    for( int i = 0; i < 3; i++ ) {
      createTabItem( SWT.NONE );
    }
    folder.setSelection( 0 );
    folder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
      }
      public void widgetDefaultSelected( final SelectionEvent event ) {
      }
    } );
    registerControl( folder );
    if( tabHeight >= 0 ) {
      folder.setTabHeight( tabHeight );
    }
    updateTopRightControl();
    updateProperties();
    updateSelFgColor();
    updateSelBgColor();
  }

  private void createTabItem( final int style ) {
    CTabItem item = new CTabItem( folder, style );
    int count = folder.getItemCount();
    item.setText( "Tab " + count );
    if( count != 3 ) {
      Text content = new Text( folder, SWT.WRAP | SWT.MULTI );
      if( count % 2 != 0 ) {
        content.setBackground( BG_COLOR_BROWN );
        content.setForeground( FG_COLOR_BLUE );
      }
      content.setText( "Some Content " + count );
      item.setControl( content );
    }
  }

  private void createTabHeightControl( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Tab Height" );
    final Spinner spinner = new Spinner( composite, SWT.BORDER );
    spinner.setSelection( folder.getTabHeight() );
    spinner.setMinimum( 0 );
    spinner.setMaximum( 100 );
    spinner.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        tabHeight = spinner.getSelection();
        folder.setTabHeight( tabHeight );
      }
    } );
  }

  private void createTopRightControl( final Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Top Right Control" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showTopRightControl = button.getSelection();
        updateTopRightControl();
      }
    } );
  }

  private Button createSelectionFgColorButton() {
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Selection Foreground" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        selFgIndex = ( selFgIndex + 1 ) % fgColors.length;
        updateSelFgColor();
      }
    } );
    return button;
  }

  private Button createSelectionBgColorButton() {
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Selection Background" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        selBgIndex = ( selBgIndex + 1 ) % bgColors.length;
        updateSelBgColor();
      }
    } );
    return button;
  }

  private void updateProperties() {
    folder.setMinimizeVisible( minVisible );
    folder.setMaximizeVisible( maxVisible );
    folder.setUnselectedCloseVisible( unselectedCloseVisible );
  }

  private void updateTopRightControl() {
    if( showTopRightControl ) {
      Label label = new Label( folder, SWT.NONE );
      label.setText( "topRight" );
      Display display = label.getDisplay();
      label.setBackground( display.getSystemColor( SWT.COLOR_DARK_CYAN ) );
      folder.setTopRight( label );
    } else {
      Control topRight = folder.getTopRight();
      if( topRight != null && !topRight.isDisposed() ) {
        topRight.dispose();
      }
      folder.setTopRight( null );
    }
  }

  private void updateSelFgColor() {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      if( control instanceof CTabFolder ) {
        CTabFolder folder = ( CTabFolder )control;
        folder.setSelectionForeground( fgColors[ selFgIndex ] );
      }
    }
  }

  private void updateSelBgColor() {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      if( control instanceof CTabFolder ) {
        CTabFolder folder = ( CTabFolder )control;
        folder.setSelectionBackground( bgColors[ selBgIndex ] );
      }
    }
  }
}
