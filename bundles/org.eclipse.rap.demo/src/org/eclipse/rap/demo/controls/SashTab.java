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
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class SashTab extends ExampleTab {

  public SashTab( final CTabFolder topFolder ) {
    super( topFolder, "Sash" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "VERTICAL", SWT.VERTICAL );
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
    createVisibilityButton();
    createEnablementButton();
  }

  protected void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    Label label = new Label( top, SWT.NONE );
    label.setLayoutData( new RowData( 50, 20 ) );
    label.setText( "Sash ->" );
    Sash sash = new Sash( top, style );
    sash.setLayoutData( ( sash.getStyle() & SWT.HORIZONTAL ) != 0
                          ? new RowData( 100, 10 )
                          : new RowData( 10, 100 ) );
    registerControl( sash );
  }
}
