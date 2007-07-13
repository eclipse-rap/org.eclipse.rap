/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
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
    createVisibilityButton();
    createEnablementButton();
    createRangeControls( parent );
// TODO [rst] Uncomment when setting fg and bg on spinner works (bug 194772)
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
  }

  protected void createExampleControls( final Composite parent ) {
    GridLayout gridLayout = new GridLayout( 2, false );
    parent.setLayout( gridLayout );
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Simple Spinner" );
    spinner = new Spinner( parent, getStyle() );
    Util.textSizeAdjustment( label, spinner );
    label = new Label( parent, SWT.NONE );
    label.setText( "Spinner with ModifyListener" );
    modifySpinner = new Spinner( parent, getStyle() );
    Util.textSizeAdjustment( label, modifySpinner );
    label = new Label( parent, SWT.NONE );
    label.setText( "Current value" );
    final Label lblSpinnerValue = new Label( parent, SWT.NONE );
    lblSpinnerValue.setText( String.valueOf( modifySpinner.getSelection() ) );
    Util.textSizeAdjustment( label, lblSpinnerValue );
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
    String minimum = String.valueOf( spinner.getMinimum() );
    final Text txtMin = createLabeledText( parent, "Minimum", minimum );
    String maximum = String.valueOf( spinner.getMaximum() );
    final Text txtMax = createLabeledText( parent, "Maximum", maximum );
    String inc = String.valueOf( spinner.getIncrement() );
    final Text txtInc = createLabeledText( parent, "Increment", inc );
    String pageInc = String.valueOf( spinner.getIncrement() );
    final Text txtPageInc = createLabeledText( parent,
                                               "PageIncrement",
                                               pageInc );
    String sel = String.valueOf( spinner.getSelection() );
    final Text txtSelection = createLabeledText( parent, "Selection", sel );
    Button btnApply = new Button( parent, SWT.PUSH );
    btnApply.setText( "Apply" );
    btnApply.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        spinner.setMinimum( Integer.parseInt( txtMin.getText() ) );
        spinner.setMaximum( Integer.parseInt( txtMax.getText() ) );
        spinner.setIncrement( Integer.parseInt( txtInc.getText() ) );
        spinner.setPageIncrement( Integer.parseInt( txtPageInc.getText() ) );
        spinner.setPageIncrement( Integer.parseInt( txtSelection.getText() ) );
      }
    } );
  }

  private Text createLabeledText( final Composite parent,
                                  final String text,
                                  final String value )
  {
    Composite container = new Composite( parent, SWT.NONE );
    container.setLayout( new GridLayout( 2, false ) );
    final Label label = new Label( container, SWT.NONE );
    label.setText( text );
    final Text result = new Text( container, SWT.BORDER );
    result.setText( value );
    parent.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent e ) {
        int height = label.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
        int width = height * 3;
        result.setLayoutData( new GridData( width, height ) );        
      }
    } );
    return result;
  }
}
