/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
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

public class ControlDecoratorTab extends ExampleTab {

  private static final String PROP_SELECTION_LISTENER = "selectionListener";

  private boolean showOnlyOnFocus;
  private boolean showHover = true;
  private final ControlDecoration[] decorations = new ControlDecoration[ 2 ];

  public ControlDecoratorTab( final CTabFolder topFolder ) {
    super( topFolder, "ControlDecorator" );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "TOP", SWT.TOP );
    createStyleButton( "BOTTOM", SWT.BOTTOM );
    createStyleButton( "LEFT", SWT.LEFT );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createVisibilityButton();
    createChangeDescriptionButton( parent );
    createShowOnlyOnFocus( parent );
    createShowHover( parent );
    createPropertyCheckbox( "Add Selection Listener", PROP_SELECTION_LISTENER );
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
    descriptionText.setText( "something" );
    Button button = new Button( group, SWT.PUSH );
    button.setText( "Set" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        for( int i = 0; i < decorations.length; i++ ) {
          ControlDecoration decoration = decorations[ i ];
          decoration.setDescriptionText( descriptionText.getText() );
        }
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, true ) );
    createControlDecorations( parent );
  }

  private void createControlDecorations( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    GridLayout groupLayout = new GridLayout( 2, false );
    groupLayout.horizontalSpacing = 10;
    groupLayout.marginRight = 10;
    group.setLayout( groupLayout );
    group.setText( "ControlDecoration" );
    SelectionListener listener = new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
        log( "widgetSelected: " + event.toString() );
      }
      public void widgetDefaultSelected( final SelectionEvent event ) {
        log( "widgetDefaultSelected: " + event.toString() );
      }
    };
    decorations[ 0 ] = createStaticExample( group );
    decorations[ 1 ] = createDynamicExample( group );
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      decorations[ 0 ].addSelectionListener( listener );
      decorations[ 1 ].addSelectionListener( listener );
    }
    decorations[ 0 ].setShowHover( showHover );
    decorations[ 1 ].setShowHover( showHover );
    decorations[ 0 ].setShowOnlyOnFocus( showOnlyOnFocus );
    decorations[ 1 ].setShowOnlyOnFocus( showOnlyOnFocus );
  }

  private ControlDecoration createDynamicExample( final Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Dynamic decoration: " );
    registerControl( label );

    final Text text = new Text( parent, SWT.SINGLE | SWT.BORDER );
    GridData data = new GridData();
    data.widthHint = 100;
    text.setLayoutData( data );
    text.setText( "remove me" );
    registerControl( text );

    final ControlDecoration dynDecoration = new ControlDecoration( text,
                                                                   getStyle() );
    Image icon = getDecorationImage( FieldDecorationRegistry.DEC_ERROR );
    dynDecoration.setImage( icon );
    dynDecoration.setDescriptionText( "Please enter something" );
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

  private ControlDecoration createStaticExample( final Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Static decoration: " );
    registerControl( label );

    Text text = new Text( parent, SWT.SINGLE | SWT.BORDER );
    GridData data = new GridData();
    data.widthHint = 100;
    text.setLayoutData( data );
    text.setText( "some text" );
    registerControl( text );

    ControlDecoration staticDecoration = new ControlDecoration( text,
                                                                getStyle() );
    Image icon = getDecorationImage( FieldDecorationRegistry.DEC_INFORMATION );
    staticDecoration.setImage( icon );
    staticDecoration.setDescriptionText( "More information" );
    staticDecoration.setMarginWidth( 3 );
    staticDecoration.show();
    return staticDecoration;
  }

  private Image getDecorationImage( String image ) {
    FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
    return registry.getFieldDecoration( image ).getImage();
  }

  private void createShowOnlyOnFocus( final Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Show only on focus" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showOnlyOnFocus = button.getSelection();
        decorations[ 0 ].setShowOnlyOnFocus( showOnlyOnFocus );
        decorations[ 1 ].setShowOnlyOnFocus( showOnlyOnFocus );
      }
    } );
  }

  private void createShowHover( final Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Show hover" );
    button.setSelection( true );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showHover = button.getSelection();
        decorations[ 0 ].setShowHover( showHover );
        decorations[ 1 ].setShowHover( showHover );
      }
    } );
  }
}
