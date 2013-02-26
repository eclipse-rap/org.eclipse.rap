/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.Infobox;
import org.eclipse.rap.examples.pages.internal.ImageUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


public class ButtonExamplePage implements IExamplePage {

  protected Image errorImage;
  protected Image warningImage;

  public void createControl( Composite parent ) {
    createImages();
    parent.setLayout( ExampleUtil.createMainLayout( 3, 30 ) );
    createPushButtons( parent );
    createRadioAndCheckButtons( parent );
    Infobox infobox = new Infobox( parent );
    infobox.addParagraph( "Use the Button widget to create push buttons, toggle buttons, checkboxes and radiobuttons." );
    infobox.addParagraph( "Push and toggle buttons also support images." );

  }

  private void createImages() {
    errorImage = getDecorationImage( FieldDecorationRegistry.DEC_ERROR );
    warningImage = getDecorationImage( FieldDecorationRegistry.DEC_WARNING );
  }

  private void createPushButtons( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 3, false, true, true ) );
    composite.setLayoutData( ExampleUtil.createHorzFillData() );
    ExampleUtil.createHeading( composite, "Push and Toggle Buttons", 3 );

    Button button = new Button( composite, SWT.PUSH );
    button.setLayoutData( ExampleUtil.createHorzFillData() );
    button.setText( "Cancel" );
    Button button1 = new Button( composite, SWT.PUSH );
    button1.setText( "Add" );
    button1.setLayoutData( ExampleUtil.createHorzFillData() );
    Display display = parent.getDisplay();
    Image imgAdd = ImageUtil.getImage( display, "add_obj.png" );
    button1.setImage( imgAdd );
    Button button2 = new Button( composite, SWT.PUSH );
    button2.setLayoutData( ExampleUtil.createHorzFillData() );
    button2.setText( "Delete" );
    Image imgDelete = ImageUtil.getImage( display, "delete_obj.png" );
    button2.setImage( imgDelete );

    Button toggle1 = new Button( composite, SWT.TOGGLE );
    toggle1.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    Image imgSynced = ImageUtil.getImage( display, "synced.png" );
    toggle1.setImage( imgSynced );
    toggle1.setToolTipText( "Keep in sync" );
    final Button toggle2 = new Button( composite, SWT.TOGGLE | SWT.LEFT );
    toggle2.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, true ) );
    toggle2.setText( "Unlocked" );
    final Image imgLocked = ImageUtil.getImage( display, "lockedstate.png" );
    final Image imgUnlocked = ImageUtil.getImage( display, "unlockedstate.png" );
    toggle2.setImage( imgUnlocked );
    toggle2.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        boolean selected = toggle2.getSelection();
        toggle2.setText( selected ? "Locked" : "Unlocked" );
        toggle2.setImage( selected ? imgLocked : imgUnlocked );
      }
    } );

    Button button3 = new Button( composite, SWT.PUSH );
    button3.setLayoutData( ExampleUtil.createFillData() );
    Image imageDownload = ImageUtil.getImage( display, "go-bottom.png" );
    button3.setImage( imageDownload );
    button3.setToolTipText( "Download" );
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
