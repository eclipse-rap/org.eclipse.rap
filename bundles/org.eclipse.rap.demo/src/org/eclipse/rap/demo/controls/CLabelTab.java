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

  private String text2;
  private String text1;
  private Image image2;
  private Image image1;

  public CLabelTab( final CTabFolder parent ) {
    super( parent, "CLabel" );
    ClassLoader classLoader = getClass().getClassLoader();
    image1 = Graphics.getImage( "resources/button-image.gif", classLoader );
    image2 = Graphics.getImage( "resources/newfile_wiz.gif", classLoader );
    text1 = "Some Text";
    text2 = "Some Other Text";
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
    left.setText( text1 );
    left.setImage( image1 );
    CLabel center = new CLabel( parent, style );
    center.setText( text2 );
    CLabel right = new CLabel( parent, style );
    right.setText( "And more" );
    right.setImage( image2 );
    registerControl( left );
    registerControl( center );
    registerControl( right );
  }
}
