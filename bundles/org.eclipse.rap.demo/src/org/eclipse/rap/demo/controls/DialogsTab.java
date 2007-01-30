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
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
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
    parent.setLayout( new RowLayout() );
    Button loginButton = new Button( parent, RWT.PUSH );
    loginButton.setText( "Login Dialog" );
    loginButton.setLayoutData( new RowData( 100, 25 ) );
    loginButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        showLoginDialog();
      }
    } );
    registerControl( loginButton );
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
}
