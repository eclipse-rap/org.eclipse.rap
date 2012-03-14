/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class CLabelTab extends ExampleTab {

  private CLabel left;
  private CLabel center;
  private CLabel right;
  private boolean showBgGradient;
  private final String markup;
  private boolean markupEnabled;

  private static final String IMAGE2 = "resources/newfile_wiz.gif";
  private static final String IMAGE1 = "resources/button-image.gif";

  public CLabelTab( CTabFolder parent ) {
    super( parent, "CLabel" );
    markup = "<big><i>Some</i></big> <b>Other</b> <small>Text With Markup</small> - 2<sup>16</sup>";
    markupEnabled = true;
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "LEFT", SWT.LEFT );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createStyleButton( "CENTER", SWT.CENTER );
    createStyleButton( "SHADOW_IN", SWT.SHADOW_IN );
    createStyleButton( "SHADOW_OUT", SWT.SHADOW_OUT );
    createStyleButton( "SHADOW_NONE", SWT.SHADOW_NONE );
    createMarkupButton();
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createBgGradientButton();
    createFontChooser();
    createCursorCombo();
    createChangeTextControl( parent );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new GridLayout() );
    int style = getStyle();
    left = new CLabel( parent, style );
    left.setText( "Some Text" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image image1 = Graphics.getImage( IMAGE1, classLoader );
    left.setImage( image1 );
    center = new CLabel( parent, style );
    center.setText( "First Line\nSecond Line\n" );
    right = new CLabel( parent, style );
    right.setData( RWT.MARKUP_ENABLED, markupEnabled ? Boolean.TRUE : null );
    right.setText( markup );
    Image image2 = Graphics.getImage( IMAGE2, classLoader );
    right.setImage( image2 );
    registerControl( left );
    registerControl( center );
    registerControl( right );
  }

  private Button createMarkupButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Enable Markup" );
    button.setSelection( markupEnabled );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        markupEnabled = button.getSelection();
        createNew();
      }
    } );
    return button;
  }

  private void createChangeTextControl( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Change text" );
    final Text text = new Text( composite, SWT.BORDER );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        right.setText( text.getText() );
        text.setText( "" );
        right.pack();
      }
    } );
  }

  @Override
  protected Button createBgGradientButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Background Gradient" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showBgGradient = button.getSelection();
        updateBgGradient();
      }
    } );
    return button;
  }

  private void updateBgGradient() {
    if( showBgGradient ) {
      Color[] gradientColors = new Color[] {
        BGG_COLOR_BLUE,
        BGG_COLOR_GREEN,
        BGG_COLOR_BLUE,
        BGG_COLOR_GREEN,
        BGG_COLOR_BLUE
      };
      int[] percents = new int[] { 25, 50, 75, 100 };
      left.setBackground( gradientColors, percents );
      center.setBackground( gradientColors, percents, true );
      right.setBackground( gradientColors, percents );
    } else {
      left.setBackground( null, null );
      center.setBackground( null, null );
      right.setBackground( null, null );
    }
  }
}
