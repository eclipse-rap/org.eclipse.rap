/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class InputExamplePage implements IExamplePage {

  protected Image errorImage;
  protected Image warningImage;

  public void createControl( Composite parent ) {
    errorImage = getDecorationImage( FieldDecorationRegistry.DEC_ERROR );
    warningImage = getDecorationImage( FieldDecorationRegistry.DEC_WARNING );
    parent.setLayout( ExampleUtil.createMainLayout( 2 ) );
    Composite leftComp = new Composite( parent, SWT.NONE );
    leftComp.setLayout( ExampleUtil.createColumnLayout() );
    leftComp.setLayoutData( ExampleUtil.createHorzFillData() );
    createInputForm( leftComp );
    createMultiline( leftComp );
    Composite rightComp = new Composite( parent, SWT.NONE );
    rightComp.setLayout( ExampleUtil.createColumnLayout() );
    rightComp.setLayoutData( ExampleUtil.createHorzFillData() );
    createControlDecoratorsForm( rightComp );
    createRadioAndCheckButtons( rightComp );
    createPushButtons( rightComp );
  }

  private void createInputForm( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( ExampleUtil.createHorzFillData() );
    group.setText( "Simple Input Widgets" );
    GridLayout gridLayout = ExampleUtil.createGridLayout( 2, false, 12, 15 );
    gridLayout.horizontalSpacing = 12;
    gridLayout.verticalSpacing = 8;
    group.setLayout( gridLayout );
    final Text firstNameText = createFirstNameField( group );
    final Text lastNameText = createLastNameField( group );
    final Text passwordText = createPasswordField( group );
    final Spinner spinner = createSpinner( group );
    final Combo countryCombo = createCountryCombo( group );
    final Combo classCombo = createCombo( group );
    final DateTime dateTime = createDateField( group );

    new Label( group, SWT.NONE );
    final Button editableCheckbox = new Button( group, SWT.CHECK );
    editableCheckbox.setText( "Editable" );
    editableCheckbox.setSelection( true );
    editableCheckbox.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        boolean editable = editableCheckbox.getSelection();
        firstNameText.setEditable( editable );
        lastNameText.setEditable( editable );
        passwordText.setEditable( editable );
        spinner.setEnabled( editable );
        countryCombo.setEnabled( editable );
        classCombo.setEnabled( editable );
        dateTime.setEnabled( editable );
      }
    } );
  }
  
  private void createControlDecoratorsForm( Composite group2 ) {
    Group formComp = new Group( group2, SWT.NONE );
    formComp.setText( "Control Decorators" );
    GridLayout gridLayout = ExampleUtil.createGridLayout( 2, false, 12, 15 );
    gridLayout.horizontalSpacing = 12;
    gridLayout.verticalSpacing = 8;
    formComp.setLayout( gridLayout );
    createVerifiedText( formComp );
    createMandatoryText( formComp );
  }

  private void createVerifiedText( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Digits Only:" );
    final Text text = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    GridData gridData1 = ExampleUtil.createHorzFillData();
    gridData1.minimumWidth = 250;
    text.setLayoutData( gridData1 );
    final ControlDecoration decoration = new ControlDecoration( text, SWT.TOP | SWT.LEFT );
    decoration.setImage( errorImage );
    decoration.hide();
    text.setText( "4711" );
    text.addModifyListener( new ModifyListener() {
      
      public void modifyText( ModifyEvent event ) {
        String content = text.getText();
        if( !isNumbers( content ) ) {
          text.setBackground( new Color( text.getDisplay(), 250, 200, 150 ) );
          decoration.show();
          decoration.setDescriptionText( "Illegal content: " + content );
        } else {
          text.setBackground( null );
          decoration.hide();
        }
      }
    } );
  }
  
  private void createMandatoryText( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Mandatory:" );
    final Text text = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    GridData gridData1 = ExampleUtil.createHorzFillData();
    gridData1.minimumWidth = 250;
    text.setLayoutData( gridData1 );
    final ControlDecoration decoration = new ControlDecoration( text, SWT.TOP | SWT.LEFT );
    decoration.setImage( warningImage );
    decoration.hide();
    final Color mandatoryColor = new Color( text.getDisplay(), 200, 200, 250 );
    text.setText( "foo" );
    text.addModifyListener( new ModifyListener() {
      
      public void modifyText( ModifyEvent event ) {
        String content = text.getText();
        if( content.trim().length() == 0 ) {
          text.setBackground( mandatoryColor );
          decoration.show();
          decoration.setDescriptionText( "This field is mandatory" );
        } else {
          text.setBackground( null );
          decoration.hide();
        }
      }
    } );
  }

  protected boolean isNumbers( String content ) {
    int length = content.length();
    for( int i = 0; i < length; i++ ) {
      char ch = content.charAt( i );
      if( !Character.isDigit( ch ) ) {
        return false;
      }
    }
    return true;
  }

  private DateTime createDateField( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Date:" );
    int dateTimeStyle = SWT.READ_ONLY | SWT.BORDER;
    final DateTime dateTime = new DateTime( formComp, dateTimeStyle );
    return dateTime;
  }

  private Text createFirstNameField( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "First Name:" );
    final Text firstNameText = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    GridData gridData1 = ExampleUtil.createHorzFillData();
    gridData1.minimumWidth = 250;
    firstNameText.setLayoutData( gridData1 );
    return firstNameText;
  }

  private Text createLastNameField( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Last Name:" );
    final Text lastNameText = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    GridData gridData2 = ExampleUtil.createHorzFillData();
    lastNameText.setLayoutData( gridData2 );
    return lastNameText;
  }

  private Text createPasswordField( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Passphrase:" );
    final Text passwordText = new Text( formComp, SWT.PASSWORD | SWT.BORDER );
    GridData gridData3 = ExampleUtil.createHorzFillData();
    passwordText.setLayoutData( gridData3 );
    passwordText.setText( "Password" );
    return passwordText;
  }

  private Spinner createSpinner( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Age:" );
    final Spinner spinner = new Spinner( formComp, SWT.BORDER );
    GridData gridData4 = ExampleUtil.createHorzFillData();
    spinner.setLayoutData( gridData4 );
    spinner.setSelection( 23 );
    return spinner;
  }

  private Combo createCountryCombo( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Country:" );
    final Combo combo = new Combo( formComp, SWT.BORDER );
    String[] countries = new String[] { "Germany", "Canada", "USA", "Bulgaria" };
    combo.setItems( countries );
    GridData gridData = ExampleUtil.createHorzFillData();
    combo.setLayoutData( gridData );
    combo.select( 0 );
    return combo;
  }

  private Combo createCombo( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Class:" );
    final Combo classCombo = new Combo( formComp, SWT.READ_ONLY | SWT.BORDER );
    String[] classes = new String[] { "Business", "Economy", "Economy Plus" };
    classCombo.setItems( classes );
    GridData gridData = ExampleUtil.createHorzFillData();
    classCombo.setLayoutData( gridData );
    classCombo.select( 0 );
    return classCombo;
  }

  private void createMultiline( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Multiline" );
    group.setLayoutData( ExampleUtil.createHorzFillData() );
    group.setLayout( ExampleUtil.createGridLayout( 1, false, 8, 12 ) );
    String text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. ";
    text = text + text;
    // left
    Composite wrapComp = new Composite( group, SWT.NONE );
    wrapComp.setLayoutData( new GridData( 350, 100 ) );
    wrapComp.setLayout( new GridLayout() );
    new Label( wrapComp, SWT.NONE ).setText( "This text box wraps:" );
    int wrapStyle = SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER;
    Text wrapText = new Text( wrapComp, wrapStyle );
    wrapText.setText( text );
    GridData wrapTextData = ExampleUtil.createFillData();
    wrapTextData.minimumHeight = 50;
    wrapText.setLayoutData( wrapTextData );
    // right
    Composite nowrapComp = new Composite( group, SWT.NONE );
    nowrapComp.setLayoutData( new GridData( 350, 100 ) );
    nowrapComp.setLayout( new GridLayout() );
    new Label( nowrapComp, SWT.NONE ).setText( "And this one on doesn't:" );
    int nowrapStyle = SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
    Text nowrapText = new Text( nowrapComp, nowrapStyle );
    nowrapText.setText( text );
    GridData nowrapData = ExampleUtil.createFillData();
    nowrapData.minimumHeight = 50;
    nowrapText.setLayoutData( nowrapData );
  }

  private static void createRadioAndCheckButtons( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Checkboxes and Radiobuttons" );
    GridLayout layout = new GridLayout( 2, true );
    layout.marginWidth = 10;
    layout.marginHeight = 10;
    layout.horizontalSpacing = 20;
    group.setLayout( layout );
    group.setLayoutData( ExampleUtil.createHorzFillData() );
    // Radio buttons
    Composite radioComp = new Composite( group, SWT.NONE );
    RowLayout radioLayout = new RowLayout( SWT.VERTICAL );
    radioLayout.marginWidth = 0;
    radioLayout.marginHeight = 0;
    radioComp.setLayout( radioLayout );
    final Button radio1 = new Button( radioComp, SWT.RADIO );
    radio1.setText( "Salami" );
    radio1.setSelection( true );
    final Button radio2 = new Button( radioComp, SWT.RADIO );
    radio2.setText( "Funghi" );
    final Button radio3 = new Button( radioComp, SWT.RADIO );
    radio3.setText( "Calzone" );
    // Check boxes
    Composite checkComp = new Composite( group, SWT.NONE );
    RowLayout checkLayout = new RowLayout( SWT.VERTICAL );
    checkLayout.marginWidth = 0;
    checkLayout.marginHeight = 0;
    checkComp.setLayout( checkLayout );
    Button check1 = new Button( checkComp, SWT.CHECK );
    check1.setText( "Extra Cheese" );
    Button check2 = new Button( checkComp, SWT.CHECK );
    check2.setText( "Extra Hot" );
    Button check3 = new Button( checkComp, SWT.CHECK );
    check3.setText( "King Size" );
    check3.setSelection( true );
  }

  private void createPushButtons( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Push and Toggle Buttons" );
    group.setLayout( ExampleUtil.createGridLayout( 2, false, 10, 10 ) );
    group.setLayoutData( ExampleUtil.createHorzFillData() );

    Composite compositeL1 = new Composite( group, SWT.NONE );
    Composite compositeR = new Composite( group, SWT.NONE );
    GridData rData = new GridData();
    rData.verticalSpan = 2;
    compositeR.setLayoutData( rData );
    Composite compositeL2 = new Composite( group, SWT.NONE );

    RowLayout layoutL1 = new RowLayout( SWT.HORIZONTAL );
    layoutL1.marginWidth = 10;
    layoutL1.marginHeight = 10;
    layoutL1.spacing = 10;
    layoutL1.center = true;
    compositeL1.setLayout( layoutL1 );

    RowLayout layoutL2 = new RowLayout( SWT.HORIZONTAL );
    layoutL2.marginWidth = 10;
    layoutL2.marginHeight = 10;
    layoutL2.spacing = 10;
    layoutL2.center = true;
    compositeL2.setLayout( layoutL2 );

    compositeR.setLayout( new FillLayout() );

    Button button = new Button( compositeL1, SWT.PUSH );
    button.setText( "Cancel" );
    Button button1 = new Button( compositeL1, SWT.PUSH );
    button1.setText( "Add" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image imgAdd = Graphics.getImage( "resources/add_obj.gif", classLoader );
    button1.setImage( imgAdd );
    Button button2 = new Button( compositeL1, SWT.PUSH );
    button2.setText( "Delete" );
    Image imgDelete = Graphics.getImage( "resources/delete_obj.gif", classLoader );
    button2.setImage( imgDelete );

    Button button3 = new Button( compositeR, SWT.PUSH );
    Image imageDownload = Graphics.getImage( "resources/go-bottom.png", classLoader );
    button3.setImage( imageDownload );
    button3.setToolTipText( "Download" );

    Button toggle1 = new Button( compositeL2, SWT.TOGGLE );
    Image imgSynced = Graphics.getImage( "resources/synced.gif", classLoader );
    toggle1.setImage( imgSynced );
    toggle1.setToolTipText( "Keep in sync" );
    final Button toggle2 = new Button( compositeL2, SWT.TOGGLE | SWT.LEFT );
    toggle2.setText( "Unlocked" );
    final Image imgLocked = Graphics.getImage( "resources/lockedstate.gif", classLoader );
    final Image imgUnlocked = Graphics.getImage( "resources/unlockedstate.gif", classLoader );
    toggle2.setImage( imgUnlocked );
    toggle2.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( SelectionEvent e ) {
        boolean selected = toggle2.getSelection();
        toggle2.setText( selected ? "Locked" : "Unlocked" );
        toggle2.setImage( selected ? imgLocked : imgUnlocked );
      }
    } );
  }

  private static Image getDecorationImage( String id ) {
    FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
    FieldDecoration decoration = registry.getFieldDecoration( id );
    return decoration.getImage();
  }
}
