/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.widgets.*;

public class LabelTab extends ExampleTab {

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
  }

  protected void createExampleControls( final Composite top ) {
    int style = getStyle();
    final Label label1 = new Label( top, style );
    label1.setLocation( 10, 10 );
    updateLabel2( label1 );
    final Label label2 = new Label( top, style );
    label2.setText( "Fixed size Label with some very long text\n"
                    + "and another line" );
    label2.setLocation( 150, 10 );
    label2.setSize( 100, 100 );
    registerControl( label1 );
    registerControl( label2 );
    
    Button text1Button = new Button( top, RWT.PUSH );
    text1Button.setText( "Text 1" );
    text1Button.setBounds( 10, 50, 80, 20 );
    text1Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelText = text1;
        labelImage = null;
        updateLabel2( label1 );
      }
    } );
    Button text2Button = new Button( top, RWT.PUSH );
    text2Button.setText( "Text 2" );
    text2Button.setBounds( 10, 75, 80, 20 );
    text2Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelText = text2;
        labelImage = null;
        updateLabel2( label1 );
      }
    } );
    Button image1Button = new Button( top, RWT.PUSH );
    image1Button.setText( "Image 1" );
    image1Button.setBounds( 10, 100, 80, 20 );
    image1Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelImage = image1;
        updateLabel2( label1 );
      }
    } );
    Button image2Button = new Button( top, RWT.PUSH );
    image2Button.setText( "Image 2" );
    image2Button.setBounds( 10, 125, 80, 20 );
    image2Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        labelImage = image2;
        updateLabel2( label1 );
      }
    } );
  }
  
  private void updateLabel2( Label label ) {
    if( labelImage != null ) {
      label.setImage( labelImage );
    } else {
      label.setText( labelText );
    }
    label.pack();
  }
}
