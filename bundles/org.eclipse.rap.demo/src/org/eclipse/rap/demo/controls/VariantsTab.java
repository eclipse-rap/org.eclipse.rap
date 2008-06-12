/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
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
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class VariantsTab extends ExampleTab {

  private static final String VARIANT_SPECIAL = "special";

  private static final String USE_VARIANTS = "useVariants";

  private static final String BUTTON_IMAGE_PATH
    = "resources/button-image.gif";

  private final Image buttonImage;

  public VariantsTab( final CTabFolder folder ) {
    super( folder, "Variants" );
    ClassLoader classLoader = getClass().getClassLoader();
    buttonImage = Graphics.getImage( BUTTON_IMAGE_PATH, classLoader );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "FLAT", SWT.FLAT );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    String text = "Create with Custom Variant '" + VARIANT_SPECIAL + "'";
    createPropertyCheckbox( text, USE_VARIANTS );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Use custom theme to see the effect of widget variants." );

    // mybutton
    int style = getStyle();
    Button mybutton1 = new Button( parent, style | SWT.PUSH );
    mybutton1.setText( "Push Button" );
    mybutton1.setImage( buttonImage );
    if( hasCreateProperty( USE_VARIANTS ) ) {
      mybutton1.setData( WidgetUtil.CUSTOM_VARIANT, VARIANT_SPECIAL );
    }
    registerControl( mybutton1 );

    // myshell
    Button myshellButton = new Button( parent, style | SWT.PUSH );
    myshellButton.setText( "Open customized Shell" );
    myshellButton.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent e ) {
        Shell myShell = new Shell( parent.getShell(),
                                   SWT.CLOSE | SWT.APPLICATION_MODAL );
        if( hasCreateProperty( USE_VARIANTS ) ) {
          myShell.setData( WidgetUtil.CUSTOM_VARIANT, VARIANT_SPECIAL );
        }
        myShell.setText( "My Shell" );
        myShell.setSize( 200, 150 );
        myShell.open();
      }
    } );
  }
}
