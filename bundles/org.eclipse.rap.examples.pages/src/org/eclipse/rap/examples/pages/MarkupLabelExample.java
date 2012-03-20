/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.rap.examples.*;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;


public class MarkupLabelExample implements IExamplePage {

  private static final String DEFAULT_TEXT = "<small>One morning, when Gregor Samsa woke from <em style='color:rgb(255,100,0)'>troubled dreams</em>, he found himself transformed in his bed into a <strong>horrible vermin</strong>.</small>\n\n<br/><br/>\n\nHe lay on his armour-like back, and if <a href='http://en.wikipedia.org/wiki/The_Metamorphosis'>he lifted his head</a> a little he could see his brown belly, slightly domed and divided by arches into stiff sections.";
  private static final String ERROR_MESSAGE = "Oooops. Found invalid markup. Please check your label text.";

  private Label markupLabel;
  private Label errorLabel;
  private Text multilineText;

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 3 ) );
    createLeftArea( parent );
    createCenterArea( parent );
    createRightArea( parent );
  }

  private void createLeftArea( Composite parent ) {
    Composite comp = new Composite( parent, SWT.NONE );
    comp.setLayout( ExampleUtil.createGridLayout( 1, false, true, true ) );
    comp.setLayoutData( ExampleUtil.createFillData() );
    markupLabel = createMarkupLabel( comp );
  }

  private void createCenterArea( Composite parent ) {
    Composite comp = new Composite( parent, SWT.NONE );
    comp.setLayout( ExampleUtil.createGridLayout( 2, true, true, true ) );
    comp.setLayoutData( ExampleUtil.createHorzFillData() );
    multilineText = createMultilineText( comp );
    createButtons( comp );
    errorLabel = createErrorLabel( comp );
    errorLabel.setVisible( false );
  }

  private void createRightArea( Composite parent ) {
    Infobox infobox = new Infobox( parent );
    infobox.addParagraph( "Since RAP 1.5 the Label widget supports markup. Use common html elements to tag portions of a label's text and change it's appearance with inline css." );
    infobox.addHeading( "Supported tags are:" );
    infobox.addParagraph( "html, br, b, strong, i, em, sub, sup, big, small, del, ins, code, samp, kbd, var, cite, dfn, q, abbr, span, img, a." );
  }

  private Label createMarkupLabel( Composite parent ) {
    Label label = new Label( parent, SWT.WRAP );
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    label.setData(  WidgetUtil.CUSTOM_VARIANT, "markup" );
    label.setText( DEFAULT_TEXT );
    label.setLayoutData( ExampleUtil.createFillData() );
    return label;
  }

  private Label createErrorLabel( Composite comp ) {
    Label label = new Label( comp, SWT.WRAP );
    label.setText( ERROR_MESSAGE );
    GridData layoutData = ExampleUtil.createHorzFillData();
    layoutData.horizontalSpan = 2;
    label.setLayoutData( layoutData );
    label.setData( WidgetUtil.CUSTOM_VARIANT, "error" );
    return label;
  }

  private Text createMultilineText( Composite parent ) {
    Text text = new Text( parent, SWT.MULTI | SWT.BORDER | SWT.WRAP );
    GridData layoutData = ExampleUtil.createHorzFillData();
    layoutData.horizontalSpan = 2;
    layoutData.heightHint = 300;
    text.setLayoutData( layoutData );
    text.setText( DEFAULT_TEXT );
    return text;
  }

  private void createButtons( Composite parent ) {
    createChangeTextButton( parent );
    createResetButton( parent );
  }

  private void createChangeTextButton( Composite parent ) {
    Button changeTextButton = new Button( parent, SWT.PUSH );
    changeTextButton.setLayoutData( ExampleUtil.createHorzFillData() );
    changeTextButton.setText( "change label text" );
    changeTextButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        try {
          markupLabel.setText( multilineText.getText() );
          multilineText.setData( WidgetUtil.CUSTOM_VARIANT, null );
          errorLabel.setVisible( false );
        } catch( IllegalArgumentException exception ) {
          multilineText.setData( WidgetUtil.CUSTOM_VARIANT, "error" );
          errorLabel.setVisible( true );
        }
      }
    } );
  }

  private void createResetButton( Composite parent ) {
    Button resetButton = new Button( parent, SWT.PUSH );
    resetButton.setLayoutData( ExampleUtil.createHorzFillData() );
    resetButton.setText( "reset" );
    resetButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        markupLabel.setText( DEFAULT_TEXT );
        multilineText.setText( DEFAULT_TEXT );
        multilineText.setData( WidgetUtil.CUSTOM_VARIANT, null );
        errorLabel.setVisible( false );
      }
    } );
  }

}
