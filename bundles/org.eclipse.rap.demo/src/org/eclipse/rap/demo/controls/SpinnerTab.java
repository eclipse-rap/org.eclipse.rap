/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class SpinnerTab extends ExampleTab {

  private Spinner spinner;
  private Spinner modifySpinner;

  public SpinnerTab( final CTabFolder topFolder ) {
    super( topFolder, "Spinner" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "READ_ONLY", SWT.READ_ONLY );
    createStyleButton( "WRAP", SWT.WRAP );
    createVisibilityButton();
    createEnablementButton();
    createRangeControls( parent );
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createCursorCombo();
  }

  protected void createExampleControls( final Composite parent ) {
    GridLayout gridLayout = new GridLayout( 2, false );
    parent.setLayout( gridLayout );
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Simple Spinner" );
    spinner = new Spinner( parent, getStyle() );
    label = new Label( parent, SWT.NONE );
    label.setText( "Spinner with ModifyListener" );
    modifySpinner = new Spinner( parent, getStyle() );
    label = new Label( parent, SWT.NONE );
    label.setText( "Current value" );
    final Label lblSpinnerValue = new Label( parent, SWT.NONE );
    lblSpinnerValue.setText( String.valueOf( modifySpinner.getSelection() ) );
    modifySpinner.addModifyListener( new ModifyListener() {

      public void modifyText( final ModifyEvent event ) {
        String value = String.valueOf( modifySpinner.getSelection() );
        lblSpinnerValue.setText( value );
      }
    } );
    registerControl( spinner );
    registerControl( modifySpinner );
  }

  private void createRangeControls( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Settings" );
    group.setLayout( new GridLayout( 2, false ) );

    String minimum = String.valueOf( spinner.getMinimum() );
    final Text txtMin = createLabeledText( group, "Minimum", minimum );
    String maximum = String.valueOf( spinner.getMaximum() );
    final Text txtMax = createLabeledText( group, "Maximum", maximum );
    String inc = String.valueOf( spinner.getIncrement() );
    final Text txtInc = createLabeledText( group, "Increment", inc );
    String pageInc = String.valueOf( spinner.getIncrement() );
    final Text txtPageInc = createLabeledText( group,
                                               "PageIncrement",
                                               pageInc );
    String sel = String.valueOf( spinner.getSelection() );
    final Text txtSelection = createLabeledText( group, "Selection", sel );
    Button btnApply = new Button( group, SWT.PUSH );
    btnApply.setText( "Apply" );
    GridData btnApplyData = new GridData( GridData.HORIZONTAL_ALIGN_END );
    btnApplyData.horizontalSpan = 2;
    btnApply.setLayoutData( btnApplyData );
    btnApply.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        spinner.setMinimum( Integer.parseInt( txtMin.getText() ) );
        spinner.setMaximum( Integer.parseInt( txtMax.getText() ) );
        spinner.setIncrement( Integer.parseInt( txtInc.getText() ) );
        spinner.setPageIncrement( Integer.parseInt( txtPageInc.getText() ) );
        spinner.setSelection( Integer.parseInt( txtSelection.getText() ) );
      }
    } );
  }

  private Text createLabeledText( final Composite parent,
                                  final String text,
                                  final String value )
  {
    final Label label = new Label( parent, SWT.NONE );
    label.setText( text );
    final Text result = new Text( parent, SWT.BORDER );
    result.setText( value );
    result.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    return result;
  }
}
