/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

class ExpandBarTab extends ExampleTab {

  private static final String PROP_CONTEXT_MENU = "contextMenu";
  private static final String PROP_EXPAND_LISTENER = "expandListener";
  ExpandBar expandBar1;
  Spinner spinner;

  ExpandBarTab( final CTabFolder topFolder ) {
    super( topFolder, "ExpandBar" );
    setDefaultStyle( SWT.BORDER | SWT.V_SCROLL );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "V_SCROLL", SWT.V_SCROLL, true );
    createStyleButton( "BORDER", SWT.BORDER, true );
    createVisibilityButton();
    createEnablementButton();
    spinner = createSpacingControl( parent );
    createFontChooser();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createInsertItemButton( parent );
    createRemoveItemButton( parent );
    createPropertyCheckbox( "Add Context Menu", PROP_CONTEXT_MENU );
    createPropertyCheckbox( "Add Expand Listener", PROP_EXPAND_LISTENER );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    ClassLoader classLoader = getClass().getClassLoader();
    int style = getStyle();
    expandBar1 = new ExpandBar( parent, style );
    if( hasCreateProperty( PROP_CONTEXT_MENU ) ) {
      Menu expandBarMenu = new Menu( expandBar1 );
      MenuItem expandBarMenuItem = new MenuItem( expandBarMenu, SWT.PUSH );
      expandBarMenuItem.addSelectionListener( new SelectionAdapter() {

        public void widgetSelected( final SelectionEvent event ) {
          String message = "You requested a context menu for the expand bar";
          MessageDialog.openInformation( expandBar1.getShell(),
                                         "Information",
                                         message );
        }
      } );
      expandBarMenuItem.setText( "Expand Bar context menu item" );
      expandBar1.setMenu( expandBarMenu );
    }
    if( hasCreateProperty( PROP_EXPAND_LISTENER ) ) {
      expandBar1.addExpandListener( new ExpandListener() {

        public void itemCollapsed( ExpandEvent e ) {
          int index = 0;
          int itemCount = expandBar1.getItemCount();
          for( int i = 0; i < itemCount; i++ ) {
            if( expandBar1.getItem( i ) == e.item ) {
              index = i;
            }
          }
          String message = "Expand item " + index + " collapsed!";
          MessageDialog.openInformation( expandBar1.getShell(),
                                         "Information",
                                         message );
        }

        public void itemExpanded( ExpandEvent e ) {
          int index = 0;
          int itemCount = expandBar1.getItemCount();
          for( int i = 0; i < itemCount; i++ ) {
            if( expandBar1.getItem( i ) == e.item ) {
              index = i;
            }
          }
          String message = "Expand item " + index + " expanded!";
          MessageDialog.openInformation( expandBar1.getShell(),
                                         "Information",
                                         message );
        }
      } );
    }
    Display display = expandBar1.getDisplay();
    Composite composite = new Composite( expandBar1, SWT.NONE );
    composite.setLayout( new GridLayout() );
    new Button( composite, SWT.PUSH ).setText( "SWT.PUSH" );
    new Button( composite, SWT.RADIO ).setText( "SWT.RADIO" );
    new Button( composite, SWT.CHECK ).setText( "SWT.CHECK" );
    new Button( composite, SWT.TOGGLE ).setText( "SWT.TOGGLE" );
    ExpandItem item = new ExpandItem( expandBar1, SWT.NONE, 0 );
    item.setText( "What is your favorite button?" );
    item.setHeight( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
    item.setControl( composite );
    item.setImage( Graphics.getImage( "resources/newfolder_wiz.gif",
                                      classLoader ) );
    composite = new Composite( expandBar1, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    Image img = display.getSystemImage( SWT.ICON_ERROR );
    new Label( composite, SWT.NONE ).setImage( img );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_ERROR" );
    img = display.getSystemImage( SWT.ICON_INFORMATION );
    new Label( composite, SWT.NONE ).setImage( img );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_INFORMATION" );
    img = display.getSystemImage( SWT.ICON_WARNING );
    new Label( composite, SWT.NONE ).setImage( img );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_WARNING" );
    img = display.getSystemImage( SWT.ICON_QUESTION );
    new Label( composite, SWT.NONE ).setImage( img );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_QUESTION" );
    item = new ExpandItem( expandBar1, SWT.NONE, 1 );
    item.setText( "What is your favorite icon?" );
    item.setHeight( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
    item.setControl( composite );
    img = Graphics.getImage( "resources/newprj_wiz.gif", classLoader );
    item.setImage( img );
    item.setExpanded( true );
    expandBar1.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    registerControl( expandBar1 );
    if( spinner != null ) {
      expandBar1.setSpacing( spinner.getSelection() );
    }
  }

  private Spinner createSpacingControl( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Spacing" );
    final Spinner spinner = new Spinner( composite, SWT.BORDER );
    spinner.setSelection( 4 );
    spinner.setMinimum( 0 );
    spinner.setMaximum( 20 );
    spinner.addModifyListener( new ModifyListener() {

      public void modifyText( ModifyEvent event ) {
        int spacing = spinner.getSelection();
        expandBar1.setSpacing( spacing );
      }
    } );
    return spinner;
  }

  private void createInsertItemButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Insert ExpandItem before first item" );
    button.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        ClassLoader classLoader = getClass().getClassLoader();
        ExpandItem item = new ExpandItem( expandBar1, SWT.NONE, 0 );
        item.setText( "ExpandItem text" );
        item.setImage( Graphics.getImage( "resources/newfile_wiz.gif",
                                          classLoader ) );
        item.setExpanded( false );
        createItemContent( item );
      }
    } );
  }

  private void createRemoveItemButton( final Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Remove first ExpandItem" );
    button.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        ExpandItem item = expandBar1.getItem( 0 );
        item.dispose();
      }
    } );
  }

  private void createItemContent( final ExpandItem item ) {
    if( item.getControl() == null ) {
      ExpandBar bar = item.getParent();
      Text content = new Text( bar, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY );
      String text = "This is the item's content";
      content.setText( text );
      item.setHeight( content.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
      item.setControl( content );
    }
  }
}
