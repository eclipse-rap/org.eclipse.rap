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
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Simple Form" );
    group.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 20 ) );
    group.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );

    Composite formComp = new Composite( group, SWT.NONE );
    formComp.setLayout( new GridLayout( 2, false ) );

    new Label( formComp, SWT.NONE ).setText( "First Name:" );
    final Text firstNameText = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    GridData firstGridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    firstGridData.minimumWidth = 250;
    firstNameText.setLayoutData( firstGridData );

    new Label( formComp, SWT.NONE ).setText( "Last Name:" );
    final Text lastNameText = new Text( formComp, SWT.SINGLE | SWT.BORDER );
    lastNameText.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );

    new Label( formComp, SWT.NONE ).setText( "Passphrase:" );
    final Text passwordText
      = new Text( formComp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER );
    passwordText.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    passwordText.setText( "Password" );

    new Label( formComp, SWT.NONE ).setText( "Age:" );
    final Spinner spinner = new Spinner( formComp, SWT.BORDER );
    spinner.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    spinner.setSelection( 23 );

    new Label( formComp, SWT.NONE ).setText( "Country:" );
    final Combo combo = new Combo( formComp, SWT.BORDER );
    combo.setItems( new String[] { "Germany", "Canada", "USA", "Bulgaria" } );
    combo.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    combo.select( 0 );

    new Label( formComp, SWT.NONE ).setText( "Class:" );
    final Combo combo2 = new Combo( formComp, SWT.READ_ONLY | SWT.BORDER );
    combo2.setItems( new String[] { "Business", "Economy", "Economy Plus" } );
    combo2.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    combo2.select( 0 );

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
        combo.setEnabled( editable );
        combo2.setEnabled( editable );
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
