/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import java.util.Arrays;

import org.eclipse.rap.rwt.addons.scanner.BarcodeScanner;
import org.eclipse.rap.rwt.addons.scanner.ScanListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public final class BarcodeScannerTab extends ExampleTab {

  private BarcodeScanner scanner;
  private Text formatField;
  private Text dataField;
  private Text rawDataField;

  public BarcodeScannerTab() {
    super( "BarcodeScanner" );
    setHorizontalSashFormWeights( new int[] { 100, 0 } );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new GridLayout( 4, true ) );
    scanner = new BarcodeScanner( parent, SWT.BORDER );
    GridData layoutData = new GridData( 400, 300 );
    layoutData.horizontalSpan = 2;
    scanner.setLayoutData( layoutData );

    createResultGroup( parent );
    createStartScanButton( parent );
    createStopScanButton( parent );

    scanner.addScanListener( new ScanListener() {
      @Override
      public void scanSucceeded( String format, String data, int[] rawData ) {
        formatField.setText( format );
        dataField.setText( data );
        rawDataField.setText( Arrays.toString( rawData ) );
      }
      @Override
      public void scanFailed( String error ) {
        log( "An error occurred while scanning the code: " + error );
      }
    } );
    registerControl( scanner );
  }

  private void createResultGroup( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    GridData layoutData = new GridData( SWT.FILL, SWT.FILL, false, false );
    layoutData.horizontalSpan = 2;
    group.setLayoutData( layoutData );
    group.setText( "Decoded data" );
    group.setLayout( new GridLayout( 2, false ) );
    new Label( group, SWT.NONE ).setText( "Format" );
    formatField = new Text( group, SWT.READ_ONLY | SWT.BORDER );
    formatField.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    new Label( group, SWT.NONE ).setText( "Data" );
    dataField = new Text( group, SWT.READ_ONLY | SWT.BORDER );
    dataField.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    new Label( group, SWT.NONE ).setText( "Raw Data" );
    rawDataField = new Text( group, SWT.READ_ONLY | SWT.BORDER );
    rawDataField.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
  }

  private void createStartScanButton( Composite parent ) {
    final Button button = new Button( parent, SWT.PUSH );
    button.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, false, false ) );
    button.setText( "Start" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        scanner.start( new BarcodeScanner.Formats[] {
          BarcodeScanner.Formats.DATA_MATRIX,
          BarcodeScanner.Formats.QR_CODE
        } );
      }
    } );
  }

  private void createStopScanButton( Composite parent ) {
    final Button button = new Button( parent, SWT.PUSH );
    button.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, false, false ) );
    button.setText( "Stop" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        scanner.stop();
        formatField.setText( "" );
        dataField.setText( "" );
        rawDataField.setText( "" );
      }
    } );
  }

}
