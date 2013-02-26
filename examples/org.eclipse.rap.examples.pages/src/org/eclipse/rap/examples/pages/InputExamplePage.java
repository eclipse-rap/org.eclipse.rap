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

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.pages.internal.ImageUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


public class InputExamplePage implements IExamplePage {

  protected Image errorImage;
  protected Image warningImage;

  public void createControl( Composite parent ) {
    createImages();
    parent.setLayout( ExampleUtil.createMainLayout( 2 ) );
    Composite upperWestSide = createPart( parent );
    Composite upperEastSide = createPart( parent );
    Composite lowerWestSide = createPart( parent );
    Composite lowerEastSide = createPart( parent );
    createInputForm( upperWestSide );
    createControlDecoratorsForm( upperEastSide );
    createMultiline( upperEastSide );
    createPushButtons( lowerWestSide );
    createRadioAndCheckButtons( lowerEastSide );
  }

  private void createImages() {
    errorImage = getDecorationImage( FieldDecorationRegistry.DEC_ERROR );
    warningImage = getDecorationImage( FieldDecorationRegistry.DEC_WARNING );
  }

  private Composite createPart( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayoutWithoutMargin( 1, false ) );
    composite.setLayoutData( ExampleUtil.createHorzFillData() );
    return composite;
  }

  private void createInputForm( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayoutData( ExampleUtil.createHorzFillData() );
    composite.setLayout( ExampleUtil.createGridLayout( 2, false, true, true ) );
    ExampleUtil.createHeading( composite, "Simple Input Widgets", 2 );
    final Text firstNameText = createFirstNameField( composite );
    final Text lastNameText = createLastNameField( composite );
    final Text passwordText = createPasswordField( composite );
    final Spinner spinner = createSpinner( composite );
    final Combo countryCombo = createCountryCombo( composite );
    final Combo classCombo = createReadOnlyCombo( composite );
    final DateTime dateTime = createDateField( composite );
    final Button enabledCheckbox = new Button( composite, SWT.CHECK );
    enabledCheckbox.setText( "Enabled" );
    enabledCheckbox.setSelection( true );
    enabledCheckbox.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        boolean editable = enabledCheckbox.getSelection();
        firstNameText.setEnabled( editable );
        lastNameText.setEnabled( editable );
        passwordText.setEnabled( editable );
        spinner.setEnabled( editable );
        countryCombo.setEnabled( editable );
        classCombo.setEnabled( editable );
        dateTime.setEnabled( editable );
      }
    } );
  }

  private void createControlDecoratorsForm( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayoutData( ExampleUtil.createHorzFillData() );
    composite.setLayout( ExampleUtil.createGridLayout( 2, false, true, true ) );
    ExampleUtil.createHeading( composite, "Control Decorators", 2 );
    createVerifiedText( composite );
    createMandatoryText( composite );
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

  private Combo createReadOnlyCombo( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Class:" );
    final Combo classCombo = new Combo( formComp, SWT.READ_ONLY | SWT.BORDER );
    String[] classes = new String[] { "Business", "Economy", "Economy Plus" };
    classCombo.setItems( classes );
    GridData gridData = ExampleUtil.createHorzFillData();
    classCombo.setLayoutData( gridData );
    classCombo.select( 0 );
    return classCombo;
  }

  private DateTime createDateField( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Date:" );
    int dateTimeStyle = SWT.READ_ONLY | SWT.BORDER;
    final DateTime dateTime = new DateTime( formComp, dateTimeStyle );
    return dateTime;
  }

  private void createMultiline( final Composite parent ) {
    Composite multiComp = new Composite( parent, SWT.NONE );
    ExampleUtil.createHeading( multiComp, "Multiline Texts", 2 );
    multiComp.setLayoutData( ExampleUtil.createHorzFillData() );
    multiComp.setLayout( ExampleUtil.createGridLayout( 1, false, true, true ) );
    String text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. ";
    text = text + text;
    // left
    new Label( multiComp, SWT.NONE ).setText( "This text box wraps:" );
    int wrapStyle = SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER;
    Text wrapText = new Text( multiComp, wrapStyle );
    wrapText.setText( text );
    GridData wrapTextData = ExampleUtil.createFillData();
    wrapTextData.minimumHeight = 50;
    wrapText.setLayoutData( wrapTextData );
    // right
    new Label( multiComp, SWT.NONE ).setText( "And this one doesn't:" );
    int nowrapStyle = SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
    Text nowrapText = new Text( multiComp, nowrapStyle );
    nowrapText.setText( text );
    GridData nowrapData = ExampleUtil.createFillData();
    nowrapData.minimumHeight = 50;
    nowrapText.setLayoutData( nowrapData );
  }

  private void createPushButtons( final Composite parent ) {
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
    Display display = parent.getDisplay();
    Image imgAdd = ImageUtil.getImage( display, "add_obj.gif" );
    button1.setImage( imgAdd );
    Button button2 = new Button( compositeL1, SWT.PUSH );
    button2.setText( "Delete" );
    Image imgDelete = ImageUtil.getImage( display, "delete_obj.gif" );
    button2.setImage( imgDelete );

    Button button3 = new Button( compositeR, SWT.PUSH );
    Image imageDownload = ImageUtil.getImage( display, "go-bottom.png" );
    button3.setImage( imageDownload );
    button3.setToolTipText( "Download" );

    Button toggle1 = new Button( compositeL2, SWT.TOGGLE );
    Image imgSynced = ImageUtil.getImage( display, "synced.gif" );
    toggle1.setImage( imgSynced );
    toggle1.setToolTipText( "Keep in sync" );
    final Button toggle2 = new Button( compositeL2, SWT.TOGGLE | SWT.LEFT );
    toggle2.setText( "Unlocked" );
    final Image imgLocked = ImageUtil.getImage( display, "lockedstate.gif" );
    final Image imgUnlocked = ImageUtil.getImage( display, "unlockedstate.gif" );
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

  private static boolean isNumbers( String content ) {
    int length = content.length();
    for( int i = 0; i < length; i++ ) {
      char ch = content.charAt( i );
      if( !Character.isDigit( ch ) ) {
        return false;
      }
    }
    return true;
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
