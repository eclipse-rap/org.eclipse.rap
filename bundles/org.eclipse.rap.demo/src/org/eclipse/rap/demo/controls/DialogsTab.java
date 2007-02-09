/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.jface.dialogs.IInputValidator;
import org.eclipse.rap.jface.dialogs.InputDialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.widgets.*;

class DialogsTab extends ExampleTab {

  private String username = "";
  private Shell shell;

  public DialogsTab( final TabFolder folder ) {
    super( folder, "Dialogs" );
  }

  void createStyleControls() {
  }

  void createExampleControls( final Composite parent ) {
    shell = parent.getShell();
    Button loginButton = new Button( parent, RWT.PUSH );
    loginButton.setText( "Login Dialog" );
    loginButton.setBounds( 10, 10, 80, 20 );
    loginButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        showLoginDialog();
      }
    } );
    registerControl( loginButton );

    // JFace input dialog
    final Label label = new Label( parent, RWT.NONE );
    label.setText( "User input from InputDialog: " );
    label.setBounds( 10, 40, 100, 20 );
    Button showInputDlgButton = new Button( parent, RWT.PUSH );
    showInputDlgButton.setText( "Input Dialog" );
    showInputDlgButton.setBounds( 10, 70, 80, 20 );
    showInputDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        showInputDialog();
      }
    });
  }

  private void showLoginDialog() {
    final LoginDialog loginDialog = new LoginDialog( shell );
    loginDialog.setCallback( new Runnable() {
      public void run() {
        username = loginDialog.getUsername();
        String password = loginDialog.getPassword();
        log( "Username: " + username );
        log( "Password: " + password );
        System.out.println( "Username: " + username );
        System.out.println( "Password: " + password );
      }
    } );
    loginDialog.setMessage( "Please sign in with your username and password:" );
    loginDialog.setUsername( username );
    loginDialog.setTitle( "Login" );
    loginDialog.open();
  }

  private void showInputDialog() {
    final IInputValidator val = new IInputValidator() {
      public String isValid( String newText ) {
        String result = null;
        if ( newText.length() < 5 ) {
          result = "Input text too short!";
        }
        return result;
      }
    };
    String title = "Input Dialog";
    String mesg = "Enter at least five characters";
    String def = "default text";
    InputDialog dlg = new InputDialog( getShell(), title, mesg, def, val );
    // if (dlg.open() == Window.OK) {
    //   // User clicked OK; update the label with the input
    //   label.setText(dlg.getValue());
    // }
    // dlg.setCallback( new Runnable() {
    // public void run() {
    // label.setText(dlg.getValue());
    dlg.open();
  }

}
