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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class LabelTab extends ExampleTab {

  private Label varSizeLabel;
  private Label fixedSizeLabel;
  private final Image image1;
  private final Image image2;
  private final String text1;
  private final String text2;
  private Image labelImage;
  private String labelText;

  public LabelTab( final CTabFolder topFolder ) {
    super( topFolder, "Label" );
    ClassLoader classLoader = getClass().getClassLoader();
    image1 = Graphics.getImage( "resources/button-image.gif", classLoader );
    image2 = Graphics.getImage( "resources/newfile_wiz.gif", classLoader );
    text1 = "Some Text";
    text2 = "Some Other Text";
    labelImage = null;
    labelText = "A Label with text";
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "SEPARATOR", SWT.SEPARATOR );
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
    createStyleButton( "VERTICAL", SWT.VERTICAL );
    createStyleButton( "SHADOW_IN", SWT.SHADOW_IN );
    createStyleButton( "SHADOW_OUT", SWT.SHADOW_OUT );
    createStyleButton( "SHADOW_NONE", SWT.SHADOW_NONE );
    createStyleButton( "LEFT", SWT.LEFT );
    createStyleButton( "CENTER", SWT.CENTER );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createStyleButton( "WRAP", SWT.WRAP );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
    createChangeTextControl( parent );
    createChangeToolTipControl( parent );
  }

  protected void createExampleControls( final Composite parent ) {
    int style = getStyle();
    RowLayout rowLayout = new RowLayout( SWT.VERTICAL );
    parent.setLayout( rowLayout );
    fixedSizeLabel = new Label( parent, style );
    fixedSizeLabel.setText(   "Fixed size Label with some very long text\n"
                              + "and another line" );
    parent.addControlListener( new ControlAdapter() {
      public void controlResized(ControlEvent e) {
        Point size = fixedSizeLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT );
        fixedSizeLabel.setLayoutData( new RowData( size.x, size.y * 2) );
      }
    } );
    fixedSizeLabel.setLayoutData( new RowData( 100, 100 ) );
    new Label( parent, SWT.NONE );
    varSizeLabel = new Label( parent, style );
    registerControl( varSizeLabel );
    registerControl( fixedSizeLabel );


    Composite buttons = new Composite( parent, SWT.NONE );
    buttons.setLayout( new FillLayout() );
    Button text1Button = new Button( buttons, SWT.PUSH );
    text1Button.setText( "Text 1" );
    text1Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelText = text1;
        labelImage = null;
        updateLabel( varSizeLabel );
      }
    } );
    Button text2Button = new Button( buttons, SWT.PUSH );
    text2Button.setText( "Text 2" );
    text2Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelText = text2;
        labelImage = null;
        updateLabel( varSizeLabel );
      }
    } );
    Button image1Button = new Button( buttons, SWT.PUSH );
    image1Button.setText( "Image 1" );
    image1Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelImage = image1;
        updateLabel( varSizeLabel );
      }
    } );
    Button image2Button = new Button( buttons, SWT.PUSH );
    image2Button.setText( "Image 2" );
    image2Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelImage = image2;
        updateLabel( varSizeLabel );
      }
    } );
    updateLabel( varSizeLabel );
  }

  private void createChangeTextControl( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Change text" );
    final Text text = new Text( composite, SWT.BORDER );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        varSizeLabel.setText( text.getText() );
        text.setText( "" );
        varSizeLabel.pack();
      }
    } );
  }

  private void createChangeToolTipControl( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Change tooltip" );
    final Text text = new Text( composite, SWT.BORDER );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        varSizeLabel.setToolTipText( text.getText() );
      }
    } );
  }

  private void updateLabel( final Label label ) {
    if( labelImage != null ) {
      label.setImage( labelImage );
    } else {
      label.setText( labelText );
    }
    label.pack();
  }
}
