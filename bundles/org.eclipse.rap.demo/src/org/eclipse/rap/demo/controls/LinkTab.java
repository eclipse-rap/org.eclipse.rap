/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class LinkTab extends ExampleTab {

  private Link customLink;

  public LinkTab( final CTabFolder topFolder ) {
    super( topFolder, "Link" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
    createCustomLinkControl( parent );
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
    customLink = new Link( parent, style );
    customLink.setText( "Custom link, use controls to your right to change" );
    customLink.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        String msg = "Link widget selected, text=" + event.text;
        MessageDialog.openInformation( getShell(), "Information", msg );
      }
    } );
    registerControl( link1 );
    registerControl( link2 );
    registerControl( link3 );
    registerControl( customLink );
  }

  private void createCustomLinkControl( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label lblText = new Label( composite, SWT.NONE );
    lblText.setText( "Text" );
    final Text txtText = new Text( composite, SWT.BORDER );
    Button btnChange = new Button( composite, SWT.PUSH );
    btnChange.setText( "Change" );
    btnChange.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        customLink.setText( txtText.getText() );
      }
    } );

  }
}
