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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

public class LoginDialog {

  private static final int STYLE = RWT.DIALOG_TRIM
                                 | RWT.APPLICATION_MODAL
                                 | RWT.RESIZE;
  private final Shell shell;
  private Text userText;
  private Text passText;
  private Label mesgLabel;
  private Button loginButton;

  public LoginDialog( final Shell parent ) {
    shell = new Shell( parent, STYLE );
    create();
  }

  public LoginDialog( final Display display ) {
    shell = new Shell( display, STYLE );
    create();
  }

  private void create() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    shell.setBounds( 100, 100, 300, 200 );
    shell.setLayout( gridLayout );
    // message label
    mesgLabel = new Label( shell, RWT.NONE );
    GridData data = new GridData();
    data.horizontalAlignment = GridData.CENTER;
    data.verticalAlignment = GridData.CENTER;
    data.horizontalSpan = 2;
    data.widthHint = 280;
    data.heightHint = 25;
    mesgLabel.setLayoutData( data );
    // user label and input field
    Label userLabel = new Label( shell, RWT.NONE );
    userLabel.setText( "Username:" );
    data = new GridData();
    data.widthHint = 60;
    data.heightHint = 20;
    data.verticalAlignment = GridData.CENTER;
    userLabel.setLayoutData( data );
    userText = new Text( shell, RWT.BORDER );
    data = new GridData( GridData.FILL_HORIZONTAL );
    data.heightHint = 20;
    userText.setLayoutData( data );
    // password label and input field
    Label passLabel = new Label( shell, RWT.NONE );
    passLabel.setText( "Password:" );
    data = new GridData();
    data.widthHint = 60;
    data.heightHint = 20;
    data.verticalAlignment = GridData.CENTER;
    passText = new Text( shell, RWT.BORDER | RWT.PASSWORD );
    passLabel.setLayoutData( data );
    data = new GridData( GridData.FILL_HORIZONTAL );
    data.heightHint = 20;
    passText.setLayoutData( data );
    loginButton = new Button( shell, RWT.PUSH );
    loginButton.setText( "Login" );
//    TODO
//    shell.setDefaultButton( loginButton );
    data = new GridData();
    data.horizontalAlignment = GridData.END;
    data.verticalAlignment = GridData.END;
    data.horizontalSpan = 2;
    data.widthHint = 100;
    data.heightHint = 25;
    loginButton.setLayoutData( data );
  }
  
  public void setCallback( final Runnable callback ) {
    loginButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        callback.run();
        shell.close();
      }
    } );
  }

  public void open() {
    shell.layout();
    shell.open();
  }

  public void close() {
    shell.close();
  }
  
  public String getPassword() {
    String result = passText.getText();
    return "null".equals(result)? "" : result.trim();
  }

  public void setUsername( final String username ) {
    userText.setText( username );
  }

  public String getUsername() {
    String result = userText.getText();
    return "null".equals(result)? "" : result.trim();
  }
  
  public void setMessage( final String message ) {
    mesgLabel.setText( message );
  }

  public void setTitle( final String title ) {
    shell.setText( title );
  }
}
