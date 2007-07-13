/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.window.IWindowCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

class DialogsTab extends ExampleTab {

  private Label inputDlgResLabel;
  private Label loginDlgResLabel;
  private Label messageDlgResLabel;
  private Label errorDlgResLabel;
  private Composite parent;

  public DialogsTab( final CTabFolder topFolder ) {
    super( topFolder, "Dialogs" );
  }

  protected void createStyleControls( final Composite parent ) {
  }

  protected void createExampleControls( final Composite parent ) {
    this.parent = parent;
    parent.setLayout( new GridLayout() );
    Group group1 = new Group( parent, SWT.NONE );
    group1.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    group1.setText( "JFace Dialogs" );
    group1.setLayout( new GridLayout( 3, true ) );
    
    // JFace input dialog
    Button showInputDlgButton = new Button( group1, SWT.PUSH );
    showInputDlgButton.setText( "Input Dialog" );
    showInputDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showInputDialog();
      }
    } );
    showInputDlgButton.setLayoutData( createGridDataFillBoth() );
    insertSpaceLabels( group1, 2 );
    
    inputDlgResLabel = new Label( group1, SWT.WRAP );
    inputDlgResLabel.setText( "Result:" );
    insertSpaceLabels( group1, 2 );
    
    Button showMessageInfoDlgButton = new Button( group1, SWT.PUSH );
    showMessageInfoDlgButton.setLayoutData( createGridDataFillBoth() );
    showMessageInfoDlgButton.setText( "Info Message" );
    showMessageInfoDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogInfo();
      }
    } );
    
    Button showMessageWarningDlgButton = new Button( group1, SWT.PUSH );
    showMessageWarningDlgButton.setLayoutData( createGridDataFillBoth() );
    showMessageWarningDlgButton.setText( "Warning Dialog" );
    showMessageWarningDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogWarning();
      }
    } );
    Button showMessageErrorDlgButton = new Button( group1, SWT.PUSH );
    showMessageErrorDlgButton.setLayoutData( createGridDataFillBoth() );
    showMessageErrorDlgButton.setText( "Error Message" );
    showMessageErrorDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogError();
      }
    } );
    
    Button showMessageQuestionDlgButton = new Button( group1, SWT.PUSH );
    showMessageQuestionDlgButton.setLayoutData( createGridDataFillBoth() );
    showMessageQuestionDlgButton.setText( "Question Dialog" );
    showMessageQuestionDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogQuestion();
      }
    } );
    Button showMessageConfirmDlgButton = new Button( group1, SWT.PUSH );
    showMessageConfirmDlgButton.setLayoutData( createGridDataFillBoth() );
    showMessageConfirmDlgButton.setText( "Confirm Message" );
    showMessageConfirmDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogConfirm();
      }
    } );
    insertSpaceLabels( group1, 1 );
    
    messageDlgResLabel = new Label( group1, SWT.WRAP );
    messageDlgResLabel.setText( "Result:" );
    insertSpaceLabels( group1, 2 );
    
    Button showErrorDlgButton = new Button( group1, SWT.PUSH );
    showErrorDlgButton.setLayoutData( createGridDataFillBoth() );
    showErrorDlgButton.setText( "Error Dialog" );
    showErrorDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showErrorDialog();
      }
    } );
    insertSpaceLabels( group1, 2 );

    errorDlgResLabel = new Label( group1, SWT.WRAP );
    errorDlgResLabel.setText( "Result:" );
    insertSpaceLabels( group1, 2 );
    
    
    Group group2 = new Group( parent, SWT.NONE );
    group2.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    group2.setText( "Custom Dialogs" );
    group2.setLayout( new GridLayout( 3, true ) );

    Button showLoginDlgButton = new Button( group2, SWT.PUSH );
    showLoginDlgButton.setText( "Login Dialog" );
    showLoginDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showLoginDialog();
      }
    } );
    showLoginDlgButton.setLayoutData( createGridDataFillBoth() );
    insertSpaceLabels( group2, 2 );
    
    loginDlgResLabel = new Label( group2, SWT.WRAP );
    loginDlgResLabel.setText( "Result:" );
  }

  private GridData createGridDataFillBoth() {
    return new GridData( GridData.FILL_BOTH );
  }

  private void insertSpaceLabels( final Group group, final int count ) {
    for( int i = 0; i < count; i++ ) {
      new Label( group, SWT.NONE );
    }
  }

  private void showInputDialog() {
    final IInputValidator val = new IInputValidator() {
      public String isValid( String newText ) {
        String result = null;
        if( newText.length() < 5 ) {
          result = "Input text too short!";
        }
        return result;
      }
    };
    String title = "Input Dialog";
    String mesg = "Enter at least five characters";
    String def = "default text";
    final InputDialog dlg;
    dlg = new InputDialog( getShell(), title, mesg, def, val );
    dlg.open( new IWindowCallback() {
      public void windowClosed( final int returnCode ) {
        if( returnCode == InputDialog.OK ) {
          inputDlgResLabel.setText( "Input Result: " + dlg.getValue() );
        } else {
          inputDlgResLabel.setText( "No Result" );
        }
        parent.layout();
      }
    } );
  }

  private void showMessageDialogInfo() {
    String title = "Information";
    String mesg = "Beer and pizza go well together.";
    IWindowCallback callback = new IWindowCallback() {
      public void windowClosed( final int returnCode ) {
        messageDlgResLabel.setText( "Info closed (" + returnCode + ")" );
        parent.layout();
      }
    };
    MessageDialog.openInformation( getShell(), title, mesg, callback );
  }

  private void showMessageDialogError() {
    String title = "Error";
    String mesg = "An everyday error occured.\n " + "Nothing to get worried.";
    MessageDialog.openError( getShell(), title, mesg, new IWindowCallback() {
      public void windowClosed( final int returnCode ) {
        messageDlgResLabel.setText( "Error closed (" + returnCode + ")" );
        parent.layout();
      }
    } );
  }

  private void showMessageDialogQuestion() {
    String title = "Question";
    String mesg = "Do you think you're smart?\n\n"
                  + "Your answer will not be recorded or evaluated "
                  + "nor does this question have any purpose apart from "
                  + "filling the empty space in this dialog window.";
    MessageDialog.openQuestion( getShell(), title, mesg, new IWindowCallback() {
      public void windowClosed( final int returnCode ) {
        messageDlgResLabel.setText( "Question closed (" + returnCode + ")" );
        parent.layout();
      }
    } );
  }

  private void showMessageDialogConfirm() {
    String title = "Confirmation";
    String mesg = "Nothing will be done. Ok?";
    MessageDialog.openConfirm( getShell(), title, mesg, new IWindowCallback() {
      public void windowClosed( final int returnCode ) {
        messageDlgResLabel.setText( "Confirm closed (" + returnCode + ")" );
        parent.layout();
      }
    } );
  }

  private void showMessageDialogWarning() {
    String title = "Warning";
    String mesg = "You have been warned.";
    MessageDialog.openWarning( getShell(), title, mesg, new IWindowCallback() {
      public void windowClosed( final int returnCode ) {
        messageDlgResLabel.setText( "Warning closed (" + returnCode + ")" );
        parent.layout();
      }
    } );
  }

  private void showErrorDialog() {
    String title = "Error";
    int code = 23;
    String mesg = "An absolutetly weird error occured";
    String reason = "Don't know, it just happened ...";
    Exception exception = new IndexOutOfBoundsException( "negative index: -1" );
    exception = new RuntimeException( exception );
    IStatus status = new Status( IStatus.ERROR,
                                 "org.eclipse.rap.demo",
                                 code,
                                 reason,
                                 exception );
    IWindowCallback callback = new IWindowCallback() {
      public void windowClosed( int returnCode ) {
        errorDlgResLabel.setText( "Error Dialog closed (" + returnCode + ")" );
        parent.layout();
      }
    };
    ErrorDialog.openError( getShell(), title, mesg, status, callback );
  }

  private void showLoginDialog() {
    String message = "Please sign in with your username and password:";
    final LoginDialog loginDialog = new LoginDialog( getShell(),
                                                     "Login",
                                                     message,
                                                     "john" );
    loginDialog.open( new IWindowCallback() {
      public void windowClosed( final int returnCode ) {
        String username = loginDialog.getUsername();
        String password = loginDialog.getPassword();
        String pwd = password == null ? "n/a" : password.length() + " chars.";
        loginDlgResLabel.setText(   "Login Dialog User: "
                                  + username
                                  + ", Password: "
                                  + pwd
                                  + " ("
                                  + returnCode
                                  + ")" );
        parent.layout();
      }
    } );
  }
}
