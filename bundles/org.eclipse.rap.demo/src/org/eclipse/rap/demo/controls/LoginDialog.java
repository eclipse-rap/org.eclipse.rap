/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


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
                      final String message )
  {
    super( parent );
    this.title = title;
    this.message = message;
  }

  public String getPassword() {
    return password;
  }

  public void setUsername( final String username ) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  protected void configureShell( final Shell shell ) {
    super.configureShell( shell );
    if( title != null ) {
      shell.setText( title );
    }
    // Workaround for RWT Text Size Determination
    shell.addControlListener( new ControlAdapter() {

      public void controlResized( ControlEvent e ) {
        initializeBounds();
      }
    } );
  }

  protected Control createDialogArea( final Composite parent ) {
    Composite composite = ( Composite )super.createDialogArea( parent );
    composite.setLayout( new GridLayout( 2, false ) );
    mesgLabel = new Label( composite, SWT.NONE );
    GridData messageData = new GridData( SWT.FILL, SWT.CENTER, true, false );
    messageData.horizontalSpan = 2;
    mesgLabel.setLayoutData( messageData );
    Label userLabel = new Label( composite, SWT.NONE );
    userLabel.setText( "Username:" );
    userText = new Text( composite, SWT.BORDER );
    userText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    Label passLabel = new Label( composite, SWT.NONE );
    passLabel.setText( "Password:" );
    passText = new Text( composite, SWT.BORDER | SWT.PASSWORD );
    passText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    initilizeDialogArea();
    return composite;
  }

  protected void createButtonsForButtonBar( final Composite parent ) {
    createButton( parent, IDialogConstants.CANCEL_ID, "Cancel", false );
    createButton( parent, LOGIN_ID, "Login", true );
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

  private void initilizeDialogArea() {
    if( message != null ) {
      mesgLabel.setText( message );
    }
    if( username != null ) {
      userText.setText( username );
    }
    userText.setFocus();
  }
}
