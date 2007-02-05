/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.widgets.*;

class GroupTab extends ExampleTab {

  private Group group;

  public GroupTab( final TabFolder folder ) {
    super( folder, "Group" );
  }

  void createStyleControls() {
    createVisibilityButton();
    createEnablementButton();
    createChangeText();
    createFontChooser();
  }

  void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    group = new Group( parent, RWT.NONE );
    group.setLayout( new FillLayout() );
    group.setText( "A group with one white label" );
    Label content = new Label( group, RWT.NONE );
    content.setText( "Hello from inside the group box..." );
    Color white = content.getDisplay().getSystemColor( RWT.COLOR_WHITE );
    content.setBackground( white );
    registerControl( group );
  }

  private void createChangeText() {
    Label label = new Label( styleComp, RWT.NONE );
    label.setText( "Text" );
    label.setLayoutData( new RowData( 80, 20 ) );
    final Text text = new Text( styleComp, RWT.BORDER );
    text.setLayoutData( new RowData( 80, 20 ) );
    text.setText( "Hello from inside the group box..." );
    Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Change text" );
    button.setLayoutData( new RowData( 80, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        group.setText( text.getText() );
      }
    } ); 
  }

}
