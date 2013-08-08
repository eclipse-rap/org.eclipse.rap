/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.pages.internal.scripting.CustomBehaviors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class ValidationExamplePage implements IExamplePage {

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 2 ) );
    createLeftInputForm( parent );
    createRightInputForm( parent );
  }

  private void createLeftInputForm( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayoutData( ExampleUtil.createFillData() );
    composite.setLayout( ExampleUtil.createGridLayoutWithoutMargin( 1, false ) );
    createDigitsOnlyExample( composite );
    createUpperCaseExample( composite );
  }

  private void createRightInputForm( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayoutData( ExampleUtil.createFillData() );
    composite.setLayout( ExampleUtil.createGridLayoutWithoutMargin( 1, false ) );
    createDateExample( composite );
    createEMailExample( composite );
  }

  private void createDigitsOnlyExample( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false, true, false ) );
    composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    ExampleUtil.createHeading( composite, "Digits-only field", 1 );
    createDigitsOnlyServerExample( composite );
    createDigitsOnlyClientExample( composite );
    createDigitsOnlyClientEnforcementExample( composite );
  }

  private void createDigitsOnlyServerExample( Composite composite ) {
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Validation on the server (with latency):" );
    Text text = new Text( composite, SWT.BORDER );
    text.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    text.setText( "23" );
    text.setSelection( 2 );
    text.setFocus();
    text.addListener( SWT.Modify, new Listener() {
      public void handleEvent( Event event ) {
        Text widget = ( Text )event.widget;
        String text = widget.getText();
        String regexp = "^[0-9]*$";
        if( !text.matches( regexp ) ) {
          widget.setBackground( new Color( widget.getDisplay(), 255, 255, 128 ) );
          widget.setToolTipText( "Only digits allowed!" );
        } else {
          widget.setBackground( null );
          widget.setToolTipText( null );
        }
      }
    } );
  }

  private void createDigitsOnlyClientExample( Composite composite ) {
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Validation on the client (immediate):" );
    Text text = new Text( composite, SWT.BORDER );
    text.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    text.setText( "23" );
    text.setSelection( 2 );
    CustomBehaviors.addDigitsOnlyBehavior( text );
  }

  private void createDigitsOnlyClientEnforcementExample( Composite composite ) {
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Enforcement on the client:" );
    Text text = new Text( composite, SWT.BORDER );
    text.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    text.setText( "23" );
    text.setSelection( 2 );
    CustomBehaviors.addDigitsOnlyEnforcementBehavior( text );
  }

  private void createUpperCaseExample( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false, true, false ) );
    composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    ExampleUtil.createHeading( composite, "Auto upper-case field", 1 );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Turns all characters to upper-case while typing:" );
    Text text = new Text( composite, SWT.BORDER );
    text.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    CustomBehaviors.addUpperCaseBehavior( text );
  }

  private void createDateExample( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false, true, false ) );
    composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    ExampleUtil.createHeading( composite, "Valid date", 1 );
    Label label = new Label( composite, SWT.NONE );
// "Basic check on the client, client-side Verify and Modify check,\n server-side validation on focus-out:"
    label.setText( "Basic check on the client, server-side validation on focus-out:" );
    Text text = new Text( composite, SWT.BORDER );
    text.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    CustomBehaviors.addDateFieldBehavior( text );
  }

  private void createEMailExample( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false, true, false ) );
    composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    ExampleUtil.createHeading( composite, "Valid email address", 1 );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Basic client-side Modify check,\n server-side validation on focus-out:" );
    Text text = new Text( composite, SWT.BORDER );
    text.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    CustomBehaviors.addEMailBehavior( text );
  }

}
