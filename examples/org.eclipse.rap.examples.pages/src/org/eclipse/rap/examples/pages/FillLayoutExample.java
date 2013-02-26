/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Spinner;


public class FillLayoutExample implements IExamplePage {

  private Composite contentComp;
  private int propSpacing = 5;
  private boolean propPrefSize;
  private Composite layoutArea;

  public void createControl( final Composite parent ) {
    contentComp = parent;
    contentComp.setLayout( ExampleUtil.createMainLayout( 1 ) );
    createLayoutArea();
    createControlButtons( parent );
  }

  private void createLayoutArea() {
    if( layoutArea == null || layoutArea.isDisposed() ) {
      layoutArea = new SashForm( contentComp, SWT.NONE );
      GridData layoutData = ExampleUtil.createFillData();
      layoutArea.setLayoutData( layoutData );
      FillLayout layout = new FillLayout();
      layout.spacing = 10;
      layoutArea.setLayout( layout );
    }
    Control[] children = layoutArea.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      if( !( children[ i ] instanceof Sash ) ) {
        children[ i ].dispose();
      }
    }
    createLayoutComp( layoutArea, SWT.HORIZONTAL );
    createLayoutComp( layoutArea, SWT.VERTICAL );
    layoutArea.layout();
  }

  private void createLayoutComp( Composite parent, int style ) {
    Composite layoutCompContainer = new Composite( parent, SWT.NONE );
    layoutCompContainer.setLayout( ExampleUtil.createGridLayout( 1, false, false, false ) );
    String orientString = style == SWT.VERTICAL ? "Vertical" : "Horizontal";
    String message = orientString + " FillLayout";
    ExampleUtil.createHeading( layoutCompContainer, message, 1 );
    Composite layoutComp = new Composite( layoutCompContainer, SWT.BORDER );
    if( !propPrefSize ) {
      layoutComp.setLayoutData( ExampleUtil.createFillData() );
    }
    FillLayout layout = new FillLayout( style );
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    layout.spacing = propSpacing;
    layoutComp.setLayout( layout );
    int count = style == SWT.VERTICAL ? 2 : 3;
    Color background = new Color( parent.getDisplay(), 220, 220, 200 );
    for( int i = 0; i < count ; i++ ) {
      Composite childComp = new Composite( layoutComp, SWT.BORDER );
      childComp.setBackground( background );
    }
  }

  private void createControlButtons( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    GridData gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    gridData.horizontalSpan = 2;
    composite.setLayoutData( gridData );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false, false, false ) );
    Composite spacingComp = new Composite( composite, SWT.NONE );
    RowLayout spacingLayout = new RowLayout();
    spacingLayout.spacing = 5;
    spacingLayout.center = true;
    spacingComp.setLayout( spacingLayout );
    new Label( spacingComp, SWT.NONE ).setText( "Set spacing:" );
    final Spinner spacingSpinner
      = new Spinner( spacingComp, SWT.READ_ONLY | SWT.BORDER );
    spacingSpinner.setMaximum( 12 );
    spacingSpinner.setSelection( propSpacing );
    spacingSpinner.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        propSpacing = spacingSpinner.getSelection();
        createLayoutArea();
      }
    } );
    final Button preferredSizeButton = new Button( composite, SWT.CHECK );
    preferredSizeButton.setText( "Shrink containers to their preferred size" );
    preferredSizeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        propPrefSize = preferredSizeButton.getSelection();
        createLayoutArea();
      }
    } );
  }
}
