/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class FillLayoutTab extends ExampleTab {

  private boolean propPrefSize;

  public FillLayoutTab( final CTabFolder folder ) {
    super( folder, "FillLayout" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
    createStyleButton( "VERTICAL", SWT.VERTICAL );
    final Button prefSizeButton = createPropertyButton( "Preferred Size" );
    prefSizeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        propPrefSize = prefSizeButton.getSelection();
        createNew();
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    int style = getStyle();
    GridLayout parentLayout = new GridLayout();
    parentLayout.marginWidth = 5;
    parent.setLayout( parentLayout );

    Composite comp = new Composite( parent, SWT.NONE );
    comp.setBackground( Graphics.getColor( 0xcc, 0xb7, 0x91 ) );

    FillLayout fillLayout = new FillLayout( style );
    fillLayout.marginWidth = 3;
    fillLayout.marginHeight = 3;
    fillLayout.spacing = 3;
    comp.setLayout( fillLayout );
    Button b1 = new Button( comp, SWT.PUSH );
    b1.setText( "Button 1" );
    Button b2 = new Button( comp, SWT.PUSH );
    b2.setText( "Button 2" );
    Button b3 = new Button( comp, SWT.PUSH );
    b3.setText( "Button 3" );

    if( propPrefSize ) {
      comp.setLayoutData( new GridData() );
    } else {
      comp.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    }
    comp.layout();
    registerControl( comp );
  }
}
