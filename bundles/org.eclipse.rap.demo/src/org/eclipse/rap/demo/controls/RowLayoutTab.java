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
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

class RowLayoutTab extends ExampleTab {

  private boolean propPrefSize;
  private boolean propWrap;

  public RowLayoutTab( final TabFolder folder ) {
    super( folder, "RowLayout" );
  }

  protected void createStyleControls() {
    createStyleButton( "HORIZONTAL" );
    createStyleButton( "VERTICAL" );
    final Button prefSizeButton = createPropertyButton( "Preferred Size" );
    prefSizeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        propPrefSize = prefSizeButton.getSelection();
        createNew();
      }
    } );
    final Button wrapButton = createPropertyButton( "Wrap" );
    wrapButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        propWrap = wrapButton.getSelection();
        createNew();
      }
    } );
//    createPropertyButton( "pack" );
//    createPropertyButton( "justify" );
  }

  protected void createExampleControls( final Composite parent ) {
    int style = getStyle();
    GridLayout parentLayout = new GridLayout();
    parentLayout.marginWidth = 5;
    parent.setLayout( parentLayout );
    Composite comp = new Composite( parent, RWT.NONE );
    comp.setBackground( Color.getColor( 0xcc, 0xb7, 0x91 ) );
    RowLayout layout = new RowLayout( style );
    layout.wrap = propWrap;
    comp.setLayout( layout );
    Button b1 = new Button( comp, RWT.PUSH );
    b1.setText( "Button 1" );
    Button b2 = new Button( comp, RWT.PUSH );
    b2.setText( "Button 2" );
    Button b3 = new Button( comp, RWT.PUSH );
    b3.setText( "Button 3" );
    Label l1 = new Label( comp, RWT.BORDER );
    l1.setText( "Label" );
    Text t1 = new Text( comp, RWT.BORDER | RWT.SINGLE );
    t1.setText( "Lorem ipsum dolor sit amet" );
//    createTabFolder( comp );
    new Text( comp, RWT.BORDER | RWT.SINGLE );
    if( propPrefSize ) {
      comp.setLayoutData( new GridData() );
    } else {
      comp.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    }
    comp.layout();
    registerControl( comp );
  }

  private TabFolder createTabFolder( Composite parent ) {
    TabFolder tf = new TabFolder( parent, RWT.BORDER );
    TabItem tab1 = new TabItem( tf, RWT.NONE );
    tab1.setText( "Tab1" );
    Label tla1 = new Label( tf, RWT.NONE );
    tla1.setText( "Content 1" );
    tab1.setControl( tla1 );
    TabItem tab2 = new TabItem( tf, RWT.NONE );
    tab2.setText( "Tab2" );
    Label tla2 = new Label( tf, RWT.NONE );
    tla2.setText( "Content 2\nwith two lines" );
    tab2.setControl( tla2 );
    TabItem tab3 = new TabItem( tf, RWT.NONE );
    tab3.setText( "Tab3" );
    Label tla3 = new Label( tf, RWT.NONE );
    tla3.setText( "Content 3 is somewhat longer" );
    tab3.setControl( tla3 );
    tf.setSelection( 0 );
    return tf;
  }
}
