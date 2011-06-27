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
import org.eclipse.rap.examples.*;
import org.eclipse.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


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
    fileNameLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true,  false ) );
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
    Composite btnComp = new Composite( group, SWT.NONE );
    GridDataFactory factory = GridDataFactory.fillDefaults();
    btnComp.setLayoutData( factory.create() );
    btnComp.setLayout( new GridLayout( 1, true ) );
    Button addBtn = new Button( btnComp, SWT.PUSH );
    addBtn.setText( "Add Single File" );
    addBtn.setToolTipText( "Launches file dialog for single file selection." );
    addBtn.setLayoutData( factory.create() );
    addBtn.addSelectionListener( new SelectionAdapter() {
  
      public void widgetSelected( SelectionEvent e ) {
        FileDialog fd = new FileDialog( Display.getDefault().getActiveShell(),
                                        SWT.SHELL_TRIM | SWT.APPLICATION_MODAL );
        fd.setAutoUpload( true );
        fd.setText( "Upload Single File" );
        fd.setFilterPath( "C:/" );
        String[] filterExt = {
          "*.mp4", "*.*"
        };
        fd.setFilterExtensions( filterExt );
        String[] filterNames = {
          "Videos", "All Files"
        };
        fd.setFilterNames( filterNames );
        String selected = fd.open();
        if( selected != null ) {
          showUploadResults( selected );
        }
      }
    } );
    Button addMultiBtn = new Button( btnComp, SWT.PUSH );
    addMultiBtn.setText( "Add Multiple Files" );
    addMultiBtn.setToolTipText( "Launches file dialog for multiple file selection." );
    addMultiBtn.setLayoutData( factory.create() );
    addMultiBtn.addSelectionListener( new SelectionAdapter() {
  
      public void widgetSelected( SelectionEvent e ) {
        FileDialog fd = new FileDialog( Display.getDefault().getActiveShell(),
                                        SWT.SHELL_TRIM
                                            | SWT.MULTI
                                            | SWT.APPLICATION_MODAL );
        fd.setAutoUpload( true );
        fd.setText( "Upload Multiple Files" );
        fd.setFilterPath( "C:/" );
        String[] filterExt = {
          "*.mp4", "*.*"
        };
        fd.setFilterExtensions( filterExt );
        String[] filterNames = {
          "Videos", "All Files"
        };
        fd.setFilterNames( filterNames );
        String selected = fd.open();
        if( selected != null && selected.length() > 0 ) {
          showUploadResults( fd.getFileNames() );
        }
      }
    } );
    Button clearBtn = new Button( btnComp, SWT.PUSH );
    clearBtn.setText( "Clear" );
    clearBtn.setToolTipText( "Clears the results list" );
    clearBtn.setLayoutData( factory.create() );
    clearBtn.addSelectionListener( new SelectionAdapter() {
  
      public void widgetSelected( SelectionEvent e ) {
        statsLabel.setText( "" );
      }
    } );
    statsLabel = new Label( group, SWT.NONE );
    statsLabel.setText( INITIAL_TEXT );
    statsLabel.setLayoutData( ExampleUtil.createFillData() );
    return group;
  }

  private void showUploadResults( String fileName ) {
    statsLabel.setText( "Result:\n" + fileName + "\n" );
  }

  private void showUploadResults( String[] fileNames ) {
    String text = "Results:\n";
    for( int i = 0; i < fileNames.length; i++ ) {
      String fileName = fileNames[ i ];
      text += fileName + "\n";
    }
    statsLabel.setText( text );
  }
}
