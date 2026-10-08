/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public final class CBannerTab extends ExampleTab {

  public CBannerTab() {
    super( "CBanner" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    createBgColorButton();
    createBgImageButton();
  }

  @Override
  protected void createExampleControls( final Composite top ) {
    top.setLayout( new GridLayout() );
    int style = getStyle();
    CBanner banner = new CBanner( top, style );
    banner.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );

    Label rightLabel = new Label( banner, SWT.NONE );
    rightLabel.setText( "Right" );
//    rightLabel.setBackground( Graphics.getColor( 250, 250, 250 ) );
    banner.setRight( rightLabel );

    Label leftLabel = new Label( banner, SWT.NONE );
    leftLabel.setText( "Left" );
//    leftLabel.setBackground( Graphics.getColor( 250, 250, 250 ) );
    banner.setLeft( leftLabel );

//    Label bottomLabel = new Label( banner, SWT.NONE );
//    bottomLabel.setText( "Bottom" );
//    bottomLabel.setBackground( Graphics.getColor( 250, 250, 250 ) );
//    banner.setBottom( bottomLabel );
//
//    Label contentLabel = new Label( banner, SWT.BORDER );
//    contentLabel.setToolTipText( "Content" );
    registerControl( banner );
  }
}
