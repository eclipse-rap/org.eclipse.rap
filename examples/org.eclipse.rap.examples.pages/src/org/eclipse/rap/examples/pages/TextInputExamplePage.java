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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


public class TextInputExamplePage implements IExamplePage {

  protected Image errorImage;
  protected Image warningImage;

  public void createControl( Composite parent ) {
    createImages();
    parent.setLayout( ExampleUtil.createMainLayout( 2 ) );
    Composite leftComp = createPart( parent );
    Composite rightComp = createPart( parent );
    createInputForm( leftComp );
    createControlDecoratorsForm( rightComp );
    createMultiline( rightComp );
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
    ExampleUtil.createHeading( composite, "Input validation with visual feedback", 2 );
    createVerifiedText( composite );
    createMandatoryText( composite );
  }

  private void createVerifiedText( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Digits Only:" );
    final Text text = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    GridData gridData = ExampleUtil.createHorzFillData();
    gridData.minimumWidth = 250;
    text.setLayoutData( gridData );
    final ControlDecoration decoration = new ControlDecoration( text, SWT.TOP | SWT.LEFT );
    decoration.setImage( errorImage );
    text.setText( "4711 abcd" );
    text.setBackground( new Color( text.getDisplay(), 250, 200, 150 ) );
    final String errorDescription = "validation failed: only numbers allowed for this textfield";
    decoration.setDescriptionText( errorDescription );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        String content = text.getText();
        if( !isNumbers( content ) ) {
          decoration.setDescriptionText( errorDescription );
          text.setBackground( new Color( text.getDisplay(), 250, 200, 150 ) );
          decoration.show();
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
    GridData gridData = ExampleUtil.createHorzFillData();
    gridData.minimumWidth = 250;
    text.setLayoutData( gridData );
    final ControlDecoration decoration = new ControlDecoration( text, SWT.TOP | SWT.LEFT );
    decoration.setImage( warningImage );
    final String errorDescription = "this field cannot be empty. Please enter mandatory content.";
    decoration.setDescriptionText( errorDescription );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        String content = text.getText();
        if( content.trim().length() == 0 ) {
          decoration.show();
          decoration.setDescriptionText( errorDescription );
        } else {
          text.setBackground( null );
          decoration.hide();
        }
      }
    } );
  }

  private Text createFirstNameField( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "First Name:" );
    Text firstNameText = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    GridData gridData = ExampleUtil.createHorzFillData();
    gridData.minimumWidth = 250;
    firstNameText.setLayoutData( gridData );
    return firstNameText;
  }

  private Text createLastNameField( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Last Name:" );
    Text lastNameText = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    lastNameText.setLayoutData( ExampleUtil.createHorzFillData() );
    return lastNameText;
  }

  private Text createPasswordField( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Passphrase:" );
    Text passwordText = new Text( formComp, SWT.PASSWORD | SWT.BORDER );
    passwordText.setLayoutData( ExampleUtil.createHorzFillData() );
    passwordText.setText( "Password" );
    return passwordText;
  }

  private Spinner createSpinner( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Age:" );
    Spinner spinner = new Spinner( formComp, SWT.BORDER );
    spinner.setLayoutData( ExampleUtil.createHorzFillData() );
    spinner.setSelection( 23 );
    return spinner;
  }

  private Combo createCountryCombo( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Country:" );
    Combo combo = new Combo( formComp, SWT.BORDER );
    String[] countries = new String[] { "Germany", "Canada", "USA", "Bulgaria" };
    combo.setItems( countries );
    combo.setLayoutData( ExampleUtil.createHorzFillData() );
    combo.select( 0 );
    return combo;
  }

  private Combo createReadOnlyCombo( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Class:" );
    Combo classCombo = new Combo( formComp, SWT.READ_ONLY | SWT.BORDER );
    String[] classes = new String[] { "Business", "Economy", "Economy Plus" };
    classCombo.setItems( classes );
    classCombo.setLayoutData( ExampleUtil.createHorzFillData() );
    classCombo.select( 0 );
    return classCombo;
  }

  private DateTime createDateField( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Date:" );
    int dateTimeStyle = SWT.READ_ONLY | SWT.BORDER;
    DateTime dateTime = new DateTime( formComp, dateTimeStyle );
    dateTime.setLayoutData( ExampleUtil.createHorzFillData() );
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
    wrapTextData.minimumHeight = 70;
    wrapText.setLayoutData( wrapTextData );
    // right
    new Label( multiComp, SWT.NONE ).setText( "And this one doesn't:" );
    int nowrapStyle = SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
    Text nowrapText = new Text( multiComp, nowrapStyle );
    nowrapText.setText( text );
    GridData nowrapData = ExampleUtil.createFillData();
    nowrapData.minimumHeight = 70;
    nowrapText.setLayoutData( nowrapData );
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

  private static Image getDecorationImage( String id ) {
    FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
    FieldDecoration decoration = registry.getFieldDecoration( id );
    return decoration.getImage();
  }
}
