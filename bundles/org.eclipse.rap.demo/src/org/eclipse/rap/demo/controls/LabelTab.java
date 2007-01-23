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

public class LabelTab extends ExampleTab {

  public LabelTab( final TabFolder parent ) {
    super( parent, "Label" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "SEPARATOR" );
    createStyleButton( "HORIZONTAL" );
    createStyleButton( "VERTICAL" );
    createStyleButton( "SHADOW_IN" );
    createStyleButton( "SHADOW_OUT" );
    createStyleButton( "SHADOW_NONE" );
    createStyleButton( "LEFT" );
    createStyleButton( "CENTER" );
    createStyleButton( "RIGHT" );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
  }

  void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    RowData data = new RowData( 80, 20 );
    Label label1 = new Label( top, style );
    label1.setText( "Label One" );
    label1.setLayoutData( data );
    Label label2 = new Label( top, style );
    label2.setText( "Label Two" );
    label2.setLayoutData( data );
    Label label3 = new Label( top, style );
    label3.setText( "Label Three" );
    label3.setLayoutData( data );
    registerControl( label1 );
    registerControl( label2 );
    registerControl( label3 );
  }
}
