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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class GridLayoutExample implements IExamplePage {

  private Composite layoutArea;
  private boolean propEqualWidth;
  private boolean propPrefSize;

  public void createControl( final Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    createLayoutArea( parent );
    createControlButtons( parent );
  }

  private void createLayoutArea( Composite parent ) {
    if( layoutArea == null || layoutArea.isDisposed() ) {
      layoutArea = new Composite( parent, SWT.NONE );
      layoutArea.setLayoutData( ExampleUtil.createFillData() );
      FillLayout layout = new FillLayout();
      layout.spacing = 10;
      layoutArea.setLayout( layout );
    }
    Control[] children = layoutArea.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].dispose();
    }
    createLayoutComp( layoutArea );
    layoutArea.layout();
  }

  private void createLayoutComp( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 0, 10 ) );
    ExampleUtil.createHeadingLabel( composite, "Grid Layout", 1 );
    Composite layoutComp = new Composite( composite, SWT.BORDER );
    if( !propPrefSize ) {
      layoutComp.setLayoutData( ExampleUtil.createFillData() );
    }
    GridLayout layout = new GridLayout( 3, propEqualWidth );
    layoutComp.setLayout( layout );
    for( int i = 0; i < 3; i++ ) {
      Label label = new Label( layoutComp, SWT.NONE );
      label.setText( "Label" );
      Text text = new Text( layoutComp, SWT.SINGLE | SWT.BORDER );
      GridData textData = new GridData( SWT.FILL, SWT.FILL, true, false );
      text.setLayoutData( textData );
      Button button = new Button( layoutComp, SWT.PUSH );
      button.setText( "Ok" );
      GridData gridData = new GridData();
      gridData.verticalAlignment = GridData.FILL;
      button.setLayoutData( gridData );
    }
  }

  protected void createControlButtons( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    GridData gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    gridData.horizontalSpan = 2;
    group.setLayoutData( gridData );
    group.setLayout( ExampleUtil.createGridLayout( 1, false, 2, 0 ) );
    final Button equalWidthButton = new Button( group, SWT.CHECK );
    equalWidthButton.setText( "Make all columns equal width" );
    equalWidthButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        propEqualWidth = equalWidthButton.getSelection();
        createLayoutArea( parent );
      }
    } );
    final Button preferredSizeButton = new Button( group, SWT.CHECK );
    preferredSizeButton.setText( "Shrink container to its preferred size" );
    preferredSizeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        propPrefSize = preferredSizeButton.getSelection();
        createLayoutArea( parent );
      }
    } );
  }
}
