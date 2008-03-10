/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


public class GroupTab extends ExampleTab {

  private Group group;

  public GroupTab( final CTabFolder folder ) {
    super( folder, "Group" );
  }

  protected void createStyleControls(Composite top) {
    createStyleButton( "BORDER", SWT.BORDER );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    group = new Group( parent, getStyle() );
    group.setLayout( new FillLayout() );
    group.setText( "A group with one white label" );
    Label content = new Label( group, SWT.NONE );
    content.setText( "Hello from inside the group box..." );
    Color white = content.getDisplay().getSystemColor( SWT.COLOR_WHITE );
    content.setBackground( white );
    registerControl( group );
  }
}
