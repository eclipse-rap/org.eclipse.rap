/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.jface.dialogs.MessageDialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.layout.GridData;
import org.eclipse.rap.rwt.layout.GridLayout;
import org.eclipse.rap.rwt.widgets.*;

public class RequestTab extends ExampleTab {

  public RequestTab( final TabFolder parent ) {
    super( parent, "Longrunning Request" );
  }

  protected void createStyleControls() {
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 4, false ) );
    Label lblInfo = new Label( parent, RWT.WRAP );
    String msg 
      = "Simulate a long running server-side task. You should see the mouse " 
      + "cursor change after a short delay.";
    lblInfo.setText( msg );
    GridData gridData = new GridData();
    gridData.horizontalSpan = 4;
    lblInfo.setLayoutData( gridData );
    Label lblProcessingTime = new Label( parent, RWT.NONE );
    lblProcessingTime.setText( "Processing time" );
    final Text txtProcessingTime = new Text( parent, RWT.BORDER );
    txtProcessingTime.setText( "5000" );
    Label lblMS = new Label( parent, RWT.NONE );
    lblMS.setText( "ms" );
    Button btnRun = new Button( parent, RWT.PUSH );
    btnRun.setText( "Run" );
    btnRun.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        String timeText = txtProcessingTime.getText().trim();
        int time = -1;
        try {
          time = Integer.parseInt( timeText );
        } catch( NumberFormatException e ) {
          // ignore as time is initialized with an illegal value
        } 
        if( time >= 0 ) {
          boolean interrupted = false;
          try {
            Thread.sleep( time );
          } catch( InterruptedException e ) {
            interrupted = true;
          }
          Shell shell = parent.getShell();
          String msg = interrupted ? "Interrupted" : "Done";
          MessageDialog.openInformation( shell, "Information", msg, null );
        } else {
          Shell shell = parent.getShell();
          String msg = "\'" + timeText + "\' is not a valid processing time.";
          MessageDialog.openError( shell, "Error", msg, null );
        }
      }
    } );
  }
}
