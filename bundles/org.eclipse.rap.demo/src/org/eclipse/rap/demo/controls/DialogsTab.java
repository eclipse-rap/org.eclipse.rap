/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class DialogsTab extends ExampleTab {

  private Label inputDlgResLabel;
  private Label loginDlgResLabel;
  private Label messageDlgResLabel;
  private Label errorDlgResLabel;
  private Label messageBoxDlgResLabel;

  private Button okButton, cancelButton;
  private Button yesButton, noButton;
  private Button retryButton;
  private Button abortButton, ignoreButton;
  private Button iconErrorButton, iconInformationButton, iconQuestionButton;
  private Button iconWarningButton, iconWorkingButton, noIconButton;

  private Button showMessageBoxDlgButton;
  private Button showColorDlgButton;

  public DialogsTab( final CTabFolder topFolder ) {
    super( topFolder, "Dialogs" );
  }

  protected void createStyleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, true ) );

    createMessageBoxStyleControls( parent );
  }

  protected void createExampleControls( final Composite parent ) {
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
    GridData gdInputDlgResLabel = new GridData();
    gdInputDlgResLabel.horizontalSpan = 3;
    inputDlgResLabel.setLayoutData( gdInputDlgResLabel );

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

    Group group3 = new Group( parent, SWT.NONE );
    group3.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    group3.setText( "SWT Dialogs" );
    group3.setLayout( new GridLayout( 3, true ) );

    showMessageBoxDlgButton = new Button( group3, SWT.PUSH );
    showMessageBoxDlgButton.setText( "MessageBox Dialog" );
    showMessageBoxDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageBoxDialog();
      }
    } );
    showMessageBoxDlgButton.setLayoutData( createGridDataFillBoth() );
    
    showColorDlgButton = new Button( group3, SWT.PUSH );
    showColorDlgButton.setText( "ColorDialog" );
    showColorDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        showColorDialog();
      }
    });
    showColorDlgButton.setLayoutData( createGridDataFillBoth() );
    insertSpaceLabels( group3, 1 );

    messageBoxDlgResLabel = new Label( group3, SWT.WRAP );
    messageBoxDlgResLabel.setText( "Result:" );
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
      public String isValid( final String newText ) {
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
    int returnCode = dlg.open();
    String resultText = "Result: " + getReturnCodeText( returnCode );
    if( returnCode == InputDialog.OK ) {
      resultText += ", value: " + dlg.getValue();
    }
    inputDlgResLabel.setText( resultText  );
    inputDlgResLabel.pack();
  }

  private void showMessageDialogInfo() {
    String title = "Information";
    String mesg = "RAP rocks!";
    MessageDialog.openInformation( getShell(), title, mesg );
    messageDlgResLabel.setText( "Result: none" );
    messageDlgResLabel.pack();
  }

  private void showMessageDialogError() {
    String title = "Error";
    String mesg = "An everyday error occured.\n " + "Nothing to get worried.";
    MessageDialog.openError( getShell(), title, mesg );
    messageDlgResLabel.setText( "Result: none" );
    messageDlgResLabel.pack();
  }

  private void showMessageDialogQuestion() {
    String title = "Question";
    String mesg = "Do you like the RAP technology?\n\n"
                  + "Note that you can also press <Return> here. "
                  + "The correct answer is automatically selected.";
    boolean result = MessageDialog.openQuestion( getShell(), title, mesg );
    messageDlgResLabel.setText( "Result: " + result );
    messageDlgResLabel.pack();
  }

  private void showMessageDialogConfirm() {
    String title = "Confirmation";
    String mesg = "Nothing will be done. Ok?";
    boolean result = MessageDialog.openConfirm( getShell(), title, mesg );
    messageDlgResLabel.setText( "Result: " + result );
    messageDlgResLabel.pack();
  }

  private void showMessageDialogWarning() {
    String title = "Warning";
    String mesg = "You have been warned.";
    MessageDialog.openWarning( getShell(), title, mesg );
    messageDlgResLabel.setText( "Result: none" );
    messageDlgResLabel.pack();
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
    int returnCode = ErrorDialog.openError( getShell(), title, mesg, status );
    errorDlgResLabel.setText( "Result: " + getReturnCodeText( returnCode ) );
    errorDlgResLabel.pack();
  }

  private void showLoginDialog() {
    String message = "Please sign in with your username and password:";
    final LoginDialog loginDialog
      = new LoginDialog( getShell(), "Login", message, "john" );
    int returnCode = loginDialog.open();
    String resultText = "Result: " + getReturnCodeText( returnCode );
    if( returnCode == Dialog.OK ) {
      String username = loginDialog.getUsername();
      String password = loginDialog.getPassword();
      String pwInfo = password == null ? "n/a" : password.length() + " chars";
      resultText += ", user: " + username + ", password: " + pwInfo;
    }
    loginDlgResLabel.setText( resultText );
    loginDlgResLabel.pack();
  }

  private String getReturnCodeText( final int code ) {
    String result;
    if( code == Dialog.OK ) {
      result = "OK";
    } else if( code == Dialog.CANCEL ) {
        result = "CANCEL";
    } else {
      result = String.valueOf( code );
    }
    return result ;
  }

  private void showMessageBoxDialog() {
    int style = getStyle();
    if( okButton.getEnabled() && okButton.getSelection() ) {
      style |= SWT.OK;
    }
    if( cancelButton.getEnabled() && cancelButton.getSelection() ) {
      style |= SWT.CANCEL;
    }
    if( yesButton.getEnabled() && yesButton.getSelection() ) {
      style |= SWT.YES;
    }
    if( noButton.getEnabled() && noButton.getSelection() ) {
      style |= SWT.NO;
    }
    if( retryButton.getEnabled() && retryButton.getSelection() ) {
      style |= SWT.RETRY;
    }
    if( abortButton.getEnabled() && abortButton.getSelection() ) {
      style |= SWT.ABORT;
    }
    if( ignoreButton.getEnabled() && ignoreButton.getSelection() ) {
      style |= SWT.IGNORE;
    }
    if( iconErrorButton.getEnabled() && iconErrorButton.getSelection() ) {
      style |= SWT.ICON_ERROR;
    }
    if( iconInformationButton.getEnabled()
        && iconInformationButton.getSelection() ) {
      style |= SWT.ICON_INFORMATION;
    }
    if( iconQuestionButton.getEnabled() && iconQuestionButton.getSelection() ) {
      style |= SWT.ICON_QUESTION;
    }
    if( iconWarningButton.getEnabled() && iconWarningButton.getSelection() ) {
      style |= SWT.ICON_WARNING;
    }
    if( iconWorkingButton.getEnabled() && iconWorkingButton.getSelection() ) {
      style |= SWT.ICON_WORKING;
    }

    String title = "MessageBox Title";
    String mesg = "Lorem ipsum dolor sit amet consectetuer adipiscing elit.";
    MessageBox mb = new MessageBox( getShell(), style );
    mb.setText( title );
    mb.setMessage( mesg );
    int result = mb.open();
    String strResult = "";
    switch( result ) {
      case SWT.OK:
        strResult = "SWT.OK";
      break;
      case SWT.YES:
        strResult = "SWT.YES";
      break;
      case SWT.NO:
        strResult = "SWT.NO";
      break;
      case SWT.CANCEL:
        strResult = "SWT.CANCEL";
      break;
      case SWT.ABORT:
        strResult = "SWT.ABORT";
      break;
      case SWT.RETRY:
        strResult = "SWT.RETRY";
      break;
      case SWT.IGNORE:
        strResult = "SWT.IGNORE";
      break;
      default:
        strResult = "" + result;
      break;
    }
    messageBoxDlgResLabel.setText( "Result: " + strResult );
    messageBoxDlgResLabel.pack();
  }
  
  private void showColorDialog() {
    ColorDialog dialog = new ColorDialog( getShell() );
    RGB result = dialog.open();
    messageBoxDlgResLabel.setText( "Result: " + result );
    messageBoxDlgResLabel.pack();
  }

  private void createMessageBoxStyleControls( final Composite parent ) {
    Group buttonStyleGroup = new Group( parent, SWT.NONE );
    buttonStyleGroup.setLayout( new GridLayout() );
    buttonStyleGroup.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_FILL
                                                  | GridData.VERTICAL_ALIGN_FILL ) );
    buttonStyleGroup.setText( "SWT MessageBox Styles" );

    okButton = new Button( buttonStyleGroup, SWT.CHECK );
    okButton.setText( "SWT.OK" );
    cancelButton = new Button( buttonStyleGroup, SWT.CHECK );
    cancelButton.setText( "SWT.CANCEL" );
    yesButton = new Button( buttonStyleGroup, SWT.CHECK );
    yesButton.setText( "SWT.YES" );
    noButton = new Button( buttonStyleGroup, SWT.CHECK );
    noButton.setText( "SWT.NO" );
    retryButton = new Button( buttonStyleGroup, SWT.CHECK );
    retryButton.setText( "SWT.RETRY" );
    abortButton = new Button( buttonStyleGroup, SWT.CHECK );
    abortButton.setText( "SWT.ABORT" );
    ignoreButton = new Button( buttonStyleGroup, SWT.CHECK );
    ignoreButton.setText( "SWT.IGNORE" );

    Group iconStyleGroup = new Group( parent, SWT.NONE );
    iconStyleGroup.setLayout( new GridLayout() );
    iconStyleGroup.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_FILL
                                                | GridData.VERTICAL_ALIGN_FILL ) );
    iconStyleGroup.setText( "SWT MessageBox Icon Styles" );

    iconErrorButton = new Button( iconStyleGroup, SWT.RADIO );
    iconErrorButton.setText( "SWT.ICON_ERROR" );
    iconInformationButton = new Button( iconStyleGroup, SWT.RADIO );
    iconInformationButton.setText( "SWT.ICON_INFORMATION" );
    iconQuestionButton = new Button( iconStyleGroup, SWT.RADIO );
    iconQuestionButton.setText( "SWT.ICON_QUESTION" );
    iconWarningButton = new Button( iconStyleGroup, SWT.RADIO );
    iconWarningButton.setText( "SWT.ICON_WARNING" );
    iconWorkingButton = new Button( iconStyleGroup, SWT.RADIO );
    iconWorkingButton.setText( "SWT.ICON_WORKING" );
    noIconButton = new Button( iconStyleGroup, SWT.RADIO );
    noIconButton.setText( "No Icon" );

    noIconButton.setSelection( true );
  }
}
