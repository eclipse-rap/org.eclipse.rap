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

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class FormLayoutExample implements IExamplePage {

  private Composite parent;
  private Composite layoutArea;
  private boolean propPrefSize;

  public void createControl( Composite parent ) {
    this.parent = parent;
    parent.setLayout( ExampleUtil.createMainLayout( 1 ) );
    createLayoutArea();
    createControlButtons( parent );
  }

  private void createLayoutArea() {
    if( layoutArea == null ) {
      layoutArea = new Composite( parent, SWT.NONE );
      layoutArea.setLayout( ExampleUtil.createFillLayout( false ) );
      layoutArea.setLayoutData( ExampleUtil.createFillData() );
    }
    Control[] children = layoutArea.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].dispose();
    }
    createLayoutComp( layoutArea );
    layoutArea.layout();
  }

  protected void createLayoutComp( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false, true, true ) );
    Composite layoutComp = new Composite( composite, SWT.BORDER );
    if( !propPrefSize ) {
      layoutComp.setLayoutData( ExampleUtil.createFillData() );
    }
    FormLayout layout = new FormLayout();
    layoutComp.setLayout( layout );

    Label label = new Label( layoutComp, SWT.NONE );
    label.setText( "Label" );

    Text text = new Text( layoutComp, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY );
    text.setText( "Text" );
    FormData textData = new FormData();
    textData.left = new FormAttachment( label, 0 );
    textData.top = new FormAttachment( label, 0 );
    text.setLayoutData( textData );

    Button button = new Button( layoutComp, SWT.PUSH );
    button.setText( "Button" );
    FormData buttonData = new FormData();
    buttonData.right = new FormAttachment( 100, 0 );
    buttonData.bottom = new FormAttachment( 100, 0 );
    button.setLayoutData( buttonData );
  }

  protected void createControlButtons( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false, false, true ) );
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
