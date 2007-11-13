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

  private static final int[] BACKGROUND_COLORS 
    = { SWT.COLOR_RED, SWT.COLOR_GREEN, SWT.COLOR_BLUE };
  
  private CTabFolder folder;
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
        updateMinMaxVisible();
      }
    } );
    final Button cbMax = createPropertyButton( "Maximize visible", SWT.CHECK );
    cbMax.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        maxVisible = cbMax.getSelection();
        updateMinMaxVisible();
      }

    } );
  }

  protected void createExampleControls( final Composite parent ) {
    Display display = parent.getDisplay();
    parent.setLayout( new FillLayout() );
    folder = new CTabFolder( parent, getStyle() );
    for( int i = 0; i < 3; i++ ) {
      CTabItem item = new CTabItem( folder, getStyle() );
      item.setText( "Tab " + ( i + 1 ) );
      Text content = new Text( folder, SWT.WRAP | SWT.MULTI );
      content.setBackground( display.getSystemColor( BACKGROUND_COLORS[ i ] ) );
      content.setText( "" );
      item.setControl( content );
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
    updateMinMaxVisible();
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
        int tabHeight = folder.getTabHeight();
        try {
          tabHeight = Integer.parseInt( text.getText() );
        } catch( NumberFormatException e ) {
          MessageDialog.openError( getShell(), "Error", "Invalid tab height" );
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

  private void updateMinMaxVisible() {
    folder.setMinimizeVisible( minVisible );
    folder.setMaximizeVisible( maxVisible );
  }

  private void updateTopRightControl() {
    if( topRightControl ) {
      Label label = new Label( folder, SWT.NONE );
      label.setText( "topRight" );
      Display display = label.getDisplay();
      label.setBackground( display.getSystemColor( SWT.COLOR_BLACK ) );
      folder.setTopRight( label );
    } else {
      folder.setTopRight( null );
    }
  }
}
