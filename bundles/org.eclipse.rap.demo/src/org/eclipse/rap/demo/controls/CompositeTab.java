/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.widgets.Composite;
import org.eclipse.rap.rwt.widgets.TabFolder;

public class CompositeTab extends ExampleTab {

  public CompositeTab( final TabFolder parent ) {
    super( parent, "Composite" );
  }

  void createStyleControls() {
    createStyleButton( "BORDER" );
    createVisibilityButton();
  }

  void createExampleControls( final Composite top ) {
    top.setLayout( new FillLayout() );
    int style = getStyle();
    final Composite comp = new Composite( top, style );
    Color bgColor = Color.getColor( 240, 250, 190 );
    comp.setBackground( bgColor );
    registerControl( comp );
  }
}
