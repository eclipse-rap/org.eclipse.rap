/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class LoginDialog extends Dialog {

  private static final int LOGIN_ID = IDialogConstants.CLIENT_ID + 1;
  
  private Text userText;
  private Text passText;
  private Label mesgLabel;
  private String title;
  private String message;
  private String username;
  private String password;

  public LoginDialog( final Shell parent,
                       final String title,
                       final String message,
                       final String defaultUsername ) 
  {
    super( parent );
    this.title = title;
    this.message = message;
    this.username = defaultUsername;
  }
  
  protected void createButtonsForButtonBar( final Composite parent ) {
    createButton( parent, LOGIN_ID, "Login", true );
  }
  
  protected Control createDialogArea( final Composite parent ) {
    // create composite
    Composite composite = (Composite) super.createDialogArea( parent );
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    composite.setLayout( gridLayout );
    
    // message label
    mesgLabel = new Label( composite, SWT.NONE );
    GridData data = new GridData();
    data.horizontalAlignment = GridData.CENTER;
    data.verticalAlignment = GridData.CENTER;
    data.horizontalSpan = 2;
    data.widthHint = 280;
    data.heightHint = 25;
    mesgLabel.setLayoutData( data );
    if( message != null ) {
      mesgLabel.setText( message );
    }

    // user label and input field
    Label userLabel = new Label( composite, SWT.NONE );
    userLabel.setText( "Username:" );
    data = new GridData();
    data.widthHint = 60;
    data.heightHint = 20;
    data.verticalAlignment = GridData.CENTER;
    userLabel.setLayoutData( data );
    userText = new Text( composite, SWT.BORDER );
    data = new GridData( GridData.FILL_HORIZONTAL );
    data.heightHint = 20;
    userText.setLayoutData( data );
    if( username != null ) {
      userText.setText( username );
    }
    userText.setFocus();
    
    // password label and input field
    Label passLabel = new Label( composite, SWT.NONE );
    passLabel.setText( "Password:" );
    data = new GridData();
    data.widthHint = 60;
    data.heightHint = 20;
    data.verticalAlignment = GridData.CENTER;
    passText = new Text( composite, SWT.BORDER | SWT.PASSWORD );
    passLabel.setLayoutData( data );
    data = new GridData( GridData.FILL_HORIZONTAL );
    data.heightHint = 20;
    passText.setLayoutData( data );
    return composite;
  }

  protected void configureShell( final Shell shell ) {
    super.configureShell( shell );
    if ( title != null ) {
      shell.setText( title );
    }
  }

  protected void buttonPressed( final int buttonId ) {
    if( buttonId == LOGIN_ID ) {
      username = userText.getText();
      password = passText.getText();
      setReturnCode( OK );
      close();
    } else {
      password = null;
    }
    super.buttonPressed( buttonId );
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }
}
