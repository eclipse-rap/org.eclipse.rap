/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.jface.dialogs.MessageDialog;
import org.eclipse.rap.jface.window.IWindowCallback;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

public class ButtonTab extends ExampleTab {

  private static final String BUTTON_IMAGE_PATH
    = "resources/button-image.gif";

  private Image buttonImage;
  
  private boolean showImage;

  private Button pushButton;
  private Button toggleButton;
  private Button checkButton;
  private Button radioButton1;
  private Button radioButton2;
  private Button radioButton3;
  private Button defaultButton;

  public ButtonTab( final TabFolder folder ) {
    super( folder, "Button" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "FLAT" );
    createStyleButton( "LEFT" );
    createStyleButton( "CENTER" );
    createStyleButton( "RIGHT" );
    createVisibilityButton();
    createEnablementButton();
    final Button imageButton = new Button( styleComp, RWT.CHECK );
    imageButton.setText( "Push Button with Image" );
    imageButton.setLayoutData( new RowData( 160, 20 ) );
    imageButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showImage = imageButton.getSelection();
        updateButtonImage( pushButton );
      }
    } );
    createFgColorButton();
//    createBgColorButton();
    createFontChooser();
    Button button = createPropertyButton( "Toggle Button", RWT.PUSH );
    button.setToolTipText( "Remote control the toggle button" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        toggleButton.setSelection( !toggleButton.getSelection() );
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout( RWT.VERTICAL ) );
    int style = getStyle();
    pushButton = new Button( parent, style | RWT.PUSH );
    pushButton.setText( "Push" );
    updateButtonImage( pushButton );
    toggleButton = new Button( parent, style | RWT.TOGGLE );
    toggleButton.setText( "Toggle" );
    checkButton = new Button( parent, style | RWT.CHECK );
    checkButton.setText( "Check" );
    radioButton1 = new Button( parent, style | RWT.RADIO );
    radioButton1.setText( "Radio 1" );
    radioButton2 = new Button( parent, style | RWT.RADIO );
    radioButton2.setText( "Radio 2" );
    radioButton3 = new Button( parent, style | RWT.RADIO );
    radioButton3.setText( "Radio 3" );
    registerControl( pushButton );
    registerControl( toggleButton );
    registerControl( checkButton );
    registerControl( radioButton1 );
    registerControl( radioButton2 );
    registerControl( radioButton3 );
    // default button
    final Group group = new Group( parent, RWT.NONE );
    group.setLayoutData( new RowData( 370, 60 ) );
    group.setText( "Default Button" );
    group.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( group, RWT.NONE );
    label.setText( "Enter some text and press Return" );
    final Text text = new Text( group, RWT.BORDER | RWT.SINGLE );
    defaultButton = new Button( group, style | RWT.PUSH );
    defaultButton.setText( "Default Button" );
    defaultButton.getShell().setDefaultButton( defaultButton );
    defaultButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        String message = "The text You entered: " + text.getText();
        IWindowCallback windowCallback = new IWindowCallback() {
          public void windowClosed( final int returnCode ) {
            // do nothing
          }
        };
        MessageDialog.openInformation( group.getShell(), 
                                       "Information", 
                                       message, 
                                       windowCallback );
      }
    } );
    
    // Set a context menu
    Menu menu = new Menu( parent );
    for( int i = 0; i < 5; i++ ) {
      MenuItem item = new MenuItem( menu, RWT.PUSH );
      item.setText( "Item " + ( i + 1 ) );
    }
    parent.setMenu( menu );
  }

  private void updateButtonImage( final Button button ) {
    if( showImage ) {
      if( buttonImage == null ) {
        ClassLoader classLoader = getClass().getClassLoader();
        buttonImage = Image.find( BUTTON_IMAGE_PATH, classLoader );
      }
      button.setImage( buttonImage );
    } else {
      button.setImage( null );
    }
  }
}
