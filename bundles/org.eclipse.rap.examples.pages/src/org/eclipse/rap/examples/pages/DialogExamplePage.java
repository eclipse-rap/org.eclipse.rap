/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.examples.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

public class DialogExamplePage implements IExamplePage {

  private Label resultsLabel;

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    createSwtArea( parent );
    createJfaceArea( parent );
    createCustomArea( parent );
    createResultsComposite( parent );
  }

  private void createSwtArea( Composite parent ) {
    Composite swtComp = new Composite( parent, SWT.NONE );
    swtComp.setLayoutData( ExampleUtil.createHorzFillData() );
    swtComp.setLayout( ExampleUtil.createGridLayout( 3, false, 20, 0 ) );
    ExampleUtil.createHeadingLabel( swtComp, "SWT Dialogs", 3 );
    createSwtDialogButtons( swtComp );
  }

  private void createJfaceArea( Composite parent ) {
    Composite jFaceComp = new Composite( parent, SWT.NONE );
    jFaceComp.setLayoutData( ExampleUtil.createHorzFillData() );
    jFaceComp.setLayout( ExampleUtil.createGridLayout( 3, false, 20, 0 ) );
    ExampleUtil.createHeadingLabel( jFaceComp, "JFace Dialogs", 3 );
    createJfaceDialogButtons( jFaceComp );
  }

  private void createCustomArea( Composite parent ) {
    Composite customComp = new Composite( parent, SWT.NONE );
    customComp.setLayoutData( ExampleUtil.createHorzFillData() );
    customComp.setLayout( ExampleUtil.createGridLayout( 3, false, 20, 0 ) );
    ExampleUtil.createHeadingLabel( customComp, "Custom Dialogs", 3 );
    createCustomDialogs( customComp );
  }

  private void createResultsComposite( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout() );
    composite.setLayoutData( ExampleUtil.createHorzFillData() );
    resultsLabel = new Label( composite, SWT.WRAP );
    resultsLabel.setLayoutData( ExampleUtil.createFillData() );
  }

  //////
  // SWT

  private void createSwtDialogButtons( Composite swtComp ) {
    createMessageDialogButton( swtComp );
    createColorDialogButton( swtComp );
    createFontDialogButton( swtComp );
  }

  private void createMessageDialogButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "MessageBox" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showMessageBox();
      }
    } );
    button.setLayoutData( createButtonGridData() );
  }

  private void createColorDialogButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "ColorDialog" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        showColorDialog();
      }
    });
    button.setLayoutData( createButtonGridData() );
  }

  private void createFontDialogButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "FontDialog" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        showFontDialog();
      }
    });
    button.setLayoutData( createButtonGridData() );
  }

  ////////
  // JFace

  private void createJfaceDialogButtons( Composite parent ) {
    createInputDialogButton( parent );
    createProgressDialogButton( parent );
    createErrorDialogButton( parent );
    createInfoMessageDialogButton( parent );
    createWarningMessageDialogButton( parent );
    createErrorMessageDialogButton( parent );
    createQuestionMessageDialogButton( parent );
    createConfirmMessageDialogButton( parent );
  }

  private void createInputDialogButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Input Dialog" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showInputDialog();
      }
    } );
    button.setLayoutData( createButtonGridData() );
  }

  private void createProgressDialogButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "ProgressDialog" );
    button.setLayoutData( createButtonGridData() );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        showProgressDialog();
      }
    } );
  }

  private void createErrorDialogButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setLayoutData( createButtonGridData() );
    button.setText( "Error Dialog" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showErrorDialog();
      }
    } );
  }

  private void createInfoMessageDialogButton( Composite parent ) {
    Button showMessageInfoDlgButton = new Button( parent, SWT.PUSH );
    showMessageInfoDlgButton.setLayoutData( createButtonGridData() );
    showMessageInfoDlgButton.setText( "Info Message" );
    showMessageInfoDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showMessageDialogInfo();
      }
    } );
  }

  private void createWarningMessageDialogButton( Composite parent ) {
    Button showMessageWarningDlgButton = new Button( parent, SWT.PUSH );
    showMessageWarningDlgButton.setLayoutData( createButtonGridData() );
    showMessageWarningDlgButton.setText( "Warning Dialog" );
    showMessageWarningDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showMessageDialogWarning();
      }
    } );
  }

  private void createErrorMessageDialogButton( Composite parent ) {
    Button showMessageErrorDlgButton = new Button( parent, SWT.PUSH );
    showMessageErrorDlgButton.setLayoutData( createButtonGridData() );
    showMessageErrorDlgButton.setText( "Error Message" );
    showMessageErrorDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showMessageDialogError();
      }
    } );
  }

  private void createQuestionMessageDialogButton( Composite parent ) {
    Button showMessageQuestionDlgButton = new Button( parent, SWT.PUSH );
    showMessageQuestionDlgButton.setLayoutData( createButtonGridData() );
    showMessageQuestionDlgButton.setText( "Question Dialog" );
    showMessageQuestionDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showMessageDialogQuestion();
      }
    } );
  }

  private void createConfirmMessageDialogButton( Composite parent ) {
    Button showMessageConfirmDlgButton = new Button( parent, SWT.PUSH );
    showMessageConfirmDlgButton.setLayoutData( createButtonGridData() );
    showMessageConfirmDlgButton.setText( "Confirm Message" );
    showMessageConfirmDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showMessageDialogConfirm();
      }
    } );
  }

  /////////
  // Custom

  private void createCustomDialogs( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Login Dialog" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showLoginDialog();
      }
    } );
    button.setLayoutData( createButtonGridData() );
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
    int returnCode = dlg.open();
    String resultText = "Result: " + getReturnCodeText( returnCode );
    if( returnCode == Window.OK ) {
      resultText += ", value: " + dlg.getValue();
    }
    showResult( resultText );
  }

  private void showProgressDialog() {
    ProgressMonitorDialog dialog = new ProgressMonitorDialog( getShell() );
    try {
      dialog.run( true, true, new IRunnableWithProgress() {
        public void run( IProgressMonitor monitor )
          throws InvocationTargetException, InterruptedException
        {
          monitor.beginTask( "Counting to 12...", 12 );
          for( int i = 1; !monitor.isCanceled() && i <= 12; i++ ) {
            monitor.worked( 1 );
            Thread.sleep( 300 );
          }
          monitor.done();
        }
      } );
    } catch( Exception e ) {
      MessageDialog.openError( getShell(), "Error", e.getMessage() );
    }
  }

  private void showMessageDialogInfo() {
    String title = "Information";
    String mesg = "This is a RAP MessageDialog.";
    MessageDialog.openInformation( getShell(), title, mesg );
    showResult( "Result: none" );
  }

  private void showMessageDialogError() {
    String title = "Error";
    String mesg = "A weird error occured.\n " + "Please reboot.";
    MessageDialog.openError( getShell(), title, mesg );
    showResult( "Result: none" );
  }

  private void showMessageDialogQuestion() {
    String title = "Question";
    String mesg = "Would you like to see the demo?\n\n"
                  + "You can have multiple lines of text here. "
                  + "Note that pressing <Return> here selects the default button.";
    boolean result = MessageDialog.openQuestion( getShell(), title, mesg );
    showResult( "Result: " + result );
  }

  private void showMessageDialogConfirm() {
    String title = "Confirmation";
    String mesg = "Nothing will be done. Ok?";
    boolean result = MessageDialog.openConfirm( getShell(), title, mesg );
    showResult( "Result: " + result );
  }

  private void showMessageDialogWarning() {
    String title = "Warning";
    String mesg = "You have been warned.";
    MessageDialog.openWarning( getShell(), title, mesg );
    showResult( "Result: none" );
  }

  private void showErrorDialog() {
    String title = "Error";
    int code = 23;
    String mesg = "Weird weird error occured";
    String reason = "Illegal array offset";
    Exception exception = new IndexOutOfBoundsException( "negative index: -1" );
    exception = new RuntimeException( exception );
    String pluginId = "org.eclipse.rap.demo";
    IStatus status1 = new Status( IStatus.ERROR, pluginId, code, reason, exception );
    String mesg2 = "Illegal array offset";
    MultiStatus multiStatus = new MultiStatus( pluginId, code, mesg2, new RuntimeException() );
    multiStatus.add( status1 );
    int returnCode = ErrorDialog.openError( getShell(), title, mesg, multiStatus );
    showResult( "Result: " + getReturnCodeText( returnCode ) );
  }

  private void showLoginDialog() {
    String message = "Please sign in with your username and password:";
    final LoginDialog loginDialog
      = new LoginDialog( getShell(), "Login", message );
    loginDialog.setUsername( "john" );
    int returnCode = loginDialog.open();
    String resultText = "Result: " + getReturnCodeText( returnCode );
    if( returnCode == Window.OK ) {
      String username = loginDialog.getUsername();
      String password = loginDialog.getPassword();
      String pwInfo = password == null ? "n/a" : password.length() + " chars";
      resultText += ", user: " + username + ", password: " + pwInfo;
    }
    showResult( resultText );
  }

  private Shell getShell() {
    return Display.getCurrent().getActiveShell();
  }

  private String getReturnCodeText( int code ) {
    String result;
    if( code == Window.OK ) {
      result = "OK";
    } else if( code == Window.CANCEL ) {
        result = "CANCEL";
    } else {
      result = String.valueOf( code );
    }
    return result ;
  }

  private void showMessageBox() {
    String title = "MessageBox Title";
    String mesg = "Lorem ipsum dolor sit amet consectetuer adipiscing elit.";
    MessageBox mb = new MessageBox( getShell(), SWT.YES | SWT.NO );
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
    showResult( "Result: " + strResult );
  }

  private void showColorDialog() {
    ColorDialog dialog = new ColorDialog( getShell() );
    RGB result = dialog.open();
    showResult( "Result: " + result );
  }

  protected void showFontDialog() {
    FontDialog dialog = new FontDialog( getShell(), SWT.SHELL_TRIM );
    FontData result = dialog.open();
    showResult( "Result: " + result + " / " + dialog.getRGB() );
  }

  private void showResult( String resultText ) {
    resultsLabel.setText( resultText );
    resultsLabel.getParent().pack();
  }

  private static GridData createButtonGridData() {
    return new GridData( 200, 28 );
  }

}
