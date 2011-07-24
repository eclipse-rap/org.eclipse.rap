/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.demo.fileupload;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rwt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class FileUploadExamplePage implements IExamplePage {

  private static final String INITIAL_TEXT = "no files uploaded.";
  private FileUpload fileUpload;
  private Label fileNameLabel;
  private Button uploadButton;
  private Label statsLabel;

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    Control fileUploadArea = createFileUploadArea( parent );
    fileUploadArea.setLayoutData( ExampleUtil.createHorzFillData() );
    Control fileDialogArea = createFileDialogArea( parent );
    fileDialogArea.setLayoutData( ExampleUtil.createFillData() );
  }

  private Control createFileUploadArea( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "File Upload" );
    GridLayout layout = ExampleUtil.createGridLayout( 3, false, 10, 10 );
    layout.marginBottom = 50;
    group.setLayout( layout );
    fileUpload = new FileUpload( group, SWT.NONE );
    fileUpload.setText( "Select File" );
    fileNameLabel = new Label( group, SWT.NONE );
    fileNameLabel.setText( "no file selected" );
    fileNameLabel.setLayoutData( ExampleUtil.createHorzFillData() );
    uploadButton = new Button( group, SWT.PUSH );
    uploadButton.setText( "Upload" );
    fileUpload.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        String fileName = fileUpload.getFileName();
        fileNameLabel.setText( fileName == null ? "" : fileName );
      }
    } );
    uploadButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        fileUpload.submit( "http://localhost/" );
      }
    } );
    return group;
  }

  private Composite createFileDialogArea( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "File Dialog" );
    group.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 10 ) );
    Composite buttonComposite = new Composite( group, SWT.NONE );
    buttonComposite.setLayoutData( GridDataFactory.fillDefaults().create() );
    buttonComposite.setLayout( new GridLayout( 1, true ) );
    createAddSingleButton( buttonComposite );
    createAddMultiButton( buttonComposite );
    createClearButton( buttonComposite );
    createStatsLabel( group );
    return group;
  }

  private void createAddSingleButton( Composite parent ) {
    Button addBtn = new Button( parent, SWT.PUSH );
    addBtn.setText( "Add Single File" );
    addBtn.setToolTipText( "Launches file dialog for single file selection." );
    final Shell parentShell = parent.getShell();
    addBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        openFileDialog( parentShell, false );
      }
    } );
  }

  private void createAddMultiButton( Composite parent ) {
    Button addMultiBtn = new Button( parent, SWT.PUSH );
    addMultiBtn.setText( "Add Multiple Files" );
    addMultiBtn.setToolTipText( "Launches file dialog for multiple file selection." );
    final Shell parentShell = parent.getShell();
    addMultiBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        openFileDialog( parentShell, true );
      }
    } );
  }
  
  private void createClearButton( Composite parent ) {
    Button clearBtn = new Button( parent, SWT.PUSH );
    clearBtn.setText( "Clear" );
    clearBtn.setToolTipText( "Clears the results list" );
    clearBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        statsLabel.setText( "" );
      }
    } );
  }

  private void createStatsLabel( Group group ) {
    statsLabel = new Label( group, SWT.NONE );
    statsLabel.setText( INITIAL_TEXT );
    statsLabel.setLayoutData( ExampleUtil.createFillData() );
  }

  private void openFileDialog( Shell parent, boolean multi ) {
    int style = getDialogStyle( multi );
    final FileDialog fileDialog = new FileDialog( parent, style );
    fileDialog.setAutoUpload( true );
    fileDialog.setText( multi ? "Upload Multiple Files" : "Upload Single File" );
    fileDialog.setFilterExtensions( new String[] { "*.txt", "*.*" } );
    fileDialog.setFilterNames( new String[] { "Text Files", "All Files" } );
    DialogUtil.open( fileDialog, new DialogCallback() {
      public void dialogClosed( int returnCode ) {
        showUploadResults( fileDialog );
      }
    } );
  }

  private void showUploadResults( FileDialog fileDialog ) {
    StringBuilder builder = new StringBuilder();
    builder.append( "Results:\n" );
    String[] selectedFiles = fileDialog.getFileNames();
    for( String fileName : selectedFiles ) {
      builder.append( fileName + "\n" );
    }
    statsLabel.setText( builder.toString() );
  }

  private static int getDialogStyle( boolean multi ) {
    int result = SWT.SHELL_TRIM | SWT.APPLICATION_MODAL;
    if( multi ) {
      result |= SWT.MULTI;
    }
    return result;
  }
}
