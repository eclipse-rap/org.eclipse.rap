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
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

public class ComboTab extends ExampleTab {
  
  public ComboTab( final TabFolder parent ) {
    super( parent, "Combo" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createVisibilityButton();
    createEnablementButton();
  }

  protected void createExampleControls( final Composite top ) {
    top.setLayout( new GridLayout( 2, false ) );
    int style = getStyle();
    String[] items
      = new String[] { "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" };
    // empty combo
    Combo combo1 = new Combo( top, style );
    registerControl( combo1 );
    new Label( top, RWT.NONE ).setText( "empty combo box" );
    // filled combo
    Combo combo2 = new Combo( top, style );
    combo2.setItems( items );
    new Label( top, RWT.NONE ).setText( "filled combo box" );
    registerControl( combo2 );
    // filled combo with preselection
    Combo combo3 = new Combo( top, style );
    combo3.setItems( items );
    combo3.select( 1 );
    new Label( top, RWT.NONE ).setText( "filled combo box with preselection" );
    registerControl( combo3 );
  }
}
