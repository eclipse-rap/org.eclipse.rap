/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.StackLayout;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.graphics.Font;
import org.eclipse.rap.rwt.layout.GridData;
import org.eclipse.rap.rwt.layout.GridLayout;
import org.eclipse.rap.rwt.widgets.*;

class StackLayoutTab extends ExampleTab {

  private static final int COUNT = 5;
  private Composite comp;
  private StackLayout stackLayout;
  private Control[] bArray;
  private int index;
  private boolean propPrefSize;

  // TODO [rst] This layout should be reimplemented using z-order
  public StackLayoutTab( final TabFolder folder ) {
    super( folder, "StackLayout" );
    index = 0;
  }

  protected void createStyleControls() {
    final Button prefSizeButton = createPropertyButton( "Preferred Size" );
    prefSizeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        propPrefSize = prefSizeButton.getSelection();
        createNew();
      }
    } );
    Button switchButton = createPropertyButton( "Next", RWT.PUSH );
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
    
    comp = new Composite( parent, RWT.NONE );
    comp.setBackground( Color.getColor( 0xcc, 0xb7, 0x91 ) );
    stackLayout = new StackLayout();
    stackLayout.marginWidth = 3;
    stackLayout.marginHeight = 3;
    comp.setLayout( stackLayout );
    bArray = new Button[ COUNT ];
    for( int i = 0; i < COUNT; i++ ) {
      Button button = new Button( comp, RWT.PUSH );
      button.setText( "Control " + ( i+1 ) );
      button.setFont( Font.getFont( "Serif", 24, RWT.BOLD ) );
      bArray[ i ] = button;
    }
    stackLayout.topControl = bArray[ 0 ];

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
