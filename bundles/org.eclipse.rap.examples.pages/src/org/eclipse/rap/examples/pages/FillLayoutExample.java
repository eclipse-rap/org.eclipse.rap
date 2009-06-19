/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.rap.examples.viewer.IExamplePage;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class FillLayoutExample implements IExamplePage {

  private static final Color BG_COLOR = Graphics.getColor( 220, 220, 200 );

  private Composite parent;
  private int propSpacing = 5;
  private boolean propPrefSize;
  private Composite layoutArea;

  public void createControl( final Composite parent ) {
    this.parent = parent;
    parent.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 10 ) );
    createLayoutArea();
    createControlButtons( parent );
  }

  private void createLayoutArea() {
    if( layoutArea == null ) {
      layoutArea = new Composite( parent, SWT.NONE );
      GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, true );
      layoutArea.setLayoutData( layoutData );
      FillLayout layout = new FillLayout();
      layout.spacing = 10;
      layoutArea.setLayout( layout );
    }
    Control[] children = layoutArea.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].dispose();
    }
    createLayoutComp( layoutArea, SWT.HORIZONTAL );
    createLayoutComp( layoutArea, SWT.VERTICAL );
    layoutArea.layout();
  }

  private void createLayoutComp( final Composite parent, final int style ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false ) );
    String orientString = style == SWT.VERTICAL ? "Vertical" : "Horizontal";
    new Label( composite, SWT.NONE ).setText( orientString + " FillLayout" );
    Composite layoutComp = new Composite( composite, SWT.BORDER );
    if( !propPrefSize ) {
      layoutComp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    }
    FillLayout layout = new FillLayout( style );
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    layout.spacing = propSpacing;
    layoutComp.setLayout( layout );
    int count = style == SWT.VERTICAL ? 2 : 3;
    for( int i = 0; i < count ; i++ ) {
      Composite childComp = new Composite( layoutComp, SWT.BORDER );
      childComp.setBackground( BG_COLOR );
    }
  }

  private void createControlButtons( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    GridData gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    gridData.horizontalSpan = 2;
    group.setLayoutData( gridData );
    group.setLayout( ExampleUtil.createGridLayout( 1, false, 5, 2 ) );
    Composite spacingComp = new Composite( group, SWT.NONE );
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
      
      public void widgetSelected( final SelectionEvent e ) {
        propSpacing = spacingSpinner.getSelection();
        createLayoutArea();
      }
    } );
    final Button preferredSizeButton = new Button( group, SWT.CHECK );
    preferredSizeButton.setText( "Shrink containers to their preferred size" );
    preferredSizeButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( final SelectionEvent e ) {
        propPrefSize = preferredSizeButton.getSelection();
        createLayoutArea();
      }
    } );
  }
}
