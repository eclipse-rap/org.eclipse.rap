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
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TextTab extends ExampleTab {

  private Text text;

  public TextTab( final TabFolder folder ) {
    super( folder, "Text" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "WRAP" );
    createStyleButton( "SINGLE" );
    createStyleButton( "MULTI" );
    createStyleButton( "PASSWORD" );
    createStyleButton( "READ_ONLY" );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
  }

  void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    text = new Text( top, style );
    if( ( text.getStyle() & RWT.SINGLE ) != 0 ) {
      text.setLayoutData( new RowData( 200, 20 ) );
    } else {
      text.setLayoutData( new RowData( 200, 200 ) );
    }
    text.setText( "Lorem ipsum dolor sit amet, consectetur adipisici "
                 + "elit, sed do eiusmod tempor incididunt ut labore et "
                 + "dolore magna aliqua.\n"
                 + "Ut enim ad minim veniam, quis nostrud exercitation "
                 + "ullamco laboris nisi ut aliquip ex ea commodo "
                 + "consequat.\n"
                 + "Duis aute irure dolor in reprehenderit in voluptate"
                 + " velit esse cillum dolore eu fugiat nulla pariatur." );    
    registerControl( text );
  }

}
