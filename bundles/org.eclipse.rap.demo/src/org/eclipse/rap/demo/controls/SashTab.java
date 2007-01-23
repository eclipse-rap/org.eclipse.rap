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

public class SashTab extends ExampleTab {

  public SashTab( TabFolder parent ) {
    super( parent, "Sash" );
  }

  void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "SMOOTH" );
    createStyleButton( "VERTICAL" );
    createStyleButton( "HORIZONTAL" );
    createVisibilityButton();
    createEnablementButton();
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    Label label = new Label( top, RWT.NONE );
    label.setLayoutData( new RowData( 50, 20 ) );
    label.setText( "Sash ->" );
    Sash sash = new Sash( top, style );
    sash.setLayoutData( ( sash.getStyle() & RWT.HORIZONTAL ) != 0
                          ? new RowData( 100, 10 )
                          : new RowData( 10, 100 ) );
    registerControl( sash );
  }
}
