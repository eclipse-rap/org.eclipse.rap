/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class StackLayoutTab extends ExampleTab {

  private static final int COUNT = 5;
  private Composite comp;
  private StackLayout stackLayout;
  private Control[] bArray;
  private int index;
  private boolean propPrefSize;

  public StackLayoutTab( final CTabFolder folder ) {
    super( folder, "StackLayout" );
    index = 0;
  }

  protected void createStyleControls( final Composite parent ) {
    final Button prefSizeButton = createPropertyButton( "Preferred Size" );
    prefSizeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        propPrefSize = prefSizeButton.getSelection();
        createNew();
      }
    } );
    Button switchButton = createPropertyButton( "Next", SWT.PUSH );
    switchButton.setLocation( 5, 220 );
    switchButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showNext();
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    GridLayout parentLayout = new GridLayout();
    parentLayout.marginWidth = 5;
    parent.setLayout( parentLayout );

    comp = new Composite( parent, SWT.NONE );
    comp.setBackground( Color.getColor( 0xcc, 0xb7, 0x91 ) );
    stackLayout = new StackLayout();
    stackLayout.marginWidth = 3;
    stackLayout.marginHeight = 3;
    comp.setLayout( stackLayout );
    bArray = new Button[ COUNT ];
    for( int i = 0; i < COUNT; i++ ) {
      Button button = new Button( comp, SWT.PUSH );
      button.setText( "Control " + ( i+1 ) );
      button.setFont( Font.getFont( "Serif", 24, SWT.BOLD ) );
      bArray[ i ] = button;
    }
    stackLayout.topControl = bArray[ index ];

    if( propPrefSize ) {
      comp.setLayoutData( new GridData() );
    } else {
      comp.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    }
    comp.layout();
    registerControl( comp );
  }

  private void showNext() {
    index = ( index + 1 ) % COUNT;
    stackLayout.topControl = bArray[ index ];
    comp.layout();
  }
}
