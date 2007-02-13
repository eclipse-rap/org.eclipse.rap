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

  private Image image1;
  private Image image2;
  private String text1 = "Some Text";
  private String text2 = "Some Other Text";

  public LabelTab( final TabFolder parent ) {
    super( parent, "Label" );
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
    label1.setText( "Label One" );
    label1.setLocation( 10, 10 );
    label1.pack();
    final Label label2 = new Label( top, style );
    label2.setText( "Label Two" );
    label2.setLocation( 110, 40 );
    label2.pack();
    final Label label3 = new Label( top, style );
    label3.setText( "Fixed Size Label with some very long text\nand another line" );
    label3.setLocation( 210, 70 );
    label3.setSize( 100, 100 );
    registerControl( label1 );
    registerControl( label2 );
    registerControl( label3 );
    
    Button text1Button = new Button( top, RWT.PUSH );
    text1Button.setText( "Text 1" );
    text1Button.setBounds( 100, 110, 80, 20 );
    text1Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        label2.setText( text1 );
        label2.pack();
        System.out.println( "Text:  " + label2.getText());
        System.out.println( "Image: " + label2.getImage());
      }
    } );
    Button text2Button = new Button( top, RWT.PUSH );
    text2Button.setText( "Text 2" );
    text2Button.setBounds( 100, 135, 80, 20 );
    text2Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        label2.setText( text2 );
        label2.pack();
        System.out.println( "Text:  " + label2.getText());
        System.out.println( "Image: " + label2.getImage());
      }
    } );
    Button image1Button = new Button( top, RWT.PUSH );
    image1Button.setText( "Image 1" );
    image1Button.setBounds( 100, 160, 80, 20 );
    image1Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        createImages();
        label2.setImage( image1 );
        label2.pack();
        System.out.println( "Text:  " + label2.getText());
        System.out.println( "Image: " + label2.getImage());
      }
    } );
    Button image2Button = new Button( top, RWT.PUSH );
    image2Button.setText( "Image 2" );
    image2Button.setBounds( 100, 185, 80, 20 );
    image2Button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        createImages();
        label2.setImage( image2 );
        label2.pack();
        System.out.println( "Text:  " + label2.getText());
        System.out.println( "Image: " + label2.getImage());
      }
    } );
  }

  private void createImages() {
    if( image1 == null ) {
      ClassLoader classLoader = getClass().getClassLoader();
      image1 = Image.find( "resources/button-image.gif", classLoader );
    }
    if( image2 == null ) {
      ClassLoader classLoader = getClass().getClassLoader();
      image2 = Image.find( "resources/newfile_wiz.gif", classLoader );
    }
  }

}
