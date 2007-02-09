/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ButtonTab extends ExampleTab {

  private static final String BUTTON_IMAGE_PATH
    = "resources/button-image.gif";

  private Image buttonImage;
  
  private boolean showImage;

  private Button button1;
  private Button check1;
  private Button radio1;
  private Button radio2;
  private Button radio3;

  public ButtonTab( final TabFolder folder ) {
    super( folder, "Button" );
  }

  void createStyleControls() {
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
        updateButtonImage( button1 );
      }
    } );
    createFontChooser();
  }

  void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout( RWT.VERTICAL ) );
    int style = getStyle();
    RowData data = new RowData( 100, 25 );
    button1 = new Button( top, style | RWT.PUSH );
    button1.setText( "Button" );
//    button1.setLayoutData( data );
    updateButtonImage( button1 );
    check1 = new Button( top, style | RWT.CHECK );
    check1.setText( "Check" );
//    check1.setLayoutData( data );
    radio1 = new Button( top, style | RWT.RADIO );
    radio1.setText( "Radio 1" );
//    radio1.setLayoutData( data );
    radio2 = new Button( top, style | RWT.RADIO );
    radio2.setText( "Radio 2" );
//    radio2.setLayoutData( data );
    radio3 = new Button( top, style | RWT.RADIO );
    radio3.setText( "Radio 3" );
//    radio3.setLayoutData( data );
    registerControl( button1 );
    registerControl( check1 );
    registerControl( radio1 );
    registerControl( radio2 );
    registerControl( radio3 );
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
