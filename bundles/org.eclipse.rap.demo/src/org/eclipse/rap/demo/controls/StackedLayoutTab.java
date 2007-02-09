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
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.widgets.*;

class StackedLayoutTab extends ExampleTab {

  private static final int COUNT = 5;
  private Composite stackedComp;
  private StackLayout stackLayout;
  private Control[] bArray;

  public StackedLayoutTab( final TabFolder folder ) {
    super( folder, "StackedLayout" );
  }

  void createStyleControls() {
    Button switchButton = createPropertyButton( "Next", RWT.PUSH );
    switchButton.setLocation( 5, 220 );
    switchButton.addSelectionListener( new SelectionListener() {
      int index = 1;
      public void widgetSelected( SelectionEvent event ) {
        stackLayout.topControl = bArray[ index ];
        stackedComp.layout();
        index = ( index + 1 ) % COUNT;
      }
    } );
  }

  void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    stackedComp = new Composite( parent, RWT.NONE );
    stackLayout = new StackLayout();
    stackedComp.setLayout( stackLayout );
    stackedComp.setBounds( 5, 5, 200, 200 );
    bArray = new Button[ COUNT ];
    for( int i = 0; i < COUNT; i++ ) {
      Button button = new Button( stackedComp, RWT.PUSH );
      button.setText( "Button " + ( i+1 ) );
      bArray[ i ] = button;
    }
    stackLayout.topControl = bArray[ 0 ];
  }

}
