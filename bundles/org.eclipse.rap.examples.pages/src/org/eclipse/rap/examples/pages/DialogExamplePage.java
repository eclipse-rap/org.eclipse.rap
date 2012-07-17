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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rwt.widgets.DialogCallback;
import org.eclipse.rwt.widgets.DialogUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


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
    swtComp.setLayout( ExampleUtil.createGridLayout( 3, false, true, true ) );
    ExampleUtil.createHeading( swtComp, "SWT Dialogs", 3 );
    createSwtDialogButtons( swtComp );
  }

  private void createJfaceArea( Composite parent ) {
    Composite jFaceComp = new Composite( parent, SWT.NONE );
    jFaceComp.setLayoutData( ExampleUtil.createHorzFillData() );
    jFaceComp.setLayout( ExampleUtil.createGridLayout( 3, false, true, true ) );
    ExampleUtil.createHeading( jFaceComp, "JFace Dialogs", 3 );
    createJfaceDialogButtons( jFaceComp );
  }

  private void createCustomArea( Composite parent ) {
    Composite customComp = new Composite( parent, SWT.NONE );
    customComp.setLayoutData( ExampleUtil.createHorzFillData() );
    customComp.setLayout( ExampleUtil.createGridLayout( 3, false, true, true ) );
    ExampleUtil.createHeading( customComp, "Custom Dialogs", 3 );
    createCustomDialogs( customComp );
  }

  private void createResultsComposite( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 3, false, true, true ) );
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
    final InputDialog dialog = new InputDialog( getShell(), title, mesg, def, val ) {
      @Override
      public boolean close() {
        boolean result = super.close();
        int returnCode = getReturnCode();
        String resultText = "Result: " + getReturnCodeText( returnCode );
        if( returnCode == Window.OK ) {
          resultText += ", value: " + getValue();
        }
        showResult( resultText );
        return result;
      }
    };
    dialog.setBlockOnOpen( false );
    dialog.open();
  }

  private void showProgressDialog() {
    ProgressMonitorDialog dialog = new ProgressMonitorDialog( getShell() ) {
      @Override
      public boolean close() {
        return super.close();
      }
    };
    dialog.setBlockOnOpen( false );
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
      MessageDialogUtil.openError( getShell(), "Error", e.getMessage(), null );
    }
  }

  private void showMessageDialogInfo() {
    String title = "Information";
    String message = "This is a RAP MessageDialog.";
    DialogCallback callback = new DialogCallback() {
      public void dialogClosed( int returnCode ) {
        showResult( "Result: none" );
      }
    };
    MessageDialogUtil.openInformation( getShell(), title, message, callback );
  }

  private void showMessageDialogError() {
    String title = "Error";
    String message = "A weird error occured.\n " + "Please reboot.";
    DialogCallback callback = new DialogCallback() {
      public void dialogClosed( int returnCode ) {
        showResult( "Result: none" );
      }
    };
    MessageDialogUtil.openError( getShell(), title, message, callback );
  }

  private void showMessageDialogQuestion() {
    String title = "Question";
    String message = "Would you like to see the demo?\n\n"
                  + "You can have multiple lines of text here. "
                  + "Note that pressing <Return> here selects the default button.";
    DialogCallback callback = new DialogCallback() {
      public void dialogClosed( int returnCode ) {
        showResult( "Result: " + returnCode );
      }
    };
    MessageDialogUtil.openQuestion( getShell(), title, message, callback );
  }

  private void showMessageDialogConfirm() {
    String title = "Confirmation";
    String message = "Nothing will be done. Ok?";
    DialogCallback callback = new DialogCallback() {
      public void dialogClosed( int returnCode ) {
        showResult( "Result: " + returnCode );
      }
    };
    MessageDialogUtil.openConfirm( getShell(), title, message, callback );
  }

  private void showMessageDialogWarning() {
    String title = "Warning";
    String message = "You have been warned.";
    DialogCallback callback = new DialogCallback() {
      public void dialogClosed( int returnCode ) {
        showResult( "Result: " + returnCode );
      }
    };
    MessageDialogUtil.openWarning( getShell(), title, message, callback );
  }

  private void showErrorDialog() {
    MultiStatus status = createStatus();
    String title = "Error";
    String message = "An error occured while processing this command";
    int displayMask = IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR;
    ErrorDialog errorDialog = new ErrorDialog( getShell(), title, message, status, displayMask ) {
      @Override
      public boolean close() {
        boolean result = super.close();
        int returnCode = getReturnCode();
        showResult( "Result: " + getReturnCodeText( returnCode ) );
        return result;
      }
    };
    errorDialog.setBlockOnOpen( false );
    errorDialog.open();
  }

  private static MultiStatus createStatus() {
    String pluginId = "org.eclipse.rap.demo";
    int code = 23;
    String message = "Illegal array offset";
    MultiStatus multiStatus = new MultiStatus( pluginId, code, message, new RuntimeException() );
    Exception exception = new IndexOutOfBoundsException( "negative index: -1" );
    multiStatus.add( new Status( IStatus.ERROR, pluginId, code, message, exception ) );
    return multiStatus;
  }

  private void showLoginDialog() {
    String message = "Please sign in with your username and password:";
    final LoginDialog loginDialog = new LoginDialog( getShell(), "Login", message ) {
      @Override
      public boolean close() {
        boolean result = super.close();
        int returnCode = getReturnCode();
        String resultText = "Result: " + getReturnCodeText( returnCode );
        if( returnCode == Window.OK ) {
          String pwInfo = getPassword() == null ? "n/a" : getPassword().length() + " chars";
          resultText += ", user: " + getUsername() + ", password: " + pwInfo;
        }
        showResult( resultText );
        return result;
      }
    };
    loginDialog.setUsername( "john" );
    loginDialog.setBlockOnOpen( false );
    loginDialog.open();
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
    MessageBox messageBox = new MessageBox( getShell(), SWT.YES | SWT.NO );
    messageBox.setText( title );
    messageBox.setMessage( mesg );
    DialogUtil.open( messageBox, new DialogCallback() {
      public void dialogClosed( int returnCode ) {
        String strResult = "";
        switch( returnCode ) {
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
            strResult = "" + returnCode;
            break;
        }
        showResult( "Result: " + strResult );
      }
    } );
  }

  private void showColorDialog() {
    final ColorDialog dialog = new ColorDialog( getShell() );
    DialogUtil.open( dialog, new DialogCallback() {
      public void dialogClosed( int returnCode ) {
        RGB result = dialog.getRGB();
        showResult( "Result: " + result );
      }
    } );
  }

  protected void showFontDialog() {
    final FontDialog dialog = new FontDialog( getShell(), SWT.SHELL_TRIM );
    DialogUtil.open( dialog, new DialogCallback() {
      public void dialogClosed( int returnCode ) {
        FontData[] fontList = dialog.getFontList();
        FontData fontData = null;
        if( fontList != null ) {
          fontData = fontList[ 0 ];
        }
        showResult( "Result: " + fontData + " / " + dialog.getRGB() );
      }
    } );
  }

  private void showResult( String resultText ) {
    resultsLabel.setText( resultText );
    resultsLabel.getParent().pack();
  }

  private static GridData createButtonGridData() {
    return new GridData( 200, 28 );
  }

}
