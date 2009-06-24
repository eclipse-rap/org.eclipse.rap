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

import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class RowLayoutExample implements IExamplePage {

  private Composite parent;
  private Composite layoutComp;
  private boolean propCenter = false;
  private boolean propFill = false;
  private boolean propJustify = false;
  private boolean propPack = true;
  private boolean propWrap = false;
  private boolean propPrefSize;

  public void createControl( final Composite parent ) {
    this.parent = parent;
    parent.setLayout( ExampleUtil.createGridLayout( 1, false, 10, 10 ) );
    createLayoutArea();
    createControlButtons( parent );
  }

  public void createLayoutArea() {
    if( layoutComp == null ) {
      layoutComp = new Composite( parent, SWT.NONE );
      GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, true );
      layoutComp.setLayoutData( layoutData );
      FillLayout layout = new FillLayout();
      layout.spacing = 10;
      layoutComp.setLayout( layout );
    }
    Control[] children = layoutComp.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].dispose();
    }
    createLayoutComp( layoutComp, SWT.HORIZONTAL );
    createLayoutComp( layoutComp, SWT.VERTICAL );
    layoutComp.layout();
  }

  private void createLayoutComp( final Composite parent, final int style ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( ExampleUtil.createGridLayout( 1, false ) );
    String text
      = style == SWT.VERTICAL ? "Vertical RowLayout" : "Horizontal RowLayout";
    new Label( composite, SWT.NONE ).setText( text );
    Composite layoutComp = new Composite( composite, SWT.BORDER );
    if( !propPrefSize ) {
      layoutComp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    }
    RowLayout layout = new RowLayout( style );
    layout.marginTop = 0;
    layout.marginBottom = 0;
    layout.marginLeft = 0;
    layout.marginRight = 0;
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    layout.center = propCenter;
    layout.fill = propFill ;
    layout.justify = propJustify;
    layout.pack = propPack;
    layout.wrap = propWrap;
    layoutComp.setLayout( layout );
    Button button1 = new Button( layoutComp, SWT.PUSH );
    button1.setText( "Add" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image imgAdd = Graphics.getImage( "resources/add_obj.gif", classLoader );
    button1.setImage( imgAdd );
    Button button2 = new Button( layoutComp, SWT.PUSH );
    button2.setText( "Remove" );
    Image imgDelete = Graphics.getImage( "resources/delete_obj.gif",
                                         classLoader );
    button2.setImage( imgDelete );
    Button button3 = new Button( layoutComp, SWT.PUSH );
    button3.setText( "Up" );
    Image imgUp = Graphics.getImage( "resources/up.gif", classLoader );
    button3.setImage( imgUp );
    Button button4 = new Button( layoutComp, SWT.PUSH );
    button4.setText( "Down" );
    Image imgDown = Graphics.getImage( "resources/down.gif", classLoader );
    button4.setImage( imgDown );
    Button button5 = new Button( layoutComp, SWT.PUSH );
    button5.setText( "Clear" );
  }

  protected void createControlButtons( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    GridData gridData = new GridData( SWT.FILL, SWT.TOP, true, false );
    gridData.horizontalSpan = 2;
    group.setLayoutData( gridData );
    group.setLayout( ExampleUtil.createGridLayout( 1, false, 5, 2 ) );
    final Button centerButton = new Button( group, SWT.CHECK );
    centerButton.setText( "Center all elements in a row" );
    centerButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( final SelectionEvent e ) {
        propCenter = centerButton.getSelection();
        createLayoutArea();
      }
    } );
    final Button fillButton = new Button( group, SWT.CHECK );
    fillButton.setText( "Make all elements the same width / height" );
    fillButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( final SelectionEvent e ) {
        propFill = fillButton.getSelection();
        createLayoutArea();
      }
    } );
    final Button packButton = new Button( group, SWT.CHECK );
    packButton.setText( "Make all elements the same size" );
    packButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( final SelectionEvent e ) {
        propPack = !packButton.getSelection();
        createLayoutArea();
      }
    } );
    final Button justifyButton = new Button( group, SWT.CHECK );
    justifyButton.setText( "Justify elements" );
    justifyButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( final SelectionEvent e ) {
        propJustify = justifyButton.getSelection();
        createLayoutArea();
      }
    } );
    final Button wrapButton = new Button( group, SWT.CHECK );
    wrapButton.setText( "Wrap" );
    wrapButton.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( final SelectionEvent e ) {
        propWrap = wrapButton.getSelection();
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
