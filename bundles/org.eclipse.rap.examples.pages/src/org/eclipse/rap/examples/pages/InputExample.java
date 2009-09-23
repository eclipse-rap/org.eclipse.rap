/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class InputExample implements IExamplePage {

  public void createControl( final Composite parent ) {
    parent.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 20 ) );
    createForm( parent );
    createMultiline( parent );
  }

  private void createForm( final Composite parent ) {
    GridData gridData;
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Simple Form" );
    group.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 20 ) );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );

    Composite formComp = new Composite( group, SWT.NONE );
    formComp.setLayout( new GridLayout( 2, false ) );

    new Label( formComp, SWT.NONE ).setText( "First Name:" );
    final Text firstNameText = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    gridData.minimumWidth = 250;
    firstNameText.setLayoutData( gridData );

    new Label( formComp, SWT.NONE ).setText( "Last Name:" );
    final Text lastNameText = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    lastNameText.setLayoutData( gridData );

    new Label( formComp, SWT.NONE ).setText( "Passphrase:" );
    final Text passwordText
      = new Text( formComp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER );
    gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    passwordText.setLayoutData( gridData );
    passwordText.setText( "Password" );

    new Label( formComp, SWT.NONE ).setText( "Age:" );
    final Spinner spinner = new Spinner( formComp, SWT.BORDER );
    gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    spinner.setLayoutData( gridData );
    spinner.setSelection( 23 );

    new Label( formComp, SWT.NONE ).setText( "Country:" );
    final Combo countryCombo = new Combo( formComp, SWT.BORDER );
    String[] countries
      = new String[] { "Germany", "Canada", "USA", "Bulgaria" };
    countryCombo.setItems( countries );
    gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    countryCombo.setLayoutData( gridData );
    countryCombo.select( 0 );

    new Label( formComp, SWT.NONE ).setText( "Class:" );
    final Combo classCombo = new Combo( formComp, SWT.READ_ONLY | SWT.BORDER );
    String[] classes = new String[] { "Business", "Economy", "Economy Plus" };
    classCombo.setItems( classes );
    gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    classCombo.setLayoutData( gridData );
    classCombo.select( 0 );

    new Label( formComp, SWT.NONE ).setText( "Date:" );
    int dateTimeStyle = SWT.READ_ONLY | SWT.BORDER;
    final DateTime dateTime = new DateTime( formComp, dateTimeStyle );

    new Label( formComp, SWT.NONE );
    final Button editableCheckbox = new Button( formComp, SWT.CHECK );
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

  private void createMultiline( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Multiline" );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    group.setLayout( ExampleUtil.createGridLayout( 2, false, 10, 20 ) );
    // left
    Composite leftComp = new Composite( group, SWT.NONE );
    leftComp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    leftComp.setLayout( new GridLayout() );
    new Label( leftComp, SWT.NONE ).setText( "This text box wraps:" );
    int wrapStyle = SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER;
    final Text wrapText = new Text( leftComp, wrapStyle );
    String text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.";
    wrapText.setText( text );
    GridData wrapTextData = new GridData( SWT.FILL, SWT.FILL, true, true );
    wrapTextData.minimumHeight = 70;
    wrapText.setLayoutData( wrapTextData );
    // right
    Composite rightComp = new Composite( group, SWT.NONE );
    rightComp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    rightComp.setLayout( new GridLayout() );
    new Label( rightComp, SWT.NONE ).setText( "And this one on doesn't:" );
    int nowrapStyle = SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
    final Text nowrapText = new Text( rightComp, nowrapStyle );
    nowrapText.setText( text );
    GridData nowrapData = new GridData( SWT.FILL, SWT.FILL, true, true );
    nowrapData.minimumHeight = 70;
    nowrapText.setLayoutData( nowrapData );
  }
}
