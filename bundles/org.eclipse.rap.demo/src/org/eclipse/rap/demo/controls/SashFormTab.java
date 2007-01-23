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
import org.eclipse.rap.rwt.custom.SashForm;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.widgets.*;

public class SashFormTab extends ExampleTab {

  public SashFormTab( TabFolder parent ) {
    super( parent, "SashForm" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "SMOOTH" );
    createStyleButton( "VERTICAL" );
    createStyleButton( "HORIZONTAL" );
    createVisibilityButton();
    createEnablementButton();
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new FillLayout() );
    int style = getStyle();
    SashForm sashForm = new SashForm( top, style );
    Text text = new Text( sashForm, RWT.MULTI | RWT.WRAP );
    text.setText( "Lorem ipsum dolor sit amet, consectetur adipisicing "
                   + "elit, sed do eiusmod tempor incididunt ut labore et "
                   + "dolore magna aliqua." );
    List list = new List( sashForm, RWT.MULTI );
    String[] items = new String[8];
    for( int i = 0; i < items.length; i++ ) {
      items[i] = "Item " + (i+1);      
    }
    list.setItems( items );
    registerControl( sashForm );
  }

}
