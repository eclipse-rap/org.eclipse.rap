/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.jface.fieldassist.*;
import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class ButtonExamplePage implements IExamplePage {

  protected Image errorImage;
  protected Image warningImage;

  public void createControl( Composite parent ) {
    createImages();
    parent.setLayout( ExampleUtil.createMainLayout( 2 ) );
    createPushButtons( parent );
    createRadioAndCheckButtons( parent );
  }

  private void createImages() {
    errorImage = getDecorationImage( FieldDecorationRegistry.DEC_ERROR );
    warningImage = getDecorationImage( FieldDecorationRegistry.DEC_WARNING );
  }

  private void createPushButtons( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    ExampleUtil.createHeading( composite, "Push and Toggle Buttons", 2 );
    composite.setLayout( ExampleUtil.createGridLayout( 2, false, true, true ) );
    composite.setLayoutData( ExampleUtil.createHorzFillData() );

    Composite compositeL1 = new Composite( composite, SWT.NONE );
    compositeL1.setLayout( createRowLayout( SWT.HORIZONTAL ) );
    Composite compositeR = new Composite( composite, SWT.NONE );
    compositeR.setLayout( new FillLayout() );
    Composite compositeL2 = new Composite( composite, SWT.NONE );
    compositeL2.setLayout( createRowLayout( SWT.HORIZONTAL ) );
    GridData rData = new GridData( SWT.TOP, SWT.RIGHT, true, false );
    rData.verticalSpan = 2;
    compositeR.setLayoutData( rData );

    Button button = new Button( compositeL1, SWT.PUSH );
    button.setText( "Cancel" );
    Button button1 = new Button( compositeL1, SWT.PUSH );
    button1.setText( "Add" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image imgAdd = Graphics.getImage( "resources/add_obj.png", classLoader );
    button1.setImage( imgAdd );
    Button button2 = new Button( compositeL1, SWT.PUSH );
    button2.setText( "Delete" );
    Image imgDelete = Graphics.getImage( "resources/delete_obj.png", classLoader );
    button2.setImage( imgDelete );

    Button button3 = new Button( compositeR, SWT.PUSH );
    Image imageDownload = Graphics.getImage( "resources/go-bottom.png", classLoader );
    button3.setImage( imageDownload );
    button3.setToolTipText( "Download" );

    Button toggle1 = new Button( compositeL2, SWT.TOGGLE );
    Image imgSynced = Graphics.getImage( "resources/synced.png", classLoader );
    toggle1.setImage( imgSynced );
    toggle1.setToolTipText( "Keep in sync" );
    final Button toggle2 = new Button( compositeL2, SWT.TOGGLE | SWT.LEFT );
    toggle2.setText( "Unlocked" );
    final Image imgLocked = Graphics.getImage( "resources/lockedstate.png", classLoader );
    final Image imgUnlocked = Graphics.getImage( "resources/unlockedstate.png", classLoader );
    toggle2.setImage( imgUnlocked );
    toggle2.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        boolean selected = toggle2.getSelection();
        toggle2.setText( selected ? "Locked" : "Unlocked" );
        toggle2.setImage( selected ? imgLocked : imgUnlocked );
      }
    } );
  }

  private void createRadioAndCheckButtons( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 2, false, true, true ) );
    composite.setLayoutData( ExampleUtil.createHorzFillData() );
    ExampleUtil.createHeading( composite, "Checkboxes and Radiobuttons", 2 );
    // Radio buttons
    Composite radioComp = new Composite( composite, SWT.NONE );
    radioComp.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    RowLayout radioLayout = createRowLayout( SWT.VERTICAL );
    radioComp.setLayout( radioLayout );
    final Button radio1 = new Button( radioComp, SWT.RADIO );
    radio1.setText( "Salami" );
    radio1.setSelection( true );
    final Button radio2 = new Button( radioComp, SWT.RADIO );
    radio2.setText( "Funghi" );
    final Button radio3 = new Button( radioComp, SWT.RADIO );
    radio3.setText( "Calzone" );
    // Check boxes
    Composite checkComp = new Composite( composite, SWT.NONE );
    checkComp.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    RowLayout checkLayout = createRowLayout( SWT.VERTICAL );
    checkComp.setLayout( checkLayout );
    Button check1 = new Button( checkComp, SWT.CHECK );
    check1.setText( "Extra Cheese" );
    Button check2 = new Button( checkComp, SWT.CHECK );
    check2.setText( "Extra Hot" );
    Button check3 = new Button( checkComp, SWT.CHECK );
    check3.setText( "King Size" );
    check3.setSelection( true );
  }

  private static RowLayout createRowLayout( int style ) {
    RowLayout layout = new RowLayout( style );
    layout.marginTop = 0;
    layout.marginLeft = 0;
    layout.marginRight = 0;
    layout.marginBottom = 0;
    layout.spacing = 10;
    layout.fill = true;
    layout.wrap = false;
    return layout;
  }

  private static Image getDecorationImage( String id ) {
    FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
    FieldDecoration decoration = registry.getFieldDecoration( id );
    return decoration.getImage();
  }
}
