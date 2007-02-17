/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

public class SpinnerTab extends ExampleTab {

  private Spinner spinner;

  public SpinnerTab( final TabFolder folder ) {
    super( folder, "Spinner" );
  }

  protected void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "WRAP" );
    createStyleButton( "READ_ONLY" );
    createVisibilityButton();
    createEnablementButton();
    createRangeControls( styleComp );
    createFontChooser();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    spinner = new Spinner( parent, getStyle() );
    spinner.setLayoutData( new RowData( 60, 22 ) );
    registerControl( spinner );
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
    Button btnApply = new Button( parent, RWT.PUSH );
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
    Composite container = new Composite( parent, RWT.NONE );
    container.setLayout( new GridLayout( 2, false ) );
    Label label = new Label( container, RWT.NONE );
    label.setLayoutData( new GridData( 100, 20 ) );
    label.setText( text );
    Text result = new Text( container, RWT.BORDER );
    result.setLayoutData( new GridData( 50, 20 ) );
    result.setText( value );
    return result;
  }
}
