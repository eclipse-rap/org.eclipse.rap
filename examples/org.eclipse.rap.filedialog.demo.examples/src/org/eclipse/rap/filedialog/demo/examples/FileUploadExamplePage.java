/*******************************************************************************
 * Copyright (c) 2011, 2019 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.filedialog.demo.examples;

import java.io.File;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.fileupload.DiskFileUploadReceiver;
import org.eclipse.rap.fileupload.FileDetails;
import org.eclipse.rap.fileupload.FileUploadEvent;
import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.FileUploadListener;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.widgets.DialogCallback;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class FileUploadExamplePage implements IExamplePage {

  private static final String INITIAL_TEXT = "no files uploaded.";
  private static final String NO_FILES_SELECTED = "no file selected";
  private static final String UPLOAD = "Upload";
  private static final String ABORT = "Abort";
  private FileUpload fileUpload;
  private Label fileNameLabel;
  private Button uploadButton;
  private Text logText;
  private ServerPushSession pushSession;

  @Override
  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 3 ) );
    Control controlsColumn = createControlsColumn( parent );
    controlsColumn.setLayoutData( ExampleUtil.createFillData() );
    Control serverColumn = createLogColumn( parent );
    serverColumn.setLayoutData( ExampleUtil.createFillData() );
    Control infoColumn = createInfoColumn( parent );
    infoColumn.setLayoutData( ExampleUtil.createFillData() );
  }

  private Control createControlsColumn( Composite parent ) {
    Composite column = new Composite( parent, SWT.NONE );
    column.setLayout( ExampleUtil.createGridLayoutWithoutMargin( 1, false ) );
    Control fileUploadArea = createFileUploadArea( column );
    fileUploadArea.setLayoutData( ExampleUtil.createHorzFillData() );
    Control fileDialogArea = createFileDialogArea( column );
    fileDialogArea.setLayoutData( ExampleUtil.createHorzFillData() );
    return column;
  }

  private Control createLogColumn( Composite parent ) {
    Composite column = new Composite( parent, SWT.NONE );
    column.setLayout( ExampleUtil.createGridLayout( 1, false, true, true ) );
    ExampleUtil.createHeading( column, "Server log", 2 );
    logText = new Text( column, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.BORDER );
    logText.setText( INITIAL_TEXT );
    logText.setLayoutData( ExampleUtil.createFillData() );
    createClearButton( column );
    return column;
  }

  private static Control createInfoColumn( Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setLayoutData( ExampleUtil.createFillData() );
    return label;
  }

  private Control createFileUploadArea( Composite parent ) {
    Composite area = new Composite( parent, SWT.NONE );
    area.setLayout( ExampleUtil.createGridLayout( 2, true, true, true ) );
    ExampleUtil.createHeading( area, "FileUpload widget", 2 );
    fileNameLabel = new Label( area, SWT.NONE );
    fileNameLabel.setText( NO_FILES_SELECTED );
    fileNameLabel.setLayoutData( ExampleUtil.createHorzFillData() );
    fileNameLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    uploadButton = new Button( area, SWT.PUSH );
    uploadButton.setText( UPLOAD );
    uploadButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );
    new Label( area, SWT.NONE );
    final String url = startUploadReceiver();
    pushSession = new ServerPushSession();
    uploadButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( UPLOAD.equals( uploadButton.getText() ) ) {
          uploadButton.setText( ABORT );
          pushSession.start();
          fileUpload.submit( url );
        } else {
          fileNameLabel.setText( NO_FILES_SELECTED );
          uploadButton.setText( UPLOAD );
          createFileUpload( area );
        }
      }
    } );
    createFileUpload( area );
    return area;
  }

  private void createFileUpload( Composite parent ) {
    if( fileUpload != null ) {
      fileUpload.dispose();
    }
    fileUpload = new FileUpload( parent, SWT.NONE );
    fileUpload.setText( "Select File" );
    fileUpload.setFilterExtensions( new String[] { ".gif", ".png", ".jpg" } );
    fileUpload.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );
    fileUpload.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        String fileName = fileUpload.getFileName();
        fileNameLabel.setText( fileName == null ? "" : fileName );
      }
    } );
    fileUpload.moveAbove( fileNameLabel );
    parent.layout();
  }

  private String startUploadReceiver() {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();
    FileUploadHandler uploadHandler = new FileUploadHandler( receiver );
    uploadHandler.addUploadListener( new FileUploadListener() {

      @Override
      public void uploadProgress( FileUploadEvent event ) {
        // handle upload progress
      }

      @Override
      public void uploadFailed( FileUploadEvent event ) {
        addToLog( "upload failed: " + event.getException() );
        resetUploadButton();
      }

      @Override
      public void uploadFinished( FileUploadEvent event ) {
        for( FileDetails file : event.getFileDetails() ) {
          addToLog( "received: " + file.getFileName() );
        }
        resetUploadButton();
      }
    } );
    return uploadHandler.getUploadUrl();
  }

  private void resetUploadButton() {
    if( !uploadButton.isDisposed() ) {
      uploadButton.getDisplay().asyncExec( new Runnable() {
        @Override
        public void run() {
          uploadButton.setText( UPLOAD );
        }
      } );
    }
  }

  private void addToLog( final String message ) {
    if( !logText.isDisposed() ) {
      logText.getDisplay().asyncExec( new Runnable() {
        @Override
        public void run() {
          String text = logText.getText();
          if( INITIAL_TEXT.equals( text ) ) {
            text = "";
          }
          logText.setText( text + message + "\n" );
          pushSession.stop();
        }
      } );
    }
  }

  private Composite createFileDialogArea( Composite parent ) {
    Composite area = new Composite( parent, SWT.NONE );
    area.setLayout( ExampleUtil.createGridLayout( 2, true, true, true ) );
    ExampleUtil.createHeading( area, "FileDialog", 2 );
    createAddSingleButton( area );
    new Label( area, SWT.NONE );
    createAddMultiButton( area );
    return area;
  }

  private void createAddSingleButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setLayoutData( ExampleUtil.createHorzFillData() );
    button.setText( "Single File" );
    button.setToolTipText( "Launches file dialog for single file selection." );
    final Shell parentShell = parent.getShell();
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        openFileDialog( parentShell, false );
      }
    } );
  }

  private void createAddMultiButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setLayoutData( ExampleUtil.createHorzFillData() );
    button.setText( "Multiple Files" );
    button.setToolTipText( "Launches file dialog for multiple file selection." );
    final Shell parentShell = parent.getShell();
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        openFileDialog( parentShell, true );
      }
    } );
  }

  private void createClearButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setLayoutData( ExampleUtil.createHorzFillData() );
    button.setText( "Clear" );
    button.setToolTipText( "Clears the results list" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        logText.setText( INITIAL_TEXT );
      }
    } );
  }

  private void openFileDialog( Shell parent, boolean multi ) {
    final FileDialog fileDialog = new FileDialog( parent, getDialogStyle( multi ) );
    fileDialog.setText( multi ? "Upload Multiple Files" : "Upload Single File" );
    fileDialog.open( new DialogCallback() {
      @Override
      public void dialogClosed( int returnCode ) {
        showUploadResults( fileDialog );
      }
    } );
  }

  private void showUploadResults( FileDialog fileDialog ) {
    String[] selectedFiles = fileDialog.getFileNames();
    for( String fileName : selectedFiles ) {
      addToLog( "received: " + new File( fileName ).getName() );
    }
  }

  private static int getDialogStyle( boolean multi ) {
    int result = SWT.SHELL_TRIM | SWT.APPLICATION_MODAL;
    if( multi ) {
      result |= SWT.MULTI;
    }
    return result;
  }

}
