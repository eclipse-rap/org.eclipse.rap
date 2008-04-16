/*******************************************************************************
 * Copyright (c) 2006 Brad Reynolds and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors: Brad
 * Reynolds - initial API and implementation Brad Reynolds - bug 116920 Samy
 * Abou-Shama - adapted for RAP and implemented Snippet as Control
 ******************************************************************************/
package org.eclipse.rap.demo.databinding;

import org.eclipse.core.databinding.*;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Snippet that displays how to bind the validation error of the
 * {@link DataBindingContext} to a label.
 * 
 * @since 1.0
 */
public class Snippet004DataBindingContextErrorLabel extends Group {

  /**
   * Validator that returns validation errors for any value other than 5.
   * 
   * @since 1.0
   */
  private static class FiveValidator implements IValidator {
    public IStatus validate( final Object value ) {
      String msg = "the value was '" + value + "', not '5'";
      return "5".equals( value ) ? Status.OK_STATUS
                                 : ValidationStatus.error( msg );
    }
  }

  public Snippet004DataBindingContextErrorLabel( final Composite parent,
                                                 final int style )
  {
    super( parent, style );
    createPartControl();
  }

  public void createPartControl() {
    // Initiating the realm
    Realm realm = SWTObservables.getRealm( Display.getCurrent() );
    FormLayout formLayout = new FormLayout();
    formLayout.marginHeight = DatabindingSnippetsView.GROUP_MARGIN_HEIGHT;
    formLayout.marginWidth = DatabindingSnippetsView.GROUP_MARGIN_WIDTH;
    setLayout( formLayout );
    setText( "Context Error Label Snippet 004 - Binding Example" );
    Label info1 = new Label( this, SWT.NONE );
    info1.setText( "Enter '5' to be valid:" );
    FormData data
      = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH_LARGE,
                      SWT.DEFAULT );
    data.top = new FormAttachment( 0, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    info1.setLayoutData( data );
    Text text = new Text( this, SWT.BORDER );
    WritableValue value = new WritableValue( realm, null, String.class );
    data = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH_LARGE,
                         SWT.DEFAULT );
    data.top = new FormAttachment( 0, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( info1, 0 );
    text.setLayoutData( data );
    Label info2 = new Label( this, SWT.NONE );
    info2.setText( "Error/Validation:" );
    data = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH_LARGE,
                         SWT.DEFAULT );
    data.top
      = new FormAttachment( info1, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    info2.setLayoutData( data );
    Label errorLabel = new Label( this, SWT.BORDER );
    Color color = Graphics.getColor( 255, 0, 0 );
    errorLabel.setForeground( color );
    data = new FormData( DatabindingSnippetsView.STD_TEXT_WIDTH_LARGE,
                         SWT.DEFAULT );
    data.top = new FormAttachment( text, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( info2, 0 );
    errorLabel.setLayoutData( data );
    errorLabel.setText( "hallo" );
    DataBindingContext dbc = new DataBindingContext( realm );
    // Bind the text to the value.
    UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
    updateValueStrategy.setAfterConvertValidator( new FiveValidator() );
    dbc.bindValue( SWTObservables.observeText( text, SWT.Modify ),
                   value,
                   updateValueStrategy,
                   null );
    // Bind the error label to the validation error on the dbc.
    int severity = AggregateValidationStatus.MAX_SEVERITY;
    AggregateValidationStatus aggregateValidationStatus 
      = new AggregateValidationStatus( realm,
                                       dbc.getBindings(),
                                       severity );
    dbc.bindValue( SWTObservables.observeText( errorLabel ),
                   aggregateValidationStatus,
                   null,
                   null );
  }
}
