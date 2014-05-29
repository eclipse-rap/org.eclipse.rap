/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.pages.internal.Countries;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.widgets.DropDown;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


public class TextInputExamplePage implements IExamplePage {

  protected Image errorImage;
  protected Image warningImage;
  private String[] currentTexts = Countries.VALUES;
  private String userText = "";

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
    final Text countryDropDown = createCountryDropDown( composite );
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
        countryDropDown.setEnabled( editable );
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

  private Text createCountryDropDown( Composite formComp ) {
    new Label( formComp, SWT.NONE ).setText( "Country:" );
    Text text = new Text( formComp, SWT.BORDER );
    text.setLayoutData( ExampleUtil.createHorzFillData() );
    DropDown dropdown = new DropDown( text );
    dropdown.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    dropdown.setItems( format( currentTexts, "" ) );
    addModifyListener( text, dropdown );
    addSelectionListener( text, dropdown );
    addDefaultSelectionListener( text, dropdown );
    addFocusListener( text, dropdown );
    text.setText( "Germany" );
    return text;
  }

  private void addModifyListener( final Text text, final DropDown dropdown ) {
    text.addListener( SWT.Modify, new Listener() {
      public void handleEvent( Event event ) {
        if( !Boolean.TRUE.equals( text.getData( "selecting" ) ) ) {
          userText = text.getText();
          if( userText.length() == 0 ) {
            currentTexts = Countries.VALUES;
            dropdown.setItems( Countries.VALUES );
          } else {
            String searchStr = userText.toLowerCase();
            currentTexts = filter( Countries.VALUES, searchStr, 10 );
            dropdown.setItems( format( currentTexts, searchStr ) );
            if( currentTexts.length > 10 ) {
              dropdown.setSelectionIndex( -1 );
            } else if( currentTexts.length == 1 ) {
              dropdown.setSelectionIndex( 0 );
            }
          }
          dropdown.setVisible( true );
        }
      }
    } );
  }

  private void addFocusListener( final Text text, final DropDown dropdown ) {
    text.addFocusListener( new FocusListener() {
      public void focusGained( FocusEvent event ) {
        dropdown.setVisible( true );
      }
      public void focusLost( FocusEvent event ) {
        dropdown.setVisible( false );
        if( !Arrays.asList( Countries.VALUES ).contains( userText ) ) {
          currentTexts = Countries.VALUES;
          dropdown.setItems( Countries.VALUES );
          text.setData( "selecting", Boolean.TRUE );
          text.setText( "" );
          text.setData( "selecting", Boolean.FALSE );
        }
      }
    } );
  }

  private void addSelectionListener( final Text text, final DropDown dropdown ) {
    dropdown.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        if( event.index != -1 ) {
          text.setData( "selecting", Boolean.TRUE );
          text.setText( currentTexts[ event.index ] );
          text.setData( "selecting", Boolean.FALSE );
          text.selectAll();
        } else {
          text.setText( userText );
          text.setSelection( userText.length(), userText.length() );
          text.setFocus();
        }
      }
    } );
  }

  private void addDefaultSelectionListener( final Text text, final DropDown dropdown ) {
    dropdown.addListener( SWT.DefaultSelection, new Listener() {
      public void handleEvent( Event event ) {
        if( event.index != -1 ) {
          text.setText( currentTexts[ event.index ] );
          text.setSelection( event.text.length() );
          dropdown.setVisible( false );
        }
      }
    } );
  }

  private static String[] filter( String[] values, String text, int limit ) {
    List<String> result = new ArrayList<String>( limit );
    for( int i = 0; result.size() < limit && i < values.length; i++ ) {
      String item = values[ i ];
      if( item.toLowerCase().startsWith( text ) ) {
        result.add( item );
      }
    }
    return result.toArray( new String[ result.size() ] );
  }

  private static String[] format( String[] values, String text ) {
    String[] result = new String[ values.length ];
    for( int i = 0; i < values.length; i++ ) {
      String item = values[ i ];
      int length = text.length();
      result[ i ] = "<b>" + item.substring( 0, length ) + "</b>" + item.substring( length );
    }
    return result;
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
    DateTime dateTime = new DateTime( formComp, SWT.DATE | SWT.BORDER );
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
