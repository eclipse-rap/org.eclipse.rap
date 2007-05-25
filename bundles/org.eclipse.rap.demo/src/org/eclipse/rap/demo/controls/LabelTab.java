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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
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

  public LabelTab( final TabFolder parent ) {
    super( parent, "Label" );
    ClassLoader classLoader = getClass().getClassLoader();
    image1 = Image.find( "resources/button-image.gif", classLoader );
    image2 = Image.find( "resources/newfile_wiz.gif", classLoader );
    text1 = "Some Text";
    text2 = "Some Other Text";
    labelImage = null;
    labelText = "A Label";
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "SEPARATOR" );
    createStyleButton( "HORIZONTAL" );
    createStyleButton( "VERTICAL" );
    createStyleButton( "SHADOW_IN" );
    createStyleButton( "SHADOW_OUT" );
    createStyleButton( "SHADOW_NONE" );
    createStyleButton( "LEFT" );
    createStyleButton( "CENTER" );
    createStyleButton( "RIGHT" );
    createStyleButton( "WRAP" );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createChangeLabelControl();
  }

  protected void createExampleControls( final Composite parent ) {
    int style = getStyle();
    varSizeLabel = new Label( parent, style );
    varSizeLabel.setLocation( 10, 10 );
    updateLabel( varSizeLabel );
    fixedSizeLabel = new Label( parent, style );
    fixedSizeLabel.setText(   "Fixed size Label with some very long text\n"
                    + "and another line" );
    fixedSizeLabel.setLocation( 150, 10 );
    fixedSizeLabel.setSize( 100, 100 );
    registerControl( varSizeLabel );
    registerControl( fixedSizeLabel );
    
    Button text1Button = new Button( parent, SWT.PUSH );
    text1Button.setText( "Text 1" );
    text1Button.setBounds( 10, 50, 80, 20 );
    text1Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelText = text1;
        labelImage = null;
        updateLabel( varSizeLabel );
      }
    } );
    Button text2Button = new Button( parent, SWT.PUSH );
    text2Button.setText( "Text 2" );
    text2Button.setBounds( 10, 75, 80, 20 );
    text2Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelText = text2;
        labelImage = null;
        updateLabel( varSizeLabel );
      }
    } );
    Button image1Button = new Button( parent, SWT.PUSH );
    image1Button.setText( "Image 1" );
    image1Button.setBounds( 10, 100, 80, 20 );
    image1Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelImage = image1;
        updateLabel( varSizeLabel );
      }
    } );
    Button image2Button = new Button( parent, SWT.PUSH );
    image2Button.setText( "Image 2" );
    image2Button.setBounds( 10, 125, 80, 20 );
    image2Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelImage = image2;
        updateLabel( varSizeLabel );
      }
    } );
  }
  
  private void createChangeLabelControl() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Change text" );
    final Text text = new Text( composite, SWT.BORDER );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        varSizeLabel.setText( text.getText() );
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
