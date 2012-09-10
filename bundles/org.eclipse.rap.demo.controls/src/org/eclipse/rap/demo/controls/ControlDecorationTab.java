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
package org.eclipse.rap.demo.controls;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class ControlDecorationTab extends ExampleTab {

  private final class LoggingSelectionListener implements SelectionListener {

    public void widgetSelected( SelectionEvent event ) {
      log( "widgetSelected: " + event.toString() );
    }

    public void widgetDefaultSelected( SelectionEvent event ) {
      log( "widgetDefaultSelected: " + event.toString() );
    }
  }

  private static final String PROP_SELECTION_LISTENER = "selectionListener";

  private boolean showOnlyOnFocus;
  private boolean showHover = true;
  private String description = "Description";
  private final ControlDecoration[] decorations = new ControlDecoration[ 2 ];

  private final SelectionListener listener;

  public ControlDecorationTab( CTabFolder topFolder ) {
    super( topFolder, "ControlDecoration" );
    listener = new LoggingSelectionListener();
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "TOP", SWT.TOP );
    createStyleButton( "BOTTOM", SWT.BOTTOM );
    createStyleButton( "LEFT", SWT.LEFT );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createChangeDescriptionButton( parent );
    createShowOnlyOnFocus( parent );
    createShowHover( parent );
    createPropertyCheckbox( "Add Selection Listener", PROP_SELECTION_LISTENER );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    GridLayout groupLayout = new GridLayout( 2, false );
    groupLayout.horizontalSpacing = 10;
    groupLayout.marginRight = 10;
    parent.setLayout( groupLayout );
    decorations[ 0 ] = createStaticExample( parent );
    decorations[ 1 ] = createDynamicExample( parent );
    configureDecorations();
  }

  private ControlDecoration createStaticExample( Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Static decoration: " );

    Text text = new Text( parent, SWT.SINGLE | SWT.BORDER );
    GridData data = new GridData();
    data.widthHint = 100;
    text.setLayoutData( data );
    text.setText( "some text" );

    ControlDecoration staticDecoration = new ControlDecoration( text, getStyle() );
    Image icon = getDecorationImage( FieldDecorationRegistry.DEC_INFORMATION );
    staticDecoration.setImage( icon );
    staticDecoration.setMarginWidth( 3 );
    staticDecoration.show();
    return staticDecoration;
  }

  private ControlDecoration createDynamicExample( Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Dynamic decoration: " );

    final Text text = new Text( parent, SWT.SINGLE | SWT.BORDER );
    GridData data = new GridData();
    data.widthHint = 100;
    text.setLayoutData( data );
    text.setText( "remove me" );

    final ControlDecoration dynDecoration = new ControlDecoration( text, getStyle() );
    Image icon = getDecorationImage( FieldDecorationRegistry.DEC_ERROR );
    dynDecoration.setImage( icon );
    dynDecoration.setMarginWidth( 3 );
    dynDecoration.hide();
    text.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent event ) {
        if( text.getText().length() > 0 ) {
          dynDecoration.hide();
        } else {
          dynDecoration.show();
        }
      }

    } );
    return dynDecoration;
  }

  private void createChangeDescriptionButton( Composite parent ) {
    Composite group = new Composite( parent, SWT.NONE );
    group.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( group, SWT.NONE );
    label.setText( "Description text: " );
    final Text descriptionText = new Text( group, SWT.SINGLE | SWT.BORDER );
    GridData data = new GridData();
    data.widthHint = 100;
    descriptionText.setLayoutData( data );
    descriptionText.setText( description );
    Button button = new Button( group, SWT.PUSH );
    button.setText( "Set" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        description = descriptionText.getText();
        configureDecorations();
      }
    } );
  }

  private void createShowOnlyOnFocus( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Show only on focus" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showOnlyOnFocus = button.getSelection();
        configureDecorations();
      }
    } );
  }

  private void createShowHover( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Show hover" );
    button.setSelection( true );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showHover = button.getSelection();
        configureDecorations();
      }
    } );
  }

  private void configureDecorations() {
    configureDecoration( decorations[ 0 ] );
    configureDecoration( decorations[ 1 ] );
  }

  private void configureDecoration( ControlDecoration decoration ) {
    decoration.setShowHover( showHover );
    decoration.setShowOnlyOnFocus( showOnlyOnFocus );
    decoration.setDescriptionText( description );
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      decoration.addSelectionListener( listener );
    } else {
      decoration.removeSelectionListener( listener );
    }
  }

  private static Image getDecorationImage( String image ) {
    FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
    return registry.getFieldDecoration( image ).getImage();
  }

}
