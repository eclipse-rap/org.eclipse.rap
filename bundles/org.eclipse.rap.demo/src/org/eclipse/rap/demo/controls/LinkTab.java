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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

public class LinkTab extends ExampleTab {

  public LinkTab( final CTabFolder topFolder ) {
    super( topFolder, "Link" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout() );
    int style = getStyle();
    Link link1 = new Link( parent, style );
    link1.setText( "Lorem <a>ipsum</a> dolor <a>sit amet</a>" );
    Link link2 = new Link( parent, style );
    link2.setText( "Link without href" );
    Link link3 = new Link( parent, style );
    link3.setText( "<a>Link with one href</a>" );
    registerControl( link1 );
    registerControl( link2 );
    registerControl( link3 );
  }
}
