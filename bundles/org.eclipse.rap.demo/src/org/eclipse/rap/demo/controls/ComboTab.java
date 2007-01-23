/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ComboTab extends ExampleTab {
  
  public ComboTab( TabFolder parent ) {
    super( parent, "Combo" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createVisibilityButton();
    createEnablementButton();
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    RowData data = new RowData( 100, 25 );
    Combo combo = new Combo( top, style );
    combo.setLayoutData( data );
    combo.setItems( new String[] { "Eins", "Zwei", "Drei" } );
    combo.select( 0 );
    registerControl( combo );
  }

}
