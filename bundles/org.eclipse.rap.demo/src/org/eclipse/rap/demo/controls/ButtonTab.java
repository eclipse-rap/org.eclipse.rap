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
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

public class ButtonTab extends ExampleTab {

  private static final String BUTTON_IMAGE_PATH
    = "resources/button-image.gif";

  private Image buttonImage;
  
  private boolean showImage;

  private Button button;
  private Button defaultButton;
  private Button check;
  private Button radio1;
  private Button radio2;
  private Button radio3;

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
        updateButtonImage( button );
      }
    } );
    createFgColorButton();
//    createBgColorButton();
    createFontChooser();
  }

  protected void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout( RWT.VERTICAL ) );
    int style = getStyle();
    button = new Button( top, style | RWT.PUSH );
    button.setText( "Button" );
    updateButtonImage( button );
    check = new Button( top, style | RWT.CHECK );
    check.setText( "Check" );
    radio1 = new Button( top, style | RWT.RADIO );
    radio1.setText( "Radio 1" );
    radio2 = new Button( top, style | RWT.RADIO );
    radio2.setText( "Radio 2" );
    radio3 = new Button( top, style | RWT.RADIO );
    radio3.setText( "Radio 3" );
    registerControl( button );
    registerControl( check );
    registerControl( radio1 );
    registerControl( radio2 );
    registerControl( radio3 );
    final Group group = new Group( top, RWT.NONE );
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
