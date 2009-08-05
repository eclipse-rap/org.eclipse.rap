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

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class CLabelTab extends ExampleTab {

  private static final String IMAGE2 = "resources/newfile_wiz.gif";
  private static final String IMAGE1 = "resources/button-image.gif";

  public CLabelTab( final CTabFolder parent ) {
    super( parent, "CLabel" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "LEFT", SWT.LEFT );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createStyleButton( "CENTER", SWT.CENTER );
    createStyleButton( "SHADOW_IN", SWT.SHADOW_IN );
    createStyleButton( "SHADOW_OUT", SWT.SHADOW_OUT );
    createStyleButton( "SHADOW_NONE", SWT.SHADOW_NONE );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
    createCursorCombo();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout() );
    int style = getStyle();
    CLabel left = new CLabel( parent, style );
    left.setText( "Some Text" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image image1 = Graphics.getImage( IMAGE1, classLoader );
    left.setImage( image1 );
    CLabel center = new CLabel( parent, style );
    center.setText( "First Line\nSecond Line\n" );
    CLabel right = new CLabel( parent, style );
    right.setText( "And more" );
    Image image2 = Graphics.getImage( IMAGE2, classLoader );
    right.setImage( image2 );
    registerControl( left );
    registerControl( center );
    registerControl( right );
  }
}
