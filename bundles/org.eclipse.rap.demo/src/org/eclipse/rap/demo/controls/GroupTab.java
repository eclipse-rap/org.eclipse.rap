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
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.widgets.*;

class GroupTab extends ExampleTab {

  private Group group;

  public GroupTab( final TabFolder folder ) {
    super( folder, "Group" );
  }

  void createStyleControls() {
    createVisibilityButton();
    createEnablementButton();
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
}
