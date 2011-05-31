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
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import java.io.Serializable;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class ButtonEntryPoint implements IEntryPoint {

  private Display display;

  public int createUI() {
    display = new Display();
    Shell shell = new Shell( display );
    shell.setText( "swt_layout" );
    shell.setLayout( new GridLayout( 3, true ) );
    Label label = new Label( shell, SWT.NONE );
    GridData gridData = new GridData( SWT.BEGINNING, SWT.CENTER, true, true );
    gridData.horizontalSpan = 3;
    label.setLayoutData( gridData );
    label.setText( "relocate me!" );
    addToggleButton( label, "left", SWT.LEFT );
    addToggleButton( label, "center", SWT.CENTER );
    addToggleButton( label, "right", SWT.RIGHT );
    shell.setSize( 300, 200 );
    shell.open();
    return 0;
  }

  private void addToggleButton( Label label, String buttonText, int position ) {
    Button button = new Button( label.getParent(), SWT.PUSH );
    button.setLayoutData( new GridData( position, SWT.CENTER, true, true ) );
    button.setText( buttonText );
    button.addSelectionListener( new UpdateLabelListener( position, label ) );
  }
  
  private static class UpdateLabelListener extends SelectionAdapter implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int position;
    private final Label label;
    private final Integer globalCounter;
    private int counter = 0;

    UpdateLabelListener( int position, Label label ) {
      this.position = position;
      this.label = label;
      this.globalCounter = new Integer( 1 );
    }

    public void widgetSelected( SelectionEvent event ) {
      updateLabel( label, position );
    }

    private void updateLabel( Label label, int position ) {
      GridData gridData = new GridData( position, SWT.CENTER, true, true );
      gridData.horizontalSpan = 3;
      label.setLayoutData( gridData );
      label.getParent().layout();
      counter++;
      label.setText( "relocated " + counter + "/" + globalCounter + " times" );
      label.pack();
    }
  }
}
